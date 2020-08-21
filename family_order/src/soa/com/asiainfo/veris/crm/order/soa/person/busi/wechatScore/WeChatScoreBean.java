package com.asiainfo.veris.crm.order.soa.person.busi.wechatScore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;

public class WeChatScoreBean extends CSBizBean{

	/**
	 * 查询用户的信息
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public IData queryCustInfo(String serialNumber)throws Exception{
		IData custInfo=new DataMap();
		
		IDataset custInfoList = CustomerInfoQry.queryCustInfoBySN(serialNumber);
		
		if(IDataUtil.isNotEmpty(custInfoList)){
			IData custData=custInfoList.getData(0);
			
			String custId=custData.getString("CUST_ID","");
			String custName=custData.getString("CUST_NAME","");
			
			IData personData = UcaInfoQry.qryPerInfoByCustId(custId);
			if(IDataUtil.isNotEmpty(personData)){
				custInfo.put("Birthday", personData.getString("BIRTHDAY",""));
				custInfo.put("Address", personData.getString("HOME_ADDRESS",""));
				
				String sex=personData.getString("SEX","");
				if(sex.equals("M")){	//男
					custInfo.put("Sex", "1");
				}else if(sex.equals("F")){	//女
					custInfo.put("Sex", "2");
				}
				
				
			}
			
			custInfo.put("CustomerName", custName);
			custInfo.put("Msisdn", serialNumber);
			
			UcaData data=UcaDataFactory.getNormalUca(serialNumber);
			String creditClass=data.getUserCreditClass();	//用户星级
			if(creditClass.equals("-1")){
				creditClass="0";
			}
			custInfo.put("Star", creditClass);
			
			//新增三个出参，1-积分余额，2-是否是移动员工或代理商，3-当前业务区  by songlm 20160222 关于开发赠送积分活动需求（俱乐部微信平台）
			//1-积分余额
			String score = "0";
			IDataset scoreInfo = AcctCall.queryUserScore(data.getUserId());
			if (IDataUtil.isNotEmpty(scoreInfo))
			{
				score = scoreInfo.getData(0).getString("SUM_SCORE");
			}
			custInfo.put("Score", score);
			
			//2-是否是移动员工或代理商
			String specialCustomer = "0";
			IDataset userDiscntList = UserDiscntInfoQry.getAllValidDiscntByUserId(data.getUserId());
			if (IDataUtil.isNotEmpty(userDiscntList))
			{
				for (int index = 0, size = userDiscntList.size(); index < size; index++)
				{
					IData userDiscnt = userDiscntList.getData(index);
					String discntCode = userDiscnt.getString("DISCNT_CODE");
					IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "536", discntCode, "0898");
					if (IDataUtil.isNotEmpty(commparaInfos))
					{
						specialCustomer = "1";
						break;
					}
				}
			}
			custInfo.put("SpecialCustomer", specialCustomer);
			
			//3-当前业务区，参考的“客户资料综合查询”模块中的，“当前业务区”取法 来源自GetUser360ViewSVC.java中的qryUserInfo
			String citycode = "";
			IData input = new DataMap();
			input.put("USER_ID", data.getUserId());
			IData userInfo = UcaInfoQry.qryUserInfoByUserId(data.getUserId());
			IDataset userCityInfo = Qry360InfoDAO.qryUserCityInfo(input);
			if(StringUtils.isNotBlank(userInfo.getString("CITY_CODE")) && IDataUtil.isNotEmpty(userCityInfo))
			{
				citycode = userCityInfo.getData(0).getString("CITY_CODE", "");
			}
			if(StringUtils.isNotBlank(userInfo.getString("CITY_CODE")) && IDataUtil.isEmpty(userCityInfo))
			{
				citycode = userInfo.getString("CITY_CODE","");
			}
			custInfo.put("Citycode", citycode);
		}
		
		return custInfo;
	}
	
	
	public void saveWeChatScore(IData param)throws Exception{
		Dao.executeUpdateByCodeCode("TL_B_WECHAT_SCORE","INS_WECHAT_SCORE", param);
	}
	
	public IDataset queryWeChatScoreRequest(IData param,Pagination pagination)throws Exception{
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_WECHAT_SCORE_BY_USERID", param,pagination);
	}
}
