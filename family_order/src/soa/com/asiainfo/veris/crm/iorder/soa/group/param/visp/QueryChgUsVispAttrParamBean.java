package com.asiainfo.veris.crm.iorder.soa.group.param.visp;

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

public class QueryChgUsVispAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryVispParamAttrForChgInit(IData param) throws Exception
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
            userVpnData.put("NOTIN_DETMANAGER_INFO", userVpnData.get("RSRV_STR7"));
            userVpnData.put("NOTIN_DETMANAGER_PHONE", userVpnData.get("RSRV_STR8"));
            userVpnData.put("NOTIN_DETADDRESS", userVpnData.get("RSRV_STR9"));
        }
        if (IDataUtil.isEmpty(userVpnData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_130, param.getString("USER_ID"));
        }
        
        
        userVpnData.put("NOTIN_METHOD_NAME", "ChgUs");
        
        
        
        
        
        
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
        
        
        IData dataLineParam = new DataMap();
        IData dataLineData = new DataMap();
        dataLineParam.put("ID", offerCode);
        dataLineParam.put("ID_TYPE", "P");
        dataLineParam.put("ATTR_CODE", "SP_LINE");
        dataLineParam.put("EPARCHY_CODE", "ZZZZ");
    	
        IDataset dataLineInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", dataLineParam);
        dataLineData.put("DATA_VAL", dataLineInfo);
        result.put("DATALINE_INFO", dataLineData);
        
        // 查询优惠信息
        IData priceParam = new DataMap();
        IData priceData= new DataMap();
        priceParam.put("SUBSYS_CODE", "CSM");
        priceParam.put("PARAM_ATTR", "555");
        priceParam.put("PARAM_CODE", offerCode);
        priceParam.put("EPARCHY_CODE", param.getString("USER_EPARCHY_CODE"));

        IDataset priceDataInfo = CSAppCall.call("SS.BookTradeSVC.getPriceDataByProductId", priceParam);
        priceData.put("DATA_VAL", priceDataInfo);
        result.put("PRICE_DATA", priceData);
        
        // 从ESOP获取专线实例编号，暂时取死值
        IData inParam = new DataMap();
        IData maxNumberLineData = new DataMap();
        IDataset seqDataSet = CSAppCall.call("CS.SeqMgrSVC.getMaxNumberLine", inParam);
        IData seqData = (IData) seqDataSet.get(0);
        String maxNumberLine = seqData.getString("seq_id");
        long maxIong = Long.parseLong(maxNumberLine) * 1000;
        maxNumberLineData.put("DATA_VAL", String.valueOf(maxIong));
        result.put("NOTIN_MAX_NUMBER_LINE", maxNumberLineData);

        // 调用后台服务,查询OTHER表信息
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "N001");
        IDataset userAttrInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);
        // 判断OTHER表中有没有数据，没有从ESOP获取
        if (null != userAttrInfo && userAttrInfo.size() > 0)
        {
            IDataset datasetB = new DatasetList();
            IData datasetBData = new DataMap();
            for (int i = 0; i < userAttrInfo.size(); i++)
            {
                IData userAttrData = (IData) userAttrInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_NOTIN_LINE_NUMBER_CODE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_LINE_NUMBER", userAttrData.get("RSRV_STR1"));
                userAttr.put("pam_NOTIN_LINE_BROADBAND", userAttrData.get("RSRV_STR2"));
                userAttr.put("pam_NOTIN_LINE_PRICE", userAttrData.get("RSRV_STR3"));
                userAttr.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));
                datasetB.add(userAttr);
            }
            datasetBData.put("DATA_VAL", JSONArray.fromObject(datasetB).toString().replaceAll("\"", "\'"));
            result.put("VISP_INFO", datasetBData);

            result.put("NOTIN_AttrInternet", datasetBData);
            result.put("NOTIN_OLD_AttrInternet", datasetBData);
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
