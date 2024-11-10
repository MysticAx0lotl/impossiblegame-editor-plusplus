/*     */ package com.grip.level;
/*     */ import com.grip.audio.AudioFile;
/*     */ import com.grip.elements.BackgroundChange;
/*     */ import com.grip.elements.BackgroundChangeInternal;
/*     */ import com.grip.elements.BlockObject;
/*     */ import com.grip.elements.LevelObject;
/*     */ import com.grip.elements.PitObject;
/*     */ import com.grip.elements.SpikeObject;
/*     */ import com.grip.gui.components.LevelNavigator;
/*     */ import com.grip.gui.components.LevelNavigatorScrollBar;
/*     */ import com.grip.gui.library.GraphicsRepository;
/*     */ import java.awt.Point;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class LevelViewSettings {
/*     */   private final LevelDefinition levelDefinition;
/*     */   private final LevelNavigator levelNavigator;
/*  20 */   private final List<LevelObject> visibleLevelObjects = new ArrayList<LevelObject>(); private final LevelNavigatorScrollBar levelNavigatorScrollBar; private final GraphicsRepository graphicsRepository;
/*  21 */   private final int PLAYER_OFFSET = 150;
/*  22 */   private final byte blockTypeId = (new BlockObject(0, 0)).getTypeId();
/*  23 */   private final String DEFAULT_BACKGROUND = (new BackgroundChangeInternal(0, 0)).getImageTitle();
/*     */   
/*  25 */   private int targetHeight = 0;
/*  26 */   private int targetWidth = 0;
/*  27 */   private int currentXOffset = 0;
/*  28 */   private int oldNavigatorHeight = -1;
/*  29 */   private int oldNavigatorWidth = -1;
/*     */   private LevelObject.ObjectType selectedLevelObjectType;
/*     */   private boolean snapToGrid = true;
/*     */   private int pitStart;
/*  33 */   private int markPosition = 0;
/*  34 */   private int backgroundType = 0;
/*     */   
/*  36 */   private TreeSet<Integer> userMarks = new TreeSet<Integer>();
/*     */   
/*  38 */   private AudioFile audioFile = null;
/*  39 */   private final int UNITS_PER_SECOND = 360;
/*     */   
/*     */   private boolean wasPlaying = false;
/*     */ 
/*     */   
/*     */   public LevelViewSettings(GraphicsRepository graphicsRepository, LevelDefinition levelDefinition, LevelNavigator levelNavigator, LevelNavigatorScrollBar levelNavigatorScrollBar) {
/*  45 */     this.levelDefinition = levelDefinition;
/*  46 */     this.levelNavigator = levelNavigator;
/*  47 */     this.levelNavigatorScrollBar = levelNavigatorScrollBar;
/*  48 */     this.graphicsRepository = graphicsRepository;
/*     */     
/*  50 */     updateDimensions();
/*  51 */     updateVisibleObjects();
/*  52 */     this.levelNavigator.setLevelViewSettings(this);
/*  53 */     this.levelDefinition.attachLevelViewSettings(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateShift() {
/*  58 */     this.currentXOffset++;
/*  59 */     this.levelNavigator.updateImage();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateDimensions() {
/*  64 */     for (BackgroundChange backgroundChange : this.levelDefinition.getBackgroundChanges()) {
/*  65 */       String imageTitle = backgroundChange.getImageTitle();
/*  66 */       this.targetHeight = Math.max(this.graphicsRepository.getFragment(imageTitle).getHeight(), this.targetHeight);
/*     */     } 
/*     */     
/*  69 */     this.targetWidth = this.levelDefinition.getActualLevelEnd();
/*     */   }
/*     */   
/*     */   public int getActualLevelEnd() {
/*  73 */     return this.levelDefinition.getActualLevelEnd();
/*     */   }
/*     */   
/*     */   public int getTargetWidth() {
/*  77 */     return this.targetWidth;
/*     */   }
/*     */   
/*     */   public int getTargetHeight() {
/*  81 */     return this.targetHeight;
/*     */   }
/*     */   
/*     */   public List<LevelObject> getVisibleLevelObjects() {
/*  85 */     levelNavigatorWindowChanged();
/*     */     
/*  87 */     return this.visibleLevelObjects;
/*     */   }
/*     */   
/*     */   public void levelNavigatorWindowChanged() {
/*  91 */     int levelNavigatorHeight = this.levelNavigator.getBufferHeight();
/*  92 */     int levelNavigatorWidth = this.levelNavigator.getBufferWidth();
/*  93 */     if (levelNavigatorWidth != this.oldNavigatorWidth || levelNavigatorHeight != this.oldNavigatorHeight) {
/*  94 */       this.oldNavigatorHeight = this.levelNavigator.getBufferHeight();
/*  95 */       this.oldNavigatorWidth = this.levelNavigator.getBufferWidth();
/*  96 */       updateVisibleObjects();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateVisibleObjects() {
/* 101 */     this.visibleLevelObjects.clear();
/*     */     
/* 103 */     int dataSpaceWidth = getDataSpaceWidth();
/*     */     
/* 105 */     for (LevelObject levelObject : this.levelDefinition.getLevelObjects()) {
/* 106 */       if (levelObject.isVisible(this.currentXOffset, dataSpaceWidth))
/* 107 */         this.visibleLevelObjects.add(levelObject); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getRatio() {
/* 112 */     int height = (getBackgrounds().size() > 0) ? getTargetHeight() : this.graphicsRepository.getFragment(this.DEFAULT_BACKGROUND).getHeight();
/*     */     
/* 114 */     return this.levelNavigator.getBufferHeight() / height;
/*     */   }
/*     */   
/*     */   public int getCurrentXOffset() {
/* 118 */     return this.currentXOffset;
/*     */   }
/*     */   
/*     */   public void setBackgroundType(int type) {
/* 122 */     this.backgroundType = type;
/*     */   }
/*     */   
/*     */   public void setNewCurrentPositionXFromScrollbar(int newX) {
/* 126 */     setNewCurrentPositionX(newX);
/*     */   }
/*     */   
/*     */   public void setNewCurrentPositionXFromNavigator(int newX) {
/* 130 */     setNewCurrentPositionX(newX);
/* 131 */     if (this.levelNavigatorScrollBar != null) {
/* 132 */       this.levelNavigatorScrollBar.updateCurrentPosition();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNewCurrentPositionXFromMark(int newX) {
/* 137 */     setNewCurrentPositionX(newX);
/* 138 */     if (this.levelNavigatorScrollBar != null) {
/* 139 */       this.levelNavigatorScrollBar.updateCurrentPosition();
/*     */     }
/*     */   }
/*     */   
/*     */   private void setNewCurrentPositionX(int newX) {
/* 144 */     this.currentXOffset = Math.min(this.levelDefinition.getLevelEnd(), Math.max(0, newX));
/* 145 */     updateVisibleObjects();
/* 146 */     this.levelNavigator.updateImage();
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 150 */     return this.levelNavigator.getBufferWidth();
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 154 */     return this.levelNavigator.getBufferHeight();
/*     */   }
/*     */   
/*     */   public int getDataSpaceWidth() {
/* 158 */     return (int)(this.levelNavigator.getBufferWidth() / getRatio());
/*     */   }
/*     */   
/*     */   public void levelDefinitionHasNewFile() {
/* 162 */     this.currentXOffset = 0;
/* 163 */     this.markPosition = 0;
/* 164 */     updateDimensions();
/* 165 */     updateVisibleObjects();
/* 166 */     this.levelNavigatorScrollBar.setFullContentsDimensions(this.levelDefinition.getLevelEnd());
/* 167 */     this.levelNavigator.updateImage();
/*     */   }
/*     */   
/*     */   public void setSelectedLevelObjectType(LevelObject.ObjectType selectedLevelObjectType) {
/* 171 */     this.selectedLevelObjectType = selectedLevelObjectType;
/*     */   }
/*     */   
/*     */   public void setSnapToGrid(boolean bSnapToGrid) {
/* 175 */     this.snapToGrid = bSnapToGrid;
/*     */   }
/*     */   
/*     */   public void mousePressed(int x, int y, int button) {
/* 179 */     if (button == 3) {
/* 180 */       LevelObject.ObjectType oldSelectedType = this.selectedLevelObjectType;
/* 181 */       this.selectedLevelObjectType = LevelObject.ObjectType.DELETE;
/* 182 */       mousePressedInPlacementMode(x, y);
/* 183 */       this.selectedLevelObjectType = oldSelectedType;
/* 184 */     } else if (button == 1) {
/* 185 */       mousePressedInPlacementMode(x, y);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Point getClickPosition(int x, int y) {
/* 190 */     double ratio = getRatio();
/* 191 */     x = Math.max(0, (int)(x / ratio + this.currentXOffset));
/* 192 */     double tmpY = this.levelNavigator.getNavigatorBaseline() - y;
/* 193 */     y = (int)(tmpY / ratio);
/* 194 */     return new Point(x, y);
/*     */   }
/*     */   public void mousePressedInPlacementMode(int x, int y) {
/*     */     LevelObject toDelete;
/* 198 */     Point clickPosition = getClickPosition(x, y);
/* 199 */     x = (int)clickPosition.getX();
/* 200 */     y = (int)clickPosition.getY();
/*     */     
/* 202 */     if (this.snapToGrid && this.selectedLevelObjectType != LevelObject.ObjectType.DELETE) {
/* 203 */       x = x - x % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
/* 204 */       if (this.selectedLevelObjectType != LevelObject.ObjectType.PIT) {
/* 205 */         y = y - y % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
/*     */       }
/*     */     } 
/* 208 */     if ((x < 0 || y < 0) && this.selectedLevelObjectType != LevelObject.ObjectType.DELETE) {
/*     */       return;
/*     */     }
/* 211 */     this.pitStart = -1;
/* 212 */     int elementWidth = 0;
/*     */     
/* 214 */     switch (this.selectedLevelObjectType) {
/*     */       case BLOCK:
/* 216 */         if (!this.levelDefinition.addBlock(x - BlockObject.getWidthStatic() / 2, y - BlockObject.getHeightStatic() / 2)) {
/*     */           return;
/*     */         }
/* 219 */         elementWidth = BlockObject.getWidthStatic();
/*     */         break;
/*     */       case SPIKE:
/* 222 */         if (!this.levelDefinition.addSpike(x - SpikeObject.getWidthStatic() / 2, y - SpikeObject.getHeightStatic() / 2)) {
/*     */           return;
/*     */         }
/*     */         
/* 226 */         elementWidth = SpikeObject.getWidthStatic();
/*     */         break;
/*     */       case PIT:
/* 229 */         this.pitStart = x;
/*     */         break;
/*     */       case DELETE:
/* 232 */         toDelete = this.levelDefinition.getFirstColliding(x, y, 0, 0);
/* 233 */         if (toDelete == null) {
/* 234 */           toDelete = this.levelDefinition.getFirstColliding(x - 1 * this.levelNavigator.getGridSize(), y - 1 * this.levelNavigator.getGridSize(), 2 * this.levelNavigator.getGridSize(), 2 * this.levelNavigator.getGridSize());
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 239 */         if (toDelete != null) {
/* 240 */           this.levelDefinition.deleteObject(toDelete);
/*     */         }
/* 242 */         if (toDelete != null) {
/* 243 */           this.levelDefinition.deleteObject(toDelete);
/*     */         }
/*     */         break;
/*     */       case MARK:
/* 247 */         this.markPosition = Math.max(0, x - 150);
/*     */         break;
/*     */       case LEVELEND:
/* 250 */         this.levelDefinition.setActualLevelEnd(Math.max(10 * this.levelNavigator.getGridSize(), x));
/* 251 */         this.levelNavigatorScrollBar.setFullContentsDimensions(this.levelDefinition.getLevelEnd());
/*     */         break;
/*     */       case BACKGROUND:
/* 254 */         this.levelDefinition.addBackgroundChangeInternal(x - 300, this.backgroundType);
/*     */         
/* 256 */         updateDimensions();
/*     */         break;
/*     */     } 
/*     */     
/* 260 */     switch (this.selectedLevelObjectType) {
/*     */       case BLOCK:
/*     */       case SPIKE:
/*     */       case PIT:
/*     */       case BACKGROUND:
/* 265 */         updateLevelEndAfterPlacing(x, elementWidth);
/*     */         break;
/*     */     } 
/*     */     
/* 269 */     updateVisibleObjects();
/* 270 */     this.levelNavigator.updateImage();
/* 271 */     this.levelNavigator.invalidate();
/* 272 */     this.levelNavigator.repaint();
/*     */   }
/*     */   
/*     */   public void updateLevelEndAfterPlacing(int x, int elementWidth) {
/* 276 */     if (x + elementWidth > getLevelEnd()) {
/* 277 */       this.levelDefinition.setActualLevelEnd(Math.max(10 * this.levelNavigator.getGridSize(), x + elementWidth / 2));
/*     */       
/* 279 */       this.levelNavigatorScrollBar.setFullContentsDimensions(this.levelDefinition.getLevelEnd());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void mouseReleased(int x, int y) {
/* 285 */     if (x > this.levelNavigator.getBufferWidth()) {
/* 286 */       this.pitStart = -1;
/*     */       
/*     */       return;
/*     */     } 
/* 290 */     if (this.selectedLevelObjectType != LevelObject.ObjectType.PIT || this.pitStart < 0) {
/*     */       return;
/*     */     }
/*     */     
/* 294 */     y = 10;
/* 295 */     int tmpPitStart = this.pitStart;
/* 296 */     this.pitStart = 0;
/* 297 */     double ratio = getRatio();
/* 298 */     x = (int)(x / ratio + this.currentXOffset);
/*     */ 
/*     */     
/* 301 */     double tmpY = this.levelNavigator.getNavigatorBaseline() - y;
/*     */ 
/*     */     
/* 304 */     y = (int)(tmpY / ratio);
/*     */     
/* 306 */     if (this.snapToGrid) {
/* 307 */       x = x - x % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
/* 308 */       tmpPitStart = tmpPitStart - tmpPitStart % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
/* 309 */       if (this.selectedLevelObjectType != LevelObject.ObjectType.PIT) {
/* 310 */         y = y - y % this.levelNavigator.getGridSize() + this.levelNavigator.getGridSize() / 2;
/*     */       }
/*     */     } else {
/* 313 */       x += this.levelNavigator.getGridSize() / 2;
/*     */     } 
/*     */     
/* 316 */     if (x < 0 || y < 0) {
/*     */       return;
/*     */     }
/* 319 */     switch (this.selectedLevelObjectType) {
/*     */       case PIT:
/* 321 */         if (tmpPitStart < x) {
/* 322 */           if (!this.levelDefinition.addPit(tmpPitStart - PitObject.getImageWidth() / 2, x - PitObject.getImageWidth() / 2)) {
/*     */             return;
/*     */           }
/*     */         }
/* 326 */         else if (!this.levelDefinition.addPit(x - PitObject.getImageWidth() / 2, tmpPitStart - PitObject.getImageWidth() / 2)) {
/*     */           return;
/*     */         } 
/*     */         
/* 330 */         updateLevelEndAfterPlacing(x, PitObject.getImageWidth());
/*     */         break;
/*     */     } 
/*     */     
/* 334 */     updateVisibleObjects();
/* 335 */     this.levelNavigator.updateImage();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLevelEnd() {
/* 340 */     return this.levelDefinition.getLevelEnd();
/*     */   }
/*     */   
/*     */   public void resetViewPosition() {
/* 344 */     setNewCurrentPositionXFromNavigator(0);
/*     */   }
/*     */   
/*     */   public void doPlayPause() {
/* 348 */     if (this.audioFile == null) {
/*     */       return;
/*     */     }
/*     */     
/* 352 */     System.out.println("Pre play/pause: " + (getPlaybackPositionInMilliseconds() / 1000.0D * 360.0D));
/* 353 */     this.audioFile.doPlayPause();
/* 354 */     System.out.println("Post play/pause: " + (getPlaybackPositionInMilliseconds() / 1000.0D * 360.0D));
/*     */   }
/*     */   
/*     */   public void doRewind() {
/* 358 */     if (this.audioFile == null)
/*     */       return; 
/* 360 */     this.audioFile.doRewind();
/* 361 */     resetViewPosition();
/* 362 */     this.levelNavigator.invalidate();
/* 363 */     this.levelNavigator.repaint();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void tickCurrentMillisecondPosition(int audioTickLength) {
/* 369 */     if (this.audioFile == null) {
/*     */       return;
/*     */     }
/* 372 */     if (this.wasPlaying != this.audioFile.isPlaying()) {
/* 373 */       this.levelNavigator.repaint();
/*     */     }
/*     */     
/* 376 */     if (this.audioFile.isPlaying()) {
/* 377 */       this.audioFile.tickCurrentMillisecondPosition(audioTickLength);
/* 378 */       this.levelNavigator.invalidate();
/* 379 */       this.levelNavigator.repaint();
/*     */     } 
/*     */     
/* 382 */     this.wasPlaying = this.audioFile.isPlaying();
/*     */   }
/*     */   
/*     */   public void tickAudio(int audioTickLength) {
/* 386 */     tickCurrentMillisecondPosition(audioTickLength);
/*     */   }
/*     */   
/*     */   public boolean isFileLoaded() {
/* 390 */     return this.levelDefinition.isFileLoaded();
/*     */   }
/*     */   
/*     */   public void doPlayFromMark() {
/* 394 */     if (this.audioFile == null)
/*     */       return; 
/* 396 */     this.audioFile.doPlayFromPosition(getMarkPositionToMilliseconds());
/* 397 */     setNewCurrentPositionXFromMark(Math.max(this.markPosition - 150, 0));
/* 398 */     this.levelNavigator.updateImage();
/* 399 */     this.levelNavigator.invalidate();
/* 400 */     this.levelNavigator.repaint();
/*     */   }
/*     */   
/*     */   private int getMarkPositionToMilliseconds() {
/* 404 */     return (int)(this.markPosition / 360.0F * 1000.0F);
/*     */   }
/*     */   
/*     */   public void deleteMark() {
/* 408 */     this.markPosition = 0;
/* 409 */     this.levelNavigator.invalidate();
/* 410 */     this.levelNavigator.repaint();
/* 411 */     this.audioFile.clearMark();
/*     */   }
/*     */   
/*     */   public int getDataPositionFromPlayback() {
/* 415 */     return (int)((this.audioFile != null) ? ((float)this.audioFile.getPlaybackPositionInMilliseconds() / 1000.0F * 360.0F) : 0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMarkPosition() {
/* 420 */     return this.markPosition;
/*     */   }
/*     */   
/*     */   public boolean setAudioFile(String filePath) {
/* 424 */     if (this.audioFile != null)
/* 425 */       this.audioFile.close(); 
/* 426 */     this.audioFile = AudioFileFactory.getAudioFile(filePath);
/*     */     
/* 428 */     return (this.audioFile != null);
/*     */   }
/*     */   
/*     */   public int getPlayerOffset() {
/* 432 */     return 150;
/*     */   }
/*     */   
/*     */   public boolean isPlaying() {
/* 436 */     return (this.audioFile != null && this.audioFile.isPlaying());
/*     */   }
/*     */   
/*     */   public double getPlaybackPositionInMilliseconds() {
/* 440 */     if (this.audioFile == null) {
/* 441 */       return 0.0D;
/*     */     }
/* 443 */     return this.audioFile.getPlaybackPositionInMilliseconds();
/*     */   }
/*     */   
/*     */   public int getStartLimit() {
/* 447 */     return this.levelDefinition.getStartOffset();
/*     */   }
/*     */   
/*     */   public Point adjustJumpGuide(int x, int y) {
/* 451 */     int gridSize = this.levelNavigator.getGridSize();
/* 452 */     LevelObject levelObject = null;
/* 453 */     while ((levelObject = this.levelDefinition.getFirstColliding(x, y, gridSize, gridSize)) != null) {
/* 454 */       if (levelObject.getTypeId() != this.blockTypeId) {
/* 455 */         return new Point(x, y);
/*     */       }
/*     */       
/* 458 */       y = (int)(levelObject.getY() + gridSize * 1.5F);
/*     */     } 
/*     */     
/* 461 */     return new Point(x, y);
/*     */   }
/*     */   
/*     */   public TreeSet<Integer> getUserMarks() {
/* 465 */     return this.userMarks;
/*     */   }
/*     */   
/*     */   public boolean tryRemoveUserMark(int xPosition, int redXWidth) {
/* 469 */     double ratio = getRatio();
/*     */     
/* 471 */     int currentPosition = (int)(xPosition / ratio - getCurrentXOffset() - getPlayerOffset());
/*     */     
/* 473 */     Integer actual = getClosestUserMark(currentPosition);
/*     */     
/* 475 */     if (actual == null) return false;
/*     */     
/* 477 */     if (actual != null && Math.abs(actual.intValue() - currentPosition) < redXWidth / ratio) {
/* 478 */       this.userMarks.remove(actual);
/* 479 */       this.levelNavigator.repaint();
/* 480 */       return true;
/*     */     } 
/*     */     
/* 483 */     return false;
/*     */   }
/*     */   
/*     */   public Integer getClosestUserMark(int position) {
/* 487 */     Integer ceiling = this.userMarks.ceiling(Integer.valueOf(position));
/* 488 */     Integer floor = this.userMarks.floor(Integer.valueOf(position));
/*     */     
/* 490 */     if (ceiling == null && floor == null) {
/* 491 */       return null;
/*     */     }
/*     */     
/* 494 */     Integer closest = ceiling;
/*     */     
/* 496 */     if (ceiling == null) {
/* 497 */       closest = floor;
/*     */     }
/* 499 */     else if (floor != null && Math.abs(floor.intValue() - position) < Math.abs(ceiling.intValue() - position)) {
/* 500 */       closest = floor;
/*     */     } 
/*     */     
/* 503 */     return closest;
/*     */   }
/*     */   
/*     */   public boolean isPlacingPit() {
/* 507 */     return (this.pitStart > 0);
/*     */   }
/*     */   
/*     */   public boolean placeUserMarkOnCurrentPlaybackPosition() {
/* 511 */     return placeUserMark(getDataPositionFromPlayback());
/*     */   }
/*     */   
/*     */   public boolean placeUserMark(int dataPosition) {
/* 515 */     Integer closest = getClosestUserMark(dataPosition);
/*     */     
/* 517 */     if (closest == null || Math.abs(dataPosition - closest.intValue()) > 30) {
/* 518 */       this.userMarks.add(Integer.valueOf(dataPosition));
/* 519 */       this.levelNavigator.repaint();
/* 520 */       return true;
/*     */     } 
/*     */     
/* 523 */     return false;
/*     */   }
/*     */   
/*     */   public void clearBackgrounds() {
/* 527 */     this.levelDefinition.clearBackgroundChanges();
/* 528 */     this.levelNavigator.repaint();
/*     */   }
/*     */   
/*     */   public List<BackgroundChange> getBackgrounds() {
/* 532 */     return this.levelDefinition.getBackgroundChanges();
/*     */   }
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\level\LevelViewSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */