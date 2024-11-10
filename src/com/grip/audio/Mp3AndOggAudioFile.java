package com.grip.audio;

import java.io.File;
import java.util.Map;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class Mp3AndOggAudioFile implements AudioFile, BasicPlayerListener {
   private String pathToAudioFile;
   private BasicPlayer mp3;
   private boolean bIsPlaying = false;
   private long playbackPositionInMilliseconds = 0L;
   private final Object syncRoot = new Object();
   private volatile int syncCode = -1;
   private boolean startUpdatesOk = false;
   private Map audioInfo = null;
   private long markPositionInMilliseconds = 0L;
   private boolean isPlayingFromMark = false;

   public Mp3AndOggAudioFile(String pathToAudioFile) {
      this.pathToAudioFile = pathToAudioFile;
      this.openAudioFile(pathToAudioFile);
   }

   private boolean openAudioFile(String audioFileName) {
      try {
         File audioFile = new File(audioFileName);
         if (this.mp3 != null) {
            this.mp3.stop();
         } else {
            this.mp3 = new BasicPlayer();
            this.mp3.addBasicPlayerListener(this);
         }

         this.mp3.open(audioFile);
         this.doRewind();
         return true;
      } catch (BasicPlayerException var3) {
         BasicPlayerException e = var3;
         e.printStackTrace();
         return false;
      }
   }

   public void doPlayPause() {
      if (this.pathToAudioFile != null) {
         long atStartPlayback = this.playbackPositionInMilliseconds;

         try {
            synchronized(this.syncRoot) {
               if (!this.bIsPlaying) {
                  this.syncCode = 5;
                  this.mp3.resume();
               } else {
                  this.syncCode = 4;
                  this.mp3.pause();
               }

               this.startUpdatesOk = false;
               System.out.println("Waiting: " + this.syncCode);

               try {
                  this.syncRoot.wait(1000L);
               } catch (InterruptedException var6) {
               }
            }

            if (this.bIsPlaying) {
               this.playbackPositionInMilliseconds = atStartPlayback;
            }

            this.bIsPlaying = !this.bIsPlaying;
         } catch (BasicPlayerException var8) {
            BasicPlayerException e = var8;
            e.printStackTrace();
         }

      }
   }

   public void doPlayFromPosition(long millis) {
      if (this.pathToAudioFile != null) {
         try {
            this.setCurrentPlaybackPosition(millis);
            this.setMarkPosition(millis);
            if (this.bIsPlaying) {
               synchronized(this.syncRoot) {
                  this.syncCode = 4;
                  this.mp3.pause();

                  try {
                     this.syncRoot.wait(1000L);
                  } catch (InterruptedException var13) {
                  }
               }
            }

            long skipBytes = (long)Math.round((float)(this.getBytesLength(this.audioInfo) * millis / this.getTimeLengthEstimation(this.audioInfo)));
            if (skipBytes > 0L) {
               synchronized(this.syncRoot) {
                  this.syncCode = 7;
                  this.mp3.seek(skipBytes);

                  try {
                     this.syncRoot.wait(1000L);
                  } catch (InterruptedException var11) {
                  }
               }
            }

            synchronized(this.syncRoot) {
               this.syncCode = 5;
               this.mp3.resume();

               try {
                  this.syncRoot.wait(1000L);
               } catch (InterruptedException var9) {
               }
            }

            this.startUpdatesOk = false;
            this.bIsPlaying = true;
            this.isPlayingFromMark = true;
         } catch (BasicPlayerException var15) {
            BasicPlayerException e = var15;
            e.printStackTrace();
         }

      }
   }

   public void setMarkPosition(long millis) {
      this.markPositionInMilliseconds = millis;
   }

   public long getBytesLength(Map properties) {
      return properties != null ? (long)(Integer)properties.get("audio.length.bytes") : -1L;
   }

   public long getTimeLengthEstimation(Map properties) {
      long milliseconds = -1L;
      int byteslength = -1;
      if (properties != null) {
         if (properties.containsKey("audio.length.bytes")) {
            byteslength = (Integer)properties.get("audio.length.bytes");
         }

         if (properties.containsKey("duration")) {
            milliseconds = (long)((int)(Long)properties.get("duration") / 1000);
         } else {
            int bitspersample = -1;
            int channels = -1;
            float samplerate = -1.0F;
            int framesize = -1;
            if (properties.containsKey("audio.samplesize.bits")) {
               bitspersample = (Integer)properties.get("audio.samplesize.bits");
            }

            if (properties.containsKey("audio.channels")) {
               channels = (Integer)properties.get("audio.channels");
            }

            if (properties.containsKey("audio.samplerate.hz")) {
               samplerate = (Float)properties.get("audio.samplerate.hz");
            }

            if (properties.containsKey("audio.framesize.bytes")) {
               framesize = (Integer)properties.get("audio.framesize.bytes");
            }

            if (bitspersample > 0) {
               milliseconds = (long)((int)(1000.0F * (float)byteslength / (samplerate * (float)channels * (float)(bitspersample / 8))));
            } else {
               milliseconds = (long)((int)(1000.0F * (float)byteslength / (samplerate * (float)framesize)));
            }
         }
      }

      return milliseconds;
   }

   public void doRewind() {
      try {
         this.mp3.stop();
         this.isPlayingFromMark = false;
         this.playbackInit();
         this.clearPlaybackPosition();
      } catch (BasicPlayerException var2) {
         BasicPlayerException e = var2;
         e.printStackTrace();
      }

   }

   public void playbackInit() {
      try {
         this.bIsPlaying = false;
         System.out.println("Waiting for play");

         BasicPlayerException e;
         try {
            this.mp3.setGain(0.0);
         } catch (BasicPlayerException var10) {
            e = var10;
            e.printStackTrace();
         }

         InterruptedException e;
         synchronized(this.syncRoot) {
            this.syncCode = 2;
            this.mp3.play();

            try {
               this.syncRoot.wait(5000L);
            } catch (InterruptedException var8) {
               e = var8;
               e.printStackTrace();
            }
         }

         this.startUpdatesOk = false;
         synchronized(this.syncRoot) {
            this.syncCode = 4;
            this.mp3.pause();

            try {
               this.syncRoot.wait(1000L);
            } catch (InterruptedException var6) {
               e = var6;
               e.printStackTrace();
            }
         }

         try {
            this.mp3.setGain(1.0);
         } catch (BasicPlayerException var5) {
            e = var5;
            e.printStackTrace();
         }
      } catch (BasicPlayerException var11) {
      }

   }

   public void clearPlaybackPosition() {
      this.playbackPositionInMilliseconds = 0L;
   }

   public boolean isPlaying() {
      return this.bIsPlaying;
   }

   public long getPlaybackPositionInMilliseconds() {
      return this.playbackPositionInMilliseconds;
   }

   public void tickCurrentMillisecondPosition(long audioTickLength) {
      if (this.isPlaying() && this.startUpdatesOk) {
         this.playbackPositionInMilliseconds += audioTickLength;
      }

   }

   public void setCurrentPlaybackPosition(long currentPlaybackPositionInMilliseconds) {
      this.playbackPositionInMilliseconds = currentPlaybackPositionInMilliseconds;
   }

   public void close() {
      try {
         this.mp3.stop();
      } catch (BasicPlayerException var2) {
         BasicPlayerException e = var2;
         e.printStackTrace();
      }

   }

   public void clearMark() {
      this.markPositionInMilliseconds = 0L;
   }

   public void opened(Object stream, Map properties) {
      this.audioInfo = properties;
   }

   public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
      this.playbackPositionInMilliseconds = microseconds / 1000L + (this.isPlayingFromMark ? this.markPositionInMilliseconds : 0L);
      this.startUpdatesOk = true;
   }

   public void stateUpdated(BasicPlayerEvent basicPlayerEvent) {
      synchronized(this.syncRoot) {
         System.out.println("Start - Received: " + basicPlayerEvent.toString());
         if (this.syncCode == basicPlayerEvent.getCode()) {
            switch (basicPlayerEvent.getCode()) {
               case 2:
               case 4:
               case 5:
                  this.syncRoot.notify();
               case 3:
               default:
                  this.syncCode = -1;
                  System.out.println("SUCCESS");
            }
         }

         System.out.println("Received: " + basicPlayerEvent.toString());
      }
   }

   public void setController(BasicController basicController) {
   }
}
