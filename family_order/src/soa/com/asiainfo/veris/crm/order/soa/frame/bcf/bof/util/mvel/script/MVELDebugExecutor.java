
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mvel2.MVEL;
import org.mvel2.MVELRuntime;
import org.mvel2.Macro;
import org.mvel2.ParserContext;
import org.mvel2.ast.Function;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.debug.Debugger;
import org.mvel2.debug.Frame;
import org.mvel2.integration.GlobalListenerFactory;
import org.mvel2.integration.Listener;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.BaseVariableResolverFactory;

import com.ailk.org.apache.commons.lang3.StringUtils;

public class MVELDebugExecutor
{
    static class DEBUGVariableResolverFactory implements VariableResolverFactory
    {
        private StringBuilder sb = null;

        private VariableResolverFactory trueFactory = null;

        public DEBUGVariableResolverFactory(StringBuilder sbv, VariableResolverFactory vtruefactory)
        {
            this.sb = sbv;
            this.trueFactory = vtruefactory;
        }

        @Override
        public VariableResolver createIndexedVariable(int arg0, String arg1, Object arg2)
        {
            sb.append("[RUN][FACTORY   ][createIndexedVariable(int,String,Object)]-->[" + arg0 + "],[" + arg1 + "],[" + arg2.toString() + "]\n");
            return this.trueFactory.createIndexedVariable(arg0, arg1, arg2);
        }

        @Override
        public VariableResolver createIndexedVariable(int arg0, String arg1, Object arg2, Class<?> arg3)
        {
            sb.append("[RUN][FACTORY   ][createIndexedVariable(int,String,Object,Class<?>)]-->[" + arg0 + "],[" + arg1 + "],[" + arg2.toString() + "],[" + arg3 + "]\n");
            return this.trueFactory.createIndexedVariable(arg0, arg1, arg2);
        }

        @Override
        public VariableResolver createVariable(String arg0, Object arg1)
        {
            sb.append("[RUN][FACTORY   ][createVariable(int,Object)]-->[" + arg0 + "],[" + arg1 + "]\n");
            return this.trueFactory.createVariable(arg0, arg1);
        }

        @Override
        public VariableResolver createVariable(String arg0, Object arg1, Class<?> arg2)
        {
            sb.append("[RUN][FACTORY   ][createIndexedVariable(int,String,Object,Class<?>)]-->[" + arg0 + "],[" + arg1 + "],[" + arg2.toString() + "]\n");
            return this.trueFactory.createVariable(arg0, arg1, arg2);
        }

        @Override
        public VariableResolver getIndexedVariableResolver(int arg0)
        {
            VariableResolver vr = this.trueFactory.getIndexedVariableResolver(arg0);
            if (vr == null)
            {
                sb.append("[RUN][FACTORY   ][getIndexedVariableResolver(int)]-->[" + arg0 + "] return =[null]\n");
            }
            else
            {
                sb.append("[RUN][FACTORY   ][getIndexedVariableResolver(int)]-->[" + arg0 + "] return =[" + vr.getName() + "],[" + vr.getClass() + "],[" + vr.getValue() + "]\n");
            }
            return vr;

        }

        @Override
        public Set<String> getKnownVariables()
        {
            sb.append("[RUN][FACTORY   ][getIndexedVariableResolver()]-->[@see Env VariableSet]\n");
            return this.trueFactory.getKnownVariables();
        }

        @Override
        public VariableResolverFactory getNextFactory()
        {
            sb.append("[RUN][FACTORY   ][getNextFactory()]-->[]\n");
            return this.trueFactory.getNextFactory();
        }

        @Override
        public VariableResolver getVariableResolver(String arg0)
        {
            VariableResolver vr = this.trueFactory.getVariableResolver(arg0);
            if (vr == null)
            {
                sb.append("[RUN][FACTORY   ][getVariableResolver(String)]-->[" + arg0 + "] return =[null]\n");
            }
            else
            {
                sb.append("[RUN][FACTORY   ][getVariableResolver(String)]-->[" + arg0 + "] return =[" + vr.getName() + "],[" + vr.getClass() + "],[" + vr.getValue() + "]\n");
            }
            return vr;
        }

        @Override
        public boolean isIndexedFactory()
        {
            sb.append("[RUN][FACTORY   ][isIndexedFactory()]-->[]\n");
            return this.trueFactory.isIndexedFactory();
        }

        @Override
        public boolean isResolveable(String arg0)
        {
            boolean v = this.trueFactory.isResolveable(arg0);
            sb.append("[RUN][FACTORY   ][isResolveable(String)]-->[" + arg0 + "] reutrn=[" + v + "]\n");
            return v;
        }

        @Override
        public boolean isTarget(String arg0)
        {
            boolean v = this.trueFactory.isTarget(arg0);
            sb.append("[RUN][FACTORY   ][isTarget(String)]-->[" + arg0 + "] reutrn=[" + v + "]\n");
            return v;
        }

        @Override
        public VariableResolver setIndexedVariableResolver(int arg0, VariableResolver arg1)
        {

            VariableResolver vr = this.trueFactory.setIndexedVariableResolver(arg0, arg1);
            if (vr == null)
            {
                sb.append("[RUN][FACTORY   ][setIndexedVariableResolver(int,VariableResolver)]-->[" + arg0 + "],[" + arg1 + "] return =[null]\n");
            }
            else
            {
                sb.append("[RUN][FACTORY   ][setIndexedVariableResolver(int,VariableResolver)]-->[" + arg0 + "],[" + arg1 + "] return =[" + vr.getName() + "],[" + vr.getClass() + "],[" + vr.getValue() + "]\n");
            }
            return vr;
        }

        @Override
        public VariableResolverFactory setNextFactory(VariableResolverFactory arg0)
        {
            sb.append("[RUN][FACTORY   ][setNextFactory(VariableResolverFactory)]-->[" + arg0 + "]\n");
            return this.trueFactory.setNextFactory(arg0);
        }

        @Override
        public void setTiltFlag(boolean arg0)
        {
            sb.append("[RUN][FACTORY   ][setTiltFlag(boolean)]-->[" + arg0 + "]\n");
            this.trueFactory.setTiltFlag(arg0);

        }

        @Override
        public boolean tiltFlag()
        {
            boolean v = this.trueFactory.tiltFlag();
            sb.append("[RUN][FACTORY   ][tiltFlag()]-->[ reutrn=[" + v + "]\n");
            return v;
        }

        @Override
        public int variableIndexOf(String arg0)
        {
            int v = this.trueFactory.variableIndexOf(arg0);
            sb.append("[RUN][FACTORY   ][variableIndexOf(String)]-->[" + arg0 + "] reutrn=[" + v + "]\n");
            return v;
        }

    }

    Logger logger = Logger.getLogger(this.getClass());

    public MVELDebugExecutor()
    {

    }

    public Object debugExecute(MVELExecutor tor, String expr) throws Exception
    {
        return debugExecute(tor, expr, null);
    }

    public Object debugExecute(MVELExecutor tor, String expr, Map<?, ?> tempvar) throws Exception
    {

        final StringBuilder sb = new StringBuilder();
        try
        {
            sb.append("\n").append("TITLE:            MVEL EXECUTOR  DETAIL").append("\n");

            writeEnv(tor, sb, tempvar);

            sb.append("SOURCE:==START==[\n");
            sb.append(genLineNumberedString(expr));
            sb.append("==END==]\n");

            String exprMacro = tor.getMiscCache().processMacro(expr);

            sb.append("SOURCE WITH MACRO REPLACED:==START==[\n");
            sb.append(genLineNumberedString(exprMacro));
            sb.append("==END==]\n");

            ExpressionCompiler compiler = new ExpressionCompiler(exprMacro);

            ParserContext context = tor.getMiscCache().getParserContext().createSubcontext();
            context.setDebugSymbols(true);
            context.setSourceFile("STRINGSOURCE");

            CompiledExpression compiled = compiler.compile(context);

            String exprDecompiled = org.mvel2.debug.DebugTools.decompile(compiled);

            sb.append("DECOMPILED SOURCE WITH MACRO REPLACED SOURCE:[\n");
            sb.append(exprDecompiled);
            sb.append("]\n");

            for (int i = 1; i < context.getLineCount() + 1; i++)
            {
                MVELRuntime.registerBreakpoint(context.getSourceFile(), i);
            }

            sb.append("DECOMPILED SOURCE WITH MACRO REPLACED SOURCE:[\n");
            Map<String, Class> minputs = context.getInputs();
            if (minputs != null)
            {
                for (Map.Entry<String, Class> e : minputs.entrySet())
                {
                    sb.append("   INPUT:      name=[" + e.getKey() + "],type=[" + e.getValue().getCanonicalName() + "]\n");
                }
            }
            Map<String, Class> mvars = context.getVariables();
            if (mvars != null)
            {
                for (Map.Entry<String, Class> e : mvars.entrySet())
                {
                    sb.append("VARIABLE:      name=[" + e.getKey() + "],type=[" + e.getValue().getCanonicalName() + "]\n");
                }
            }
            Map mfunc = context.getFunctions();
            if (mfunc != null)
            {
                for (Map.Entry e : mvars.entrySet())
                {
                    sb.append("FUNCTION:      name=[" + e.getKey() + "],value=[" + e.getValue() + "]\n");
                }
            }
            Map<String, Object> mimps = context.getImports();
            if (mimps != null)
            {
                for (Map.Entry e : mvars.entrySet())
                {
                    sb.append(" IMPORTS:      name=[" + e.getKey() + "],value=[" + e.getValue() + "]\n");
                }
            }
            sb.append("==END==]\n");

            Debugger testDebugger = new Debugger()
            {

                @Override
                public int onBreak(Frame frame)
                {
                    sb.append("[RUN][BREAKPOINT]Source:" + frame.getSourceName() + "; line:" + frame.getLineNumber() + "]\n");
                    return 0;
                }

            };

            Listener lget = new Listener()
            {
                @Override
                public void onEvent(Object context, String contextName, VariableResolverFactory variableFactory, Object value)
                {
                    sb.append("[RUN][EVENT  GET]OnEvent()-->[" + context + "],[" + contextName + "],[" + variableFactory + "],[" + value + "]\n");
                }
            };
            Listener lset = new Listener()
            {
                @Override
                public void onEvent(Object context, String contextName, VariableResolverFactory variableFactory, Object value)
                {
                    sb.append("[RUN][EVENT SET]OnEvent()-->[" + context + "],[" + contextName + "],[" + variableFactory + "],[" + value + "]\n");
                }
            };
            GlobalListenerFactory.registerGetListener(lget);
            GlobalListenerFactory.registerSetListener(lset);

            PropertyHandlerFactory.registerPropertyHandler(Map.class, new PropertyHandler()
            {
                @Override
                public Object getProperty(String name, Object contextObj, VariableResolverFactory variableFactory)
                {
                    sb.append("[RUN][GET       ]getProperty()-->[" + name + "],[" + contextObj + "],[" + variableFactory + "]\n");
                    return "gotcalled";
                }

                @Override
                public Object setProperty(String name, Object contextObj, VariableResolverFactory variableFactory, Object value)
                {
                    sb.append("[RUN][SET       ]setProperty()-->[" + name + "],[" + contextObj + "],[" + variableFactory + "],[" + value + "]\n");
                    return null;
                }
            });

            DEBUGVariableResolverFactory df = null;
            if (tempvar == null)
            {
                df = new DEBUGVariableResolverFactory(sb, tor.getVariableResolverFactory());
            }
            else
            {
                VariableResolverFactory tempFactory = new ReadOnlyCachingMapVariableResolverFactory(tempvar);
                tempFactory.setNextFactory(tor.getVariableResolverFactory());
                df = new DEBUGVariableResolverFactory(sb, tempFactory);
            }
            MVELRuntime.setThreadDebugger(testDebugger);
            Object o = MVEL.executeDebugger(compiled, tor.getMiscCache().getParserContext(), df);
            return o;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw (e);
            // sb.append("[RUN][EXCEPTION ]" + ExceptionUtils.getExceptionStack(e));
            // return e;
        }
        finally
        {
        	if(logger.isDebugEnabled())
            logger.debug(sb.toString());
            MVELRuntime.clearAllBreakpoints();
            MVELRuntime.resetDebugger();
            GlobalListenerFactory.disposeAll();
            PropertyHandler m = null;
            PropertyHandler v = null;
            if (PropertyHandlerFactory.hasNullMethodHandler())
            {
                m = PropertyHandlerFactory.getNullMethodHandler();
            }
            if (PropertyHandlerFactory.hasNullPropertyHandler())
            {
                v = PropertyHandlerFactory.getNullPropertyHandler();
            }
            PropertyHandlerFactory.disposeAll();
            if (m != null)
                PropertyHandlerFactory.setNullMethodHandler(m);
            if (v != null)
                PropertyHandlerFactory.setNullPropertyHandler(v);
        }
    }

    private String genLineNumberedString(String es)
    {
        String[] ss = StringUtils.splitByWholeSeparatorPreserveAllTokens(es, "\n");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ss.length; i++)
        {
            sb.append(StringUtils.leftPad(Integer.toString(i + 1), 5, " ")).append(":  ").append(ss[i]).append("\n");
        }
        return sb.toString();
    }

    public void writeEnv(MVELExecutor tor, StringBuilder sb, Map<?, ?> tempvar)
    {
        sb.append("\nENV ==START==[\n");
        ParserContext ctx = tor.getMiscCache().getParserContext();

        Set<String> pkgimports = ctx.getParserConfiguration().getPackageImports();
        if (pkgimports != null)
        {
            for (String s : pkgimports)
            {
                sb.append("\tPACKAGE IMPORT: [" + s + "]\n");
            }
        }
        Map<String, Object> imports = ctx.getImports();
        if (imports != null)
        {
            for (Map.Entry<String, Object> e : imports.entrySet())
            {
                sb.append("\t        IMPORT: [" + e.getKey() + "]=[" + e.getValue().toString() + "]\n");
            }
        }
        Map<String, Macro> macros = tor.getMiscCache().getAllMacros();
        if (macros != null)
        {
            for (Map.Entry<String, Macro> e : macros.entrySet())
            {
                sb.append("\t         MACRO: [" + e.getKey() + "]=[" + e.getValue().doMacro() + "]\n");
            }
        }

        VariableResolverFactory vf = tor.getMiscCache().getFunctionFactory();
        BaseVariableResolverFactory vrf = (BaseVariableResolverFactory) vf;
        Map<String, VariableResolver> mfuncs = vrf.getVariableResolvers();
        if (mfuncs != null)
        {
            for (Map.Entry<String, VariableResolver> e : mfuncs.entrySet())
            {
                Function func = ((Function) e.getValue().getValue());
                String funcs = e.getKey() + "(";
                if (func.hasParameters())
                {
                    String[] ss = func.getParameters();
                    for (int i = 0; i < ss.length; i++)
                    {
                        funcs = funcs + ss[i];
                        if (i != ss.length - 1)
                        {
                            funcs = funcs + ",";
                        }
                    }
                }
                funcs = funcs + ")";
                sb.append("\t      FUNCTION: [" + funcs + "]\n");
            }
        }
        VariableResolverFactory factory = tor.getVariableResolverFactory();
        Map<VariableResolverFactory, Boolean> loopDetector = new HashMap<VariableResolverFactory, Boolean>();
        while (factory != null)
        {
            if (loopDetector.containsKey(factory))
                break;
            if (factory == vf)
            {
                sb.append("\t       FACTORY: [" + factory.toString() + "]\n");
                sb.append("\t             With Function Definition Above \n");
            }
            else
            {
                sb.append("\t       FACTORY: [" + factory.toString() + "]\n");
                sb.append("\t             With Variables[\n");
                Set<String> allvars = factory.getKnownVariables();
                for (String svars : allvars)
                    sb.append("\t             [" + svars + "]\n");
                sb.append("\t                           ]\n");
            }
            loopDetector.put(factory, new Boolean(true));
            factory = factory.getNextFactory();
        }

        if (tempvar != null)
        {
            sb.append("\t       FACTORY: [" + "Temporary Variable Factory" + "]\n");
            sb.append("\t                 With Temporary Variable: " + tempvar.keySet().toString() + "\n");
        }
    }
}
