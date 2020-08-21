
package com.asiainfo.veris.crm.order.soa.script.data.svcstate;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class UserSvcStateByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(UserSvcStateByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserSvcStateByUserId() >>>>>>>>>>>>>>>>>>");
        if (databus.containsKey("UCADATA"))
        {
            UcaData ucaData = (UcaData) databus.get("UCADATA");
            List<SvcStateTradeData> list = ucaData.getUserSvcsState();
            databus.put("TF_F_USER_SVCSTATE", list);
        }
        else
        {
            IDataset listUser = databus.getDataset("TF_F_USER");

            IData param = new DataMap();
            param.put("USER_ID", listUser.get(0, "USER_ID"));

            databus.put("TF_F_USER_SVCSTATE", Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USERID_NOW", param));

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserSvcStateByUserId() <<<<<<<<<<<<<<<<<<<");
    }

}
