package com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CustServiceAuthSVC extends CSBizService
{
    private static transient Logger logger = Logger.getLogger(CustServiceAuthSVC.class);
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    /**
     * 随机密码生成
     * @param data
     * @return
     * @throws Exception
     */
    public IData randomPassword(IData data) throws Exception
    {
        CustServiceAuthBean bean = BeanManager.createBean(CustServiceAuthBean.class);
        return bean.randomPassword(data);
    }
    
    /**
     * 绑定手机号码
     * @param data
     * @return
     * @throws Exception
     */
    public IData bindMobilePhone(IData data) throws Exception
    {
        CustServiceAuthBean bean = BeanManager.createBean(CustServiceAuthBean.class);
        return bean.bindMobilePhone(data);
    }
    /**
     * 解除绑定手机号码
     * @param data
     * @return
     * @throws Exception
     */
    public IData unBindMobilePhone(IData data) throws Exception
    {
        CustServiceAuthBean bean = BeanManager.createBean(CustServiceAuthBean.class);
        return bean.unBindMobilePhone(data);
    }
    
    /**
     * 申请凭证
     * @param data
     * @return
     * @throws Exception
     */
    public IData certificateRequest(IData data) throws Exception
    {
        CustServiceAuthBean bean = BeanManager.createBean(CustServiceAuthBean.class);
        return bean.certificateRequest(data);
    } 
    /**
     * 凭证校验
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkCertificate(IData data) throws Exception
    {
        CustServiceAuthBean bean = BeanManager.createBean(CustServiceAuthBean.class);
        return bean.checkCertificate(data);
    } 
    /**
     * 申请延时
     * @param data
     * @return
     * @throws Exception
     */
    public IData certificateDelay(IData data) throws Exception
    {
        CustServiceAuthBean bean = BeanManager.createBean(CustServiceAuthBean.class);
        return bean.certificateDelay(data);
    }
    

    /**
     * 自助服务密码凭证申请
     * @param data
     * @return
     * @throws Exception
     */
    public IData selfServiceCertificateRequest(IData data) throws Exception
    {
        CustServiceAuthBean bean = BeanManager.createBean(CustServiceAuthBean.class);
        return bean.selfServiceCertificateRequest(data);
    }

    /**
     *自助服务密码凭证返回
     * @param data
     * @throws Exception
     */
    public IData selfServiceCertificateResponse(IData data) throws Exception
    {
        CustServiceAuthBean bean = BeanManager.createBean(CustServiceAuthBean.class);
        return bean.selfServiceCertificateResponse(data);
    }

}
