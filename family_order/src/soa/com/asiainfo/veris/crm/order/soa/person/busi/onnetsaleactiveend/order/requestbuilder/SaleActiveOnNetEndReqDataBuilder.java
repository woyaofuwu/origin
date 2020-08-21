
package com.asiainfo.veris.crm.order.soa.person.busi.onnetsaleactiveend.order.requestbuilder;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.onnetsaleactiveend.order.requestdata.SaleActiveOnNetEndReqData;

public class SaleActiveOnNetEndReqDataBuilder extends BaseBuilder implements IBuilder
{

    private void buildBaseRequestData(SaleActiveOnNetEndReqData saleActiveOnNetEndReq, IData param) throws Exception
    {
        saleActiveOnNetEndReq.setProductId(param.getString("PRODUCT_ID"));
        saleActiveOnNetEndReq.setPackageId(param.getString("PACKAGE_ID"));
        saleActiveOnNetEndReq.setCampnType(param.getString("CAMPN_TYPE"));
        saleActiveOnNetEndReq.setRelationTradeId(param.getString("RELATION_TRADE_ID"));
        saleActiveOnNetEndReq.setEndDate(saleActiveOnNetEndReq.getAcceptTime());
        if (StringUtils.isNotBlank(param.getString("FORCE_END_DATE")))
        {
            saleActiveOnNetEndReq.setEndDate(param.getString("FORCE_END_DATE"));
            saleActiveOnNetEndReq.setForceEndDate(param.getString("FORCE_END_DATE"));
        }
        saleActiveOnNetEndReq.setRemark(param.getString("REMARK"));
        saleActiveOnNetEndReq.setEndDateValue(param.getString("END_DATE_VALUE"));
        saleActiveOnNetEndReq.setTerminalReturnTag(param.getString("IS_RETURN", "0"));
        saleActiveOnNetEndReq.setCallType(param.getString("CALL_TYPE"));
        saleActiveOnNetEndReq.setReturnfee(param.getString("RETURNFEE"));
        saleActiveOnNetEndReq.setIntface(param.getString("INTERFACE","0"));
        if (StringUtils.isNotBlank(param.getString("WIDE_YEAR_ACTIVE_BACK_FEE")))
        {
            saleActiveOnNetEndReq.setWideYearActiveBackFee(param.getString("WIDE_YEAR_ACTIVE_BACK_FEE"));
        }else{
        	saleActiveOnNetEndReq.setWideYearActiveBackFee("");
        }
        
    }

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        SaleActiveOnNetEndReqData saleActiveOnNetEndReq = (SaleActiveOnNetEndReqData) brd;

        buildBaseRequestData(saleActiveOnNetEndReq, param);        
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new SaleActiveOnNetEndReqData();
    }
    
    private void createBusiMainTradeData(BusiTradeData bd) throws Exception
	{
    	
	}

}
