package com.asiainfo.veris.crm.iorder.soa.group.param.gdzy;

import java.util.Set;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

public class QueryCrtGdzyAttrParamBean  extends QueryAttrParamBean 
{

    public static IData queryGdzyParamAttrForCrtInit(IData param) throws Exception
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
        
        // 调用后台服务查页面上需要显示的参数信息
        //IDataset productInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "GDZY", "ZZZZ");
        
        IData productParam = new DataMap();
        productParam.put("ID", offerCode);
        productParam.put("ID_TYPE", "P");
        productParam.put("ATTR_CODE", "GDZY");
        productParam.put("EPARCHY_CODE", "ZZZZ");
        IDataset productInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", productParam);
        
        IData productData = new DataMap();
        productData.put("DATA_VAL", productInfo);
        result.put("GRPGDZY_INFO", productData);
        
        IData methodData=new DataMap();
        methodData.put("DATA_VAL", "CrtUs");
        result.put("NOTIN_METHOD_NAME", methodData);
        
        IData currentDateData = new DataMap();
        currentDateData.put("DATA_VAL", SysDateMgr.getSysDate());
        result.put("NOTIN_CURRENT_DATE", currentDateData);
        
        IDataset feeNameInfo = StaticUtil.getStaticList("GDZY_FEENAME");
        IData feeNameData = new DataMap();
        feeNameData.put("DATA_VAL", feeNameInfo);
        result.put("FEENAME_INFO", feeNameData);
        
        return result;
    }
    
}
