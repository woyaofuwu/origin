package com.asiainfo.veris.crm.iorder.soa.group.param.broadband;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

public class QueryBroadbandAttrParamBean  extends QueryAttrParamBean 
{

    public static IData queryBroadbandParamAttrForChgInit(IData param) throws Exception
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
        
        String staffId = getVisit().getStaffId();
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, "GRP_BROADBAND_FEE_PRV");
        IData parainfoData=new DataMap();
        parainfoData.put("DATA_VAL", bool);
        result.put("HAS_FEE_PRIV", parainfoData);
        
        //IDataset broadbandInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "BROADBAND", "ZZZZ");
        IData broadbandParam = new DataMap();
        broadbandParam.put("ID", offerCode);
        broadbandParam.put("ID_TYPE", "P");
        broadbandParam.put("ATTR_CODE", "BROADBAND");
        broadbandParam.put("EPARCHY_CODE", "ZZZZ");
        IDataset broadbandInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", broadbandParam);
        IData broadbandData=new DataMap();
        broadbandData.put("DATA_VAL", broadbandInfo);
        result.put("BROADBAND_INFO", broadbandData);
        
        IData methodData=new DataMap();
        methodData.put("DATA_VAL", "CrtUs");
        result.put("NOTIN_METHOD_NAME", methodData);
        
        return result;
    
    }
    
}
