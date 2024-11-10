/*    */ package com.grip.gui.library;
/*    */ 
/*    */ import com.grip.Fragment;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.swing.ImageIcon;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GraphicsRepository
/*    */ {
/* 15 */   private final Map<String, Fragment> fragments = new HashMap<String, Fragment>();
/*    */ 
/*    */   
/*    */   public GraphicsRepository() {}
/*    */   
/*    */   public GraphicsRepository(List<Fragment> fragments) {
/* 21 */     addFragments(fragments);
/*    */   }
/*    */   
/*    */   public void addFragments(List<Fragment> fragments) {
/* 25 */     for (Fragment fragment : fragments) {
/* 26 */       this.fragments.put(fragment.getFragmentName(), fragment);
/*    */     }
/*    */   }
/*    */   
/*    */   public Fragment getFragment(String imageTitle) {
/* 31 */     return this.fragments.get(imageTitle);
/*    */   }
/*    */   
/*    */   public ImageIcon getImageIcon(String imageTitle) {
/* 35 */     return new ImageIcon(getFragment(imageTitle).getFragmentImage());
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\gui\library\GraphicsRepository.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */