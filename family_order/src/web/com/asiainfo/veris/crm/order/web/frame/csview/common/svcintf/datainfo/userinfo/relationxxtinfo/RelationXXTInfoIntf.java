
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationxxtinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class RelationXXTInfoIntf
{

    /**
     * 查询校讯通学生参数信息
     *
     * @param bc
     * @param serialNumber
     * @param userIdA
     * @param serialNumberB
     * @param type
     * @return
     * @throws Exception
     */

    public static IDataset qryStuDisParamInfoBySnAUserIdASnBDsiType(IBizCommon bc, String serialNumber, String userIdA, String serialNumberB, String type) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("EC_USER_ID", userIdA);
        inparam.put("SERIAL_NUMBER_B", serialNumberB);
        inparam.put("ELEMENT_TYPE_CODE", type);
        return CSViewCall.call(bc, "CS.RelaXXTInfoQrySVC.qryStuDisParamInfoBySnAUserIdASnBDsiType", inparam);
    }

    /**
     * 查询同一集团 同一个付费号下所有代付号码
     */
    public static IDataset queryMemInfoBySNandUserIdA(IBizCommon bc, String serialNumber, String userIdA) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("EC_USER_ID", userIdA);
        return CSViewCall.call(bc, "CS.RelaXXTInfoQrySVC.queryMemInfoBySNandUserIdA", inparam);
    }
    /**
     * 查询同一集团 同一个付费号下所有代付号码信息
     */
    public static IDataset qryMemInfoBySNandUserIdA(IBizCommon bc, String serialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        return CSViewCall.call(bc, "CS.RelaXXTInfoQrySVC.qryMemInfoBySNandUserIdA", inparam);
    }

    /**
     *
     */
    public static IDataset queryXxtInfoBySnaGroup(IBizCommon bc, String serialNumbera, String serialNumberb, String ecUserId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumbera);
        inparam.put("SERIAL_NUMBER_A", serialNumbera);
        inparam.put("SERIAL_NUMBER_B", serialNumberb);
        inparam.put("EC_USER_ID", ecUserId);
        return CSViewCall.call(bc, "CS.RelaXXTInfoQrySVC.queryXxtInfoBySnaGroup", inparam);
    }

    public static IDataset qrymsisdn(IBizCommon bc, String serialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        return CSViewCall.call(bc, "CS.RelaXXTInfoQrySVC.qrymsisdn", inparam);
    }
    public static IDataset qryMemInfoBySNForUIPDestroy(IBizCommon bc, String serialNumber, String ecUserId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("USER_ID_A", ecUserId);
        return CSViewCall.call(bc, "CS.RelaXXTInfoQrySVC.qryMemInfoBySNForUIPDestroy", inparam);
    }
}
