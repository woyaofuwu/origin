
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserValidCodeInfoQry
{
    public static IData getUserValidCode(String userId, String serialNumber, String productId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();

        param.put("PRODUCT_ID", productId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset results = Dao.qryByCode("TF_F_USER_VALIDCODE", "SEL_BY_SN", param);

        return IDataUtil.isNotEmpty(results) ? results.getData(0) : null;
    }

    public static void insertUserValidCode(String serialNumber, String userId, String productId, String packageId, String smsCode, String eparchyCode) throws Exception
    {
        IData condData = new DataMap();

        condData.put("SERIAL_NUMBER", serialNumber);
        condData.put("USER_ID", userId);
        condData.put("PRODUCT_ID", productId);
        condData.put("PACKAGE_ID", packageId);
        condData.put("SMS_CODE", smsCode);
        condData.put("CHECK_COUNT", "1");
        condData.put("EPARCHY_CODE", eparchyCode);
        condData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        condData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        condData.put("INDATE", SysDateMgr.getSysTime());
        condData.put("UPDATE_TIME", SysDateMgr.getSysTime());

        Dao.insert("TF_F_USER_VALIDCODE", condData);
    }

    public static void updateUserValidCode(String serialNumber, String userId, String productId, String smsCode, int checkCount, String eparchyCode) throws Exception
    {
        IData params = new DataMap();

        params.put("SERIAL_NUMBER", serialNumber);
        params.put("USER_ID", userId);
        params.put("PRODUCT_ID", productId);
        params.put("SMS_CODE", smsCode);
        params.put("CHECK_COUNT", checkCount);
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("UPDATE_TIME", SysDateMgr.getSysTime());

        Dao.executeUpdateByCodeCode("TF_F_USER_VALIDCODE", "UPDATE_BY_SN", params);
    }
}
