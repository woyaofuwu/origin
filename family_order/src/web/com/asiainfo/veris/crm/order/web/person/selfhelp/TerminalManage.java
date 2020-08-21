
package com.asiainfo.veris.crm.order.web.person.selfhelp;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TerminalManage extends PersonBasePage
{

	public abstract void setCondition(IData condition);
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData params);
	public abstract void setCityInfo(IDataset dataset);
	public abstract void setCount(long count);
//	public abstract void setExport(IData data);

	/**
	 * 页面初始化设置
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void init(IRequestCycle cycle) throws Exception
	{
		IData params = getData();
		
		String loginStaffId = getVisit().getStaffId(); // 系统登录工号
		String cityCode = getVisit().getCityCode(); // 归属业务区
		
		if (checkRight(loginStaffId))
		{
			params.put("EPARCH_CODE", "0898");
		}
		else
		{
			params.put("EPARCH_CODE", cityCode);
		}
		
		IDataset cityInfos = CSViewCall.call(this, "SS.TerminalManageSVC.queryCityInfos", params);
		setCityInfo(cityInfos);
		
		params.put("CITY_CODE", cityCode);
		setCondition(params);

	}

	/**
	 * 自助终端设备资料（查询）
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryTerminals(IRequestCycle cycle) throws Exception
	{

		IData data = getData("cond", true);

		IDataOutput terminalInfos = CSViewCall.callPage(this, "SS.TerminalManageSVC.queryTerminals", data, getPagination("navt"));
		IDataset terminalInfo = terminalInfos.getData();
		
		/**
		 * 页面刷新，将分公司权限重新设置。
		 */
		String loginStaffId = getVisit().getStaffId(); // 系统登录工号
		String cityCode = getVisit().getCityCode(); // 归属业务区
		
		if (checkRight(loginStaffId))
		{
			data.put("EPARCH_CODE", "0898");
		}
		else
		{
			data.put("EPARCH_CODE", cityCode);
		}
		
		IDataset cityInfos = CSViewCall.call(this, "SS.TerminalManageSVC.queryCityInfos", data);
		setCityInfo(cityInfos);
		
		setCount(terminalInfos.getDataCount());
		
		setCondition(data); // 保留查询条件
		
		setInfos(terminalInfo);  // 返回结果
	}

	/**
	 * 删除自助终端
	 * @param cycle
	 * @throws Exception
	 */
	public void deleteTerminal(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
        setCondition(data);

        CSViewCall.call(this, "SS.TerminalManageSVC.deleteTerminal", data);
	}

	/**
	 * 保存自助终端
	 * @param cycle
	 * @throws Exception
	 */
	public void saveTerminal(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
        setCondition(data);
        
        IData param = getData("cond", true);
        
        CSViewCall.call(this, "SS.TerminalManageSVC.saveTerminal", param);
	}

	/**
	 * 工号权限校验
	 * @param staffid
	 * @return
	 * @throws Exception
	 */
	public boolean checkRight(String staffid) throws Exception
	{
		boolean isTrue = false;
		
		if (staffid.substring(0, 4).equals("HNSJ") || staffid.substring(0, 4).equals("HNYD") || staffid.substring(0, 4).equals("SUPE"))
		{
			isTrue = true;
		}
		
		return isTrue;
	}

	/**
	 * 单个查询
	 * @param cycle
	 * @throws Exception
	 */
	public void queryTerminal(IRequestCycle cycle) throws Exception
	{
		IData data = getData("cond", true);
		IDataOutput terminalInfos = CSViewCall.callPage(this, "SS.TerminalManageSVC.queryTerminal", data, getPagination("navt"));
		IDataset terminalInfo = terminalInfos.getData();
		setCount(terminalInfos.getDataCount());
		setInfos(terminalInfo);
	}
	
	/**
	 * 缴费提交
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
		IData data = getData("cond", true);
		IData info = CSViewCall.call(this, "SS.TerminalManageSVC.saveTerminalFee", data).getData(0);
		setAjax(info);
	}
	
	public void loadPrintData(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		IDataset dataset = CSViewCall.call(this, "SS.TerminalManageSVC.loadPrintData", data);
		setAjax(dataset);
	}
	
}