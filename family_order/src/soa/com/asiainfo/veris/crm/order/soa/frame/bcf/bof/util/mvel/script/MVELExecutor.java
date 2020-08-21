
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;
import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.cache.localcache.interfaces.IReadWriteCache;

public class MVELExecutor
{

    static
    {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

        OptimizerFactory.setDefaultOptimizer(OptimizerFactory.SAFE_REFLECTIVE);

    }

    public static String apply(String template, Object... args) throws Exception
    {
        MVELExecutor exector = new MVELExecutor();
        exector.prepare(args);
        return exector.applyTemplate(template);
    }

    public static Object execute(String script, Object... args) throws Exception
    {
        MVELExecutor exector = new MVELExecutor();
        exector.prepare(args);
        return exector.execScript(script);
    }

    public static boolean getBooleanValue(Object o)
    {
        if (o == null)
            return false;
        if (o instanceof Boolean)
        {
            return ((Boolean) o).booleanValue();
        }
        else if (o instanceof String)
        {
            return Boolean.parseBoolean(o.toString().trim());
        }
        return false;
    }

    private VariableResolverFactory thisFactory = null;

    private MVELMiscCache miscCache = new MVELMiscCache();

    private IReadWriteCache stmtCache = null;

    private IReadWriteCache tpltCache = null;

    private boolean isCacheStmt = false;

    private boolean isCacheTplt = false;

    public MVELExecutor()
    {
        this(null, MVELCompiledCache.getStatementCache(), MVELCompiledCache.getTemplateCache());
    }

    public MVELExecutor(Map<String, String> conf)
    {
        this(conf, MVELCompiledCache.getStatementCache(), MVELCompiledCache.getTemplateCache());
    }

    public MVELExecutor(Map<String, String> conf, IReadWriteCache stmtCache, IReadWriteCache tpltCache)
    {
        this.stmtCache = stmtCache;
        this.tpltCache = tpltCache;

        if (conf != null)
        {
            String v = conf.get("NOT_CACHE");
            if (MVELExecutor.getBooleanValue(v))
            {
                this.stmtCache = null;
                this.tpltCache = null;
            }
            boolean vv = false;
            boolean vm = false;
            v = conf.get("NULL_SAFE");
            if (v != null)
            {
                boolean vt = getBooleanValue(v);
                vv = vt;
                vm = vt;
            }
            v = conf.get("NULL_VARIABLE");
            if (v != null)
            {
                boolean vt = getBooleanValue(v);
                vv = vt;
            }
            v = conf.get("NULL_METHOD");
            if (v != null)
            {
                boolean vt = getBooleanValue(v);
                vm = vt;
            }
            if (vv)
            {
                PropertyHandlerFactory.setNullPropertyHandler(new PropertyHandler()
                {

                    @Override
                    public Object getProperty(String arg0, Object arg1, VariableResolverFactory arg2)
                    {
                        return "";
                    }

                    @Override
                    public Object setProperty(String arg0, Object arg1, VariableResolverFactory arg2, Object arg3)
                    {
                        return null;
                    }
                });
            }
            if (vm)
            {
                PropertyHandlerFactory.setNullMethodHandler(new PropertyHandler()
                {

                    @Override
                    public Object getProperty(String arg0, Object arg1, VariableResolverFactory arg2)
                    {
                        return null;
                    }

                    @Override
                    public Object setProperty(String arg0, Object arg1, VariableResolverFactory arg2, Object arg3)
                    {
                        return null;
                    }
                });
            }

        }

        if (this.stmtCache != null)
            this.isCacheStmt = true;
        if (this.tpltCache != null)
            this.isCacheTplt = true;

    }

    public String applyTemplate(CompiledTemplate ct) throws MVELException
    {
        return applyTemplate(ct, null);
    }

    public String applyTemplate(CompiledTemplate ct, Map<?, ?> tempvar) throws MVELException
    {
        String p = "";
        if (tempvar == null)
        {
            p = (String) TemplateRuntime.execute(ct, miscCache.getParserContext(), this.thisFactory);
        }
        else
        {
            VariableResolverFactory tempFactory = new ReadOnlyCachingMapVariableResolverFactory(tempvar);
            tempFactory.setNextFactory(this.thisFactory);
            p = (String) TemplateRuntime.execute(ct, miscCache.getParserContext(), tempFactory);
        }
        return p;
    }

    public String applyTemplate(String template) throws Exception
    {
        return applyTemplate(template, null, this.isCacheTplt);
    }

    public String applyTemplate(String template, Map<?, ?> tempvar) throws Exception
    {
        return applyTemplate(template, tempvar, this.isCacheTplt);
    }

    public String applyTemplate(String template, Map<?, ?> tempvar, boolean isCache) throws Exception
    {
        if (isCache && tpltCache != null)
        {
            CompiledTemplate es = (CompiledTemplate) tpltCache.get(template);
            if (es != null)
            {
                return applyTemplate(es, tempvar);
            }
        }

        Object o = miscCache.compileTemplateWhenNecessary(template);
        if (o instanceof String)
        {
            return (String) TemplateRuntime.eval((String) o, miscCache.getParserContext(), this.thisFactory);
        }
        else
        {
            if (isCache && tpltCache != null)
            {
                tpltCache.put(template, o);
            }
            return applyTemplate((CompiledTemplate) o, tempvar);
        }
    }

    public Object execScript(ExecutableStatement sz)
    {
        return execScript(sz, null);
    }

    public Object execScript(ExecutableStatement sz, Map<?, ?> tempVars)
    {
        Object o = null;
        if (tempVars == null)
        {
            o = MVEL.executeExpression(sz, miscCache.getParserContext(), this.thisFactory);
        }
        else
        {
            VariableResolverFactory tempFactory = new ReadOnlyCachingMapVariableResolverFactory(tempVars);
            tempFactory.setNextFactory(this.thisFactory);
            o = MVEL.executeExpression(sz, miscCache.getParserContext(), tempFactory);
        }
        return o;
    }

    public Object execScript(String script) throws Exception
    {
        return execScript(script, null, this.isCacheStmt);
    }
    
    public Object execScript(String script, Map<?, ?> tempVars) throws Exception
    {
        return execScript(script, tempVars, this.isCacheStmt);
    }

    public Object execScript(String script, Map<?, ?> tempVars, boolean isCache) throws Exception
    {
        if (isCache && stmtCache != null)
        {
            ExecutableStatement es = (ExecutableStatement) stmtCache.get(script);
            if (es != null)
            {
                return execScript(es, tempVars);
            }
        }

        Object o = miscCache.compileWhenNecessary(script);
        if (o instanceof String)
        {
            return MVEL.eval((String) o, miscCache.getParserContext(), this.thisFactory);
        }
        else
        {
            if (isCache && stmtCache != null)
            {
                stmtCache.put(script, o);
            }
            return execScript((ExecutableStatement) o, tempVars);
        }
    }

    public MVELMiscCache getMiscCache()
    {
        return miscCache;
    }

    public VariableResolverFactory getVariableResolverFactory()
    {
        return this.thisFactory;
    }

    @SuppressWarnings("unchecked")
    public void prepare(Object... args)
    {
        Map<String, Object> margs = new HashMap<String, Object>();
        VariableResolverFactory factortyFirst = new CachingMapVariableResolverFactory(margs);
        VariableResolverFactory factory = factortyFirst;

        int i = 0;
        for (Object o : args)
        {
            margs.put("$" + i, o);
            margs.put("arg" + i, o);
            margs.put("参数" + i, o);

            i++;
            if (o instanceof VariableResolverFactory)
            {
                factory.setNextFactory((VariableResolverFactory) o);
                factory = (VariableResolverFactory) o;
            }
            else if (o instanceof IMVELScriptCompatible)
            {
                VariableResolverFactory factorynext = new MVELVariableResolverFactory((IMVELScriptCompatible) o);
                factory.setNextFactory(factorynext);
                factory = factorynext;
            }
            else if (o instanceof Map)
            {
                VariableResolverFactory factorynext = new CachingMapVariableResolverFactory((Map) o);
                factory.setNextFactory(factorynext);
                factory = factorynext;
            }

        }
        factory.setNextFactory(this.miscCache.getFunctionFactory());

        thisFactory = factortyFirst;

    }

    public void setMiscCache(MVELMiscCache miscCache)
    {
        this.miscCache = miscCache;
    }

	public Object execTemplate(String template) throws Exception {
		return execTemplate(template, null);
	}

	public Object execTemplate(String template, Map<?, ?> tempvar) throws Exception {
		return execTemplate(template, tempvar, this.isCacheTplt);
	}

	public Object execTemplate(String template, Map<?, ?> tempvar, boolean isCache) throws Exception {
		if (isCache && tpltCache != null) {
			CompiledTemplate es = (CompiledTemplate) tpltCache.get(template);
			if (es != null) {
				return execTemplate(es, tempvar);
			}
		}

		Object o = miscCache.compileTemplateWhenNecessary(template);
		if (o instanceof String) {
			return TemplateRuntime.eval((String) o, miscCache.getParserContext(), this.thisFactory);
		} else {
			if (isCache && tpltCache != null) {
				tpltCache.put(template, o);
			}
			return execTemplate((CompiledTemplate) o, tempvar);
		}
	}
	
	public Object execTemplate(CompiledTemplate ct, Map<?, ?> tempvar) throws MVELException {
		Object p = null;
		if (tempvar == null) {
			p = TemplateRuntime.execute(ct, miscCache.getParserContext(), this.thisFactory);
		} else {
			VariableResolverFactory tempFactory = new ReadOnlyCachingMapVariableResolverFactory(tempvar);
			tempFactory.setNextFactory(this.thisFactory);
			p = TemplateRuntime.execute(ct, miscCache.getParserContext(), tempFactory);
		}
		return p;
	}
}