
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.staff.staffbbossinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class StaffBBossInfoIntf
{

    /**
     * 查询工号的联系人电话及其总部用户名
     * 
     * @param bc
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IDataset qryStaffBBossInfosByStaffId(IBizCommon bc, String staffId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("STAFF_ID", staffId);
        return CSViewCall.call(bc, "CS.StaffInfoQrySVC.queryStaffInfoForBBoss", inparam);
    }
}
