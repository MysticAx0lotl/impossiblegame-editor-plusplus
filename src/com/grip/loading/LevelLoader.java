/*     */ package com.grip.loading;
/*     */ 
/*     */ import com.grip.level.LevelDefinition;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class LevelLoader {
/*     */   public static boolean load(String path, LevelDefinition levelDefinition) throws IOException {
/*  10 */     DataInputStream inputStream = new DataInputStream(new FileInputStream(path));
/*     */ 
/*     */     
/*  13 */     int version = inputStream.readInt();
/*     */     
/*  15 */     switch (version) {
/*     */       case 0:
/*  17 */         loadVersion0(inputStream, levelDefinition);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  23 */         inputStream.close();
/*  24 */         return true;
/*     */     } 
/*     */     inputStream.close();
/*     */     return false;
/*     */   }
/*     */   protected static boolean loadVersion0(DataInputStream inputStream, LevelDefinition levelDefinition) throws IOException {
/*  30 */     levelDefinition.setUsesSpecialGraphics(inputStream.readBoolean());
/*     */ 
/*     */     
/*  33 */     short objectsCount = inputStream.readShort();
/*     */ 
/*     */     
/*  36 */     for (int i = 0; i < objectsCount; i++) {
/*  37 */       byte type = inputStream.readByte();
/*  38 */       int x = inputStream.readInt();
/*  39 */       int y = inputStream.readInt();
/*     */       
/*  41 */       switch (type) {
/*     */         case 0:
/*  43 */           levelDefinition.addBlock(x, y);
/*     */           break;
/*     */         case 1:
/*  46 */           levelDefinition.addSpike(x, y);
/*     */           break;
/*     */         case 2:
/*  49 */           levelDefinition.addPit(x, y);
/*     */           break;
/*     */         default:
/*  52 */           return false;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/*  57 */     levelDefinition.setLevelEnd(inputStream.readInt());
/*     */ 
/*     */     
/*  60 */     int backgroundChangesCount = inputStream.readInt();
/*     */ 
/*     */     
/*  63 */     for (int j = 0; j < backgroundChangesCount; j++) {
/*  64 */       int bgId; String bgName; int positionX = inputStream.readInt();
/*  65 */       byte type = inputStream.readByte();
/*     */       
/*  67 */       switch (type) {
/*     */         case 0:
/*  69 */           bgId = inputStream.readInt();
/*  70 */           levelDefinition.addBackgroundChangeInternal(positionX, bgId);
/*     */           break;
/*     */         case 1:
/*  73 */           bgName = inputStream.readUTF();
/*  74 */           levelDefinition.addBackgroundChangeCustom(positionX, bgName);
/*     */           break;
/*     */         default:
/*  77 */           return false;
/*     */       } 
/*     */     
/*     */     } 
/*  81 */     int gravityChangesCount = inputStream.readInt();
/*     */ 
/*     */     
/*  84 */     for (int k = 0; k < gravityChangesCount; k++) {
/*  85 */       levelDefinition.addGravityChange(inputStream.readInt());
/*     */     }
/*     */ 
/*     */     
/*  89 */     int blocksFallingCount = inputStream.readInt();
/*     */ 
/*     */     
/*  92 */     for (int m = 0; m < blocksFallingCount; m++) {
/*  93 */       levelDefinition.addBlocksFalling(inputStream.readInt(), inputStream.readInt());
/*     */     }
/*     */ 
/*     */     
/*  97 */     int blocksRisingCount = inputStream.readInt();
/*     */ 
/*     */     
/* 100 */     for (int n = 0; n < blocksRisingCount; n++) {
/* 101 */       levelDefinition.addBlocksRising(inputStream.readInt(), inputStream.readInt());
/*     */     }
/*     */     
/* 104 */     levelDefinition.finishLoading();
/*     */     
/* 106 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\loading\LevelLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */