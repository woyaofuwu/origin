
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry; 
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.FastAuthApproveQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry; 
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry; 
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class WideChangeUserCheckBean extends CSBizBean
{
	private static final Logger log = Logger.getLogger(WideChangeUserCheckBean.class);
	/**
	 * 新号码校验
	 * */
    public IData checkNewNumForChangeUser(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        IData param = new DataMap();
        String serial_number_a = input.getString("SERIAL_NUMBER_A", "");
        String wSerialNumber = "KD_" + serial_number_a;
        IData wUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
        String wUserId = "";
        if(wUserInfo!=null ){
        	wUserId = wUserInfo.getString("USER_ID");
        }
        String acctId = ""; // 账户id
        String userId = "";
        String custId = "";
        String checkDesc = "";
        String isCheck=""; //校验通过状态  1=校验通过
        String isCheckDec="";
        // 查询用户资料
        IDataset datauser = UserInfoQry.getAllUserInfoBySn(serial_number_a);
        if (datauser == null || datauser.size() == 0)
        {
            //CSAppException.apperr(CrmCommException.CRM_COMM_1122);
            checkDesc="用户资料不存在！";
        }
        else
        {
            if (!"0".equals(datauser.getData(0).getString("USER_STATE_CODESET")))
            {
                //CSAppException.apperr(CrmCommException.CRM_COMM_103, "701009:新手机号码不是正常开通状态，不给予过户！");
                checkDesc="@新手机号码不是正常开通状态，不允许过户！";
            }
            userId = datauser.getData(0).getString("USER_ID");
            custId = datauser.getData(0).getString("CUST_ID");
            // 查询客户信息
            IData datacust = UcaInfoQry.qryCustomerInfoByCustId(custId);
            if (datacust != null && datacust.size() > 0)
            {
                // 客户资料
                IData custPersonInfos = UcaInfoQry.qryPerInfoByCustId(custId);
                // 找不到再找集团用户
                if (custPersonInfos == null || custPersonInfos.size() <= 0)
                {

                    param.clear();
                    param.put("CUST_ID", custId);
                    IData custGroupInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
                    if (IDataUtil.isEmpty(custGroupInfo))
                    {
                        //CSAppException.apperr(CrmCommException.CRM_COMM_103, "客户标识号获取客户个人或客户集团资料无数据！");
                        checkDesc=checkDesc+"  @  客户标识号获取客户个人或客户集团资料无数据！";
                    }
                } 
                String isRealName=datacust.getString("IS_REAL_NAME","");
                if(isRealName==null || !"1".equals(isRealName)){
                	//CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户为非实名制，无法办理过户宽带！");
                	checkDesc=checkDesc+"  @  该用户为非实名制，无法办理过户宽带！";
                }
                
            }else{
            	checkDesc=checkDesc+"  @  客户标识号获取客户个人资料无数据！";
            }
            // 查询账户资料
            IData datapay = UcaInfoQry.qryPayRelaByUserId(userId);
            if (IDataUtil.isEmpty(datapay))
            {
                //CSAppException.apperr(CrmCommException.CRM_COMM_103, "701008:该号码[" + serial_number_a + "]无默认付费帐目！");
                checkDesc=checkDesc+"  @  该号码[" + serial_number_a + "]无默认付费帐目！";
            }
            else
            {
                IData temp = datapay;
                acctId = temp.getString("ACCT_ID");
            }
            IData acctInfos = UcaInfoQry.qryAcctInfoByUserId(userId);
            if (acctInfos == null || acctInfos.size() <= 0)
            {
                //CSAppException.apperr(CrmCommException.CRM_COMM_103, "701007:获取帐户资料无数据！");
                checkDesc=checkDesc+"  @  获取帐户资料无数据！";
            }
        }
        // 查询宽带，不允许有宽带用户
        param.clear();
        param.put("USER_ID", userId);
        IDataset dataset = WidenetInfoQry.getUserWidenetInfo(wUserId);
        if (IDataUtil.isNotEmpty(dataset)){
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, "此用户已经开通宽带业务，请换另外的手机号码！");
            checkDesc=checkDesc+"  @  此用户已经开通宽带业务，请换另外的手机号码！";
        }
        //是否有未完工单
        IDataset  countSet=TradeInfoQry.CheckIsExistNotGHFinishedTrade("KD_"+serial_number_a);
        if (!StringUtils.equals(countSet.getData(0).getString("ROW_COUNT"), "0"))
        {
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, "该["+serial_number_a+"]号码下存在未完工单，不能进行宽带帐号变更！");
            checkDesc=checkDesc+"  @  该["+serial_number_a+"]号码下存在未完工单，不能进行宽带帐号变更！";
        }
        
        IDataset  tradeList= WidenetInfoQry.getTradeListByUserId(userId);
        if(IDataUtil.isNotEmpty(tradeList)){
        	//CSAppException.apperr(CrmCommException.CRM_COMM_103, "该["+userId+"]下用户存在未完工单，不能进行宽带帐号变更！");
        	checkDesc=checkDesc+"  @  该["+userId+"]下用户存在未完工单，不能进行宽带帐号变更！";
        }
        //老用户剩余金额+光猫押金
        //String payRemainFee=input.getString("ACTIVE_REMAIN_FEE", "");
        String payModemFee=input.getString("MODEM_FEE", "");
        if(!"".equals(payModemFee) && (checkDesc==null || "".equals(checkDesc))){
	        //int rfee=Integer.parseInt(payRemainFee);
	        int mfee=Integer.parseInt(payModemFee);
	        //新用户余额
	        String userDeposit=getNewUserAcctDeposit(serial_number_a);
	        if(Double.parseDouble(userDeposit)>0){ 
	        	if(mfee>Double.parseDouble(userDeposit)){
	        		checkDesc=checkDesc+"  @  用户余额不足以支付过户需要的光猫押金！余额【"+Double.parseDouble(userDeposit)/100+"】元；光猫押金【"+mfee/100+"】元。";
	        	}
	        }else{
	        	checkDesc=checkDesc+"  @  用户账户余额不足以支付过户需要的光猫押金。余额【"+Double.parseDouble(userDeposit)/100+"】元；光猫押金【"+mfee/100+"】元。";
	        }
        }
        
        if(checkDesc!=null && !"".equals(checkDesc)){
        	isCheck="0";
        	isCheckDec="不允许办理";
        }else{
        	isCheck="1";
        	isCheckDec="允许办理";
        }
        IData info = new DataMap();
        info.put("SERIAL_NUMBER_PRE", serial_number_a);
        info.put("IS_CHECK", isCheck);
        info.put("IS_CHECK_DEC", isCheckDec);
        info.put("CHECK_DESC", checkDesc);
        info.put("ACCT_ID_CHANGE", acctId);
        info.put("CUST_ID_NEW", custId);
        info.put("USER_ID_NEW", userId);
        return info;
    }
    /**
     * 原始号码校验、获取宽带、营销活动、光猫等信息
     * */
    public IData checkOldNumForChangeUser(IData input) throws Exception
    {
    	IData rtnData=new DataMap();
        IData param = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER"); 
       
        // 查询手机号码的宽带用户信息
        IDataset userInfo = WidenetInfoQry.getUserInfo(serialNumber);
        if (userInfo == null || userInfo.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户尚未开通宽带业务，不能进行宽带帐号变更！");
        }
        String userId = userInfo.getData(0).getString("USER_ID");//宽带USER_ID
        
        String wSerialNumber = "KD_" + serialNumber;
        IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String wUserId = wUserInfo.getString("USER_ID");
        
        //查询手机号是否存在老的包年宽带信息
        IData inparam=new DataMap();
        inparam.put("KD_SERIAL_NUMBER", wSerialNumber);
        IDataset oldList=qryOldWideDiscnt(inparam);
        if(oldList!=null && oldList.size()>0){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户存在老的包年套餐，不能进行宽带帐号变更！");
        }
        
        // 用户宽带资料
        IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户宽带信息出错：无记录！");
        }
        param.put("USER_ID_WIDE", userId);
        // 判断宽带状态: --- 可以通过配置 td_s_svcstate_trade_limit 表, 只配置1,5, 4种业务类型8条记录
        // 外加一个规则: --- WideChangeUserCheckBean.java
        // boolean statusFlag = userQuery.queryWideNetOpenStatus(pd, userInfo);
        // if (statusFlag) {
        // common.error("该宽带用户为非开通状态，不能进行宽带帐号变更！");
        // }
        // 判断是否有未完工的工单-- j2ee默认就有判断
        // 查询主账号下所有子账号
        IDataset allAcct = RelaUUInfoQry.getAllSubAcct(userId, "77");
        if (allAcct != null && allAcct.size() > 0)
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "此号码属于平行帐号关系，不能变更帐号");

        IDataset allAcctF = RelaUUInfoQry.getAllSubAcct(userId, "78");
        if (allAcctF != null && allAcctF.size() > 0)
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "此号码属于家庭帐号关系，不能变更帐号");
        // 查询生效的优惠
        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
        if (discntInfo == null || discntInfo.size() <= 0)
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户在首月免费期中，不能变更帐号");
        // 判断是不是存在特殊优惠
        else
        {
            for (int i = 0; i < discntInfo.size(); i++)
            {
                String disCode = ((IData) discntInfo.get(i)).getString("DISCNT_CODE");

                IDataset exit = FastAuthApproveQry.queryCommByAttrCode("CSM", "640", disCode, CSBizBean.getTradeEparchyCode());
                if (exit != null && exit.size() > 0)
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "您的优惠不允许办理变更帐号业务！");
            }
        }
        // 获取用户指定业务最近一次办理记录, 查看是否办理过宽带过户
//        IData resultData = TradeHistoryInfoQry.getTradeInfoByUserTrade(userId, input.getString("TRADE_TYPE_CODE"));
//        if (IDataUtil.isNotEmpty(resultData) && "1".equals(resultData.getString("X_RECORDNUM")))
//        {
//            CSAppException.apperr(CrmCommException.CRM_COMM_103, "本月已经变更一次帐号，不能再次办理!");
//        }
        //判断是否已经存在未完工的宽带信息
        IDataset tradeList=WideChangeUserCheckBean.CheckIsExistNotFinishedTradeOldSn("KD_"+serialNumber);
        if(tradeList!=null && tradeList.size()>0){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该宽带号码【KD_"+serialNumber+"】存在宽带过户未完工工单，不允许再次办理。");
        }
        
        String curMonth=SysDateMgr.getCurMonth();
        boolean isChangeOwner=TradeUserInfoQry.queryWideNetUserIsChangeOwner(userId, curMonth);
        if(isChangeOwner){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "本月已经变更一次帐号，不能再次办理!");
        }
         
        IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(wUserId, "51");//biz_type_code=51为互联网电视类的平台服务
        if (IDataUtil.isNotEmpty(platSvcInfos))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户当前存在生效的魔百和平台业务，不能再办理。");
        }
        
        IDataset platSvcInfostow = UserPlatSvcInfoQry.getPlatSvcByUserBizType(wUserId, "86");//biz_type_code=86为互联网电视类的平台服务
        if (IDataUtil.isNotEmpty(platSvcInfostow))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户当前存在生效的魔百和平台业务，不能再办理。");
        }
		 
		rtnData=dataset.getData(0);
		//取宽带用户优惠信息
		IDataset discntList=getWideUserDiscntInfos(userId);
		if(discntList!=null && discntList.size()>0){
			String discntCode=discntList.getData(0).getString("DISCNT_CODE",""); 
			IDataset offerInfos =UpcCall.queryOfferNameByOfferCodeAndType("D",discntCode);
			if(offerInfos!=null && offerInfos.size()>0){  
				rtnData.put("DISCNT_NAME",offerInfos.getData(0).getString("OFFER_NAME",""));//优惠商品名称
				rtnData.put("DISCNT_START_DATE",discntList.getData(0).getString("START_DATE",""));
				rtnData.put("DISCNT_END_DATE",discntList.getData(0).getString("END_DATE",""));
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"无法找到offer_code=["+discntCode+"]对应的商品信息。");
			}
		}
		//FTTH取光猫信息
		String widetype=rtnData.getString("RSRV_STR2");
		if("3".equals(widetype) || "5".equals(widetype) )//统一了typecode，只能用这个来判断宽带类型了
        {
			IData info = getModemInfo(serialNumber);
			rtnData.put("MODEM_CODE", info.getString("MODEM_CODE",""));
			rtnData.put("MODEM_FEE", info.getString("MODEM_FEE",""));
			rtnData.put("MODEM_DEPOSIT", info.getString("MODEM_DEPOSIT",""));
			rtnData.put("MODEM_MODE", info.getString("MODEM_MODE",""));
			rtnData.put("OTHER_DATA", info.getString("OTHER_DATA",""));//OTHER表是否存在数据
			
			rtnData.put("MODEM_FEE_STATE", info.getString("MODEM_FEE_STATE",""));
			rtnData.put("MODEM_START_DATE", info.getString("MODEM_START_DATE",""));
			rtnData.put("MODEM_END_DATE", info.getString("MODEM_END_DATE",""));
			
			rtnData.put("MODEM_MODE_NAME",info.getString("MODEM_MODE_NAME","")); 
			rtnData.put("MODEM_FEE_STATE_NAME",info.getString("MODEM_FEE_STATE_NAME",""));
			 
        }else{
        	rtnData.put("OTHER_DATA", "0");//OTHER表是否存在数据
        }
		
		//取营销活动
		String snUserId="";
		 
		IDataset ucaInfos = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber) ;
		if(ucaInfos!=null && ucaInfos.size()>0){
			snUserId=ucaInfos.getData(0).getString("USER_ID");
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据手机号【"+serialNumber+"】查不到用户信息，可能数据有误！");
		}
		
		IDataset futureActs = getSaleActiveFutureInfo(snUserId);
		if(futureActs!=null && futureActs.size()>0){
			String errProdName=futureActs.getData(0).getString("PRODUCT_NAME","");
			String errPackName=futureActs.getData(0).getString("PACKAGE_NAME","");
			String errStartDate=futureActs.getData(0).getString("START_DATE","").substring(0,10);
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户存在未生效的宽带活动【"+errProdName+"-"+errPackName+"】，开始日期【"+errStartDate+"】。目前无法办理过户，请先办理取消业务！");
        } 
		/**
		 * 算了， 这里还是得循环获取金额,如果用户存在续约或者产品变更还未生效的套餐，
		 * 就会存在多条有效的营销活动。 否则计算不正确
		 */
		String remainMon="0";
		String remainFee ="0";
		IDataset actives=getSaleActiveInfo(snUserId);
		for(int i=0; i< actives.size(); i++){
			log.debug("------------***cxy***--------actives="+actives);
			rtnData.put("SALE_ACTIVE_TAG", "1"); 
			String productId=actives.getData(i).getString("PRODUCT_ID","");
			String packageId=actives.getData(i).getString("PACKAGE_ID","");
			String productName=actives.getData(i).getString("PRODUCT_NAME","");
			String packageName=actives.getData(i).getString("PACKAGE_NAME","");
			String startDate=actives.getData(i).getString("START_DATE","");
			String endDate="";//取优惠的
			String monFee="";//每月金额
			
			//取当前有效的优惠（未来的不要）用于计算
			IData inparams=new DataMap();
			inparams.put("USER_ID", snUserId);
			inparams.put("PRODUCT_ID", productId);
			inparams.put("PACKAGE_ID", packageId); 
			
			IDataset discnts=getDiscntInfo(inparams);
			String paraCode3="0";//配置的金额，如果为0则不需要计算每月金额。
			
			//妈蛋的，不能整除12，搞几把毛线
			String paraCode8="";
			String paraCode9="";
			String paraCode10="";
			if(discnts!=null && discnts.size()>0){
				inparams.put("DISCNT_CODE", discnts.getData(0).getString("PARAM_CODE")); 
				paraCode3=discnts.getData(0).getString("PARA_CODE3");
				paraCode8=discnts.getData(0).getString("PARA_CODE8","");
				paraCode9=discnts.getData(0).getString("PARA_CODE9","");
				paraCode10=discnts.getData(0).getString("PARA_CODE10","");
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户参加的宽带营销活动没有配置到相关参数commpara表7112.请配置后处理。");
			}
			
			
			IDataset userDiscnts=qryOldSaleActiveDiscnt(inparams);//宽带1+的不需要计算每月金额
			
			if(userDiscnts!=null && userDiscnts.size()>0){
				endDate=userDiscnts.getData(0).getString("END_DATE",""); 
				//IDataset feeInfos=getSaleActiveTradeFeeInfo(productId,packageId); 
				//查询营销包下面所有默认必选元素费用
				if(paraCode3!=null && !"0".equals(paraCode3)){
					int activeFee=0;
			    	IDataset feeInfos = WideNetUtil.getWideNetSaleAtiveTradeFee(productId, packageId); 
			    	log.debug("------------***cxy***--------feeInfos="+feeInfos);
			        for(int j = 0 ; j < feeInfos.size() ; j++)
			        {
			             IData feeData = feeInfos.getData(j);
			             log.debug("------------***cxy***--------feeData["+j+"]="+feeData);
			            String payMode = feeData.getString("PAY_MODE");
			            String feeMode = feeData.getString("FEE_MODE");
			            String fee = feeData.getString("FEE");
			            
			            if(fee != null && !"".equals(fee) && Integer.parseInt(fee) >0 &&  "2".equals(feeMode))
			            { 
			            	 
			            	activeFee += Integer.parseInt(fee);
			            } 
			        }
			        log.debug("------------***cxy***--------activeFee="+activeFee);
					if(activeFee>0){
						//取剩余月份和金额 
						if("69908016".equals(productId)){//候鸟是4个月
							monFee=""+activeFee/4;
						}else if("67220429".equals(productId)){//学期包是6个月
							monFee=""+activeFee/6;
						}else if("67220428".equals(productId)){//包年的是12个月
							monFee=""+activeFee/12;
							if("1".equals(paraCode8))
							{
								int currentUseMonths = 0;
								String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
                                currentUseMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(firstDayOfNextMonth,"yyyy-MM-dd","yyyyMM"));
                                if(currentUseMonths<=11)
                                {
                                	monFee = ""+paraCode9;
                                }else
                                {
                                	monFee = ""+paraCode10;
                                }
							}
							
						}
						
						String today = SysDateMgr.getSysDate();
						today = SysDateMgr.getDateNextMonthFirstDay(today); 
						IData feeData=WideChangeUserCheckBean.getRemainMonFee(SysDateMgr.getDateNextMonthFirstDay(endDate),today,String.valueOf(activeFee));
						String mon=feeData.getString("REMAIN_MON","0");
						String fee =feeData.getString("REMAIN_FEE","0");
						remainMon=""+(Integer.parseInt(remainMon)+Integer.parseInt(mon));
						remainFee=""+(Integer.parseInt(remainFee)+Integer.parseInt(fee));
					}
				}
			}
			
			
			rtnData.put("SALE_ACTIVE_NAME",productName+"—"+packageName);  
			rtnData.put("PRODUCT_ID",productId);
			rtnData.put("PACKAGE_ID",packageId);
			rtnData.put("ACTIVE_START_DATE",startDate);
			rtnData.put("ACTIVE_END_DATE",endDate);
			rtnData.put("ACTIVE_REMAIN_MON",remainMon);                  
			rtnData.put("ACTIVE_REMAIN_FEE",remainFee);
			rtnData.put("ACTIVE_EVERY_MON_FEE",monFee);
			rtnData.put("ACT_REMAIN_FEE",Double.parseDouble(remainFee)/100);
			log.debug("------------***cxy***--------rtnData="+rtnData);
		}
		
        return rtnData;
    }
    
    /**
     * 取剩余月份，剩余费用
     * */
    public static IData getRemainMonFee(String endDate,String startDate,String fee)throws Exception{
    	IData rtnData=new DataMap();
      
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1=Calendar.getInstance();
		Calendar c2=Calendar.getInstance();
		c1.setTime(sdf.parse(startDate));
		c2.setTime(sdf.parse(endDate));
		int year1=c1.get(Calendar.YEAR);
		int month1=c1.get(Calendar.MONTH);
		int year2=c2.get(Calendar.YEAR);
		int month2=c2.get(Calendar.MONTH);
		int remainMon=0; 
        if(year1==year2){
        	remainMon=month2-month1;
        }else{
        	remainMon=(year2-year1)*12+(month2-month1);
        }
        int monFee=Integer.parseInt(fee)/12;
        int remainFee=monFee*remainMon;
        rtnData.put("MONTH_FEE", monFee);
        rtnData.put("REMAIN_MON", remainMon);
        rtnData.put("REMAIN_FEE", remainFee);
        return rtnData;
    }
    
    /**
	 * 获取宽带营销活动信息
	 */
	public static IDataset getSaleActiveInfo(String snUserId) throws Exception
	{
		IData inparam = new DataMap();
        inparam.put("USER_ID", snUserId); 
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_WIDE_ACTIVE_BY_USER_ID", inparam);
	}
	
	/**
	 * 获取未生效宽带营销活动
	 * 可能是产品变更、或者续约后未生效的。
	 * 当前要拦截，去宽带产品预约变更取消后才能过户。
	 */
	public static IDataset getSaleActiveFutureInfo(String snUserId) throws Exception
	{
		IData inparam = new DataMap();
        inparam.put("USER_ID", snUserId); 
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_WIDE_ACTIVE_FUTURE_BY_USER_ID", inparam);
	}
	
	 /**
	 * 获取宽带营销活动费用的信息
	 */
	public static IDataset getSaleActiveTradeFeeInfo(String productId,String packageId) throws Exception
	{
		IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId); 
        inparam.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_WIDE_ACTIVE_FEEINFO_BY_PRODUCT", inparam,Route.CONN_CRM_CEN);
	}
    /**
	 * 根据原手机号查看TF_F_TRADE表的RSRV_STR1是否存在
	 * 即原手机号是否存在未完工的宽带转移信息
	 */
	public static IDataset CheckIsExistNotFinishedTradeOldSn(String serialNumber) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_WIDE_BY_SN", inparams,Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
	
	/**
	 * 根据原手机号查看TF_F_USER_DISCNT信息
	 */
	public static IDataset getWideUserDiscntInfos(String kdUserId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("KD_USER_ID", kdUserId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_WIDE_DISCNT_INFO", inparams);
	}
	
	//FTTH取光猫信息
	public static IData getModemInfo(String serialNumber)throws Exception
	{ 
		IData info = new DataMap();
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber);
    	
    	IDataset userinfo =  CSAppCall.call( "SS.DestroyUserNowSVC.getUserInfoBySerailNumber", param);
    	
    	if(!userinfo.isEmpty())
    	{
    		if(!userinfo.first().getString("RSRV_STR10","").equals("BNBD"))
    		{
    			//IDataset userOtherinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryUserOtherInfo", userinfo.first());
    			IDataset userOtherinfo =  CSAppCall.call( "SS.DestroyUserNowSVC.queryUserModemRent", userinfo.first());
        		if(!userOtherinfo.isEmpty())
        		{
        			info.put("MODEM_CODE", userOtherinfo.getData(0).getString("RSRV_STR1", ""));
        			info.put("MODEM_FEE", userOtherinfo.getData(0).getString("RSRV_STR2", "0"));
        			info.put("MODEM_MODE", userOtherinfo.getData(0).getString("RSRV_TAG1", "0"));
        			info.put("OTHER_DATA", "1");

        			info.put("MODEM_FEE_STATE", userOtherinfo.getData(0).getString("RSRV_STR7", "0"));
        			info.put("MODEM_START_DATE", userOtherinfo.getData(0).getString("START_DATE", "0"));
        			info.put("MODEM_END_DATE", userOtherinfo.getData(0).getString("END_DATE", "0"));
        		}else{
        			info.put("MODEM_MODE", "-1");
        			info.put("MODEM_FEE_STATE", "-1");
        			info.put("OTHER_DATA", "-1");
        		}
    		}
    		else
    		{
    			IDataset userOtherinfo =  CSAppCall.call("SS.DestroyUserNowSVC.queryGroupUserOtherInfo", userinfo.first());
        		if(!userOtherinfo.isEmpty())
        		{
        			info.put("MODEM_CODE", userOtherinfo.getData(0).getString("RSRV_STR1", ""));
        			info.put("MODEM_FEE", userOtherinfo.getData(0).getString("RSRV_STR2", "0"));
        			info.put("MODEM_MODE", userOtherinfo.getData(0).getString("RSRV_TAG1", "0"));
        			info.put("OTHER_DATA", "1");
        			
        			info.put("MODEM_FEE_STATE", userOtherinfo.getData(0).getString("RSRV_STR7", "0"));
        			info.put("MODEM_START_DATE", userOtherinfo.getData(0).getString("START_DATE", "0"));
        			info.put("MODEM_END_DATE", userOtherinfo.getData(0).getString("END_DATE", "0"));
        		}else{
        			info.put("MODEM_MODE", "-1");
        			info.put("MODEM_FEE_STATE", "-1");
        			info.put("OTHER_DATA", "-1"); 
        		}
    		}
    	}
    	String modemFee=info.getString("MODEM_FEE","");
    	if(modemFee!=null && !"".equals(modemFee) && !"0".equals(modemFee)){
    		info.put("MODEM_DEPOSIT", Integer.parseInt(modemFee)/100);
    	}else{
    		info.put("MODEM_FEE", "0");
    		info.put("MODEM_DEPOSIT", "0");
    	}
    	String modemmode=info.getString("MODEM_MODE","");
    	if ("0".equals(modemmode))
    	{
    		info.put("MODEM_MODE_NAME","租赁");
    	}else if ("1".equals(modemmode))
    	{
    		info.put("MODEM_MODE_NAME","购买");
    	}else if ("2".equals(modemmode))
    	{
    		info.put("MODEM_MODE_NAME","赠送");
    	}else if ("3".equals(modemmode))
    	{
    		info.put("MODEM_MODE_NAME","自备");
    	}else if ("-1".equals(modemmode))
    	{
    		info.put("MODEM_MODE_NAME","未申领光猫");
    	}
    	
    	//押金状态
    	String feestate=info.getString("MODEM_FEE_STATE","");
    	if ("0".equals(feestate))
    	{
    		info.put("MODEM_FEE_STATE_NAME","正常");
    	}else if ("1".equals(feestate))
    	{
    		info.put("MODEM_FEE_STATE_NAME","已转移");
    	}else if ("2".equals(feestate))
    	{
    		info.put("MODEM_FEE_STATE_NAME","已退还");
    	}else if ("3".equals(feestate))
    	{
    		info.put("MODEM_FEE_STATE_NAME","已沉淀");
    	}else if ("-1".equals(feestate))
    	{
    		info.put("MODEM_FEE_STATE_NAME","无押金状态");
    	}
    	
    	return info;
	}
	
	/**
	 * 根据原手机号查看TF_F_USER_DISCNT信息
	 */
	public static String getNewUserAcctDeposit(String newSn) throws Exception
	{
		String DEPOSIT_BALANCE="0";//存折余额
		//3、获取默认账户  （acct_id)		
    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(newSn);
    	String acctId=accts.getData(0).getString("ACCT_ID");
    	IData param = new DataMap();
    	param.put("ACCT_ID", acctId); 
    	/**调用账务查询接口*/
    	IDataset checkCash= AcctCall.queryAcctDeposit(param); 
    	
    	String allCash="0";
    	if(checkCash!=null && checkCash.size()>0){
    		for(int j=0;j<checkCash.size();j++){
    			IData acctInfo=checkCash.getData(j);
	    		String DEPOSIT_CODE=acctInfo.getString("DEPOSIT_CODE");//存折编码
	    		
	    		if("0".equals(DEPOSIT_CODE)){
	    			DEPOSIT_BALANCE=""+(Integer.parseInt(DEPOSIT_BALANCE)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
	    		}
    		}
    	}
    	return DEPOSIT_BALANCE;
	}
	
	
	public static IDataset qryOldWideDiscnt(IData inparams) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t1.*  from tf_F_user t,tf_F_user_discnt t1  ");
        parser.addSQL(" where t.user_id=t1.user_id  ");
        parser.addSQL(" and t.serial_number=:KD_SERIAL_NUMBER  ");
        parser.addSQL(" and t1.discnt_code in (36120005,36120006,36120007,36120008,36120014,36120015,36120016,36120017)   ");
        parser.addSQL(" and t.remove_tag='0'  ");
        parser.addSQL(" and sysdate < t1.end_date  ");
    	return Dao.qryByParse(parser);
    }
	
	/**
	 * 获取当前有效的营销活动优惠，未来的不要
	 * */
	public static IDataset qryOldSaleActiveDiscnt(IData inparams) throws Exception
    {
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t.*  from tf_F_user_discnt t ");
        parser.addSQL(" where t.user_id=:USER_ID  "); 
        parser.addSQL(" and t.DISCNT_CODE=:DISCNT_CODE ");
        parser.addSQL(" and SYSDATE between t.start_date and t.end_date  ");
    	return Dao.qryByParse(parser);
    }
	
	public static IDataset getDiscntInfo(IData inparams) throws Exception{
		 SQLParser parser = new SQLParser(inparams); 
	     parser.addSQL(" select t.* from td_s_commpara t   ");
	     parser.addSQL(" where t.subsys_code='CSM'  ");
	     parser.addSQL(" AND T.PARAM_ATTR='7112'  ");
	     parser.addSQL(" AND T.PARA_CODE1=:PACKAGE_ID  ");
	     parser.addSQL(" AND T.PARA_CODE2=:PRODUCT_ID  ");
	     parser.addSQL(" AND SYSDATE < T.END_DATE  ");
	     parser.addSQL(" and T.PARA_CODE2 in (67220428,67220429,69908016,69908001,69908015) "); 
	     return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}
	
	
	/**
     * 插临时表
     * */
    public static void insertWideChangeUserTempInfo(IData param) throws Exception{
    	IData inParam=new DataMap();
        inParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER_PRE",""));
        inParam.put("IN_DATE", SysDateMgr.getSysDateYYYYMMDD());
    	IDataset tempList=WideChangeUserCheckBean.qryWideChangeUserTempInfo(inParam);
    	if(tempList!=null && tempList.size()>0){}
    	else{
	    	DBConnection conn = null;
	    	try 
			{
	    		conn = SessionManager.getInstance().getAsyncConnection("cen1"); 
	    		IData insData=new DataMap();
		    	insData.put("SERIAL_NUMBER_OLD", param.getString("SERIAL_NUMBER","")); 
	        	insData.put("SERIAL_NUMBER_NEW", param.getString("SERIAL_NUMBER_PRE","")); 
	        	insData.put("IN_DATE",SysDateMgr.getSysDateYYYYMMDDHHMMSS());   
	        	insData.put("STATUS","1");         
	        	  
	        	BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
				dao.insert(conn, "TL_B_WIDECHANGE_TEMPINFO", insData);
			 
		    	conn.commit();
			}
	    	catch (Exception e1) 
			{ 
				if(conn != null)
				{
					conn.rollback();
				}			
				CSAppException.appError("2017", e1.getMessage());
			} 
			finally 
			{
				if(conn != null)
				{
					conn.close();
				}
			}
    	} 
    }
    /**
     * 查询临时表
     * */
    public static IDataset qryWideChangeUserTempInfo(IData inparams) throws Exception{
		 SQLParser parser = new SQLParser(inparams); 
	     parser.addSQL(" select t.* from TL_B_WIDECHANGE_TEMPINFO t   ");
	     parser.addSQL(" where t.STATUS='1'  ");
	     parser.addSQL(" AND T.SERIAL_NUMBER_NEW=:SERIAL_NUMBER  ");
	     parser.addSQL(" AND to_char(T.IN_DATE,'yyyymmdd')=:IN_DATE  "); 
	     return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}
    
    /**
     * 更新临时表
     * */
    public static void updWideChangeUserTempInfo(IData param) throws Exception{
    	IData inParam=new DataMap();
        inParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER_PRE",""));
        inParam.put("IN_DATE", SysDateMgr.getSysDateYYYYMMDD());
        inParam.put("STATUS", param.getString("STATUS","9"));
    	IDataset tempList=WideChangeUserCheckBean.qryWideChangeUserTempInfo(inParam);
    	if(tempList!=null && tempList.size()>0){}
    	else{
	    	DBConnection conn = null;
	    	try 
			{
	    		conn = SessionManager.getInstance().getAsyncConnection("cen1");  
	    		StringBuilder sql = new StringBuilder(1000); 
	        	
	        	sql.append(" update TL_B_WIDECHANGE_TEMPINFO t ");
	        	sql.append(" set t.STATUS=:STATUS ");
	        	sql.append(" where t.STATUS='1'  ");
	        	sql.append(" AND T.SERIAL_NUMBER_NEW=:SERIAL_NUMBER  ");
	        	sql.append(" AND to_char(T.IN_DATE,'yyyymmdd')=:IN_DATE  "); 
	        	  
	        	BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
				dao.executeUpdate(conn, String.valueOf(sql), inParam);
			 
		    	conn.commit();
			}
	    	catch (Exception e1) 
			{ 
				if(conn != null)
				{
					conn.rollback();
				}			
				CSAppException.appError("2017", e1.getMessage());
			} 
			finally 
			{
				if(conn != null)
				{
					conn.close();
				}
			}
    	} 
    }
}
