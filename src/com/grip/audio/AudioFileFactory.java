package com.grip.audio;

public class AudioFileFactory {
   public static AudioFile getAudioFile(String fileName) {
      if (fileName != null && !fileName.isEmpty()) {
         if (fileName.endsWith(".ogg")) {
            return new Mp3AndOggAudioFile(fileName);
         } else {
            return fileName.endsWith(".mp3") ? new Mp3AndOggAudioFile(fileName) : null;
         }
      } else {
         return null;
      }
   }

   public static enum EFileFormat {
      OGG,
      MP3;
   }
}
