package com.asiainfo.veris.crm.iorder.soa.group.param.jwtvpn;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryChgUsJwtvpnAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryJwtVpnAttrForChgInit(IData param) throws Exception
    {
    	IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String custId = param.getString("CUST_ID");
        String userId = param.getString("USER_ID");
        
        IData resultA = new DataMap();
        IDataset datasA = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);
        IDataset dataChaSpec = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "2", null);
        IData attrIteA = new DataMap();
        IData attrIteB = new DataMap();
        if (IDataUtil.isNotEmpty(datasA))
        {
        	attrIteA = IDataUtil.hTable2STable(datasA, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        if (IDataUtil.isNotEmpty(dataChaSpec))
        {
        	attrIteB = IDataUtil.hTable2STable(dataChaSpec, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        
        if (IDataUtil.isNotEmpty(attrIteA))
        {
            Set<String> propNames = attrIteA.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrIteA.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                if (IDataUtil.isNotEmpty(workTypeCodeInfo))
                {
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    resultA.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", attrItemValue);
                    resultA.put(key, attrItem);
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
                    resultA.put(key, attrItem);
                }
            }
        }
        if (IDataUtil.isNotEmpty(attrIteB))
        {
            Set<String> propNames = attrIteB.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrIteB.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                attrItem.put("DATA_VAL", workTypeCodeInfo);
                resultA.put(key, attrItem);
            }
        }
        
    	/***********/
        
        IDataset dataset = UserAttrInfoQry.getUserProductAttrByUTForGrp(userId, "P", null);
        IData attrItemA = new DataMap();
        
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData user_attrida = (IData) dataset.get(i);
                String byFeeParam = user_attrida.getString("ATTR_CODE");
                if (!"".equals(byFeeParam) && byFeeParam.length() > 3 && byFeeParam.substring(0, 3).equals("FEE"))
                {
                    String serParamStr = user_attrida.getString("ATTR_VALUE", "0");
                    user_attrida.put("ATTR_VALUE", Integer.parseInt(serParamStr) / 100);
                    dataset.set(i, user_attrida);
                }

            }
        }
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItemA, pzAttrItem);
        }
        
       
        IDataset userInfo = UserVpnInfoQry.qryUserVpnByUserId(userId);
        IData userVpnData =new DataMap();
        if(IDataUtil.isNotEmpty(userInfo)){
            userVpnData = userInfo.getData(0);
        }
        if (IDataUtil.isEmpty(userVpnData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_130, param.getString("USER_ID"));
        }
        String callNetType = userVpnData.getString("CALL_NET_TYPE", ""); // 呼叫网络类型

        if (callNetType.length() == 4)
        {
            userVpnData.put("CALL_NET_TYPE1", callNetType.substring(0, 1));
            userVpnData.put("CALL_NET_TYPE2", callNetType.substring(1, 2));
            userVpnData.put("CALL_NET_TYPE3", callNetType.substring(2, 3));
            userVpnData.put("CALL_NET_TYPE4", callNetType.substring(3, 4));
        }
        IData userAttrItem = IDataUtil.iDataA2iDataB(userVpnData, "ATTR_VALUE");
        transComboBoxValue(userAttrItem,attrItemA);
        /*IDataset custManagerList = CSAppCall.call("CS.StaffInfoQrySVC.qryManagerIdJobType", param);

        if (IDataUtil.isNotEmpty(custManagerList))
        {
            for (int i = 0, row = custManagerList.size(); i < row; i++)
            {
                IData custManagerData = custManagerList.getData(i);

                String custManagerId = custManagerData.getString("CUST_MANAGER_ID");
                String custManagerName = custManagerData.getString("CUST_MANAGER_NAME");

                custManagerData.put("ATTR_FIELD_CODE", custManagerId);
                custManagerData.put("ATTR_FIELD_NAME", custManagerId + "|" + custManagerName);
            }
        }*/
        
        if (IDataUtil.isNotEmpty(userAttrItem))
        {
            Set<String> propNames = userAttrItem.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = userAttrItem.getData(key);
                IData attrItem = new DataMap();
                if(IDataUtil.isEmpty(attrCodeInfo))
                  continue;
                if ("VISION_NUMBER".equals(key))
                {
                    IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    attrItem.put("ATTR_VALUE", attrItemValue);
                    result.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    if(StringUtils.isBlank(attrItemValue))
                       continue;
                    attrItem.put("DATA_VAL", attrItemValue);
                    result.put(key, attrItem);
                    
                }
                
               
            }
        }
        
        IData custMan = new DataMap();
        IData custData = new DataMap();
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
        custData.put("DATA_VAL", managerInfos);
        result.put("CUST_MANAGER", custData);
        
        
        
        String custManager=result.getData("RSRV_STR5").getString("DATA_VAL");
        result.getData("CUST_MANAGER").put("ATTR_VALUE",custManager);

        String scpCode=result.getData("SCP_CODE").getString("DATA_VAL");
        String workTypeCode=result.getData("WORK_TYPE_CODE").getString("DATA_VAL");

        String vpnScareCode=result.getData("VPN_SCARE_CODE").getString("DATA_VAL");
        String sinwordTypeCode=result.getData("SINWORD_TYPE_CODE").getString("DATA_VAL");
        String overFeeTag=result.getData("OVER_FEE_TAG").getString("DATA_VAL");
        String callAreaType=result.getData("CALL_AREA_TYPE").getString("DATA_VAL");


        result.put("WORK_TYPE_CODE", resultA.getData("WORK_TYPE_CODE"));
        result.getData("WORK_TYPE_CODE").put("ATTR_VALUE",workTypeCode);

        result.put("SCP_CODE", resultA.getData("SCP_CODE"));
        result.getData("SCP_CODE").put("ATTR_VALUE",scpCode);

        result.put("VPN_SCARE_CODE", resultA.getData("VPN_SCARE_CODE"));
        result.getData("VPN_SCARE_CODE").put("ATTR_VALUE",vpnScareCode);

        result.put("SINWORD_TYPE_CODE", resultA.getData("SINWORD_TYPE_CODE"));
        result.getData("SINWORD_TYPE_CODE").put("ATTR_VALUE",sinwordTypeCode);

        result.put("OVER_FEE_TAG", resultA.getData("OVER_FEE_TAG"));
        result.getData("OVER_FEE_TAG").put("ATTR_VALUE",overFeeTag);

        result.put("CALL_AREA_TYPE", resultA.getData("CALL_AREA_TYPE"));
        result.getData("CALL_AREA_TYPE").put("ATTR_VALUE",callAreaType);

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
