package com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow;
		
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ShareClusterFlowSVC extends CSBizService
{
    // 校验方法
    public IDataset check(IData data) throws Exception
    {
    	ShareClusterFlowBean bean = BeanManager.createBean(ShareClusterFlowBean.class);
        return bean.check(data);
    }

    // 新增成员时候的校验
    public void checkAddMeb(IData data) throws Exception
    {
    	ShareClusterFlowBean bean = BeanManager.createBean(ShareClusterFlowBean.class);
        bean.checkAddMeb(data);
    }
    // 副卡申请加入家庭群组时校验主卡信息
    public IDataset checkAddMain(IData data) throws Exception
    {
    	ShareClusterFlowBean bean = BeanManager.createBean(ShareClusterFlowBean.class);
        return bean.checkAddMain(data);
    }

    // 查询可以共享的优惠
    public IDataset queryFamilyDiscntList(IData data) throws Exception
    {
    	ShareClusterFlowBean bean = BeanManager.createBean(ShareClusterFlowBean.class);
        return bean.queryShareDiscntList(data);
    }

    // 查询副卡本身
    public IDataset queryFamilyMeb(IData data) throws Exception
    {
    	ShareClusterFlowBean bean = BeanManager.createBean(ShareClusterFlowBean.class);
        return bean.queryShareMeb(data);
    }

    // 查询共享关系所有副卡成员
    public IDataset queryFamilyMebList(IData data) throws Exception
    {
    	ShareClusterFlowBean bean = BeanManager.createBean(ShareClusterFlowBean.class);
        return bean.queryShareMebList(data);
    }

}
