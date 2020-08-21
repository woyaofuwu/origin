
package com.asiainfo.veris.crm.order.soa.frame.bre.script.rule.common;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class SplCheckByRegular extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(SplCheckByRegular.class);

    /**
     * 判断Hd的规则前面是否有针对手机号码的特殊判断
     * 0123$|1234$|2345$|3456$|4567$|5678$|6789$|9876$|8765$|7654$|6543$|5432$|4321$|3210$|\\d{7}
     * (00|11|22|33|44|55|66|77
     * |88|99)(66|77|88|99)|\\d{6}(00|11|22|33|44|55)(\\d)\\1|\\d{6}(000|111|222|333|444|555|666|
     * 777|888|999)(00|11|22|33|44|55)
     * 
     * @author gaoyuan3@asiainfo-linkage.com @ 2012-8-26
     * @param strText
     * @return
     */
    public static boolean isCycleJudge(String strText) throws Exception
    {
        Pattern pattern = Pattern.compile("^\\d(.*)");
        Matcher matcher = pattern.matcher(strText);

        return matcher.matches();
    }

    /**
     * 简单正则表达式匹配 覆盖 SUPER_LIMIT 中 regexp 函数使用
     * 
     * @author GaoYuan
     * @param strBunch
     *            String 正则表达式规则字符串
     * @param strText
     *            String 需要匹配的字符串
     * @return boolean
     * @throws Exception
     */
    public static boolean matcherText(String strBunch, String strText) throws Exception
    {
        if (strBunch.equals("^(\\d{15}|\\d{17}[\\dxyXY])$") || "(4\\d|\\d4)[^4][^4]$".equals(strBunch))
        {
            return matcherTextSpec(strBunch, strText);
        }
        // else if (strBunch.indexOf("\\d") > -1)
        // {
        // return matcherTextSpecHd(strBunch, strText);
        // }
        else
        {
            return matcherTextSpecNd(strBunch, strText);
        }
    }

    /**
     * 针对身份证号码正则式的特殊判断 ^(\\d{15}|\\d{17}[\\dxyXY])$
     * 
     * @author gaoyuan3@asiainfo-linkage.com @ 2012-8-26
     * @param strBunch
     * @param strText
     * @return
     * @throws Exception
     */
    public static boolean matcherTextSpec(String strBunch, String strText) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("-------------------------------matcherTextSpec");

        Pattern pattern = null;
        Matcher matcher = null;
        StringBuilder strMatcher = new StringBuilder();
        String[] strArray;

        pattern = Pattern.compile(strBunch);

        matcher = pattern.matcher(strText);

        if (matcher.matches())
        {
            return true;
        }

        return false;
    }

    /**
     * 针对有\d的判断手机号码数字的特殊判断
     * \\d{5}(000|111|222|333|444|555|666|777|888|999)(000|111|222|333|444|555)|\\d{5}(66|77|88|99)(00
     * |11|22|33|44|55|66|
     * 77|88|99)(66|77|88|99)|\\d{5}(\\d)\\1(\\d)\\1(\\d)\\1|\\d{6}(00|11|22|33|44|55|66|77|88|99)(666|777|888|999)
     * 
     * @author gaoyuan3@asiainfo-linkage.com @ 2012-8-26
     * @param strBunch
     * @param strText
     * @return
     * @throws Exception
     */
    public static boolean matcherTextSpecHd(String strBunch, String strText) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("-------------------------------matcherTextSpecHd");

        Pattern pattern = null;
        Matcher matcher = null;
        StringBuilder strMatcher = new StringBuilder();
        String[] strArray;

        strArray = strBunch.split("\\|\\\\d");

        for (int i = 0; i < strArray.length; i++)
        {
            strMatcher.delete(0, strMatcher.length());

            strMatcher.append(strArray[i]);

            if (isCycleJudge(strMatcher.toString()) && strMatcher.indexOf("\\d") < 0 && !strBunch.startsWith("\\d"))
            {
                if (matcherTextSpecNd(strMatcher.toString(), strText))
                {
                    return true;
                }
            }
            else if (strMatcher.toString().startsWith("^"))
            {

            }
            else if (!strMatcher.toString().startsWith("\\d"))
            {
                strMatcher = new StringBuilder("\\d").append(strMatcher);
            }

            if (logger.isDebugEnabled())
                logger.debug(strMatcher);

            pattern = Pattern.compile(strMatcher.toString());

            matcher = pattern.matcher(strText);

            if (matcher.matches())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 针对没有\d的判断手机号码数字的特殊判断 0123$|1234$|2345$|3456$|4567$|5678$|6789$|9876$|8765$|7654$|6543$|5432$|4321$|3210$
     * 
     * @author gaoyuan3@asiainfo-linkage.com @ 2012-8-26
     * @param strBunch
     * @param strText
     * @return
     * @throws Exception
     */
    public static boolean matcherTextSpecNd(String strBunch, String strText) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("-------------------------------matcherTextSpecNd");

        Pattern pattern = null;
        Matcher matcher = null;
        StringBuilder strMatcher = new StringBuilder();
        String[] strArray;
        String strRegex = "";

        strArray = strSplit(strBunch);

        for (int i = 0; i < strArray.length; i++)
        {
            strRegex = strArray[i].toString();

            if (strRegex.startsWith("^") && strRegex.endsWith("$"))
            {
                strMatcher.delete(0, strMatcher.length());
                strMatcher.append(strRegex);
            }
            else if (strRegex.startsWith("^"))
            {
                strMatcher.delete(0, strMatcher.length());
                strMatcher.append(strArray[i]).append("(.*)");
            }
            else if (strRegex.endsWith("$"))
            {
                strMatcher.delete(0, strMatcher.length());
                strMatcher.append("(.*)").append(strArray[i]);
            }
            else
            {
                strMatcher.delete(0, strMatcher.length());
                strMatcher.append(strArray[i]);
            }

            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug("SplCheckByRegular:["+strText+"]["+strMatcher.toString()+"]");

            pattern = Pattern.compile(strMatcher.toString());

            matcher = pattern.matcher(strText);

            if (matcher.matches())
            {
                return true;
            }
        }

        return false;
    }

    public static String[] strSplit(String str) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("SplCheckByRegular.strSplit into = [" + str + "]");
        }

        /* del by gaoyuan @ 2013-12-27 13:50 desc :: 直接用正则表达式去分割字符串
        String[] strM2;
        
        
        if (str.indexOf("$|") > 0)
        {
            String strTmp = str;
            ArrayList<String> list = new ArrayList<String>();
            int iStart = 0;
            int idx = 0;

            while (strTmp.indexOf("$|") > 0)
            {
                iStart = strTmp.indexOf("$|") + 1;
                if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                {
                    logger.debug("[iStart = " + iStart + "], strM2[" + idx + "] = [" + strTmp.substring(0, iStart) + "]");
                }
                list.add(strTmp.substring(0, iStart));
                idx++;

                strTmp = strTmp.substring(iStart + 1);

            }

            if (strTmp.length() > 0)
            {
                list.add(strTmp.substring(0));
                idx++;
                if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                {
                    logger.debug("at last,     strM2[" + idx + "] = [" + strTmp.substring(0) + "]");
                }
            }

            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            {
                logger.debug("--------------MMMMMMMMMMMMMMMM---------- ArrayList 构造完成！");
            }

            strM2 = new String[list.size()];
            for (int i = 0; i < list.size(); i++)
            {
                strM2[i] = list.get(i);
            }
        }
        else
        {
            strM2 = str.split("\\|");
        }
		*/
        
        String strPattern = "(?<!\\([^\\)]{1,100})\\|";
        String[] strM2 = str.split(strPattern);
        
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("SplCheckByRegular.strSplit exit = [" + strM2 + "]");
        }

        return strM2;
    }

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 tbCheckByRegular() >>>>>>>>>>>>>>>>>>");

        String strText1 = ruleParam.getString(databus, "BRE_PARAM_VALUE1");
        String strText2 = ruleParam.getString(databus, "BRE_PARAM_VALUE2");

        if (strText1 == null || "".equals(strText1) || strText1 == null || "".equals(strText1))
        {
            return false;
        }

        boolean bResult = this.matcherText(strText2, strText1);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 tbCheckByRegular() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
