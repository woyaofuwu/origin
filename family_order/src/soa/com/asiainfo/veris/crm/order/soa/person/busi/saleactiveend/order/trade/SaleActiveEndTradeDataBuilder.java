package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.trade;

import java.math.BigDecimal;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveBean;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.requestdata.SaleActiveEndReqData;


public class SaleActiveEndTradeDataBuilder extends BaseTrade implements ITrade
{
	
    @SuppressWarnings("unchecked")
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        SaleActiveEndReqData saleactiveEndReqData = (SaleActiveEndReqData) btd.getRD();
		
        createThisSaleActiveTradeData(btd);
        
        createOtherSaleActiveData(btd);
        //strat--20181018--wangsc10
        createPlatSvcData(btd);
        //end
        ProductModuleCreator.createProductModuleTradeData(saleactiveEndReqData.getPmds(), btd);

        MainTradeData mainTradeData = btd.getMainTradeData();

        mainTradeData.setRemark(saleactiveEndReqData.getRemark());
        mainTradeData.setRsrvStr1(saleactiveEndReqData.getProductId());
        mainTradeData.setRsrvStr2(saleactiveEndReqData.getPackageId());
        mainTradeData.setRsrvStr10(saleactiveEndReqData.getBackTerm());//是否退还摄像头标记
        
        mainTradeData.setRsrvStr9(btd.getRD().getPageRequestData().getString("PAGE_SOURCE",""));
        
        /*
         * 打印使用字段
         * 
         */
    	StringBuilder rsrvStr8=new StringBuilder();
        IData productInfo=UProductInfoQry.qrySaleActiveProductByPK(saleactiveEndReqData.getProductId());//ProductInfoQry.queryAllProductInfo(saleactiveEndReqData.getProductId());
        if(IDataUtil.isNotEmpty(productInfo)){
        	rsrvStr8.append(productInfo.getString("PRODUCT_NAME")); //终止活动名称
        }else{
        	rsrvStr8.append("");
        }
        rsrvStr8.append("#null#");
        
        
        //获取违约终止时间
        boolean isGetValue=false;
        List<SaleActiveTradeData>  SaleActiveTradeDatas=btd.get("TF_B_TRADE_SALE_ACTIVE");
        for (SaleActiveTradeData saleActiveTradeData : SaleActiveTradeDatas) {
			if(saleActiveTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_DEL)
					&&saleActiveTradeData.getProductId().equals(saleactiveEndReqData.getProductId())){
				//计算已经使用的月份
				String startDate=saleActiveTradeData.getStartDate();
				//String curDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
				String curDate=saleActiveTradeData.getEndDate();
				
				int usedMonths=0;
				
				if(startDate.compareTo(curDate)<=0){
					usedMonths=SysDateMgr.monthInterval(startDate, curDate);
				}
				
				rsrvStr8.append(String.valueOf(usedMonths));  //活动正常履约月份
				rsrvStr8.append("#null#");
				rsrvStr8.append(saleActiveTradeData.getEndDate()); //违约终止时间
				rsrvStr8.append("#null#");
				
				isGetValue=true;
				
				break;
			}
		}
        if(!isGetValue){
        	rsrvStr8.append("");  //活动正常履约月份
			rsrvStr8.append("#null#");
			rsrvStr8.append(""); //违约终止时间
			rsrvStr8.append("#null#");
        }
        
        IData pageParam=btd.getRD().getPageRequestData(); 
        if(IDataUtil.isNotEmpty(pageParam)){      
        	String returnFee=pageParam.getString("RETURNFEE","0");
        	String ysreturnFee=pageParam.getString("YSRETURNFEE","0");
        	if(returnFee!=null&&!returnFee.trim().equals("")){
        		BigDecimal returnFeeD=new BigDecimal(returnFee);
        		double finalReturnFee=returnFeeD.divide(new BigDecimal(100)).doubleValue();
        		rsrvStr8.append(finalReturnFee);  //实收金额
        	}else{
        		rsrvStr8.append("");  //实收金额
        	}
        	
        	rsrvStr8.append("#null#");
        	
        	if(ysreturnFee!=null&&!ysreturnFee.trim().equals("")){
        		BigDecimal ysreturnFeeD=new BigDecimal(ysreturnFee);
        		double ysfinalReturnFee=ysreturnFeeD.divide(new BigDecimal(100)).doubleValue();
        		rsrvStr8.append(ysfinalReturnFee);  //应收金额
        	}else{
        		rsrvStr8.append("");  //应收金额
        	}
        }else{
        	rsrvStr8.append("");  //实收金额
        	rsrvStr8.append("#null#");
        	rsrvStr8.append("");   //应收金额
        }
        
        mainTradeData.setRsrvStr8(rsrvStr8.toString());

        if("1".equals(saleactiveEndReqData.getWideYearActiveBackFee()))
        	return ;
        
    	//查看该活动是否是包年宽带活动
    	String serialNumber = btd.getRD().getUca().getSerialNumber();
    	String userId = btd.getRD().getUca().getUserId();
    	String productId = saleactiveEndReqData.getProductId();
    	String packageId = saleactiveEndReqData.getPackageId();
    	
    	if("69900199".equals(productId))
    	{
    		int userMonths=0;	//履行合约月份
			IDataset orderSaleActives=UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
			if(IDataUtil.isNotEmpty(orderSaleActives))
			{
				IData orderSaleActive=orderSaleActives.getData(0);
				//计算已经使用的月份
				String startDate=orderSaleActive.getString("START_DATE");
				String curDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
				userMonths=SysDateMgr.monthInterval(startDate, curDate);
			}
    		
			if(userMonths < 12){
	    		IDataset AcctDeposit = AcctCall.queryAccountDepositBySn(serialNumber);
	    		if(IDataUtil.isNotEmpty(AcctDeposit)){
	    			for(int i=0;i<AcctDeposit.size();i++){
	    	    		IData deposit = AcctDeposit.getData(i);
	    	    		String acct_id = deposit.getString("ACCT_ID","");
	    	    		String trade_fee = deposit.getString("DEPOSIT_BALANCE", "");
	    	    		String deposit_code = deposit.getString("DEPOSIT_CODE","");
	    	    		if(("361".equals(deposit_code) || "362".equals(deposit_code)) && !"0".equals(trade_fee)){
	    	    			deposit.put("TRADE_FEE", trade_fee);
	    	    			deposit.put("ANNUAL_TAG", "1");
	    	    			IData inAcct = AcctCall.AMBackFee(deposit);
	    	    			if(IDataUtil.isNotEmpty(inAcct)&&("0".equals(inAcct.getString("RESULT_CODE",""))||"0".equals(inAcct.getString("X_RESULTCODE","")))){
	    	    				// 成功！ 处理other表
	    	    			}else{
	    	    				CSAppException.appError("60831", "调用接口 AM_CRM_BackFee 接口错误:"+inAcct.getString("X_RESULTINFO"));
	    	    			}
	    	    		}
	    	    	}
	    		}
			}
    	}
        
    	IDataset actives = CommparaInfoQry.getCommpara("CSM", "178", "600", saleactiveEndReqData.getUca().getUserEparchyCode());
    	boolean isYear = false;
    	for(int i=0;i<actives.size();i++){
    		IData active = actives.getData(i);
    		if(productId.equals(active.getString("PARA_CODE4",""))
    				&&"WIDE_YEAR_ACTIVE".equals(active.getString("PARA_CODE7",""))){
    			isYear = true;
    			break;
    		}
    	}
    	if(!isYear) return ;
    	
    	//计算专项存折的费用
    	int balance9021 = 0;
    	IDataset allUserMoney = AcctCall.queryAccountDepositBySn(serialNumber);
    	for(int i=0;i<allUserMoney.size();i++){
    		if("9021".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE"))){
    			String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
                int balance2 = Integer.parseInt(balance1);
                balance9021 = balance9021 + balance2;
    		}
    	}
    	
    	//包年费用
    	String activeYearFee = "";
    	WidenetMoveBean wb = new WidenetMoveBean();
    	IData input = new DataMap();
    	input.put("PRODUCT_ID", productId);
    	input.put("PACKAGE_ID", packageId);
    	IData feeInfo = wb.queryCheckSaleActiveFee(input);
    	if(IDataUtil.isNotEmpty(feeInfo)){
    		activeYearFee = feeInfo.getString("SALE_ACTIVE_FEE", "0");
    	}else{
    		return ;
    	}
    	
    	//根据包年优惠，计算当前包年活动优惠使用时间
    	int endMonths = 0;
    	IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(saleactiveEndReqData.getUca().getUserId());
    	if (IDataUtil.isNotEmpty(useDiscnts)){
    		for(int i=0;i<useDiscnts.size();i++){
    			if(productId.equals(useDiscnts.getData(i).getString("PRODUCT_ID", ""))&&packageId.equals(useDiscnts.getData(i).getString("PACKAGE_ID", ""))){
    				String startDate = useDiscnts.getData(i).getString("START_DATE", "");
    				endMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(saleactiveEndReqData.getEndDate(),"yyyy-MM-dd HH:mm:ss","yyyyMM")) + 1;
    				//endMonths = SysDateMgr.monthIntervalYYYYMM(startDate, saleactiveEndReqData.getEndDate()) + 1;
    			}
    		}
    	}
    	
        //查看包年活动周期
    	int activeMonth = 12;
        IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", packageId, null);
        if(IDataUtil.isNotEmpty(com181)) activeMonth = Integer.parseInt(com181.getData(0).getString("PARA_CODE4", "12"));
    	if(endMonths == activeMonth) return ;

    	//计算包年活动需要转账赠送或沉淀的预存
    	int yearFee = Integer.parseInt(activeYearFee);
    	int backFee = yearFee - yearFee/activeMonth*endMonths;

		/**add by xuzh5 2018-7-21 14:18:25 REQ201804190001 关于宽带包年特殊终止年费沉淀策略优化需求*/
		balance9021=balance9021-yearFee/activeMonth;//9021存折少沉余一个月

    	
    	if(backFee!=balance9021){
    		String flag = "2";
    		if(balance9021==0)flag= "0";
    		if(backFee>balance9021) flag="1";
    		btd.getMainTradeData().setRemark(flag);
    		if(balance9021<=0) return;
    	}
    	//if(balance9021<=0) return;btd.getMainTradeData()
    	
    	//如果是宽带产品变更界面发起的校验动作，则不进行沉淀。
    	String preType = btd.getRD().getPreType();
		//System.out.println("---------------SaleActiveEndTradeDataBuilder--------------"+preType);
		if (preType.equals(BofConst.PRE_TYPE_CHECK))
		{
			return;
		}
		
		//资金进行沉淀
		IData depositeParam=new DataMap();
		IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
		depositeParam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
		depositeParam.put("TRADE_FEE", balance9021);
		depositeParam.put("ANNUAL_TAG", "1");
		depositeParam.put("DEPOSIT_CODE", "9021");
		IData inAcct = AcctCall.AMBackFee(depositeParam);
		if(IDataUtil.isNotEmpty(inAcct)&&("0".equals(inAcct.getString("RESULT_CODE",""))||"0".equals(inAcct.getString("X_RESULTCODE","")))){
			// 成功！ 处理other表
		}else{
			CSAppException.appError("61312", "调用接口 AM_CRM_BackFee 接口错误:"+inAcct.getString("X_RESULTINFO"));
		}
    }

    @SuppressWarnings("unchecked")
    private void createOtherSaleActiveData(BusiTradeData btd) throws Exception
    {
        SaleActiveEndReqData saleactiveEndReqData = (SaleActiveEndReqData) btd.getRD();

        UcaData uca = saleactiveEndReqData.getUca();

        SaleActiveTradeData thisActiveUserData = uca.getUserSaleActiveByRelaTradeId(saleactiveEndReqData.getRelationTradeId());

        String thisActiveStartDate = thisActiveUserData.getStartDate();
        
        //需要获取修改前的END_DATE，所以不从UCA中取了  //QR-20150109-14 营销活动终止时间不对BUG by songlm @20150114 
        //String thisActiveStartDate = thisActiveUserData.getEndDate();
        IDataset beforeModifyActiveUserData = UserSaleActiveInfoQry.queryRelationSaleActiveList(uca.getUserId(),saleactiveEndReqData.getRelationTradeId());
        String thisActiveEndDate = beforeModifyActiveUserData.first().getString("END_DATE");
        //end
        
        String thisActiveMonth = thisActiveUserData.getMonths();
        
        //如果是宽带1+(魔百和)， 宽带1+（含预存款）活动，由于活动已经被延长2050年了，需要去tf_f_user_sale_depost表取结束时间和月份数。
        if("69908012".equals(saleactiveEndReqData.getProductId()) || "69908015".equals(saleactiveEndReqData.getProductId())){
        	IDataset saleDepostData = UserSaleActiveInfoQry.queryRelationSaleDepositList(uca.getUserId(),saleactiveEndReqData.getRelationTradeId());
        	if(IDataUtil.isNotEmpty(saleDepostData) && saleDepostData.size()>0){
        		//取返还里的结束时间和月份数
        		thisActiveEndDate = saleDepostData.first().getString("END_DATE");
        		thisActiveMonth = saleDepostData.first().getString("MONTHS");
        		//如果返还已经结束，说明原来的活动已经结束，后面的不需要前移。
                if(thisActiveEndDate.compareTo(SysDateMgr.getSysTime())<= 0){
                	saleactiveEndReqData.setIntervalMonth(-1); //不再需要调账务接口处理返还信息
                	return;
                }
        	}
        }
        
        //原来的算法貌似不对，改用getIntervalMonths   songlm 20150115 QR-20150109-14营销活动终止时间不对BUG
        int intervalMoths = Integer.parseInt(SaleActiveUtil.getIntervalMoths(thisActiveStartDate, thisActiveEndDate, thisActiveMonth));
        
        //单独针对约定在网时间处理 by songlm 20150116
        int intervalOnNetMoths = 0;
        String thisActiveOnNetStartDate = thisActiveUserData.getRsrvDate1();//针对后继活动，需要前移约定在网起始和终止时间，原方案存在bug，修复bug   by songlm 20150116
        String thisActiveOnNetEndDate = beforeModifyActiveUserData.first().getString("RSRV_DATE2");//约定在网结束时间
        if(StringUtils.isNotBlank(thisActiveOnNetStartDate) && StringUtils.isNotBlank(thisActiveOnNetEndDate))
        {
        	String thisActiveOnNetMonth = String.valueOf(SysDateMgr.monthInterval(thisActiveOnNetStartDate, thisActiveOnNetEndDate));
        	intervalOnNetMoths = Integer.parseInt(SaleActiveUtil.getIntervalMoths(thisActiveOnNetStartDate, thisActiveOnNetEndDate, thisActiveOnNetMonth));
        }
        //end
        
        //计算出来月份包当前月了，迁移时不包
        intervalMoths = intervalMoths - 1;
        intervalOnNetMoths = intervalOnNetMoths - 1;
        //先默认0，防止营业不前移活动，账务前移
        saleactiveEndReqData.setIntervalMonth(0);

        List<SaleActiveTradeData> userSaleActiveList = uca.getUserSaleActives();
        userSaleActiveList = SaleActiveUtil.filterUserSaleActivesByProcessTag(userSaleActiveList);
        userSaleActiveList = SaleActiveUtil.filterUserSaleActivesByModifyTag(userSaleActiveList);

        for (SaleActiveTradeData saleActiveData : userSaleActiveList)
        {
            String relationTradeId = saleActiveData.getRelationTradeId();

            if (saleactiveEndReqData.getRelationTradeId().equals(relationTradeId))
                continue;

            //判断是否是后继营销活动，即其他营销活动的开始时间是否大于当前终止营销活动的开始时间
            if (SaleActiveUtil.getDayIntervalNoAbs(saleActiveData.getStartDate(), thisActiveStartDate) > 0)
                continue;

            boolean isDateChanged = false;

            boolean userActiveIsBackActive = SaleActiveUtil.isBackActive(saleActiveData.getProductId(), uca.getUserEparchyCode());
            boolean thisActiveIsBackActive = SaleActiveUtil.isBackActive(saleactiveEndReqData.getProductId(), uca.getUserEparchyCode());

            if (userActiveIsBackActive && thisActiveIsBackActive)
            {
                saleActiveData.setStartDate(SysDateMgr.getAddMonthsNowday(-intervalMoths, saleActiveData.getStartDate()));
                saleActiveData.setEndDate(SysDateMgr.getAddMonthsNowday(-intervalMoths, saleActiveData.getEndDate()));
                isDateChanged = true;
            }

            boolean userActiveIsOnNetActive = SaleActiveUtil.isQyyx(saleActiveData.getCampnType());
            boolean thisActiveIsOnNetActvie = SaleActiveUtil.isQyyx(saleactiveEndReqData.getCampnType());
            boolean thisActiveOnNetActvieStart = false;
            if(StringUtils.isNotBlank(saleActiveData.getRsrvDate1()))
            {
            	thisActiveOnNetActvieStart = (SysDateMgr.getSysTime().compareTo(saleActiveData.getRsrvDate1()) < 0);
            }

            if (userActiveIsOnNetActive && thisActiveIsOnNetActvie && thisActiveOnNetActvieStart)
            {
                saleActiveData.setRsrvDate1(SysDateMgr.getAddMonthsNowday(-intervalOnNetMoths, saleActiveData.getRsrvDate1()));
                saleActiveData.setRsrvDate2(SysDateMgr.getAddMonthsNowday(-intervalOnNetMoths, saleActiveData.getRsrvDate2()));
                isDateChanged = true;
            }

            if (!isDateChanged)
            {
            	continue;
            }
                
            saleactiveEndReqData.setIntervalMonth(intervalMoths);
            saleActiveData.setRemark("营销活动终止业务导致后继活动前移[" + saleactiveEndReqData.getTradeId() + "]，前移月份["+intervalMoths+"]。");
            saleActiveData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(btd.getRD().getUca().getSerialNumber(), saleActiveData);
        }
    }
    //strat--20181018--wangsc10
    @SuppressWarnings("unchecked")
    public void createPlatSvcData(BusiTradeData btd)
    {
		SaleActiveEndReqData saleactiveEndReqData = (SaleActiveEndReqData) btd.getRD();
	    IDataset noBackParaActives = null;
		try {
			noBackParaActives = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "8919", saleactiveEndReqData.getProductId(), saleactiveEndReqData.getPackageId() ,"0898");
			if(noBackParaActives != null && !noBackParaActives.isEmpty()){
				for(int i=0;i<noBackParaActives.size();i++){
					String serviceid = noBackParaActives.getData(i).getString("PARA_CODE2");
		        	PlatSvcTradeData data=new PlatSvcTradeData();
			  
		        	UcaData uca = btd.getRD().getUca();
		        	List<PlatSvcTradeData> userPlatSvcs = null;
					try {
						userPlatSvcs = uca.getUserPlatSvcByServiceId(serviceid);
						if (userPlatSvcs.size() > 0) {
				        	  data=userPlatSvcs.get(0);
				        	  data.setModifyTag(BofConst.MODIFY_TAG_DEL);
				        	  data.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
				        	  data.setEndDate(SysDateMgr.getSysTime());
				        	  data.setOprSource("08");
				        	  data.setIsNeedPf("1");
				        	  data.setBizStateCode("E");
				        	  btd.add(btd.getRD().getUca().getSerialNumber(), data);
				         }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	 }
    //end
    @SuppressWarnings("unchecked")
    private void createThisSaleActiveTradeData(BusiTradeData btd) throws Exception
    {
        SaleActiveEndReqData saleactiveEndReqData = (SaleActiveEndReqData) btd.getRD();

        UcaData uca = saleactiveEndReqData.getUca();

        SaleActiveTradeData userSaleActiveTradeData = uca.getUserSaleActiveByRelaTradeId(saleactiveEndReqData.getRelationTradeId());

        SaleActiveTradeData saleActiveTradeData = userSaleActiveTradeData.clone();
        saleActiveTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        
        //QR-20150109-14 营销活动终止时间不对BUG by songlm @20150114 
        //选择终止时间为本月底，且不是宽带1+营销活动，则营销活动表的终止时间置为本月底；除此之外，都置为当前时间
        String endDate = saleactiveEndReqData.getAcceptTime();//默认终止时间为当前
        if(StringUtils.isNotBlank(saleactiveEndReqData.getEndDateValue()))
        {
        	String endDateValue = saleactiveEndReqData.getEndDateValue();
        	if("3".equals(endDateValue))//选择终止在本月底，且不为宽带1+
        	{
        		//BUS201809110024 宽带1+活动客户申请办理不限量套餐时系统自动终止宽带1+活动需求屏蔽校验
        		if(StringUtils.isNotBlank(btd.getRD().getPageRequestData().getString("END_MONTH_LAST"))){
        			
        			endDate = SysDateMgr.suffixDate(SysDateMgr.getAddMonthsLastDay(1),1);//获取本月最后一天23:59:59
        			
        		}else{
        			if("69908001".equals(saleActiveTradeData.getProductId()) || "69908015".equals(saleActiveTradeData.getProductId()))
            		{
            			
            		}
            		else
            		{
            			endDate = SysDateMgr.suffixDate(SysDateMgr.getAddMonthsLastDay(1),1);//获取本月最后一天23:59:59
            		}
        		}
        		
        	}
        }
        //强制取消的话，重置结束时间
        String endDataValue = saleactiveEndReqData.getEndDateValue();
        if(endDataValue != null && BofConst.MODIFY_TAG_FORCE_END.equals(endDataValue))
        {
        	String forceEndDate = saleactiveEndReqData.getForceEndDate() ;
        	if(forceEndDate != null && !"".equals(forceEndDate))
        	{
        		endDate = saleactiveEndReqData.getForceEndDate();  //指定结束时间强制结束时间
        	}
        }
        saleActiveTradeData.setEndDate(endDate);
        //end
        
        saleActiveTradeData.setCancelDate(saleactiveEndReqData.getAcceptTime());
        saleActiveTradeData.setRemark(saleactiveEndReqData.getRemark());
        saleActiveTradeData.setRsrvStr7(saleactiveEndReqData.getIntface());        	
        saleActiveTradeData.setRsrvStr6(saleactiveEndReqData.getReturnfee());
        saleActiveTradeData.setRsrvStr8(CSBizBean.getVisit().getCityCode());
        
        String onNetEndDate = saleActiveTradeData.getRsrvDate2();

        if (StringUtils.isNotBlank(onNetEndDate))
        {
            saleActiveTradeData.setRsrvDate2(saleactiveEndReqData.getEndDate());
        }

        if (StringUtils.isNotBlank(saleactiveEndReqData.getCallType()))
        {
            saleActiveTradeData.setRsrvTag3("S");
        }

        StringBuilder rsrvStr25 = new StringBuilder();
        if(StringUtils.isNotBlank(saleactiveEndReqData.getYSReturnfee())){
        	rsrvStr25.append(saleactiveEndReqData.getYSReturnfee());//应收违约金
        }else{
        	rsrvStr25.append("0");
        }
        rsrvStr25.append("|");
        
        if(StringUtils.isNotBlank(saleactiveEndReqData.getTrueReturnFeeCode())){
        	rsrvStr25.append(saleactiveEndReqData.getTrueReturnFeeCode());//实收违约成本金
        }else{
        	rsrvStr25.append("0");
        }
        rsrvStr25.append("|");
        
        if(StringUtils.isNotBlank(saleactiveEndReqData.getReturnfee())){
        	rsrvStr25.append(Integer.valueOf(saleactiveEndReqData.getReturnfee())-Integer.valueOf(saleactiveEndReqData.getTrueReturnFeeCode()));//实收违约金
        }else{
        	rsrvStr25.append("0");
        }
        rsrvStr25.append("|");
        
        if(StringUtils.isNotBlank(saleactiveEndReqData.getReturnfee())){
        	rsrvStr25.append(saleactiveEndReqData.getReturnfee());//实收总违约金
        }else{
        	rsrvStr25.append("0");
        }
        rsrvStr25.append("|");
        
        rsrvStr25.append("0");//减免违约成本金
        rsrvStr25.append("|");
        
        if(StringUtils.isNotBlank(saleactiveEndReqData.getYSReturnfee())){
        	rsrvStr25.append(Integer.valueOf(saleactiveEndReqData.getYSReturnfee())-Integer.valueOf(saleactiveEndReqData.getReturnfee()));//减免违约金
        }else{
        	rsrvStr25.append("0");
        }
        rsrvStr25.append("|");
        
        if(StringUtils.isNotBlank(saleactiveEndReqData.getRemark())){
        	rsrvStr25.append(saleactiveEndReqData.getRemark());//审批工单号
        }else{
        	rsrvStr25.append("");
        }
        rsrvStr25.append("|");
        
        if(StringUtils.isNotBlank(saleactiveEndReqData.getSrcPage())){
        	rsrvStr25.append(saleactiveEndReqData.getSrcPage());//减免违约金
        }else{
        	rsrvStr25.append("0");
        }
        saleActiveTradeData.setRsrvStr25(rsrvStr25.toString());
        
        
        btd.add(btd.getRD().getUca().getSerialNumber(), saleActiveTradeData);
    }
}

