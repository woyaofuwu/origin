
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changepayrel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;

public class PayRelationSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(PayRelationSVC.class);

    private static final long serialVersionUID = 1L;

    // 已用
    /**
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getAdvChgNewSnInfo(IData data) throws Exception
    {
        String pdata_id = "";
        IDataset dataset = new DatasetList();

        IData inParams = new DataMap();
        inParams.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        inParams.put("X_CONN_DB_CODE", getVisit().getStaffEparchyCode());
        inParams.put("SERIAL_NUMBER", data.get("SERIAL_NUMBER"));
        inParams.put("REMOVE_TAG", "0");
        inParams.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE"));

        // 用户资料
        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (userInfo == null || userInfo.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据新号码没找到对应的用户资料");
        }
        else
        {

            // 对新号码进行较验
            //if (!userInfo.getString("NET_TYPE_CODE").equals("12"))
        	if (!userInfo.getString("NET_TYPE_CODE").equals("00"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码不是固话用户!");
            }
        }

        inParams.put("USER_ID", userInfo.get("USER_ID"));
        inParams.put("CUST_ID", userInfo.get("CUST_ID"));

        // 客户资料
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));

        // 帐户资料
        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userInfo.getString("USER_ID"));

        String oldUserEarCode = data.getString("OLDUSEREREA");
        String oldAcctId = data.getString("OLDACCTID");
        if (!userInfo.getString("EPARCHY_CODE").equals(oldUserEarCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "非同一归属区,不能办理该业务!");
        }
        if (oldAcctId.equals(acctInfo.getString("ACCT_ID")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "不能和业务账户相同的账户进行合帐！");
        }

        String bank_code = acctInfo.getString("BANK_CODE");
        if (bank_code != null && !"".equals(bank_code))
        {
            IDataset bankInfo = BankInfoQry.getBankByBankCtt("ZZZZ", null, bank_code, null);
            if (!bankInfo.isEmpty())
            {
                pdata_id = bankInfo.getData(0).getString("SUPER_BANK_CODE");
            }
        }

        acctInfo.put("SUPER_BANK_CODE", pdata_id);

        // 其它信息
        IData otherInfo = new DataMap();
        otherInfo.put("START_CYCLE_ID", SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD));
        otherInfo.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());

        IData oweFee = AcctCall.getOweFeeByUserId(userInfo.getString("USER_ID"));
        String fee1 = oweFee.size() > 0 ? oweFee.getString("LAST_OWE_FEE") : "0";
        String fee3 = oweFee.size() > 0 ? oweFee.getString("ACCT_BALANCE") : "0";
        if (Integer.parseInt(fee1) > 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该服务号码有往月欠费！");
        }
        String bank_code1 = acctInfo.getString("BANK_CODE");
        String bank_name = "";
        if (bank_code != null && !"".equals(bank_code))
        {
            IDataset bankinfos = BankInfoQry.getBankByBankCtt("ZZZZ", null, bank_code1, null);
            if (bankinfos != null && !bankinfos.isEmpty() && !(bankinfos.size() <= 0))
            {
                pdata_id = bankinfos.getData(0).getString("SUPER_BANK_CODE");
                bank_name = bankinfos.getData(0).getString("BANK_NAME");
            }

        }
        custInfo.put("RSRV_NUM3", fee3);
        custInfo.put("BRAND_CODE1", bank_code1);
        dataset.add(0, userInfo);
        dataset.add(1, custInfo);
        dataset.add(2, acctInfo);
        dataset.add(3, otherInfo);
        return dataset;
    }

    /**
     * 取业务参数
     */
    public IData getBusiParam(IData inParams) throws Exception
    {
        String str = DiversifyAcctUtil.getFirstDayThisAcct(inParams.getString("USER_ID"));// 获取用户当前账期
        IData result = new DataMap();
        result.put("START_CYCLE_ID", SysDateMgr.decodeTimestamp(str, "yyyyMMdd"));
        return result;
    }

    /*
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset isAcctOnly(IData data) throws Exception
    {
        String acctId = data.getString("ACCT_ID");
        String default_tag = data.getString("DEFAULT_TAG");
        String act_tag = data.getString("ACT_TAG");
        int count = 0;
        IDataset dataset = PayRelaInfoQry.getAllValidUserPayByAcctId(acctId);
        if (dataset.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到正常的付费关系");
        }
        else
        {
            IDataset temp = new DatasetList();
            IData tmp = new DataMap();

            for (int i = 0; i < dataset.size(); i++)
            {
                tmp.put("USER_ID", dataset.get(i, "USER_ID"));
                temp = IDataUtil.idToIds(UcaInfoQry.qryUserInfoByUserId(dataset.getData(i).getString("USER_ID")));
                if (temp.size() == 1 && "0".equals(temp.get(0, "REMOVE_TAG")))
                {
                    count = count + 1;
                }
                if (count > 1)
                {// by zengzb 当有一个帐户给多个用户付费的情况,大于2,则直接退出循环
                    break;
                }
            }
        }

        if (count == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据付费关系用户ID未获取到正常用户！");
        }
        // 等于1,则是独立帐户,大于且不等于1 则不是独立帐户
        IData returnData = new DataMap();
        String acctOnly = count == 1 ? "TRUE" : "FALSE";
        returnData.put("ACCT_ONLY", acctOnly);
        IDataset returnSet = new DatasetList();
        returnSet.add(returnData);
        return returnSet;
    }

}
