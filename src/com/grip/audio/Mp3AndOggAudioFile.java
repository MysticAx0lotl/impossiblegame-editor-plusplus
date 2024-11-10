/*     */ package com.grip.audio;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Map;
/*     */ import javazoom.jlgui.basicplayer.BasicController;
/*     */ import javazoom.jlgui.basicplayer.BasicPlayer;
/*     */ import javazoom.jlgui.basicplayer.BasicPlayerEvent;
/*     */ import javazoom.jlgui.basicplayer.BasicPlayerException;
/*     */ import javazoom.jlgui.basicplayer.BasicPlayerListener;
/*     */ 
/*     */ public class Mp3AndOggAudioFile
/*     */   implements AudioFile, BasicPlayerListener {
/*     */   private String pathToAudioFile;
/*  14 */   private long playbackPositionInMilliseconds = 0L; private BasicPlayer mp3; private boolean bIsPlaying = false;
/*  15 */   private final Object syncRoot = new Object();
/*  16 */   private volatile int syncCode = -1;
/*     */   private boolean startUpdatesOk = false;
/*  18 */   private Map audioInfo = null;
/*  19 */   private long markPositionInMilliseconds = 0L;
/*     */   private boolean isPlayingFromMark = false;
/*     */   
/*     */   public Mp3AndOggAudioFile(String pathToAudioFile) {
/*  23 */     this.pathToAudioFile = pathToAudioFile;
/*  24 */     openAudioFile(pathToAudioFile);
/*     */   }
/*     */   
/*     */   private boolean openAudioFile(String audioFileName) {
/*     */     try {
/*  29 */       File audioFile = new File(audioFileName);
/*  30 */       if (this.mp3 != null) {
/*  31 */         this.mp3.stop();
/*     */       } else {
/*  33 */         this.mp3 = new BasicPlayer();
/*  34 */         this.mp3.addBasicPlayerListener(this);
/*     */       } 
/*  36 */       this.mp3.open(audioFile);
/*  37 */       doRewind();
/*  38 */       return true;
/*  39 */     } catch (BasicPlayerException e) {
/*  40 */       e.printStackTrace();
/*  41 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void doPlayPause() {
/*  47 */     if (this.pathToAudioFile == null)
/*  48 */       return;  long atStartPlayback = this.playbackPositionInMilliseconds;
/*     */     
/*     */     try {
/*  51 */       synchronized (this.syncRoot) {
/*  52 */         if (!this.bIsPlaying) {
/*  53 */           this.syncCode = 5;
/*  54 */           this.mp3.resume();
/*     */         } else {
/*  56 */           this.syncCode = 4;
/*  57 */           this.mp3.pause();
/*     */         } 
/*     */         
/*  60 */         this.startUpdatesOk = false;
/*     */         
/*  62 */         System.out.println("Waiting: " + this.syncCode);
/*     */         
/*     */         try {
/*  65 */           this.syncRoot.wait(1000L);
/*  66 */         } catch (InterruptedException e) {}
/*     */       } 
/*     */       
/*  69 */       if (this.bIsPlaying) {
/*  70 */         this.playbackPositionInMilliseconds = atStartPlayback;
/*     */       }
/*  72 */       this.bIsPlaying = !this.bIsPlaying;
/*  73 */     } catch (BasicPlayerException e) {
/*  74 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void doPlayFromPosition(long millis) {
/*  80 */     if (this.pathToAudioFile == null)
/*     */       return;  try {
/*  82 */       setCurrentPlaybackPosition(millis);
/*  83 */       setMarkPosition(millis);
/*     */       
/*  85 */       if (this.bIsPlaying) {
/*  86 */         synchronized (this.syncRoot) {
/*  87 */           this.syncCode = 4;
/*  88 */           this.mp3.pause();
/*     */           try {
/*  90 */             this.syncRoot.wait(1000L);
/*  91 */           } catch (InterruptedException e) {}
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  97 */       long skipBytes = Math.round((float)(getBytesLength(this.audioInfo) * millis / getTimeLengthEstimation(this.audioInfo)));
/*     */ 
/*     */       
/* 100 */       if (skipBytes > 0L) {
/* 101 */         synchronized (this.syncRoot) {
/* 102 */           this.syncCode = 7;
/* 103 */           this.mp3.seek(skipBytes);
/*     */           try {
/* 105 */             this.syncRoot.wait(1000L);
/* 106 */           } catch (InterruptedException e) {}
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 111 */       synchronized (this.syncRoot) {
/* 112 */         this.syncCode = 5;
/* 113 */         this.mp3.resume();
/*     */         try {
/* 115 */           this.syncRoot.wait(1000L);
/* 116 */         } catch (InterruptedException e) {}
/*     */       } 
/*     */ 
/*     */       
/* 120 */       this.startUpdatesOk = false;
/* 121 */       this.bIsPlaying = true;
/* 122 */       this.isPlayingFromMark = true;
/* 123 */     } catch (BasicPlayerException e) {
/* 124 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setMarkPosition(long millis) {
/* 129 */     this.markPositionInMilliseconds = millis;
/*     */   }
/*     */   
/*     */   public long getBytesLength(Map properties) {
/* 133 */     return (properties != null) ? ((Integer)properties.get("audio.length.bytes")).intValue() : -1L;
/*     */   }
/*     */   
/*     */   public long getTimeLengthEstimation(Map properties) {
/* 137 */     long milliseconds = -1L;
/* 138 */     int byteslength = -1;
/* 139 */     if (properties != null) {
/* 140 */       if (properties.containsKey("audio.length.bytes")) {
/* 141 */         byteslength = ((Integer)properties.get("audio.length.bytes")).intValue();
/*     */       }
/* 143 */       if (properties.containsKey("duration")) {
/* 144 */         milliseconds = ((int)((Long)properties.get("duration")).longValue() / 1000);
/*     */       } else {
/* 146 */         int bitspersample = -1;
/* 147 */         int channels = -1;
/* 148 */         float samplerate = -1.0F;
/* 149 */         int framesize = -1;
/* 150 */         if (properties.containsKey("audio.samplesize.bits")) {
/* 151 */           bitspersample = ((Integer)properties.get("audio.samplesize.bits")).intValue();
/*     */         }
/* 153 */         if (properties.containsKey("audio.channels")) {
/* 154 */           channels = ((Integer)properties.get("audio.channels")).intValue();
/*     */         }
/* 156 */         if (properties.containsKey("audio.samplerate.hz")) {
/* 157 */           samplerate = ((Float)properties.get("audio.samplerate.hz")).floatValue();
/*     */         }
/* 159 */         if (properties.containsKey("audio.framesize.bytes")) {
/* 160 */           framesize = ((Integer)properties.get("audio.framesize.bytes")).intValue();
/*     */         }
/* 162 */         if (bitspersample > 0) {
/* 163 */           milliseconds = (int)(1000.0F * byteslength / samplerate * channels * (bitspersample / 8));
/*     */         } else {
/* 165 */           milliseconds = (int)(1000.0F * byteslength / samplerate * framesize);
/*     */         } 
/*     */       } 
/*     */     } 
/* 169 */     return milliseconds;
/*     */   }
/*     */ 
/*     */   
/*     */   public void doRewind() {
/*     */     try {
/* 175 */       this.mp3.stop();
/* 176 */       this.isPlayingFromMark = false;
/* 177 */       playbackInit();
/* 178 */       clearPlaybackPosition();
/* 179 */     } catch (BasicPlayerException e) {
/* 180 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void playbackInit() {
/*     */     try {
/* 186 */       this.bIsPlaying = false;
/* 187 */       System.out.println("Waiting for play");
/*     */       try {
/* 189 */         this.mp3.setGain(0.0D);
/* 190 */       } catch (BasicPlayerException e) {
/* 191 */         e.printStackTrace();
/*     */       } 
/* 193 */       synchronized (this.syncRoot) {
/* 194 */         this.syncCode = 2;
/* 195 */         this.mp3.play();
/*     */         try {
/* 197 */           this.syncRoot.wait(5000L);
/* 198 */         } catch (InterruptedException e) {
/* 199 */           e.printStackTrace();
/*     */         } 
/*     */       } 
/* 202 */       this.startUpdatesOk = false;
/* 203 */       synchronized (this.syncRoot) {
/* 204 */         this.syncCode = 4;
/* 205 */         this.mp3.pause();
/*     */         try {
/* 207 */           this.syncRoot.wait(1000L);
/* 208 */         } catch (InterruptedException e) {
/* 209 */           e.printStackTrace();
/*     */         } 
/*     */       } 
/*     */       try {
/* 213 */         this.mp3.setGain(1.0D);
/* 214 */       } catch (BasicPlayerException e) {
/* 215 */         e.printStackTrace();
/*     */       } 
/* 217 */     } catch (BasicPlayerException e) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearPlaybackPosition() {
/* 223 */     this.playbackPositionInMilliseconds = 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPlaying() {
/* 228 */     return this.bIsPlaying;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPlaybackPositionInMilliseconds() {
/* 233 */     return this.playbackPositionInMilliseconds;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tickCurrentMillisecondPosition(long audioTickLength) {
/* 238 */     if (isPlaying() && this.startUpdatesOk)
/* 239 */       this.playbackPositionInMilliseconds += audioTickLength; 
/*     */   }
/*     */   
/*     */   public void setCurrentPlaybackPosition(long currentPlaybackPositionInMilliseconds) {
/* 243 */     this.playbackPositionInMilliseconds = currentPlaybackPositionInMilliseconds;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 249 */       this.mp3.stop();
/* 250 */     } catch (BasicPlayerException e) {
/* 251 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearMark() {
/* 257 */     this.markPositionInMilliseconds = 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void opened(Object stream, Map properties) {
/* 262 */     this.audioInfo = properties;
/*     */   }
/*     */ 
/*     */   
/*     */   public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
/* 267 */     this.playbackPositionInMilliseconds = microseconds / 1000L + (this.isPlayingFromMark ? this.markPositionInMilliseconds : 0L);
/*     */     
/* 269 */     this.startUpdatesOk = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void stateUpdated(BasicPlayerEvent basicPlayerEvent) {
/* 274 */     synchronized (this.syncRoot) {
/* 275 */       System.out.println("Start - Received: " + basicPlayerEvent.toString());
/* 276 */       if (this.syncCode == basicPlayerEvent.getCode()) {
/* 277 */         switch (basicPlayerEvent.getCode()) {
/*     */           case 2:
/*     */           case 4:
/*     */           case 5:
/* 281 */             this.syncRoot.notify(); break;
/*     */         } 
/* 283 */         this.syncCode = -1;
/* 284 */         System.out.println("SUCCESS");
/*     */       } 
/*     */       
/* 287 */       System.out.println("Received: " + basicPlayerEvent.toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setController(BasicController basicController) {}
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\audio\Mp3AndOggAudioFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */