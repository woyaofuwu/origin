
package com.asiainfo.veris.crm.order.web.group.param.imsvpn;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class CentUserParamInfo extends IProductParamDynamic
{
    /**
     * 获取初始属性值
     * 
     * @param bp
     * @param data
     * @return
     * @throws Throwable
     */
    public IData getAttrInitValue(IBizCommon bp, IData data) throws Throwable
    {
        IDataset dataset = AttrItemInfoIntfViewUtil.qryGrpProductItemAInfosByGrpProductIdAndEparchyCode(bp, data.getString("PRODUCT_ID", ""), data.getString("USER_EPARCHY_CODE"));
        IData attrData = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE", "ATTR_VALUE");
        return attrData;
    }

    // 原个性化参数保存在TF_F_USER_VPN里，因此还是从TF_F_USER_VPN表读取个性化
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData paraminfo = result.getData("PARAM_INFO");
        if (IDataUtil.isEmpty(paraminfo))
        {
            paraminfo = new DataMap();
        }
        IData attrItem = result.getData("ATTRITEM");

        String userId = data.getString("USER_ID", "");
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, userId);
        
        paraminfo.put("METHOD", "ChgUs");

        String custId = userInfo.getString("CUST_ID", "");
        IData inparme = new DataMap();
        inparme.put("CUST_ID", custId);
        
        // 查询VPN信息
        //IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, userId, false);
        IDataset userVpnData = CSViewCall.call(bp, "CS.UserVpnInfoQrySVC.getUserDesktopVPNInfoByCstId", inparme);
        if (IDataUtil.isEmpty(userVpnData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_421);
        }

        IData attrData = new DataMap();

        //String vpn_no = userVpnData.getString("VPN_NO", ""); // 集团VPMN编码
        String vpn_no = userVpnData.getData(0).getString("VPN_NO",""); // 集团VPMN编码
        attrData.put("VPN_NO", vpn_no);

        IData userattritem = IDataUtil.iDataA2iDataB(attrData, "ATTR_VALUE");
        
        super.transComboBoxValue(userattritem, getAttrItem());
        
        attrItem.putAll(userattritem);
        result.put("ATTRITEM", attrItem);

        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        parainfo.put("METHOD", "CtrUs");

        String cust_id = data.getString("CUST_ID", "");

        IData inparme = new DataMap();
        inparme.put("CUST_ID", cust_id);

        // 调用后台服务，查vpn信息
        //IDataset uservpninfos = CSViewCall.call(bp, "CS.UserVpnInfoQrySVC.getUserVPNInfoByCstId", inparme);
        IDataset uservpninfos = CSViewCall.call(bp, "CS.UserVpnInfoQrySVC.getUserDesktopVPNInfoByCstId", inparme);
        if (IDataUtil.isNotEmpty(uservpninfos))
        {
            parainfo.put("VPN_INFOS", uservpninfos);
        }

        result.put("PARAM_INFO", parainfo);
        return result;
    }
}
