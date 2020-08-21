
package com.asiainfo.veris.crm.order.web.group.param.cttline;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.attriteminfo.AttrItemInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        String userId = data.getString("USER_ID");
        parainfo.put("NOTIN_METHOD_NAME", "ChgUs");

        IData userParam = new DataMap();
        userParam.put("USER_ID", userId);
        userParam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset userInfoList = CSViewCall.call(bp, "CS.UcaInfoQrySVC.qryUserInfoByUserId", userParam);
        // 将用户开通专线的状态返回至前台
        if (null != userInfoList && userInfoList.size() > 0)
        {
            IData userInfo = (IData) userInfoList.get(0);
            String userState = userInfo.getString("RSRV_STR1");
            parainfo.put("NOTIN_USER_DATELINE_STATE", userState);

        }

        String productNo = parainfo.getString("PRODUCT_ID");
        
        IDataset productType = AttrItemInfoIntf.qryAttrItemBInfoByIdAndIdTypeAttrCode(bp, productNo, "P", "PRODUCT_TYPE", "ZZZZ");
        
        parainfo.put("PRODUCT_TYPE", productType);

        
        IDataset dataLineSta = AttrItemInfoIntf.qryAttrItemBInfoByIdAndIdTypeAttrCode(bp, productNo, "P", "USER_STATE", "ZZZZ");
        
        parainfo.put("DATALINE_STATE", dataLineSta);


        // 调用后台服务,查询OTHER表信息
        IData inparmePath = new DataMap();
        inparmePath.put("USER_ID", userId);
        inparmePath.put("RSRV_VALUE_CODE", "PATH");
        IDataset pathInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmePath);

        if (null != pathInfo && pathInfo.size() > 0)
        {
            IData userAttrData = (IData) pathInfo.get(0);
            parainfo.put("NOTIN_PATH", userAttrData.get("RSRV_STR10"));
        }

        IData inparmeAddress = new DataMap();
        inparmeAddress.put("USER_ID", userId);
        inparmeAddress.put("RSRV_VALUE_CODE", "ADDRESS");
        IDataset addressInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeAddress);

        if (null != addressInfo && addressInfo.size() > 0)
        {
            IData userAttrData = (IData) addressInfo.get(0);
            parainfo.put("NOTIN_INSTALL_ADDRESS", userAttrData.get("RSRV_STR10"));
        }

        IData inparmeWide = new DataMap();
        inparmeWide.put("USER_ID", userId);
        inparmeWide.put("RSRV_VALUE_CODE", "BINDWIDE");
        IDataset wideInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeWide);

        if (null != wideInfo && wideInfo.size() > 0)
        {
            IDataset datasetWide = new DatasetList();
            for (int i = 0; i < wideInfo.size(); i++)
            {
                IData userAttrData = (IData) wideInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_NOTIN_WIDE_ACCT_ID", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_OLD_WIDE_ACCT_ID", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_WIDE_MONTH", userAttrData.get("RSRV_STR1"));
                userAttr.put("pam_NOTIN_WIDE_NET_LINE", userAttrData.get("RSRV_STR2"));
                datasetWide.add(userAttr);

            }
            parainfo.put("WIDE_INFO", datasetWide);
            parainfo.put("NOTIN_OLD_WideData", datasetWide);
        }

        IData inparmeFixed = new DataMap();
        inparmeFixed.put("USER_ID", userId);
        inparmeFixed.put("RSRV_VALUE_CODE", "BINDFIXED");
        IDataset fixedInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeFixed);

        if (null != fixedInfo && fixedInfo.size() > 0)
        {
            IDataset datasetFixed = new DatasetList();
            for (int i = 0; i < fixedInfo.size(); i++)
            {
                IData userAttrData = (IData) fixedInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_NOTIN_FIXED_PHONE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_OLD_FIXED_PHONE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_FIXED_MONEY", userAttrData.get("RSRV_STR1"));
                datasetFixed.add(userAttr);

            }
            parainfo.put("FIXED_INFO", datasetFixed);
            parainfo.put("NOTIN_OLD_FixedData", datasetFixed);
        }

        IData inparmeSerial = new DataMap();
        inparmeSerial.put("USER_ID", userId);
        inparmeSerial.put("RSRV_VALUE_CODE", "BINDSERIAL");
        IDataset serialInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeSerial);

        if (null != serialInfo && serialInfo.size() > 0)
        {
            IDataset datasetSerial = new DatasetList();
            for (int i = 0; i < serialInfo.size(); i++)
            {
                IData userAttrData = (IData) serialInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_NOTIN_SERIAL_PHONE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_OLD_SERIAL_PHONE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_SERIAL_MONEY", userAttrData.get("RSRV_STR1"));
                datasetSerial.add(userAttr);

            }
            parainfo.put("SERIAL_INFO", datasetSerial);
            parainfo.put("NOTIN_OLD_SerialData", datasetSerial);

        }

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
        String productNo = parainfo.getString("PRODUCT_ID");
        
        IDataset productType = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "PRODUCT_TYPE", "ZZZZ");
        
        parainfo.put("PRODUCT_TYPE", productType);


        IDataset dataLineSta = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "USER_STATE", "ZZZZ");
        
        parainfo.put("DATALINE_STATE", dataLineSta);

        IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(bp, data.getString("GROUP_ID"));
        if (null != custInfo && custInfo.size() > 0)
        {
            parainfo.put("NOTIN_INSTALL_ADDRESS", custInfo.getString("GROUP_ADDR"));
        }

        parainfo.put("NOTIN_METHOD_NAME", "CrtUs");


        result.put("PARAM_INFO", parainfo);
        return result;
    }

    public IData initZXOpen(IBizCommon bp, IData data) throws Throwable
    {
        IData result = initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        parainfo.put("NOTIN_USER_DATELINE_STATE", "1");
        parainfo.put("NOTIN_METHOD_NAME", "ZXOpen");

        IData items = result.getData("ATTRITEM");
        if (IDataUtil.isNotEmpty(items))
        {
            IData itemData = items.getData("USER_STATE");
            if (IDataUtil.isNotEmpty(itemData))
            {
                itemData.put("ATTR_VALUE", "1");
            }
        }
        return result;
    }
}
