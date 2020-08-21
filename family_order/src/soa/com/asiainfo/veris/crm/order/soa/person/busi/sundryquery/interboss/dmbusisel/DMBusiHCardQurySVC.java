
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DMBusiHCardQurySVC extends CSBizService
{
    static transient final Logger logger = Logger.getLogger(DMBusiHCardQurySVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * 历史机卡配对关系查询
     * 
     * @param conParams
     * @return
     * @throws Exception
     */
    public IDataset queryHCard(IData conParams) throws Exception
    {
        DMBusiHCardQuryBean busiHCardQuryBean = (DMBusiHCardQuryBean) BeanManager.createBean(DMBusiHCardQuryBean.class);

        return busiHCardQuryBean.getHCardInfo(conParams);
    }
}
