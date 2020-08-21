
package com.asiainfo.veris.crm.order.web.person.goodsapply;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GoodsApply extends PersonBasePage
{
	/**
	 * 查询用户信息
	 * 查询用户礼品信息
	 * */
    public void queryUserScoreGoods(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        Pagination page = getPagination("recordNav");
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
     // 获取子业务资料
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        inparam.put("USER_ID", userInfo.getString("USER_ID"));
        inparam.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
        
        //取客户信息、积分信息
        IDataset custInfos = CSViewCall.call(this, "SS.ScoreExchangeSVC.getCommInfo", inparam);
       
        //回传客户信息
        IData comminfo = new DataMap();
        IData temp = custInfos.getData(0);
        comminfo.putAll(temp.getData("COMMINFO"));
        comminfo.put("EPARCHY_NAME", userInfo.getString("EPARCHY_NAME"));
        comminfo.put("CUST_NAME", data.getString("CUST_NAME"));
 
        setCommInfo(comminfo);
        queryUserScoreGoodsList( cycle); 
    }
    
    /**
     * 领取礼品，更新数据
     * */
    public void exchangeGoods(IRequestCycle cycle)throws Exception{
    	IData data = getData();
    	
    	data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
    	data.put("UPDATE_STAFF_ID", getVisit().getStaffId());
    	data.put("UPDATE_DEPART_ID", getVisit().getDepartId());
    	 
    	IDataset dataset = CSViewCall.call(this, "SS.GoodsApplySVC.exchangeGoods", data);
        setAjax(dataset.getData(0));
    }
    
    /**
	 * 查询用户礼品信息
	 * */
    public void queryUserScoreGoodsList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        Pagination page = getPagination("recordNav");
        //String userId=data.getString("USER_ID","");
        IData inparam = new DataMap();
       /* if("".equals(userId)){
        	IData userInfo = new DataMap(data.getString("USER_INFO", "")); 
            inparam.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            inparam.put("USER_ID", userInfo.getString("USER_ID"));
        }else{
        	inparam.put("USER_ID", userId);
        }*/
        inparam.put("SERIAL_NUMBER", data.getString("HID_SERIAL_NUM"));
        
        inparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        //取该用户的礼品信息TL_B_USER_SCORE_GOODS  
        IDataOutput result = CSViewCall.callPage(this, "SS.GoodsApplySVC.getUserGoodsInfos", inparam, page); 
        
        //回传礼品信息
        long dataCount=result.getDataCount();
        setRecordCount(dataCount);
        IDataset goodset=result.getData();
        if(goodset!=null &&goodset.size()>0){
        	for(int k=0;k<goodset.size();k++){
        		String state=goodset.getData(k).getString("STATE");
        		if("0".equals(state)){
        			state="未领取";
        		}else if("1".equals(state)){
        			state="已领取部分";
        		}else if("2".equals(state)){
        			state="全部领取完成";
        		}else if("3".equals(state)){
        			state="已返销";
        		}
        		String dateState=goodset.getData(k).getString("DATE_STATE");
        		String dateStateDesc="";
        		if("EXP".equals(dateState)){
        			dateStateDesc="已过期";
        		}else{
        			dateStateDesc="有效";
        		}
        		goodset.getData(k).put("GOODS_STATE", state);
        		goodset.getData(k).put("DATE_STATE_DESC", dateStateDesc);
        	}
        }
        setInfos(result.getData());
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount); 
    
    public abstract void setCommInfo(IData info);
}
