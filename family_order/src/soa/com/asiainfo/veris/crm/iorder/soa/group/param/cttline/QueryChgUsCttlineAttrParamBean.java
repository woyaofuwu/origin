package com.asiainfo.veris.crm.iorder.soa.group.param.cttline;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

import net.sf.json.JSONArray;

public class QueryChgUsCttlineAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryCttlineParamAttrForChgInit(IData param) throws Exception
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
        String productValue=attrItemA.getData("PRODUCT_TYPE").getString("ATTR_VALUE");
       
        //IDataset userInfo = UserVpnInfoQry.qryUserVpnByUserId(userId);
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", userId);
        userInParam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset userInfoList = CSAppCall.call("CS.UcaInfoQrySVC.qryUserInfoByUserId", userInParam);
        IData userCttlineData =new DataMap();
        
        // 将用户开通专线的状态返回至前台
        if (null != userInfoList && userInfoList.size() > 0)
        {
            IData userInfo = (IData) userInfoList.get(0);
            String userState = userInfo.getString("RSRV_STR1");
            userCttlineData.put("NOTIN_USER_DATELINE_STATE", userState);

        }
        
        userCttlineData.put("NOTIN_METHOD_NAME", "ChgUs");

        
    	
    	// 调用后台服务,查询OTHER表信息
        IData inparmePath = new DataMap();
        inparmePath.put("USER_ID", userId);
        inparmePath.put("RSRV_VALUE_CODE", "PATH");
        IDataset pathInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmePath);
        if (null != pathInfo && pathInfo.size() > 0)
        {
            IData userAttrData = (IData) pathInfo.get(0);
            userCttlineData.put("NOTIN_PATH", userAttrData.get("RSRV_STR10"));
        }
        
        IData inparmeAddress = new DataMap();
        inparmeAddress.put("USER_ID", userId);
        inparmeAddress.put("RSRV_VALUE_CODE", "ADDRESS");
        IDataset addressInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeAddress);
        if (null != addressInfo && addressInfo.size() > 0)
        {
            IData userAttrData = (IData) addressInfo.get(0);
            userCttlineData.put("NOTIN_INSTALL_ADDRESS", userAttrData.get("RSRV_STR10"));
        }
    	
        IData inparmeWide = new DataMap();
        inparmeWide.put("USER_ID", userId);
        inparmeWide.put("RSRV_VALUE_CODE", "BINDWIDE");
        IDataset wideInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeWide);

        if (null != wideInfo && wideInfo.size() > 0)
        {
            IDataset datasetWide = new DatasetList();
            for (int i = 0; i < wideInfo.size(); i++)
            {
                IData userAttrData = (IData) wideInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_NOTIN_WIDE_ACCT_ID", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_OLD_WIDE_ACCT_ID", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_WIDE_MONTH", userAttrData.get("RSRV_STR1"));
                userAttr.put("pam_NOTIN_WIDE_NET_LINE", userAttrData.get("RSRV_STR2"));
                datasetWide.add(userAttr);

            }
            userCttlineData.put("WIDE_INFO", JSONArray.fromObject(datasetWide).toString().replaceAll("\"", "\'"));
            userCttlineData.put("NOTIN_OLD_WideData", JSONArray.fromObject(datasetWide).toString().replaceAll("\"", "\'"));
        }
    	
        IData inparmeFixed = new DataMap();
        inparmeFixed.put("USER_ID", userId);
        inparmeFixed.put("RSRV_VALUE_CODE", "BINDFIXED");
        IDataset fixedInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeFixed);
        if (null != fixedInfo && fixedInfo.size() > 0)
        {
            IDataset datasetFixed = new DatasetList();
            for (int i = 0; i < fixedInfo.size(); i++)
            {
                IData userAttrData = (IData) fixedInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_NOTIN_FIXED_PHONE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_OLD_FIXED_PHONE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_FIXED_MONEY", userAttrData.get("RSRV_STR1"));
                datasetFixed.add(userAttr);

            }
            userCttlineData.put("FIXED_INFO", JSONArray.fromObject(datasetFixed).toString().replaceAll("\"", "\'")); 
            userCttlineData.put("NOTIN_OLD_FixedData", JSONArray.fromObject(datasetFixed).toString().replaceAll("\"", "\'"));
        }
    	
        IData inparmeSerial = new DataMap();
        inparmeSerial.put("USER_ID", userId);
        inparmeSerial.put("RSRV_VALUE_CODE", "BINDSERIAL");
        IDataset serialInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeSerial);
        if (null != serialInfo && serialInfo.size() > 0)
        {
            IDataset datasetSerial = new DatasetList();
            for (int i = 0; i < serialInfo.size(); i++)
            {
                IData userAttrData = (IData) serialInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_NOTIN_SERIAL_PHONE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_OLD_SERIAL_PHONE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_SERIAL_MONEY", userAttrData.get("RSRV_STR1"));
                datasetSerial.add(userAttr);

            }
            userCttlineData.put("SERIAL_INFO", JSONArray.fromObject(datasetSerial).toString().replaceAll("\"", "\'"));
            userCttlineData.put("NOTIN_OLD_SerialData", JSONArray.fromObject(datasetSerial).toString().replaceAll("\"", "\'"));

        }
        
        IData userAttrItem = IDataUtil.iDataA2iDataB(userCttlineData, "ATTR_VALUE");
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
        
        IData productTypeParam = new DataMap();
        productTypeParam.put("ID", offerCode);
        productTypeParam.put("ID_TYPE", "P");
        productTypeParam.put("ATTR_CODE", "PRODUCT_TYPE");
        productTypeParam.put("EPARCHY_CODE", "ZZZZ");
    	IDataset productType = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", productTypeParam);
    	IData productTypeData = new DataMap();
    	productTypeData.put("DATA_VAL", productType);
    	productTypeData.put("ATTR_VALUE", productValue);
    	result.put("PRODUCT_TYPE", productTypeData);
    	
    	IData dataLineStaParam = new DataMap();
    	dataLineStaParam.put("ID", offerCode);
    	dataLineStaParam.put("ID_TYPE", "P");
    	dataLineStaParam.put("ATTR_CODE", "USER_STATE");
    	dataLineStaParam.put("EPARCHY_CODE", "ZZZZ");
    	IDataset dataLineSta = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", dataLineStaParam);
    	IData dataLineData = new DataMap();
    	dataLineData.put("DATA_VAL", dataLineSta);
    	result.put("DATALINE_STATE", dataLineData);
        
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
    
    /**
     * 从ESOP获取专线信息
     * 
     * @author liujy
     * @param bc
     * @param param
     * @return
     * @throws Exception
     */
    public static IData getEsopData(IDataset eosDataset) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData eosData = eosDataset.getData(0);
        IData resultDataset = new DataMap();

        IData inputParam = new DataMap();
        inputParam.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
        inputParam.put("X_SUBTRANS_CODE", "GetEosInfo");
        inputParam.put("NODE_ID", eosData.getString("NODE_ID", ""));
        inputParam.put("IBSYSID", eosData.getString("IBSYSID", ""));
        inputParam.put("SUB_IBSYSID", eosData.getString("SUB_IBSYSID", ""));
        inputParam.put("PRODUCT_ID", eosData.getString("PRODUCT_ID"));
        inputParam.put("OPER_CODE", "14");

        IDataset httResultSetDataset = CSAppCall.call("SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inputParam);

        if (null != httResultSetDataset && httResultSetDataset.size() > 0)
        {
            IData dataLine = httResultSetDataset.getData(0);
            IData data = dataLine.getData("DLINE_DATA");
            if (null != data && data.size() > 0)
            {
                resultDataset = mergeData(dataset, httResultSetDataset);
            }
        }

        return resultDataset;
    }
    
    /**
     * 解析专线数据
     * 
     * @param dataset
     * @param httpResult
     * @return
     * @throws Exception
     */
    protected static IData mergeData(IDataset dataset, IDataset httpResult) throws Exception
    {
        IData resultData = new DataMap();
        IDataset comData = new DatasetList();
        IDataset dataLineAttr = new DatasetList();

        if (null != httpResult && httpResult.size() > 0)
        {
            IData dataLine = httpResult.getData(0);
            if (null != dataLine && dataLine.size() > 0)
            {
                IData totalData = dataLine.getData("DLINE_DATA");
                IData commonData = totalData.getData("COMM_DATA_MAP");
                IDataset lineDataList = totalData.getDataset("LINE_DATA_LIST");

                // 公共数据
                for (int i = 0; i < commonData.size(); i++)
                {
                    IData attrValue = new DataMap();
                    String attr[] = commonData.getNames();
                    attrValue.put("ATTR_CODE", attr[i]);
                    attrValue.put("ATTR_VALUE", commonData.getString(attr[i]));
                    comData.add(attrValue);
                }

                // 专线数据
                for (int j = 0; j < lineDataList.size(); j++)
                {
                    IData data = lineDataList.getData(j);
                    IData attrValue = new DataMap();
                    for (int k = 0; k < data.size(); k++)
                    {
                        String attr[] = data.getNames();
                        attrValue.put(attr[k], data.getString(attr[k]));
                    }
                    dataLineAttr.add(attrValue);
                }
            }
        }

        resultData.put("COMMON_DATA", comData);
        resultData.put("DLINE_DATA", dataLineAttr);

        return resultData;
    }
    
}
