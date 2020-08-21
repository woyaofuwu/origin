package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate.order.requestdata.NoPhoneTopSetBoxCreateRequestData;

public class BuildNoPhoneTopSetBoxCreateReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	NoPhoneTopSetBoxCreateRequestData reqData = (NoPhoneTopSetBoxCreateRequestData) brd;
    	reqData.setTopSetBoxProductId(param.getString("TOP_SET_BOX_PRODUCT_ID",""));//魔百和产品ID
    	reqData.setTopSetBoxBasePkgs(param.getString("BASE_PACKAGES",""));//魔百和 必选基础包
    	reqData.setTopSetBoxOptionPkgs(param.getString("OPTION_PACKAGES",""));//魔百和 可选基础包
    	reqData.setArtificialServices("0");//新魔百和开户不需要上门服务
    	reqData.setDetailAddress(param.getString("WIDE_ADDRESS"));//宽带地址
    	reqData.setRsrvStr4(param.getString("RSRV_STR4"));//给PBOSS自动预约派单和回单用
    	
    	reqData.setWorkType(param.getString("WORK_TYPE","0"));
    	
        if("Y".equals(param.getString("NEW_TAG"))){//新数据模型下的手机号码   duhj
            reqData.setSerialNumberB(param.getString("NEW_SERIAL_NUMBER_B"));//绑定魔百和手机号码
        }else {
            reqData.setSerialNumberB(param.getString("SERIAL_NUMBER_B"));//绑定魔百和手机号码
        }
        
        reqData.setTopSetBoxTime(param.getString("TOP_SET_BOX_TIME")); //魔百和受理时长
        
        reqData.setTopSetBoxFee(param.getString("TOP_SET_BOX_FEE"));   //魔百和受理时长费用

    	//魔百和押金
    	if (StringUtils.isNotBlank(param.getString("TOP_SET_BOX_DEPOSIT", "")))
    	{
    		reqData.setTopSetBoxDeposit(Integer.valueOf(param.getString("TOP_SET_BOX_DEPOSIT"))*100);
    	}
    	else
        {
        	reqData.setTopSetBoxDeposit(0);
        }
    }

	public BaseReqData getBlankRequestDataInstance()
	{
    	return new NoPhoneTopSetBoxCreateRequestData();
	}
}
