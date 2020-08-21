package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformProductExtHBean {
    
    public static int[] insertWorkformProductH(IDataset workformProducts) throws Exception{
        return Dao.insert("TF_BH_EOP_PRODUCT_EXT", workformProducts,Route.getJourDb(Route.CONN_CRM_CG));
    }
}
