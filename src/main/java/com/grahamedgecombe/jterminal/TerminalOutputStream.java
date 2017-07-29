package com.grahamedgecombe.jterminal;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;

public class TerminalOutputStream extends OutputStream {
    private JTerminal terminal;
    private StringBuffer sbWord = new StringBuffer();

    public TerminalOutputStream(JTerminal terminal) {
        super();
        this.terminal = terminal;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        if (b == KeyEvent.VK_ENTER) {
            terminal.println("");
            terminal.print(String.format("I %s", sbWord.toString()));
            sbWord.setLength(0);
            sbWord.trimToSize();
        } else {
            sbWord.append((char) b);
            //terminal.print( Character.toString((char) b));
        }
    }
}
