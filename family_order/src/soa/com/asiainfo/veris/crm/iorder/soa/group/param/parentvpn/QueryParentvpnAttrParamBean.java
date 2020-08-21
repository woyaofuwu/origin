package com.asiainfo.veris.crm.iorder.soa.group.param.parentvpn;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;

public class QueryParentvpnAttrParamBean extends QueryAttrParamBean
{

    public static IData queryParentvpnParamAttrForChgInit(IData param) throws Exception
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
                if ("CUST_MANAGER".equals(key))
                {
                    IData custMan = new DataMap();
                    IDataset managerInfos = StaffInfoQry.qryManagerIdJobType(custMan);
                    if (IDataUtil.isNotEmpty(managerInfos))
                    {
                        int len = managerInfos.size();
                        for (int i = 0; i < len; i++)
                        {
                            IData infoData = managerInfos.getData(i);
                            String rsrvStr1 = infoData.getString("CUST_MANAGER_NAME");
                            String rsrvStr2 = infoData.getString("CUST_MANAGER_ID");
                            infoData.put("CUST_MANAGER_NAME", rsrvStr1 + "|" + rsrvStr2);
                        }
                    }
                    attrItem.put("DATA_VAL", managerInfos);
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
        IData linkMan = new DataMap();
        String cityCode = getVisit().getCityCode();
        if ("HK".equals(cityCode.substring(0, 2)))
        {
        	linkMan.put("DATA_VAL", "10");
        }
        else
        {
        	linkMan.put("DATA_VAL", "5");
        }
        result.put("MAX_LINKMAN_NUM", linkMan);
        
        
        
        return result;

    }

}
