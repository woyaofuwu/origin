
package com.asiainfo.veris.crm.order.soa.script.data.vip;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;

public class CustVipByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(CustVipByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CustVipByUserId() >>>>>>>>>>>>>>>>>>");

        IDataset ids = CustVipInfoQry.qryVipInfoByUserId(databus.getString("USER_ID"));

        databus.put("TF_F_CUST_VIP", ids);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CustVipByUserId() <<<<<<<<<<<<<<<<<<<");

    }
}
