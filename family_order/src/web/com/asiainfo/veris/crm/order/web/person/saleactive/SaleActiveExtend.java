
package com.asiainfo.veris.crm.order.web.person.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SaleActiveExtend extends PersonBasePage
{
	
	/**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
    	IData pageParam = getData();
        String nowDate = SysDateMgr.getSysDate();
        //pageParam.put("cond_START_DATE", nowDate);
        pageParam.put("cond_END_DATE", nowDate);
        String strStaffId = getVisit().getStaffId();
        boolean bExtend = StaffPrivUtil.isFuncDataPriv(strStaffId, "SALEACTIVE_EXTEND");
        pageParam.put("PRODT_FLAG", !bExtend);
        this.setCondition(pageParam);
    }
	
    public void loadInfo(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
    	IData userInfo = new DataMap(pageData.getString("USER_INFO", ""));
    	/*IData acctInfo = new DataMap(pageData.getString("ACCT_INFO", ""));
    	IData custInfo = new DataMap(pageData.getString("CUST_INFO", ""));*/
    	IData Param = new DataMap();
    	Param.put("USER_ID", userInfo.getString("USER_ID"));
    	Param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
    	Param.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
    	IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveExtendSVC.queryExtendSaleActives", Param);   	
    	setInfos(saleActives);
    }
    
    public void getSaleActiveExtend(IRequestCycle cycle) throws Exception
    {
    	IData pageParam = getData();
        String strEndDate = pageParam.getString("END_DATE", "");
        String strDayCount = pageParam.getString("DAY_COUNT", "0");
        String strProdtflag = pageParam.getString("PRODT_FLAG", "0");
        if( "0".equals(strProdtflag) ){
        	strEndDate = SysDateMgr.endDateOffset(strEndDate, strDayCount, "0");
            pageParam.put("cond_END_DATE", strEndDate);
            pageParam.put("PRODT_FLAG", true);
            //String strStaffId = getVisit().getStaffId();
            //boolean bExtend = StaffPrivUtil.isFuncDataPriv(strStaffId, "SALEACTIVE_EXTEND");
        }else{
            pageParam.put("cond_END_DATE", strEndDate);
            pageParam.put("PRODT_FLAG", false);
        }
        this.setCondition(pageParam);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	String strEndDate = data.getString("END_DATE") + " 23:59:59";
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        param.put("RELATION_TRADE_ID", data.getString("RELATION_TRADE_ID"));
        param.put("REMARK", data.getString("REMARK"));
        param.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        param.put("END_DATE", strEndDate);
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveExtendSVC.tradeReg", param);
        setAjax(saleActives);
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfos(IDataset infos);
}
