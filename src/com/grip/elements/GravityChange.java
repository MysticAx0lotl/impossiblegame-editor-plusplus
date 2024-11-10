/*    */ package com.grip.elements;
/*    */ 
/*    */ import com.grip.saving.SaveData;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class GravityChange
/*    */   extends SaveData {
/*    */   private final int positionX;
/*    */   
/*    */   public GravityChange(int positionX) {
/* 12 */     this.positionX = positionX;
/*    */   }
/*    */   
/*    */   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
/* 16 */     outputStream.writeInt(this.positionX);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 21 */     if (this == o) return true; 
/* 22 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 24 */     GravityChange that = (GravityChange)o;
/*    */     
/* 26 */     if (this.positionX != that.positionX) return false;
/*    */     
/* 28 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 33 */     return this.positionX;
/*    */   }
/*    */   
/*    */   public int getX() {
/* 37 */     return this.positionX;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\GravityChange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */