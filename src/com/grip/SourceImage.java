/*    */ package com.grip;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.imageio.ImageIO;
/*    */ 
/*    */ public class SourceImage
/*    */ {
/*    */   private final String fileName;
/*    */   private final String filePath;
/*    */   private final BufferedImage sourceImage;
/*    */   private final TransparencyMode transparencyMode;
/* 16 */   private final List<Fragment> fragmentsList = new ArrayList<Fragment>();
/*    */   private final boolean isResource;
/*    */   
/*    */   public SourceImage(String contentRoot, boolean isResource, String fileName, TransparencyMode transparencyMode) throws IOException {
/* 20 */     this.isResource = isResource;
/* 21 */     this.filePath = contentRoot + ((contentRoot.charAt(contentRoot.length() - 1) != '/') ? "/" : "") + fileName;
/*    */     
/* 23 */     this.fileName = fileName;
/* 24 */     if (!isResource) {
/* 25 */       this.sourceImage = ImageIO.read(new File(this.filePath));
/*    */     } else {
/* 27 */       this.sourceImage = ImageIO.read(getClass().getResourceAsStream(this.filePath));
/*    */     } 
/* 29 */     this.transparencyMode = transparencyMode;
/*    */   }
/*    */   
/*    */   public BufferedImage getSubImage(float x, float y, float width, float height) {
/* 33 */     return this.sourceImage.getSubimage((int)(x * this.sourceImage.getWidth()), (int)(y * this.sourceImage.getHeight()), (int)(width * this.sourceImage.getWidth()), (int)(height * this.sourceImage.getHeight()));
/*    */   }
/*    */ 
/*    */   
/*    */   public void addFragment(Fragment fragment) {
/* 38 */     this.fragmentsList.add(fragment);
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 42 */     return this.sourceImage.getWidth();
/*    */   }
/*    */   
/*    */   public int getHeight() {
/* 46 */     return this.sourceImage.getHeight();
/*    */   }
/*    */   
/*    */   public TransparencyMode getTransparencyMode() {
/* 50 */     return this.transparencyMode;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\SourceImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */