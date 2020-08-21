
package com.asiainfo.veris.crm.order.soa.group.apnusermgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ApnUserMgrSvcBean
{
	
	/**
	 * APN用户从临时表入到正式表
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
    public static boolean doThisApnUserMgrInfo(IData inParam) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        String departId = inParam.getString("DEPART_ID");
        String operStaffId = inParam.getString("STAFF_ID");
        String eparchyCode = inParam.getString("EPARCHY_CODE");

        String importId = inParam.getString("IMPORT_ID");
        IDataset importDataset = CustManagerInfoQry.queryThisVpmnManagerInfo(importId, "", null);

        if(IDataUtil.isNotEmpty(importDataset))
        {
        	 for (int i = 0; i < importDataset.size(); i++)
             {
                 IData tempData = importDataset.getData(i);
                 String serialNumber = tempData.getString("RSRV_STR1","").trim();
                 if(StringUtils.isBlank(serialNumber))
                 {
                	 tempData.put("DEAL_STATE", "E");
                     tempData.put("REMARK", "从RSRV_STR1获取手机号码为空!");
                     tempData.put("DEAL_TIME", sysTime);
                     tempData.put("DEAL_STAFF_ID", operStaffId);
                     tempData.put("DEAL_DEPART_ID", departId);
                     ApnUserMgrInfoQry.updateImportDataApnUserInfo(tempData);
                     continue;
                 }
                 
                 IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
                 if (IDataUtil.isEmpty(userInfo))
                 {
                	 tempData.put("DEAL_STATE", "E");
                     tempData.put("REMARK", "用户资料不存在!");
                     tempData.put("DEAL_TIME", sysTime);
                     tempData.put("DEAL_STAFF_ID", operStaffId);
                     tempData.put("DEAL_DEPART_ID", departId);
                     ApnUserMgrInfoQry.updateImportDataApnUserInfo(tempData);
                     continue;
                 }
                 
                 String userId = userInfo.getString("USER_ID","");
                 IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValue(userId, "USER_APNTAG", serialNumber);
                 if(IDataUtil.isNotEmpty(otherInfos))
                 {
                     tempData.put("DEAL_STATE", "E");
                     tempData.put("REMARK", "用户已经是APN用户!");
                     tempData.put("DEAL_TIME", sysTime);
                     tempData.put("DEAL_STAFF_ID", operStaffId);
                     tempData.put("DEAL_DEPART_ID", departId);
                     ApnUserMgrInfoQry.updateImportDataApnUserInfo(tempData);
                     continue;
                 }
                 
                 IData otherInfo = new DataMap();
                 otherInfo.put("USER_ID", userId);
                 otherInfo.put("RSRV_VALUE_CODE", "USER_APNTAG");
                 otherInfo.put("RSRV_VALUE", serialNumber);
                 otherInfo.put("START_DATE", SysDateMgr.getSysTime());
                 otherInfo.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                 otherInfo.put("REMARK", "APN用户界面添加");
                 otherInfo.put("INST_ID", SeqMgr.getInstId("0898"));
                 otherInfo.put("STAFF_ID", operStaffId);
                 otherInfo.put("DEPART_ID", departId);
                 ApnUserMgrInfoQry.insOtherInfoForApnUser(otherInfo);
                 
                 tempData.put("DEAL_STATE", "J");
                 tempData.put("REMARK", "处理成功!");
                 tempData.put("DEAL_TIME", sysTime);
                 tempData.put("DEAL_STAFF_ID", operStaffId);
                 tempData.put("DEAL_DEPART_ID", departId);
                 ApnUserMgrInfoQry.updateImportDataApnUserInfo(tempData);
             }
        }

        IData batParam = new DataMap();
        batParam.put("IMPORT_ID", importId);
        batParam.put("DEAL_STATE", "2");
        batParam.put("DEAL_TIME", sysTime);
        batParam.put("DEAL_STAFF_ID", operStaffId);
        batParam.put("DEAL_DEPART_ID", departId);
        batParam.put("RSRV_STR10", "处理完成");
        return ApnUserMgrInfoQry.updateImportBatApnUserInfo(batParam, eparchyCode);
        
    }
    
}
