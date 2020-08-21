
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class CustGrpPreBusQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据ID查询预受理信息详情(产品参数)
     */
    public IDataset queryApplyAttrforGrp(IData input) throws Exception
    {
        String apply_id = input.getString("APPLY_ID");
        IDataset data = CustGrpPreBusQry.queryApplyAttrforGrp(apply_id);

        return data;
    }

    /**
     * 根据ID查询预受理信息详情（集团业务）
     */
    public IDataset queryApplyDetailforGrp(IData input) throws Exception
    {
        String apply_id = input.getString("APPLY_ID");
        IData d = CustGrpPreBusQry.queryApplyDetailforGrp(apply_id);
        IDataset data = IDataUtil.idToIds(d);

        return data;
    }

    /**
     * 根据ID查询预受理信息详情（成员业务）
     */
    public IDataset queryApplyDetailForMeb(IData input) throws Exception
    {
        String apply_id = input.getString("APPLY_ID");

        return CustGrpPreBusQry.queryApplyDetailForMeb(apply_id);
    }

    /**
     * 查询集团预受理信息
     */
    public IDataset queryPrebusiness(IData input) throws Exception
    {
        String obj_type_code = input.getString("OBJ_TYPE_CODE");
        String deal_state = input.getString("DEAL_STATE");
        String cust_manager_id = input.getString("CUST_MANAGER_ID");
        String serial_number = input.getString("SERIAL_NUMBER");
        String start_time = input.getString("START_TIME");
        String end_time = input.getString("END_TIME");

        IDataset data = CustGrpPreBusQry.queryPrebusiness(obj_type_code, deal_state, cust_manager_id, serial_number, start_time, end_time, getPagination());

        if (IDataUtil.isNotEmpty(data))
        {
            for (int i = 0; i < data.size(); i++)
            {
                IData idata = data.getData(i);
                idata.put("CUST_MANAGER_NAME", UStaffInfoQry.getStaffNameByStaffId(idata.getString("CUST_MANAGER_ID")));
            }
        }
        return data;
    }

    /**
     * 查询成员预受理信息，查4个库
     */
    public IDataset queryPrebusinessByCg(IData input) throws Exception
    {
        String obj_type_code = input.getString("OBJ_TYPE_CODE");
        String deal_state = input.getString("DEAL_STATE");
        String cust_manager_id = input.getString("CUST_MANAGER_ID");
        String start_time = input.getString("START_TIME");
        String end_time = input.getString("END_TIME");

        IDataset data = CustGrpPreBusQry.queryPrebusinessByCg(obj_type_code, deal_state, cust_manager_id, start_time, end_time, getPagination());

        if (IDataUtil.isNotEmpty(data))
        {
            for (int i = 0; i < data.size(); i++)
            {
                IData idata = data.getData(i);
                idata.put("CUST_MANAGER_NAME", UStaffInfoQry.getStaffNameByStaffId(idata.getString("CUST_MANAGER_ID")));
            }
        }

        return data;
    }

    /**
     * 查询成员预受理信息，指定路由
     */
    public IDataset queryPrebusinessByEeparchyCode(IData input) throws Exception
    {
        String obj_type_code = input.getString("OBJ_TYPE_CODE");
        String deal_state = input.getString("DEAL_STATE");
        String cust_manager_id = input.getString("CUST_MANAGER_ID");
        String serial_number = input.getString("SERIAL_NUMBER");
        String start_time = input.getString("START_TIME");
        String end_time = input.getString("END_TIME");

        IDataset data = CustGrpPreBusQry.queryPrebusinessByEeparchyCode(obj_type_code, deal_state, cust_manager_id, serial_number, start_time, end_time, getPagination());
        if (IDataUtil.isNotEmpty(data))
        {
            for (int i = 0; i < data.size(); i++)
            {
                IData idata = data.getData(i);
                idata.put("CUST_MANAGER_NAME", UStaffInfoQry.getStaffNameByStaffId(idata.getString("CUST_MANAGER_ID")));
            }
        }
        return data;
    }
}
