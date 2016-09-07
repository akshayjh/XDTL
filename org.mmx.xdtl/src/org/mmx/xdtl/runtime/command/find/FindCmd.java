package org.mmx.xdtl.runtime.command.find;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Types;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mmx.xdtl.db.Column;
import org.mmx.xdtl.db.RowSet;
import org.mmx.xdtl.db.RowSet.Row;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.services.PathList;

public class FindCmd implements RuntimeCommand {
    private static final Logger m_logger = XdtlLogger.getLogger("xdtl.cmd.find");

    private String m_source;
    private Pattern m_pattern;
    private boolean m_recursive;
    private String m_rowsetVarName;
    private RowSet m_rowset;

    public FindCmd(String source, String match, boolean recursive, String rowsetVarName) {
        m_source = source;
        m_pattern = Pattern.compile(match);
        m_recursive = recursive;
        m_rowsetVarName = rowsetVarName;

        ArrayList<Column> columns = new ArrayList<Column>(4);
        columns.add(new Column("name", Types.VARCHAR, "VARCHAR"));
        columns.add(new Column("path", Types.VARCHAR, "VARCHAR"));
        columns.add(new Column("size", Types.BIGINT, "BIGINT"));
        columns.add(new Column("type", Types.VARCHAR, "VARCHAR"));

        m_rowset = new RowSet(columns);
    }

    @Override
    public void run(Context context) throws Throwable {
        if (m_logger.isTraceEnabled()) {
            m_logger.trace(String.format("source=%s, match=%s, recursive=%s, rowset=%s",
                    m_source, m_pattern.pattern(), m_recursive, m_rowsetVarName));
        }

        PathList pathList = new PathList("file:", m_source);

        final FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return true;
            }
        };

        PathList.ForEachCallback callback = new PathList.ForEachCallback() {
            @Override
            public Object execute(File file) {
                try {
                    if (file.isDirectory() && m_recursive) {
                        new PathList("file:", file.getPath()).forEachFile(filter, this);
                        return null;
                    }

                    if (file.isFile() && m_pattern.matcher(file.getPath()).matches()) {
                        Row row = m_rowset.newRow();
                        row.set(0, file.getName());
                        row.set(1, file.getCanonicalPath());
                        row.set(2, file.length());
                        row.set(3, getExtension(file.getName()));
                        m_rowset.add(row);
                    }
                } catch (Exception e) {
                    throw new XdtlException(e);
                }

                return null;
            }

            private String getExtension(String name) {
                int pos = name.lastIndexOf('.');
                return (pos != -1) ? name.substring(pos + 1, name.length()) : "";
            }
        };


        pathList.forEachFile(filter, callback);
        context.assignVariable(m_rowsetVarName, m_rowset);
    }
}
