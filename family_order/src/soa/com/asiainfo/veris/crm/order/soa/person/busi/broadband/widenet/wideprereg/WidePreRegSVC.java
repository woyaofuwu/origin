package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WidePreRegSVC extends CSBizService
{
	protected static Logger log = Logger.getLogger(WidePreRegSVC.class);

    private static final long serialVersionUID = 1L;
	    
    public IDataset getPreRegInfosByFiveAddr(IData param) throws Exception
    {
    	WidePreRegBean bean = BeanManager.createBean(WidePreRegBean.class);
    	return bean.getPreRegInfosByFiveAddr(param);
    }
    
    /**
     * AEE定时任务
     * 宽带需求资料地址汇总、预警
     * */
    public void checkWidePreRegByAddrCollectEarlyWarning(IData userInfo) throws Exception{
    	WidePreRegBean bean= BeanManager.createBean(WidePreRegBean.class);
    	bean.checkWidePreRegByAddrCollectEarlyWarning(userInfo);
    }
    
//    /**
//     *
//     * 宽带需求收集提供宽带能力通知
//     * */
//    public void checkWidePreRegByAddrCollectNotify(IData userInfo) throws Exception{
//    	WidePreRegBean bean= BeanManager.createBean(WidePreRegBean.class);
//    	bean.checkWidePreRegByAddrCollectNotify(userInfo);
//    }
}
