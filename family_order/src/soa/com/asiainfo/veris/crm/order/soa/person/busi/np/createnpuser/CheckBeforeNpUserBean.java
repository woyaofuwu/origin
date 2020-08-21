package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
/**
 * 
 * @author dengyi5
 * @title 携入申请查验流程（新）
 * @time 2019/3/13
 */
public class CheckBeforeNpUserBean extends CSBizBean {

	public IData insUserNpCheck(IData input) throws Exception {
		IData result = new DataMap();
		result.put("RESULT_CODE", "1");
		
		IData params = new DataMap();
		params.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		params.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		CSAppCall.call("SS.CreateNpUserTradeSVC.checkSerialNumber", params);
		
		String sysTime = SysDateMgr.getSysTime();
		String psptTypeCode = input.getString("PSPT_TYPE_CODE");
		if("00".equals(psptTypeCode)){
			psptTypeCode = "0";//能开传入的证件类型为00，表示身份证
		}
		
        IData tradenpinfo = new DataMap();
        tradenpinfo.put("TRADE_ID",SeqMgr.getTradeId());
        tradenpinfo.put("ACCEPT_MONTH",SysDateMgr.getCurMonth());
        tradenpinfo.put("USER_ID","0");//携入前用户ID未知
        tradenpinfo.put("TRADE_TYPE_CODE", "38");
        tradenpinfo.put("NP_SERVICE_TYPE", "MOBILE");
        tradenpinfo.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        tradenpinfo.put("MSG_CMD_CODE", "AUTH_REQ");
        tradenpinfo.put("PORT_OUT_NETID", "00248980");//查验流程不需要该字段
        tradenpinfo.put("PORT_IN_NETID", "00248980");
        tradenpinfo.put("HOME_NETID", "00248980");//查验流程不需要该字段        
        tradenpinfo.put("CRED_TYPE", psptTypeCode);
        tradenpinfo.put("PSPT_ID", input.getString("PSPT_ID"));
        tradenpinfo.put("ACCEPT_DATE",sysTime);
        tradenpinfo.put("CREATE_TIME", sysTime);
        tradenpinfo.put("BOOK_SEND_TIME", sysTime);
        tradenpinfo.put("CANCEL_TAG", "0");
        tradenpinfo.put("STATE", "040");
        tradenpinfo.put("AUTH_CODE", input.getString("AUTH_CODE"));
        tradenpinfo.put("AUTH_CODE_EXPIRED", input.getString("AUTH_CODE_EXPIRED"));
        tradenpinfo.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        tradenpinfo.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        tradenpinfo.put("EPARCHY_CODE", getTradeEparchyCode());
        boolean Isins = Dao.insert("TF_B_TRADE_NP", tradenpinfo);
        if(Isins)
        {
    		result.put("RESULT_CODE", "0");
        }
        return result;
	}
	
}
