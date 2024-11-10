package com.grip;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class SourceImage {
   private final String fileName;
   private final String filePath;
   private final BufferedImage sourceImage;
   private final TransparencyMode transparencyMode;
   private final List fragmentsList = new ArrayList();
   private final boolean isResource;

   public SourceImage(String contentRoot, boolean isResource, String fileName, TransparencyMode transparencyMode) throws IOException {
      this.isResource = isResource;
      this.filePath = contentRoot + (contentRoot.charAt(contentRoot.length() - 1) != '/' ? "/" : "") + fileName;
      this.fileName = fileName;
      if (!isResource) {
         this.sourceImage = ImageIO.read(new File(this.filePath));
      } else {
         this.sourceImage = ImageIO.read(this.getClass().getResourceAsStream(this.filePath));
      }

      this.transparencyMode = transparencyMode;
   }

   public BufferedImage getSubImage(float x, float y, float width, float height) {
      return this.sourceImage.getSubimage((int)(x * (float)this.sourceImage.getWidth()), (int)(y * (float)this.sourceImage.getHeight()), (int)(width * (float)this.sourceImage.getWidth()), (int)(height * (float)this.sourceImage.getHeight()));
   }

   public void addFragment(Fragment fragment) {
      this.fragmentsList.add(fragment);
   }

   public int getWidth() {
      return this.sourceImage.getWidth();
   }

   public int getHeight() {
      return this.sourceImage.getHeight();
   }

   public TransparencyMode getTransparencyMode() {
      return this.transparencyMode;
   }
}
