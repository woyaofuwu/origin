
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ForbidenInfoBean extends CSBizBean
{
    /**
     * 删除用户禁查信息
     * 
     * @param pd
     * @param param
     * @throws Exception
     */
    public void CancelForbidenInfo(IData param) throws Exception
    {

        IData inparams = new DataMap();
        String strUserId = param.getString("USER_ID");
        String strDepartId = param.getString("DEPART_ID");
        String strStaffId = param.getString("STAFF_ID");

        inparams.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        inparams.put("STAFF_ID", strStaffId);
        inparams.put("DEPART_ID", strDepartId);
        inparams.put("USER_ID", strUserId);
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "UPD_TIME_BY_PK", inparams);
    }

    /**
     * 新增用户禁查信息
     * 
     * @param pd
     * @param param
     * @throws Exception
     */
    public void InsertForbidenInfo(IData param) throws Exception
    {

        IData data = new DataMap();
        String strUserId = param.getString("USER_ID");
        String strSerial_Number = param.getString("SERIAL_NUMBER");
        String strDepartId = param.getString("DEPART_ID");
        String strStaffId = param.getString("STAFF_ID");
        String strStartDate = param.getString("START_DATE");
        if (StringUtils.isBlank(strStartDate))
        {
            strStartDate = SysDateMgr.getSysTime();
        }

        String instId = SeqMgr.getInstId();
        Long pValue = Long.parseLong(strUserId) % 10000;
        data.put("PARTITION_ID", pValue.toString());
        data.put("INST_ID", instId);
        data.put("USER_ID", strUserId);
        data.put("SERVICE_MODE", "2");
        data.put("SERIAL_NUMBER", strSerial_Number);
        data.put("PROCESS_INFO", "用户禁止查询打印清单");
        data.put("RSRV_NUM1", "0");
        data.put("RSRV_NUM2", "0");
        data.put("RSRV_NUM3", "0");
        data.put("RSRV_STR1", "");
        data.put("RSRV_STR2", "");
        data.put("RSRV_STR3", "");
        data.put("RSRV_STR4", "");
        data.put("RSRV_STR5", "");
        data.put("RSRV_STR6", "");
        data.put("RSRV_STR7", "");
        data.put("RSRV_STR8", "");
        data.put("RSRV_STR9", "");
        data.put("RSRV_STR10", "");
        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("PROCESS_TAG", "0");
        data.put("STAFF_ID", strStaffId);
        data.put("DEPART_ID", strDepartId);
        data.put("START_DATE", strStartDate);
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        data.put("REMARK", "");
        Dao.insert("TF_F_USER_OTHERSERV", data);
    }

    public IDataset queryForbidenInfo(IData data) throws Exception
    {
        IData temp = new DataMap();
        String userId = data.getString("USER_ID");
        IDataset result = UserOtherInfoQry.getUserOtherservByPK(userId, "2", "0", null);

        if (IDataUtil.isEmpty(result))
        {
            temp.put("COUNT", "0");
        }
        else
        {
            temp.put("COUNT", "1");
        }

        return new DatasetList(temp);
    }
}
