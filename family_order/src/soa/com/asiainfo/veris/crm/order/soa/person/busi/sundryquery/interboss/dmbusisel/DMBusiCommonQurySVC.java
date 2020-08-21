
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DMBusiCommonQurySVC extends CSBizService
{
    static transient final Logger logger = Logger.getLogger(DMBusiCommonQurySVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * DM业务查询、操作取消
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDMBusi(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();

        DMBusiSelBean dmBusiSelBean = (DMBusiSelBean) BeanManager.createBean(DMBusiSelBean.class);

        IData infos = dmBusiSelBean.queryDMBusi(input);

        dataset.add(infos);

        return dataset;
    }

    /**
     * DM业务查询、操作取消
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDMBusi_Sub(IData input) throws Exception
    {

        IDataset dataset = new DatasetList();

        DMBusiSelBean dmBusiSelBean = (DMBusiSelBean) BeanManager.createBean(DMBusiSelBean.class);

        IData infos = dmBusiSelBean.queryDMBusi_Sub(input);

        dataset.add(infos);

        return dataset;
    }

    /**
     * DM机卡配对关系查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDMBusiCard(IData input) throws Exception
    {
        DMBusiCardSelBean busiCardSelBean = BeanManager.createBean(DMBusiCardSelBean.class);

        return busiCardSelBean.getBusiCardSelInfo(input);
    }

    /**
     * 终端静态信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryStaticInfo(IData input) throws Exception
    {
        StaticInfoQueryBean staticInfoQueryBean = (StaticInfoQueryBean) BeanManager.createBean(StaticInfoQueryBean.class);

        return staticInfoQueryBean.getStaticInfo(input);
    }

    /**
     * DM业务操作取消——业务提交
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset submitTrade(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("----------DMBusiCommonQurySVC------------submitTrade----------------");

        IDataset dataset = new DatasetList();

        DMBusiSelBean dmBusiSelBean = (DMBusiSelBean) BeanManager.createBean(DMBusiSelBean.class);

        IData infos = dmBusiSelBean.submitTrade(input);

        dataset.add(infos);

        return dataset;
    }

}
