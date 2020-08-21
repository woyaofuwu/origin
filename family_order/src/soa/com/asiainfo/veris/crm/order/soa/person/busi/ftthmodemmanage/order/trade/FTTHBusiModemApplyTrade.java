
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.FTTHBusiModemApplyReqData;

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
    			String modem_id = members.getData(k).getString("KD_MODEM_ID","");
    			String modem_type = members.getData(k).getString("KD_MODEM_TYPE","");
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
    	        otherTradeData.setRsrvStr7("0");//押金状态，预留,与FTTH一致:0,押金、1,已转移、2已退还、3,已沉淀
    	        otherTradeData.setRsrvStr8("");//BOSS押金转移流水
    	        otherTradeData.setRsrvTag1("0");//申领模式  0租赁
    	        otherTradeData.setRsrvTag2("1");//光猫状态：1:申领，2:更改，3:退还，4:丢失
    	        otherTradeData.setRsrvStr11(btd.getTradeTypeCode());//业务类型
    	        otherTradeData.setRsrvStr1(modem_id);//光猫串号
    	        otherTradeData.setRsrvStr6(modem_type);//光猫型号
    	        otherTradeData.setRsrvStr3(kdNumber);//宽带号码
    	        otherTradeData.setRsrvStr4(btd.getRD().getUca().getSerialNumber());//主号号码
    	        otherTradeData.setRsrvStr5(kdTradeId);//开户的TRADE_ID
    	        otherTradeData.setRsrvStr12(btd.getTradeId());//光猫终端出库流水
    	        btd.add(kdNumber, otherTradeData);
    		}
    	} 
    }
}
