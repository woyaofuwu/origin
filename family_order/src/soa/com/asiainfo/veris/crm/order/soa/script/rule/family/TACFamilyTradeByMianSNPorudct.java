
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

public class TACFamilyTradeByMianSNPorudct extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACFamilyTradeByMianSNPorudct.class);

    /**
     * 判断亲情主卡用户产品是否能办理亲情产品
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACFamilyTradeByMianSNPorudct() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData param = new DataMap();
        boolean bVipNotJudge = false;
        boolean bExists = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strProductId = databus.getString("PRODUCT_ID");

        /* 开始逻辑规则校验 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");
        for (Iterator iter = listTradeRelation.iterator(); iter.hasNext();)
        {
            IData element = (IData) iter.next();

            if ("0".equals(element.getString("MODIFY_TAG")))
            {
                bExists = true;
                break;
            }
        }

        param.clear();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", 5092);
        param.put("PARAM_CODE", databus.getString("TRADE_TYPE_CODE"));
        param.put("EPARCHY_CODE", databus.getString("EPARCHY_CODE"));

        IDataset listCommpara = Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param);

        if (listCommpara.size() == 1 && listCommpara.getData(0).getString("PARAM_NAME").indexOf("|" + strProductId + "|") > -1)
        {
            bVipNotJudge = "1".equals(listCommpara.getData(0).getString("PARAM_NAME")) ? true : false;
        }

        param.clear();
        param.put("USER_ID", databus.getString("USER_ID"));
        param.put("CLASS_ID", "*");

        if (Dao.qryByRecordCount("TD_S_CPARAM", "IsVipClass", param) && bVipNotJudge)
        {
            /* 是VIP， 并该产品可以不判断 */
        }
        else
        {
            String strOldProductId = databus.getString("RSRV_STR2", "");

            param.clear();
            param.put("PRODUCT_ID_A", strProductId);
            param.put("PRODUCT_ID_B", strOldProductId);
            param.put("LIMIT_TAG", "2");

            if (bExists && !Dao.qryByRecordCount("TD_S_PRODUCTLIMIT", "SEL_EXISTS_AB_LIMIT", param))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201103, "业务登记后条件判断:主卡产品类型不可以办理亲情业务！");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACFamilyTradeByMianSNPorudct() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
