
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.specialtrademgr.ProtectPassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ProtectPassInfoBean extends CSBizBean
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

    // ---------------------------------------------
    /**
     * 服务密码查询 (老系统接口接口名: ITF_CRM_QueryProtectPass) yangsh6
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData queryProtectPass(IData data) throws Exception
    {

        IData inparam = new DataMap();
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        // 查询用户是否存在，并且或者USER_ID
        IData user = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER", ""));
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到您输入手机号码对应的用户!");
        }
        String userId = user.getString("USER_ID", "");
        inparam.put("USER_ID", userId);
        IData retProtectParam = this.queryProtectPassInfo(inparam);
        IData otherParam = new DataMap();
        // 查询用户是否实名制
        inparam.clear();
        inparam.put("REMOVE_TAG", "0");
        inparam.put("NET_TYPE_CODE", "00");
        inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        IDataset userInfos = ProtectPassInfoQry.queryCustId(inparam);
        IData nameParm = new DataMap();
        if (IDataUtil.isNotEmpty(userInfos))
        {
            nameParm.put("CUST_ID", userInfos.getData(0).getString("CUST_ID"));
            nameParm.put("PARTITION_ID", userInfos.getData(0).getString("PARTITION_ID"));
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取客户信息无数据!");
        }

        IData isRealNameParam = UcaInfoQry.qryCustomerInfoByCustId(nameParm.getString("CUST_ID"));
        boolean resultName = false;
        if ("1".equals(isRealNameParam.getString("IS_REAL_NAME")))
        {
            resultName = true;
        }
        // 查询用户联系邮箱
        IData regEmailParam = UcaInfoQry.qryPerInfoByCustId(nameParm.getString("CUST_ID"));

        if (IDataUtil.isNotEmpty(retProtectParam.getData("PROTECT_INFO")))
        {
            inparam.clear();

            inparam = retProtectParam.getData("PROTECT_PASS"); // 密码保护属性
            otherParam.put("QUESTION_FIRST", inparam.getString("RSRV_STR11"));
            otherParam.put("QUESTION_SECOND", inparam.getString("RSRV_STR13"));
            otherParam.put("QUESTION_THIRD", inparam.getString("RSRV_STR15"));
            otherParam.put("CHECK_EMAIL", inparam.getString("RSRV_STR17")); // 验证邮箱
            otherParam.put("CONTACT_EMAIL", regEmailParam.getString("EMAIL")); // 联系邮箱
            otherParam.put("CHECK_IS_REAL_NAME", resultName);
            otherParam.put("CHECK_IS_OPEN", "1");
            otherParam.put("X_RESULTINFO", "已设置服务密码保护");

            if (!"".equals(inparam.getString("CUSTOM_QUESTION_FIRST", "")))
            {
                otherParam.put("CUSTOM_QUESTION_FIRST", inparam.getString("CUSTOM_QUESTION_FIRST"));
            }
            if (!"".equals(inparam.getString("CUSTOM_QUESTION_SECOND", "")))
            {
                otherParam.put("CUSTOM_QUESTION_SECOND", inparam.getString("CUSTOM_QUESTION_SECOND"));
            }
            if (!"".equals(inparam.getString("CUSTOM_QUESTION_THIRD", "")))
            {
                otherParam.put("CUSTOM_QUESTION_THIRD", inparam.getString("CUSTOM_QUESTION_THIRD"));
            }
        }
        else
        {
            otherParam.put("X_RESULTCODE", "5");
            otherParam.put("CHECK_IS_REAL_NAME", resultName);
            otherParam.put("CONTACT_EMAIL", regEmailParam.getString("EMAIL")); // 联系邮箱
            otherParam.put("CHECK_IS_OPEN", "0");
            otherParam.put("X_RESULTINFO", "未设置服务密码保护");
            otherParam.put("X_RSPTYPE", "2");
            otherParam.put("X_RSPCODE", "2998");
        }
        otherParam.put("X_RECORDNUM", retProtectParam.getData("PROTECT_INFO").size());
        return otherParam;
    }

    /**
     * 将查询出来的数据转换后设置到页面上,将查询的值转换后设置到td中 flag标记，默认false为不存在设置了密码，true为已经设置了密码保护
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public IData queryProtectPassInfo(IData data) throws Exception
    {

        IData returnData = new DataMap(); // 总的结果
        // 更新逻辑.td无用 , 那改方法中多个map
        IData param = new DataMap();
        IData inparam = new DataMap();
        IData userSvc = new DataMap();

        String flag = "false";

        IDataset userSvcData = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(data.getString("USER_ID"), "3312");

        if (userSvcData != null && userSvcData.size() > 0)
        {
            userSvc = userSvcData.getData(0);
            IDataset dataset = UserOtherInfoQry.getUserOtherInfoByAll(data.getString("USER_ID"), "SPWP");
            if (!dataset.isEmpty() && dataset.size() > 0)
            {
                flag = "true";
                param.clear();
                param = dataset.getData(0);
                // 自定义问题
                if ("z".equals(param.getString("RSRV_STR6")))
                {
                    param.put("CUSTOM_QUESTION_FIRST", param.getString("RSRV_STR11"));
                }
                if ("z".equals(param.getString("RSRV_STR7")))
                {
                    param.put("CUSTOM_QUESTION_SECOND", param.getString("RSRV_STR13"));
                }
                if ("z".equals(param.getString("RSRV_STR8")))
                {
                    param.put("CUSTOM_QUESTION_THIRD", param.getString("RSRV_STR15"));
                }
                // 问题字段
                param.put("QUESTION_FIRST", param.getString("RSRV_STR6"));
                param.put("QUESTION_SECOND", param.getString("RSRV_STR7"));
                param.put("QUESTION_THIRD", param.getString("RSRV_STR8"));
                param.put("EMAIL", param.getString("RSRV_STR17"));
                param.put("START_TIME", param.getString("RSRV_DATE10").substring(0, 19));
                param.put("UPDATE_TIME", param.getString("UPDATE_TIME").substring(0, 19));
                inparam.putAll(param);
            }
        }

        inparam.put("flag", flag);
        userSvc.put("flag", flag);

        returnData.put("PROTECT_INFO", param);
        returnData.put("PROTECT_PASS", inparam);// 密码保护属性
        returnData.put("PROTECT_USER_SVC", userSvc); // 服务属性

        return returnData;
    }
}
