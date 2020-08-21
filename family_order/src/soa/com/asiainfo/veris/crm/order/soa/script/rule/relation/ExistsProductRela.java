
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsProductRela extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsProductRela.class);

    /**
     * 查询副卡用户产品
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsProductRela() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        boolean bExists = false;
        IData param = new DataMap();

        /* 获取规则配置信息 */
        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");
        String strEparchyCode = ruleParam.getString(databus, "EPARCHY_CODE");
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");

        IDataset listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strEparchyCode);

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeRelation.iterator(); iter.hasNext();)
        {
            IData tradeRelation = (IData) iter.next();

            if (strRelationTypeCode.equals(tradeRelation.getString("RELATION_TYPE_CODE")) && strModifyTag.equals(tradeRelation.getString("MODIFY_TAG")) && "2".equals(tradeRelation.getString("ROLE_CODE_B")))
            {
                bExists = false;

                IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

                for (Iterator iterator = listTradeDiscnt.iterator(); iterator.hasNext();)
                {
                    IData tradeDiscnt = (IData) iterator.next();

                    if (tradeDiscnt.getString("TRADE_ID").equals(tradeRelation.getString("TRADE_ID")) && tradeDiscnt.getString("USER_ID").equals(tradeRelation.getString("USER_ID_B")) && tradeDiscnt.getString("DISCNT_CODE").equals(strDiscntCode))
                    {
                        bExists = true;
                        break;
                    }
                }

                if (bExists)
                {
                    String strProductId = "";
                    String userId = tradeRelation.getString("USER_ID_B");
                    IData user = UcaInfoQry.qryUserMainProdInfoByUserId(userId);

                    if (user.isEmpty())
                    {
                        bExists = false;
                    }
                    else
                    {
                        strProductId = user.getString("PRODUCT_ID");

                        for (Iterator iterator = listCommpara.iterator(); iterator.hasNext();)
                        {
                            IData commpara = (IData) iterator.next();

                            if (strProductId.equals(commpara.getString("PARAM_CODE")))
                            {
                                bExists = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (bExists)
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsProductRela() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
