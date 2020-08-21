
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.WhiteUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class ManageWhiteBean extends CSBizBean
{

    private void addWhiteUser(IData data) throws Exception
    {
        IData inParam = new DataMap();
        String sysDate = SysDateMgr.getSysDate();
        inParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        inParam.put("IN_TIME", sysDate);
        inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        inParam.put("USER_ID", data.getString("USER_ID"));
        inParam.put("USER_ATTR", "02");
        inParam.put("USER_LEVEL", "02");
        inParam.put("PROVINCE_CODE", "898");
        inParam.put("EFFECT_TAG", "1");
        inParam.put("BEGIN_TIME", "");
        inParam.put("END_TIME", "");
        inParam.put("EXEC_TIME", sysDate);
        inParam.put("OPERATE_FLAG", "0");
        inParam.put("PROCESS_TAG", "0");
        inParam.put("UPDATE_TIME", sysDate);
        inParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inParam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        inParam.put("REMARK", data.getString("REMARK", ""));

        Dao.insert("TL_F_WHITEUSER", inParam);
    }

    private void backupBlackUser(IData data) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        inParam.put("OPERATE_FLAG", "2"); // 删除黑名单
        inParam.put("PROCESS_TAG", "0"); // 设置没处理
        inParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inParam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        inParam.put("REMARK", "前台增加免拦截时删除黑名单");
        Dao.executeUpdateByCodeCode("TL_F_WHITEUSER", "INS_BLACK_BACKUP", inParam);
    }

    public IDataset delWhiteUser(IData data) throws Exception
    {
        String[] serialNumbers = data.getString("SERIAL_NUMBER_LIST").split(",");
        IDataset dataset = new DatasetList();
        for (int i = 0; i < serialNumbers.length; i++)
        {
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serialNumbers[i]);
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            dataset.add(param);
        }

        Dao.executeBatchByCodeCode("TL_F_WHITEUSER", "INS_WHITE_BACKUP", dataset);// 备份数据到免拦截号码历史表
        Dao.executeBatchByCodeCode("TL_F_WHITEUSER", "DEL_WHITE_USER", dataset);// 白名单数据删除

        return null;
    }

    public IDataset loadChildInfo(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String serialNumber = data.getString("SERIAL_NUMBER");
        String stateCode = data.getString("USER_STATE_CODESET", "");

        IDataset result = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_433, serialNumber);
        }
        else
        {
            if (!stateCode.equals(result.getData(0).getString("STATE_CODE")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_433, serialNumber);
            }
        }

        result = WhiteUserInfoQry.queryWhiteInfoBySerialNumber(serialNumber);
        if (IDataUtil.isNotEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经在免拦截号码里,不能再办理该业务");
        }
        return result;
    }

    public IDataset onTradeSubmit(IData data) throws Exception
    {
        addWhiteUser(data);

        IDataset result = queryBlackUserExit(data);
        // 加入白名单后判断是否在黑名单中存在，如果在黑名单中存在则从黑名单中删除
        if (IDataUtil.isNotEmpty(result))
        {
            backupBlackUser(data);// 备份数据到免拦截号码历史表
            removeBlackUser(data);// 删除黑名单表内的数据
        }
        return null;
    }

    private IDataset queryBlackUserExit(IData data) throws Exception
    {
        return WhiteUserInfoQry.queryBlackInfoBySerialNumber(data.getString("SERIAL_NUMBER"));
    }

    private void removeBlackUser(IData data) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        Dao.executeUpdateByCodeCode("TL_F_WHITEUSER", "DEL_BLACK_USER", inParam);
    }
}
