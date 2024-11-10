package com.grip.audio;

public interface AudioFile {
  void doPlayPause();
  
  void doRewind();
  
  void doPlayFromPosition(long paramLong);
  
  void clearPlaybackPosition();
  
  boolean isPlaying();
  
  long getPlaybackPositionInMilliseconds();
  
  void tickCurrentMillisecondPosition(long paramLong);
  
  void setCurrentPlaybackPosition(long paramLong);
  
  void close();
  
  void clearMark();
}


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\audio\AudioFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */