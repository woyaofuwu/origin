package com.asiainfo.veris.crm.order.soa.person.busi.checksanyou;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService; 
public  class CheckValuecardSanyouSVC extends CSBizService {  
	private static final long serialVersionUID = 1L;

    /**
     * REQ201505120012异常三友佳充值卡查询页面需求
     * 定时调用该服务。
     * SS.CheckValuecardSanyouSVC.checkValuecardSanyou
     * 2015-05-21 CHENXY3
     */
  
    public void checkValuecardSanyou(IData params) throws Exception
    {
    	CheckValuecardSanyouBean checkBean=new CheckValuecardSanyouBean();
    	checkBean.checkSanyouTempData(); 
    }
   
}
