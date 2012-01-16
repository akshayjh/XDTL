package org.mmx.xdtl.conf;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.mmx.xdtl.parser.Parser;
import org.mmx.xdtl.parser.element.CallHandler;
import org.mmx.xdtl.parser.element.ClearHandler;
import org.mmx.xdtl.parser.element.ColumnHandler;
import org.mmx.xdtl.parser.element.ConditionHandler;
import org.mmx.xdtl.parser.element.ConnectionHandler;
import org.mmx.xdtl.parser.element.DecodeHandler;
import org.mmx.xdtl.parser.element.DefaultElementHandler;
import org.mmx.xdtl.parser.element.ErrorHandler;
import org.mmx.xdtl.parser.element.ExecHandler;
import org.mmx.xdtl.parser.element.ExitHandler;
import org.mmx.xdtl.parser.element.FetchHandler;
import org.mmx.xdtl.parser.element.ForHandler;
import org.mmx.xdtl.parser.element.GetHandler;
import org.mmx.xdtl.parser.element.IfHandler;
import org.mmx.xdtl.parser.element.LogHandler;
import org.mmx.xdtl.parser.element.MappingsHandler;
import org.mmx.xdtl.parser.element.MoveHandler;
import org.mmx.xdtl.parser.element.AnnotationElementHandler;
import org.mmx.xdtl.parser.element.PackHandler;
import org.mmx.xdtl.parser.element.PackageHandler;
import org.mmx.xdtl.parser.element.ParameterHandler;
import org.mmx.xdtl.parser.element.PutHandler;
import org.mmx.xdtl.parser.element.QueryHandler;
import org.mmx.xdtl.parser.element.ReadHandler;
import org.mmx.xdtl.parser.element.RenderHandler;
import org.mmx.xdtl.parser.element.ScriptHandler;
import org.mmx.xdtl.parser.element.SendHandler;
import org.mmx.xdtl.parser.element.SourceHandler;
import org.mmx.xdtl.parser.element.StepsHandler;
import org.mmx.xdtl.parser.element.StripHandler;
import org.mmx.xdtl.parser.element.TargetHandler;
import org.mmx.xdtl.parser.element.TaskHandler;
import org.mmx.xdtl.parser.element.TasksHandler;
import org.mmx.xdtl.parser.element.TransactionHandler;
import org.mmx.xdtl.parser.element.UnpackHandler;
import org.mmx.xdtl.parser.element.VariableHandler;
import org.mmx.xdtl.parser.element.WriteHandler;
import org.mmx.xdtl.parser.impl.CachingParser;
import org.mmx.xdtl.parser.impl.ElementHandlerSet;
import org.mmx.xdtl.parser.impl.NonCaching;
import org.mmx.xdtl.parser.impl.SaxParser;
import org.mmx.xdtl.services.PackageCache;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

public class ParserModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(Parser.class).to(CachingParser.class);
        bind(Parser.class).annotatedWith(NonCaching.class).to(SaxParser.class);
        bind(PackageCache.class);
        bind(SAXParser.class).toProvider(SAXParserProvider.class);
    }

    @Provides
    ElementHandlerSet getElementHandlerSet() {
        ElementHandlerSet set = new ElementHandlerSet();
        set.add("package", PackageHandler.class);
        set.add("parameter", ParameterHandler.class);
        set.add("variable", VariableHandler.class);
        set.add("connection", ConnectionHandler.class);
        set.add("tasks", TasksHandler.class);
        set.add("task", TaskHandler.class);
        set.add("steps", StepsHandler.class);
        set.add("call", CallHandler.class);
        set.add("mappings", MappingsHandler.class);
        set.add("render", RenderHandler.class);
        set.add("transaction", TransactionHandler.class);
        set.add("source", SourceHandler.class);
        set.add("target", TargetHandler.class);
        set.add("column", ColumnHandler.class);
        set.add("condition", ConditionHandler.class);
        set.add("query", QueryHandler.class);
        set.add("exec", ExecHandler.class);
        set.add("get", GetHandler.class);
        set.add("put", PutHandler.class);
        set.add("pack", PackHandler.class);
        set.add("unpack", UnpackHandler.class);
        set.add("read", ReadHandler.class);
        set.add("write", WriteHandler.class);
        set.add("strip", StripHandler.class);
        set.add("move", MoveHandler.class);
        set.add("clear", ClearHandler.class);
        set.add("script", ScriptHandler.class);
        set.add("if", IfHandler.class);
        set.add("log", LogHandler.class);
        set.add("fetch", FetchHandler.class);
        set.add("for", ForHandler.class);
        set.add("send", SendHandler.class);
        set.add("exit", ExitHandler.class);
        set.add("error", ErrorHandler.class);
        set.add("annotation", AnnotationElementHandler.class);
        set.add("decode", DecodeHandler.class);
        set.setDefault(DefaultElementHandler.class);
        return set;
    }
    
    @Provides @Named("schema")
    InputStream getSchemaInputStream(@Named("useinternalschema") boolean useInternalSchema ) {
        if (useInternalSchema) {
            return getClass().getResourceAsStream("/org/mmx/xdtl/xdtl.xsd");
        }
        return null;
    }
}
