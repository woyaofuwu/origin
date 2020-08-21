package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.pc.esop;

import java.util.Date;
import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizPage;
import com.ailk.common.config.ModuleCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.ailk.web.util.CookieUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;

public abstract class Welcome extends BizPage {

	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData info);

	public abstract void setCondition(IData condition);

	public abstract void setCenters(List types);


	public void init(IRequestCycle cycle) throws Exception{
//		getVisit().setStaffId("PE501026");
//		initContract(cycle);
//		initBirthday(cycle);
		initMenu();
	}

	public void queryDeskTopWorkFlowInst(IRequestCycle cycle) throws Exception 
    {
        Pagination pagination = this.getPagination();
        //首页显示5条
        pagination.setPageSize(7);
        
        IData param = getData();
        param.put("STAFF_ID", getVisit().getStaffId());
        
        IDataOutput out = ServiceFactory.call("SS.WorkTaskMgrSVC.qryWorkTaskInfo", createDataInput(param), pagination);
        IDataset workTaskList = out.getData();
        
        if(IDataUtil.isNotEmpty(workTaskList))
        {
            IDataset unDoneList = new DatasetList();
            IDataset unReadList = new DatasetList();
            for(int i = 0, size = workTaskList.size(); i < size; i++)
            {
                IData workTask = workTaskList.getData(i);
                if("1".equals(workTask.getString("TASK_TYPE_CODE")))
                {//电子工单
                    StringBuilder tipStr = new StringBuilder(100);
                    tipStr.append("待办类型：").append(StaticUtil.getStaticValue("ESOP_TASKTYPECODE", workTask.getString("TASK_TYPE_CODE"))).append("<br/>");
                    tipStr.append("分派人：").append(StaticUtil.getStaticValueDataSource(getVisit(), "sys", "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", workTask.getString("TASK_AUTH"))).append("<br/>");
                    tipStr.append("要求完成时间：").append(workTask.getString("PLAN_FINISH_TIME")).append("<br/>");
//                  tipStr.append("原始工单时间：").append(workTask.getString("TASK_TYPE_CODE")).append("<br/>");
                    workTask.put("TIP", tipStr.toString());
                    
                    workTask.put("TIME_DIFF", getTimeDiff(SysDateMgr.getSysTime(), workTask.getString("PLAN_FINISH_TIME")));
                    
                    unDoneList.add(workTask);
                }
                else if("2".equals(workTask.getString("TASK_TYPE_CODE")))
                {//待阅工单
                    StringBuilder tipStr = new StringBuilder(100);
                    tipStr.append("分派人：").append(StaticUtil.getStaticValueDataSource(getVisit(), "sys", "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", workTask.getString("TASK_AUTH"))).append("<br/>");
                    tipStr.append("分派时间：").append(workTask.getString("PLAN_FINISH_TIME")).append("<br/>");
                    
                    workTask.put("TIP", tipStr.toString());
                    unReadList.add(workTask);
                }
            }
            IData info = new DataMap();
            info.put("UNDONE_WORKLIST", unDoneList);
            info.put("UNREAD_NOTICELIST", unReadList);
            setInfo(info);
        }
    }
	

	public void initMenu() throws Exception {
		String staffId = getVisit().getStaffId();
		
		//读取menu_subsys信息到cookie
		CookieUtil cookie = new CookieUtil(getRequest(),getResponse(), "TOUCHFRAME_LOGIN_COOKIE", 24*7);
		cookie.load();
		String menuSubsysCode = cookie.get("MENU_SUBSYS_CODE");
		
		List subsysList = new DatasetList();
		//读取子系统信息
		//if(subsysList==null)
		{
			subsysList=ModuleCfg.getElements("/module/subsys/*");
		}
		
		IData subsys;
		for(Object entry:subsysList){
			subsys = (IData)entry;
			//if(subsys.containsKey("cls")){
				subsys.remove("cls");
			//}
			if(menuSubsysCode != null && !"".equals(menuSubsysCode)){
				if(menuSubsysCode.equals(subsys.getString("code"))){
					subsys.put("cls", "on");
				}
			}else{
				if("true".equals(subsys.getString("default"))){
					subsys.put("cls", "on");
				}
			}
		}

		setCenters(subsysList);
		
		getHomeMenu();
	}

	public void fetchProductPage(IRequestCycle cycle) throws Exception{
		IData offfer = new DataMap();
		offfer.put("OFFER_ID", getParameter("offerId"));
		offfer.put("OFFER_TYPE", getParameter("offerType"));
		offfer.put("MGMT_DISTRICT", getVisit().getLoginEparchyCode());

		IDataset offfers = new DatasetList();
		offfers.add(offfer);

		IData data = new DataMap();
		data.put("OFFER_LIST", offfers);
		
		//接口未替换
		
/*		IDataOutput out = ServiceFactory.call("OrderCentre.person.offer.IOfferSV.getOfferDetailPage", createDataInput(data));
		IDataset offferList = out.getData();
		setAjax(offferList.first());
*/	}

	public void initGroupInfo(IRequestCycle cycle) throws Exception{
		String custManagerId = getVisit().getStaffId();//客户经理

		IData data = new DataMap();
		data.put("CUST_MANAGER_ID",custManagerId);
		
		//接口未替换
/*		IDataOutput out = ServiceFactory.call("CustomerCentre.custmgr.ICCOutQuerySV.qryGroupAndMemberCount", createDataInput(data));
		IDataset dateset = out.getData();
		IData iData = dateset.getData(0);*/
		setInfo(new DataMap());

	}
	
	public void fetchSaleStatistic(IRequestCycle cycle) throws Exception
	{
	    
	}
	
	public void getHomeMenu() throws Exception {
		IData param = new DataMap();
		param.put("STAFF_ID", "HOME"+getVisit().getStaffEparchyCode());
		
		try {
			IDataOutput out = ServiceFactory.call("SYS_Menu_QueryHomeMenus", createDataInput(param));
			IDataset menus = out.getData();
			
			if(menus != null && menus.size()>0){
				setInfos(menus);
			}
		} catch (Exception e) {
			log.error("加载首页菜单异常", e);
			setInfos(new DatasetList());
		}
	}
	
	public static String getTimeDiff(String strDate1, String strDate2) throws Exception
    {
	    if(StringUtils.isBlank(strDate1) || StringUtils.isBlank(strDate2))
        {
            return "";
        }
        Date date1 = SysDateMgr.string2Date(strDate1, SysDateMgr.PATTERN_STAND);
        Date date2 = SysDateMgr.string2Date(strDate2, SysDateMgr.PATTERN_STAND);
        
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = date2.getTime() - date1.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        
        StringBuilder timeDiffSb = new StringBuilder(100);
        timeDiffSb.append(day>0?day:0).append("天").append(hour>0?hour:0).append("小时").append(min>0?min:0).append("分钟");
        return timeDiffSb.toString();
    }
	
//	private String getStaffId(String staffId) throws Exception
//	{
//	    String staffName = "";
//	    IData param = new DataMap();
//	    param.put("STAFF_ID", staffId);
//	    IDataOutput out = ServiceFactory.call("CS.StaffInfoQrySVC.getStaffInfo", createDataInput(param));
//	    IDataset staffList = out.getData();
//	    if(IDataUtil.isNotEmpty(staffList))
//	    {
//	        staffName = staffList.first().getString("STAFF_NAME");
//	    }
//	    return staffName;
//	}
	
}