package com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.query.RouteInfoQry;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class EValueCardInfoQry  extends CSBizBean {

	private final static Logger logger = Logger.getLogger(Dao.class);

	/**
	 * 根据产品id找出crm测的信息
	 * String idvalue
	 * @return
	 * @throws Exception
	 */
	public static IDataset getCardSaleInfo(String idvalue) throws Exception {

		IData param = new DataMap();
		param.put("IDVALUE", idvalue);
		IDataset result = Dao.qryByCode("TI_B_ELECTCARD_SELLANDCANCEL","SEL_EVALUE_CARD_RECORD", param, Route.CONN_CRM_CEN);
		return result;
	}
	
	
	/**
	 * 根据购卡手机号或交易流水查询售卡信息
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCanCancelInfo(IData param) throws Exception {
		
		IDataset result = Dao.qryByCode("TI_B_ELECTCARD_SELLANDCANCEL","SEL_CANCANCEL_EVALUE_CARD", param, Route.CONN_CRM_CEN);
		return result;
	}

    /**
     * 根据业务流水号获取电子卡批量充值请求信息
     * 
     * @param identCode
     * @return
     * @throws Exception
     */
    public static IDataset queryBatchRecReqInfo(IData param, Pagination pagination) throws Exception
    {
		if (StringUtils.isNotEmpty(param.getString("TRADE_ID"))) {
			return Dao.qryByCodeParser("TI_B_ELECTCARD_BAT_RECH", "SEL_BY_TRADE_ID", param,pagination,Route.CONN_CRM_CEN);
		} else {
			return Dao.qryByCodeParser("TI_B_ELECTCARD_BAT_RECH", "SEL_BY_ACCEPTDATE", param,pagination,Route.CONN_CRM_CEN);
		}    	
    }
	
	/**
	 * 发短信通知客户
	 * 
	 * @param pd
	 * @param td
	 * @param sn
	 * @param content
	 * @param remark
	 * @throws Exception
	 */
	public static void sms(IData input,String content , String userId,String password) throws Exception
	{
		String route = RouteInfoQry.getEparchyCodeBySnForCrm(input.getString("SERIAL_NUMBER",""));
        if (StringUtils.isBlank(route))
        {
        	route = "0898";
        }
		IData newParam = new DataMap ( ) ;
		String seqId = SeqMgr.getSmsSendId();
		newParam.put("SMS_NOTICE_ID",seqId);
		newParam.put("PARTITION_ID",seqId.substring(seqId.length() - 4));
		newParam.put("SEND_COUNT_CODE","1");
		newParam.put("REFERED_COUNT","0");
		newParam.put("EPARCHY_CODE", input.getString("TRADE_EPARCHY_CODE"));
		newParam.put("IN_MODE_CODE", input.getString("IN_MODE_CODE") );
		newParam.put("CHAN_ID","11");//短信渠道编码:客户服务
		newParam.put("RECV_OBJECT_TYPE","00");//被叫对象类型:00－手机号码
		newParam.put("FORCE_OBJECT", "10086");
		newParam.put("RECV_OBJECT" , input.getString("IDVALUE")) ;   //被叫对象:传手机号码
		newParam.put("RECV_ID" ,userId ) ;                                 //被叫对象标识:传用户标识
		newParam.put("SMS_TYPE_CODE","20");                     //短信类型:20-业务通知
		newParam.put("SMS_KIND_CODE","31");                     //短信种类:02－短信通知
		newParam.put("NOTICE_CONTENT_TYPE","0");//短信内容类型:0－指定内容发送
		if (content.length() > 2000) content = content.substring(0, 2000);
		newParam.put("NOTICE_CONTENT",content);//短信内容类型:0－指定内容发送
		newParam.put("FORCE_REFER_COUNT","1");//指定发送次数
		newParam.put("SMS_PRIORITY",1000);//短信优先级
		newParam.put("REFER_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss") ) ;        //提交时间
		newParam.put("REFER_STAFF_ID",input.getString("TRADE_STAFF_ID"));//提交员工
		newParam.put("REFER_DEPART_ID",input.getString("TRADE_DEPART_ID"));//提交部门
		newParam.put("DEAL_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")  ) ;         //处理时间
		newParam.put("DEAL_STATE","15");//处理状态:0－未处理
		//newParam.put("REMARK", "电子有价卡销售短信" );
		/**
		 * 修改为用REMARK存放卡密加密字符串
		 * @author zhuoyingzhi
		 * @date 20170804
		 */
		newParam.put("REMARK", password);
		
		newParam.put("SEND_TIME_CODE","1");	
		newParam.put("SEND_OBJECT_CODE","6");	
		newParam.put("SMS_NET_TAG","0");
		newParam.put("MONTH",Integer.parseInt ( SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").substring(5, 7)));
		newParam.put("DAY",Integer.parseInt   (SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").substring(8, 10)));
		//newParam.put("REVC3", password);
		if (!"".equals(input.getString("IDVALUE",""))) {
			Dao.insert("TI_O_SMS", newParam,route);
		}
		
	}
	
	
	public static void recordCardSaleInfo (IData input) throws Exception{
		
		IData param  = new DataMap();
		param.put("TRANSACTIONID", input.getString("TRANSACTIONID"));
		param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		param.put("IDVALUE", input.getString("SERIAL_NUMBER"));
		param.put("CHANNEL_TYPE", input.getString("CHANNEL_TYPE"));
		param.put("CARD_PASSWORD", input.getString("CARD_PASSWORD"));
		param.put("CARD_STATE", "01");
		param.put("CARD_NO", input.getString("CARD_NO"));
		param.put("CARD_TYPE", input.getString("CARD_TYPE"));
		param.put("CRAD_BUSIPROP", input.getString("CARD_BUSIPROP"));
		param.put("CARD_MONEY", input.getString("CARD_MONEY"));
		param.put("CARD_EXPIREDDATE", input.getString("ACTIVE_DAYS"));
		param.put("IS_GIVE", "0");
		param.put("CHANGE_TYPE", input.getString("OPER_TYPE"));
		param.put("PAYMENT", input.getString("PAYMENT"));
		param.put("ACTION_TIME", input.getString("ACTION_TIME"));
		param.put("SETTLE_DATE", input.getString("SETTLE_DATE"));
		param.put("RESELL_TYPE", input.getString("RESELL_TYPE"));
		param.put("RESELL_TRANSACTIONID", input.getString("RESELL_TRANSACTIONID"));
		param.put("RESELL_CARD_NO", input.getString("RESELL_CARD_NO"));
		param.put("HOME_PRO", "898");
		param.put("BUY_COUNT", input.getString("BUY_COUNT"));
		param.put("EMAIL", input.getString("EMAIL"));
		param.put("EXEC_STATE","0");
		param.put("EXEC_MON_STATE","0");
		param.put("RSRV_STR1", input.getString("RSRV_STR1"));
		param.put("RSRV_STR2", input.getString("RSRV_STR2"));
		param.put("RSRV_STR3", input.getString("RSRV_STR3"));
		param.put("RSRV_STR4", input.getString("RSRV_STR4"));
		param.put("RSRV_STR5", input.getString("RSRV_STR5"));
		
		Dao.insert("TI_B_ELECTCARD_SELLANDCANCEL", param,Route.CONN_CRM_CEN);
	}

	public static void recordBatchCardSaleInfo (IData input) throws Exception{
		
		IData param  = new DataMap();
		param.put("TRANSACTION_ID", input.getString("TRANSACTION_ID"));
		param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		param.put("CHANNEL_TYPE", input.getString("CHANNEL_TYPE"));
		param.put("CARD_TYPE", input.getString("CARD_TYPE"));
		param.put("CARD_BUSIPROP", input.getString("CARD_BUSIPROP"));
		param.put("CARD_MONEY", input.getString("CARD_MONEY"));
		param.put("BUY_COUNT", input.getString("BUY_COUNT"));
		param.put("HOME_PRO", "898");
		param.put("PAYMENT", input.getString("PAYMENT"));
		param.put("ACCEPT_DATE",SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		param.put("SETTLE_DATE",SysDateMgr.getSysDate("yyyyMMdd"));
		param.put("PUBLIC_KEY_ID", input.getString("PUBLIC_KEY_ID"));
		param.put("STATUS", input.getString("STATUS"));
		param.put("ORDER_NO", input.getString("ORDER_NO"));
		param.put("RSRV_STR2", input.getString("RSRV_STR2"));
		Dao.insert("TI_B_ELECTCARD_BATSELL", param,Route.CONN_CRM_CEN);
	}
	
    /**
     * 根据业务流水号获取电子卡批量销售请求信息
     * 
     * @param identCode
     * @return
     * @throws Exception
     */
    public static IDataset queryBatchReqInfo(IData param, Pagination pagination) throws Exception
    {
		if (StringUtils.isNotEmpty(param.getString("TRANSACTION_ID"))) {
			return Dao.qryByCodeParser("TI_B_ELECTCARD_BATSELL", "SEL_BY_TRANSACTIONID", param,pagination);
		} else {
			return Dao.qryByCodeParser("TI_B_ELECTCARD_BATSELL", "SEL_BY_ACCEPTDATE", param,pagination);
		}    	
    } 
	/**
	 * 根据产品id找出crm测的信息
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkOrderId(String orderId) throws Exception {

		IData param = new DataMap();
		param.put("RSRV_STR1", orderId);
		IDataset result = Dao.qryByCode("TI_B_ELECTCARD_SELLANDCANCEL","SEL_BY_ORDERID", param, Route.CONN_CRM_CEN);
		return result;
	}
	
	/**
	 * 安全密钥查询
	 * @param input
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRsaPublicKey(IData input, Pagination page) throws Exception{
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("STAFF_ID", input.getString("STAFF_ID"));
		return Dao.qryByCode("TI_B_ELECTCARD_RSAKEY","SEL_PUBLIC_KEY", param, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 查询省公钥ID
	 */
	public static String qryProPublicKeyID() throws Exception{
        IDataset result = Dao.qryByCode("TI_B_ELECTCARD_RSAKEY","SEL_PASSWD_ID", null, Route.CONN_CRM_CEN);
        return IDataUtil.isEmpty(result)?"":result.getData(0).getString("PASSWD_ID");        
	}
	
	/**
	 * 安全密钥表新增
	 */
	public static boolean recordRsaPublicKey(IData input) throws Exception{
		IData insertdata = new  DataMap();
		insertdata.put("PASSWD_ID", input.getString("SECRPASSWD_ID"));
		insertdata.put("PASSWD_VALUE", input.getString("SECR_PASSWD"));
		insertdata.put("STAFF_ID", getVisit().getStaffId());
		insertdata.put("UPDATE_TIME", SysDateMgr.getSysTime());
		insertdata.put("MODIFY_TAG", "1");
		insertdata.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		insertdata.put("HOME_PRO", input.getString("HOME_PRO"));
		insertdata.put("RSRV_STR1",input.getString("REMARK"));
		insertdata.put("RSRV_STR2",input.getString("PRORSA_FLAG"));
		return Dao.insert("TI_B_ELECTCARD_RSAKEY", insertdata, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 安全密钥表更新
	 */
	public static boolean updateRsaPublicKey(IData input) throws Exception{
		IData insertdata = new  DataMap();
		insertdata.put("PASSWD_ID", input.getString("SECRPASSWD_ID"));
		insertdata.put("PASSWD_VALUE", input.getString("SECR_PASSWD"));
		insertdata.put("STAFF_ID", getVisit().getStaffId());
		insertdata.put("UPDATE_TIME", SysDateMgr.getSysTime());
		insertdata.put("MODIFY_TAG", "2");
		insertdata.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		insertdata.put("HOME_PRO", input.getString("HOME_PRO"));
		insertdata.put("RSRV_STR1",input.getString("REMARK"));
		insertdata.put("RSRV_STR2",input.getString("PRORSA_FLAG"));
		return Dao.update("TI_B_ELECTCARD_RSAKEY", insertdata,new String[]{ "PASSWD_ID" },Route.CONN_CRM_CEN);
	}
	
	/**
	 * 安全密钥表删除
	 */
	public static boolean deleteRsaPublicKey(IData input) throws Exception{
		IData deldata = new  DataMap();
		deldata.put("PASSWD_ID", input.getString("SECRPASSWD_ID"));
		return Dao.delete("TI_B_ELECTCARD_RSAKEY", deldata,new String[]{ "PASSWD_ID" }, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 判断平台公钥ID是否存在
	 */
	public static IDataset qryDownProPublicKeyID(String pwdid) throws Exception{
		IData param = new DataMap();
        param.put("PASSWD_ID", pwdid);
        IDataset result = Dao.qryByCode("TI_B_ELECTCARD_RSAKEY","SEL_DOWN_PASSWD_ID", param, Route.CONN_CRM_CEN);
        return result;        
	}
	/**
	 * 生成安全密钥ID
	 */
	public static String getSecrPasswdId() throws Exception{
		String secrPasswdId = "";
        StringBuilder sql = new StringBuilder("select seq_secrpasswd_id.nextval as SEQ_SECRPASSWD_ID from dual"); // 执行SQL
        IDataset results = Dao.qryBySql(sql, new DataMap(), Route.CONN_CRM_CEN);
        if (null != results && results.size() > 0)
        {
        	secrPasswdId = results.getData(0).getString("SEQ_SECRPASSWD_ID");
        }
        int index = Integer.parseInt(secrPasswdId);
        if (index>0 && index<10) {
        	secrPasswdId = "000" + index;
		}
		
		if (index>9 && index<100) {
			secrPasswdId = "00" + index;
		}
		
		if (index>99 && index<1000) {
			secrPasswdId = "0" + index;
		}
		
		if (index>999 && index<10000) {
			secrPasswdId = "" + index;
		}
        return secrPasswdId;
	}
	
}
