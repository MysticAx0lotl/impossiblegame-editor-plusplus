/*     */ package com.grip.elements;
/*     */ 
/*     */ import com.grip.gui.library.GraphicsRepository;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.util.List;
/*     */ 
/*     */ public class BlockObject
/*     */   extends LevelObject
/*     */ {
/*     */   public BlockObject(int x, int y) {
/*  12 */     super(x, y);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getTypeId() {
/*  17 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Integer> getValuesForSaving() {
/*  22 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getImageName() {
/*  27 */     return "block";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean drawObject(ImageObserver imageObserver, Graphics2D graph, GraphicsRepository graphicsRepository, double baseLine, double scaleRatio, int currentXStart, int scaledTargetWidth) {
/*  36 */     int width = getWidth();
/*  37 */     int height = getHeight();
/*     */     
/*  39 */     if (!isVisible(currentXStart, scaledTargetWidth)) {
/*  40 */       return false;
/*     */     }
/*  42 */     graph.drawImage(graphicsRepository.getFragment(getImageName()).getFragmentImage(), (int)Math.floor((getX() - currentXStart) * scaleRatio), (int)Math.floor(baseLine + 1.0D - (getY() + height) * scaleRatio), (int)Math.ceil(width * scaleRatio), (int)Math.ceil(height * scaleRatio), imageObserver);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  47 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isVisible(int currentXStart, int scaledTargetWidth) {
/*  52 */     int width = getWidth();
/*  53 */     if (getX() < currentXStart - width) {
/*  54 */       return false;
/*     */     }
/*  56 */     if (getX() > currentXStart + width + scaledTargetWidth) {
/*  57 */       return false;
/*     */     }
/*  59 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWidth() {
/*  64 */     return getWidthStatic();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/*  69 */     return getHeightStatic();
/*     */   }
/*     */   
/*     */   public static int getWidthStatic() {
/*  73 */     return 30;
/*     */   }
/*     */   
/*     */   public static int getHeightStatic() {
/*  77 */     return 30;
/*     */   }
/*     */   
/*     */   public static int getCollisionWidthStatic() {
/*  81 */     return getWidthStatic();
/*     */   }
/*     */   
/*     */   public static int getCollisionHeightStatic() {
/*  85 */     return getHeightStatic();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionX() {
/*  90 */     return getX();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionY() {
/*  95 */     return getY();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionWidth() {
/* 100 */     return getWidth();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionHeight() {
/* 105 */     return getHeight();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isColliding(int collisionX, int collisionY, int collisionWidth, int collisionHeight) {
/* 110 */     int diffX = collisionX - getCollisionX();
/*     */     
/* 112 */     if (diffX > 0 && diffX >= getCollisionWidth()) {
/* 113 */       return false;
/*     */     }
/* 115 */     if (diffX < 0 && -diffX >= collisionWidth) {
/* 116 */       return false;
/*     */     }
/* 118 */     int diffY = collisionY - getCollisionY();
/* 119 */     if (diffY > 0 && diffY >= getCollisionHeight()) {
/* 120 */       return false;
/*     */     }
/*     */     
/* 123 */     if (diffY < 0 && -diffY >= collisionHeight) {
/* 124 */       return false;
/*     */     }
/*     */     
/* 127 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean tryMergeWith(LevelObject levelObject) {
/* 132 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\BlockObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */