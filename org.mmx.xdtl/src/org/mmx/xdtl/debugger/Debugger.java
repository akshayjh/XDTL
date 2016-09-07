package org.mmx.xdtl.debugger;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.impl.Console;

import com.google.inject.Inject;

public class Debugger {
    private static final String HELP =
              "Debugger commands:\n"
            + "------------------\n"
            + "<space> Step into\n"
            + "<enter> Step over\n"
            + "?       Dump current context\n"
            + "b       Manage breakpoints\n"
            + "c       Continue\n"
            + "e       Evaluate expression\n"
            + "h       Help\n"
            + "q       Quit\n"
            + "v       Display variable\n"
            + "\n"
            + "Breakpoints:\n"
            + "------------\n"
            + "?       List breakpoints\n"
            + "<spec>  Add breakpoint\n"
            + "-       Remove all breakpoints\n"
            + "-<spec> Remove breakpoint by spec\n"
            + "-<idx>  Remove breakpoint by index\n"
            + "Spec format is <url>[:<line>]";

    private ExpressionEvaluator m_exprEval;
    private Console m_console;
    private BreakpointList m_breakpoints = new BreakpointList();
    private boolean m_continue;
    private Command m_stepOverCmd;

    @Inject
    public Debugger(ExpressionEvaluator exprEval) throws IOException, InterruptedException {
        m_exprEval = exprEval;
        m_console = new Console();
    }

    public void preInvoke(Command step, Context context) {
        if (m_continue && !m_breakpoints.containsMatch(step.getSourceLocator())) {
            return;
        }

        m_continue = false;
        System.out.println("Break: " + step.getSourceLocator());

        while (true) {
            try {
                if (readAndExecuteDebuggerCmd(step, context)) {
                    break;
                }
            } catch (Throwable t) {
                System.out.println(t.getClass().getName() + ": " + t.getMessage());
            }
        }
    }

    public void postInvoke(Command step) {
        if (step == m_stepOverCmd) {
            m_continue = false;
            m_stepOverCmd = null;
        }
    }

    private boolean readAndExecuteDebuggerCmd(Command step, Context context) throws Exception {
        int c = m_console.readKey();

        switch (c) {
        case ' ':
            return true;
        case '?':
            dumpContext(context);
            break;
        case 3:
        case 'q':
            System.exit(0);
            break;
        case 'e':
            evaluate(context);
            break;
        case 'v':
            showVariable(context);
            break;
        case 'b':
            manageBreakpoints();
            break;
        case 'c':
            m_continue = true;
            return true;
        case '\n':
            stepOver(step);
            return true;
        case 'h':
            help();
            break;
        default:
            System.out.println("Unknown command '" + (char) c + "'\nPress 'h' for help");
        }

        return false;
    }

    private void help() {
        System.out.println(HELP);
    }

    private void stepOver(Command step) {
        m_stepOverCmd = step;
        m_continue = true;
    }

    private void manageBreakpoints() throws IOException {
        System.out.print("break> ");
        String spec = m_console.readLine();
        if (spec.length() == 0) {
            return;
        }
        else if (spec.equals("?")) {
            m_breakpoints.print(System.out);
        }
        else if (spec.equals("-")) {
            m_breakpoints.clear();
            System.out.println("All breakpoints deleted.");
            return;
        } else if (spec.startsWith("-")) {
            spec = spec.substring(1, spec.length());
            int index;

            try {
                index = Integer.parseUnsignedInt(spec);
            } catch (NumberFormatException e) {
                if (!m_breakpoints.remove(spec.substring(1, spec.length()))) {
                    System.out.println("Breakpoint not found.");
                } else {
                    System.out.println("Breakpoint deleted.");
                }
                return;
            }

            if (index >= m_breakpoints.size()) {
                System.out.println("Breakpoint not found.");
            } else {
                m_breakpoints.remove(index);
                System.out.println("Breakpoint deleted.");
            }
        } else {
            if (m_breakpoints.add(spec)) {
                System.out.println("Breakpoint added.");
            } else {
                System.out.println("Duplicate breakpoint not added.");
            }
        }
    }

    public void addBreakpoint(SourceLocator locator) {
        m_breakpoints.add(locator);
    }

    private void evaluate(Context context) throws IOException {
        System.out.print("eval> ");
        String expr = m_console.readLine();
        if (expr.length() > 0) {
            System.out.println(m_exprEval.evaluate(context, expr));
        }
    }

    private void dumpContext(Context context) {
        context.dump(System.out);
    }

    private void showVariable(Context context) throws IOException {
        System.out.print("var> ");
        String varName = m_console.readLine();
        if (varName.length() == 0) {
            return;
        }

        Variable var = context.getVariable(varName);
        if (var == null) {
            System.out.println("Variable '" + varName + "' is not defined.");
            return;
        }

        System.out.println(var.getValue());
    }

    private static class Breakpoint {
        private String m_url;
        private int m_line;

        public Breakpoint(SourceLocator locator) {
            m_url = locator.getDocumentUrl();
            m_line = locator.getLineNumber();
        }

        public Breakpoint(String spec) {
            int pos = spec.lastIndexOf(':');
            if (pos == -1) {
                m_url = spec;
                m_line = -1;
            } else {
                m_url = spec.substring(0, pos);
                m_line = Integer.parseInt(spec.substring(pos + 1, spec.length()));
            }
        }

        public boolean matches(SourceLocator locator) {
            if (locator.getDocumentUrl().equals(m_url)) {
                if (m_line == -1 || m_line == locator.getLineNumber()) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return m_url + ":" + m_line;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + m_line;
            result = prime * result + ((m_url == null) ? 0 : m_url.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Breakpoint other = (Breakpoint) obj;
            if (m_line != other.m_line)
                return false;
            if (m_url == null) {
                if (other.m_url != null)
                    return false;
            } else if (!m_url.equals(other.m_url))
                return false;
            return true;
        }
    }

    private static class BreakpointList {
        private ArrayList<Breakpoint> m_list = new ArrayList<>();

        public boolean containsMatch(SourceLocator locator) {
            for (Breakpoint bp: m_list) {
                if (bp.matches(locator)) {
                    return true;
                }
            }

            return false;
        }

        public boolean remove(String spec) {
            Breakpoint bp = new Breakpoint(spec);
            return m_list.remove(bp);
        }

        public void remove(int index) {
            m_list.remove(index);
        }

        public void clear() {
            m_list.clear();
        }

        public void print(PrintStream out) {
            if (m_list.size() == 0) {
               out.println("No breakpoints.");
               return;
            }

            int i = 0;

            for (Breakpoint bp: m_list) {
                out.printf("%s: %s\n", i, bp);
            }
        }

        public int size() {
            return m_list.size();
        }

        /**
         * Adds a new breakpoint to the list.
         *
         * @param spec The breakpoint specification
         * @return true if breakpoint was added or false if it was a duplicate
         */
        public boolean add(String spec) {
            return add(new Breakpoint(spec));
        }

        public boolean add(SourceLocator locator) {
            return add(new Breakpoint(locator));
        }

        public boolean add(Breakpoint bp) {
            if (m_list.contains(bp)) {
                return false;
            }

            m_list.add(bp);
            return true;
        }
    }
}
