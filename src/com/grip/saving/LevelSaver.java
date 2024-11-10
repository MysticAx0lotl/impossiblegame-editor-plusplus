/*    */ package com.grip.saving;
/*    */ import com.grip.elements.BackgroundChange;
/*    */ import com.grip.elements.BlocksFall;
/*    */ import com.grip.elements.BlocksRise;
/*    */ import com.grip.elements.GravityChange;
/*    */ import com.grip.elements.LevelObject;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ 
/*    */ public class LevelSaver {
/*    */   public static boolean saveVersion0(String path, boolean useSpecialGraphics, List<LevelObject> levelObjects, int levelEnd, List<BackgroundChange> backgroundChanges, List<GravityChange> gravityChanges, List<BlocksFall> blocksFallingIntervals, List<BlocksRise> blocksRisingIntervals) throws IOException {
/* 14 */     DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path));
/*    */ 
/*    */     
/* 17 */     outputStream.writeInt(0);
/*    */ 
/*    */     
/* 20 */     outputStream.writeBoolean(useSpecialGraphics);
/*    */ 
/*    */     
/* 23 */     outputStream.writeShort(levelObjects.size());
/*    */ 
/*    */     
/* 26 */     for (LevelObject levelObject : levelObjects) {
/* 27 */       levelObject.writeToDataOutputStream(outputStream);
/*    */     }
/*    */ 
/*    */     
/* 31 */     outputStream.writeInt(levelEnd);
/*    */ 
/*    */     
/* 34 */     outputStream.writeInt(backgroundChanges.size());
/*    */ 
/*    */     
/* 37 */     for (BackgroundChange backgroundChange : backgroundChanges) {
/* 38 */       backgroundChange.writeToDataOutputStream(outputStream);
/*    */     }
/*    */ 
/*    */     
/* 42 */     outputStream.writeInt(gravityChanges.size());
/*    */ 
/*    */     
/* 45 */     for (GravityChange gravityChange : gravityChanges) {
/* 46 */       gravityChange.writeToDataOutputStream(outputStream);
/*    */     }
/*    */ 
/*    */     
/* 50 */     outputStream.writeInt(blocksFallingIntervals.size());
/*    */ 
/*    */     
/* 53 */     for (BlocksFall blocksFall : blocksFallingIntervals) {
/* 54 */       blocksFall.writeToDataOutputStream(outputStream);
/*    */     }
/*    */ 
/*    */     
/* 58 */     outputStream.writeInt(blocksRisingIntervals.size());
/*    */ 
/*    */     
/* 61 */     for (BlocksRise blocksRise : blocksRisingIntervals) {
/* 62 */       blocksRise.writeToDataOutputStream(outputStream);
/*    */     }
/* 64 */     outputStream.close();
/*    */     
/* 66 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\saving\LevelSaver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */