
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interfaceinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GroupCutInfoQry
{

    public static IDataset getGroupCustInfoByUserIdByRouteId(IData param, String routeId) throws Exception
    {

        IData result = Dao.qryByPK("TI_O_GROUPCUT", param, routeId);
        IDataset resultset = new DatasetList();
        if (IDataUtil.isNotEmpty(result))
            resultset.add(result);
        return resultset;
    }

    public static IDataset getGroupCutInfoByUserId(IData param) throws Exception
    {

        IData result = Dao.qryByPK("TI_O_GROUPCUT", param);
        IDataset resultset = new DatasetList();
        if (IDataUtil.isNotEmpty(result))
            resultset.add(result);
        return resultset;
    }
}
