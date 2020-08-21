package com.asiainfo.veris.crm.iorder.soa.group.param.wlan;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

import net.sf.json.JSONArray;

public class QueryChgUsWlanAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryWlanParamAttrForChgInit(IData param) throws Exception
    {
    	IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String custId = param.getString("CUST_ID");
        String userId = param.getString("USER_ID");
        
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
       
        //IDataset userInfo = UserVpnInfoQry.qryUserVpnByUserId(userId);
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", userId);
        userInParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSAppCall.call("CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
        IData userVpnData =new DataMap();
        
        if(IDataUtil.isNotEmpty(userInfo)){
            userVpnData = userInfo.getData(0);
            userVpnData.put("DETMANAGERPHONE", userVpnData.getString("RSRV_STR8", ""));
            userVpnData.put("DETMANAGERINFO", userVpnData.getString("RSRV_STR7", ""));
            userVpnData.put("DETADDRESS", userVpnData.getString("RSRV_STR9", ""));
        }
        if (IDataUtil.isEmpty(userVpnData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_130, param.getString("USER_ID"));
        }
        
        
        userVpnData.put("METHOD", "ChgUs");
        
        
        
        
        
        
        IData userAttrItem = IDataUtil.iDataA2iDataB(userVpnData, "ATTR_VALUE");
        transComboBoxValue(userAttrItem,attrItemA);
        
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
        

        // 调用后台服务,查询OTHER表信息
        IData datasetBData = new DataMap();
        datasetBData.put("DATA_VAL", "");
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "GRP_WLAN");
        IDataset userAttrInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);
        // 判断OTHER表中有没有数据
        if (null != userAttrInfo && userAttrInfo.size() > 0)
        {
            IDataset datasetB = new DatasetList();
            
            for (int i = 0; i < userAttrInfo.size(); i++)
            {
                IData otherInfo = (IData) userAttrInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_GRP_WLAN_CODE", otherInfo.getString("RSRV_VALUE", ""));
                userAttr.put("pam_GRP_WLAN", otherInfo.getString("RSRV_STR1", ""));
                userAttr.put("pam_NET_LINE", otherInfo.getString("RSRV_STR2", ""));
                userAttr.put("pam_PRICE", otherInfo.getString("RSRV_STR3", ""));
                userAttr.put("pam_DIS_DATA", otherInfo.getString("RSRV_STR4", ""));
                userAttr.put("pam_COMPANY_NAME_CODE", otherInfo.getString("RSRV_STR8", ""));
                userAttr.put("pam_COMPANY_NAME", otherInfo.getString("RSRV_STR9", ""));
                userAttr.put("pam_REMARK", otherInfo.getString("RSRV_STR10", ""));
                userAttr.put("INSTID", otherInfo.getString("INST_ID", ""));
                datasetB.add(userAttr);
            }
            datasetBData.put("DATA_VAL", JSONArray.fromObject(datasetB).toString().replaceAll("\"", "\'"));
        }
        result.put("WLAN_INFO", datasetBData);
        
        //IDataset itemdatas = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productId, "P", "GRP_WLAN", "ZZZZ");
        IData wlanInfosParam = new DataMap();
        IData wlanInfosData = new DataMap();
    	
        wlanInfosParam.put("ID", offerCode);
        wlanInfosParam.put("ID_TYPE", "P");
        wlanInfosParam.put("ATTR_CODE", "GRP_WLAN");
        wlanInfosParam.put("EPARCHY_CODE", "ZZZZ");
    	
    	IDataset itemdatas = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", wlanInfosParam);
    	wlanInfosData.put("DATA_VAL", itemdatas);
    	result.put("WLAN_INFOS", wlanInfosData);
    	
    	// 设置宽带资费
        IData commparam = new DataMap();
        IData commData = new DataMap();
        commparam.put("PARAM_CODE", offerCode);
        commparam.put("SUBSYS_CODE", "CSM");
        commparam.put("PARAM_ATTR", "555");
        commparam.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
        IDataset bandwidthdatas = CSAppCall.call("CS.CommparaInfoQrySVC.getCommpara", commparam);
        commData.put("DATA_VAL", JSONArray.fromObject(bandwidthdatas).toString().replaceAll("\"", "\'"));
        result.put("BANDWIDTHDATAS", commData);
        
        // 落地分公司信息
        //IDataset companyinfos = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productId, "P", "COMPANY_NAME", "ZZZZ");
        IData companyParam = new DataMap();
        IData companyData = new DataMap();
        companyParam.put("ID", offerCode);
        companyParam.put("ID_TYPE", "P");
        companyParam.put("ATTR_CODE", "COMPANY_NAME");
        companyParam.put("EPARCHY_CODE", "ZZZZ");
    	
        IDataset companyinfos = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", companyParam);
        companyData.put("DATA_VAL", companyinfos);
        result.put("COMPANY_INFOS", companyData);
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
