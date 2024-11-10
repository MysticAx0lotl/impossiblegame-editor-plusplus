/*     */ package com.grip.elements;
/*     */ import java.io.DataOutputStream;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class LevelObject {
/*     */   private int x;
/*     */   private int y;
/*     */   
/*     */   public abstract int getCollisionX();
/*     */   
/*     */   public abstract int getCollisionY();
/*     */   
/*     */   public abstract int getCollisionWidth();
/*     */   
/*     */   public abstract int getCollisionHeight();
/*     */   
/*     */   public abstract boolean isColliding(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   public abstract boolean tryMergeWith(LevelObject paramLevelObject);
/*     */   
/*     */   public enum ObjectType {
/*  22 */     BLOCK,
/*  23 */     SPIKE,
/*  24 */     PIT,
/*  25 */     DELETE,
/*  26 */     MARK,
/*  27 */     LEVELEND,
/*  28 */     BACKGROUND;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected LevelObject(int x, int y) {
/*  35 */     this.x = x;
/*  36 */     this.y = y;
/*     */   }
/*     */   
/*     */   public abstract byte getTypeId();
/*     */   
/*     */   public abstract List<Integer> getValuesForSaving();
/*     */   
/*     */   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
/*  44 */     outputStream.writeByte(getTypeId());
/*  45 */     outputStream.writeInt(this.x);
/*  46 */     outputStream.writeInt(this.y);
/*     */     
/*  48 */     List<Integer> valuesForSaving = getValuesForSaving();
/*  49 */     if (valuesForSaving != null) {
/*  50 */       for (Iterator<Integer> i$ = valuesForSaving.iterator(); i$.hasNext(); ) { int value = ((Integer)i$.next()).intValue();
/*  51 */         outputStream.writeInt(value); }
/*     */     
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  58 */     if (this == o) return true; 
/*  59 */     if (o == null || o.getClass().isInstance(getClass())) return false;
/*     */     
/*  61 */     LevelObject that = (LevelObject)o;
/*     */     
/*  63 */     if (this.x != that.x) return false; 
/*  64 */     if (this.y != that.y) return false;
/*     */     
/*  66 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  71 */     int result = this.x;
/*  72 */     result = 31 * result + this.y;
/*  73 */     return result;
/*     */   }
/*     */   
/*     */   public int getX() {
/*  77 */     return this.x;
/*     */   }
/*     */   
/*     */   public int getY() {
/*  81 */     return this.y;
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract String getImageName();
/*     */ 
/*     */   
/*     */   public abstract boolean drawObject(ImageObserver paramImageObserver, Graphics2D paramGraphics2D, GraphicsRepository paramGraphicsRepository, double paramDouble1, double paramDouble2, int paramInt1, int paramInt2);
/*     */   
/*     */   public abstract boolean isVisible(int paramInt1, int paramInt2);
/*     */   
/*     */   public abstract int getWidth();
/*     */   
/*     */   public abstract int getHeight();
/*     */   
/*     */   protected void setX(int x) {
/*  97 */     this.x = x;
/*     */   }
/*     */   
/*     */   protected void setY(int y) {
/* 101 */     this.y = y;
/*     */   }
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\LevelObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */