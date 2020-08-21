
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.staff.staffbbossinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.StaffException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.staff.staffbbossinfo.StaffBBossInfoIntf;

public class StaffBBossInfoIntfViewUtil
{

    /**
     * 查询BBOSS工号的联系人电话及其总部用户名
     * 
     * @param bc
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IData qryBBossAttrInfoByProductIdAndOperTypeBizType(IBizCommon bc, String staffId) throws Exception
    {
        return qryBBossAttrInfoByProductIdAndOperTypeBizType(bc, staffId, true);
    }

    /**
     * 查询BBOSS工号的联系人电话及其总部用户名
     * 
     * @param bc
     * @param staffId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryBBossAttrInfoByProductIdAndOperTypeBizType(IBizCommon bc, String staffId, boolean isThrowException) throws Exception
    {
        IDataset infosDataset = StaffBBossInfoIntf.qryStaffBBossInfosByStaffId(bc, staffId);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            return infosDataset.getData(0);
        }

        if (isThrowException)
        {
            CSViewException.apperr(StaffException.CRM_STAFF_1, "查询STAFF_BBOSS信息失败", staffId);
            return null;
        }
        return new DataMap();

    }

    /**
     * 查询BBOSS工号的联系人电话及其总部用户名
     * 
     * @param bc
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrInfosByProductIdAndOperTypeBizType(IBizCommon bc, String staffId) throws Exception
    {
        IDataset infosDataset = StaffBBossInfoIntf.qryStaffBBossInfosByStaffId(bc, staffId);
        return infosDataset;
    }

}
