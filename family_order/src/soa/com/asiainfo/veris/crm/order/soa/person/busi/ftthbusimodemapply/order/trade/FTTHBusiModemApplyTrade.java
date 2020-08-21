
package com.asiainfo.veris.crm.order.soa.person.busi.ftthbusimodemapply.order.trade;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthbusimodemapply.FTTHBusiModemApplyBean;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthbusimodemapply.order.requestdata.FTTHBusiModemApplyReqData;

/**
 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
 * @author chenxy3 2015-12-7
 */
public class FTTHBusiModemApplyTrade extends BaseTrade implements ITrade
{
	 
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
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
    	FTTHBusiModemApplyReqData rd = (FTTHBusiModemApplyReqData) btd.getRD();
    	IDataset members=rd.getMemberList();
    	if(members!=null && members.size()>0){
    		for(int k=0;k<members.size();k++){
    			String kdNumber=members.getData(k).getString("KD_NUMBER","");
    			String kdUserId=members.getData(k).getString("KD_USERID","");
    			String kdTradeId=members.getData(k).getString("KD_TRADE_ID","");
    			OtherTradeData otherTradeData = new OtherTradeData();
    	        otherTradeData.setRsrvValueCode("FTTH_GROUP");
    	        otherTradeData.setRsrvValue("商务宽带光猫申领");
    	        otherTradeData.setUserId(kdUserId);
    	        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
    	        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
    	        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
    	        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
    	        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);  
    	        otherTradeData.setRsrvStr2("0");//押金,预留,与FTTH一致
    	        otherTradeData.setRsrvStr3(kdNumber);//宽带号码
    	        otherTradeData.setRsrvStr4(btd.getRD().getUca().getSerialNumber());//主号号码
    	        otherTradeData.setRsrvStr5(kdTradeId);//开户的TRADE_ID
    	        btd.add(kdNumber, otherTradeData);
    		}
    	} 
    }
}
