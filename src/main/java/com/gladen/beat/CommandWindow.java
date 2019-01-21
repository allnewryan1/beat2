package com.gladen.beat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
//import javax.swing.SwingUtilities;

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
        /*
        ConfigWindow window = new ConfigWindow();
        config.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                window.setVisible(true);    //currently broken
            });
        });
        */
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
}