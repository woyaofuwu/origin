
package com.asiainfo.veris.crm.order.soa.frame.bre.script.data.common;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class TdBreInfocode extends BreBase implements IBREDataPrepare
{
    private static final Logger logger = Logger.getLogger(TdBreInfocode.class);

    @Override
    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>step in TdBreInfocode() >>>>>>>>>>>>>>>>>>");

        IData param = new DataMap();
        param.put("RULE_CHECK_MODE", "0");
        param.put("STATE", "1");
        param.put("ID", databus.getString("RULE_ID"));
        databus.put("TD_BRE_INFOCODE", Dao.qryByCode("TD_BRE_INFOCODE", "SEL_ERRORCODE_BYID", param, Route.CONN_CRM_CEN));

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out TdBreInfocode() >>>>>>>>>>>>>>>>>>");

    }

}
