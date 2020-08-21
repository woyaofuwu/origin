
package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean.AcceptanceInfoQueryBean;
import com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean.CreateAgreementListBean;
import com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean.DealInfoBean;
import com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean.SaveAgreementListBean;
import com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean.TradeRuleCheckBean;



/**
* 智慧中台 —— 集团接口验收规范
* 政企市场
* 融合业务受理场景
* @date 20200805
* @author zhengkai5
* */

public class IntegrationAcceptanceSVC extends CSBizService
{
    
    private static final long serialVersionUID = 1L;

    /**
     * 融合业务资费计算服务
     * 该服务用于根据编号查询商品费用信息
     * @return
     * @throws Exception
     */
    public IDataset offerFeeQuery(IData inParam) throws Exception
    {
        return null ;
    }
    /**
      * 人工派单服务
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData manualDispatch(IData inParam) throws Exception
    {
    	
    	DealInfoBean bean = BeanManager.createBean(DealInfoBean.class);
    	// SS.WorkformNodeDealSVC.dealInfo 
    	return bean.dealInfo( inParam )  ;
    }
    /**
     * 生成电子协议服务  
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData createAgreementList(IData inParam) throws Exception
    {
        //直接根据产品 id 调用产品模板
        CreateAgreementListBean bean = BeanManager.createBean(CreateAgreementListBean.class);
//		IData result = ElecAgreementUtils.buildTemplateToFile(CLOUD_AGREEMENT_DEF_ID,archivesInfo, getVisit(),"");
    	IData result = bean.createAgreementList( inParam ) ;
		return result   ;
    }
    /**
     * 保存签约后电子协议服务  
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData saveAgreementList(IData inParam) throws Exception
    {
    	//直接根据产品 id 调用产品模板
    	SaveAgreementListBean bean = BeanManager.createBean(SaveAgreementListBean.class);
//		IData result = ElecAgreementUtils.buildTemplateToFile(CLOUD_AGREEMENT_DEF_ID,archivesInfo, getVisit(),"");
    	IData result = bean.saveAgreementList( inParam ) ;
    	return result   ;
    }
    /**
     * 查询融合受理单信息服务
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData acceptanceInfoQuery(IData inParam) throws Exception
    {
    	
    	AcceptanceInfoQueryBean bean = new AcceptanceInfoQueryBean();
    	return bean.acceptanceInfoQuery( inParam )  ;
    }
    /**
     * 	业务规则校验服务
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData tradeRuleCheck(IData inParam) throws Exception
    {
    	TradeRuleCheckBean bean = BeanManager.createBean(TradeRuleCheckBean.class);
    	return bean.checkTrade( inParam )  ;
    }
    
}