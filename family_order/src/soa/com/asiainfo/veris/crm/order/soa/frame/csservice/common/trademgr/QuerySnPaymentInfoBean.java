
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trademgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.PayMentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QuerySnPaymentInfoBean extends CSBizBean
{

    /**
     * 查询手机缴费通使用情况
     * 
     * @param pd
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author:chenzg
     * @date:2010-9-9
     */
    public IDataset querySnUsePaymentInfo(IData inparams) throws Exception
    {
        IDataset retDs = new DatasetList();
        IData userInfo = UserInfoQry.getUserInfoBySn2(inparams);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            inparams.put("USER_ID", userInfo.getString("USER_ID", ""));
            IDataset paymentDs = PayMentInfoQry.querySnPaymentInfo(inparams);
            // 用户有定制手机缴费通
            if (paymentDs != null && paymentDs.size() > 0)
            {
                IDataset paymentLogDs = AcctCall.querySnPaymentPayLogInfo(userInfo.getString("SERIAL_NUMBER", "")); // 调用账务接口
                // 三个月之内有使用手机缴费通缴费
                boolean existFlag = false;
                if (paymentLogDs != null && paymentLogDs.size() > 0)
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
                    dm.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER", ""));
                    dm.put("STATE_CODE", "2");
                    dm.put("STATE_NAME", "活跃客户");
                    retDs.add(dm);
                }
                else
                {
                    IData dm = new DataMap();
                    dm.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER", ""));
                    dm.put("STATE_CODE", "3");
                    dm.put("STATE_NAME", "沉默客户");
                    retDs.add(dm);
                }
            }
            else
            {
                IData dm = new DataMap();
                dm.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER", ""));
                dm.put("STATE_CODE", "1");
                dm.put("STATE_NAME", "未定制客户");
                retDs.add(dm);
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您传入的手机号码的,用户资料未找到!");
        }
        return retDs;
    }
}
