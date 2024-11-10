package com.grip.audio;

public interface AudioFile {
   void doPlayPause();

   void doRewind();

   void doPlayFromPosition(long var1);

   void clearPlaybackPosition();

   boolean isPlaying();

   long getPlaybackPositionInMilliseconds();

   void tickCurrentMillisecondPosition(long var1);

   void setCurrentPlaybackPosition(long var1);

   void close();

   void clearMark();
}
