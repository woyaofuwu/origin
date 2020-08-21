package com.asiainfo.veris.crm.order.web.person.view360;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryOpenInfo extends PersonBasePage{
    /**
     * 已开业务查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER");
        IDataset feeInfos = new DatasetList();
        IDataset output = new DatasetList();
        try {
        	feeInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.getOpenInfo", data);
        	output = CSViewCall.call(this, "SS.GetUser360ViewSVC.getSaleInfo", data);
		} catch (Exception e) {
			e.printStackTrace();
			CSViewException.apperr(CrmUserException.CRM_USER_1);
		} 
		IData sum = new DataMap();
    	IDataset sumInfos = new DatasetList();
    	if(IDataUtil.isNotEmpty(feeInfos)){
    		if("1".equals(feeInfos.getData(0).getString("RESULT_CODE"))){
    			sum.put("SUM_MONTH_RENT", feeInfos.getData(0).getString("SUM_MONTH_RENT"," "));
            	sum.put("SUM_DAY_RECVE_FEE", feeInfos.getData(0).getString("SUM_DAY_RECVE_FEE"," "));
            	sum.put("SUM_RECVE_FEE_SUM", feeInfos.getData(0).getString("SUM_RECVE_FEE_SUM"," "));
            	sum.put("SUM", "合计");
            	sum.put("NUM", "--");    	
            	sumInfos.add(sum);
            	setSumInfos(sumInfos);
            	feeInfos.remove(0);
    		}else{
    			sum.put("SUM_MONTH_RENT", "0");
            	sum.put("SUM_DAY_RECVE_FEE", "--");
            	sum.put("SUM_RECVE_FEE_SUM", "--");
            	sum.put("SUM", "合计");
            	sum.put("NUM", "--");    	
            	sumInfos.add(sum);
            	setSumInfos(sumInfos);
    		}
    	   	
    		DataHelper.sort(feeInfos,"BUSINESS_TYPE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND,"MONTH_RENT", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        	DataHelper.sort(feeInfos,"ITEM_CODE", IDataset.TYPE_INTEGER, IDataset.ORDER_DESCEND);
    		setOpenInfos(feeInfos);
        	
    	}
        setSaleInfos(output); 
    }
    
    
    /**
     * 按业务名称/月租/扣费方式/开始时间排序
     * 
     * @param cycle
     * @throws Exception
     */
    public void sort(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER");
        IDataset feeInfos = new DatasetList();
        try {
        	feeInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.getOpenInfo", data);
		} catch (Exception e) {
			e.printStackTrace();
			CSViewException.apperr(CrmUserException.CRM_USER_1);
		}
        
    	IData sum = new DataMap();
    	IDataset sumInfos = new DatasetList();
    	if("1".equals(feeInfos.getData(0).getString("RESULT_CODE"))){
			sum.put("SUM_MONTH_RENT", feeInfos.getData(0).getString("SUM_MONTH_RENT"," "));
        	sum.put("SUM_DAY_RECVE_FEE", feeInfos.getData(0).getString("SUM_DAY_RECVE_FEE"," "));
        	sum.put("SUM_RECVE_FEE_SUM", feeInfos.getData(0).getString("SUM_RECVE_FEE_SUM"," "));
        	sum.put("SUM", "合计");
        	sum.put("NUM", "--");    	
        	sumInfos.add(sum);
        	feeInfos.remove(0);
		}else{
			sum.put("SUM_MONTH_RENT", "0");
        	sum.put("SUM_DAY_RECVE_FEE", "--");
        	sum.put("SUM_RECVE_FEE_SUM", "--");
        	sum.put("SUM", "合计");
        	sum.put("NUM", "--");    	
        	sumInfos.add(sum);
		} 
        if("1".equals(data.getString("TYPE"))){
        	DataHelper.sort(feeInfos, "BUSINESS_TYPE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
        }       
        if("2".equals(data.getString("TYPE"))){
        	DataHelper.sort(feeInfos, "MONTH_RENT", IDataset.TYPE_DOUBLE, IDataset.ORDER_DESCEND);
        }
        if("3".equals(data.getString("TYPE"))){
        	DataHelper.sort(feeInfos, "RECVE_TYPE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        }
        if("4".equals(data.getString("TYPE"))){
        	DataHelper.sort(feeInfos, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        }
    	setOpenInfos(feeInfos);
    	setSumInfos(sumInfos);
    }

    public abstract void setCond(IData cond);

    public abstract void setOpenInfos(IDataset feeInfos);
    public abstract void setSaleInfos(IDataset openInfos);
    public abstract void setSumInfos(IDataset sumInfos);
    
}
