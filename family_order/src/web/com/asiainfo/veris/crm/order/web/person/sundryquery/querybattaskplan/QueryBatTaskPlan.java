package com.asiainfo.veris.crm.order.web.person.sundryquery.querybattaskplan;

import java.text.DecimalFormat;
import java.util.logging.SimpleFormatter;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：批量任务计划查询 author@yanmm
 */
public abstract class QueryBatTaskPlan extends PersonBasePage {

	public abstract IDataset getInfos();

	/**
	 * 初始化
	 */
	public void init(IRequestCycle cycle) throws Exception {
		// 设置创建和启动时间
		String creatDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
		String startDate = creatDate;
		IData data = getData("cond", true);
		data.put("cond_START_DATE", startDate);
		data.put("cond_CREAT_DATE", creatDate);
		// data.put("cond_TRADE_STAFF_ID", this.getVisit().getStaffId());
		
		 data = new DataMap();
	        data.put("CHECK_PRIV_FLAG", "0");
	        data.put("RIGHT_CLASS", "0");
	        data.put("TRADE_ATTR", "1");
	        IDataset batchOpertypes = CSViewCall.call(this, "CS.BatDealSVC.queryBatchTypes", data);
	        setBatchOperTypes(batchOpertypes);
		this.setCondition(data);
	}

	/**
	 * 功能：批量任务计划查询时
	 */
	public void queryBatTaskPlan(IRequestCycle cycle) throws Exception {
		IData inparam = getData("cond", true);

		inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
		IDataOutput dataCount = CSViewCall.callPage(this,
				"SS.QueryBatTaskPlanSVC.queryBatTaskPlan", inparam,
				getPagination("navt"));
		IDataset results = dataCount.getData();
		int tCnt=0;
		int fCnt=0;
		double per=0l;
		for(int i=0;i<results.size();i++){
			IData tmp=results.getData(i);
			tCnt+=Integer.parseInt(tmp.get("TOTAL_COUNT").toString());
			fCnt+=Integer.parseInt(tmp.get("FINISH_COUNT").toString());
		}
		String tCntStr=tCnt+"";
		String fCntStr=fCnt+"";
		String perStr="";
		if(tCnt!=0){
			DecimalFormat fm=new DecimalFormat("#.##%");
			per=fCnt*1.0/tCnt;
			perStr=fm.format(per);
		}
        
    	IData total=new DataMap();
		total.put("BATCH_TASK_NAME","任务合计");
		total.put("BATCH_OPER_NAME","");
		total.put("CREATE_STAFF_ID","");
    	total.put("CREATE_TIME","");
		total.put("START_DATE","");
		total.put("TOTAL_COUNT",tCntStr);
		total.put("FINISH_COUNT",fCntStr);
		total.put("PER",perStr);
		
		results.add(total);
	
	String alertInfo = "";
		if (IDataUtil.isEmpty(results)) {
			alertInfo = "没有符合查询条件的【批量任务计划查询】数据~";
		}
		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		setInfos(results);
		setCount(dataCount.getDataCount());
		setCondition(getData("cond", true));

	}

	public abstract void setCondition(IData cond);

	public abstract void setCount(long count);

	public abstract void setInfos(IDataset infos);
	 public abstract void setBatchOperTypes(IDataset batchOperTypes);

}
