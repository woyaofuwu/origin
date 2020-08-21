
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotewritecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RemoteWriteCardSVC extends CSBizService
{
    private static final long serialVersionUID = 2394740971982801892L;

    public IDataset getSimCardInfo(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.getSimCardInfo(input);
    }

    /**
     * 异地客户信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryCustInfo(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.queryCustInfo(input);
    }

    public IDataset queryMPayInfo(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.queryMPayInfo(input);
    }

    public IDataset writeCardActive(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.writeCardActive(input);
    }

    public IDataset writeCardResultback(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.writeCardResultback(input);
    }
    
    public IDataset getDevicePrice(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.getDevicePrice(input);
    }
    
    public IDataset loadPrintData(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.loadPrintData(input);
    }
    public IDataset printTrade(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.printTrade(input);
    }
    

    
    
    ////端午跨区第二方案
    public IDataset applyRemoteWrite(IData input) throws Exception
    {
        System.out.println("RemoteWriteCardSVC:applyRemoteWritexxxxxxxxxxxxxxx84 "+input);
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.applyRemoteWrite(input);
    } 
    public IDataset applyResultActive(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.applyResultActive(input);
    }

    public IDataset applyResultActiveCallRes(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.applyResultActiveCallRes(input);
    }
    
    
       
    public void SynPicId(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        bean.SynPicId(input);
    }
    
    
    public IDataset beforeWriteCard(IData input) throws Exception
    {
        System.out.println("RemoteWriteCardSVC:beforeWriteCardxxxxxxxxxxxxxxx90 "+input);
        RemoteWriteCardBean bean = BeanManager.createBean(RemoteWriteCardBean.class);
        IDataset returnSet = new DatasetList();
        String phoneNum = input.getString("PHONE_NUM", "1");
        if ("1".equals(phoneNum))
        {
            returnSet = bean.beforeWriteCard(input);
        }
        else if ("2".equals(phoneNum))
        {
        }
        setUserEparchyCode(getVisit().getStaffEparchyCode());//外省用户实际没有用户路由，怕路由出问题，将其改成交易路由
        String tradeEparchyCode = getVisit().getStaffEparchyCode();
        if(!getTradeEparchyCode().equals(tradeEparchyCode)) 
        {
        setTradeEparchyCode(tradeEparchyCode);
        }
        return returnSet;
    }
    public IDataset afterWriteCard(IData input) throws Exception
    {
        System.out.println("RemoteWriteCardSVC:afterWriteCardxxxxxxxxxxxxxxx111 "+input);
        RemoteWriteCardBean bean = BeanManager.createBean(RemoteWriteCardBean.class);
        IDataset returnSet = new DatasetList();
        String phoneNum = input.getString("PHONE_NUM", "1");
        if ("1".equals(phoneNum))
        {
            returnSet = bean.afterWriteCard(input);
        }
        else if ("2".equals(phoneNum))
        {
        }
        setUserEparchyCode(getVisit().getStaffEparchyCode());//外省用户实际没有用户路由，怕路由出问题，将其改成交易路由
        String tradeEparchyCode = getVisit().getStaffEparchyCode();
        if(!getTradeEparchyCode().equals(tradeEparchyCode)) 
        {
        setTradeEparchyCode(tradeEparchyCode);
        }
        return returnSet;
    }
    public IDataset queryRemoteWriteCustomer(IData input) throws Exception
    {
        System.out.println("RemoteWriteCardSVC:queryRemoteWriteCustomerxxxxxxxxxxxxxxx132 "+input);
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.queryRemoteWriteCustomer(input);
    }
    public IDataset getStaticValue(IData input) throws Exception
    {
        System.out.println("RemoteWriteCardSVC:getStaticValuexxxxxxxxxxxxxxx138 "+input);
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.getStaticValue(input);
    }
    public IDataset logRealNameInfo(IData input) throws Exception
    {
        System.out.println("RemoteWriteCardSVC:getStaticValuexxxxxxxxxxxxxxx138 "+input);
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.logRealNameInfo(input);
    }
    public IData qryEmptyCardResult(IData input) throws Exception
    {
        RemoteWriteCardBean bean = (RemoteWriteCardBean) BeanManager.createBean(RemoteWriteCardBean.class);
        return bean.qryEmptyCardResult(input);
    }
    
}
