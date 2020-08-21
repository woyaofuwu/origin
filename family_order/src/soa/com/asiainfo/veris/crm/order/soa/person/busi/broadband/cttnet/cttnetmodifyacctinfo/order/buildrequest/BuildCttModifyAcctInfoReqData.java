
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyacctinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyacctinfo.order.requestdata.CttModifyAcctInfoReqData;

public class BuildCttModifyAcctInfoReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CttModifyAcctInfoReqData reqData = (CttModifyAcctInfoReqData) brd;

        reqData.setPayModeCode(param.getString("PAY_MODE_CODE"));// 帐户类别
        reqData.setPayName(param.getString("PAY_NAME").contains("*") ? reqData.getUca().getAccount().getPayName() : param.getString("PAY_NAME"));// 帐户名称
        reqData.setBankCode(param.getString("BANK_CODE"));// 银行名称
        reqData.setBankAcctNo(param.getString("BANK_ACCT_NO").contains("*") ? reqData.getUca().getAccount().getBankAcctNo() : param.getString("BANK_ACCT_NO"));// 银行帐号
        reqData.setRsrvStr3(param.getString("RSRV_STR3"));// 账单类型
        reqData.setRsrvStr6(param.getString("RSRV_STR6"));// 银行协议号
        reqData.setContractNo(param.getString("CONTRACT_NO").contains("*") ? reqData.getUca().getAccount().getContractNo() : param.getString("CONTRACT_NO"));// 合同号
        reqData.setSuperbankCode(param.getString("SUPERBANK_CODE"));// 上级银行
        reqData.setRemark(param.getString("REMARK"));// 备注

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {

        return new CttModifyAcctInfoReqData();
    }

}
