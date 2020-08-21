
package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean.*;


/**
 *   智慧中台 —— 集团接口验收规范
 *   政企市场
 *   政企订单透明化场景
 * @date 20200805
 * @author zhengkai5
 * */
public class OrderTransparencySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     *  订单列表查询
     */
    public IData orderListQuery(IData input) throws Exception
    {
        OrderListQueryBean bean = new OrderListQueryBean();
        return bean.orderListQuery(input);
    }

    /**
     * 	订单基本信息服务查询
     */
    public IData orderInfoQuery(IData input) throws Exception
    {
        OrderInfoQueryBean bean = BeanManager.createBean(OrderInfoQueryBean.class);
        return bean.orderInfoQuery(input);
    }
    /**
     * 	订单行基本信息查询服务
     */
    public IData orderItemInfoQuery(IData input) throws Exception
    {
        OrderItemInfoQueryBean bean = BeanManager.createBean(OrderItemInfoQueryBean.class);
        return bean.orderItemInfoQuery(input);
    }

    /**
     * 	客户基本信息查询服务
     */
    public IData customInfoQuery(IData input) throws Exception
    {
        CustomInfoQueryBean bean = BeanManager.createBean(CustomInfoQueryBean.class);
        return bean.customInfoQuery(input);
    }

    /**
     * 	商品基本信息查询服务
     */
    public IData offerInfoQuery(IData input) throws Exception
    {
        OfferInfoQueryBean bean = BeanManager.createBean(OfferInfoQueryBean.class);
        return bean.offerInfoQuery(input);
    }

    /**
     *  渠道基本信息查询服务
     */
    public IData channelIfnoQuery(IData input) throws Exception
    {
        ChannelIfnoQueryBean bean = BeanManager.createBean(ChannelIfnoQueryBean.class);
        return bean.channelIfnoQuery(input);
    }

    /**
     *  	合同基本信息查询服务
     */
    public IData contractInfoQuery(IData input) throws Exception
    {
        ContractItemInfoQueryBean bean = BeanManager.createBean(ContractItemInfoQueryBean.class);
        return bean.contractItemInfoQuery(input,false);
    }
    /**
     *  	合同基本详情信息查询服务
     */
    public IData contractItemInfoQuery(IData input) throws Exception
    {
        ContractItemInfoQueryBean bean = BeanManager.createBean(ContractItemInfoQueryBean.class);
        return bean.contractItemInfoQuery(input,true);
    }
    /**
     * 	合同撤销申请服务
     * @param input
     * @return
     * @throws Exception
     */
    public IData contractRevoke(IData input) throws Exception
    {
        ContractRevokeBean bean = BeanManager.createBean(ContractRevokeBean.class);
        return bean.contractRevoke(input);
    }
    /**
     * 	工单执行反馈服务
     * @param input
     * @return
     * @throws Exception
     */
    public IData contractRevokeFeedback(IData input) throws Exception
    {
        ContractRevokeFeedbackBean bean = BeanManager.createBean(ContractRevokeFeedbackBean.class);
        return bean.contractRevokeFeedback(input);
    }

}