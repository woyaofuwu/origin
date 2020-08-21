
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class VpmnSaleActiveQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getTotalSaleActiveByGtag(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A");
        String active_type = input.getString("ACTIVE_TYPE");
        return VpmnSaleActiveQry.getTotalSaleActiveByGtag(user_id_a, active_type);
    }

    public IDataset queryVPMNSaleActiveByUserIdAActype(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A");
        String active_type = input.getString("ACTIVE_TYPE");
        String eparchy_code = input.getString(Route.ROUTE_EPARCHY_CODE);
        return VpmnSaleActiveQry.queryVPMNSaleActiveByUserIdAActype(user_id_a, active_type, eparchy_code, this.getPagination());
    }

    public IDataset queryVPMNSaleActiveByUserIdBActype(IData input) throws Exception
    {
        String user_id_b = input.getString("USER_ID_B");
        String active_type = input.getString("ACTIVE_TYPE");
        String eparchy_code = input.getString(Route.ROUTE_EPARCHY_CODE);
        return VpmnSaleActiveQry.queryVPMNSaleActiveByUserIdBActype(user_id_b, active_type, eparchy_code);
    }
}
