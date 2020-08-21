package com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util.SynJingxinUtils;

/**
 * 同步到京信平台数据
 * @author Administrator
 *
 */
public class SyncPurchaseDataAction implements ITradeFinishAction
{
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	if("0".equals(mainTrade.getString("RSRV_STR7"))){
    		IData insparam = new DataMap();
    		//获取京信平台对应套餐编码
    		IDataset dataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "313", mainTrade.getString("RSRV_STR6"),"ZZZZ");
    		
    		insparam.put("appId", SynJingxinUtils.getJingxinAppId());
    		insparam.put("timeStamp", String.valueOf(System.currentTimeMillis()));
    		insparam.put("account", mainTrade.getString("SERIAL_NUMBER"));
    		insparam.put("packageCode",dataset.first().getString("PARA_CODE3"));
    		SynJingxinUtils.post("purchase", insparam);
    	}
    }
}
