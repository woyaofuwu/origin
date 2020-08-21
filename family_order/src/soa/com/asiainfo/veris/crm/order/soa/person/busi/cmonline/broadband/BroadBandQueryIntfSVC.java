
package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.broadband;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.NoPhoneWideChangeProdBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.InterforResalQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * 中移在线宽带业务查询接口方法类
 * @author Administrator
 *
 */
public class BroadBandQueryIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    private static final transient Logger log = Logger.getLogger(BroadBandQueryIntfSVC.class);
    
    /**
     * 入参参数转换公共类
     * @param input
     * @throws Exception
     */
    public void transferDataInput(IData input) throws Exception
    {
    	input.put("SERIAL_NUMBER", input.getString("busiNum"));
    	input.put("EPARCHY_CODE", input.getString("regionId"));
    	input.put("CHANNEL_ID", input.getString("channelId"));
    }
    
    /**
     *  201804中移在线四轮驱动接口需求-订单撤单 （订单撤单）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData revokeOrder(IData inputs) throws Exception
    {
    	IData input = new DataMap(inputs.toString()).getData("params");
    	IData resultData = new DataMap();
    	IDataset resultDataset = new DatasetList();
    	IData result = new DataMap();
    	
    	try 
   	 	{
    		//入参转换
	    	transferDataInput(input);
	    	
	    	//服务号码
	    	IDataUtil.chkParam(input, "busiNum");
	    	
	    	//订单号码
	    	IDataUtil.chkParam(input, "orderId");
	    	
	    	//撤单原因
	    	IDataUtil.chkParam(input, "revokeReason");
	    	
	    	String orderId = input.getString("orderId");
	    	
	        IDataset wideNetTradeInfos = UTradeInfoQry.qryTradeByOrderId(orderId, "0", getRouteId());
	        
	        if (IDataUtil.isEmpty(wideNetTradeInfos))
	        {
	        	CSAppException.appError("-1", "该订单信息不存在！");
	        }
	        else
	        {
	        	 String widenetTradeId = "";
	             
	             for (int i = 0; i < wideNetTradeInfos.size(); i++) 
	             {
	             	IData wideNetTradeInfo = wideNetTradeInfos.getData(i);
	             	
	             	//只有宽带开户跟宽带移机可以撤单
	     			if ("600".equals(wideNetTradeInfo.getString("TRADE_TYPE_CODE"))
	     					||"606".equals(wideNetTradeInfo.getString("TRADE_TYPE_CODE")))
	     			{
	     				widenetTradeId = wideNetTradeInfo.getString("TRADE_ID");
	     			}
	     		}
	             
	             if (StringUtils.isBlank(widenetTradeId))
	             {
	            	 //0:失败；1：成功
	             	 CSAppException.appError("-1", "不存在可撤销的订单信息！");
	             }
	             else
	             {
	            	 IData inputData = new DataMap();
	            	 
	            	 inputData.put("TRADE_ID", widenetTradeId);
	            	 inputData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
	            	 inputData.put("CITY_CODE", getVisit().getCityCode());
	            	 inputData.put("CANCEL_REASON", input.getString("revokeReason"));
	            	 
            	 
            		 IDataset cancelResult = CSAppCall.call("SS.CancelWidenetTradeService.cancelTradeRegIntf", inputData);
                	 
            		 if (IDataUtil.isNotEmpty(cancelResult))
                	 {
                		 result.put("flag", "1");
                     	 result.put("desc", "宽带撤单成功！");
                     	 result.put("iteractId", cancelResult.getData(0).getString("NEW_ORDER_ID"));
                	 }
            		 else
            		 {
                     	 CSAppException.appError("-1", "宽带订单撤销失败！");
            		 }
	             }
	        }
	    } catch (Exception e) {
			 result.put("flag", "0");
        	 result.put("desc", e.getMessage());
		}
        
        resultDataset.add(result);
        
        IData output = new DataMap();
		IData object = new DataMap();
		
		object.put("respCode", "0");
		object.put("respDesc", "success");
		object.put("result", resultDataset);
		
		output.put("rtnCode", "0");
		output.put("rtnMsg", "成功！");
		output.put("object", object);
        
    	return output;
    }
    
    /**
     *  201804中移在线四轮驱动接口需求-宽带业务受理校验 
     * @param input
     * @throws Exception
     * @author yuyj3
     */
    public IData bussinessValidate(IData inputs) throws Exception
    {
    	if(true){
    		return bussinessValidate_HNKDZQ(inputs);
    	}
    	IData input = new DataMap(inputs.toString()).getData("params");
    	IData resultData = new DataMap();
    	IDataset resultDataset = new DatasetList();
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
	    	//服务号码
	    	IDataUtil.chkParam(input, "busiNum");
	    	
	    	//订单号码
	    	IDataUtil.chkParam(input, "busiType");
	    	
	    	//入参转换
	//    	transferDataInput(input);
	    	
	    	//0：宽带新装；1：宽带续约；2：增值产品办理；3：宽带提速；4：移机；5：宽带停复机；6：资源变更
	    	String busiType = input.getString("busiType");
	    	
	    	String serialNum = input.getString("busiNum");
	    	
    		if ("0".equals(busiType))
        	{
            	checkBeforeTrade(serialNum, "600");
        	}
        	else
        	{
        		IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(serialNum);
        		
        		if ("1".equals(busiType))
            	{
            		
            		//如果是无手机魔百和业务
            		if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
            		{
            			//
            			checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "681");
            			
            			IData inParam = new DataMap();
            			
            			inParam.put("USER_ID", wideTypeInfo.getString("WIDE_USER_ID"));
            			
            			//宽带预约拆机信息
            	        IDataset destroyInfos = CSAppCall.call("SS.NoPhoneWideDestroyUserSVC.getDestroyInfo", inParam);
            	        //有预约拆机报错
            	        if(IDataUtil.isNotEmpty(destroyInfos))
            	        {
            	        	String destroyState = destroyInfos.getData(0).getString("DESTORY_STATE","");
            	        	if("已预约".equals(destroyState))
            	        	{
            	        		CSAppException.appError("-1", "业务受理限制:该用户含有宽带预约拆机记录,不能办理该业务!");
            	        	}
            	        }
            	        
            	        IDataset userDiscntList = NoPhoneWideChangeProdBean.qryNoPhoneUserDiscnt(inParam);
            		    if (IDataUtil.isNotEmpty(userDiscntList))
            		    {
            		      //填充PRODUCT_ID、PACKAGE_ID
            	            FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscntList, null, null);// 填充productId和packageId
            		        
            		        if(userDiscntList.size()>1) 
            		        { 
            	        		CSAppException.appError("-1", "已续约，不能再次办理!");
            		        }
            		        else if(userDiscntList.size()==1)
            		        {
            		            
            		            //如果是下月后才生效的套餐，也不允许办。
            		            if(SysDateMgr.compareTo(SysDateMgr.getSysDate(), userDiscntList.getData(0).getString("START_DATE")) <= 0)
            		            {
                	        		CSAppException.appError("-1", "存在即将生效的宽带套餐!");
            		            }
            		        }
            		    }
            			else
            			{
            				//这种没有有效优惠的情况，则分为真的没有优惠；已经到期停机的优惠；2种情况。
            				//取该人员的优惠最后一条（日期最大）失效的记录。
            				IDataset userDiscntList2 = NoPhoneWideChangeProdBean.qryNoPhoneUserDiscnt2(inParam);
            				
            				if(IDataUtil.isEmpty(userDiscntList2))
            				{
            	        		CSAppException.appError("-1", "未查询到该用户有效的优惠信息!");
            				}
            			}
            		}
            		//有手机宽带
            		else
            		{
            			//
            			checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "601");
            			
            			IData params = new DataMap();
            			params.put("SERIAL_NUMBER", wideTypeInfo.getString("SERIAL_NUMBER"));
            			
            			CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetProductInfo", params);
            		}
            		
            	}
            	//增值产品办理
            	else if ("2".equals(busiType))
            	{
            		//如果是无手宽带业务
            		if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
            		{
            			//1.校验是否可以办理无手机魔百和业务
            			//业务规则校验
            			checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "4900");
            	        
            	        IDataset relaUUInfos = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(wideTypeInfo.getString("WIDE_USER_ID"),"47","1");
            	        
            	        if(!IDataUtil.isEmpty(relaUUInfos))
            	        {
            	        	IData userInfoA = relaUUInfos.first();
            	        	
            	        	String userIdA = userInfoA.getString("USER_ID_A");
            	        	
            	        	// 1.是否有购机信息
            	            IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userIdA, "4", "J");
            	            if (DataSetUtils.isNotBlank(boxInfos))
            	            {
            	        		CSAppException.appError("-1", "用户当前存在生效的魔百和业务，不能再办理!");
            	            }
            	        	
            	        	// 2.判断用户是否含有有效的平台业务
            	            IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userIdA, "51");//biz_type_code=51为互联网电视类的平台服务
            	            if (IDataUtil.isNotEmpty(platSvcInfos))
            	            {
            	        		CSAppException.appError("-1", "用户当前存在生效的魔百和平台业务，不能再办理!");
            	            }
            	            
            	            IDataset platSvcInfostow = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userIdA, "86");//biz_type_code=86为互联网电视类的平台服务
            	            if (IDataUtil.isNotEmpty(platSvcInfostow))
            	            {
            	        		CSAppException.appError("-1", "用户当前存在生效的魔百和平台业务，不能再办理!");
            	            }
            	            
            	            //4910工单为魔百和正式受理，用的是147号码
            	            IDataset trade3800 = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4910", userIdA, "0");
            	            if (IDataUtil.isNotEmpty(trade3800))
            	            {
            	        		CSAppException.appError("-1", "该用户有魔百和开户工单未完工!");
            	            }
            	        }
            	        
            	        //4900工单是魔百和预受理，用的宽带号码
            	        IDataset trade4900 = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4900", wideTypeInfo.getString("WIDE_USER_ID"), "0");
            	        if (IDataUtil.isNotEmpty(trade4900))
            	        {
        	        		CSAppException.appError("-1", "该用户有魔百和开户预受理工单未完工！");
            	        }
            		}
            		else
            		{
            			//1.校验是否可以办理无手机魔百和业务
            			//业务规则校验
            			checkBeforeTrade(wideTypeInfo.getString("SERIAL_NUMBER"), "4800");
            			
            	        //判断用户是否含有有效的平台业务
            	        IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(wideTypeInfo.getString("USER_ID"), "51");//biz_type_code=51为互联网电视类的平台服务
            	        
            	        if (IDataUtil.isNotEmpty(platSvcInfos))
            	        {
        	        		CSAppException.appError("-1", "该用户有魔百和开户预受理工单未完工！");
            	        }
        	            
            	        //2.校验是否可以办理IMS固话
            	        checkBeforeTrade(wideTypeInfo.getString("SERIAL_NUMBER"), "6800");
            	        
            	        // 2.1用户宽带FTTH类型宽带
                        IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wideTypeInfo.getString("WIDE_SERIAL_NUMBER")).first();
                        
                        if (IDataUtil.isEmpty(wideNetInfo))
                        {
        	        		CSAppException.appError("-1", "该用户宽带资料信息不存在！");
                        }
                        else
                        {
                        	if(!(StringUtils.equals("3", wideNetInfo.getString("RSRV_STR2", "")) || StringUtils.equals("5", wideNetInfo.getString("RSRV_STR2", ""))))
                            {
            	        		CSAppException.appError("-1", "用户宽带非FTTH类型宽带！");
                            }
                        }
                        
                        // 2.2用户是否已经办理家庭IMS固话
                        IDataset uuInfo = RelaUUInfoQry.getRelationUUInfoByDeputySn(wideTypeInfo.getString("USER_ID"), "MS",null);
                        
                        if (IDataUtil.isNotEmpty(uuInfo))
                        {
        	        		CSAppException.appError("-1", "该用户已经办理过家庭IMS固话业务！");
                        }
            	        
                        
                        //3.校验是否可以办理和目业务
            	        IDataset heMuSaleActives = UserSaleActiveInfoQry.getHeMuSaleActiveByUserId(wideTypeInfo.getString("USER_ID"));
            	        
            	        if (IDataUtil.isNotEmpty(heMuSaleActives))
            	        {
        	        		CSAppException.appError("-1", "该用户已经办理过和目业务！");
            	        }
            		}
            	}
            	//3：宽带提速
            	else if ("3".equals(busiType))
            	{
            		//如果是无手宽带业务
            		if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
            		{
            			checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "681");
            		}
            		else
            		{
            			checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "601");
            		}
            		
            	}
            	//4：移机
            	else if ("4".equals(busiType))
            	{
            		//如果是无手宽带业务
            		if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
            		{
            			checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "686");
            		}
            		else
            		{
            			checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "606");
            		}
            	}
            	//5：宽带停复机
            	else if ("5".equals(busiType))
            	{
            		IDataset mainSvcStates = UserSvcStateInfoQry.getUserMainState(wideTypeInfo.getString("WIDE_USER_ID"));
            		
            		//如果是无手宽带业务
            		if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
            		{
                		CSAppException.appError("-1", "无手机宽带没有停机业务，不能办理！");
            		}
            		else
            		{
            			//如果是正常状态则校验是否能停机
            			if ("0".equals(mainSvcStates.first().getString("STATE_CODE")))
            			{
            				checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "603");
            			}
            			else
            			{
            				checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "604");
            			}
            		}
            	}
            	else if ("6".equals(busiType))
            	{
            		CSAppException.appError("-1", "没有该业务场景，不能办理！");
            	}
        	}
			
		} catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
		}
    	
    	result.put("flag", flag);
    	result.put("desc", desc);
    	
    	resultDataset.add(result);
    	
        IDataset resultList = new DatasetList();
        if(!"0".equals(resultDataset.getData(0).getString("flag")))
		{
			resultData.put("FLAG","0" );
			resultData.put("retCode","0" );
			resultData.put("retMsg","校验成功" );
		}else {
			resultData.put("FLAG","-1" );
			resultData.put("retCode","-1" );
			resultData.put("retMsg","检验失败" );
		}
		resultList.add(resultData);
        return BroadBandUtil.requestData(resultList);
    }
    
    
    /**
     *  201804中移在线四轮驱动接口需求-商品办理资格校验 （校验选择的宽带套餐、宽带营销活动或提速包等商品是否能够办理）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData offerValidate(IData inputs) throws Exception
    {
    	if(true){
    		return offerValidate_HNKDZQ(inputs);
    	}
    	IData input = new DataMap(inputs.toString()).getData("params");
    	IDataset resultDataset = new DatasetList();
    	IData result = new DataMap();
    	
    	String flag = "1";
    	String desc = "校验成功";
    	try
		{
	    	//服务号码
	    	IDataUtil.chkParam(input, "busiNum");
	    	
	    	//订单号码
	    	IDataUtil.chkParam(input, "offerId");
	    	
	    	//入参转换
	    	transferDataInput(input);
	    	
	    	String offerId = input.getString("offerId");
	    	
	    	String serialNum = input.getString("SERIAL_NUMBER");
	    	
	    	IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(input.getString("SERIAL_NUMBER"));
	    	
	    	if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
	    	{
	    		CSAppException.appError("-1", "无手机宽带暂不支持此业务！");
	    	}
	    	else
	    	{
    			//"2"开头则表示宽带产品ID，其他表示宽带营销活动ID
            	if (offerId.startsWith("2"))
            	{
            		IData inParam = new DataMap();
            		
            		inParam.put("SERIAL_NUMBER", serialNum);
            		inParam.put("PRODUCT_ID", offerId);
            		//预约时间默认为下个月1号
            		inParam.put("BOOKING_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime()));
            		
            		CSAppCall.call("SS.WidenetChangeProductIntfSVC.checkProductInfoIntf", inParam);
            	}
            	else
            	{
            		IDataset wideProducts = UserProductInfoQry.queryMainProduct(wideTypeInfo.getString("WIDE_UESR_ID"));
            		
            		if (IDataUtil.isNotEmpty(wideProducts))
            		{
            			IData inParam = new DataMap();
                		
                		inParam.put("SERIAL_NUMBER", serialNum);
                		inParam.put("PRODUCT_ID", wideProducts.first().getString("PRODUCT_ID"));
                		inParam.put("SALE_ACTIVE_ID", offerId);
                		
                		//预约时间默认为下个月1号
                		inParam.put("BOOKING_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime()));
                		
                		CSAppCall.call("SS.WidenetChangeProductIntfSVC.checkWidenetSaleActiveIntf", inParam);
            		}
            		else
            		{
    					CSAppException.appError("-1", "宽带用户主产品不存在！");
            		}
            	}
			} 
    	}
    	catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
		}
    	
    	result.put("flag", flag);
    	result.put("desc", desc);
    	result.put("dynamicParams", flag);
    	
    	resultDataset.add(result);
    	
    	IData output = new DataMap();
		IData object = new DataMap();
		
		object.put("respCode", "0");
		object.put("respDesc", "success");
		object.put("result", resultDataset);
		
		output.put("rtnCode", "0");
		output.put("rtnMsg", "成功！");
		output.put("object", object);
        
    	return output;
    }
    
    
    
    /**
     *  201804中移在线四轮驱动接口需求-增值产品退订校验 （校验当前选择的增值产品（魔百和、IMS固话、和目等）是否可以退订）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData unsubscribeVAProductValidate(IData inputs) throws Exception
    {
    	IData input = new DataMap(inputs.toString()).getData("params");
    	IDataset resultDataset = new DatasetList();
    	IData result = new DataMap();
    	
    	String flag = "1";
    	String desc = "校验成功";
		try {
			//服务号码
			IDataUtil.chkParam(input, "busiNum");
			
			//产品编码
			IDataUtil.chkParam(input, "offerId");
			
			//入参转换
			transferDataInput(input);
			
			String offerId = input.getString("offerId");
			
			String serialNum = input.getString("SERIAL_NUMBER");
			
			IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(input.getString("SERIAL_NUMBER"));
			
			if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
			{
				CSAppException.appError("-1", "无手机宽带暂不支持此业务！");
			}
			else
			{
				IDataset topSetBoxProducts = CommparaInfoQry.getCommparaByCodeCode1("CSM", "182", "600", offerId);
				
				if (IDataUtil.isNotEmpty(topSetBoxProducts))
				{
					String userId = wideTypeInfo.getString("USER_ID");
					
					IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
					
					if(IDataUtil.isEmpty(boxInfos))
					{
			    		CSAppException.appError("-1", "用户未开通互联网电视，无法办理互联网拆机！");
			        }
					else
					{
						//业务校验
						checkBeforeTrade(serialNum, "3806");
					}
					
				}
				else if (offerId.endsWith("84004439"))
				{
					//获取主号信息
			         IDataset iDataset=RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(wideTypeInfo.getString("USER_ID"), "MS", "1");
			         
			         IDataset imsUserRelInfo = null;
			         
			         if(IDataUtil.isNotEmpty(iDataset))
			         {
			        	 //获取虚拟号
			        	 String userIdA=iDataset.getData(0).getString("USER_ID_A", "");
			        	 //通过虚拟号获取关联的IMS家庭固话号码信息
			        	 imsUserRelInfo = RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "2");
			         }
			         
			         if (IDataUtil.isEmpty(imsUserRelInfo))
			         {
			        	 CSAppException.appError("-1", "用戶沒有办理IMS固话业务，不能退订此增值产品！");
			         }
				}
				else
				{
					CSAppException.appError("-1", "暂不支持退订该宽带增值业务！");
				}
			}
		} catch (Exception e) {
			flag = "0";
			desc = e.getMessage();
		}
    	
    	result.put("flag", flag);
    	result.put("desc", desc);
    	result.put("dynamicParams", flag);
    	
    	resultDataset.add(result);
    	
    	IData output = new DataMap();
		IData object = new DataMap();
		
		object.put("respCode", "0");
		object.put("respDesc", "success");
		object.put("result", resultDataset);
		
		output.put("rtnCode", "0");
		output.put("rtnMsg", "成功！");
		output.put("object", object);
        
    	return output;
    }
    
    /**
     *  201804中移在线四轮驱动接口需求-增值产品办理业务受理预校验 （）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData orderVAProductCheck(IData inputs) throws Exception
    {
    	IData input = new DataMap(inputs.toString()).getData("params");
    	IDataset resultDataset = new DatasetList();
    	IData result = new DataMap();
    	
    	String flag = "1";
    	String desc = "校验成功";
    	
    	if (IDataUtil.isEmpty(input.getData("custInfo")))
    	{
    		flag = "0";
    		desc = "接口参数检查: 输入参数[custInfo]不存在或者参数值为空";
    	}
    	else if (IDataUtil.isEmpty(input.getData("broadBandInfo")))
    	{
    		flag = "0";
    		desc = "接口参数检查: 输入参数[broadBandInfo]不存在或者参数值为空";
    	}
    	else if (IDataUtil.isEmpty(input.getDataset("valueAddedProducts")))
    	{
    		flag = "0";
    		desc = "接口参数检查: 输入参数[valueAddedProducts]不存在或者参数值为空";
    	}
    	else if (IDataUtil.isEmpty(input.getData("feeInfo")))
    	{
    		flag = "0";
    		desc = "接口参数检查: 输入参数[feeInfo]不存在或者参数值为空";
    	}
    	else
    	{
    		if (StringUtils.isEmpty(input.getData("custInfo").getString("acceptNum")))
    		{
    			flag = "0";
        		desc = "接口参数检查: 输入参数[acceptNum]不存在或者参数值为空";
    		}
    	}
    	
    	if ("1".equals(flag))
    	{
    		try {
				IData custInfo = input.getData("custInfo");
				IData broadBandInfo = input.getData("broadBandInfo");
				IDataset valueAddedProducts = input.getDataset("valueAddedProducts");
				IData feeInfo = input.getData("feeInfo");
				
				IDataUtil.chkParam(feeInfo, "payType");
				
				//0：在线支付；1：话费抵扣；2：上门收费’
				if ("1".equals(feeInfo.getString("payType")))
				{
					IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(custInfo.getString("acceptNum"));
					
					//如果是无手宽带业务
					if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
					{
						flag = "0";
						desc = "无手机宽带暂不支持该业务办理！";
					}
					else
					{
						for (int i = 0; i < valueAddedProducts.size(); i++)
						{
							IData valueAddedProduct = valueAddedProducts.getData(i);
							
							//0：魔百和；1：和目；2：IMS固话；3：其他
							if ("0".equals(valueAddedProduct.getString("productType")))
							{

								if (IDataUtil.isNotEmpty(UserSvcInfoQry.checkInternetTvWide(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"))))
					            {
					                CSAppException.apperr(CrmCommException.CRM_COMM_103,"该宽带服务不允许办理魔百和开户"); // 该用户不是不允许的带宽
					            }
								
								//1.校验是否可以办理魔百和业务
				    			//业务规则校验
				    			checkBeforeTrade(wideTypeInfo.getString("SERIAL_NUMBER"), "4800");
				    			
				    	        //判断用户是否含有有效的平台业务
				    	        IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(wideTypeInfo.getString("USER_ID"), "51");//biz_type_code=51为互联网电视类的平台服务
				    	        
				    	        if (IDataUtil.isNotEmpty(platSvcInfos))
				    	        {
				    	        	flag = "0";
				            		desc= "该用户有魔百和开户预受理工单未完工！";
				    	        }
							}
							else if ("1".equals(valueAddedProduct.getString("productType")))
							{
								//3.校验是否可以办理和目业务
				    	        IDataset heMuSaleActives = UserSaleActiveInfoQry.getHeMuSaleActiveByUserId(wideTypeInfo.getString("USER_ID"));
				    	        
				    	        if (IDataUtil.isNotEmpty(heMuSaleActives))
				    	        {
				    	        	flag = "0";
				            		desc= "该用户已经办理过和目业务！";
				    	        }
							}
							else if ("2".equals(valueAddedProduct.getString("productType")))
							{
								//2.校验是否可以办理IMS固话
				    	        checkBeforeTrade(wideTypeInfo.getString("SERIAL_NUMBER"), "6800");
				    	        
				    	        // 2.1用户宽带FTTH类型宽带
				                IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wideTypeInfo.getString("WIDE_SERIAL_NUMBER")).first();
				                
				                if (IDataUtil.isEmpty(wideNetInfo))
				                {
				                    flag = "0";
				            		desc= "该用户宽带资料信息不存在！";
				                }
				                else
				                {
				                	if(!(StringUtils.equals("3", wideNetInfo.getString("RSRV_STR2", "")) || StringUtils.equals("5", wideNetInfo.getString("RSRV_STR2", ""))))
				                    {
				                    	flag = "0";
				    	        		desc= "用户宽带非FTTH类型宽带！";
				                    }
				                }
				                
				                // 2.2用户是否已经办理家庭IMS固话
				                IDataset uuInfo = RelaUUInfoQry.getRelationUUInfoByDeputySn(wideTypeInfo.getString("USER_ID"), "MS",null);
				                
				                if (IDataUtil.isNotEmpty(uuInfo))
				                {
				                    flag = "0";
				            		desc= "该用户已经办理过家庭IMS固话业务！";
				                }
							}
							else
							{
								flag = "0";
				        		desc = "暂不支持其他类型宽带增值业务办理！";
							}
						}
					}
				}
				else
				{
					flag = "0";
					desc= "当前付费方式暂只支持话费抵扣！";
				}
			} catch (Exception e) {
				flag = "0";
				desc = e.getMessage();
			}
    	}
    	
    	result.put("flag", flag);
    	result.put("desc", desc);
    	result.put("dynamicParams", flag);
    	
    	resultDataset.add(result);
    	
    	IData output = new DataMap();
		IData object = new DataMap();
		
		object.put("respCode", "0");
		object.put("respDesc", "success");
		object.put("result", resultDataset);
		
		output.put("rtnCode", "0");
		output.put("rtnMsg", "成功！");
		output.put("object", object);
        
    	return output;
    }
    
   
    /**
     * 业务校验
     * @param serialNum
     * @param tradeTypeCode
     * @throws Exception
     */
    private void checkBeforeTrade(String serialNum, String tradeTypeCode) throws Exception 
    {
    	IData input = new DataMap();
    	
    	IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNum);
        
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的用户信息！");
        }
        
        IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
        
        if (IDataUtil.isEmpty(customerInfo))
        {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的客户信息！");
        }
        
        input.putAll(userInfo);
        input.put("IS_REAL_NAME", customerInfo.getString("IS_REAL_NAME"));
        input.put("TRADE_TYPE_CODE", tradeTypeCode);
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
       
        input.put("X_CHOICE_TAG", "0");
        
        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        
        IDataset infos = CSAppCall.call( "CS.CheckTradeSVC.checkBeforeTrade", input);
        
        CSAppException.breerr(infos.getData(0));
	}
    
    /**
	 * 查询用户列表接口        (已废弃)
	 * 根据产品实例id、客户编码查询用户产品列表
	 * @author zhengkai5
	 * @throws Exception 
	 */
	public IData BroadbandListInfo(IData param) throws Exception{
		
		IDataUtil.chkParam(param, "idNo");
		IDataUtil.chkParam(param, "custId");
		
		IDataset result = new DatasetList();
		String instId = param.getString("idNo");//产品ID
		String custId = param.getString("custId");//客户ID
		String sysDate = SysDateMgr.getSysTime();
		
		IDataset userInfos = UserInfoQry.getAllUserInfoByCustId(custId);
		if(IDataUtil.isNotEmpty(userInfos))
		{
			for(int j = 0;j<userInfos.size();j++)
			{
				IData outInfoDate = new DataMap();
				IData outInfoList = new DataMap();
				IData userInfo = userInfos.getData(j);
				if(userInfo.getString("SERIAL_NUMBER").startsWith("KD_"))
				{
					String userId = userInfo.getString("USER_ID");
					IDataset userProductInfos = UserProductInfoQry.getuserProductByUserIdInstId(userId,instId);
					if(IDataUtil.isNotEmpty(userProductInfos))
					{
						IDataset outInfo = new DatasetList();
						for(int i = 0;i<userProductInfos.size();i++)
						{
							IData userProductInfo = userProductInfos.getData(i);
							IData  data = new DataMap();
							String serialNumber = userInfo.getString("SERIAL_NUMBER");
							String brandCode = userProductInfo.getString("BRAND_CODE");
							String prodectId = userProductInfo.getString("PRODUCT_ID");
							String brandName = UBrandInfoQry.getBrandNameByBrandCode(brandCode);
							String productName = UProductInfoQry.getProductNameByProductId(prodectId);
							data.put("srvCode", serialNumber.replace("KD_", ""));  //服务号码
							data.put("idNo", instId);  
							data.put("prodName", productName);   //产品名称
							data.put("prodId", prodectId);      //产品id
							data.put("brandName", brandName);   //品牌名称
							data.put("dynamicParams", "");  
							if (userProductInfo.getString("START_DATE").compareTo(sysDate) < 0)
							{
								data.put("runCode", "已销"); //状态编码
							}else {
								data.put("runCode", "预销");
							}
							outInfo.add(data);
						}
						outInfoList.put("outInfo", outInfo);
						outInfoDate.put("outInfoList", outInfoList);
					}
				}
				if(IDataUtil.isNotEmpty(outInfoDate))
				{
					result.add(outInfoDate);
				}
			}
		}
		return BroadBandUtil.requestData(result);
	}
	
	/**
	 * 套餐信息查询接口
	 * @author zhengkai5
	 * @throws Exception 
	 * */
	public IData QueryPackageInfo (IData params) throws Exception {
		
		IData param = new DataMap(params.toString()).getData("params");
		
		IDataset feeRecord = new DatasetList();
		
		IData feeRecords= new DataMap();
		
		IData feeRecordresult= new DataMap();
		
		IDataset result = new DatasetList();
		
		try {
			IDataUtil.chkParam(param, "busiCode");
			
			IData userData = WideNetUtil.getWideNetTypeInfo(param.getString("busiCode",""));
			String wideSerialNumber = userData.getString("WIDE_SERIAL_NUMBER");
			String wideUserId = userData.getString("WIDE_USER_ID");
			String userId = userData.getString("USER_ID");
			String isNoPhoneWideNet = userData.getString("IS_NOPHONE_WIDENET");
			
			/**
			 * 查询用户服务
			 * */
			IDataset userSvcs = UserSvcInfoQry.queryUserSvcByUserId(wideUserId);
			if (IDataUtil.isNotEmpty(userSvcs))
			{
				for(int i=0;i<userSvcs.size();i++)
				{
					IData userSvc = userSvcs.getData(i);
					String serviceId = userSvc.getString("SERVICE_ID");
					IDataset offerChas = UpcCall.queryOfferNameByOfferCodeAndType(BofConst.ELEMENT_TYPE_CODE_SVC,serviceId);
					if(IDataUtil.isNotEmpty(offerChas))
					{
						IData offercha = offerChas.getData(0);
						IData svcData = new DataMap();
						svcData.put("startTime", userSvc.getString("START_DATE"));
						svcData.put("endTime", userSvc.getString("END_DATE"));
						svcData.put("feeType", BofConst.ELEMENT_TYPE_CODE_SVC);   //资费类型
						svcData.put("feeName", offercha.getString("OFFER_NAME"));   //资费名称
						svcData.put("operChannel", "0");  //操作渠道
						svcData.put("operId",UStaffInfoQry.getStaffNameByStaffId(userSvc.getString("UPDATE_STAFF_ID")));  //操作员
						svcData.put("feeContent",offercha.getString("DESCRIPTION"));  //套餐资费详情
			    		svcData.put("remark", userSvc.getString("REMARK"));  //备注
			    		feeRecord.add(svcData);
					}
				}
			}
			
			/**
			 * 无手机宽带没有营销活动
			 * */
			if("N".equals(isNoPhoneWideNet))
			{
				//查看用户营销活动
				IDataset saleActives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId);
				IDataset results = CommparaInfoQry.getCommpara("CSM", "178", "600", "0898");
				IDataset kdActives = BreQryForCommparaOrTag.getCommpara("CSM", 212, "WIDE_YEAR_ACTIVE", CSBizBean.getUserEparchyCode());
				kdActives.addAll(results);
				
				if ((saleActives != null && saleActives.size() > 0)&&(kdActives != null && kdActives.size() > 0))
				{
					for (int i = 0; i < saleActives.size(); i++)
					{
						IData element = saleActives.getData(i);
						for(int j=0;j<kdActives.size();j++){
							if(element.getString("PRODUCT_ID").equals(kdActives.getData(j).getString("PARA_CODE1"))){
								IData effActive = new DataMap();
								String packageId = element.getString("PACKAGE_ID");
								IDataset offerChas = UpcCall.queryOfferComChaByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE,packageId);
								if(IDataUtil.isNotEmpty(offerChas)){
									IData offercha = offerChas.getData(0);
									effActive.put("startTime", element.getString("START_DATE"));
									effActive.put("endTime", element.getString("END_DATE"));
									effActive.put("feeType", "02");   //资费类型
									effActive.put("feeName", offercha.getString("OFFER_NAME"));   //资费名称
									effActive.put("operChannel", "0");  //操作渠道
									effActive.put("operId",UStaffInfoQry.getStaffNameByStaffId(element.getString("UPDATE_STAFF_ID")));  //操作员
									effActive.put("feeContent", offercha.getString("DESCRIPTION"));  //套餐资费详情
									effActive.put("remark", element.getString("REMARK"));  //备注
									feeRecord.add(effActive);
								}
							}
						}
					}
				}
			}
			
			/**
			 * 查询用户优惠
			 * */
			IData KVuserInfo = UserInfoQry.getUserInfoBySN(wideSerialNumber.replace("KD_", "KV_"));
			if(IDataUtil.isNotEmpty(KVuserInfo))
			{
				IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(KVuserInfo.getString("USER_ID"));
				if (IDataUtil.isNotEmpty(useDiscnts)){
					for(int j=0;j<useDiscnts.size();j++){
						IData userDs = useDiscnts.getData(j);
						IDataset offerChas = UpcCall.queryOfferNameByOfferCodeAndType(BofConst.ELEMENT_TYPE_CODE_DISCNT,userDs.getString("DISCNT_CODE"));
						if(IDataUtil.isNotEmpty(offerChas))
						{
							IData offercha = offerChas.getData(0);
							IData disData = new DataMap();
							disData.put("startTime", userDs.getString("START_DATE"));
							disData.put("endTime", userDs.getString("END_DATE"));
							disData.put("feeType", BofConst.ELEMENT_TYPE_CODE_DISCNT);   //资费类型
							disData.put("feeName", offercha.getString("OFFER_NAME"));   //资费名称
							disData.put("operChannel", "0");  //操作渠道
							disData.put("operId",UStaffInfoQry.getStaffNameByStaffId(userDs.getString("UPDATE_STAFF_ID")));  //操作员
							disData.put("feeContent", offercha.getString("DESCRIPTION"));  //套餐资费详情
							disData.put("remark", userDs.getString("REMARK"));  //备注
							feeRecord.add(disData);
						}
					}
				}
			}
		} catch (Exception e) {
			IData errorData = new DataMap();
			
			errorData.put("errorMessage", e.getMessage());
			errorData.put("FLAG", "-1");
			
			result.add(errorData);
			return BroadBandUtil.requestData(result);
		}
		
		feeRecords.put("feeRecord", feeRecord);
		
		feeRecordresult.put("feeRecords", feeRecords);
		
		result.add(feeRecordresult);

		
		return BroadBandUtil.requestData(result);
	}
	
	/**
	 * 证件鉴权接口
	 * @author zhengkai5
	 * @throws Exception 
	 * */
	public IData AuthIDCardInfo(IData params) throws Exception
	{ 
		IData param = new DataMap(params.toString()).getData("params");
		
		IDataUtil.chkParam(param, "account");  //手机账号
		IDataUtil.chkParam(param, "idType");   //证件类型
		IDataUtil.chkParam(param, "idCard");   //证件号码
		
		param.put("userMobile", param.getString("account"));
		param.put("idType", param.getString("idType"));
		param.put("idValue", param.getString("idCard"));
		
		IDataset result = CSAppCall.call("C898HQauthUserIden", param);
		
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();
		
		if("0".equals(result.getData(0).getString("rtnCode")))
		{
			resultData.put("FLAG","0" );
			resultData.put("retCode","0" );
			resultData.put("retMsg","验证成功" );
		}else {
			resultData.put("FLAG","-1" );
			resultData.put("retCode","-1" );
			resultData.put("retMsg","验证失败" );
		}
		resultList.add(resultData);
        return BroadBandUtil.requestData(resultList);
	}
	
	/**
	 * 密码验证接口
	 * @author zhengkai5
	 * @throws Exception 
	 * */
	public IData CheckPasswordInfo(IData params) throws Exception
	{
		IData param = new DataMap(params.toString()).getData("params");
		
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();
		
		IDataset result = new DatasetList();
		try {
			IDataUtil.chkParam(param, "account");    //账号
			IDataUtil.chkParam(param, "password");   //密码
			IDataUtil.chkParam(param, "accessMode"); //接入方式
			
			String sn = param.getString("account");
			if(!sn.startsWith("KD_"))
			{
				sn = "KD_"+sn;
			}
			
			param.put("userMobile", sn);
			param.put("password", param.getString("password"));
			
			result = CSAppCall.call("C898HQauthUserPasswd", param);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			resultData.put("FLAG","-1" );
			resultData.put("retCode","-1" );
			resultData.put("retMsg",errorMessage);
			resultList.add(resultData);
	        return BroadBandUtil.requestData(resultList);
		}
		
		if("0".equals(result.getData(0).getString("rtnCode")))
		{
			resultData.put("FLAG","0" );
			resultData.put("retCode","0" );
			resultData.put("retMsg","验证成功" );
		}else {
			resultData.put("FLAG","-1" );
			resultData.put("retCode","-1" );
			resultData.put("retMsg","验证失败" );
		}
		resultList.add(resultData);
        return BroadBandUtil.requestData(resultList);
	}
	
	/**
	 * 密码修改接口
	 *  @author zhengkai5
	 * @throws Exception 
	 * */
	public IData AlterPassword(IData params) throws Exception
	{
		IData param = new DataMap(params.toString()).getData("params");
		
		IDataset result = null;
		try {
			String sn =  IDataUtil.chkParam(param, "account");       //账号
			IDataUtil.chkParam(param, "oldPassWord");   //旧密码
			IDataUtil.chkParam(param, "newPassword");   //新密码
			IDataUtil.chkParam(param, "accessMode");    //接入方式
			
			if(!sn.startsWith("KD_"))
			{
				sn = "KD_"+sn;
			}
			
			//密码校验
			/*IData inputchk = new DataMap();
			inputchk.put("userMobile", sn);
			inputchk.put("password", param.getString("oldPassWord"));
			IDataset resultchk = CSAppCall.call("C898HQauthUserPasswd", inputchk);
			if("0".equals(resultchk.getData(0).getString("rtnCode")))
			{

			}
			else {
				CSAppException.appError("-1", "旧密码校验失败，请输入正确的旧密码");
			}*/

			param.put("SERIAL_NUMBER",sn );
			param.put("OLDPASSWD", param.getString("oldPassWord"));
			param.put("NEW_PASSWORD2", param.getString("newPassword"));

			param.put("QUERY_TYPE", "1");   //密码类型 ：3 密码重置   ，1 密码修改  ,2 新增密码  ， 4 取消密码

			param.getString("RESET_TAG","0");      //密码标识 0：宽带密码修改；1是重置密码，不用校验密码
			//param.getString("INSPECTION_TAG","0"); //修改模式 0：密码修改；1 身份修改，不用校验密码

			result = CSAppCall.call("SS.WidenetPswSVC.PswChg", param);  //  SS.WidenetPswChgRegSVC.tradeReg
		} catch (Exception e) {
			IData exception = new DataMap();
			exception.put("retMsg", e.toString());
			exception.put("retCode", "-1");
			exception.put("FLAG", "-1");
			return BroadBandUtil.requestData(IDataUtil.idToIds(exception));
		}
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();
		
		if("0".equals(result.getData(0).getString("X_RESULTCODE")))
		{
			resultData.put("FLAG","0" );
			resultData.put("retCode","0" );
			resultData.put("retMsg","密码修改成功" );
		}else {
			resultData.put("FLAG","-1" );
			resultData.put("retCode","-1" );
			resultData.put("retMsg","密码修改失败" );
		}
		resultList.add(resultData);
        return BroadBandUtil.requestData(resultList);
	}
	
	/**
	 * 宽带产品套餐信息查询
	 * 查询宽带套餐信息，区分业务场景（新装、续约等）和宽带类型（单宽带和融合等）
	 * 
	 * 海南所有宽带产品都为单宽带
	 * 新装，续约：都是同一个产品
	 * 
	 *   无增值产品
	 *  @author zhengkai5
	 * @throws Exception 
	 * */
	public IData QueryMKPackageInfo(IData params) throws Exception{
		if(true){
			return QueryMKPackageInfo_HNKDZQ(params);
		}
		IData param = new DataMap(params.toString()).getData("params");
		
		IDataset result = new DatasetList();
		IDataset packageList = new DatasetList();
		IData resultData = new DataMap();
		try {
			IDataUtil.chkParam(param, "busiType");          //0：宽带新装；1：宽带续约
			IDataUtil.chkParam(param, "broadBandTypeId");   //0: 单宽带；   1：融合宽带   
			IDataUtil.chkParam(param, "regionId");          //地市编码   
			
			//融合宽带：1+营销活动
			if(param.getString("broadBandTypeId").equals("1"))
			{
				return null;
			}
			else
			{	
				
			
				String[] productModes = {"09","11","07"};
				for (int i = 0; i < productModes.length; i++) {
					String productMode = productModes[i];
					IDataset widenetProductInfos = WidenetInfoQry.getWidenetProduct_RATE(productMode, param.getString("regionId"));
					for (int j = 0; j < widenetProductInfos.size(); j++) {
						
						
						IData packageData = new DataMap();
						
						IData wideProductInfo = widenetProductInfos.getData(j);
						String packageId = wideProductInfo.getString("PRODUCT_ID");
						String packageName = wideProductInfo.getString("PRODUCT_NAME");  
						String offerRate = wideProductInfo.getString("WIDE_RATE");        //速率
						String packageContent = wideProductInfo.getString("PRODUCT_NAME");        //套餐内容
						String packageDesc = wideProductInfo.getString("PRODUCT_NAME");        //套餐描述
						
						packageData.put("packageId", packageId);
						packageData.put("packageName", packageName);
						packageData.put("offerRate", offerRate);   //速率
						
						packageData.put("packagType", "0");  //所有的宽带产品都是单宽带
						
						packageData.put("packageContent", packageContent);  //包id
						packageData.put("packageDesc", packageDesc);  //品牌
						
						IDataset valueAddedProducts = new DatasetList();
						
						//魔百和
						IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");
						for (int k = 0; k < topSetBoxProducts.size();k++)
						{
							IData topSetBoxInfo = new DataMap();
							topSetBoxInfo.put("productId", topSetBoxProducts.getData(k).getString("PRODUCT_ID"));
							topSetBoxInfo.put("productName", topSetBoxProducts.getData(k).getString("PRODUCT_NAME"));
							topSetBoxInfo.put("productBrand", topSetBoxProducts.getData(k).getString("PRODUCT_NAME"));
							valueAddedProducts.add(topSetBoxInfo);
						}
						
						IData IMSProductTypeInfo = new DataMap();
						IMSProductTypeInfo.put("ROUTE_EPARCHY_CODE",  "0898");
						IDataset ProductTypeList = CSAppCall.call("SS.IMSLandLineSVC.onInitTrade", IMSProductTypeInfo);
						for (int x = 0; x < ProductTypeList.size(); x++) 
						{
							String productTypeCode = ProductTypeList.getData(x).getString("PRODUCT_TYPE_CODE");
							String productTypeName = ProductTypeList.getData(x).getString("PRODUCT_TYPE_NAME");
							IMSProductTypeInfo.put("ROUTE_EPARCHY_CODE", "0898");
							IMSProductTypeInfo.put("IMS_PRODUCT_TYPE_CODE", productTypeCode);
							IDataset dataset = CSAppCall.call("SS.MergeWideUserCreateSVC.getIMSProductByType", IMSProductTypeInfo);
							for (int y = 0; y < dataset.size();y++) 
							{
								IData IMSInfo = new DataMap();
								IMSInfo.put("productId",dataset.getData(y).getString("OFFER_CODE"));
								IMSInfo.put("productName",dataset.getData(y).getString("OFFER_NAME"));
								IMSInfo.put("productBrand",productTypeName);
								valueAddedProducts.add(IMSInfo);
							}
						}
						
						packageData.put("valueAddedProducts", valueAddedProducts);
						packageList.add(packageData);
					}
				}
				resultData.put("packageList", packageList);
			}
		} catch (Exception e) {
			IData errData = new DataMap();
			String err = e.getMessage();
			errData.put("FLAG", "-1");
			errData.put("errorMessage", err);
			result.add(errData);
			return BroadBandUtil.requestData(result);
		}
		result.add(resultData);
		return BroadBandUtil.requestData(result);
	}
	
	/**
	 * 宽带营销活动查询
	 * 查询宽带产品可办理的营销活动信息，区分业务场景（新装、续约等）和宽带类型（单宽带和融合等）。
	 * 
	 * 1+营销活动为融合宽带，
	 * 包年为单宽带；
	 * @author zhengkai5
	 * @throws Exception 
	 * */
	public IData QueryActivityInfo(IData params) throws Exception
	{
		if(true){
			return QueryActivityInfo_HNKDZQ(params);
		}
		IData param = new DataMap(params.toString()).getData("params");
		
		IDataUtil.chkParam(param, "busiType");          //0：宽带新装；1：宽带续约；
		IDataUtil.chkParam(param, "broadBandTypeId");   //0:单宽带；     1：融合宽带 
		
		IDataset activityList = new DatasetList();
		
		IDataset saleActiveList = null;
		 //新装  ：  600
		if(param.getString("busiType").equals("0"))
		{
			saleActiveList = CommparaInfoQry.getCommparaInfos("CSM", "178", "600");
		}// 宽带续约  601
		else if(param.getString("busiType").equals("1"))
		{
			saleActiveList = CommparaInfoQry.getCommparaInfos("CSM", "178", "601");
		}
		
		//去重
		IDataset saleActives = BroadBandUtil.distinct(saleActiveList);
		
		if(IDataUtil.isNotEmpty(saleActives))
		{
			for(int i =0;i<saleActives.size();i++)
			{
				IData activity = new DataMap();
				IData wideNetSaleActive = saleActives.getData(i);
				
				//融合宽带：1+营销活动
				if(param.getString("broadBandTypeId").equals("1"))
				{
					if(!"WIDE_YEAR_ACTIVE".equals(wideNetSaleActive.getString("PARA_CODE7","")))
					{
						String saleActiveId = wideNetSaleActive.getString("PARA_CODE5");
						IData saleActivePackageInfo = UPackageInfoQry.getPackageByPK(saleActiveId);
						
						activity.put("activityId", saleActiveId);
						activity.put("activityName", saleActivePackageInfo.getString("PACKAGE_NAME"));
						activity.put("activityDesc", saleActivePackageInfo.getString("DESCRIPTION"));
						activity.put("activityFee", saleActivePackageInfo.getString("DESCRIPTION"));  //营销活动费用
						activity.put("effDate", saleActivePackageInfo.getString("START_DATE"));
						activity.put("expDate", saleActivePackageInfo.getString("END_DATE"));
						activityList.add(activity);
					}
				}
				else
				{
					//包年优惠
					if("WIDE_YEAR_ACTIVE".equals(wideNetSaleActive.getString("PARA_CODE7","")))
					{
						String saleActiveId = wideNetSaleActive.getString("PARA_CODE5");
						IData saleActivePackageInfo = UPackageInfoQry.getPackageByPK(saleActiveId);
						
						activity.put("activityId", saleActiveId);
						activity.put("activityName", saleActivePackageInfo.getString("PACKAGE_NAME"));
						activity.put("activityDesc", saleActivePackageInfo.getString("DESCRIPTION"));
						activity.put("activityFee", saleActivePackageInfo.getString("DESCRIPTION"));
						activity.put("effDate", saleActivePackageInfo.getString("START_DATE"));
						activity.put("expDate", saleActivePackageInfo.getString("END_DATE"));
						activityList.add(activity);
					}
				}
			}
		}
		IDataset result = new DatasetList();
		IData activityLists = new DataMap();
		activityLists.put("activityList", activityList);
		result.add(activityLists);
		return BroadBandUtil.requestData(result);
	}
	
	/**
     *  201804中移在线四轮驱动接口需求-增值产品列表查询 （查询宽带用户增值产品（魔百和、和目和IMS固话等）信息列表）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData queryVAProductList(IData inputs) throws Exception
    {
    	IData input = new DataMap(inputs.toString()).getData("params");
    	
    	//入参转换
    	transferDataInput(input);
    	
    	//服务号码
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	
    	IData resultData = new DataMap();
    	IDataset resultdDataset = new DatasetList();
    	
        IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(input.getString("SERIAL_NUMBER"));
        
        //无手机宽带暂时不能办理魔百和、IMS固话、和目
        if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
        {
        	return resultData;
        }
        
        //手机号码用户ID
        String userId = wideTypeInfo.getString("USER_ID");
        
        IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");

        //如果用户办理了魔百合业务
        if (IDataUtil.isNotEmpty(boxInfos))
        {
        	IData boxInfo = boxInfos.first();
        	
        	IData resultTopSetBoxInfo = new DataMap();
        	
        	queryVAProductPutMethod(resultTopSetBoxInfo);
        	
        	String productId=boxInfo.getString("RSRV_STR1","");
        	
        	//0：魔百和；1：和目；2：IMS固话；3：其他
        	resultTopSetBoxInfo.put("productType", "0");
        	
        	//魔百和产品ID
        	resultTopSetBoxInfo.put("productId", productId);
        	
        	resultTopSetBoxInfo.put("productNum", productId);
            
            //查询产品的名称
            if(StringUtils.isNotEmpty(productId))
            {
            	//魔百和产品名称
            	resultTopSetBoxInfo.put("productName", UProductInfoQry.getProductNameByProductId(productId));
            	
            	String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
            	
            	if (StringUtils.isNotBlank(brandCode))
            	{
            		//魔百和产品品牌
            		resultTopSetBoxInfo.put("productBrand", UBrandInfoQry.getBrandNameByBrandCode(brandCode));
            	}
            }
            
            //必选包
            String basePackageInfo=boxInfo.getString("RSRV_STR2");

            if(StringUtils.isNotBlank(basePackageInfo))
            {
            	String[] basePackages = basePackageInfo.split(",");
            	
            	if(basePackages!=null && basePackages.length>0)
            	{
            		String serviceId = basePackages[0];
            		
            		if(StringUtils.isNotBlank(serviceId) &&!serviceId.trim().equals("-1")&&!serviceId.trim().equals("null"))
            		{
            			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
            			
            			//魔百和产品品牌
                		resultTopSetBoxInfo.put("packageCode", serviceId);
            			
            			if(IDataUtil.isNotEmpty(svcInfo))
            			{
            				resultTopSetBoxInfo.put("packageName", svcInfo.getString("SERVICE_NAME",""));
            				
            				resultTopSetBoxInfo.put("packageDesc", svcInfo.getString("DESCRIPTION",""));
            			}
            		}
            		
            		IDataset topsetBoxPlatSvcInfos = UserPlatSvcInfoQry.qryPlatSvcByUserIdServiceId(userId, serviceId);
            		
            		//设置魔百和基础套餐生失效时间
            		if (IDataUtil.isNotEmpty(topsetBoxPlatSvcInfos))
            		{
            			resultTopSetBoxInfo.put("effDate", topsetBoxPlatSvcInfos.first().getString("START_DATE",""));
            			resultTopSetBoxInfo.put("expDate", topsetBoxPlatSvcInfos.first().getString("END_DATE",""));
            		}
	            }
	        }
            
            //用户魔百和营销活动
            IDataset userTopSetBoxSaleActive = UserSaleActiveInfoQry.getTopSetBoxSaleActiveByUserId(userId);
            
            if (IDataUtil.isNotEmpty(userTopSetBoxSaleActive))
            {
            	resultTopSetBoxInfo.put("activityCode", userTopSetBoxSaleActive.first().getString("PACKAGE_ID"));
            	resultTopSetBoxInfo.put("activityName", userTopSetBoxSaleActive.first().getString("PACKAGE_NAME"));
            	
            	IData saleActivePackageInfo = UPackageInfoQry.getPackageByPK(userTopSetBoxSaleActive.first().getString("PACKAGE_ID"));
            	
            	if (IDataUtil.isNotEmpty(saleActivePackageInfo))
            	{
            		resultTopSetBoxInfo.put("activityDesc", saleActivePackageInfo.getString("DESCRIPTION"));
            	}
            }
            
            resultdDataset.add(resultTopSetBoxInfo);
        }
        
        //用户是否已经办理家庭IMS固话
        IDataset imsRelaInfo = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "MS", "1");
        
        if(IDataUtil.isNotEmpty(imsRelaInfo))
        {
        	IData imsResultData = new DataMap();
        	queryVAProductPutMethod(imsResultData);
        	//获取虚拟号
          	String userIdA = imsRelaInfo.getData(0).getString("USER_ID_A", "");
          	
            //通过虚拟号获取关联的IMS家庭固话号码信息
          	IDataset userBInfo = RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "2");
          	 
          	if(IDataUtil.isNotEmpty(userBInfo))
          	{
          		imsResultData.put("productType", "2");
          		
          		String imsUserId = userBInfo.getData(0).getString("USER_ID_B");
          		
          		IDataset imsProductInfo = UserProductInfoQry.queryUserMainProduct(imsUserId);
          		
          		if (IDataUtil.isNotEmpty(imsProductInfo))
          		{
          			imsResultData.put("productId", imsProductInfo.first().getString("PRODUCT_ID"));
          			imsResultData.put("productNum", imsProductInfo.first().getString("PRODUCT_ID"));
          			
          			imsResultData.put("productName", UProductInfoQry.getProductNameByProductId(imsProductInfo.first().getString("PRODUCT_ID")));
          			imsResultData.put("productBrand", UBrandInfoQry.getBrandNameByBrandCode(imsProductInfo.first().getString("BRAND_CODE")));
          		}
          		
          		
          		IDataset imsDiscntInfos = UserDiscntInfoQry.getAllDiscntInfo(imsUserId);
          		
          		
          		if (IDataUtil.isNotEmpty(imsDiscntInfos))
          		{
          			for (int i = 0; i < imsDiscntInfos.size(); i++)
          			{
          				if ("0".equals(imsDiscntInfos.getData(i).getString("SPEC_TAG")))
          				{
          					imsResultData.put("packageCode", imsDiscntInfos.getData(i).getString("DISCNT_CODE"));
                  			imsResultData.put("packageName", UDiscntInfoQry.getDiscntNameByDiscntCode(imsDiscntInfos.getData(i).getString("DISCNT_CODE")));
                  			imsResultData.put("packageDesc", UDiscntInfoQry.getDiscntExplainByDiscntCode(imsDiscntInfos.getData(i).getString("DISCNT_CODE")));
                  			
                  			imsResultData.put("effDate", imsDiscntInfos.getData(i).getString("START_DATE",""));
                  			imsResultData.put("expDate", imsDiscntInfos.getData(i).getString("END_DATE",""));
          				}
          			}
          		}
          		
          		
          		//用户IMS固话营销活动
                IDataset imSaleActive = UserSaleActiveInfoQry.getImsSaleActiveByUserId(userId);
                
                if (IDataUtil.isNotEmpty(imSaleActive))
                {
                	imsResultData.put("activityCode", imSaleActive.first().getString("PACKAGE_ID"));
                	imsResultData.put("activityName", imSaleActive.first().getString("PACKAGE_NAME"));
                	
                	IData saleActivePackageInfo = UPackageInfoQry.getPackageByPK(imSaleActive.first().getString("PACKAGE_ID"));
                	
                	if (IDataUtil.isNotEmpty(saleActivePackageInfo))
                	{
                		imsResultData.put("activityDesc", saleActivePackageInfo.getString("DESCRIPTION"));
                	}
                }
                resultdDataset.add(imsResultData);
          	}
        }
        
        //用户和目营销活动
        IDataset heMuSaleActives = UserSaleActiveInfoQry.getHeMuSaleActiveByUserId(userId);
        
        if (IDataUtil.isNotEmpty(heMuSaleActives))
        {
        	IData heMuResultData =  new DataMap();
        	queryVAProductPutMethod(heMuResultData);
        	IData heMuSaleActive = heMuSaleActives.first();
        	//0：魔百和；1：和目；2：IMS固话；3：其他
        	heMuResultData.put("productType", "1");
        	
        	heMuResultData.put("productId", heMuSaleActive.getString("PRODUCT_ID"));
        	heMuResultData.put("productNum", heMuSaleActive.getString("PRODUCT_ID"));
  			
        	
        	IDataset catalogInfos = UpcCall.qryCatalogByCatalogId(heMuSaleActive.getString("CAMPN_TYPE"));
        	
        	if (IDataUtil.isNotEmpty(catalogInfos))
    		{
    			heMuResultData.put("productName", catalogInfos.first().getString("CATALOG_NAME"));
    		}
        	
        	
        	if (StringUtils.isNotBlank(heMuSaleActive.getString("CAMPN_TYPE")))
        	{
        		IDataset upCatalogInfos = UpcCall.qryCatalogByCatalogId(heMuSaleActive.getString("CAMPN_TYPE"));
        		
        		if (IDataUtil.isNotEmpty(upCatalogInfos))
        		{
        			heMuResultData.put("productBrand", upCatalogInfos.first().getString("CATALOG_NAME"));
        		}
        	}
        	
        	//和目只有营销活动没套餐，所以将营销活动内容也设置到套餐内
        	heMuResultData.put("packageCode", heMuSaleActive.getString("PACKAGE_ID"));
        	heMuResultData.put("packageName", heMuSaleActive.getString("PACKAGE_NAME"));
        	
        	heMuResultData.put("activityCode", heMuSaleActive.getString("PACKAGE_ID"));
        	heMuResultData.put("activityName", heMuSaleActive.getString("PACKAGE_NAME"));
        	
        	IData saleActivePackageInfo = UPackageInfoQry.getPackageByPK(heMuSaleActive.getString("PACKAGE_ID"));
        	
        	if (IDataUtil.isNotEmpty(saleActivePackageInfo))
        	{
        		heMuResultData.put("packageDesc", saleActivePackageInfo.getString("DESCRIPTION"));
        		
        		heMuResultData.put("activityDesc", saleActivePackageInfo.getString("DESCRIPTION"));
        	}
        	
        	heMuResultData.put("effDate", heMuSaleActive.getString("START_DATE",""));
        	heMuResultData.put("expDate", heMuSaleActive.getString("END_DATE",""));
        	
        	resultdDataset.add(heMuResultData);
        }
        
        IData result =  new DataMap();
        result.put("productList", resultdDataset);
        
        IDataset resultlist = new DatasetList();
        resultlist.add(result);
        
        return BroadBandUtil.requestData(resultlist);
        
    }
	
    /**
     *  201804中移在线四轮驱动接口需求-已办理营销活动信息查询 （查询宽带用户已办理的宽带类营销活动列表）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData queryMKActivityList(IData inputs) throws Exception
    {
    	IData input = new DataMap(inputs.toString()).getData("params");
    	
    	//入参转换
    	transferDataInput(input);
    	
    	//服务号码
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	
    	IData resultData = new DataMap();
    	IDataset resultdDataset = new DatasetList();
    	
        IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(input.getString("SERIAL_NUMBER"));
        
        //无手机宽带暂时不能办理宽带营销活动
        if (!"Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
        {
        	//手机号码用户ID
            String userId = wideTypeInfo.getString("USER_ID");
            
            IDataset wideNetSaleActives = UserSaleActiveInfoQry.getWideNetSaleActiveByUserId(userId);
            
            if (IDataUtil.isNotEmpty(wideNetSaleActives))
            {
            	IData resultInfo = null;
            	IData wideNetSaleActive = null;
            	
            	for (int i = 0; i < wideNetSaleActives.size(); i++) 
            	{
            		resultInfo = new DataMap();
            		
            		wideNetSaleActive = wideNetSaleActives.getData(i);
            		
            		//0：魔百和；1：和目；2：IMS固话；3：其他
            		resultInfo.put("productType", "1");
            		
            		//活动编码
            		resultInfo.put("activityId", wideNetSaleActive.getString("PACKAGE_ID"));
            		//活动名称
                	resultInfo.put("activityName", wideNetSaleActive.getString("PACKAGE_NAME"));
                	
                	IData saleActivePackageInfo = UPackageInfoQry.getPackageByPK(wideNetSaleActive.getString("PACKAGE_ID"));
                	
                	if (IDataUtil.isNotEmpty(saleActivePackageInfo))
                	{
                		//活动描述
                		resultInfo.put("activityDesc", saleActivePackageInfo.getString("DESCRIPTION"));
                	}
                	
                	//生效时间
                	resultInfo.put("effDate", wideNetSaleActive.getString("START_DATE"));
                	//失效时间
                	resultInfo.put("expDate", wideNetSaleActive.getString("END_DATE"));
                	
                	resultdDataset.add(resultInfo);
    			}
            }
        }
        
        IData result =  new DataMap();
        result.put("activitytList", resultdDataset);
        
        IDataset resultlist = new DatasetList();
        resultlist.add(result);
        
        return BroadBandUtil.requestData(resultlist);
    }
    
    /**
     *  201804中移在线四轮驱动接口需求-宽带类型查询 （查询宽带类型，例如单宽带（与手机套餐资费无关联 ）、融合宽带（与手机套餐资费有关联）等）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData queryBroadBandType(IData inputs) throws Exception
    {
    	IDataset resultdDataset = new DatasetList();
		IData resultInfo = new DataMap();
		
		IDataset resultlist = new DatasetList();
		
		try {
			IData input = new DataMap(inputs.toString()).getData("params");
			
			//服务号码
			IDataUtil.chkParam(input, "busiNum");
			
			IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(input.getString("busiNum"));
			
			//无手机宽带都是单宽带
			if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
			{
				resultInfo.put("broadBandTypeId", "0");
				resultInfo.put("broadBandTypeName", "单宽带");
			}
			else
			{
				//手机号码用户ID
			    String userId = wideTypeInfo.getString("USER_ID");
			    
			    IDataset wideNetSaleActives = UserSaleActiveInfoQry.getWideNetSaleActiveByUserId(userId);
			    
			    //没办宽带营销活动则是单宽带
			    if (IDataUtil.isNotEmpty(wideNetSaleActives))
			    {
			    	IData wideNetSaleActive = null;
			    	
			    	boolean flag = false;
			    	
			    	for (int i = 0; i < wideNetSaleActives.size(); i++) 
			    	{
			    		wideNetSaleActive = wideNetSaleActives.getData(i);
			    		
			    		//宽带包年营销活动为单宽带，宽带1+营销活动为融合宽带
			    		if ("67220428".equals(wideNetSaleActive.getString("PRODUCT_ID")) || "67220429".equals(wideNetSaleActive.getString("PRODUCT_ID")))
			    		{
			    			flag = true;
			    			break;
			    		}
					}
			    	
			    	if (flag)
			    	{
			    		resultInfo.put("broadBandTypeId", "0");
			        	resultInfo.put("broadBandTypeName", "单宽带");
			    	}
			    	else
			    	{
			    		resultInfo.put("broadBandTypeId", "1");
			        	resultInfo.put("broadBandTypeName", "融合宽带");
			    	}
			    }
			    else
			    {
			    	resultInfo.put("broadBandTypeId", "0");
			    	resultInfo.put("broadBandTypeName", "单宽带");
			    }
			}
		} catch (Exception e) {
			IData error = new DataMap();
			error.put("FLAG", "-1");
			error.put("MESSAGE", e.getMessage());
			return BroadBandUtil.requestData(IDataUtil.idToIds(error));
		}
        
        resultdDataset.add(resultInfo);
        
        IData result =  new DataMap();
        result.put("broadBandTypeList", resultdDataset);
        
        resultlist.add(result);
        
        return BroadBandUtil.requestData(resultlist);
    }
     
    
    /**
	 * 宽带新装业务受理预校验
	 * @author zhengkai5
	 * @throws Exception 
	 * */
	public IData BroadBandInstallCheck(IData params) throws Exception
	{
		IData param = new DataMap(params.toString()).getData("params");
		
		IData custInfo = param.getData("custInfo");          //客户信息
		IData widenetInfo = param.getData("broadBandInfo");  //宽带产品信息
		
		IDataset groupInfo = param.getDataset("groupInfo");  //副卡成员信息    ？  
		
		IDataset valueAddedProducts = param.getDataset("valueAddedProducts");  //增值产品列表
		IData feeInfo = param.getData("feeInfo");  //费用信息
		IDataset expenseItemList = param.getDataset("expenseItemList");  //费用列表
		
		try {
			
			//宽带类业务只支持  话费抵扣
			if (!"1".equals(feeInfo.getString("payType")))
    		{
				CSAppException.appError("-1", "当前付费方式暂只支持话费抵扣！");
    		}
			
			/*
			 * 服务号码校验
			 * */
			IDataUtil.chkParam(custInfo, "acceptNum");
			String serialNumber = custInfo.getString("acceptNum");  //服务号码
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if(IDataUtil.isEmpty(userInfo))
			{
				CSAppException.appError("-1","该手机号码不存在有效的用户信息！");
			}
			String userId = userInfo.getString("USER_ID");
			//宽带or无手机宽带校验
			//IData widenet =  WideNetUtil.getWideNetTypeInfo(serialNumber);
			
			//宽带地址&&产品 
			IDataUtil.chkParam(widenetInfo, "standardAddressID");    //标准地址Id
			IDataUtil.chkParam(widenetInfo, "standardAddressName");  // 标准名称
			IDataUtil.chkParam(widenetInfo, "acceptType");           //接入方式：FTTB、FTTH、GPON
			String wideType = widenetInfo.getString("acceptType");  
			String productId = widenetInfo.getString("packageId");   //宽带产品id
			String saleActiveId = widenetInfo.getString("activityId");   // 宽带营销包id
			
			widenetInfo.put("WIDE_PRODUCT_ID", productId);      //宽带产品id
			widenetInfo.put("SALE_ACTIVE_ID", saleActiveId);   //宽带营销包
			widenetInfo.put("DISCNT_IDS", saleActiveId);       //优惠id
			
			//1.校验是否可以办理宽带业务
			//业务规则校验
			if(serialNumber.length() == 11)
			{
				checkBeforeTrade(serialNumber, "600");
			}else 
			{
				checkBeforeTrade(serialNumber, "680");
			}
			// 宽带产品校验
			//BroadBandUtil.chkProdeductInfo(widenetInfo);
			
			// 宽带地址端口校验
			IDataUtil.chkParam(widenetInfo, "freePort");           //可用端口数
			int freePort = widenetInfo.getInt("freePort");
			if (!(freePort>0)) {
				CSAppException.appError("-1", "该地址下已无可用端口！");
			}
			
			//无手机宽带   没有增值产品
			if(serialNumber.length() == 11)
			{
				
				// 增值产品校验
				if(IDataUtil.isNotEmpty(valueAddedProducts))
				{
					for (int i = 0; i < valueAddedProducts.size(); i++) {
						
						IData addedProduct = valueAddedProducts.getData(i);
						
						IDataUtil.chkParam(addedProduct, addedProduct.getString("productType"));  //0：魔百和；1：和目；2：IMS固话；3：其他
						
						IDataUtil.chkParam(addedProduct, addedProduct.getString("packageId"));  //增值产品ID
						
						String addProductType = addedProduct.getString("productType");
						
						addedProduct.put("STAFF_Id", param.getString("operatorId"));
						addedProduct.put("SERIAL_NUMBER", serialNumber);
						
						if("0".equals(addProductType))  //魔百和校验
						{
							//魔百和营销活动校验
							BroadBandUtil.chkTopSetBox(addedProduct);
							
							//判断用户是否含有有效的平台业务
                	        IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "51");//biz_type_code=51为互联网电视类的平台服务
                	        
                	        if (IDataUtil.isNotEmpty(platSvcInfos))
                	        {
                	        	CSAppException.appError("-1", "该用户有魔百和开户预受理工单未完工！");
                	        }
						
						}
						else if ("1".equals(addProductType))   // 和目营销活动校验
						{	
							
							//3.校验是否可以办理和目业务
                	        IDataset heMuSaleActives = UserSaleActiveInfoQry.getHeMuSaleActiveByUserId(userId);
                	        
                	        if (IDataUtil.isNotEmpty(heMuSaleActives))
                	        {
                	        	CSAppException.appError("-1", "该用户已经办理过和目业务！");
                	        }
							
							//  50M/100M 宽带 才可办理和目
							addedProduct.put("PRODUCT_ID",productId);
							
							//和目营销活动校验
							BroadBandUtil.chkHEMU(addedProduct);
					
						}
						else if ("2".equals(addProductType))  // IMS固话校验
						{  
							//ftth才能办理固话
							addedProduct.put("PRODUCT_ID",productId);
							addedProduct.put("WIDE_TYPE",wideType); //FTTB、FTTH、GPON
							
							if(!"FTTH".equals(wideType))
							{
								CSAppException.appError("-1", "用户宽带非FTTH类型宽带，无法办理IMS固话 ！");
							}
                             
                            // 2.2用户是否已经办理家庭IMS固话
                            IDataset uuInfo = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "MS",null);
                            
                            if (IDataUtil.isNotEmpty(uuInfo))
                            {
                            	CSAppException.appError("-1", "该用户已经办理过家庭IMS固话业务！");
                            }
							
                            //IMS固话营销活动校验
							BroadBandUtil.ChkIMS(addedProduct);
							
						}else if ("3".equals(addProductType))// 其他校验  :  光猫校验
						{  
							/*IData inParam = new DataMap();
						    inParam.put("SERIAL_NUMBER", serialNumber);
						    IDataset custModemInfo = new FTTHModemManageBean().getCustModermInfo(inParam);
						    if (IDataUtil.isNotEmpty(custModemInfo))
						    {
								CSAppException.appError("-1", "该用户已经租赁或者赠送过光猫不能再次租赁或赠送！请选择自备模式办理；或者光猫管理将原有光猫进行处理后再次办理。");
						    }*/
						}
					}
				}
			}
			
			/*
			 * 调PBOSS资源提供的接口，判断是否还有空闲的端口号 ? 入参中无设备ID(deviceID) ,无法判断
			 * 标准地址ID对应多个设备ID
			 * */ 
			
			/*
			 * 费用校验
			 * */
			IDataUtil.chkParam(feeInfo, "totalMoney");  //总费用
			String userCash = WideNetUtil.qryBalanceDepositBySn(serialNumber);
	        int allTotalTransFee = feeInfo.getInt("totalMoney");
	        
	        if(Integer.parseInt(userCash)< allTotalTransFee )
	        {
	        	StringBuffer message = new StringBuffer();
	        	message.append("您的账户存折可用余额不足，请先办理缴费。本次需转出费用：[") ;
	        	
	        	for (int i = 0; i < expenseItemList.size(); i++) {
					String expenseItemName = expenseItemList.getData(i).getString("expenseItemName");  //费用项
					String expenseItemMoney = expenseItemList.getData(i).getString("expenseItemMoney");  //费用项金额
					message.append(expenseItemName+":"+expenseItemMoney+"元;");
				}
	        	message.append("]") ;
	        	
				CSAppException.appError("-1", message.toString());
				
	        }
		} catch (Exception e) {
			
			IData resultparam = new DataMap();
			resultparam.put("flag", "-1");
			resultparam.put("desc", e.getMessage());
			return BroadBandUtil.result(resultparam);
		}
		
        IData resultparam = new DataMap();
		resultparam.put("flag", "1");
		resultparam.put("desc", "预校验通过");
		return BroadBandUtil.result(resultparam);
	}
	
	
	/**
	 * 增值产品品牌查询(QueryVAProductBrand)
	 * 
	 * 和目没有产品，品牌等信息
	 * @author zhengkai5
	 * @throws Exception 
	 * */
	public IData QueryVAProductBrand(IData params) throws Exception
	{
		IData param = new DataMap(params.toString()).getData("params");
		
		//品牌列表
		IDataset brandyList = new DatasetList();
		
		String productType = IDataUtil.chkParam(param, "productType");
		
		if ("0".equals(productType))  //0：魔百和；1：和目；2：IMS固话；3：其他
		{ 
			IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");
			for (int i = 0; i < topSetBoxProducts.size(); i++)
			{
				IData topSetBoxInfo = new DataMap();
				topSetBoxInfo.put("productId", topSetBoxProducts.getData(i).getString("PRODUCT_ID"));
				topSetBoxInfo.put("productName", topSetBoxProducts.getData(i).getString("PRODUCT_NAME"));
				topSetBoxInfo.put("brandId", topSetBoxProducts.getData(i).getString("PRODUCT_ID"));
				topSetBoxInfo.put("brandName", topSetBoxProducts.getData(i).getString("PRODUCT_NAME"));
				topSetBoxInfo.put("productType", productType);
				brandyList.add(topSetBoxInfo);
			}
		}
		else if ("2".equals(productType)) { //固话
			IData IMSData = new DataMap();
			
			IData IMSProductTypeInfo = new DataMap();
			IMSProductTypeInfo.put("ROUTE_EPARCHY_CODE",  "0898");
			IDataset ProductTypeList = CSAppCall.call("SS.IMSLandLineSVC.onInitTrade", IMSProductTypeInfo);
			for (int i = 0; i < ProductTypeList.size(); i++) 
			{
				String productTypeCode = ProductTypeList.getData(i).getString("PRODUCT_TYPE_CODE");
				String productTypeName = ProductTypeList.getData(i).getString("PRODUCT_TYPE_NAME");
				IMSData.put("ROUTE_EPARCHY_CODE", "0898");
				IMSData.put("IMS_PRODUCT_TYPE_CODE", productTypeCode);
				IDataset dataset = CSAppCall.call("SS.MergeWideUserCreateSVC.getIMSProductByType", IMSData);
				for (int j = 0; j < dataset.size(); j++) 
				{
					IData IMSInfo = new DataMap();
					IMSInfo.put("productId",dataset.getData(j).getString("OFFER_CODE"));
					IMSInfo.put("productName",dataset.getData(j).getString("OFFER_NAME"));
					IMSInfo.put("brandId",productTypeCode);
					IMSInfo.put("brandName",productTypeName);
					IMSInfo.put("productType",productType);
					brandyList.add(IMSInfo);
				}
			}
		}
		
		IData result =  new DataMap();
        result.put("brandyList", brandyList);
        IDataset resultlist = new DatasetList();
        resultlist.add(result);
        return BroadBandUtil.requestData(resultlist);
	}
	
	/**
	 * 增值产品营销活动查询(QueryVAProductActivity)
	 * 
	 * @author zhengkai5
	 * @throws Exception 
	 * */
	public IData QueryVAProductActivity(IData params)
	{
		
		
		return null;
	}
	
	

	/**
     *  201804中移在线四轮驱动接口需求-宽带提速包查询 （）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData QueryIncreasePackageInfo(IData inputs) throws Exception
    {
    	IDataset resultdDataset = new DatasetList();
		IData resultInfo = new DataMap();
		
		IDataset resultlist = new DatasetList();
		
		try 
		{
			IData input = new DataMap(inputs.toString()).getData("params");
			
			//服务号码
			IDataUtil.chkParam(input, "busiNum");
			
			IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(input.getString("busiNum"));
			
			//无手机宽带都是单宽带
			if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
			{
				IData error = new DataMap();
				error.put("FLAG", "-1");
				error.put("MESSAGE", "无手机宽带暂不支持此业务场景！");
				return BroadBandUtil.requestData(IDataUtil.idToIds(error));
			}
			else
			{
				//宽带号码用户ID
			    String userId = wideTypeInfo.getString("WIDE_USER_ID");
			    
			    IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
			    
			    if (IDataUtil.isNotEmpty(userMainProducts))
			    {
			    	String sysDate = SysDateMgr.getSysTime();
			    	
			    	String userProductId = "";
			    	String productMode = "";
			    	//用户宽带速率
			    	String userRate = "";
			    	
			    	int size = userMainProducts.size();
		            for (int i = 0; i < size; i++)
		            {
		                IData userProduct = userMainProducts.getData(i);
		                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
		                {
		                    userProductId = userProduct.getString("PRODUCT_ID");
		                    productMode = userProduct.getString("PRODUCT_MODE");
		                }
		            }
		            
		            if (StringUtils.isBlank(userProductId))
		            {
		            	CSAppException.appError("-1", "当前用户没有有效的宽带主产品，不能办理此业务！");
		            }
		            
		          IDataset userRates = CommparaInfoQry.getCommpara("CSM", "4000", userProductId, "0898");
		          
		          if (IDataUtil.isEmpty(userRates))
		          {
		              IDataset userSvcs  =  UserSvcInfoQry.queryUserSvcByUserIdAllNew(userId);
		              
		              if (IDataUtil.isNotEmpty(userSvcs))
		              {
		                  for (int i = 0; i < userSvcs.size(); i++)
		                  {
		                      if ("0".equals(userSvcs.getData(i).getString("MAIN_TAG")))
		                      {
		                    	  userRates = CommparaInfoQry.getCommpara("CSM", "4000", userSvcs.getData(i).getString("SERVICE_ID"), "0898");
		                          break;
		                      }
		                  }
		              }
		          }
		          
		          if (IDataUtil.isNotEmpty(userRates))
		          {
		        	  userRate = userRates.getData(0).getString("PARA_CODE1","0");
		          }
		          else
		          {
		              CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查到产品对应的速率，用户当前产品【"+userProductId+"】速率参数配置有误，请检查！");
		          }
		          
		          //修改原有方法，改为调用新增的查询产品列表的方法，新增的方法有速率信息
		          IDataset widenetProductInfos =WidenetInfoQry.getWidenetProduct_RATE(productMode, CSBizBean.getTradeEparchyCode());
		         
		          //商务宽带产品过滤
		          widenetProductInfos = WideNetUtil.filterBusinessProduct(wideTypeInfo.getString("SERIAL_NUMBER"), widenetProductInfos);
		          
		          // 产品权限控制
		          ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), widenetProductInfos);
		          
		          if (IDataUtil.isNotEmpty(widenetProductInfos))
		          {
		        	  for (int i = 0; i < widenetProductInfos.size(); i++)
			          {
		        		  IData widenetProductInfo = widenetProductInfos.getData(i);
		        		  
		        		  //只返回比用户当前速率大的宽带产品作为提速包
		        		  if (Integer.valueOf(widenetProductInfo.getString("WIDE_RATE","0")) > Integer.valueOf(userRate))
		        		  {
		        			  resultInfo = new DataMap();
		        			  
		        			  resultInfo.put("speedPackageId", widenetProductInfo.getString("PRODUCT_ID"));
		        			  resultInfo.put("speedPackageName", widenetProductInfo.getString("PRODUCT_NAME"));
		        			  resultInfo.put("speedPackageDesc", widenetProductInfo.getString("PRODUCT_EXPLAIN"));
		        			  
		        			  IDataset monthFeeDatas = CommparaInfoQry.getCommpara("CSM", "3200", widenetProductInfo.getString("WIDE_RATE"), "0898");

		        			  String fee = "0";
		        			  if (IDataUtil.isNotEmpty(monthFeeDatas))
		        			  {
								  fee = monthFeeDatas.getData(0).getString("PARA_CODE1");
		        			  }
		        			  
		        			  resultInfo.put("speedPackageFee", fee);
		        			  resultInfo.put("effDate", SysDateMgr.getFirstDayOfNextMonth());
		        			  resultInfo.put("expDate", SysDateMgr.END_DATE_FOREVER);
		        			  
		        			  resultdDataset.add(resultInfo);
		        		  }
			        	  
			          }
		          }
		          else
		          {
		        	  CSAppException.appError("-1", "当前用户没有可升级的提速包,不能办理此业务！");
		          }
		          
			    }
			    else
			    {
			    	CSAppException.appError("-1", "当前用户没有有效的宽带主产品，不能办理此业务！");
			    }
			}
		} catch (Exception e) {
			IData error = new DataMap();
			error.put("FLAG", "-1");
			error.put("MESSAGE", e.getMessage());
			return BroadBandUtil.requestData(IDataUtil.idToIds(error));
		}
        
        IData result =  new DataMap();
        result.put("speedPackageList", resultdDataset);
        
        resultlist.add(result);
        
        return BroadBandUtil.requestData(resultlist);
    }
    
    
    /**
     *  201804中移在线四轮驱动接口需求-提速费用计算 （）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData SpeedUpFeeCalculate(IData inputs) throws Exception
    {
    	IDataset resultdDataset = new DatasetList();
		IData resultInfo = new DataMap();
		IData result =  new DataMap();
		
		IDataset resultlist = new DatasetList();
		
		try 
		{
			IData input = new DataMap(inputs.toString()).getData("params");
			
			//服务号码
			IDataUtil.chkParam(input, "busiNum");
			IDataUtil.chkParam(input, "speedPackageId");
			
			IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(input.getString("busiNum"));
			
			//无手机宽带都是单宽带
			if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
			{
				IData error = new DataMap();
				error.put("FLAG", "-1");
				error.put("MESSAGE", "无手机宽带暂不支持此业务场景！");
				return BroadBandUtil.requestData(IDataUtil.idToIds(error));
			}
			else
			{
				//宽带号码用户ID
			    String userId = wideTypeInfo.getString("WIDE_USER_ID");
			    
			    IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
			    
			    if (IDataUtil.isNotEmpty(userMainProducts))
			    {
			    	String  speedProductId = input.getString("speedPackageId");
			    	String  productRate = "";
			    	
			    	IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, speedProductId, "1", "1");
	                
	                if (IDataUtil.isNotEmpty(forceElements))
	                {
	                    IDataset rate_ds = null;
	                    IData forceElement = null;
	                    
	                    for (int j = 0; j < forceElements.size(); j++)
	                    {
	                        forceElement = forceElements.getData(j);
	                        
	                        if ("S".equals(forceElement.getString("OFFER_TYPE")))
	                        {
	                            //根据产品下的服务ID查询宽带速率
	                            rate_ds = CommparaInfoQry.getCommpara("CSM", "4000",forceElement.getString("OFFER_CODE") , "0898");
	                            
	                            if (IDataUtil.isNotEmpty(rate_ds))
	                            {
	                                break;
	                            }
	                        }
	                    }
	                    
	                    if (IDataUtil.isNotEmpty(rate_ds))
	                    {
	                    	productRate = rate_ds.getData(0).getString("PARA_CODE1","");
	                    }
	                    
	                    IDataset monthFeeDatas = CommparaInfoQry.getCommpara("CSM", "3200", productRate, "0898");

	                    String fee = "0";
	        			if (IDataUtil.isNotEmpty(monthFeeDatas))
	        			{
	        				fee = monthFeeDatas.getData(0).getString("PARA_CODE1");
	        			}
	        	
	        			resultInfo.put("expenseItemName", "套餐月基础费用");
	        			resultInfo.put("expenseItemMoney",fee );
	        			
	        			resultdDataset.add(resultInfo);
	        			
	        	        result.put("expenseItemList", resultdDataset);
	        	        result.put("totalMoney", fee);
	                }
	                else
	                {
	                	CSAppException.appError("-1", "当前用户没有有效的宽带主产品，不能办理此业务！");
	                }
			    }
			}
		} 
		catch (Exception e) 
		{
			IData error = new DataMap();
			error.put("FLAG", "-1");
			error.put("MESSAGE", e.getMessage());
			return BroadBandUtil.requestData(IDataUtil.idToIds(error));
		}
        
        resultlist.add(result);
        
        return BroadBandUtil.requestData(resultlist);
    }
    
	/**
	 * 密码重置接口
	 *  @author zhengkai5
	 * @throws Exception 
	 * */
	public IData ResetPassword(IData params) throws Exception
	{
		IData param = new DataMap(params.toString()).getData("params");
		
		IDataUtil.chkParam(param, "account");       //账号
		IDataUtil.chkParam(param, "newPassword");   //新密码
		IDataUtil.chkParam(param, "accessMode");    //接入方式
		IDataUtil.chkParam(param, "opeUserId");		//操作人员ID
		IDataUtil.chkParam(param, "opeUserName");	//操作人员姓名
		IDataUtil.chkParam(param, "provinceId");	//省编码
		IDataUtil.chkParam(param, "regionId");		//地市编码
		IDataUtil.chkParam(param, "channelId");		//渠道标识
		
		param.put("SERIAL_NUMBER", param.getString("account"));
		param.put("NEW_PASSWORD2", param.getString("newPassword"));
		param.put("QUERY_TYPE", "1");
		
		IDataset result = new DatasetList();
		try {
 			result = CSAppCall.call("SS.WidenetPswSVC.PswChg", param);
		} catch (Exception e) {
			IData exception = new DataMap();
			exception.put("retMsg", e.toString());
			exception.put("FLAG", "-1");
			return BroadBandUtil.requestData(IDataUtil.idToIds(exception));
		}
		
		IData resultMap = new DataMap();
		IData object = new DataMap();
		IData returnMap = new DataMap();
		
		if("0".equals(result.getData(0).getString("X_RESULTCODE"))){
			resultMap.put("retCode", "0");
			resultMap.put("retMsg", "重置成功");
		}else{
			resultMap.put("retCode", "2998");
			resultMap.put("retMsg", "重置失败");
		}
		
		result.remove(0);
		result.add(resultMap);
		object.put("result", result);
		object.put("respCode ", "0");
		object.put("respDesc", "success");
		
		returnMap.put("object", object);
		returnMap.put("rtnMsg", "成功");
		returnMap.put("rtnCode", "0");
		return returnMap;
	}
	
	public IData AppointmentQuery(IData params) throws Exception{
		
		IData param = new DataMap(params.toString()).getData("params");
		IData appointInfo = param.getData("appointInfo");
		IData inputData = new DataMap();
		
		IDataUtil.chkParam(param, "provinceId");
		IDataUtil.chkParam(param, "regionId");
		IDataUtil.chkParam(param, "channelId");
		IDataUtil.chkParam(appointInfo, "beginDate");
		IDataUtil.chkParam(appointInfo, "endDate");
		
		inputData.put("BOOK_TYPE_CODE", "118");//宽带开户预约编码
		inputData.put("BOOK_DATE", appointInfo.getString("beginDate"));
		inputData.put("BOOK_END_DATE", appointInfo.getString("endDate"));
		inputData.put("BOOK_STATUS", "0");//固定取正常预约状态
		inputData.put("CITY_CODE", param.getString("regionId"));
		
		IData bookData = new DataMap();
		IData tempData = new DataMap();
		IDataset resultList = new DatasetList();
		
		IDataset bookList1 = CSAppCall.call("SS.TradeNetBookDealSVC.qryBookInfo", inputData);//查询宽带开户预约单
		if(IDataUtil.isNotEmpty(bookList1)){
			for(int i=0; i<bookList1.size(); i++){
				tempData = bookList1.getData(i);
				bookData.put("oddNum", tempData.getString("TRADE_ID"));
				String custName=tempData.getString("CUST_NAME");
				if(StringUtils.isNotEmpty(custName)&&custName.length()>1){
					custName=custName.substring(0, 1)+"**";
				}
				bookData.put("custName", custName);
				bookData.put("busiNum", tempData.getString("SERIAL_NUMBER"));
				bookData.put("linkName", "");//表中无联系人字段
				bookData.put("linkPhone", tempData.getString("CONTACT_PHONE"));
				bookData.put("regionId", tempData.getString("RSRV_STR1"));
				bookData.put("busiType", "1");//开户取1
				resultList.add(bookData);
			}
		}
		
		inputData.put("BOOK_TYPE_CODE", "119");//宽带移机预约编码
		inputData.put("BOOK_DATE", appointInfo.getString("beginDate"));
		inputData.put("BOOK_END_DATE", appointInfo.getString("endDate"));
		inputData.put("BOOK_STATUS", "0");
		inputData.put("CITY_CODE", appointInfo.getString("regionId"));
		
		IDataset bookList2 = CSAppCall.call("SS.TradeNetBookDealSVC.qryBookInfo", inputData);//查询宽带移机预约单
		if(IDataUtil.isNotEmpty(bookList2)){
			for(int j=0; j<bookList2.size(); j++){
				tempData = bookList2.getData(j);
				bookData.put("oddNum", tempData.getString("TRADE_ID"));
				String custName=tempData.getString("CUST_NAME");
				if(StringUtils.isNotEmpty(custName)&&custName.length()>1){
					custName=custName.substring(0, 1)+"**";
				}
				bookData.put("custName", custName);
				bookData.put("busiNum", tempData.getString("SERIAL_NUMBER"));
				bookData.put("linkName", "");//表中无联系人字段
				bookData.put("linkPhone", tempData.getString("CONTACT_PHONE",""));
				bookData.put("regionId", tempData.getString("RSRV_STR1"));
				bookData.put("busiType", "3");//其他取3
				resultList.add(bookData);
			}
		}
		
		if(IDataUtil.isEmpty(resultList)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到预约单信息");
		}
		
		IData resultMap = new DataMap();
		IData returnMap = new DataMap();
		
		resultMap.put("result", resultList);
		resultMap.put("respCode", "0");
		resultMap.put("respDesc", "success");
		returnMap.put("rtnCode", "0");
		returnMap.put("rtnMsg", "成功");
		returnMap.put("object", resultMap);
		return returnMap;
	}
	
	
	public IData MoveFeeCalculate(IData params) throws Exception{
		
		IData param = new DataMap(params.toString()).getData("params");
		IData broadBandInfo = param.getData("broadBandInfo");
		
		IDataUtil.chkParam(param, "busiNum");
		IDataUtil.chkParam(param, "provinceId");
		IDataUtil.chkParam(param, "regionId");
		IDataUtil.chkParam(param, "channelId");
		IDataUtil.chkParam(param, "broadBandInfo");
		IDataUtil.chkParam(broadBandInfo, "standardAddressID");
		
		String serialNumber = param.getString("busiNum");
		
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		data.put("OPEN_TYPE", "FTTH");
		data.put("MODEL_MODE", "0");
		data.put("IS_BUSINESS_WIDE", "0");
		data.put("IS_EXCHANGE_MODEL", "1");
		
		IDataset feeList = new DatasetList();
		IDataset expenseItemList = new DatasetList();
		IDataset resultList = new DatasetList();
		IData result = new DataMap();
		IData object = new DataMap();
		try{
			feeList = CSAppCall.call("SS.WideNetMoveIntfSVC.dealModelMoney", data);
		}catch (Exception e) {
			result.put("FLAG", "-1");
			result.put("DESC", e.getMessage());
			resultList.add(result);
			object = BroadBandUtil.requestData(resultList);
		}
		if(IDataUtil.isNotEmpty(feeList)){
			String expenseItemName = "光猫费用";
			String expenseItemMoney = String.valueOf(Integer.parseInt(feeList.getData(0).getString("MODEM_DEPOSIT"))/100);
			IData fee = new DataMap();
			fee.put("expenseItemName", expenseItemName);
			fee.put("expenseItemMoney", expenseItemMoney);
			expenseItemList.add(fee);
			result.put("expenseItemList", expenseItemList);
			resultList.add(result);
			result.put("FLAG", "1");
			object = BroadBandUtil.requestData(resultList);
		}
		return object;
	}
	
	
	//=====================海南宽带新装专区接口=====================================
	/**
	 * 宽带受理校验
	 */
	public IData bussinessValidate_HNKDZQ(IData inputs) throws Exception
    {	
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
	    	//服务号码
	    	IDataUtil.chkParam(input, "busiNum");
	    	//业务类型
	    	IDataUtil.chkParam(input, "busiType");
	    	String serialNumber=input.getString("busiNum");
	    	String busiType=input.getString("busiType");
	    	//宽带新装
	    	if("0".equals(busiType)){
	    		IData params=new DataMap();
	    		UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
	    		String isRealName=ucaData.getCustomer().getIsRealName();
	    		//拼入参
	    		params.put("SERIAL_NUMBER", serialNumber);
	    		params.put("X_CHOICE_TAG", "0");
	    		params.put("TRADE_TYPE_CODE", "600");
	    		params.put("ROUTE_EPARCHY_CODE", "0898");
	    		params.put("EPARCHY_CODE", "0898");
	    		params.put("IS_REAL_NAME", isRealName);
	    		IDataset resultList=CSAppCall.call("SS.CreatePersonIntfSVC.checkBusinessRule", params);
	    		
	    		if(resultList!=null&&resultList.size()>0){
	    			IDataset errorList=resultList.getData(0).getDataset("TIPS_TYPE_ERROR");
	    			if(errorList!=null&&errorList.size()>0){
	    				String errorMsg="";
	    				for(int i=0;i<errorList.size();i++){
	    					errorMsg+=(errorList.getData(i).getString("TIPS_INFO","")+"|");
	    				}
	    				throw new Exception(errorMsg);
	    			}
	    		}
	    	}else{
	    		throw new Exception("对不起，目前不支持该类型");
	    	}
			
		} catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in bussinessValidate_HNKDZQ()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseData(result);
    }
	/**
	 * 查询宽带标准地址
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData queryStandardAddress(IData inputs) throws Exception
    {
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "addressName");
    		IData params=new DataMap();
    		params.put("REGION_SP", input.getString("addressName"));
    		params.put("AREA_CODE", input.getString("areaCode",""));
    		params.put("CURRENT_PAGE", input.getString("currentPage",""));
    		params.put("PAGE_SIZE", input.getString("pageSize",""));

    		IDataset resultList=CSAppCall.call("PB.AddressManageSvc.queryAddressForApp", params);
    		
    		IDataset resourcesInfo=new DatasetList();
    		if(resultList!=null&&resultList.size()>0){
    			IData data=null;
    			for(int i=0;i<resultList.size();i++){
    				data=new DataMap();
    				data.put("deviceID", resultList.getData(i).getString("DEVICE_ID",""));
    				data.put("deviceName", resultList.getData(i).getString("DEVICE_NAME",""));
    				data.put("standardAddressID", resultList.getData(i).getString("REGION_NAME",""));
    				data.put("freePort", resultList.getData(i).getString("PORT_NUM",""));
    				data.put("accessType", resultList.getData(i).getString("OPEN_TYPE",""));
    				data.put("IS1000", resultList.getData(i).getString("IS1000","否"));
    				data.put("areaCode", resultList.getData(i).getString("AREA_CODE",""));
    				data.put("floorRoomNum", resultList.getData(i).getString("GIS8",""));
    				resourcesInfo.add(data);
    			}
    		}
    		result.put("resourcesInfo", resourcesInfo);
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryStandardAddress()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
    }
	/**
	 * 宽带产品信息查询
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData QueryMKPackageInfo_HNKDZQ(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "busiType");
    		IData pubInfo=input.getData("crmpfPubInfo");
    		IData dynamicParams=input.getData("dynamicParams");
    		
    		String IS1000="";
    		String accessType="";
    		if(dynamicParams!=null){
    			IS1000=dynamicParams.getString("IS1000","");
    			accessType=dynamicParams.getString("accessType","");
    		}
    		if("".equals(accessType)){
    			throw new Exception("对不起，accessType为空");
    		}
    		
    		String staffId="";
    		if(pubInfo!=null){
    			staffId=pubInfo.getString("staffId","");
    		}
    		if("".equals(staffId)){
    			throw new Exception("对不起，staffId为空");
    		}
    		
    		if("0".equals(input.getString("busiType"))){
    			IData params=new DataMap();
        		params.put("WIDE_TYPE", accessType);
        		params.put("ROUTE_EPARCHY_CODE", "0898");
        		params.put("TRADE_STAFF_ID", staffId);
        		params.put("isGigabit", IS1000);

        		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.getWidenetProductInfoIntf", params);
        		
        		IDataset packageList=new DatasetList();
        		if(resultList!=null&&resultList.size()>0){
        			IData data=null;
        			for(int i=0;i<resultList.size();i++){
        				data=new DataMap();
        				data.put("packageId", resultList.getData(i).getString("PRODUCT_ID",""));
        				data.put("packageName", resultList.getData(i).getString("PRODUCT_NAME",""));
        				data.put("packageContent", resultList.getData(i).getString("BRAND_CODE",""));
        				data.put("packageDesc", resultList.getData(i).getString("PRODUCT_EXPLAIN",""));
        				data.put("offerRate", "");
        				data.put("giveInfo", "");
        				data.put("packagType", "");
        				data.put("effDate", "");
        				data.put("expDate", "");
        				data.put("groupNum", "");
        				data.put("valueAddedProducts", "");
        				data.put("productId", "");
        				data.put("productName", "");
        				data.put("productBrand", "");
        				packageList.add(data);
        			}
        		}
        		result.put("packageList", packageList);
    		}else{
    			throw new Exception("对不起，目前不支持该类型");
    		}
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryMKPackageInfo__HNKDZQ()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	/**
	 * 查询宽带服务与优惠
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData QueryServiceDiscount(IData inputs) throws Exception
    {
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "packageId");
    		IData params=new DataMap();
    		params.put("NEW_PRODUCT_ID", input.getString("packageId"));
    		params.put("ROUTE_EPARCHY_CODE", "0898");

    		IDataset resultList=CSAppCall.call("CS.SelectedElementSVC.getWidenetUserOpenElements", params);
    		
    		IDataset sAndDList=new DatasetList();
    		if(resultList!=null&&resultList.size()>0){
    			IDataset selectElements=resultList.getData(0).getDataset("SELECTED_ELEMENTS");
    			if(selectElements!=null&&selectElements.size()>0){
	    			IData data=null;
	    			for(int i=0;i<selectElements.size();i++){
	    				data=new DataMap();
	    				data.put("elementType", selectElements.getData(i).getString("ELEMENT_TYPE_CODE",""));
	    				data.put("elementId", selectElements.getData(i).getString("ELEMENT_ID",""));
	    				data.put("elementName", selectElements.getData(i).getString("ELEMENT_NAME",""));
	    				data.put("elementDesc", selectElements.getData(i).getString("ELEMENT_EXPLAIN",""));
	    				data.put("productId", selectElements.getData(i).getString("PRODUCT_ID",""));
	    				data.put("packageId", selectElements.getData(i).getString("PACKAGE_ID",""));
	    				data.put("startDate", selectElements.getData(i).getString("START_DATE",""));
	    				data.put("endDate", selectElements.getData(i).getString("END_DATE",""));
	    				data.put("modifyTag", selectElements.getData(i).getString("MODIFY_TAG",""));
	    				sAndDList.add(data);
	    			}
    			}
    		}
    		result.put("sAndDList", sAndDList);
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryServiceDiscount()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
    }
	/**
	 * 查询宽带产品营销活动
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData QueryActivityInfo_HNKDZQ(IData inputs) throws Exception
	{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "busiType");
    		
    		IData dynamicParams=input.getData("dynamicParams");
    		if(dynamicParams==null){
    			throw new Exception("对不起，dynamicParams为空");
    		}
    		
    		IDataUtil.chkParam(dynamicParams, "productId");
    		IDataUtil.chkParam(dynamicParams, "phoneNum");
    		
    		if("0".equals(input.getString("busiType"))){
	    		IData params=new DataMap();
	    		params.put("PRODUCT_ID", dynamicParams.getString("productId"));
	    		params.put("SERIAL_NUMBER", dynamicParams.getString("phoneNum"));
	    		params.put("ROUTE_EPARCHY_CODE", "0898");
	    		//关于开发家庭终端调测费的需求(在线客服)
	    		params.put("QUERY_TYPE", dynamicParams.getString("queryType",""));
	    		//关于开发家庭终端调测费的需求(在线客服)
	    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.querySaleActivesByProductIdIntf", params);
	    		
	    		IDataset activityList=new DatasetList();
	    		if(resultList!=null&&resultList.size()>0){
	    			IData data=null;
	    			for(int i=0;i<resultList.size();i++){
	    				data=new DataMap();
	    				data.put("activityId", resultList.getData(i).getString("SALE_ACTIVE_ID",""));
	    				data.put("activityName", resultList.getData(i).getString("SALE_ACTIVE_NAME",""));
	    				data.put("activityDesc", resultList.getData(i).getString("SALE_ACTIVE_EXPLAIN",""));
	    				data.put("activityFee", resultList.getData(i).getString("SALE_ACTIVE_FEE",""));
	    				data.put("effDate", "");
	    				data.put("expDate", "");
	    				activityList.add(data);
	    			}
	    		}
	    		result.put("activityList", activityList);
    		}else{
	    		throw new Exception("对不起，目前不支持该类型");
	    	}
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryActivityInfo_HNKDZQ()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	/**
	 * 宽带营销活动校验
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData offerValidate_HNKDZQ(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "busiNum");
    		IDataUtil.chkParam(input, "offerId");
    		String discountIds="";
    		String serviceIds="";
    		String activityId="";
    		
    		IData dynamicParams=input.getData("dynamicParams");
    		if(dynamicParams!=null){
    			discountIds=dynamicParams.getString("discountIds","");
    			serviceIds=dynamicParams.getString("serviceIds","");
    			activityId=dynamicParams.getString("activityId","");
    		}
    		if("".equals(discountIds)){
    			throw new Exception("对不起，discountIds为空");
    		}
    		if("".equals(serviceIds)){
    			throw new Exception("对不起，serviceIds为空");
    		}
    		if("".equals(activityId)){
    			throw new Exception("对不起，activityId为空");
    		}
    		IData params=new DataMap();
    		params.put("WIDE_PRODUCT_ID", input.getString("offerId"));
    		params.put("SERIAL_NUMBER", input.getString("busiNum"));
    		params.put("ROUTE_EPARCHY_CODE", "0898");
    		params.put("DISCNT_IDS", discountIds);
    		params.put("WIDE_USER_SELECTED_SERVICEIDS", serviceIds);
    		params.put("SALE_ACTIVE_ID", activityId);
    		
    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.checkSaleActiveInft", params);
    		if(resultList!=null&&resultList.size()>0){
    			if(!"0".equals(resultList.getData(0).getString("RESULT_CODE"))){
    				throw new Exception("校验失败，应答结果码为："+resultList.getData(0).getString("RESULT_CODE"));
    			}
    		}
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in offerValidate_HNKDZQ()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseData(result);
	}
	/**
	 * 魔百和营销活动校验
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData MBHActivityValidate(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "busiNum");
    		IDataUtil.chkParam(input, "offerId");
    		String activityId="";
    		String MBHActivityId="";
    		String serviceIds="";
    		
    		IData dynamicParams=input.getData("dynamicParams");
    		if(dynamicParams!=null){
    			MBHActivityId=dynamicParams.getString("MBHActivityId","");
    			serviceIds=dynamicParams.getString("serviceIds","");
    			activityId=dynamicParams.getString("activityId","");
    		}
    		if("".equals(serviceIds)){
    			throw new Exception("对不起，serviceIds为空");
    		}
    		if("".equals(MBHActivityId)){
    			throw new Exception("对不起，MBHActivityId为空");
    		}
    		IData params=new DataMap();
    		params.put("WIDE_PRODUCT_ID", input.getString("offerId"));
    		params.put("SERIAL_NUMBER", input.getString("busiNum"));
    		params.put("TOP_SET_BOX_SALE_ACTIVE_ID", MBHActivityId);
    		params.put("WIDE_USER_SELECTED_SERVICEIDS", serviceIds);
    		params.put("SALE_ACTIVE_ID",  activityId);
    		params.put("ROUTE_EPARCHY_CODE", "0898");
    		
    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.checkTopSetBoxSaleActiveInft", params);
    		if(resultList!=null&&resultList.size()>0){
    			if(!"0".equals(resultList.getData(0).getString("RESULT_CODE"))){
    				throw new Exception("校验失败，应答结果码为："+resultList.getData(0).getString("RESULT_CODE"));
    			}
    		}
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in MBHActivityValidate()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseData(result);
	}
	/**
	 * 光猫押金查询校验
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData ModemDepositCheck(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	IData dynamicParams=new DataMap();
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "busiNum");
    		
    		IData params=new DataMap();
    		params.put("SERIAL_NUMBER", input.getString("busiNum"));
    		params.put("SALE_ACTIVE_ID",  input.getString("activityId"));
    		params.put("ROUTE_EPARCHY_CODE", "0898");
    		
    		if(!("0".equals(input.getString("modemStyle")))){
    			dynamicParams.put("modemDeposit", "0");
    		}else{
    		
	    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.getModemDepositInft", params);
	    		if(resultList!=null&&resultList.size()>0){
	    			if(!"0".equals(resultList.getData(0).getString("resultCode"))){
	    				throw new Exception("校验失败，应答结果码为："+resultList.getData(0).getString("resultCode"));
	    			}
	    			dynamicParams.put("modemDeposit", resultList.getData(0).getString("MODEM_DEPOSIT",""));
	    		}
    		
    		}
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in ModemDepositCheck()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", dynamicParams);
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseData(result);
	}
	/**
	 * 提交前费用校验
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData FeeValidate(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "busiNum");
    		String activityFee="0";
    		String MBHActivityFee="0";
    		String modemDeposit="0";
    		String productDeposit="0";
    		//关于开发家庭终端调测费的需求(在线客服)
    		String activityFee2="0";
    		String MBHActivityFee2="0";
    		//关于开发家庭终端调测费的需求(在线客服)
    		IData dynamicParams=input.getData("dynamicParams");
    		if(dynamicParams!=null){
    			activityFee=dynamicParams.getString("activityFee","0");
    			MBHActivityFee=dynamicParams.getString("MBHActivityFee","0");
    			//关于开发家庭终端调测费的需求(在线客服)
    			activityFee2=dynamicParams.getString("activityFee2","0");
    			MBHActivityFee2=dynamicParams.getString("MBHActivityFee2","0");
    			//关于开发家庭终端调测费的需求(在线客服)
    			modemDeposit=dynamicParams.getString("modemDeposit","0");
    			productDeposit=dynamicParams.getString("productDeposit","0");
    		}
    		
    		IData params=new DataMap();
    		params.put("SERIAL_NUMBER", input.getString("busiNum"));
    		params.put("MODEM_DEPOSIT", modemDeposit);
    		params.put("TOPSETBOX_DEPOSIT", productDeposit);
    		params.put("SALE_ACTIVE_FEE",  activityFee);
    		params.put("TOPSETBOX_SALE_ACTIVE_FEE",  MBHActivityFee);
    		//关于开发家庭终端调测费的需求(在线客服)
    		params.put("SALE_ACTIVE_FEE2",  activityFee2);
    		params.put("TOPSETBOX_SALE_ACTIVE_FEE2",  MBHActivityFee2);
    		//关于开发家庭终端调测费的需求(在线客服)
    		params.put("ROUTE_EPARCHY_CODE", "0898");
    		
    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.checkFeeBeforeSubmitInft", params);
    		if(resultList!=null&&resultList.size()>0){
    			if(!"0".equals(resultList.getData(0).getString("X_RESULTCODE"))){
    				throw new Exception("校验失败，应答结果码为："+resultList.getData(0).getString("X_RESULTCODE"));
    			}
    		}
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in FeeValidate()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseData(result);
	}
	/**
	 * 产品组信息查询接口
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData QueryProductGroupDiscount(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "packageId");
    		
    		IData params=new DataMap();
    		params.put("PRODUCT_ID", input.getString("packageId"));

    		IDataset resultList=CSAppCall.call("SS.CreatePersonIntfSVC.queryGroupByProductId", params);
    		
    		IDataset groupList=new DatasetList();
    		if(resultList!=null&&resultList.size()>0){
    			IData data=null;
    			for(int i=0;i<resultList.size();i++){
    				data=new DataMap();
    				data.put("groupId", resultList.getData(i).getString("GROUP_ID",""));
    				data.put("groupName", resultList.getData(i).getString("GROUP_NAME",""));
    				data.put("forceTag", resultList.getData(i).getString("FORCE_TAG",""));
    				data.put("defaultTag", resultList.getData(i).getString("DEFAULT_TAG",""));
    				data.put("PACKAGE_TYPE_CODE", resultList.getData(i).getString("PACKAGE_TYPE_CODE",""));
    				data.put("MIN_NUMBER", resultList.getData(i).getString("MIN_NUMBER",""));
    				data.put("MAX_NUMBE", resultList.getData(i).getString("MAX_NUMBE",""));
    				data.put("PRODUCT_ID", resultList.getData(i).getString("PRODUCT_ID",""));
    				data.put("SELECT_FLAG", resultList.getData(i).getString("SELECT_FLAG",""));
    				groupList.add(data);
    			}
    		}
    		result.put("groupList", groupList);
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryProductGroupDiscount()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	/**
	 * 根据分组查询优惠接口
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData QueryDiscount(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "groupId");
    		
    		IData params=new DataMap();
    		params.put("GROUP_ID", input.getString("groupId"));
    		params.put("QRY_TAG", input.getString("distinction","1"));

    		IDataset resultList=CSAppCall.call("SS.CreatePersonIntfSVC.queryOfferByGroupId", params);
    		
    		IDataset discountList=new DatasetList();
    		if(resultList!=null&&resultList.size()>0){
    			IData data=null;
    			for(int i=0;i<resultList.size();i++){
    				data=new DataMap();
    				data.put("offerId", resultList.getData(i).getString("OFFER_ID",""));
    				data.put("offerName", resultList.getData(i).getString("OFFER_NAME",""));
    				data.put("offerType", resultList.getData(i).getString("OFFER_TYPE",""));
    				data.put("offerCode", resultList.getData(i).getString("OFFER_CODE",""));
    				data.put("discription", resultList.getData(i).getString("DESCRIPTION",""));
    				data.put("selectFlag", resultList.getData(i).getString("SELECT_FLAG",""));
    				discountList.add(data);
    			}
    		}
    		result.put("discountList", discountList);
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryDiscount()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	/**
	 * 查询地市区县信息
	 */
	public IData queryCityArea(IData inputs) throws Exception
    {	
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
	    	IDataUtil.chkParam(input, "addressLevel");
	    	IDataUtil.chkParam(input, "parentAddressId");
	    	
    		IData params=new DataMap();
    		//拼入参
    		params.put("ADDR_LEVEL", input.get("addressLevel"));
    		params.put("PARENT_ADDR_ID", input.get("parentAddressId"));
    		IDataset resultList=CSAppCall.call("PB.AddressManageSvc.queryAddressNameLevelOne", params);
    		
    		IDataset addressInfo=new DatasetList();
    		if(resultList!=null&&resultList.size()>0){
    			IData data=null;
    			for(int i=0;i<resultList.size();i++){
    				data=new DataMap();
    				data.put("addressId", resultList.getData(i).getString("ADDR_ID",""));
    				data.put("addressName", resultList.getData(i).getString("ADDR_NAME",""));
    				addressInfo.add(data);
    			}
    		}
    		result.put("addressInfo", addressInfo);
			
		} catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in queryCityArea()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
    }
	
	
	
	
	/**
	 * 专区：订单进度查询
	 * @return
	 * @throws Exception
	 * xuzh5
	 * 2018-10-9 14:25:37
	 */
	public IData orderProgressRateSearch (IData param) throws Exception{
		IData input = new DataMap(param.toString()).getData("params");		
		IData currentInfo=new DataMap();
		IData nodeInfo=new DataMap();
    	String flag="1";
    	String desc="success";
    	try 
   	 	{
    	if(IDataUtil.isEmpty(input))
    		throw new Exception("参数params的值不能为空！");
    	
	    if(input.getString("crmNum")==null && "".equals(input.getString("crmNum")))
	    	throw new Exception("参数crmNum不能为空！");
	    
    	String state="O";//S竣工, C 撤单 ,O在途
    	IDataset wfSet = UTradeInfoQry.qryTradeHisInfo(input.getString("crmNum"), Route.CONN_CRM_CG);
    	for(int i=0 ;wfSet.size()>i;i++){
    		
    		if("3".equals(wfSet.getData(i).getString("CANCEL_TAG"))||"1".equals(wfSet.getData(i).getString("CANCEL_TAG"))){
    			state="C";
    			currentInfo.put("stateName", "撤单");//状态名称
    			currentInfo.put("state", state);//S竣工, C 撤单 ,O在途
    			currentInfo.put("remark", wfSet.getData(i).getString("REMARK",""));//撤单原因
    		}else{
    			state="S";
    			currentInfo.put("stateName", "竣工");//状态名称
    			currentInfo.put("state", state);//S竣工, C 撤单 ,O在途
    			currentInfo.put("remark", "");//撤单原因
    			}
    		currentInfo.put("srvCode",wfSet.getData(i).getString("SERIAL_NUMBER"));//宽带账号
    		currentInfo.put("busiCode", wfSet.getData(i).getString("SERIAL_NUMBER").substring(3));
    		currentInfo.put("operateID", wfSet.getData(i).getString("TRADE_STAFF_ID"));
    		currentInfo.put("operateName", wfSet.getData(i).getString("TRADE_STAFF_ID"));
    		currentInfo.put("createTime", wfSet.getData(i).getString("ACCEPT_DATE"));
    		currentInfo.put("updateTime", wfSet.getData(i).getString("UPDATE_TIME"));
    		String tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", wfSet.getData(i).getString("TRADE_TYPE_CODE"));
			currentInfo.put("busiName", tradeTypeName);
    		}
    		
    	if("O".equals(state)){
    			IDataset tradeinfo=UTradeInfoQry.qryTradebyTradeid(input.getString("crmNum"), Route.CONN_CRM_CG);
    			if(IDataUtil.isNotEmpty(tradeinfo)){
        		currentInfo.put("srvCode",tradeinfo.getData(0).getString("SERIAL_NUMBER"));//宽带账号
        		currentInfo.put("busiCode", tradeinfo.getData(0).getString("SERIAL_NUMBER").substring(3));
        		currentInfo.put("operateID", tradeinfo.getData(0).getString("TRADE_STAFF_ID"));
        		currentInfo.put("operateName", tradeinfo.getData(0).getString("TRADE_STAFF_ID"));
        		currentInfo.put("createTime", tradeinfo.getData(0).getString("ACCEPT_DATE"));
        		currentInfo.put("updateTime", tradeinfo.getData(0).getString("UPDATE_TIME"));
        		currentInfo.put("stateName", "在途");//状态名称
        		currentInfo.put("state", state);//S竣工, C 撤单 ,O在途
    			String tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeinfo.getData(0).getString("TRADE_TYPE_CODE"));
    			currentInfo.put("busiName", tradeTypeName);
    			currentInfo.put("remark", "");//撤单原因  这都敢提出来 醉了！
    			
    			//长流节点信息
    			IData inParam=new DataMap();
    			inParam.put("SUBSCRIBE_ID", tradeinfo.getData(0).getString("TRADE_ID"));
    			IDataOutput nodeOutData=CSAppCall.callNGPf("orderStatusQuery", inParam);//调用服开查询工单节点信息接口
    			 IDataset noderesult = nodeOutData.getData();
    			 System.out.println(noderesult.toString());
    			 if(IDataUtil.isNotEmpty(noderesult)){
    				 IData nodeData=noderesult.getData(0);
    				 
    				 IDataset nodeset=nodeData.getDataset("data");
    				 if(nodeset.size()>0){
    				 IData info=nodeset.getData(nodeset.size()-1);
    				 nodeInfo.put("linkName", info.getString("operationDesc")); //环节名称   例如：网络激活、上门施工中等
    				 nodeInfo.put("crtTime", info.getString("operTime"));
    				 String enddate=SysDateMgr.getAddHoursDate(info.getString("operTime","")==""? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()):info.getString("operTime"), 48);
    				 nodeInfo.put("surplusDuration", enddate);
    				 
    				 Date endDate=SysDateMgr.string2Date(enddate, "yyyy-MM-dd HH:mm:ss");
    				 boolean flg=endDate.after(new Date());
    				 nodeInfo.put("tlmtStsCd", flg==true?"未超时":"超时");										//本环节时限状态
    				 nodeInfo.put("actualHandleDuration", info.getString("operTime"));
    				 nodeInfo.put("linkJudge", "");
    				 nodeInfo.put("dspsMobilePhone", info.getString("operUserTel"));
    				 nodeInfo.put("dspsOpinCntt", "");														//环节评价
    				 nodeInfo.put("opTypeNm", tradeinfo.getData(0).getString("TRADE_TYPE_CODE"));
    				 nodeInfo.put("dspsStaffId", tradeinfo.getData(0).getString("TRADE_STAFF_ID"));
    				 nodeInfo.put("dspsWorkGrpId", tradeinfo.getData(0).getString("TRADE_DEPART_ID"));		//处理人组织
    				 nodeInfo.put("dspsOrgBrnchId", "");
    				 
    				 }
    				 
    			 }

    			
    			
    			
    			}		
    	}
    	if(IDataUtil.isEmpty(currentInfo))
    		throw new Exception("该工单没有对应的数据信息！");
    	}catch(Exception e){
    		//e.printStackTrace();
    		log.error(e);
    		flag="0";
    		desc=e.getMessage();
		}
        IDataset linkList=new DatasetList();
        IData result=new DataMap();
        linkList.add(nodeInfo);
        
        if(currentInfo.size()>0)
        	result.put("currentStateInfo", currentInfo);
        if(nodeInfo.size()>0)
        	result.put("linkList", linkList);
        
		
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
    	
        return BroadBandUtilHNKDZQ.responseQryData(result);
		
	}
	
	
	
	/**
	 * 互联网电视子账号查询
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData QueryInternetTVListInfo(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try {
    		
    		
    		if(IDataUtil.isEmpty(input))
        		throw new Exception("参数params的值不能为空！");
    	    if(input.getString("busiCode") == null || "".equals(input.getString("busiCode")))
    	    	throw new Exception("参数busiCode不能为空！");
    	    if(input.getString("type") == null || "".equals(input.getString("type")))
    	    	throw new Exception("参数type不能为空！");
    	    
    	    String busiCode = input.getString("busiCode");//业务号码
    	    String type = input.getString("type");//类型   0 手机号码   1 宽带账号  2 身份证号码
    	    String serialNumber = "";//手机号码
    	    String userId = "";
    	    
    	    
    	    String interTvAccount = "";//互联网电视子账号
    	    String statusName = "";//状态名称
    	    String stbSerilaNum = "";//机顶盒串号
    	    String packagName = "";//套餐名称
    	    String intTvType = "";//互联网电视类型
    	    String tvRunTime = "";//开通时间
    	    
    	    
    	    //获取userId
    	    if("0".equals(type)){//busiCode 为手机号码
    	    	serialNumber = busiCode;
    	    	IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumber);
    	    	if(IDataUtil.isNotEmpty(userInfos)){
    	    		userId = userInfos.getData(0).getString("USER_ID");
    	    	}
    	    }else if("1".equals(type)){//busiCode 为宽带账号
    	    	if(busiCode.startsWith("KD_")){
    	    		serialNumber = busiCode.substring(3);
            	}
	    		IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumber);
	    		if(IDataUtil.isNotEmpty(userInfos)){
    	    		userId = userInfos.getData(0).getString("USER_ID");
    	    	}
    	    }else if("2".equals(type)){//busiCode 为身份证号码
    	    	
    	    	IDataset custInfos = CustomerInfoQry.getCustIdByPspt(busiCode);//根据身份证找到客户信息
    	    	 if(IDataUtil.isNotEmpty(custInfos)){
    	    		for (int i = 0; i < custInfos.size(); i++) {
    	    			IData custInfo = custInfos.getData(i);
    	    			String custId = custInfo.getString("CUST_ID");
    	    			IDataset userCustInfos = UserInfoQry.getAllUserInfoByCustId(custId);//根据CUST_ID找到所有的用户信息
     	    			if(IDataUtil.isNotEmpty(userCustInfos)){
     	    				for(int j = 0;j<userCustInfos.size();j++){
     	    					IData userInfo = userCustInfos.getData(j);
     	    					if(userInfo.getString("REMOVE_TAG").equals("0") && userInfo.getString("SERIAL_NUMBER").startsWith("KD_")){//判断是否有宽带号码
     	    						serialNumber = userInfo.getString("SERIAL_NUMBER");
     	    						serialNumber = serialNumber.substring(3);

     	    						IDataset userInfos= UserInfoQry.getAllUserInfoBySn(serialNumber);
     	    			    		if(IDataUtil.isNotEmpty(userInfos)){
     	    		    	    		userId = userInfos.getData(0).getString("USER_ID");
     	    		    	    		break;
     	    		    	    	}
     	    					}
     	    				}
     	    			}
					}
    	    	}
    	    }
    	    
    	    
    	    if(StringUtils.isNotEmpty(userId)){
    	    	interTvAccount = serialNumber;
    	    	boolean internetTvFlag = false;//办理互联网电视标识    	    	
        			IDataset platsvcInfos = UserPlatSvcInfoQry.queryPlatSvcByUserIdNew(userId);//查询平台业务
        			if(IDataUtil.isNotEmpty(platsvcInfos)){
        				for (int i = 0; i < platsvcInfos.size(); i++) {
        					String serviceId = platsvcInfos.getData(i).getString("SERVICE_ID");
        					if("40227762".equals(serviceId) || "80025539".equals(serviceId) ){//该用户有办理魔百和服务
        						internetTvFlag = true;
	            				//数据表状态 A-正常，N-暂停，E-退订，L-挂失
	            				String bizStateCode = platsvcInfos.getData(i).getString("BIZ_STATE_CODE");
	            				tvRunTime = platsvcInfos.getData(i).getString("START_DATE");//开通时间 
	            				if("A".equals(bizStateCode)){
	            					statusName = "正常";
	            				}else if("N".equals(bizStateCode)){
	            					statusName = "暂停";
	            				}else if("E".equals(bizStateCode)){
	            					statusName = "退订";
	            				}else if("L".equals(bizStateCode)){
	            					statusName = "挂失";
	            				}
	            				
	            				//获取套餐名称
    	                		IDataset upcDatas = UpcCall.querySpComprehensiveInfoByServiceId(serviceId);
    	                		if(IDataUtil.isNotEmpty(upcDatas)){
    	                			packagName = upcDatas.getData(0).getString("OFFER_NAME");
    	                		}
	            				
	            				//获取资源信息
	                			IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
	                			if(IDataUtil.isNotEmpty(boxInfos)){
    	                			stbSerilaNum = boxInfos.getData(0).getString("IMSI");//机顶盒串号
	                    			String boxProductId = boxInfos.getData(0).getString("RSRV_STR1");
	                    		    if(boxProductId!=null&&!boxProductId.trim().equals("")){
	                    		    	intTvType = UProductInfoQry.getProductNameByProductId(boxProductId);//查询产品的名称
	                    		    }
	                				
	                			}
	                			
	                			
	                			result.put("interTvAccount", interTvAccount);//互联网电视子账号
	                	    	result.put("statusName", statusName);//状态名称
	                	    	result.put("stbSerilaNum", stbSerilaNum);//机顶盒串号
	                	    	result.put("packagName", packagName);//套餐名称
	                	    	result.put("intTvType", intTvType);//互联网电视类型
	                	    	result.put("tvRunTime", tvRunTime);//开通时间
	            				break;
        					}
						}
        				
        			}
    	    	
    	    	if(!internetTvFlag){
    	    		flag = "0";
        			desc = "该业务号码未开通互联网电视业务";
    	    	}
    	    	
    	    }else{
    	    	flag = "0";
    			desc = "不存在有效的用户信息，请确认业务号码是否正确";
    	    }
    	    
    	}catch (Exception e) {
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryInternetTVListInfo()==", e);
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	
	
	
	/**
	 * 互联网电视基本信息查询接口
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData QueryBaseInfoInternetTV(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try {
    		
    		if(IDataUtil.isEmpty(input))
        		throw new Exception("参数params的值不能为空！");
    	    if(input.getString("busiCode") == null || "".equals(input.getString("busiCode")))
    	    	throw new Exception("参数busiCode不能为空！");
    	    if(input.getString("type") == null || "".equals(input.getString("type")))
    	    	throw new Exception("参数type不能为空！");
    	    
    	    String busiCode = input.getString("busiCode");//业务号码
    	    String type = input.getString("type");//类型   0 手机号码   1 宽带账号  2 机顶盒串号
    	    
    	    
    	    String intTvCreateTime = "";//互联网电视开户时间
    	    String intTvFinishTime = "";//互联网电视竣工时间
    	    String operId = "";//开户操作工号
    	    String orgId = "";//开户营业厅
    	    String intTvBrand = "";//互联网电视牌照方
    	    String packageCode = "";//互联网电视套餐编码
    	    String packagName = "";//套餐名称
    	    String openDate = "";//套餐开通时间
    	    String effDate = "";//套餐生效时间
    	    String expDate = "";//套餐失效时间
    	    String listNumberSub = "";//号码所属名单
    	    String listEntryTime = "";//名单移入时间
    	    String serialNumber = "";//手机号码
    	    String userId = "";
    	    
    	  //获取userId
    	    if("0".equals(type)){//busiCode 为手机号码
    	    	serialNumber = busiCode;
    	    	IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumber);
    	    	if(IDataUtil.isNotEmpty(userInfos)){
    	    		userId = userInfos.getData(0).getString("USER_ID");
    	    	}
    	    }else if("1".equals(type)){//busiCode 为宽带账号
    	    	if(busiCode.startsWith("KD_")){
    	    		serialNumber = busiCode.substring(3);
            	}
	    		IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumber);
	    		if(IDataUtil.isNotEmpty(userInfos)){
    	    		userId = userInfos.getData(0).getString("USER_ID");
    	    	}
	    		
    	    }else if("2".equals(type)){//busiCode 为机顶盒串号
    	    	IDataset imsiInfos  = UserResInfoQry.queryUserIMEI(busiCode);
    			if (IDataUtil.isNotEmpty(imsiInfos)) {
    				userId = imsiInfos.getData(0).getString("USER_ID");
    			}
    	    	
    	    }
    	    if(StringUtils.isNotEmpty(userId)){
    	    	
    	    	boolean internetTvFlag = false;//办理互联网电视标识    	    	
    			IDataset platsvcInfos = UserPlatSvcInfoQry.queryPlatSvcByUserIdNew(userId);//查询平台业务
    			if(IDataUtil.isNotEmpty(platsvcInfos)){
    				for (int i = 0; i < platsvcInfos.size(); i++) {
    					String serviceId = platsvcInfos.getData(i).getString("SERVICE_ID");
    					if("40227762".equals(serviceId) || "80025539".equals(serviceId) ){//该用户有办理魔百和服务
    						
    						internetTvFlag = true;
    						packageCode = serviceId;
            				
            				//获取套餐名称
	                		IDataset upcDatas = UpcCall.querySpComprehensiveInfoByServiceId(serviceId);
	                		if(IDataUtil.isNotEmpty(upcDatas)){
	                			packagName = upcDatas.getData(0).getString("OFFER_NAME");
	                		}
            				
	                		intTvCreateTime  = platsvcInfos.getData(i).getString("START_DATE");
	                		intTvFinishTime  = platsvcInfos.getData(i).getString("START_DATE");
	                		openDate  = platsvcInfos.getData(i).getString("START_DATE");
	                		effDate = platsvcInfos.getData(i).getString("START_DATE");
	                		expDate = platsvcInfos.getData(i).getString("END_DATE");
	                		
	                		
	                		operId = platsvcInfos.getData(i).getString("UPDATE_STAFF_ID");
	                		orgId = platsvcInfos.getData(i).getString("UPDATE_DEPART_ID");
	                		
	                		
	                		//获取资源信息
                			IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
                			if(IDataUtil.isNotEmpty(boxInfos)){
                    			String boxProductId = boxInfos.getData(0).getString("RSRV_STR1");
                    		    if(boxProductId!=null&&!boxProductId.trim().equals("")){
                    		    	intTvBrand = UProductInfoQry.getProductNameByProductId(boxProductId);//查询产品的名称
                    		    }
                				
                			}
            				
                			
                			
	                		result.put("intTvCreateTime", intTvCreateTime);//互联网电视开户时间
	        	    	    result.put("intTvFinishTime", intTvFinishTime);//互联网电视竣工时间
	        	    	    result.put("operId", operId);//开户操作工号
	        	    	    result.put("orgId", orgId);//开户营业厅
	        	    	    result.put("intTvBrand", intTvBrand);//互联网电视牌照方
	        	    	    result.put("packageCode", packageCode);//互联网电视套餐编码
	        	    	    result.put("packagName", packagName);//套餐名称
	        	    	    result.put("openDate", openDate);//套餐开通时间
	        	    	    result.put("effDate", effDate);//套餐生效时间
	        	    	    result.put("expDate", expDate);//套餐失效时间
	        	    	    result.put("listNumberSub", listNumberSub);//号码所属名单
	        	    	    result.put("listEntryTime", listEntryTime);//名单移入时间
                	    	
            				break;
    					}
					}
    				
    			}
	            			
					
    	    	
    	    	if(!internetTvFlag){
    	    		flag = "0";
        			desc = "该业务号码未开通互联网电视业务";
    	    	}
    	    }else{
    	    	flag = "0";
    			desc = "不存在有效的用户信息，请确认业务号码是否正确";
    	    }
    	    
    	    
    	}catch (Exception e) {
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryBaseInfoInternetTV()==", e);
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	
	
	
	/**
	 * 互联网电视用户状态查询接口
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData QueryInternetTVState(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try {
    		if(IDataUtil.isEmpty(input))
        		throw new Exception("参数params的值不能为空！");
    	    if(input.getString("tvAccount") == null || "".equals(input.getString("tvAccount")))
    	    	throw new Exception("参数tvAccount不能为空！");
    	    String tvAccount = input.getString("tvAccount");//业务号码
    	    
    	    
    	    String status = "";//状态编码
    	    String statusName = "";//状态名称
    	    String serialNumber = "";//手机号码
    	    String userId = "";
    	    
    	    
    	    serialNumber = tvAccount;
    	    IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumber);
    	    if(IDataUtil.isNotEmpty(userInfos)){
    	    	userId = userInfos.getData(0).getString("USER_ID");
    	    }
    	    
    	    if(StringUtils.isNotEmpty(userId)){
    	    	
    	    	boolean internetTvFlag = false;//办理互联网电视标识    	    	
    			IDataset platsvcInfos = UserPlatSvcInfoQry.queryPlatSvcByUserIdNew(userId);//查询平台业务
        			
    			if(IDataUtil.isNotEmpty(platsvcInfos)){
    				for (int i = 0; i < platsvcInfos.size(); i++) {
    					String serviceId = platsvcInfos.getData(i).getString("SERVICE_ID");
    					if("40227762".equals(serviceId) || "80025539".equals(serviceId) ){//该用户有办理魔百和服务
    						internetTvFlag = true;
    						
    						//数据表状态 A-正常，N-暂停，E-退订，L-挂失
    						//接口状态 0：正常 1：暂停 2：欠费 3：停机 4：注销 5：到期  -9999：不存在
            				String bizStateCode = platsvcInfos.getData(i).getString("BIZ_STATE_CODE");
            				if("A".equals(bizStateCode)){
            					status = "0";
            					statusName = "正常";
            				}else if("N".equals(bizStateCode)){
            					status = "1";
            					statusName = "暂停";
            				}else if("E".equals(bizStateCode)){
            					status = "5";
            					statusName = "到期";
            				}else if("L".equals(bizStateCode)){
            					status = "1";
            					statusName = "暂停";
            				}
            				break;
    					}
					}
    			}
	            			
    	    	if(!internetTvFlag){
    	    		status = "-9999";
					statusName = "不存在";
    	    	}
    	    	
    	    	result.put("status", status);//状态编码
	    	    result.put("statusName", statusName);//状态名称
    	    }else{
    	    	flag = "0";
    			desc = "不存在有效的用户信息，请确认业务号码是否正确";
    	    }
    	    
    	    
    	}catch (Exception e) {
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryInternetTVState()==", e);
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	
	
	
	
	/**
	 * 互联网电视订购记录信息查询接口
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData QueryInternetTVOrderInfo(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try {
    		
    		if(IDataUtil.isEmpty(input))
        		throw new Exception("参数params的值不能为空！");
    	    if(input.getString("busiCode") == null || "".equals(input.getString("busiCode")))
    	    	throw new Exception("参数busiCode不能为空！");
    	    if(input.getString("type") == null || "".equals(input.getString("type")))
    	    	throw new Exception("参数type不能为空！");
    	    if(input.getString("startTime") == null || "".equals(input.getString("startTime")))
    	    	throw new Exception("参数startTime不能为空！");
    	    if(input.getString("endTime") == null || "".equals(input.getString("endTime")))
    	    	throw new Exception("参数endTime不能为空！");
    	    if(!checkDateFormat(input.getString("startTime")))
    	    	throw new Exception("startTime时间格式不正确！");
    	    if(!checkDateFormat(input.getString("endTime")))
    	    	throw new Exception("endTime时间格式不正确！");
    	    
    	    String busiCode = input.getString("busiCode");//业务号码
    	    String type = input.getString("type");//类型   0 互联网电视子账号  1 手机号码
    	    String startTime = input.getString("startTime");//开始时间
    	    String endTime = input.getString("endTime");//结束时间

    	    
    	    IDataset orderPackages = new DatasetList();
    	    String serialNumber = "";//手机号码
    	    String userId = "";
    	    
    	    
    	  //获取userId
    	    if("0".equals(type) || "1".equals(type) ){
    	    	serialNumber = busiCode;
    	    	IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumber);
    	    	if(IDataUtil.isNotEmpty(userInfos)){
    	    		userId = userInfos.getData(0).getString("USER_ID");
    	    	}
    	    }
    	    
    	    if(StringUtils.isNotEmpty(userId)){
    	    	//根据时间获取该用户的所有的平台服务
    	    	IDataset platSvcInfos = UserPlatSvcInfoQry.queryPlatSvcByUpdateTime(userId,startTime,endTime);
    	    	if(IDataUtil.isNotEmpty(platSvcInfos)){
    	    		
    	    		for (int i = 0; i < platSvcInfos.size(); i++) {
    	    			IData platSvcInfo = platSvcInfos.getData(i);
    	    			String serviceId = platSvcInfo.getString("SERVICE_ID");//服务编码
    	    			String bizStateCode = platSvcInfo.getString("BIZ_STATE_CODE");//状态标识
    	    			String instId = platSvcInfo.getString("INST_ID");
    	    			
    	    			IData orderPackage = new DataMap();
    	    			String orderId = "";//订单号
    	    			String orderPackageCode = "";//订购包编码
    	    			String orderPackageName = "";//订购包名称
    	    			String orderStartTime = "";//订购日期
    	    			String orderEndTime = "";//退订日期
    	    			String state = "";//订购状态
    	    			String businessType = "";//业务类型
    	    			String payMethod = "";//支付方式
    	    			String operChannel = "";//订购渠道
    	    			String operId = "";//操作员工号
    	    			String feeContent = "";//订购包套餐详情
    	    			String remark = "";//备注
    	    			String money = "";//扣费金额
    	    			String freqUseAfterOrder = "";//订购后使用次数
    	    			String totalUseAfterOrder = "";//订购后使用总时长
    	    			String freqUseCurMonth = "";//当月使用次数
    	    			String totalUseCurMonth = "";//当月使用总时长
    	    			
    	    			if(StringUtils.isNotEmpty(serviceId)){
    	    				//查询产商品库业务类型
    	    				IDataset spInfos = UpcCall.querySpServiceAndProdByCond(null, null, null, serviceId);
    	    				if(IDataUtil.isNotEmpty(spInfos)){
    	    					String bizTypeCode = spInfos.getData(0).getString("BIZ_TYPE_CODE");//业务类型
    	    					
    	    					//判断用户是否办理魔百和业务
    	    					if("51".equals(bizTypeCode)|| "86".equals(bizTypeCode)){//51:魔百和流媒体业务  86:魔百和IPTV业务
    	    						
    	    						businessType = "包月";
        	                		payMethod = "话费抵扣";
        	                		operChannel = "营业厅";
    	    						
    	    						
    	    						//根据instId去找platSvc台账
    	    						IDataset tradePlatSvcInfos = UserPlatSvcInfoQry.queryTradePlatSvcByInstId(userId,instId,serviceId,bizStateCode);
    	    						if(IDataUtil.isNotEmpty(tradePlatSvcInfos)){
    	    							orderId = tradePlatSvcInfos.getData(0).getString("TRADE_ID");
    	    						}
    	    						orderPackageCode = serviceId;
    	    						
    	    						//获取套餐名称
        	                		IDataset upcDatas = UpcCall.querySpComprehensiveInfoByServiceId(serviceId);
        	                		if(IDataUtil.isNotEmpty(upcDatas)){
        	                			orderPackageName = upcDatas.getData(0).getString("OFFER_NAME");
        	                			money = upcDatas.getData(0).getString("PRICE");
        	                		}
        	                		
        	                		feeContent = UPlatSvcInfoQry.getSvcExplainBySvcId(serviceId);
    	            				
        	                		orderStartTime = platSvcInfo.getString("START_DATE");
        	                		orderEndTime = platSvcInfo.getString("END_DATE");
        	                		operId =  platSvcInfo.getString("UPDATE_STAFF_ID");
        	                		
        	                		if("A".equals(bizStateCode)){
        	                			state = "正常";
    	            				}else if("N".equals(bizStateCode)){
    	            					state = "暂停";
    	            				}else if("E".equals(bizStateCode)){
    	            					state = "退订";
    	            				}else if("L".equals(bizStateCode)){
    	            					state = "挂失";
    	            				}
        	                		
    	    						
        	    	    			orderPackage.put("orderId", orderId);//订单号
        	    	    			orderPackage.put("orderPackageCode", orderPackageCode);//订购包编码
        	    	    			orderPackage.put("orderPackageName", orderPackageName);//订购包名称
        	    	    			orderPackage.put("orderStartTime", orderStartTime);//订购日期
        	    	    			orderPackage.put("orderEndTime", orderEndTime);//退订日期
        	    	    			orderPackage.put("state", state);//订购状态
        	    	    			orderPackage.put("businessType", businessType);//业务类型
        	    	    			orderPackage.put("payMethod", payMethod);//支付方式
        	    	    			orderPackage.put("operChannel", operChannel);//订购渠道
        	    	    			orderPackage.put("operId", operId);//操作员工号
        	    	    			orderPackage.put("feeContent", feeContent);//订购包套餐详情
        	    	    			orderPackage.put("remark", remark);//备注
        	    	    			orderPackage.put("money", money);//扣费金额
        	    	    			orderPackage.put("freqUseAfterOrder", freqUseAfterOrder);//订购后使用次数
        	    	    			orderPackage.put("totalUseAfterOrder", totalUseAfterOrder);//订购后使用总时长
        	    	    			orderPackage.put("freqUseCurMonth", freqUseCurMonth);//当月使用次数
        	    	    			orderPackage.put("totalUseCurMonth", totalUseCurMonth);//当月使用总时长
        	    	    			orderPackage.put("dynamicParams", "");
    	    						
    	    						orderPackages.add(orderPackage);
    	    						
    	    					}else{
    	    						continue;
    	    					}
    	    					
    	    					
    	    				}else{
    	    					continue;
    	    				}
    	    				
    	    			}else{
    	    				continue;
    	    			}
					}
    	    		
    	    		result.put("orderPackages", orderPackages);
    	    	}else{
    	    		orderPackages = new DatasetList();
    	    		result.put("orderPackages", orderPackages);
    	    	}
    	    	
    	    }else{
    	    	flag = "0";
    			desc = "不存在有效的用户信息，请确认业务号码是否正确";
    	    }
    	    
    	    
    	}catch (Exception e) {
			flag = "0";
			desc = e.getMessage();
			log.error("==error in QueryInternetTVOrderInfo()==", e);
		}
		
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	
	
	/**
	 * 
	 * 校验时间格式是否正确
	 * @param dateStr
	 * @return
	 */
	private boolean checkDateFormat(String dateStr){
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = (Date)df.parse(dateStr);
			return dateStr.equals(df.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	
	/**
	 * 没有值参数需要返回空
	 * @param paramType
	 * @throws Exception
	 * xuzh5
	 * 2018-10-15 11:03:32
	 */
	public void queryVAProductPutMethod(IData paramType) throws Exception{
		paramType.put("productType", "");
		paramType.put("productId", "");
		paramType.put("productName", "");
		paramType.put("productBrand", "");
		paramType.put("productNum", "");
		paramType.put("packageCode", "");
		paramType.put("packageName", "");
		paramType.put("packageDesc", "");
		paramType.put("activityCode", "");
		paramType.put("activityName", "");
		paramType.put("activityDesc", "");
		paramType.put("effDate", "");
		paramType.put("expDate", "");
	}
	
}








