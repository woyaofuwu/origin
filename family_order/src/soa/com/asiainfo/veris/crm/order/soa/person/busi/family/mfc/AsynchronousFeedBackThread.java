package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;


import org.apache.log4j.Logger;

import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public abstract class AsynchronousFeedBackThread extends Thread
{
	/**
     * 传入参数
     */
    private IData param = new DataMap();
    
    private BizVisit visit = null;
    
    private Object peek = null;
	
    private static transient final Logger log = Logger.getLogger(AsynchronousFeedBackThread.class);
	/**
     * @param param 传入参数
     */

	public AsynchronousFeedBackThread(IData param, BizVisit visit,Object service) {
        
        this.param = param;
        this.visit = visit;        
        this.peek = service;
    }

    @Override
    public void run() {

    	  SessionManager manager = SessionManager.getInstance();

    	  try {
    	    // 激活会话,并设置上下文对象
    	    manager.start();
    	    manager.setContext(this.peek, this.visit);

    	   //todo 这里可以调服务
    	    this.controlInfo(param);
    	    
    	    manager.commit();
    	  } catch (Exception e) {
    	    try {
    	    	IData in = new DataMap(param);
    	    	in.put("RSP_CODE","99");
    	    	in.put("RSP_TYPE", "11");
            	in.put("RSP_DESC","调用异常");
    	    	controlInfo(param);
				manager.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    	  } finally {
    	    try {
				manager.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	  }

    }
    
       
    /**
     * 调bboss
     * @param param  RSP_TYPE   11-成员省针对BBOSS下发省BOSS开通资质验证请求交易资质校验失败后给BBOSS的应答 
     *							12-成员省针对个人客户二次确认通过后给BBOSS的交易应答
     *							13-成员省对个人客户二次确认不通过后给BBOSS的交易应答
     * @return
     * @throws Exception
     */
    public void controlInfo(IData in) throws Exception{
    	IData bBossInput = new DataMap();
    	IData inparam = new DataMap();
        inparam.putAll(in);
        
    	
        if (log.isDebugEnabled())
      	{
      		log.debug("============AsynchronousFeedBackThread========inparam="+inparam);
      	}
    	
    	bBossInput.put("PRODUCT_OFFERING_ID",inparam.getString("PRODUCT_OFFERING_ID") );
    	bBossInput.put("PO_ORDER_NUMBER", inparam.getString("PO_ORDER_NUMBER"));
    	bBossInput.put("CUSTOMER_PHONE", inparam.getString("CUSTOMER_PHONE",""));
    	bBossInput.put("BUSINESS_TYPE", "1"); //1-个人业务 2-集团业务
    	bBossInput.put("ORDER_TYPE", "0");//    0-省BOSS申请变更成员  1-BBOSS下发成员省开通确认
    	bBossInput.put("RSP_TYPE", inparam.getString("RSP_TYPE"));
    	
    	bBossInput.put("RSLT", inparam.getDataset("PRODUCT_ORDER_MEMBER"));
    	bBossInput.put("BIZ_VERSION", "1.0.0");
    	bBossInput.put("KIND_ID", "MFCMemRsp_BBOSS_0_0");
    	log.debug("======AsynchronousFeedBackThread===this.visit==="+this.visit);
    	log.debug("======AsynchronousFeedBackThread===IBossCall==="+bBossInput);
    	CSAppCall.call("SS.VirtulFamilyTwoCheckIntfSVC.controlInfo",bBossInput);
    	//IDataset  res = IBossCall.dealInvokeUrl("MFCMemRsp_BBOSS_0_0","IBOSS2", bBossInput);
    	//return res;
    }    
}
