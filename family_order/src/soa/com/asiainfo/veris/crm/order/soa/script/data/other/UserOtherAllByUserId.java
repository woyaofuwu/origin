
package com.asiainfo.veris.crm.order.soa.script.data.other;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class UserOtherAllByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(UserOtherAllByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserOtherAllByUserId() >>>>>>>>>>>>>>>>>>");

        IData param = new DataMap();

        String strUserId = databus.getString("USER_ID");

        param.put("USER_ID", strUserId);
        param.put("PARTITION_ID", strUserId.substring(strUserId.length() - 4));

        databus.put("TF_F_USER_OTHER", Dao.qryByCode("TF_F_USER_OTHER", "SEL_ALL_BY_USERID", param));

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserOtherAllByUserId() <<<<<<<<<<<<<<<<<<<");
    }

}
