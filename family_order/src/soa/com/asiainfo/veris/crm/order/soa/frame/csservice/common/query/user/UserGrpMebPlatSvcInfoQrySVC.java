
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserGrpMebPlatSvcInfoQrySVC extends CSBizService
{

    public IDataset getUserProductBySnUid(IData param) throws Exception
    {
        String sn = param.getString("SERIAL_NUMBER", "");
        String ecUserId = param.getString("EC_USER_ID", "");
        String servCode = param.getString("SERV_CODE", "");
        return UserGrpMebPlatSvcInfoQry.getUserProductBySnUidPagination(sn, ecUserId, servCode, this.getPagination());
    }

}
