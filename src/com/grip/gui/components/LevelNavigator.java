package com.grip.gui.components;

import com.grip.elements.BackgroundChange;
import com.grip.elements.BackgroundChangeInternal;
import com.grip.elements.LevelObject;
import com.grip.gui.TIGEditor;
import com.grip.gui.library.GraphicsRepository;
import com.grip.level.LevelViewSettings;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.swing.JPanel;

public class LevelNavigator extends JPanel {
   private final Rectangle RED_X_RECT = new Rectangle(2, 2, 5, 5);
   private TIGEditor editor;
   private LevelViewSettings levelViewSettings;
   private final GraphicsRepository graphicsRepository;
   boolean drawBlockSizedGrid;
   private volatile BufferedImage secondBuffer;
   private volatile BufferedImage currentBuffer;
   private Object lock = new Object();
   private Point dragViewMousePos;
   private Point dragViewScrollPos;
   private int gridSize = 30;
   private Point mousePosition;
   private Point jumpGuidePosition;
   private static final BackgroundChange startingChange = new BackgroundChangeInternal(0, 0);

   public LevelNavigator(TIGEditor editor, GraphicsRepository graphicsRepository, int width, int height, boolean isGridEnabledInitially) {
      this.editor = editor;
      this.graphicsRepository = graphicsRepository;
      this.addComponentListener(new ResizedListener());
      this.addMouseListener(new MouseClickListener());
      this.addMouseMotionListener(new MouseMotionListener());
      this.addKeyListener(new KeyboardListener());
      this.setFocusable(true);
      this.setFocusTraversalKeysEnabled(false);
      this.setSize(width, height);
      this.drawBlockSizedGrid = isGridEnabledInitially;
   }

   public void setLevelViewSettings(LevelViewSettings levelViewSettings) {
      this.levelViewSettings = levelViewSettings;
   }

   public void paintComponent(Graphics g) {
      synchronized(this.lock) {
         Graphics2D graph = (Graphics2D)g;
         graph.drawImage(this.currentBuffer, 0, 0, this.getWidth(), this.getHeight(), this);
         int playbackPositionInData = this.levelViewSettings.getDataPositionFromPlayback();
         this.drawMark(graph);
         this.drawCurrentPlaybackPosition(graph, playbackPositionInData);
         this.drawPosition(graph, playbackPositionInData);
         this.drawUserMarks(graph);
         this.drawUnplaceableArea(graph);
         this.drawJumpGuide(graph);
         this.drawJumpGuideInfo(graph);
         this.drawUserMarksInfo(graph);
      }
   }

   private void drawUserMarksInfo(Graphics2D graph) {
      if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded() && this.levelViewSettings.isPlaying()) {
         Color oldColor = graph.getColor();
         graph.setColor(Color.RED);
         Font oldFont = graph.getFont();
         int FONT_HEIGHT = true;
         graph.setFont(new Font("SansSerif", 1, 14));
         String positionStr = "X to place hint mark";
         graph.drawString(positionStr, this.currentBuffer.getWidth() - 5 - graph.getFontMetrics().stringWidth(positionStr), 14);
         graph.setFont(oldFont);
         graph.setColor(oldColor);
      }

   }

   private void drawUserMarks(Graphics2D graph) {
      double ratio = this.levelViewSettings.getRatio();
      Color oldColor = graph.getColor();
      Iterator i$ = this.levelViewSettings.getUserMarks().iterator();

      while(i$.hasNext()) {
         int mark = (Integer)i$.next();
         int currentPosition = (int)((double)(mark + this.levelViewSettings.getPlayerOffset() - this.levelViewSettings.getCurrentXOffset()) * ratio);
         if (currentPosition >= 0) {
            graph.setColor(Color.BLACK);
            graph.fillRect(currentPosition - 2, 0, 5, this.getBufferHeight());
            graph.setColor(Color.BLACK);
            graph.fillRect(currentPosition - 2, 0, 14, 12);
            graph.setColor(Color.WHITE);
            graph.fillRect(currentPosition, 0, 10, 10);
            graph.setColor(Color.WHITE);
            graph.drawLine(currentPosition, 0, currentPosition, this.getBufferHeight());
            graph.setColor(Color.RED);
            Rectangle redXRect = this.getRedXOffset(currentPosition);
            graph.drawLine((int)redXRect.getX(), (int)redXRect.getY(), (int)redXRect.getMaxX(), (int)redXRect.getMaxY());
            graph.drawLine((int)redXRect.getMaxX(), (int)redXRect.getY(), (int)redXRect.getX(), (int)redXRect.getMaxY());
         }
      }

      graph.setColor(oldColor);
   }

   private Rectangle getRedXOffset(int xPosition) {
      return new Rectangle((int)this.RED_X_RECT.getX() + xPosition, (int)this.RED_X_RECT.getY(), (int)this.RED_X_RECT.getWidth(), (int)this.RED_X_RECT.getHeight());
   }

   private void drawJumpGuide(Graphics2D graph) {
      if (this.jumpGuidePosition != null) {
         double ratio = this.levelViewSettings.getRatio();
         Point2D currentPosition = new Point2D.Float((float)((double)((int)(this.jumpGuidePosition.getX() - (double)this.levelViewSettings.getCurrentXOffset())) * ratio), (float)(this.getNavigatorBaseline() - this.jumpGuidePosition.getY() * ratio));
         double halfGridSizeInRepresentation = (double)this.gridSize * ratio * 0.5;
         double maxY = this.getNavigatorBaseline() - halfGridSizeInRepresentation;
         if (((Point2D)currentPosition).getY() > maxY) {
            ((Point2D)currentPosition).setLocation(((Point2D)currentPosition).getX(), maxY);
         }

         Point2D lastPosition = this.clonePoint2D(currentPosition);
         Point2D translation = new Point2D.Float(6.0F, -8.8F);
         Color oldColor = graph.getColor();
         if (((Point2D)currentPosition).getX() < (double)this.currentBuffer.getWidth() && ((Point2D)currentPosition).getY() < this.getNavigatorBaseline()) {
            while(true) {
               Point2D tmp = new Point2D.Float((float)(((Point2D)translation).getX() * ratio), (float)(((Point2D)translation).getY() * ratio));
               this.translatePoint2D(currentPosition, tmp);
               this.drawBoxEdgeTrajectories(graph, ratio, currentPosition, lastPosition);
               if (((Point2D)translation).getY() < 26.666662216186523) {
                  ((Point2D)translation).setLocation(((Point2D)translation).getX(), ((Point2D)translation).getY() + 0.800000011920929);
               }

               if (((Point2D)translation).getY() > 0.0 && (((Point2D)currentPosition).getX() + ((Point2D)translation).getX() + halfGridSizeInRepresentation > (double)this.currentBuffer.getWidth() || ((Point2D)currentPosition).getY() + ((Point2D)translation).getY() + halfGridSizeInRepresentation > this.getNavigatorBaseline())) {
                  float diff = (float)(lastPosition.getY() + halfGridSizeInRepresentation - this.getNavigatorBaseline());
                  if (diff < 0.0F) {
                     float coef = (float)((double)(-diff) / ((Point2D)translation).getY());
                     ((Point2D)currentPosition).setLocation(lastPosition.getX() + ((Point2D)translation).getX() * (double)coef, lastPosition.getY() + ((Point2D)translation).getY() * (double)coef);
                     this.drawBoxEdgeTrajectories(graph, ratio, currentPosition, lastPosition);
                  }
                  break;
               }

               lastPosition.setLocation(((Point2D)currentPosition).getX(), ((Point2D)currentPosition).getY());
            }
         }

         graph.setColor(oldColor);
      }
   }

   private void drawBoxEdgeTrajectories(Graphics2D graph, double ratio, Point2D currentPosition, Point2D lastPosition) {
      graph.setColor(Color.MAGENTA);
      graph.drawLine((int)(lastPosition.getX() - (double)this.gridSize * ratio * 0.5), (int)(lastPosition.getY() + (double)this.gridSize * ratio * 0.5), (int)(currentPosition.getX() - (double)this.gridSize * ratio * 0.5), (int)(currentPosition.getY() + (double)this.gridSize * ratio * 0.5));
      graph.setColor(Color.BLUE);
      graph.drawLine((int)(lastPosition.getX() - (double)this.gridSize * ratio * 0.5), (int)(lastPosition.getY() - (double)this.gridSize * ratio * 0.5), (int)(currentPosition.getX() - (double)this.gridSize * ratio * 0.5), (int)(currentPosition.getY() - (double)this.gridSize * ratio * 0.5));
      graph.setColor(Color.ORANGE);
      graph.drawLine((int)(lastPosition.getX() + (double)this.gridSize * ratio * 0.5), (int)(lastPosition.getY() - (double)this.gridSize * ratio * 0.5), (int)(currentPosition.getX() + (double)this.gridSize * ratio * 0.5), (int)(currentPosition.getY() - (double)this.gridSize * ratio * 0.5));
      graph.setColor(Color.WHITE);
      graph.drawLine((int)(lastPosition.getX() + (double)this.gridSize * ratio * 0.5), (int)(lastPosition.getY() + (double)this.gridSize * ratio * 0.5), (int)(currentPosition.getX() + (double)this.gridSize * ratio * 0.5), (int)(currentPosition.getY() + (double)this.gridSize * ratio * 0.5));
   }

   private void translatePoint2D(Point2D point2D, Point2D translation) {
      point2D.setLocation(point2D.getX() + translation.getX(), point2D.getY() + translation.getY());
   }

   private Point2D clonePoint2D(Point2D point2D) {
      return new Point2D.Float((float)point2D.getX(), (float)point2D.getY());
   }

   private void drawUnplaceableArea(Graphics2D graph) {
      int width = (int)((double)(this.levelViewSettings.getStartLimit() - this.levelViewSettings.getCurrentXOffset()) * this.levelViewSettings.getRatio());
      if (width > 0) {
         Color oldColor = graph.getColor();
         graph.setColor(new Color(255, 0, 0, 50));
         graph.fillRect(0, 0, width, this.currentBuffer.getHeight());
         graph.setColor(oldColor);
      }

   }

   private void drawMark(Graphics2D graph) {
      if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded()) {
         int markPosition = this.levelViewSettings.getMarkPosition();
         if (markPosition >= 0) {
            double ratio = this.levelViewSettings.getRatio();
            int markOnScreenPosition = (int)((double)(markPosition + this.levelViewSettings.getPlayerOffset() - this.levelViewSettings.getCurrentXOffset()) * ratio);
            Color oldColor = graph.getColor();
            graph.setColor(Color.BLACK);
            graph.fillRect(markOnScreenPosition - 2, 0, 5, this.getBufferHeight());
            graph.setColor(Color.GREEN);
            graph.drawLine(markOnScreenPosition, 0, markOnScreenPosition, this.getBufferHeight());
            graph.setColor(oldColor);
         }
      }

   }

   private void drawPosition(Graphics2D graph, int dataPosition) {
      if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded()) {
         Color oldColor = graph.getColor();
         graph.setColor(Color.YELLOW);
         Font oldFont = graph.getFont();
         int FONT_HEIGHT = true;
         graph.setFont(new Font("SansSerif", 1, 14));
         String positionStr = "Position: " + dataPosition;
         graph.drawString(positionStr, 5, 14);
         graph.setFont(oldFont);
         graph.setColor(oldColor);
      }

   }

   private void drawJumpGuideInfo(Graphics2D graph) {
      if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded() && !this.levelViewSettings.isPlaying()) {
         Color oldColor = graph.getColor();
         graph.setColor(Color.YELLOW);
         Font oldFont = graph.getFont();
         int FONT_HEIGHT = true;
         graph.setFont(new Font("SansSerif", 1, 14));
         String positionStr = "CTRL+LMB for jump guide";
         graph.drawString(positionStr, this.currentBuffer.getWidth() - 5 - graph.getFontMetrics().stringWidth(positionStr), 14);
         graph.setFont(oldFont);
         graph.setColor(oldColor);
      }

   }

   private void drawCurrentPlaybackPosition(Graphics2D graph, int dataPosition) {
      if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded() && this.levelViewSettings.getPlaybackPositionInMilliseconds() >= 0.0) {
         Color oldColor = graph.getColor();
         graph.setColor(Color.BLACK);
         int xPosition = (int)((double)(dataPosition + this.levelViewSettings.getPlayerOffset() - this.levelViewSettings.getCurrentXOffset()) * this.levelViewSettings.getRatio());
         graph.fillRect(xPosition - 2, 0, 5, this.getBufferHeight());
         graph.setColor(Color.RED);
         graph.drawLine(xPosition, 0, xPosition, this.getBufferHeight());
         if (this.levelViewSettings.isPlaying()) {
            if ((float)xPosition > (float)this.getBufferWidth() * 0.8F) {
               this.levelViewSettings.setNewCurrentPositionXFromNavigator(this.levelViewSettings.getCurrentXOffset() + (int)((double)((float)this.getBufferWidth() * 0.75F) / this.levelViewSettings.getRatio()));
            } else if ((double)xPosition < (double)this.levelViewSettings.getPlayerOffset() * this.levelViewSettings.getRatio() * 0.5) {
               this.levelViewSettings.setNewCurrentPositionXFromNavigator((int)((double)this.levelViewSettings.getCurrentXOffset() - (double)((float)this.levelViewSettings.getPlayerOffset() + (float)this.getBufferWidth() * 0.35F) / this.levelViewSettings.getRatio()));
            }
         }

         graph.setColor(oldColor);
      }

   }

   private void drawFinishLine(Graphics2D graph) {
      if (this.levelViewSettings != null) {
         Composite oldComp = graph.getComposite();
         graph.setComposite(AlphaComposite.getInstance(3, 0.25F));
         Color oldColor = graph.getColor();
         graph.setColor(Color.red);
         double ratio = this.levelViewSettings.getRatio();
         double x = (double)((int)((double)(this.levelViewSettings.getLevelEnd() - this.levelViewSettings.getCurrentXOffset()) * ratio) - 10);
         if (x < (double)this.currentBuffer.getWidth()) {
            graph.fillRect((int)x, 0, 10, this.getHeight());
         }

         graph.setColor(oldColor);
         graph.setComposite(oldComp);
      }
   }

   private void drawBaseLine(Graphics2D graph, int baseLine) {
      Color oldColor = graph.getColor();
      graph.setColor(Color.white);
      graph.drawLine(0, baseLine + 1, this.currentBuffer.getWidth(), baseLine + 1);
      graph.setColor(oldColor);
   }

   private void drawLevelObjects(Graphics2D graph, double baseLine) {
      AffineTransform transform = graph.getTransform();
      double ratio = this.levelViewSettings.getRatio();
      Iterator i$ = this.levelViewSettings.getVisibleLevelObjects().iterator();

      while(i$.hasNext()) {
         LevelObject levelObject = (LevelObject)i$.next();
         levelObject.drawObject(this, graph, this.graphicsRepository, baseLine - 1.0, ratio, this.levelViewSettings.getCurrentXOffset(), this.levelViewSettings.getDataSpaceWidth());
      }

      graph.setTransform(transform);
   }

   private void drawGrid(Graphics2D graph, double baseLine) {
      Composite oldComp = graph.getComposite();
      graph.setComposite(AlphaComposite.getInstance(3, 0.5F));
      Color oldColor = graph.getColor();
      graph.setColor(Color.black);
      double ratio = this.levelViewSettings.getRatio();

      double j;
      int yLocation;
      for(j = (double)(this.gridSize - this.levelViewSettings.getCurrentXOffset() % this.gridSize); j < (double)this.secondBuffer.getWidth() / ratio; j += (double)this.gridSize) {
         yLocation = (int)(j * ratio) - 1;
         graph.drawLine(yLocation, 0, yLocation, (int)baseLine);
      }

      for(j = 0.0; j < (double)((int)((double)this.getHeight() / ratio)); j += (double)this.gridSize) {
         yLocation = (int)(baseLine - j * ratio);
         graph.drawLine(0, yLocation, this.currentBuffer.getWidth(), yLocation);
      }

      graph.setColor(oldColor);
      graph.setComposite(oldComp);
   }

   private void drawBackgrounds(Graphics2D graph) {
      BackgroundChange current = startingChange;

      BackgroundChange change;
      for(Iterator i$ = this.levelViewSettings.getBackgrounds().iterator(); i$.hasNext(); current = change) {
         change = (BackgroundChange)i$.next();
         this.drawBackground(graph, current.getX(), current.getImageTitle(), change.getX());
      }

      this.drawBackground(graph, current.getX(), current.getImageTitle(), Integer.MAX_VALUE);
   }

   private void drawBackground(Graphics2D graph, int currentStart, String currentImageName, int nextStart) {
      BufferedImage backgroundImage = this.graphicsRepository.getFragment(currentImageName).getFragmentImage();
      double ratio = this.levelViewSettings.getRatio();
      double actualWidth = (double)backgroundImage.getWidth() * ratio;
      int repeats = (int)Math.ceil((double)(nextStart - currentStart) / actualWidth) + 1;
      int onScreenPosition;
      if (currentStart > 0) {
         onScreenPosition = (int)((double)(currentStart - this.levelViewSettings.getCurrentXOffset() + 2 * this.levelViewSettings.getPlayerOffset()) * ratio);
      } else {
         onScreenPosition = (int)((double)(-this.levelViewSettings.getCurrentXOffset()) * ratio);
      }

      int onScreenNextStart;
      if (nextStart < Integer.MAX_VALUE) {
         onScreenNextStart = (int)((double)(nextStart - this.levelViewSettings.getCurrentXOffset() + 2 * this.levelViewSettings.getPlayerOffset()) * ratio);
      } else {
         onScreenNextStart = Integer.MAX_VALUE;
      }

      double x = (double)onScreenPosition;

      for(int i = 0; i < repeats; ++i) {
         boolean smallerThanTopLimit = x < (double)this.secondBuffer.getWidth();
         if (smallerThanTopLimit && x <= (double)onScreenNextStart) {
            graph.drawImage(backgroundImage, (int)x, 0, (int)actualWidth + 1, this.getHeight(), this);
         }

         if (!smallerThanTopLimit) {
            Color oldColor = graph.getColor();
            graph.setColor(Color.GREEN);
            graph.fillRect((int)x, 0, this.secondBuffer.getWidth(), this.getHeight());
            graph.setColor(oldColor);
            break;
         }

         x += actualWidth;
      }

   }

   public boolean isDrawBlockSizedGrid() {
      return this.drawBlockSizedGrid;
   }

   public void setDrawBlockSizedGrid(boolean drawBlockSizedGrid) {
      this.drawBlockSizedGrid = drawBlockSizedGrid;
      this.updateImage();
   }

   public void updateImage() {
      if (this.levelViewSettings != null) {
         int width = this.getWidth();
         int height = this.getHeight();
         if (width != 0 && height != 0) {
            if (this.secondBuffer == null || this.secondBuffer.getWidth() != width || this.secondBuffer.getHeight() != height) {
               this.currentBuffer = new BufferedImage(width, height, 2);
               this.currentBuffer.getGraphics().clearRect(0, 0, width, height);
               this.secondBuffer = new BufferedImage(width, height, 2);
               this.levelViewSettings.levelNavigatorWindowChanged();
            }

            double baseLine = this.getNavigatorBaseline();
            Graphics2D secondBufferGraphics = (Graphics2D)this.secondBuffer.getGraphics();
            secondBufferGraphics.clearRect(0, 0, this.secondBuffer.getWidth(), this.secondBuffer.getHeight());
            this.drawBackgrounds(secondBufferGraphics);
            this.drawFinishLine(secondBufferGraphics);
            this.drawBaseLine(secondBufferGraphics, (int)baseLine);
            this.drawLevelObjects(secondBufferGraphics, baseLine);
            if (this.isDrawBlockSizedGrid()) {
               this.drawGrid(secondBufferGraphics, baseLine);
            }

            secondBufferGraphics.finalize();
            synchronized(this.lock) {
               BufferedImage tmp = this.currentBuffer;
               this.currentBuffer = this.secondBuffer;
               this.secondBuffer = tmp;
            }

            this.repaint();
         }
      }
   }

   public double getNavigatorBaseline() {
      return 0.95 * (double)this.getHeight();
   }

   public void clearBackgrounds() {
      this.levelViewSettings.clearBackgrounds();
   }

   public int getBufferWidth() {
      return this.currentBuffer == null ? 0 : this.currentBuffer.getWidth();
   }

   public int getBufferHeight() {
      return this.currentBuffer == null ? 0 : this.currentBuffer.getHeight();
   }

   public int getGridSize() {
      return this.gridSize;
   }

   private void calculateJumpGuidePosition(int x, int y) {
      this.jumpGuidePosition = this.levelViewSettings.getClickPosition(x, y);
      this.jumpGuidePosition = this.levelViewSettings.adjustJumpGuide((int)this.jumpGuidePosition.getX(), (int)this.jumpGuidePosition.getY());
   }

   private class KeyboardListener implements KeyListener {
      private KeyboardListener() {
      }

      public void keyPressed(KeyEvent e) {
         if (LevelNavigator.this.levelViewSettings != null) {
            if (!e.isControlDown() && !e.isMetaDown()) {
               switch (e.getKeyCode()) {
                  case 32:
                     if (LevelNavigator.this.dragViewMousePos == null) {
                        LevelNavigator.this.dragViewMousePos = LevelNavigator.this.getMousePosition();
                        LevelNavigator.this.dragViewScrollPos = new Point(LevelNavigator.this.levelViewSettings.getCurrentXOffset(), 0);
                     }
                     break;
                  case 37:
                     LevelNavigator.this.levelViewSettings.setNewCurrentPositionXFromNavigator(LevelNavigator.this.levelViewSettings.getCurrentXOffset() - (int)((double)(LevelNavigator.this.getParent().getWidth() / 2) / LevelNavigator.this.levelViewSettings.getRatio()));
                     break;
                  case 39:
                     LevelNavigator.this.levelViewSettings.setNewCurrentPositionXFromNavigator(LevelNavigator.this.levelViewSettings.getCurrentXOffset() + (int)((double)(LevelNavigator.this.getParent().getWidth() / 2) / LevelNavigator.this.levelViewSettings.getRatio()));
                     break;
                  case 49:
                     LevelNavigator.this.editor.blockButton.setSelected(true);
                     LevelNavigator.this.editor.blockButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.blockButton, 0, (String)null));
                     break;
                  case 50:
                     LevelNavigator.this.editor.spikeButton.setSelected(true);
                     LevelNavigator.this.editor.spikeButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.spikeButton, 0, (String)null));
                     break;
                  case 51:
                     LevelNavigator.this.editor.pitButton.setSelected(true);
                     LevelNavigator.this.editor.pitButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.pitButton, 0, (String)null));
                     break;
                  case 52:
                     LevelNavigator.this.editor.deleteObjectButton.setSelected(true);
                     LevelNavigator.this.editor.deleteObjectButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.deleteObjectButton, 0, (String)null));
                     break;
                  case 53:
                     LevelNavigator.this.editor.placeMusicMarkButton.setSelected(true);
                     LevelNavigator.this.editor.placeMusicMarkButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.placeMusicMarkButton, 0, (String)null));
                     break;
                  case 54:
                     LevelNavigator.this.editor.placeLevelEndButton.setSelected(true);
                     LevelNavigator.this.editor.placeLevelEndButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.placeLevelEndButton, 0, (String)null));
                     break;
                  case 55:
                     LevelNavigator.this.editor.placeBackgroundButton.setSelected(true);
                     LevelNavigator.this.editor.placeBackgroundButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.placeBackgroundButton, 0, (String)null));
                     break;
                  case 66:
                     LevelNavigator.this.editor.backgroundTypeComboBox.setSelectedIndex((LevelNavigator.this.editor.backgroundTypeComboBox.getSelectedIndex() + 1) % LevelNavigator.this.editor.backgroundTypeComboBox.getItemCount());
                     break;
                  case 73:
                     LevelNavigator.this.editor.rewindMusicButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
                     break;
                  case 79:
                     LevelNavigator.this.editor.playFromMarkButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
                     break;
                  case 80:
                     LevelNavigator.this.editor.playPauseButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
                     break;
                  case 88:
                     if (LevelNavigator.this.levelViewSettings.isPlaying()) {
                        LevelNavigator.this.levelViewSettings.placeUserMarkOnCurrentPlaybackPosition();
                     }
                     break;
                  case 116:
                     LevelNavigator.this.editor.launchInGameButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
               }

            } else {
               switch (e.getKeyCode()) {
                  case 65:
                     LevelNavigator.this.editor.openAudioButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
                     break;
                  case 78:
                     LevelNavigator.this.editor.newFileButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
                     break;
                  case 79:
                     LevelNavigator.this.editor.openFileButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
                     break;
                  case 83:
                     LevelNavigator.this.editor.saveFileButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
               }

            }
         }
      }

      public void keyReleased(KeyEvent e) {
         if (LevelNavigator.this.levelViewSettings != null) {
            switch (e.getKeyCode()) {
               case 32:
                  LevelNavigator.this.dragViewMousePos = null;
               default:
            }
         }
      }

      public void keyTyped(KeyEvent e) {
      }

      // $FF: synthetic method
      KeyboardListener(Object x1) {
         this();
      }
   }

   private class MouseMotionListener extends MouseAdapter {
      private MouseMotionListener() {
      }

      public void mouseMoved(MouseEvent e) {
         if (LevelNavigator.this.levelViewSettings != null) {
            LevelNavigator.this.mousePosition = LevelNavigator.this.getMousePosition();
            if (LevelNavigator.this.dragViewMousePos != null && LevelNavigator.this.mousePosition != null) {
               double delta = (double)(LevelNavigator.this.dragViewMousePos.x - LevelNavigator.this.mousePosition.x);
               delta = Math.signum(delta) * Math.pow(Math.abs(delta), 1.2);
               LevelNavigator.this.levelViewSettings.setNewCurrentPositionXFromNavigator(LevelNavigator.this.dragViewScrollPos.x + (int)(delta / LevelNavigator.this.levelViewSettings.getRatio()));
            }

         }
      }

      // $FF: synthetic method
      MouseMotionListener(Object x1) {
         this();
      }
   }

   private class MouseClickListener extends MouseAdapter {
      private MouseClickListener() {
      }

      public void mousePressed(MouseEvent e) {
         if (!e.isControlDown()) {
            LevelNavigator.this.levelViewSettings.mousePressed(e.getX(), e.getY(), e.getButton());
         }

      }

      public void mouseReleased(MouseEvent e) {
         if (e.getButton() == 1) {
            if (e.isControlDown()) {
               LevelNavigator.this.calculateJumpGuidePosition(e.getX(), e.getY());
               LevelNavigator.this.repaint();
            } else {
               boolean found = false;
               if (!LevelNavigator.this.levelViewSettings.isPlacingPit() && LevelNavigator.this.RED_X_RECT.getY() <= (double)e.getY() && LevelNavigator.this.RED_X_RECT.getMaxY() + 3.0 >= (double)e.getY()) {
                  found = LevelNavigator.this.levelViewSettings.tryRemoveUserMark((int)((double)e.getX() - LevelNavigator.this.RED_X_RECT.getWidth() * 0.5), (int)LevelNavigator.this.RED_X_RECT.getWidth() + 1);
               }

               if (!found) {
                  LevelNavigator.this.levelViewSettings.mouseReleased(e.getX(), e.getY());
               }
            }
         }

      }

      // $FF: synthetic method
      MouseClickListener(Object x1) {
         this();
      }
   }

   private class ResizedListener extends ComponentAdapter {
      private ResizedListener() {
      }

      public void componentResized(ComponentEvent e) {
         LevelNavigator.this.updateImage();
      }

      // $FF: synthetic method
      ResizedListener(Object x1) {
         this();
      }
   }
}
