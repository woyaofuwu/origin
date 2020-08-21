
package com.asiainfo.veris.crm.order.soa.script.data.imei;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class UserImeiByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(UserImeiByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserImeiByUserId() >>>>>>>>>>>>>>>>>>");

        IDataset listUser = databus.getDataset("TF_F_USER");

        IData param = new DataMap();
        param.put("USER_ID", listUser.get(0, "USER_ID"));

        databus.put("TF_F_USER_IMEI", Dao.qryByCode("TF_F_USER_IMEI", "SEL_IMEI_BY_USERID", param));

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserImeiByUserId() <<<<<<<<<<<<<<<<<<<");
    }

}
