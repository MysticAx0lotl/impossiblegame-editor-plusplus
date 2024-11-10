/*    */ package com.grip;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
/*    */ 
/*    */ public class Fragment {
/*    */   private final String fragmentName;
/*    */   private final BufferedImage fragmentImage;
/*    */   private final int x;
/*    */   private final int y;
/*    */   private final int width;
/*    */   private final int height;
/*    */   
/*    */   public Fragment(SourceImage sourceImage, String imageName, float x, float y, float width, float height) {
/* 14 */     this.fragmentName = imageName;
/* 15 */     this.fragmentImage = sourceImage.getSubImage(x, y, width, height);
/*    */     
/* 17 */     this.x = (int)(x * sourceImage.getWidth());
/* 18 */     this.y = (int)(y * sourceImage.getHeight());
/* 19 */     this.width = (int)(width * sourceImage.getWidth());
/* 20 */     this.height = (int)(height * sourceImage.getHeight());
/*    */   }
/*    */   
/*    */   public String getFragmentName() {
/* 24 */     return this.fragmentName;
/*    */   }
/*    */   
/*    */   public BufferedImage getFragmentImage() {
/* 28 */     return this.fragmentImage;
/*    */   }
/*    */   
/*    */   public int getX() {
/* 32 */     return this.x;
/*    */   }
/*    */   
/*    */   public int getY() {
/* 36 */     return this.y;
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 40 */     return this.width;
/*    */   }
/*    */   
/*    */   public int getHeight() {
/* 44 */     return this.height;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\Fragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */