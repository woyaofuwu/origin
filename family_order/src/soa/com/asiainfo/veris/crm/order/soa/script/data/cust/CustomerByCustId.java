
package com.asiainfo.veris.crm.order.soa.script.data.cust;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class CustomerByCustId implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(CustomerByCustId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CustomerByCustId() >>>>>>>>>>>>>>>>>>");
        if (databus.containsKey("UCADATA"))
        {
            UcaData ucaData = (UcaData) databus.get("UCADATA");
            CustomerTradeData list = ucaData.getCustomer();
            databus.put("TF_F_CUSTOMER", list);
        }
        else
        {
            String strCustId = databus.getDataset("TF_F_USER").getData(0).getString("CUST_ID");

            IData ids = UcaInfoQry.qryCustomerInfoByCustId(strCustId);
            databus.put("TF_F_CUSTOMER", IDataUtil.idToIds(ids));
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CustomerByCustId() <<<<<<<<<<<<<<<<<<<");

    }

}
