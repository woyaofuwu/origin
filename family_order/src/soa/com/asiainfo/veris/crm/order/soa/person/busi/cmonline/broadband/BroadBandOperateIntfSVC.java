
package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.broadband;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.filter.MergeWideUserCreateIntfFilter;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveBean;
import com.asiainfo.veris.crm.order.soa.person.busi.intf.PersonCallResIntfSVC;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 中移在线宽带业务操作接口方法类
 * @author Administrator
 */
public class BroadBandOperateIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    private static final transient Logger log = Logger.getLogger(BroadBandOperateIntfSVC.class);
    
    /**
     * 办理费用计算(接口)
     * @author zhengkai5
     * @throws Exception 
	 * @throws Exception 
     * */
    public IData AcceptFeeCalculate(IData params) throws Exception
    {
    	if(true){
    		return AcceptFeeCalculate_HNKDZQ(params);
    	}
    	IData param = new DataMap(params.toString()).getData("params");
    	
    	IDataset expenseItemList = new DatasetList();
    	
    	IDataset resultlist = new DatasetList();
		IData result =  new DataMap();
    	
//    	String expenseItemName = "";  //费用项    0:宽带新装   1：续约    2：魔百和    3：和目     4：IMS固话       
    	String expenseItemMoney = ""; //费用项金额
//    	boolean flag = true;
    	try {
			String sn = IDataUtil.chkParam(param, "busiNum"); // 受理号码
			String busiType = IDataUtil.chkParam(param, "busiType"); // 业务类型   0：宽带新装；1：宽带续约；2：增值产品办理；
			
			//判断无手机or有手机宽带用户
	    	IData wideNetData = WideNetUtil.getWideNetTypeInfo(sn);
	    	
	    	//无手机宽带不能受理增值产品
	    	if ("Y".equals(wideNetData.getString("IS_NOPHONE_WIDENET"))) 
	    	{
	    		if ("2".equals(param.getString("busiType"))) 
	    		{
	    			CSAppException.appError("-1", "无手机宽带不能受理增值产品,故没有增值费用!");
				}
			}
	    	
	    	//宽带新装费用
	    	if ("0".equals(busiType)) 
	    	{
	    		
	    		IData broadBandInfo = param.getData("broadBandInfo");
	    		
	    		if (IDataUtil.isEmpty(broadBandInfo))
	    		{
	    			CSAppException.appError("-1", "入参【broadBandInfo】不能为空！");
				}
	    		
				IData widenetFee = new DataMap();
				String  speedProductId = IDataUtil.chkParam(broadBandInfo, "packageId");
				//expenseItemName = "0";
					
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

        			if (IDataUtil.isEmpty(monthFeeDatas))
        			{
						widenetFee.put("expenseItemName", "套餐月基础费用");
						widenetFee.put("busiType", busiType);
						widenetFee.put("expenseItemMoney", "0");
						expenseItemMoney = "0";
        			}else{

						widenetFee.put("expenseItemName", "套餐月基础费用");
						widenetFee.put("busiType", busiType);
						widenetFee.put("expenseItemMoney", monthFeeDatas.getData(0).getString("PARA_CODE1"));
						expenseItemMoney = monthFeeDatas.getData(0).getString("PARA_CODE1");
					}

                }
                else
                {
                	CSAppException.appError("-1", " 没有配置该["+speedProductId+"]宽带产品，请联系管理员！");
                }	
				
				expenseItemList.add(widenetFee);
			}
	    	else if ("1".equals(busiType))  //宽带续约费用
	    	{
	    		
	    		IData broadBandInfo = param.getData("broadBandInfo");

	    		IData widenetFee = new DataMap();

	    		if (IDataUtil.isEmpty(broadBandInfo))
	    		{
	    			CSAppException.appError("-1", "入参【broadBandInfo】不能为空！");
				}

				String newProductId = broadBandInfo.getString("packageId","");

				//新营销活动产品ID
				String newSaleProductId = broadBandInfo.getString("activityId","");

				//老营销活动包ID
				String oldSalePackageId = broadBandInfo.getString("OLD_SALE_ACTIVE_PACKAGE_ID");

				//老营销活动产品ID
				String oldSaleProductId = broadBandInfo.getString("OLD_SALE_ACTIVE_PRODUCT_ID");

				//预约时间
				String bookingDate = SysDateMgr.getSysDate();

				IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId, "1", "1");
				forceElements = ProductUtils.offerToElement(forceElements, newProductId);
				IDataset selectedelements = new DatasetList();
				if (IDataUtil.isNotEmpty(forceElements))
				{
					String sysDate = SysDateMgr.getSysTime();

					for (int i = 0; i < forceElements.size(); i++)
					{
						IData element = new DataMap();
						element.put("ELEMENT_ID", forceElements.getData(i).getString("ELEMENT_ID"));
						element.put("ELEMENT_TYPE_CODE", forceElements.getData(i).getString("ELEMENT_TYPE_CODE"));
						element.put("PRODUCT_ID", newProductId);
						element.put("PACKAGE_ID", forceElements.getData(i).getString("PACKAGE_ID"));
						element.put("MODIFY_TAG", "0");
						element.put("START_DATE", sysDate);
						element.put("END_DATE", "2050-12-31");
						selectedelements.add(element);
					}
				}
				else
				{
					CSAppException.appError("-1", "产品["+newProductId+"]没有配置,请联系管理员!");
				}
				String elements = selectedelements.toString();

				//新营销活动费用
				int newActiveFee = 0;

				//用户需要支付的金额
				int payFee = 0;

				//CRM计算出的包年套餐到预约生效时剩余的包年余额
				int yearDiscntRemainFee = 0;

				//根据账务查询的余额计算出预约生效时剩余的余额
				int acctRemainFee = 0;

				WidenetMoveBean wb = new WidenetMoveBean();
				IData input = new DataMap();
				input.put("PRODUCT_ID", newSaleProductId);

				//获得营销活动费用信息
				IData feeInfo = wb.queryCheckSaleActiveFee(input);

				if(IDataUtil.isNotEmpty(feeInfo))
				{
					if (StringUtils.isNotBlank(feeInfo.getString("SALE_ACTIVE_FEE", "0")))
					{
						newActiveFee = Integer.valueOf(feeInfo.getString("SALE_ACTIVE_FEE", "0"));
					}
				}

				//新的营销活动需要交纳费用的时候才需要进行费用计算以及校验
				if (newActiveFee > 0)
				{
					//包年优惠费用
					String yearDiscntFee = "0" ;
					String yearDiscntStartDate = "";

					if(StringUtils.isNotBlank(elements))
					{
						IDataset selectedElements = new DatasetList(elements);
						if(IDataUtil.isNotEmpty(selectedElements))
						{
							//包年优惠配置参数
							IDataset commparaInfos532 = CommparaInfoQry.getCommpara("CSM", "532", "601", "0898");

							if(IDataUtil.isNotEmpty(commparaInfos532))
							{
								for(int i = 0 ;i < selectedElements.size() ; i++)
								{
									IData element = selectedElements.getData(i);
									String elementTypeCode = element.getString("ELEMENT_TYPE_CODE","");
									String modifyTag = element.getString("MODIFY_TAG","");

									//如果是删除的优惠
									if("1".equals(modifyTag) && "D".equals(elementTypeCode))
									{
										String delDiscntId =  element.getString("ELEMENT_ID","");
										IData commpara532 = null;

										for(int j = 0 ; j < commparaInfos532.size() ; j++)
										{
											commpara532 = commparaInfos532.getData(j);

											if(StringUtils.equals(delDiscntId, commpara532.getString("PARA_CODE1","")))
											{
												//是包年套餐
												yearDiscntFee = commpara532.getString("PARA_CODE3","0");
												yearDiscntStartDate = element.getString("START_DATE");
												break;
											}
										}
									}
								}
							}
						}
					}

					//如果用户原来订购的是包年套餐
					if (StringUtils.isNotBlank(yearDiscntFee) && StringUtils.isNotBlank(yearDiscntStartDate))
					{
						//包年套餐到预约变更时已使用的月数
						int useMonths = SysDateMgr.monthIntervalYYYYMM(chgFormat(yearDiscntStartDate,"yyyy-MM-dd","yyyyMM"), chgFormat(bookingDate,"yyyy-MM-dd","yyyyMM"));


						if (useMonths < 0)
						{
							useMonths = 0;
						}
						else if (useMonths > 12)
						{
							useMonths = 12;
						}

						int yearDiscntFeeInt = Integer.valueOf(yearDiscntFee);

						//CRM计算出的包年套餐到预约生效时剩余的包年余额
						yearDiscntRemainFee = yearDiscntFeeInt - (yearDiscntFeeInt/12)*useMonths;
						if(yearDiscntRemainFee<0){
							yearDiscntRemainFee = 0;
						}

						//实际要支付的钱以账务那边查出的费用为准
						payFee = newActiveFee - yearDiscntRemainFee;
					}
					//如果不是判断用户当前订购的是否是包年类营销活动
					else
					{
						if (StringUtils.isNotBlank(oldSaleProductId) && StringUtils.isNotBlank(oldSalePackageId))
						{
							//包年费用
							int oldActiveYearFee = 0;

							input.put("PRODUCT_ID", oldSaleProductId);
							input.put("PACKAGE_ID", oldSalePackageId);

							IData oldSalefeeInfo = wb.queryCheckSaleActiveFee(input);

							if(IDataUtil.isNotEmpty(oldSalefeeInfo))
							{
								if (StringUtils.isNotBlank(feeInfo.getString("SALE_ACTIVE_FEE", "0")))
								{
									oldActiveYearFee = Integer.valueOf(oldSalefeeInfo.getString("SALE_ACTIVE_FEE", "0"));
								}
							}

							//如果老的营销活动是存在费用的
							if (oldActiveYearFee > 0)
							{
								IData userInfoData = UcaInfoQry.qryUserInfoBySn(sn);

								//根据包年优惠，计算当前包年活动优惠使用时间
								int useMonths = 0;
								//到下个月初实际使用月份
								int currentUseMonths = 0;

								//默认生效时间为次月1日
								String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());

								IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(userInfoData.getString("USER_ID"));
								if (IDataUtil.isNotEmpty(useDiscnts))
								{
									for(int i=0;i<useDiscnts.size();i++)
									{
										if(oldSaleProductId.equals(useDiscnts.getData(i).getString("PRODUCT_ID", ""))
												&&oldSalePackageId.equals(useDiscnts.getData(i).getString("PACKAGE_ID", "")))
										{
											String startDate = useDiscnts.getData(i).getString("START_DATE", "");
											useMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(bookingDate,"yyyy-MM-dd","yyyyMM"));

											currentUseMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(firstDayOfNextMonth,"yyyy-MM-dd","yyyyMM"));

											break;
										}
									}
								}

								//获取营销活动的周期
								IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", oldSalePackageId, null);
								//IDataset newcom181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", newSalePackageId, null);

								//妈的脑袋有坑，12月不能整除68180510,68180511,68180512,68180513,68180514
								//前十一个月32元，最后一个月36元
								boolean oldmd = false;
								boolean newmd = false;
								int oldmd1 = 0;
								int oldmd2 = 0;
								int newmd1 = 0;
								int newmd2 = 0;
								int months=12;//默认12个月
								if (IDataUtil.isNotEmpty(com181))
								{
									months=com181.getData(0).getInt("PARA_CODE4",12);
									//老的包年套餐是388、588
									if("1".equals(com181.getData(0).getString("PARA_CODE7","")))
									{
										oldmd = true;
									}
								}

								/*if(IDataUtil.isNotEmpty(newcom181))
								{
									//新的包年套餐是388、588
									if("1".equals(newcom181.getData(0).getString("PARA_CODE7","")))
									{
										newmd = true;
									}
								}*/

								/*if (useMonths < 0)
								{
									useMonths = 0;
								}
								else if(useMonths > months)
								{
									useMonths = months;
								}*/

								//如果是包年营销活动终止，此处需要对包年活动剩余的费用（宽带包年活动专项款存折（赠送）9023和 宽带包年活动专项款存折9021）赠送给用户
								//到（宽带包年活动变更预存回退存折（赠送） 宽带包年活动变更预存回退存折）
								//查询用户该存折（9021,9023）是否还有剩余的费用
								//计算专项存折的费用
								int balance9021 = 0,balance9023 = 0;

								IDataset allUserMoney = AcctCall.queryAccountDepositBySn(sn);

								if(IDataUtil.isNotEmpty(allUserMoney))
								{
									for(int i=0;i<allUserMoney.size();i++)
									{
										String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
										int balance2 = Integer.parseInt(balance1);

										if("9021".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE")))
										{
											balance9021 = balance9021 + balance2;
										}

										if("9023".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE")))
										{
											balance9023 = balance9023 + balance2;
										}
									}
								}

								//包年套餐到预约变更的月数
								int bookingMonths = SysDateMgr.monthIntervalYYYYMM(chgFormat(SysDateMgr.getSysDate(),"yyyy-MM-dd","yyyyMM"), chgFormat(bookingDate,"yyyy-MM-dd","yyyyMM"))-1;

								//营销活动剩余周期
								int remainMonths = months - currentUseMonths;

								//如果预约的月数大于营销活动剩余的周期月数，则以剩余的周期月数为准
								if (remainMonths < bookingMonths)
								{
									bookingMonths = remainMonths;
								}

								//根据账务查询出的包年存折的余额计算出到预约生效时剩余的包年余额
								acctRemainFee = (balance9021+balance9023) - (oldActiveYearFee/months)*(bookingMonths+1); //加账务预销户一个月的钱
								if(remainMonths == months)
								{
									acctRemainFee = balance9021+balance9023;
								}

								if(acctRemainFee<0){
									acctRemainFee=0;
								}

								if(oldmd)
								{
									if(remainMonths==1)
									{
										acctRemainFee = 0; //加账务预销户一个月的钱
									}else
									{
										acctRemainFee = (balance9021+balance9023) - Integer.parseInt(com181.getData(0).getString("PARA_CODE5","0"))*(bookingMonths+1); //加账务预销户一个月的钱
									}

								}

								payFee = newActiveFee - acctRemainFee;
							}
							else
							{
								payFee = newActiveFee;
							}

						}
						else
						{
							payFee = newActiveFee;
						}
					}

					//如果需要支付的金额为负数，则设置为0
					if (payFee < 0)
					{
						payFee = 0;
					}
				}

				widenetFee.put("expenseItemName", "套餐月基础费用");
				widenetFee.put("expenseItemMoney", payFee);

				expenseItemMoney = String.valueOf(payFee);
				expenseItemList.add(widenetFee);
			}
	    	else if ("2".equals(busiType))   //增值产品费用
			{
				
	    		IDataset valueAddedProducts = param.getDataset("valueAddedProducts");
				
				if(IDataUtil.isEmpty(valueAddedProducts))
				{
					CSAppException.appError("-1", "参数【valueAddedProducts】为空");
				}
				
				for (int i = 0; i < valueAddedProducts.size(); i++) 
				{

					String addProductType = IDataUtil.chkParam(valueAddedProducts.getData(i), "productType");  // 0：魔百和；1：和目；2：IMS固话
					
					IData info = new DataMap();
					
					if("0".equals(addProductType)) //魔百和费用
					{
						//获取费用信息
			    		String money="20000";
			    		IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
			    		if(IDataUtil.isNotEmpty(moneyDatas)){
			    			money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
			    		}
			    		info.put("expenseItemName", "魔百和费用");
			    		info.put("expenseItemMoney", money);
						expenseItemList.add(info);
			    		
					}
					else if ("1".equals(addProductType))  //和目费用
					{ 
						//String hemuProductID = IDataUtil.chkParam(valueAddedProducts.getData(i), "packageId");
						String hemuSaleActiveID = IDataUtil.chkParam(valueAddedProducts.getData(i), "activityId");
						String hemuPackageId = "";
						IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "HEMU", null, null, hemuSaleActiveID, null);
				        
				        if(IDataUtil.isNotEmpty(saleActiveList))
				        {
				        	if(hemuSaleActiveID.equals(saleActiveList.getData(0).getString("PARA_CODE4")))
				        	{
				        		hemuPackageId = saleActiveList.getData(0).getString("PARA_CODE5");
				        	}
				        }
						
						info.put("expenseItemName", "和目营销活动费用");
			    		info.put("expenseItemMoney", fee(hemuSaleActiveID, hemuPackageId));
						expenseItemList.add(info);
					}
					else if ("2".equals(addProductType))  // IMS固话费用
					{ 
						String IMSSaleActiveID = IDataUtil.chkParam(valueAddedProducts.getData(i), "activityId");
						String IMSPackageId = "";
						
						//固话营销活动
						IDataset IMSActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "6800", "84004439");
						if(IDataUtil.isNotEmpty(IMSActiveList))
				        {
				        	if(IMSSaleActiveID.equals(IMSActiveList.getData(0).getString("PARA_CODE4")))
				        	{
				        		IMSPackageId = IMSActiveList.getData(0).getString("PARA_CODE5");
				        	}
				        }
					    info.put("expenseItemName", "固话费用");
			    		info.put("expenseItemMoney", fee(IMSSaleActiveID, IMSPackageId));
						expenseItemList.add(info);
					}
				}
			}
    	} catch (Exception e) 
		{
			IData error = new DataMap();
			error.put("FLAG", "-1");
			error.put("MESSAGE", e.getMessage());
			return BroadBandUtil.requestData(IDataUtil.idToIds(error));
		}
        
    	result.put("expenseItemList", expenseItemList);
    	result.put("totalMoney", expenseItemMoney);
    	result.put("FLAG", "1");
        resultlist.add(result);
        
        return BroadBandUtil.requestData(resultlist);
    }
    
    /**
	 * 宽带续约业务受理    C898HQBroadBandRenew
	 * @author zhengkai5
	 * @throws Exception 
	 * */
    public IData BroadBandRenew(IData params) throws Exception
    {
    	IData param = new DataMap(params.toString()).getData("params");
		
		IDataset dataset = new DatasetList(); 
		boolean flag = true;
		String errorMessage = "";
		
		try {
			IData custInfo = param.getData("custInfo");  //客户信息 
			
			//校验  服务号码
			String sn = IDataUtil.chkParam(custInfo, "acceptNum");  
			IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(sn);
			
			IData broadBandInfo = param.getData("broadBandInfo");   // 宽带产品信息
			String productId = IDataUtil.chkParam(broadBandInfo, "packageId"); //产品id
			
			IData feeInfo = param.getData("feeInfo");
			String payType = IDataUtil.chkParam(feeInfo, "payType");
			if (!"1".equals(payType))
			{
				CSAppException.appError("-1", "当前宽带只支持手机转账方式付费!");
			}
			
			IData userWidenetData = new DataMap(); 
			
			//无手机宽带续费   681
			if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET"))) {
				
				IData valueAddedProducts = param.getData("valueAddedProducts");
				if(IDataUtil.isNotEmpty(valueAddedProducts))
				{
					CSAppException.appError("-1", "无手机宽带没有增值产品！");
				}
				
				userWidenetData.put("SERIAL_NUMBER", wideTypeInfo.getString("WIDE_SERIAL_NUMBER"));  
				userWidenetData.put("USER_ID", wideTypeInfo.getString("WIDE_USER_ID"));  
				
				//宽带金额
			    IDataset feeInfos = CSAppCall.call("SS.NoPhoneWideChangeProdSVC.getProductFeeInfo", userWidenetData);
			    if(feeInfos!=null && feeInfos.size()>0)
			    {
			    	userWidenetData.put("MODE", feeInfos.getData(0).getString("FEE_MODE"));
			    	userWidenetData.put("CODE", feeInfos.getData(0).getString("FEE_TYPE_CODE"));
			    	userWidenetData.put("FEE", feeInfos.getData(0).getString("FEE")); 
			    	userWidenetData.put("PRODUCT_ID", feeInfos.getData(0).getString("PRODUCT_ID"));
			    	userWidenetData.put("PACKAGE_ID", feeInfos.getData(0).getString("PACKAGE_ID"));
			    	userWidenetData.put("DISCNT_CODE", feeInfos.getData(0).getString("DISCNT_CODE")); 
			    	userWidenetData.put("DISCNT_NAME", feeInfos.getData(0).getString("DISCNT_NAME"));
			    	userWidenetData.put("DISCNT_START_DATE", feeInfos.getData(0).getString("DISCNT_START_DATE"));
			    	userWidenetData.put("DISCNT_END_DATE", feeInfos.getData(0).getString("DISCNT_END_DATE"));
			    	userWidenetData.put("START_DATE", feeInfos.getData(0).getString("NEW_START_DATE"));
			    	userWidenetData.put("END_DATE", feeInfos.getData(0).getString("NEW_END_DATE"));
			    	userWidenetData.put("RENEW_TAG_DEC", feeInfos.getData(0).getString("RENEW_TAG_DEC"));
			    	userWidenetData.put("RENEW_TAG", feeInfos.getData(0).getString("RENEW_TAG"));
			    	userWidenetData.put("RENEW_FEE", feeInfos.getData(0).getString("FEE"));
			    	userWidenetData.put("FEE_YEAR", feeInfos.getData(0).getString("FEE_YEAR"));
			    	userWidenetData.put("FEE_DAY", feeInfos.getData(0).getString("FEE_DAY"));
			    	userWidenetData.put("STOP_OPEN_TAG", feeInfos.getData(0).getString("STOP_OPEN_TAG"));//停机后办理标记
					
			    }   
			    dataset = CSAppCall.call("SS.NoPhoneWideRenewRegSVC.tradeReg", userWidenetData);
			}
			else //601   
			{
				String wideUserId = wideTypeInfo.getString("WIDE_USER_ID");
				
				IDataset selectedelements = new DatasetList();
				
				IData inParam = new DataMap();
				
			    IDataset userInfo = UserInfoQry.getUserInfoByUserIdTag(wideUserId, "0");
		        if(IDataUtil.isNotEmpty(userInfo))
		        {
			        String inDate = userInfo.getData(0).getString("OPEN_DATE");
					String inMonth = SysDateMgr.getDateForYYYYMMDD(inDate).substring(0, 6);
					String currMonth = SysDateMgr.getNowCycle().substring(0, 6);
					if (inMonth.equals(currMonth))//判断是不是宽带首月免费期内不能办理产品变更
					{
						CSAppException.apperr(CrmUserException.CRM_USER_783,"宽带免费期内不能办理产品变更!");
					}
		        }

		        // 查询生效的优惠
		        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(wideUserId);
		        if (IDataUtil.isEmpty(discntInfo))
		        {
		        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带免费期内不能办理产品变更!");
		        }
				
				inParam.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
				inParam.put("TRADE_TYPE_CODE", "601");
				inParam.put("SERIAL_NUMBER_A", wideTypeInfo.getString("SERIAL_NUMBER"));
		        IDataset checkWidenetActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", inParam);
				
		        if (IDataUtil.isNotEmpty(checkWidenetActives))
		        {
		        	if ("1".equals(checkWidenetActives.getData(0).getString("FLAG","")))
		        	{
		        		CSAppException.appError("-1", "用户的宽带营销活动还在合同期内，不能办理此业务！");
		        	}
		        }
		        
		        inParam.put("SERIAL_NUMBER", wideTypeInfo.getString("WIDE_SERIAL_NUMBER"));
		        inParam.put("CHANGE_TYPE", "1");
		        inParam.put("BOOKING_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime()));
		        inParam.put("PRODUCT_ID", productId);
		        
		        IData oldPorductInfo = UcaInfoQry.qryMainProdInfoByUserId(wideTypeInfo.getString("WIDE_USER_ID"));
	            
	            if (IDataUtil.isEmpty(oldPorductInfo))
	            {
	                CSAppException.appError("-1", "该宽带用户主产品信息不存在！");
	            }
		        
		        // 必选或者默认的元素
	    		IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
	    		forceElements = ProductUtils.offerToElement(forceElements, productId);
	    		
	        	if (IDataUtil.isNotEmpty(forceElements))
	        	{
	        		
	        		for (int i = 0; i < forceElements.size(); i++) 
	        		{
	        			//主服务不需要再次订购
	        			if ("S".equals(forceElements.getData(i).getString("ELEMENT_TYPE_CODE")) && "1".equals(forceElements.getData(i).getString("MAIN_TAG")))
	        			{
	        				continue;
	        			}
	        			
	        			IData element = new DataMap();
	        	        element.put("ELEMENT_ID", forceElements.getData(i).getString("ELEMENT_ID"));
	        	        element.put("ELEMENT_TYPE_CODE", forceElements.getData(i).getString("ELEMENT_TYPE_CODE"));
	        	        element.put("PRODUCT_ID", productId);
	        	        element.put("PACKAGE_ID", forceElements.getData(i).getString("PACKAGE_ID"));
	        	        element.put("MODIFY_TAG", "0");
	        	        element.put("START_DATE", inParam.getString("BOOKING_DATE"));
	        	        element.put("END_DATE", "2050-12-31");
	        	        selectedelements.add(element);
					}
	        	}
	        	else
	        	{
	        		CSAppException.appError("-1", "该产品["+productId+"]下面没有元素配置信息，请联系管理员!");
	        	}
	        	
	        	//用户已有的产品元素
	            IDataset userElements = UserSvcInfoQry.getElementFromPackageByUser(wideTypeInfo.getString("WIDE_USER_ID"), oldPorductInfo.getString("PRODUCT_ID"), null);
	            
	            if (IDataUtil.isNotEmpty(userElements))
	            {
	                for (int j = 0; j < userElements.size(); j++ )
	                {
	                    IData element = userElements.getData(j);
	                    
	                    //宽带主服务不变
	                    if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")) && "1".equals(element.getString("MAIN_TAG")))
	                    {
	                        continue;
	                    }
	                    
	                    element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
	                    element.put("END_DATE", SysDateMgr.getLastSecond(inParam.getString("BOOKING_DATE")));
	                    element.put("PRODUCT_ID", oldPorductInfo.getString("PRODUCT_ID"));
	                    
	                    selectedelements.add(element);
	                }
	            }
	            
	            inParam.put("SELECTED_ELEMENTS", selectedelements.toString());
	            
	            IDataset changeResults = CSAppCall.call("SS.WidenetChangeProductNewRegSVC.tradeReg", inParam);
			}
		} catch (Exception e) {
			flag = false;
			errorMessage = e.getMessage();
		}
		
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();
		
		if(flag)
		{
			resultData.put("FLAG","1");
			resultData.put("flag","1" );
			resultData.put("desc","续约受理成功" );
			resultData.put("iteractId",dataset.first().getString("ORDER_ID",""));
		}else {
			resultData.put("FLAG","-1" );
			resultData.put("flag","0" );
			resultData.put("desc",errorMessage );
		}
		resultList.add(resultData);
        return BroadBandUtil.requestData(resultList);
    }
    
	/**
	 * 宽带新装业务受理
	 * @author zhengkai5
	 * @throws Exception 
	 * */
	public  IData BroadBandInstall(IData params) throws Exception
	{
		if(true){
			return BroadBandInstall_HNKDZQ(params);
		}
		IData param = new DataMap(params.toString()).getData("params");
		
		IDataset dataset = new DatasetList(); 
		boolean flag = true;
		String errorMessage = "";
		try {
			IData custInfo = param.getData("custInfo");
			
			IData broadBandInfo = param.getData("broadBandInfo");
			String sn = IDataUtil.chkParam(custInfo, "acceptNum");
			
			String productId = IDataUtil.chkParam(broadBandInfo, "packageId"); //产品id
			String DEVICE_ID = IDataUtil.chkParam(broadBandInfo, "DEVICE_ID"); //设备id
			
			IData widenetInfo = new DataMap();
			
			IDataset selectedelements = new DatasetList();
			
			// 必选或者默认的元素
    		IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
    		forceElements = ProductUtils.offerToElement(forceElements, productId);
    		
        	if (IDataUtil.isNotEmpty(forceElements))
        	{
        		
        		for (int i = 0; i < forceElements.size(); i++) 
        		{
        			//主服务不需要再次订购
        			if ("S".equals(forceElements.getData(i).getString("ELEMENT_TYPE_CODE")) && "1".equals(forceElements.getData(i).getString("MAIN_TAG")))
        			{
        				continue;
        			}
        			
        			IData element = new DataMap();
        	        element.put("ELEMENT_ID", forceElements.getData(i).getString("ELEMENT_ID"));
        	        element.put("ELEMENT_TYPE_CODE", forceElements.getData(i).getString("ELEMENT_TYPE_CODE"));
        	        element.put("PRODUCT_ID", productId);
        	        element.put("PACKAGE_ID", forceElements.getData(i).getString("PACKAGE_ID"));
        	        element.put("MODIFY_TAG", "0");
        	        element.put("START_DATE", SysDateMgr.getSysDate());
        	        element.put("END_DATE", "2050-12-31");
        	        selectedelements.add(element);
				}
        	}
        	else
        	{
        		CSAppException.appError("-1", "该产品["+productId+"]下面没有元素配置信息，请联系管理员!");
        	}
        	
        	widenetInfo.put("SELECTED_ELEMENTS", selectedelements.toString());
			
			widenetInfo.put("SERIAL_NUMBER", sn);
			widenetInfo.put("WIDE_PRODUCT_ID", productId);
			
			IDataset wideSNdataset = CSAppCall.call("SS.WideUserCreateSVC.getWideSerialNumber", widenetInfo);
			
	        String wideSerialNumber = wideSNdataset.getData(0).getString("WIDE_SERIAL_NUMBER");
	        widenetInfo.put("WIDE_SERIAL_NUMBER", wideSerialNumber);
			
			widenetInfo.put("AUTH_SERIAL_NUMBER", sn);
			
			widenetInfo.put("WIDE_PSPT_ID", custInfo.getString("certNbr"));
			widenetInfo.put("CONTACT", custInfo.getString("linkMan"));
			widenetInfo.put("CONTACT_PHONE", custInfo.getString("linkNum",sn));
			widenetInfo.put("STAND_ADDRESS", broadBandInfo.getString("standardAddressName"));
			widenetInfo.put("STAND_ADDRESS_CODE", broadBandInfo.getString("standardAddressID"));
			widenetInfo.put("SALE_ACTIVE_ID", broadBandInfo.getString("activityId")); //营销活动 
			//关于开发家庭终端调测费的需求(在线客服)
			widenetInfo.put("SALE_ACTIVE_ID2", broadBandInfo.getString("activityId2","")); //营销活动 
			//关于开发家庭终端调测费的需求(在线客服)
			widenetInfo.put("IS_HIGHTACTIVE", broadBandInfo.getString("highValueTag")); //是否办理搞价值小区 0：不办理  1：办理

			widenetInfo.put("DEVICE_ID", DEVICE_ID); //设备ID 
			
			IDataset valueAddedProducts = param.getDataset("valueAddedProducts");
			if(IDataUtil.isNotEmpty(valueAddedProducts))	
			{
				for (int i = 0; i < valueAddedProducts.size(); i++) 
				{
					String productType = valueAddedProducts.getData(i).getString("productType");
					
					if("0".equals(productType))  //当前业务场景只支持魔百和受理    0：魔百和；1：和目；2：IMS固话；3：其他
					{
						String topsetboxProductId = valueAddedProducts.getData(i).getString("packageId");
						
						IData topSetBoxPlatSvcPackages = PlatSvcInfoQry.queryDiscntPackagesByPID(productId);
				        String BASE_PACKAGES = topSetBoxPlatSvcPackages.getString("BASE_PACKAGES");
				        String OPTION_PACKAGES = topSetBoxPlatSvcPackages.getString("OPTION_PACKAGES");
						
						widenetInfo.put("TOP_SET_BOX_PRODUCT_ID", topsetboxProductId);
						//关于开发家庭终端调测费的需求(在线客服)
						widenetInfo.put("TOP_SET_BOX_SALE_ACTIVE_ID", valueAddedProducts.getData(i).getString("activityId",""));
						widenetInfo.put("TOP_SET_BOX_SALE_ACTIVE_ID2", valueAddedProducts.getData(i).getString("activityId2",""));
						//关于开发家庭终端调测费的需求(在线客服)
						widenetInfo.put("BASE_PACKAGES", BASE_PACKAGES);
						widenetInfo.put("OPTION_PACKAGES", OPTION_PACKAGES);
						widenetInfo.put("TOP_SET_BOX_DEPOSIT", "0");
					}
				}
			}
			
			if ("1".equals(param.getData("feeInfo").getString("payType")))
			{
				widenetInfo.put("WIDENET_PAY_MODE", "P"); //立即支付
			} 
			else if ("2".equals(param.getData("feeInfo").getString("payType"))) 
			{
				widenetInfo.put("WIDENET_PAY_MODE", "A"); //先装后付
			} 
			else 
			{
				CSAppException.appError("-1", "不支持当前支付方式！");
			}
			
			widenetInfo.put("TRADE_TYPE_CODE","600"); 
			widenetInfo.put("ORDER_TYPE_CODE","600"); 
			dataset = CSAppCall.call("SS.MergeWideUserCreateRegSVC.tradeReg", widenetInfo);
		} catch (Exception e) {
			errorMessage = e.getMessage();
			flag = false;
		}
		
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();
		
		if(flag)
		{
			resultData.put("FLAG","1");
			resultData.put("flag","1" );
			resultData.put("desc","受理成功" );
			resultData.put("iteractId",dataset.first().getString("ORDER_ID",""));
		}else {
			resultData.put("FLAG","-1" );
			resultData.put("flag","0" );
			resultData.put("desc",errorMessage );
		}
		resultList.add(resultData);
        return BroadBandUtil.requestData(resultList);
	}
	
	
	 /**
     *  201804中移在线四轮驱动接口需求-增值产品办理业务受理
     * @param input
     * @return
     * @throws Exception
     * @author zhengkai5
     */
    public IData OrderVAProduct (IData params) throws Exception
    {
    	boolean flag = true;
    	String errorMessage = "";
    	IDataset result = new DatasetList();
    	try {
	    	IData param = new DataMap(params.toString()).getData("params");
	    	IDataset valueAddedProducts = param.getDataset("valueAddedProducts");
    	
	    	//判断无手机or有手机宽带用户
	    	String sn = IDataUtil.chkParam(param.getData("custInfo"), "acceptNum");  
	    	IData wideNetData = WideNetUtil.getWideNetTypeInfo(sn);
	    	
	    	//无手机宽带不能受理增值产品
	    	if ("Y".equals(wideNetData.getString("IS_NOPHONE_WIDENET"))) 
	    	{
				CSAppException.appError("-1", "无手机宽带不能受理增值产品!");
			}
	    	
	    	//手机号码
	    	String serialNumber = wideNetData.getString("SERIAL_NUMBER");  
	    	String wSerialNumber = wideNetData.getString("WIDE_SERIAL_NUMBER");  
	    	
	    	IDataset wideProductIdInfo = UserProductInfoQry.queryAllUserValidMainProducts(wideNetData.getString("WIDE_USER_ID"));
	    	if(IDataUtil.isEmpty(wideProductIdInfo))
	    	{
	    		CSAppException.appError("-1", "无手机宽带不能受理增值产品!");
	    	}
	    	String wideProductID = wideProductIdInfo.first().getString("PRODUCT_ID");
	    	
	    	//校验受理费用
	    	String totalMoney = IDataUtil.chkParam(param.getData("feeInfo"), "totalMoney");  
	    	
	    	String leftFee = WideNetUtil.qryBalanceDepositBySn(sn);
	    	
	    	if(Integer.parseInt(leftFee) < Integer.parseInt(totalMoney))
	        {
	    		CSAppException.appError("-1", "您的账户存折可用余额不足，请先办理缴费!");
	        }
	    	
	    	//增值产品受理
    		if(IDataUtil.isNotEmpty(valueAddedProducts))
    		{
    			for (int i = 0; i < valueAddedProducts.size(); i++) {
					
    				IData addedProduct = valueAddedProducts.getData(i);
					
					String productType = IDataUtil.chkParam(addedProduct, "productType");
					
					addedProduct.put("SERIAL_NUMBER", serialNumber);
					addedProduct.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
					
					// 0：魔百和；  1：和目；  2：IMS固话；
					if("0".equals(productType))
					{
						IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
			            
			            addedProduct.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
			            addedProduct.put("RSRV_STR4", wideNetInfo.getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
						
						String productId = IDataUtil.chkParam(addedProduct, "packageId"); 
						
						addedProduct.put("PRODUCT_ID", productId);  // 魔百和产品
						addedProduct.put("MO_PRODUCT_ID", addedProduct.getString("activityId")); //魔百和营销活动
						
						IData topSetBoxPlatSvcPackages = PlatSvcInfoQry.queryDiscntPackagesByPID(productId);
				        String BASE_PACKAGES = topSetBoxPlatSvcPackages.getString("BASE_PACKAGES");
				        String OPTION_PACKAGES = topSetBoxPlatSvcPackages.getString("OPTION_PACKAGES");
						
				        addedProduct.put("WORK_TYPE", BASE_PACKAGES);
				        addedProduct.put("BASE_PACKAGES", BASE_PACKAGES);
				        addedProduct.put("OPTION_PACKAGES", OPTION_PACKAGES);
						
				        result= CSAppCall.call("SS.InternetTvOpenRegSVC.tradeReg", addedProduct);
					}
					else if ("1".equals(productType)) 
					{
						String resID = IDataUtil.chkParam(addedProduct, "RES_CODE");
						String productId = IDataUtil.chkParam(addedProduct, "activityId");   //和目产品本身就是营销活动
				        
						IData hemuData = new DataMap();
						hemuData.put("ROUTE_EPARCHY_CODE", "0898");
						hemuData.put("productId", wideProductID);
						IDataset hemuSaleActiveInfos = CSAppCall.call("SS.MergeWideUserCreateSVC.getHeMuSaleActive", hemuData);
						String PACKAGE_ID = "";
						for (int j = 0; j < hemuSaleActiveInfos.size(); j++) {
							if( productId.equals(hemuSaleActiveInfos.getData(j).getString("PARA_CODE5") ))
							{
								PACKAGE_ID = hemuSaleActiveInfos.getData(j).getString("PARA_CODE4");
							}
						}
						
						IData heMuSaleActiveInParam = new DataMap();
						heMuSaleActiveInParam.put("SERIAL_NUMBER",serialNumber);
						heMuSaleActiveInParam.put("PRODUCT_ID",productId);
						heMuSaleActiveInParam.put("PACKAGE_ID", PACKAGE_ID);
						heMuSaleActiveInParam.put("SALEGOODS_IMEI", resID);
//						heMuSaleActiveInParam.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);
						//认证方式
						heMuSaleActiveInParam.put("CHECK_MODE","Z" );
		              
						result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", heMuSaleActiveInParam);
			              
					} 
					else if ("2".equals(productType)) 
					{
						String productId = IDataUtil.chkParam(addedProduct, "packageId"); 
						String IMSSaleActiveProductId = IDataUtil.chkParam(addedProduct, "activityId"); 
						String SERIAL_NUMBER  =  IDataUtil.chkParam(addedProduct, "IMS_SERIAL_NUMBER"); 
						
						IData imsData = new DataMap();
			        	
			        	imsData.put("PRODUCT_ID", productId);
			        	imsData.put("SERIAL_NUMBER", serialNumber);
			        	imsData.put("WIDE_SERIAL_NUMBER","0898" + SERIAL_NUMBER);
			        	
			        	
			        	imsData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
			        	imsData.put("WIDE_PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(wideProductID));
			        	
			        	// IMS固话产品必选或者默认的元素
			    		IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
			    		forceElements = ProductUtils.offerToElement(forceElements, productId);
			    		
			    		IDataset selectedelements = new DatasetList();
			    		
			        	if (IDataUtil.isNotEmpty(forceElements))
			        	{
			        		String sysDate = SysDateMgr.getSysTime();
			        		
			        		for (int j = 0; j < forceElements.size(); j++) 
			        		{
			        			IData element = new DataMap();
			        	        element.put("ELEMENT_ID", forceElements.getData(j).getString("ELEMENT_ID"));
			        	        element.put("ELEMENT_TYPE_CODE", forceElements.getData(j).getString("ELEMENT_TYPE_CODE"));
			        	        element.put("PRODUCT_ID", productId);
			        	        element.put("PACKAGE_ID", forceElements.getData(j).getString("PACKAGE_ID"));
			        	        element.put("MODIFY_TAG", "0");
			        	        element.put("START_DATE", sysDate);
			        	        element.put("END_DATE", "2050-12-31");
			        	        selectedelements.add(element);
							}
			        	}
			            
			        	imsData.put("SELECTED_ELEMENTS", selectedelements.toString());
			        	
			        	IData packagesData = new DataMap();
			        	packagesData.put("PRODUCT_ID", productId);
			        	packagesData.put("ROUTE_EPARCHY_CODE", "0898");
			            IDataset results = CSAppCall.call("SS.IMSLandLineSVC.queryDiscntPackagesByPID", packagesData);
			            
			            IData retData = results.first();
			            
			            IDataset imsSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST");
			        	
			            String imsSaleActiveId = "";
			            
			            for (int j = 0; j < imsSaleActiveList.size(); j++) 
			            {
			            	if (IMSSaleActiveProductId == imsSaleActiveList.getData(j).getString("PARA_CODE4"));
							{
								imsSaleActiveId = imsSaleActiveList.getData(j).getString("PARA_CODE5");
							}
						}
			            
			            //计算营销活动费用
			            String ImsSaleActiveFee = fee(IMSSaleActiveProductId,imsSaleActiveId);
			            
			        	//IMS固话营销活动产品ID
			        	imsData.put("MO_PRODUCT_ID", IMSSaleActiveProductId);
			        	//IMS固话营销活动包ID
			        	imsData.put("MO_PACKAGE_ID", imsSaleActiveId);
			        	//IMS固话营销活动预存费用
			        	imsData.put("TOP_SET_BOX_SALE_ACTIVE_FEE", ImsSaleActiveFee);
			        	
			        	//认证方式
			            imsData.put("CHECK_MODE","Z" );
			        	
			            result = CSAppCall.call("SS.IMSLandLineRegSVC.tradeReg", imsData);
					}
				}
    		}
    		else
    		{
    			CSAppException.appError("-1", "接口参数检查: 输入参数[valueAddedProducts]不存在或者参数值为空");
    		}
		} catch (Exception e) {
			errorMessage = e.getMessage();
			flag = false;
		}
		
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();
		
		if(flag)
		{
			resultData.put("FLAG","1");
			resultData.put("retCode","0" );
			resultData.put("desc","增值产品受理成功" );
			resultData.put("iteractId",result.first().getString("ORDER_ID","") );
		}else {
			resultData.put("FLAG","-1" );
			resultData.put("retCode","-1" );
			resultData.put("desc","增值产品受理失败" );
			resultData.put("retMsg",errorMessage );
		}
		resultList.add(resultData);
        return BroadBandUtil.requestData(resultList);
    }
    
    
    /**
     * 营销活动费用计算
     * */
	public String fee (String productId ,  String packageId) throws Exception
	{
		IData feeData = new DataMap();
		String SaleActiveFee = "";
        feeData.put("PACKAGE_ID", packageId);
        feeData.put("PRODUCT_ID", productId);
        IData SaleActiveFeeData = CSAppCall.callOne("SS.MergeWideUserCreateSVC.queryCheckSaleActiveFee", feeData);
        if(IDataUtil.isNotEmpty(SaleActiveFeeData))
        {
        	SaleActiveFee = SaleActiveFeeData.getString("SALE_ACTIVE_FEE");
        }
        
        return SaleActiveFee ;
	}
	
	
	/**
     * 增值产品退订受理(UnsubscribeVAProduct)
     * @param input
     * @return
     * @throws Exception
     * @author zhengkai5
	 * @throws Exception 
     */
	public IData UnsubscribeVAProduct(IData params) throws Exception
	{
		IData param = new DataMap(params.toString()).getData("params"); 
		
		boolean flag = true;
		String errorMessage = "";
		IDataset dataset  = new DatasetList();
		try {
			String sn = IDataUtil.chkParam(param, "busiNum");
			
			String productId = IDataUtil.chkParam(param, "productId");
			
			IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(sn);
			
			if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET")))
			{
				CSAppException.appError("-1", "无手机宽带暂不支持此业务！");
			}
			else 
			{
				IDataset topSetBoxProducts = CommparaInfoQry.getCommparaByCodeCode1("CSM", "182", "600", productId);
				
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
						//魔百和拆机
						IData topsetbox = new DataMap();
						topsetbox.put("SERIAL_NUMBER", wideTypeInfo.getString("SERIAL_NUMBER"));
						topsetbox.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
				        dataset = CSAppCall.call("SS.DestroyTopSetBoxRegSVC.tradeReg", topsetbox);
					}
					
				}
				else if (productId.endsWith("84004439"))
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
			         
			         //和目营销活动返销
			         
			         
				}
				else
				{
					CSAppException.appError("-1", "暂不支持退订该宽带增值业务！");
				}
			}
		} catch (Exception e) {
			flag = false;
			errorMessage = e.getMessage();
		}
		
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();
		
		if(flag)
		{
			resultData.put("FLAG","1");
			resultData.put("flag","1");
			resultData.put("retCode","0" );
			resultData.put("desc","增值产品退订成功" );
			resultData.put("iteractId","");
		}
		else 
		{
			resultData.put("FLAG","-1" );
			resultData.put("flag","0");
			resultData.put("retCode","-1" );
			resultData.put("desc","增值产品退订失败" );
			resultData.put("retMsg",errorMessage );
		}
		resultList.add(resultData);
        return BroadBandUtil.requestData(resultList);
	}
	
	
	/**
     *  201804中移在线四轮驱动接口需求-宽带提速业务受理 （）
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData BroadBandSpeedUp(IData inputs) throws Exception
    {
    	IDataset resultdDataset = new DatasetList();
		IData resultInfo = new DataMap();
		
		try 
		{
			IData input = new DataMap(inputs.toString()).getData("params");
			
			IData custInfo = input.getData("custInfo");
			IData speedPackageInfo = input.getData("speedPackageInfo");
			IData feeInfo = input.getData("feeInfo");
			
			//服务号码
			String sn = IDataUtil.chkParam(custInfo, "acceptNum");
			String speedPackageId = IDataUtil.chkParam(speedPackageInfo, "speedPackageId");
			String payType = IDataUtil.chkParam(feeInfo, "payType");
			
			IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(sn);
			
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
				if (!"1".equals(payType))
				{
					CSAppException.appError("-1", "当前宽带只支持手机转账方式付费!");
				}
				
				String wideUserId = wideTypeInfo.getString("WIDE_USER_ID");
				
				IDataset selectedelements = new DatasetList();
				
				IData inParam = new DataMap();
				
			    IDataset userInfo = UserInfoQry.getUserInfoByUserIdTag(wideUserId, "0");
		        if(IDataUtil.isNotEmpty(userInfo))
		        {
			        String inDate = userInfo.getData(0).getString("OPEN_DATE");
					String inMonth = SysDateMgr.getDateForYYYYMMDD(inDate).substring(0, 6);
					String currMonth = SysDateMgr.getNowCycle().substring(0, 6);
					if (inMonth.equals(currMonth))//判断是不是宽带首月免费期内不能办理产品变更
					{
						CSAppException.apperr(CrmUserException.CRM_USER_783,"宽带免费期内不能办理产品变更!");
					}
		        }

		        // 查询生效的优惠
		        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(wideUserId);
		        if (IDataUtil.isEmpty(discntInfo))
		        {
		        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带免费期内不能办理产品变更!");
		        }
				
				inParam.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
				inParam.put("TRADE_TYPE_CODE", "601");
				inParam.put("SERIAL_NUMBER_A", wideTypeInfo.getString("SERIAL_NUMBER"));
		        IDataset checkWidenetActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", inParam);
				
		        if (IDataUtil.isNotEmpty(checkWidenetActives))
		        {
		        	if ("1".equals(checkWidenetActives.getData(0).getString("FLAG","")))
		        	{
		        		CSAppException.appError("-1", "用户的宽带营销活动还在合同期内，不能办理此业务！");
		        	}
		        }
		        
		        inParam.put("SERIAL_NUMBER", wideTypeInfo.getString("WIDE_SERIAL_NUMBER"));
		        inParam.put("CHANGE_TYPE", "1");
		        inParam.put("BOOKING_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime()));
		        inParam.put("PRODUCT_ID", speedPackageId);
		        
		        
		        IData oldPorductInfo = UcaInfoQry.qryMainProdInfoByUserId(wideTypeInfo.getString("WIDE_USER_ID"));
	            
	            if (IDataUtil.isEmpty(oldPorductInfo))
	            {
	                CSAppException.appError("-1", "该宽带用户主产品信息不存在！");
	            }
		        
		        // 必选或者默认的元素
	    		IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, speedPackageId, "1", "1");
	    		forceElements = ProductUtils.offerToElement(forceElements, speedPackageId);
	    		
	        	if (IDataUtil.isNotEmpty(forceElements))
	        	{
	        		
	        		for (int i = 0; i < forceElements.size(); i++) 
	        		{
	        			//主服务不需要再次订购
	        			if ("S".equals(forceElements.getData(i).getString("ELEMENT_TYPE_CODE")) && "1".equals(forceElements.getData(i).getString("MAIN_TAG")))
	        			{
	        				continue;
	        			}
	        			
	        			IData element = new DataMap();
	        	        element.put("ELEMENT_ID", forceElements.getData(i).getString("ELEMENT_ID"));
	        	        element.put("ELEMENT_TYPE_CODE", forceElements.getData(i).getString("ELEMENT_TYPE_CODE"));
	        	        element.put("PRODUCT_ID", speedPackageId);
	        	        element.put("PACKAGE_ID", forceElements.getData(i).getString("PACKAGE_ID"));
	        	        element.put("MODIFY_TAG", "0");
	        	        element.put("START_DATE", inParam.getString("BOOKING_DATE"));
	        	        element.put("END_DATE", "2050-12-31");
	        	        selectedelements.add(element);
					}
	        	}
	        	else
	        	{
	        		CSAppException.appError("-1", "该产品["+speedPackageId+"]下面没有元素配置信息，请联系管理员!");
	        	}
	        	
	        	
	        	//用户已有的产品元素
	            IDataset userElements = UserSvcInfoQry.getElementFromPackageByUser(wideTypeInfo.getString("WIDE_USER_ID"), oldPorductInfo.getString("PRODUCT_ID"), null);
	            
	            if (IDataUtil.isNotEmpty(userElements))
	            {
	                for (int j = 0; j < userElements.size(); j++ )
	                {
	                    IData element = userElements.getData(j);
	                    
	                    //宽带主服务不变
	                    if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")) && "1".equals(element.getString("MAIN_TAG")))
	                    {
	                        continue;
	                    }
	                    
	                    element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
	                    element.put("END_DATE", SysDateMgr.getLastSecond(inParam.getString("BOOKING_DATE")));
	                    element.put("PRODUCT_ID", oldPorductInfo.getString("PRODUCT_ID"));
	                    
	                    selectedelements.add(element);
	                }
	            }
	            
	            inParam.put("SELECTED_ELEMENTS", selectedelements.toString());
	            
	            IDataset changeResults = CSAppCall.call("SS.WidenetChangeProductNewRegSVC.tradeReg", inParam);
				
	            resultInfo.put("flag", "1");
    			resultInfo.put("desc", "宽带提速包办理成功");
    			resultInfo.put("iteractId", changeResults.getData(0).getString("TRADE_ID"));
    			
    			resultdDataset.add(resultInfo);
			}
		} 
		catch (Exception e) 
		{
			IData error = new DataMap();
			error.put("FLAG", "-1");
			error.put("MESSAGE", e.getMessage());
			return BroadBandUtil.requestData(IDataUtil.idToIds(error));
		}
        
        return BroadBandUtil.requestData(resultdDataset);
    }

	public static String chgFormat(String strDate, String oldForm, String newForm) throws Exception{
		if (null == strDate)
		{
			throw new NullPointerException();
		}

		DateFormat oldDf = new SimpleDateFormat(oldForm);
		Date date = oldDf.parse(strDate);

		String newStr = "";
		DateFormat newDf = new SimpleDateFormat(newForm);
		newStr = newDf.format(date);
		return newStr;
	}
	 //=====================海南宽带新装专区接口=====================================
    public IData AcceptFeeCalculate_HNKDZQ(IData inputs) throws Exception
    {
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
    		//关于开发家庭终端调测费的需求(在线客服)
    		String activityFee2="0";
    		String MBHActivityFee2="0";
    		String activityId2="";
    		String mbhActivityId2="";
    		//关于开发家庭终端调测费的需求(在线客服)
    		String modemDeposit="0";
    		String MBHDeposit="0";
    		String productId="";
    		String activityId="";
    		String mbhActivityId="";
    		IData dynamicParams=input.getData("dynamicParams");
    		if(dynamicParams!=null){
    			activityFee=dynamicParams.getString("activityFee","0");
    			MBHActivityFee=dynamicParams.getString("MBHActivityFee","0");
    			//关于开发家庭终端调测费的需求(在线客服)
    			activityFee2=dynamicParams.getString("activityFee2","0");
    			MBHActivityFee2=dynamicParams.getString("MBHActivityFee2","0");
    			activityId2=dynamicParams.getString("activityId2","");
    			mbhActivityId2=dynamicParams.getString("mbhActivityId2","");
    			//关于开发家庭终端调测费的需求(在线客服)
    			modemDeposit=dynamicParams.getString("modemDeposit","0");
    			productId=dynamicParams.getString("productId","");
    			activityId=dynamicParams.getString("activityId","");
    			mbhActivityId=dynamicParams.getString("mbhActivityId","");
    		}
    		/*if("".equals(productId)){
    			throw new Exception("对不起，productId为空");
    		}*/

    		//查询魔百和费用
    		if(StringUtils.isNotEmpty(productId)){
	    		IData params=new DataMap();
	    		params.put("TOP_SET_BOX_PRODUCT_ID", productId);
	    		params.put("TOP_SET_BOX_SALE_ACTIVE_ID",mbhActivityId);
	    		params.put("SALE_ACTIVE_ID",activityId);
	    		params.put("SERIAL_NUMBER", input.getString("busiNum"));
	    		params.put("QUERY_TYPE", "4");
	    		params.put("ROUTE_EPARCHY_CODE", "0898");
	    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.queryTopSetBoxDiscntPackagesIntf", params);
	    		if(resultList!=null&&resultList.size()>0){
	    			MBHDeposit="0";//关于开发家庭终端调测费的需求(在线客服)
	    		}
    		}
    		IDataset expenseItemList=new DatasetList();
    		IData feeData=new DataMap();
    		feeData.put("expenseItemName", "宽带营销活动费用");
    		feeData.put("expenseItemNameID", "activityFee");
    		feeData.put("recExpItemMoney", activityFee);
    		feeData.put("preExpItemMoney", "0");
    		feeData.put("actExpItemMoney", activityFee);
    		expenseItemList.add(feeData);
    		
    		//关于开发家庭终端调测费的需求(在线客服)
    		feeData=new DataMap();
    		feeData.put("expenseItemName", "宽带调测费活动费用");
    		feeData.put("expenseItemNameID", "activityFee2");
    		feeData.put("recExpItemMoney", activityFee2);
    		feeData.put("preExpItemMoney", "0");
    		feeData.put("actExpItemMoney", activityFee2);
    		expenseItemList.add(feeData);
    		feeData=new DataMap();
    		feeData.put("expenseItemName", "魔百和调测费活动费用");
    		feeData.put("expenseItemNameID", "MBHActivityFee2");
    		feeData.put("recExpItemMoney", MBHActivityFee2);
    		feeData.put("preExpItemMoney", "0");
    		feeData.put("actExpItemMoney", MBHActivityFee2);
    		expenseItemList.add(feeData);
    		//关于开发家庭终端调测费的需求(在线客服)
    		
    		feeData=new DataMap();
    		feeData.put("expenseItemName", "光猫押金");
    		feeData.put("expenseItemNameID", "modemDeposit");
    		feeData.put("recExpItemMoney", modemDeposit);
    		feeData.put("preExpItemMoney", "0");
    		feeData.put("actExpItemMoney", modemDeposit);
    		expenseItemList.add(feeData);
    		feeData=new DataMap();
    		feeData.put("expenseItemName", "魔百和营销活动费用");
    		feeData.put("expenseItemNameID", "MBHActivityFee");
    		feeData.put("recExpItemMoney", MBHActivityFee);
    		feeData.put("preExpItemMoney", "0");
    		feeData.put("actExpItemMoney", MBHActivityFee);
    		expenseItemList.add(feeData);
    		feeData=new DataMap();
    		feeData.put("expenseItemName", "魔百和押金");
    		feeData.put("expenseItemNameID", "MBHDeposit");
    		feeData.put("recExpItemMoney", (Integer.parseInt(MBHDeposit))+"");
    		feeData.put("preExpItemMoney", "0");
    		feeData.put("actExpItemMoney", (Integer.parseInt(MBHDeposit))+"");
    		expenseItemList.add(feeData);
    		//费用明细
    		result.put("expenseItemList", expenseItemList);
    		//应收总费用
    		result.put("recTotalMoney", (Integer.parseInt(activityFee)+Integer.parseInt(MBHActivityFee)
    				+Integer.parseInt(activityFee2)+Integer.parseInt(MBHActivityFee2) //关于开发家庭终端调测费的需求(在线客服)
    				+Integer.parseInt(modemDeposit)+Integer.parseInt(MBHDeposit))+"");
    		//优惠总费用
    		result.put("preTotalMoney", "0");
    		//实收总费用
    		result.put("actTotalMoney", (Integer.parseInt(activityFee)+Integer.parseInt(MBHActivityFee)
    				+Integer.parseInt(activityFee2)+Integer.parseInt(MBHActivityFee2) //关于开发家庭终端调测费的需求(在线客服)
    				+Integer.parseInt(modemDeposit)+Integer.parseInt(MBHDeposit))+"");
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in AcceptFeeCalculate_HNKDZQ()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
    }
    /**
     * 无资源登记
     * @param inputs
     * @return
     * @throws Exception
     */
    public IData NoResourceEnter(IData inputs) throws Exception
    {
    	IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		//0为无资源录入 1为端口满
    		String type="";
    		IData addressInfo=input.getData("addressInfo");
    		if(addressInfo==null){
    			throw new Exception("对不起，addressInfo为空");
    		}
    		IData dynamicParams=addressInfo.getData("dynamicParams");
    		if(dynamicParams==null){
    			throw new Exception("对不起，动态参数dynamicParams为空");
    		}
    		
    		IData crmpfPubInfo=input.getData("crmpfPubInfo");
    		if(crmpfPubInfo==null){
    			throw new Exception("对不起，动态参数crmpfPubInfo为空");
    		}
    		
    		IDataUtil.chkParam(addressInfo, "type");
    		IDataUtil.chkParam(crmpfPubInfo, "staffId");
    		
    		//查询并设定登录员工信息
    		IData staffData=BroadBandUtilHNKDZQ.qryLoginStaffInfo(crmpfPubInfo, getVisit());
    		
    		//无资源录入
    		if("0".equals(addressInfo.getString("type"))){
    			IDataUtil.chkParam(addressInfo, "busiNum");
    			IDataUtil.chkParam(addressInfo, "linkName");
    			IDataUtil.chkParam(addressInfo, "linkNum");
    			
    			IDataUtil.chkParam(dynamicParams, "CityCode");
    			IDataUtil.chkParam(dynamicParams, "City");
    			IDataUtil.chkParam(dynamicParams, "CountyCode");
    			IDataUtil.chkParam(dynamicParams, "County");
    			IDataUtil.chkParam(dynamicParams, "Town");
    			IDataUtil.chkParam(dynamicParams, "Village");
    			IDataUtil.chkParam(dynamicParams, "Building");
    			IDataUtil.chkParam(dynamicParams, "Room");
    			
        		//拼入参
    			IData params=new DataMap();
        		params.put("SERIAL_NUMBER", addressInfo.get("busiNum"));
        		params.put("CUST_NAME", addressInfo.get("linkName"));
        		params.put("CUST_PONE", addressInfo.get("linkNum"));
        		params.put("REMARK", addressInfo.get("remark"));
        		params.put("BAND_WIDTH", dynamicParams.get("BAND_WIDTH"));
        		params.put("Reason", dynamicParams.get("Reason"));
        		params.put("CityCode", dynamicParams.get("CityCode"));
        		params.put("City", dynamicParams.get("City"));
        		params.put("CountyCode", dynamicParams.get("CountyCode"));
        		params.put("County", dynamicParams.get("County"));
        		params.put("Town", dynamicParams.get("Town"));
        		params.put("Village", dynamicParams.get("Village"));
        		params.put("Building", dynamicParams.get("Building"));
        		params.put("Room", dynamicParams.get("Room"));
        		
        		IDataset resultList=CSAppCall.call("SS.WidePreRegRegSVC.tradeReg", params);
        		if(resultList!=null&&resultList.size()>0){
        			result.put("iteractId", resultList.getData(0).get("TRADE_ID"));
        		}
    			
    		//端口满	
    		}else if("1".equals(addressInfo.getString("type"))){
    			IDataUtil.chkParam(addressInfo, "busiNum");
    			
    			IDataUtil.chkParam(dynamicParams, "DETAIL_ADDRESS");
    			IDataUtil.chkParam(dynamicParams, "DEVICE_ID");
    			IDataUtil.chkParam(dynamicParams, "DEVICE_NAME");
    			IDataUtil.chkParam(dynamicParams, "AREA_CODE");
    			
    			//拼入参
    			IData params=new DataMap();
        		params.put("DETAIL_ADDRESS", dynamicParams.getString("DETAIL_ADDRESS"));
        		params.put("AREA_CODE", dynamicParams.getString("AREA_CODE"));
        		params.put("MOBILE_NO", addressInfo.getString("busiNum"));
        		params.put("DEVICE_ID", dynamicParams.getString("DEVICE_ID"));
        		params.put("DEVICE_NAME", dynamicParams.getString("DEVICE_NAME"));
        		params.put("PRE_STAFF_NAME", staffData.getString("STAFF_NAME"));
        		params.put("PRE_STAFF_ID", staffData.getString("STAFF_ID"));
        		params.put("PRE_DEPT_ID", staffData.getString("DEPART_ID"));
        		params.put("PRE_DEPT_NAME", staffData.getString("DEPART_ID"));
        		params.put("CITY_CODE", staffData.getString("CITY_CODE"));
        		params.put("SERIAL_NUMBER", staffData.getString("SERIAL_NUMBER",""));
        		params.put("EPARCHY_CODE", dynamicParams.getString("0898"));

        		IDataset resultList=CSAppCall.call("PB.AddrPreDeal.addAddrPreDeal", params);
        		if(resultList!=null&&resultList.size()>0){
        			if("1".equals(resultList.getData(0).getString("FLAG"))){
        				throw new Exception(resultList.getData(0).getString("RESULT"));
        			}
        		}
    		}else{
    			throw new Exception("对不起，type不匹配:"+type);
    		}
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in NoResourceEnter()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseData(result);
    }
    /**
     * 宽带办理提交接口
     * @param inputs
     * @return
     * @throws Exception
     */
    public IData BroadBandInstall_HNKDZQ(IData inputs) throws Exception
    {
    	IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IData custInfo=input.getData("custInfo");
    		IData broadBandInfo=input.getData("broadBandInfo");
    		IDataset valueAddedProducts=input.getDataset("valueAddedProducts");
    		IData feeInfo=input.getData("feeInfo");
    		IData dynamicParams=input.getData("dynamicParams");
    		if(custInfo==null){
    			throw new Exception("对不起，custInfo为空");
    		}
    		if(broadBandInfo==null){
    			throw new Exception("对不起，broadBandInfo为空");
    		}
    		/*if(valueAddedProducts==null||valueAddedProducts.size()==0){
    			throw new Exception("对不起，valueAddedProducts为空");
    		}*/
    		if(feeInfo==null){
    			throw new Exception("对不起，feeInfo为空");
    		}
    		if(dynamicParams==null){
    			throw new Exception("对不起，dynamicParams为空");
    		}
    		IData crmpfPubInfo=input.getData("crmpfPubInfo");
    		if(crmpfPubInfo==null){
    			throw new Exception("对不起，动态参数crmpfPubInfo为空");
    		}
    		
    		//非空判断
    		IDataUtil.chkParam(custInfo, "acceptNum");
			IDataUtil.chkParam(broadBandInfo, "standardAddressName");
			IDataUtil.chkParam(broadBandInfo, "remarkAddress");
			IDataUtil.chkParam(custInfo, "linkMan");
			IDataUtil.chkParam(custInfo, "linkNum");
			IDataUtil.chkParam(dynamicParams, "areaCode");
			IDataUtil.chkParam(broadBandInfo, "packageId");
			IDataUtil.chkParam(dynamicParams, "serviceId");
			IDataUtil.chkParam(dynamicParams, "discntId");

			IDataUtil.chkParam(feeInfo, "PAY_MODE");
			IDataUtil.chkParam(broadBandInfo, "acceptType");
			IDataUtil.chkParam(dynamicParams, "deviceId");
			
			IDataUtil.chkParam(crmpfPubInfo, "staffId");
			
			//查询并设定登录员工信息
    		BroadBandUtilHNKDZQ.qryLoginStaffInfo(crmpfPubInfo, getVisit());
			/*IData staffParam=new DataMap();
			staffParam.put("STAFF_ID", crmpfPubInfo.getString("staffId"));
			IDataset staffDataSet=PersonCallResIntfSVC.callRes("RCF.resource.IResComponentQuerySV.queryStaffInfoByStaffId", staffParam);
			IData staffData=null;
			if(staffDataSet!=null&&staffDataSet.getData(0)!=null
					&&staffDataSet.getData(0).getDataset("DATAS")!=null){
				
				staffData=staffDataSet.getData(0).getDataset("DATAS").getData(0);
			}
			
			if(staffData==null){
				throw new Exception("对不起，staffData为空");
			}
			IDataUtil.chkParam(staffData, "STAFF_ID");
			IDataUtil.chkParam(staffData, "STAFF_NAME");
			IDataUtil.chkParam(staffData, "DEPART_ID");
			IDataUtil.chkParam(staffData, "CITY_CODE");
			IDataUtil.chkParam(staffData, "SERIAL_NUMBER");
			
			getVisit().setStaffId(staffData.getString("STAFF_ID"));
			getVisit().setStaffName(staffData.getString("STAFF_NAME"));
			getVisit().setDepartId(staffData.getString("DEPART_ID"));
			getVisit().setCityCode(staffData.getString("CITY_CODE"));
			getVisit().setLoginEparchyCode("0898");
			getVisit().setInModeCode("SD");
			getVisit().setStaffEparchyCode("0898");*/
			
			//拼入参
			IData params=new DataMap();
    		params.put("SERIAL_NUMBER", custInfo.getString("acceptNum"));
    		params.put("STAND_ADDRESS", broadBandInfo.getString("standardAddressName"));
    		params.put("DETAIL_ADDRESS", broadBandInfo.getString("remarkAddress"));
    		params.put("CONTACT", custInfo.getString("linkMan"));
    		params.put("CONTACT_PHONE", custInfo.getString("linkNum"));
    		params.put("PHONE", custInfo.getString("linkNum"));
    		params.put("AREA_CODE", dynamicParams.getString("areaCode"));
    		params.put("PRODUCT_ID", broadBandInfo.getString("packageId"));
    		params.put("SERVICE_ID", dynamicParams.getString("serviceId"));
    		params.put("DISCNT_CODE", dynamicParams.getString("discntId"));
    		params.put("WIDENET_PAY_MODE", feeInfo.getString("PAY_MODE"));
    		params.put("WIDE_TYPE", broadBandInfo.getString("acceptType"));
    		params.put("MODEM_STYLE", dynamicParams.getString("modemStyle"));
    		params.put("FLOOR_AND_ROOM_NUM", dynamicParams.getString("floorRoomNum"));
    		params.put("FLOOR_AND_ROOM_NUM_FLAG", dynamicParams.getString("floorRoomNumState","1"));
    		params.put("DEVICE_ID", dynamicParams.getString("deviceId"));
    		params.put("IS_HIGHTACTIVE", broadBandInfo.getString("highValueTag")); //是否办理搞价值小区 0：不办理  1：办理
    		params.put("SUGGEST_DATE", broadBandInfo.getString("appointmentDate"));
    		params.put("SALE_ACTIVE_ID", broadBandInfo.getString("activityId"));
    		params.put("SALE_ACTIVE_FEE", dynamicParams.getString("saleActiveFee"));
    		//关于开发家庭终端调测费的需求(在线客服)
    		params.put("SALE_ACTIVE_ID2", broadBandInfo.getString("activityId2",""));
    		params.put("SALE_ACTIVE_FEE2", dynamicParams.getString("saleActiveFee2",""));
    		//关于开发家庭终端调测费的需求(在线客服)
    		if(valueAddedProducts!=null&&valueAddedProducts.size()>0){
    			params.put("TOP_SET_BOX_PRODUCT_ID", valueAddedProducts.getData(0).getString("brandId"));
        		params.put("TOP_SET_BOX_SALE_ACTIVE_ID", valueAddedProducts.getData(0).getString("activityId"));
        		//关于开发家庭终端调测费的需求(在线客服)
        		params.put("TOP_SET_BOX_SALE_ACTIVE_ID2", valueAddedProducts.getData(0).getString("activityId2",""));
        		//关于开发家庭终端调测费的需求(在线客服)
    		}else{
    			params.put("TOP_SET_BOX_PRODUCT_ID", "");
        		params.put("TOP_SET_BOX_SALE_ACTIVE_ID", "");
        		//关于开发家庭终端调测费的需求(在线客服)
        		params.put("TOP_SET_BOX_SALE_ACTIVE_ID2", "");
        		//关于开发家庭终端调测费的需求(在线客服)
    		}
    		
    		params.put("BASE_PACKAGES", dynamicParams.getString("basePackages"));
    		params.put("OPTION_PACKAGES", dynamicParams.getString("optionPackages"));
    		params.put("TOP_SET_BOX_SALE_ACTIVE_FEE", dynamicParams.getString("boxSaleActiveFee"));
    		//关于开发家庭终端调测费的需求(在线客服)
    		params.put("TOP_SET_BOX_SALE_ACTIVE_FEE2", dynamicParams.getString("boxSaleActiveFee2",""));
    		//关于开发家庭终端调测费的需求(在线客服)
    		params.put("MODEM_DEPOSIT", dynamicParams.getString("modemDeposit"));
    		params.put("TOP_SET_BOX_DEPOSIT", dynamicParams.getString("boxDeposit"));
    		
    		//入参转换
    		new MergeWideUserCreateIntfFilter().transferDataInput(params);
    		
    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateRegSVC.tradeReg", params);
			
    		if(resultList!=null&&resultList.size()>0){
    			result.put("iteractId", resultList.getData(0).get("ORDER_ID"));
    		}
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in BroadBandInstall_HNKDZQ()==", e);
			//e.printStackTrace();
			throw e;
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseData(result);
    }
}
