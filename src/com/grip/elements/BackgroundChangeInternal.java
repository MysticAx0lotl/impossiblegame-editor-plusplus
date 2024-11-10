package com.grip.elements;

import java.io.DataOutputStream;
import java.io.IOException;

public class BackgroundChangeInternal extends BackgroundChange {
   private final int backgroundId;

   public BackgroundChangeInternal(int positionX, int backgroundId) {
      super(positionX);
      this.backgroundId = backgroundId;
   }

   public int getTypeId() {
      return 0;
   }

   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
      super.writeToDataOutputStream(outputStream);
      outputStream.writeInt(this.backgroundId);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            BackgroundChangeInternal that = (BackgroundChangeInternal)o;
            return this.backgroundId == that.backgroundId;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.backgroundId;
      return result;
   }

   public int getBackgroundId() {
      return this.backgroundId;
   }

   public String getImageTitle() {
      int id = this.getBackgroundId() + 1;
      return "background" + id;
   }
}
