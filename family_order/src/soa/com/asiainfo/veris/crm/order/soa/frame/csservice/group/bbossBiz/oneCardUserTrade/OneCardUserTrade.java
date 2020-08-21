
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.oneCardUserTrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class OneCardUserTrade
{

    /**
     * 字段名转换方法
     */
    public static IData checkKeyName(IData data, String sSrcKey, String sDesKey) throws Exception
    {
        IData retdata = data;
        String sSrcValue = retdata.getString(sSrcKey);
        if (sSrcValue != null)
        {
            retdata.put(sDesKey, sSrcValue);
        }
        return retdata;
    }

    /**
     * 表字段名转换成协议字段名方法(IData版)
     */
    public static IData FeildToProtocal(IData data) throws Exception
    {
        IData retData = data;
        // 商品规格编码
        retData = checkKeyName(retData, "MERCH_SPEC_CODE", "POSPECNUMBER");
        // 产品规格编码
        retData = checkKeyName(retData, "PRODUCT_SPEC_CODE", "PRODUCTSPECNUMBER");
        // 产品订购关系ID
        retData = checkKeyName(retData, "PRODUCT_OFFER_ID", "PRODUCTID");
        // 商品订单号
        retData = checkKeyName(retData, "MERCH_ORDER_ID", "POORDERNUMBER");

        return retData;
    }

    /**
     * 表字段名转换成协议字段名 方法(IDataset版)
     */
    public static IDataset FeildToProtocal(IDataset ds) throws Exception
    {
        IDataset retDatas = ds;
        for (int nIndex = 0; nIndex < retDatas.size(); nIndex++)
        {
            IData data = FeildToProtocal(retDatas.getData(nIndex));
            retDatas.remove(nIndex);
            retDatas.add(nIndex, data);
        }
        return retDatas;
    }

    /*
     * 一卡通成员,400白名单业务查询
     */
    public static IDataset getPOUserMbmp(IData data, Pagination pg) throws Exception
    {

        IData param = ProtocalToFeild(data);

        String sGroupID = IDataUtil.getMandaData(param, "GROUP_ID");
        String sMERCH_SPEC_CODE = IDataUtil.getMandaData(param, "MERCH_SPEC_CODE");
        String sPRODUCT_SPEC_CODE = IDataUtil.getMandaData(param, "PRODUCT_SPEC_CODE");

        if (sMERCH_SPEC_CODE.equals("01114001"))
        {
            // 400商品只支持白名单 RSRV_TAG1//成员类型,1-签约成员2-白名单0-黑名单
            param.put("RSRV_TAG1", "2");
        }
        else if (sMERCH_SPEC_CODE.equals("010105002"))
        {
            // 一卡通支持所有成员

        }
        else
        {
            // 只支持400和一卡通查询
            CSAppException.apperr(CrmCardException.CRM_CARD_1);
        }
        IDataset idsRet = new DatasetList();
        // String cnnStr = "sdcrm";
        for (int i = 1; i < 5; i++)
        {

            idsRet = UserGrpMerchMebInfoQry.qryMerchMebInfoByGroupIdMerchScProductSc(sGroupID, sMERCH_SPEC_CODE, sPRODUCT_SPEC_CODE);
        }

        for (int iIndex = 0; iIndex < idsRet.size(); iIndex++)
        {
            IData dEech = idsRet.getData(iIndex);
            String sSn = dEech.getString("SERIAL_NUMBER", "");
            String sUID = dEech.getString("USER_ID", "");
            if (sSn.equals("") || sUID.equals(""))
            {
                idsRet.getData(iIndex).put("CUST_NAME", "");
                continue;
            }
            // 获取路由地州

            String strRouteEparchyCode = RouteInfoQry.getEparchyCodeBySn(sSn);

            IDataset idsCust = UserInfoQry.getUserPrsinfoByuserid(dEech.getString("USER_ID", ""), strRouteEparchyCode);
            if (idsCust.size() > 0)
            {
                idsRet.getData(iIndex).put("CUST_NAME", idsCust.getData(0).getString("CUST_NAME", ""));
            }
        }

        idsRet = FeildToProtocal(idsRet);
        return idsRet;
    }

    /**
     * 协议字段名转换成表字段名方法(IData版)
     */
    public static IData ProtocalToFeild(IData data) throws Exception
    {

        IData retData = data;
        // 商品规格编码
        retData = checkKeyName(retData, "POSPECNUMBER", "MERCH_SPEC_CODE");
        // 产品规格编码
        retData = checkKeyName(retData, "PRODUCTSPECNUMBER", "PRODUCT_SPEC_CODE");
        // 产品订购关系ID
        retData = checkKeyName(retData, "PRODUCTID", "PRODUCT_OFFER_ID");
        // 商品订单号
        retData = checkKeyName(retData, "POORDERNUMBER", "MERCH_ORDER_ID");

        return retData;
    }
}
