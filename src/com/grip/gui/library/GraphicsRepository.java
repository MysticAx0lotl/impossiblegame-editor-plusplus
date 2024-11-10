package com.grip.gui.library;

import com.grip.Fragment;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

public class GraphicsRepository {
   private final Map fragments = new HashMap();

   public GraphicsRepository() {
   }

   public GraphicsRepository(List fragments) {
      this.addFragments(fragments);
   }

   public void addFragments(List fragments) {
      Iterator i$ = fragments.iterator();

      while(i$.hasNext()) {
         Fragment fragment = (Fragment)i$.next();
         this.fragments.put(fragment.getFragmentName(), fragment);
      }

   }

   public Fragment getFragment(String imageTitle) {
      return (Fragment)this.fragments.get(imageTitle);
   }

   public ImageIcon getImageIcon(String imageTitle) {
      return new ImageIcon(this.getFragment(imageTitle).getFragmentImage());
   }
}
