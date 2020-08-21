
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.usergrpmerchmebinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.usergrpmerchmebinfo.UserGrpMerchMebInfoIntf;

public class UserGrpMerchMebInfoIntfViewUtil
{

    /**
     * 通过成员USERID和集团的EC_USER_ID查询成员订购的bboss订购信息
     * 
     * @param bc
     * @param ecUserId
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserGrpMerchMebInfoByEcUserIdAndUserId(IBizCommon bc, String ecUserId, String userId, String routeId) throws Exception
    {
        IDataset infosDataset = qryUserGrpMerchMebInfosByEcUserIdAndUserId(bc, ecUserId, userId, routeId);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            return infosDataset.getData(0);
        }
        return null;
    }

    /**
     * 通过成员USERID,成员号码SERIAL_NUMBER和集团的EC_USER_ID查询成员订购的bboss订购信息
     * 
     * @param bc
     * @param ecUserId
     * @param userId
     * @param serialNumber
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserGrpMerchMebInfoByEcUserIdAndUserIdSerialNumber(IBizCommon bc, String ecUserId, String userId, String serialNumber, String routeId) throws Exception
    {
        IDataset infosDataset = qryUserGrpMerchMebInfosByEcUserIdAndUserIdSerialNumber(bc, ecUserId, userId, serialNumber, routeId);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            return infosDataset.getData(0);
        }
        return null;
    }

    /**
     * 通过成员USERID和集团的EC_USER_ID查询成员订购的bboss订购信息
     * 
     * @param bc
     * @param ecUserId
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpMerchMebInfosByEcUserIdAndUserId(IBizCommon bc, String ecUserId, String userId, String routeId) throws Exception
    {
        IDataset infosDataset = UserGrpMerchMebInfoIntf.qryUserGrpMerchMebInfosByEcUserIdAndUserIdSerialNumber(bc, ecUserId, userId, null, routeId);

        return infosDataset;
    }

    /**
     * 通过成员USERID,成员号码SERIAL_NUMBER和集团的EC_USER_ID查询成员订购的bboss订购信息
     * 
     * @param bc
     * @param ecUserId
     * @param userId
     * @param serialNumber
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpMerchMebInfosByEcUserIdAndUserIdSerialNumber(IBizCommon bc, String ecUserId, String userId, String serialNumber, String routeId) throws Exception
    {
        IDataset infosDataset = UserGrpMerchMebInfoIntf.qryUserGrpMerchMebInfosByEcUserIdAndUserIdSerialNumber(bc, ecUserId, userId, serialNumber, routeId);

        return infosDataset;
    }

}
