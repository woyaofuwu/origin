package com.asiainfo.veris.crm.order.soa.person.busi.intelligentnk;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IntelligentNetworKingSVC extends CSBizService
{
    private static final long serialVersionUID = 1L; 

    private static Logger logger = Logger.getLogger(IntelligentNetworKingSVC.class);

    /**
     * 6.5.1	宽带账号查询
     * 服务使用方（一级组网）向服务提供方（省CRM）发起宽带账号查询请求，服务提供方将查询结果反馈至服务使用方
     * @param param
     * @return
     * @throws Exception
     */
    public IData wbandInq(IData input) throws Exception {
		  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
	        return bean.wbandInq(input);
	}
    
    //6.5.2  智能组网预装订单信息受理
    public IData orderBizOrder(IData input) throws Exception {
		  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
	        return bean.orderBizOrder(input);
	}
    
    //6.5.4	    工单信息同步
    public IData listInfoSyn(IData input) throws Exception {
		  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
	        return bean.listInfoSyn(input);
	}
    
    //6.5.5   支付信息同步
    public IData payInfoSyn(IData input) throws Exception {
		  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
	        return bean.payInfoSyn(input);
	}
    //6.5.6   报结信息同步
	 public IData evaInfoSyn(IData input) throws Exception {
	  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
       return bean.evaInfoSyn(input);
    }
	 //6.5.7   组网业务状态查询
	 public IData busiStatusQuery(IData input) throws Exception {
		  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
	        return bean.busiStatusQuery(input);
	  }
	 //6.5.8   用户评价信息同步
	 public IData busiCommentSyn(IData input) throws Exception {
		  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
	        return bean.busiCommentSyn(input);
	 }
	//6.5.9   订单退订取消同步
	 public IData tdCancelSyn(IData input) throws Exception {
		  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
	        return bean.tdCancelSyn(input);
	 }
	//6.5.10   请求重发
	 public IData resend(IData input) throws Exception {
		  IntelligentNetworKingBean bean = BeanManager.createBean(IntelligentNetworKingBean.class);
	        return bean.resend(input);
	 }
   
}
