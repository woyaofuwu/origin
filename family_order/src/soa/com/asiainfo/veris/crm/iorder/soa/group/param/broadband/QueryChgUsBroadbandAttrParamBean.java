package com.asiainfo.veris.crm.iorder.soa.group.param.broadband;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.ailk.bizcommon.priv.StaffPrivUtil;
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

public class QueryChgUsBroadbandAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryBroadbandAttrForChgInit(IData param) throws Exception
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
        
        
        
        IData userVpnData =new DataMap();
        IData userParam = new DataMap();
        userParam.put("USER_ID", userId);
        userParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSAppCall.call("CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userParam);
        if(IDataUtil.isNotEmpty(userInfo)){
            userVpnData = userInfo.getData(0);
            
            userVpnData.put("NOTIN_DETMANAGER_INFO", userVpnData.getString("RSRV_STR7"));
            userVpnData.put("NOTIN_DETMANAGER_PHONE", userVpnData.getString("RSRV_STR8"));
            userVpnData.put("NOTIN_DETADDRESS", userVpnData.getString("RSRV_STR9"));
            userVpnData.put("NOTIN_PROJECT_NAME", userVpnData.getString("RSRV_STR10"));
        }
        
      //获取权限
        String staffId = getVisit().getStaffId();
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, "GRP_BROADBAND_FEE_PRV");
        userVpnData.put("HAS_FEE_PRIV", bool);
        
     // 调用后台服务查保存的商务宽带参数信息
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "N002");

        IDataset userAttrInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);

        // 判断OTHER表中有没有数据
        if (null != userAttrInfo && userAttrInfo.size() > 0)
        {
            IDataset datasetA = new DatasetList();
            for (int i = 0; i < userAttrInfo.size(); i++)
            {
                IData userAttrData = (IData) userAttrInfo.get(i);
                IData userData = new DataMap();

                userData.put("pam_NOTIN_OPER_TAG", userAttrData.get("RSRV_VALUE"));
                userData.put("pam_NOTIN_NUM", userAttrData.get("RSRV_STR1"));
                //userData.put("pam_NOTIN_MONTHLY_FEE", userAttrData.get("RSRV_STR2"));
                                
                // 安装调试费、一次性费用START_DATE不在当月显示为0
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date nowDate = new Date();
                String startDate4 = userAttrData.getString("START_DATE");
                int nowYear = nowDate.getYear();
                int nowMonth = nowDate.getMonth();
                int startYear = sdf.parse(startDate4).getYear();
                int startMonth = sdf.parse(startDate4).getMonth();
                
                if (nowMonth == startMonth && nowYear == startYear){
                    userData.put("pam_NOTIN_INSTALLATION_COST", userAttrData.get("RSRV_STR3"));//安装调测费
                    userData.put("pam_NOTIN_ONE_COST", userAttrData.get("RSRV_STR4")); //一次性通信服务费
                    
                }else{
                    userData.put("pam_NOTIN_INSTALLATION_COST", "0");
                    userData.put("pam_NOTIN_ONE_COST", "0");
                }
                
                IData broadData = new DataMap();
                broadData.put("USER_ID", userId);
                broadData.put("RSRV_VALUE_CODE", "N003");
                IDataset userBroadInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", broadData);
                
                if (IDataUtil.isNotEmpty(userBroadInfo)){
                    for (int j = 0; j < userBroadInfo.size(); j++)
                    {
                        IData userBroadData = (IData) userBroadInfo.get(j);
                        String broadCode = userBroadData.getString("RSRV_VALUE","");
                        String rsrvStr2 = userBroadData.getString("RSRV_STR2","");
                        
                        if("M4".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_4", rsrvStr2);
                        } else if("M8".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_8", rsrvStr2);
                        } else if("M10".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_10", rsrvStr2);
                        } else if("M12".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_12", rsrvStr2);
                        } else if("M20".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_20", rsrvStr2);
                        } else if("M50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_50", rsrvStr2);
                        } else if("M100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_100", rsrvStr2);
                        } else if("CZM20".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZ20", rsrvStr2);
                        } else if("CZM50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZ50", rsrvStr2);
                        } else if("CZM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZ100", rsrvStr2);
                        } else if("CZM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZ200", rsrvStr2);
                        } else if("JCM20".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JC20", rsrvStr2);
                        } else if("JCM50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JC50", rsrvStr2);
                        } else if("JCM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JC100", rsrvStr2);
                        } else if("JCM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JC200", rsrvStr2);
                        } else if("JYM50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JY50", rsrvStr2);
                        } else if("JYM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JY100", rsrvStr2);
                        } else if("JYM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JY200", rsrvStr2);
                        } else if("JYM500".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JY500", rsrvStr2);
                        } else if("ZZM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_ZZ100", rsrvStr2);
                        } else if("ZZM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_ZZ200", rsrvStr2);
                        } else if("ZZM500".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_ZZ500", rsrvStr2);
                        } else if("ZZM1000".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_ZZ1000", rsrvStr2);
                        } else if("CZSXM20".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZSX20", rsrvStr2);
                        } else if("CZSXM50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZSX50", rsrvStr2);
                        } else if("SWSXM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_SWSX100", rsrvStr2);
                        } else if("SWSXM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_SWSX200", rsrvStr2);
                        } else if("SWSXM500".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_SWSX500", rsrvStr2);
                        } else if("QYSWM1000".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_QYSW1000", rsrvStr2);
                        }
                    }
                }
                
                datasetA.add(userData);
            }
            userVpnData.put("VISP_INFO", JSONArray.fromObject(datasetA).toString().replaceAll("\"", "\'"));
            userVpnData.put("NOTIN_AttrInternet", JSONArray.fromObject(datasetA).toString().replaceAll("\"", "\'"));
            userVpnData.put("NOTIN_OLD_AttrInternet", JSONArray.fromObject(datasetA).toString().replaceAll("\"", "\'"));

        }
        
        if (IDataUtil.isEmpty(userVpnData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_130, param.getString("USER_ID"));
        }
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
