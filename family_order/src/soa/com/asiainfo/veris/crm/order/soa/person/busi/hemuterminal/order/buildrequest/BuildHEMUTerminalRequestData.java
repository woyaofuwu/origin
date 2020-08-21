
package com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal.order.requestdata.HEMUTerminalReqData;

public class BuildHEMUTerminalRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	HEMUTerminalReqData hemuRd = (HEMUTerminalReqData) brd;
    	hemuRd.setRemark(param.getString("REMARK"));
    	hemuRd.setAction_type(param.getString("TERMINAL_TRADE_TYPE",""));
    	hemuRd.setInstId(param.getString("INST_ID",""));
    	hemuRd.setDeposit(param.getString("DEPOSIT",""));
    	hemuRd.setTerminalId(param.getString("RES_ID",""));
    	hemuRd.setDepositTradeId(param.getString("DEPOSIT_TRADE_ID",""));
    	hemuRd.setOldTerminalId(param.getString("OLD_RES_ID",""));
    	hemuRd.setTerminalType(param.getString("MODERMTYPE",""));
    	hemuRd.setProductId(param.getString("PRODUCT_ID",""));
    	hemuRd.setPackageId(param.getString("PACKAGE_ID",""));
    	hemuRd.setIsHSW(StringUtils.isNotBlank(param.getString("PRODUCT_ID",""))?"1":"0");//是否为和商务用户1：是，0：不是
    	hemuRd.setTerminalName(param.getString("RES_KIND_NAME",""));
    	hemuRd.setReturnDate(param.getString("RETURN_DATE"));
    }

    /**
	 * @Description：TODO
	 * @param:@param brd
	 * @param:@param string
	 * @return void
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-17下午06:20:56
	 */
	private void chanMainTrade(BaseReqData brd, String string) {
		
	}

	@Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new HEMUTerminalReqData();
    }

}
