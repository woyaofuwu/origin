
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.oneCardTrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;

public class OneCardTradeQuery
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

    public static IDataset getPOMbmp(IData param) throws Exception
    {

        String sGroupID = IDataUtil.getMandaData(param, "GROUP_ID");
        String sMERCH_SPEC_CODE = IDataUtil.getMandaData(param, "MERCH_SPEC_CODE");
        String sPRODUCT_SPEC_CODE = IDataUtil.getMandaData(param, "PRODUCT_SPEC_CODE");

        IData inparmaData = new DataMap();
        inparmaData.put("GROUP_ID", sGroupID);
        inparmaData.put("MERCH_SPEC_CODE", sMERCH_SPEC_CODE);
        inparmaData.put("PRODUCT_SPEC_CODE", sPRODUCT_SPEC_CODE);

        IDataset idsRet = UserGrpMerchInfoQry.selMerchinfoByPk(inparmaData);
        idsRet = FeildToProtocal(idsRet);
        return idsRet;

    }
}
