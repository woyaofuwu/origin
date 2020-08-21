package com.asiainfo.veris.crm.order.soa.person.busi.coupons;
 
import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;   
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class CouponsTradeBean extends CSBizBean
{ 
	static Logger logger=Logger.getLogger(CouponsTradeBean.class);

    /**
     *录入优惠券用户信息
     * chenxy3 @ 2016-05-17 
     */
    public IData insertTicketInfo(IData input) throws Exception
    {
        IData returnData = new DataMap();
        IDataset dataset = new DatasetList();

        //优惠券（map）集合
        IDataset tikList = input.getDataset("TICKET_LIST");
        String operMonth = SysDateMgr.getCurMonth();
        String operDate = SysDateMgr.getSysTime(); 

   
        //一条记录是一张优惠券
        for (int i = 0; i < tikList.size(); i++)
        { 
        	String userId="";
        	String ticketCode="";
        	String ticketEndDate="";
        	
        	//获取电话号码和优惠券值
        	String serialNumber=tikList.getData(i).getString("SERIAL_NUMBER","");
        	String ticketVal=tikList.getData(i).getString("TICKET_VALUE","");
        	
        	//查询电话号码是否可用
        	IData qryData=new DataMap();
            qryData.put("SERIAL_NUMBER", serialNumber);
            IDataset userInfos=checkSn(qryData);
            
            if(userInfos!=null && userInfos.size()>0){
            	/**取该区最大的编号*/
            	int code=1;
            	IData inparam=new DataMap();
            	inparam.put("CITY_CODE", getVisit().getCityCode());
            	IDataset maxCodeset=this.getMaxTicketCode(inparam);
            	if(maxCodeset!=null && maxCodeset.size()>0){
            		//[{"MAX_CODE":null}]
            		String maxData=maxCodeset.getData(0).getString("MAX_CODE","");
            		if(!"".equals(maxData) && !"null".equals(maxData)){
            			code=Integer.parseInt(maxData)+1;
            		} 
            	}

            	
            	ticketEndDate=userInfos.getData(0).getString("TICKET_END_DATE");
            	userId=userInfos.getData(0).getString("USER_ID");
            	String seqNum=String.format("%05d", code);//5位，不足左补0
            	ticketCode=getVisit().getCityCode()+seqNum;
            	
            }
        	
            IData data = new DataMap();
            data.put("TICKET_CODE", ticketCode); 
            data.put("TICKET_END_DATE", ticketEndDate);
            data.put("TICKET_VALUE", ticketVal); 
            data.put("REMARK", tikList.getData(i).getString("REMARK",""));
            data.put("OPER_MONTH", operMonth);
            data.put("OPER_DATE", operDate); 
            data.put("SERIAL_NUMBER", serialNumber); 
            data.put("RSRV_STR1", tikList.getData(i).getString("AUDIT_ORDER_ID","")); 
            //存入优惠券数据库
            insertTicketTab(data);
            
            
            
            
            
            
            //以下是和操作员相关的操作
            IData quotaData = new DataMap();
            String auditOrderId=tikList.getData(i).getString("AUDIT_ORDER_ID","");
        	
            //更新限额
            CouponsQuotaMgrBean bean = (CouponsQuotaMgrBean) BeanManager.createBean(CouponsQuotaMgrBean.class);
            //审批单号不需要改变，传到数据库用于作为条件更新
            quotaData.put("AUDIT_ORDER_ID", auditOrderId);  
            //操作员工号不需要改变，传到数据库作为条件更新
            quotaData.put("OPERA_STAFF_ID",CSBizBean.getVisit().getStaffId());
            
            //更新限额，读取一张优惠券减去一张（现在无需改变，更新下操作时间便可）
            //int j = bean.updateCouponsBalanceInfo(quotaData);
            int j = bean.updateCouponsSumValueInfo(quotaData);
            if(j<=0)
            {
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "更新电子券配额审批表失败,请刷新页面重新操作!");
            }            
            
            //尊敬的客户，您本次获赠手机维修优惠券**元，优惠券编号为**，请在201*年*月*日前到我公司终端售后服务网点，凭手机号码、服务密码、优惠券编号使用手机维修优惠券，终端售后网点信息和授权品牌请关注中国移动10086微信公众号查看
            String tipDate=ticketEndDate.substring(0,10);
            String tipDate2=tipDate.substring(0,tipDate.indexOf("-"))+"年"+tipDate.substring(tipDate.indexOf("-")+1,tipDate.lastIndexOf("-"))+"月"+tipDate.substring(tipDate.lastIndexOf("-")+1)+"日";
            String content="尊敬的客户，您本次获赠手机维修优惠券"+ticketVal+"元，优惠券编号为 "+ticketCode+" ，请在"+tipDate2+"前到我公司终端售后服务网点，凭手机号码、优惠券编号、现场短信验证码使用手机维修优惠券，终端售后网点信息和授权品牌请关注“中国移动10086”微信公众号。【中国移动】";
			IData smsData = new DataMap();
			smsData.put("SERIAL_NUMBER",serialNumber);
			smsData.put("USER_ID",userId);
			String sysTime=SysDateMgr.getSysTime();
			sysTime=sysTime.substring(sysTime.indexOf(":")-2); 				 
			smsData.put("NOTICE_CONTENT",content);
			smsData.put("STAFF_ID",CSBizBean.getVisit().getStaffId());
			smsData.put("DEPART_ID",CSBizBean.getVisit().getDepartId());
			smsData.put("REMARK","手机维修优惠券赠送短信");
			smsData.put("BRAND_CODE","");
			smsData.put("FORCE_START_TIME","");
			smsData.put("FORCE_END_TIME","");
			smsSent(smsData);
        } 
        return returnData;
    }
    
    /**
     *录入优惠券用户信息 - 插入优惠券表
     * chenxy3 @ 2016-05-17 
     * */
    public static void insertTicketTab(IData param) throws Exception{
    	IData insData=new DataMap();
    	insData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); 
    	insData.put("TICKET_CODE",param.getString("TICKET_CODE",""));
    	insData.put("TICKET_VALUE",param.getString("TICKET_VALUE",""));
    	insData.put("TICKET_END_DATE",param.getString("TICKET_END_DATE",""));
    	insData.put("SERIAL_NUMBER",param.getString("SERIAL_NUMBER",""));
    	insData.put("SPEND_VALUE",param.getString("SPEND_VALUE",""));
    	insData.put("TICKET_STATE","0");//默认为0未领取
    	insData.put("UPDATE_STAFF_ID",getVisit().getStaffId());
    	insData.put("UPDATE_DEPART_ID",getVisit().getDepartId());
    	insData.put("UPDATE_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS()); 
    	insData.put("TRADE_ID",param.getString("TRADE_ID",""));
    	insData.put("REMARK",param.getString("REMARK","")); 
    	insData.put("RSRV_STR1",param.getString("RSRV_STR1",""));
    	insData.put("RSRV_STR2",param.getString("RSRV_STR2",""));
    	insData.put("RSRV_STR3",param.getString("RSRV_STR3",""));
    	insData.put("RSRV_STR4",param.getString("RSRV_STR4",""));
    	insData.put("RSRV_STR5",param.getString("RSRV_STR5","")); 
    	Dao.executeUpdateByCodeCode("TL_B_USER_COUPONS", "INS_USER_COUPONS", insData); 
    }
    /**
     *录入优惠券用户信息 - 查询用户号码是否有效
     * chenxy3 @ 2016-05-17 
     * */
    public static IDataset checkSn(IData input) throws Exception{ 
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER")); 
        IDataset users = Dao.qryByCode("TF_F_USER", "SEL_USER_IF_NORMAL", param); 
        return users;
    }
    
    /**
	 * 查询用户优惠券信息
	 * */
	public static IDataset getMaxTicketCode(IData inParam) throws Exception
    { 
        IDataset userInfo = Dao.qryByCode("TL_B_USER_COUPONS", "SEL_COUPONS_MAX_CODE", inParam);
        return userInfo;
    }
    
    /**
	 * 查询用户优惠券信息
	 * */
	public static IDataset getUserTicketInfos(IData inParam,Pagination pagen) throws Exception
    { 
        IDataset userInfo = Dao.qryByCode("TL_B_USER_COUPONS", "SEL_USER_COUPONS_INFO", inParam,pagen);
        return userInfo;
    }
	
	/**
	 * 通用的SMS发送方法
	 * */
    public static void smsSent(IData param) throws Exception{
    	
    	   String serialNumber=param.getString("SERIAL_NUMBER","");
    	   String userId=param.getString("USER_ID","");
    	   String content=param.getString("NOTICE_CONTENT","");
    	   String staffId=param.getString("STAFF_ID",getVisit().getStaffId());
    	   String departId=param.getString("DEPART_ID",getVisit().getDepartId());
    	   String remark=param.getString("REMARK","");
    	   String brandCode=param.getString("BRAND_CODE","");
    	   String forStartTime=param.getString("FORCE_START_TIME","");
    	   String forEndTime=param.getString("FORCE_END_TIME","");
		   IData smsData = new DataMap();

			// 插入短信表
           String seq = SeqMgr.getSmsSendId();
           long seq_id = Long.parseLong(seq);
           long partition_id = seq_id % 1000;
           
           smsData.put("SMS_NOTICE_ID",seq_id);
           smsData.put("PARTITION_ID",partition_id);
           smsData.put("EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
           smsData.put("BRAND_CODE",brandCode);
           smsData.put("IN_MODE_CODE",getVisit().getInModeCode());
           smsData.put("SMS_NET_TAG","0");
           smsData.put("CHAN_ID","GTM");
           smsData.put("SEND_OBJECT_CODE","1");
           smsData.put("SEND_TIME_CODE","1");
           smsData.put("SEND_COUNT_CODE","");
           smsData.put("RECV_OBJECT_TYPE","00");
           smsData.put("RECV_OBJECT",serialNumber);
           smsData.put("RECV_ID",userId);
           smsData.put("SMS_TYPE_CODE","20");
           smsData.put("SMS_KIND_CODE","08");
           smsData.put("NOTICE_CONTENT_TYPE","0");
           smsData.put("NOTICE_CONTENT",content);
           smsData.put("REFERED_COUNT","");
           smsData.put("FORCE_REFER_COUNT","1");
           smsData.put("FORCE_OBJECT","10086");
           smsData.put("FORCE_START_TIME",forStartTime);
           smsData.put("FORCE_END_TIME",forEndTime);
           smsData.put("SMS_PRIORITY","1001");
           smsData.put("REFER_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
           smsData.put("REFER_STAFF_ID",staffId);
           smsData.put("REFER_DEPART_ID",departId);
           smsData.put("DEAL_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
           smsData.put("DEAL_STAFFID",staffId);
           smsData.put("DEAL_DEPARTID",departId);
           smsData.put("DEAL_STATE","15");
           smsData.put("REMARK",remark);
           smsData.put("REVC1","");
           smsData.put("REVC2","");
           smsData.put("REVC3","");
           smsData.put("REVC4","");
           smsData.put("MONTH",SysDateMgr.getCurMonth());
           smsData.put("DAY",SysDateMgr.getCurDay());

           Dao.insert("TI_O_SMS", smsData);
    }
    
    /**
     * 使用优惠券更新优惠券信息
     */
    public void updUserCouponsInfo(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TL_B_USER_COUPONS", "UPD_USER_COUPONS_SPEND", inparams);

    }
    
    /**
     * 获取操作员ID
     */
    public String getStaffId(IData inparams) throws Exception
    {
    	//获取该优惠券的TICKET_VALUE
    	//查出优惠券金额
        String sql="select RSRV_STR1 from TL_B_USER_COUPONS T where T.TICKET_CODE=:TICKET_CODE";
        String staff_id = null;
        IDataset staff_ids = Dao.qryBySql(new StringBuilder(sql), inparams);
        if(staff_ids.size()>0)
        	staff_id = staff_ids.getData(0).getString("RSRV_STR1");
        return staff_id;
        
    }
    
    /**
     * 使用了优惠券后，更新操作员的限额
     */
    public void updCouponsQuota(IData inparams) throws Exception
    {

    	//获取发券员id
    	String staffId = getStaffId(inparams);
    	inparams.put("AUDIT_ORDER_ID", staffId);
        //更新操作员限额
        String opersql = "UPDATE TL_B_COUPONS_QUOTA T SET T.BALANCE=to_number(balance)-to_number(:TICKET_VALUE) * 100," +
        		"T.AMOUNTS=to_number(amounts)+to_number(:TICKET_VALUE) * 100 where AUDIT_ORDER_ID=:AUDIT_ORDER_ID";
        Dao.executeUpdate(new StringBuilder(opersql), inparams);
        
    }
 
}