package com.grip.gui;

import com.grip.FragmentReader;
import com.grip.elements.LevelObject;
import com.grip.gui.components.LevelNavigator;
import com.grip.gui.components.LevelNavigatorScrollBar;
import com.grip.gui.library.GraphicsRepository;
import com.grip.level.LevelDefinition;
import com.grip.level.LevelViewSettings;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class TIGEditor {
   private final int audioTickLength = 50;
   private JFrame mainFrame;
   private LevelNavigator levelNavigator;
   private LevelViewSettings levelViewSettings;
   private JMenuBar menuBar;
   public JButton newFileButton;
   public JButton openFileButton;
   public JButton saveFileButton;
   public JToggleButton gridToggleButton;
   public JToggleButton snapToGridButton;
   public JToggleButton blockButton;
   public JToggleButton spikeButton;
   public JToggleButton pitButton;
   public JToggleButton deleteObjectButton;
   public JButton rewindMusicButton;
   public JButton playFromMarkButton;
   public JToggleButton placeMusicMarkButton;
   public JToggleButton placeLevelEndButton;
   public JToggleButton placeBackgroundButton;
   public JButton deleteMusicMarkButton;
   public JToggleButton playPauseButton;
   public Box backgroundTypePanel;
   public JComboBox backgroundTypeComboBox;
   public JLabel backgroundTypeLabel;
   public JButton openAudioButton;
   public JButton launchInGameButton;
   private LevelNavigatorScrollBar levelNavigatorScrollBar;
   private GraphicsRepository graphicsRepository;
   private LevelDefinition levelDefinition;
   private JToolBar toolBarPanel;
   private JFileChooser openFile;
   private JFileChooser saveFile;
   private JFileChooser openAudioFile;
   private boolean isGridEnabledInitially = true;
   public final ButtonGroup navigatorClickTypeButtonGroup = new ButtonGroup();
   private boolean isSnapToGridEnabledInitially = true;
   private String includedResourcesDir = "/base/";
   private File levelFile;
   private File audioFile;
   private boolean debugLaunch;
   private File levelFileDir = null;
   private boolean firstSaveDone = false;

   public TIGEditor(boolean debugLaunch) {
      this.debugLaunch = debugLaunch;
      if (debugLaunch) {
         System.out.println("Launching in debug mode");
      }

      this.createUIComponents();
   }

   private JFileChooser prepareFileDialog(String dialogTitle, int dialogType, boolean directoriesOnly, FileNameExtensionFilter fileNameExtensionFilter) {
      List filters = new ArrayList();
      filters.add(fileNameExtensionFilter);
      return this.prepareFileDialog(dialogTitle, dialogType, directoriesOnly, (List)filters);
   }

   private JFileChooser prepareFileDialog(String dialogTitle, int dialogType, boolean directoriesOnly, List fileNameExtensionFilters) {
      JFileChooser fileDialog = new JFileChooser(System.getProperty("user.dir"));
      fileDialog.setDialogTitle(dialogTitle);
      fileDialog.setDialogType(dialogType);
      if (directoriesOnly) {
         fileDialog.setFileSelectionMode(1);
      } else if (fileNameExtensionFilters != null) {
         fileDialog.removeChoosableFileFilter(fileDialog.getAcceptAllFileFilter());
         Iterator i$ = fileNameExtensionFilters.iterator();

         while(i$.hasNext()) {
            FileNameExtensionFilter filter = (FileNameExtensionFilter)i$.next();
            fileDialog.addChoosableFileFilter(filter);
         }

         fileDialog.setFileFilter((FileFilter)fileNameExtensionFilters.get(0));
      }

      return fileDialog;
   }

   private JFileChooser prepareFileDialog(String dialogTitle, int dialogType, boolean directoriesOnly) {
      JFileChooser fileDialog = new JFileChooser(System.getProperty("user.dir"));
      fileDialog.setDialogTitle(dialogTitle);
      fileDialog.setDialogType(dialogType);
      if (directoriesOnly) {
         fileDialog.setFileSelectionMode(1);
      }

      return fileDialog;
   }

   private void createUIComponents() {
      try {
         this.prepareGraphics();
         this.prepareMainFrame();
         this.prepareFileDialogs();
         this.mainFrame.setLayout(new GridBagLayout());
         this.prepareButtons();
         GridBagConstraints gridBagConstraints = new GridBagConstraints();
         this.prepareHeader(gridBagConstraints);
         this.prepareToolBar(gridBagConstraints);
         this.prepareCustomPanels(gridBagConstraints);
         this.setDefaultSelectedObjectType();
         this.attachButtonActionHandlers();
         this.newFileButton.getActionListeners()[0].actionPerformed((ActionEvent)null);
      } catch (IOException var2) {
         IOException e = var2;
         e.printStackTrace();
      } catch (ParserConfigurationException var3) {
         ParserConfigurationException e = var3;
         e.printStackTrace();
      } catch (SAXException var4) {
         SAXException e = var4;
         e.printStackTrace();
      }

   }

   private void attachButtonActionHandlers() {
      this.newFileButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TIGEditor.this.levelDefinition.newLevel();
            TIGEditor.this.levelFile = null;
            TIGEditor.this.launchInGameButton.setEnabled(true);
            TIGEditor.this.firstSaveDone = false;
            TIGEditor.this.levelFileDir = null;
         }
      });
      this.openFileButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (TIGEditor.this.openFile.showDialog(TIGEditor.this.mainFrame, "Select directory") == 0) {
               try {
                  TIGEditor.this.levelFile = new File(TIGEditor.this.openFile.getSelectedFile().getAbsolutePath() + "/level.dat");
                  boolean result = TIGEditor.this.levelDefinition.openFile(TIGEditor.this.levelFile.getAbsolutePath());
                  if (!result) {
                     JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Failed to open level at\n" + TIGEditor.this.levelFile.getAbsolutePath());
                     TIGEditor.this.levelFile = null;
                  } else {
                     TIGEditor.this.launchInGameButton.setEnabled(true);
                  }

                  String[] arr$ = new String[]{".ogg", ".mp3"};
                  int len$ = arr$.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     String extension = arr$[i$];
                     TIGEditor.this.audioFile = new File(TIGEditor.this.openFile.getSelectedFile().getAbsolutePath() + "/music" + extension);
                     if (TIGEditor.this.audioFile.exists()) {
                        result = TIGEditor.this.levelViewSettings.setAudioFile(TIGEditor.this.audioFile.getAbsolutePath());
                        if (!result) {
                           JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Failed audio file at\n" + TIGEditor.this.audioFile.getAbsolutePath());
                           TIGEditor.this.audioFile = null;
                        } else {
                           TIGEditor.this.playPauseButton.setEnabled(true);
                           TIGEditor.this.playFromMarkButton.setEnabled(true);
                           TIGEditor.this.rewindMusicButton.setEnabled(true);
                        }
                        break;
                     }
                  }

                  if (!TIGEditor.this.audioFile.exists()) {
                     TIGEditor.this.audioFile = null;
                  } else {
                     TIGEditor.this.playPauseButton.setSelected(false);
                  }

                  TIGEditor.this.levelFileDir = TIGEditor.this.openFile.getSelectedFile();
                  TIGEditor.this.firstSaveDone = true;
               } catch (IOException var7) {
                  IOException e1 = var7;
                  JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Failed opening level.\n\n" + e1.toString());
               }

            }
         }
      });
      this.openAudioButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (TIGEditor.this.openAudioFile.showDialog(TIGEditor.this.mainFrame, "Select file") == 0) {
               TIGEditor.this.audioFile = TIGEditor.this.openAudioFile.getSelectedFile();
               boolean result = TIGEditor.this.levelViewSettings.setAudioFile(TIGEditor.this.audioFile.getAbsolutePath());
               if (!result) {
                  JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Failed audio file at\n" + TIGEditor.this.audioFile.getAbsolutePath());
                  TIGEditor.this.audioFile = null;
               } else {
                  TIGEditor.this.playPauseButton.setEnabled(true);
                  TIGEditor.this.playFromMarkButton.setEnabled(true);
                  TIGEditor.this.rewindMusicButton.setEnabled(true);
               }

            }
         }
      });
      this.saveFileButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               String saveDir;
               if (TIGEditor.this.firstSaveDone) {
                  saveDir = TIGEditor.this.levelFileDir.getAbsolutePath();
               } else {
                  if (TIGEditor.this.saveFile.showDialog(TIGEditor.this.mainFrame, "Save level") != 0) {
                     return;
                  }

                  saveDir = TIGEditor.this.saveFile.getSelectedFile().getAbsolutePath();
               }

               TIGEditor.this.saveLevel(saveDir);
               TIGEditor.this.firstSaveDone = true;
            } catch (IOException var3) {
               IOException e1 = var3;
               JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Level save failed!\n\n" + e1.toString());
            }

         }
      });
      this.gridToggleButton.setSelected(this.isGridEnabledInitially);
      this.gridToggleButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TIGEditor.this.levelNavigator.setDrawBlockSizedGrid(TIGEditor.this.gridToggleButton.getModel().isSelected());
         }
      });
      this.snapToGridButton.setSelected(this.isSnapToGridEnabledInitially);
      this.snapToGridButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TIGEditor.this.levelViewSettings.setSnapToGrid(TIGEditor.this.snapToGridButton.getModel().isSelected());
         }
      });
      ActionListener objectTypeListener = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(TIGEditor.this.blockButton)) {
               TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.BLOCK);
            } else if (e.getSource().equals(TIGEditor.this.spikeButton)) {
               TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.SPIKE);
            } else if (e.getSource().equals(TIGEditor.this.pitButton)) {
               TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.PIT);
            } else if (e.getSource().equals(TIGEditor.this.deleteObjectButton)) {
               TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.DELETE);
            } else if (e.getSource().equals(TIGEditor.this.placeMusicMarkButton)) {
               TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.MARK);
            } else if (e.getSource().equals(TIGEditor.this.placeLevelEndButton)) {
               TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.LEVELEND);
            } else {
               if (!e.getSource().equals(TIGEditor.this.placeBackgroundButton)) {
                  throw new RuntimeException("Unrecognized toolbar button selected");
               }

               TIGEditor.this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.BACKGROUND);
            }

         }
      };
      this.blockButton.addActionListener(objectTypeListener);
      this.spikeButton.addActionListener(objectTypeListener);
      this.pitButton.addActionListener(objectTypeListener);
      this.deleteObjectButton.addActionListener(objectTypeListener);
      this.placeMusicMarkButton.addActionListener(objectTypeListener);
      this.placeLevelEndButton.addActionListener(objectTypeListener);
      this.placeBackgroundButton.addActionListener(objectTypeListener);
      this.backgroundTypeComboBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TIGEditor.this.levelViewSettings.setBackgroundType(TIGEditor.this.backgroundTypeComboBox.getSelectedIndex());
         }
      });
      this.playPauseButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TIGEditor.this.levelViewSettings.doPlayPause();
         }
      });
      this.rewindMusicButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TIGEditor.this.levelViewSettings.doRewind();
            TIGEditor.this.playPauseButton.setSelected(false);
         }
      });
      this.playFromMarkButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TIGEditor.this.levelViewSettings.doPlayFromMark();
            TIGEditor.this.playPauseButton.setSelected(true);
         }
      });
      this.deleteMusicMarkButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TIGEditor.this.levelViewSettings.deleteMark();
         }
      });
      this.launchInGameButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               if (TIGEditor.this.playPauseButton.isSelected()) {
                  TIGEditor.this.levelViewSettings.doPlayPause();
               }

               String currentDir = System.getProperty("user.dir") + "/";
               String cacheDir = "editorcache/";
               String levelName = "test";
               File targetDir = new File(currentDir + cacheDir + levelName);
               if (TIGEditor.this.isMac()) {
                  targetDir = new File(currentDir + "TheImpossibleGame.app/Contents/Resources/" + cacheDir + levelName);
               }

               if (targetDir.exists() && !TIGEditor.this.deleteDirectory(targetDir)) {
                  throw new IOException("Failed to delete temporary directory:\n" + targetDir.getCanonicalPath());
               }

               if (!targetDir.mkdirs()) {
                  throw new IOException("Failed to create directories to save the level:\n" + targetDir.getCanonicalPath());
               }

               System.out.println("Created dir: " + targetDir.getCanonicalPath());
               String result = TIGEditor.this.saveFile(targetDir.getCanonicalPath() + "/level.dat", false);
               if (result == null) {
                  throw new IOException("Failed to save level!");
               }

               File commandx;
               if (TIGEditor.this.audioFile != null) {
                  commandx = TIGEditor.this.getAudioFileNameForSaving(targetDir.getCanonicalPath(), TIGEditor.this.audioFile.getName());
                  TIGEditor.this.copyFile(TIGEditor.this.audioFile, commandx);
               }

               commandx = null;
               if (TIGEditor.this.isWindows()) {
                  String[] var10000 = new String[]{"ImpossibleGame.exe", "log", "level", cacheDir + levelName};
               }

               String[] command;
               if (TIGEditor.this.isMac()) {
                  command = new String[]{"TheImpossibleGame.app/Contents/MacOS/TheImpossibleGame", "log", "level", cacheDir + levelName};
               } else if (TIGEditor.this.debugLaunch) {
                  command = new String[]{"run.sh", "./ImpossibleGame", "log", "level", cacheDir + levelName};
               } else {
                  command = new String[]{"./ImpossibleGame", "log", "level", cacheDir + levelName};
               }

               ArrayList enviroVars = new ArrayList();
               boolean foundLibPath = false;
               Map env = System.getenv();
               Iterator i$ = env.entrySet().iterator();

               while(i$.hasNext()) {
                  Map.Entry entry = (Map.Entry)i$.next();
                  if (((String)entry.getKey()).compareTo("LD_LIBRARY_PATH") == 0) {
                     foundLibPath = true;
                     enviroVars.add("LD_LIBRARY_PATH=.:" + (String)entry.getValue());
                     System.out.println("Setting " + (String)enviroVars.get(enviroVars.size() - 1));
                  } else {
                     enviroVars.add((String)entry.getKey() + "=" + (String)entry.getValue());
                  }
               }

               if (!foundLibPath) {
                  enviroVars.add("LD_LIBRARY_PATH=.");
                  System.out.println("Setting " + (String)enviroVars.get(enviroVars.size() - 1));
               }

               System.out.println("Running " + command[0] + " for " + cacheDir + levelName);
               System.out.println("Curdir: " + currentDir);
               Process p = Runtime.getRuntime().exec(command, (String[])enviroVars.toArray(new String[0]), new File(currentDir));
               BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));

               String line;
               while((line = in.readLine()) != null) {
                  System.out.println(line);
               }

               in.close();
               int resultCode = p.waitFor();
               System.out.println("Process exited with code " + resultCode);
            } catch (Exception var15) {
               Exception e1 = var15;
               JOptionPane.showMessageDialog(TIGEditor.this.mainFrame, "Game launch failed!\n\n" + e1.toString());
            }

         }
      });
   }

   public void saveLevel(String saveDir) throws IOException {
      if (!saveDir.endsWith(".lvl")) {
         saveDir = saveDir + ".lvl";
      }

      File newLevelFile = new File(saveDir + "/level.dat");
      File levelDir = newLevelFile.getParentFile();
      if (!levelDir.exists()) {
         levelDir.mkdirs();
      }

      File newAudioFile = null;
      if (this.audioFile != null) {
         String audioFileName = this.audioFile.getName();
         newAudioFile = this.getAudioFileNameForSaving(saveDir, audioFileName);
         this.copyFile(this.audioFile, newAudioFile);
      }

      if (this.saveFile(newLevelFile.getAbsolutePath(), true) != null) {
         this.levelFile = newLevelFile;
         this.levelFileDir = this.levelFile.getParentFile();
         this.audioFile = newAudioFile;
      }

   }

   public File getAudioFileNameForSaving(String saveDir, String audioFileName) {
      String extension = audioFileName.substring(audioFileName.lastIndexOf(46), audioFileName.length());
      File newAudioFile = new File(saveDir + "/music" + extension);
      return newAudioFile;
   }

   public String saveFile(String path, boolean showResultMessage) {
      try {
         if (!path.endsWith(".dat")) {
            path = path + ".dat";
         }

         boolean result = this.levelDefinition.saveFile(path);
         if (result) {
            if (showResultMessage) {
               JOptionPane.showMessageDialog(this.mainFrame, "Level saved successfully.");
            }

            return path;
         } else {
            if (showResultMessage) {
               JOptionPane.showMessageDialog(this.mainFrame, "Failed saving level.");
            }

            return null;
         }
      } catch (IOException var4) {
         JOptionPane.showMessageDialog(this.mainFrame, "Failed saving level.");
         return null;
      }
   }

   private void prepareGraphics() throws ParserConfigurationException, SAXException, IOException {
      FragmentReader fragmentReader = this.readImageList(this.includedResourcesDir + "game1.xml", true);
      this.graphicsRepository = new GraphicsRepository(fragmentReader.getFragments());
      fragmentReader = this.readImageList(this.includedResourcesDir + "gui.xml", true);
      this.graphicsRepository.addFragments(fragmentReader.getFragments());
      fragmentReader = this.readImageList(this.includedResourcesDir + "editorheader.xml", true);
      this.graphicsRepository.addFragments(fragmentReader.getFragments());
   }

   private void prepareCustomPanels(GridBagConstraints gridBagConstraints) throws IOException {
      this.levelDefinition = new LevelDefinition();
      this.levelNavigator = new LevelNavigator(this, this.graphicsRepository, this.mainFrame.getWidth(), this.mainFrame.getHeight(), this.isGridEnabledInitially);
      this.levelNavigatorScrollBar = new LevelNavigatorScrollBar();
      this.levelViewSettings = new LevelViewSettings(this.graphicsRepository, this.levelDefinition, this.levelNavigator, this.levelNavigatorScrollBar);
      this.levelNavigatorScrollBar.setReferences(this.mainFrame, this.levelViewSettings);
      gridBagConstraints.gridy = 2;
      gridBagConstraints.weighty = 1.0;
      this.mainFrame.getContentPane().add(this.levelNavigator, gridBagConstraints);
      gridBagConstraints.gridy = 3;
      gridBagConstraints.weighty = 0.0;
      gridBagConstraints.ipady = 1;
      this.mainFrame.getContentPane().add(this.levelNavigatorScrollBar, gridBagConstraints);
   }

   private void prepareButtons() {
      this.newFileButton = new JButton("New", this.graphicsRepository.getImageIcon("btnNew"));
      this.newFileButton.setToolTipText("CTRL/CMD + N");
      this.openFileButton = new JButton("Open", this.graphicsRepository.getImageIcon("btnOpen"));
      this.openFileButton.setToolTipText("CTRL/CMD + O");
      this.saveFileButton = new JButton("Save", this.graphicsRepository.getImageIcon("btnSave"));
      this.saveFileButton.setToolTipText("CTRL/CMD + S");
      this.gridToggleButton = new JToggleButton("Toggle grid", this.graphicsRepository.getImageIcon("btnToggleGrid"));
      this.gridToggleButton.setSelected(this.isGridEnabledInitially);
      this.snapToGridButton = new JToggleButton("Snap to grid", this.graphicsRepository.getImageIcon("btnSnapToGrid"));
      this.blockButton = new JToggleButton("Block", this.graphicsRepository.getImageIcon("btnBlock"));
      this.blockButton.setToolTipText("ALFANUM 1");
      this.navigatorClickTypeButtonGroup.add(this.blockButton);
      this.spikeButton = new JToggleButton("Spike", this.graphicsRepository.getImageIcon("btnSpike"));
      this.spikeButton.setToolTipText("ALFANUM 2");
      this.navigatorClickTypeButtonGroup.add(this.spikeButton);
      this.pitButton = new JToggleButton("Pit", this.graphicsRepository.getImageIcon("btnPit"));
      this.pitButton.setToolTipText("ALFANUM 3");
      this.navigatorClickTypeButtonGroup.add(this.pitButton);
      this.deleteObjectButton = new JToggleButton("Delete object", this.graphicsRepository.getImageIcon("btnDelete"));
      this.deleteObjectButton.setToolTipText("ALFANUM 4");
      this.navigatorClickTypeButtonGroup.add(this.deleteObjectButton);
      this.placeMusicMarkButton = new JToggleButton("Place mark", this.graphicsRepository.getImageIcon("btnMark"));
      this.placeMusicMarkButton.setToolTipText("ALFANUM 5");
      this.navigatorClickTypeButtonGroup.add(this.placeMusicMarkButton);
      this.placeLevelEndButton = new JToggleButton("Level end", this.graphicsRepository.getImageIcon("btnLevelEnd"));
      this.placeLevelEndButton.setToolTipText("ALFANUM 6");
      this.navigatorClickTypeButtonGroup.add(this.placeLevelEndButton);
      this.placeBackgroundButton = new JToggleButton("Place background", this.graphicsRepository.getImageIcon("btnBackground"));
      this.placeBackgroundButton.setToolTipText("ALFANUM 7");
      this.navigatorClickTypeButtonGroup.add(this.placeBackgroundButton);
      int backgroundTypeWidth = 65;
      if (this.isMac()) {
         backgroundTypeWidth = 70;
      }

      this.backgroundTypePanel = Box.createVerticalBox();
      this.backgroundTypePanel.setMaximumSize(new Dimension(backgroundTypeWidth, 50));
      this.backgroundTypePanel.setMinimumSize(new Dimension(backgroundTypeWidth, 50));
      this.backgroundTypePanel.setPreferredSize(new Dimension(backgroundTypeWidth, 50));
      this.backgroundTypePanel.setFocusable(false);
      String[] backgroundTypes = new String[]{"blue", "yellow", "green", "violet", "pink", "black"};
      this.backgroundTypeComboBox = new JComboBox(backgroundTypes);
      this.backgroundTypeComboBox.setToolTipText("KEY B");
      this.backgroundTypeComboBox.setFocusable(false);
      this.backgroundTypeComboBox.setPreferredSize(new Dimension(35, 37));
      this.backgroundTypePanel.add(this.backgroundTypeComboBox);
      JPanel panel = new JPanel(new BorderLayout());
      this.backgroundTypeLabel = new JLabel("Background:");
      panel.add(this.backgroundTypeLabel);
      this.backgroundTypePanel.add(panel);
      this.rewindMusicButton = new JButton("Rewind", this.graphicsRepository.getImageIcon("btnRewind"));
      this.rewindMusicButton.setToolTipText("KEY I");
      this.playPauseButton = new JToggleButton("Play", this.graphicsRepository.getImageIcon("btnPlay"));
      this.playPauseButton.setToolTipText("KEY P");
      this.playFromMarkButton = new JButton("Play from mark", this.graphicsRepository.getImageIcon("btnPlayFromMark"));
      this.playFromMarkButton.setToolTipText("KEY O");
      this.deleteMusicMarkButton = new JButton("Delete mark", this.graphicsRepository.getImageIcon("btnDeleteMark"));
      this.openAudioButton = new JButton("Open audio", this.graphicsRepository.getImageIcon("btnOpenAudio"));
      this.openAudioButton.setToolTipText("CTRL/CMD + A");
      this.playPauseButton.setEnabled(false);
      this.playFromMarkButton.setEnabled(false);
      this.rewindMusicButton.setEnabled(false);
      this.launchInGameButton = new JButton("Test", this.graphicsRepository.getImageIcon("btnLaunchGame"));
      this.launchInGameButton.setToolTipText("F5");
      this.launchInGameButton.setEnabled(false);
   }

   private void setDefaultSelectedObjectType() {
      this.navigatorClickTypeButtonGroup.setSelected(this.blockButton.getModel(), true);
      this.levelViewSettings.setSelectedLevelObjectType(LevelObject.ObjectType.BLOCK);
   }

   private void prepareHeader(GridBagConstraints gridBagConstraints) {
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 0.0;
      gridBagConstraints.weighty = 0.0;
      gridBagConstraints.ipady = 0;
      gridBagConstraints.anchor = 23;
      JLabel header = new JLabel(this.graphicsRepository.getImageIcon("editorheader"), 2);
      this.mainFrame.getContentPane().add(header, gridBagConstraints);
   }

   private void prepareToolBar(GridBagConstraints gridBagConstraints) {
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 10.0;
      gridBagConstraints.weighty = 0.0;
      gridBagConstraints.ipady = 0;
      Dimension separatorSize = new Dimension(20, 60);
      this.toolBarPanel = new JToolBar("Toolbar");
      this.toolBarPanel.setFloatable(false);
      this.toolBarPanel.setFocusable(false);
      this.toolBarPanel.setRequestFocusEnabled(false);
      this.toolBarPanel.setFocusTraversalKeysEnabled(false);
      this.toolBarPanel.add(this.newFileButton);
      this.toolBarPanel.add(this.openFileButton);
      this.toolBarPanel.add(this.openAudioButton);
      this.toolBarPanel.add(this.saveFileButton);
      this.toolBarPanel.add(this.launchInGameButton);
      this.toolBarPanel.addSeparator(separatorSize);
      this.toolBarPanel.add(this.gridToggleButton);
      this.toolBarPanel.add(this.snapToGridButton);
      this.toolBarPanel.addSeparator(separatorSize);
      this.toolBarPanel.add(this.blockButton);
      this.toolBarPanel.add(this.spikeButton);
      this.toolBarPanel.add(this.pitButton);
      this.toolBarPanel.add(this.deleteObjectButton);
      this.toolBarPanel.add(this.placeMusicMarkButton);
      this.toolBarPanel.add(this.placeLevelEndButton);
      this.toolBarPanel.add(this.placeBackgroundButton);
      this.toolBarPanel.addSeparator(separatorSize);
      this.toolBarPanel.add(this.deleteMusicMarkButton);
      this.toolBarPanel.add(this.backgroundTypePanel);
      this.toolBarPanel.addSeparator(separatorSize);
      this.toolBarPanel.add(this.rewindMusicButton);
      this.toolBarPanel.add(this.playFromMarkButton);
      this.toolBarPanel.add(this.playPauseButton);
      Component[] arr$ = this.toolBarPanel.getComponents();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Component c = arr$[i$];
         c.setFocusable(false);
         if (c instanceof AbstractButton) {
            ((AbstractButton)c).setVerticalTextPosition(3);
            ((AbstractButton)c).setHorizontalTextPosition(0);
            if (this.isWindows()) {
               ((AbstractButton)c).setMargin(new Insets(0, 5, 0, 5));
            } else if (this.isMac()) {
               ((AbstractButton)c).setMargin(new Insets(0, 3, 0, 3));
               if (!(c instanceof JToggleButton)) {
                  ((AbstractButton)c).setBorderPainted(false);
               }
            } else {
               ((AbstractButton)c).setMargin(new Insets(0, 0, 0, 0));
            }

            ((AbstractButton)c).setFont(new Font("Arial", 0, 10));
         }
      }

      this.backgroundTypeLabel.setFont(new Font("Arial", 0, 10));
      this.backgroundTypeComboBox.setFont(new Font("Arial", 0, 10));
      this.mainFrame.getContentPane().add(this.toolBarPanel, gridBagConstraints);
   }

   private void prepareMenuBar() {
      this.menuBar = new JMenuBar();
      this.menuBar.setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.fill = 3;
      constraints.anchor = 17;
      constraints.weightx = 0.0;
      JMenuItem menuItemFile = new JMenuItem("File");
      JMenuItem menuItemView = new JMenuItem("View");
      JMenuItem menuItemOptions = new JMenuItem("Options");
      JMenuItem menuItemAbout = new JMenuItem("About");
      this.menuBar.add(menuItemFile, constraints);
      this.menuBar.add(menuItemView, constraints);
      this.menuBar.add(menuItemOptions, constraints);
      this.menuBar.add(menuItemAbout, constraints);
      constraints.weightx = 1.0;
      constraints.fill = 1;
      this.menuBar.add(Box.createHorizontalGlue(), constraints);
      menuItemFile.setAlignmentX(0.0F);
      menuItemView.setAlignmentX(0.0F);
      menuItemOptions.setAlignmentX(0.0F);
      menuItemAbout.setAlignmentX(0.0F);
      this.menuBar.add(Box.createHorizontalGlue());
      this.menuBar.setMaximumSize(new Dimension(this.menuBar.getWidth(), menuItemFile.getHeight()));
      menuItemFile.setMinimumSize(new Dimension(menuItemFile.getWidth(), this.menuBar.getHeight()));
      this.mainFrame.setJMenuBar(this.menuBar);
   }

   private void prepareFileDialogs() {
      this.openFile = this.prepareFileDialog("Open Level", 0, true);
      this.saveFile = this.prepareFileDialog("Save Level", 1, false, (FileNameExtensionFilter)(new FileNameExtensionFilter("Level name (*.lvl)", new String[]{"lvl"})));
      List filters = new ArrayList();
      FileNameExtensionFilter audioExtensionFilter = new FileNameExtensionFilter("Supported audio file (*.mp3; *.ogg)", new String[]{"mp3", "ogg"});
      filters.add(audioExtensionFilter);
      audioExtensionFilter = new FileNameExtensionFilter("Ogg audio file (*.ogg)", new String[]{"ogg"});
      filters.add(audioExtensionFilter);
      audioExtensionFilter = new FileNameExtensionFilter("MP3 audio file (*.mp3)", new String[]{"mp3"});
      filters.add(audioExtensionFilter);
      this.openAudioFile = this.prepareFileDialog("Open Audio File", 0, false, (List)filters);
   }

   private void prepareMainFrame() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (ClassNotFoundException var2) {
         ClassNotFoundException e = var2;
         e.printStackTrace();
      } catch (InstantiationException var3) {
         InstantiationException e = var3;
         e.printStackTrace();
      } catch (IllegalAccessException var4) {
         IllegalAccessException e = var4;
         e.printStackTrace();
      } catch (UnsupportedLookAndFeelException var5) {
         UnsupportedLookAndFeelException e = var5;
         e.printStackTrace();
      }

      this.mainFrame = new JFrame("The Impossible Game Editor");
      this.mainFrame.setDefaultCloseOperation(3);
      this.mainFrame.setSize(1280, 720);
      this.mainFrame.setLocationRelativeTo((Component)null);
      this.mainFrame.setMinimumSize(new Dimension(640, 480));
   }

   private FragmentReader readImageList(String imageList, boolean isResource) throws ParserConfigurationException, SAXException, IOException {
      Object fileInputStream;
      if (!isResource) {
         fileInputStream = new FileInputStream(convertToFileURL(imageList));
      } else {
         fileInputStream = this.getClass().getResourceAsStream(imageList);
      }

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      XMLReader xmlParser = saxParser.getXMLReader();
      FragmentReader fragmentParser = new FragmentReader(this.includedResourcesDir, isResource);
      xmlParser.setContentHandler(fragmentParser);
      xmlParser.parse(new InputSource((InputStream)fileInputStream));
      return fragmentParser;
   }

   private static String convertToFileURL(String filename) {
      String path = (new File(filename)).getAbsolutePath();
      if (File.separatorChar != '/') {
         path = path.replace(File.separatorChar, '/');
      }

      if (!path.startsWith("/")) {
         path = "/" + path;
      }

      return "file:" + path;
   }

   public static void main(String[] args) {
      boolean debugLaunch = args.length > 0 && args[0].compareTo("-d") == 0;
      TIGEditor tigEditor = new TIGEditor(debugLaunch);
      tigEditor.showForm();
   }

   private void showForm() {
      this.mainFrame.pack();
      this.mainFrame.setSize(1280, 720);
      this.mainFrame.setVisible(true);

      while(this.mainFrame.isVisible()) {
         this.levelViewSettings.tickAudio(50);

         try {
            Thread.sleep(50L);
         } catch (InterruptedException var2) {
            InterruptedException e = var2;
            e.printStackTrace();
         }
      }

   }

   private void copyFile(File source, File dest) throws IOException {
      if (!source.equals(dest)) {
         FileChannel sourceChannel = null;
         FileChannel destChannel = null;

         try {
            sourceChannel = (new FileInputStream(source)).getChannel();
            destChannel = (new FileOutputStream(dest)).getChannel();
            destChannel.transferFrom(sourceChannel, 0L, sourceChannel.size());
         } finally {
            sourceChannel.close();
            destChannel.close();
         }

      }
   }

   private boolean deleteDirectory(File file) {
      if (!file.exists()) {
         return false;
      } else {
         boolean result = true;
         if (file.isDirectory()) {
            File[] arr$ = file.listFiles();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               File f = arr$[i$];
               if (!this.deleteDirectory(f)) {
                  result = false;
               }
            }
         }

         if (!file.delete()) {
            result = false;
         }

         return result;
      }
   }

   private boolean isWindows() {
      return System.getProperty("os.name").startsWith("Windows");
   }

   private boolean isMac() {
      return System.getProperty("os.name").startsWith("Mac");
   }
}
