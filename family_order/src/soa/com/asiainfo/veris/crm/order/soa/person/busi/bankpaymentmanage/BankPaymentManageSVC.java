/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED by gongp@2014-6-20 修改历史 Revision 2014-6-20 上午11:32:59
 */
public class BankPaymentManageSVC extends CSBizService
{
    private static final long serialVersionUID = 7416263045210187376L;

    /**
     * 关联副号码，主号码校验
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-26
     */
    public IDataset loadAddSubNumInfo(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.loadAddSubNumInfo(data);
    }

    public IDataset loadCancelChangeContractInfo(IData data) throws Exception
    {

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(mainSignInfo))
        {
            // common.error("该用户未办理总对总缴费签约业务！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_1);
        }
        return mainSignInfo;
    }

    public IDataset loadCancelSubNumInfo(IData data) throws Exception
    {

        IDataset subSignInfo = new DatasetList();

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(mainSignInfo))
        {
            // common.error("该用户未办理总对总缴费签约业务！");
            // CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_1);

            subSignInfo = UserBankMainSignInfoQry.queryUserBankSubSignByUID("01", data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(subSignInfo))
            {
                // common.error("该用户无相关副号码信息！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_229);
            }
        }
        else
        {
            UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
            subSignInfo = UserBankMainSignInfoQry.queryUserBankSubCountByUID(data.getString("SERIAL_NUMBER"), ucaData.getUserEparchyCode());

            if (IDataUtil.isEmpty(subSignInfo))
            {
                // common.error("该用户无相关副号码信息！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_229);
            }
        }

        return subSignInfo;
    }

    public IDataset subNumCheck(IData data) throws Exception
    {

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        return bean.subNumCheck(data);
    }

}
