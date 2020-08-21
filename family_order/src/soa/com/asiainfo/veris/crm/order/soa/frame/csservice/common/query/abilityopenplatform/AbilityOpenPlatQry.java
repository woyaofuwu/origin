package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Created by zhaohj3 on 2019/5/28.
 */
public class AbilityOpenPlatQry {

    public static IDataset queryListInfo(String eparchyCode, String ctrmProductId) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("CTRM_PRODUCT_ID", ctrmProductId);
        return Dao.qryByCodeParser("TD_B_CTRM_RELATION", "SEL_BY_CTRM_PRODUCT_ID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryContractInfo(String contractId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("CONTRACT_ID", contractId);
        return Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_CTRM_CONTRACT_BY_CONID", inparam, Route.CONN_CRM_CEN);
    }
}
