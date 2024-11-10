package com.grip;

import java.awt.image.BufferedImage;

public class Fragment {
   private final String fragmentName;
   private final BufferedImage fragmentImage;
   private final int x;
   private final int y;
   private final int width;
   private final int height;

   public Fragment(SourceImage sourceImage, String imageName, float x, float y, float width, float height) {
      this.fragmentName = imageName;
      this.fragmentImage = sourceImage.getSubImage(x, y, width, height);
      this.x = (int)(x * (float)sourceImage.getWidth());
      this.y = (int)(y * (float)sourceImage.getHeight());
      this.width = (int)(width * (float)sourceImage.getWidth());
      this.height = (int)(height * (float)sourceImage.getHeight());
   }

   public String getFragmentName() {
      return this.fragmentName;
   }

   public BufferedImage getFragmentImage() {
      return this.fragmentImage;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }
}
