
package com.asiainfo.veris.crm.order.web.group.param.groupsms;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    @Override
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);

        String user_id = data.getString("USER_ID", "");
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, user_id);

        IData map = new DataMap();
        map.put("DETMANAGERPHONE", userInfo.getString("RSRV_STR8", ""));
        map.put("DETMANAGERINFO", userInfo.getString("RSRV_STR7", ""));
        map.put("DETLINKPHONE", userInfo.getString("RSRV_STR9", ""));
        IData userParamData = IDataUtil.iDataA2iDataB(map, "ATTR_VALUE");
        transComboBoxValue(userParamData, getAttrItem());
        super.setAttrItem(userParamData);

        result.put("ATTRITEM", super.getAttrItem());
        return result;
    }

}
