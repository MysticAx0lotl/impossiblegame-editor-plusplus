/*     */ package com.grip.level;
/*     */ import com.grip.elements.BackgroundChange;
/*     */ import com.grip.elements.BackgroundChangeInternal;
/*     */ import com.grip.elements.BlocksFall;
/*     */ import com.grip.elements.BlocksRise;
/*     */ import com.grip.elements.GravityChange;
/*     */ import com.grip.elements.LevelObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class LevelDefinition {
/*  14 */   private final int START_OFFSET = 810;
/*     */   private boolean fileLoaded = false;
/*     */   private boolean usesSpecialGraphics;
/*  17 */   private final List<LevelObject> levelObjects = new ArrayList<LevelObject>();
/*     */   private int levelEnd;
/*  19 */   private final List<BackgroundChange> backgroundChanges = new ArrayList<BackgroundChange>();
/*  20 */   private final List<GravityChange> gravityChanges = new ArrayList<GravityChange>();
/*  21 */   private final List<BlocksFall> blocksFallList = new ArrayList<BlocksFall>();
/*  22 */   private final List<BlocksRise> blocksRiseList = new ArrayList<BlocksRise>();
/*  23 */   private LevelViewSettings levelViewSettings = null;
/*     */   
/*     */   public void attachLevelViewSettings(LevelViewSettings levelViewSettings) {
/*  26 */     this.levelViewSettings = levelViewSettings;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean newLevel() {
/*  31 */     getBackgroundChanges().clear();
/*  32 */     getLevelObjects().clear();
/*  33 */     getGravityChanges().clear();
/*  34 */     getBlocksFallList().clear();
/*  35 */     getBlocksRiseList().clear();
/*  36 */     this.usesSpecialGraphics = false;
/*  37 */     int gridSize = 30;
/*  38 */     this.levelEnd = 100 * gridSize + gridSize / 2;
/*     */ 
/*     */ 
/*     */     
/*  42 */     finishLoading();
/*     */     
/*  44 */     if (this.levelViewSettings != null) {
/*  45 */       this.levelViewSettings.levelDefinitionHasNewFile();
/*     */     }
/*     */     
/*  48 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean openFile(String file) throws IOException {
/*  54 */     LevelDefinition levelDefinition = new LevelDefinition();
/*     */     
/*  56 */     boolean result = LevelLoader.load(file, levelDefinition);
/*     */     
/*  58 */     if (result) {
/*  59 */       getBackgroundChanges().clear();
/*  60 */       getLevelObjects().clear();
/*  61 */       getGravityChanges().clear();
/*  62 */       getBlocksFallList().clear();
/*  63 */       getBlocksRiseList().clear();
/*  64 */       this.usesSpecialGraphics = false;
/*  65 */       this.levelEnd = -1;
/*     */       
/*  67 */       getBackgroundChanges().addAll(levelDefinition.getBackgroundChanges());
/*  68 */       getLevelObjects().addAll(levelDefinition.getLevelObjects());
/*  69 */       getGravityChanges().addAll(levelDefinition.getGravityChanges());
/*  70 */       getBlocksFallList().addAll(levelDefinition.getBlocksFallList());
/*  71 */       getBlocksRiseList().addAll(levelDefinition.getBlocksRiseList());
/*  72 */       this.usesSpecialGraphics = levelDefinition.isUsesSpecialGraphics();
/*  73 */       this.levelEnd = levelDefinition.getLevelEnd();
/*     */       
/*  75 */       if (!this.fileLoaded) {
/*  76 */         finishLoading();
/*     */       }
/*  78 */       if (this.levelViewSettings != null) {
/*  79 */         this.levelViewSettings.levelDefinitionHasNewFile();
/*     */       }
/*     */     } 
/*  82 */     return result;
/*     */   }
/*     */   
/*     */   public boolean saveFile(String file) throws IOException {
/*  86 */     if (!this.fileLoaded) {
/*  87 */       return false;
/*     */     }
/*  89 */     sortAll();
/*     */     
/*  91 */     return LevelSaver.saveVersion0(file, this.usesSpecialGraphics, this.levelObjects, this.levelEnd, this.backgroundChanges, this.gravityChanges, this.blocksFallList, this.blocksRiseList);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUsesSpecialGraphics(boolean usesSpecialGraphics) {
/*  96 */     this.usesSpecialGraphics = usesSpecialGraphics;
/*     */   }
/*     */   
/*     */   public boolean isCollidingWithExisting(int x, int y, int collisionWidth, int collisionHeight) {
/* 100 */     return (getFirstColliding(x, y, collisionWidth, collisionHeight) != null);
/*     */   }
/*     */   
/*     */   public LevelObject getFirstColliding(int x, int y, int collisionWidth, int collisionHeight) {
/* 104 */     if (x < 0) {
/* 105 */       return null;
/*     */     }
/*     */     
/* 108 */     for (LevelObject levelObject : this.levelObjects) {
/* 109 */       if (levelObject.isColliding(x, y, collisionWidth, collisionHeight)) {
/* 110 */         return levelObject;
/*     */       }
/*     */     } 
/*     */     
/* 114 */     return null;
/*     */   }
/*     */   
/*     */   public boolean addBlock(int x, int y) {
/* 118 */     if (x < 810 || y < 0) {
/* 119 */       return false;
/*     */     }
/* 121 */     if (this.fileLoaded && isCollidingWithExisting(x, y, BlockObject.getCollisionWidthStatic(), BlockObject.getCollisionHeightStatic()))
/*     */     {
/* 123 */       return false;
/*     */     }
/* 125 */     this.levelObjects.add(new BlockObject(x, y));
/* 126 */     return true;
/*     */   }
/*     */   
/*     */   public boolean addSpike(int x, int y) {
/* 130 */     if (x < 810 || y < 0) {
/* 131 */       return false;
/*     */     }
/* 133 */     if (this.fileLoaded && isCollidingWithExisting(x, y, SpikeObject.getCollisionWidthStatic(), SpikeObject.getCollisionHeightStatic()))
/*     */     {
/* 135 */       return false;
/*     */     }
/* 137 */     this.levelObjects.add(new SpikeObject(x, y));
/* 138 */     return true;
/*     */   }
/*     */   public boolean addPit(int x, int y) {
/*     */     LevelObject levelObject;
/* 142 */     if (x < 810 || y < 0) {
/* 143 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     PitObject pitObject = new PitObject(x, y);
/* 151 */     boolean merged = false;
/* 152 */     List<LevelObject> toRemove = new ArrayList<LevelObject>();
/* 153 */     for (LevelObject levelObject1 : this.levelObjects) {
/* 154 */       if (levelObject1.tryMergeWith((LevelObject)pitObject)) {
/* 155 */         if (merged) {
/* 156 */           toRemove.add(pitObject);
/*     */         }
/* 158 */         levelObject = levelObject1;
/* 159 */         merged = true;
/*     */       } 
/*     */     } 
/*     */     
/* 163 */     this.levelObjects.removeAll(toRemove);
/*     */     
/* 165 */     if (!merged)
/* 166 */       this.levelObjects.add(levelObject); 
/* 167 */     return true;
/*     */   }
/*     */   
/*     */   public void deleteObject(LevelObject toDelete) {
/* 171 */     this.levelObjects.remove(toDelete);
/*     */   }
/*     */   
/*     */   public void setLevelEnd(int levelEnd) {
/* 175 */     this.levelEnd = levelEnd;
/*     */   }
/*     */   
/*     */   public void removeBackgroundChangesAfterX(int x) {
/* 179 */     boolean changed = true;
/* 180 */     while (changed) {
/* 181 */       changed = false;
/* 182 */       for (BackgroundChange change : this.backgroundChanges) {
/* 183 */         if (change.getX() >= x) {
/* 184 */           this.backgroundChanges.remove(change);
/* 185 */           changed = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addBackgroundChangeInternal(int x, int bgId) {
/* 193 */     removeBackgroundChangesAfterX(x);
/* 194 */     BackgroundChangeInternal newChange = new BackgroundChangeInternal(x, bgId);
/*     */     
/* 196 */     if (this.backgroundChanges.isEmpty()) {
/* 197 */       this.backgroundChanges.add(newChange);
/*     */       
/*     */       return;
/*     */     } 
/* 201 */     BackgroundChange lastChange = this.backgroundChanges.get(this.backgroundChanges.size() - 1);
/* 202 */     if (!lastChange.getImageTitle().equals(newChange.getImageTitle())) {
/* 203 */       this.backgroundChanges.add(newChange);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addBackgroundChangeCustom(int x, String bgName) {
/* 208 */     removeBackgroundChangesAfterX(x);
/* 209 */     this.backgroundChanges.add(new BackgroundChangeCustom(x, bgName));
/*     */   }
/*     */   
/*     */   public void addGravityChange(int x) {
/* 213 */     this.gravityChanges.add(new GravityChange(x));
/*     */   }
/*     */   
/*     */   public void addBlocksFalling(int startX, int startY) {
/* 217 */     this.blocksFallList.add(new BlocksFall(startX, startY));
/*     */   }
/*     */   
/*     */   public void addBlocksRising(int startX, int startY) {
/* 221 */     this.blocksRiseList.add(new BlocksRise(startX, startY));
/*     */   }
/*     */   
/*     */   public boolean isUsesSpecialGraphics() {
/* 225 */     return this.usesSpecialGraphics;
/*     */   }
/*     */   
/*     */   public List<LevelObject> getLevelObjects() {
/* 229 */     return this.levelObjects;
/*     */   }
/*     */   
/*     */   public int getLevelEnd() {
/* 233 */     return this.levelEnd;
/*     */   }
/*     */   
/*     */   public List<BackgroundChange> getBackgroundChanges() {
/* 237 */     return this.backgroundChanges;
/*     */   }
/*     */   
/*     */   public List<GravityChange> getGravityChanges() {
/* 241 */     return this.gravityChanges;
/*     */   }
/*     */   
/*     */   public List<BlocksFall> getBlocksFallList() {
/* 245 */     return this.blocksFallList;
/*     */   }
/*     */   
/*     */   public List<BlocksRise> getBlocksRiseList() {
/* 249 */     return this.blocksRiseList;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 254 */     if (other == null || other.getClass() != getClass()) {
/* 255 */       return false;
/*     */     }
/* 257 */     LevelDefinition otherLevelDefinition = (LevelDefinition)other;
/* 258 */     if (this.usesSpecialGraphics != otherLevelDefinition.usesSpecialGraphics) {
/* 259 */       return false;
/*     */     }
/* 261 */     if (!this.levelObjects.equals(otherLevelDefinition.levelObjects)) {
/* 262 */       return false;
/*     */     }
/* 264 */     if (this.levelEnd != otherLevelDefinition.levelEnd) {
/* 265 */       return false;
/*     */     }
/* 267 */     if (!this.backgroundChanges.equals(otherLevelDefinition.backgroundChanges)) {
/* 268 */       return false;
/*     */     }
/* 270 */     if (!this.gravityChanges.equals(otherLevelDefinition.gravityChanges)) {
/* 271 */       return false;
/*     */     }
/* 273 */     if (!this.blocksFallList.equals(otherLevelDefinition.blocksFallList)) {
/* 274 */       return false;
/*     */     }
/* 276 */     if (!this.blocksRiseList.equals(otherLevelDefinition.blocksRiseList)) {
/* 277 */       return false;
/*     */     }
/* 279 */     return true;
/*     */   }
/*     */   
/*     */   public int getActualLevelEnd() {
/* 283 */     return getLevelEnd() - 300;
/*     */   }
/*     */   
/*     */   public void setActualLevelEnd(int x) {
/* 287 */     setLevelEnd(x);
/*     */   }
/*     */   
/*     */   private void sortAll() {
/* 291 */     Collections.sort(this.levelObjects, new Comparator<LevelObject>()
/*     */         {
/*     */           public int compare(LevelObject o1, LevelObject o2) {
/* 294 */             return o1.getX() - o2.getX();
/*     */           }
/*     */         });
/*     */     
/* 298 */     Collections.sort(this.backgroundChanges, new Comparator<BackgroundChange>()
/*     */         {
/*     */           public int compare(BackgroundChange o1, BackgroundChange o2) {
/* 301 */             return o1.getX() - o2.getX();
/*     */           }
/*     */         });
/*     */     
/* 305 */     Collections.sort(this.gravityChanges, new Comparator<GravityChange>()
/*     */         {
/*     */           public int compare(GravityChange o1, GravityChange o2) {
/* 308 */             return o1.getX() - o2.getX();
/*     */           }
/*     */         });
/*     */     
/* 312 */     Collections.sort(this.blocksFallList, new Comparator<BlocksFall>()
/*     */         {
/*     */           public int compare(BlocksFall o1, BlocksFall o2) {
/* 315 */             return o1.getX() - o2.getX();
/*     */           }
/*     */         });
/*     */     
/* 319 */     Collections.sort(this.blocksRiseList, new Comparator<BlocksRise>()
/*     */         {
/*     */           public int compare(BlocksRise o1, BlocksRise o2) {
/* 322 */             return o1.getX() - o2.getX();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void finishLoading() {
/* 328 */     sortAll();
/* 329 */     this.fileLoaded = true;
/*     */   }
/*     */   
/*     */   public boolean isFileLoaded() {
/* 333 */     return this.fileLoaded;
/*     */   }
/*     */   
/*     */   public int getStartOffset() {
/* 337 */     return 810;
/*     */   }
/*     */   
/*     */   public void clearBackgroundChanges() {
/* 341 */     this.backgroundChanges.clear();
/*     */   }
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\level\LevelDefinition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */