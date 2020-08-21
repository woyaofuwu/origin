package com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.InterRoamDayException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.requestdata.InterRoamDayReqData;

public class BuildInterRoamDayReqData extends BaseBuilder implements IBuilder {

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brq)
			throws Exception {
		// TODO Auto-generated method stub
		InterRoamDayReqData request = (InterRoamDayReqData) brq;
		request.setProdId(param.getString("ELEMENT_ID"));
		
		if("".equals(param.getString("ELEMENT_ID",""))){
			request.setProdId(param.getString("PROD_ID"));
		}
		request.setActiveInstId(param.getString("INST_ID"));
		request.setUserType(param.getString("USER_TYPE","00"));
		request.setGroupName(param.getString("GROUP_NAME"));
		request.setProvCode(param.getString("PROV_CODE"));
		request.setUpdateTime(param.getString("UPDATE_TIME"));
		request.setProdInsId(param.getString("PROD_INST_ID"));
		
		request.setProdType(param.getString("PROD_TYPE"));
		request.setProdStat(param.getString("PROD_STAT"));
		//由于国漫航空99992647套餐会连带开通99992649，TD_B_ELEM_RELADEAL_ELEM，而连带订购套餐时无法处理20181029114907格式的日期偏移6个月的结束日期
		//故将99992647的订购日期由20181029114907格式转为2018/10/29 11:49:07格式
		if(!"".equals(param.getString("VALID_DATE"))&&"99992647".equals(param.getString("ELEMENT_ID"))){
			request.setValidDate(SysDateMgr.decodeTimestamp(param.getString("VALID_DATE"), "yyyy-MM-dd HH:mm:ss"));
		}else{
			request.setValidDate(param.getString("VALID_DATE"));
		}
		request.setExpireDate(param.getString("EXPIRE_TIME",SysDateMgr.END_DATE_FOREVER));
		request.setFirstTime(param.getString("FIRST_TIME"));
		request.setEndTime(param.getString("END_TIME"));
		request.setRelation_stat(param.getString("RELATION_STAT"));
		
		if("".equals(param.getString("RELATION_STAT",""))){
			
			request.setRelation_stat("0".equals(param.getString("MODIFY_TAG","0")) ? "01":"");
			//前台退订
			if("1".equals(param.getString("MODIFY_TAG","0")))
			{
				request.setRelation_stat("03");
			}
		} 
		request.setFee(param.getString("FEE"));
		request.setFeeType(param.getString("FEE_TYPE"));
		request.setFeeCycle(param.getString("FEE_CYCLE"));
		request.setChannel(param.getString("CHANNEL"));
		String newDiscntCode = param.getString("ELEMENT_ID");
		IData newResult = new DataMap();
		//查询包下所有优惠
		IDataset  upackageEleInfos=UPackageElementInfoQry.getElementInfoByGroupId(PersonConst.INTER_ROAM_DAY_PACKAGE_ID);
	       if(StringUtils.isNotBlank(newDiscntCode)){
	    	   for(int k=0; k<upackageEleInfos.size();k++){
		    	     String  prarmdiscode=upackageEleInfos.getData(k).getString("DISCNT_CODE");
		    	     if(newDiscntCode.equals(prarmdiscode)){   	 
		    	    	newResult.putAll(upackageEleInfos.getData(k));
		    	     }
		      }
	       }
//		IData newResult = bean.getPackageDiscnt("99990000", newDiscntCode);
		// 判断用户的预存款是不是够
	    String inModeCode = param.getString("IN_MODE_CODE");//BOSS发起的还是需要判断余额
		if("01".equals(param.getString("RELATION_STAT","")) && "0".equals(inModeCode)){
		String serialNumber = param.getString("SERIAL_NUMBER");
		String userId = UserInfoQry.getUserinfo(serialNumber).getData(0).getString("USER_ID");
		String discntFee = newResult.getString("RSRV_STR1", "0"); // 获取办理的费用信息
		double discntFeeInt = Double.parseDouble(discntFee);
		IDataset oweInfo = AcctCall.queryUserEveryBalanceFeeNew(userId, "1");
		
		double nCreditValue = 0;
		IDataset ids = AcctCall.getUserCreditInfos("0", userId);
		if (IDataUtil.isNotEmpty(ids)) {
			IData idCreditInfo = ids.first();
			nCreditValue = idCreditInfo.getInt("CREDIT_VALUE");
		}

		if (IDataUtil.isNotEmpty(oweInfo)) {
			String balance = oweInfo.getData(0).getString("RSRV_NUM3", "0");// 客户余额
			double balanceInt = Double.parseDouble(balance);

			double nFee = discntFeeInt - balanceInt - nCreditValue;
			if (nFee > 0) // discntFeeInt > balanceInt
			{
				CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_300005,nFee / 100);
			}
		} else {
			double nFee = discntFeeInt - nCreditValue;
			if (nFee > 0) // discntFeeInt
			{
				CSAppException.apperr(
						InterRoamDayException.CRM_INTERROAMDAY_300005,
						nFee / 100);
			}
		}
	}
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new InterRoamDayReqData();
	}

}
