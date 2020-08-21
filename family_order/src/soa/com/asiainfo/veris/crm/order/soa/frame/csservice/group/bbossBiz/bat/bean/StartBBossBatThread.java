package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean;


import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public abstract class StartBBossBatThread extends Thread
{
	/**
     * 传入参数
     */
    private IData param = new DataMap();
    
    private BizVisit visit = null;
    
    private Object peek = null;
	
	/**
     * @param param 传入参数
     */

	public StartBBossBatThread(IData param, BizVisit visit,Object service) {
        
        this.param = param;
        this.visit = visit;        
        this.peek = service;
    }

    @Override
    public void run() {

    	  SessionManager manager = SessionManager.getInstance();

    	  try {
        	    //1- 激活会话,并设置上下文对象
        	    manager.start();
        	    manager.setContext(this.peek, this.visit);
        	    
        	    //2- 调用服务处理
        	    CSAppCall.call("CS.BatDealBBossMebSubSVC.dealBBossMebSub", param);
        	    
        	    //3- 事务提交
        	    manager.commit();
    	  } catch (Exception e) {
    	    try {
				manager.rollback();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	  } finally {
    	    try {
				manager.destroy();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	  }
    }
    
   
}
