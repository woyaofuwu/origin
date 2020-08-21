/* $Header$ */

package com.asiainfo.veris.crm.order.soa.frame.bre.tools;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.rule.log.BreActLog;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public final class BreSuperLimit extends BreBase
{

    private static final Logger logger = Logger.getLogger(BreSuperLimit.class);

    static String strBunch[] =
    { "USER_ID", "SCRIPT_ID", "TRADE_TYPE_CODE" };

    /**
     * 给短信调的东西
     * 
     * @param databus
     *            :: 数据总线，tf_f_user ， tf_b_trade 信息都放在这里
     * @param rule
     *            :: 规则信息， 短信表里面配置的S类型的字段值 {IS_REVOLT=["0"], BRE_PARAM_VALUE=["ExistsTradeMultiDiscnt"],
     *            BRE_PARAM_VALUE2={PARAM_CODE=["KHGFDIST"], MODIFY_TAG=["0"], ACCEPT_MONTH=["%ACCEPT_MONTH!"],
     *            EPARCHY_CODE=["%TRADE_EPARCHY_CODE!"], TRADE_ID=["%TRADE_ID!"]}}
     * @return
     * @throws Exception
     */
    public static boolean jSuperLimit(IData databus, IData rule) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("进入特殊规则校验 tsgzjy [" + rule + "]");
        }

        boolean bExit = false;

        /* 获取配置信息 */
        String strScriptId = rule.getString("BRE_PARAM_VALUE");
        boolean bIsRevolt = rule.getString("IS_REVOLT").equals("1") ? true : false;

        /* 转换得到 BreDataBus, BreRuleParam */
        BreDataBus bus = new BreDataBus(databus);

        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        if (listTrade.size() > 0)
        {
            bus.putAll(listTrade.getData(0));
        }

        BreRuleParam ruleParam = new BreRuleParam();

        // 正则表达式的 BRE_PARAM_VALUE2 不是IData 格式
        if ("splCheckByRegular".equals(strScriptId))
        {
            ruleParam.put("BRE_PARAM_VALUE1", rule.getString("BRE_PARAM_VALUE1"));
            ruleParam.put("BRE_PARAM_VALUE2", rule.getString("BRE_PARAM_VALUE2"));
        }
        else
        {
            IData ruledef = rule.getData("BRE_PARAM_VALUE2");

            String[] strNames = ruledef.getNames();
            for (int idx = 0; idx < strNames.length; idx++)
            {
                ruleParam.put(strNames[idx], ruledef.getString(strNames[idx]));
            }
        }

        ruleParam.put("EXECUTE_SQL_REF", strScriptId);

        /* 执行规则 */
        Object obj = BreCacheHelp.getScriptObjectByScriptId(strScriptId);

        if (obj != null)
        {
            IBREScript ruleObj = (IBREScript) obj;

            bExit = ruleObj.run(bus, ruleParam);
        }

        bExit = bIsRevolt ? !bExit : bExit;

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("退出特殊规则校验 tsgzjy [" + bExit + "]");
        }

        return bExit;
    }

    public static void jSuperList(IData databus, IDataset listRule) throws Exception
    {
        boolean bTips = true;
        String strRuleCheckMode = null;
        int iTipsType = 0;
        String strTipsInfo = null;
        String strRuleBizId = null;
        String strRuleDscription = null;

        String strScriptId = null;
        String strRuleId = null;
        String strScriptPath = null;
        StringBuilder strError = null;

        boolean bIsRevolt;
        boolean bExit;

        IData mapRule = null;

        mapRule = listRule.getData(0);

        strRuleBizId = mapRule.getString("RULE_BIZ_ID");
        iTipsType = mapRule.getInt("TIPS_TYPE");
        strTipsInfo = mapRule.getString("TIPS_INFO");
        strRuleCheckMode = mapRule.getString("RULE_CHECK_MODE");

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("[" + strRuleBizId + "]>->->->->->->->->->->->->->-> Rule_Group_in Rule_Biz_id = " + strRuleBizId + " size = " + listRule.size());
        }

        if (strRuleCheckMode.equals("0"))
        {
            bTips = true;
        }
        else
        {
            bTips = false;
        }

        IData result = databus.getData("RULE_RESULT");

        IBREScript rule = null;
        long lstartTime = -1;

        for (int idx = 0, size = listRule.size(); idx < size; idx++)
        {
            mapRule = listRule.getData(idx);
            bExit = false;

            strScriptId = mapRule.getString("SCRIPT_ID");
            strScriptPath = mapRule.getString("SCRIPT_PATH");
            strRuleId = mapRule.getString("RULE_ID");
            bIsRevolt = mapRule.getString("IS_REVOLT").equals("1") ? true : false;
            strRuleDscription = mapRule.getString("RULE_DESCRIPTION", "");

            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            {
                logger.debug("[" + strRuleBizId + "][" + strRuleId + "]------------------------------- 第 " + idx + " 条 RULE_DEF; Rule_id = " + strRuleId + " ; script_id = " + strScriptId + " ; script_path = " + strScriptPath + " ;");
            }

            try
            {
                /* 继续兼容NG版本的CheckBySql，判断如果是CheckBySql，讲SqlRef传入到RuleData中，方便后续处理 */
                if (strScriptId == null)
                {
                    StringBuilder sb = new StringBuilder(300);
                    sb.append("系统规则配置错误，不影响业务执行，请联系系统管理员查看规则配置！BRE Error : Rule_biz_id = ").append(strRuleBizId).append("; Rule_id = ").append(strRuleId).append("; Script_id ").append(strScriptId).append(" Runtime Error!");

                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 888888, sb.toString());

                    continue;
                }

                /* 获取规则参数 根据 rule_id 取 td_bre_parameter 表数据 */
                BreRuleParam ruleParam = BreCacheHelp.getRulePerameterMapByRuleId(strRuleId);

                /* 构造RULE_RESULT结果KEY */
                StringBuilder strbKey = new StringBuilder(strScriptId).append(ruleParam.toString());

                /* 如果databus 中不包涵规则结果缓存， 则执行规则， else 直接去规则结果 */
                if (!result.containsKey(strbKey.toString()))
                {
                    if (strScriptPath.equals(BreFactory.CHECK_BY_SQL))
                    {
                        ruleParam.put("EXECUTE_SQL_REF", strScriptId);
                    }

                    /* 根据规则脚本配置信息, 反射调用规则类.run方法 */
                    Object obj = BreCacheHelp.getScriptObjectByScriptId(strScriptId);

                    if (obj != null)
                    {
                        rule = (IBREScript) obj;

                        // 日志开始时间
                        lstartTime = BreActLog.getStartTime();

                        databus.put("RULE_ID", strRuleId);
                        databus.put("RULE_BIZ_ID", strRuleBizId);
                        // 执行
                        bExit = rule.run(databus, ruleParam);

                        BreActLog.log(databus, strScriptId, lstartTime);
                    }

                    /* 对于同一个用户来说,同一个RULE_ID的执行结果肯定是一样的,所以可以保存下来 */
                    if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                    {
                        logger.debug("PPPPPPPPPPPPPPPPPPPPPPPPPP rrt ----- 结果放入缓存 ------[" + strbKey.toString() + "][" + bExit + "]");
                    }
                    result.put(strbKey.toString(), bExit);
                }
                else
                {
                    bExit = result.getBoolean(strbKey.toString());

                    if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                    {
                        logger.debug("RRRRRRRRRRRRRRRRR rrt---  从缓存中取到规则结果  ---- [" + strbKey.toString() + "][" + bExit + "]");
                    }
                }
            }
            catch (SQLException e)
            {
                StringBuilder sb = new StringBuilder(300);
                sb.append("系统规则配置错误，不影响业务执行，请联系系统管理员查看规则配置！BRE Error : Rule_biz_id = ").append(strRuleBizId).append("; Rule_id = ").append(strRuleId).append("; Script_id ").append(strScriptId).append(" SQL Not Found!").append(e);

                logger.error(sb.toString(),e);
                e.printStackTrace();

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, sb.toString());
                bExit = true;
                bTips = false;

                break;
            }
            catch (ClassNotFoundException e)
            {
                StringBuilder sb = new StringBuilder(300);
                sb.append("系统规则配置错误，不影响业务执行，请联系系统管理员查看规则配置！BRE Error : Rule_biz_id = ").append(strRuleBizId).append("; Rule_id = ").append(strRuleId).append("; Script_id ").append(strScriptId).append("  No Definition!").append(e);

                logger.error(sb.toString(),e);
                e.printStackTrace();

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, sb.toString());
                bExit = true;
                bTips = false;

                break;
            }
            catch (Exception e)
            {
                StringBuilder sb = new StringBuilder(300);
                sb.append("系统规则配置错误，不影响业务执行，请联系系统管理员查看规则配置！BRE Error : Rule_biz_id = ").append(strRuleBizId).append("; Rule_id = ").append(strRuleId).append("; Script_id ").append(strScriptId).append(" Runtime Error!").append(
                        e.getMessage());

                logger.error(sb.toString(),e);
                e.printStackTrace();

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 888888, sb.toString());
                bExit = true;
                bTips = false;

                break;
            }

            bExit = bIsRevolt ? !bExit : bExit;

            /* RULE_CHECK_MODE == 0 只要有一条不符合就不报错 */
            if (strRuleCheckMode.equals("0") && !bExit)
            {
                bTips = false;

                break;
            }
            /* RULE_CHECK_MODE == 1 满足所有规则就报错 */
            else if (strRuleCheckMode.equals("1") && !bExit)
            {
                bTips = true;
                break;
            }

            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            {
                logger.debug("[" + strRuleBizId + "][" + strRuleId + "]<<<<<<<<<<<<<<<<<<<<<<<<<<<< Rule_out Rule_id = " + strRuleId + "  ---- return  ----- " + bTips);
            }
        }

        /* 规则配置满足,需要对前台有响应 */
        if (bTips)
        {
            strError = null;
            if (strTipsInfo == null)
            {
                strTipsInfo = "";
            }
            if (strRuleCheckMode.equals("1"))
            {
                strError = new StringBuilder(strTipsInfo).append(strRuleDscription);
            }
            else
            {
                strError = new StringBuilder(strTipsInfo);
            }
            String errorCode = "666666";
            IDataset errroList = databus.getDataset("TD_BRE_INFOCODE") == null ? new DatasetList() : databus.getDataset("TD_BRE_INFOCODE");
            if (IDataUtil.isEmpty(errroList))
            {
                errorCode = strRuleBizId;
            }
            else
            {
                errorCode = errroList.first().getString("CODE");
            }
            BreTipsHelp.addTipsInfoByBus(databus, errorCode, iTipsType, strError.toString());
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("[" + strRuleBizId + "]<-<-<-<-<-<-<-<-<-<-<-<-<-< Rule_Group_out Rule_Biz_id = " + strRuleBizId);
        }
    }
}
