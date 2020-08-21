package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.reg;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.requestdata.IMSLandLineRequestData;

@SuppressWarnings("unchecked")
public class IMSSpcDiscntAction implements ITradeAction
{
	public void executeAction(BusiTradeData btd) throws Exception
	{
		IMSLandLineRequestData IMSLandLineRequestData = (IMSLandLineRequestData) btd.getRD();
		String serial_number = btd.getRD().getUca().getSerialNumber();

		String userId = btd.getRD().getUca().getUserId();

		String start_date = SysDateMgr.getSysTime();
		String inst_id = SeqMgr.getInstId();
		// 查询优惠配置
		DiscntTradeData discntTradeData = new DiscntTradeData();
		discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);// 0:新增,1:删除,2:修改
		discntTradeData.setUserId(userId);
		discntTradeData.setInstId(inst_id);
		discntTradeData.setStartDate(start_date);
		discntTradeData.setEndDate(SysDateMgr.getAddMonthsLastDay(3));
		
		discntTradeData.setUserIdA("-1");
		discntTradeData.setSpecTag("2"); // 特殊优惠标记
		discntTradeData.setPackageId("-1");
		discntTradeData.setProductId("-1");

		discntTradeData.setRelationTypeCode("MS");
		discntTradeData.setElementId("84004242");
		discntTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
		btd.add(btd.getRD().getUca().getSerialNumber(), discntTradeData);
	}

}
