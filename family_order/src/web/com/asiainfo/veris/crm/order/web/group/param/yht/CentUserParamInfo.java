
package com.asiainfo.veris.crm.order.web.group.param.yht;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class CentUserParamInfo extends IProductParamDynamic
{
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        parainfo.put("METHOD", "CtrUs");

        // 查询集团客户信息
        String grpId = data.getString("GROUP_ID", "");
        IData grpCustInfoData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(bp, grpId);
        String cust_id = grpCustInfoData.getString("CUST_ID", "");

        IData inparme = new DataMap();
        inparme.put("CUST_ID", cust_id);

        // 调用后台服务，查vpn信息
        IDataset uservpninfos = CSViewCall.call(bp, "CS.UserVpnInfoQrySVC.getUserVPNInfoByCstId", inparme);
        if (IDataUtil.isNotEmpty(uservpninfos))
        {
            parainfo.put("VPN_INFOS", uservpninfos);
        }

        result.put("PARAM_INFO", parainfo);
        return result;
    }
}
