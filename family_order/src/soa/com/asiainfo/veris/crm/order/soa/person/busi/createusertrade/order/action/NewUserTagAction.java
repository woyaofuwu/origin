package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.queryuser.queryNewCardIdUserBean;

public class NewUserTagAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		IData redata = new DataMap();
		redata.put("CARD_ID_NUM",btd.getRD().getUca().getCustomer().getPsptId());
		String psptTypeCode = btd.getRD().getUca().getCustomer().getPsptTypeCode();
		System.out.println("NewUserTagActioncan参数"+psptTypeCode);
		if("0".equals(psptTypeCode)||"1".equals(psptTypeCode) ){//只有身份证才执行
			queryNewCardIdUserBean bean = BeanManager.createBean(queryNewCardIdUserBean.class);
			IDataset queryInfos = bean.Query(redata);
			System.out.println("进入NewUserTagAction"+queryInfos);
			if (null != queryInfos && queryInfos.size() > 0){
				return;
			}
			CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
	        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
	        String agentDepartId = ("".equals(createPersonUserRD.getAgentDepartId()) || createPersonUserRD.getAgentDepartId() == null) ? CSBizBean.getVisit().getDepartId() : createPersonUserRD.getAgentDepartId();

	        OtherTradeData otherTD = new OtherTradeData();
	        otherTD.setUserId(createPersonUserRD.getUca().getUserId());
	        otherTD.setRsrvValueCode("NEW_USER_TAG");
	        otherTD.setRsrvValue(serialNumber);

	        otherTD.setRsrvStr1(CSBizBean.getVisit().getStaffId());
	        otherTD.setRsrvStr2(SysDateMgr.getSysTime());
	        //otherTD.setRsrvStr3("1");
	       // otherTD.setRsrvStr4("10");
	        otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
	        otherTD.setStartDate(SysDateMgr.getSysTime());
	        otherTD.setEndDate(SysDateMgr.getTheLastTime());
	        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        /*
	         * if ("1".equals(createPersonUserRD.getBReopenTag())) otherTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
	         */
	        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
	        otherTD.setDepartId(agentDepartId);
	        otherTD.setInstId(SeqMgr.getInstId());
	        btd.add(serialNumber, otherTD);
		}
		
	}

}
