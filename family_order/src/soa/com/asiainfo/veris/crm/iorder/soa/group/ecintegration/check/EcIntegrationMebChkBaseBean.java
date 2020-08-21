package com.asiainfo.veris.crm.iorder.soa.group.ecintegration.check;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class EcIntegrationMebChkBaseBean extends CSBizBean
{
    public static boolean isBatch = true; //成员新增是否批量操作
    public static String productId ; //产品id
    public static String custId ;    //客户id
    //add 11/5
    public static String ecUserId ;    //产品用户id
    public static String bpmTempletId ;    //流程模板id

    protected static final String IMPORT_RESULT = "IMPORT_RESULT";

    protected static final String IMPORT_FLAG= "IMPORT_FLAG";

    protected static final String IMPORT_ERROR = "IMPORT_ERROR"; 

    public IDataset checkEcIntegrationMember(IData param) throws Exception
    {
        init(param);
        
        IDataset resultList = new DatasetList();
        if(isBatch)
        {//批量新增
            IDataset memberList = param.getDataset("CHECK_MEMBERLIST");
            
            IData result = checkEcIntegrationMebList(memberList);
            resultList.add(result);
        }
        else
        {//单个新增
            IData memberInfo = param.getData("CHECK_MEMBERINFO");
            
            IData result = checkEcIntegrationMebSingle(memberInfo);
            resultList.add(result);
        }
        return resultList;
    }
    
    protected IData checkEcIntegrationMebSingle(IData memberInfo) throws Exception
    {

        checkBase(memberInfo);
        
        checkOther(memberInfo);
        
        if(memberInfo.getBoolean(IMPORT_RESULT))
        {
            memberInfo.put("OPER_CODE", "0"); //0-新增
        }
        
        return memberInfo;
    }
    
    protected IData checkEcIntegrationMebList(IDataset checkMebList) throws Exception
    {
        IDataset successList = new DatasetList();
        IDataset failureList = new DatasetList();
        if(IDataUtil.isNotEmpty(checkMebList))
        {
            for(int i = 0, size = checkMebList.size(); i < size; i++)
            {
                IData mebInfo = checkMebList.getData(i);
                
                checkBase(mebInfo);
                
                checkOther(mebInfo);
                
                if(mebInfo.getBoolean(IMPORT_RESULT))
                {
                    mebInfo.put("OPER_CODE", "0"); //0-新增
                    successList.add(mebInfo);
                }
                else
                {
                    failureList.add(mebInfo);
                }
            }
        }
        IData checkResult = new DataMap();
        checkResult.put("SUCC_LIST", successList);
        checkResult.put("FAIL_LIST", failureList);
        return checkResult;
    }
    
    /**
     * 子类重写该方法，用于校验业务特殊
     * @param mebInfo
     * @throws Exception
     */
    public void checkOther(IData mebInfo) throws Exception
    {
        
    }
    
    protected void checkBase(IData mebInfo) throws Exception
    {
        IData userRuleResult = checkValidUserRule(mebInfo.getString("SERIAL_NUMBER"), mebInfo.getBoolean("IMPORT_RESULT", true));
        mebInfo.putAll(userRuleResult);
    }
    
    /**
     * 是否有效的在网用户
     * @param serilaNumber
     * @param importResult
     * @return
     * @throws Exception
     */
    protected IData checkValidUserRule(String serilaNumber, boolean importResult) throws Exception
    {
        IData checkResult = new DataMap();
        if(importResult)
        {
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serilaNumber);
            if(IDataUtil.isEmpty(userInfo))
            {
                checkResult.put(IMPORT_RESULT, false);
                checkResult.put(IMPORT_ERROR, "根据手机号码没有获取到有效的用户信息！");
                return checkResult;
            }
        }
        checkResult.put(IMPORT_RESULT, importResult);
        return checkResult;
    }
    
    protected void init(IData param) throws Exception
    {
        isBatch = param.getBoolean("IS_BATCH");
        productId = param.getString("PRODUCT_ID");
        custId = param.getString("CUST_ID");
        ecUserId = param.getString("EC_USER_ID");
        bpmTempletId = param.getString("BPM_TEMPLET_ID");
    }
}
