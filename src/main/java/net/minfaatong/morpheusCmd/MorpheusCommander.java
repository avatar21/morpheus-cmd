/*
 * Copyright (c) 2017-2018 Avatar Ng.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package net.minfaatong.morpheusCmd;

import net.minfaatong.morpheusCmd.interpreter.CmdInterpreter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Avatar Ng
 */
public class MorpheusCommander implements KeyListener, FocusListener, MouseListener, ListSelectionListener, ActionListener {
    // TODO add batch job execution to this ...
    // TODO add executor history to this ...
    private static final Logger logger = Logger.getLogger(MorpheusCommander.class.getCanonicalName());

    private List<Object> objects = new ArrayList<Object>();
    private JTerminal term;
    private JList list;
    private DefaultListModel listModel;
    private JButton btnAddFile;
    private static final String TERM = "$ ";
    private static StringBuffer lineBuffer = new StringBuffer();

    private TerminalOutputStream infoTermOutStream;
    private TerminalErrorOutputStream errorTermOutStream;
    private CmdInterpreter mCmdInterpreter;

    public MorpheusCommander() {
        term = new JTerminal();
        infoTermOutStream = new TerminalOutputStream(term);
        errorTermOutStream = new TerminalErrorOutputStream(term);
        mCmdInterpreter = new CmdInterpreter();

        term.println("=== Morpheus Commander ===");
        term.print(TERM);
        term.addKeyListener(this);
        term.addFocusListener(this);
        term.addMouseListener(this);
        term.setFocusable(true);

        listModel = new DefaultListModel();

        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
        listScrollPane.setPreferredSize(new Dimension(200, 505));
        final JPanel pnlFile = new JPanel(new BorderLayout());
        btnAddFile = new JButton("Add File");
        btnAddFile.addActionListener(this);
        pnlFile.add(btnAddFile, SpringLayout.NORTH);
        pnlFile.add(listScrollPane);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Morpheus Commander");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(term);
                frame.add(pnlFile, SpringLayout.EAST);
                frame.pack();
                frame.setSize(505, 300);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    @Override
    public synchronized void keyTyped(KeyEvent e) {
        Character keyPressed = e.getKeyChar();
        if (e.isActionKey()) {
            switch (keyPressed) {
                case KeyEvent.VK_HOME:
                    // TODO home
                    break;
                case KeyEvent.VK_END:
                    break;
                case KeyEvent.VK_PAGE_UP:
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    break;
                case KeyEvent.VK_UP:
                    // TODO retrieve last entered history
                    break;
                case KeyEvent.VK_DOWN:
                    // TODO retrieve next entered history
                    break;
                case KeyEvent.VK_LEFT:
                    term.moveCursorBack(1);
                    break;
                case KeyEvent.VK_RIGHT:
                    term.moveCursorForward(1);
                    break;
                case KeyEvent.VK_BEGIN:
                    break;
            }
        } else {
            if (keyPressed != KeyEvent.VK_ENTER) {
                lineBuffer.append(keyPressed);
                //logger.info(String.format("%s pressed", keyPressed));
                getTerm().print("" + keyPressed);
            } else {
                switch (keyPressed) {
                    case KeyEvent.VK_ENTER:
                        boolean isClear = handleCommand(lineBuffer.toString());
                        logger.info(String.format("entered text = %s", lineBuffer.toString()));
                        lineBuffer.setLength(0);
                        lineBuffer.trimToSize();
                        if (!isClear) {
                            //logger.info(String.format("ENTER pressed", keyPressed));
                            // TODO handle shell command here
                        } else {
                            int row = getTerm().getModel().getCursorRow();
                            getTerm().getModel().moveCursorUp(row);
                        }
                        getTerm().println("");
                        getTerm().print(TERM);
                        break;
                    case KeyEvent.VK_DELETE:

                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        // delete last character
                        lineBuffer.setLength(lineBuffer.length() - 1);
                        term.backspace();
                        break;
                }
            }
        }
    }

    private boolean handleCommand(String cmd) {
        boolean isExec = false;
        switch (cmd) {
            case "clear":
                getTerm().clear();
                isExec = true;
                break;
            default:
                mCmdInterpreter.interpret(cmd, infoTermOutStream, errorTermOutStream);

                break;
        }
        return isExec;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        logger.info(String.format("Key pressed = %s", new Character((char)e.getKeyCode())));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        logger.info(String.format("Key released = %s", new Character((char)e.getKeyCode())));
    }

    public List<Object> getObjects() {
        return objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public JTerminal getTerm() {
        return term;
    }

    public void setTerm(JTerminal term) {
        this.term = term;
    }

    public static void main(String[] args) {
        new MorpheusCommander();
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public TerminalOutputStream getInfoTermOutStream() {
        return infoTermOutStream;
    }

    public void setInfoTermOutStream(TerminalOutputStream infoTermOutStream) {
        this.infoTermOutStream = infoTermOutStream;
    }

    public TerminalErrorOutputStream getErrorTermOutStream() {
        return errorTermOutStream;
    }

    public void setErrorTermOutStream(TerminalErrorOutputStream errorTermOutStream) {
        this.errorTermOutStream = errorTermOutStream;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddFile) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            //fileChooser.setSelectedFile(new File("README.html"));

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getPath();
                JOptionPane.showMessageDialog(null, "You selected " + filename);
            } else if (result == JFileChooser.CANCEL_OPTION) {
                JOptionPane.showMessageDialog(null, "You selected nothing.");
            } else if (result == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(null, "An error occurred.");
            }
        }
    }
}
