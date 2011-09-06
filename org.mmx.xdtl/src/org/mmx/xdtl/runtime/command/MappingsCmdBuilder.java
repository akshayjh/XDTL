package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class MappingsCmdBuilder extends AbstractCmdBuilder {
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConverter;

    private String m_target;
    private Mappings m_mappings = new Mappings();
    
    @Inject
    public MappingsCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        m_exprEval = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
                getRuntimeClass().getConstructor(String.class, Mappings.class);

        return ctor.newInstance(m_target, m_mappings);
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
        org.mmx.xdtl.model.command.Mappings mappings =
                (org.mmx.xdtl.model.command.Mappings) cmd;
        
        Context ctx = getContext();
        
        m_target = (String) m_exprEval.evaluate(ctx,
                mappings.getTargetVarName());
        
        addSources(ctx, mappings);
        addTargets(ctx, mappings);
        addColumns(ctx, mappings);
        addConditions(ctx, mappings);
    }

    private void addSources(Context ctx,
            org.mmx.xdtl.model.command.Mappings mappings) {
        for (org.mmx.xdtl.model.Source source: mappings.getSources()) {
            Object mapId = m_exprEval.evaluate(ctx, source.getMapId());
            Object src = m_exprEval.evaluate(ctx, source.getSource());
            Object alias = m_exprEval.evaluate(ctx, source.getAlias());
            Object isQuery = m_exprEval.evaluate(ctx, source.getIsQuery());
            
            if (mapId == null) {
                throw new XdtlException("mapId cannot be null", source.getSourceLocator());
            }
            
            if (isQuery == null) {
                isQuery = false;
            }
            
            m_mappings.addSource(new Source(
                    m_typeConverter.toInteger(mapId),
                    m_typeConverter.toString(src),
                    m_typeConverter.toString(alias),
                    m_typeConverter.toBoolean(isQuery)
            ));
        }
    }

    private void addTargets(Context ctx,
            org.mmx.xdtl.model.command.Mappings mappings) {
        for (org.mmx.xdtl.model.Target target: mappings.getTargets()) {
            Object mapId = m_exprEval.evaluate(ctx, target.getMapId());
            Object tgt = m_exprEval.evaluate(ctx, target.getTarget());
            Object alias = m_exprEval.evaluate(ctx, target.getAlias());
            Object isVirtual = m_exprEval.evaluate(ctx, target.getIsVirtual());
            
            if (mapId == null) {
                throw new XdtlException("mapId cannot be null", target.getSourceLocator());
            }
            
            if (isVirtual == null) {
                isVirtual = false;
            }
            
            m_mappings.addTarget(new Target(
                    m_typeConverter.toInteger(mapId),
                    m_typeConverter.toString(tgt),
                    m_typeConverter.toString(alias),
                    m_typeConverter.toBoolean(isVirtual)
            ));
        }
    }

    private void addColumns(Context ctx,
            org.mmx.xdtl.model.command.Mappings mappings) {

        for (org.mmx.xdtl.model.Column col: mappings.getColumns()) {
            Object mapId = m_exprEval.evaluate(ctx, col.getMapId());
            Object tgt = m_exprEval.evaluate(ctx, col.getTarget());
            Object src = m_exprEval.evaluate(ctx, col.getSource());
            Object func = m_exprEval.evaluate(ctx, col.getFunction());
            Object datatype = m_exprEval.evaluate(ctx, col.getDataType());
            Object isJoinKey = m_exprEval.evaluate(ctx, col.getIsJoinKey());
            Object isUpdatable = m_exprEval.evaluate(ctx, col.getIsUpdatable());
            Object isDistinct = m_exprEval.evaluate(ctx, col.getIsDistinct());
            Object isAggregate = m_exprEval.evaluate(ctx, col.getIsAggregate());
            
            if (mapId == null) {
                throw new XdtlException("mapId cannot be null", col.getSourceLocator());
            }
            
            if (isJoinKey == null) isJoinKey = false;
            if (isUpdatable == null) isUpdatable = false;
            if (isDistinct == null) isDistinct = false;
            if (isAggregate == null) isAggregate = false;
            
            m_mappings.addColumn(new Column(
                    m_typeConverter.toInteger(mapId),
                    m_typeConverter.toString(tgt),
                    m_typeConverter.toString(src),
                    m_typeConverter.toString(func),
                    m_typeConverter.toString(datatype),
                    m_typeConverter.toBoolean(isJoinKey),
                    m_typeConverter.toBoolean(isUpdatable),
                    m_typeConverter.toBoolean(isDistinct),
                    m_typeConverter.toBoolean(isAggregate)
            ));
        }
    }

    private void addConditions(Context ctx,
            org.mmx.xdtl.model.command.Mappings mappings) {
        for (org.mmx.xdtl.model.Condition cond: mappings.getConditions()) {
            Object mapId = m_exprEval.evaluate(ctx, cond.getMapId());
            Object alias = m_exprEval.evaluate(ctx, cond.getAlias());
            Object condition = m_exprEval.evaluate(ctx, cond.getCondition());
            Object condType = m_exprEval.evaluate(ctx, cond.getCondType());
            Object joinType = m_exprEval.evaluate(ctx, cond.getJoinType());

            if (mapId == null) {
                throw new XdtlException("mapId cannot be null", cond.getSourceLocator());
            }

            m_mappings.addCondition(new Condition(
                    m_typeConverter.toInteger(mapId),
                    m_typeConverter.toString(alias),
                    m_typeConverter.toString(condition),
                    m_typeConverter.toString(condType),
                    m_typeConverter.toString(joinType)
            ));
        }
    }
}
