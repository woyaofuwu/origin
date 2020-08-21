
package com.asiainfo.veris.crm.order.soa.script.data.platsvc;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class UserPlatSvcByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(UserPlatSvcByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserPlatSvcByUserId() >>>>>>>>>>>>>>>>>>");
        if (databus.containsKey("UCADATA"))
        {
            UcaData ucaData = (UcaData) databus.get("UCADATA");
            List<PlatSvcTradeData> list = ucaData.getUserPlatSvcs();
            databus.put("TF_F_USER_PLATSVC", list);
        }
        else
        {
            IData param = new DataMap();

            String strUserId = databus.getString("USER_ID");

            param.put("USER_ID", strUserId);

            databus.put("TF_F_USER_PLATSVC", Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID", param));

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserPlatSvcByUserId() <<<<<<<<<<<<<<<<<<<");
    }
}
