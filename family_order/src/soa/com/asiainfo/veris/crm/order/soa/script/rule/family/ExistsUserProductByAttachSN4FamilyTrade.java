
package com.asiainfo.veris.crm.order.soa.script.rule.family;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class ExistsUserProductByAttachSN4FamilyTrade extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsUserProductByAttachSN4FamilyTrade.class);

    /**
     * 判断附卡产品是否允许加入亲情产品
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsUserProductByAttachSN4FamilyTrade() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strProductId = ruleParam.getString(databus, "PRODUCT_ID");
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strProductName = ruleParam.getString(databus, "PRODUCT_NAME");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeRelation = listTradeRelation.iterator(); iterTradeRelation.hasNext();)
        {
            IData tradeRelation = (IData) iterTradeRelation.next();

            if ("0".equals(tradeRelation.getString("MODIFY_TAG")) && strRelationTypeCode.indexOf("|" + tradeRelation.getString("RELATION_TYPE_CODE") + "|") > -1 && "2".equals(tradeRelation.getString("ROLE_CODE_B")))
            {
                IData listUser = UcaInfoQry.qryUserMainProdInfoByUserId(tradeRelation.getString("USER_ID_B"));
                if (IDataUtil.isNotEmpty(listUser))
                {
                    if (listUser.size() > 0 && strProductId.indexOf("|" + listUser.getString("PRODUCT_ID") + "|") > 0)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201670, "#业务后特殊限制表判断：用户【" + strProductName + "】类型限制成为副号码！");
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsUserProductByAttachSN4FamilyTrade() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
