package com.asiainfo.veris.crm.order.web.person.dimensionalcode;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 二维码
 */
public abstract class DimensionalCode extends PersonBasePage
{
	public abstract void setCondition(IData condition);

	public abstract void setInfos(IDataset infos);

	public abstract void setInfo(IData info);

	/**
	 * 查询功能：初始化页面条件
	 */
	public void onInitQryPage(IRequestCycle cycle) throws Exception {
		IData condition = new DataMap();
		condition.put("DATE_FROM", SysDateMgr.getTodayLastMonth());
		condition.put("DATE_TO", SysDateMgr.getSysDate());
		setCondition(condition);
	}
	/**查询功能：查询列表
	 */
	public void qryDimensionalCodeStateList(IRequestCycle cycles) throws Exception {
		IData condition = getData("COND", true);
		IData result = CSViewCall.call(this,"SS.DimensionalCodeSVC.qryDimensionalCodeStateList",condition).getData(0);
		if(null!=result&&!result.isEmpty()){
			IDataset history_list=result.getDataset("HISTORY_LIST");
			if(null!=history_list&&!history_list.isEmpty()){
				this.setInfos(history_list);
				this.setCondition(condition);
			}else {
				CSViewException.apperr(CrmCommException.CRM_COMM_103,"没有状态变更");
			}
		}else{
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"查询失败");
		}
	}
	/**
	 * 变更功能：重载父类函数 获取三户以外的参数、业务数据 初始化页面
	 */
	public void qryOtherInfos(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IData auth = getData("AUTH", true);
		pageData.putAll(auth);
		CSViewCall.call(this,"SS.DimensionalCodeSVC.isNormalMainService",pageData);
		//初始化页面
		IData info = new DataMap();
		info.put("STATUS", "08");
		info.put("DIMENSIONALCODE_OPR_CODE", "01");
		IData resultData = CSViewCall.call(this,"SS.DimensionalCodeSVC.qryDimensionalCodeStateList",pageData).getData(0);
		if(null!=resultData&&!resultData.isEmpty()){
			IDataset history_list=resultData.getDataset("HISTORY_LIST");
			if(null!=history_list&&!history_list.isEmpty()){
				info.clear();
				info.put("STATUS", history_list.getData(0).getString("STATUS", "08"));
			}
		}
		else{
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"调用IBOSS接口出错");
		}
		this.setInfo(info);
	}
	/**
	 * 变更功能：校验选择的操作是否合法
	 */
	public void checkStatus(IRequestCycle cycles) throws Exception {
		IData pageData = getData();
		IData input = getData("AUTH", true);
		pageData.putAll(input);
		String cur_status=pageData.getString("CUR_STATUS");
		String cur_opr_code=pageData.getString("CUR_OPR_CODE");
		pageData.put("STATUS",cur_status);
		pageData.put("OPR_CODE",cur_opr_code);
		IData resultData =  CSViewCall.call(this,"SS.DimensionalCodeSVC.isLegalOprCode",pageData).getData(0);
		this.setAjax(resultData);
	}
	/**
	 * 变更功能：提交变更
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IData input = getData("AUTH", true);
		pageData.putAll(input);
		IDataset returnDataset = CSViewCall.call(this, "SS.DimensionalCodeSVC.dimensionalCodeOperate", pageData);
        this.setAjax(returnDataset);
	}
}