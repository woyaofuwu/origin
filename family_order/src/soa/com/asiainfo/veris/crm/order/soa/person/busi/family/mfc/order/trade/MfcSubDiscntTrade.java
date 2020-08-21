package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.requestdata.MfcSubDiscntReqData;

public class MfcSubDiscntTrade extends BaseTrade implements ITrade
{

	private static final transient Logger log = Logger.getLogger(MfcSubDiscntTrade.class);
	
	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception 
	{
		MfcSubDiscntReqData subDiscntRD = (MfcSubDiscntReqData)btd.getRD();
		if (log.isDebugEnabled())
		{
			log.debug("11111111111111111111111111MfcSubDiscntTrade11111111111111");
		}
		//1.查询资费编码
		IDataset discntInfo = MfcCommonUtil.qryByParam134("CSM", "2018", "KSJTW_MAIN_DIS", "", subDiscntRD.getRoleCode(), subDiscntRD.getProductCode());
    	if(DataUtils.isEmpty(discntInfo))
    	{//不存在该资费，不做处理
    		return;
    	}
    	
		//2.新增/删除资费
    	if(BofConst.MODIFY_TAG_ADD.equals(subDiscntRD.getModifyTag()))
    	{
    		addDiscnt(btd,subDiscntRD,discntInfo.getData(0).getString("PARA_CODE1"));
    	}
    	else
    	{
    		delDiscnt(btd,subDiscntRD,discntInfo.getData(0).getString("PARA_CODE1"));
    	}
	}

	private void delDiscnt(BusiTradeData btd, MfcSubDiscntReqData subDiscntRD, String discntCode) throws Exception
	{
    	IDataset delDiscntTradeDatas = MfcCommonUtil.queryDiscntInfoByUserIdAndDisCode(btd.getRD().getUca().getUserId(), discntCode, subDiscntRD.getProductOfferingID());
    	if(delDiscntTradeDatas == null || delDiscntTradeDatas.isEmpty())
    	{//不存在该资费不做处理
    		return;
    	}
    	DiscntTradeData delDiscntTradeData = new DiscntTradeData(delDiscntTradeDatas.getData(0));
    	delDiscntTradeData.setEndDate(SysDateMgr.getSysTime());
    	delDiscntTradeData.setRemark("副号资费删除");
    	delDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
    	btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTradeData);
	}

	private void addDiscnt(BusiTradeData btd, MfcSubDiscntReqData subDiscntRD, String discntCode) throws Exception
	{
		DiscntTradeData dt = new DiscntTradeData();
		dt.setUserId(btd.getRD().getUca().getUserId());//绑定资费
		dt.setUserIdA(subDiscntRD.getUserIdA());
		dt.setProductId("-1");
		dt.setPackageId("-1");
		dt.setElementId(discntCode);
		dt.setSpecTag("0");
		dt.setModifyTag(BofConst.MODIFY_TAG_ADD);
		dt.setStartDate(subDiscntRD.getStartDate());
		dt.setEndDate(subDiscntRD.getEndDate());
		dt.setInstId(SeqMgr.getInstId());
		dt.setRelationTypeCode("MF");
		dt.setRsrvStr1(subDiscntRD.getProductOfferingID());//业务订购实例ID,用于唯一定位家庭
		btd.add(btd.getRD().getUca().getSerialNumber(), dt);
	}
}
