
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
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

import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class MVELMiscCache extends AbstractReadOnlyCache
{
	private static final transient Logger log = Logger.getLogger(MVELMiscCache.class);

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

    @Override
    public Map<String, Object> loadData() throws Exception
    {
    	boolean logFlag = false;
    	if(ProvinceUtil.isProvince(ProvinceUtil.SHXI)){
    		logFlag = true;
    	}
        DBConnection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        String sql = "SELECT * FROM td_s_crm_mvelmisc where EFF_STATE='1' and  sysdate between eff_start_date and eff_end_date";
        try
        {
            conn = new DBConnection("cen1", false, false);
            statement = conn.prepareStatement(sql);
            result = statement.executeQuery();
            while (result.next())
            {
                try
                {
                    processResultSet(result);
                }
                catch (Exception e)
                {
                	if(logFlag){
                		//log.info("("===============MVELMiscCacheSHXI==liujian==jiexi=Exception==111===============" + e.getCause());
    					log.error(e.getMessage(), e);
                	}else{
                		e.printStackTrace();
                	}
                }
            }
        }
        catch (Exception e)
        {
        	if(logFlag){
	        	//log.info("("===============MVELMiscCacheSHXI==liujian==jiexi=Exception==111===============" + e.getCause());
				log.error(e.getMessage(), e);
	        }else{
	    		e.printStackTrace();
	    	}
        }
        finally
        {
            if (result != null)
            {
                try
                {
                    result.close();
                }
                catch (SQLException e)
                {
                	if(logFlag)
                		log.error(e.getMessage(), e);
                	else
                		e.printStackTrace();
                }
            }
            if (statement != null)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                	if(logFlag)
                		log.error(e.getMessage(), e);
                	else
                		e.printStackTrace();
                }
            }
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException e)
                {
                	if(logFlag)
                		log.error(e.getMessage(), e);
                	else
                		e.printStackTrace();
                }
            }
        }
        Map<String, Object> cache = new HashMap<String, Object>();
        cache.put("CRM_MVEL_CACHE", this);
        return cache;
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

    private void processResultSet(ResultSet rs) throws Exception
    {
        String miscName = rs.getString("MISC_NAME");
        String miscType = rs.getString("MISC_TYPE");
        String s1 = rs.getString("SCRIPT_1");
        String s2 = rs.getString("SCRIPT_2");
        String s3 = rs.getString("SCRIPT_3");
        String s4 = rs.getString("SCRIPT_4");
        String s5 = rs.getString("SCRIPT_5");
        String s6 = rs.getString("SCRIPT_6");
        String s7 = rs.getString("SCRIPT_7");
        String s8 = rs.getString("SCRIPT_8");
        String s9 = rs.getString("SCRIPT_9");
        String s10 = rs.getString("SCRIPT_10");
        if (s1 == null)
            s1 = "";
        if (s2 == null)
            s2 = "";
        if (s3 == null)
            s3 = "";
        if (s4 == null)
            s4 = "";
        if (s5 == null)
            s5 = "";
        if (s6 == null)
            s6 = "";
        if (s7 == null)
            s7 = "";
        if (s8 == null)
            s8 = "";
        if (s9 == null)
            s9 = "";
        if (s10 == null)
            s10 = "";

        String miscScript = s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + s9 + s10;
        this.addByType(miscType, miscName, miscScript);
    }
}
