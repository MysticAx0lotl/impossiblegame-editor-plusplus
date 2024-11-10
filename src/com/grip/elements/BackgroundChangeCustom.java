package com.grip.elements;

import java.io.DataOutputStream;
import java.io.IOException;

public class BackgroundChangeCustom extends BackgroundChange {
   private final String backgroundName;

   public BackgroundChangeCustom(int positionX, String backgroundName) {
      super(positionX);
      this.backgroundName = backgroundName;
   }

   public int getTypeId() {
      return 1;
   }

   public void writeToDataOutputStream(DataOutputStream outputStream) throws IOException {
      super.writeToDataOutputStream(outputStream);
      outputStream.writeChars(this.backgroundName);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            BackgroundChangeCustom that = (BackgroundChangeCustom)o;
            return this.backgroundName.equals(that.backgroundName);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.backgroundName.hashCode();
      return result;
   }

   public String getImageTitle() {
      return "CstBg" + this.getBackgroundName();
   }

   public String getBackgroundName() {
      return this.backgroundName;
   }
}
