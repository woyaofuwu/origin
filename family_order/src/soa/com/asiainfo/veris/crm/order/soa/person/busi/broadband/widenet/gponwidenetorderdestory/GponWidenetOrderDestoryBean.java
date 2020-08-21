package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.gponwidenetorderdestory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class GponWidenetOrderDestoryBean extends CSBizBean
{

	private static final long serialVersionUID = 1L;

	/**
     * 查询是否登记
     */
    public IDataset queryInfo(IData params) throws Exception
    {
    	IData tempParam = new DataMap();
    	String userId = params.getString("USER_ID");
    	tempParam.put("USER_ID", userId);
        IDataset result = Dao.qryByCode("TF_F_USER_GPON_DESTROY", "SEL_DESTORY_ORDER_BY_USERID", tempParam);
        
    	IData returnInfo = new DataMap();

    	if(IDataUtil.isEmpty(result))
    	{
    		returnInfo.put("DESTORY_STATE", "未预约");
    		returnInfo.put("DESTORY_TIME", "");
    	}
    	else
    	{
    		String destoryTime = result.getData(0).getString("DESTROY_ORDER_TIME");
    		returnInfo.put("DESTORY_STATE", "已预约");
    		returnInfo.put("DESTORY_TIME", destoryTime);
    	}
    	
    	IDataset returnInfos = new DatasetList();
    	returnInfos.add(returnInfo);
    	
    	return returnInfos;
    }

    
    /**
     * 调拆机接口
     */
    public IDataset callGponDestroyService(IData params) throws Exception
    {
        String serialNumber = params.getString("SERIAL_NUMBER");
		String kdUserId = params.getString("KD_USER_ID");
		
        IData param = new DataMap();
        String tradeStaffId = params.getString("TRADE_STAFF_ID","SUPERUSR");
		String tradeDepartId = params.getString("TRADE_DEPART_ID","36601");
		String tradeCityCode = params.getString("TRADE_CITY_CODE","HNSJ");
		CSBizBean.getVisit().setStaffId(tradeStaffId);
	    CSBizBean.getVisit().setDepartId(tradeDepartId);
	    CSBizBean.getVisit().setCityCode(tradeCityCode);
		CSBizBean.getVisit().setLoginEparchyCode("0898");
		CSBizBean.getVisit().setStaffEparchyCode("0898");
		param.put("TRADE_STAFF_ID", tradeStaffId);
    	param.put("TRADE_DEPART_ID", tradeDepartId);
    	param.put("TRADE_CITY_CODE", tradeStaffId);
    	param.put("SERIAL_NUMBER", serialNumber);
    	param.put("SKIP_RULE", "TRUE");//新增标识，不进行规则判断

    	String widetype = params.getString("RSRV_TAG1","1");
    	if ("1".equals(widetype))
        {
    		param.put("TRADE_TYPE_CODE", "605");//605-GPON宽带拆机
        }else if ("3".equals(widetype))
        {
        	param.put("TRADE_TYPE_CODE", "605");//
        }else if ("2".equals(widetype) || "5".equals(widetype) || "6".equals(widetype))
        {
        	param.put("TRADE_TYPE_CODE", "605");
        }
        //FTTH宽带，取光猫串号
    	if ("3".equals(widetype) || "5".equals(widetype))
    	{
    		param.put("MODEM_FEE", params.getString("RSRV_STR2","0"));
    		param.put("MODEM_RETUAN", params.getString("RSRV_TAG2","0"));
    		param.put("MODEM_MODE", params.getString("RSRV_TAG3","0"));
    	}
    	param.put("WIDE_TYPE_CODE", widetype);//宽带类型
        
    	param.put("DESTROY_ORDER_TIME", params.getString("DESTROY_ORDER_TIME"));//预约时间
    	param.put("IS_PRE_DESTROY_ORDER", "Y"); //是否预约拆机
    	
        /**
         * REQ201609280002 宽带功能优化 chenxy3 2016-11-29
         * */
        param.put("DESTORYREASON", params.getString("RSRV_STR4","")); //销号原因
        param.put("REASONELSE", params.getString("RSRV_STR5","")); //销号原因-其他
        
        IDataset result = null;
        
        try
        {
        	IDataset userSet = UserInfoQry.getUserInfoBySn("KD_" + serialNumber,"0");
        	String NowUserId = "";
        	String rsrvStr1 ="";
        	boolean Flag = true;
        	if(IDataUtil.isNotEmpty(userSet)&&userSet.size()>0){
        		NowUserId = userSet.getData(0).getString("USER_ID");
        		if(kdUserId!=null && kdUserId!="" && !kdUserId.equals(NowUserId))
        		{
        			Flag = false;
        		}
        	}
        	if(Flag){
        		result = CSAppCall.call("SS.WidenetDestroyNewRegSVC.tradeReg", param);
        		rsrvStr1 = result.getData(0).getString("TRADE_ID","");
        	}else{
        		rsrvStr1 = "预约拆机用户的USER_ID和在网的用户USER_ID不一致，请联系系统管理员核对后处理！";
        	}
        	
    		updateInfos(kdUserId,rsrvStr1);
        }
        catch(Exception e)
        {
        	String rsrvStr1 = getErrorMsg(e.getMessage(),200);
        	updateInfos(kdUserId,rsrvStr1);
        	
        	//add by zhangxing3 for BUG20190107092636优化预约宽带拆机问题 start
        	//记录失败日志
            String errorMsg = Utility.getBottomException(e).getMessage();
            if(errorMsg != null && !"".equals(errorMsg))
            	if(errorMsg.length() > 2000)
            		errorMsg = errorMsg.substring(0,1999);
            IData logData = new DataMap();
            logData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            logData.put("OPER_ID", params.getString("RSRV_STR3",""));
            logData.put("STAFF_ID", "SUPERUSR");
            logData.put("OPER_MOD", "宽带预约拆机");
            logData.put("OPER_TYPE", "INS");
            logData.put("OPER_TIME", SysDateMgr.getSysTime());
            logData.put("OPER_DESC", errorMsg);
            logData.put("RSRV_STR1", kdUserId);
            logData.put("RSRV_STR2", serialNumber);
            logData.put("RSRV_STR3", "");
            
            Dao.insert("TL_B_CRM_OPERLOG", logData);
            //错误的关联拆机同时插入TF_B_ERROR_TRADE，aee调用重跑
            IData errorInfo = new DataMap();
            errorInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            errorInfo.put("RELA_TRADE_ID", params.getString("RSRV_STR3",""));
            errorInfo.put("TRADE_TYPE_CODE", "605");
            errorInfo.put("RELA_SERIAL_NUMBER", serialNumber);
            errorInfo.put("DEAL_NUM", "0");
            errorInfo.put("STATUS", "0");
            errorInfo.put("CREATE_TIME", SysDateMgr.getSysTime());
            errorInfo.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            errorInfo.put("CREATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            errorInfo.put("SVC_ID", "SS.WidenetDestroyNewRegSVC.tradeReg");
            errorInfo.put("IN_PARAM", param.toString());
            errorInfo.put("IN_MODE_CODE",CSBizBean.getVisit().getInModeCode());
            errorInfo.put("REMARK", errorMsg);
            
            Dao.insert("TF_B_ERROR_TRADE", errorInfo,Route.getJourDb());
            //add by zhangxing3 for BUG20190107092636优化预约宽带拆机问题 end
        }
        
        return result;
    }
    
    public static void updateInfos(String kdUserId, String rsrvStr1) throws Exception
	{
		IData cond = new DataMap();
        cond.put("KD_USER_ID", kdUserId);
        cond.put("RSRV_STR1", rsrvStr1);
        
        StringBuilder sql = new StringBuilder(200);
        sql.append(" UPDATE TF_F_USER_GPON_DESTROY ");
        sql.append(" SET RSRV_STR1 = :RSRV_STR1, RSRV_DATE1 = SYSDATE ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND KD_USER_ID = :KD_USER_ID ");
        //sql.append(" AND ACCEPT_MONTH = TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') ");
        sql.append(" AND TO_CHAR(DESTROY_ORDER_TIME,'YYYYMM') = TO_CHAR(SYSDATE,'YYYYMM') ");
        
        Dao.executeUpdate(sql, cond);
	}
    
    private String getErrorMsg(String msg, int length)
    {
        String error = "";
        byte[] bytes = msg.getBytes();
        if (bytes.length <= length)
        {
            error = msg;
        }
        else
        {
            byte[] newbytes = new byte[length];
            for (int i = 0; i < length; i++)
            {
                newbytes[i] = bytes[i];
            }
            error = new String(newbytes);
        }
        return error;
    }
    
    public IDataset queryWidenetInstallFee(IData input) throws Exception
    {
        IData retrunData = new DataMap();
       
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        //0.如果集团商务宽带，退出。
        if(serialNumber.length() > 14){
        	retrunData.put("INSTALL_FEE_TAG","0");
    		return IDataUtil.idToIds(retrunData);
    	}
        String serialNumberKD = "";
        String serialNumberMobile = "";
        if(serialNumber.startsWith("KD_")){
    		serialNumberKD = serialNumber;
    		serialNumberMobile = serialNumber.substring(3);

    	}
    	else
    	{
    		serialNumberMobile = serialNumber;
    		serialNumberKD = "KD_"+serialNumber;
    	}
        
        // 1.查询候鸟活动66002202，如果存在，则可能要收宽带装机费；不存在候鸟活动，直接退出
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumberMobile);
    	String seriUserId = userInfo.getData(0).getString("USER_ID");
    	String KdUserId = input.getString("USER_ID","");
    	IDataset saleActives = UserSaleActiveInfoQry.getHouNiaoSaleActiveByUserId(seriUserId);
		//System.out.println("=============queryWidnetInstallFee=============saleActives:"+saleActives);
    	String discntCode = "84018442";//度假宽带承诺在网套餐
    	IDataset userDiscntInfos = UserDiscntInfoQry.getAllDiscntByUD(KdUserId, discntCode);

    	
    	if (IDataUtil.isEmpty(saleActives) && IDataUtil.isEmpty(userDiscntInfos)){
    		retrunData.put("INSTALL_FEE_TAG","0");
    		return IDataUtil.idToIds(retrunData);
    	}
    	
    	// 2. 查询宽带用户开户时间
    	

    	IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", serialNumberKD);
		//System.out.println("=============queryWidnetInstallFee=============userInfos:"+userInfos);
    	if(IDataUtil.isEmpty(userInfos))
    	{
    		retrunData.put("INSTALL_FEE_TAG","0");
    		return IDataUtil.idToIds(retrunData);
    	}
    	String openDate= userInfos.getData(0).getString("OPEN_DATE","");
		String curDate = SysDateMgr.getSysDate();
		SysDateMgr.addMonths(openDate, 24);
		//System.out.println("=============queryWidnetInstallFee=============curDate:"+curDate);
		//System.out.println("=============queryWidnetInstallFee=============openDate:"+openDate);
		//System.out.println("=============queryWidnetInstallFee=============SysDateMgr.addMonths(openDate, 24):"+SysDateMgr.addMonths(openDate, 24));
		//System.out.println("=============queryWidnetInstallFee=============curDate.compareTo(SysDateMgr.addMonths(openDate, 24)):"+curDate.compareTo(SysDateMgr.addMonths(openDate, 24)));
		// 2.1宽带开户时间大于2年，直接退出，不再收宽带装机费
		if(curDate.compareTo(SysDateMgr.addMonths(openDate, 24)) > 0 )
		{
			retrunData.put("INSTALL_FEE_TAG","0");
		}
		else {
			retrunData.put("INSTALL_FEE_TAG","1");
		}
        
        return IDataUtil.idToIds(retrunData);
    }  
    
    public IDataset queryWidenetCommissioningFee(IData input) throws Exception
    {
        IData retrunData = new DataMap();
       
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        String modelType = input.getString("MODEL_TYPE","");
        String destroyTime = input.getString("DESTROY_TIME","");
        //0.如果集团商务宽带，退出。
        if(serialNumber.length() > 14){
        	retrunData.put("COMMISSIONING_FEE_TAG","0");
    		return IDataUtil.idToIds(retrunData);
    	}
        String serialNumberKD = "";
        String serialNumberMobile = "";
        if(serialNumber.startsWith("KD_")){
    		serialNumberKD = serialNumber;
    		serialNumberMobile = serialNumber.substring(3);

    	}
    	else
    	{
    		serialNumberMobile = serialNumber;
    		serialNumberKD = "KD_"+serialNumber;
    	}
        
        // 1.查询宽带调测费活动，如果存在，则可能要收宽带调测费；不存在，直接退出
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumberMobile);
    	String seriUserId = userInfo.getData(0).getString("USER_ID");
    	String KdUserId = input.getString("USER_ID","");
    	String saleProductId = "60828536";//光猫
    	if("TOP_SET_BOX".equals(modelType))
    	{
    		saleProductId = "66000308";//机顶盒
    	}
    	IDataset saleActives = UserSaleActiveInfoQry.getWidenetCommissioningFeeByUserId(seriUserId,saleProductId);
    	IDataset otherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(seriUserId,"FTTH");//光猫升级
    	boolean tagFlg = true;
    	String startTime = "";
        for(int i=0;i<otherInfos.size();i++){
        	String tag = otherInfos.getData(i).getString("RSRV_TAG2");//光猫状态 :1:申领，2:更改，3:退还，4:丢失 ，5:升级
        	if("5".equals(tag)){
        		startTime = otherInfos.getData(i).getString("START_DATE");
        		tagFlg = false ;
        		break;
        	}
     	}
        
        //REQ202003050012关于开发融合套餐增加魔百和业务优惠体验权益的需求
        IData params=new DataMap();
		params.put("USER_ID", seriUserId);
		params.put("PRODUCT_ID", "66005203");
		IDataset iDataset= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params); 
		if (IDataUtil.isNotEmpty(iDataset)){
			retrunData.put("ACTIVE_FEE_TAG","1");
		}
   	    
    	if (IDataUtil.isEmpty(saleActives)&&tagFlg){
    		retrunData.put("COMMISSIONING_FEE_TAG","0");
    		return IDataUtil.idToIds(retrunData);
    	}
    	
    	if(IDataUtil.isNotEmpty(saleActives)){
    		String packageId= saleActives.getData(0).getString("PACKAGE_ID","");
        	if ( "79082808".equals(packageId) || "79082908".equals(packageId)){
        		retrunData.put("COMMISSIONING_FEE_TAG","0");
        		retrunData.put("COMMISSIONING_FEE","0");
        		return IDataUtil.idToIds(retrunData);
        	}
    	}
    	
    	String startDate ="";
    	if(IDataUtil.isNotEmpty(saleActives)){
    		startDate = saleActives.getData(0).getString("START_DATE","");   	
    	}else{
    		startDate = startTime;
    	}
    	
    	
    	
		String curDate = SysDateMgr.getSysDate();
		if(curDate.compareTo(SysDateMgr.addMonths(startDate, 24)) > 0 )
		{
			retrunData.put("COMMISSIONING_FEE_TAG","1");
			retrunData.put("COMMISSIONING_FEE","10000");
			retrunData.put("LEFT_MONTHS","0");
		}
		else 
		{
			int betweenMonth = 0 ;
			if(!"".equals(destroyTime))
			{
				betweenMonth = SysDateMgr.monthsBetween(startDate, destroyTime);
			}
			else
			{
				betweenMonth = SysDateMgr.monthsBetween(startDate, SysDateMgr.getSysDate());
			}	

			retrunData.put("COMMISSIONING_FEE_TAG","1");

			if(betweenMonth > 0 && betweenMonth < 25){
				if( 1000*(24-betweenMonth) > 10000)
				{
					retrunData.put("COMMISSIONING_FEE","10000");//折旧费上限为100元/部
				}
				else
				{
					retrunData.put("COMMISSIONING_FEE",1000*(24-betweenMonth));
				}
				retrunData.put("LEFT_MONTHS",24-betweenMonth);

			}
			else
			{
				retrunData.put("COMMISSIONING_FEE","0");
				retrunData.put("LEFT_MONTHS","0");

			}
		}
        
        return IDataUtil.idToIds(retrunData);
    }  
    
    public IData queryCommissioningFee(IData input) throws Exception
    {
        IData retrunData = new DataMap();
        retrunData.put("COMMISSIONING_FEE_WIDENET", "0");
        retrunData.put("LEFT_MONTHS_WIDENET","0");
        retrunData.put("COMMISSIONING_FEE_TOPSETBOX", "0");
        retrunData.put("LEFT_MONTHS_TOPSETBOX","0");
        
        IDataUtil.chkParam(input, "SERIAL_NUMBER"); 
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        if(!serialNumber.startsWith("KD_")){
    		serialNumber = "KD_"+serialNumber;
    	}
        
        //判断是否无手机宽带
        IData widenetinfo = WideNetUtil.getWideNetTypeInfo(serialNumber);
        if (!"Y".equals(widenetinfo.getString("IS_NOPHONE_WIDENET"))) {
        	CSAppException.apperr(TradeException.CRM_TRADE_333,"该用户不是无手机宽带号码，请输入无手机宽带号码！");
		}
        
        IDataset tradeHis688 = TradeHistoryInfoQry.queryTradeHisInfo(serialNumber,"688","0");//无手机宽带激活       
		if (IDataUtil.isNotEmpty(tradeHis688)) {
			IDataset tradeInfo = TradeHistoryInfoQry.query_TF_B_TRADE_ByTradeId(tradeHis688.first().getString("TRADE_ID"));
			if (IDataUtil.isNotEmpty(tradeInfo)) {
				IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUser_2(tradeInfo.first().getString("USER_ID"), "84073842");//调测费模式不退押金  这个是光猫调测费
				if (IDataUtil.isNotEmpty(discntInfo)) {
					retrunData.put("WIDE_MODE_FEE", "1");
				}
				String startDate688 = tradeInfo.first().getString("FINISH_DATE", "");
				retrunData.put("COMMISSIONING_FEE_WIDENET", this.getFee(startDate688).getString("COMMISSIONING_FEE"));
				retrunData.put("LEFT_MONTHS_WIDENET", this.getFee(startDate688).getString("LEFT_MONTHS"));
			}

		}
        
        IDataset topSetInfo = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(widenetinfo.getString("WIDE_USER_ID"),"47");
        if(IDataUtil.isNotEmpty(topSetInfo)){
        	 String serialNumber147 = topSetInfo.first().getString("SERIAL_NUMBER_A");
        	 IDataset tradeHis4910 = TradeHistoryInfoQry.queryTradeHisInfo(serialNumber147,"4910","0");//无手机魔百和激活     
             if(IDataUtil.isNotEmpty(tradeHis4910)){
             	 IDataset tradeInfo2 = TradeHistoryInfoQry.query_TF_B_TRADE_ByTradeId(tradeHis4910.first().getString("TRADE_ID"));
             	 if(IDataUtil.isNotEmpty(tradeInfo2)){
             		 String startDate4910 = tradeInfo2.first().getString("FINISH_DATE","");  
                     retrunData.put("COMMISSIONING_FEE_TOPSETBOX", this.getFee(startDate4910).getString("COMMISSIONING_FEE"));
                     retrunData.put("LEFT_MONTHS_TOPSETBOX",this.getFee(startDate4910).getString("LEFT_MONTHS"));
             	 }
                 
             }
        }
        
    	return retrunData;
    }
    
    public IData getFee(String startDate) throws Exception
    {
    	IData retrunData = new DataMap();
    	
    	String curDate = SysDateMgr.getSysDate();
		if(curDate.compareTo(SysDateMgr.addMonths(startDate, 24)) > 0 )
		{
			retrunData.put("COMMISSIONING_FEE","0");
			retrunData.put("LEFT_MONTHS","0");
		}else{
			int betweenMonth =  SysDateMgr.monthsBetween(startDate, curDate);
			if(betweenMonth < 25){
				if( 1000*(24-betweenMonth) > 10000)
				{
					retrunData.put("COMMISSIONING_FEE","10000");//上限为100元/部
				}
				else
				{
					retrunData.put("COMMISSIONING_FEE",1000*(24-betweenMonth));
				}
				retrunData.put("LEFT_MONTHS",24-betweenMonth);

			}
			else
			{
				retrunData.put("COMMISSIONING_FEE","0");
				retrunData.put("LEFT_MONTHS","0");

			}
		}
    	
    	return retrunData;
    }
    
}
