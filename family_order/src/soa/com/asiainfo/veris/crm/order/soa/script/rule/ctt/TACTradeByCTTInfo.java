
package com.asiainfo.veris.crm.order.soa.script.rule.ctt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TACTradeByCTTInfo extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACTradeByCTTInfo.class);

    /**
     * 判断客户名称必须包涵铁通信息
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACTradeByCTTInfo() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        IDataset listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 3765, databus.getString("PRODUCT_ID"), databus.getString("TRADE_TYPE_CODE"), databus.getString("EPARCHY_CODE"));

        if (listCommpara.size() > 0)
        {
            IData param = new DataMap();
            param.put("EPARCHY_CODE", databus.getString("EPARCHY_COEE"));
            param.put("PARA_ATTR", "3216");
            param.put("PARA_CODE1", "TIETBUSY_INLAND_DATA");
            param.put("PARA_VALUE1", databus.getString("SERIAL_NUMBER"));
            IDataset listExtNum = Dao.qryByCode("TF_M_EXT_NUM", "SEL_BY_PARAVALUE1", param, "res");

            if (listExtNum.size() > 0)
            {
                if (databus.getString("CUST_NAME", "").indexOf(listExtNum.getData(0).getString("PARA_CODE2")) < 0)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201129, "#业务登记后条件判断:该业务[客户名称]须包含铁通号码信息！");
                }

                IDataset listTradeAccount = databus.getDataset("TF_B_TRADE_ACCOUNT");

                if (listTradeAccount.size() > 0 && listTradeAccount.getData(0).getString("PAY_NAME").indexOf(listExtNum.getData(0).getString("PARA_CODE2")) < 0)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201130, "#业务登记后条件判断:该业务[账户付费名称]须包含铁通号码信息！");
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACTradeByCTTInfo() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
