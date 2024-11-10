package com.grip.elements;

import com.grip.gui.library.GraphicsRepository;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.List;

public class BlockObject extends LevelObject {
   public BlockObject(int x, int y) {
      super(x, y);
   }

   public byte getTypeId() {
      return 0;
   }

   public List getValuesForSaving() {
      return null;
   }

   public String getImageName() {
      return "block";
   }

   public boolean drawObject(ImageObserver imageObserver, Graphics2D graph, GraphicsRepository graphicsRepository, double baseLine, double scaleRatio, int currentXStart, int scaledTargetWidth) {
      int width = this.getWidth();
      int height = this.getHeight();
      if (!this.isVisible(currentXStart, scaledTargetWidth)) {
         return false;
      } else {
         graph.drawImage(graphicsRepository.getFragment(this.getImageName()).getFragmentImage(), (int)Math.floor((double)(this.getX() - currentXStart) * scaleRatio), (int)Math.floor(baseLine + 1.0 - (double)(this.getY() + height) * scaleRatio), (int)Math.ceil((double)width * scaleRatio), (int)Math.ceil((double)height * scaleRatio), imageObserver);
         return true;
      }
   }

   public boolean isVisible(int currentXStart, int scaledTargetWidth) {
      int width = this.getWidth();
      if (this.getX() < currentXStart - width) {
         return false;
      } else {
         return this.getX() <= currentXStart + width + scaledTargetWidth;
      }
   }

   public int getWidth() {
      return getWidthStatic();
   }

   public int getHeight() {
      return getHeightStatic();
   }

   public static int getWidthStatic() {
      return 30;
   }

   public static int getHeightStatic() {
      return 30;
   }

   public static int getCollisionWidthStatic() {
      return getWidthStatic();
   }

   public static int getCollisionHeightStatic() {
      return getHeightStatic();
   }

   public int getCollisionX() {
      return this.getX();
   }

   public int getCollisionY() {
      return this.getY();
   }

   public int getCollisionWidth() {
      return this.getWidth();
   }

   public int getCollisionHeight() {
      return this.getHeight();
   }

   public boolean isColliding(int collisionX, int collisionY, int collisionWidth, int collisionHeight) {
      int diffX = collisionX - this.getCollisionX();
      if (diffX > 0 && diffX >= this.getCollisionWidth()) {
         return false;
      } else if (diffX < 0 && -diffX >= collisionWidth) {
         return false;
      } else {
         int diffY = collisionY - this.getCollisionY();
         if (diffY > 0 && diffY >= this.getCollisionHeight()) {
            return false;
         } else {
            return diffY >= 0 || -diffY < collisionHeight;
         }
      }
   }

   public boolean tryMergeWith(LevelObject levelObject) {
      return false;
   }
}
