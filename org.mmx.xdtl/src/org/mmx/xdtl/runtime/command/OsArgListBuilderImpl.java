package org.mmx.xdtl.runtime.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OsArgListBuilderImpl implements OsArgListBuilder {
    private static final char CHR_ESCAPE = '\\';
    private static final Pattern m_pattern = Pattern.compile("%.*?%");
    private HashMap<String, Object> m_variables;
    
    private static class VariableRef {
        private String varName;
        private String leftVal;
        private String rightVal;

        VariableRef(String varName, String leftVal, String rightVal) {
            super();
            this.varName = varName;
            this.leftVal = leftVal;
            this.rightVal = rightVal;
        }

        String getVarName() {
            return varName;
        }

        String getLeftVal() {
            return leftVal;
        }

        String getRightVal() {
            return rightVal;
        }
        
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("varName='").append(varName).append("', ");
            buf.append("leftVal='").append(leftVal).append("', ");
            buf.append("rightVal='").append(rightVal).append("'");
            return buf.toString();
        }
    }
    
    @Override
    public void addVariable(String name, Object value) {
        getVariables().put(name, value);
    }

    @Override
    public void addVariableEscaped(String name, String value) {
        getVariables().put(name, escape(value));
    }
    
    @Override
    public List<String> build(String cmdline, boolean resolveVariables) {
        if (resolveVariables) {
            cmdline = ResolveVariables(cmdline);
        }

        return toArgumentList(cmdline);
    }

    /**
     * Converts a string to list of command line arguments. Arguments are split
     * from the string at first whitespace, unless escaped with '\'. 
     *  
     * @param cmdline
     * @return
     */
    private List<String> toArgumentList(String cmdline) {
        StringBuilder buf = new StringBuilder();
        ArrayList<String> list = new ArrayList<String>();
        boolean escape = false;
        
        for (int i = 0; i < cmdline.length(); i++) {
            char c = cmdline.charAt(i);
            
            if (!escape) {
                if (Character.isWhitespace(c)) {
                    if (buf.length() != 0) {
                        list.add(buf.toString());
                        buf.setLength(0);
                    }
                    continue;
                } else if (c == CHR_ESCAPE) {
                    escape = true;
                    continue;
                }
            }
            
            buf.append(c);
            escape = false;
        }
        
        // last argument
        if (buf.length() != 0) {
            list.add(buf.toString());
        }
        
        return list;
    }

    /**
     * Replaces command line variables with values. Variable format is
     * <code>%varname:trueval:falseval%</code>.
     * 
     * @param cmdline
     * @return
     */
    private String ResolveVariables(String cmdline) {
        Matcher matcher = m_pattern.matcher(cmdline);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String group = matcher.group();
            group = group.substring(1, group.length() - 1);
            VariableRef ref = createVariableRef(group);
            
            String s = getVariableValue(ref);
            
            String replacement = Matcher.quoteReplacement(s);
            matcher.appendReplacement(sb, replacement);
        }
        
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String getVariableValue(VariableRef ref) {
        Object value = (m_variables != null) ?
                m_variables.get(ref.getVarName()) : "";
        
        String s;
        
        if (value != null && (value instanceof Boolean)) {
            Boolean b = ((Boolean) value).booleanValue();
            s = b ? ref.getLeftVal() : ref.getRightVal();
        } else {
            if (value == null) value = "";
            s = value.toString();
            
            if (s.length() == 0) {
                // if the string is empty return right value from variable ref
                s = ref.getRightVal();
            } else {
                // if the string is not empty return left value from variable
                // ref concatenated with variable value
                s = ref.getLeftVal() + s;
            }
        }
        
        return s;
    }

    private VariableRef createVariableRef(String group) {
        String[] arr = group.split(":");
        
        VariableRef result = new VariableRef(arr[0],
                arr.length > 1 ? arr[1] : "",
                arr.length > 2 ? arr[2] : "");
        
        return result;
    }

    @Override
    public String escape(String str) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c) || c == CHR_ESCAPE) {
                buf.append(CHR_ESCAPE);
            }
            buf.append(c);
        }
        
        return buf.toString();
    }
    
    private HashMap<String, Object> getVariables() {
        if (m_variables == null) {
            m_variables = new HashMap<String, Object>();
        }
        
        return m_variables;
    }
}
