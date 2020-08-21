
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class SvcStateInfoQry
{

    /**
     * 获取用户状态
     * 
     * @return userStateName
     * @throws Exception
     */
    public static String getUserStateName(IData param) throws Exception
    { 
        return getUserMainSvcState(param.getString("USER_ID"),param.getString("USER_STATE_CODESET"));

    }

    public static String getUserMainSvcState(String userId, String stateCode) throws Exception
    {

        String userStateName = "";
        String svcId = "";
        IData inparam = new DataMap();

        inparam.put("USER_ID", userId); 
        
        IDataset userSvcInfo= Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_ALL", inparam);
        
        IDataset tmp = DataHelper.filter(userSvcInfo, "MAIN_TAG=1");
        if (tmp.size() > 0)
            svcId =  tmp.getData(0).getString("SERVICE_ID", ""); 
        
        IDataset offerStaInfos = UpcCall.qryOfferFuncStaByAnyOfferIdStatus(svcId, BofConst.ELEMENT_TYPE_CODE_SVC, stateCode);
        if (offerStaInfos.size() > 0)
        	userStateName = offerStaInfos.getData(0).getString("STATUS_NAME", ""); 
 

        return userStateName;

    }
    
    /**
     * 获取用户状态
     * 
     * @return userStateName
     * @throws Exception
     */
    public static String getUserStateName(String userId, String userStateCodeset) throws Exception
    { 
        return getUserMainSvcState(userId, userStateCodeset);

    }

    public static IDataset queryTradeTypeSvcStates(String tradeTypeCode, String brandCode, String productId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_S_TRADE_SVCSTATE", "SEL_BY_PK", param);
    }
}
