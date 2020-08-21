package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod;
 
import java.math.BigDecimal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;

/**
 * 无手机宽带业务
 * CHENXY3
 * 2016-12-23
 * */
public class NoPhoneWideChangeProdSVC extends CSBizService
{ 
	/**
     * 根据trade_type_code判断是否无手机业务
     * */
    public IDataset checkIfNoPhoneTrade(IData input) throws Exception
    {
    	return CommparaInfoQry.getCommparaAllCol("CSM", "6800", input.getString("TRADE_TYPE_CODE"), "0898");   	
    	
    }
    
    /**
     * 根据SERIAL_NUMBER判断是否无手机宽带用户
     * 入参：SERIAL_NUMBER，3种情况：
     * 1、KD_开头的
     * 2、18位长度的（身份证）
     * 3、非KD_开头且非身份证
     * */
    public IDataset checkIfNoPhoneWideUser(IData input) throws Exception
    {
    	return NoPhoneWideChangeProdBean.checkIfNoPhoneWideUser(input);
    	
    }
    
	/**
     * 根据身份证号获取宽带账号
     * */
    public IDataset noPhoneUserQryByPSPTID(IData input) throws Exception
    {
    	IDataset rtnset=new DatasetList();
    	IData rtndata=new DataMap();
    	IDataset users= NoPhoneWideChangeProdBean.noPhoneUserQryByPSPTID(input);
    	if(users !=null && users.size()==1){
    		rtndata.put("SERIAL_NUMBER", users.getData(0).getString("SERIAL_NUMBER",""));
    		rtnset.add(rtndata);
    	}else if(users.size()>1){
    		String sns="";
    		for(int i=0;i<users.size();i++){
    			String sn=users.getData(i).getString("SERIAL_NUMBER","").replace("KD_", "");
    			if("".equals(sns)){
    				sns="["+sn+"]";
    			}else{
    				sns=sns+",["+sn+"]";
    			} 
    		}
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该身份证开了多个无手机宽带账号，无法使用身份证登陆，请选择一个账号登陆办理："+sns);
    	}
    	return rtnset;
    }
    
  //判断是否是包年套餐
    public IDataset checkWidenetProduct(IData param) throws Exception
    {
    	//这里用的是宽带用户的user_id
    	//qryParam.put("USER_ID", strUserId);
    	param.put("PARAM_CODE", param.getString("TRADE_TYPE_CODE"));
    	//判断
    	IDataset ds = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_DISCNT2", param);
    	if (IDataUtil.isNotEmpty(ds))
    	{
    		//包年的直接取结束日期
    		String v_end = ds.getData(0).getString("END_DATE");
        	
        	String itag = getFlagForActive(v_end);
        	ds.getData(0).put("V_BOOK_TAG", itag);
    		return ds;
    	}
    	return null;
    }
    
    public String getFlagForActive(String v_end) throws Exception
    {
    	String booktag="";
    	String now_end = SysDateMgr.getSysDate();
    	now_end = SysDateMgr.addMonths(now_end, 2);//也是提前三个月办理降档。（升档随时可以办）
    	now_end = SysDateMgr.getDateLastMonthSec(now_end);
    	int iret = SysDateMgr.compareTo(v_end, now_end);
    	if (iret>0)
    	{
    		//活动结束时间还在3个月之后，暂时不能预约
    		booktag= "0";
    	}else if (iret<0)
    	{
    		//活动已接结束，或在3个月内结束，可以预约
    		booktag= "1";
    	}else
    	{
    		//活动在3个月后结束，刚好可以预约
    		booktag= "1";
    	}
    	return booktag;
    }
    /**
     * 无手机宽带续费，获取用户宽带优惠信息
     * */
    public IData getWideUserDiscntInfo(IData param) throws Exception{
    	String renewTagDec="可以续约";
    	String renewTag="1";//允许续约标记 0=不允许 1=允许
    	IData rtnData=new DataMap();
    	String userId=param.getString("USER_ID");
    	String productId="";
		String packageId="";
		String discntCode="";
		String discntName="";
		String startDateOld="";
		String endDateOld="";
		String newEndDate="";
		String newStartDate="";
		String today=SysDateMgr.getSysDate();
	    IDataset userDiscntList = NoPhoneWideChangeProdBean.qryNoPhoneUserDiscnt(param);
	    /**
	     * 分几种情况
	     * 1、超过一条有效记录，说明已经续约过
	     * 2、只有一条记录（“当前有效” 和 “未来生效” 2种情况）
	     * 3、没有记录（“真的没有” 和 “已经到期停机” 2种情况）
	     * */
	    if (IDataUtil.isNotEmpty(userDiscntList))
	    {
	      //填充PRODUCT_ID、PACKAGE_ID
            FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscntList, null, null);// 填充productId和packageId
	        
	        if(userDiscntList.size()>1) 
	        { 
	            renewTagDec="已续约，不能再次办理。";
	            renewTag="0";//允许续约标记  0=不允许 1=允许
	            
	            for(int k=0;k<userDiscntList.size();k++)
	            {
	                String startDate_Old=userDiscntList.getData(k).getString("START_DATE");
	                
	                if(SysDateMgr.compareTo(today, startDate_Old) < 0)
	                {//取未来的一条记录
	                    productId=userDiscntList.getData(k).getString("PRODUCT_ID");
	                    packageId=userDiscntList.getData(k).getString("PACKAGE_ID");
	                    discntCode=userDiscntList.getData(k).getString("DISCNT_CODE");
	                    discntName= UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
	                    newStartDate=userDiscntList.getData(k).getString("START_DATE");
	                    newEndDate=userDiscntList.getData(k).getString("END_DATE");
	                }
	                else
	                {
	                    startDateOld=userDiscntList.getData(k).getString("START_DATE");
	                    endDateOld=userDiscntList.getData(k).getString("END_DATE"); 
	                }
	            }
	        }
	        else if(userDiscntList.size()==1)
	        {
	            productId=userDiscntList.getData(0).getString("PRODUCT_ID");
	            packageId=userDiscntList.getData(0).getString("PACKAGE_ID");
	            discntCode=userDiscntList.getData(0).getString("DISCNT_CODE");
	            //度假宽带月套餐2019（无手机）
	            if(!"".equals(param.getString("DISCNT_CODE","")))
	            {
	            	discntCode = param.getString("DISCNT_CODE","");
	            }
	            //度假宽带月套餐2019（无手机）
	            discntName= UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
	            startDateOld=userDiscntList.getData(0).getString("START_DATE");
	            endDateOld=userDiscntList.getData(0).getString("END_DATE");	 
	            
	            String firstRenewDate = SysDateMgr.firstDayOfDate(endDateOld, -2);
	            
	            //如果是下月后才生效的套餐，也不允许办。
	            if(SysDateMgr.compareTo(today, startDateOld) <= 0)
	            {
	                renewTagDec="存在即将生效的宽带套餐";
	                renewTag="0";
	                newStartDate=startDateOld;
	                newEndDate=endDateOld;
	            }
	            
	            //add by zhangxing3 for 候鸟月、季、半年套餐（海南）	            
	            else if(SysDateMgr.compareTo(today, firstRenewDate) < 0 && ( "84014242".equals(discntCode)))
	            {
	                renewTagDec="候鸟半年套餐，仅能提前三个月续费";
	                renewTag="0";
	                newStartDate=startDateOld;
	                newEndDate=endDateOld;
	            }
	            //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	            else
	            {
	                //续约取终止日期次月1号，如果该日期大于当前日期，则取该日期
	                //如果该日期小于当前日期，则取当前日期的次月1日               
	                newStartDate=SysDateMgr.getFirstDayOfNextMonth(userDiscntList.getData(0).getString("END_DATE"));//原终止日期次月1日
	                //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	                if ("84014240".equals(discntCode) || "84074442".equals(discntCode))
					{
	                	newEndDate = newStartDate;
					}
					else if ("84014241".equals(discntCode))
					{
						newEndDate=SysDateMgr.addMonths(newStartDate, 2);

					}
	                //BUS201907300031新增度假宽带季度半年套餐开发需求
					else if ("84071448".equals(discntCode))
					{
						newEndDate=SysDateMgr.addMonths(newStartDate, 3);

					}
					else if ("84014242".equals(discntCode) || "84071449".equals(discntCode))
					{
						newEndDate=SysDateMgr.addMonths(newStartDate, 5);

					}
	                //BUS201907300031新增度假宽带季度半年套餐开发需求
					else {
						newEndDate=SysDateMgr.addMonths(newStartDate, 11);
					}
	                //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	                newEndDate = SysDateMgr.getDateLastMonthSec(newEndDate);
	            } 
	        }
	    }
		else
		{
			//这种没有有效优惠的情况，则分为真的没有优惠；已经到期停机的优惠；2种情况。
			//取该人员的优惠最后一条（日期最大）失效的记录。
			IDataset userDiscntList2 = NoPhoneWideChangeProdBean.qryNoPhoneUserDiscnt2(param);
			
			if(IDataUtil.isNotEmpty(userDiscntList2))
			{
			    //填充PRODUCT_ID、PACKAGE_ID
	            FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscntList2, null, null);// 填充productId和packageId
			    
			    productId=userDiscntList2.getData(0).getString("PRODUCT_ID","");
			    //add by zhangxing3 for 无手机宽带续费bug start
			    if("".equals(productId))
			    {
			    	IDataset offerRels = UserOfferRelInfoQry.qryUserAllOfferRelByRelOfferInsId(userDiscntList2.getData(0).getString("INST_ID"));
			    	if(IDataUtil.isNotEmpty(offerRels)){
			    		userDiscntList2.getData(0).put("PRODUCT_ID", offerRels.getData(0).getString("OFFER_CODE", ""));
			    		productId =  offerRels.getData(0).getString("OFFER_CODE", "");
			    	}
			    }
			    //add by zhangxing3 for 无手机宽带续费bug end
	            packageId=userDiscntList2.getData(0).getString("PACKAGE_ID");
	            startDateOld=userDiscntList2.getData(0).getString("START_DATE");
	            endDateOld=userDiscntList2.getData(0).getString("END_DATE");
	            discntCode=userDiscntList2.getData(0).getString("DISCNT_CODE");
	            
	            //度假宽带月套餐2019（无手机）
	            if(!"".equals(param.getString("DISCNT_CODE","")))
	            {
	            	discntCode = param.getString("DISCNT_CODE","");
	            }
	            //度假宽带月套餐2019（无手机）
	            
	            discntName= UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
	            startDateOld=userDiscntList2.getData(0).getString("START_DATE");
	            endDateOld=userDiscntList2.getData(0).getString("END_DATE");
			    
				//停机后办理：续约取终止日期次月1号，如果该日期大于当前日期，则取该日期
				//如果该日期小于当前日期，则取当前日期的次月1日				
				newStartDate=SysDateMgr.getFirstDayOfNextMonth(userDiscntList2.getData(0).getString("END_DATE"));//原终止日期次月1日
				int remainDay=0;
				if(SysDateMgr.compareTo(today, newStartDate) >= 0)//超过了原终止日期才来续约，则开始日期取下月一日。当月以天收费
	         	{
					/**
					 * 这里存在问题，用户如果已经停机，那么续约的开始日期如何制定？
					 * 应该是设定为下月一号。但是当前服务先开机，然后费用按天算计算到月底。
					 */  
					newStartDate=SysDateMgr.getFirstDayOfNextMonth();//次月1日
					remainDay=SysDateMgr.getDayIntervalNoAbs(today,newStartDate);//计算剩余多少天
					rtnData.put("NEW_REMAIN_DAY", remainDay);
					rtnData.put("STOP_OPEN_TAG", "1");//停机后办理标记
				} 
				//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
				if ("84014240".equals(discntCode) || "84074442".equals(discntCode))
				{
					newEndDate = newStartDate;
				}
				else if ("84014241".equals(discntCode))
				{
					newEndDate=SysDateMgr.addMonths(newStartDate, 2);

				}
				//BUS201907300031新增度假宽带季度半年套餐开发需求
				else if ("84071448".equals(discntCode))
				{
					newEndDate=SysDateMgr.addMonths(newStartDate, 3);

				}
				else if ("84014242".equals(discntCode) || "84071449".equals(discntCode))
				{
					newEndDate=SysDateMgr.addMonths(newStartDate, 5);

				}
				//BUS201907300031新增度假宽带季度半年套餐开发需求
				else {
					newEndDate=SysDateMgr.addMonths(newStartDate, 11);
				}
				//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
				newEndDate = SysDateMgr.getDateLastMonthSec(newEndDate);
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到该用户有效的优惠信息。");
			}
		}
		rtnData.put("PRODUCT_ID", productId);
		rtnData.put("PACKAGE_ID", packageId);
		rtnData.put("DISCNT_CODE", discntCode);
		rtnData.put("DISCNT_NAME", discntName);
		rtnData.put("DISCNT_START_DATE", startDateOld);
		rtnData.put("DISCNT_END_DATE", endDateOld);
		rtnData.put("NEW_START_DATE", newStartDate);
		rtnData.put("NEW_END_DATE", newEndDate);
		rtnData.put("RENEW_TAG_DEC", renewTagDec);
		rtnData.put("RENEW_TAG", renewTag);
		
		return rtnData;
	}
    /**
     * 无手机宽带续费，获取宽带金额
     * SS.NoPhoneWideChangeProdSVC.getProductFeeInfo
     * */
    public IDataset getProductFeeInfo(IData param) throws Exception{
    	IData discntInfo=getWideUserDiscntInfo(param);//获取用户宽带优惠信息
    	String productId=discntInfo.getString("PRODUCT_ID");
		String packageId=discntInfo.getString("PACKAGE_ID");
		String discntCode=discntInfo.getString("DISCNT_CODE"); 
		String renewTag=discntInfo.getString("RENEW_TAG","");
		String newRemainDay=discntInfo.getString("NEW_REMAIN_DAY", "");
		
		//REQ201904280035优化无手机宽带续约界面问题
/*		IDataset ids = CommparaInfoQry.getCommparaByCode1("CSM", "9522", discntCode, "0898");
        //System.out.println("------------getProductFeeInfo-------------ids:"+ids);

		if (IDataUtil.isNotEmpty(ids))
		{
			discntCode = ids.getData(0).getString("PARA_CODE2", "");
			packageId = ids.getData(0).getString("PARA_CODE3", "");
			productId = ids.getData(0).getString("PARA_CODE4", "");
			discntInfo.put("DISCNT_CODE", discntCode);
			discntInfo.put("PACKAGE_ID", packageId);
			discntInfo.put("PRODUCT_ID", productId);
		}*/
		//REQ201904280035优化无手机宽带续约界面问题
		
    	//feeMgr费用计算
        IDataset feeDatas = new DatasetList();
        IData feeData = new DataMap();
        feeData.putAll(param);
        feeData.putAll(discntInfo);
        
//        feeData.put("DISCNT_NAME", discntInfo.getString("DISCNT_NAME"));
//        feeData.put("DISCNT_START_DATE", discntInfo.getString("DISCNT_START_DATE"));
//        feeData.put("DISCNT_END_DATE", discntInfo.getString("DISCNT_END_DATE"));
//        feeData.put("NEW_START_DATE", discntInfo.getString("NEW_START_DATE"));
//        feeData.put("NEW_END_DATE", discntInfo.getString("NEW_END_DATE"));
//        feeData.put("RENEW_TAG_DEC", discntInfo.getString("RENEW_TAG_DEC"));
//        feeData.put("RENEW_TAG", discntInfo.getString("RENEW_TAG"));
        IDataset feeConfigs = ProductFeeInfoQry.getElementFee("681", CSBizBean.getVisit().getInModeCode(), "", "D",productId,packageId, "-1", discntCode, "0898", "3");
        if (IDataUtil.isEmpty(feeConfigs))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "产品["+productId+"]包["+packageId+"]优惠["+discntCode+"]在TD_B_PRODUCT_TRADEFEE表配置不存在，请查看。");
        }
        if(renewTag!=null && "1".equals(renewTag)){
	        
	        int feeSize = feeConfigs.size();
	
	        for (int j = 0; j < feeSize; j++)
	        {
	            IData feeConfig = feeConfigs.getData(j);
	            if (!"0".equals(feeConfig.getString("PAY_MODE")) || "0".equals(feeConfig.getString("FEE")))
	            {
	                continue;
	            }
	            String fee=feeConfig.getString("FEE");
	            feeData.put("FEE_MODE", feeConfig.getString("FEE_MODE"));
	            feeData.put("FEE_TYPE_CODE", feeConfig.getString("FEE_TYPE_CODE"));
	            feeData.put("FEE", fee);
	            feeData.put("FEE_YEAR", fee);
	            feeData.put("FEE_DAY", "0");
	            
	            //如果剩余天数有值，说明该用户是停机后再来续费的。包年下月开始，首月按天算。
	            //需求描述——按天资费：向下取整(月租*12/365)，单位为0.1元
	            double remainFee=0.0;
	            if(!"".equals(newRemainDay)){
	            	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	            	if ("84014240".equals(discntCode))
					{
	            		remainFee=Integer.parseInt(fee)*12/365*Integer.parseInt(newRemainDay);
					}
					else if ("84014241".equals(discntCode))
					{
						remainFee=Integer.parseInt(fee)*4/365*Integer.parseInt(newRemainDay);
					}
					else if ("84014242".equals(discntCode))
					{
						remainFee=Integer.parseInt(fee)*2/365*Integer.parseInt(newRemainDay);
					}
	            	//BUS201907300031新增度假宽带季度半年套餐开发需求
					else if ("84071448".equals(discntCode) || "84071449".equals(discntCode))
					{
						remainFee=130*Double.parseDouble(newRemainDay);
					}
	            	//BUS201907300031新增度假宽带季度半年套餐开发需求
					else {
		            	//计算首月剩余天数费用：
		            	//先计算每天金额，再计算剩余天数*每日金额
		            	remainFee=Integer.parseInt(fee)/365*Integer.parseInt(newRemainDay);
					}
	            	//这里的金额是到分的，所以不保留小数位
	            	BigDecimal bd=new BigDecimal(remainFee).setScale(0, BigDecimal.ROUND_HALF_DOWN);
	            	int allFee=Integer.parseInt(fee)+Integer.parseInt(bd.toString());
	            	feeData.put("FEE", allFee);
	            	feeData.put("FEE_YEAR", fee);
	            	feeData.put("FEE_DAY", bd.toString());
	            	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	            }
	        }
        }else{
        	feeData.put("FEE_MODE", feeConfigs.getData(0).getString("FEE_MODE"));
            feeData.put("FEE_TYPE_CODE", feeConfigs.getData(0).getString("FEE_TYPE_CODE"));
            feeData.put("FEE", 0);
        }
        feeDatas.add(feeData);
        //System.out.println("------------getProductFeeInfo-------------feeDatas:"+feeDatas);
        return feeDatas;
    }
    
    /**
     * 无手机宽带续费，变更优惠时获取宽带金额
     * SS.NoPhoneWideChangeProdSVC.getProductFeeInfoNew
     * */
    public IDataset getProductFeeInfoNew(IData param) throws Exception{
    	String serialNumber = param.getString("SERIAL_NUMBER","");
    	if (!serialNumber.startsWith("KD_"))
		{
    		serialNumber = "KD_"+serialNumber;
    		param.put("SERIAL_NUMBER",serialNumber);
		}
		IData idKdUser = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(idKdUser))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"业务受理限制:用户宽带信息不存在，请检查!");
		}    	
    	param.put("USER_ID", idKdUser.getString("USER_ID",""));
    	IData discntInfo=getWideUserDiscntInfo(param);//获取用户宽带优惠信息
    	String productId=discntInfo.getString("PRODUCT_ID","");
		String packageId=discntInfo.getString("PACKAGE_ID","");
		String discntCode=param.getString("DISCNT_CODE",""); 
		if(!"".equals(discntCode))
        {
			discntInfo.put("DISCNT_CODE", discntCode);
        }else{//当入参DISCNT_CODE为空时取用户当前的优惠
			discntCode=discntInfo.getString("DISCNT_CODE"); 
		}
		String renewTag=discntInfo.getString("RENEW_TAG","");
		String newRemainDay=discntInfo.getString("NEW_REMAIN_DAY", "");
		
		//REQ201904280035优化无手机宽带续约界面问题
		IDataset ids = CommparaInfoQry.getCommparaByCode1("CSM", "9522", discntCode, "0898");
        //System.out.println("------------getProductFeeInfo-------------ids:"+ids);

		if (IDataUtil.isNotEmpty(ids))
		{
			discntCode = ids.getData(0).getString("PARA_CODE2", "");
			packageId = ids.getData(0).getString("PARA_CODE3", "");
			productId = ids.getData(0).getString("PARA_CODE4", "");
			discntInfo.put("DISCNT_CODE", discntCode);
			discntInfo.put("PACKAGE_ID", packageId);
			discntInfo.put("PRODUCT_ID", productId);
		}
		//REQ201904280035优化无手机宽带续约界面问题
		
    	//feeMgr费用计算
        IDataset feeDatas = new DatasetList();
        IData feeData = new DataMap();
        feeData.putAll(param);
        feeData.putAll(discntInfo);
        
        IDataset feeConfigs = ProductFeeInfoQry.getElementFee("681", CSBizBean.getVisit().getInModeCode(), "", "D",productId,packageId, "-1", discntCode, "0898", "3");
        if (IDataUtil.isEmpty(feeConfigs))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "产品["+productId+"]包["+packageId+"]优惠["+discntCode+"]在TD_B_PRODUCT_TRADEFEE表配置不存在，请查看。");
        }
        if(renewTag!=null && "1".equals(renewTag)){
	        
	        int feeSize = feeConfigs.size();
	
	        for (int j = 0; j < feeSize; j++)
	        {
	            IData feeConfig = feeConfigs.getData(j);
	            if (!"0".equals(feeConfig.getString("PAY_MODE")) || "0".equals(feeConfig.getString("FEE")))
	            {
	                continue;
	            }
	            String fee=feeConfig.getString("FEE");
	            feeData.put("FEE_MODE", feeConfig.getString("FEE_MODE"));
	            feeData.put("FEE_TYPE_CODE", feeConfig.getString("FEE_TYPE_CODE"));
	            feeData.put("FEE", fee);
	            feeData.put("FEE_YEAR", fee);
	            feeData.put("FEE_DAY", "0");
	            
	            //如果剩余天数有值，说明该用户是停机后再来续费的。包年下月开始，首月按天算。
	            //需求描述——按天资费：向下取整(月租*12/365)，单位为0.1元
	            double remainFee=0.0;
	            if(!"".equals(newRemainDay)){
	            	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	            	if ("84014240".equals(discntCode))
					{
	            		remainFee=Integer.parseInt(fee)*12/365*Integer.parseInt(newRemainDay);
					}
					else if ("84014241".equals(discntCode))
					{
						remainFee=Integer.parseInt(fee)*4/365*Integer.parseInt(newRemainDay);
					}
					else if ("84014242".equals(discntCode))
					{
						remainFee=Integer.parseInt(fee)*2/365*Integer.parseInt(newRemainDay);
					}
	            	//BUS201907300031新增度假宽带季度半年套餐开发需求
					else if ("84071448".equals(discntCode) || "84071449".equals(discntCode) || "84074442".equals(discntCode))
					{
						remainFee=130*Double.parseDouble(newRemainDay);
					}
	            	//BUS201907300031新增度假宽带季度半年套餐开发需求
					else {
		            	//计算首月剩余天数费用：
		            	//先计算每天金额，再计算剩余天数*每日金额
		            	remainFee=Integer.parseInt(fee)/365*Integer.parseInt(newRemainDay);
					}
	            	//这里的金额是到分的，所以不保留小数位
	            	BigDecimal bd=new BigDecimal(remainFee).setScale(0, BigDecimal.ROUND_HALF_DOWN);
	            	int allFee=Integer.parseInt(fee)+Integer.parseInt(bd.toString());
	            	feeData.put("FEE", allFee);
	            	feeData.put("FEE_YEAR", fee);
	            	feeData.put("FEE_DAY", bd.toString());
	            	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	            }
	        }
        }else{
        	feeData.put("FEE_MODE", feeConfigs.getData(0).getString("FEE_MODE"));
            feeData.put("FEE_TYPE_CODE", feeConfigs.getData(0).getString("FEE_TYPE_CODE"));
            feeData.put("FEE", 0);
        }
        feeDatas.add(feeData);
        //System.out.println("------------getProductFeeInfo-------------feeDatas:"+feeDatas);
        return feeDatas;
    }
    
  //初始化预约日期列表
    public IDataset onInitBookTimeList(IData data) throws Exception
    {
        String startDate = data.getString("START_DATE");
        
        //默认生效时间为次月1日
        String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        
        if (StringUtils.isNotBlank(startDate))
        {
            //如果优惠开始时间大于次月一号预约时间，则将第一个预约时间改为优惠生效当月1号
            if (SysDateMgr.compareTo(startDate, firstDayOfNextMonth) > 0)
            {
                firstDayOfNextMonth = SysDateMgr.firstDayOfMonth(startDate, 0);
            }
        }
        
        IDataset bookTimeList = new DatasetList();
        
        IData bookTime = new DataMap();     
        bookTime.put("DATA_ID", firstDayOfNextMonth);       
        bookTime.put("DATA_NAME", firstDayOfNextMonth); 
        
        bookTimeList.add(bookTime);
        
        String firstDayOfNextTwoMonth = SysDateMgr.addMonths(firstDayOfNextMonth, 1);
        IData bookTimeTwo = new DataMap();      
        bookTimeTwo.put("DATA_ID", firstDayOfNextTwoMonth);     
        bookTimeTwo.put("DATA_NAME", firstDayOfNextTwoMonth); 
        
        bookTimeList.add(bookTimeTwo);
        
        String firstDayOfNextThreeMonth = SysDateMgr.addMonths(firstDayOfNextTwoMonth, 1);
        IData bookTimeThree = new DataMap();        
        bookTimeThree.put("DATA_ID", firstDayOfNextThreeMonth);     
        bookTimeThree.put("DATA_NAME", firstDayOfNextThreeMonth); 
        
        bookTimeList.add(bookTimeThree);
        
        return bookTimeList;
    }
    
    
    /**
     * 无手机宽带到期停机
     * chenxy3 2017-3-15
     * SS.NoPhoneWideChangeProdSVC.wideExpireStopService
     * */
    public IDataset wideExpireStopService(IData param) throws Exception{
    	//先判断是否还有其他无手机宽带业务有效的或者即将生效的。
    	IDataset userDiscnts = NoPhoneWideChangeProdBean.qryNoPhoneUserDiscnt(param);
    	if(userDiscnts==null || userDiscnts.size()==0){ //没有后续的宽带再办理
    		String userId=param.getString("USER_ID");
        	IDataset userInfos=UserInfoQry.getUserInfoByUserId(userId);
	        if(userInfos!=null && userInfos.size()>0){
	        	String serialNum=userInfos.getData(0).getString("SERIAL_NUMBER");
		    	IData openData = new DataMap();
		    	openData.put("SERIAL_NUMBER", serialNum);  
		    	openData.put("TRADE_TYPE_CODE", "683");   
		        CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", openData); 
        	}
    	}
        return new DatasetList();
    }

	/**
	 * 无手机宽带受理接口
	 * 需求编码：REQ201711160016   关于电子渠道“宽带包年续约”功能的优化
	 * @param param
	 * @author micy
	 * @return
	 * @throws Exception
	 */
	public IData noPhoneWidenetRenewFee(IData param) throws Exception{
    	//1.判断是否为空
		chkParamNoStr(param, "SERIAL_NUMBER", "201703001"); // 手机号码
        chkParamNoStr(param,"FEE_MONEY","201802009");
		String phoneNumber = param.getString("SERIAL_NUMBER");
//		2.如果传过来的号码不是以KD_开头，加上，默认为无手机宽带
		if (!phoneNumber.startsWith("KD_"))
		{
			param.put("SERIAL_NUMBER","KD_"+phoneNumber);
		}
		IData  data = new DataMap();

		data.put("TRADE_TYPE_CODE", "682");
		data.put("EPARCHY_CODE", "0898");
		data.put(Route.ROUTE_EPARCHY_CODE, "0898");
//		3.获取宽带账户信息，判断宽带账户是否存在
		IData idKdUser = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
		if(IDataUtil.isEmpty(idKdUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "201703105");
			idError.put("X_RESULTINFO", "用户宽带信息不存在，请检查");
			return idError;
		}

		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		data.putAll(idKdUser);

		IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", data);
		CSAppException.breerr(infos.getData(0));
		//宽带预约拆机信息
		IDataset destroyInfos = CSAppCall.call( "SS.NoPhoneWideDestroyUserSVC.getDestroyInfo", data);

		//有预约拆机报错
		if(IDataUtil.isNotEmpty(destroyInfos))
		{
			String destroyState = destroyInfos.getData(0).getString("DESTORY_STATE","");
			if("已预约".equals(destroyState))
			{
				//报错
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"业务受理限制:该用户含有宽带预约拆机记录,不能办理该业务!");
			}
		}
		
		//关于度假宽带月套餐
		if(!"".equals(param.getString("DISCNT_CODE","")));
		{
			data.put("DISCNT_CODE", param.getString("DISCNT_CODE",""));
		}
		//关于度假宽带月套餐
		
		IData idError = new DataMap();
		IDataset feeInfos = CSAppCall.call("SS.NoPhoneWideChangeProdSVC.getProductFeeInfoNew", data);
		if(feeInfos!=null && feeInfos.size()>0) {
			IData feeData = feeInfos.getData(0);
			int renewTag = feeData.getInt("RENEW_TAG");
			if (renewTag == 0)
			{
				idError.put("X_RESULTCODE", "201802011");
				idError.put("X_RESULTINFO", feeData.getString("RENEW_TAG_DEC"));
				return idError;
			}
			feeData.put("START_DATE",feeData.getString("NEW_START_DATE"));
			feeData.put("END_DATE",feeData.getString("NEW_END_DATE"));
//			构建费用子台账数据
			IData feeTradeData = new DataMap();
			feeTradeData.put("PAY_MONEY_CODE",feeData.getString("FEE_MODE"));
			feeTradeData.put("MONEY",feeData.getString("FEE"));
			IDataset feeTradeDataSet = new DatasetList();
			feeTradeDataSet.add(feeTradeData);
			feeData.put("X_TRADE_PAYMONEY",feeTradeDataSet);
			
            IData tradeFeeSub = new DataMap();
			IDataset tradeFeeSubDataSet = new DatasetList();
            tradeFeeSub.put("TRADE_TYPE_CODE", "682");
            tradeFeeSub.put("FEE_TYPE_CODE", feeData.getString("FEE_TYPE_CODE",""));
            tradeFeeSub.put("FEE", feeData.getString("FEE"));
            tradeFeeSub.put("OLDFEE", feeData.getString("FEE"));
            tradeFeeSub.put("FEE_MODE", feeData.getString("FEE_MODE"));
            tradeFeeSub.put("ELEMENT_ID", "");
            tradeFeeSubDataSet.add(tradeFeeSub);
            feeData.put("X_TRADE_FEESUB",tradeFeeSubDataSet);
            
			int feeMoney = param.getInt("FEE_MONEY");
			int fee = feeData.getInt("FEE");
			if (feeMoney < fee)
            {
                idError.put("X_RESULTCODE", "201802010");
                idError.put("X_RESULTINFO", "金额不足，无法受理！！");
                return idError;
            }

			IDataset resultDataSet = CSAppCall.call("SS.NoPhoneWideRenewRegSVC.tradeReg", feeData);
			if (resultDataSet!=null && resultDataSet.size()>0)
			{
				return resultDataSet.getData(0);
			}
		} else
		{
			idError.put("X_RESULTCODE", "201802001");
			idError.put("X_RESULTINFO", "获取用户宽带金额信息出错，请检查");

		}
        return idError;
	}

	/**
	 * 无手机宽带费用查询接口
	 * 需求编码：REQ201711160016   关于电子渠道“宽带包年续约”功能的优化
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset getnoPhoneWidenetFee(IData param) throws Exception{
		//1.判断是否为空
		chkParamNoStr(param, "SERIAL_NUMBER", "201703001"); // 手机号码
		String phoneNumber = param.getString("SERIAL_NUMBER");
//		2.如果传过来的号码不是以KD_开头，加上，默认为无手机宽带
		if (!phoneNumber.startsWith("KD_"))
		{
			param.put("SERIAL_NUMBER","KD_"+phoneNumber);
		}
		IData  data = new DataMap();

		data.put("TRADE_TYPE_CODE", "682");
		data.put("EPARCHY_CODE", "0898");
		data.put(Route.ROUTE_EPARCHY_CODE, "0898");
//		3.获取宽带账户信息，判断宽带账户是否存在
		IData idKdUser = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
		if(IDataUtil.isEmpty(idKdUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "201703105");
			idError.put("X_RESULTINFO", "用户宽带信息不存在，请检查");
			return IDataUtil.idToIds(idError);
		}

		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		data.putAll(idKdUser);

		IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", data);
		CSAppException.breerr(infos.getData(0));
		//宽带预约拆机信息
		IDataset destroyInfos = CSAppCall.call( "SS.NoPhoneWideDestroyUserSVC.getDestroyInfo", data);

		//有预约拆机报错
		if(IDataUtil.isNotEmpty(destroyInfos))
		{
			String destroyState = destroyInfos.getData(0).getString("DESTORY_STATE","");
			if("已预约".equals(destroyState))
			{
				//报错
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"业务受理限制:该用户含有宽带预约拆机记录,不能办理该业务!");
			}
		}

		IData idError = new DataMap();
		IDataset feeInfos = CSAppCall.call("SS.NoPhoneWideChangeProdSVC.getProductFeeInfo", data);
		if(feeInfos!=null && feeInfos.size()>0) {
			IData feeData = feeInfos.getData(0);
			int renewTag = feeData.getInt("RENEW_TAG");
			/*if (renewTag == 0)
			{
				idError.put("X_RESULTCODE", "201802011");
				idError.put("X_RESULTINFO", "当前已存在或存在即将生效的宽带套餐");
				return idError;
			}*/
			IData resultData = new DataMap();
			resultData.put("DISCNT_NAME",feeData.getString("DISCNT_NAME"));
			resultData.put("DISCNT_CODE",feeData.getString("DISCNT_CODE"));
			resultData.put("RENEW_TAG",feeData.getString("RENEW_TAG"));
			resultData.put("FEE",feeData.getString("FEE"));
			resultData.put("PRODUCT_ID",feeData.getString("PRODUCT_ID"));
			resultData.put("RENEW_TAG_DEC",feeData.getString("RENEW_TAG_DEC"));
			resultData.put("PACKAGE_ID",feeData.getString("PACKAGE_ID"));
			
			//新增“度假宽带月套餐”
	    	String discntCode = feeInfos.getData(0).getString("DISCNT_CODE");
	    	IData inData = new DataMap();
	    	inData.put("SUBSYS_CODE", "CSM");
	    	inData.put("PARAM_ATTR", "9123");
	    	inData.put("PARAM_CODE", "NOPHONE_WIDE_RENEW");
	    	inData.put("PARA_CODE1", discntCode);
	    	inData.put("EPARCHY_CODE", "0898");

			IDataset ids = CSAppCall.call("CS.CommparaInfoQrySVC.getCommparaInfoBy5", inData);
	    	if(IDataUtil.isNotEmpty(ids))
	    	{
	    		IDataset resultInfos = new DatasetList();
	    		IDataset discntInfoList = CSAppCall.call("CS.CommparaInfoQrySVC.getCommparaByParser", inData);
	    		if(IDataUtil.isNotEmpty(discntInfoList))
	        	{
	        		for(int i =0; i <discntInfoList.size();i++ )
	        		{
	        			data.put("DISCNT_CODE", discntInfoList.getData(i).getString("PARA_CODE1", "")) ;
	        			IDataset feeInfos1 = CSAppCall.call("SS.NoPhoneWideChangeProdSVC.getProductFeeInfoNew", data);
	        			if(feeInfos1!=null && feeInfos1.size()>0) {
	        				IData feeData1 = feeInfos1.getData(0);
	        				IData resultData1 = new DataMap();
	        				resultData1.put("DISCNT_NAME",feeData1.getString("DISCNT_NAME"));
	        				resultData1.put("DISCNT_CODE",feeData1.getString("DISCNT_CODE"));
	        				resultData1.put("RENEW_TAG",feeData1.getString("RENEW_TAG"));
	        				resultData1.put("FEE",feeData1.getString("FEE"));
	        				resultData1.put("PRODUCT_ID",feeData1.getString("PRODUCT_ID"));
	        				resultData1.put("RENEW_TAG_DEC",feeData1.getString("RENEW_TAG_DEC"));
	        				resultData1.put("PACKAGE_ID",feeData1.getString("PACKAGE_ID"));
	        				resultInfos.add(resultData1);
	        			}
	        		}
	        	}
	    		return resultInfos;
	    	}
	    	//新增“度假宽带月套餐”
	    	else
	    	{
	    		return IDataUtil.idToIds(resultData);
	    	}
		} 
		else
		{
			idError.put("X_RESULTCODE", "201802001");
			idError.put("X_RESULTINFO", "获取用户宽带金额信息出错，请检查");

		}
		return IDataUtil.idToIds(idError);
	}

	/**
	 * 无手机宽带鉴权接口
	 * 需求编码：REQ201711160016   关于电子渠道“宽带包年续约”功能的优化
	 * @param param
	 * @author micy
	 * @return
	 * @throws Exception
	 */
	public IData noPhoneWidenetAuthentication(IData param) throws Exception {

		//1.判断是否为空
		chkParamNoStr(param, "SERIAL_NUMBER", "201703001"); // 手机号码
		chkParamNoStr(param, "CHECK_MODE", "201802002");

		String phoneNumber = param.getString("SERIAL_NUMBER");
		param.put("TRADE_TYPE_CODE", "682");
		/**
		 * 这里针对无手机宽带进行处理
		 * 1、判断如果是无手机宽带同时录入的SN长度是18，说明是通过身份证查询的
		 * 2、根据身份证捞取他的宽带账号进行鉴权（如：KD_1000062)
		 * */

		if(param.getString("SERIAL_NUMBER").length()==18){
			IDataset nophoneset = CSAppCall.call( "SS.NoPhoneWideChangeProdSVC.checkIfNoPhoneTrade", param);//获取无手机宽带业务类型
			if(nophoneset!=null && nophoneset.size()>0){
				//通过身份证获取KD_账号
				IData callParam=new DataMap();
				callParam.put("PSPT_ID", param.getString("SERIAL_NUMBER"));
				IDataset phoneSet = CSAppCall.call( "SS.NoPhoneWideChangeProdSVC.noPhoneUserQryByPSPTID", callParam);//根据身份证获取无手机宽带账号
				if(phoneSet!=null && phoneSet.size()==1){
					param.put("SERIAL_NUMBER", phoneSet.getData(0).getString("SERIAL_NUMBER",""));
				}
			}
		}
		checkNoStrByCheckMode(param);



//		2.如果传过来的号码不是以KD_开头，加上，默认为无手机宽带
		if (!phoneNumber.startsWith("KD_"))
		{
			param.put("SERIAL_NUMBER","KD_"+phoneNumber);
		}

//		3.获取宽带账户信息，判断宽带账户是否存在
		IData idKdUser = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
		IData idError = new DataMap();
		if(IDataUtil.isEmpty(idKdUser))
		{
			idError.put("X_RESULTCODE", "201703105");
			idError.put("X_RESULTINFO", "用户宽带信息不存在，请检查");
			return idError;
		}
		IData input = new DataMap(param);
		input.putAll(param);
		input.put("USER_ID", idKdUser.getString("USER_ID"));
		input.put(Route.ROUTE_EPARCHY_CODE, "0898");
		input.put("TRADE_TYPE_CODE", "682");
		input.put("EPARCHY_CODE", "0898");
		//宽带预约拆机信息
		IDataset destroyInfos = CSAppCall.call( "SS.NoPhoneWideDestroyUserSVC.getDestroyInfo", input);

		//有预约拆机报错
 		if(IDataUtil.isNotEmpty(destroyInfos))
		{
			String destroyState = destroyInfos.getData(0).getString("DESTORY_STATE","");
			if("已预约".equals(destroyState))
			{
				//报错
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"业务受理限制:该用户含有宽带预约拆机记录,不能办理该业务!");
			}
		}
		IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", input);
		CSAppException.breerr(infos.getData(0));
		IDataset checkInfos = CSAppCall.call( "CS.AuthCheckSVC.authBroadbandCheck", input);
		if (!checkInfos.isEmpty())
		{
			IData authData = checkInfos.getData(0);
			if("0".equals(authData.getString("RESULT_CODE")) || "2".equals(authData.getString("RESULT_CODE"))) {
				/*IDataset feeInfos = CSAppCall.call("SS.NoPhoneWideChangeProdSVC.getProductFeeInfo", input);
				if (feeInfos != null && feeInfos.size() > 0) {
					int renewTag = feeInfos.getData(0).getInt("RENEW_TAG");
					if (renewTag == 0)
					{
						idError.put("X_RESULTCODE", "201802011");
						idError.put("X_RESULTINFO", "当前已存在或存在即将生效的宽带套餐");
						return idError;
					}
					IDataset tradeSet = CSAppCall.call("CS.GetInfosSVC.getAuthBroadbandUser", input);
					IData tradeData = tradeSet.getData(0);
					IData userInfo = tradeData.getData("USER_INFO");
					return userInfo;
				} else
				{
					idError.put("X_RESULTCODE", "201802001");
					idError.put("X_RESULTINFO", "获取用户宽带金额信息出错，请检查");
					return idError;
				}*/
				IDataset tradeSet = CSAppCall.call("CS.GetInfosSVC.getAuthBroadbandUser", input);
				IData tradeData = tradeSet.getData(0);
				IData userInfo = tradeData.getData("USER_INFO");
				return userInfo;
			} else
			{
				idError.put("X_RESULTCODE", "201802005");
				idError.put("X_RESULTINFO", authData);
				return idError;
			}
		} else {
			idError.put("X_RESULTCODE", "201802006");
			idError.put("X_RESULTINFO", "用户校验接口返回数据为空");
			return idError;
		}
	}

	/**
	 * 判断相应的CheckMode的所需要的字段是否为空
	 * @param param
	 */
	private void checkNoStrByCheckMode(IData param) throws Exception {
		//  默认是服务密码校验
		int iCheckMode = param.getInt("CHECK_MODE", 1);
		switch (iCheckMode)
		{
			case 0:
				chkParamNoStr(param, "PSPT_TYPE_CODE", "201802004");
				chkParamNoStr(param, "PSPT_ID", "201802008");
				break;
			case 1:
				chkParamNoStr(param, "USER_PASSWD", "201802003");

				break;
			case 4:
				chkParamNoStr(param, "PSPT_TYPE_CODE", "201802004");
				chkParamNoStr(param, "PSPT_ID", "201802008");
				chkParamNoStr(param, "USER_PASSWD", "201802003");
				break;
		}

	}


	/**
	 * 校验传入在是否为空
	 * 需求编码：REQ201711160016   关于电子渠道“宽带包年续约”功能的优化
	 * @param data
	 * @param keys
	 * @throws Exception
	 */
	public void chkParamNoStr(IData data, String keys, String errorCode) throws Exception
	{
		String key = data.getString(keys, "");
		if ("".equals(key))
		{
			CSAppException.appError(errorCode, "传入在字段" + keys + "值不能为空！");
		}
	}

}