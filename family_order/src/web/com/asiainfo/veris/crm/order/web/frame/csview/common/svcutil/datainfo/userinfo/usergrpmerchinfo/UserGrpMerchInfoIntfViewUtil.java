
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.usergrpmerchinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.usergrpmerchinfo.UserGrpMerchInfoIntf;

public class UserGrpMerchInfoIntfViewUtil
{

    /**
     * 通过USER_ID查询用户订购的商品信息
     * 
     * @param bc
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryUserGrpMerchInfoByUserId(IBizCommon bc, String userId) throws Exception
    {
        return qryUserGrpMerchInfoByUserIdAndMerchSpecCodeAndStatus(bc, userId, null, null, true);
    }

    /**
     * 通过USER_ID,MERCH_SPEC_CODE,STATUC 查询用户订购的商品信息
     * 
     * @param bc
     * @param userId
     * @param merchSpecCode
     * @param status
     * @return
     * @throws Exception
     */
    public static IData qryUserGrpMerchInfoByUserIdAndMerchSpecCodeAndStatus(IBizCommon bc, String userId, String merchSpecCode, String status) throws Exception
    {
        return qryUserGrpMerchInfoByUserIdAndMerchSpecCodeAndStatus(bc, userId, merchSpecCode, status, true);
    }

    /**
     * 通过USER_ID,MERCH_SPEC_CODE,STATUC 查询用户订购的商品信息
     * 
     * @param bc
     * @param userId
     * @param merchSpecCode
     * @param status
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryUserGrpMerchInfoByUserIdAndMerchSpecCodeAndStatus(IBizCommon bc, String userId, String merchSpecCode, String status, boolean isThrowException) throws Exception
    {
        IDataset infosDataset = qryUserGrpMerchInfosByUserIdAndMerchSpecCodeAndStatus(bc, userId, merchSpecCode, status);

        if (IDataUtil.isNotEmpty(infosDataset))
        {
            return infosDataset.getData(0);
        }
        if (isThrowException)
        {
            CSViewException.apperr(CrmUserException.CRM_USER_540);
            return null;
        }
        return new DataMap();
    }

    /**
     * 通过USER_ID,MERCH_SPEC_CODE,STATUC 查询用户订购的商品信息
     * 
     * @param bc
     * @param userId
     * @param merchSpecCode
     * @param status
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpMerchInfosByUserIdAndMerchSpecCodeAndStatus(IBizCommon bc, String userId, String merchSpecCode, String status) throws Exception
    {
        IDataset infosDataset = UserGrpMerchInfoIntf.qryUserGrpMerchInfosByUserIdAndMerchSpecCodeAndStatus(bc, userId, merchSpecCode, status);

        return infosDataset;
    }
}
