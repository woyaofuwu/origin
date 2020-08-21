
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * BBOSSTASK_GRP表查询
 * 
 * @author chenkh 2014-7-25
 */
public class BbossTaskChargeAgainstGrp
{
    public static IDataset queryBbossTaskGrp() throws Exception
    {
        IData param = new DataMap();
        IDataset dataset = Dao.qryByCode("BBOSSTASK_GRP", "SEL_ALL", param, Route.CONN_CRM_CG);

        return dataset;
    }
}
