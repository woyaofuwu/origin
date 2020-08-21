
package com.asiainfo.veris.crm.iorder.soa.group.ecintegration.check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class SumBusinessTVMebChkBean extends EcIntegrationMebChkBaseBean
{

    /**
     * mac号码正则验证
     */
    public static String MAC_NUM_REG = "[0-9a-fA-F]{12}";
    public static String SN_NUM_REG = "[0-9a-fA-F]{32}";
    public static final Integer MAC_NUM_STR = 12;

    @Override
    public IDataset checkEcIntegrationMember(IData param) throws Exception
    {
        init(param);
        IDataset resultList = new DatasetList();
        if (isBatch)
        {// 批量新增
            IDataset memberList = param.getDataset("CHECK_MEMBERLIST");
            IData result = checkEcIntegrationMebList(memberList);
            resultList.add(result);
        }
        else
        {// 单个新增
            IData memberInfo = param.getData("CHECK_MEMBERINFO");
            IData result = checkEcIntegrationMebSingle(memberInfo);
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 批量校验成员
     */
    @Override
    protected IData checkEcIntegrationMebList(IDataset checkMebList) throws Exception
    {
        IDataset successList = new DatasetList();
        IDataset failureList = new DatasetList();
        if (IDataUtil.isNotEmpty(checkMebList))
        {
            //新增号码重复交验
            batchCheckOther(checkMebList);
            for (int i = 0, size = checkMebList.size(); i < size; i++)
            {
                IData mebInfo = checkMebList.getData(i);
                mebInfo.put("EC_USER_ID", ecUserId);
                mebInfo.put("BPM_TEMPLET_ID", bpmTempletId);
                //号码规则校验
                checkOther(mebInfo);
                if (mebInfo.getBoolean(IMPORT_RESULT))
                {
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
     ** 批量查询成员号码说法已有和正在被占用。
     * @param checkMebList
     * @Date 2019年12月21日
     * @author xieqj 
     * @throws Exception 
     */
    private void batchCheckOther(IDataset checkMebList) throws Exception {
        IData queryData = new DataMap();
        queryData.put("MEMBERLIST", checkMebList);
        IDataset existMemberList = qryExistMember(queryData);
        StringBuilder existsNums = new StringBuilder();
        for(Object obj : existMemberList) {
            IData data = (IData)obj;
            existsNums.append(data.getString("SERIAL_NUMBER_B") + ",");
        }
        for (int i = 0, size = checkMebList.size(); i < size; i++) {
            IData mebInfo = checkMebList.getData(i);
            String macNumber = mebInfo.getString("DEV_MAC_NUMBER");
            if(existsNums.toString().contains(macNumber)) {
                mebInfo.put(IMPORT_RESULT, false);
                mebInfo.put(IMPORT_ERROR, "机顶盒侧MAC号【" + macNumber + "】已存在，请勿重复添加！");
            }
        }
    }

    /**
     * 单个成员校验
     */
    @Override
    protected IData checkEcIntegrationMebSingle(IData memberInfo) throws Exception
    {
        //号码规则校验
        checkOther(memberInfo);
        String macNumber = memberInfo.getString("DEV_MAC_NUMBER");
        // 新增成员重复交验
        IDataset memberList = qryExistMember(memberInfo);
        if (IDataUtil.isNotEmpty(memberList))
        {
            for (Object obj : memberList)
            {
                IData mebData = (IData) obj;
                String oldNumber = mebData.getString("SERIAL_NUMBER_B");
                if (macNumber.equals(oldNumber))
                {
                    memberInfo.put(IMPORT_RESULT, false);
                    memberInfo.put(IMPORT_ERROR, "机顶盒侧MAC号【" + macNumber + "】已存在，请勿重复添加！");
                }
            }
        }
        return memberInfo;
    }

    /**
     * 规则信息校验
     */
    @Override
    public void checkOther(IData mebInfo) throws Exception
    {
        IData checkResult = new DataMap();
        String macNumber = mebInfo.getString("DEV_MAC_NUMBER");
        String snNumber = mebInfo.getString("DEV_SN_NUMBER");
        if (!mebInfo.containsKey(IMPORT_RESULT)) {
            checkResult.put(IMPORT_RESULT, true);
            mebInfo.putAll(checkResult);
        }
        // 正则表达式校验
        Pattern patternMAC = Pattern.compile(MAC_NUM_REG);
        Matcher matcherMAC = patternMAC.matcher(macNumber);
        Pattern patternSN = Pattern.compile(SN_NUM_REG);
        Matcher matcherSN = patternSN.matcher(snNumber);
        if (StringUtils.isEmpty(macNumber))
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "机顶盒侧MAC号为空，请输入！");
            mebInfo.putAll(checkResult);
        }
        else if (StringUtils.isEmpty(snNumber))
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "机顶盒侧SN号为空，请输入！");
            mebInfo.putAll(checkResult);
        }
        else if (!matcherMAC.matches())
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "机顶盒侧MAC号为12位字母【a-fA-F】+数字的组合，请输入正确！");
            mebInfo.putAll(checkResult);
        }
        else if (!matcherSN.matches())
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "机顶盒侧SN号为32位字母【a-fA-F】+数字的组合，请输入正确！");
            mebInfo.putAll(checkResult);
        }
        else if (!macNumber.equals(snNumber.substring(snNumber.length() - MAC_NUM_STR, snNumber.length())))
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "MAC地址必须和SN号码后12位相同！");
            mebInfo.putAll(checkResult);
        }
    }

    /**
     * 
    * @Title: qryExistMember 
    * @Description: 获取已添加成员信息 
    * @param ecUserId
    * @return
    * @throws Exception IDataset
    * @author zhangzg
     * @param macNumber 
    * @date 2019年11月4日下午7:16:56
     */
    public IDataset qryExistMember(IData queryData) throws Exception
    {
        IData result = new DataMap();
        IDataset results = new DatasetList();
        result.put("PRODUCT_ID", "380700");
        result.put("SERIAL_NUMBER_B", queryData.getString("DEV_MAC_NUMBER"));
        result.put("MEMBERLIST", queryData.getDataset("MEMBERLIST"));
        //同时查询 TF_F_RELATION_BB   TF_B_EOP_QUICKORDER_MEB
        IDataset bbMebInfos = CSAppCall.call("SS.QuickOrderMemberSVC.qryExistsRelationbbMebInfos", result);
//        IDataset existMebInfos = CSAppCall.call("SS.QuickOrderMemberSVC.qryAllMebInfosByProductId", result);
        if(IDataUtil.isNotEmpty(bbMebInfos)) {
            results.addAll(bbMebInfos);
        }
//        if(IDataUtil.isNotEmpty(existMebInfos)) {
//            results.addAll(existMebInfos);
//        }
        return results;
    }

}
