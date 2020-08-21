
package com.asiainfo.veris.crm.order.soa.script.data.svc;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class UserSvcByUserId extends BreBase implements IBREDataPrepare
{
    private static Logger logger = Logger.getLogger(UserSvcByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserSvcByUserId() >>>>>>>>>>>>>>>>>>");

        IDataset listUser = databus.getDataset("TF_F_USER");

        if (databus.containsKey("UCADATA"))
        {
            UcaData ucaData = (UcaData) databus.get("UCADATA");
            List<SvcTradeData> list = ucaData.getUserSvcs();
            databus.put("TF_F_USER_SVC", list);
        }
        else
        {
            IData param = new DataMap();
            param.put("USER_ID", listUser.get(0, "USER_ID"));

            databus.put("TF_F_USER_SVC", Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID", param));
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserSvcByUserId() <<<<<<<<<<<<<<<<<<<");
    }

}
