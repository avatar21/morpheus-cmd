package com.grahamedgecombe.jterminal;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 */
public class SimpleTerminal implements KeyListener, FocusListener, MouseListener {
    // TODO add batch job execution to this ...
    // TODO add executor history to this ...
    private static final Logger logger = Logger.getLogger("test");

    private List<Object> objects = new ArrayList<Object>();
    private JTerminal term;
    private static final String TERM = "$ ";
    private static StringBuffer lineBuffer = new StringBuffer();
    private static final String TIMEOUT_PROPERTY = "20000";
    private final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
    private final String shell = isWindows ? "cmd /c" : "bash -c";
    private TerminalOutputStream infoTermOutStream;
    private TerminalErrorOutputStream errorTermOutStream;

    public SimpleTerminal() {

        //term = new JTerminal(new Vt100TerminalModel());
        term = new JTerminal();
        infoTermOutStream = new TerminalOutputStream(term);
        errorTermOutStream = new TerminalErrorOutputStream(term);

        JFrame frame = new JFrame("Testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(term);
        frame.pack();
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        term.println("Welcome to Simple Terminal");
        term.print(TERM);
        term.addKeyListener(this);
        term.addFocusListener(this);
        term.addMouseListener(this);
        term.setFocusable(true);
//		AnsiControlSequenceParser parser = new AnsiControlSequenceParser(this);

//		for (int i = 0; i<1000; i++) {
//			term.model.print("i = " + i);
//			logger.info("i = " + i);
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
    }

    @Override
    public synchronized void keyTyped(KeyEvent e) {
        Character keyPressed = e.getKeyChar();
        if (!e.isActionKey() && keyPressed != KeyEvent.VK_ENTER) {
            lineBuffer.append(keyPressed);
            //logger.info(String.format("%s pressed", keyPressed));
            getTerm().print(""+keyPressed);
        } else {
            if (keyPressed == KeyEvent.VK_ENTER) {
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
                CommandLine cmdLine = CommandLine.parse(shell);
                // the Windows CMD shell doesn't handle multiline statements,
                // they need to be delimited by '&&' instead
                if (isWindows) {
                    String[] lines = StringUtils.split(cmd, "\n");
                    cmd = StringUtils.join(lines, " && ");
                }
                cmdLine.addArgument(cmd, false);
                int exitVal = -1;

                try {
                    DefaultExecutor executor = new DefaultExecutor();
                    executor.setStreamHandler(new PumpStreamHandler(
                            infoTermOutStream, errorTermOutStream));
                    executor.setWatchdog(new ExecuteWatchdog(Long.valueOf((TIMEOUT_PROPERTY))));
                    //executors.put(contextInterpreter.getParagraphId(), executor);
                    exitVal = executor.execute(cmdLine);
                    //logger.info("Paragraph " + .getParagraphId()
                    //        + " return with exit value: " + exitVal);
                    //return new InterpreterResult(Code.SUCCESS, outStream.toString());
                } catch (IOException e) {
                    logger.warning(e.getLocalizedMessage());
                }

                break;
        }
        return isExec;
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

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
        new SimpleTerminal();
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
}
