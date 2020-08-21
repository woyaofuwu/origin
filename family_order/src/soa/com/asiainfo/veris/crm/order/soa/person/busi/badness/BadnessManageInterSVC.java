
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BadnessManageInterSVC extends CSBizService
{

    /**
     * @Function: dealHLRStopOpenReg
     * @Description: HLR加解黑命令接口 ITF_CRM_BadInfoDeal
     * @date Jun 4, 2014 5:24:06 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset createHLRStopOpenReg(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.createHLRStopOpenReg(data);
    }

    public IDataset createHLRStopOpenRegIner(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.createHLRStopOpenRegIner(data);
    }

    /**
     * @Function: dealBadness
     * @Description: 不良信息处理 ITF_CRM_DealBadness
     * @date May 17, 2014 3:16:20 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealBadness(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.dealBadness(data);
    }
    /**
     * @Function: dealPTPmessage
     * @Description: 点对点短信屏蔽 ITF_CTRM_PTPMESSAGE_SHIELD
     * @date May 17, 2014 3:16:20 PM
     * @param data
     * @return
     * @throws Exception
     * @author xiaochang
     */
    public IDataset dealPTPmessage(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.dealPTPmessage(data);
    }

    /**
     * @Function: dealReprot
     * @Description: 不良信息举报受理一级boss(异地举报) ITF_CRM_BossReportbedinfoDeal
     * @date Jun 12, 2014 5:05:54 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealReprot(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.dealReprot(data);
    }

    /**
     * @Function: dealReprotnet
     * @Description: 垃圾短信举报(new) ITF_CRM_NetReportbedinfoDeal
     * @date Jun 12, 2014 5:09:41 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealReprotnet(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.dealReprotnet(data);
    }

    /**
     * @Function: dealReprotout
     * @Description: 不良信息举报一级客服派发 ITF_CRM_ReportbedinfooutDeal
     * @date May 30, 2014 2:48:24 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealReprotout(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.dealReprotout(data);
    }

    /**
     * @Function: hastenBadness
     * @Description: 不良信息催办 ITF_CRM_HastenBadness
     * @date Jun 12, 2014 5:05:44 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset hastenBadness(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.hastenBadness(data);
    }

    /**
     * @Function: queryBadness
     * @Description: TODO
     * @date Jun 12, 2014 5:05:47 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset queryBadness(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.queryBadness(data);
    }

    public IDataset updateBadDealInfoByMsgIntf(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return IDataUtil.idToIds(bean.updateBadDealInfoByMsgIntf(data));
    }

    /**
     * @Function: updateBadInfoByArchIntf
     * @Description: 不良信息接口归档 ITF_CRM_BadnessArchIntf
     * @date Jun 12, 2014 5:05:58 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset updateBadInfoByArchIntf(IData data) throws Exception
    {
        BadnessManageInterBean bean = BeanManager.createBean(BadnessManageInterBean.class);
        return bean.updateBadInfoByArchIntf(data);
    }
}
