package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.plataction;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;



/**
 *  2015年第二季数据业务整合营销活动开发需求
 *  需求：客户活动期间订购五类指定业务（咪咕特级会员、游戏玩家、V+精选包、精品阅读包、精品漫画包），即可连续两个月获得2G/月的定向流量（不管客户是否退订，每办理一项业务均赠送对应业务的2G/月定向流量），并根据客户是否退订业务而向客户赠送6元和包电子券奖励
 * @author dujt
 *
 */ 
public class PlatWinterActionSecond implements IProductModuleAction { 
	
	public void executeProductModuleAction(ProductModuleTradeData dealPmtd,
			UcaData uca, BusiTradeData btd) throws Exception {  
		PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
		String serialNumber = uca.getSerialNumber();
		String userId = uca.getUserId();
		String smsContent = "";
		String discntCode ="";
		String discntCode3 ="";
		String tradetypecode =  "";  
		//先看当前平台服务是否在配置里面，只有在订购了在配置里面的平台业务，才能赠送和电子劵
		//IDataset commpara3716List = CommparaInfoQry.getCommByParaAttr("CSM", "3716", "ZZZZ");//getCommNetInfo
		IDataset commpara3716List = CommparaInfoQry.getCommNetInfo("CSM", "3740", pstd.getElementId()); 
		IData commpara = null;
		boolean flag = false;  
		//if ((commpara3716List!=null || "0".equals(commpara3716List)) && PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
		//if ( 0!= commpara3716List.size() && PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
		 if (!IDataUtil.isEmpty(commpara3716List) && PlatConstants.OPER_ORDER.equals(pstd.getOperCode())) 
			{   
			for(int i =0;i<commpara3716List.size();i++)
			{
				commpara = commpara3716List.getData(i);
				String serviceId = commpara.getString("PARAM_CODE","");
				if(serviceId.equals(pstd.getElementId()))
				{
					  smsContent = commpara.getString("PARA_CODE24");
					  discntCode = commpara.getString("PARA_CODE2");//用户绑定了2g流量
					  discntCode3 = commpara.getString("PARA_CODE3");//用户首免套餐
					//若用户已经绑定了2g流量套餐，则不需要再给用户绑定上2g流量，也不给用户下发电子券
					//为了适应后续活动延期，进行改造,只要用户曾被绑定过2g定向流量，则后续不参与改活动
					 
					/*List discntList = uca.getUserDiscntByDiscntId(discntCode); 
					if(discntList ==null || discntList.size()==0)
					{
						flag = true;
					}*/
					
				}
			} 
			 IData idata = new DataMap();
		        idata.put("USER_ID", userId);
		        idata.put("DISCNT_CODE", discntCode); 
		        IDataset attrList = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_DISCNTCODE_NOTENDDATE", idata);  
		        if (attrList == null || attrList.size()==0)
		        {   
		        	flag = true;
		        }  
		}
		
		if(flag)
		{
			 
			if("98001901".equals(pstd.getElementId()))
			{
			  String memberLevel = PlatUtils.getAttrValue("302", pstd.getAttrTradeDatas());
			  //只有高级的会员能参加活动
			  if(!"3".equals(memberLevel))
			  {
				  return;
			  }
			} 
			  //判断是否通过主资费套餐订购的，如果是，则不获赠6元/月和包电子券及绑定2G/月业务定向流量
			  IDataset commpara219List = CommparaInfoQry.getCommparaByAttrCode1("CSM", "219", pstd.getElementId(), CSBizBean.getUserEparchyCode(),null);
			  List<DiscntTradeData> userDiscntList = new ArrayList<DiscntTradeData>();
			  for(int j =0;j<commpara219List.size();j++)
			  {
				  commpara = commpara219List.getData(j);
				  userDiscntList.addAll(uca.getUserDiscntByDiscntId(commpara.getString("PARAM_CODE")));
			  } 
			  if(userDiscntList.size() >0)
			  {
				  return;
			  } 
			  //首免用户及计费用户，都需要给用户绑定2g流量套餐优惠  
			    //String productId=  uca.getProductId();
	            //String packageId = "50000000"; 
		     /* String DiscntcodeStartDate = dealPmtd.getStartDate().substring(0, 4) + dealPmtd.getStartDate().substring(5,7)
						+ dealPmtd.getStartDate().substring(8,10);    
				String PlatactionStartDate = "20150731"; 
				int ComparaNum =  DiscntcodeStartDate.compareTo(PlatactionStartDate);
			if ( ComparaNum <= 0 ) //若业务办理的时间（预约时间）超过7月31号，则不给予绑定2g流量套餐
			{
			*/
			  
	            IData tempPage0 = new DataMap();
	            String discntEndDate = "";
	            discntEndDate=	SysDateMgr.getAddMonthsLastDay(2);
	            //discntEndDate=	SysDateMgr.getAddMonthsLastDay(2,dealPmtd.getStartDate());//       1个月后的最后一天   第二季只绑定2个月流量包
		        DiscntTradeData newDiscnt = new DiscntTradeData();
		        newDiscnt.setUserId(uca.getUserId());
		        newDiscnt.setProductId("50000000");
		        newDiscnt.setPackageId(PlatConstants.PACKAGE_ID);//package_id修改 "50000000"
		        newDiscnt.setElementId(discntCode);
		        newDiscnt.setInstId(SeqMgr.getInstId());
		        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
		        newDiscnt.setSpecTag("0"); 
		        //newDiscnt.setStartDate(dealPmtd.getStartDate());
		        newDiscnt.setStartDate(SysDateMgr.getSysTime());
		        newDiscnt.setEndDate(discntEndDate);
		        btd.add(uca.getSerialNumber(), newDiscnt); 
		        
			//}   
               //给计费用户下发6元和电子劵 
	    		IDataset commpara3717List = CommparaInfoQry.getCommByParaAttr("CSM", "3741", "ZZZZ"); //和电子卷的配置
	    	    if (IDataUtil.isNotEmpty(commpara3717List)&&"3700".equals(btd.getTradeTypeCode())) //只有在平台业务订购计费业务，当月才会下发和电子劵，首免用户不需要下发
	            {
	                IData mobilePayConfig = commpara3717List.getData(0);
	               // boolean hasPlatSvc = has54PlatSvc(uca);
	                OtherTradeData other = new OtherTradeData();
	                other.setUserId(uca.getUserId());
	                other.setInstId(SeqMgr.getInstId());
	                other.setRsrvValueCode(mobilePayConfig.getString("PARA_CODE1", ""));//参数配置为：ACTIVE_DMS_SMS
	                other.setRsrvValue(mobilePayConfig.getString("PARAM_CODE", "")); //参数配置为：10203414，通过配置
	                other.setRsrvStr1(mobilePayConfig.getString("PARAM_CODE", ""));
	                other.setOperCode("06");
	                other.setRsrvStr2(mobilePayConfig.getString("PARA_CODE2", ""));//活动编号，建立电子卷活动的活动号，读commpara 3717的配置，other插RSRV_STR2，IBOSS对应MD10_SYSTEMID，规范中对应actId
	                other.setRsrvStr3(mobilePayConfig.getString("PARA_CODE3", ""));//卷别类型，电子券的类型，读commpara 3717的配置，other插RSRV_STR3，IBOSS对应MD10_ISSPID，规范中对应prdId
	                other.setRsrvStr4(uca.getSerialNumber());//手机号码，other插RSRV_STR4，规范中对应mobileId
	                other.setRsrvStr5(mobilePayConfig.getString("PARA_CODE5", ""));//电子卷金额，单位：分，读commpara 3717的配置，other插RSRV_STR5，IBOSS对应MD10_SUBJECT，规范中对应amount
	                other.setRsrvStr7(btd.getTradeId());//by songlm@20141011 电子券编码，相当于一个订单编码，不重复唯一的14位数字编码。所以直接插入了tradeId。  REQ201402190004 新增手机支付电子券联机发放接口需求。other插RSRV_STR7，IBOSS对应MD10_ACTIVITYID，规范中对应ttNum
	                other.setRsrvStr8(mobilePayConfig.getString("PARA_CODE8", ""));//貌似没有用到
	                other.setRsrvStr9("5001");//接口功能码，other插RSRV_STR9，IBOSS对应MD10_ORGTIMES，规范中对应funCode
	                other.setRsrvStr10(mobilePayConfig.getString("PARA_CODE10", ""));//商户号码，读commpara 3717的配置，other插RSRV_STR10，IBOSS对应MD10_ORGAMT，规范中对应merId
	                other.setRsrvStr11(SysDateMgr.getSysDateYYYYMMDD());//商户日期，other插RSRV_STR11，到IBOSS对应MD10_BMPFLAG，规范中对应merDate
	                other.setRsrvStr12(SysDateMgr.getSysDateYYYYMMDD());//发放日期，other插RSRV_STR12，到IBOSS对应MD10_PRINTTEXT，规范中股对应ttDate
	                //other.setRsrvStr14(hasPlatSvc ? "1" : "0");
	                other.setRsrvStr14("1");//by   麦通邮件并抄送给局方刘海云、亚信李贤敏、宋立明，提出修改该值为固定值1。  other插RSRV_STR14，到IBOSS对应MD10_PINMD5，规范中对应opType
	                other.setStartDate(btd.getRD().getAcceptTime());
	                other.setEndDate(SysDateMgr.getTheLastTime()); //默认2050年失效，按照需求，要改成当月失效
	                other.setStaffId(CSBizBean.getVisit().getStaffId());
	                other.setDepartId(CSBizBean.getVisit().getDepartId());
	                other.setModifyTag(BofConst.MODIFY_TAG_ADD);
	                other.setRemark("2015年第二季数据业务整合营销活动");
	                btd.add(uca.getSerialNumber(), other);
	                //btd.getTradeDatasMap().get("start_date")
	               //增加tf_b_trade_cj表，主要用于判断当月只能下发一次电子券，避免用户1，2，3用户办理付费业务后，过程还重复下发电子券
	              IData tradeCJParam = new DataMap();
	  			  tradeCJParam.put("TRADE_ID", btd.getTradeId());
	  			  tradeCJParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
	  			  tradeCJParam.put("USER_ID", userId);
	  			  tradeCJParam.put("SERIAL_NUMBER", serialNumber);
	  			  tradeCJParam.put("SERVICE_ID", pstd.getElementId());
	  			  tradeCJParam.put("ACCEPT_DATE", SysDateMgr.getSysTime());
	  			  tradeCJParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	  			  tradeCJParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	  			  tradeCJParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
	  			  tradeCJParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
	  			  tradeCJParam.put("CANCEL_TAG", "6");
	  			  tradeCJParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
	  			  tradeCJParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	  			  tradeCJParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	  			  tradeCJParam.put("RSRV_STR1", "6");
	  			  tradeCJParam.put("REMARK", "2015年第二季数据业务整合营销活动");
	  			  Dao.insert("TF_B_TRADE_CJ", tradeCJParam);  
	  			  
	              PlatUtils.insertSms(serialNumber, userId, smsContent, CSBizBean.getUserEparchyCode(), "2015年第二季数据业务整合营销活动"); 
	            }  
			}  
		}
 
}
