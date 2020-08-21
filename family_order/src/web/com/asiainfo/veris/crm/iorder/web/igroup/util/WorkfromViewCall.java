package com.asiainfo.veris.crm.iorder.web.igroup.util;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public class WorkfromViewCall {
    public static IData qryWorkformSubscribeByIbsysid(IBizCommon bc, String ibsysid) throws Exception {
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        return CSViewCall.callone(bc, "SS.WorkformSubscribeSVC.qryWorkformSubscribeByIbsysid", data);
    }

    public static IData qryworkformEOMSBySerialNo(IBizCommon bc, String serialNo) throws Exception {
        IData data = new DataMap();
        data.put("SERIALNO", serialNo);
        return CSViewCall.callone(bc, "SS.WorkformEomsSVC.qryworkformEOMSBySerialNo", data);
    }

    public static IData qryEomsDetailByIbsysidAndProductNo(IBizCommon bc, String ibsysid, String productNo) throws Exception {
        IData data = new DataMap();
        data.put("PRODUCT_NO", productNo);
        data.put("IBSYSID", ibsysid);
        return CSViewCall.callone(bc, "SS.WorkformEomsSVC.qryEomsDetailByIbsysidAndProductNo", data);
    }

    public static IDataset qryWorkformAttrByIbsysid(IBizCommon bc, String ibsysid) throws Exception {
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        return CSViewCall.call(bc, "SS.WorkformAttrSVC.qryAttrByIbsysid", data);

    }

    public static IData qryEopProductByIbsysId(IBizCommon bc, String ibsysid, String recordNum) throws Exception {
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("RECORD_NUM", recordNum);
        return CSViewCall.callone(bc, "SS.WorkformProductSVC.qryEopProductByIbsysId", data);

    }

    public static IDataset qryNodeByIbsysidNodeTimeDesc(IBizCommon bc, String ibsysid, String nodeId) throws Exception {
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("NODE_ID", nodeId);
        return CSViewCall.call(bc, "SS.WorkformNodeSVC.qryNodeByIbsysidNodeTimeDesc", data);

    }

    public static IDataset qryBusiTypeByProductId(IBizCommon bc, String operType, String productId, String areaId, String inModeCode) throws Exception {
        IData data = new DataMap();
        data.put("OPER_TYPE", operType);
        data.put("PRODUCT_ID", productId);
        data.put("AREA_ID", areaId);
        data.put("IN_MODE_CODE", inModeCode);
        return CSViewCall.call(bc, "SS.BusiFlowReleSVC.qryBusiTypeByProductId", data);
    }

    public static IData getOperTypeByTempletId(IBizCommon bc, String templetId) throws Exception {
        IData data = new DataMap();
        data.put("BUSI_CODE", templetId);

        IDataset resultDataset = CSViewCall.call(bc, "SS.BusiFlowReleSVC.getOperTypeByTempletId", data);
        if (IDataUtil.isEmpty(resultDataset)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据BUSI_CODE=" + "templetId，查TD_B_EOP_BUSI_FLOW_RELE表数据不存在！");
        }
        return resultDataset.first();
    }

    public static IDataset qryDataLineInfoByIbsysid(IBizCommon bc, String ibsysid) throws Exception {
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        return CSViewCall.call(bc, "SS.WorkformAttrSVC.querylineAttrByIbsysid", data);
    }

    public static IDataset qryOtherInfoByIbsysidAndAttrCode(IBizCommon bc, String ibsysid, String attrCode) throws Exception {
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("ATTR_CODE", attrCode);
        return CSViewCall.call(bc, "SS.WorkformOtherSVC.qryLastInfoByIbsysidAndAttrCode", data);
    }

    public static IDataset qryLastInfoByIbsysidAndAttrCode(IBizCommon bc, String ibsysid, String attrCode) throws Exception {
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("ATTR_CODE", attrCode);
        return CSViewCall.call(bc, "SS.WorkformOtherSVC.qryLastInfoByIbsysidAndAttrCode", data);
    }

    public static String getLastAttrValue(IBizCommon bc, String ibsysid, String attrCode) throws Exception {
        String val = "";
        IDataset otherInfos = WorkfromViewCall.qryLastInfoByIbsysidAndAttrCode(bc, ibsysid, attrCode);
        if (IDataUtil.isNotEmpty(otherInfos)) {
            val = otherInfos.getData(0).getString("ATTR_VALUE");
        }
        return val;
    }

}
