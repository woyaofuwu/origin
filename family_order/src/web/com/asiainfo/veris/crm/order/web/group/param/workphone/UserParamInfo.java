
package com.asiainfo.veris.crm.order.web.group.param.workphone;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{
    @Override
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        String eparchyCode = data.getString("USER_EPARCHY_CODE");

        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        return result;
    }

    @Override
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        String eparchyCode = data.getString("USER_EPARCHY_CODE");
        String productId = parainfo.getString("PRODUCT_ID");
        String userId = data.getString("USER_ID");

        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        
        IData userParam = new DataMap();
        userParam.put("USER_ID", userId);
        userParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSViewCall.call(bp, "CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userParam);

        // 获取新增时选择的产品类型
        if (null != userInfo && userInfo.size() > 0)
        {
            IData userData = userInfo.getData(0);

            parainfo.put("TD_S_WORKPHONE_STATE", userData.getString("RSRV_STR6", ""));

        }
        
        IData userattritem = IDataUtil.iDataA2iDataB(parainfo, "ATTR_VALUE"); // 转格式为可ognl:getAttrItemValue('CALL_AREA_TYPE','ATTR_VALUE')
        transComboBoxValue(userattritem, getAttrItem());
        getAttrItem().putAll(userattritem);
        super.setAttrItem(userattritem);
        super.setAttrItemSet(IDataUtil.iData2iDataset(parainfo, "ATTR_CODE", "ATTR_VALUE"));
        
        result.put("PARAM_INFO", parainfo);
        result.put("ATTRITEM", this.getAttrItem());
        result.put("ATTRITEMSET", this.getAttrItemSet());
        return result;
    }

}
