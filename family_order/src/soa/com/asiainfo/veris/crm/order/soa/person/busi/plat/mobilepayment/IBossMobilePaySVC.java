
package com.asiainfo.veris.crm.order.soa.person.busi.plat.mobilepayment;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IBossMobilePaySVC extends CSBizService
{

    /**
     * 一级boss手机支付冲正
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData accountDec(IData param) throws Exception
    {
        return IBossMobilePayBean.accountDec(param);
    }

    /**
     * 一级boss手机支付充值
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData accountPay(IData param) throws Exception
    {
        return IBossMobilePayBean.accountPay(param);
    }

    /**
     * 手机支付鉴权校验
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData accountPayCheck(IData param) throws Exception
    {
        return IBossMobilePayBean.accountPayCheck(param);
    }

    /**
     * 获取手机支付充值的收据打印数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAccPayData(IData input) throws Exception
    {
        return IBossMobilePayBean.getAccPayData(input);
    }

    /**
     * 查询手机支付绑定银行卡信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryAccountBindBankInfos(IData param) throws Exception
    {
        return IBossMobilePayBean.queryAccountBindBankInfos(param);
    }

    /**
     * 查询可冲正的记录
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryAccountPay(IData param) throws Exception
    {
        return IBossMobilePayBean.queryAccountPay(param, this.getPagination());
    }

    /**
     * 重置手机支付密码
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset resetPassword(IData param) throws Exception
    {
        return IBossMobilePayBean.resetAccountPassword(param);
    }
}
