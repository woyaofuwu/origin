
package com.asiainfo.veris.crm.order.web.group.param.wlan;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        String productId = parainfo.getString("PRODUCT_ID");
        String userId = data.getString("USER_ID");
        String eparchyCode = data.getString("USER_EPARCHY_CODE");

        parainfo.put("METHOD", "ChgUs");

        IData userParam = new DataMap();
        userParam.put("USER_ID", userId);
        userParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSViewCall.call(bp, "CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userParam);

        // 管理员信息
        if (null != userInfo && userInfo.size() > 0)
        {
            IData userData = userInfo.getData(0);

            parainfo.put("DETMANAGERPHONE", userData.getString("RSRV_STR8", ""));
            parainfo.put("DETMANAGERINFO", userData.getString("RSRV_STR7", ""));
            parainfo.put("DETADDRESS", userData.getString("RSRV_STR9", ""));

        }

        // 调用后台服务
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "GRP_WLAN");

        IDataset otherDataset = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);

        // 判断OTHER表中有没有数据
        if (IDataUtil.isNotEmpty(otherDataset))
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < otherDataset.size(); i++)
            {
                IData otherInfo = otherDataset.getData(i);
                IData otherData = new DataMap();

                otherData.put("pam_GRP_WLAN_CODE", otherInfo.getString("RSRV_VALUE", ""));
                otherData.put("pam_GRP_WLAN", otherInfo.getString("RSRV_STR1", ""));
                otherData.put("pam_NET_LINE", otherInfo.getString("RSRV_STR2", ""));
                otherData.put("pam_PRICE", otherInfo.getString("RSRV_STR3", ""));
                otherData.put("pam_DIS_DATA", otherInfo.getString("RSRV_STR4", ""));
                otherData.put("pam_COMPANY_NAME_CODE", otherInfo.getString("RSRV_STR8", ""));
                otherData.put("pam_COMPANY_NAME", otherInfo.getString("RSRV_STR9", ""));
                otherData.put("pam_REMARK", otherInfo.getString("RSRV_STR10", ""));
                otherData.put("INSTID", otherInfo.getString("INST_ID", ""));
                dataset.add(otherData);
            }
            parainfo.put("WLAN_INFO", dataset);
        }

        // 绑定下拉框的值
        IDataset itemdatas = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productId, "P", "GRP_WLAN", "ZZZZ");

        parainfo.put("WLAN_INFOS", itemdatas);
        //        
        // //权限控制优惠价格
        //        
        // if (pd.getContext().hasPriv("COMPANY_LED")){
        // IDataset datas = ParamQry.getCommpara(pd, "CSM", "556", "COMPANY_LED", pd.getContext().getEpachyId());
        // setDiscountdata(datas);
        // }else if(pd.getContext().hasPriv("PROVINCE_LED")){
        // IDataset datas = ParamQry.getCommpara(pd, "CSM", "556", "PROVINCE_LED", pd.getContext().getEpachyId());
        // setDiscountdata(datas);
        // }else if(pd.getContext().hasPriv("COM_TREE_LED")){
        // IDataset datas = ParamQry.getCommpara(pd, "CSM", "556", "COM_TREE_LED", pd.getContext().getEpachyId());
        // setDiscountdata(datas);
        // }

        // 设置宽带资费
        IData commparam = new DataMap();
        commparam.put("PARAM_CODE", productId);
        commparam.put("SUBSYS_CODE", "CSM");
        commparam.put("PARAM_ATTR", "555");
        commparam.put("EPARCHY_CODE", eparchyCode);
        IDataset bandwidthdatas = CSViewCall.call(bp, "CS.CommparaInfoQrySVC.getCommpara", commparam);
        parainfo.put("BANDWIDTHDATAS", bandwidthdatas);

        // 落地分公司信息
        IDataset companyinfos = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productId, "P", "COMPANY_NAME", "ZZZZ");
        
        parainfo.put("COMPANY_INFOS", companyinfos);
        
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        String eparchyCode = data.getString("USER_EPARCHY_CODE");

        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        String productId = parainfo.getString("PRODUCT_ID");

        // 绑定下拉框的值
        IDataset itemdatas = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productId, "P", "GRP_WLAN", "ZZZZ");

        parainfo.put("WLAN_INFOS", itemdatas);

        // 权限控制优惠价格
        IData discountparam = new DataMap();
        discountparam.put("SUBSYS_CODE", "CSM");
        discountparam.put("PARAM_ATTR", "556");
        discountparam.put("EPARCHY_CODE", eparchyCode);

        discountparam.put("PARAM_CODE", "COMPANY_LED");// PROVINCE_LED COM_TREE_LED

        IDataset discountDataset = CSViewCall.call(bp, "CS.CommparaInfoQrySVC.getCommpara", discountparam);
        parainfo.put("DISCOUNTDATA", discountDataset);

        // 设置宽带资费
        IData commparam = new DataMap();
        commparam.put("PARAM_CODE", productId);
        commparam.put("SUBSYS_CODE", "CSM");
        commparam.put("PARAM_ATTR", "555");
        commparam.put("EPARCHY_CODE", eparchyCode);
        IDataset bandwidthdatas = CSViewCall.call(bp, "CS.CommparaInfoQrySVC.getCommpara", commparam);
        parainfo.put("BANDWIDTHDATAS", bandwidthdatas);

        // 落地分公司信息
        IDataset companyinfos = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productId, "P", "COMPANY_NAME", "ZZZZ");
        
        parainfo.put("COMPANY_INFOS", companyinfos);
        parainfo.put("METHOD", "CtrUs");
        return result;
    }
}
