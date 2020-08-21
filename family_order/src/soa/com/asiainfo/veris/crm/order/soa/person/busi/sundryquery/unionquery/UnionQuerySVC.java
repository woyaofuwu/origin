
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.unionquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 统一查询服务接口类
 * 
 * @author zhouwu
 * @date 2014-07-29 20:58:17
 */
public class UnionQuerySVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 2625042564102819122L;

    /**
     * 统一退订查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryUnionCancelInfos(IData data) throws Exception
    {
        UnionQueryBean bean = (UnionQueryBean) BeanManager.createBean(UnionQueryBean.class);
        return bean.queryUnionCancelInfos(data, getPagination());
    }

    /**
     * 统一订购查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryUnionOrderInfos(IData data) throws Exception
    {
        UnionQueryBean bean = (UnionQueryBean) BeanManager.createBean(UnionQueryBean.class);
        return bean.queryUnionOrderInfos(data, getPagination());
    }

    /**
     * 统一退订业务详细信息查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset showCancelDetailInfo(IData data) throws Exception
    {
        UnionQueryBean bean = (UnionQueryBean) BeanManager.createBean(UnionQueryBean.class);
        return bean.showCancelDetailInfo(data);
    }

    /**
     * 统一订购详细信息查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset showOrderDetailInfo(IData data) throws Exception
    {
        UnionQueryBean bean = (UnionQueryBean) BeanManager.createBean(UnionQueryBean.class);
        return bean.showOrderDetailInfo(data);
    }

    /**
     * 统一退订会话详细信息查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset showSessionDetailInfo(IData data) throws Exception
    {
        UnionQueryBean bean = (UnionQueryBean) BeanManager.createBean(UnionQueryBean.class);
        return bean.showSessionDetailInfo(data);
    }

    /**
     * 统一退订产线保存处理意见信息
     * 
     * @param data
     * @throws Exception
     */
    public void updateUnionCancelInfos(IData data) throws Exception
    {
        UnionQueryBean bean = (UnionQueryBean) BeanManager.createBean(UnionQueryBean.class);
        bean.updateUnionCancelInfos(data);
    }

    /**
     * 保存处理意见信息
     * 
     * @param data
     * @throws Exception
     */
    public void updateUnionOrderInfos(IData data) throws Exception
    {
        UnionQueryBean bean = (UnionQueryBean) BeanManager.createBean(UnionQueryBean.class);
        bean.updateUnionOrderInfos(data);
    }

}
