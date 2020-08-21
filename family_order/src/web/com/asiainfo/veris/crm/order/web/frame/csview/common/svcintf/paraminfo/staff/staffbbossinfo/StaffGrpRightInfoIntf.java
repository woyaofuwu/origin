
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.staff.staffbbossinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class StaffGrpRightInfoIntf
{

    public static IDataset qryGrpRightInfosByStaffIdRightCodeUserProductCode(IBizCommon bc, String staffId, String rightCode, String userProductCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("STAFF_ID", staffId);
        inparam.put("RIGHT_CODE", rightCode);
        inparam.put("USER_PRODUCT_CODE", userProductCode);
        return CSViewCall.call(bc, "CS.StaffInfoQrySVC.queryGrpRightByIdCode", inparam);
    }
}
