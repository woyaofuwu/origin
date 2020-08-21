/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.marketmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @CREATED by gongp@2014-7-25 修改历史 Revision 2014-7-25 下午08:02:00
 */
public class MarketInfoMgrSVC extends CSBizService
{
    private static final long serialVersionUID = -7033020331671055045L;

    public IDataset synchronizeAllCommodity(IData inparam) throws Exception
    {
        MarketInfoMgrBean bean = BeanManager.createBean(MarketInfoMgrBean.class);

        return bean.synchronizeAllCommodity(inparam);
    }

}
