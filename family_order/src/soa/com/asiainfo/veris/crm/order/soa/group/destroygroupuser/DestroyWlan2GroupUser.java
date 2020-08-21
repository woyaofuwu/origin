
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyWlan2GroupUser extends DestroyGroupUser
{
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataOther();
    }

    /**
     * 其它台帐处理
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset otherInfoList = UserOtherInfoQry.getUserOtherByUserRsrvValueCode(reqData.getUca().getUserId(), "GRP_WLAN", null);

        IDataset otherDataset = new DatasetList();

        if (IDataUtil.isNotEmpty(otherInfoList))
        {
            for (int i = 0; i < otherInfoList.size(); i++)
            {
                IData tempOtherData = otherInfoList.getData(i);
                tempOtherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                tempOtherData.put("END_DATE", getAcceptTime());
                tempOtherData.put("OPER_CODE", "07");

                otherDataset.add(tempOtherData);
            }
        }
    }

    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
        super.setTradeSvc(map);
        map.put("OPER_CODE", "07"); // 供服开使用
    }
}
