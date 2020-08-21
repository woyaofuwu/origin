/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 下午04:17:46
 */
public class EntityCardSVC extends CSBizService
{

    private static final long serialVersionUID = 3986932897267961238L;

    /**
     * 激活实体卡
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-7
     */
    public IData activeEntityCard(IData data) throws Exception
    {

        EntityCardBean bean = BeanManager.createBean(EntityCardBean.class);

        IData result = new DataMap();

        result.put("ACTIVE_FLAG", bean.activeEntityCard(data));

        return result;
    }

    public IData activeEntityCardForRes(IData data) throws Exception
    {
        EntityCardBean bean = BeanManager.createBean(EntityCardBean.class);

        return bean.activeEntityCardForRes(data);
    }

    public IDataset checkNewEntityCard(IData data) throws Exception
    {

        EntityCardBean bean = BeanManager.createBean(EntityCardBean.class);

        return bean.checkNewEntityCard(data);
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-4
     */
    public IData getResInfo(IData data) throws Exception
    {

        EntityCardBean bean = BeanManager.createBean(EntityCardBean.class);

        return bean.getResInfo(data);
    }

    /**
     * 实体卡锁定
     * 
     * @param dataNum
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-29
     */
    public IDataset lockEntityCard(IData dataNum) throws Exception
    {

        EntityCardBean bean = BeanManager.createBean(EntityCardBean.class);

        return bean.lockEntityCard(dataNum);
    }

    /**
     * 根据实体卡卡号查询实体卡信息
     * 
     * @param pd
     * @param inpara
     * @return
     * @throws Exception
     */
    public IDataset QueryEntityCard(IData input) throws Exception
    {

        EntityCardBean bean = BeanManager.createBean(EntityCardBean.class);

        return bean.QueryEntityCard(input, this.getPagination());
    }

}
