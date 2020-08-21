
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyacctinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;

/**
 * 账户资料修改服务类
 */
public class CttModifyAcctInfoSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(CttModifyAcctInfoSVC.class);

    private static final long serialVersionUID = 1L;

    // 判断是否是统付用户
    public IDataset checkUserSpecialepay(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String payModeCode = input.getString("PAY_MODE_CODE");
        String erroInfo = "";
        String flag = "0";
        IData returnInfo = new DataMap();
        IData info = new DataMap();
        IDataset result = PayRelaInfoQry.getSpecPayByUserId(userId);// 查询用户统付信息
        if (IDataUtil.isNotEmpty(result))
        {
            flag = "1";
            info = UcaInfoQry.qryUserInfoByUserId(result.getData(0).getString("USER_ID_A"));
        }
        else
        {
            flag = "0";
        }

        if (StringUtils.equals("0", payModeCode) && StringUtils.equals("1", flag))
        {
            erroInfo = "用户有统付，统付编码:" + info.getString("SERIAL_NUMBER") + "，是否要将帐户类型改成现金？\n \n选择[确定]继续办理本业务，[取消]退出本业务？";
            returnInfo.put("X_RESULT_CODE", "1");
            returnInfo.put("PLUG_TYPE", "2");
            returnInfo.put("X_RESULTINFO", erroInfo);
        }
        IDataset dataset = new DatasetList();
        dataset.add(returnInfo);
        return dataset;
    }

    // 根据BANK_ACCT_NO获取账户名称
    public IDataset getAcctPayName(String bankAcctNo) throws Exception
    {
        return AcctInfoQry.getAcctPayName(bankAcctNo, this.getTradeEparchyCode());
    }

    /**
     *获取用户账期相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserAcctDayInfo(IData input) throws Exception
    {

        IData acctPayInfo = new DataMap();
        String userId = input.getString("USER_ID");
        IDataset userAcctDays = UserAcctDayInfoQry.getUserAcctDay(userId);// 据USER_ID查询用户的结账日以及首次出账日
        IData userAcctDay = new DataMap();
        if (userAcctDays != null && userAcctDays.size() > 0)
        {
            userAcctDay = userAcctDays.getData(0);
        }
        else
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_137);// "获取用户账期资料无数据，业务办理失败！"
        }

        if (!StringUtils.equals("1", userAcctDay.getString("ACCT_DAY")))
        {
            acctPayInfo.put("ACCT_DAY_TAG", "0");// 做页面标记"0"代表非自然月，1代表自然月

        }
        else
        {
            acctPayInfo.put("ACCT_DAY_TAG", "1");
        }

        // 判断是否存在预约账期
        IData bookUserAcctDays = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);//
        if (bookUserAcctDays != null && bookUserAcctDays.getString("NEXT_ACCT_DAY") != null)
        {
            acctPayInfo.put("NEXT_ACCT_DAY", bookUserAcctDays.getString("NEXT_ACCT_DAY"));
        }
        else
        {
            acctPayInfo.put("NEXT_ACCT_DAY", "0");
        }

        if ("0".equals(acctPayInfo.get("ACCT_DAY_TAG")))
        {// 判断非自然月，办理托收时的生效时间，给前台展示
            // 获取下帐期第一天
            String nextAcct = SysDateMgr.getFirstDayNextAcct(SysDateMgr.getSysDate(), userAcctDay.getString("ACCT_DAY"), userAcctDay.getString("FIRST_DATE"));
            acctPayInfo.put("BOOK_ACCT_DAY", nextAcct);
        }
        else
        {
            acctPayInfo.put("BOOK_ACCT_DAY", SysDateMgr.getSysDate());
        }

        IDataset dataset = new DatasetList();
        dataset.add(acctPayInfo);
        return dataset;
    }

    // public IDataset checkUserSpecialepayAndAcctNoUniq(IData params) throws Exception
    // {
    // // 对非现金支付的账户进行唯一性检查
    // // 根据银行账号和账户名称来判断---
    // if (!"0".equals(params.getString("PAY_MODE_CODE")) && !"5".equals(params.getString("PAY_MODE_CODE")) &&
    // !"".equals(params.getString("BANK_CODE")) && !"".equals(params.getString("BANK_ACCT_NO")))
    // {
    // IDataset result = getAcctPayName(params.getString("BANK_ACCT_NO"));
    //
    // if (result != null && result.size() > 0)
    // {
    // for (int i = 0; i < result.size(); i++)
    // {
    // String payName = result.getData(i).getString("PAY_NAME");
    // String userId = params.getString("USER_ID");
    // String userIdTemp = result.getData(i).getString("USER_ID");
    // String pdpayname = params.getString("PAY_NAME");
    // if (!userId.equals(userIdTemp) && !payName.equals(pdpayname))
    // {
    // CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_30);// 特殊限制判断:该银行帐号只能对应唯一的帐户名称！
    // }
    // }
    // }
    // }
    // IData returnInfo = checkUserSpecialepay(params.getString("USER_ID"), params.getString("PAY_MODE_CODE"));
    // IDataset dataset = new DatasetList();
    // dataset.add(returnInfo);
    // return dataset;
    // }

    /**
     * 根据上级银行获取银行数据
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryBankInfoBySup(IData params) throws Exception
    {
        String super_bank_code = params.getString("SUPERBANK_CODE");
        return BankInfoQry.getBankBySuperBankCtt(super_bank_code, null);
    }

    /**
     * 根据银行名称或编码模糊查询
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryBurBankInfo(IData params) throws Exception
    {
        String super_bank_code = params.getString("SUPERBANK_CODE");
        String eparchy_code = CSBizBean.getTradeEparchyCode();
        String bank_code = params.getString("BANK_OR_CODE", "").toUpperCase();
        String bank = params.getString("BANK_OR_CODE", "").toUpperCase();
        return BankInfoQry.getBankByBankCtt(eparchy_code, super_bank_code, bank_code, bank);
    }
}
