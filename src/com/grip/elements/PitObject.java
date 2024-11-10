/*     */ package com.grip.elements;
/*     */ 
/*     */ import com.grip.gui.library.GraphicsRepository;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.util.List;
/*     */ 
/*     */ public class PitObject
/*     */   extends LevelObject
/*     */ {
/*     */   public PitObject(int x, int y) {
/*  12 */     super(x, y);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getTypeId() {
/*  17 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Integer> getValuesForSaving() {
/*  22 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getImageName() {
/*  27 */     return "pit";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean drawObject(ImageObserver imageObserver, Graphics2D graph, GraphicsRepository graphicsRepository, double baseLine, double scaleRatio, int currentXStart, int scaledTargetWidth) {
/*  35 */     int width = getImageWidth();
/*  36 */     int height = getHeight();
/*     */     
/*  38 */     if (!isVisible(currentXStart, scaledTargetWidth)) {
/*  39 */       return false;
/*     */     }
/*  41 */     int i = (int)((getX() - currentXStart) * scaleRatio);
/*  42 */     int pitWidth = (int)(width * scaleRatio);
/*  43 */     int pitHeight = (int)(height * scaleRatio);
/*     */     
/*  45 */     double max = (getY() - currentXStart) * scaleRatio;
/*  46 */     while (i < max - pitWidth) {
/*  47 */       graph.drawImage(graphicsRepository.getFragment(getImageName()).getFragmentImage(), i, (int)baseLine + 1, pitWidth, pitHeight, imageObserver);
/*     */ 
/*     */       
/*  50 */       i += pitWidth;
/*     */     } 
/*     */     
/*  53 */     double diff = max + pitWidth - i + 1.0D;
/*  54 */     graph.drawImage(graphicsRepository.getFragment(getImageName()).getFragmentImage(), i, (int)baseLine + 1, (int)diff, pitHeight, imageObserver);
/*     */     
/*  56 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isVisible(int currentXStart, int scaledTargetWidth) {
/*  61 */     int width = getWidth();
/*  62 */     if (getY() < currentXStart - width) {
/*  63 */       return false;
/*     */     }
/*  65 */     if (getX() > currentXStart + width + scaledTargetWidth) {
/*  66 */       return false;
/*     */     }
/*  68 */     return true;
/*     */   }
/*     */   
/*     */   public static int getImageWidth() {
/*  72 */     return 30;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWidth() {
/*  77 */     return getY() - getX();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/*  82 */     return getHeightStatic();
/*     */   }
/*     */   
/*     */   public static int getHeightStatic() {
/*  86 */     return 30;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionX() {
/*  91 */     return getX();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionY() {
/*  96 */     return -getCollisionHeight();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionWidth() {
/* 101 */     return getY() - getX() + getImageWidth();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollisionHeight() {
/* 106 */     return getHeightStatic();
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
/* 133 */     if (!(levelObject instanceof PitObject)) {
/* 134 */       return false;
/*     */     }
/*     */     
/* 137 */     PitObject pitObject = (PitObject)levelObject;
/*     */     
/* 139 */     if (pitObject.getY() + getImageWidth() >= getX() && getY() + getImageWidth() >= pitObject.getX()) {
/* 140 */       setX(Math.min(pitObject.getX(), getX()));
/* 141 */       setY(Math.max(pitObject.getY(), getY()));
/* 142 */       return true;
/*     */     } 
/*     */     
/* 145 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\PitObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */