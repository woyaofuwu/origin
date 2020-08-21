
package com.asiainfo.veris.crm.order.soa.person.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CustUserInfoQry
{

    /**
     * 根据证件类型，证件号码查询所有的用户信息包含产品信息
     * 
     * @param psptTypeCode
     * @param psptId
     * @return
     * @throws Exception
     */
    public static IDataset qryAllUserAndBrandInfoByPsptId(String psptTypeCode, String psptId) throws Exception
    {
        IDataset allUserInfos = new DatasetList();

        IDataset custInfos = CustPersonInfoQry.qryPerInfoByPsptId(psptTypeCode, psptId);
        if (IDataUtil.isNotEmpty(custInfos))
        {
            int size = custInfos.size();
            if (size > 100)
            {
                // 该证件号[psptId]对应的客户信息大于100条，是测试证件号，不能在此查询！
                CSAppException.apperr(CrmUserException.CRM_USER_1180, psptId);
            }
            for (int i = 0; i < size; i++)
            {
                IData tempCustData = custInfos.getData(i);
                String custId = tempCustData.getString("CUST_ID");

                IData custData = new DataMap();
                custData.put("CUST_NAME", tempCustData.getString("CUST_NAME"));
                custData.put("LOCAL_NATIVE_CODE", tempCustData.getString("LOCAL_NATIVE_CODE"));
                custData.put("SEX", tempCustData.getString("SEX"));
                custData.put("PHONE", tempCustData.getString("PHONE"));
                custData.put("WORK_NAME", tempCustData.getString("WORK_NAME"));
                custData.put("POST_ADDRESS", tempCustData.getString("POST_ADDRESS"));
                custData.put("POST_CODE", tempCustData.getString("POST_CODE"));
                custData.put("POST_PERSON", tempCustData.getString("POST_PERSON"));
                custData.put("CONTACT", tempCustData.getString("CONTACT"));
                custData.put("CONTACT_PHONE", tempCustData.getString("CONTACT_PHONE"));
                custData.put("PSPT_TYPE_CODE", tempCustData.getString("PSPT_TYPE_CODE"));
                custData.put("PSPT_ID", tempCustData.getString("PSPT_ID"));
                custData.put("PSPT_ADDR", tempCustData.getString("PSPT_ADDR"));
                custData.put("HOME_ADDRESS", tempCustData.getString("HOME_ADDRESS"));
                // 查在线表
                IDataset userInfos = UserInfoQry.qryUserInfoByCusts(custId);
                if (IDataUtil.isNotEmpty(userInfos))
                {
                    if (userInfos.size() > 100)
                    {
                        // 该证件号[psptId]对应的客户ID[custId]的用户信息大于100条，是测试证件号，不能在此查询！
                        CSAppException.apperr(CrmUserException.CRM_USER_1181, psptId, custId);

                    }
                    for (int j = 0, jsize = userInfos.size(); j < jsize; j++)
                    {
                        IData tempUserData = userInfos.getData(j);
                        String userId = tempUserData.getString("USER_ID");
                        String removeTag = tempUserData.getString("REMOVE_TAG");

                        String brandCode = "";
                        String brandName = "";
                        String productId = "";
                        String productName = "";

                        if (StringUtils.equals("0", removeTag))
                        {
                            // 查有效的
                            IDataset productInfo = UserProductInfoQry.queryMainProductNow(userId);
                            if (IDataUtil.isNotEmpty(productInfo))
                            {
                                brandCode = productInfo.getData(0).getString("BRAND_CODE");
                                brandName = productInfo.getData(0).getString("BRAND_NAME");
                                productId = productInfo.getData(0).getString("PRODUCT_ID");
                                productName = productInfo.getData(0).getString("PRODUCT_NAME");
                            }

                        }
                        else if (StringUtils.equals("5", removeTag))// 返销的用户
                        {
                            // 返销的用户没有产品了
                        }
                        else
                        {
                            // 查失效的
                            IData productInfo = UserProductInfoQry.qryLasterMainProdInfoByUserId(userId);
                            if (IDataUtil.isNotEmpty(productInfo))
                            {
                                brandCode = productInfo.getString("BRAND_CODE");
                                brandName = productInfo.getString("BRAND_NAME");
                                productId = productInfo.getString("PRODUCT_ID");
                                productName = productInfo.getString("PRODUCT_NAME");
                            }
                        }

                        tempUserData.put("BRAND_CODE", brandCode);
                        tempUserData.put("BRAND_NAME", brandName);
                        tempUserData.put("PRODUCT_ID", productId);
                        tempUserData.put("PRODUCT_NAME", productName);
                        tempUserData.putAll(custData);// 加入客户资料信息
                    }
                    allUserInfos.addAll(userInfos);
                }

                // 查历史表
                IDataset hisUserInfos = UserInfoQry.qryAllUserInfoByCustIdFromHis(custId);
                if (IDataUtil.isNotEmpty(hisUserInfos))
                {
                    if (userInfos.size() > 100)
                    {
                        // 该证件号[psptId]对应的客户ID[custId]的历史用户信息大于100条，是测试证件号，不能在此查询！
                        CSAppException.apperr(CrmUserException.CRM_USER_1182, psptId, custId);
                    }
                    for (int j = 0, jsize = hisUserInfos.size(); j < jsize; j++)
                    {
                        IData tempUserData = hisUserInfos.getData(j);
                        String userId = tempUserData.getString("USER_ID");
                        String removeTag = tempUserData.getString("REMOVE_TAG");
                        String brandCode = "";
                        String brandName = "";
                        String productId = "";
                        String productName = "";

                        if (StringUtils.equals("5", removeTag))// 返销的用户
                        {
                            // 返销的用户没有产品了
                        }
                        else
                        {
                            IData hisProductInfo = UserProductInfoQry.qryLasterMainProdInfoByUserIdFromHis(userId);
                            if (IDataUtil.isNotEmpty(hisProductInfo))
                            {
                                brandCode = hisProductInfo.getString("BRAND_CODE");
                                brandName = hisProductInfo.getString("BRAND_NAME");
                                productId = hisProductInfo.getString("PRODUCT_ID");
                                productName = hisProductInfo.getString("PRODUCT_NAME");
                            }
                        }

                        tempUserData.put("BRAND_CODE", brandCode);
                        tempUserData.put("BRAND_NAME", brandName);
                        tempUserData.put("PRODUCT_ID", productId);
                        tempUserData.put("PRODUCT_NAME", productName);
                        tempUserData.putAll(custData);// 加入客户资料信息
                    }
                    allUserInfos.addAll(hisUserInfos);
                }
            }
        }
        return allUserInfos;
    }

    /**
     * 根据证件类型，证件号码查询所有的用户信息
     * 
     * @param psptTypeCode
     * @param psptId
     * @return
     * @throws Exception
     */
    public static IDataset qryAllUserInfoByPsptId(String psptTypeCode, String psptId) throws Exception
    {
        IDataset allUserInfos = new DatasetList();

        IDataset custInfos = CustPersonInfoQry.qryPerInfoByPsptId(psptTypeCode, psptId);
        if (IDataUtil.isNotEmpty(custInfos))
        {
            int size = custInfos.size();
            for (int i = 0; i < size; i++)
            {
                String custId = custInfos.getData(i).getString("CUST_ID");
                // 查在线表
                IDataset userInfos = UserInfoQry.getUserInfoByCusts(custId);
                allUserInfos.addAll(userInfos);

                // 查历史表
                IDataset hisUserInfos = UserInfoQry.qryAllUserInfoByCustIdFromHis(custId);
                allUserInfos.addAll(hisUserInfos);
            }
        }
        return allUserInfos;
    }

    /**
     * 查询身份证可以选占的有效号码个数
     * @param params
     * @return
     * @throws Exception
     * wangjx
     * 2014-10-24
     */
    public static IDataset qryValidPhoneNumByPsptId(IData params) throws Exception
    {
    	IDataset returnData = new DatasetList();
    	IData data = new DataMap();
    	data.put("X_RESULTCODE", "0");
    	data.put("X_RESULTINFO", "ok");
    	
    	//查询该号码是否空闲
    	params.put("QRY_TAG", 0);
    	String serialNumber = params.getString("SERIAL_NUMBER", "");
    	if(serialNumber==null||"".equals(serialNumber)){
        	data.put("X_RESULTCODE", "-1");
        	data.put("X_RESULTINFO", "请输入选择的手机号码");
        	returnData.add(data);
        	return returnData;
    	}
    	String psptId = params.getString("PSPT_ID", "");
    	if(psptId==null||"".equals(psptId)){
        	data.put("X_RESULTCODE", "-2");
        	data.put("X_RESULTINFO", "请输入办理业务的证件号码");
        	returnData.add(data);
        	return returnData;
    	}
    	
    	try{
        	IDataset phoneInfo = ResCall.getMphonecodeInfo(params.getString("SERIAL_NUMBER"),params.getString("QRY_TAG"));//CSAppCall.call("RM.ResPhoneIntfSvc.getMphonecodeInfo", params);
        	if (IDataUtil.isEmpty(phoneInfo))
            {
            	data.put("X_RESULTCODE", "-3");
            	data.put("X_RESULTINFO", "该号码已经被选占或开户");
            	returnData.add(data);
            	return returnData;
            }
    	}catch(Exception e){
        	data.put("X_RESULTCODE", "-99");
        	data.put("X_RESULTINFO", "查询号码空闲信息失败："+e.getMessage());
        	returnData.add(data);
        	return returnData;
    	}
    	
    	try{
        	returnData = ResCall.qryValidPhoneNumByPsptId(params);//CSAppCall.call("RM.ResPhoneIntfSvc.qryValidPhoneNumByPsptId", params);
    	}catch(Exception e){
    		String resultInfo = e.toString();
    		int deal1 = resultInfo.indexOf("已经开户的用户数+正在有效期内的预约选号数大于等");
    		int deal3 = resultInfo.indexOf("RES_PHONE_1606:网上选号");
    		if(deal1>=0||deal3>=0){
            	data.put("X_RESULTCODE", "-6");
            	data.put("X_RESULTINFO", "该身份证号码已经开户的用户数+正在有效期内的预约选号数大于等于最大值，不允许再预约新号码");
            	returnData.add(data);
            	return returnData;
    		}
    		int deal2 = resultInfo.indexOf("RES_PHONE_1600:号码");
    		if(deal2>=0){
            	data.put("X_RESULTCODE", "-5");
            	data.put("X_RESULTINFO", "该号码[["+serialNumber+"]已被其他用户选占或预占，请重新选择其它号码");
            	returnData.add(data);
            	return returnData;
    		}
        	data.put("X_RESULTCODE", "-99");
        	data.put("X_RESULTINFO", "查询证件开户信息失败："+e.toString());
        	returnData.add(data);
        	return returnData;
    	}
    	
    	return returnData;
    }
}
