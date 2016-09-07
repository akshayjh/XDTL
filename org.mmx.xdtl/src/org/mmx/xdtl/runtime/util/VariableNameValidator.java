package org.mmx.xdtl.runtime.util;

import java.util.regex.Pattern;

public class VariableNameValidator {
    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z_]\\w*");

    public boolean isValidVariableName(String str) {
        return (str != null) ? PATTERN.matcher(str).matches() : false;
    }
}
