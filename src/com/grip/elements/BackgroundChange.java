package com.grip.elements;

import com.grip.saving.SaveData;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class BackgroundChange extends SaveData {
   private final int x;

   protected BackgroundChange(int x) {
      this.x = x;
   }

   public abstract int getTypeId();

   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
      outputStream.writeInt(this.x);
      outputStream.writeByte(this.getTypeId());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && !o.getClass().isInstance(this.getClass())) {
         BackgroundChange that = (BackgroundChange)o;
         return this.x == that.x;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.x;
   }

   public int getX() {
      return this.x;
   }

   public abstract String getImageTitle();
}
