package com.asiainfo.veris.crm.iorder.web.family.destroy;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 家庭业务注销
 * @author wuwangfeng
 */
public abstract class FamilyTradeDestroy extends PersonBasePage {

	public abstract void setMemberInfos(IDataset memberInfos);
	
	/**
	 * 加载家庭成产品和员信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void afterAuthLoadChildInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IDataset results = CSViewCall.call(this, "SS.FamilyDestroySVC.loadFamilyMember", data);
		IData result = results.first();
		
		String productId = result.getString("PRODUCT_ID");
		String productName = result.getString("PRODUCT_NAME");
		IDataset memberList = result.getDataset("MEMBER_LIST");
		
		IData ajax = new DataMap();
		ajax.put("FAMILY_SN", result.getString("FAMILY_SN"));
		ajax.put("EPARCHY_CODE", result.getString("EPARCHY_CODE"));
		ajax.put("PRODUCT_NAME", productName + "【" + productId + "】");				
		setAjax(ajax);
		
		setMemberInfos(memberList);
	}

	/**
	 * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {		
		IData data = this.getData();
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
    	
    	IDataset result = CSViewCall.call(this, "SS.FamilyDestroyRegSVC.tradeReg", data);
        setAjax(result);
	}

	/**
	 * 
	 * 方法功能说明：随心用退订恢复产品查询
	 * 创建时间：2019年5月8日
	 * 开发者：liulei5
	 * @参数： @param cycle
	 * @参数： @throws Exception
	 * @return void
	 * @throws
	 */
//	public void destroyRecoveProduct(IRequestCycle cycle) throws Exception {
//		IData data = getData();
//		data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
//		IData info = CSViewCall.callone(this, "SS.FamilyPackageTradeViewSVC.destroyRecoveProduct", data);
//		setAjax(info);
//	}
	
	
//	public IDataset getSelectOptions(String relationTypeCode) throws Exception {
//		IDataset result = new DatasetList();
//		IDataset list =pageutil.getStaticList("FAMILYDROP_PRODUCT");
//		if(!"FT".equals(relationTypeCode)){
//			return list;
//		} else {
//			for (int i = 0; null!=list && i < list.size(); i++) {
//				if("2".equals(list.getData(i).getString("DATA_ID"))){
//					IData list2 = new DataMap();
//					list2.put("DATA_NAME", "2-不变更产品");
//					list2.put("DATA_ID", "2");
//					result.add(list2);
//				}
//			}
//		}
//		return result;
//	}

}

