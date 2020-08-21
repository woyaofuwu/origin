
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;

public class VipSimBakQuerySVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 598810070326252442L;

    public IDataset checkBakCard(IData params) throws Exception
    {

        VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);

        return bean.checkBakCard(params);
    }

    /**
     * 获取申请备卡设备价格
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getDevicePrice(IData param) throws Exception
    {

        VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);

        return bean.getDevicePrice(param);
    }

    /**
     * 获取备卡信息
     * 
     * @param params
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-9-21
     */
    public IDataset getSimBakInfos(IData params) throws Exception
    {

        return CustVipInfoQry.getSimBakInfos(params);
    }

    /**
     * 获取SIM卡信息
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-9-21
     */
    public IDataset getSimCardInfo(IData data) throws Exception
    {

        VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);

        return bean.getSimCardInfo(data.getString("SIM_CARD_NO"));
    }

    /**
     * 获取TD_S_TAG中参数值
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-9-21
     */
    public IDataset getSysTagInfo(IData param) throws Exception
    {

        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        return TagInfoQry.getSysTagInfo(param);

    }

    /**
     * 获取用户有效资源信息
     * 
     * @param pd
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getUserResInfo(IData param) throws Exception
    {

        VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);

        return bean.getUserResInfo(param);
    }

    /**
     * 获取vip信息
     * 
     * @param params
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-9-21
     */
    public IDataset getVipInfos(IData params) throws Exception
    {

        VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);
        return bean.getVipInfos(params);
    }

    /**
     * 返回大客户备卡激活所需的信息 包括 IDataset 申请的备卡信息 IData 大客户信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getVipSimBakActInfo(IData param) throws Exception
    {

        VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);

        return bean.getVipSimBakActInfo(param);
    }

    /**
     * 返回大客户备卡申请信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-8-24
     */
    public IDataset getVipSimBakAppInfo(IData param) throws Exception
    {

        VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);

        return bean.getVipSimBakAppInfo(param);
    }

    /**
     * 返回大客户备卡取消所需的信息 包括 IDataset 申请的备卡信息 IData 大客户信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getVipSimBakCancelInfo(IData params) throws Exception
    {

        VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);

        return bean.getVipSimBakCancelInfo(params);
    }
}
