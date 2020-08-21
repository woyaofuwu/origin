
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;

import com.ailk.org.apache.commons.lang3.StringUtils;

public class MVELCacheDataSHXI extends MVELMiscCache
{

    private Map<String, Macro> vars = new HashMap<String, Macro>();

    private ParserContext pctx = new ParserContext();

    private VariableResolverFactory functionFactory = new CachingMapVariableResolverFactory(new HashMap());
    
	public void addByType(String miscType, String miscName, String miscScript)
    {
        if (miscType.equalsIgnoreCase("MACRO") || miscType.equals("M"))
        {
            addMacro(miscName, miscScript);
        }
        else if (miscType.equalsIgnoreCase("FUNCTION") || miscType.equals("F"))
        {
            addFunction(miscScript);
        }
        else if (miscType.equalsIgnoreCase("IMPORT") || miscType.equals("I"))
        {
            addImport(miscName, miscScript);
        }
        else if (miscType.equalsIgnoreCase("PACKAGE") || miscType.equals("P"))
        {
            addPackageImport(miscScript);
        }
    }

    public void addFunction(String funcDef)
    {
        ExpressionCompiler compiler = new ExpressionCompiler(funcDef);
        CompiledExpression es = compiler.compile();
        MVEL.executeExpression(es, pctx, this.functionFactory);
    }

    public void addImport(String c, Class clazz)
    {
        pctx.addImport(c, clazz);
    }

    public void addImport(String c, String clazz)
    {
        try
        {
            Class mc = Class.forName(clazz);
            addImport(c, mc);
        }
        catch (ClassNotFoundException ce)
        {

        }
    }

    public void addMacro(String name, Macro m)
    {
        this.vars.put(name, m);
    }

    public void addMacro(String macro, final String s)
    {
        this.vars.put(macro, new Macro()
        {

            @Override
            public String doMacro()
            {
                return s;
            }
        });
    }

    public void addPackageImport(String imports)
    {
        String[] importss = StringUtils.split(imports, ";");
        if (importss != null)
        {
            for (String si : importss)
            {
                String sii = si.trim();
                sii = StringUtils.replace(sii, "\r", "");
                sii = StringUtils.replace(sii, "\n", "");
                if (sii.startsWith("import "))
                    sii = StringUtils.replaceOnce(sii, "import ", "");
                sii = si.trim();
                pctx.addPackageImport(sii);
            }
        }
    }

    public void clearFunction()
    {
        this.functionFactory = new CachingMapVariableResolverFactory(new HashMap());
    }

    public void clearMacro()
    {
        this.vars.clear();
    }

    public void clearParserContext()
    {
        this.pctx = new ParserContext();
    }

    public ExecutableStatement compile(String script2)
    {
        String script = processMacro(script2);
        ExecutableStatement es = (ExecutableStatement) MVEL.compileExpression(script, pctx);
        return es;

    }

    public CompiledTemplate compileTemplate(String template2)
    {
        String template = processMacro(template2);
        CompiledTemplate ct = TemplateCompiler.compileTemplate(template, pctx);
        return ct;
    }

    public Object compileTemplateWhenNecessary(String script)
    {
        if (script.startsWith("@comment{NO_CACHE}"))
            return script;
        else
            return compileTemplate(script);
    }

    public Object compileWhenNecessary(String script)
    {
        if (script.startsWith("/*NO_CACHE*/"))
            return script;
        else
            return compile(script);
    }

    public Map<String, Macro> getAllMacros()
    {
        return this.vars;
    }

    public VariableResolverFactory getFunctionFactory()
    {
        return this.functionFactory;
    }

    public ParserContext getParserContext()
    {
        return this.pctx;
    }

    public String processMacro(String s)
    {
        if (this.vars.size() == 0)
            return s;
        MacroProcessor macroProcessor = new MacroProcessor();
        macroProcessor.setMacros(vars);
        String v = macroProcessor.parse(s);
        return v;
    }

}
