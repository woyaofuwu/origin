package com.asiainfo.veris.crm.order.web.person.valuecard;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


/**
 * 有价卡赠送,无客户号码
 * @author zyz
 *
 */
public abstract class GiveValueCardNotPhone extends PersonBasePage{

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setListInfos(IDataset listInfos);

    public abstract void setPageCount(long count);
    
    public static Logger logger=Logger.getLogger(GiveValueCardNotPhone.class);
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryValueCardInfo(IRequestCycle cycle) throws Exception
    {
    	 try {
        	 Pagination page = getPagination("recordNav");
             IData data = getData("cond", true);
             data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());       
             IDataOutput result = CSViewCall.callPage(this, "SS.GiveValueCardNotPhoneSVC.queryValueCardNotPhoneInfo", data, page);
             setListInfos(result.getData());
             setCondition(data);
             setPageCount(result.getDataCount());
             setAjax("DATA_COUNT", "" + result.getData().size());
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e.getMessage());
			throw e;
		}

    }
    /**
     * 提交业务
     * @param cycle
     * @throws Exception
     */
    public void submitValueCardInfo(IRequestCycle cycle) throws Exception{
    	try {
    		 IData  data=getData();
    		 IDataset iDataList=CSViewCall.call(this,"SS.GiveValueCardNotPhoneSVC.updateValueCardNotPhoneInfoByCond", data);
    		 setAjax(iDataList);
		} catch (Exception e) {
			CSViewException.apperr(CrmCommException.CRM_COMM_103,e.getMessage());
		}
    }
    
    /**
     * 有价卡补录信息导入
     * @param cycle
     * @throws Exception
     */
    public void  importValueCardNotPhoneinfo(IRequestCycle cycle) throws Exception{
    	try {
			IData data=getData();
			//获取excel信息
    	    IDataset reuslt=CSViewCall.call(this, "SS.GiveValueCardNotPhoneSVC.importValueCardNotPhoneinfo", data);
    	    
    		setAjax(reuslt);
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
    
    /**
     * 初实化，用户权限
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception{
   	 try {
         IData data =new DataMap();
         boolean isValueCradRightPriv = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "isValueCradRight");
         /**
          * 判断是否具有权限
          */
         if (isValueCradRightPriv)
         {
            //有权限
        	 data.put("cond_CITY_CODE","");
        	 data.put("cityDisabledFlag", false);
        	 //城市编码
        	 data.put("cond_UPDATE_DEPART_ID","");
        	 data.put("departDisabledFlag", false);
         }else{
        	//无权限
        	 //部门编码 
        	 data.put("cond_CITY_CODE", this.getVisit().getCityCode());
        	 data.put("cityDisabledFlag", true);
        	 //城市编码
        	 data.put("cond_UPDATE_DEPART_ID", this.getVisit().getDepartCode());
        	 data.put("departDisabledFlag", true);
        	 
         }
         setCondition(data);
	} catch (Exception e) {
		// TODO: handle exception
		if(logger.isInfoEnabled()) logger.info(e.getMessage());
		throw e;
	  }
    }   
}
