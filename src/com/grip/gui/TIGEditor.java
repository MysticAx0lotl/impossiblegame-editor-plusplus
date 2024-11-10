/*     */ package com.grip.gui;
/*     */ import com.grip.FragmentReader;
/*     */ import com.grip.elements.LevelObject;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.filechooser.FileNameExtensionFilter;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class TIGEditor {
/*  29 */   private final int audioTickLength = 50;
/*     */   private JFrame mainFrame;
/*     */   private LevelNavigator levelNavigator;
/*     */   private LevelViewSettings levelViewSettings;
/*     */   private JMenuBar menuBar;
/*     */   public JButton newFileButton;
/*     */   public JButton openFileButton;
/*     */   public JButton saveFileButton;
/*     */   public JToggleButton gridToggleButton;
/*     */   public JToggleButton snapToGridButton;
/*     */   public JToggleButton blockButton;
/*     */   public JToggleButton spikeButton;
/*     */   public JToggleButton pitButton;
/*     */   public JToggleButton deleteObjectButton;
/*     */   public JButton rewindMusicButton;
/*     */   public JButton playFromMarkButton;
/*     */   public JToggleButton placeMusicMarkButton;
/*     */   public JToggleButton placeLevelEndButton;
/*     */   public JToggleButton placeBackgroundButton;
/*     */   public JButton deleteMusicMarkButton;
/*     */   public JToggleButton playPauseButton;
/*     */   public Box backgroundTypePanel;
/*     */   public JComboBox backgroundTypeComboBox;
/*     */   public JLabel backgroundTypeLabel;
/*     */   public JButton openAudioButton;
/*     */   public JButton launchInGameButton;
/*     */   private LevelNavigatorScrollBar levelNavigatorScrollBar;
/*     */   private GraphicsRepository graphicsRepository;
/*     */   private LevelDefinition levelDefinition;
/*     */   private JToolBar toolBarPanel;
/*     */   private JFileChooser openFile;
/*     */   private JFileChooser saveFile;
/*     */   private JFileChooser openAudioFile;
/*     */   private boolean isGridEnabledInitially = true;
/*  63 */   public final ButtonGroup navigatorClickTypeButtonGroup = new ButtonGroup();
/*     */   
/*     */   private boolean isSnapToGridEnabledInitially = true;
/*  66 */   private String includedResourcesDir = "/base/";
/*     */   private File levelFile;
/*     */   private File audioFile;
/*     */   private boolean debugLaunch;
/*  70 */   private File levelFileDir = null;
/*     */   private boolean firstSaveDone = false;
/*     */   
/*     */   public TIGEditor(boolean debugLaunch) {
/*  74 */     this.debugLaunch = debugLaunch;
/*  75 */     if (debugLaunch) {
/*  76 */       System.out.println("Launching in debug mode");
/*     */     }
/*  78 */     createUIComponents();
/*     */   }
/*     */ 
/*     */   
/*     */   private JFileChooser prepareFileDialog(String dialogTitle, int dialogType, boolean directoriesOnly, FileNameExtensionFilter fileNameExtensionFilter) {
/*  83 */     List<FileNameExtensionFilter> filters = new ArrayList<FileNameExtensionFilter>();
/*  84 */     filters.add(fileNameExtensionFilter);
/*     */     
/*  86 */     return prepareFileDialog(dialogTitle, dialogType, directoriesOnly, filters);
/*     */   }
/*     */ 
/*     */   
/*     */   private JFileChooser prepareFileDialog(String dialogTitle, int dialogType, boolean directoriesOnly, List<FileNameExtensionFilter> fileNameExtensionFilters) {
/*  91 */     JFileChooser fileDialog = new JFileChooser(System.getProperty("user.dir"));
/*  92 */     fileDialog.setDialogTitle(dialogTitle);
/*  93 */     fileDialog.setDialogType(dialogType);
/*  94 */     if (directoriesOnly) {
/*  95 */       fileDialog.setFileSelectionMode(1);
/*     */     }
/*  97 */     else if (fileNameExtensionFilters != null) {
/*  98 */       fileDialog.removeChoosableFileFilter(fileDialog.getAcceptAllFileFilter());
/*  99 */       for (FileNameExtensionFilter filter : fileNameExtensionFilters) {
/* 100 */         fileDialog.addChoosableFileFilter(filter);
/*     */       }
/* 102 */       fileDialog.setFileFilter(fileNameExtensionFilters.get(0));
/*     */     } 
/*     */ 
/*     */     
/* 106 */     return fileDialog;
/*     */   }
/*     */   
/*     */   private JFileChooser prepareFileDialog(String dialogTitle, int dialogType, boolean directoriesOnly) {
/* 110 */     JFileChooser fileDialog = new JFileChooser(System.getProperty("user.dir"));
/* 111 */     fileDialog.setDialogTitle(dialogTitle);
/* 112 */     fileDialog.setDialogType(dialogType);
/*     */     
/* 114 */     if (directoriesOnly) {
/* 115 */       fileDialog.setFileSelectionMode(1);
/*     */     }
/* 117 */     return fileDialog;
/*     */   }
/*     */   
/*     */   private void createUIComponents() {
/*     */     try {
/* 122 */       prepareGraphics();
/*     */       
/* 124 */       prepareMainFrame();
/*     */       
/* 126 */       prepareFileDialogs();
/*     */       
/* 128 */       this.mainFrame.setLayout(new GridBagLayout());
/*     */ 
/*     */ 
/*     */       
/* 132 */       prepareButtons();
/*     */       
/* 134 */       GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*     */       
/* 136 */       prepareHeader(gridBagConstraints);
/*     */       
/* 138 */       prepareToolBar(gridBagConstraints);
/*     */       
/* 140 */       prepareCustomPanels(gridBagConstraints);
/*     */       
/* 142 */       setDefaultSelectedObjectType();
/*     */       
/* 144 */       attachButtonActionHandlers();
/*     */ 
/*     */       
/* 147 */       this.newFileButton.getActionListeners()[0].actionPerformed(null);
/*     */     }
/* 149 */     catch (IOException e) {
/* 150 */       e.printStackTrace();
/* 151 */     } catch (ParserConfigurationException e) {
/* 152 */       e.printStackTrace();
/* 153 */     } catch (SAXException e) {
/* 154 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void attachButtonActionHandlers() {
/* 159 */     this.newFileButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 162 */             TIGEditor.this.levelDefinition.newLevel();
/* 163 */             TIGEditor.this.levelFile = null;
/* 164 */             TIGEditor.this.launchInGameButton.setEnabled(true);
/* 165 */             TIGEditor.this.firstSaveDone = false;
/* 166 */             TIGEditor.this.levelFileDir = null;
/*     */           }
/*     */         });
/*     */     
/* 170 */     this.openFileButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 173 */             if (TIGEditor.this.openFile.showDialog(TIGEditor.this.mainFrame, "Select directory") != 0) {
/*     */               return;
/*     */             }
/*     */             try {
/* 177 */               TIGEditor.this.levelFile = new File(TIGEditor.this.openFile.getSelectedFile().getAbsolutePath() + "/level.dat");
/* 178 */               boolean result = TIGEditor.this.levelDefinition.openFile(TIGEditor.this.levelFile.getAbsolutePath());
/* 179 */               if (!result) {
/* 180 */                 JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Failed to open level at\n" + TIGEditor.this.levelFile.getAbsolutePath());
/* 181 */                 TIGEditor.this.levelFile = null;
/*     */               } else {
/* 183 */                 TIGEditor.this.launchInGameButton.setEnabled(true);
/*     */               } 
/*     */               
/* 186 */               for (String extension : new String[] { ".ogg", ".mp3" }) {
/* 187 */                 TIGEditor.this.audioFile = new File(TIGEditor.this.openFile.getSelectedFile().getAbsolutePath() + "/music" + extension);
/* 188 */                 if (TIGEditor.this.audioFile.exists()) {
/* 189 */                   result = TIGEditor.this.levelViewSettings.setAudioFile(TIGEditor.this.audioFile.getAbsolutePath());
/* 190 */                   if (!result) {
/* 191 */                     JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Failed audio file at\n" + TIGEditor.this.audioFile.getAbsolutePath());
/* 192 */                     TIGEditor.this.audioFile = null; break;
/*     */                   } 
/* 194 */                   TIGEditor.this.playPauseButton.setEnabled(true);
/* 195 */                   TIGEditor.this.playFromMarkButton.setEnabled(true);
/* 196 */                   TIGEditor.this.rewindMusicButton.setEnabled(true);
/*     */                   
/*     */                   break;
/*     */                 } 
/*     */               } 
/*     */               
/* 202 */               if (!TIGEditor.this.audioFile.exists()) {
/* 203 */                 TIGEditor.this.audioFile = null;
/*     */               } else {
/* 205 */                 TIGEditor.this.playPauseButton.setSelected(false);
/*     */               } 
/*     */               
/* 208 */               TIGEditor.this.levelFileDir = TIGEditor.this.openFile.getSelectedFile();
/* 209 */               TIGEditor.this.firstSaveDone = true;
/* 210 */             } catch (IOException e1) {
/* 211 */               JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Failed opening level.\n\n" + e1.toString());
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 216 */     this.openAudioButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 219 */             if (TIGEditor.this.openAudioFile.showDialog(TIGEditor.this.mainFrame, "Select file") != 0) {
/*     */               return;
/*     */             }
/* 222 */             TIGEditor.this.audioFile = TIGEditor.this.openAudioFile.getSelectedFile();
/* 223 */             boolean result = TIGEditor.this.levelViewSettings.setAudioFile(TIGEditor.this.audioFile.getAbsolutePath());
/* 224 */             if (!result) {
/* 225 */               JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Failed audio file at\n" + TIGEditor.this.audioFile.getAbsolutePath());
/* 226 */               TIGEditor.this.audioFile = null;
/*     */             } else {
/* 228 */               TIGEditor.this.playPauseButton.setEnabled(true);
/* 229 */               TIGEditor.this.playFromMarkButton.setEnabled(true);
/* 230 */               TIGEditor.this.rewindMusicButton.setEnabled(true);
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 235 */     this.saveFileButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/*     */             try {
/*     */               String saveDir;
/* 240 */               if (TIGEditor.this.firstSaveDone) {
/* 241 */                 saveDir = TIGEditor.this.levelFileDir.getAbsolutePath();
/*     */               } else {
/* 243 */                 if (TIGEditor.this.saveFile.showDialog(TIGEditor.this.mainFrame, "Save level") != 0) {
/*     */                   return;
/*     */                 }
/* 246 */                 saveDir = TIGEditor.this.saveFile.getSelectedFile().getAbsolutePath();
/*     */               } 
/* 248 */               TIGEditor.this.saveLevel(saveDir);
/* 249 */               TIGEditor.this.firstSaveDone = true;
/* 250 */             } catch (IOException e1) {
/* 251 */               JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Level save failed!\n\n" + e1.toString());
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 256 */     this.gridToggleButton.setSelected(this.isGridEnabledInitially);
/* 257 */     this.gridToggleButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 260 */             TIGEditor.this.levelNavigator.setDrawBlockSizedGrid(TIGEditor.this.gridToggleButton.getModel().isSelected());
/*     */           }
/*     */         });
/*     */     
/* 264 */     this.snapToGridButton.setSelected(this.isSnapToGridEnabledInitially);
/* 265 */     this.snapToGridButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 268 */             TIGEditor.this.levelViewSettings.setSnapToGrid(TIGEditor.this.snapToGridButton.getModel().isSelected());
/*     */           }
/*     */         });
/*     */     
/* 272 */     ActionListener objectTypeListener = new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 275 */           if (e.getSource().equals(TIGEditor.this.blockButton)) {
/* 276 */             TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.BLOCK);
/* 277 */           } else if (e.getSource().equals(TIGEditor.this.spikeButton)) {
/* 278 */             TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.SPIKE);
/* 279 */           } else if (e.getSource().equals(TIGEditor.this.pitButton)) {
/* 280 */             TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.PIT);
/* 281 */           } else if (e.getSource().equals(TIGEditor.this.deleteObjectButton)) {
/* 282 */             TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.DELETE);
/* 283 */           } else if (e.getSource().equals(TIGEditor.this.placeMusicMarkButton)) {
/* 284 */             TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.MARK);
/* 285 */           } else if (e.getSource().equals(TIGEditor.this.placeLevelEndButton)) {
/* 286 */             TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.LEVELEND);
/* 287 */           } else if (e.getSource().equals(TIGEditor.this.placeBackgroundButton)) {
/* 288 */             TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.BACKGROUND);
/*     */           } else {
/* 290 */             throw new RuntimeException("Unrecognized toolbar button selected");
/*     */           } 
/*     */         }
/*     */       };
/*     */     
/* 295 */     this.blockButton.addActionListener(objectTypeListener);
/* 296 */     this.spikeButton.addActionListener(objectTypeListener);
/* 297 */     this.pitButton.addActionListener(objectTypeListener);
/* 298 */     this.deleteObjectButton.addActionListener(objectTypeListener);
/* 299 */     this.placeMusicMarkButton.addActionListener(objectTypeListener);
/* 300 */     this.placeLevelEndButton.addActionListener(objectTypeListener);
/* 301 */     this.placeBackgroundButton.addActionListener(objectTypeListener);
/*     */     
/* 303 */     this.backgroundTypeComboBox.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 306 */             TIGEditor.this.levelViewSettings.setBackgroundType(TIGEditor.this.backgroundTypeComboBox.getSelectedIndex());
/*     */           }
/*     */         });
/*     */     
/* 310 */     this.playPauseButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 313 */             TIGEditor.this.levelViewSettings.doPlayPause();
/*     */           }
/*     */         });
/* 316 */     this.rewindMusicButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 319 */             TIGEditor.this.levelViewSettings.doRewind();
/* 320 */             TIGEditor.this.playPauseButton.setSelected(false);
/*     */           }
/*     */         });
/* 323 */     this.playFromMarkButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 326 */             TIGEditor.this.levelViewSettings.doPlayFromMark();
/* 327 */             TIGEditor.this.playPauseButton.setSelected(true);
/*     */           }
/*     */         });
/* 330 */     this.deleteMusicMarkButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 333 */             TIGEditor.this.levelViewSettings.deleteMark();
/*     */           }
/*     */         });
/*     */     
/* 337 */     this.launchInGameButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/*     */             try {
/* 342 */               if (TIGEditor.this.playPauseButton.isSelected()) {
/* 343 */                 TIGEditor.this.levelViewSettings.doPlayPause();
/*     */               }
/*     */               
/* 346 */               String currentDir = System.getProperty("user.dir") + "/";
/* 347 */               String cacheDir = "editorcache/";
/* 348 */               String levelName = "test";
/* 349 */               File targetDir = new File(currentDir + cacheDir + levelName);
/* 350 */               if (TIGEditor.this.isMac()) {
/* 351 */                 targetDir = new File(currentDir + "TheImpossibleGame.app/Contents/Resources/" + cacheDir + levelName);
/*     */               }
/*     */               
/* 354 */               if (targetDir.exists() && 
/* 355 */                 !TIGEditor.this.deleteDirectory(targetDir)) {
/* 356 */                 throw new IOException("Failed to delete temporary directory:\n" + targetDir.getCanonicalPath());
/*     */               }
/*     */ 
/*     */               
/* 360 */               if (!targetDir.mkdirs()) {
/* 361 */                 throw new IOException("Failed to create directories to save the level:\n" + targetDir.getCanonicalPath());
/*     */               }
/* 363 */               System.out.println("Created dir: " + targetDir.getCanonicalPath());
/*     */ 
/*     */               
/* 366 */               String result = TIGEditor.this.saveFile(targetDir.getCanonicalPath() + "/level.dat", false);
/* 367 */               if (result == null) {
/* 368 */                 throw new IOException("Failed to save level!");
/*     */               }
/* 370 */               if (TIGEditor.this.audioFile != null) {
/* 371 */                 File newAudioFile = TIGEditor.this.getAudioFileNameForSaving(targetDir.getCanonicalPath(), TIGEditor.this.audioFile.getName());
/*     */ 
/*     */                 
/* 374 */                 TIGEditor.this.copyFile(TIGEditor.this.audioFile, newAudioFile);
/*     */               } 
/*     */               
/* 377 */               String[] command = null;
/* 378 */               if (TIGEditor.this.isWindows())
/* 379 */                 command = new String[] { "ImpossibleGame.exe", "log", "level", cacheDir + levelName }; 
/* 380 */               if (TIGEditor.this.isMac()) {
/* 381 */                 command = new String[] { "TheImpossibleGame.app/Contents/MacOS/TheImpossibleGame", "log", "level", cacheDir + levelName };
/*     */               }
/* 383 */               else if (TIGEditor.this.debugLaunch) {
/* 384 */                 command = new String[] { "run.sh", "./ImpossibleGame", "log", "level", cacheDir + levelName };
/*     */               } else {
/* 386 */                 command = new String[] { "./ImpossibleGame", "log", "level", cacheDir + levelName };
/*     */               } 
/*     */ 
/*     */               
/* 390 */               ArrayList<String> enviroVars = new ArrayList<String>();
/* 391 */               boolean foundLibPath = false;
/* 392 */               Map<String, String> env = System.getenv();
/* 393 */               for (Map.Entry<String, String> entry : env.entrySet()) {
/* 394 */                 if (((String)entry.getKey()).compareTo("LD_LIBRARY_PATH") == 0) {
/* 395 */                   foundLibPath = true;
/* 396 */                   enviroVars.add("LD_LIBRARY_PATH=.:" + (String)entry.getValue());
/* 397 */                   System.out.println("Setting " + (String)enviroVars.get(enviroVars.size() - 1)); continue;
/*     */                 } 
/* 399 */                 enviroVars.add((String)entry.getKey() + "=" + (String)entry.getValue());
/*     */               } 
/*     */               
/* 402 */               if (!foundLibPath) {
/* 403 */                 enviroVars.add("LD_LIBRARY_PATH=.");
/* 404 */                 System.out.println("Setting " + (String)enviroVars.get(enviroVars.size() - 1));
/*     */               } 
/*     */ 
/*     */               
/* 408 */               System.out.println("Running " + command[0] + " for " + cacheDir + levelName);
/* 409 */               System.out.println("Curdir: " + currentDir);
/* 410 */               Process p = Runtime.getRuntime().exec(command, enviroVars.<String>toArray(new String[0]), new File(currentDir));
/*     */ 
/*     */               
/* 413 */               BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream())); String line;
/* 414 */               while ((line = in.readLine()) != null) {
/* 415 */                 System.out.println(line);
/*     */               }
/* 417 */               in.close();
/* 418 */               int resultCode = p.waitFor();
/* 419 */               System.out.println("Process exited with code " + resultCode);
/*     */             }
/* 421 */             catch (Exception e1) {
/* 422 */               JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Game launch failed!\n\n" + e1.toString());
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void saveLevel(String saveDir) throws IOException {
/* 429 */     if (!saveDir.endsWith(".lvl")) {
/* 430 */       saveDir = saveDir + ".lvl";
/*     */     }
/* 432 */     File newLevelFile = new File(saveDir + "/level.dat");
/* 433 */     File levelDir = newLevelFile.getParentFile();
/* 434 */     if (!levelDir.exists()) {
/* 435 */       levelDir.mkdirs();
/*     */     }
/*     */     
/* 438 */     File newAudioFile = null;
/* 439 */     if (this.audioFile != null) {
/* 440 */       String audioFileName = this.audioFile.getName();
/* 441 */       newAudioFile = getAudioFileNameForSaving(saveDir, audioFileName);
/* 442 */       copyFile(this.audioFile, newAudioFile);
/*     */     } 
/*     */     
/* 445 */     if (saveFile(newLevelFile.getAbsolutePath(), true) != null) {
/* 446 */       this.levelFile = newLevelFile;
/* 447 */       this.levelFileDir = this.levelFile.getParentFile();
/* 448 */       this.audioFile = newAudioFile;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public File getAudioFileNameForSaving(String saveDir, String audioFileName) {
/* 454 */     String extension = audioFileName.substring(audioFileName.lastIndexOf('.'), audioFileName.length());
/*     */ 
/*     */ 
/*     */     
/* 458 */     File newAudioFile = new File(saveDir + "/music" + extension);
/* 459 */     return newAudioFile;
/*     */   }
/*     */   
/*     */   public String saveFile(String path, boolean showResultMessage) {
/*     */     try {
/* 464 */       if (!path.endsWith(".dat")) {
/* 465 */         path = path + ".dat";
/*     */       }
/* 467 */       boolean result = this.levelDefinition.saveFile(path);
/*     */       
/* 469 */       if (result) {
/* 470 */         if (showResultMessage) {
/* 471 */           JOptionPane.showMessageDialog(this.mainFrame, "Level saved successfully.");
/*     */         }
/* 473 */         return path;
/*     */       } 
/* 475 */       if (showResultMessage) {
/* 476 */         JOptionPane.showMessageDialog(this.mainFrame, "Failed saving level.");
/*     */       }
/* 478 */       return null;
/*     */     }
/* 480 */     catch (IOException e1) {
/* 481 */       JOptionPane.showMessageDialog(this.mainFrame, "Failed saving level.");
/* 482 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void prepareGraphics() throws ParserConfigurationException, SAXException, IOException {
/* 487 */     FragmentReader fragmentReader = readImageList(this.includedResourcesDir + "game1.xml", true);
/* 488 */     this.graphicsRepository = new GraphicsRepository(fragmentReader.getFragments());
/* 489 */     fragmentReader = readImageList(this.includedResourcesDir + "gui.xml", true);
/* 490 */     this.graphicsRepository.addFragments(fragmentReader.getFragments());
/* 491 */     fragmentReader = readImageList(this.includedResourcesDir + "editorheader.xml", true);
/* 492 */     this.graphicsRepository.addFragments(fragmentReader.getFragments());
/*     */   }
/*     */   
/*     */   private void prepareCustomPanels(GridBagConstraints gridBagConstraints) throws IOException {
/* 496 */     this.levelDefinition = new LevelDefinition();
/* 497 */     this.levelNavigator = new LevelNavigator(this, this.graphicsRepository, this.mainFrame.getWidth(), this.mainFrame.getHeight(), this.isGridEnabledInitially);
/* 498 */     this.levelNavigatorScrollBar = new LevelNavigatorScrollBar();
/* 499 */     this.levelViewSettings = new LevelViewSettings(this.graphicsRepository, this.levelDefinition, this.levelNavigator, this.levelNavigatorScrollBar);
/* 500 */     this.levelNavigatorScrollBar.setReferences(this.mainFrame, this.levelViewSettings);
/*     */     
/* 502 */     gridBagConstraints.gridy = 2;
/* 503 */     gridBagConstraints.weighty = 1.0D;
/* 504 */     this.mainFrame.getContentPane().add((Component)this.levelNavigator, gridBagConstraints);
/*     */     
/* 506 */     gridBagConstraints.gridy = 3;
/* 507 */     gridBagConstraints.weighty = 0.0D;
/* 508 */     gridBagConstraints.ipady = 1;
/* 509 */     this.mainFrame.getContentPane().add((Component)this.levelNavigatorScrollBar, gridBagConstraints);
/*     */   }
/*     */   
/*     */   private void prepareButtons() {
/* 513 */     this.newFileButton = new JButton("New", this.graphicsRepository.getImageIcon("btnNew"));
/* 514 */     this.newFileButton.setToolTipText("CTRL/CMD + N");
/* 515 */     this.openFileButton = new JButton("Open", this.graphicsRepository.getImageIcon("btnOpen"));
/* 516 */     this.openFileButton.setToolTipText("CTRL/CMD + O");
/* 517 */     this.saveFileButton = new JButton("Save", this.graphicsRepository.getImageIcon("btnSave"));
/* 518 */     this.saveFileButton.setToolTipText("CTRL/CMD + S");
/*     */     
/* 520 */     this.gridToggleButton = new JToggleButton("Toggle grid", this.graphicsRepository.getImageIcon("btnToggleGrid"));
/* 521 */     this.gridToggleButton.setSelected(this.isGridEnabledInitially);
/*     */     
/* 523 */     this.snapToGridButton = new JToggleButton("Snap to grid", this.graphicsRepository.getImageIcon("btnSnapToGrid"));
/*     */     
/* 525 */     this.blockButton = new JToggleButton("Block", this.graphicsRepository.getImageIcon("btnBlock"));
/* 526 */     this.blockButton.setToolTipText("ALFANUM 1");
/* 527 */     this.navigatorClickTypeButtonGroup.add(this.blockButton);
/* 528 */     this.spikeButton = new JToggleButton("Spike", this.graphicsRepository.getImageIcon("btnSpike"));
/* 529 */     this.spikeButton.setToolTipText("ALFANUM 2");
/* 530 */     this.navigatorClickTypeButtonGroup.add(this.spikeButton);
/* 531 */     this.pitButton = new JToggleButton("Pit", this.graphicsRepository.getImageIcon("btnPit"));
/* 532 */     this.pitButton.setToolTipText("ALFANUM 3");
/* 533 */     this.navigatorClickTypeButtonGroup.add(this.pitButton);
/* 534 */     this.deleteObjectButton = new JToggleButton("Delete object", this.graphicsRepository.getImageIcon("btnDelete"));
/* 535 */     this.deleteObjectButton.setToolTipText("ALFANUM 4");
/* 536 */     this.navigatorClickTypeButtonGroup.add(this.deleteObjectButton);
/* 537 */     this.placeMusicMarkButton = new JToggleButton("Place mark", this.graphicsRepository.getImageIcon("btnMark"));
/* 538 */     this.placeMusicMarkButton.setToolTipText("ALFANUM 5");
/* 539 */     this.navigatorClickTypeButtonGroup.add(this.placeMusicMarkButton);
/* 540 */     this.placeLevelEndButton = new JToggleButton("Level end", this.graphicsRepository.getImageIcon("btnLevelEnd"));
/* 541 */     this.placeLevelEndButton.setToolTipText("ALFANUM 6");
/* 542 */     this.navigatorClickTypeButtonGroup.add(this.placeLevelEndButton);
/* 543 */     this.placeBackgroundButton = new JToggleButton("Place background", this.graphicsRepository.getImageIcon("btnBackground"));
/* 544 */     this.placeBackgroundButton.setToolTipText("ALFANUM 7");
/* 545 */     this.navigatorClickTypeButtonGroup.add(this.placeBackgroundButton);
/*     */     
/* 547 */     int backgroundTypeWidth = 65;
/* 548 */     if (isMac()) {
/* 549 */       backgroundTypeWidth = 70;
/*     */     }
/* 551 */     this.backgroundTypePanel = Box.createVerticalBox();
/* 552 */     this.backgroundTypePanel.setMaximumSize(new Dimension(backgroundTypeWidth, 50));
/* 553 */     this.backgroundTypePanel.setMinimumSize(new Dimension(backgroundTypeWidth, 50));
/* 554 */     this.backgroundTypePanel.setPreferredSize(new Dimension(backgroundTypeWidth, 50));
/* 555 */     this.backgroundTypePanel.setFocusable(false);
/* 556 */     String[] backgroundTypes = { "blue", "yellow", "green", "violet", "pink", "black" };
/* 557 */     this.backgroundTypeComboBox = new JComboBox<String>(backgroundTypes);
/* 558 */     this.backgroundTypeComboBox.setToolTipText("KEY B");
/* 559 */     this.backgroundTypeComboBox.setFocusable(false);
/* 560 */     this.backgroundTypeComboBox.setPreferredSize(new Dimension(35, 37));
/* 561 */     this.backgroundTypePanel.add(this.backgroundTypeComboBox);
/* 562 */     JPanel panel = new JPanel(new BorderLayout());
/* 563 */     this.backgroundTypeLabel = new JLabel("Background:");
/* 564 */     panel.add(this.backgroundTypeLabel);
/* 565 */     this.backgroundTypePanel.add(panel);
/*     */     
/* 567 */     this.rewindMusicButton = new JButton("Rewind", this.graphicsRepository.getImageIcon("btnRewind"));
/* 568 */     this.rewindMusicButton.setToolTipText("KEY I");
/* 569 */     this.playPauseButton = new JToggleButton("Play", this.graphicsRepository.getImageIcon("btnPlay"));
/* 570 */     this.playPauseButton.setToolTipText("KEY P");
/* 571 */     this.playFromMarkButton = new JButton("Play from mark", this.graphicsRepository.getImageIcon("btnPlayFromMark"));
/* 572 */     this.playFromMarkButton.setToolTipText("KEY O");
/*     */     
/* 574 */     this.deleteMusicMarkButton = new JButton("Delete mark", this.graphicsRepository.getImageIcon("btnDeleteMark"));
/* 575 */     this.openAudioButton = new JButton("Open audio", this.graphicsRepository.getImageIcon("btnOpenAudio"));
/* 576 */     this.openAudioButton.setToolTipText("CTRL/CMD + A");
/*     */     
/* 578 */     this.playPauseButton.setEnabled(false);
/* 579 */     this.playFromMarkButton.setEnabled(false);
/* 580 */     this.rewindMusicButton.setEnabled(false);
/*     */     
/* 582 */     this.launchInGameButton = new JButton("Test", this.graphicsRepository.getImageIcon("btnLaunchGame"));
/* 583 */     this.launchInGameButton.setToolTipText("F5");
/* 584 */     this.launchInGameButton.setEnabled(false);
/*     */   }
/*     */   
/*     */   private void setDefaultSelectedObjectType() {
/* 588 */     this.navigatorClickTypeButtonGroup.setSelected(this.blockButton.getModel(), true);
/* 589 */     this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.BLOCK);
/*     */   }
/*     */   
/*     */   private void prepareHeader(GridBagConstraints gridBagConstraints) {
/* 593 */     gridBagConstraints.gridx = 0;
/* 594 */     gridBagConstraints.gridy = 0;
/* 595 */     gridBagConstraints.fill = 2;
/* 596 */     gridBagConstraints.weightx = 0.0D;
/* 597 */     gridBagConstraints.weighty = 0.0D;
/* 598 */     gridBagConstraints.ipady = 0;
/* 599 */     gridBagConstraints.anchor = 23;
/*     */     
/* 601 */     JLabel header = new JLabel(this.graphicsRepository.getImageIcon("editorheader"), 2);
/* 602 */     this.mainFrame.getContentPane().add(header, gridBagConstraints);
/*     */   }
/*     */   
/*     */   private void prepareToolBar(GridBagConstraints gridBagConstraints) {
/* 606 */     gridBagConstraints.gridx = 0;
/* 607 */     gridBagConstraints.gridy = 1;
/* 608 */     gridBagConstraints.fill = 1;
/* 609 */     gridBagConstraints.weightx = 10.0D;
/* 610 */     gridBagConstraints.weighty = 0.0D;
/* 611 */     gridBagConstraints.ipady = 0;
/*     */     
/* 613 */     Dimension separatorSize = new Dimension(20, 60);
/*     */     
/* 615 */     this.toolBarPanel = new JToolBar("Toolbar");
/* 616 */     this.toolBarPanel.setFloatable(false);
/* 617 */     this.toolBarPanel.setFocusable(false);
/* 618 */     this.toolBarPanel.setRequestFocusEnabled(false);
/* 619 */     this.toolBarPanel.setFocusTraversalKeysEnabled(false);
/*     */     
/* 621 */     this.toolBarPanel.add(this.newFileButton);
/* 622 */     this.toolBarPanel.add(this.openFileButton);
/* 623 */     this.toolBarPanel.add(this.openAudioButton);
/* 624 */     this.toolBarPanel.add(this.saveFileButton);
/* 625 */     this.toolBarPanel.add(this.launchInGameButton);
/* 626 */     this.toolBarPanel.addSeparator(separatorSize);
/* 627 */     this.toolBarPanel.add(this.gridToggleButton);
/* 628 */     this.toolBarPanel.add(this.snapToGridButton);
/* 629 */     this.toolBarPanel.addSeparator(separatorSize);
/* 630 */     this.toolBarPanel.add(this.blockButton);
/* 631 */     this.toolBarPanel.add(this.spikeButton);
/* 632 */     this.toolBarPanel.add(this.pitButton);
/* 633 */     this.toolBarPanel.add(this.deleteObjectButton);
/* 634 */     this.toolBarPanel.add(this.placeMusicMarkButton);
/* 635 */     this.toolBarPanel.add(this.placeLevelEndButton);
/* 636 */     this.toolBarPanel.add(this.placeBackgroundButton);
/* 637 */     this.toolBarPanel.addSeparator(separatorSize);
/* 638 */     this.toolBarPanel.add(this.deleteMusicMarkButton);
/* 639 */     this.toolBarPanel.add(this.backgroundTypePanel);
/* 640 */     this.toolBarPanel.addSeparator(separatorSize);
/* 641 */     this.toolBarPanel.add(this.rewindMusicButton);
/* 642 */     this.toolBarPanel.add(this.playFromMarkButton);
/* 643 */     this.toolBarPanel.add(this.playPauseButton);
/*     */     
/* 645 */     for (Component c : this.toolBarPanel.getComponents()) {
/* 646 */       c.setFocusable(false);
/* 647 */       if (c instanceof AbstractButton) {
/* 648 */         ((AbstractButton)c).setVerticalTextPosition(3);
/* 649 */         ((AbstractButton)c).setHorizontalTextPosition(0);
/* 650 */         if (isWindows()) {
/* 651 */           ((AbstractButton)c).setMargin(new Insets(0, 5, 0, 5));
/* 652 */         } else if (isMac()) {
/* 653 */           ((AbstractButton)c).setMargin(new Insets(0, 3, 0, 3));
/* 654 */           if (!(c instanceof JToggleButton)) {
/* 655 */             ((AbstractButton)c).setBorderPainted(false);
/*     */           }
/*     */         } else {
/* 658 */           ((AbstractButton)c).setMargin(new Insets(0, 0, 0, 0));
/*     */         } 
/* 660 */         ((AbstractButton)c).setFont(new Font("Arial", 0, 10));
/*     */       } 
/*     */     } 
/* 663 */     this.backgroundTypeLabel.setFont(new Font("Arial", 0, 10));
/* 664 */     this.backgroundTypeComboBox.setFont(new Font("Arial", 0, 10));
/*     */ 
/*     */     
/* 667 */     this.mainFrame.getContentPane().add(this.toolBarPanel, gridBagConstraints);
/*     */   }
/*     */   
/*     */   private void prepareMenuBar() {
/* 671 */     this.menuBar = new JMenuBar();
/* 672 */     this.menuBar.setLayout(new GridBagLayout());
/* 673 */     GridBagConstraints constraints = new GridBagConstraints();
/* 674 */     constraints.fill = 3;
/* 675 */     constraints.anchor = 17;
/* 676 */     constraints.weightx = 0.0D;
/* 677 */     JMenuItem menuItemFile = new JMenuItem("File");
/* 678 */     JMenuItem menuItemView = new JMenuItem("View");
/* 679 */     JMenuItem menuItemOptions = new JMenuItem("Options");
/* 680 */     JMenuItem menuItemAbout = new JMenuItem("About");
/* 681 */     this.menuBar.add(menuItemFile, constraints);
/* 682 */     this.menuBar.add(menuItemView, constraints);
/* 683 */     this.menuBar.add(menuItemOptions, constraints);
/* 684 */     this.menuBar.add(menuItemAbout, constraints);
/* 685 */     constraints.weightx = 1.0D;
/* 686 */     constraints.fill = 1;
/* 687 */     this.menuBar.add(Box.createHorizontalGlue(), constraints);
/* 688 */     menuItemFile.setAlignmentX(0.0F);
/* 689 */     menuItemView.setAlignmentX(0.0F);
/* 690 */     menuItemOptions.setAlignmentX(0.0F);
/* 691 */     menuItemAbout.setAlignmentX(0.0F);
/* 692 */     this.menuBar.add(Box.createHorizontalGlue());
/* 693 */     this.menuBar.setMaximumSize(new Dimension(this.menuBar.getWidth(), menuItemFile.getHeight()));
/* 694 */     menuItemFile.setMinimumSize(new Dimension(menuItemFile.getWidth(), this.menuBar.getHeight()));
/* 695 */     this.mainFrame.setJMenuBar(this.menuBar);
/*     */   }
/*     */   
/*     */   private void prepareFileDialogs() {
/* 699 */     this.openFile = prepareFileDialog("Open Level", 0, true);
/* 700 */     this.saveFile = prepareFileDialog("Save Level", 1, false, new FileNameExtensionFilter("Level name (*.lvl)", new String[] { "lvl" }));
/*     */     
/* 702 */     List<FileNameExtensionFilter> filters = new ArrayList<FileNameExtensionFilter>();
/* 703 */     FileNameExtensionFilter audioExtensionFilter = new FileNameExtensionFilter("Supported audio file (*.mp3; *.ogg)", new String[] { "mp3", "ogg" });
/* 704 */     filters.add(audioExtensionFilter);
/* 705 */     audioExtensionFilter = new FileNameExtensionFilter("Ogg audio file (*.ogg)", new String[] { "ogg" });
/* 706 */     filters.add(audioExtensionFilter);
/* 707 */     audioExtensionFilter = new FileNameExtensionFilter("MP3 audio file (*.mp3)", new String[] { "mp3" });
/* 708 */     filters.add(audioExtensionFilter);
/* 709 */     this.openAudioFile = prepareFileDialog("Open Audio File", 0, false, filters);
/*     */   }
/*     */   
/*     */   private void prepareMainFrame() {
/*     */     try {
/* 714 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/* 715 */     } catch (ClassNotFoundException e) {
/* 716 */       e.printStackTrace();
/* 717 */     } catch (InstantiationException e) {
/* 718 */       e.printStackTrace();
/* 719 */     } catch (IllegalAccessException e) {
/* 720 */       e.printStackTrace();
/* 721 */     } catch (UnsupportedLookAndFeelException e) {
/* 722 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 725 */     this.mainFrame = new JFrame("The Impossible Game Editor");
/* 726 */     this.mainFrame.setDefaultCloseOperation(3);
/* 727 */     this.mainFrame.setSize(1280, 720);
/* 728 */     this.mainFrame.setLocationRelativeTo((Component)null);
/* 729 */     this.mainFrame.setMinimumSize(new Dimension(640, 480));
/*     */   }
/*     */ 
/*     */   
/*     */   private FragmentReader readImageList(String imageList, boolean isResource) throws ParserConfigurationException, SAXException, IOException {
/*     */     InputStream fileInputStream;
/* 735 */     if (!isResource) {
/* 736 */       fileInputStream = new FileInputStream(convertToFileURL(imageList));
/*     */     } else {
/* 738 */       fileInputStream = getClass().getResourceAsStream(imageList);
/*     */     } 
/* 740 */     SAXParserFactory factory = SAXParserFactory.newInstance();
/* 741 */     SAXParser saxParser = factory.newSAXParser();
/* 742 */     XMLReader xmlParser = saxParser.getXMLReader();
/* 743 */     FragmentReader fragmentParser = new FragmentReader(this.includedResourcesDir, isResource);
/* 744 */     xmlParser.setContentHandler((ContentHandler)fragmentParser);
/* 745 */     xmlParser.parse(new InputSource(fileInputStream));
/* 746 */     return fragmentParser;
/*     */   }
/*     */   
/*     */   private static String convertToFileURL(String filename) {
/* 750 */     String path = (new File(filename)).getAbsolutePath();
/* 751 */     if (File.separatorChar != '/') {
/* 752 */       path = path.replace(File.separatorChar, '/');
/*     */     }
/*     */     
/* 755 */     if (!path.startsWith("/")) {
/* 756 */       path = "/" + path;
/*     */     }
/* 758 */     return "file:" + path;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 762 */     boolean debugLaunch = (args.length > 0 && args[0].compareTo("-d") == 0);
/* 763 */     TIGEditor tigEditor = new TIGEditor(debugLaunch);
/* 764 */     tigEditor.showForm();
/*     */   }
/*     */   
/*     */   private void showForm() {
/* 768 */     this.mainFrame.pack();
/* 769 */     this.mainFrame.setSize(1280, 720);
/* 770 */     this.mainFrame.setVisible(true);
/*     */     
/* 772 */     while (this.mainFrame.isVisible()) {
/* 773 */       this.levelViewSettings.tickAudio(50);
/*     */       try {
/* 775 */         Thread.sleep(50L);
/* 776 */       } catch (InterruptedException e) {
/* 777 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void copyFile(File source, File dest) throws IOException {
/* 792 */     if (source.equals(dest)) {
/*     */       return;
/*     */     }
/* 795 */     FileChannel sourceChannel = null;
/* 796 */     FileChannel destChannel = null;
/*     */     try {
/* 798 */       sourceChannel = (new FileInputStream(source)).getChannel();
/* 799 */       destChannel = (new FileOutputStream(dest)).getChannel();
/* 800 */       destChannel.transferFrom(sourceChannel, 0L, sourceChannel.size());
/*     */     } finally {
/* 802 */       sourceChannel.close();
/* 803 */       destChannel.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean deleteDirectory(File file) {
/* 808 */     if (!file.exists()) {
/* 809 */       return false;
/*     */     }
/*     */     
/* 812 */     boolean result = true;
/* 813 */     if (file.isDirectory()) {
/* 814 */       for (File f : file.listFiles()) {
/* 815 */         if (!deleteDirectory(f)) {
/* 816 */           result = false;
/*     */         }
/*     */       } 
/*     */     }
/* 820 */     if (!file.delete()) {
/* 821 */       result = false;
/*     */     }
/*     */     
/* 824 */     return result;
/*     */   }
/*     */   
/*     */   private boolean isWindows() {
/* 828 */     return System.getProperty("os.name").startsWith("Windows");
/*     */   }
/*     */   
/*     */   private boolean isMac() {
/* 832 */     return System.getProperty("os.name").startsWith("Mac");
/*     */   }
/*     */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\gui\TIGEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */