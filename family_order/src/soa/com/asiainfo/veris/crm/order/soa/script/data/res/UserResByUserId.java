
package com.asiainfo.veris.crm.order.soa.script.data.res;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class UserResByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(UserResByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserResByUserId() >>>>>>>>>>>>>>>>>>");

        IData inParam = new DataMap();
        inParam.put("USER_ID", databus.get("USER_ID"));

        IDataset ids = UserResInfoQry.qryUserResByUserId(databus.getString("USER_ID"), null);

        databus.put("TF_F_USER_RES", ids);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserResByUserId() <<<<<<<<<<<<<<<<<<<");

    }

}
