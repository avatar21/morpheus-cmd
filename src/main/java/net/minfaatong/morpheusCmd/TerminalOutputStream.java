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

import java.awt.event.KeyEvent;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Terminal output stream
 * @author Avatar Ng
 */
public class TerminalOutputStream extends OutputStream {
    private static final Logger logger = Logger.getLogger(TerminalOutputStream.class.getCanonicalName());
    private JTerminal terminal;
    private ByteBuffer bytes = ByteBuffer.allocate(2048);

    public TerminalOutputStream(JTerminal terminal) {
        super();
        this.terminal = terminal;
    }

    @Override
    public synchronized void write(int b) {
        try {
            if (b == KeyEvent.VK_ENTER) {
                terminal.println("");
                //terminal.print(String.format("I %s", sbWord.toString()));
                //logger.info(String.format("encoding = %s", super.))
                terminal.print(String.format("I %s", new String(bytes.array(), "UTF-8")));
                /*sbWord.setLength(0);
                sbWord.trimToSize();*/
                // clear byte array
                bytes.rewind();
            } else {
                bytes.put((byte)b);
                //sb.append((char) b);
                //terminal.print( Character.toString((char) b));
            }
        } catch (UnsupportedEncodingException e) {
            logger.warning(e.getLocalizedMessage());
        }
    }
}
