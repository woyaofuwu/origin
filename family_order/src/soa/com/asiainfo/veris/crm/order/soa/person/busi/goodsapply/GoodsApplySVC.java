package com.asiainfo.veris.crm.order.soa.person.busi.goodsapply;

import java.util.StringTokenizer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/** 
 * REQ201603010009 申请新增积分兑换业务
 * @chenxy3@2016-3-11 修改 
 */
public class GoodsApplySVC extends CSBizService
{
 
    /**
     * 查询用户礼品信息
     */
    public IDataset getUserGoodsInfos(IData inparams) throws Exception
    { 
    	return GoodsApplyBean.getUserGoodsInfos(inparams,getPagination());
    }
    
    /**
     *  
	  1、领取礼品：更新表TL_B_USER_SCORE_GOODS
	  2、下发短息
     * */
    public IDataset exchangeGoods(IData inparams) throws Exception{
    	IDataset rtnset=new DatasetList();
    	IData rtnData=new DataMap();
    	IDataset infos = new DatasetList(inparams.getString("GOODS_INFOS", "[]"));
    	String updateStaffId=inparams.getString("UPDATE_STAFF_ID","");
    	String updateDepartId=inparams.getString("UPDATE_DEPART_ID","");
    	if(infos!=null && infos.size()>0){
    		String userId=infos.getData(0).getString("USER_ID");
    		String serialNumber=infos.getData(0).getString("SERIAL_NUMBER");
    		String remainNum=infos.getData(0).getString("REMAIN_NUM");
    		String userCode=infos.getData(0).getString("USER_QUANCODE");
    		String getCode=infos.getData(0).getString("GET_QUANCODE");
    		String ruleName=infos.getData(0).getString("RULE_NAME");
    		
    		//如果已经领取的码是空的，直接加上，如果不为空，则要加上,xxxx
    		String getQuanCode="";
    		if("".equals(getCode)){
    			getQuanCode=userCode;
    		}else{
    			getQuanCode=getCode+","+userCode;
    		}
    		
    		//如果剩余的数量=1，则领完这次没了，状态要改成2，如果大于1，则状态改成领取了部分
    		String state="";
    		if(Integer.parseInt(remainNum)>1){
    			state="1";
    		}else if(Integer.parseInt(remainNum)==1){
    			state="2";
    		}
    		
    		IData updData=new DataMap();
    		updData.put("GET_QUANCODE", getQuanCode);
    		updData.put("STATE", state);
    		updData.put("UPDATE_STAFF_ID", updateStaffId);
    		updData.put("UPDATE_DEPART_ID", updateDepartId);
    		updData.put("USER_ID", infos.getData(0).getString("USER_ID"));
    		updData.put("RULE_ID", infos.getData(0).getString("RULE_ID"));
    		updData.put("ACCEPT_MONTH", infos.getData(0).getString("ACCEPT_MONTH"));
    		updData.put("TRADE_ID", infos.getData(0).getString("TRADE_ID"));
    		updData.put("QUANCODE", infos.getData(0).getString("QUANCODE")); 
        	GoodsApplyBean.exchangeGoods(updData);
        	
        	rtnData.put("RESULT_CODE", "1");
        	rtnData.put("RULE_NAME", infos.getData(0).getString("RULE_NAME"));
        	rtnset.add(rtnData);
        	String content="您的【"+ruleName+"】礼品券号为“"+userCode+"”于“"+SysDateMgr.getSysTime()+"”在"+getVisit().getDepartName()+"使用，详询12580【中国移动】";
			IData smsData = new DataMap();
			smsData.put("SERIAL_NUMBER",serialNumber);
			smsData.put("USER_ID",userId);
			String sysTime=SysDateMgr.getSysTime();
			sysTime=sysTime.substring(sysTime.indexOf(":")-2); 				 
			smsData.put("NOTICE_CONTENT",content);
			smsData.put("STAFF_ID",getVisit().getStaffId());
			smsData.put("DEPART_ID",getVisit().getDepartId());
			smsData.put("REMARK","积分礼品验证码短信");
			smsData.put("BRAND_CODE","");
			smsData.put("FORCE_START_TIME","");
			smsData.put("FORCE_END_TIME","");
			GoodsApplyBean.smsSent(smsData);
    	} 
    	return rtnset;
    }
    /**
     * REQ201603310017 积分兑换实物礼品报表需求  -- 获取礼品领取信息
     * chenxy3 20160418
     * 1.不含税结算金额=不含税单价*领取数量
     * 2.结算税额=不含税结算金额*17%
     * 3.含税结算金额=不含税结算金额+结算税额
     */
    public IDataset queryGoodsList(IData inparams) throws Exception
    { 
    	IDataset goodsList= GoodsApplyBean.queryGoodsList(inparams,getPagination());
    	for(int k=0;k<goodsList.size();k++){
    		double noTaxPrice=Double.parseDouble(goodsList.getData(k).getString("NO_TAX_PRICE",""));//不含税结算单价
    		String taxRate=goodsList.getData(k).getString("TAX_RATE","");//税率
    		double rate=Double.parseDouble(taxRate.substring(0,taxRate.indexOf("%")))*0.01;
    		int getNum=Integer.parseInt(goodsList.getData(k).getString("GET_NUM",""));//领取数量
    		
    		double tax=0;//结算税额
    		double noTaxAmount=0;//不含税结算金额
    		double taxAmount=0;//含税结算金额
    		
    		noTaxAmount=noTaxPrice*getNum;//不含税结算金额=不含税单价*领取数量
    		tax=noTaxAmount*rate;//结算税额=不含税结算金额*17%
    		taxAmount=noTaxAmount+tax;//含税结算金额=不含税结算金额+结算税额
    		
    		goodsList.getData(k).put("NO_TAX_AMOUNT", String.format("%.2f", noTaxAmount));
    		goodsList.getData(k).put("TAX", String.format("%.2f", tax));
    		goodsList.getData(k).put("TAX_AMOUNT", String.format("%.2f", taxAmount));
    		
    	}
    	return goodsList;
    }     
    
    /**
     * REQ201604290035 关于优化15款积分兑换实物礼品业务的需求
     * chenxy3 20160518
     * 重新发送短信
     */
    public IData resendSMSforGoods(IData input) throws Exception{
    	IData rtnData=new DataMap();
    	String serialNum=input.getString("SERIAL_NUMBER","");
    	String serialNumB=input.getString("SERIAL_NUMBER_B","");
    	String fromType=input.getString("FROM_TYPE","");//从页面过来，加个判断标示
    	
    	/**
    	 * 无论接口、界面传，始终传2个号码
    	 * SERIAL_NUMBER=礼品信息号码
    	 * SERIAL_NUMBER_B=发短信的号码（可能2个号码一致）
    	 * */
    	IDataset userInfos=UserInfoQry.getUserinfo(serialNumB);
    	if(userInfos!=null && userInfos.size()>0){
    		String send_NO = serialNumB;
    		String send_userid = userInfos.getData(0).getString("USER_ID");
			IData inparam=new DataMap();
	    	//从页面过来，则要求带上具体的哪条记录。
	    	if("PAGE".equals(fromType)){
	    		inparam.put("SERIAL_NUMBER", serialNum);
	    		inparam.put("RULE_ID", input.getString("RULE_ID",""));
	    		inparam.put("TRADE_ID", input.getString("TRADE_ID",""));
	    	}else{
	    		//从短厅过来，则要求全部未领的都要发短信
	    		inparam.put("SERIAL_NUMBER", serialNum);
	    	}
	    	
	    	IDataset remainInfo=GoodsApplyBean.queryUserRemainGoods(inparam);
	    	if(remainInfo!=null && remainInfo.size()>0){
	    		for(int k=0; k<remainInfo.size(); k++){
	    			String ruleId=remainInfo.getData(k).getString("RULE_ID","");
	    			String goodsName=remainInfo.getData(k).getString("RULE_NAME","");
	    			String endDate=remainInfo.getData(k).getString("ENDDATE","");
	    			String quancode=remainInfo.getData(k).getString("QUANCODE","");
	    			String getQuancode=remainInfo.getData(k).getString("GET_QUANCODE","");
	    			String score=remainInfo.getData(k).getString("SCORE","");
	    			String userId=remainInfo.getData(k).getString("USER_ID","");
	    			
	    			StringTokenizer st=new StringTokenizer(quancode,",");
	    	    	while(st.hasMoreTokens()){ 
	    	    		String verifyCode =st.nextToken();
	    	    		//单个验证码不在已领的验证码中，则下发短信
	    	        	if(getQuancode.indexOf(verifyCode)< 0) { 
	    	        		String content="您已成功使用"+score+"积分兑换"+goodsName+"礼品1份，券号："+verifyCode+"，有效期至"+endDate+"。凭券到各营业网点领取。";
	    	    			IData smsData = new DataMap();
	    					smsData.put("SERIAL_NUMBER",send_NO);
	    					smsData.put("USER_ID",send_userid);
	    					String sysTime=SysDateMgr.getSysTime();
	    					sysTime=sysTime.substring(sysTime.indexOf(":")-2); 				 
	    					smsData.put("NOTICE_CONTENT",content);
	    					smsData.put("STAFF_ID",CSBizBean.getVisit().getStaffId());
	    					smsData.put("DEPART_ID",CSBizBean.getVisit().getDepartId());
	    					smsData.put("REMARK","积分礼品验证码短信");
	    					smsData.put("BRAND_CODE","");
	    					smsData.put("FORCE_START_TIME","");
	    					smsData.put("FORCE_END_TIME","");
	    					GoodsApplyBean.smsSent(smsData);
	    	        	}
	    	    	}
	    		}
	    		rtnData.put("X_RESULTCODE", "0");
	    		rtnData.put("X_RESULTINFO", "已成功发送短信。");
	    	}else{
	    		rtnData.put("X_RESULTCODE", "9");
	    		rtnData.put("X_RESULTINFO", "用户不存在未领取的礼品信息。");
	    	}	    	
    	}else{
    		rtnData.put("X_RESULTCODE", "9");
    		rtnData.put("X_RESULTINFO", "找不到补发短信号码【"+serialNumB+"】的相关记录，无法发送。");
    	}
    	
    	
    	return rtnData;
    }
    /**
     * REQ201610190011 关于积分兑换礼品延长验证码有效期的需求
     * chenxy3 20161114
     */
    public IDataset importDelayData(IData input) throws Exception
    {
    	return GoodsApplyBean.importDelayData(input);
    }
}