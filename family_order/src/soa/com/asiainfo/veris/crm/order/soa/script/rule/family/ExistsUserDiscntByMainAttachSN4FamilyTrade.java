
package com.asiainfo.veris.crm.order.soa.script.rule.family;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForUser;

public class ExistsUserDiscntByMainAttachSN4FamilyTrade extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsUserDiscntByMainAttachSN4FamilyTrade.class);

    /**
     * 获取commpara配置信息
     * 
     * @param list
     * @param strSubsysCode
     * @param iParamAttr
     * @param strParamCode
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getCommpara(IDataset list, String strSubsysCode, int iParamAttr, String strParamCode, String strEparchyCode) throws Exception
    {
        if (list == null)
        {
            list = BreQryForCommparaOrTag.getCommpara(strSubsysCode, iParamAttr, strParamCode, strEparchyCode);
        }

        return list;
    }

    /**
     * 判断亲情业务主副卡优惠
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsUserDiscntByMainAttachSN4FamilyTrade() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IDataset listCommpara = null;
        IDataset listUserDiscnt = null;
        String strError = null;
        String strErr = "办理";

        /* 获取规则配置信息 */
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strJudgeMianSn = ruleParam.getString(databus, "JUDGE_MAIN_SN");
        String strJudgeAttachSn = ruleParam.getString(databus, "JUDGE_ATTACH_SN");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");

        if ("1".equals(strModifyTag))
        {
            strErr = "取消";
        }

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strSnLastChar = databus.getString("SERIAL_NUMBER").substring(databus.getString("SERIAL_NUMBER").length() - 1);

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeRelation = listTradeRelation.iterator(); iterTradeRelation.hasNext();)
        {
            IData tradeRelation = (IData) iterTradeRelation.next();

            if (strRelationTypeCode.indexOf("|" + tradeRelation.getString("RELATION_TYPE_CODE") + "|") > -1 && strModifyTag.equals(tradeRelation.getString("MODIFY_TAG")))
            {
                if (("1".equals(tradeRelation.getString("ROLE_CODE_B")) && "1".equals(strJudgeMianSn)) /* 判断是否校验主卡 */
                        || ("2".equals(tradeRelation.getString("ROLE_CODE_B")) && "1".equals(strJudgeAttachSn))) /* 判断是否校验附卡 */
                {
                    listCommpara = this.getCommpara(listCommpara, "CSM", 1001, "DISCNT", strEparchyCode);
                    listUserDiscnt = BreQryForUser.getUserDiscntByUserId(tradeRelation.getString("USER_ID_B"));

                    for (Iterator iterUserDiscnt = listUserDiscnt.iterator(); iterUserDiscnt.hasNext();)
                    {
                        IData userDiscnt = (IData) iterUserDiscnt.next();

                        for (Iterator iterCommpara = listCommpara.iterator(); iterCommpara.hasNext();)
                        {
                            IData commpara = (IData) iterCommpara.next();

                            if ((userDiscnt.getString("DISCNT_CODE").equals(commpara.getString("PARA_CODE2")))
                                    && ("Z".equals(commpara.getString("PARA_CODE1")) || "2".equals(tradeRelation.getString("ROLE_CODE_B")) || strSnLastChar.equals(commpara.getString("PARA_CODE1"))))
                            {
                                if ("2".equals(tradeRelation.getString("ROLE_CODE_B")))
                                {
                                    String strSn = UcaInfoQry.qryUserMainProdInfoByUserId(tradeRelation.getString("USER_ID_B")).getString("SERIAL_NUMBER");
                                    strError = "#业务后特殊限制表判断：附卡用户【" + strSn + "】有【" + commpara.getString("PARA_CODE2") + "】优惠，不能" + strErr + "此业务！";
                                }
                                else
                                {
                                    strError = "#业务后特殊限制表判断：主卡用户有【" + commpara.getString("PARA_CODE2") + "】优惠，不能" + strErr + "此业务！";
                                }
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201670, strError);
                                break;
                            }
                        }

                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsUserDiscntByMainAttachSN4FamilyTrade() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
