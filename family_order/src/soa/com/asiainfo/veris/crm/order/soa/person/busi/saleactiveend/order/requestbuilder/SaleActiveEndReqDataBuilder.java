
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.requestbuilder;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.SaleActiveEndBean;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.requestdata.SaleActiveEndReqData;

public class SaleActiveEndReqDataBuilder extends BaseBuilder implements IBuilder
{

    private void buildBaseRequestData(SaleActiveEndReqData saleActiveEndReq, IData param) throws Exception
    {
        saleActiveEndReq.setProductId(param.getString("PRODUCT_ID"));
        saleActiveEndReq.setPackageId(param.getString("PACKAGE_ID"));
        saleActiveEndReq.setCampnType(param.getString("CAMPN_TYPE"));
        saleActiveEndReq.setRelationTradeId(param.getString("RELATION_TRADE_ID"));
        saleActiveEndReq.setEndDate(saleActiveEndReq.getAcceptTime());
        if (StringUtils.isNotBlank(param.getString("FORCE_END_DATE")))
        {
            saleActiveEndReq.setEndDate(param.getString("FORCE_END_DATE"));
            saleActiveEndReq.setForceEndDate(param.getString("FORCE_END_DATE"));
        }
        saleActiveEndReq.setRemark(param.getString("REMARK"));
        saleActiveEndReq.setEndDateValue(param.getString("END_DATE_VALUE"));
        saleActiveEndReq.setTerminalReturnTag(param.getString("IS_RETURN", "0"));
        saleActiveEndReq.setCallType(param.getString("CALL_TYPE"));
        saleActiveEndReq.setReturnfee(param.getString("RETURNFEE"));
        saleActiveEndReq.setYSReturnfee(param.getString("YSRETURNFEE"));
        saleActiveEndReq.setIntface(param.getString("INTERFACE","0"));
        saleActiveEndReq.setTrueReturnFeeCode(param.getString("TRUE_RETURNFEE_COST","0"));
        saleActiveEndReq.setTrueReturnFeePrice(param.getString("TRUE_RETURNFEE_PRICE","0"));
        saleActiveEndReq.setSrcPage(param.getString("SRC_PAGE"));
        if (StringUtils.isNotBlank(param.getString("WIDE_YEAR_ACTIVE_BACK_FEE")))
        {
            saleActiveEndReq.setWideYearActiveBackFee(param.getString("WIDE_YEAR_ACTIVE_BACK_FEE"));
        }else{
        	saleActiveEndReq.setWideYearActiveBackFee("");
        }
        saleActiveEndReq.setBackTerm(param.getString("BACK_TERM",""));
    }

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        SaleActiveEndReqData saleActiveEndReq = (SaleActiveEndReqData) brd;

        buildBaseRequestData(saleActiveEndReq, param);

        SaleActiveEndBean saleActiveEndBean = BeanManager.createBean(SaleActiveEndBean.class);
        saleActiveEndBean.buildBusiRequestObj(saleActiveEndReq);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new SaleActiveEndReqData();
    }

}
