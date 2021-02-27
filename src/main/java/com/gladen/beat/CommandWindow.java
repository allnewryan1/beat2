package com.gladen.beat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class CommandWindow extends JFrame {
    private static final long serialVersionUID = 550987585;

    public PrintStream out;
    private static JLabel nowPlaying;

    public CommandWindow() {
        super("Beat - music unleashed!");
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem config = new JMenuItem("Config");
        ConfigWindow window = new ConfigWindow();
        config.addActionListener(e -> window.setVisible(true));
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        JMenuItem test = new JMenuItem("Testing");
        test.addActionListener(e -> {
            out.println("Text channels:");
            for (String channel:Login.textChannels1) {
                out.println(channel);
            }
            out.println("Voice channels:");
            for (String channel:Login.voiceChannels1) {
                out.println(channel);
            }
        });
        menu.add(config);
        menu.add(test);
        menu.add(exit);
        menubar.add(menu);
        setJMenuBar(menubar);


        //The text area which is used for displaying logging information.
        JTextArea textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        out = new PrintStream(new CustomOutputStream(textArea));

        // create the status bar panel and shove it down the bottom of the frame
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        JLabel statusLabel = new JLabel("Now Playing: ");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nowPlaying = new JLabel("");
        nowPlaying.setHorizontalAlignment(SwingConstants.RIGHT);
        statusPanel.add(statusLabel);
        statusPanel.add(nowPlaying);
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
        add(statusPanel, statusConstraints);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 320);
        setLocationRelativeTo(null);
    }



    public static void updatePlayingTrack(String s) {
        if (nowPlaying == null) return;
        nowPlaying.setText(s);
    }




    public static class CustomOutputStream extends OutputStream {
        private final JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            textArea.append(String.valueOf((char)b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}