package com.asiainfo.veris.crm.iorder.soa.group.param.idc;

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

public class QueryChgUsIdcAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryIdcParamAttrForChgInit(IData param) throws Exception
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
            
            String rsrvStr10 = userVpnData.getString("RSRV_STR10"); // TODO 机柜服务总电费（元）|IDC总增值服务费（元）
            
            if (StringUtils.isNotBlank(rsrvStr10)) {
	        	String[] resultMsg = rsrvStr10.split("\\|");
	        	if (resultMsg.length > 0) {
	        		userVpnData.put("N_CABINET_SVC_EC_INCOME", resultMsg[0]); // 机柜服务总电费
	        	}

	        	if (resultMsg.length > 1) {
	        		userVpnData.put("N_IDC_VAL_ADDED_SVC_INCOME", resultMsg[1]); // IDC总增值服务费
	        	}
            }
            
            userVpnData.put("N_GROUP_CITY_CODE", userVpnData.get("RSRV_STR6"));
            userVpnData.put("N_JF_CODE", userVpnData.get("RSRV_STR5"));
            userVpnData.put("N_IDC_INCOME", userVpnData.get("RSRV_NUM1"));
            userVpnData.put("N_DEPOSIT_INCOME", userVpnData.get("RSRV_NUM2"));
            userVpnData.put("N_ACCESS_INCOME", userVpnData.get("RSRV_NUM3"));
            userVpnData.put("N_OTHER_INCOME", userVpnData.get("RSRV_NUM4"));
            userVpnData.put("N_DEPOSIT_SUM", userVpnData.get("RSRV_STR1"));
            userVpnData.put("N_DEVICE_SIZE", userVpnData.get("RSRV_NUM5"));
            userVpnData.put("N_DEPOSIT_DISCOUNT", userVpnData.get("RSRV_STR2"));
            userVpnData.put("N_ACCESS_BANDWIDTH", userVpnData.get("RSRV_STR3"));
            userVpnData.put("N_ACCESS_DISCOUNT", userVpnData.get("RSRV_STR4"));
            //REQ201808130004关于IDC产品界面增加“是否为统谈合同”字段的需求
            userVpnData.put("N_IDC_AGREEMENT_CONTRACT", userVpnData.get("RSRV_TAG2"));
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
        dataLineParam.put("ATTR_CODE", "IP_PORT");
        dataLineParam.put("EPARCHY_CODE", "ZZZZ");
    	
        IDataset dataLineInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", dataLineParam);
        dataLineData.put("DATA_VAL", dataLineInfo);
        result.put("DATALINE_INFO", dataLineData);

        // REQ201808210047 关于IDC产品界面增加“IP地址类型”字段的需求
        IData ipTypeParam = new DataMap();
        IData ipTypeData = new DataMap();
        ipTypeParam.put("ID", offerCode);
        ipTypeParam.put("ID_TYPE", "P");
        ipTypeParam.put("ATTR_CODE", "TYPE_OF_IP_ADDRESS");
        ipTypeParam.put("EPARCHY_CODE", "ZZZZ");

        IDataset ipTypeInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", ipTypeParam);
        ipTypeData.put("DATA_VAL", ipTypeInfo);
        result.put("IPTYPE_INFO", ipTypeData);
        
        //REQ201812180018关于本省IDC流量计费产品增加按天生效时间免费月份及其他优化的需求
        IData effectiveWayParam = new DataMap();
        IData effectiveWayData = new DataMap();
        effectiveWayParam.put("ID", offerCode);
        effectiveWayParam.put("ID_TYPE", "P");
        effectiveWayParam.put("ATTR_CODE", "EFFECTIVE_WAY");
        effectiveWayParam.put("EPARCHY_CODE", "ZZZZ");

        IDataset effectiveWayInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", effectiveWayParam);
        effectiveWayData.put("DATA_VAL", effectiveWayInfo);
        result.put("EFFECTIVE_WAY", effectiveWayData);
        
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
        inparme.put("RSRV_VALUE_CODE", "IDC");
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
                userAttr.put("pam_NOTIN_OPER_TAG", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_PORT_PRICE", userAttrData.get("RSRV_STR1"));
                userAttr.put("pam_NOTIN_SPACE_PRICE", userAttrData.get("RSRV_STR2"));
                userAttr.put("pam_NOTIN_SERVER_TYPE", userAttrData.get("RSRV_STR3"));
                userAttr.put("pam_NOTIN_IP_ADDRESS", userAttrData.get("RSRV_STR4"));
                userAttr.put("pam_NOTIN_IP_PORT", userAttrData.get("RSRV_STR5"));
                userAttr.put("pam_NOTIN_IP_PORT_NAME", userAttrData.get("RSRV_STR6"));
                userAttr.put("pam_CABINET_SVC_EC_INCOME", userAttrData.get("RSRV_STR7")); // 机柜服务电费（元）
                userAttr.put("pam_IDC_VAL_ADDED_SVC_INCOME", userAttrData.get("RSRV_STR8")); // IDC增值服务费（元）
                userAttr.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));
                userAttr.put("pam_TYPE_OF_IP_ADDRESS", userAttrData.get("RSRV_STR11")); // REQ201808210047 关于IDC产品界面增加“IP地址类型”字段的需求
                datasetB.add(userAttr);
            }
            datasetBData.put("DATA_VAL", JSONArray.fromObject(datasetB).toString().replaceAll("\"", "\'"));
            result.put("VISP_INFO", datasetBData);

            result.put("NOTIN_AttrInternet", datasetBData);
            result.put("NOTIN_OLD_AttrInternet", datasetBData);
        }
        
        //---add by chenhh6@20190225--begin--REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求----
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("RSRV_VALUE_CODE", "IDCORDER");
        IDataset orderInfos = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", params);
        if(IDataUtil.isNotEmpty(orderInfos)){
        	IDataset ds = new DatasetList();
        	IData datasetBData = new DataMap();
        	for(int i=0;i<orderInfos.size();i++){
        		IData each = orderInfos.getData(i);
        		IData userData = new DataMap();
                userData.put("IDC_DEVICE_NAME", each.getString("RSRV_STR1", ""));	//设备名称
                userData.put("IDC_DEVICE_IP", each.getString("RSRV_STR2", ""));	//设备IP
                userData.put("IDC_DEVICE_PORT", each.getString("RSRV_STR3", ""));	//设备端口名称
                userData.put("IDC_DEVICE_INSTID", each.getString("INST_ID", ""));
                //REQ201812180018关于本省IDC流量计费产品增加按天生效时间免费月份及其他优化的需求
                userData.put("pam_EFFECTIVE_WAY_TYPE", each.getString("RSRV_STR4","")); //生效方式
                userData.put("pam_EFFECTIVE_DATE", each.getString("RSRV_STR5","")); //生效日期
                userData.put("pam_FREE_MONTH", each.getString("RSRV_STR6","")); //免费月份
                userData.put("pam_FREE_MONTH1", each.getString("RSRV_STR7","")); //免费月份
                userData.put("pam_FREE_MONTH2", each.getString("RSRV_STR8","")); //免费月份
                ds.add(userData);
        	}
        	datasetBData.put("DATA_VAL", JSONArray.fromObject(ds).toString().replaceAll("\"", "\'"));
        	result.put("IDCORDER_INFO", datasetBData);
        	result.put("IDC_ORDER_DATA", datasetBData);
        	result.put("IDC_ORDER_OLD_DATA", datasetBData);
        }
        //---add by chenhh6@20190225--end----REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求----
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
