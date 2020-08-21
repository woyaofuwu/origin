package com.asiainfo.veris.crm.iorder.soa.group.param.centrexsupertel;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class QueryCrtMbCentrexsupertelAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryCentrexsupertelAttrForChgInit(IData param) throws Exception
    {
        
        IData result = new DataMap();
        String mebUserId = param.getString("USER_ID");
        String ecUserId = param.getString("EC_USER_ID");
        String custId = param.getString("CUST_ID");
        String eparchyCode = param.getString("EPARCHY_CODE");
        
        IDataset dataset =  UItemAInfoQry.queryOfferChaAndValByCond("6130", "P", "1", null);
        
        IData attrItemA = new DataMap();
        
        IData relaData20 = new DataMap();
        
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        
        IData vpnMebData = new DataMap();
        // VPMN关系
        IDataset infosDataset = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(mebUserId, eparchyCode);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            relaData20 = infosDataset.getData(0);
        }
        
        if (IDataUtil.isNotEmpty(relaData20))
        {
            vpnMebData.put("SHORT_CODE", relaData20.getString("SHORT_CODE", ""));
        }
        else
        {
            vpnMebData.put("FLAG", "0");
        }
        
        IData userAttrItem = IDataUtil.iDataA2iDataB(vpnMebData, "ATTR_VALUE");
        
        attrItemA.putAll(userAttrItem);
        
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
                    attrItem.put("ATTR_VALUE", attrItemValue);
                    result.put(key, attrItem);
                }
           
            }
        }
        
        IData paramInfo = new DataMap();
        
        IDataset superTelList = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(ecUserId, "MUTISUPERTEL");
        
        if (IDataUtil.isNotEmpty(superTelList))
        {
            paramInfo.put("SUPERTELLIST", superTelList);
        }
        
        paramInfo.put("PRODUCT_ID", "6130");
        
      
        if (IDataUtil.isNotEmpty(paramInfo))
        {
            result.put("PARAM_INFO", paramInfo);
        }
        return result;
       
    }
    
}

