
package com.asiainfo.veris.crm.order.soa.frame.bre.tools;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;

/**
 * 提示信息处理
 * 
 * @author Administrator
 */

public final class BreTipsHelp extends BreBase
{
    private static final Logger logger = Logger.getLogger(BreTipsHelp.class);

    /**
     * 普通错误信息加入
     */
    public static void addNorTipsInfo(IData databus, int iTipsType, int iErrorCode, String strTipsInfo) throws Exception
    {
        /* 处理错误信息 */
        addNorTipsInfo(databus, iTipsType, String.valueOf(iErrorCode), strTipsInfo);
    }

    /**
     * 普通错误信息加入
     */
    public static void addNorTipsInfo(IData databus, int iTipsType, String strErrorCode, String strTipsInfo) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("------报错报错了  addNorTipsInfo！－－－－－－－－－－" + " RULE_INFO " + strTipsInfo);

        /* 处理错误信息 */
        IDataset listErrorInfo = databus.getDataset("RULE_INFO");

        if (listErrorInfo == null)
        {
            listErrorInfo = new DatasetList();
            databus.put("RULE_INFO", listErrorInfo);
        }

        IData errorData = new DataMap();

        errorData.put("TIPS_TYPE", iTipsType);
        errorData.put("TIPS_CODE", strErrorCode);
        errorData.put("TIPS_INFO", strTipsInfo);

        listErrorInfo.add(errorData);

        if (iTipsType == BreFactory.TIPS_TYPE_FORCE_EXIT)
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW FORCE_EXIT set");

            databus.put("FORCE_EXIT", "FORCE_EXIT");
        }
    }

    /**
     * 处理报错信息， 1、加跳转链接 2、CodeToName的替换 ×
     * 
     * @author gaoyuan3@asiainfo-linkage.com @ 2012-1-16
     * @param pd
     * @param databus
     * @param strTipsInfo
     * @throws Exception
     */
    public static void addTipsInfoByBus(IData databus, String strRuleBizId, int iTipsType, String strTipsInfo) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("------报错报错了！－－－－－－－－－－" + strRuleBizId + " RULE_INFO " + strTipsInfo);

        if (strTipsInfo.indexOf("$[GetNameByData") > 0 && strTipsInfo.indexOf(")]") > 0)
        {
            strTipsInfo = GetNameByData(databus, strTipsInfo);
        }
        else if (strTipsInfo.indexOf("$[CodeToName") > 0 && strTipsInfo.indexOf(")]") > 0)
        {
            strTipsInfo = getNameByCode(databus, strTipsInfo);
        }
        else if (strTipsInfo.indexOf("$[FLD") > 0 && strTipsInfo.indexOf(")]") > 0)
        {
            strTipsInfo = getValueByFLD(databus, strTipsInfo);
        }

        /* 处理错误信息 */
        IDataset listErrorInfo = databus.getDataset("RULE_INFO");

        if (listErrorInfo == null)
        {
            listErrorInfo = new DatasetList();
            databus.put("RULE_INFO", listErrorInfo);
        }

        IData errorData = new DataMap();
        errorData.put("TIPS_TYPE", iTipsType);
        errorData.put("TIPS_CODE", strRuleBizId);
        errorData.put("TIPS_INFO", replaceErrorInfo(databus, strTipsInfo));
        listErrorInfo.add(errorData);

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("------报错报错了！－－－－－－－－－－" + strRuleBizId + " RULE_INFO " + listErrorInfo);

        /* 如果错误类型配置的是"4", 需要强制退出 */
        if (iTipsType == BreFactory.TIPS_TYPE_FORCE_EXIT)
        {
            databus.put("FORCE_EXIT", "FORCE_EXIT");
        }
    }

    /**
     * 字符串分离
     * 
     * @author GaoYuan
     * @param databus
     *            IData
     * @param strParam
     *            String
     */
    public static String decodeParam(IData databus, String strParam) throws Exception
    {
        StringBuilder strError = null;

        // if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        // logger.debug(" decodeParam databus ==  " + databus);

        String strResult = "";

        if (strParam.startsWith("%") && strParam.endsWith("!"))
        {
            strParam = strParam.substring(1, strParam.length() - 1);

            if (strParam.equalsIgnoreCase("SYSDATE"))
            {
                strResult = databus.getString("CUR_DATE");
            }
            else if (databus.containsKey(strParam))
            {
                strResult = databus.getString(strParam);

                if (strResult != null && !"".equals(strResult) && strParam.indexOf("FEE") > -1 && StringUtils.isNumeric(strResult))
                {
                    strResult = String.valueOf(Double.parseDouble(strResult) / 100);
                }
            }
            else
            {
                strError = new StringBuilder("业务规则限制判断:获取参数域值出错！无法获取参数域值[").append(strParam).append("]");
                // THROW_EXCEPTION(strError.toString());
                logger.error(strError.toString());
                strResult = "";
            }
        }
        else
        {
            strResult = strParam;
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" decodeParam  strResult  ===   " + strResult);

        return strResult;
    }

    public static IData formatInfo(IData result) throws Exception
    {
        IDataset listTips = new DatasetList();
        IDataset listError = new DatasetList();
        IDataset listChoice = new DatasetList();

        IDataset listInfo = result.getDataset("RULE_INFO");

        if (listInfo != null)
        {
            for (Iterator iter = listInfo.iterator(); iter.hasNext();)
            {
                IData info = (IData) iter.next();

                if ("1".equals(info.getString("TIPS_TYPE")))
                {
                    listTips.add(info);
                }
                else if ("2".equals(info.getString("TIPS_TYPE")))
                {
                    listChoice.add(info);
                }
                else
                {
                    listError.add(info);
                }
            }
        }

        result.remove("RULE_INFO");
        result.put("TIPS_TYPE_TIP", listTips);
        result.put("TIPS_TYPE_ERROR", listError);
        result.put("TIPS_TYPE_CHOICE", listChoice);

        return result;
    }

    /**
     * 解析 Tips_Info 中的 CodeToName
     * 
     * @param PageData
     * @param databus
     *            IData
     * @param strTipsInfo
     *            String
     * @return String
     * @throws Exception
     */
    public static String getNameByCode(IData databus, String strTipsInfo) throws Exception
    {
        StringBuilder strb = new StringBuilder(strTipsInfo.substring(0, strTipsInfo.indexOf("$[CodeToName")));

        String strValueName = strTipsInfo.substring(strTipsInfo.indexOf(",%") + 2, strTipsInfo.indexOf("!)"));
        String strParam = strTipsInfo.substring(strTipsInfo.indexOf(",%") + 1, strTipsInfo.indexOf("!)") + 1);

        String strValue = decodeParam(databus, strParam);

        strValue = BreQueryHelp.getNameByCode(strValueName, strValue);

        strb.append(strValue).append(strTipsInfo.substring(strTipsInfo.indexOf(")]") + 2));

        return strb.toString();
    }

    /**
     * 返回当前被操作的服务或者优惠的列表名称
     * 
     * @author GaoYuan
     * @param PageData
     * @param databus
     *            IData
     * @param strParam
     *            String
     * @throws Exception
     * @return String
     */
    public static String GetNameByData(IData databus, String strParam) throws Exception
    {

        String strKey = null;
        String strStart = strParam.substring(0, strParam.indexOf("$[GetNameByData"));
        String strEnd = strParam.substring(strParam.indexOf(")]") + 2, strParam.length());
        String strBunch = strParam.substring(strParam.indexOf("GetNameByData") + 14, strParam.indexOf(")]"));

        StringBuilder strResult = new StringBuilder("");
        StringBuilder strNames = new StringBuilder("");
        StringBuilder strResult2 = new StringBuilder().append(strStart).append("[").append(strBunch).append("]").append(strEnd);

        IData tradeData = null;
        int iCount = 0;

        String[] strAttr = strBunch.split(",");

        if (strAttr.length >= 4)
        {
            String strTableName = strAttr[0];
            String strKeyName = strAttr[1];
            String strValueName = strAttr[2];

            StringBuilder strTradeCount = new StringBuilder(strTableName).append(":COUNT");
            String strTradeRow = new String(strTableName) + ":";

            if (databus.containsKey("X_TRADE_DATA"))
            {
                tradeData = new DataMap(databus.getString("X_TRADE_DATA"));
            }
            else
            {
                return strResult2.toString(); // 没有找到 TradeData
            }

            if (tradeData.containsKey(strTradeCount.toString()))
            {
                iCount = Integer.parseInt(tradeData.getString(strTradeCount.toString()));
            }
            else
            {
                return strResult2.toString(); // 在传入databus中没有找到台账子表记录
            }

            for (int iRow = 1; iRow <= iCount; iRow++)
            {
                boolean bExist = true;

                IData tradeSubTable = new DataMap(tradeData.getString(strTradeRow + String.valueOf(iRow)));
                ;

                if (tradeSubTable.containsKey(strKeyName)) // 结构中, 没有匹配标识字段
                {
                    for (int iCol = 3; iCol < strAttr.length; iCol++)
                    {
                        if (strAttr[iCol].indexOf("=") > 0)
                        {
                            String[] strTmp = strAttr[iCol].split("=");
                            if (!tradeSubTable.getString(strTmp[0]).equals(strTmp[1]))
                            {
                                bExist = false;
                                break;
                            }
                        }
                    }

                    if (bExist)
                    {
                        strKey = tradeSubTable.getString(strKeyName);
                        if (strValueName == null || strValueName.equals(""))
                        {
                            strNames.append(strKey).append(",");
                        }
                        else
                        {
                            strNames.append(BreQueryHelp.getNameByCode(strValueName, strKey)).append(",");
                        }
                    }
                }
            }
        }

        // 截断末尾的逗号,
        strNames = strNames.lastIndexOf(",") == (strNames.length() - 1) ? strNames.deleteCharAt(strNames.length() - 1) : strNames;

        strResult.append(strStart).append("[").append(strNames).append("]").append(strEnd);

        return strResult.toString();
    }

    /**
     * 解析 Tips_info 中的 $[FLD:objUser.openDate]
     * 
     * @param PageData
     * @param databus
     *            IData
     * @param strParam
     *            String
     * @return String
     * @throws Exception
     */
    public static String getValueByFLD(IData databus, String strTipsInfo) throws Exception
    {
        StringBuilder strb = new StringBuilder(strTipsInfo.substring(0, strTipsInfo.indexOf("$[FLD:")));

        String strValue = strTipsInfo.substring(strTipsInfo.indexOf("$[FLD:") + 6, strTipsInfo.indexOf("!]") + 1);

        strValue = decodeParam(databus, strValue);

        strb.append(strValue).append(strTipsInfo.substring(strTipsInfo.indexOf("!]") + 2));

        return strb.toString();
    }

    /**
     * 报错信息特殊处理
     * 
     * @param databus
     * @param strTipsInfo
     * @return
     * @throws Exception
     */
    private static String replaceErrorInfo(IData databus, String strTipsInfo) throws Exception
    {
        String strInfo = strTipsInfo;
        String strTmp = null;

        /* 报错信息特殊处理 */
        while (strTipsInfo.indexOf("%") > -1 && strTipsInfo.indexOf("!") > -1)
        {
            strTmp = strTipsInfo.substring(strTipsInfo.indexOf("%"), strTipsInfo.indexOf("!") + 1);
            strInfo = strInfo.replaceAll(strTmp, decodeParam(databus, strTmp));
            strTipsInfo = strTipsInfo.substring(strTipsInfo.indexOf("!") + 1);
        }

        return strInfo;
    }
}
