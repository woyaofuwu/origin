
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateIBossRequestData;

public class DonateIBossOutFilter implements IFilterOut
{
    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
    	ScoreDonateIBossRequestData reqData = (ScoreDonateIBossRequestData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        //计算用户可用积分余额
		int lscoreValue = Integer.parseInt(reqData.getSCORE());
		int bscoreValue = Integer.parseInt(reqData.getOBJ_SCORE());
		int lscoreSumValue = Integer.parseInt(reqData.getSUM_SCORE());
		int bscoreSumValue = Integer.parseInt(reqData.getOBJ_SUM_SCORE());
	  	int score = Integer.parseInt(mainList.getRsrvStr10());
	  	int	l_score_value = lscoreValue - score;
	  	int	b_score_value = bscoreValue + score;
	  	int	l_score_sum_value = lscoreSumValue - score;
	  	int	b_score_sum_value = bscoreSumValue + score;
        // 设置交易成功数据
        IData succData = new DataMap();
        succData.put("TRADE_SEQ", reqData.getTRADE_SEQ());
		succData.put("L_MOBILE", reqData.getL_MOBILE());
		succData.put("B_MOBILE", reqData.getB_MOBILE());
		succData.put("TRANSFER_POINT", reqData.getTRANSFER_POINT());
		succData.put("OPERATOR_TIME", mainList.getRsrvStr9());
		succData.put("L_POINT_BALANCE", l_score_value);
		succData.put("B_POINT_BALANCE", b_score_value);
		JSONArray scoreInfosTypes =new JSONArray().fromObject(mainList.getRsrvStr6());
		JSONArray scoreInfosPoints =new JSONArray().fromObject(mainList.getRsrvStr7());
		JSONArray scoreInfosTimes =new JSONArray().fromObject(mainList.getRsrvStr8());
		IDataset scoreInfos=new DatasetList();
		if(null!=scoreInfosTypes&&scoreInfosTypes.size()>0){
        	for(int i=0;i<scoreInfosTypes.size();i++){
        		IData scoreInfo=new DataMap();
        		scoreInfo.put("POINT_TYPE", scoreInfosTypes.get(i));
        		scoreInfo.put("CURRENT_TYPE_POINT",scoreInfosPoints.get(i));
        		scoreInfo.put("VALIDATE_TIME", scoreInfosTimes.get(i));
                scoreInfos.add(scoreInfo);
        	}
        }
        succData.put("TRANSFER_POINT_INFO", scoreInfos);
        succData.put("L_TOTAL_POINT", l_score_sum_value);
        succData.put("B_TOTAL_POINT", b_score_sum_value);
        return succData;
    }
}
