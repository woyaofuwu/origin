package com.asiainfo.veris.crm.order.web.person.onecardmultino;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OneCardMultiFuCard  extends PersonBasePage
{
	public abstract void setRelation(IData relationList);
	/**
	 * 根据副号码查询主号码信息
	 * @param cycle
	 * @throws Exception
	 */
	public void qryMainCardInfoByFuCard(IRequestCycle cycle) throws Exception {
	     IData servicelist = (IData) CSViewCall.call(this,"SS.OneCardMultiFuCardSVC.qryMainCardInfo", getData()).get(0);
	     this.setRelation(servicelist);
	}
	/**
	 * 根据副号码查询副号码信息
	 * @param cycle
	 * @throws Exception
	 */
	public void qryFuCardInfoByFuCard(IRequestCycle cycle) throws Exception {
	     IData servicelist = (IData) CSViewCall.call(this,"SS.OneCardMultiFuCardSVC.qryFuCardInfo", getData()).get(0);
	     this.setRelation(servicelist);
	}	
}
