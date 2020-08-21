
package com.asiainfo.veris.crm.order.soa.script.data.discnt;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class UserDiscntByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(UserDiscntByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserDiscntByUserId() >>>>>>>>>>>>>>>>>>");
        if (databus.containsKey("UCADATA"))
        {
            UcaData ucaData = (UcaData) databus.get("UCADATA");
            List<DiscntTradeData> list = ucaData.getUserDiscnts();
            databus.put("TF_F_USER_DISCNT", list);
        }
        else
        {
            IDataset listUser = databus.getDataset("TF_F_USER");

            IData param = new DataMap();
            param.put("USER_ID", listUser.get(0, "USER_ID"));

            databus.put("TF_F_USER_DISCNT", Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID", param));

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserDiscntByUserId() <<<<<<<<<<<<<<<<<<<");
    }

}
