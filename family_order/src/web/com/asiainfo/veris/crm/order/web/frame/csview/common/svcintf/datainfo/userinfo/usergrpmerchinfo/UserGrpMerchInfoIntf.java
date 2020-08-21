
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.usergrpmerchinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserGrpMerchInfoIntf
{

    /**
     * 通过USER_ID,MERCH_SPEC_CODE,STATUC 查询用户订购的商品信息
     * 
     * @param bc
     * @param userId
     * @param merchSpecCode
     *            （可为空）
     * @param status
     *            （可为空）
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpMerchInfosByUserIdAndMerchSpecCodeAndStatus(IBizCommon bc, String userId, String merchSpecCode, String status) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("MERCH_SPEC_CODE", merchSpecCode);
        inparam.put("STATUS", status);
        return CSViewCall.call(bc, "CS.UserGrpMerchInfoQrySVC.qryMerchInfoByUserIdMerchSpecStatus", inparam);
    }

}
