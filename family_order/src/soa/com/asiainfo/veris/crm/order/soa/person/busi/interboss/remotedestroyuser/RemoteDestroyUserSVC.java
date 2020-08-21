package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RemoteDestroyUserSVC extends CSBizService{
    public IDataset queryCardType(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.queryCardType(input);
    }
    public IDataset openResultAuthF(IData input) throws Exception{
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.openResultAuthF(input);
    }
	public IDataset applySubmitCancel(IData input) throws Exception
    {
		RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.applySubmitCancel(input);
    }

	public IDataset applyCancelAccount(IData input) throws Exception
    {
		RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.applyCancelAccount(input);
    }

	public IDataset queryDestroyOrder(IData input) throws Exception
    {
		RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.queryDestroyOrder(input);
    }

    public IDataset queryReceiptOrder(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.queryReceiptOrder(input);
    }

    public IDataset updateReceiveDestroyUserTrade(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.updateReceiveDestroyUserTrade(input);
    }

    public IDataset applyCancelAccountSyn(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.applyCancelAccountSyn(input);
    }

    public IDataset queryTransferInfo(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.queryTransferInfo(input);
    }

    public IDataset checkSerialNumberProv(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.checkSerialNumberProv(input);
    }

    public IDataset cancelAccount(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.cancelAccount(input);
    }

    public IDataset cancelAccountSyn(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.cancelAccountSyn(input);
    }

    public IDataset queryReceiveDestroyOrderHis(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.queryReceiveDestroyOrderHis(input,getPagination());
    }
    /**
     * k3
     * 插入身份证正反面
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset insPsptFrontBack(IData input) throws Exception
    {
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.insPsptFrontBack(input);
    }
    /**
     * k3
     * 查询已审核的工单
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAuditedOrder(IData input) throws Exception
    {
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.qryAuditedOrder(input);
    }
    /**
	 * k3
	 * 查询当天收到的工单
	 * @return
	 * @throws Exception
	 */
    public IDataset qryNowDayOrder(IData input) throws Exception
    {
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.qryNowDayOrder(input);
    }
    /**
     * k3
     * 自动销户失败更新接单表
     * @return
     * @throws Exception
     */
    public IDataset updateDestroyFail(IData input) throws Exception
    {
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.updateDestroyFail(input);
    }
    public IDataset queryApplyDestroyUserTradeByOrderId(IData input) throws Exception
    {
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.queryApplyDestroyUserTradeByOrderId(input);
    }
    /**
     * 销户前校验  发起
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset destroyCheck(IData input) throws Exception{
        RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
        return bean.destroyCheck(input);
    }
    /**
     * 催单 落地
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset busRemind(IData input) throws Exception{
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.busRemind(input);
    }
    /**
     * 调iboss 查询已派过但归属省未审核的单
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryRemindOrder(IData input) throws Exception{
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.queryRemindOrder(input);
    }
    /**
     * 跨区销户查询省内已催单和未催单的工单
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDestroyLocalOrder(IData input) throws Exception{
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.queryDestroyLocalOrder(input,getPagination());
    }
    /**
     * 查询跨区销户被催的工单
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDestroyReceiOrder(IData input) throws Exception{
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.queryDestroyReceiOrder(input,getPagination());
    }
    
    
    /**
     * 调iboss工具接口
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset callIbossTool(IData input) throws Exception{
    	RemoteDestroyUserBean bean = (RemoteDestroyUserBean) BeanManager.createBean(RemoteDestroyUserBean.class);
    	return bean.callIbossTool(input);
    }
}
