
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserGrpPlatSvcInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getGrpPackagePlatService(IData input) throws Exception
    {
        String group_id = input.getString("GROUP_ID");
        IDataset dataset = UserGrpPlatSvcInfoQry.getGrpPackagePlatService(group_id);

        return dataset;
    }

    public IDataset getGrpPayItemInfoByUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String service_id = input.getString("SERVICE_ID");
        IData data = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(user_id, service_id);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    public IDataset getLxtGrpPlatSvcByUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        IDataset dataset = UserGrpPlatSvcInfoQry.getLxtGrpPlatSvcByUserId(user_id);
        return dataset;
    }

    public IDataset getUserAttrByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IDataset dataset = UserGrpPlatSvcInfoQry.getUserAttrByUserIda(userId);

        return dataset;
    }

    // getUserAttrByUserIdandSvc
    public IDataset getUserAttrByUserIdandSvc(IData input) throws Exception
    {
        IDataset dataset = UserGrpPlatSvcInfoQry.getUserAttrByUserIdandSvc(input);

        return dataset;
    }

    public IDataset getuserPlatsvcbyservcode(IData input) throws Exception
    {
        String serCode = input.getString("SERV_CODE");
        String biz_state_code = input.getString("BIZ_STATE_CODE", "");
        IDataset dataset = UserGrpPlatSvcInfoQry.getuserPlatsvcbyservcode(biz_state_code, serCode);
        return dataset;
    }

}
