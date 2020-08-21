package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.ViceRealInfoReRegBean;

public class DealMospViceUserAction  implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		UcaData uca = btd.getRD().getUca();
		String tradeTypeCode = btd.getTradeTypeCode();
		String action = this.getActionByTradeTypeCode(tradeTypeCode);
		String sn = uca.getSerialNumber();
		if(StringUtils.isBlank(action))
		{
			return;
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TF_F_RELATION_UU A WHERE SERIAL_NUMBER_B =:SERIAL_NUMBER_B AND RELATION_TYPE_CODE =:RELATION_TYPE_CODE AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", sn);
		param.put("RELATION_TYPE_CODE", "M2");
		
		IDataset relationList = Dao.qryBySql(sql, param, CSBizBean.getTradeEparchyCode());
		
		if(IDataUtil.isEmpty(relationList))
		{
			ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
			IData input = new DataMap();
			input.put("SERIAL_NUMBER_B", sn);
			relationList= bean.qryHdhSynInfo(input);
			if(IDataUtil.isNotEmpty(relationList))
			{//TP_F_HDH_SYNINFO
				IData temp = relationList.getData(0);
				String category = temp.getString("CATEGORY");
				String sna = temp.getString("SERIAL_NUMBER");
				String orderNo = temp.getString("ORDERNO");
				
				this.createOtherTradeData(btd, sna, category, action,orderNo);
			}
			
		}else{
			//uu表
			IData temp = relationList.getData(0);
			String userIdb = temp.getString("USER_ID_B");
			String category = userIdb.substring(userIdb.length()-1, userIdb.length());
			String sna = temp.getString("SERIAL_NUMBER_A");
			String orderNo = temp.getString("ORDERNO");
			
			this.createOtherTradeData(btd, sna, category, action,orderNo);
		}
	}
	
	public String getActionByTradeTypeCode(String tradeTypeCode)throws Exception
	{
		String action ="";
		
		IDataset mospPara = CommparaInfoQry.getCommparaAllCol("CSM", "9753", tradeTypeCode, CSBizBean.getTradeEparchyCode());
		
		if(IDataUtil.isNotEmpty(mospPara))
		{
			action = mospPara.getData(0).getString("PARA_CODE1");
		}
		
		return action;
	}
	
	public void createOtherTradeData(BusiTradeData btd,String sna,String category,String action,String orderNo)throws Exception
	{
		OtherTradeData otherTd = new OtherTradeData();
		otherTd.setInstId(SeqMgr.getInstId());
		otherTd.setUserId(btd.getRD().getUca().getUserId());
		otherTd.setRsrvValueCode("MOSP_VICE_USER");
		otherTd.setRsrvStr1(category);
		otherTd.setRsrvStr2(sna);
		otherTd.setRsrvStr3(action);
		otherTd.setRsrvStr4("731");
		otherTd.setRsrvStr5(btd.getRD().getUca().getSerialNumber());
		otherTd.setRsrvStr6(orderNo);
		otherTd.setRsrvStr9("99140605");
		otherTd.setIsNeedPf("1");
		otherTd.setOperCode(action);
		otherTd.setStartDate(btd.getRD().getAcceptTime());
		otherTd.setEndDate(SysDateMgr.addDays(1));
		otherTd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		
		btd.add(btd.getRD().getUca().getSerialNumber(), otherTd);
		
		MainTradeData mtd = btd.getMainTradeData();
		mtd.setOlcomTag("1");
	}

}
