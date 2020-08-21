/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.terminalAfterSales;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


/**
 * 进度查询接口
 * @author zyz
 *
 */
public abstract class AfterSalesSchedule extends PersonBasePage
{
	static  Logger logger=Logger.getLogger(AfterSalesSchedule.class);
	
    public void qryScheduleInfo(IRequestCycle cycle) throws Exception
    {

        IData data = this.getData("cond", true);
        Pagination page = getPagination("pageNav");
       
        String serial_number=data.getString("MOBILEPHONE", "");
       
        String type=data.getString("TYPE");
        
        if(!"".equals(serial_number)||serial_number!=null)
        {
            data.put("mobilephone", serial_number);
        }
        if(!"".equals(type)||type !=null){
        	
        	 data.put("type", type);
        }
        
        IDataOutput result= CSViewCall.callPage(this, "SS.AfterSalesSVC.qrySalesSchedule", data, page);
        
        IDataset list=result.getData();
        if(list.size() <= 0){
      	    //无数据
       	    setAjax("DATA_COUNT","0");
        }else{
        	for(int i=0;i<list.size();i++){
        		IData iData=list.getData(i);
        		 //查询客户星级
        		 String phone=iData.getString("MOBILEPHONE");
        		 if(!"".equals(phone)||phone !=null){
        			 IData param=new DataMap();
        			 param.put("SERIAL_NUMBER", phone);
        			 IDataset  dataset=CSViewCall.call(this, "SS.QryUserCreditClassSVC.qryUserCreditClass", param);
        			 if("0".equals(dataset.getData(0).getString("X_RESULTCODE"))){
        				 list.getData(i).put("CREDITCLASS", dataset.getData(0).getString("CREDIT_CLASS"));
        			 }else{
        				 list.getData(i).put("CREDITCLASS", "");
        			 }
        		 }else{
        			 list.getData(i).put("CREDITCLASS", "");
        		 }
        		 //查询  优惠券赠送金额、优惠券实际使用金额 rsrv_str5
        		 //获取服务号
        		 String serviceNo=iData.getString("SERVICENO");
        		 IData serviceNoParam=new DataMap();
        		 serviceNoParam.put("SERVICENO", serviceNo);
        		 IDataset  couponInfoList=CSViewCall.call(this, "SS.AfterSalesSVC.getCouponInfoServiceno", serviceNoParam);
        		if(IDataUtil.isNotEmpty(couponInfoList)){
        			IData couponInfo=couponInfoList.getData(0);
        			//优惠券赠送金额
        			list.getData(i).put("GIVEMONEY", couponInfo.getString("TICKET_VALUE"));
        			//优惠券实际使用金额
        			list.getData(i).put("COUPONMONEY", couponInfo.getString("SPEND_VALUE"));
        		}else{
        			list.getData(i).put("GIVEMONEY", "");
        			list.getData(i).put("COUPONMONEY", "");
        		}
        		 
        	}
        	setInfos(list);
        	setPageCount(list.size());
        }
        setCondition(data);
        

    }
    
    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);
    
}
