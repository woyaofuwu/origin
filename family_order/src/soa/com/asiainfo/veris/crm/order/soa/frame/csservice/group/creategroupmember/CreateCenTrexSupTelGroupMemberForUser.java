
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateCenTrexSupTelGroupMemberForUser extends GroupOrderBaseBean
{
    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 创建集团成员信息
        IDataset superParams = new DatasetList(map.getString("SUPERNUMBER", ""));
        for (int i = 0; i < superParams.size(); i++)
        {
            IData tempParam = superParams.getData(0);
            tempParam = IDataUtil.replaceIDataKeyDelPrefix(tempParam, "pam_");
            IDataset cstGrpOutDataset = GrpInvoker.ivkProduct(tempParam, BizCtrlType.CreateMember, "CreateClass");
        }
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "2434";
    }

}
