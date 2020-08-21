
package com.asiainfo.veris.crm.order.soa.person.busi.tempphonereturn.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.person.busi.tempphonereturn.order.requestdata.PhoneReturnRequestData;

public class BuildPhoneReturnRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        PhoneReturnRequestData phoneReturnReqData = (PhoneReturnRequestData) brd;
        phoneReturnReqData.setSerialNumber(param.getString("SERIAL_NUMBER"));
        phoneReturnReqData.setSimCardNo(param.getString("SIM_CARD_NO"));
        phoneReturnReqData.setBatchId(param.getString("BATCH_ID"));
        phoneReturnReqData.setFlg(param.getString("X_CHOICE_TAG"));
        phoneReturnReqData.setImis(param.getString("IMSI"));
        phoneReturnReqData.setOpc(param.getString("OPC"));
        phoneReturnReqData.setEmptyCard(param.getString("EMPTY_CARD_ID"));
        phoneReturnReqData.setKi(param.getString("KI"));
    }

    @Override
    public BaseReqData buildRequestData(IData param) throws Exception
    {
        // 构建基本请求对象
        BaseReqData brd = this.getBlankRequestDataInstance();
        brd.setXTransCode(CSBizBean.getVisit().getXTransCode());
        brd.setJoinType(param.getString("JOIN_TYPE", "0"));

        // 设置业务类型参数
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        IData tradeType = UTradeTypeInfoQry.getTradeType(tradeTypeCode, param.getString(Route.ROUTE_EPARCHY_CODE, "0898"));

        if (IDataUtil.isEmpty(tradeType))
        {
            CSAppException.apperr(BofException.CRM_BOF_001, tradeTypeCode);
        }
        brd.setTradeType(new TradeTypeData(tradeType));
        OrderDataBus orderDataBus = DataBusManager.getDataBus();
        brd.setSubmitType(orderDataBus.getSubmitType());

        if (StringUtils.isNotBlank(param.getString("BATCH_ID")))
        {
            brd.setBatchId(param.getString("BATCH_ID"));
        }
        if (StringUtils.isNotBlank(param.getString("REMARK")))
        {
            brd.setRemark(param.getString("REMARK"));
        }
        if (StringUtils.isNotBlank(param.getString("PRE_TYPE")))
        {
            brd.setPreType(param.getString("PRE_TYPE"));
        }
        if (StringUtils.isNotBlank(param.getString("IS_CONFIRM")))
        {
            brd.setIsConfirm(param.getString("IS_CONFIRM"));
        }
        if ("1".equals(param.getString("IS_QUADRIC_NOTE")))
        {
            brd.setIsConfirm("true");
        }

        brd.setBatchDealType(param.getString("BATCH_DEAL_TYPE"));
        // 身份校验方式
        brd.setCheckMode(param.getString("CHECK_MODE", "Z"));// 默认为无

        // 构建业务请求对象
        this.buildBusiRequestData(param, brd);

        brd.setPageRequestData(param);

        UcaData uca = this.buildUcaData(param);

        brd.setUca(uca);

        return brd;
    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        String userId = SeqMgr.getUserId();
        String custId = SeqMgr.getCustId();
        String acctId = SeqMgr.getAcctId();
        UcaData uca = new UcaData();
        UserTradeData utd = new UserTradeData();
        CustomerTradeData ctd = new CustomerTradeData();
        AccountTradeData atd = new AccountTradeData();

        utd.setUserId(userId);
        utd.setSerialNumber(param.getString("SERIAL_NUMBER"));
        utd.setCustId(custId);
        utd.setNetTypeCode("ZZ");
        utd.setEparchyCode("0898");
        utd.setCityCode(CSBizBean.getVisit().getCityCode());

        ctd.setCustId(custId);
        ctd.setCustName("海南移动");

        atd.setAcctId(acctId);

        uca.setUser(utd);
        uca.setCustomer(ctd);
        uca.setAccount(atd);
        uca.setProductId("-1");
        uca.setBrandCode("ZZZZ");

        return uca;
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new PhoneReturnRequestData();
    }
}
