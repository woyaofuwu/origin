
package com.asiainfo.veris.crm.order.soa.script.rule.family;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class ExistsVipUserByMainAttachSNForFamilyTrade extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsVipUserByMainAttachSNForFamilyTrade.class);

    /**
     * 判断亲情业务主副卡VIP信息
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsVipUserByMainAttachSNForFamilyTrade() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData param = new DataMap();
        String strError = null;
        boolean bExist = false;
        String strRoleCodeB = null;

        /* 获取规则配置信息 */
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strJudgeMianSn = ruleParam.getString(databus, "JUDGE_MAIN_SN");
        String strJudgeAttachSn = ruleParam.getString(databus, "JUDGE_ATTACH_SN");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strSnLastChar = databus.getString("SERIAL_NUMBER").substring(databus.getString("SERIAL_NUMBER").length() - 1);

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeRelation = listTradeRelation.iterator(); iterTradeRelation.hasNext();)
        {
            IData tradeRelation = (IData) iterTradeRelation.next();
            strRoleCodeB = tradeRelation.getString("ROLE_CODE_B");

            if (strRelationTypeCode.indexOf("|" + tradeRelation.getString("RELATION_TYPE_CODE") + "|") > -1 && "0".equals(tradeRelation.getString("MODIFY_TAG")))
            {
                if (("2".equals(strRoleCodeB) && "1".equals(strJudgeAttachSn)) /* 附卡是否判断 */
                        || ("1".equals(strRoleCodeB) && "1".equals(strJudgeMianSn))) /* 主卡是否判断 */
                {
                    param.put("USER_ID", tradeRelation.getString("USER_ID_B"));
                    param.put("CLASS_ID", "*");

                    bExist = Dao.qryByRecordCount("TD_S_CPARAM", "IsVipClass", param);

                    if ("1".equals(strRoleCodeB) && !bExist)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201670, "#业务后特殊限制表判断：主卡用户不是大客户不能办理该业务！");
                        continue;
                    }
                    else if ("2".equals(strRoleCodeB) && bExist)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201670, "#业务后特殊限制表判断：用户【%s】是大客户不能做为副号码！");
                        continue;
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsVipUserByMainAttachSNForFamilyTrade() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
