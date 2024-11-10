package com.grip.elements;

import com.grip.saving.SaveData;
import java.io.DataOutputStream;
import java.io.IOException;

public class GravityChange extends SaveData {
   private final int positionX;

   public GravityChange(int positionX) {
      this.positionX = positionX;
   }

   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
      outputStream.writeInt(this.positionX);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         GravityChange that = (GravityChange)o;
         return this.positionX == that.positionX;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.positionX;
   }

   public int getX() {
      return this.positionX;
   }
}
