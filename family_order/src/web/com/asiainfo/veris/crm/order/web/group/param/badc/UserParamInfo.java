
package com.asiainfo.veris.crm.order.web.group.param.badc;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo.UserOtherInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData paramInfo = new DataMap();
        IData map = new DataMap();

        String grpUserId = data.getString("USER_ID", "");
        String rsrvValueCode = "OFEE";
        IDataset userOther = UserOtherInfoIntfViewUtil.qryGrpUserOtherInfosByUserIdAndRsrvValueCode(bp, grpUserId, rsrvValueCode);
        if (IDataUtil.isNotEmpty(userOther))
        {
            map.put("NOTIN_HIRE_FEE", userOther.getData(0).getString("RSRV_VALUE"));
            map.put("NOTIN_FEE_CYCLE", userOther.getData(0).getString("RSRV_STR1"));

        }
        paramInfo.put("PARAM_INFO", map);
        return paramInfo;
    }

}
