/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.specompensatecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @CREATED by gongp@2014-4-11 修改历史 Revision 2014-4-11 下午03:08:04
 */
public class SpeCompensateCardSVC extends CSBizService
{

    private static final long serialVersionUID = -6344599956142261771L;

    public IDataset getInfoBySimCardNo(IData param) throws Exception
    {
        SpeCompensateCardBean bean = (SpeCompensateCardBean) BeanManager.createBean(SpeCompensateCardBean.class);

        return bean.getInfoBySimCardNo(param);
    }

    public IDataset tradeReg(IData param) throws Exception
    {
        SpeCompensateCardBean bean = (SpeCompensateCardBean) BeanManager.createBean(SpeCompensateCardBean.class);
        return bean.tradeReg(param);
    }
    
    
    public IDataset printSpeCompensateCard(IData input) throws Exception
    {
        SpeCompensateCardBean bean = (SpeCompensateCardBean) BeanManager.createBean(SpeCompensateCardBean.class);
        return bean.printSpeCompensateCard(input);
    }

}
