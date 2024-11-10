/*    */ package com.grip.gui.components;
/*    */ 
/*    */ import com.grip.level.LevelViewSettings;
/*    */ import java.awt.HeadlessException;
/*    */ import java.awt.Scrollbar;
/*    */ import java.awt.event.AdjustmentEvent;
/*    */ import java.awt.event.AdjustmentListener;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class LevelNavigatorScrollBar
/*    */   extends Scrollbar
/*    */ {
/*    */   private LevelViewSettings levelViewSettings;
/*    */   private JFrame parentFrame;
/* 15 */   private final ScrollPositionChangedListener scrollPositionChangedListener = new ScrollPositionChangedListener();
/*    */   
/*    */   public LevelNavigatorScrollBar() throws HeadlessException {
/* 18 */     super(0);
/*    */   }
/*    */   
/*    */   public void setReferences(JFrame parentFrame, LevelViewSettings levelViewSettings) {
/* 22 */     this.parentFrame = parentFrame;
/* 23 */     this.levelViewSettings = levelViewSettings;
/* 24 */     addAdjustmentListener(this.scrollPositionChangedListener);
/*    */   }
/*    */   
/*    */   public void setFullContentsDimensions(int maxX) {
/* 28 */     setMinimum(0);
/* 29 */     setMaximum(Math.max(0, maxX / 100));
/* 30 */     setUnitIncrement((int)((this.parentFrame.getWidth() / 2) / this.levelViewSettings.getRatio() / 1000.0D));
/* 31 */     setBlockIncrement((int)((this.parentFrame.getWidth() / 2) / this.levelViewSettings.getRatio() / 100.0D));
/* 32 */     setFocusable(false);
/* 33 */     updateCurrentPosition();
/*    */   }
/*    */   
/*    */   public void updateCurrentPosition() {
/* 37 */     setValue(this.levelViewSettings.getCurrentXOffset() / 100);
/*    */   }
/*    */   
/*    */   private class ScrollPositionChangedListener
/*    */     implements AdjustmentListener {
/*    */     public void adjustmentValueChanged(AdjustmentEvent e) {
/* 43 */       LevelNavigatorScrollBar.this.levelViewSettings.setNewCurrentPositionXFromScrollbar(e.getValue() * 100);
/*    */     }
/*    */     
/*    */     private ScrollPositionChangedListener() {}
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\gui\components\LevelNavigatorScrollBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */