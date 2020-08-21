
package com.asiainfo.veris.crm.order.soa.script.data.relation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class RelationUUByUserIdB extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(RelationUUByUserIdB.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SaleActive() >>>>>>>>>>>>>>>>>>");

        String strUserId = databus.getString("USER_ID");

        if (strUserId == null || "".equals(strUserId))
        {
            strUserId = databus.getString("ID");
        }

        IData param = new DataMap();
        param.put("USER_ID_B", strUserId);

        databus.put("TF_F_RELATION_UU", Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDB", param));

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SaleActive() <<<<<<<<<<<<<<<<<<<");

    }

}
