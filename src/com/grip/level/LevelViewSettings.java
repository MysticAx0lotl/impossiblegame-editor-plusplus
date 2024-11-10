package com.grip.level;

import com.grip.audio.AudioFile;
import com.grip.audio.AudioFileFactory;
import com.grip.elements.BackgroundChange;
import com.grip.elements.BackgroundChangeInternal;
import com.grip.elements.BlockObject;
import com.grip.elements.LevelObject;
import com.grip.elements.PitObject;
import com.grip.elements.SpikeObject;
import com.grip.gui.components.LevelNavigator;
import com.grip.gui.components.LevelNavigatorScrollBar;
import com.grip.gui.library.GraphicsRepository;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class LevelViewSettings {
   private final LevelDefinition levelDefinition;
   private final LevelNavigator levelNavigator;
   private final LevelNavigatorScrollBar levelNavigatorScrollBar;
   private final GraphicsRepository graphicsRepository;
   private final List visibleLevelObjects = new ArrayList();
   private final int PLAYER_OFFSET = 150;
   private final byte blockTypeId = (new BlockObject(0, 0)).getTypeId();
   private final String DEFAULT_BACKGROUND = (new BackgroundChangeInternal(0, 0)).getImageTitle();
   private int targetHeight = 0;
   private int targetWidth = 0;
   private int currentXOffset = 0;
   private int oldNavigatorHeight = -1;
   private int oldNavigatorWidth = -1;
   private LevelObject.ObjectType selectedLevelObjectType;
   private boolean snapToGrid = true;
   private int pitStart;
   private int markPosition = 0;
   private int backgroundType = 0;
   private TreeSet userMarks = new TreeSet();
   private AudioFile audioFile = null;
   private final int UNITS_PER_SECOND = 360;
   private boolean wasPlaying = false;

   public LevelViewSettings(GraphicsRepository graphicsRepository, LevelDefinition levelDefinition, LevelNavigator levelNavigator, LevelNavigatorScrollBar levelNavigatorScrollBar) {
      this.levelDefinition = levelDefinition;
      this.levelNavigator = levelNavigator;
      this.levelNavigatorScrollBar = levelNavigatorScrollBar;
      this.graphicsRepository = graphicsRepository;
      this.updateDimensions();
      this.updateVisibleObjects();
      this.levelNavigator.setLevelViewSettings(this);
      this.levelDefinition.attachLevelViewSettings(this);
   }

   public void updateShift() {
      ++this.currentXOffset;
      this.levelNavigator.updateImage();
   }

   public void updateDimensions() {
      String imageTitle;
      for(Iterator i$ = this.levelDefinition.getBackgroundChanges().iterator(); i$.hasNext(); this.targetHeight = Math.max(this.graphicsRepository.getFragment(imageTitle).getHeight(), this.targetHeight)) {
         BackgroundChange backgroundChange = (BackgroundChange)i$.next();
         imageTitle = backgroundChange.getImageTitle();
      }

      this.targetWidth = this.levelDefinition.getActualLevelEnd();
   }

   public int getActualLevelEnd() {
      return this.levelDefinition.getActualLevelEnd();
   }

   public int getTargetWidth() {
      return this.targetWidth;
   }

   public int getTargetHeight() {
      return this.targetHeight;
   }

   public List getVisibleLevelObjects() {
      this.levelNavigatorWindowChanged();
      return this.visibleLevelObjects;
   }

   public void levelNavigatorWindowChanged() {
      int levelNavigatorHeight = this.levelNavigator.getBufferHeight();
      int levelNavigatorWidth = this.levelNavigator.getBufferWidth();
      if (levelNavigatorWidth != this.oldNavigatorWidth || levelNavigatorHeight != this.oldNavigatorHeight) {
         this.oldNavigatorHeight = this.levelNavigator.getBufferHeight();
         this.oldNavigatorWidth = this.levelNavigator.getBufferWidth();
         this.updateVisibleObjects();
      }

   }

   private void updateVisibleObjects() {
      this.visibleLevelObjects.clear();
      int dataSpaceWidth = this.getDataSpaceWidth();
      Iterator i$ = this.levelDefinition.getLevelObjects().iterator();

      while(i$.hasNext()) {
         LevelObject levelObject = (LevelObject)i$.next();
         if (levelObject.isVisible(this.currentXOffset, dataSpaceWidth)) {
            this.visibleLevelObjects.add(levelObject);
         }
      }

   }

   public double getRatio() {
      int height = this.getBackgrounds().size() > 0 ? this.getTargetHeight() : this.graphicsRepository.getFragment(this.DEFAULT_BACKGROUND).getHeight();
      return (double)this.levelNavigator.getBufferHeight() / (double)height;
   }

   public int getCurrentXOffset() {
      return this.currentXOffset;
   }

   public void setBackgroundType(int type) {
      this.backgroundType = type;
   }

   public void setNewCurrentPositionXFromScrollbar(int newX) {
      this.setNewCurrentPositionX(newX);
   }

   public void setNewCurrentPositionXFromNavigator(int newX) {
      this.setNewCurrentPositionX(newX);
      if (this.levelNavigatorScrollBar != null) {
         this.levelNavigatorScrollBar.updateCurrentPosition();
      }

   }

   public void setNewCurrentPositionXFromMark(int newX) {
      this.setNewCurrentPositionX(newX);
      if (this.levelNavigatorScrollBar != null) {
         this.levelNavigatorScrollBar.updateCurrentPosition();
      }

   }

   private void setNewCurrentPositionX(int newX) {
      this.currentXOffset = Math.min(this.levelDefinition.getLevelEnd(), Math.max(0, newX));
      this.updateVisibleObjects();
      this.levelNavigator.updateImage();
   }

   public int getWidth() {
      return this.levelNavigator.getBufferWidth();
   }

   public int getHeight() {
      return this.levelNavigator.getBufferHeight();
   }

   public int getDataSpaceWidth() {
      return (int)((double)this.levelNavigator.getBufferWidth() / this.getRatio());
   }

   public void levelDefinitionHasNewFile() {
      this.currentXOffset = 0;
      this.markPosition = 0;
      this.updateDimensions();
      this.updateVisibleObjects();
      this.levelNavigatorScrollBar.setFullContentsDimensions(this.levelDefinition.getLevelEnd());
      this.levelNavigator.updateImage();
   }

   public void setSelectedLevelObjectType(LevelObject.ObjectType selectedLevelObjectType) {
      this.selectedLevelObjectType = selectedLevelObjectType;
   }

   public void setSnapToGrid(boolean bSnapToGrid) {
      this.snapToGrid = bSnapToGrid;
   }

   public void mousePressed(int x, int y, int button) {
      if (button == 3) {
         LevelObject.ObjectType oldSelectedType = this.selectedLevelObjectType;
         this.selectedLevelObjectType = LevelObject.ObjectType.DELETE;
         this.mousePressedInPlacementMode(x, y);
         this.selectedLevelObjectType = oldSelectedType;
      } else if (button == 1) {
         this.mousePressedInPlacementMode(x, y);
      }

   }

   public Point getClickPosition(int x, int y) {
      double ratio = this.getRatio();
      x = Math.max(0, (int)((double)((float)x) / ratio + (double)this.currentXOffset));
      double tmpY = this.levelNavigator.getNavigatorBaseline() - (double)y;
      y = (int)(tmpY / ratio);
      return new Point(x, y);
   }

   public void mousePressedInPlacementMode(int x, int y) {
      Point clickPosition = this.getClickPosition(x, y);
      x = (int)clickPosition.getX();
      y = (int)clickPosition.getY();
      if (this.snapToGrid && this.selectedLevelObjectType != LevelObject.ObjectType.DELETE) {
         x = x - x % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
         if (this.selectedLevelObjectType != LevelObject.ObjectType.PIT) {
            y = y - y % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
         }
      }

      if (x >= 0 && y >= 0 || this.selectedLevelObjectType == LevelObject.ObjectType.DELETE) {
         this.pitStart = -1;
         int elementWidth = 0;
         switch (this.selectedLevelObjectType) {
            case BLOCK:
               if (!this.levelDefinition.addBlock(x - BlockObject.getWidthStatic() / 2, y - BlockObject.getHeightStatic() / 2)) {
                  return;
               }

               elementWidth = BlockObject.getWidthStatic();
               break;
            case SPIKE:
               if (!this.levelDefinition.addSpike(x - SpikeObject.getWidthStatic() / 2, y - SpikeObject.getHeightStatic() / 2)) {
                  return;
               }

               elementWidth = SpikeObject.getWidthStatic();
               break;
            case PIT:
               this.pitStart = x;
               break;
            case DELETE:
               LevelObject toDelete = this.levelDefinition.getFirstColliding(x, y, 0, 0);
               if (toDelete == null) {
                  toDelete = this.levelDefinition.getFirstColliding(x - 1 * this.levelNavigator.getGridSize(), y - 1 * this.levelNavigator.getGridSize(), 2 * this.levelNavigator.getGridSize(), 2 * this.levelNavigator.getGridSize());
               }

               if (toDelete != null) {
                  this.levelDefinition.deleteObject(toDelete);
               }

               if (toDelete != null) {
                  this.levelDefinition.deleteObject(toDelete);
               }
               break;
            case MARK:
               this.markPosition = Math.max(0, x - 150);
               break;
            case LEVELEND:
               this.levelDefinition.setActualLevelEnd(Math.max(10 * this.levelNavigator.getGridSize(), x));
               this.levelNavigatorScrollBar.setFullContentsDimensions(this.levelDefinition.getLevelEnd());
               break;
            case BACKGROUND:
               this.levelDefinition.addBackgroundChangeInternal(x - 300, this.backgroundType);
               this.updateDimensions();
         }

         switch (this.selectedLevelObjectType) {
            case BLOCK:
            case SPIKE:
            case PIT:
            case BACKGROUND:
               this.updateLevelEndAfterPlacing(x, elementWidth);
            case DELETE:
            case MARK:
            case LEVELEND:
            default:
               this.updateVisibleObjects();
               this.levelNavigator.updateImage();
               this.levelNavigator.invalidate();
               this.levelNavigator.repaint();
         }
      }
   }

   public void updateLevelEndAfterPlacing(int x, int elementWidth) {
      if (x + elementWidth > this.getLevelEnd()) {
         this.levelDefinition.setActualLevelEnd(Math.max(10 * this.levelNavigator.getGridSize(), x + elementWidth / 2));
         this.levelNavigatorScrollBar.setFullContentsDimensions(this.levelDefinition.getLevelEnd());
      }

   }

   public void mouseReleased(int x, int y) {
      if (x > this.levelNavigator.getBufferWidth()) {
         this.pitStart = -1;
      } else if (this.selectedLevelObjectType == LevelObject.ObjectType.PIT && this.pitStart >= 0) {
         y = 10;
         int tmpPitStart = this.pitStart;
         this.pitStart = 0;
         double ratio = this.getRatio();
         x = (int)((double)x / ratio + (double)this.currentXOffset);
         double tmpY = this.levelNavigator.getNavigatorBaseline() - (double)y;
         y = (int)(tmpY / ratio);
         if (this.snapToGrid) {
            x = x - x % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
            tmpPitStart = tmpPitStart - tmpPitStart % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
            if (this.selectedLevelObjectType != LevelObject.ObjectType.PIT) {
               y = y - y % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
            }
         } else {
            x += this.levelNavigator.getGridSize() / 2;
         }

         if (x >= 0 && y >= 0) {
            switch (this.selectedLevelObjectType) {
               case PIT:
                  if (tmpPitStart < x) {
                     if (!this.levelDefinition.addPit(tmpPitStart - PitObject.getImageWidth() / 2, x - PitObject.getImageWidth() / 2)) {
                        return;
                     }
                  } else if (!this.levelDefinition.addPit(x - PitObject.getImageWidth() / 2, tmpPitStart - PitObject.getImageWidth() / 2)) {
                     return;
                  }

                  this.updateLevelEndAfterPlacing(x, PitObject.getImageWidth());
               default:
                  this.updateVisibleObjects();
                  this.levelNavigator.updateImage();
            }
         }
      }
   }

   public int getLevelEnd() {
      return this.levelDefinition.getLevelEnd();
   }

   public void resetViewPosition() {
      this.setNewCurrentPositionXFromNavigator(0);
   }

   public void doPlayPause() {
      if (this.audioFile != null) {
         System.out.println("Pre play/pause: " + this.getPlaybackPositionInMilliseconds() / 1000.0 * 360.0);
         this.audioFile.doPlayPause();
         System.out.println("Post play/pause: " + this.getPlaybackPositionInMilliseconds() / 1000.0 * 360.0);
      }
   }

   public void doRewind() {
      if (this.audioFile != null) {
         this.audioFile.doRewind();
         this.resetViewPosition();
         this.levelNavigator.invalidate();
         this.levelNavigator.repaint();
      }
   }

   public void tickCurrentMillisecondPosition(int audioTickLength) {
      if (this.audioFile != null) {
         if (this.wasPlaying != this.audioFile.isPlaying()) {
            this.levelNavigator.repaint();
         }

         if (this.audioFile.isPlaying()) {
            this.audioFile.tickCurrentMillisecondPosition((long)audioTickLength);
            this.levelNavigator.invalidate();
            this.levelNavigator.repaint();
         }

         this.wasPlaying = this.audioFile.isPlaying();
      }
   }

   public void tickAudio(int audioTickLength) {
      this.tickCurrentMillisecondPosition(audioTickLength);
   }

   public boolean isFileLoaded() {
      return this.levelDefinition.isFileLoaded();
   }

   public void doPlayFromMark() {
      if (this.audioFile != null) {
         this.audioFile.doPlayFromPosition((long)this.getMarkPositionToMilliseconds());
         this.setNewCurrentPositionXFromMark(Math.max(this.markPosition - 150, 0));
         this.levelNavigator.updateImage();
         this.levelNavigator.invalidate();
         this.levelNavigator.repaint();
      }
   }

   private int getMarkPositionToMilliseconds() {
      return (int)((float)this.markPosition / 360.0F * 1000.0F);
   }

   public void deleteMark() {
      this.markPosition = 0;
      this.levelNavigator.invalidate();
      this.levelNavigator.repaint();
      this.audioFile.clearMark();
   }

   public int getDataPositionFromPlayback() {
      return (int)(this.audioFile != null ? (float)this.audioFile.getPlaybackPositionInMilliseconds() / 1000.0F * 360.0F : 0.0F);
   }

   public int getMarkPosition() {
      return this.markPosition;
   }

   public boolean setAudioFile(String filePath) {
      if (this.audioFile != null) {
         this.audioFile.close();
      }

      this.audioFile = AudioFileFactory.getAudioFile(filePath);
      return this.audioFile != null;
   }

   public int getPlayerOffset() {
      return 150;
   }

   public boolean isPlaying() {
      return this.audioFile != null && this.audioFile.isPlaying();
   }

   public double getPlaybackPositionInMilliseconds() {
      return this.audioFile == null ? 0.0 : (double)this.audioFile.getPlaybackPositionInMilliseconds();
   }

   public int getStartLimit() {
      return this.levelDefinition.getStartOffset();
   }

   public Point adjustJumpGuide(int x, int y) {
      int gridSize = this.levelNavigator.getGridSize();

      for(LevelObject levelObject = null; (levelObject = this.levelDefinition.getFirstColliding(x, y, gridSize, gridSize)) != null; y = (int)((float)levelObject.getY() + (float)gridSize * 1.5F)) {
         if (levelObject.getTypeId() != this.blockTypeId) {
            return new Point(x, y);
         }
      }

      return new Point(x, y);
   }

   public TreeSet getUserMarks() {
      return this.userMarks;
   }

   public boolean tryRemoveUserMark(int xPosition, int redXWidth) {
      double ratio = this.getRatio();
      int currentPosition = (int)((double)xPosition / ratio - (double)this.getCurrentXOffset() - (double)this.getPlayerOffset());
      Integer actual = this.getClosestUserMark(currentPosition);
      if (actual == null) {
         return false;
      } else if (actual != null && (double)Math.abs(actual - currentPosition) < (double)redXWidth / ratio) {
         this.userMarks.remove(actual);
         this.levelNavigator.repaint();
         return true;
      } else {
         return false;
      }
   }

   public Integer getClosestUserMark(int position) {
      Integer ceiling = (Integer)this.userMarks.ceiling(position);
      Integer floor = (Integer)this.userMarks.floor(position);
      if (ceiling == null && floor == null) {
         return null;
      } else {
         Integer closest = ceiling;
         if (ceiling == null) {
            closest = floor;
         } else if (floor != null && Math.abs(floor - position) < Math.abs(ceiling - position)) {
            closest = floor;
         }

         return closest;
      }
   }

   public boolean isPlacingPit() {
      return this.pitStart > 0;
   }

   public boolean placeUserMarkOnCurrentPlaybackPosition() {
      return this.placeUserMark(this.getDataPositionFromPlayback());
   }

   public boolean placeUserMark(int dataPosition) {
      Integer closest = this.getClosestUserMark(dataPosition);
      if (closest != null && Math.abs(dataPosition - closest) <= 30) {
         return false;
      } else {
         this.userMarks.add(dataPosition);
         this.levelNavigator.repaint();
         return true;
      }
   }

   public void clearBackgrounds() {
      this.levelDefinition.clearBackgroundChanges();
      this.levelNavigator.repaint();
   }

   public List getBackgrounds() {
      return this.levelDefinition.getBackgroundChanges();
   }
}
