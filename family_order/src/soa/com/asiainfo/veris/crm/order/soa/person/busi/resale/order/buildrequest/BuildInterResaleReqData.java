package com.asiainfo.veris.crm.order.soa.person.busi.resale.order.buildrequest;


import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.person.busi.resale.order.requestdata.BaseInterResaleRequestData;

public class BuildInterResaleReqData extends BaseBuilder implements IBuilder {

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
		
		BaseInterResaleRequestData  reqData = (BaseInterResaleRequestData) brd;
		reqData.setSerialNumber(param.getString("MSISDN"));
		reqData.setUserId(param.getString("USER_ID"));
		reqData.setTradeCityCode(param.getString("TRADE_CITY_CODE"));
		reqData.setTradeDepartId(param.getString("TRADE_DEPART_ID"));
		reqData.setTradeEparchyCode(param.getString("TRADE_EPARCHY_CODE"));
		reqData.setTradeStaffId(param.getString("TRADE_STAFF_ID"));
		reqData.setImsi(param.getString("IMSI"));
		reqData.setOprCode(param.getString("OPR_CODE"));
		reqData.setOprNumb(param.getString("OPR_NUMB"));
		reqData.setNewImsi(param.getString("NEWIMSI"));
		reqData.setLteUserId(param.getString("LTE_USER_ID"));
		reqData.setUpdateTime(param.getString("UPDATE_TIME"));
	    reqData.setSvcInfos(param.getDataset("SVC_INFOS"));
	    reqData.setResData(param.getDataset("RES_DATA"));
		
	}
	
	@Override
	public UcaData buildUcaData(IData param) throws Exception
    {

        UcaData uca = new UcaData();

        String strCustCityCode = param.getString("TRADE_CITY_CODE", "");
        String cityCode = "".equals(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode;
        param.put("CITY_CODE", cityCode);
        // 设置三户资料对象
        // 构建用户资料
        IData userInfo = new DataMap();
        userInfo.put("USER_ID", param.getString("USER_ID"));
        userInfo.put("CUST_ID", "-1");
        userInfo.put("USECUST_ID", "-1");
        userInfo.put("SERIAL_NUMBER", param.getString("MSISDN"));
        userInfo.put("NET_TYPE_CODE", "00");
        userInfo.put("EPARCHY_CODE", param.getString("TRADE_EPARCHY_CODE","0898"));
        userInfo.put("NET_TYPE_CODE", BofConst.NET_TYPE_CODE);
       
        uca.setUser(new UserTradeData(userInfo));
        uca.setCustomer(new CustomerTradeData());
        uca.setCustPerson(new CustPersonTradeData());
        uca.setAccount(new AccountTradeData());
        uca.setAcctBlance("0");
        uca.setLastOweFee("0");
        uca.setRealFee("0");
        
        return uca;
    }
	

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new BaseInterResaleRequestData();
	}
	
	@Override
	public BaseReqData buildRequestData(IData param) throws Exception
	{
		IData cond = new DataMap();

		// 构建三户资料对象
		UcaData uca = this.buildUcaData(param);
		uca.setSubmitType(DataBusManager.getDataBus().getSubmitType());
		DataBusManager.getDataBus().setUca(uca);

		// 构建基本请求对象
		BaseReqData brd = this.getBlankRequestDataInstance();
		brd.setUca(uca);
		brd.setXTransCode(CSBizBean.getVisit().getXTransCode());
		brd.setJoinType(param.getString("JOIN_TYPE","0"));

		// 设置业务类型参数
		String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
		 //param.getString(Route.ROUTE_EPARCHY_CODE, "0898")
		IData tradeType = UTradeTypeInfoQry.getTradeType(tradeTypeCode, uca.getUserEparchyCode());

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
		
		brd.setPageRequestData(param);

		// 构建业务请求对象
		this.buildBusiRequestData(param, brd);
		return brd;
	}

}
