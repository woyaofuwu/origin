package com.asiainfo.veris.crm.iorder.soa.group.param.centrexsupertel;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryChgMbCentrexsupertelAttrParamBean  extends QueryAttrParamBean
{
   
    public static IData queryCentrexsupertelAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        IData attrItemA = new DataMap();
        IData parainfo = new DataMap();
        String ecUserId = param.getString("EC_USER_ID");
        String userId = param.getString("USER_ID");
        String eparchyCode = param.getString("EPARCHY_CODE");
        IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond("6130", "P", "1", null);
        

        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        
        IDataset uuInfo_S31 = RelaUUInfoQry.qryUU(ecUserId, userId, "S3", null, null);
        IData uuInfo_S3 = new DataMap();
        if (IDataUtil.isNotEmpty(uuInfo_S31))
        {
            uuInfo_S3 = uuInfo_S31.getData(0);
        }
        IDataset mebResInfo = UserResInfoQry.getUserResByUserIdA(userId, ecUserId);
        
        if (IDataUtil.isNotEmpty(mebResInfo))
        {
            IData userres = mebResInfo.getData(0);
            uuInfo_S3.put("SHORT_CODE", userres.getString("RES_CODE", ""));
            uuInfo_S3.put("hidden_SHORT_CODE", userres.getString("RES_CODE", ""));
        }

        IDataset superTels = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(ecUserId, "MUTISUPERTEL");
        
        if (IDataUtil.isNotEmpty(superTels))
        {
            attrItemA.put("SUPERTELLIST", superTels);
        }
        IData userattrinfo = UserVpnInfoQry.getMemberVpnByUserId(userId, ecUserId, eparchyCode);
        
        if(IDataUtil.isNotEmpty(userattrinfo)){
            
            uuInfo_S3.put("SUPERTELNUMBER", userattrinfo.getString("RSRV_STR2", ""));
            uuInfo_S3.put("OPERATORPRIONTY", userattrinfo.getString("RSRV_STR3", ""));
        }
        IData uuAttrItem = IDataUtil.iDataA2iDataB(uuInfo_S3, "ATTR_VALUE");
        transComboBoxValue(uuAttrItem, attrItemA);
        
        if (IDataUtil.isNotEmpty(uuAttrItem))
        {
            Set<String> propNames = uuAttrItem.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = uuAttrItem.getData(key);
                IData attrItem = new DataMap();
              if(IDataUtil.isEmpty(attrCodeInfo)){
                  continue;
              }
              IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
              
              if (IDataUtil.isNotEmpty(workTypeCodeInfo))
              {
                  String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                  attrItem.put("DATA_VAL", workTypeCodeInfo);
                  attrItem.put("ATTR_VALUE", attrItemValue);
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
        return result;

    }
    
    public static void transComboBoxValue(IData userAttrItem, IData pzAttrItem) throws Exception
    {
        if (IDataUtil.isEmpty(pzAttrItem))
        {
            return;
        }
        else if (IDataUtil.isEmpty(userAttrItem)) 
        {
            userAttrItem.putAll(pzAttrItem);
        }
        else 
        {
            for (Iterator iterator = pzAttrItem.keySet().iterator(); iterator.hasNext();)
            {
                String datakey = (String) iterator.next();
                IData tempData = userAttrItem.getData(datakey);
                IData tempData2 = pzAttrItem.getData(datakey);
                if (IDataUtil.isEmpty(tempData))
                {
                    tempData = tempData2;
                    userAttrItem.put(datakey, tempData);
                }
                else if (IDataUtil.isNotEmpty(tempData2))
                {
                    tempData.put("DATA_VAL", tempData2.getDataset("DATA_VAL"));
                }
            }
        }
    }
    
}

