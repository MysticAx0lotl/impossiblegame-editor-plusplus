/*    */ package com.grip.audio;
/*    */ 
/*    */ public class AudioFileFactory
/*    */ {
/*    */   public enum EFileFormat {
/*  6 */     OGG,
/*  7 */     MP3;
/*    */   }
/*    */   
/*    */   public static AudioFile getAudioFile(String fileName) {
/* 11 */     if (fileName == null || fileName.isEmpty()) {
/* 12 */       return null;
/*    */     }
/* 14 */     if (fileName.endsWith(".ogg"))
/* 15 */       return new Mp3AndOggAudioFile(fileName); 
/* 16 */     if (fileName.endsWith(".mp3")) {
/* 17 */       return new Mp3AndOggAudioFile(fileName);
/*    */     }
/* 19 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\audio\AudioFileFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */