package org.mmx.xdtl.conf;

import java.lang.annotation.Annotation;
import java.util.Properties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.velocity.app.VelocityEngine;
import org.mmx.xdtl.Version;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Call;
import org.mmx.xdtl.model.command.Clear;
import org.mmx.xdtl.model.command.Error;
import org.mmx.xdtl.model.command.Exec;
import org.mmx.xdtl.model.command.Exit;
import org.mmx.xdtl.model.command.Fetch;
import org.mmx.xdtl.model.command.For;
import org.mmx.xdtl.model.command.Get;
import org.mmx.xdtl.model.command.If;
import org.mmx.xdtl.model.command.Log;
import org.mmx.xdtl.model.command.Mappings;
import org.mmx.xdtl.model.command.Move;
import org.mmx.xdtl.model.command.Pack;
import org.mmx.xdtl.model.command.Put;
import org.mmx.xdtl.model.command.Query;
import org.mmx.xdtl.model.command.Read;
import org.mmx.xdtl.model.command.Render;
import org.mmx.xdtl.model.command.Script;
import org.mmx.xdtl.model.command.Send;
import org.mmx.xdtl.model.command.Strip;
import org.mmx.xdtl.model.command.Transaction;
import org.mmx.xdtl.model.command.Unpack;
import org.mmx.xdtl.model.command.Write;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.ConnectionManager;
import org.mmx.xdtl.runtime.Engine;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.runtime.command.CallCmd;
import org.mmx.xdtl.runtime.command.CallCmdBuilder;
import org.mmx.xdtl.runtime.command.ClearCmd;
import org.mmx.xdtl.runtime.command.ClearCmdBuilder;
import org.mmx.xdtl.runtime.command.ErrorCmd;
import org.mmx.xdtl.runtime.command.ErrorCmdBuilder;
import org.mmx.xdtl.runtime.command.ExecCmd;
import org.mmx.xdtl.runtime.command.ExecCmdBuilder;
import org.mmx.xdtl.runtime.command.ExitCmd;
import org.mmx.xdtl.runtime.command.ExitCmdBuilder;
import org.mmx.xdtl.runtime.command.FetchCmd;
import org.mmx.xdtl.runtime.command.FetchCmdBuilder;
import org.mmx.xdtl.runtime.command.FileTransferCmd;
import org.mmx.xdtl.runtime.command.FileTransferCmdBuilder;
import org.mmx.xdtl.runtime.command.ForCmd;
import org.mmx.xdtl.runtime.command.ForCmdBuilder;
import org.mmx.xdtl.runtime.command.IfCmd;
import org.mmx.xdtl.runtime.command.IfCmdBuilder;
import org.mmx.xdtl.runtime.command.LogCmd;
import org.mmx.xdtl.runtime.command.LogCmdBuilder;
import org.mmx.xdtl.runtime.command.MappingsCmd;
import org.mmx.xdtl.runtime.command.MappingsCmdBuilder;
import org.mmx.xdtl.runtime.command.OsArgListBuilder;
import org.mmx.xdtl.runtime.command.OsArgListBuilderImpl;
import org.mmx.xdtl.runtime.command.OsProcessRunner;
import org.mmx.xdtl.runtime.command.OsProcessRunnerImpl;
import org.mmx.xdtl.runtime.command.PostgresqlReadCmd;
import org.mmx.xdtl.runtime.command.PostgresqlWriteCmd;
import org.mmx.xdtl.runtime.command.QueryCmd;
import org.mmx.xdtl.runtime.command.QueryCmdBuilder;
import org.mmx.xdtl.runtime.command.ReadCmdBuilder;
import org.mmx.xdtl.runtime.command.RenderCmd;
import org.mmx.xdtl.runtime.command.RenderCmdBuilder;
import org.mmx.xdtl.runtime.command.ScriptCmd;
import org.mmx.xdtl.runtime.command.ScriptCmdBuilder;
import org.mmx.xdtl.runtime.command.SendCmd;
import org.mmx.xdtl.runtime.command.SendCmdBuilder;
import org.mmx.xdtl.runtime.command.StripCmd;
import org.mmx.xdtl.runtime.command.StripCmdBuilder;
import org.mmx.xdtl.runtime.command.TransactionCmd;
import org.mmx.xdtl.runtime.command.TransactionCmdBuilder;
import org.mmx.xdtl.runtime.command.WriteCmdBuilder;
import org.mmx.xdtl.runtime.impl.CommandInvoker;
import org.mmx.xdtl.runtime.impl.CommandInvokerImpl;
import org.mmx.xdtl.runtime.impl.CommandMapping;
import org.mmx.xdtl.runtime.impl.CommandMappingSet;
import org.mmx.xdtl.runtime.impl.ConnectionManagerImpl;
import org.mmx.xdtl.runtime.impl.EngineImpl;
import org.mmx.xdtl.runtime.impl.ScriptExpressionEvaluator;
import org.mmx.xdtl.runtime.impl.TypeConverterImpl;
import org.mmx.xdtl.runtime.util.PathList;
import org.mmx.xdtl.runtime.util.StringShortener;
import org.mmx.xdtl.runtime.util.VariableNameValidator;
import org.mmx.xdtl.services.GuiceInjector;
import org.mmx.xdtl.services.Injector;
import org.mmx.xdtl.services.UriSchemeParser;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class RuntimeModule extends AbstractModule {
    private static final CommandMapping[] DEFAULT_COMMAND_MAPPINGS = {
        new CommandMapping(Call.class, CallCmdBuilder.class, CallCmd.class),        
        new CommandMapping(Get.class, FileTransferCmdBuilder.class, FileTransferCmd.class),
        new CommandMapping(Put.class, FileTransferCmdBuilder.class, FileTransferCmd.class),
        new CommandMapping(Pack.class, FileTransferCmdBuilder.class, FileTransferCmd.class),        
        new CommandMapping(Unpack.class, FileTransferCmdBuilder.class, FileTransferCmd.class),
        new CommandMapping(Mappings.class, MappingsCmdBuilder.class, MappingsCmd.class),
        new CommandMapping(Render.class, RenderCmdBuilder.class, RenderCmd.class),
        new CommandMapping(Transaction.class, TransactionCmdBuilder.class, TransactionCmd.class),
        new CommandMapping(Query.class, QueryCmdBuilder.class, QueryCmd.class),
        new CommandMapping(Exec.class, ExecCmdBuilder.class, ExecCmd.class),
        new CommandMapping(Read.class, ReadCmdBuilder.class, PostgresqlReadCmd.class),
        new CommandMapping(Write.class, WriteCmdBuilder.class, PostgresqlWriteCmd.class),
        new CommandMapping(Strip.class, StripCmdBuilder.class, StripCmd.class),
        new CommandMapping(Move.class, FileTransferCmdBuilder.class, FileTransferCmd.class),
        new CommandMapping(Clear.class, ClearCmdBuilder.class, ClearCmd.class),
        new CommandMapping(Script.class, ScriptCmdBuilder.class, ScriptCmd.class),
        new CommandMapping(If.class, IfCmdBuilder.class, IfCmd.class),
        new CommandMapping(Log.class, LogCmdBuilder.class, LogCmd.class),
        new CommandMapping(Fetch.class, FetchCmdBuilder.class, FetchCmd.class),
        new CommandMapping(For.class, ForCmdBuilder.class, ForCmd.class),
        new CommandMapping(Send.class, SendCmdBuilder.class, SendCmd.class),
        new CommandMapping(Exit.class, ExitCmdBuilder.class, ExitCmd.class),
        new CommandMapping(Error.class, ErrorCmdBuilder.class, ErrorCmd.class)
    };

    private final CommandMappingSet m_commandMappings = createDefaultCommandMappings();
    private final ScriptEngineManager m_scriptEngineManager = new ScriptEngineManager();
    private final Properties m_properties;
    
    public RuntimeModule(Properties properties) {
        m_properties = properties;
        CommandOverrides overrides = new CommandOverrides(properties, m_commandMappings);
        
        try {
            overrides.apply();
        } catch (ClassNotFoundException e) {
            throw new XdtlException(e);
        }
    }
    
    public void addCommandMapping(CommandMapping mapping) {
        m_commandMappings.putMapping(mapping);
    }
    
    @Override
    protected void configure() {
        bind(ExpressionEvaluator.class).to(ScriptExpressionEvaluator.class).in(Singleton.class);
        bind(TypeConverter.class).to(TypeConverterImpl.class).in(Singleton.class);
        bind(Engine.class).to(EngineImpl.class);
        bind(CommandInvoker.class).to(CommandInvokerImpl.class);
        bind(ConnectionManager.class).to(ConnectionManagerImpl.class);
        bind(VelocityEngine.class).toProvider(VelocityEngineProvider.class);
        bind(Properties.class).annotatedWith(Names.named("velocity.properties")).toProvider(VelocityPropertiesProvider.class);
        bind(OsProcessRunner.class).to(OsProcessRunnerImpl.class);
        bind(OsArgListBuilder.class).to(OsArgListBuilderImpl.class);
        bind(CommandMappingSet.class).toInstance(m_commandMappings);
        bind(VariableNameValidator.class);
        bind(StringShortener.class);
        bind(UriSchemeParser.class).in(Singleton.class);
        
        String homeDirUrl = "file://" + m_properties.getProperty("home");
        PathList startupScriptsPathList = new PathList(homeDirUrl,
                m_properties.getProperty("startupscripts.path"));
        bind(PathList.class).annotatedWith(Names.named("startupscripts.path")).toInstance(startupScriptsPathList);
        
        PathList velocityPathList = new PathList(homeDirUrl,
                m_properties.getProperty("velocity.path"));
        bind(PathList.class).annotatedWith(Names.named("velocity.path")).toInstance(velocityPathList);

        bindCommandBuilders();
        Version ver = new Version();
        bind(String.class).annotatedWith(Names.named("xdtl.version")).toInstance(ver.getImplementationVersion());
        bind(Injector.class).to(GuiceInjector.class);
    }
    
    @Provides @Singleton
    protected ScriptEngine getScriptEngine() {
        return m_scriptEngineManager.getEngineByName("JavaScript");
    }
    
    private void bindCommandBuilders() {
        for (CommandMapping mapping: m_commandMappings.getMappings()) {
            bind(key(CommandBuilder.class, mapping.getModelClass())).to(mapping.getBuilderClass());
        }
    }

    private Annotation name(Class<?> type) {
        return Names.named(type.getName());
    }
    
    private <T> Key<T> key(Class<T> type1, Class<?> type2) {
        return Key.get(type1, name(type2));
    }

    private CommandMappingSet createDefaultCommandMappings() {
        CommandMappingSet result = new CommandMappingSet();
        for (CommandMapping mapping: DEFAULT_COMMAND_MAPPINGS) {
            result.putMapping(mapping);
        }

        return result;
    }
}
