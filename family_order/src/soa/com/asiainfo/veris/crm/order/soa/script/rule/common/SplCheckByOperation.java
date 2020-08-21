
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class SplCheckByOperation extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(SplCheckByOperation.class);

    public static void main(String[] args)
    {
        SplCheckByOperation a = new SplCheckByOperation();
    }

    public boolean isNumeric(String str)
    {
        if (!"null".equalsIgnoreCase(str) && StringUtils.isNotBlank(str) && str.length() <= 8)
        {
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(str).matches();
        }

        return false;
    }

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 tbCheckByOperation() >>>>>>>>>>>>>>>>>>" + ruleParam);

        String strSymbol = ruleParam.getString(databus, "BRE_PARAM_VALUE3");
        String strText1 = ruleParam.getString(databus, "BRE_PARAM_VALUE1");
        String strText2 = ruleParam.getString(databus, "BRE_PARAM_VALUE2");

        if (strText1 == null)
            strText1 = "";
        if (strText2 == null)
            strText2 = "";

        boolean bResult = this.stringCompare(databus, strText1, strSymbol, strText2);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 tbCheckByOperation() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

    /**
     * 字符串比较函数
     * 
     * @author GaoYuan
     * @param strText1
     *            String 比较字符串1
     * @param strSymbol
     *            Stirng 比较符号(==, != >=, <=, >, >)
     * @param strText2
     *            String 比较字符串2
     * @return boolean
     * @throws Exception
     *             "2008-10-20 23:54:32" < "2009-01-01" return true;
     */
    public boolean stringCompare(IData databus, String strText1, String strSymbol, String strText2) throws Exception
    {
        if (strText1.equalsIgnoreCase("SYSDATE"))
        {
            strText1 = databus.getString("CUR_DATE");
        }
        if (strText2.equalsIgnoreCase("SYSDATE"))
        {
            strText2 = databus.getString("CUR_DATE");
        }

        int i = 0;

        if (this.isNumeric(strText1) && this.isNumeric(strText2))
        {
            Integer iText1 = Integer.parseInt(strText1);
            Integer iText2 = Integer.parseInt(strText2);
            i = iText1.compareTo(iText2);
        }
        else
        {
            i = strText1.compareTo(strText2);
        }

        if (strSymbol.equals("=="))
        {
            if (i == 0)
            {
                return true;
            }
        }
        else if (strSymbol.equals("!="))
        {
            if (i != 0)
            {
                return true;
            }
        }
        else if (strSymbol.equals(">="))
        {
            if (i >= 0)
            {
                return true;
            }
        }
        else if (strSymbol.equals("<="))
        {
            if (i <= 0)
            {
                return true;
            }
        }
        else if (strSymbol.equals(">"))
        {
            if (i > 0)
            {
                return true;
            }
        }
        else if (strSymbol.equals("<"))
        {
            if (i < 0)
            {
                return true;
            }
        }

        return false;
    }
}
