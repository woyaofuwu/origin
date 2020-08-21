
package com.asiainfo.veris.crm.order.soa.person.busi.ziyoubusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ZiYouBusinessQrySVC extends CSBizService
{
	
	
    public IDataset qryBlackList(IData input) throws Exception
    {
        ZiYouBusinessQryBean queryBlackListBean = BeanManager.createBean(ZiYouBusinessQryBean.class);
        return queryBlackListBean.queryBlackListInfo(input);
    }

    public IDataset qryOtherRecord(IData input) throws Exception
    {
        ZiYouBusinessQryOtherRecordBean queryOtherRecordBean = BeanManager.createBean(ZiYouBusinessQryOtherRecordBean.class);
        return queryOtherRecordBean.queryOtherRecord(input);
    }

    public IDataset subClosePage(IData input) throws Exception
    {
        ZiYouBusinessQryBean queryBlackListBean = BeanManager.createBean(ZiYouBusinessQryBean.class);
        return queryBlackListBean.subClosePage(input);
    }

    /*
     *功能描述 能力清单查询
     * @author heyy3
     * @date 2019/8/19
     * @param
     * @return
     */
    public IDataset qryAbilityList(IData input) throws Exception
    {
        AbilityListQryBean abilityListQryBean = BeanManager.createBean(AbilityListQryBean.class);
        return abilityListQryBean.qryAbilityList(input);
    }

    /*
     *功能描述 :单项能力查询
     * @author heyy3
     * @date 2019/8/19
     * @param
     * @return
     */
    public IDataset singleAbilityQry(IData input) throws Exception
    {
        SingleAbilityQryBean singleAbilityQryBean = BeanManager.createBean(SingleAbilityQryBean.class);
        return singleAbilityQryBean.qrySingleAbility(input);
    }

}
