package com.asiainfo.veris.crm.order.soa.person.busi.unitopenchangeactive.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;

/**
 * 政企激活后更新状态。
 */
public class UpdateStateAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {

		CustPersonTradeData custPerson = btd.getRD().getUca().getCustPerson();
		IData data = new DataMap();
		data.put("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		data.put("PSPT_ID",custPerson.getRsrvStr7());
		data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		data.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());

		StringBuilder updSql = new StringBuilder();

		updSql.append(" UPDATE TL_B_UNITOPEN_RECORD  ");
		updSql.append(" SET  ");
		updSql.append("STATE = '2' ,");
		updSql.append("UPDATE_TIME = SYSDATE ,");
		updSql.append("UPDATE_STAFF_ID = :UPDATE_STAFF_ID ,");
		updSql.append("UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");




		updSql.append("  WHERE SERIAL_NUMBER = :SERIAL_NUMBER AND PSPT_ID= :PSPT_ID  ");
		Dao.executeUpdate(updSql, data, Route.getCrmDefaultDb());

		}

	}


