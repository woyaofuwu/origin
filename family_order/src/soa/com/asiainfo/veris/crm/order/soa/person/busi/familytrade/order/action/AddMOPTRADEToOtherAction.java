
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 将MOP嵌入的互联网化界面与BOSS系统对应界面进行受理渠道（in_mode_code）的区分 REQ201909030018
 * @author wangck3
 *
 */
public class AddMOPTRADEToOtherAction implements ITradeAction
{
	private static final Logger logger = Logger.getLogger(AddMOPTRADEToOtherAction.class);
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String serialNumber = btd.getRD().getUca().getSerialNumber();
    	String tradeTypeCode = btd.getTradeTypeCode();
    	String userId = btd.getRD().getUca().getUserId();
    	IData pageData = btd.getRD().getPageRequestData();
    	logger.info("======tradeTypeCode=====："+tradeTypeCode);
    	IDataset tradeTypeParam = CommparaInfoQry.getCommparaByCode1("CSM", "6880", "MOP_TRADE", tradeTypeCode, null);
    	if(IDataUtil.isNotEmpty(tradeTypeParam)&&IDataUtil.isNotEmpty(pageData)){
    		String isphone = pageData.getString("ISPHONE_K");
    		logger.info("======ISPHONE_K=====："+isphone);
    		if("PHONE_TAG".equals(isphone)){
    			OtherTradeData otherData =	new OtherTradeData();
    			otherData.setModifyTag(BofConst.MODIFY_TAG_ADD);
    			otherData.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    			otherData.setEndDate(SysDateMgr.getSysDate(SysDateMgr.END_DATE_FOREVER));
    			otherData.setInstId(SeqMgr.getInstId());
    			otherData.setUserId(userId);
    			otherData.setRsrvValueCode("MOP_TRADE");
    			otherData.setRsrvValue(serialNumber);
    			otherData.setRemark("MOP嵌入互联网化界面办理业务时,新增MOP_TRADE标识");
    			btd.add(serialNumber, otherData);
    		}
    	}
    }

}
