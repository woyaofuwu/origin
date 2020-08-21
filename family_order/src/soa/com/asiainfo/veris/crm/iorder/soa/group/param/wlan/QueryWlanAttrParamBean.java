package com.asiainfo.veris.crm.iorder.soa.group.param.wlan;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

public class QueryWlanAttrParamBean  extends QueryAttrParamBean 
{

    public static IData queryWlanParamAttrForChgInit(IData param) throws Exception
    {
    	IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");

        IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);
        IDataset dataChaSpec = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "2", null);
        IData attrItemA = new DataMap();
        IData attrItemB = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemB = IDataUtil.hTable2STable(dataChaSpec, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }

        if (IDataUtil.isNotEmpty(attrItemA))
        {
            Set<String> propNames = attrItemA.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrItemA.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                if (IDataUtil.isNotEmpty(workTypeCodeInfo))
                {
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    result.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", attrItemValue);
                    result.put(key, attrItem);
                }
            }
        }
        if (IDataUtil.isNotEmpty(attrItemB))
        {
            Set<String> propNames = attrItemB.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrItemB.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                attrItem.put("DATA_VAL", workTypeCodeInfo);
                result.put(key, attrItem);
            }
        }
        
        // 绑定下拉框的值
        /*IData wlanParam = new DataMap();
        wlanParam.put("ID", offerCode);
        wlanParam.put("ID_TYPE", "P");
        wlanParam.put("ATTR_CODE", "GRP_WLAN");
        wlanParam.put("EPARCHY_CODE", "ZZZZ");
        IDataset itemdatas = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", wlanParam);
        IData wlanInfo = new DataMap();
        wlanInfo.put("DATA_VAL", itemdatas);
        result.put("WLAN_INFOS", wlanInfo);*/
        result.put("WLAN_INFOS", result.getData("GRP_WLAN"));
        
        // 权限控制优惠价格
        String eparchyCode = param.getString("EPARCHY_CODE");
        IData discountparam = new DataMap();
        discountparam.put("SUBSYS_CODE", "CSM");
        discountparam.put("PARAM_ATTR", "556");
        discountparam.put("EPARCHY_CODE", eparchyCode);
        discountparam.put("PARAM_CODE", "COMPANY_LED");// PROVINCE_LED COM_TREE_LED
        IDataset discountDataset = CSAppCall.call("CS.CommparaInfoQrySVC.getCommpara", discountparam);
        IData discountInfo = new DataMap();
        discountInfo.put("DATA_VAL", discountDataset);
        result.put("DISCOUNTDATA", discountInfo);
        
        // 设置宽带资费
        IData commparam = new DataMap();
        commparam.put("PARAM_CODE", offerCode);
        commparam.put("SUBSYS_CODE", "CSM");
        commparam.put("PARAM_ATTR", "555");
        commparam.put("EPARCHY_CODE", eparchyCode);
        IDataset bandwidthdatas = CSAppCall.call("CS.CommparaInfoQrySVC.getCommpara", commparam);
        IData bandwidthInfo = new DataMap();
        bandwidthInfo.put("DATA_VAL", bandwidthdatas);
        result.put("BANDWIDTHDATAS", bandwidthInfo);
        
        // 落地分公司信息
    	result.put("COMPANY_INFOS", result.getData("COMPANY_NAME"));
        
    	IData methodInfo = new DataMap();
    	methodInfo.put("DATA_VAL", "CrtUs");
    	result.put("METHOD", methodInfo);
        return result;
    
    }
    
}
