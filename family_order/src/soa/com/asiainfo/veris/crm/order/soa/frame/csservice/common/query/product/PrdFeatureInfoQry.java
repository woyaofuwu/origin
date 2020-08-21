
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/***
 * 
 */
public class PrdFeatureInfoQry
{

    public static IDataset queryFeatureByFIdAndEIdAndType(String elementId, String elementTypeCode, String featureId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_TYPE_CODE", elementTypeCode);
        data.put("ELEMENT_ID", elementId);
        data.put("FEATURE_ID", featureId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        IDataset result = Dao.qryByCode("TD_B_PRD_FEATURE", "SEL_BY_FEAID_EID_TYPE", data);
        return result;
    }

    public static IDataset queryFeatureByIdAndType(String elementId, String elementTypeCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_TYPE_CODE", elementTypeCode);
        data.put("ELEMENT_ID", elementId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        IDataset result = Dao.qryByCode("TD_B_PRD_FEATURE", "SEL_BY_ID_TYPE", data);
        return result;
    }
}
