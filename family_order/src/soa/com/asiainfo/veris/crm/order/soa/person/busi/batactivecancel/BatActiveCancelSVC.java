
package com.asiainfo.veris.crm.order.soa.person.busi.batactivecancel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;  
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 批量返销营销活动
 * 2016-01-25
 * chenxy3
 * */
public class BatActiveCancelSVC extends CSBizService
{
     
    /**
     *  获取营销活动
     * */
    public IDataset queryCampnTypes(IData userInfo) throws Exception{
    	IData param=new DataMap(); 
    	BatActiveCancelBean bean= BeanManager.createBean(BatActiveCancelBean.class);
    	return bean.queryCampnTypes(param);
    } 
    
    /**
	 * 根据活动类型获取产品* 
	 * */
	public IDataset queryProductsByLabel(IData input) throws Exception
    { 
        BatActiveCancelBean bean= BeanManager.createBean(BatActiveCancelBean.class);
        IDataset results = bean.querySaleActiveProductByLabel(input); 
        return results;
    }
	
	/**
	 * 根据产品ID获取包* 
	 * */
	public IDataset queryPackageByProdID(IData input) throws Exception
    { 
        BatActiveCancelBean bean= BeanManager.createBean(BatActiveCancelBean.class);
        IDataset results = bean.queryPackageByProdID(input); 
        return results;
    }
	
	/**
	 * 取返销用户营销活动信息，先要查得到才行
	 * */
	public static IDataset queryUserSaleActiveInfo(IData inParam) throws Exception
    { 
		BatActiveCancelBean bean= BeanManager.createBean(BatActiveCancelBean.class);
        IDataset results = bean.queryUserSaleActiveInfo(inParam); 
        return results;
    }
}
