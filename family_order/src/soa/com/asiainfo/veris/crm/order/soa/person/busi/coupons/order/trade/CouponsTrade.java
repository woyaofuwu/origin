
package com.asiainfo.veris.crm.order.soa.person.busi.coupons.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.coupons.order.requestdata.CouponsReqData; 

/**
 * REQ201505210004 FTTH光猫申领
 * 
 * @author chenxy3 2015-6-1
 */
public class CouponsTrade extends BaseTrade implements ITrade
{
	 
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	// 成功！ 处理other表
        createOtherTradeInfo(btd);  
    } 

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd) throws Exception
    {
    	CouponsReqData rd = (CouponsReqData) btd.getRD();
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("COUPONS");
        otherTradeData.setRsrvValue("手机维修优惠券使用");
        otherTradeData.setUserId(rd.getUca().getUser().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark(rd.getRemark());  
        otherTradeData.setRsrvStr1(serialNumber);//优惠券手机号
        otherTradeData.setRsrvStr2(rd.getTicketCode());//优惠券编码
        otherTradeData.setRsrvStr3(rd.getTicketValue());//优惠券价值
        otherTradeData.setRsrvStr4(rd.getSpendValue());//优惠券实际使用价值
        otherTradeData.setRsrvStr5(rd.getRepairNO());//维修单号
        btd.add(serialNumber, otherTradeData); 
        
        //并利用传递的参数，更新 TL_B_COUPONS_QUOTA表的限额
        IData param=new DataMap(); 
        param.put("TICKET_VALUE", rd.getSpendValue());
        param.put("TICKET_CODE", rd.getTicketCode());
        CSAppCall.call("SS.CouponsTradeSVC.updCouponsQuota", param);
        
        /**更新TL_B_USER_COUPONS表*/
        IData input=new DataMap(); 
        input.put("SPEND_VALUE", rd.getSpendValue());
        input.put("SPEND_STAFF_ID", CSBizBean.getVisit().getStaffId());
        input.put("SPEND_DEPART_ID", CSBizBean.getVisit().getDepartId());
        input.put("TICKET_CODE", rd.getTicketCode());
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("REPAIR_NO", rd.getRepairNO());
        input.put("REMARK", rd.getRemark());
        CSAppCall.call("SS.CouponsTradeSVC.updUserCouponsInfo", input);
    }
}
