package org.mmx.xdtl.runtime.impl;

import java.io.IOException;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class Console {
    IConsoleHelper m_helper;

    private interface IConsoleHelper {
        int readKey() throws IOException;
    }

    public Console() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                m_helper = new WinConsoleHelper();
            } else {
                m_helper = new UnixConsoleHelper();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int readKey() throws IOException {
        return m_helper.readKey();
    }

    public String readLine() throws IOException {
        StringBuilder buf = new StringBuilder();

        while (true) {
            int c = readKey();

            if (c == 10 || c == 13) {
                System.out.print('\n');
                break;
            }

            if (c == 0x7f || c == 0x08) {
                if (buf.length() > 0) {
                    buf.setLength(buf.length() - 1);
                    System.out.print("\u0008 \u0008");
                }
            } else if (c >= 0x20) {
                buf.append((char) c);
                System.out.print((char) c);
            }
        }

        return buf.toString();
    }

    private static class WinConsoleHelper implements IConsoleHelper {
        private Msvcrt m_msvcrt;

        public WinConsoleHelper() {
            m_msvcrt = (Msvcrt) Native.loadLibrary("msvcrt", Msvcrt.class);
        }

        @Override
        public int readKey() {
            return m_msvcrt._getwch();
        }
    }

    private static interface Msvcrt extends Library {
        int _getwch();
    }

    private static class UnixConsoleHelper implements IConsoleHelper {
        public UnixConsoleHelper() throws InterruptedException, IOException {
            ProcessBuilder pb = new ProcessBuilder("/bin/stty", "-icanon", "-echo");
            pb.inheritIO();
            pb.start().waitFor();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    ProcessBuilder pb = new ProcessBuilder("/bin/stty", "icanon", "echo");
                    pb.inheritIO();
                    try {
                        pb.start().waitFor();
                    } catch (InterruptedException | IOException e) {
                        System.out.println(e);
                    }
                }
            }));
        }

        @Override
        public int readKey() throws IOException {
            return System.in.read();
        }
    }
}
