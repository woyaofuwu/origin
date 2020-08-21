package com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.requestdata.InternetTvOpenRequestData;

public class BuildInternetTvOpenReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	InternetTvOpenRequestData reqData = (InternetTvOpenRequestData) brd;
    	reqData.setNormalUserId(reqData.getUca().getUserId());
    	reqData.setNormalSerialNumber(param.getString("SERIAL_NUMBER"));
    	reqData.setTopSetBoxProductId(param.getString("PRODUCT_ID",""));//魔百和产品ID
    	reqData.setTopSetBoxBasePkgs(param.getString("BASE_PACKAGES",""));//魔百和 必选基础包
    	reqData.setTopSetBoxPlatSvcPkgs(param.getString("PLATSVC_PACKAGES",""));//魔百和 必选优惠包
    	reqData.setTopSetBoxOptionPkgs(param.getString("OPTION_PACKAGES",""));//魔百和 可选基础包
    	reqData.setTopSetBoxSaleActiveId(param.getString("TOP_SET_BOX_SALE_ACTIVE_ID", ""));//魔百和营销活动的序列号
    	reqData.setMoProductId(param.getString("MO_PRODUCT_ID", ""));//魔百和营销活动的产品编码
    	reqData.setMoPackageId(param.getString("MO_PACKAGE_ID", ""));//魔百和营销活动的包编码
    	reqData.setArtificialServices("0");//新魔百和开户不需要上门服务
    	reqData.setDetailAddress(param.getString("WIDE_ADDRESS"));//宽带地址
    	reqData.setMoFee(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE"));//魔百和营销活动费用
    	reqData.setRsrvStr4(param.getString("RSRV_STR4"));//给PBOSS自动预约派单和回单用

    	reqData.setWorkType(param.getString("WORK_TYPE","0"));
    	
    	//BUS201907310012关于开发家庭终端调测费的需求
    	reqData.setMoProductId2(param.getString("MO_PRODUCT_ID2", ""));//魔百和调测费活动的产品编码
    	reqData.setMoPackageId2(param.getString("MO_PACKAGE_ID2", ""));//魔百和调测费活动的包编码
    	reqData.setTopSetBoxSaleActiveId2(param.getString("TOP_SET_BOX_SALE_ACTIVE_ID2", ""));//魔百和调测费活动的序列号
    	reqData.setMoFee2(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE2"));//魔百和营销活动费用
    	//BUS201907310012关于开发家庭终端调测费的需求
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
    	return new InternetTvOpenRequestData();
	}
}
