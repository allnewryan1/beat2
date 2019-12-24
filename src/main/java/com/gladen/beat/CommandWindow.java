package com.gladen.beat;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class CommandWindow extends JFrame {
    private static final long serialVersionUID = 550987585;

    /**
     * The text area which is used for displaying logging information.
     */
    private JTextArea textArea;
    public PrintStream out;

    public CommandWindow() {
        super("Beat - music unleashed!");
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem config = new JMenuItem("Config");
        ConfigWindowTest window = new ConfigWindowTest();
        config.addActionListener(e -> {
            window.setVisible(true);
        });
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            System.exit(0);
        });
        menu.add(config);
        menu.add(exit);
        menubar.add(menu);
        setJMenuBar(menubar);

        textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        out = new PrintStream(new CustomOutputStream(textArea));

        // create the status bar panel and shove it down the bottom of the frame
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        JLabel statusLabel = new JLabel("status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        GridBagConstraints statusConstraints = new GridBagConstraints();

        statusConstraints.gridx = 0;
        statusConstraints.gridy = 1;
        statusConstraints.gridwidth = GridBagConstraints.REMAINDER;
        statusConstraints.fill = GridBagConstraints.HORIZONTAL;
        statusConstraints.anchor = GridBagConstraints.SOUTH;
        statusConstraints.insets = new Insets(10, 10, 10, 10);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(new JScrollPane(textArea), constraints);
        //add(statusPanel, statusConstraints);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 320);
        setLocationRelativeTo(null);
    }

    public class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.append(String.valueOf((char)b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    public class ConfigWindowTest extends JFrame {
    private static final long serialVersionUID = 65654465;
   JLabel lbDc_l;
   JTextField tfTokenInput;
   JLabel lbYt_l;
   JTextField tfYtInput;
   JLabel lbCp_l;
   JTextField tfText2;
   JLabel lbSv_l;
   JSlider sdV_slide;
   JTextField tfV_text;
   JComboBox<String> cmbTc_list;
   JLabel lbTc_l;
   JLabel lbVc_l;
   JComboBox<String> cmbVc_list;
   JButton btCancel;
   JButton btSave;

   public ConfigWindowTest() {
      super("Config");

      GridBagLayout gbConfigWindow = new GridBagLayout();
      GridBagConstraints gbcConfigWindow = new GridBagConstraints();
      setLayout(gbConfigWindow);

      lbDc_l = new JLabel("Discord API Token: ");
      gbcConfigWindow.gridx = 0;
      gbcConfigWindow.gridy = 0;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 1;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(lbDc_l, gbcConfigWindow);
      add(lbDc_l);

      tfTokenInput = new JTextField();
      tfTokenInput.setText(Config.discord_token);
      gbcConfigWindow.gridx = 1;
      gbcConfigWindow.gridy = 0;
      gbcConfigWindow.gridwidth = 2;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(tfTokenInput, gbcConfigWindow);
      add(tfTokenInput);

      lbYt_l = new JLabel("YouTube API Token:");
      gbcConfigWindow.gridx = 0;
      gbcConfigWindow.gridy = 1;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 1;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(lbYt_l, gbcConfigWindow);
      add(lbYt_l);

      tfYtInput = new JTextField();
      tfYtInput.setText(Config.yt_api_key);
      gbcConfigWindow.gridx = 1;
      gbcConfigWindow.gridy = 1;
      gbcConfigWindow.gridwidth = 2;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(tfYtInput, gbcConfigWindow);
      add(tfYtInput);

      lbCp_l = new JLabel("Command Prefix:");
      gbcConfigWindow.gridx = 0;
      gbcConfigWindow.gridy = 2;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 1;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(lbCp_l, gbcConfigWindow);
      add(lbCp_l);

      tfText2 = new JTextField();
      tfText2.setText(Config.command_prefix);
      gbcConfigWindow.gridx = 1;
      gbcConfigWindow.gridy = 2;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(tfText2, gbcConfigWindow);
      add(tfText2);

      lbSv_l = new JLabel("Server Volume:");
      gbcConfigWindow.gridx = 0;
      gbcConfigWindow.gridy = 3;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 1;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(lbSv_l, gbcConfigWindow);
      add(lbSv_l);

      sdV_slide = new JSlider();
      sdV_slide.setValue(Config.volume);
      gbcConfigWindow.gridx = 1;
      gbcConfigWindow.gridy = 3;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(sdV_slide, gbcConfigWindow);
      add(sdV_slide);

      tfV_text = new JTextField();
      tfV_text.setText(String.valueOf(Config.volume));
      gbcConfigWindow.gridx = 2;
      gbcConfigWindow.gridy = 3;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(tfV_text, gbcConfigWindow);
      add(tfV_text);

      List<TextChannel> txtChl = Login.Jda.getTextChannels();
      String[] txtArray = txtChl.toArray(new String[txtChl.size()]);
      cmbTc_list = new JComboBox<String>(txtArray);
      gbcConfigWindow.gridx = 1;
      gbcConfigWindow.gridy = 4;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(cmbTc_list, gbcConfigWindow);
      add(cmbTc_list);

      lbTc_l = new JLabel("Text Channel:");
      gbcConfigWindow.gridx = 0;
      gbcConfigWindow.gridy = 4;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 1;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(lbTc_l, gbcConfigWindow);
      add(lbTc_l);

      lbVc_l = new JLabel("Voice Channel:");
      gbcConfigWindow.gridx = 0;
      gbcConfigWindow.gridy = 5;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 1;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(lbVc_l, gbcConfigWindow);
      add(lbVc_l);

      List<VoiceChannel> voChl = Login.Jda.getVoiceChannels();
      String[] voArray = voChl.toArray(new String[voChl.size()]);
      cmbVc_list = new JComboBox<String>(voArray);
      gbcConfigWindow.gridx = 1;
      gbcConfigWindow.gridy = 5;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(cmbVc_list, gbcConfigWindow);
      add(cmbVc_list);

      btCancel = new JButton("Cancel");
      btCancel.addActionListener(e -> {
          this.setVisible(false);
      });
      gbcConfigWindow.gridx = 1;
      gbcConfigWindow.gridy = 6;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(btCancel, gbcConfigWindow);
      add(btCancel);

      btSave = new JButton("Save");
      btSave.addActionListener(e -> {

          this.setVisible(false);
      });
      gbcConfigWindow.gridx = 2;
      gbcConfigWindow.gridy = 6;
      gbcConfigWindow.gridwidth = 1;
      gbcConfigWindow.gridheight = 1;
      gbcConfigWindow.fill = GridBagConstraints.BOTH;
      gbcConfigWindow.weightx = 1;
      gbcConfigWindow.weighty = 0;
      gbcConfigWindow.anchor = GridBagConstraints.NORTH;
      gbConfigWindow.setConstraints(btSave, gbcConfigWindow);
      add(btSave);

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setSize(720, 320);
    setLocationRelativeTo(null);
   }
}
}