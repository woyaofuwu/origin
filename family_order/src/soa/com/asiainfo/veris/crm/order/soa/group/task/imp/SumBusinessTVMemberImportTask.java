
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class SumBusinessTVMemberImportTask extends ImportTaskExecutor
{

    private static String MAC_NUM_REG = "[0-9a-fA-F]{12}";

    private static String SN_NUM_REG = "[0-9a-fA-F]{32}";

    private static final String IMPORT_RESULT = "IMPORT_RESULT";

    private static final String IMPORT_ERROR = "IMPORT_ERROR";
    
    public static final Integer MAC_NUM_STR = 12;

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {
        // 导入文件不为空
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到表格数据，或表格未填写完整！");
        }
        IDataset infos = new DatasetList();
        String operType = data.getString("OPER_TYPE");
        String serialNumber = data.getString("EC_SERIAL_NUMBER");
        String applyMebStr = data.getString("MEB_LIST");
        String productId = data.getString("PRODUCT_ID");
        IDataset applyMebList = new DatasetList();
        if(StringUtils.isNotBlank(applyMebStr)) {
            applyMebList = new DatasetList(applyMebStr);
        }
        IDataset serialNumberList = new DatasetList();
        if ("DelMeb".equals(operType)) {
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serialNumber);
            IDataset userInfoBySn = CSAppCall.call("SS.QuickOrderDataSVC.qryUserInfoUserIdBySerialNumber", param);
            if (IDataUtil.isEmpty(userInfoBySn))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据服务号码【" + serialNumber + "】， 未找到用户信息！");
            }
            data.put("USER_ID_A", userInfoBySn.first().getString("USER_ID"));
            // 删除成员交验
            IDataset memberList = CSAppCall.call("SS.QryEspMemberSVC.queryEspMember", data);
            for (int i = 0, len = memberList.size(); i < len; i++) {
                IData memberData = (IData) memberList.get(i);
                if (memberData.getBoolean("FLAG")) {
                    serialNumberList.add(memberData.getString("SERIAL_NUMBER"));
                } else {
                    serialNumberList.add(memberData.getString("DEV_MAC_NUMBER"));
                }
            }

        }
        else {
            //新增时，自身添加的号码进行过滤
            IDataset oldmebList = new DatasetList();
            if(IDataUtil.isNotEmpty(applyMebList)) {
                for(Object obj : applyMebList) {
                    IData memberData = (IData) obj;
                    oldmebList.add(memberData.getString("SERIAL_NUMBER"));
                }
            }
            //查询历史数据和在途数据
            IData queryData = new DataMap();
            queryData.put("PRODUCT_ID", "380700");
            queryData.put("MEMBERLIST", dataset);
            IDataset existMemberList = qryExistNums(queryData);
            if (IDataUtil.isNotEmpty(existMemberList)) {
                for (int i = 0, len = existMemberList.size(); i < len; i++) {
                    IData memberData = (IData) existMemberList.get(i);
                    //过滤掉自身添加的号码
                    if (oldmebList.contains(memberData.getString("SERIAL_NUMBER_B"))) {
                        continue;
                    }
                    serialNumberList.add(memberData.getString("SERIAL_NUMBER_B"));
                }
            }
        }
        
        IDataset existList = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData memberData = new DataMap();
            IData importData = dataset.getData(i);
            //判断成员信息是否为空
            IDataUtil.chkParam(importData, "DEV_MAC_NUMBER");
            IDataUtil.chkParam(importData, "DEV_SN_NUMBER");
            String macNumber = importData.getString("DEV_MAC_NUMBER");
            String snNumber = importData.getString("DEV_SN_NUMBER");
            IData mebInfo = new DataMap();
            mebInfo.put("DEV_MAC_NUMBER", macNumber);
            mebInfo.put("DEV_SN_NUMBER", snNumber);
            // 成员号码格式有效性校验
            IData resultInfo = checkSimpleNumber(mebInfo);
            // 校验导入模板是否存在重复mac号
            if (existList.contains(macNumber)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "文件中存在重复机顶盒侧MAC号【" + macNumber + "】, 请修改后重新提交！");
            }
            existList.add(macNumber);
            if (BizCtrlType.MinorecAddMember.equals(operType) || StringUtils.isBlank(operType)) {
                if (serialNumberList.contains(macNumber)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "机顶盒侧MAC号【" + macNumber + "】已存在，请勿重复添加！");
                }
            } else if (BizCtrlType.MinorecDestroyMember.equals(operType)) {
                if (!serialNumberList.contains(macNumber)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "删除机顶盒侧MAC号【" + macNumber + "】不存在，请重试！");
                }
            }
            if (!resultInfo.getBoolean(IMPORT_RESULT))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo.getString(IMPORT_ERROR));
            }
            memberData.put("DEV_MAC_NUMBER", macNumber);
            memberData.put("DEV_SN_NUMBER", snNumber);
            infos.add(memberData);
        }
        SharedCache.set("IMPORT_MEMBER_INFO" + productId, infos);
        return null;
    }

    /**
     * @Title: checkNumber
     * @Description: 校验新增成员的有效性
     * @param macNumber
     * @param snNumber IData
     * @author zhangzg
     * @date 2019年11月6日上午11:47:49
     */
    public static IData checkSimpleNumber(IData mebInfo)
    {

        IData checkResult = new DataMap();
        String macNumber = mebInfo.getString("DEV_MAC_NUMBER");
        String snNumber = mebInfo.getString("DEV_SN_NUMBER");
        checkResult.put(IMPORT_RESULT, true);
        mebInfo.putAll(checkResult);
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
            checkResult.put(IMPORT_ERROR, "错误【" + macNumber + "】！机顶盒侧MAC号为12位字母【a-fA-F】+数字的组合，请输入正确！");
            mebInfo.putAll(checkResult);
        }
        else if (!matcherSN.matches())
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "错误【" + snNumber + "】！机顶盒侧SN号为32位字母【a-fA-F】+数字的组合，请输入正确！");
            mebInfo.putAll(checkResult);
        }
        else if (!macNumber.equals(snNumber.substring(snNumber.length() - MAC_NUM_STR, snNumber.length())))
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "错误【" + macNumber + "】！MAC地址必须和SN号的12位相同！");
            mebInfo.putAll(checkResult);
        }
        return mebInfo;
    }

    /**
     * @Title: checkAddMebNumber
     * @Description: 新增号码重复交验
     * @param macNumber
     * @param snNumber
     * @return
     * @throws Exception IData
     * @author zhangzg
     * @date 2019年11月6日上午11:48:12
     */
    public static IData checkAddMebNumber(IData mebInfo) throws Exception
    {
        IData checkResult = new DataMap();
        String macNumber = mebInfo.getString("DEV_MAC_NUMBER");
        IDataset applyMebList = mebInfo.getDataset("APPLY_MEBLIST");
        IDataset applySeriNums = new DatasetList();
        if(IDataUtil.isNotEmpty(applyMebList)) {
            for (Object obj : applyMebList) {
                IData applyMebData = (IData) obj;
                applySeriNums.add(applyMebData.getString("SERIAL_NUMBER"));
            }
        }
        IData data = new DataMap();
        data.put("PRODUCT_ID", "380700");
        data.put("MEMBERLIST", mebInfo);
        // 新增成员重复交验
        IDataset results = new DatasetList();
        IDataset bbMebInfos = CSAppCall.call("SS.QuickOrderMemberSVC.qryExistsRelationbbMebInfos", data);
//        IDataset existMebInfos = CSAppCall.call("SS.QuickOrderMemberSVC.qryAllMebInfosByProductId", data);
        if(IDataUtil.isNotEmpty(bbMebInfos)) {
            results.addAll(bbMebInfos);
        }
//        if(IDataUtil.isNotEmpty(existMebInfos)) {
//            results.addAll(existMebInfos);
//        }
        if (IDataUtil.isNotEmpty(results))
        {
            for (Object obj : results)
            {
                IData mebData = (IData) obj;
                String oldNumber = mebData.getString("SERIAL_NUMBER_B");
                if(applySeriNums.contains(oldNumber)) {
                    continue;
                }
                if (macNumber.equals(oldNumber))
                {
                    checkResult.put(IMPORT_RESULT, false);
                    checkResult.put(IMPORT_ERROR, "机顶盒侧MAC号【" + macNumber + "】已存在，请勿重复添加！");
                    mebInfo.putAll(checkResult);
                    break;
                }
            }
        }
        return mebInfo;
    }
    
    public IDataset qryExistNums(IData queryData) throws Exception {
        IData result = new DataMap();
        result.put("PRODUCT_ID", "380700");
        result.put("MEMBERLIST", queryData.getDataset("MEMBERLIST"));
        IDataset memberList = CSAppCall.call("SS.QuickOrderMemberSVC.qryExistsRelationbbMebInfos", result);
        return memberList;
    }

    /**
     * @Title: checkDelMebNumber
     * @Description: 删除号码有效校验
     * @param macNumber
     * @param snNumber
     * @return
     * @throws Exception IData
     * @author zhangzg
     * @date 2019年11月6日上午11:48:29
     */
    public static IData checkDelMebNumber(IData mebInfo) throws Exception
    {
        IData checkResult = new DataMap();
        String macNumber = mebInfo.getString("DEV_MAC_NUMBER");
        IData data = new DataMap();
        data.put("USER_ID_A", mebInfo.getString("USER_ID_A"));
        // 删除成员交验
        IDataset mebList = CSAppCall.call("SS.QryEspMemberSVC.queryEspMember", data);
        if (IDataUtil.isNotEmpty(mebList))
        {
            for (Object obj : mebList)
            {
                IData mebData = (IData) obj;
                String oldNumber = mebData.getString("DEV_MAC_NUMBER");
                checkResult.put(IMPORT_RESULT, false);
                if (macNumber.equals(oldNumber))
                {
                    checkResult.put(IMPORT_RESULT, true);
                    mebInfo.putAll(checkResult);
                    checkResult.put(IMPORT_ERROR, "");
                    break;
                }
                checkResult.put(IMPORT_ERROR, "删除机顶盒侧MAC号【" + macNumber + "】不存在，请重试！");
                mebInfo.putAll(checkResult);
            }
        }
        return mebInfo;
    }
}
