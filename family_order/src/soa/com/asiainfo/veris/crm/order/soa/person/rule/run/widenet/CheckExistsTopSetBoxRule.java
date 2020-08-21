package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class CheckExistsTopSetBoxRule extends BreBase implements IBREScript{

	private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	String serialNumber = databus.getString("SERIAL_NUMBER");
    	if(serialNumber.indexOf("KD_")!=-1){
    		serialNumber=serialNumber.replaceAll("KD_", "");
    	}
    	
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(IDataUtil.isNotEmpty(userInfo)){
    		String userId = userInfo.getString("USER_ID");
        	
        	IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
        	
        	if(IDataUtil.isNotEmpty(boxInfos)){
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "用户存在魔百和业务，请先对魔百和进行拆机才能办理！");
        		return true;
        	}else{
        		/*
        		 * 是否存在正在办理当中的魔百和业务
        		 */
        		IDataset set=TradeInfoQry.getTradeInfosByCancelTag(userId, "0", "3800");
        		if(IDataUtil.isNotEmpty(set)){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "不能办理，客户仍有魔百和开户未完工。");
        			return true;
        		}
        	}
    	}
    	
    	
    	return false;
    	
    }
}
