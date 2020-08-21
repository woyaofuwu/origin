
package com.asiainfo.veris.crm.order.web.group.param.colorring;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class MemParamInfo extends IProductParamDynamic
{

    /**
     * 成员变更初始化
     * 
     * @author sht
     */
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        String eparchyCode = data.getString("MEB_EPARCHY_CODE", "");
        String memUserId = data.getString("MEB_USER_ID", "");
        IData param = new DataMap();
        param.put("USER_ID", memUserId);
        param.put("RSRV_VALUE_CODE", "DLMR");
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset userresinfo = CSViewCall.call(bp, "CS.UserOtherInfoQrySVC.getUserOtherByUseridRsrvcode", param);

        IData data1 = new DataMap();
        if (IDataUtil.isNotEmpty(userresinfo))
        {
            data1.put("CANCEL_LING", "1");
        }
        else
        {
            data1.put("CANCEL_LING", "0");
        }
        IData userattritem = IDataUtil.iDataA2iDataB(data1, "ATTR_VALUE");
        transComboBoxValue(userattritem, getAttrItem());
        super.setAttrItem(userattritem);

        result.put("ATTRITEM", this.getAttrItem());
        return result;
    }
}
