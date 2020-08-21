
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.UserPasswordInfoComm;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.UnLockUserPwdReqData;

/**
 * 用户密码解锁写台账
 * 
 * @author liutt
 */
public class UnLockUserPwdTrade extends BaseTrade implements ITrade
{

    /**
     * 验证密码解锁参数的正确性
     * 
     * @param btd
     * @throws Exception
     */
    private void checkParams(BusiTradeData btd) throws Exception
    {
        UnLockUserPwdReqData reqData = (UnLockUserPwdReqData) btd.getRD();
        String unLockType = reqData.getUnLockType();
        if (StringUtils.equals("1", unLockType))
        {// 短信随机码解锁
            checkSendMsg(btd);
        }
        else if (StringUtils.equals("2", unLockType))
        {// 证件解锁
            // String psptTypeCode = reqData.getPsptTypeCode();
            String psptId = reqData.getPsptId();
            // if(StringUtils.isBlank(psptTypeCode)){
            // CSAppException.apperr(CustException.CRM_CUST_103);//证件类型编码为空!
            // }
            if (StringUtils.isBlank(psptId))
            {
                CSAppException.apperr(CustException.CRM_CUST_112);// 证件号码不能为空！
            }
            String oldPsptId = reqData.getUca().getCustomer().getPsptId();
            if (!StringUtils.equals(psptId, oldPsptId))
            {
                CSAppException.apperr(CustException.CRM_CUST_116);// 证件号码不正确！
            }
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1100);// 解锁类型不存在！
        }
    }

    /**
     * 验证短信随机码是否正确
     * 
     * @param btd
     * @throws Exception
     */
    private void checkSendMsg(BusiTradeData btd) throws Exception
    {
        UnLockUserPwdReqData reqData = (UnLockUserPwdReqData) btd.getRD();
        String msg = reqData.getMessage();
        String userId = reqData.getUca().getUserId();
        // 获取用户一段时间内是否发送过短信随机码
        IDataset userOtherSet = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "UNLOCKMSG");
        boolean isEqual = false;
        if (IDataUtil.isNotEmpty(userOtherSet))
        {
            for (int i = 1, size = userOtherSet.size(); i < size; i++)
            {
                if (StringUtils.equals(msg, userOtherSet.getData(i).getString("RSRV_VALUE")))
                {
                    isEqual = true;
                    break;
                }
            }
        }
        if (isEqual)
        {// 删除已使用的短信随机码
            IData param = new DataMap();
            param.put("USER_ID", userId);
            Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "DEL_UNLOCK_MSG", param);
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1101);// 短信随机码不正确，无法解锁！
        }
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        checkParams(btd);
        String userId = btd.getRD().getUca().getUserId();
        // 查询密码是否已锁定
        IDataset userOtherSet = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PWDLOCK");
        if (IDataUtil.isEmpty(userOtherSet))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1058);// 该用户密码未锁定，不需要解锁！
        }
        createTradeMainInfo(btd);
        unLockUserPwd(btd);

    }

    /**
     * 功能说明：设置主台帐数据
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void createTradeMainInfo(BusiTradeData btd) throws Exception
    {
        UnLockUserPwdReqData reqData = (UnLockUserPwdReqData) btd.getRD();
        // 通过营业厅解锁界面解锁才有以下参数值
        btd.getMainTradeData().setRsrvStr1(reqData.getUnLockType());// 解锁方式
        btd.getMainTradeData().setRsrvStr2(reqData.getMessage());// 解锁随机码
        // btd.getMainTradeData().setRsrvStr3(reqData.getPsptTypeCode());// 证件类型
        btd.getMainTradeData().setRsrvStr4(reqData.getPsptId());// 证件号码
        this.unLockUserPwd(btd);

    }

    /**
     * 修改tf_f_user_other.end_date实现密码解锁
     * 
     * @param btd
     * @throws Exception
     */
    private void unLockUserPwd(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_ID", btd.getTradeId());
        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("REMARK", "密码解锁");
        // 如果已经锁定则结束锁定时间进行解锁
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_UNLOCK_USERPWD", param);
        // 密码输入错误次数清零
        UserPasswordInfoComm.delPwdErrInfo(userId);

    }

}
