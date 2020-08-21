package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;

public class DonateITFOutFilter implements IFilterOut {

	@Override
	public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception {
        MainTradeData mainTradeData = btd.getMainTradeData();

        int score = Integer.parseInt(mainTradeData.getRsrvStr1()); // 转让人原积分总额
        int scoreBalance = Integer.parseInt(mainTradeData.getRsrvStr2()); // 转让人剩余积分总额
        String tradeEparchyCode = mainTradeData.getEparchyCode();
        String tradeCityCode = mainTradeData.getCityCode();
        String tradeDepartId = CSBizBean.getVisit().getDepartId();
        String tradeStaffId = CSBizBean.getVisit().getStaffId();
        String tradeDate = mainTradeData.getExecTime();
        String serialNumber = mainTradeData.getSerialNumber();
        int donateScore = Integer.parseInt(mainTradeData.getRsrvStr10()); // 转赠积分值
        
        IData resultData = new DataMap();//返回数据
		resultData.put("SCORE_COUNT", score);
		resultData.put("SCORE_BANLACE", scoreBalance);
		resultData.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
		resultData.put("TRADE_CITY_CODE", tradeCityCode);
		resultData.put("TRADE_DEPART_ID", tradeDepartId);
		resultData.put("TRADE_STAFF_ID", tradeStaffId);
		//组织返回数据
		resultData.put("TRADE_DATE", tradeDate);
		resultData.put("SERIAL_NUMBER", serialNumber);
		resultData.put("DONATE_SCORE", donateScore);

        IData succData = new DataMap();
        succData.put("X_RESULTCODE", "0");
        succData.put("X_RESULTINFO", "积分转赠成功！");
        succData.put("X_RESULTPARAM", resultData);
		
		return succData;
	}
}
