package com.grip.level;

import com.grip.elements.BackgroundChange;
import com.grip.elements.BackgroundChangeCustom;
import com.grip.elements.BackgroundChangeInternal;
import com.grip.elements.BlockObject;
import com.grip.elements.BlocksFall;
import com.grip.elements.BlocksRise;
import com.grip.elements.GravityChange;
import com.grip.elements.LevelObject;
import com.grip.elements.PitObject;
import com.grip.elements.SpikeObject;
import com.grip.loading.LevelLoader;
import com.grip.saving.LevelSaver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LevelDefinition {
   private final int START_OFFSET = 810;
   private boolean fileLoaded = false;
   private boolean usesSpecialGraphics;
   private final List levelObjects = new ArrayList();
   private int levelEnd;
   private final List backgroundChanges = new ArrayList();
   private final List gravityChanges = new ArrayList();
   private final List blocksFallList = new ArrayList();
   private final List blocksRiseList = new ArrayList();
   private LevelViewSettings levelViewSettings = null;

   public void attachLevelViewSettings(LevelViewSettings levelViewSettings) {
      this.levelViewSettings = levelViewSettings;
   }

   public boolean newLevel() {
      this.getBackgroundChanges().clear();
      this.getLevelObjects().clear();
      this.getGravityChanges().clear();
      this.getBlocksFallList().clear();
      this.getBlocksRiseList().clear();
      this.usesSpecialGraphics = false;
      int gridSize = 30;
      this.levelEnd = 100 * gridSize + gridSize / 2;
      this.finishLoading();
      if (this.levelViewSettings != null) {
         this.levelViewSettings.levelDefinitionHasNewFile();
      }

      return true;
   }

   public boolean openFile(String file) throws IOException {
      LevelDefinition levelDefinition = new LevelDefinition();
      boolean result = LevelLoader.load(file, levelDefinition);
      if (result) {
         this.getBackgroundChanges().clear();
         this.getLevelObjects().clear();
         this.getGravityChanges().clear();
         this.getBlocksFallList().clear();
         this.getBlocksRiseList().clear();
         this.usesSpecialGraphics = false;
         this.levelEnd = -1;
         this.getBackgroundChanges().addAll(levelDefinition.getBackgroundChanges());
         this.getLevelObjects().addAll(levelDefinition.getLevelObjects());
         this.getGravityChanges().addAll(levelDefinition.getGravityChanges());
         this.getBlocksFallList().addAll(levelDefinition.getBlocksFallList());
         this.getBlocksRiseList().addAll(levelDefinition.getBlocksRiseList());
         this.usesSpecialGraphics = levelDefinition.isUsesSpecialGraphics();
         this.levelEnd = levelDefinition.getLevelEnd();
         if (!this.fileLoaded) {
            this.finishLoading();
         }

         if (this.levelViewSettings != null) {
            this.levelViewSettings.levelDefinitionHasNewFile();
         }
      }

      return result;
   }

   public boolean saveFile(String file) throws IOException {
      if (!this.fileLoaded) {
         return false;
      } else {
         this.sortAll();
         return LevelSaver.saveVersion0(file, this.usesSpecialGraphics, this.levelObjects, this.levelEnd, this.backgroundChanges, this.gravityChanges, this.blocksFallList, this.blocksRiseList);
      }
   }

   public void setUsesSpecialGraphics(boolean usesSpecialGraphics) {
      this.usesSpecialGraphics = usesSpecialGraphics;
   }

   public boolean isCollidingWithExisting(int x, int y, int collisionWidth, int collisionHeight) {
      return this.getFirstColliding(x, y, collisionWidth, collisionHeight) != null;
   }

   public LevelObject getFirstColliding(int x, int y, int collisionWidth, int collisionHeight) {
      if (x < 0) {
         return null;
      } else {
         Iterator i$ = this.levelObjects.iterator();

         LevelObject levelObject;
         do {
            if (!i$.hasNext()) {
               return null;
            }

            levelObject = (LevelObject)i$.next();
         } while(!levelObject.isColliding(x, y, collisionWidth, collisionHeight));

         return levelObject;
      }
   }

   public boolean addBlock(int x, int y) {
      if (x >= 810 && y >= 0) {
         if (this.fileLoaded && this.isCollidingWithExisting(x, y, BlockObject.getCollisionWidthStatic(), BlockObject.getCollisionHeightStatic())) {
            return false;
         } else {
            this.levelObjects.add(new BlockObject(x, y));
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean addSpike(int x, int y) {
      if (x >= 810 && y >= 0) {
         if (this.fileLoaded && this.isCollidingWithExisting(x, y, SpikeObject.getCollisionWidthStatic(), SpikeObject.getCollisionHeightStatic())) {
            return false;
         } else {
            this.levelObjects.add(new SpikeObject(x, y));
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean addPit(int x, int y) {
      if (x >= 810 && y >= 0) {
         LevelObject pitObject = new PitObject(x, y);
         boolean merged = false;
         List toRemove = new ArrayList();
         Iterator i$ = this.levelObjects.iterator();

         while(i$.hasNext()) {
            LevelObject levelObject = (LevelObject)i$.next();
            if (levelObject.tryMergeWith((LevelObject)pitObject)) {
               if (merged) {
                  toRemove.add(pitObject);
               }

               pitObject = levelObject;
               merged = true;
            }
         }

         this.levelObjects.removeAll(toRemove);
         if (!merged) {
            this.levelObjects.add(pitObject);
         }

         return true;
      } else {
         return false;
      }
   }

   public void deleteObject(LevelObject toDelete) {
      this.levelObjects.remove(toDelete);
   }

   public void setLevelEnd(int levelEnd) {
      this.levelEnd = levelEnd;
   }

   public void removeBackgroundChangesAfterX(int x) {
      boolean changed = true;

      while(true) {
         while(changed) {
            changed = false;
            Iterator i$ = this.backgroundChanges.iterator();

            while(i$.hasNext()) {
               BackgroundChange change = (BackgroundChange)i$.next();
               if (change.getX() >= x) {
                  this.backgroundChanges.remove(change);
                  changed = true;
                  break;
               }
            }
         }

         return;
      }
   }

   public void addBackgroundChangeInternal(int x, int bgId) {
      this.removeBackgroundChangesAfterX(x);
      BackgroundChangeInternal newChange = new BackgroundChangeInternal(x, bgId);
      if (this.backgroundChanges.isEmpty()) {
         this.backgroundChanges.add(newChange);
      } else {
         BackgroundChange lastChange = (BackgroundChange)this.backgroundChanges.get(this.backgroundChanges.size() - 1);
         if (!lastChange.getImageTitle().equals(newChange.getImageTitle())) {
            this.backgroundChanges.add(newChange);
         }

      }
   }

   public void addBackgroundChangeCustom(int x, String bgName) {
      this.removeBackgroundChangesAfterX(x);
      this.backgroundChanges.add(new BackgroundChangeCustom(x, bgName));
   }

   public void addGravityChange(int x) {
      this.gravityChanges.add(new GravityChange(x));
   }

   public void addBlocksFalling(int startX, int startY) {
      this.blocksFallList.add(new BlocksFall(startX, startY));
   }

   public void addBlocksRising(int startX, int startY) {
      this.blocksRiseList.add(new BlocksRise(startX, startY));
   }

   public boolean isUsesSpecialGraphics() {
      return this.usesSpecialGraphics;
   }

   public List getLevelObjects() {
      return this.levelObjects;
   }

   public int getLevelEnd() {
      return this.levelEnd;
   }

   public List getBackgroundChanges() {
      return this.backgroundChanges;
   }

   public List getGravityChanges() {
      return this.gravityChanges;
   }

   public List getBlocksFallList() {
      return this.blocksFallList;
   }

   public List getBlocksRiseList() {
      return this.blocksRiseList;
   }

   public boolean equals(Object other) {
      if (other != null && other.getClass() == this.getClass()) {
         LevelDefinition otherLevelDefinition = (LevelDefinition)other;
         if (this.usesSpecialGraphics != otherLevelDefinition.usesSpecialGraphics) {
            return false;
         } else if (!this.levelObjects.equals(otherLevelDefinition.levelObjects)) {
            return false;
         } else if (this.levelEnd != otherLevelDefinition.levelEnd) {
            return false;
         } else if (!this.backgroundChanges.equals(otherLevelDefinition.backgroundChanges)) {
            return false;
         } else if (!this.gravityChanges.equals(otherLevelDefinition.gravityChanges)) {
            return false;
         } else if (!this.blocksFallList.equals(otherLevelDefinition.blocksFallList)) {
            return false;
         } else {
            return this.blocksRiseList.equals(otherLevelDefinition.blocksRiseList);
         }
      } else {
         return false;
      }
   }

   public int getActualLevelEnd() {
      return this.getLevelEnd() - 300;
   }

   public void setActualLevelEnd(int x) {
      this.setLevelEnd(x);
   }

   private void sortAll() {
      Collections.sort(this.levelObjects, new Comparator() {
         public int compare(LevelObject o1, LevelObject o2) {
            return o1.getX() - o2.getX();
         }
      });
      Collections.sort(this.backgroundChanges, new Comparator() {
         public int compare(BackgroundChange o1, BackgroundChange o2) {
            return o1.getX() - o2.getX();
         }
      });
      Collections.sort(this.gravityChanges, new Comparator() {
         public int compare(GravityChange o1, GravityChange o2) {
            return o1.getX() - o2.getX();
         }
      });
      Collections.sort(this.blocksFallList, new Comparator() {
         public int compare(BlocksFall o1, BlocksFall o2) {
            return o1.getX() - o2.getX();
         }
      });
      Collections.sort(this.blocksRiseList, new Comparator() {
         public int compare(BlocksRise o1, BlocksRise o2) {
            return o1.getX() - o2.getX();
         }
      });
   }

   public void finishLoading() {
      this.sortAll();
      this.fileLoaded = true;
   }

   public boolean isFileLoaded() {
      return this.fileLoaded;
   }

   public int getStartOffset() {
      return 810;
   }

   public void clearBackgroundChanges() {
      this.backgroundChanges.clear();
   }
}
