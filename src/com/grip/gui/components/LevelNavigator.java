/*     */ package com.grip.gui.components;
/*     */ import com.grip.elements.BackgroundChange;
/*     */ import com.grip.elements.LevelObject;
/*     */ import com.grip.gui.TIGEditor;
/*     */ import com.grip.gui.library.GraphicsRepository;
/*     */ import com.grip.level.LevelViewSettings;
/*     */ import java.awt.Color;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public class LevelNavigator extends JPanel {
/*  22 */   private final Rectangle RED_X_RECT = new Rectangle(2, 2, 5, 5);
/*     */   
/*     */   private TIGEditor editor;
/*     */   private LevelViewSettings levelViewSettings;
/*     */   private final GraphicsRepository graphicsRepository;
/*     */   boolean drawBlockSizedGrid;
/*     */   private volatile BufferedImage secondBuffer;
/*     */   private volatile BufferedImage currentBuffer;
/*  30 */   private Object lock = new Object();
/*     */   private Point dragViewMousePos;
/*     */   private Point dragViewScrollPos;
/*  33 */   private int gridSize = 30;
/*     */   
/*     */   private Point mousePosition;
/*     */   private Point jumpGuidePosition;
/*     */   
/*     */   public LevelNavigator(TIGEditor editor, GraphicsRepository graphicsRepository, int width, int height, boolean isGridEnabledInitially) {
/*  39 */     this.editor = editor;
/*  40 */     this.graphicsRepository = graphicsRepository;
/*     */     
/*  42 */     addComponentListener(new ResizedListener());
/*  43 */     addMouseListener(new MouseClickListener());
/*  44 */     addMouseMotionListener(new MouseMotionListener());
/*  45 */     addKeyListener(new KeyboardListener());
/*  46 */     setFocusable(true);
/*  47 */     setFocusTraversalKeysEnabled(false);
/*     */     
/*  49 */     setSize(width, height);
/*  50 */     this.drawBlockSizedGrid = isGridEnabledInitially;
/*     */   }
/*     */   
/*     */   public void setLevelViewSettings(LevelViewSettings levelViewSettings) {
/*  54 */     this.levelViewSettings = levelViewSettings;
/*     */   }
/*     */   
/*     */   public void paintComponent(Graphics g) {
/*  58 */     synchronized (this.lock) {
/*  59 */       Graphics2D graph = (Graphics2D)g;
/*  60 */       graph.drawImage(this.currentBuffer, 0, 0, getWidth(), getHeight(), this);
/*  61 */       int playbackPositionInData = this.levelViewSettings.getDataPositionFromPlayback();
/*  62 */       drawMark(graph);
/*  63 */       drawCurrentPlaybackPosition(graph, playbackPositionInData);
/*  64 */       drawPosition(graph, playbackPositionInData);
/*  65 */       drawUserMarks(graph);
/*  66 */       drawUnplaceableArea(graph);
/*  67 */       drawJumpGuide(graph);
/*  68 */       drawJumpGuideInfo(graph);
/*  69 */       drawUserMarksInfo(graph);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void drawUserMarksInfo(Graphics2D graph) {
/*  74 */     if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded() && this.levelViewSettings.isPlaying()) {
/*  75 */       Color oldColor = graph.getColor();
/*  76 */       graph.setColor(Color.RED);
/*  77 */       Font oldFont = graph.getFont();
/*  78 */       int FONT_HEIGHT = 14;
/*  79 */       graph.setFont(new Font("SansSerif", 1, 14));
/*  80 */       String positionStr = "X to place hint mark";
/*     */ 
/*     */       
/*  83 */       graph.drawString(positionStr, this.currentBuffer.getWidth() - 5 - graph.getFontMetrics().stringWidth(positionStr), 14);
/*     */ 
/*     */       
/*  86 */       graph.setFont(oldFont);
/*  87 */       graph.setColor(oldColor);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void drawUserMarks(Graphics2D graph) {
/*  92 */     double ratio = this.levelViewSettings.getRatio();
/*     */     
/*  94 */     Color oldColor = graph.getColor();
/*  95 */     for (Iterator<Integer> i$ = this.levelViewSettings.getUserMarks().iterator(); i$.hasNext(); ) { int mark = ((Integer)i$.next()).intValue();
/*     */       
/*  97 */       int currentPosition = (int)((mark + this.levelViewSettings.getPlayerOffset() - this.levelViewSettings.getCurrentXOffset()) * ratio);
/*     */ 
/*     */       
/* 100 */       if (currentPosition >= 0) {
/*     */ 
/*     */         
/* 103 */         graph.setColor(Color.BLACK);
/* 104 */         graph.fillRect(currentPosition - 2, 0, 5, getBufferHeight());
/*     */         
/* 106 */         graph.setColor(Color.BLACK);
/* 107 */         graph.fillRect(currentPosition - 2, 0, 14, 12);
/*     */         
/* 109 */         graph.setColor(Color.WHITE);
/* 110 */         graph.fillRect(currentPosition, 0, 10, 10);
/*     */         
/* 112 */         graph.setColor(Color.WHITE);
/* 113 */         graph.drawLine(currentPosition, 0, currentPosition, getBufferHeight());
/*     */         
/* 115 */         graph.setColor(Color.RED);
/* 116 */         Rectangle redXRect = getRedXOffset(currentPosition);
/* 117 */         graph.drawLine((int)redXRect.getX(), (int)redXRect.getY(), (int)redXRect.getMaxX(), (int)redXRect.getMaxY());
/*     */         
/* 119 */         graph.drawLine((int)redXRect.getMaxX(), (int)redXRect.getY(), (int)redXRect.getX(), (int)redXRect.getMaxY());
/*     */       }  }
/*     */ 
/*     */     
/* 123 */     graph.setColor(oldColor);
/*     */   }
/*     */   
/*     */   private Rectangle getRedXOffset(int xPosition) {
/* 127 */     return new Rectangle((int)this.RED_X_RECT.getX() + xPosition, (int)this.RED_X_RECT.getY(), (int)this.RED_X_RECT.getWidth(), (int)this.RED_X_RECT.getHeight());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void drawJumpGuide(Graphics2D graph) {
/* 134 */     if (this.jumpGuidePosition == null) {
/*     */       return;
/*     */     }
/* 137 */     double ratio = this.levelViewSettings.getRatio();
/*     */     
/* 139 */     Point2D currentPosition = new Point2D.Float((float)((int)(this.jumpGuidePosition.getX() - this.levelViewSettings.getCurrentXOffset()) * ratio), (float)(getNavigatorBaseline() - this.jumpGuidePosition.getY() * ratio));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 144 */     double halfGridSizeInRepresentation = this.gridSize * ratio * 0.5D;
/*     */     
/* 146 */     double maxY = getNavigatorBaseline() - halfGridSizeInRepresentation;
/* 147 */     if (currentPosition.getY() > maxY) {
/* 148 */       currentPosition.setLocation(currentPosition.getX(), maxY);
/*     */     }
/*     */     
/* 151 */     Point2D lastPosition = clonePoint2D(currentPosition);
/*     */     
/* 153 */     Point2D translation = new Point2D.Float(6.0F, -8.8F);
/*     */     
/* 155 */     Color oldColor = graph.getColor();
/*     */     
/* 157 */     if (currentPosition.getX() < this.currentBuffer.getWidth() && currentPosition.getY() < getNavigatorBaseline()) {
/*     */       
/*     */       while (true) {
/* 160 */         Point2D tmp = new Point2D.Float((float)(translation.getX() * ratio), (float)(translation.getY() * ratio));
/*     */         
/* 162 */         translatePoint2D(currentPosition, tmp);
/* 163 */         drawBoxEdgeTrajectories(graph, ratio, currentPosition, lastPosition);
/*     */         
/* 165 */         if (translation.getY() < 26.666662216186523D) {
/* 166 */           translation.setLocation(translation.getX(), translation.getY() + 0.800000011920929D);
/*     */         }
/*     */         
/* 169 */         if (translation.getY() > 0.0D && (currentPosition.getX() + translation.getX() + halfGridSizeInRepresentation > this.currentBuffer.getWidth() || currentPosition.getY() + translation.getY() + halfGridSizeInRepresentation > getNavigatorBaseline())) {
/*     */           break;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 177 */         lastPosition.setLocation(currentPosition.getX(), currentPosition.getY());
/*     */       } 
/*     */       
/* 180 */       float diff = (float)(lastPosition.getY() + halfGridSizeInRepresentation - getNavigatorBaseline());
/*     */       
/* 182 */       if (diff < 0.0F) {
/* 183 */         float coef = (float)(-diff / translation.getY());
/* 184 */         currentPosition.setLocation(lastPosition.getX() + translation.getX() * coef, lastPosition.getY() + translation.getY() * coef);
/*     */         
/* 186 */         drawBoxEdgeTrajectories(graph, ratio, currentPosition, lastPosition);
/*     */       } 
/*     */     } 
/*     */     
/* 190 */     graph.setColor(oldColor);
/*     */   }
/*     */ 
/*     */   
/*     */   private void drawBoxEdgeTrajectories(Graphics2D graph, double ratio, Point2D currentPosition, Point2D lastPosition) {
/* 195 */     graph.setColor(Color.MAGENTA);
/* 196 */     graph.drawLine((int)(lastPosition.getX() - this.gridSize * ratio * 0.5D), (int)(lastPosition.getY() + this.gridSize * ratio * 0.5D), (int)(currentPosition.getX() - this.gridSize * ratio * 0.5D), (int)(currentPosition.getY() + this.gridSize * ratio * 0.5D));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 201 */     graph.setColor(Color.BLUE);
/* 202 */     graph.drawLine((int)(lastPosition.getX() - this.gridSize * ratio * 0.5D), (int)(lastPosition.getY() - this.gridSize * ratio * 0.5D), (int)(currentPosition.getX() - this.gridSize * ratio * 0.5D), (int)(currentPosition.getY() - this.gridSize * ratio * 0.5D));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 207 */     graph.setColor(Color.ORANGE);
/* 208 */     graph.drawLine((int)(lastPosition.getX() + this.gridSize * ratio * 0.5D), (int)(lastPosition.getY() - this.gridSize * ratio * 0.5D), (int)(currentPosition.getX() + this.gridSize * ratio * 0.5D), (int)(currentPosition.getY() - this.gridSize * ratio * 0.5D));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 213 */     graph.setColor(Color.WHITE);
/* 214 */     graph.drawLine((int)(lastPosition.getX() + this.gridSize * ratio * 0.5D), (int)(lastPosition.getY() + this.gridSize * ratio * 0.5D), (int)(currentPosition.getX() + this.gridSize * ratio * 0.5D), (int)(currentPosition.getY() + this.gridSize * ratio * 0.5D));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void translatePoint2D(Point2D point2D, Point2D translation) {
/* 221 */     point2D.setLocation(point2D.getX() + translation.getX(), point2D.getY() + translation.getY());
/*     */   }
/*     */ 
/*     */   
/*     */   private Point2D clonePoint2D(Point2D point2D) {
/* 226 */     return new Point2D.Float((float)point2D.getX(), (float)point2D.getY());
/*     */   }
/*     */   
/*     */   private void drawUnplaceableArea(Graphics2D graph) {
/* 230 */     int width = (int)((this.levelViewSettings.getStartLimit() - this.levelViewSettings.getCurrentXOffset()) * this.levelViewSettings.getRatio());
/*     */     
/* 232 */     if (width > 0) {
/* 233 */       Color oldColor = graph.getColor();
/* 234 */       graph.setColor(new Color(255, 0, 0, 50));
/* 235 */       graph.fillRect(0, 0, width, this.currentBuffer.getHeight());
/*     */       
/* 237 */       graph.setColor(oldColor);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void drawMark(Graphics2D graph) {
/* 242 */     if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded()) {
/* 243 */       int markPosition = this.levelViewSettings.getMarkPosition();
/* 244 */       if (markPosition >= 0) {
/* 245 */         double ratio = this.levelViewSettings.getRatio();
/* 246 */         int markOnScreenPosition = (int)((markPosition + this.levelViewSettings.getPlayerOffset() - this.levelViewSettings.getCurrentXOffset()) * ratio);
/*     */ 
/*     */         
/* 249 */         Color oldColor = graph.getColor();
/* 250 */         graph.setColor(Color.BLACK);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 257 */         graph.fillRect(markOnScreenPosition - 2, 0, 5, getBufferHeight());
/* 258 */         graph.setColor(Color.GREEN);
/* 259 */         graph.drawLine(markOnScreenPosition, 0, markOnScreenPosition, getBufferHeight());
/*     */         
/* 261 */         graph.setColor(oldColor);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void drawPosition(Graphics2D graph, int dataPosition) {
/* 267 */     if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded()) {
/* 268 */       Color oldColor = graph.getColor();
/* 269 */       graph.setColor(Color.YELLOW);
/* 270 */       Font oldFont = graph.getFont();
/* 271 */       int FONT_HEIGHT = 14;
/* 272 */       graph.setFont(new Font("SansSerif", 1, 14));
/* 273 */       String positionStr = "Position: " + dataPosition;
/*     */ 
/*     */       
/* 276 */       graph.drawString(positionStr, 5, 14);
/*     */       
/* 278 */       graph.setFont(oldFont);
/* 279 */       graph.setColor(oldColor);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void drawJumpGuideInfo(Graphics2D graph) {
/* 284 */     if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded() && !this.levelViewSettings.isPlaying()) {
/* 285 */       Color oldColor = graph.getColor();
/* 286 */       graph.setColor(Color.YELLOW);
/* 287 */       Font oldFont = graph.getFont();
/* 288 */       int FONT_HEIGHT = 14;
/* 289 */       graph.setFont(new Font("SansSerif", 1, 14));
/* 290 */       String positionStr = "CTRL+LMB for jump guide";
/*     */ 
/*     */       
/* 293 */       graph.drawString(positionStr, this.currentBuffer.getWidth() - 5 - graph.getFontMetrics().stringWidth(positionStr), 14);
/*     */ 
/*     */       
/* 296 */       graph.setFont(oldFont);
/* 297 */       graph.setColor(oldColor);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void drawCurrentPlaybackPosition(Graphics2D graph, int dataPosition) {
/* 302 */     if (this.levelViewSettings != null && this.levelViewSettings.isFileLoaded() && this.levelViewSettings.getPlaybackPositionInMilliseconds() >= 0.0D) {
/*     */       
/* 304 */       Color oldColor = graph.getColor();
/* 305 */       graph.setColor(Color.BLACK);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 314 */       int xPosition = (int)((dataPosition + this.levelViewSettings.getPlayerOffset() - this.levelViewSettings.getCurrentXOffset()) * this.levelViewSettings.getRatio());
/*     */       
/* 316 */       graph.fillRect(xPosition - 2, 0, 5, getBufferHeight());
/* 317 */       graph.setColor(Color.RED);
/* 318 */       graph.drawLine(xPosition, 0, xPosition, getBufferHeight());
/*     */       
/* 320 */       if (this.levelViewSettings.isPlaying()) {
/* 321 */         if (xPosition > getBufferWidth() * 0.8F) {
/* 322 */           this.levelViewSettings.setNewCurrentPositionXFromNavigator(this.levelViewSettings.getCurrentXOffset() + (int)((getBufferWidth() * 0.75F) / this.levelViewSettings.getRatio()));
/*     */ 
/*     */         
/*     */         }
/* 326 */         else if (xPosition < this.levelViewSettings.getPlayerOffset() * this.levelViewSettings.getRatio() * 0.5D) {
/* 327 */           this.levelViewSettings.setNewCurrentPositionXFromNavigator((int)(this.levelViewSettings.getCurrentXOffset() - (this.levelViewSettings.getPlayerOffset() + getBufferWidth() * 0.35F) / this.levelViewSettings.getRatio()));
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 335 */       graph.setColor(oldColor);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void drawFinishLine(Graphics2D graph) {
/* 340 */     if (this.levelViewSettings == null) {
/*     */       return;
/*     */     }
/* 343 */     Composite oldComp = graph.getComposite();
/* 344 */     graph.setComposite(AlphaComposite.getInstance(3, 0.25F));
/* 345 */     Color oldColor = graph.getColor();
/* 346 */     graph.setColor(Color.red);
/*     */     
/* 348 */     double ratio = this.levelViewSettings.getRatio();
/* 349 */     double x = ((int)((this.levelViewSettings.getLevelEnd() - this.levelViewSettings.getCurrentXOffset()) * ratio) - 10);
/* 350 */     if (x < this.currentBuffer.getWidth()) {
/* 351 */       graph.fillRect((int)x, 0, 10, getHeight());
/*     */     }
/*     */     
/* 354 */     graph.setColor(oldColor);
/* 355 */     graph.setComposite(oldComp);
/*     */   }
/*     */   
/*     */   private void drawBaseLine(Graphics2D graph, int baseLine) {
/* 359 */     Color oldColor = graph.getColor();
/* 360 */     graph.setColor(Color.white);
/*     */     
/* 362 */     graph.drawLine(0, baseLine + 1, this.currentBuffer.getWidth(), baseLine + 1);
/*     */     
/* 364 */     graph.setColor(oldColor);
/*     */   }
/*     */   
/*     */   private void drawLevelObjects(Graphics2D graph, double baseLine) {
/* 368 */     AffineTransform transform = graph.getTransform();
/*     */     
/* 370 */     double ratio = this.levelViewSettings.getRatio();
/* 371 */     for (LevelObject levelObject : this.levelViewSettings.getVisibleLevelObjects()) {
/* 372 */       levelObject.drawObject(this, graph, this.graphicsRepository, baseLine - 1.0D, ratio, this.levelViewSettings.getCurrentXOffset(), this.levelViewSettings.getDataSpaceWidth());
/*     */     }
/*     */ 
/*     */     
/* 376 */     graph.setTransform(transform);
/*     */   }
/*     */   
/*     */   private void drawGrid(Graphics2D graph, double baseLine) {
/* 380 */     Composite oldComp = graph.getComposite();
/* 381 */     graph.setComposite(AlphaComposite.getInstance(3, 0.5F));
/* 382 */     Color oldColor = graph.getColor();
/* 383 */     graph.setColor(Color.black);
/*     */     
/* 385 */     double ratio = this.levelViewSettings.getRatio(); double i;
/* 386 */     for (i = (this.gridSize - this.levelViewSettings.getCurrentXOffset() % this.gridSize); i < this.secondBuffer.getWidth() / ratio; 
/* 387 */       i += this.gridSize) {
/* 388 */       int xLocation = (int)(i * ratio) - 1;
/* 389 */       graph.drawLine(xLocation, 0, xLocation, (int)baseLine);
/*     */     } 
/*     */     double j;
/* 392 */     for (j = 0.0D; j < (int)(getHeight() / ratio); j += this.gridSize) {
/* 393 */       int yLocation = (int)(baseLine - j * ratio);
/* 394 */       graph.drawLine(0, yLocation, this.currentBuffer.getWidth(), yLocation);
/*     */     } 
/*     */     
/* 397 */     graph.setColor(oldColor);
/* 398 */     graph.setComposite(oldComp);
/*     */   }
/*     */   
/* 401 */   private static final BackgroundChange startingChange = (BackgroundChange)new BackgroundChangeInternal(0, 0);
/*     */ 
/*     */   
/*     */   private void drawBackgrounds(Graphics2D graph) {
/* 405 */     BackgroundChange current = startingChange;
/* 406 */     for (BackgroundChange change : this.levelViewSettings.getBackgrounds()) {
/* 407 */       drawBackground(graph, current.getX(), current.getImageTitle(), change.getX());
/* 408 */       current = change;
/*     */     } 
/*     */     
/* 411 */     drawBackground(graph, current.getX(), current.getImageTitle(), 2147483647);
/*     */   }
/*     */ 
/*     */   
/*     */   private void drawBackground(Graphics2D graph, int currentStart, String currentImageName, int nextStart) {
/*     */     int onScreenPosition, onScreenNextStart;
/* 417 */     BufferedImage backgroundImage = this.graphicsRepository.getFragment(currentImageName).getFragmentImage();
/*     */     
/* 419 */     double ratio = this.levelViewSettings.getRatio();
/*     */     
/* 421 */     double actualWidth = backgroundImage.getWidth() * ratio;
/*     */     
/* 423 */     int repeats = (int)Math.ceil((nextStart - currentStart) / actualWidth) + 1;
/*     */ 
/*     */ 
/*     */     
/* 427 */     if (currentStart > 0) {
/* 428 */       onScreenPosition = (int)((currentStart - this.levelViewSettings.getCurrentXOffset() + 2 * this.levelViewSettings.getPlayerOffset()) * ratio);
/*     */     }
/*     */     else {
/*     */       
/* 432 */       onScreenPosition = (int)(-this.levelViewSettings.getCurrentXOffset() * ratio);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 438 */     if (nextStart < Integer.MAX_VALUE) {
/* 439 */       onScreenNextStart = (int)((nextStart - this.levelViewSettings.getCurrentXOffset() + 2 * this.levelViewSettings.getPlayerOffset()) * ratio);
/*     */     } else {
/*     */       
/* 442 */       onScreenNextStart = Integer.MAX_VALUE;
/*     */     } 
/*     */     
/* 445 */     double x = onScreenPosition;
/* 446 */     for (int i = 0; i < repeats; i++) {
/* 447 */       boolean smallerThanTopLimit = (x < this.secondBuffer.getWidth());
/* 448 */       if (smallerThanTopLimit && x <= onScreenNextStart) {
/* 449 */         graph.drawImage(backgroundImage, (int)x, 0, (int)actualWidth + 1, getHeight(), this);
/*     */       }
/* 451 */       if (!smallerThanTopLimit) {
/* 452 */         Color oldColor = graph.getColor();
/* 453 */         graph.setColor(Color.GREEN);
/* 454 */         graph.fillRect((int)x, 0, this.secondBuffer.getWidth(), getHeight());
/* 455 */         graph.setColor(oldColor);
/*     */         break;
/*     */       } 
/* 458 */       x += actualWidth;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isDrawBlockSizedGrid() {
/* 463 */     return this.drawBlockSizedGrid;
/*     */   }
/*     */   
/*     */   public void setDrawBlockSizedGrid(boolean drawBlockSizedGrid) {
/* 467 */     this.drawBlockSizedGrid = drawBlockSizedGrid;
/* 468 */     updateImage();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateImage() {
/* 473 */     if (this.levelViewSettings == null) {
/*     */       return;
/*     */     }
/* 476 */     int width = getWidth();
/* 477 */     int height = getHeight();
/* 478 */     if (width == 0 || height == 0)
/*     */       return; 
/* 480 */     if (this.secondBuffer == null || this.secondBuffer.getWidth() != width || this.secondBuffer.getHeight() != height) {
/* 481 */       this.currentBuffer = new BufferedImage(width, height, 2);
/* 482 */       this.currentBuffer.getGraphics().clearRect(0, 0, width, height);
/* 483 */       this.secondBuffer = new BufferedImage(width, height, 2);
/* 484 */       this.levelViewSettings.levelNavigatorWindowChanged();
/*     */     } 
/*     */     
/* 487 */     double baseLine = getNavigatorBaseline();
/* 488 */     Graphics2D secondBufferGraphics = (Graphics2D)this.secondBuffer.getGraphics();
/* 489 */     secondBufferGraphics.clearRect(0, 0, this.secondBuffer.getWidth(), this.secondBuffer.getHeight());
/* 490 */     drawBackgrounds(secondBufferGraphics);
/* 491 */     drawFinishLine(secondBufferGraphics);
/* 492 */     drawBaseLine(secondBufferGraphics, (int)baseLine);
/* 493 */     drawLevelObjects(secondBufferGraphics, baseLine);
/* 494 */     if (isDrawBlockSizedGrid()) {
/* 495 */       drawGrid(secondBufferGraphics, baseLine);
/*     */     }
/* 497 */     secondBufferGraphics.finalize();
/*     */     
/* 499 */     synchronized (this.lock) {
/* 500 */       BufferedImage tmp = this.currentBuffer;
/* 501 */       this.currentBuffer = this.secondBuffer;
/* 502 */       this.secondBuffer = tmp;
/*     */     } 
/*     */     
/* 505 */     repaint();
/*     */   }
/*     */   
/*     */   public double getNavigatorBaseline() {
/* 509 */     return 0.95D * getHeight();
/*     */   }
/*     */   
/*     */   public void clearBackgrounds() {
/* 513 */     this.levelViewSettings.clearBackgrounds();
/*     */   }
/*     */   
/*     */   public int getBufferWidth() {
/* 517 */     if (this.currentBuffer == null)
/* 518 */       return 0; 
/* 519 */     return this.currentBuffer.getWidth();
/*     */   }
/*     */   
/*     */   public int getBufferHeight() {
/* 523 */     if (this.currentBuffer == null)
/* 524 */       return 0; 
/* 525 */     return this.currentBuffer.getHeight();
/*     */   }
/*     */   
/*     */   public int getGridSize() {
/* 529 */     return this.gridSize;
/*     */   }
/*     */   private class ResizedListener extends ComponentAdapter { private ResizedListener() {}
/*     */     
/*     */     public void componentResized(ComponentEvent e) {
/* 534 */       LevelNavigator.this.updateImage();
/*     */     } }
/*     */   
/*     */   private class MouseClickListener extends MouseAdapter {
/*     */     private MouseClickListener() {}
/*     */     
/*     */     public void mousePressed(MouseEvent e) {
/* 541 */       if (!e.isControlDown()) {
/* 542 */         LevelNavigator.this.levelViewSettings.mousePressed(e.getX(), e.getY(), e.getButton());
/*     */       }
/*     */     }
/*     */     
/*     */     public void mouseReleased(MouseEvent e) {
/* 547 */       if (e.getButton() == 1)
/* 548 */         if (e.isControlDown()) {
/* 549 */           LevelNavigator.this.calculateJumpGuidePosition(e.getX(), e.getY());
/* 550 */           LevelNavigator.this.repaint();
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 555 */           boolean found = false;
/*     */           
/* 557 */           if (!LevelNavigator.this.levelViewSettings.isPlacingPit() && LevelNavigator.this.RED_X_RECT.getY() <= e.getY() && LevelNavigator.this.RED_X_RECT.getMaxY() + 3.0D >= e.getY())
/*     */           {
/* 559 */             found = LevelNavigator.this.levelViewSettings.tryRemoveUserMark((int)(e.getX() - LevelNavigator.this.RED_X_RECT.getWidth() * 0.5D), (int)LevelNavigator.this.RED_X_RECT.getWidth() + 1);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 564 */           if (!found) {
/* 565 */             LevelNavigator.this.levelViewSettings.mouseReleased(e.getX(), e.getY());
/*     */           }
/*     */         }  
/*     */     }
/*     */   }
/*     */   
/*     */   private void calculateJumpGuidePosition(int x, int y) {
/* 572 */     this.jumpGuidePosition = this.levelViewSettings.getClickPosition(x, y);
/* 573 */     this.jumpGuidePosition = this.levelViewSettings.adjustJumpGuide((int)this.jumpGuidePosition.getX(), (int)this.jumpGuidePosition.getY());
/*     */   }
/*     */   
/*     */   private class MouseMotionListener extends MouseAdapter {
/*     */     private MouseMotionListener() {}
/*     */     
/*     */     public void mouseMoved(MouseEvent e) {
/* 580 */       if (LevelNavigator.this.levelViewSettings == null)
/*     */         return; 
/* 582 */       LevelNavigator.this.mousePosition = LevelNavigator.this.getMousePosition();
/* 583 */       if (LevelNavigator.this.dragViewMousePos != null && LevelNavigator.this.mousePosition != null) {
/* 584 */         double delta = (LevelNavigator.this.dragViewMousePos.x - LevelNavigator.this.mousePosition.x);
/* 585 */         delta = Math.signum(delta) * Math.pow(Math.abs(delta), 1.2D);
/* 586 */         LevelNavigator.this.levelViewSettings.setNewCurrentPositionXFromNavigator(LevelNavigator.this.dragViewScrollPos.x + (int)(delta / LevelNavigator.this.levelViewSettings.getRatio()));
/*     */       } 
/*     */     } }
/*     */   
/*     */   private class KeyboardListener implements KeyListener { private KeyboardListener() {}
/*     */     
/*     */     public void keyPressed(KeyEvent e) {
/* 593 */       if (LevelNavigator.this.levelViewSettings == null) {
/*     */         return;
/*     */       }
/* 596 */       if (e.isControlDown() || e.isMetaDown()) {
/* 597 */         switch (e.getKeyCode()) {
/*     */           case 78:
/* 599 */             LevelNavigator.this.editor.newFileButton.getActionListeners()[0].actionPerformed(null);
/*     */             break;
/*     */           case 79:
/* 602 */             LevelNavigator.this.editor.openFileButton.getActionListeners()[0].actionPerformed(null);
/*     */             break;
/*     */           case 83:
/* 605 */             LevelNavigator.this.editor.saveFileButton.getActionListeners()[0].actionPerformed(null);
/*     */             break;
/*     */           case 65:
/* 608 */             LevelNavigator.this.editor.openAudioButton.getActionListeners()[0].actionPerformed(null);
/*     */             break;
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/* 614 */       switch (e.getKeyCode()) {
/*     */         case 88:
/* 616 */           if (LevelNavigator.this.levelViewSettings.isPlaying()) {
/* 617 */             LevelNavigator.this.levelViewSettings.placeUserMarkOnCurrentPlaybackPosition();
/*     */           }
/*     */           break;
/*     */         case 32:
/* 621 */           if (LevelNavigator.this.dragViewMousePos == null) {
/* 622 */             LevelNavigator.this.dragViewMousePos = LevelNavigator.this.getMousePosition();
/* 623 */             LevelNavigator.this.dragViewScrollPos = new Point(LevelNavigator.this.levelViewSettings.getCurrentXOffset(), 0);
/*     */           } 
/*     */           break;
/*     */         case 37:
/* 627 */           LevelNavigator.this.levelViewSettings.setNewCurrentPositionXFromNavigator(LevelNavigator.this.levelViewSettings.getCurrentXOffset() - (int)((LevelNavigator.this.getParent().getWidth() / 2) / LevelNavigator.this.levelViewSettings.getRatio()));
/*     */           break;
/*     */         case 39:
/* 630 */           LevelNavigator.this.levelViewSettings.setNewCurrentPositionXFromNavigator(LevelNavigator.this.levelViewSettings.getCurrentXOffset() + (int)((LevelNavigator.this.getParent().getWidth() / 2) / LevelNavigator.this.levelViewSettings.getRatio()));
/*     */           break;
/*     */         case 116:
/* 633 */           LevelNavigator.this.editor.launchInGameButton.getActionListeners()[0].actionPerformed(null);
/*     */           break;
/*     */         
/*     */         case 49:
/* 637 */           LevelNavigator.this.editor.blockButton.setSelected(true);
/* 638 */           LevelNavigator.this.editor.blockButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.blockButton, 0, null));
/*     */           break;
/*     */         case 50:
/* 641 */           LevelNavigator.this.editor.spikeButton.setSelected(true);
/* 642 */           LevelNavigator.this.editor.spikeButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.spikeButton, 0, null));
/*     */           break;
/*     */         case 51:
/* 645 */           LevelNavigator.this.editor.pitButton.setSelected(true);
/* 646 */           LevelNavigator.this.editor.pitButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.pitButton, 0, null));
/*     */           break;
/*     */         case 52:
/* 649 */           LevelNavigator.this.editor.deleteObjectButton.setSelected(true);
/* 650 */           LevelNavigator.this.editor.deleteObjectButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.deleteObjectButton, 0, null));
/*     */           break;
/*     */         case 53:
/* 653 */           LevelNavigator.this.editor.placeMusicMarkButton.setSelected(true);
/* 654 */           LevelNavigator.this.editor.placeMusicMarkButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.placeMusicMarkButton, 0, null));
/*     */           break;
/*     */         case 54:
/* 657 */           LevelNavigator.this.editor.placeLevelEndButton.setSelected(true);
/* 658 */           LevelNavigator.this.editor.placeLevelEndButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.placeLevelEndButton, 0, null));
/*     */           break;
/*     */         case 55:
/* 661 */           LevelNavigator.this.editor.placeBackgroundButton.setSelected(true);
/* 662 */           LevelNavigator.this.editor.placeBackgroundButton.getActionListeners()[0].actionPerformed(new ActionEvent(LevelNavigator.this.editor.placeBackgroundButton, 0, null));
/*     */           break;
/*     */         
/*     */         case 73:
/* 666 */           LevelNavigator.this.editor.rewindMusicButton.getActionListeners()[0].actionPerformed(null);
/*     */           break;
/*     */         case 79:
/* 669 */           LevelNavigator.this.editor.playFromMarkButton.getActionListeners()[0].actionPerformed(null);
/*     */           break;
/*     */         case 80:
/* 672 */           LevelNavigator.this.editor.playPauseButton.getActionListeners()[0].actionPerformed(null);
/*     */           break;
/*     */         
/*     */         case 66:
/* 676 */           LevelNavigator.this.editor.backgroundTypeComboBox.setSelectedIndex((LevelNavigator.this.editor.backgroundTypeComboBox.getSelectedIndex() + 1) % LevelNavigator.this.editor.backgroundTypeComboBox.getItemCount());
/*     */           break;
/*     */       } 
/*     */     }
/*     */     
/*     */     public void keyReleased(KeyEvent e) {
/* 682 */       if (LevelNavigator.this.levelViewSettings == null) {
/*     */         return;
/*     */       }
/* 685 */       switch (e.getKeyCode()) {
/*     */         case 32:
/* 687 */           LevelNavigator.this.dragViewMousePos = null;
/*     */           break;
/*     */       } 
/*     */     }
/*     */     
/*     */     public void keyTyped(KeyEvent e) {} }
/*     */ 
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\gui\components\LevelNavigator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */