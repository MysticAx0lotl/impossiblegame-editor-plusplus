/*    */ package com.grip.elements;
/*    */ 
/*    */ import com.grip.saving.SaveData;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class BlocksRise
/*    */   extends SaveData {
/*    */   private final int startX;
/*    */   private final int endX;
/*    */   
/*    */   public BlocksRise(int startX, int endX) {
/* 13 */     this.startX = startX;
/* 14 */     this.endX = endX;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
/* 19 */     outputStream.writeInt(this.startX);
/* 20 */     outputStream.writeInt(this.endX);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 25 */     if (this == o) return true; 
/* 26 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 28 */     BlocksRise that = (BlocksRise)o;
/*    */     
/* 30 */     if (this.endX != that.endX) return false; 
/* 31 */     if (this.startX != that.startX) return false;
/*    */     
/* 33 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 38 */     int result = this.startX;
/* 39 */     result = 31 * result + this.endX;
/* 40 */     return result;
/*    */   }
/*    */   
/*    */   public int getX() {
/* 44 */     return this.startX;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\BlocksRise.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */