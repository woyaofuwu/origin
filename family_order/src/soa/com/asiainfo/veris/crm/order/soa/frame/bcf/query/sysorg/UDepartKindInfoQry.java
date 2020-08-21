
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UDepartKindInfoQry
{

    /**
     * 根据地州编码和部门/渠道标识查询部门类别列表
     * 
     * @param eparchyCode
     * @param departId
     * @return
     * @throws Exception
     */
    public static IDataset qryDeparKindByDepartId(String eparchyCode, String departId) throws Exception
    {
        IData data = new DataMap();
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("DEPART_ID", departId);

        return Dao.qryByCode("TD_M_DEPARTKIND", "SEL_BY_CODETYPE", data, Route.CONN_SYS);
    }

}
