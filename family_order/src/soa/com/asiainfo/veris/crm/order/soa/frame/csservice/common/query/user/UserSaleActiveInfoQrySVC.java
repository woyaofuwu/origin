
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class UserSaleActiveInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getSaleActiveByUserIdProcessTag(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String process_tag = input.getString("PROCESS_TAG");
        return UserSaleActiveInfoQry.getSaleActiveByUserIdProcessTag(user_id, process_tag);
    }

    public IDataset qrySaleActiveBySnStaffDeptCampnSDateEDate(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String staffId = input.getString("STAFF_ID");
        String deptId = input.getString("DEPART_ID");
        String campnType = input.getString("CAMPN_TYPE");
        String startDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");

        return UserSaleActiveInfoQry.qrySaleActiveBySnStaffDeptCampnSDateEDate(serialNumber, staffId, deptId, campnType, startDate, endDate, this.getPagination());
    }

    public IDataset querySaleActDetail(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String relation_trade_id = input.getString("RELATION_TRADE_ID");
        String package_id = input.getString("PACKAGE_ID");
        String campn_id = input.getString("CAMPN_ID");
        String product_id = input.getString("PRODUCT_ID");
        String start_date = input.getString("START_DATE");
        return UserSaleActiveInfoQry.querySaleActDetail(user_id, product_id, package_id, campn_id, relation_trade_id, start_date, this.getPagination());
    }

    public IDataset querySaleDiscnts(IData input) throws Exception
    {
        String relation_trade_id = input.getString("RELATION_TRADE_ID");
        String campn_id = input.getString("CAMPN_ID");
        String user_id = input.getString("USER_ID");
        return UserSaleActiveInfoQry.querySaleDiscnts(relation_trade_id, campn_id, user_id);
    }

    public IDataset querySaleServs(IData input) throws Exception
    {
        String relation_trade_id = input.getString("RELATION_TRADE_ID");
        String campn_id = input.getString("CAMPN_ID");
        String user_id = input.getString("USER_ID");
        return UserSaleActiveInfoQry.querySaleServs(relation_trade_id, campn_id, user_id);
    }

    /**
     * 调用帐务接口 查询用户所有有效的营销活动
     * 
     * @param input
     * @return [{"DISCNT_LIST":[{},{},……]}]
     * @throws Exception
     */
    public IDataset queryUserDiscntAction(IData input) throws Exception
    {
        String x_getmode = input.getString("X_GETMODE");
        String serial_number = input.getString("SERIAL_NUMBER");
        String user_id = input.getString("USER_ID");

        IDataset dataset = AcctCall.queryUserDiscntAction(x_getmode, serial_number, user_id);
        return dataset.getData(0).getDataset("DISCNT_LIST");
    }
    
    public IDataset queryUserSaleActiveByTag(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        return UserSaleActiveInfoQry.queryUserSaleActiveByTag(user_id);
    }
    
    
    public static IDataset qrySaleActiveBySnFLUX(IData input, Pagination pagination) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String campnType = input.getString("CAMPN_TYPE");
        String productid = input.getString("PRODUCT_ID");

        return UserSaleActiveInfoQry.qrySaleActiveBySnFLUX(serialNumber, campnType, productid, pagination);
    }
    
    
}
