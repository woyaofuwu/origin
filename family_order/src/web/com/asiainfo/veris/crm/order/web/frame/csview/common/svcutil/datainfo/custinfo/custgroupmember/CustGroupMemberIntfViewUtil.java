
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.custgroupmember;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.custinfo.custgroupmember.CustGroupMemberIntf;

public class CustGroupMemberIntfViewUtil
{

    /**
     * 通过手机号码查询号码的资料归属集团
     * 
     * @param bc
     * @param serialNumber
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebsBySN(IBizCommon bc, String serialNumber, String routeId) throws Exception
    {
        IDataset infosDataset = CustGroupMemberIntf.qryGrpMebsBySN(bc, serialNumber, routeId);

        return infosDataset;
    }

}
