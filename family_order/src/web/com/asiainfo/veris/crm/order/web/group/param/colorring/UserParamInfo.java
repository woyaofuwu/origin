
package com.asiainfo.veris.crm.order.web.group.param.colorring;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{
    @Override
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        // j2ee 彩铃制作费权限 hasPriv("GRP_COLORRING_FEE");
        String staffId = ((CSBasePage) bp).getVisit().getStaffId();
        String dataPriv = "GRP_COLORRING_FEE";
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, dataPriv);
        IData parainfo = new DataMap();
        if (!IDataUtil.isNotEmpty(result))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        parainfo.put("HAS_FEE_PRIV", bool);
        result.put("PARAM_INFO", parainfo);
        return result;
    }

    @Override
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = new DataMap();
        if (!IDataUtil.isNotEmpty(result))
            parainfo = result.getData("PARAM_INFO");

        // 支持老数据，如果在attr表无数据 则从user表预留字段获取数据
        IData attrItem = super.getAttrItem();
        if (attrItem == null || attrItem.isEmpty())
        {
            String user_id = data.getString("USER_ID", "");
            IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, user_id);

            IData map = new DataMap();
            map.put("PASSWORD", userInfo.getString("RSRV_STR4", ""));
            map.put("MANAGER_NAME", userInfo.getString("RSRV_STR6", ""));
            map.put("MANAGER_PHONE", userInfo.getString("RSRV_STR7", ""));
            map.put("MANAGER_INFO", userInfo.getString("RSRV_STR8", ""));
            super.setAttrItem(IDataUtil.iDataA2iDataB(map, "ATTR_VALUE"));
        }
        // j2ee 彩铃制作费权限 hasPriv("GRP_COLORRING_FEE");
        String staffId = ((CSBasePage) bp).getVisit().getStaffId();
        String dataPriv = "GRP_COLORRING_FEE";
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, dataPriv);
        parainfo.put("HAS_FEE_PRIV", bool);
        parainfo.put("SERIAL_NUMBER", data.getString("GRP_SN", ""));
        result.put("PARAM_INFO", parainfo);
        result.put("ATTRITEM", this.getAttrItem());
        return result;
    }

}
