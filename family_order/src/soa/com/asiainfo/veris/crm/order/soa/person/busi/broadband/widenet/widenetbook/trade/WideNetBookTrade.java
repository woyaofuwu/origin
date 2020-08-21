
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
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
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook.requsetdata.WideNetBookReqData;

/**
 * REQ201607290010 关于在“我比九环多一环”活动中增加“办宽带得话费”环节
 * <br/>
 * 宽带预约登记
 * @author zyz
 * 
 */
public class WideNetBookTrade extends BaseTrade implements ITrade
{
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	WideNetBookReqData rd = (WideNetBookReqData) btd.getRD();
    	//验证码
    	String  validCode=rd.getValidCode();
    	IData params=new DataMap();
    	params.put("validCode", validCode);
    	IData iData=CSAppCall.call("SS.WideNetBookSVC.checkValidCodeIsExist", params).first();
    	String stauts=iData.getString("stauts");
    	//验证码是否存在
    	if("1".equals(stauts)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_1173);
    	}
    	
    	IData data=CSAppCall.call("SS.WideNetBookSVC.checkValidCodeNum", params).first();
    	String stauts2=data.getString("stauts");
    	//验证码使用人数
    	if("1".equals(stauts2)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_1174);
    	}
    	//
	    createOtherTradeInfo(btd,rd); 
	    
    } 

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd,WideNetBookReqData rd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("WIDENET_BOOK");// 材料编码
        otherTradeData.setRsrvValue("宽带营销预登记");
        otherTradeData.setUserId(rd.getUca().getUser().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark(rd.getRemark()); //备注
        otherTradeData.setRsrvStr1(rd.getValidCode());//验证码
       
        btd.add(serialNumber, otherTradeData);
        
    }
}
