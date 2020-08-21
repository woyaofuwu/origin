package com.asiainfo.veris.crm.iorder.soa.group.param.cttline;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

public class QueryCttlineAttrParamBean  extends QueryAttrParamBean 
{

    public static IData queryCttlineParamAttrForChgInit(IData param) throws Exception
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
        //IDataset dataLineSta = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "USER_STATE", "ZZZZ");
        IData dataLineParam = new DataMap();
    	
        dataLineParam.put("ID", offerCode);
        dataLineParam.put("ID_TYPE", "P");
        dataLineParam.put("ATTR_CODE", "USER_STATE");
        dataLineParam.put("EPARCHY_CODE", "ZZZZ");
    	
    	IDataset dataLineSta = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", dataLineParam);
    	
    	IData dataLineData = new DataMap();
    	dataLineData.put("DATA_VAL", dataLineSta);
        result.put("DATALINE_STATE", dataLineData);
        
        //IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(bp, data.getString("GROUP_ID"));
        
        /*IData custParam = new DataMap();
        custParam.put("GROUP_ID", param.getString("GROUP_ID"));
        IData custInfo= CSAppCall.callOne("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", custParam);*/
        /*IData custInfo= UcaInfoQry.qryCustInfoByCustId(param.getString("CUST_ID"));*/
        //IData custInfo=UcaInfoQry.qryCustInfoByCustId(param.getString("CUST_ID"));
        String groupId = UcaInfoQry.qryGrpInfoByCustId(param.getString("CUST_ID")).getString("GROUP_ID", "");
        IData custParam = new DataMap();
        custParam.put("GROUP_ID", groupId);
        IData custInfo= CSAppCall.callOne("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", custParam);
        if (null != custInfo && custInfo.size() > 0)
        {
        	IData custData=new DataMap();
        	custData.put("DATA_VAL", custInfo.getString("GROUP_ADDR"));
            result.put("NOTIN_INSTALL_ADDRESS", custData);
        }
        
        IData methodData=new DataMap();
        methodData.put("DATA_VAL", "CrtUs");
        result.put("NOTIN_METHOD_NAME", methodData);
        
        return result;
    
    }
    
}
