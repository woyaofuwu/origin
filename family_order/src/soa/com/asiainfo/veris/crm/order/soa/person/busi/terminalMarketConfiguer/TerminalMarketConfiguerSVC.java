package com.asiainfo.veris.crm.order.soa.person.busi.terminalMarketConfiguer;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.terminalMarketConfiguer.TerminalMarketConfiguerBean;

public class TerminalMarketConfiguerSVC extends CSBizService {


	// 查询
	public IData qurTerminalData(IData param) throws Exception {
		TerminalMarketConfiguerBean terminalMarketConfiguerBean = BeanManager.createBean(TerminalMarketConfiguerBean.class);
		IData datas = terminalMarketConfiguerBean.qurTerminalData(param);
		return datas;
	}

	// 新增
	public IData insertTerminalData(IData param) throws Exception {
		IData data = new DataMap();
		TerminalMarketConfiguerBean terminalMarketConfiguerBean = BeanManager.createBean(TerminalMarketConfiguerBean.class);
		data = terminalMarketConfiguerBean.insertTerminalData(param);
		return data;
		
	}
	// 删除
	public void delTerminalData(IData param) throws Exception {
		TerminalMarketConfiguerBean terminalMarketConfiguerBean = BeanManager.createBean(TerminalMarketConfiguerBean.class);
		terminalMarketConfiguerBean.delTerminalData(param);
		
	}
	
	// 修改
	public IData updateTerminalData(IData param) throws Exception {
		IData data = new DataMap();
		TerminalMarketConfiguerBean terminalMarketConfiguerBean = BeanManager.createBean(TerminalMarketConfiguerBean.class);
		data = terminalMarketConfiguerBean.updateTerminalData(param);
		return data;
		
	}
	

}