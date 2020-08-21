
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifycustinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

public class CttModifyCustInfoSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(CttModifyCustInfoSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * 查询改名费
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset ajaxModiCustName(IData data) throws Exception
    {
        return ProductFeeInfoQry.getElementFee(data.getString("TRADE_TYPE_CODE"), null, null, "P", "-1", "-1", "-1", "-1", getVisit().getStaffEparchyCode(), "3");
    }

    /**
     * 记录特殊权限工号查询实名制用户资料
     * 
     * @param td
     * @return
     * @throws Exception
     */
    public IData writeLanuchLog(IData params) throws Exception
    {

        String eparchy_code = CSBizBean.getTradeEparchyCode();
        String systime = SysDateMgr.getSysTime();
        String trade_id = SeqMgr.getTradeId();
        IData inparam = new DataMap();
        String trade_type_code = "2101";

        inparam.put("TRADE_ID", trade_id);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", SeqMgr.getOrderId());
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", trade_type_code);// 业务类型编码：见参数表TD_S_TRADETYPE
        inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        inparam.put("SUBSCRIBE_STATE", "0");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("CUST_ID", params.getString("CUST_ID"));
        inparam.put("CUST_NAME", params.getString("CUST_NAME"));
        inparam.put("USER_ID", params.getString("USER_ID"));
        inparam.put("ACCT_ID", params.getString("ACCT_ID"));
        inparam.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", params.getString("NET_TYPE_CODE", "00"));
        inparam.put("EPARCHY_CODE", eparchy_code);
        inparam.put("CITY_CODE", "");
        inparam.put("PRODUCT_ID", "");
        inparam.put("BRAND_CODE", params.getString("BRAND_CODE", ""));
        inparam.put("ACCEPT_DATE", systime);
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", eparchy_code);
        inparam.put("OPER_FEE", "0");
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "                    ");
        inparam.put("OLCOM_TAG", "0");
        inparam.put("FEE_STATE", "0");
        inparam.put("FINISH_DATE", systime);
        inparam.put("EXEC_TIME", systime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("REMARK", "记录特殊权限工号查询实名制用户资料！");
        Dao.insert("TF_BH_TRADE", inparam);
        return inparam;
    }
}
