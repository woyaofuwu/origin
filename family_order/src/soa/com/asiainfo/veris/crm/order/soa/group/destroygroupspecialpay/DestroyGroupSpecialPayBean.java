
package com.asiainfo.veris.crm.order.soa.group.destroygroupspecialpay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;

public class DestroyGroupSpecialPayBean extends CSBizBean
{

    /**
     * 创建批量
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtBat(IData inParam) throws Exception
    {
        String checkValueListStr = inParam.getString("CHECKVALUE_LIST");
        String actionFlag = inParam.getString("ACTION_FLAG");
        String smsFlag = inParam.getString("SMS_FLAG");
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        String productId = inParam.getString("PRODUCT_ID");

        // 解析前台传过来的字符串信息
        String[] checkListArray = checkValueListStr.split("#");

        IDataset batDealList = new DatasetList();
        String grpBatchId = "";
        for (int i = 0, row = checkListArray.length; i < row; i++)
        {
            String checkValue = checkListArray[i];

            if (StringUtils.isEmpty(checkValue))
            {
                continue;
            }

            String[] checkArray = checkValue.trim().split(",");

            if (checkArray.length < 4)
            {
                continue;
            }

            String acctId = checkArray[0];
            String userId = checkArray[1];
            String payItemCode = checkArray[2];
            serialNumber = checkArray[3];
            
            String isGpwpUser = "";
            if(checkArray.length == 5)
            {
            	isGpwpUser = checkArray[4];	//1-是
            }

            if (StringUtils.isEmpty(acctId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(payItemCode) || StringUtils.isEmpty(serialNumber))
            {
                continue;
            }

            // 查询用户代付信息
            IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(userId, acctId, payItemCode);

            if (IDataUtil.isEmpty(userSpecialPayList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_638, acctId, userId);
            }

            // 构建批量明细数据
            IData batdealData = new DataMap();
            batdealData.put("SERIAL_NUMBER", serialNumber);
            batdealData.put("DATA1", acctId);
            batdealData.put("DATA2", userSpecialPayList.getData(0).getString("USER_ID_A"));
            batdealData.put("DATA3", payItemCode);
            batdealData.put("DATA4", actionFlag);
            if (StringUtils.isNotBlank(smsFlag) && "2".equals(smsFlag))
            {
                batdealData.put("DATA5", smsFlag);
            }
            batdealData.put("DATA6", isGpwpUser);	//add by chenzg@20170809 REQ201707240050关于在集团统一付费注销界面增加办理工作手机套餐折扣提醒的需求
            batDealList.add(batdealData);
        }

        // 创建批量任务
        if (IDataUtil.isNotEmpty(batDealList))
        {
            String batchTaskName = StringUtils.isBlank(serialNumber) ? "集团产品[" + productId + "]统付关系注销" : "用户[" + serialNumber + "]统付关系注销";
            IData condStrMap = new DataMap();
            condStrMap.put("MEB_VOUCHER_FILE_LIST", inParam.getString("MEB_VOUCHER_FILE_LIST", ""));
            condStrMap.put("AUDIT_STAFF_ID", inParam.getString("AUDIT_STAFF_ID", ""));
            
            // 创建集团注销的批量任务
            IData grpBatData = new DataMap();
            grpBatData.put("BATCH_OPER_TYPE", "PAYRELATIONDESTROY");
            grpBatData.put("BATCH_TASK_NAME", batchTaskName);
            grpBatData.put("SMS_FLAG", smsFlag);
            grpBatData.put("CREATE_TIME", SysDateMgr.getSysTime());
            grpBatData.put("ACTIVE_FLAG", "1");
            grpBatData.put("ACTIVE_TIME", SysDateMgr.getSysTime());
            grpBatData.put("DEAL_TIME", SysDateMgr.getSysTime());
            grpBatData.put("DEAL_STATE", "1");
            grpBatData.put("PRIORITY", "10");
            grpBatData.put("CODING_STR", condStrMap.toString());	//add by chenzg@20180706 REQ201804280001集团合同管理界面优化需求
            grpBatchId = BatDealBean.createBat(grpBatData, batDealList);
        }

        IData retData = new DataMap();
        retData.put("ORDER_ID", "批量任务号[" + grpBatchId + "]");

        return IDataUtil.idToIds(retData);
    }

}
