
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

/**
 * 台账登记接口
 * 
 * @author liutt
 */
public class InsertTradeSVC extends CSBizService
{
    /**
     * 根据需求，需要CRM做的改造是提供台账登记接口给外围接口及电子渠道，该接口直接生成台账记录并查到台账历史表：tf_bh_trade。
     * 接口输入参数暂定如下：SERIAL_NUMBER(用户号码)、TRADE_TYPE_CODE（工单类型）、DEVELOP_STAFF_ID（推荐工号）、EXEC_DESC(执行说明)。
     * 其他接口的通用参数（例如TRADE_STAFF_ID等）保持不变。 DEVELOP_STAFF_ID记到台账表的保留字段RSRV_STR10中、EXEC_DESC记到字段EXEC_DESC。
     * 另外需要为“开通手机缴费通”分配一个trade_type_code。
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData insertTrade(IData data) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "TRADE_TYPE_CODE");
        IDataUtil.chkParam(data, "DEVELOP_STAFF_ID");
        IDataUtil.chkParam(data, "EXEC_DESC");
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        String tradeId = recordHisMainTrade(data, ucaData);
        IData result = new DataMap();
        result.put("TRADE_ID", tradeId);
        return result;
    }

    /**
     * 记录历史主台账
     * 
     * @param data
     * @return
     * @throws Exception
     */
    private String recordHisMainTrade(IData data, UcaData ucaData) throws Exception
    {
        String tradeId = SeqMgr.getTradeId();
        String orderId = SeqMgr.getOrderId();
        String sysdate = SysDateMgr.getSysTime();
        UserTradeData userInfo = ucaData.getUser();
        IData iparam = new DataMap();
        iparam.put("TRADE_ID", tradeId);
        iparam.put("BATCH_ID", "");
        iparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        iparam.put("ORDER_ID", orderId);
        iparam.put("PROD_ORDER_ID", "");
        iparam.put("BPM_ID", "");
        iparam.put("CAMPN_ID", "");
        iparam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        iparam.put("PRIORITY", "0");
        iparam.put("SUBSCRIBE_TYPE", "0");
        iparam.put("SUBSCRIBE_STATE", "9");
        iparam.put("NEXT_DEAL_TAG", "0");
        iparam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", getVisit().getInModeCode()));
        iparam.put("USER_ID", ucaData.getUserId());
        iparam.put("CUST_NAME", ucaData.getCustomer().getCustName());
        iparam.put("ACCT_ID", ucaData.getAcctId());
        iparam.put("CUST_ID", ucaData.getCustId());
        iparam.put("SERIAL_NUMBER", ucaData.getSerialNumber());
        iparam.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", ucaData.getUser().getNetTypeCode()));
        iparam.put("EPARCHY_CODE", userInfo.getEparchyCode());
        iparam.put("CITY_CODE", userInfo.getCityCode());
        iparam.put("PRODUCT_ID", ucaData.getProductId());
        iparam.put("BRAND_CODE", ucaData.getBrandCode());
        iparam.put("CUST_ID_B", "");
        iparam.put("ACCT_ID_B", "");
        iparam.put("USER_ID_B", "");
        iparam.put("SERIAL_NUMBER_B", "");
        iparam.put("CUST_CONTACT_ID", "");
        iparam.put("SERV_REQ_ID", "");
        iparam.put("INTF_ID", "");
        iparam.put("ACCEPT_DATE", sysdate);
        iparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        iparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        iparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        iparam.put("TRADE_EPARCHY_CODE", this.getTradeEparchyCode());
        iparam.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        iparam.put("OPER_FEE", "0");
        iparam.put("FOREGIFT", "0");
        iparam.put("ADVANCE_PAY", "0");
        iparam.put("INVOICE_NO", "");
        iparam.put("FEE_STATE", "0");
        iparam.put("FEE_TIME", "");
        iparam.put("FEE_STAFF_ID", "");
        iparam.put("PROCESS_TAG_SET", "0");
        iparam.put("OLCOM_TAG", "0");// 默认不发
        iparam.put("FINISH_DATE", sysdate);
        iparam.put("EXEC_TIME", sysdate);
        iparam.put("EXEC_ACTION", "0");
        iparam.put("EXEC_RESULT", "");
        iparam.put("EXEC_DESC", data.getString("EXEC_DESC", ""));
        iparam.put("CANCEL_TAG", "0");
        iparam.put("CANCEL_DATE", "");
        iparam.put("CANCEL_STAFF_ID", "");
        iparam.put("CANCEL_DEPART_ID", "");
        iparam.put("CANCEL_CITY_CODE", "");
        iparam.put("CANCEL_EPARCHY_CODE", "");
        iparam.put("UPDATE_TIME", sysdate);
        iparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        iparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        iparam.put("REMARK", "开通手机缴费通，直接插历史台账");// 备注
        iparam.put("RSRV_STR1", "");
        iparam.put("RSRV_STR2", "");
        iparam.put("RSRV_STR3", "");
        iparam.put("RSRV_STR4", "");
        iparam.put("RSRV_STR5", "");
        iparam.put("RSRV_STR6", "");
        iparam.put("RSRV_STR7", "");
        iparam.put("RSRV_STR8", "");
        iparam.put("RSRV_STR9", "");
        iparam.put("RSRV_STR10", data.getString("DEVELOP_STAFF_ID", ""));
        iparam.put("PF_WAIT", "0");// 是否发开通
        Dao.insert("TF_BH_TRADE", iparam,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return tradeId;
    }

}
