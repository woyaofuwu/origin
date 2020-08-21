
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.ibplatsyn;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IbPlatSynQuerySVC extends CSBizService
{
    static transient final Logger logger = Logger.getLogger(IbPlatSynQuerySVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset queryIbPlatSynInfo(IData input) throws Exception
    {
        IbPlatSynQueryBean ibplatsyn = (IbPlatSynQueryBean) BeanManager.createBean(IbPlatSynQueryBean.class);

        IDataset infos = ibplatsyn.queryIbPlatSynInfo(input, getPagination());

        return infos;
    }

}
