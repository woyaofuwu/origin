
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserGrpMerchMebInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * @author ft
     * @description 根据集团用户编号和成员用户编号查询集团与成员用户关系表
     */
    public IDataset qryMerchMebInfoByEcUserIdSnUserId(IData param) throws Exception
    {
        String ecUserId = param.getString("EC_USER_ID", "");
        String serialNumber = param.getString("SERIAL_NUMBER", "");
        String userId = param.getString("USER_ID", "");

        return UserGrpMerchMebInfoQry.qryMerchMebInfoByEcUserIdSnUserId(userId, serialNumber, ecUserId, null);
    }

}
