package com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class DimensionalCodeBean extends CSBizBean {
	public static final String TRADE_TYPE_CODE = "517";// 业务类型代码 注：需要现场自己提供 并根据设计文档中的配置进行相应配置 并修改DimensionalCodeModify.html页面中的这个值
	public static final String ORG_DOMAIN = "EWMP";// 平台编码        
	public static final String BIZ_TYPE_CODE = "EP";// 业务类型代码     
	public static final String SPID = "REG_SP";// SP企业代码            
	public static final String BIZ_CODE = "REG_SP";// SP业务代码 
	public static final String PACKAGE_ID = "50000000";
	public static final String PRODUCT_ID = "50000000";
	public static final String PROV_CODE = "898";//省份代码：海南898 湖南731 青海971 陕西029 天津220 新疆991 云南871
	// 状态 01：新生成 02：补发 03：重置 04：冻结 05：解冻 06：启用消费密码 07：关闭消费密码 08：未生成 09：其他状态
	public static final String STATUS_GENERATE = "01";
	public static final String STATUS_REISSUE = "02";
	public static final String STATUS_RESET = "03";
	public static final String STATUS_FREEZE = "04";
	public static final String STATUS_UNFREEZE = "05";
	public static final String STATUS_ENABLE_CONSUMER_PASSWORD = "06";
	public static final String STATUS_CLOSE_CONSUMPTION_PASSWORD = "07";
	public static final String STATUS_NOGENERATE = "08";
	public static final String STATUS_OTHER = "09";
	// 操作代码 01：下发 02：补发（已经生成的下发） 03：重置（重新生成下发） 04：冻结 05：解冻 06：启用消费密码 07：关闭消费密码
	public static final String OPR_CODE_GENERATE = "01";
	public static final String OPR_CODE_REISSUE = "02";
	public static final String OPR_CODE_RESET = "03";
	public static final String OPR_CODE_FREEZE = "04";
	public static final String OPR_CODE_UNFREEZE = "05";
	public static final String OPR_CODE_ENABLE_CONSUMER_PASSWORD = "06";
	public static final String OPR_CODE_CLOSE_CONSUMPTION_PASSWORD = "07";
	// 状态与操作的合法性 匹配
	public static final String[] STATUS_OPR_CODE = { "02,03,04,06,07","用户二维码当前状态为【新生成 】状态，只支持【补发、重置、冻结、启用消费密码、关闭消费密码】操作！", 
													 "02,03,04,06,07","用户二维码当前状态为【补发】状态，只支持【补发、重置、冻结、启用消费密码、关闭消费密码】操作！", 
													 "02,03,04,06,07","用户二维码当前状态为【重置 】状态，只支持【补发、重置、冻结、启用消费密码、关闭消费密码】操作！",
													 "05", "用户二维码当前状态为【冻结 】状态，只支持【解冻】操作！",
													 "02,03,04,06,07", "用户二维码当前状态为【解冻 】状态，只支持【补发、重置、冻结、启用消费密码、关闭消费密码】操作！", 
													 "02,03,04,07",	"用户二维码当前状态为【启用消费密码 】状态，只支持【补发、重置、冻结、关闭消费密码】操作！", 
													 "02,03,04,06",	"用户二维码当前状态为【关闭消费密码 】状态，只支持【补发、重置、冻结、启用消费密码】操作！", 
													 "01","用户二维码当前状态为【未生成 】状态，只支持【下发】操作！", 
													 " ", "用户二维码当前状态为【其他状态 】状态，不支持任何操作！" };
	/**
	 * 获取交易包流水号
	 */
	public String getSeqID(String bip_code) throws Exception {
		String dateString =SysDateMgr.getSysDate("yyyyMMddHHmmss");
		String seqID = String.format("%06d", (int)(Math.random()*1000000));
		return PROV_CODE+bip_code+dateString+seqID;
//		String seqID=SeqMgr.getSeqId("SEQ_SP_SEQ_ID");
//		String.format("%06d", Integer.parseInt(seqID.toString()));
//		return PROV_CODE+bip_code+seqID;
	}
	/**
	 * 根据输入14位字符串(20120224134333)，返回日期
	 * @return 时间YYYY-MM-DD HH24:MI:SS字符串格式
	 */
	public String getDateFromStr(String dateStr) throws Exception{
		String strDate = new String();
		String str1 = dateStr.substring(0,4);
		String str2 = dateStr.substring(4, 6);
		String str3 = dateStr.substring(6, 8);
		String str4 = dateStr.substring(8,10);
		String str5 = dateStr.substring(10,12);
		String str6 = dateStr.substring(12,14);
		strDate = str1+"-"+str2+"-"+str3+" "+str4+":"+str5+":"+str6;
		Date date = SysDateMgr.string2Date(strDate, SysDateMgr.PATTERN_STAND);
		return SysDateMgr.date2String(date,SysDateMgr.PATTERN_STAND);
	}
	/**
	 * 调用网状网查询变更状态列表
	 */
	public IData qryDimensionalCodeStateList(IData input) throws Exception {
		IData resultData = new DataMap();
		String serial_number = input.getString("SERIAL_NUMBER");
		String date_from=SysDateMgr.getDateForYYYYMMDD(input.getString("DATE_FROM","1900-01-01"));
		String date_to= SysDateMgr.getDateForYYYYMMDD(input.getString("DATE_TO",SysDateMgr.getTheLastTime()));
		
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
	    if (IDataUtil.isEmpty(user)){
	    	CSAppException.apperr(CrmUserException.CRM_USER_112);
	    }
		// 调用IBOSS接口查询
		IData inIBossParam = new DataMap();
		inIBossParam.put("KIND_ID", "BIP2B335_T2001135_0_0");// 接口标识
		inIBossParam.put("ID_TYPE", "01");// 01-手机号码
		inIBossParam.put("ID_VALUE", serial_number); // 用户ID标识值 手机号码
		inIBossParam.put("SEQ", input.getString("SEQ",getSeqID("BIP2B335")));
		inIBossParam.put("DATE_FROM",date_from);
		inIBossParam.put("DATE_TO", date_to);
		
		resultData = IBossCall.dealInvokeUrl("BIP2B335_T2001135_0_0", "IBOSS", inIBossParam).first();
	    //状态变更根据时间倒序 并格式化时间
		if(null!=resultData&&!resultData.isEmpty()){
			IDataset history_list=resultData.getDataset("HISTORY_LIST");
			if(null!=history_list&&!history_list.isEmpty()){
				DataHelper.sort(history_list, "OPR_TIME", IDataset.TYPE_STRING,IDataset.ORDER_DESCEND);
				for (int i = 0; i < history_list.size(); i++) {
					IData history=history_list.getData(i);
					history.put("OPR_TIME", getDateFromStr(history.getString("OPR_TIME")));
				}
				resultData.put("HISTORY_LIST", history_list); 
			}
		}
		return resultData;
	}
	/**
	 * 三户资料校验  主体服务正常校验
	 */
	public void isNormalMainService(String serial_number) throws Exception {
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
	    if (IDataUtil.isEmpty(user)){
	    	CSAppException.apperr(CrmUserException.CRM_USER_112);
	    }
//		IData inputParam=new DataMap();
//		inputParam.put("SERIAL_NUMBER", serial_number);
//		inputParam.put("REMOVE_TAG", "0");
//		IData userInfo=UcaInfoQry.qryUserInfoBySn(serial_number);
		if(!"0".equals(user.getString("USER_STATE_CODESET"))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户主体状态非正常");
		}
	}
	/**
	 * 是否合法操作
	 */
	public IData isLegalOprCode(String cur_status,String cur_opr_code) throws Exception {
		IData resultData = new DataMap();
		if(STATUS_OPR_CODE[Integer.parseInt(cur_status)*2-2].indexOf(cur_opr_code)>-1){
			resultData.put("X_RESULT_CODE", "0");
			return resultData;
		}
		resultData.put("X_RESULT_CODE", "-1");
		resultData.put("X_RESULT_INFO", STATUS_OPR_CODE[Integer.parseInt(cur_status)*2-1]);
		return resultData;
	}
}