/*    */ package com.grip.elements;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public class BackgroundChangeCustom
/*    */   extends BackgroundChange
/*    */ {
/*    */   private final String backgroundName;
/*    */   
/*    */   public BackgroundChangeCustom(int positionX, String backgroundName) {
/* 13 */     super(positionX);
/* 14 */     this.backgroundName = backgroundName;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getTypeId() {
/* 19 */     return 1;
/*    */   }
/*    */   
/*    */   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
/* 23 */     super.writeToDataOutputStream(outputStream);
/* 24 */     outputStream.writeChars(this.backgroundName);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 29 */     if (this == o) return true; 
/* 30 */     if (o == null || getClass() != o.getClass()) return false; 
/* 31 */     if (!super.equals(o)) return false;
/*    */     
/* 33 */     BackgroundChangeCustom that = (BackgroundChangeCustom)o;
/*    */     
/* 35 */     if (!this.backgroundName.equals(that.backgroundName)) return false;
/*    */     
/* 37 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 42 */     int result = super.hashCode();
/* 43 */     result = 31 * result + this.backgroundName.hashCode();
/* 44 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getImageTitle() {
/* 49 */     return "CstBg" + getBackgroundName();
/*    */   }
/*    */   
/*    */   public String getBackgroundName() {
/* 53 */     return this.backgroundName;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\elements\BackgroundChangeCustom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */