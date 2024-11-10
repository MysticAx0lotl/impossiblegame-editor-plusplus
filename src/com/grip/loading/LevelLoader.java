package com.grip.loading;

import com.grip.level.LevelDefinition;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class LevelLoader {
   public static boolean load(String path, LevelDefinition levelDefinition) throws IOException {
      DataInputStream inputStream = new DataInputStream(new FileInputStream(path));
      int version = inputStream.readInt();
      switch (version) {
         case 0:
            loadVersion0(inputStream, levelDefinition);
            inputStream.close();
            return true;
         default:
            inputStream.close();
            return false;
      }
   }

   protected static boolean loadVersion0(DataInputStream inputStream, LevelDefinition levelDefinition) throws IOException {
      levelDefinition.setUsesSpecialGraphics(inputStream.readBoolean());
      short objectsCount = inputStream.readShort();

      int backgroundChangesCount;
      int gravityChangesCount;
      int blocksFallingCount;
      int blocksRisingCount;
      for(backgroundChangesCount = 0; backgroundChangesCount < objectsCount; ++backgroundChangesCount) {
         gravityChangesCount = inputStream.readByte();
         blocksFallingCount = inputStream.readInt();
         blocksRisingCount = inputStream.readInt();
         switch (gravityChangesCount) {
            case 0:
               levelDefinition.addBlock(blocksFallingCount, blocksRisingCount);
               break;
            case 1:
               levelDefinition.addSpike(blocksFallingCount, blocksRisingCount);
               break;
            case 2:
               levelDefinition.addPit(blocksFallingCount, blocksRisingCount);
               break;
            default:
               return false;
         }
      }

      levelDefinition.setLevelEnd(inputStream.readInt());
      backgroundChangesCount = inputStream.readInt();

      int i;
      for(gravityChangesCount = 0; gravityChangesCount < backgroundChangesCount; ++gravityChangesCount) {
         blocksFallingCount = inputStream.readInt();
         byte type = inputStream.readByte();
         switch (type) {
            case 0:
               i = inputStream.readInt();
               levelDefinition.addBackgroundChangeInternal(blocksFallingCount, i);
               break;
            case 1:
               String bgName = inputStream.readUTF();
               levelDefinition.addBackgroundChangeCustom(blocksFallingCount, bgName);
               break;
            default:
               return false;
         }
      }

      gravityChangesCount = inputStream.readInt();

      for(blocksFallingCount = 0; blocksFallingCount < gravityChangesCount; ++blocksFallingCount) {
         levelDefinition.addGravityChange(inputStream.readInt());
      }

      blocksFallingCount = inputStream.readInt();

      for(blocksRisingCount = 0; blocksRisingCount < blocksFallingCount; ++blocksRisingCount) {
         levelDefinition.addBlocksFalling(inputStream.readInt(), inputStream.readInt());
      }

      blocksRisingCount = inputStream.readInt();

      for(i = 0; i < blocksRisingCount; ++i) {
         levelDefinition.addBlocksRising(inputStream.readInt(), inputStream.readInt());
      }

      levelDefinition.finishLoading();
      return true;
   }
}
