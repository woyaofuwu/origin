package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish.insertSMSFTTHAction;

public class NoPhoneWideDestroyUserBean extends CSBizBean
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
    public IDataset callNoPhoneDestroyService(IData params) throws Exception
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
    		param.put("TRADE_TYPE_CODE", "685");//宽带拆机
        }else if ("3".equals(widetype))
        {
        	param.put("TRADE_TYPE_CODE", "685");//
        }else if ("2".equals(widetype) || "5".equals(widetype) || "6".equals(widetype))
        {
        	param.put("TRADE_TYPE_CODE", "685");
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
    	
        IDataset result = null;
        
        try
        {
        	IDataset userSet = UserInfoQry.getUserInfoBySn(serialNumber,"0");
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
        		result = CSAppCall.call("SS.NoPhoneWideDestroyUserRegSVC.tradeReg", param);
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
    
    /**
     * 调拆机接口
     */
    public IDataset stop3MonDestroyUser(IData params) throws Exception
    {
    	IDataset result = new DatasetList();
    	IData updData=new DataMap();
        String serialNumber = params.getString("SERIAL_NUMBER");
		String kdUserId = params.getString("KD_USER_ID");
		String inte="";
		String reason="";
		try
        {
			//查看是否存在未完工移机工单
			IDataset tradeLists=getMainTradeByUserIdTypeCode(serialNumber,"686");
			if(tradeLists!=null && tradeLists.size()>0){
				inte="SS.CancelNoPhoneWidenetTradeService.cancelTradeReg";
				reason="存在<移机未完工单>，先进行撤单操作，下次进行拆机。";
				//调用移机撤单接口
				IData inparam=new DataMap();
				inparam.put("TRADE_ID",tradeLists.getData(0).getString("TRADE_ID"));
				inparam.put("TRADE_TYPE_CODE",tradeLists.getData(0).getString("TRADE_TYPE_CODE"));
				inparam.put("ACCEPT_MONTH",tradeLists.getData(0).getString("ACCEPT_MONTH"));
				inparam.put("OPER_FEE",tradeLists.getData(0).getString("OPER_FEE","0"));
				inparam.put("FOREGIFT",tradeLists.getData(0).getString("FOREGIFT","0"));
				inparam.put("ADVANCE_PAY",tradeLists.getData(0).getString("ADVANCE_PAY","0"));
				inparam.put("FEE_STATE",tradeLists.getData(0).getString("FEE_STATE"));
				inparam.put("FEE_STAFF_ID",tradeLists.getData(0).getString("FEE_STAFF_ID"));
				inparam.put("TRADE_STAFF_ID",tradeLists.getData(0).getString("TRADE_STAFF_ID"));
				inparam.put("TRADE_EPARCHY_CODE",tradeLists.getData(0).getString("TRADE_EPARCHY_CODE"));
				inparam.put("TRADE_DEPART_ID",tradeLists.getData(0).getString("TRADE_DEPART_ID",""));
				inparam.put("SERIAL_NUMBER",serialNumber) ;
				inparam.put("REMARK","无手机宽带停机超3个月自动拆机后台撤销移机工单");
				inparam.put("CANCEL_REASON_ONE","101909");
				result=CSAppCall.call("SS.CancelNoPhoneWidenetTradeService.cancelTradeReg", inparam); 
//				String tradeId="";
//	        	if(result!=null && result.size()>0){
//	        		tradeId=result.getData(0).getString("TRADE_ID");
//	        	}  
//				reason=reason+"trade_id="+tradeId;
			}else{
				inte="SS.NoPhoneWideDestroyUserRegSVC.tradeReg";
				reason="停机超3个月自动拆机。";
				String tradeId="";
				//由于拆机是长流程，如果每天跑，则要求判断未完工情况
				IDataset lists1=getMainTradeByUserIdTypeCode(serialNumber,"685");
				IDataset lists2=getMainTradeByUserIdTypeCode(serialNumber,"687");
				IDataset lists=new DatasetList();
				lists.addAll(lists1);
				lists.addAll(lists2);
				if(lists!=null && lists.size()>0){
					tradeId=lists.getData(0).getString("TRADE_ID");
					reason="已经存在拆机工单，无需重复办理。";
				}else{
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
			    		param.put("TRADE_TYPE_CODE", "685");//宽带特殊拆机（主要涉及光猫押金的沉淀）
			        }else if ("3".equals(widetype))
			        {
			        	param.put("TRADE_TYPE_CODE", "685");//宽带特殊拆机（主要涉及光猫押金的沉淀）
			        }else if ("2".equals(widetype) || "5".equals(widetype) || "6".equals(widetype))
			        {
			        	param.put("TRADE_TYPE_CODE", "685");//宽带特殊拆机（主要涉及光猫押金的沉淀）
			        }
			        //FTTH宽带，取光猫串号
			    	if ("3".equals(widetype) || "5".equals(widetype))
			    	{
			    		param.put("MODEM_FEE", params.getString("RSRV_STR2","0"));
			    		param.put("MODEM_RETUAN", params.getString("RSRV_TAG2","0"));
			    		param.put("MODEM_MODE", params.getString("RSRV_TAG3","0"));
			    	}
			    	param.put("WIDE_TYPE_CODE", widetype);//宽带类型  
			    	param.put("NO_PHONE_90DAY_DESTORY", "90");//光猫丢失押金沉淀
		        	result = CSAppCall.call("SS.NoPhoneWideDestroyUserRegSVC.tradeReg", param);
		        	
		        	if(result!=null && result.size()>0){
		        		tradeId=result.getData(0).getString("TRADE_ID");
		        	}  
				}
				reason=reason+"trade_id="+tradeId;
			}
			 
        	updData.put("SERIAL_NUMBER",serialNumber);
        	updData.put("DEAL_INTF",inte );
        	updData.put("DEAL_REASON",reason );
        	updData.put("DEAL_RESULT","成功" );
        	upAutoDestroyTab(updData);
        }
        catch(Exception e)
        {   
        	String rsrvStr1 = getErrorMsg(e.getMessage(),200);
        	updData.put("SERIAL_NUMBER",serialNumber);
        	updData.put("DEAL_INTF",inte );
        	updData.put("DEAL_REASON",reason );
        	updData.put("DEAL_RESULT",rsrvStr1 );
        	upAutoDestroyTab(updData);
        }
        
        return result;
    }
    
    /**
     * 根据手机号查询是否存在无手机撤机工单
     * 
     * */
    public static IDataset getMainTradeByUserIdTypeCode(String serialNum, String trade_type_code) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNum);
		inparams.put("TRADE_TYPE_CODE", trade_type_code);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, TO_CHAR(BPM_ID) BPM_ID, ");
		sql.append("TRADE_TYPE_CODE, IN_MODE_CODE, PRIORITY, ");
		sql.append("SUBSCRIBE_STATE, NEXT_DEAL_TAG, PRODUCT_ID, ");
		sql.append("BRAND_CODE, TO_CHAR(USER_ID) USER_ID, ");
		sql.append("TO_CHAR(CUST_ID) CUST_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, CUST_NAME, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("ACCEPT_MONTH, TRADE_STAFF_ID, TRADE_DEPART_ID, ");
		sql.append("TRADE_CITY_CODE, TRADE_EPARCHY_CODE, TERM_IP, ");
		sql.append("EPARCHY_CODE, CITY_CODE, OLCOM_TAG, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, PROCESS_TAG_SET, RSRV_STR1, ");
		sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
		sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, REMARK ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER ");
		sql.append("AND T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");

		return Dao.qryBySql(sql, inparams,Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
    
    
    public static void upAutoDestroyTab(IData updData) throws Exception
	{
		IData cond = new DataMap();
		 
        cond.put("SERIAL_NUMBER",updData.getString("SERIAL_NUMBER",""));
        cond.put("DEAL_INTF",updData.getString("DEAL_INTF",""));
        cond.put("DEAL_TIME",updData.getString("DEAL_TIME",""));
        cond.put("DEAL_REASON",updData.getString("DEAL_REASON",""));
        cond.put("DEAL_RESULT",updData.getString("DEAL_RESULT",""));

        
        StringBuilder sql = new StringBuilder(200);
        sql.append(" insert into TL_B_NONPHONEWIDE_AUTO_DESTROY( ");
        		sql.append(" SERIAL_NUMBER, ");
        		sql.append(" DEAL_INTF, ");
        		sql.append(" DEAL_TIME, ");
        		sql.append(" DEAL_REASON, ");
        		sql.append(" DEAL_RESULT ");
        		sql.append(" )values( :SERIAL_NUMBER, ");
        		sql.append(" :DEAL_INTF, ");
        		sql.append(" sysdate, ");
        		sql.append(" :DEAL_REASON, ");
        		sql.append(" :DEAL_RESULT) ");
        
        Dao.executeUpdate(sql, cond);
	}
    /**
     * 调拆机接口
     */
    public IDataset queryWidenetInstallFee(IData input) throws Exception
    {
        IData retrunData = new DataMap();
       
        
        String userId = input.getString("USER_ID","");
    	String openDate= input.getString("OPEN_DATE","");
    	
    	IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUser_2(userId, "84073842");//调测费模式不退押金  这个是光猫调测费，根据这个判断就行
 	    if(IDataUtil.isNotEmpty(discntInfo)){
 	    	retrunData.put("WIDE_MODE_FEE", "1");
 	    }
 	    
    	IDataset saleActives = UserDiscntInfoQry.getAllDiscntUID(userId);
		//System.out.println("=============queryWidnetInstallFee=============saleActives:"+saleActives);

    	if (IDataUtil.isEmpty(saleActives)){
    		retrunData.put("INSTALL_FEE_TAG","0");
    		return IDataUtil.idToIds(retrunData);
    	}
    	
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
    
	/**
	 * 到期处理无手机宽带停机接口
	 * */
	public IDataset nophoneWideStopServiceDeal(IData inparams) throws Exception
    { 
		IData dealParam = new DataMap(inparams.getString("DEAL_COND"));
		IDataset results = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", dealParam);
		 //处理完成发短信提醒用户宽带已停机，局方要求  modify by duhj_kd 2020518
		String noPhoneWideSn = dealParam.getString("SERIAL_NUMBER");
		insertSms(noPhoneWideSn);
	    return results;
		 
    }


    private void insertSms(String noPhoneWideSn) throws Exception
    {
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(noPhoneWideSn);
        if (IDataUtil.isNotEmpty(widenetInfos))
        {
            String contackPhone = widenetInfos.getData(0).getString("CONTACT_PHONE"); // 联系人号码
            if (StringUtils.isNotBlank(contackPhone))
            {
                IData userInfo = UcaInfoQry.qryUserInfoBySn(contackPhone);
                if (IDataUtil.isNotEmpty(userInfo))
                {
                    // 查询出该宽带号码开户时候的联系人号码，只能给该号码发短信提醒了
                    String noticeContent = "尊敬的客户：您办理的无手机宽带" + noPhoneWideSn + "已经到期，为了不影响您的正常使用，请及时续费";
                    IData smsData = new DataMap();
                    smsData.put("RECV_OBJECT", userInfo.getString("SERIAL_NUMBER"));
                    smsData.put("NOTICE_CONTENT", noticeContent);
                    smsData.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
                    smsData.put("RECV_ID", userInfo.getString("USER_ID"));
                    SmsSend.insSms(smsData);
                }
            }

        }

    }
}
