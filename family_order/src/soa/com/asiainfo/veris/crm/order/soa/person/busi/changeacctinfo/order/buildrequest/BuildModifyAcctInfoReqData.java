
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order.buildrequest;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order.requestdata.ModifyAcctInfoReqData;

public class BuildModifyAcctInfoReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ModifyAcctInfoReqData reqData = (ModifyAcctInfoReqData) brd;

        reqData.setPayModeCode(param.getString("PAY_MODE_CODE"));// 帐户类别
        reqData.setPayName(param.getString("PAY_NAME").contains("*") ? reqData.getUca().getAccount().getPayName() : param.getString("PAY_NAME"));// 帐户名称
        reqData.setBankCode(param.getString("BANK_CODE"));// 银行名称
        reqData.setBankAcctNo(param.getString("BANK_ACCT_NO").contains("*") ? reqData.getUca().getAccount().getBankAcctNo() : param.getString("BANK_ACCT_NO"));// 银行帐号
        reqData.setRsrvStr6(param.getString("RSRV_STR6"));// 银行协议号
        reqData.setContractNo(param.getString("CONTRACT_NO").contains("*") ? reqData.getUca().getAccount().getContractNo() : param.getString("CONTRACT_NO"));// 合同号
        reqData.setPostCode(param.getString("POST_CODE"));
        reqData.setPostAddress(param.getString("POST_ADDRESS","").contains("*")?reqData.getUca().getAccount().getRsrvStr10() : param.getString("POST_ADDRESS",""));
        reqData.setSuperbankCode(param.getString("SUPERBANK_CODE"));// 上级银行
        reqData.setmainTradeRemark(param.getString("MAIN_TRADE_REMARK",""));
        
        if (StringUtils.isNotBlank(param.getString("BANK_CODE")) && StringUtils.isBlank(param.getString("SUPERBANK_CODE")))
        {
            String pdata_id = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", new String[]
            { "EPARCHY_CODE", "BANK_CODE" }, "SUPER_BANK_CODE", new String[]
            { CSBizBean.getVisit().getStaffEparchyCode(), param.getString("BANK_CODE") });
            reqData.setSuperbankCode(pdata_id); // 取上级银行
        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {

        return new ModifyAcctInfoReqData();
    }

}
