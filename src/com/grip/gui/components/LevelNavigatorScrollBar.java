package com.grip.gui.components;

import com.grip.level.LevelViewSettings;
import java.awt.HeadlessException;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JFrame;

public class LevelNavigatorScrollBar extends Scrollbar {
   private LevelViewSettings levelViewSettings;
   private JFrame parentFrame;
   private final ScrollPositionChangedListener scrollPositionChangedListener = new ScrollPositionChangedListener();

   public LevelNavigatorScrollBar() throws HeadlessException {
      super(0);
   }

   public void setReferences(JFrame parentFrame, LevelViewSettings levelViewSettings) {
      this.parentFrame = parentFrame;
      this.levelViewSettings = levelViewSettings;
      this.addAdjustmentListener(this.scrollPositionChangedListener);
   }

   public void setFullContentsDimensions(int maxX) {
      this.setMinimum(0);
      this.setMaximum(Math.max(0, maxX / 100));
      this.setUnitIncrement((int)((double)(this.parentFrame.getWidth() / 2) / this.levelViewSettings.getRatio() / 1000.0));
      this.setBlockIncrement((int)((double)(this.parentFrame.getWidth() / 2) / this.levelViewSettings.getRatio() / 100.0));
      this.setFocusable(false);
      this.updateCurrentPosition();
   }

   public void updateCurrentPosition() {
      this.setValue(this.levelViewSettings.getCurrentXOffset() / 100);
   }

   private class ScrollPositionChangedListener implements AdjustmentListener {
      private ScrollPositionChangedListener() {
      }

      public void adjustmentValueChanged(AdjustmentEvent e) {
         LevelNavigatorScrollBar.this.levelViewSettings.setNewCurrentPositionXFromScrollbar(e.getValue() * 100);
      }

      // $FF: synthetic method
      ScrollPositionChangedListener(Object x1) {
         this();
      }
   }
}
