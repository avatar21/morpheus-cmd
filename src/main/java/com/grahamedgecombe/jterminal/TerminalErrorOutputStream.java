package com.grahamedgecombe.jterminal;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;

public class TerminalErrorOutputStream extends OutputStream {
    private JTerminal terminal;
    private StringBuffer sbWord = new StringBuffer();

    public TerminalErrorOutputStream(JTerminal terminal) {
        super();
        this.terminal = terminal;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        if (b == KeyEvent.VK_ENTER) {
            terminal.println("");
            terminal.print(String.format("E %s", sbWord.toString()), Color.RED);
            sbWord.setLength(0);
            sbWord.trimToSize();
        } else {
            sbWord.append((char) b);
            //terminal.print( Character.toString((char) b));
        }
    }
}
