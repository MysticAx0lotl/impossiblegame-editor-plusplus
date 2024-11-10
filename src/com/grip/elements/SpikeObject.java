/*     */ package com.grip.elements;
/*     */ 
/*     */ import com.grip.gui.library.GraphicsRepository;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SpikeObject
/*     */   extends LevelObject
/*     */ {
/*     */   public SpikeObject(int x, int y) {
/*  12 */     super(x, y);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getTypeId() {
/*  17 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Integer> getValuesForSaving() {
/*  22 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getImageName() {
/*  27 */     return "spike";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean drawObject(ImageObserver imageObserver, Graphics2D graph, GraphicsRepository graphicsRepository, double baseLine, double scaleRatio, int currentXStart, int scaledTargetWidth) {
/*  35 */     int width = getWidth();
/*  36 */     int height = getHeight();
/*     */     
/*  38 */     if (!isVisible(currentXStart, scaledTargetWidth)) {
/*  39 */       return false;
/*     */     }
/*  41 */     graph.drawImage(graphicsRepository.getFragment(getImageName()).getFragmentImage(), (int)Math.floor((getX() - currentXStart) * scaleRatio), (int)Math.floor(baseLine + 1.0D - (getY() + height) * scaleRatio), (int)Math.ceil(width * scaleRatio), (int)Math.ceil(height * scaleRatio), imageObserver);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  46 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isVisible(int currentXStart, int scaledTargetWidth) {
/*  53 */     int width = getWidth();
/*  54 */     if (getX() < currentXStart - width) {
/*  55 */       return false;
/*     */     }
/*  57 */     if (getX() > currentXStart + width + scaledTargetWidth) {
/*  58 */       return false;
/*     */     }
/*  60 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWidth() {
/*  65 */     return getWidthStatic();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/*  70 */     return getHeightStatic();
/*     */   }
/*     */   
/*     */   public static int getWidthStatic() {
/*  74 */     return 30;
/*     */   }
/*     */   
/*     */   public static int getHeightStatic() {
/*  78 */     return 30;
/*     */   }
/*     */   
/*     */   public static int getCollisionWidthStatic() {
/*  82 */     return getWidthStatic();
/*     */   }
/*     */   
/*     */   public static int getCollisionHeightStatic() {
/*  86 */     return getHeightStatic();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionX() {
/*  91 */     return getX();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionY() {
/*  96 */     return getY();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionWidth() {
/* 101 */     return getWidth();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionHeight() {
/* 106 */     return getHeight();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isColliding(int collisionX, int collisionY, int collisionWidth, int collisionHeight) {
/* 111 */     int diffX = collisionX - getCollisionX();
/*     */     
/* 113 */     if (diffX > 0 && diffX >= getCollisionWidth()) {
/* 114 */       return false;
/*     */     }
/* 116 */     if (diffX < 0 && -diffX >= collisionWidth) {
/* 117 */       return false;
/*     */     }
/* 119 */     int diffY = collisionY - getCollisionY();
/* 120 */     if (diffY > 0 && diffY >= getCollisionHeight()) {
/* 121 */       return false;
/*     */     }
/*     */     
/* 124 */     if (diffY < 0 && -diffY >= collisionHeight) {
/* 125 */       return false;
/*     */     }
/*     */     
/* 128 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean tryMergeWith(LevelObject levelObject) {
/* 133 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\SpikeObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */