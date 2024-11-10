package com.grip.saving;

import com.grip.elements.BackgroundChange;
import com.grip.elements.BlocksFall;
import com.grip.elements.BlocksRise;
import com.grip.elements.GravityChange;
import com.grip.elements.LevelObject;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class LevelSaver {
   public static boolean saveVersion0(String path, boolean useSpecialGraphics, List levelObjects, int levelEnd, List backgroundChanges, List gravityChanges, List blocksFallingIntervals, List blocksRisingIntervals) throws IOException {
      DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path));
      outputStream.writeInt(0);
      outputStream.writeBoolean(useSpecialGraphics);
      outputStream.writeShort(levelObjects.size());
      Iterator i$ = levelObjects.iterator();

      while(i$.hasNext()) {
         LevelObject levelObject = (LevelObject)i$.next();
         levelObject.writeToDataOutputStream(outputStream);
      }

      outputStream.writeInt(levelEnd);
      outputStream.writeInt(backgroundChanges.size());
      i$ = backgroundChanges.iterator();

      while(i$.hasNext()) {
         BackgroundChange backgroundChange = (BackgroundChange)i$.next();
         backgroundChange.writeToDataOutputStream(outputStream);
      }

      outputStream.writeInt(gravityChanges.size());
      i$ = gravityChanges.iterator();

      while(i$.hasNext()) {
         GravityChange gravityChange = (GravityChange)i$.next();
         gravityChange.writeToDataOutputStream(outputStream);
      }

      outputStream.writeInt(blocksFallingIntervals.size());
      i$ = blocksFallingIntervals.iterator();

      while(i$.hasNext()) {
         BlocksFall blocksFall = (BlocksFall)i$.next();
         blocksFall.writeToDataOutputStream(outputStream);
      }

      outputStream.writeInt(blocksRisingIntervals.size());
      i$ = blocksRisingIntervals.iterator();

      while(i$.hasNext()) {
         BlocksRise blocksRise = (BlocksRise)i$.next();
         blocksRise.writeToDataOutputStream(outputStream);
      }

      outputStream.close();
      return true;
   }
}
