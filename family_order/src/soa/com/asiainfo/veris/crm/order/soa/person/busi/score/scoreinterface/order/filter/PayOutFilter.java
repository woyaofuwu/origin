
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import net.sf.json.JSONArray;

import com.ailk.bizcommon.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

import com.ailk.common.data.IDataOutput;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScorePayReqData;

public class PayOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        ScorePayReqData reqData = (ScorePayReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        int balanceScore = Integer.parseInt(mainList.getRsrvStr1().split("\\^")[0]) - Integer.parseInt(mainList.getRsrvStr2());
        int totalPoint = Integer.parseInt(mainList.getRsrvStr1().split("\\^")[1]) - Integer.parseInt(mainList.getRsrvStr2());
     // 设置交易成功数据
        IData succData = new DataMap();
        succData.put("TRADE_SEQ", reqData.getTRADE_SEQ());
        succData.put("ORGID", reqData.getORGID());
        succData.put("MOBILE", btd.getRD().getUca().getSerialNumber());
        succData.put("PAY_POINT", reqData.getPAY_POINT());
        succData.put("POINT_BALANCE", balanceScore);
        succData.put("CONSUME_POINT", mainList.getRsrvStr3().split("\\^")[0]);
        succData.put("PROMOTION_POINT", mainList.getRsrvStr3().split("\\^")[1]);
		JSONArray scoreInfosTimes =new JSONArray().fromObject(mainList.getRsrvStr6());
		JSONArray scoreInfosPoints =new JSONArray().fromObject(mainList.getRsrvStr7());
		succData.put("VALIDATE_TIME", scoreInfosTimes);
        succData.put("P_PROMOTION_POINT", scoreInfosPoints);
        succData.put("TOTAL_POINT", totalPoint);
		
        if ("SS.ScoreDeductRegSVC.tradeRegOutter".equals(btd.getRD().getXTransCode())) {
        	IData userScoreInfo = queryUserScoreOutter(btd);
        	//succData.put("POINT_BALANCE", userScoreInfo.getString("POINT_BALANCE"));
        	succData.put("CONSUME_POINT_BALANCE", userScoreInfo.getString("CONSUME_POINT_BALANCE"));
        	succData.put("PROMOTION_POINT_BALANCE", userScoreInfo.getString("PROMOTION_POINT_BALANCE"));
        }
		
        return succData;
    }
	
	private IData queryUserScoreOutter(BusiTradeData btd) throws Exception 
    {
    	IData param = new DataMap();
    	param.put("PROVINCE_CODE", "HAIN");// 省别编码
    	param.put("TRADE_EPARCHY_CODE", btd.getRoute());// 受理地州
    	param.put(Route.ROUTE_EPARCHY_CODE, btd.getMainTradeData().getEparchyCode());// 路由地州
        param.put("USER_ID", btd.getRD().getUca().getUserId());
        param.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
        IDataOutput result = CSAppCall.callAcct("SM_BBOSS_UserScoreOut", param, false);
        return result.getData().getData(0);
    }

}
