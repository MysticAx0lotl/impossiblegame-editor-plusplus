package com.grip.elements;

import com.grip.gui.library.GraphicsRepository;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.List;

public class PitObject extends LevelObject {
   public PitObject(int x, int y) {
      super(x, y);
   }

   public byte getTypeId() {
      return 2;
   }

   public List getValuesForSaving() {
      return null;
   }

   public String getImageName() {
      return "pit";
   }

   public boolean drawObject(ImageObserver imageObserver, Graphics2D graph, GraphicsRepository graphicsRepository, double baseLine, double scaleRatio, int currentXStart, int scaledTargetWidth) {
      int width = getImageWidth();
      int height = this.getHeight();
      if (!this.isVisible(currentXStart, scaledTargetWidth)) {
         return false;
      } else {
         int i = (int)((double)(this.getX() - currentXStart) * scaleRatio);
         int pitWidth = (int)((double)width * scaleRatio);
         int pitHeight = (int)((double)height * scaleRatio);

         double max;
         for(max = (double)(this.getY() - currentXStart) * scaleRatio; (double)i < max - (double)pitWidth; i += pitWidth) {
            graph.drawImage(graphicsRepository.getFragment(this.getImageName()).getFragmentImage(), i, (int)baseLine + 1, pitWidth, pitHeight, imageObserver);
         }

         double diff = max + (double)pitWidth - (double)i + 1.0;
         graph.drawImage(graphicsRepository.getFragment(this.getImageName()).getFragmentImage(), i, (int)baseLine + 1, (int)diff, pitHeight, imageObserver);
         return true;
      }
   }

   public boolean isVisible(int currentXStart, int scaledTargetWidth) {
      int width = this.getWidth();
      if (this.getY() < currentXStart - width) {
         return false;
      } else {
         return this.getX() <= currentXStart + width + scaledTargetWidth;
      }
   }

   public static int getImageWidth() {
      return 30;
   }

   public int getWidth() {
      return this.getY() - this.getX();
   }

   public int getHeight() {
      return getHeightStatic();
   }

   public static int getHeightStatic() {
      return 30;
   }

   public int getCollisionX() {
      return this.getX();
   }

   public int getCollisionY() {
      return -this.getCollisionHeight();
   }

   public int getCollisionWidth() {
      return this.getY() - this.getX() + getImageWidth();
   }

   public int getCollisionHeight() {
      return getHeightStatic();
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
      if (!(levelObject instanceof PitObject)) {
         return false;
      } else {
         PitObject pitObject = (PitObject)levelObject;
         if (pitObject.getY() + getImageWidth() >= this.getX() && this.getY() + getImageWidth() >= pitObject.getX()) {
            this.setX(Math.min(pitObject.getX(), this.getX()));
            this.setY(Math.max(pitObject.getY(), this.getY()));
            return true;
         } else {
            return false;
         }
      }
   }
}
