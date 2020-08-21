
package com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen.ActiveSaleCardOpenSVC;

public class ActiveSaleCardOpenIntfFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset userInfo = UserInfoQry.getAllUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {

            IData inData = userInfo.getData(0);
            ActiveSaleCardOpenSVC checkData = new ActiveSaleCardOpenSVC();
            checkData.checkInfo(inData);
        }

    }
}
