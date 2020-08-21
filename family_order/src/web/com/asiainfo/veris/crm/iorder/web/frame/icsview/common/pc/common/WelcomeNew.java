package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.pc.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizVisit;
 
import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;


public abstract class WelcomeNew extends CSBasePage {
	
	public abstract void setCenters(List types);
	public abstract void setInfos(IDataset infos);
	public abstract void setStatistic(IDataset sta);

	public abstract void setActivities(IDataset offers);
	public abstract void setCombos(IDataset offers);
	public abstract void setPhones(IDataset offers);
	public abstract void setBroadbands(IDataset offers);
	public abstract void setBroadbandSales(IDataset offers);
	

	
	public void init(IRequestCycle cycle) throws Exception{
		String staffId = getVisit().getStaffId();
		List<Map<String, String>> domains = new ArrayList<Map<String,String>>();
		setCenters(domains);
		
		getHomeMenu();
		
		IDataset sta = new DatasetList();
		IData d1 = new DataMap();
		d1.put("name", "昨天");
		d1.put("value", "D");
		
		IData d2 = new DataMap();
		d2.put("name", "本周");
		d2.put("value", "W");
		
		IData d3 = new DataMap();
		d3.put("name", "本月");
		d3.put("value", "M");
		
		sta.add(d1);
		sta.add(d2);
		sta.add(d3);
		
		setStatistic(sta);
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
		
//		ServiceResponse response = BizServiceFactory.call("OrderCentre.person.offer.IOfferSV.getOfferDetailPage", data);
//		IData re = response.getData("DATA");
//		IDataset offferList = re.getDataset("OFFER_LIST");
//		
//		setAjax(offferList.first());
	}
	
//	desc = "CONTACT_CNT|接触总量,SUCCEED_CNT|成功数量,FAIL_CNT|失败数量,CNT_NUM1|其他数量1,CNT_NUM2|其他数量2,CNT_NUM3|其他数量3,CNT_NUM4|其他数量4,CNT_NUM5|其他数量5",                 
	public void fetchSaleStatistic(IRequestCycle cycle) throws Exception{
		
		String type = getParameter("type");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		
		if("D".equals(type)){
			calendar.add(Calendar.DATE, -1);
		}else if("W".equals(type)){
			int w = calendar.get(Calendar.DAY_OF_WEEK);
			if(w > 1){
				w -= 2;
			}else{
				w = 6;
			}
			calendar.add(Calendar.DATE, -w);
		}else if("M".equals(type)){
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String beginDate = format.format(calendar.getTime()); 
		beginDate += " 00:00:00";
		
		IData data = new DataMap();
		data.put("STAT_TYPE",type);
		data.put("STAT_DATE", beginDate);
		data.put("STAFF_ID", getVisit().getStaffId());
		
		/*IData re = ServiceClient.call("MarketingCentre.job.IJobOutQuerySV.queryStaffStat", data);
		
		IDataset set = re.getDataset("OUTDATA");
		
		setAjax(set.first());*/
	}	
	 
	/*
	 * 
	 * 
    @PART(name = "OFFER_ID", desc = "商品编码",  type = PartType.STRING),
    @PART(name = "OFFER_NAME", desc = "商品名称",  type = PartType.STRING),
    @PART(name = "OFFER_TYPE", desc = "商品类型",  type = PartType.STRING),
    @PART(name = "BRAND", desc = "品牌",  type = PartType.STRING),
    @PART(name = "DESCRIPTION", desc = "商品描述",  type = PartType.STRING),
    @PART(name = "PRICE", desc = "营销价格",  type = PartType.STRING),
    @PART(name = "TAG_ID", desc = "标签规格编码",  type = PartType.STRING),
    @PART(name = "TAG_NAME", desc = "商品编码",  type = PartType.STRING),
    @PART(name = "MIN_PIC_URL", desc = "小图地址",  type = PartType.STRING)
	 */
	public void fetchOffers(IRequestCycle cycle) throws Exception{
		
		String offerType = getParameter("OFFER_TYPE");
		IData data = new DataMap();
		data.put("TYPE", offerType);// "查询类型（1.活动 2.套餐 3.手机 4.宽带套餐 5.宽带营销活动））",
		data.put("MGMT_DISTRICT", getVisit().getLoginEparchyCode());
		data.put("START", getParameter("START"));
		data.put("END", getParameter("END"));
//		
//		ServiceResponse response = BizServiceFactory.call("UnifyProductCentre.offer.IOfferQueryFSV.queryHotOffer", data);
//		IData re = response.getData("DATA");
//		IDataset offers = re.getDataset("DATAS");
//
//		if("1".equals(offerType)){
//			setActivities(offers);
//		}else if("2".equals(offerType)){
//			setCombos(offers);
//		}else if("3".equals(offerType)){
//			setPhones(offers);
//		}else if("4".equals(offerType)){
//			setBroadbands(offers);
//		}else if("5".equals(offerType)){
//			setBroadbandSales(offers);
//		}
	}
	
	/**
	 * 营销案查询
	 * @param cycle
	 * @throws Exception
	 */
	public void fetchActions(IRequestCycle cycle) throws Exception{
		IData data = new DataMap();
		data.put("ORDER_TYPE", "CAMPN_URGENCY");
		data.put("REGION_ID", getVisit().getLoginEparchyCode());
		data.put("START_NUM", "1");
		data.put("END_NUM", "20");
		
//		ServiceResponse response = BizServiceFactory.call("MarketingCentre.campn.IMktCampnOutSV.queryBsCampnIcon", data);
//		IData re = response.getData("DATA");
//		IDataset offers = re.getDataset("OUTDATA");
//		setActivities(offers);
	}	
	
	public void getHomeMenu() throws Exception {
		BizVisit visit = getVisit();
		String region_id = visit.getStaffEparchyCode();
		String oper_id = visit.getStaffId();
		String tab_id = "0";//首页常用菜单用0来标识

		IData cond = new DataMap();
		cond.put("REGION_ID", region_id);
		cond.put("OPERATOR_ID", oper_id);
		cond.put("TAB_ID", tab_id);

		/*try {
			IData result = this.call("CommonBusinessCentre.secframe.op.IOpMenuSV.getHomeMenu",cond);
			IDataset datas1 = result.getDataset("DATAS");
			IDataset list = new DatasetList();
			for (int j = 0; j < datas1.size(); j++) {
				IData data = datas1.getData(j);
				data.put("MENU_ID", data.getString("FUNC_ID"));
				list.add(data);
			}
			IDataset datas = PrivCheckUtil.checkMenus(oper_id, list, false);
			setInfos(datas);
		} catch (Exception e) {
			log.error("加载首页菜单异常", e);
			setInfos(new DatasetList());
		}*/
	}

 
}