package com.asiainfo.veris.crm.iorder.soa.group.param.idc;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

public class QueryIdcAttrParamBean  extends QueryAttrParamBean 
{

    public static IData queryIdcParamAttrForChgInit(IData param) throws Exception
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
        IData paramData = new DataMap();
    	
        paramData.put("ID", offerCode);
        paramData.put("ID_TYPE", "P");
        paramData.put("ATTR_CODE", "IP_PORT");
        paramData.put("EPARCHY_CODE", "ZZZZ");
    	
        IDataset dataLineInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", paramData);
        IData dataLineData=new DataMap();
        dataLineData.put("DATA_VAL", dataLineInfo);
        result.put("DATALINE_INFO", dataLineData);

        IData inParam = new DataMap();
        IDataset seqDataSet = CSAppCall.call("CS.SeqMgrSVC.getMaxNumberLine", inParam);
        IData seqData = (IData) seqDataSet.get(0);
        String maxNumberLine = seqData.getString("seq_id");
        long maxIong = Long.parseLong(maxNumberLine) * 1000;
        IData maxNumberLineData=new DataMap();
        maxNumberLineData.put("DATA_VAL", String.valueOf(maxIong));
        result.put("NOTIN_MAX_NUMBER_LINE", maxNumberLineData);
        
        return result;
    
    }
    
}
