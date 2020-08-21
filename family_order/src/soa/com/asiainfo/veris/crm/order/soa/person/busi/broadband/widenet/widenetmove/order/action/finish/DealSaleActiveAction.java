
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveBean;

/**
 * 宽带回单营销活动处理
 * 此处为移机处理，从开户复制过来
 * 
 * @author chenzm
 */
public class DealSaleActiveAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeId = mainTrade.getString("TRADE_ID");
        String orderId = mainTrade.getString("ORDER_ID");
        String rsrvStr9 = mainTrade.getString("RSRV_STR9");
        
        //rsrvStr9为1代表有需要中止的营销活动
        String mobileSeri = "";
        if(serialNumber.startsWith("KD_1")){
        	mobileSeri = serialNumber.substring(3);
    		//input.put("SERIAL_NUMBER", serialNumber);
    	}else{
    		return ;
    	}
    	IDataset userInfoA = UserInfoQry.getUserInfoBySerailNumber("0", mobileSeri);
    	String seriUserId = userInfoA.getData(0).getString("USER_ID");
    	
    	//查询用户是否有生效的营销活动
    	IData effActive = new DataMap();
    	IDataset kdAct1 = new DatasetList();
    	if("1".equals(rsrvStr9)){
    		IDataset saleActive = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(seriUserId);
    		IDataset kdActives = BreQryForCommparaOrTag.getCommpara("CSM", 212, "606", CSBizBean.getUserEparchyCode());
    		kdAct1 = BreQryForCommparaOrTag.getCommpara("CSM", 212, "WIDE_YEAR_ACTIVE", CSBizBean.getUserEparchyCode());
            if (IDataUtil.isNotEmpty(kdAct1)) kdActives.addAll(kdAct1);
    		if ((saleActive != null && saleActive.size() > 0)&&(kdActives != null && kdActives.size() > 0))
            {
    			for (int i = 0; i < saleActive.size(); i++)
                {
                    IData element = saleActive.getData(i);
                    if("".equals(element.getString("CANCEL_DATE", "")))
                    {
                        for(int j=0;j<kdActives.size();j++){
                        	if(element.getString("PRODUCT_ID").equals(kdActives.getData(j).getString("PARA_CODE1"))){
                        		effActive = element;
                        	}
                        }
                    }
                }
            }
    	}
		
    	//营销活动终止时间为回单时间的月底
		String moveTradeId = "";
		if(orderId!=null&&!"".equals(orderId)){
			IDataset tradeInfos = TradeInfoQry.queryTradeByOrerId(orderId, "0");
        	if(IDataUtil.isNotEmpty(tradeInfos)){
        		for(int j=0;j<tradeInfos.size();j++){
        			if("606".equals(tradeInfos.getData(j).getString("TRADE_TYPE_CODE")))
        				moveTradeId = tradeInfos.getData(j).getString("TRADE_ID","");
        		}
        	}
		}
		
        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(moveTradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
        	CSAppException.apperr(WidenetException.CRM_WIDENET_14);
        }
    	String finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
    	
		String cancelTradeId = "";
		if(IDataUtil.isNotEmpty(effActive)){
			IData endActiveParam = new DataMap();
            endActiveParam.put("SERIAL_NUMBER", effActive.getString("SERIAL_NUMBER"));
            endActiveParam.put("PRODUCT_ID", effActive.getString("PRODUCT_ID"));
            endActiveParam.put("PACKAGE_ID", effActive.getString("PACKAGE_ID"));
            endActiveParam.put("RELATION_TRADE_ID", effActive.getString("RELATION_TRADE_ID"));
            endActiveParam.put("IS_RETURN", "0");
            endActiveParam.put("FORCE_END_DATE", SysDateMgr.getDateLastMonthSec(finishDate));
            endActiveParam.put("END_DATE_VALUE", "7"); //强制终止
            endActiveParam.put("WIDE_YEAR_ACTIVE_BACK_FEE", "1");
            endActiveParam.put("EPARCHY_CODE",CSBizBean.getUserEparchyCode());//认证方式
            //endActiveParam.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE"));
            //endActiveParam.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS"));

            IDataset saleActives = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
            cancelTradeId = saleActives.getData(0).getString("TRADE_ID", "");
            if(kdAct1!=null&&kdAct1.size()>0){
            	for(int i=0;i<kdAct1.size();i++){
            		if(effActive.getString("PRODUCT_ID").equals(kdAct1.getData(i).getString("PARA_CODE1"))){
            			dealYearActiveEndFee(finishDate, tradeId, effActive);
            			break;
            		}
            	}
            }
		}
		
        String productId = "";
        String packageId = "";
        IDataset saleActiveInfos = UserSaleActiveInfoQry.getBook2ValidSaleActiveByTradeId(tradeId, serialNumber);
        for (int i = 0, size = saleActiveInfos.size(); i < size; i++)
        {
            IData saleActiveInfo = saleActiveInfos.getData(i);
            serialNumber = saleActiveInfo.getString("SERIAL_NUMBER");
            productId = saleActiveInfo.getString("PRODUCT_ID_B");
            packageId = saleActiveInfo.getString("PACKAGE_ID_B");
            
//            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
//            String userId = userInfo.getString("USER_ID");
//            
//            IDataset validSaleActiveInfos = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, productId,packageId);
            UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", "");
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("PRODUCT_ID", productId);
            param.put("PACKAGE_ID", packageId);
            param.put("TRADE_STAFF_ID", "SUPERUSR");
            param.put("TRADE_DEPART_ID", "36601");
            param.put("TRADE_CITY_CODE", "HNSJ");
            //移机时，修改营销活动的生效时间为PBOSS回单时间的下月1号
            String bookDate = SysDateMgr.getFirstDayOfNextMonth(finishDate);
            param.put("BOOKING_DATE", bookDate);
            param.put("WIDENET_MOVE_SALEACTIVE_SIGN", "1");
            //标记是宽带开户营销活动
            param.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
            //营销活动转正式需要跳过工单互斥校验
            param.put("NO_TRADE_LIMIT", "TRUE");
            
            //add by zhangxing3 for REQ201810290024宽带开户界面增加手机号码套餐的判断
            param.put("NO_SALE_TRADE_LIMIT_642","TRUE");
            //add by zhangxing3 for REQ201810290024宽带开户界面增加手机号码套餐的判断
            
            IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
            String intfTradeId = result.getData(0).getString("TRADE_ID");

            if(cancelTradeId!=null&&!"".equals(cancelTradeId)){
            	MebCommonBean.regTradeLimitInfo(intfTradeId, cancelTradeId);
            }
            UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", intfTradeId);
        }
        
        //宽带提速，新增的gpon和ftth的4M宽带，自动绑定宽带1+活动
        //根据trade_id获取服务子台帐
        IDataset tradeSvcInfos = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
        for (int i = 0, size = tradeSvcInfos.size(); i < size; i++)
        {
        	IData tradeSvcInfo = tradeSvcInfos.getData(i);
        	String serviceId = tradeSvcInfo.getString("SERVICE_ID");
        	String modifyTag = tradeSvcInfo.getString("MODIFY_TAG");
        	//通过524的配置，查看是否有开通的服务需要绑定营销活动的情况
        	IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "524", serviceId, mainTrade.getString("TRADE_EPARCHY_CODE"));
        	//如果有，则进行绑定
        	if(IDataUtil.isNotEmpty(commparaInfos)&&("0".equals(modifyTag)))
        	{
        		IData commparaInfo = commparaInfos.getData(0);
        		
        		//给宽带用户的手机号码绑活动
        		if ("KD_".equals(serialNumber.substring(0, 3)))
                {
                    serialNumber = serialNumber.substring(3);
                }
        		
        		IData param = new DataMap();
	            param.put("SERIAL_NUMBER", serialNumber);
	            param.put("PRODUCT_ID", commparaInfo.getString("PARA_CODE1"));
	            param.put("PACKAGE_ID", commparaInfo.getString("PARA_CODE2"));
	            param.put("TRADE_STAFF_ID", "SUPERUSR");
	            param.put("TRADE_DEPART_ID", "36601");
	            param.put("TRADE_CITY_CODE", "HNSJ");
	            CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
        	}
        }

    }
    
    public void dealYearActiveEndFee(String finishDate, String tradeId, IData yearActive) throws Exception{
    	String productId = yearActive.getString("PRODUCT_ID");
    	String packageId = yearActive.getString("PACKAGE_ID");
    	String serialNumber = yearActive.getString("SERIAL_NUMBER");
    	String userId = yearActive.getString("USER_ID");
    	
        //如果是包年营销活动终止，此处需要对包年活动剩余的费用（宽带包年活动专项款存折（赠送）9023和 宽带包年活动专项款存折9021）赠送给用户
        //到（宽带包年活动变更预存回退存折（赠送） 宽带包年活动变更预存回退存折）
        //查询用户该存折（9021,9023）是否还有剩余的费用
        //计算专项存折的费用
    	int balance9021 = 0,balance9023 = 0;
    	IDataset allUserMoney = AcctCall.queryAccountDepositBySn(serialNumber);
        if(IDataUtil.isNotEmpty(allUserMoney)){
        	for(int i=0;i<allUserMoney.size();i++){
    			String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
                int balance2 = Integer.parseInt(balance1);
        		if("9021".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE"))){
                    balance9021 = balance9021 + balance2;
        		}
        		if("9023".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE"))){
        			balance9023 = balance9023 + balance2;
        		}
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
    	IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(userId);
    	if (IDataUtil.isNotEmpty(useDiscnts)){
    		for(int i=0;i<useDiscnts.size();i++){
    			if(productId.equals(useDiscnts.getData(i).getString("PRODUCT_ID", ""))&&packageId.equals(useDiscnts.getData(i).getString("PACKAGE_ID", ""))){
    				String startDate = useDiscnts.getData(i).getString("START_DATE", "");
    				endMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(finishDate,"yyyy-MM-dd HH:mm:ss","yyyyMM")) + 1;
    				//endMonths = SysDateMgr.monthIntervalYYYYMM(startDate, saleactiveEndReqData.getEndDate()) + 1;
    			}
    		}
    	}

        //查看包年活动周期
    	int activeMonth = 12;
    	boolean oldmd = false;
        IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", packageId, null);
        if(IDataUtil.isNotEmpty(com181))
        {
        	activeMonth = Integer.parseInt(com181.getData(0).getString("PARA_CODE4", "12"));
        	//老的包年套餐是388、588
            if("1".equals(com181.getData(0).getString("PARA_CODE7","")))
            {
            	oldmd = true;
            }
        }
    	if(endMonths == activeMonth) return ;
    	
    	

    	//计算包年活动需要转账赠送或沉淀的预存
    	int yearFee = Integer.parseInt(activeYearFee);
    	int backFee = yearFee - yearFee/activeMonth*endMonths;
    	
    	if(oldmd)
    	{
    		if(endMonths<=11)
    		{
    			backFee = yearFee - Integer.parseInt(com181.getData(0).getString("PARA_CODE5","0"))*endMonths;
    		}else
    		{
    			backFee =0;
    		}
    	}
    	
    	if((backFee!=(balance9021+balance9023))||(balance9021<=0&&balance9023<=0)){
    		String feeFlag = "1";
    		if(backFee<(balance9021+balance9023)) feeFlag = "2";
    		if(backFee>(balance9021+balance9023)) feeFlag = "3";
    		if(balance9021<=0&&balance9023<=0) feeFlag = "0";
    		
    		StringBuilder buf = new StringBuilder();
    		IData param = new DataMap();
	    	param.put("TRADE_ID",tradeId);
	    	param.put("REMARK",feeFlag);
            buf.append(" UPDATE TF_B_TRADE T ");
            buf.append(" SET T.REMARK=:REMARK ");
            buf.append(" WHERE T.CANCEL_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
            Dao.executeUpdate(buf, param, Route.getJourDb(CSBizBean.getUserEparchyCode()));
    	}
    	
    	String strRemark = backFee +"," + balance9021 + "," + balance9023;
    	
    	//由于预销账，多销一个月的费用,故以营业计算的为准 。9021比9023优先级高
    	if(backFee<(balance9021+balance9023)){ //如果营业计算的剩余值小于两个存折返回来的剩余值，则于营业计算的值为准
    		if(backFee<balance9021){ //9021存在的钱比营业计算剩余的钱还大的话，只返9021存折就够了。
    			balance9021 = backFee;
    			balance9023 = 0;
    		}else if(backFee>=balance9021){ //如果剩余的钱比9021存折多，则全返9021存折的，剩余部分返9023的
    			if((backFee - balance9021)<balance9023){
    				balance9023=backFee - balance9021;
    			}
    		}
    	}else{ //如果账务那边返回的值比营业还小，则先预销账一个月
    		balance9023 = balance9023 - yearFee/activeMonth;
    		if(balance9023< 0){
    			balance9021 = balance9021 + balance9023;
    		}
    	}
    	
    	strRemark = strRemark + "计算后：" + balance9021 + "," + balance9023;
    	
    	if(balance9021<=0&&balance9023<=0) return;
    	
    	//调用账务接口
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER_1", serialNumber);
    	param.put("SERIAL_NUMBER_2", serialNumber);
    	param.put("REMARK",  strRemark);
    	if(balance9021>0){
        	param.put("DEPOSIT_CODE_1", "9021");
        	param.put("DEPOSIT_CODE_2", "9022");
        	param.put("FEE", balance9021);
        	try{
        		IData backEnds = AcctCall.depositeToPhoneMoney(param);
        		if(IDataUtil.isNotEmpty(backEnds)&&("0".equals(backEnds.getString("RESULT_CODE",""))||"0".equals(backEnds.getString("X_RESULTCODE","")))){
        		// 成功！ 处理other表
        		}else{
        			CSAppException.appError("61312", "调用接口AM_CRM_TransFee转存(宽带包年活动专项款存折)错误:"+"入参：【"+param.toString() + "】" + backEnds.getString("X_RESULTINFO"));
        		}
        	} catch (Exception e){
        		CSAppException.appError("61313", "调用接口AM_CRM_TransFee转存(宽带包年活动专项款存折)错误:"+"入参：【"+param.toString() + "】" + e.getStackTrace());
    		}
    	}
    	if(balance9023>0){
        	param.put("DEPOSIT_CODE_1", "9023");
        	param.put("DEPOSIT_CODE_2", "9024");
        	param.put("FEE", balance9023);
        	try{
        		IData backEnds = AcctCall.depositeToPhoneMoney(param);
        		if(IDataUtil.isNotEmpty(backEnds)&&("0".equals(backEnds.getString("RESULT_CODE",""))||"0".equals(backEnds.getString("X_RESULTCODE","")))){
        		// 成功！ 处理other表
        		}else{
        			CSAppException.appError("61312", "调用接口AM_CRM_TransFee转存(宽带包年活动专项款存折（赠送）)错误:"+"入参：【"+param.toString() + "】"+backEnds.getString("X_RESULTINFO"));
        		}
        	} catch (Exception e){
        		CSAppException.appError("61313", "调用接口AM_CRM_TransFee转存(宽带包年活动专项款存折)错误:"+"入参：【"+param.toString() + "】" + e.getStackTrace());
    		}
    	}
    }

}
