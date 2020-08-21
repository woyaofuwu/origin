/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 银行总对总接口集
 * 
 * @CREATED by gongp@2014-6-24 修改历史 Revision 2014-6-24 上午09:21:07
 */
public class BankPaymentManageIntfSVC extends CSBizService
{
    private static final long serialVersionUID = -4805510771070293917L;

    static final Logger logger = Logger.getLogger(BankPaymentManageIntfSVC.class);

    /**
     * <!--销户自动解约接口-->
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-14
     */
    public IData autoDestroyBankSign(IData data) throws Exception
    {
        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.autoDestroyBankSign(data);
    }

    /**
     * <!--缴费接口-->
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-21
     */
    public IData bankSignPayment(IData data) throws Exception
    {
        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.bankSignPayment(data);
    }

    public IData bankSignPaymentConfirmation(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.bankSignPaymentConfirmation(data);
    }

    /**
     * 银行手机号码身份验证接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData banksignSNCheck(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.banksignSNCheck(data);

    }

    /**
     * <!-- 解约-->
     * 
     * @param cond
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData cancelledBank(IData cond) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.cancelledBank(cond);
    }

    /**
     * 主号签约结果通知接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData mainsignBankNotice(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.mainsignBankNotice(data);
    }

    /**
     * 主号码解除签约关系
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData mainsignCancel(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.mainsignCancel(data);
    }

    /**
     * 主号码签约信息变更校验订单生成接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData mainsignChange(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.mainsignChange(data);
    }

    /**
     * 主号码签约校验接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-21
     */
    public IData mainsignCheck(IData data) throws Exception
    {
        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.mainsignCheck(data);
    }

    /**
     * 修改银行参数
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-15
     */
    public IData modifyBankParam(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.modifyBankParam(data);
    }

    /**
     * 银行卡签约缴费-缴费接口
     * 
     * @param cond
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData paycostBank(IData cond) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.PaycostBank(cond);
    }

    /**
     * 查询用户签约信息
     * 
     * @param cond
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-21
     */
    public IData queryBank(IData cond) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.queryBank(cond);
    }

    /**
     * 查询关联号码接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData querySubsign(IData data) throws Exception
    {
        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.querySubsign(data);
    }

    @Override
    public void setTrans(IData input) throws Exception
    {

        // IBOSS接口入参转换,避免找不到路由
        if ("6".equals(getVisit().getInModeCode()))
        {
            if (StringUtils.isNotBlank(input.getString("SERIAL_NUMBER")))
            {
                return;
            }
            else if ("01".equals(input.getString("ROUTETYPE")) && StringUtils.isNotBlank(input.getString("ROUTEVALUE")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据ROUTETYPE[" + input.getString("ROUTETYPE") + "], + ROUTEVALUE[" + input.getString("ROUTEVALUE", "") + "]转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("ROUTEVALUE"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("IDVALUE")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据IDVALUE[" + input.getString("IDVALUE") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("MSISDN")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据MSISDN[" + input.getString("MSISDN") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("MSISDN"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("ID_VALUE")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据ID_VALUE[" + input.getString("ID_VALUE") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("ID_VALUE"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("IDITEMRANGE")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据IDITEMRANGE[" + input.getString("IDITEMRANGE") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("IDITEMRANGE", ""));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("SUB_NUMBER")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据SUB_NUMBER[" + input.getString("SUB_NUMBER") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("SUB_NUMBER"));
                return;
            }
        }
        else if (!"0".equals(getVisit().getInModeCode()) && !"6".equals(getVisit().getInModeCode()))
        {
            if (StringUtils.isNotBlank(input.getString("MSISDN")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据MSISDN[" + input.getString("MSISDN") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("MSISDN"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("PHONE_CODE")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据MSISDN[" + input.getString("MSISDN") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("PHONE_CODE"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("USER_VALUE")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据USER_VALUE[" + input.getString("USER_VALUE") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("USER_VALUE"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("MAIN_USER_VALUE")))
            {
            	if(logger.isDebugEnabled()){
            		logger.debug(">>>>>>>>>>>>>>>>>>根据MAIN_USER_VALUE[" + input.getString("MAIN_USER_VALUE") + "] 转换SERIAL_NUMBER");
            	}
                input.put("SERIAL_NUMBER", input.getString("MAIN_USER_VALUE"));
                return;
            }
            else
            {
                input.put(Route.ROUTE_EPARCHY_CODE, "0898");
                return;
            }
        }
    }

    /**
     * 签约关系同步
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData signingsynchronousBank(IData param) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.signingsynchronousBank(param);
    }

    /**
     * 解约副号码主号侧生成
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData subsignCancelMain(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.subsignCancelMain(data);
    }

    /**
     * 解约副号码副号侧生成
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-26
     */
    public IData subsignCancelSub(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.subsignCancelSub(data);
    }

    /**
     * 关联副号码主号侧生成
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-26
     */
    public IData subsignCreateMain(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.subsignCreateMain(data);

    }

    /**
     * 关联副号码副号侧生成
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData subsignCreateSub(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.subsignCreateSub(data);

    }

    /**
     * 关联副号码副号校验
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData subsignCreateSubCheck(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.subsignCreateSubCheck(data);
    }

    /**
     * 缴费提醒关键时刻阀值同步接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-14
     */
    public IData syncPayPrompt(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.syncPayPrompt(data);

    }

}
