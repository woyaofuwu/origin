
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.UserTelephoeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTelephoeInfoQry;

public class ChangeTrunkIdSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 进行校验判断
     * 
     * @param pd
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset checkInfo(IData inData) throws Exception
    {
        IDataset ajaxDataset = new DatasetList();
        IData data = new DataMap();
        IDataset userTrunkSet = UserTelephoeInfoQry.getUserTrunkInfoByUserId(inData.getString("USER_ID"));
        if (IDataUtil.isEmpty(userTrunkSet))
            CSAppException.apperr(UserTelephoeException.CRM_TELEPHOE_1);
        String str = "";
        for (int i = 0; i < userTrunkSet.size(); i++)
        {
            str = str + userTrunkSet.getData(i).getString("RSRV_STR3") + ",";
        }

        if (str.length() >= 1)
        {
            data.put("TRUNK_ID", str.substring(0, str.length() - 1));
            data.put("SWITCH_ID", userTrunkSet.getData(0).getString("RSRV_STR2"));
        }
        ajaxDataset.add(data);
        return ajaxDataset;
    }

}
