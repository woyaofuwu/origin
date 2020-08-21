
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order.buildrequest;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order.requestdata.ModifyAcctInfoReqData;

/**
 * 账户资料变更接口调用的Build类
 * 
 * @author liutt
 */
public class BuildModifyAcctInfoIntfReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ModifyAcctInfoReqData reqData = (ModifyAcctInfoReqData) brd;
        AccountTradeData oldAcct = brd.getUca().getUserOriginalData().getAccount();
        UserTradeData userdata = brd.getUca().getUser();
        // 查询用户构机信息
        IDataset saleActiveSet = UserSaleActiveInfoQry.queryPurchaseInfo(userdata.getUserId(), userdata.getEparchyCode(), "0");
        if (IDataUtil.isNotEmpty(saleActiveSet))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_415);// 90001: 该用户已办理过不能改帐户资料的约定消费购机活动
        }

        reqData.setPayModeCode(StringUtils.isBlank(param.getString("PAY_MODE_CODE")) ? oldAcct.getPayModeCode() : param.getString("PAY_MODE_CODE"));// 帐户类别
        reqData.setPayName(StringUtils.isBlank(param.getString("PAY_NAME")) ? oldAcct.getPayName() : param.getString("PAY_NAME"));// 帐户名称
        reqData.setBankCode(StringUtils.isBlank(param.getString("BANK_CODE")) ? oldAcct.getBankCode() : param.getString("BANK_CODE"));// 银行名称
        reqData.setBankAcctNo(StringUtils.isBlank(param.getString("BANK_ACCT_NO")) ? oldAcct.getBankAcctNo() : param.getString("BANK_ACCT_NO"));// 银行帐号
        reqData.setRsrvStr5(StringUtils.isBlank(param.getString("RSRV_STR5")) ? oldAcct.getRsrvStr5() : param.getString("RSRV_STR5"));// 欠费周期
        reqData.setRsrvStr6(StringUtils.isBlank(param.getString("RSRV_STR6")) ? oldAcct.getRsrvStr6() : param.getString("RSRV_STR6"));// 银行协议号
        reqData.setContractNo(StringUtils.isBlank(param.getString("CONTRACT_NO")) ? oldAcct.getContractNo() : param.getString("CONTRACT_NO"));// 合同号
        reqData.setPostCode(StringUtils.isBlank(param.getString("POST_CODE")) ? oldAcct.getRsrvStr3() : param.getString("POST_CODE"));// 邮寄编码
        reqData.setPostAddress(StringUtils.isBlank(param.getString("POST_ADDRESS")) ? oldAcct.getRsrvStr10() : param.getString("POST_ADDRESS"));// 邮寄地址
        reqData.setSuperbankCode(param.getString("SUPERBANK_CODE"));// 上级银行
        if (!StringUtils.equals("0", reqData.getPayModeCode()) && StringUtils.isNotBlank(reqData.getBankCode()) && StringUtils.isBlank(param.getString("SUPERBANK_CODE")))
        {
            String pdata_id = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", new String[]
            { "EPARCHY_CODE", "BANK_CODE" }, "SUPER_BANK_CODE", new String[]
            { CSBizBean.getVisit().getStaffEparchyCode(), reqData.getBankCode()});
            reqData.setSuperbankCode(pdata_id); // 取上级银行
            if (StringUtils.isBlank(pdata_id))
            {
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_143);
            }
        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {

        return new ModifyAcctInfoReqData();
    }

}
