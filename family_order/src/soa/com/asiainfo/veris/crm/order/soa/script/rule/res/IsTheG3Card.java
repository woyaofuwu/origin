
package com.asiainfo.veris.crm.order.soa.script.rule.res;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsTheG3Card extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsTheG3Card.class);

    /**
     * 判断是否3G卡
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsTheG3Card() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRsrvStr10 = ruleParam.getString(databus, "RSRV_STR10");

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("SIM_CARD_NO", strRsrvStr10);

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "IsTheG3Card", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsTheG3Card() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
