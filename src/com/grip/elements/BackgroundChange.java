/*    */ package com.grip.elements;
/*    */ 
/*    */ import com.grip.saving.SaveData;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class BackgroundChange
/*    */   extends SaveData
/*    */ {
/*    */   private final int x;
/*    */   
/*    */   protected BackgroundChange(int x) {
/* 13 */     this.x = x;
/*    */   }
/*    */   
/*    */   public abstract int getTypeId();
/*    */   
/*    */   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
/* 19 */     outputStream.writeInt(this.x);
/* 20 */     outputStream.writeByte(getTypeId());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 25 */     if (this == o) return true; 
/* 26 */     if (o == null || o.getClass().isInstance(getClass())) return false;
/*    */     
/* 28 */     BackgroundChange that = (BackgroundChange)o;
/*    */     
/* 30 */     if (this.x != that.x) return false;
/*    */     
/* 32 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 37 */     return this.x;
/*    */   }
/*    */   
/*    */   public int getX() {
/* 41 */     return this.x;
/*    */   }
/*    */   
/*    */   public abstract String getImageTitle();
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\BackgroundChange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */