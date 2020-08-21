
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.selfhelpcard;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SelfHelpChangeCardSVC extends CSBizService
{

    /**
     * 自助换卡第四步
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData changeCard(IData input) throws Exception
    {
        SelfHelpChangeCardBean bean = (SelfHelpChangeCardBean) BeanManager.createBean(SelfHelpChangeCardBean.class);
        return bean.changeCard(input);
    }

    /**
     * 自助换卡第四步 异地使用本地
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData changeCardF2L(IData input) throws Exception
    {
        SelfHelpChangeCardBean bean = (SelfHelpChangeCardBean) BeanManager.createBean(SelfHelpChangeCardBean.class);
        return bean.changeCardF2L(input);
    }

    /**
     * 自助换卡第一步
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkUserCardFlow(IData input) throws Exception
    {
        SelfHelpChangeCardBean bean = (SelfHelpChangeCardBean) BeanManager.createBean(SelfHelpChangeCardBean.class);
        return bean.checkUserCardFlow(input);
    }

    /**
     * 自助换卡第一步 异地使用本地
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkUserCardFlowF2L(IData input) throws Exception
    {
        SelfHelpChangeCardBean bean = (SelfHelpChangeCardBean) BeanManager.createBean(SelfHelpChangeCardBean.class);
        return bean.checkUserCardFlowF2L(input);
    }

    /**
     * 自助换卡第三步
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkWriteInfo(IData input) throws Exception
    {
        SelfHelpChangeCardBean bean = (SelfHelpChangeCardBean) BeanManager.createBean(SelfHelpChangeCardBean.class);
        return bean.checkWriteInfo(input);
    }

    /**
     * 自助换卡第三步 本地使用异地
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkWriteInfoL2F(IData input) throws Exception
    {
        SelfHelpChangeCardBean bean = (SelfHelpChangeCardBean) BeanManager.createBean(SelfHelpChangeCardBean.class);
        return bean.checkWriteInfoL2F(input);
    }

    /**
     * 自助换卡第二步
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getWriteSimCardInfo(IData input) throws Exception
    {
        SelfHelpChangeCardBean bean = (SelfHelpChangeCardBean) BeanManager.createBean(SelfHelpChangeCardBean.class);
        return bean.getWriteSimCardInfo(input);
    }

    /**
     * 自助换卡第二步 本地使用异地
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getWriteSimCardInfoL2F(IData input) throws Exception
    {
        SelfHelpChangeCardBean bean = (SelfHelpChangeCardBean) BeanManager.createBean(SelfHelpChangeCardBean.class);
        return bean.getWriteSimCardInfoL2F(input);
    }

    public void setTrans(IData input) throws Exception
    {
        if (StringUtils.isEmpty(input.getString("SERIAL_NUMBER")))
        {
            if (StringUtils.isNotEmpty(input.getString("SERIAL_NUMBER_TEMP")))
            {
                input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER_TEMP"));
            }

            if (StringUtils.isNotEmpty(input.getString("TEMP_NUMBER")))
            {
                input.put("SERIAL_NUMBER", input.getString("TEMP_NUMBER"));
            }
        }
    }

}
