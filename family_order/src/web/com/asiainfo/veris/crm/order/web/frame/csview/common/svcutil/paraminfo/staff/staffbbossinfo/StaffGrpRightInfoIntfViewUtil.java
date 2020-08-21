
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.staff.staffbbossinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.staff.staffbbossinfo.StaffGrpRightInfoIntf;

public class StaffGrpRightInfoIntfViewUtil
{
    /**
     * CS.StaffInfoQrySVC.queryGrpRightByIdCode
     * 
     * @param bc
     * @param staffId
     * @param rightCode
     * @param userProductCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpRightInfosByStaffIdRightCodeUserProductCode(IBizCommon bc, String staffId, String rightCode, String userProductCode) throws Exception
    {
        IDataset infosDataset = StaffGrpRightInfoIntf.qryGrpRightInfosByStaffIdRightCodeUserProductCode(bc, staffId, rightCode, userProductCode);
        return infosDataset;
    }

}
