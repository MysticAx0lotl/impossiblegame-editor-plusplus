package com.grip.elements;

import com.grip.gui.library.GraphicsRepository;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public abstract class LevelObject {
   private int x;
   private int y;

   public abstract int getCollisionX();

   public abstract int getCollisionY();

   public abstract int getCollisionWidth();

   public abstract int getCollisionHeight();

   public abstract boolean isColliding(int var1, int var2, int var3, int var4);

   public abstract boolean tryMergeWith(LevelObject var1);

   protected LevelObject(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public abstract byte getTypeId();

   public abstract List getValuesForSaving();

   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
      outputStream.writeByte(this.getTypeId());
      outputStream.writeInt(this.x);
      outputStream.writeInt(this.y);
      List valuesForSaving = this.getValuesForSaving();
      if (valuesForSaving != null) {
         Iterator i$ = valuesForSaving.iterator();

         while(i$.hasNext()) {
            int value = (Integer)i$.next();
            outputStream.writeInt(value);
         }
      }

   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && !o.getClass().isInstance(this.getClass())) {
         LevelObject that = (LevelObject)o;
         if (this.x != that.x) {
            return false;
         } else {
            return this.y == that.y;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.x;
      result = 31 * result + this.y;
      return result;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public abstract String getImageName();

   public abstract boolean drawObject(ImageObserver var1, Graphics2D var2, GraphicsRepository var3, double var4, double var6, int var8, int var9);

   public abstract boolean isVisible(int var1, int var2);

   public abstract int getWidth();

   public abstract int getHeight();

   protected void setX(int x) {
      this.x = x;
   }

   protected void setY(int y) {
      this.y = y;
   }

   public static enum ObjectType {
      BLOCK,
      SPIKE,
      PIT,
      DELETE,
      MARK,
      LEVELEND,
      BACKGROUND;
   }
}
