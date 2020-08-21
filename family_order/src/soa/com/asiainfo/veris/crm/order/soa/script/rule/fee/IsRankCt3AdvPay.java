
package com.asiainfo.veris.crm.order.soa.script.rule.fee;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsRankCt3AdvPay extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsRankCt3AdvPay.class);

    /**
     * 判断是否3G卡
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsRankCt3AdvPay() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strLeveaRealFee = databus.getString("LEVEA_REAL_FEE");
        String strEparchyCode = databus.getString("EPARCHY_CODE");

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("SIM_CARD_NO", strLeveaRealFee);
        param.put("EPARCHY_CODE", strEparchyCode);

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "IsRankCt3AdvPay", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsRankCt3AdvPay() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
