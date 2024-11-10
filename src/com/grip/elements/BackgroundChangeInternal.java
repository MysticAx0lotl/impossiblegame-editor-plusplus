/*    */ package com.grip.elements;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class BackgroundChangeInternal
/*    */   extends BackgroundChange
/*    */ {
/*    */   private final int backgroundId;
/*    */   
/*    */   public BackgroundChangeInternal(int positionX, int backgroundId) {
/* 12 */     super(positionX);
/* 13 */     this.backgroundId = backgroundId;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getTypeId() {
/* 18 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
/* 23 */     super.writeToDataOutputStream(outputStream);
/* 24 */     outputStream.writeInt(this.backgroundId);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 29 */     if (this == o) return true; 
/* 30 */     if (o == null || getClass() != o.getClass()) return false; 
/* 31 */     if (!super.equals(o)) return false;
/*    */     
/* 33 */     BackgroundChangeInternal that = (BackgroundChangeInternal)o;
/*    */     
/* 35 */     if (this.backgroundId != that.backgroundId) return false;
/*    */     
/* 37 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 42 */     int result = super.hashCode();
/* 43 */     result = 31 * result + this.backgroundId;
/* 44 */     return result;
/*    */   }
/*    */   
/*    */   public int getBackgroundId() {
/* 48 */     return this.backgroundId;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getImageTitle() {
/* 53 */     int id = getBackgroundId() + 1;
/* 54 */     return "background" + id;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\BackgroundChangeInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */