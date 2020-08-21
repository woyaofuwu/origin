package com.asiainfo.veris.crm.order.web.person.suppliercharge;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
public abstract class MobileShopManager extends PersonBasePage{
	
	public void onInitTrade(IRequestCycle cycle) throws Exception{
		IData param = this.getData();
//		param.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
		IDataset dataset = CSViewCall.call(this, "SS.MobileShopManagerSvc.querySuppInfo", param);
		if(dataset != null && dataset.size() > 0){
			IData data = dataset.getData(0);
			this.setSellSupp(data.getDataset("sellSupp"));
			this.setTermSupp(data.getDataset("termSupp"));
			this.setCityList(data.getDataset("cityList"));
			this.setFeeItemList(data.getDataset("feeItemList"));
		}
    }
	
	
	
	/*
	 * 查询
	 */
	public void queryMobileShopInfo(IRequestCycle cycle) throws Exception
	{
		IData param = getData("query", true);
		String startData = param.getString("START_DATE");
		String endData = param.getString("END_DATE");
		startData=startData.replace("-", "");
		endData=endData.replace("-", "");
		param.put("START_TIME", startData);
		param.put("END_TIME", endData);
		IDataOutput output = CSViewCall.callPage(this, "SS.MobileShopManagerSvc.queryMobileShopInfo", param, getPagination());

		setCondition(param);
	    setInfos(output.getData());
	    setCount(output.getDataCount());
	}
	
	/*
	 * 删除
	 */
	public void deleteMobileInShop (IRequestCycle cycle) throws Exception
	{
		IData param = getData();
		
        IDataset dataset = CSViewCall.call(this, "SS.MobileShopManagerSvc.deleteMobileInShop", param);
        IData retData = dataset.getData(0);
        if ("Y".equals(retData.getString("SUCC_TAG")))
        {
        }
        else
        {
        }
	}
	
	public void mobileShopInsert(IRequestCycle cycle) throws Exception
	{
		IData param = getData("cond",true);
		
		IDataset dataset =CSViewCall.call(this, "SS.MobileShopManagerSvc.mobileShopInsert", param);
		IData retData = dataset.getData(0);
		log.debug("--------------------------------------"+retData.toString());
        if ("Y".equals(retData.getString("SUCC_TAG")))
        {
//            setAjaxMsg("插入成功");
        }
        else
        {
//            setAjaxMsg(msgError, "插入失败");
        }
	}
	
	
	

	 public abstract void setCondition(IData condition);

	 public abstract void setCount(long count);

	 public abstract void setInfo(IData info);
	    
	 public abstract void setInfos(IDataset infos);
	 public abstract void setSellSupp(IDataset infos);
	 public abstract void setTermSupp(IDataset infos);
	 public abstract void setCityList(IDataset infos);
	 public abstract void setFeeItemList(IDataset infos);
	    

}
