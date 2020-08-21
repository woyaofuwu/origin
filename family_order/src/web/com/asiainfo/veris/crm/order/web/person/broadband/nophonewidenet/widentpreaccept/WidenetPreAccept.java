
package com.asiainfo.veris.crm.order.web.person.broadband.nophonewidenet.widentpreaccept;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidenetPreAccept extends PersonBasePage
{
   /**
    * 查询预受理信息
    * @param clcle
    * @throws Exception
    */
    public void loadInfo(IRequestCycle clcle) throws Exception
    {
    	IData param = getData();
		IDataOutput infos = CSViewCall.callPage(this, "SS.WidenetPreAcceptSVC.loadInfo", param, getPagination("navt"));
		IDataset info = infos.getData();
    	if(IDataUtil.isNotEmpty(info)){
    		setAjax(info.first());
    		setCount(infos.getDataCount());
    	}
        setInfos(info);
        setCommInfo(info.first());
    }
    
    /**
     * 查询无手机宽带账号信息
     * @param clcle
     * @throws Exception
     */
     public void loadWidenetInfo(IRequestCycle clcle) throws Exception
     {
     	IData param = getData();
     	IDataset infos = CSViewCall.call(this, "SS.WidenetPreAcceptSVC.loadWidenetInfo", param);
     	setAjax(infos);
         setInfos(infos);
         setCommInfo(infos.first());
     }
    
    /**
     * 撤单
     * @param clcle
     * @throws Exception
     */
    public void cancelPreTrade(IRequestCycle clcle) throws Exception
    {
    	IData param = getData();
    	IDataset infos = CSViewCall.call(this, "SS.WidenetPreAcceptSVC.cancelPreTrade", param);
    	setAjax(infos);
        setInfos(infos);
    }
    
    /**
     * 新增外呼记录
     * @param clcle
     * @throws Exception
     */
    public void updatePreTrade(IRequestCycle clcle) throws Exception
    {
    	IData param = getData();
    	IDataset infos = CSViewCall.call(this, "SS.WidenetPreAcceptSVC.updatePreTrade", param);
    	setAjax(infos);
    	setInfos(infos);
    }
    
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setCommInfo(IData info);
    
    public abstract void setCount(long count);
}
