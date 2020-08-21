
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.payment.UserPayMentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QuerySnPaymentInfoBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(QuerySnPaymentInfoBean.class);

    public IDataset querySnPaymentInfo(IData param, Pagination pagination) throws Exception
    {
        IDataset retDs = new DatasetList();
        String msisdn = param.getString("SERIAL_NUMBER", "");
        IDataset userResult = UserInfoQry.getUserInfoForPayMent(msisdn);
        if (IDataUtil.isEmpty(userResult))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1143);
        }
        param.put("MAKE_STATE", "0");
        param.put("USER_ID", userResult.getData(0).getString("USER_ID", ""));
        // 查询用户是否定制或定制过手机缴费通
        IDataset paymentDs = UserPayMentInfoQry.queryUserHavePayMent(param, pagination);
        if (IDataUtil.isNotEmpty(paymentDs))
        {
            // 查询用户使用手机缴费通缴费情况
            IDataset paymentLogDs = querySnPaymentPayLogInfo(param, pagination);
            // 三个月之内有使用手机缴费通缴费
            boolean existFlag = false;
            if (IDataUtil.isNotEmpty(paymentLogDs))
            {
                for (int i = 0; i < paymentLogDs.size(); i++)
                {
                    IData data = paymentLogDs.getData(i);
                    String payFeeModeCode = data.getString("PAY_FEE_MODE_CODE", "");
                    String paymentId = data.getString("PAYMENT_ID", "");
                    String recvStaffId = data.getString("RECV_STAFF_ID", "");
                    if ("10".equals(payFeeModeCode) && "3".equals(paymentId) && "ITFOULIK".equals(recvStaffId))
                    {
                        existFlag = true;
                        break;
                    }
                }
            }
            // 三个月之内使用缴费通缴费则为活跃客户
            if (existFlag)
            {
                IData dm = new DataMap();
                dm.put("SERIAL_NUMBER", msisdn);
                dm.put("STATE_CODE", "2");
                dm.put("STATE_NAME", "活跃客户");
                retDs.add(dm);
            }
            else
            {
                IData dm = new DataMap();
                dm.put("SERIAL_NUMBER", msisdn);
                dm.put("STATE_CODE", "3");
                dm.put("STATE_NAME", "沉默客户");
                retDs.add(dm);
            }
        }
        else
        {
            IData dm = new DataMap();
            dm.put("SERIAL_NUMBER", msisdn);
            dm.put("STATE_CODE", "1");
            dm.put("STATE_NAME", "未定制客户");
            retDs.add(dm);
        }
        return retDs;
    }

    // 查询用户使用手机缴费通缴费情况
    public IDataset querySnPaymentPayLogInfo(IData param, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        // params.put("X_PAY_USER_ID", param.getString("USER_ID", ""));
        // params.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
        // params.put("NET_TYPE_CODE", "00");
        // params.put("REMOVE_TAG", "0");
        // params.put("CANCEL_TAG", "0");
        // params.put("BEGIN_TIME", SysDateMgr.getAddMonthsNowday(-3, SysDateMgr.getSysTime()));
        // params.put("END_TIME", SysDateMgr.getSysTime());
        IDataset idset = AcctCall.querySnPaymentPayLogInfo(param.getString("SERIAL_NUMBER", ""));
        return idset;
    }

}
