
package com.asiainfo.veris.crm.order.soa.person.busi.dandelionplans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.FreezeUserQry;

public class FreezeUserManageBean extends CSBizBean
{

    public int createPhone(IData param) throws Exception
    {
        String msisdn = param.getString("SERIAL_NUMBER");
        String numberType = param.getString("NUMBER_TYPE");
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        String createStaffId = getVisit().getStaffId();
        String createDepartId = getVisit().getDepartId();
        String status = "0001";
        String createTime = SysDateMgr.getSysTime(); // 获取当前时间
        if ("B".equals(numberType))
        {
            startDate = createTime;
            endDate = SysDateMgr.getTheLastTime(); // 获取
            status = "0002";
        }
        else
        {
            endDate = SysDateMgr.getLastDateThisMonth(); // 获取本月最后一天
        }
        boolean isSucceed = false;
        int isExist = 0;

        // 判断手机用户是否正常在网
        IData data = UcaInfoQry.qryUserInfoBySn(msisdn);

        if (IDataUtil.isEmpty(data))
        {
            isExist = 1;
            return isExist;
        }
        String userId = data.getString("USER_ID");
        // 新增之前，先判断是否存在同样的正常记录。
        param.put("USER_ID", userId);
        IDataset dataDeviceStaffId = FreezeUserQry.addCheckFreezeUser(param);
        // 当天不能重复添加
        IDataset dataDeviceStaffId2 = FreezeUserQry.addCheckFreezeUser2(param);
        if (IDataUtil.isNotEmpty(dataDeviceStaffId))
        {
            isExist = 2;
            return isExist;
        }
        else if (IDataUtil.isNotEmpty(dataDeviceStaffId2))
        {
            isExist = 3;
            return isExist;
        }
        else
        {
            param.put("PARTITION_ID", Integer.parseInt(userId) % 10000);
            param.put("USER_ID", ((IData) data.get(0)).getString("USER_ID"));
            param.put("SERIAL_NUMBER", msisdn);
            param.put("NUMBER_TYPE", numberType);
            param.put("STATUS", status);
            param.put("START_DATE", startDate);
            param.put("END_DATE", endDate);
            param.put("UPDATE_TIME", createTime);
            param.put("UPDATE_STAFF_ID", createStaffId);
            param.put("UPDATE_DEPART_ID", createDepartId);
            param.put("REMARK", "");
            param.put("RSRV_NUM1", "");
            param.put("RSRV_NUM2", "");
            param.put("RSRV_NUM3", "");
            param.put("RSRV_NUM4", "");
            param.put("RSRV_NUM5", "");
            param.put("RSRV_NUM6", "");
            param.put("RSRV_NUM7", "");
            param.put("RSRV_NUM8", "");
            param.put("RSRV_NUM9", "");
            param.put("RSRV_NUM10", "");
            param.put("RSRV_STR1", "");
            param.put("RSRV_STR2", "");
            param.put("RSRV_STR3", "");
            param.put("RSRV_STR4", "");
            param.put("RSRV_STR5", "");
            param.put("RSRV_STR6", "");
            param.put("RSRV_STR7", "");
            param.put("RSRV_STR8", "");
            param.put("RSRV_STR9", "");
            param.put("RSRV_STR10", "");
            param.put("RSRV_DATE1", "");
            param.put("RSRV_DATE2", "");
            param.put("RSRV_DATE3", "");
            param.put("RSRV_DATE4", "");
            param.put("RSRV_DATE5", "");
            param.put("RSRV_DATE6", "");
            param.put("RSRV_DATE7", "");
            param.put("RSRV_DATE8", "");
            param.put("RSRV_DATE9", "");
            param.put("RSRV_DATE10", "");
            param.put("RSRV_TAG1", "");
            param.put("RSRV_TAG2", "");
            param.put("RSRV_TAG3", "");
            param.put("RSRV_TAG4", "");
            param.put("RSRV_TAG5", "");
            param.put("RSRV_TAG6", "");
            param.put("RSRV_TAG7", "");
            param.put("RSRV_TAG8", "");
            param.put("RSRV_TAG9", "");
            param.put("RSRV_TAG10", "");
            isSucceed = Dao.insert("TF_F_USER_DANDELION_FREEZE", param); // 新增成功，返回true
            if (isSucceed)
            {
                isExist = 0;
            }
            return isExist;
        }
    }

    public IDataset queryUserFreeze(IData data, Pagination pagination) throws Exception
    {
        IDataset results = FreezeUserQry.queryUserFreeze(data, pagination);
        return results;
    }
}
