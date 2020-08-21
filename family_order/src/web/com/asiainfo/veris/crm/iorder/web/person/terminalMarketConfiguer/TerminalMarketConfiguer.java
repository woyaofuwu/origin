package com.asiainfo.veris.crm.iorder.web.person.terminalMarketConfiguer;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TerminalMarketConfiguer extends PersonBasePage {
	public abstract void setInfo(IData info);
	
	public void insertActiv(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ACTIV_ID", param.getString("ACTIV_ID"));
		inParam.put("APPOINT_AMOUNT", param.getString("APPOINT_AMOUNT"));
		inParam.put("TERMINAL_IN_PRICE", param.getString("TERMINAL_IN_PRICE"));
		inParam.put("TERMINAL_SAL_PRICE", param.getString("TERMINAL_SAL_PRICE"));
		inParam.put("BARE_SAL_PRICE", param.getString("BARE_SAL_PRICE"));
		inParam.put("SUBSIDY", param.getString("SUBSIDY"));
		inParam.put("RETURN_BILL", param.getString("RETURN_BILL"));
		inParam.put("TOTAL", param.getString("TOTAL"));
		inParam.put("RETURN_DATE", param.getString("RETURN_DATE"));
		inParam.put("SENT_GIFT", param.getString("SENT_GIFT"));
		inParam.put("ACTIV_DATE", param.getString("ACTIV_DATE"));
		inParam.put("UPDATE_DATE", SysDateMgr.getSysDate());
		inParam.put("UPDATE_STAFFID", getVisit().getStaffId());
		inParam.put("REMOVE_TAG", "0");
		inParam.put("SENT_PHONE_BILL", param.getString("SENT_PHONE_BILL"));
		inParam.put("GIFT_ITEMS", param.getString("GIFT_ITEMS"));
		IData result = CSViewCall.callone(this,"SS.TerminalMarketConfiguerSVC.insertTerminalData", inParam);
		setAjax(result);
	}
	
	public void updateActiv(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		IData inParamId = new DataMap();
		inParamId.put("ACTIV_ID", param.getString("ACTIV_ID"));
		inParam.put("ACTIV_ID", param.getString("ACTIV_ID"));
		inParam.put("APPOINT_AMOUNT", param.getString("APPOINT_AMOUNT"));
		inParam.put("TERMINAL_IN_PRICE", param.getString("TERMINAL_IN_PRICE"));
		inParam.put("TERMINAL_SAL_PRICE", param.getString("TERMINAL_SAL_PRICE"));
		inParam.put("BARE_SAL_PRICE", param.getString("BARE_SAL_PRICE"));
		inParam.put("SUBSIDY", param.getString("SUBSIDY"));
		inParam.put("RETURN_BILL", param.getString("RETURN_BILL"));
		inParam.put("TOTAL", param.getString("TOTAL"));
		inParam.put("RETURN_DATE", param.getString("RETURN_DATE"));
		inParam.put("SENT_GIFT", param.getString("SENT_GIFT"));
		inParam.put("ACTIV_DATE", param.getString("ACTIV_DATE"));
		inParam.put("SENT_PHONE_BILL", param.getString("SENT_PHONE_BILL"));
		inParam.put("GIFT_ITEMS", param.getString("GIFT_ITEMS"));
		//查询修改但是为修改比较
		IData resultQry = CSViewCall.callone(this,"SS.TerminalMarketConfiguerSVC.qurTerminalData", inParamId);
		boolean flag = compare(inParam,resultQry);
		//如果为false说明被修改过了可以提交了
		if(flag == false){
			inParam.put("UPDATE_DATE", SysDateMgr.getSysDate());
			inParam.put("UPDATE_STAFFID", getVisit().getStaffId());
			inParam.put("REMOVE_TAG", "0");
			IData result = CSViewCall.callone(this,"SS.TerminalMarketConfiguerSVC.updateTerminalData", inParam);
			setAjax(result);
		}else{
			String ALERT_INFO = "isflag";
			this.setAjax("ALERT_INFO", ALERT_INFO);// 传给页面提示
		}
		
	}
	//获取前台的数据和查询数据库的数据来进行比较，如果字段都相等则返回则不允许提交修改，inParam前台传的数据，resultQry后台查询出来的数据
	public boolean compare(IData inParam,IData resultQry){
		//不提交
		boolean flag = true;
		Set<String> set = inParam.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			//str前台的字段
			String str = it.next();
			//根据字段获取前台传的字段值
			String str1 = inParam.getString(str);
			//不为空的时候才比较
			if(StringUtils.isNotEmpty(str1)){
				if(!(StringUtils.isBlank(str))
						&&!inParam.getString(str).equals(resultQry.getString(str))){
					//比较满足两个参数不相等则提交
					flag = false;
				}
			}
			
		}
		
		return flag;
	}
	
	public void delActiv(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ACTIV_ID", param.getString("ACTIV_ID"));
		CSViewCall.callone(this,"SS.TerminalMarketConfiguerSVC.delTerminalData", inParam);
	}
	
	public void queryActiv(IRequestCycle cycle)throws Exception {
		String alertInfo = "";
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ACTIV_ID", param.getString("ACTIV_ID"));
		IData result = CSViewCall.callone(this,"SS.TerminalMarketConfiguerSVC.qurTerminalData", inParam);
		if (IDataUtil.isEmpty(result))			
        {
            alertInfo = "没有符合查询条件的数据!";
        }
		if(IDataUtil.isNotEmpty(result)){
			String str = result.getString("REMOVE_TAG");
			if("1".equals(str)){
				alertInfo = "该营销活动ID已置失效!";
			}else{
				this.setInfo(result);
			}
		}
		
		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		
		
	}
}
