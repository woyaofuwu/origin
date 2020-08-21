
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.grppayaccountedit;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bankinfo.BankInfoIntfViewUtil;

public abstract class GrpPayAccountEdit extends CSBizTempComponent
{

    /**
     * 根据合同号获取账户信息
     * 
     * @description
     * @author hud
     * @throws Exception
     */
    public void getAccountInfoByContract(IData inpara) throws Exception
    {
        String contractId = inpara.getString("ACCT_CONTRACT_NO");
        if ("".equals(contractId))
            return;
        // 根据合同号查询账户信息
        setAcctInfosTrancserTrancser(UCAInfoIntfViewUtil.qryGrpAcctInfoByContractNo(this, contractId, false));
    }

    /**
     * 作用： 根据账户ID查询账户信息
     */
    public void getAcctByActId(IData indata) throws Exception
    {

        String acctId = indata.getString("ACCT_ID");
        if (StringUtils.isEmpty(acctId))
            return;
        String routeId = indata.getString("ROUTE_ID");
        IData acctInfo = new DataMap();
        if (StringUtils.isNotBlank(acctId))
        {
            acctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId);
        }
        setAcctInfo(acctInfo);

    }

    /**
     * 作用： 根据集团客户id查询账户
     */
    public void getAcctByGrpId(IData indata) throws Exception
    {

        String acctGrpId = indata.getString("POP_cond_GROUP_ID", "");
        if (StringUtils.isEmpty(acctGrpId))
            return;
        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, acctGrpId, false);
        if (IDataUtil.isEmpty(result))
            return;

        String acctGrpCustId = result.getString("CUST_ID");
        if (StringUtils.isEmpty(acctGrpCustId))
            return;

        setAcctInfosTrancserTrancser(UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, acctGrpCustId));

    }

    public void getAcctByGrpSn(IData inpara) throws Exception
    {

        String grpSn = inpara.getString("GRP_SERIAL_NUMBER", "");

        if (StringUtils.isEmpty(grpSn))
            return;
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, grpSn);
        IDataset defPayRelaInfos = UCAInfoIntfViewUtil.qryGrpValidPayRelaInfoByUserId(this, userInfo.getString("USER_ID"));
        IDataset acctInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(defPayRelaInfos))
        {
            for (int i = 0; i < defPayRelaInfos.size(); i++)
            {
                String acctId = defPayRelaInfos.getData(i).getString("ACCT_ID");
                IData acctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId, false);
                acctInfos.add(acctInfo);
            }
        }

        setAcctInfosTrancserTrancser(acctInfos);

    }

    /**
     * 作用： 根据手机用户号码查询账户
     */
    public void getAcctByPsnSn(IData indata) throws Exception
    {

        String mebSn = indata.getString("GRP_SERIAL_NUMBER", "");
        if (StringUtils.isEmpty(mebSn))
            return;
        // 查询成员的用户信息
        IData userInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, mebSn);
        // 查询成员的有效付费关系信息
        IDataset defPayRelaInfos = UCAInfoIntfViewUtil.qryMebValidPayRelaInfoByUserIdAndRoute(this, userInfo.getString("USER_ID"), userInfo.getString("EPARCHY_CODE"));

        IDataset acctInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(defPayRelaInfos))
        {
            for (int i = 0; i < defPayRelaInfos.size(); i++)
            {
                String acctId = defPayRelaInfos.getData(i).getString("ACCT_ID");
                IData acctInfo = UCAInfoIntfViewUtil.qryMebAcctInfoByAcctId(this, acctId, userInfo.getString("EPARCHY_CODE"), false);
                acctInfos.add(acctInfo);
            }
        }

        setAcctInfosTrancserTrancser(acctInfos);
    }

    public abstract IData getAcctInfo();

    public abstract IDataset getAcctInfos();

    public abstract IDataset getBankList();

    public abstract String getCustId();

    public abstract String getProductId();

    /**
     * 作用： 根据上级银行获取银行数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBank(IData inpara) throws Exception
    {

        String SUPER_BANK_CODE = inpara.getString("SUPER_BANK_CODE", "");

        if ("".equals(SUPER_BANK_CODE))
            return;

        IDataset bankList = BankInfoIntfViewUtil.qryBankInfosBySuperBankCodeAndEparchyCode(this, SUPER_BANK_CODE, getTradeEparchyCode());
        setBankList(bankList);
    }

    /**
     * render component
     * 
     * @param writer
     * @param cycle
     * @throws Exception
     */
    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        if (cycle.isRewinding())
            return;
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/grppayaccountedit/grppayaccountedit.js");

        IData inpara = this.getPage().getData();
        String tag = "";
        if (IDataUtil.isNotEmpty(inpara))
        {
            tag = inpara.getString("LISTENER", "");
        }

        // tag = getAcctByGrpSn 通过集团服务号码查询账户信息
        if (tag.equals("getAcctByGrpSn"))
        {
            getAcctByGrpSn(inpara);
        }
        else if (tag.equals("getAcctByPsnSn"))
        {
            getAcctByPsnSn(inpara);
        }
        else if (tag.equals("getAcctByGrpId"))
        {
            getAcctByGrpId(inpara);
        }
        else if (tag.equals("getAccountInfoByContract"))
        {
            getAccountInfoByContract(inpara);
        }
        else if (tag.equals("queryBank"))
        {
            queryBank(inpara);
        }
        else if (tag.equals("getAcctByActId"))
        {
            getAcctByActId(inpara);
        }
        else if (tag.equals("getAcctByCustId"))
        {
            String custIdString = inpara.getString("cond_CUST_ID");
            setAcctInfosTrancserTrancser(UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, custIdString));
        }
        else
        {
            String custId = getCustId();
            String productId = getProductId();
            if (StringUtils.isNotBlank(custId))
            {

                setAcctInfosTrancserTrancser(UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, custId));
                if (StringUtils.isNotBlank(productId))
                {
                    setProductId(productId);
                }
            }
        }

    }

    public abstract void setAcctInfo(IData acctInfo);
    
    public abstract void setProductId(String productId);

    public abstract void setAcctInfos(IDataset acctInfos);

    public void setAcctInfosTrancserTrancser(IDataset acctInfos)
    {
        if (IDataUtil.isNotEmpty(acctInfos))
        {
            int len = acctInfos.size();
            for (int i = 0; i < len; i++)
            {
                IData acctInfoData = acctInfos.getData(i);
                String acctId = acctInfoData.getString("ACCT_ID", "");
                String payName = acctInfoData.getString("PAY_NAME", "");
                String payModeName = acctInfoData.getString("PAY_MODE_NAME", "");
                String eparchyName = acctInfoData.getString("EPARCHY_NAME", "");
                acctInfoData.put("DISPLAY_NAME", acctId + "|" + payName + "|" + payModeName + "|" + eparchyName);
            }
        }
        setAcctInfos(acctInfos);

    }

    public abstract void setBankList(IDataset bankList);
}
