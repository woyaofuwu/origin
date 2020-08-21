package com.asiainfo.veris.crm.order.web.person.coupons;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList; 
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SpendCoupons extends PersonBasePage
{ 
	/**
	 * 查询用户优惠券信息
	 * */
    public void queryUserTicketList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        Pagination page = getPagination("recordNav");
        String userInfo=data.getString("USER_INFO", "");
        String serialNum="";
        String userId="";
        if(!"".equals(userInfo)){
        	IData userData = new DataMap(userInfo);
        	serialNum=userData.getString("SERIAL_NUMBER","");
            userId=userData.getString("USER_ID","");
        }else{
        	serialNum=data.getString("AUTH_SERIAL_NUMBER", "");
        	userId=data.getString("USER_ID","");
        }
        
        
        IData inparam=new DataMap();
        inparam.put("SERIAL_NUMBER", serialNum); 
        inparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        //取该用户的优惠券信息TL_B_USER_COUPONS
        IDataOutput result = CSViewCall.callPage(this, "SS.CouponsTradeSVC.getUserTicketInfos", inparam, page); 
        
        //回传优惠券信息
        long dataCount=result.getDataCount();
        setRecordCount(dataCount);
        IDataset tikset=result.getData();
        if(tikset!=null &&tikset.size()>0){
        	for(int k=0;k<tikset.size();k++){
        		String state=tikset.getData(k).getString("TICKET_STATE");
        		if("0".equals(state)){
        			state="未使用";
        		}else if("1".equals(state)){
        			state="已使用";
        		}
        		String dateState=tikset.getData(k).getString("DATE_STATE");
        		String dateStateDesc="";
        		if("EXP".equals(dateState)){
        			dateStateDesc="已过期";
        		}else{
        			dateStateDesc="有效";
        		}
        		tikset.getData(k).put("TICKET_STATE_DESC", state);
        		tikset.getData(k).put("DATE_STATE_DESC", dateStateDesc);
        		tikset.getData(k).put("USER_ID", userId);
        	}
        }
        setInfos(result.getData());
    }
	
	
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    { 
    	IData data = getData();
    	IDataset tikInfos = new DatasetList(data.getString("TICKET_INFO", ""));
    	IData tikInfo=new DataMap();
    	if(tikInfos!=null && tikInfos.size()>0){
    		tikInfo=tikInfos.getData(0);
    	}
        String routeId = data.getString("EPARCHY_CODE");
        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
        	tikInfo.put("EPARCHY_CODE", "0898");
        }
        data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.CouponsRegSVC.tradeReg", tikInfo);
        setAjax(dataset);
    } 
    
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount); 
}