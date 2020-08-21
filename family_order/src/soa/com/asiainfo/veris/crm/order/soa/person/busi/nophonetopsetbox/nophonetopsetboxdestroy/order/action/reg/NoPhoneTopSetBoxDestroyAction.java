package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxdestroy.order.action.reg;

import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
/**
 * 无手机宽带拆机 ：
 * 无手机魔百和也需拆机
 * @author zhengkai5
 */
public class NoPhoneTopSetBoxDestroyAction implements ITradeAction {

	protected static Logger log = Logger.getLogger(NoPhoneTopSetBoxDestroyAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String sn = btd.getRD().getPageRequestData().getString("SERIAL_NUMBER","");
		
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", sn);
		IData topsetboxInfo = CSAppCall.callOne("SS.NoPhoneTopSetBoxSVC.queryTopSetBoxInfoByWsn",data);
        if(IDataUtil.isNotEmpty(topsetboxInfo))
        {
        	data.put("SERIAL_NUMBER", topsetboxInfo.getString("SERIAL_NUMBER"));   //147号码
    		data.put("SERIAL_NUMBER_B", topsetboxInfo.getString("WIDE_SERIAL_NUMBER"));   //宽带号码
    		
            data.put(Route.ROUTE_EPARCHY_CODE, "0898");
            IDataset dataset = CSAppCall.call("SS.NoPhoneTopSetBoxDestroyRegSVC.tradeReg", data);
        }
	} 
}
