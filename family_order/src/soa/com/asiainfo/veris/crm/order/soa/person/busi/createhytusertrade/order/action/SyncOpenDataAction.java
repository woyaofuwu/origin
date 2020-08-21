package com.asiainfo.veris.crm.order.soa.person.busi.createhytusertrade.order.action;  

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util.SynJingxinUtils;

/**
 * 同步TF_F_SHIP_INFO数据
 * @author Administrator
 *
 */
public class SyncOpenDataAction implements ITradeFinishAction
{
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	
    	IData insparam = new DataMap();
    	String psptId = mainTrade.getString("RSRV_STR7");
    	//获取京信平台对应套餐编码
    	IDataset dataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "313", mainTrade.getString("RSRV_STR6"),"ZZZZ");
    	
    	insparam.put("appId", SynJingxinUtils.getJingxinAppId());
    	insparam.put("timeStamp", String.valueOf(System.currentTimeMillis()));
    	insparam.put("account", mainTrade.getString("SERIAL_NUMBER"));
        insparam.put("password", psptId.substring(psptId.length()-6));
        insparam.put("shipCode", mainTrade.getString("RSRV_STR1"));
        insparam.put("identityType",  "0".equals(mainTrade.getString("RSRV_STR2"))?"1":"0");
        insparam.put("packageCode", dataset.first().getString("PARA_CODE3"));
        SynJingxinUtils.post("register", insparam);
    }
}
