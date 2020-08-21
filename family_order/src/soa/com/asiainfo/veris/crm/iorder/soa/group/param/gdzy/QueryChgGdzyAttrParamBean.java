package com.asiainfo.veris.crm.iorder.soa.group.param.gdzy;

import java.util.Iterator;
import java.util.Set;

import net.sf.json.JSONArray;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class QueryChgGdzyAttrParamBean  extends QueryAttrParamBean 
{

    public static IData queryGdzyParamAttrForChgInit(IData param) throws Exception
    {
    	IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String userId = param.getString("USER_ID");
        
        // 调用后台服务查页面上需要显示的参数信息
        //IDataset productInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "GDZY", "ZZZZ");
        IData productParam = new DataMap();
        productParam.put("ID", offerCode);
        productParam.put("ID_TYPE", "P");
        productParam.put("ATTR_CODE", "GDZY");
        productParam.put("EPARCHY_CODE", "ZZZZ");
        IDataset productInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", productParam);
        
        IData productData = new DataMap();
        productData.put("DATA_VAL", productInfo);
        result.put("GRPGDZY_INFO", productData);
        
        IData methodData = new DataMap();
        methodData.put("DATA_VAL", "ChgUs");
        result.put("NOTIN_METHOD_NAME", methodData);
        
        IData currentDateData = new DataMap();
        currentDateData.put("DATA_VAL", SysDateMgr.getSysDate());
        result.put("NOTIN_CURRENT_DATE", currentDateData);
        
        IDataset feeNameInfo = StaticUtil.getStaticList("GDZY_FEENAME");
        IData feeNameData = new DataMap();
        feeNameData.put("DATA_VAL", feeNameInfo);
        result.put("FEENAME_INFO", feeNameData);
        
        IData userOtherInfoData =new DataMap();
        IData attrItemA = new DataMap();
        
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        inParam.put("RSRV_VALUE_CODE", "GDZY");
        IDataset userOtherInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inParam);
        //判断OTHER表中有没有数据
        IDataset dataset = new DatasetList();
        if (null != userOtherInfo && userOtherInfo.size() > 0)
        {
            for (int i = 0; i < userOtherInfo.size(); i++)
            {
                IData userOtherData = (IData) userOtherInfo.get(i);
                IData userData = new DataMap();

                userData.put("pam_NOTIN_OPER_TAG", userOtherData.getString("RSRV_VALUE",""));
                userData.put("pam_NOTIN_PROJECT_NAME", userOtherData.getString("RSRV_STR1",""));//项目名称
                
                String dataId = userOtherData.getString("RSRV_STR2","");
                userData.put("pam_NOTIN_FEE_NAME", dataId);//收费名称
                userData.put("pam_NOTIN_FEE_NAME_V", "");//收费名称
                if(StringUtils.isNotBlank(dataId))
                {
                	String feeName = StaticUtil.getStaticValue("GDZY_FEENAME",dataId);
                	if(StringUtils.isNotBlank(feeName))
                	{
                		userData.put("pam_NOTIN_FEE_NAME_V", feeName);
                	}
                }
                
                userData.put("pam_NOTIN_FEE_COST", userOtherData.getString("RSRV_STR3","0"));//收费金额
                userData.put("pam_NOTIN_FEE_END_DATE", userOtherData.getString("RSRV_STR4",""));//收费截止时间
                userData.put("pam_NOTIN_REMARK", userOtherData.getString("RSRV_STR6",""));//备注
                dataset.add(userData);
            }
            
            userOtherInfoData.put("GDZY_INFO", JSONArray.fromObject(dataset).toString().replaceAll("\"", "\'"));
            userOtherInfoData.put("NOTIN_AttrGdzy", JSONArray.fromObject(dataset).toString().replaceAll("\"", "\'"));
            userOtherInfoData.put("NOTIN_OLD_AttrGdzy", JSONArray.fromObject(dataset).toString().replaceAll("\"", "\'"));
        }
        
        IData userAttrItem = IDataUtil.iDataA2iDataB(userOtherInfoData, "ATTR_VALUE");
        transComboBoxValue(userAttrItem,attrItemA);
        
        if (IDataUtil.isNotEmpty(userAttrItem))
        {
            Set<String> propNames = userAttrItem.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = userAttrItem.getData(key);
                IData attrItem = new DataMap();
                if(IDataUtil.isEmpty(attrCodeInfo))
                {
                	continue;
                }
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
                    {
                    	continue;
                    }
                    attrItem.put("DATA_VAL", attrItemValue);
                    result.put(key, attrItem);
                }
            }
        }
        
        return result;
    }
    
    private static void transComboBoxValue(IData userAttrItem, IData pzAttrItem) throws Exception
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
