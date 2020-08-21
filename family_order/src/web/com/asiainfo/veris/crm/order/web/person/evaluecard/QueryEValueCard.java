package com.asiainfo.veris.crm.order.web.person.evaluecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryEValueCard extends PersonBasePage
{
	public abstract void setCond(IData cond);
	
	public abstract void setCardInfos(IDataset infos);

	public abstract void setPayinfos(IDataset infos);
	
	/**
     * 卡信息及充值记录查询
     * add by huping 20161009
     * @param cycle
	 * @throws Exception
     */
    public void queryCardInfoAndPayInfo(IRequestCycle cycle) throws Exception
    {
       IData data = this.getData();
       data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
       IDataset infos = CSViewCall.call(this, "SS.TelValueCardSVC.QueryValueCardInfo", data);
       if(infos != null){
    	   IDataset payinfos = infos.getData(0).getDataset("PAYED_INFO");
           IDataset cardinfos = infos.getData(0).getDataset("CARD_INFO");
           this.setPayinfos(payinfos);
           this.setCardInfos(cardinfos);
       }
       if (infos != null && !infos.isEmpty() && "0000".equals(infos.getData(0).getString("X_RSPCODE"))) {
    	   data.put("IS_SUCCESS", "0");
	   }else{
		   data.put("IS_SUCCESS", "1");
		   data.put("RESULT_INFO",infos.getData(0).getString("X_RESULTINFO"));
	   }
       this.setAjax(data);
    }
}
