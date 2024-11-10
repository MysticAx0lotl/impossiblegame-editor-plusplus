package com.grip.elements;

import com.grip.saving.SaveData;
import java.io.DataOutputStream;
import java.io.IOException;

public class BlocksRise extends SaveData {
   private final int startX;
   private final int endX;

   public BlocksRise(int startX, int endX) {
      this.startX = startX;
      this.endX = endX;
   }

   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
      outputStream.writeInt(this.startX);
      outputStream.writeInt(this.endX);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BlocksRise that = (BlocksRise)o;
         if (this.endX != that.endX) {
            return false;
         } else {
            return this.startX == that.startX;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.startX;
      result = 31 * result + this.endX;
      return result;
   }

   public int getX() {
      return this.startX;
   }
}
