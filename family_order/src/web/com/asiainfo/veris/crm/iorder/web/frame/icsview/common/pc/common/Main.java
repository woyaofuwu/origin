package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.pc.common;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageEvent;

import com.ailk.biz.BizConstants;
import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.ailk.web.util.CookieUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;


public abstract class Main extends CSBasePage {

    public abstract void setSaleInfo(IData data);
	public abstract void setEparchies(IDataset eparchies);
	public abstract void setWelcomePage(String page);
	public abstract void setIsManager(String i);
	public abstract void setIsProv(String i);
	
	public abstract void setFuncs(IDataset funcs);
	public abstract void setReports(IDataset reports);
	public abstract void setGoods(IDataset goods);
	public abstract void setRewards(IDataset rewards);
	
	public abstract void setNavShow(String i);
	public abstract void setMsgShow(String i);
	
	@Override
	public void pageBeginRender(PageEvent evt){
		super.pageBeginRender(evt);
		
		String welcomePage = null;
		
		//测试esop
        String loginChannel = "";
        if("HN073108".equals(getVisit().getStaffId())){
            loginChannel = "2";
        }
        
        if("2".equals(loginChannel))
        {
            welcomePage = "?service=page/touchframe.frame.pc.esop.Welcome&listener=init";
            setIsManager("1");
        }
        else
        {
            welcomePage = "?service=page/frame.pc.common.WelcomeNew&listener=init";
            setIsManager("0");
        }
		
	 
		setWelcomePage(welcomePage);
		
	}
	
	public void init(IRequestCycle cycle) throws Exception{
		/*if(PrivCheckUtil.checkDom(getVisit().getStaffId())){//全省工号
			//IData result = ServiceClient.call("BusiFrame.base.IBaseSV.getRegion", new DataMap());
			IData result = ServiceClient.call("quickstart.frame.login", new DataMap());
			IDataset ds = result.getDataset("INFOS");
			setEparchies(ds);
		}else{
			setIsProv("0");
		}*/
		setIsProv("1");
		

    	String navShow = null;
    	CookieUtil cookie1 = new CookieUtil(getRequest(),getResponse(), "NGBOSS_NAVHELP_COOKIE", 24*30*12*10);
		if( cookie1.load() ){
			navShow = cookie1.get("NAVHELP_REM");
		}
		String msgShow = null;
		CookieUtil cookie2 = new CookieUtil(getRequest(),getResponse(), "NGBOSS_MSGTIP_COOKIE", 24*30*12*10);
		if( cookie2.load() ){
			msgShow = cookie2.get("MSGTIP_REM");
		}
		setNavShow(navShow);
		setMsgShow(msgShow);
		
	}
	
	public void changeLoginEparchy(IRequestCycle cycle) throws Exception{
		String eparchyCode = getParameter("eparchyCode");
		String eparchyName = getParameter("eparchyName");
		if(StringUtils.isBlank(eparchyCode)||StringUtils.isBlank(eparchyName)){
			pageutil.error("地州不能为空！");
		}
		/*if(PrivCheckUtil.checkDom(getVisit().getStaffId())){//全省工号
			Visit visit = getVisit();
			visit.setStaffEparchyCode(eparchyCode);
			visit.setStaffEparchyName(eparchyName);
			visit.setLoginEparchyCode(eparchyCode);
			visit.setLoginEparchyName(eparchyName);
			setVisit(visit);
			IData data = new DataMap();
			data.put("eparchyName", eparchyName);
			setAjax(data);
		}*/
	}
	
	public void fetchFee(IRequestCycle cycle) throws Exception{
//		IData p = new DataMap();
//		p.put("OP_ID", getVisit().getStaffId());
//
//		ServiceResponse response = BizServiceFactory.call("AM.crm.queryBusiByOpId",p);
//		IData result = response.getData("DATA");
//		IDataset set = result.getDataset("OUTDATA");
//		long m = 0,d = 0;
//		if(set.size()>0){
//			IData r = set.getData(0);
//			m = Long.parseLong(r.getString("MONTH_TOTAL"));
//			d = Long.parseLong(r.getString("DAY_TOTAL"));
//		}
//		//分转元
//		double mm = m/100.0;
//		double dd = d/100.0;
//		
//		BigDecimal mmm = new BigDecimal(mm).setScale(2,BigDecimal.ROUND_HALF_UP);
//		BigDecimal ddd = new BigDecimal(dd).setScale(2,BigDecimal.ROUND_HALF_UP); 
//		
//		IData data = new DataMap();
//		data.put("MONTH_TOTAL", mmm.toString());
//		data.put("DAY_TOTAL", ddd.toString());
//		setAjax(data);
	}
	
	public void addFavoMenu(IRequestCycle cycle) throws Exception{
//		
//		IData data = new DataMap();
//		data.put("OPERATOR_ID", getVisit().getStaffId());
//		data.put("REGION_ID", getVisit().getLoginEparchyCode());
//		data.put("FUNC_ID", getParameter("menuId","").trim());
//		data.put("ENTITY_ID", getParameter("type","").trim());//0 menu  1 offer
//		
//		BizServiceFactory.call("CommonBusinessCentre.secframe.op.IOpMenuSV.addCollectMenu", data);
	}
	
	public void checkStaffPwd(IRequestCycle cycle) throws Exception{
	}
	
	/**
	 * 导航帮助标志
	 * @param cycle
	 * @throws Exception
	 */
	public void tipAction(IRequestCycle cycle) throws Exception{
		
		String operType  = getParameter("operType");
		
		if("0".equals(operType)){//查询
			CookieUtil cookie = new CookieUtil(getRequest(),getResponse(), "NGBOSS_NAVHELP_COOKIE", 24*30*12*10);
			cookie.put("NAVHELP_REM", "1");
			cookie.store();
		}else if("1".equals(operType)){//修改 设置为不在显示
			CookieUtil cookie = new CookieUtil(getRequest(),getResponse(), "NGBOSS_MSGTIP_COOKIE", 24*30*12*10);
			cookie.put("MSGTIP_REM", "1");
			cookie.store();
		}
	}

	public void initSn(IRequestCycle cycle)throws Exception{
		IData data = this.getData();
		if("".equals(data.getString("ACCESS_NUM",""))){
			return;
		}
		setSaleInfo(data);
	}

	/**
	 * 工单提交
	 * @param cycle
	 * @throws Exception
	 */
	public void submitSale(IRequestCycle cycle)throws Exception{
//		IData data  = getData();
//		if(!data.containsKey("SERIAL_NUMBER")||!data.containsKey("ACT_ID")){
//            return;
//		}
//		data.put("STAFF_ID",getVisit().getStaffId());
//		data.put("EXEC_CHNL","T01");//经分约定营业厅编码
//			//调用接触式营销接口
//		BizServiceFactory.call("MarketingCentre.job.IJobQuerySV.submitTouchSale", data);

	}
	
	public void fetchProductPage(IRequestCycle cycle) throws Exception{
//		IData offfer = new DataMap();
//		offfer.put("OFFER_ID", getParameter("offerId"));
//		offfer.put("OFFER_TYPE", getParameter("offerType"));
//		offfer.put("MGMT_DISTRICT", getVisit().getLoginEparchyCode());
//		
//		IDataset offfers = new DatasetList();
//		offfers.add(offfer);
//		
//		IData data = new DataMap();
//		data.put("OFFER_LIST", offfers);
//		
//		ServiceResponse response = BizServiceFactory.call("OrderCentre.person.offer.IOfferSV.getOfferDetailPage", data);
//		IData re = response.getData("DATA");
//		
//		IDataset offferList = re.getDataset("OFFER_LIST");
//		
//		setAjax(offferList.first());
	}
    
	
	/**
	 * 工单日记保存
	 * @param cycle
	 * @throws Exception
	 */
	public void saveSaleLog(IRequestCycle cycle)throws Exception{
//		IData data  = getData();
//		if(!data.containsKey("SERIAL_NUMBER")) {
//            return;
//		}
//		data.put("TOUCH_TYPE", "2");
//		data.put("OPER_TYPE", "3");
//		data.put("CHNL_TYPE", "T000");
//		data.put("STAFF_ID",getVisit().getStaffId());
//        try {
//			//调用接触式营销接口
//        	BizServiceFactory.call("MarketingCentre.job.ITouchJobLogOperateSV.addTouchJobLog", data);
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
	}	
	
	@Override
	protected void cleanupAfterRender(IRequestCycle cycle){
		super.cleanupAfterRender(cycle);
		
		setSaleInfo(null);
		setEparchies(null);
		setWelcomePage(null);
	}

	public void getCollectMenu(IRequestCycle cycle) throws Exception {
//		BizVisit visit = getVisit();
//		String region_id = visit.getStaffEparchyCode();
//		String oper_id = visit.getStaffId();
//		String fav_type = "1";
//
//		for (int i = 0; i < 4; i++) {
//			IData cond = new DataMap();
//			cond.put("REGION_ID", region_id);
//			cond.put("OPERATOR_ID", oper_id);
//			cond.put("FAV_TYPE", fav_type);
//			cond.put("TAB_ID", String.valueOf(i));
//			ServiceResponse response = BizServiceFactory.call("CommonBusinessCentre.secframe.op.IOpMenuSV.getCollectMenu",cond);
//			IData result = response.getData("DATA");
//			IDataset datas = result.getDataset("DATAS");
//			for (int j = datas.size() -1; j > -1; j--) {
//				IData data = datas.getData(j);
//				if(i == 2){
//					data.put("OFFER_ID", data.getString("FUNC_ID"));
//					IDataset set = getOfferInfo(data.getString("FUNC_ID"));
//					if(set!=null && set.size()>0){
//						data.put("OFFER_TYPE", set.first().getString("OFFER_TYPE"));
//						data.put("OFFER_NAME", set.first().getString("OFFER_NAME"));
//					}else{
//						datas.remove(j);
//					}
//				}else{
//					data.put("MENU_ID", data.getString("FUNC_ID"));
//				}
//			}
//			if(i == 2){
//				setGoods(datas);
//			}else{
//				/*datas = PrivCheckUtil.checkMenus(oper_id, datas, false);
//				if (i == 0) {
//					setFuncs(datas);
//				} else if (i == 1) {
//					setReports(datas);
//				} else if (i == 3) {
//					setRewards(datas);
//				}*/
//			}
//		}
	}
  
	public void deleteCollect(IRequestCycle cycle) throws Exception{
		String favId=getParameter("favId");
		BizVisit visit = getVisit();

		IData cond = new DataMap();
		cond.put("MENU_FAV_ID", favId);
		cond.put("OPERATOR_ID", visit.getStaffId());
		cond.put("REGION_ID", visit.getStaffEparchyCode());
//		BizServiceFactory.call("CommonBusinessCentre.secframe.op.IOpMenuSV.deleteCollectMenu", cond);
	}


	public IDataset getOfferInfo(String offer_id) throws Exception{
		Map<String,String> cond = new HashMap<String,String>();
		cond.put("OFFER_ID", offer_id);
		SearchResponse proRes = SearchClient.search("PM_OFFER", null, cond, 0, 10);
		IDataset re = proRes.getDatas();

		return re;
	}
	 
	 
	 /**
	  * 跨省工单地址解析处理
	  * @param cycle
	  * @throws Exception
	  */
	 public void getProvinceUrl(IRequestCycle cycle) throws Exception {
//	   String tag =  this.getData().getString("tag");
//	    
//	   String staffId = getVisit().getStaffId();
//       IData data = new DataMap();
//       data.put("STAFF_ID", staffId);
//       ServiceResponse response = BizServiceFactory.call("CommonBusinessCentre.secframe.orgstaffmgt.IOrgStaffQuerySV.querySatffBbossBySatffId", data);
//       IData result = response.getData("DATA");
//       IDataset staffInfo = result.getDataset("INFOS");
//       if (staffInfo.size() == 0) {
//           pageutil.abort("工号" + staffId + "在系统中找不到对应的总部ESOP系统工号，请确认！");
//       }
//       String staffNo = staffInfo.getData(0).getString("STAFF_NUMBER");
//       String url = getUrl(tag, staffNo);
//       Map map = new HashMap();
//       map.put("URL", url);
//       setAjax(map);
    }
    	
    private String getUrl(String tag,  String staffNumber ) throws Exception {
      
//        IData data = new DataMap();
//        data.put("STAFF_NO", staffNumber);
//        
//        ServiceResponse response = BizServiceFactory.call("CommonBusinessCentre.proviwork.IProviWorkSV.queryProviWork", data);
//        IData result = response.getData("DATA");
//        IDataset idata = result.getDataset("OUTDATA");
//        IData staffInfo = idata.getData(0);
//        
//        String beginUrl =  staffInfo.getString("URL");
//        String currentTime = staffInfo.getString("TIME"); 
//        String staff_no = staffInfo.getString("STAFF_NO");
//        String url = beginUrl+"?tag="+tag+"&companyNumber=871&UserName="+staff_no+"&TimeStamp="+currentTime;
//        return url;
    	return "";
    }
}
