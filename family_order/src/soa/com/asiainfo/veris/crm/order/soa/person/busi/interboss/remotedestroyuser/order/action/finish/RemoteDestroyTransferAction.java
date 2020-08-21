package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser.order.action.finish;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser.RemoteDestroyUserDao;

public class RemoteDestroyTransferAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        IData queryParam = new DataMap();
        queryParam.put("SERIAL_NUMBER", serialNumber);
        queryParam.put("DEAL_TAG", "1");
        //校验是否有异地销户工单
        IDataset cancelOrder = RemoteDestroyUserDao.queryReceiveDestroyUserTrade(queryParam);
        if(DataSetUtils.isNotBlank(cancelOrder)){
            IData orderData = cancelOrder.first();
            String orderId = orderData.getString("ORDER_ID");//发起省异地销户工单号
            String cashTransferNumber = orderData.getString("GIFT_SERIAL_NUMBER");
            String noCashTransferNumber = orderData.getString("GIFT_SERIAL_NUMBER_B");
            IData input = new DataMap();
            input.put("SERIAL_NUM_OUT", serialNumber);//转出号码
            input.put("OUTER_TRADE_ID", orderId);//业务唯一流水号，用来异步反馈结果或者查询日志记录等
            input.put("SERIAL_NUM_IN_MONEY", cashTransferNumber);//现金转入手机号
            input.put("SERIAL_NUM_IN_PAY", noCashTransferNumber);//非现金转入手机号
            input.put("SERIAL_NUM_TRADE", orderData.getString("CONTACT_PHONE"));//联系人号码
            input.put("CHANNEL_ID", "01");
            IDataset result = AcctCall.applyRemoveSn(input);
            //String resultCode = result.first().getString("RESULT_CODE");
            
            
            
            updateAccountTag(orderId, "1");
            /*IData params = new DataMap();
            params.put("CONTACT_PHONE", orderData.getString("CONTACT_PHONE"));//跨区销户联系号码
            params.put("SERIAL_NUMBER", orderData.getString("SERIAL_NUMBER"));//跨区销户号码
            params.put("GIFT_SERIAL_NUMBER", orderData.getString("GIFT_SERIAL_NUMBER"));//跨区销户转账用户号码（现金）
            params.put("GIFT_SERIAL_NUMBER_B", orderData.getString("GIFT_SERIAL_NUMBER_B"));//跨区销户转账用户号码（非现金）
            params.put("CASH_AMOUNT", orderData.getString("CASH_AMOUNT"));//转账现金金额
            params.put("NOCASH_AMOUNT", orderData.getString("NOCASH_AMOUNT"));//转账非现金金额
            sendSMS(params);*/
         /*   if ("0000".equals(resultCode)) {
            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_301, "跨区销户用户转费失败！");
            }*/
        }
    }

    /**
     * 更新接单表
     *
     * @return
     * @throws Exception
     */
    private static void updateAccountTag(String orderid, String accountTag) throws Exception {
        IData input = new DataMap();
        input.put("ORDER_ID", orderid);
        //input.put("ACCOUNT_TAG", accountTag);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("update ");
        sql.append("TF_B_RECEIPT_ORDER  m ");
        //sql.append("set  m.ACCOUNT_TAG= :ACCOUNT_TAG, ");
        sql.append("set m.DEAL_TAG = '9', ");//销户成功
        sql.append("m.DEAL_INFO = '已销户', ");
        sql.append("m.RSRV_STR1 = '已销户', ");
        sql.append("m.UPDATE_TIME = SYSDATE ");
        sql.append("WHERE m.ORDER_ID = :ORDER_ID ");
        Dao.executeUpdate(sql, input, Route.CONN_CRM_CEN);
    }

    private void sendSMS(IData input) throws Exception {
        String sum = Integer.toString(Integer.parseInt(input.getString("CASH_AMOUNT")) + Integer.parseInt(input.getString("NOCASH_AMOUNT")));//剩余总话费
        if (IDataUtil.isNotEmpty(RouteInfoQry.getMofficeInfoBySn(input.getString("CONTACT_PHONE")))) {
            String content = "尊敬的客户，您好！您申请的" + input.getString("SERIAL_NUMBER") + "号码已销户，销户后剩余话费" + sum + "元，" +
                    "向您登记的号码" + input.getString("GIFT_SERIAL_NUMBER") + "转账" + input.getString("CASH_AMOUNT") + "元，" +
                    "向您登记的" + input.getString("GIFT_SERIAL_NUMBER_B") + "号码转账" + input.getString("NOCASH_AMOUNT") + "元，已于今天到账，请您知悉。感谢您的支持！【中国移动】";
            IData sendInfo = new DataMap();
            sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(input.getString("CONTACT_PHONE")));
            sendInfo.put("RECV_OBJECT", input.getString("CONTACT_PHONE"));
            sendInfo.put("RECV_ID", input.getString("CONTACT_PHONE"));
            sendInfo.put("SMS_PRIORITY", "50");
            sendInfo.put("NOTICE_CONTENT", content);
            sendInfo.put("REMARK", "跨区销户");
            sendInfo.put("FORCE_OBJECT", "10086");
            SmsSend.insSms(sendInfo, RouteInfoQry.getEparchyCodeBySn(input.getString("CONTACT_PHONE")));
        }
    }
    
}
