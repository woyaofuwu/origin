package com.asiainfo.veris.crm.order.soa.person.busi.cttTerminalSale.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.cttTerminalSale.order.trade.CttTerminalSaleReqData;

public class BuildCttTerminalSaleReqData extends BaseBuilder implements IBuilder{
	
	@Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
		CttTerminalSaleReqData terminalSaleReqData = (CttTerminalSaleReqData) brd;
		
		terminalSaleReqData.setTerminalType(param.getString("TERMINAL_TYPE"));
		terminalSaleReqData.setTerminalTypeName(param.getString("TERMINAL_TYPE_NAME"));
		terminalSaleReqData.setTerminalCode(param.getString("TERMINAL_CODE"));
		terminalSaleReqData.setTerminalCodeName(param.getString("TERMINAL_CODE_NAME"));
		terminalSaleReqData.setTerminalPrice(param.getString("TERMINAL_PRICE"));
		terminalSaleReqData.setTerminalNumber(param.getString("TERMINAL_NUMBER"));
		terminalSaleReqData.setTerminalTotalPrice(param.getString("TERMINAL_TOTAL_PRICE"));
		terminalSaleReqData.setFeeTypeCode(param.getString("FEE_TYPE_CODE"));
    }
	
	@Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new CttTerminalSaleReqData();
    }
}
