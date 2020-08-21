
package com.asiainfo.veris.crm.order.soa.script.data.saleactive;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class SaleActiveByUserId extends BreBase implements IBREDataPrepare
{
    private static Logger logger = Logger.getLogger(SaleActiveByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SaleActive() >>>>>>>>>>>>>>>>>>");

        String strUserId = databus.getString("USER_ID");
        if (databus.containsKey("UCADATA"))
        {
            UcaData ucaData = (UcaData) databus.get("UCADATA");
            List<SaleActiveTradeData> list = ucaData.getUserSaleActives();
            databus.put("TF_F_USER_SALE_ACTIVE", list);
        }
        else
        {
            if (strUserId == null || "".equals(strUserId))
            {
                strUserId = databus.getString("ID");
            }

            IData param = new DataMap();
            param.put("USER_ID", strUserId);
            param.put("PROCESS_TAG", "0");

            databus.put("TF_F_USER_SALE_ACTIVE", Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_USER_SALE_ACTIVE", param));
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SaleActive() <<<<<<<<<<<<<<<<<<<");

    }
}
