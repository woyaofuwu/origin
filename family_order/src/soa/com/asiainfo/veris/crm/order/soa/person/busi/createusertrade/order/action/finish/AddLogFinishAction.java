package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.MD5Util;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;

public class AddLogFinishAction implements ITradeFinishAction {

	protected static Logger log = Logger.getLogger(AddLogFinishAction.class);
	
	public void executeAction(IData mainTrade) throws Exception {
		 
		String strTradeId = mainTrade.getString("TRADE_ID");
		IDataset customerDs = TradeCustomerInfoQry.getTradeCustomerByTradeId(strTradeId);
		if (IDataUtil.isNotEmpty(customerDs)) {
			IData customer = customerDs.getData(0);
			String psptId = customer.getString("PSPT_ID", "").trim();
			String psptTypeCode = this.revertPsptTypeCode(customer.getString("PSPT_TYPE_CODE", "").trim());
			//String psptAddr = customer.getString("PSPT_ADDR", "").trim();
			
			String rsrvStr6 = customer.getString("RSRV_STR6", "").trim();
			if( null!=rsrvStr6 && !("".equals(rsrvStr6)) && rsrvStr6.split(",") != null && rsrvStr6.split(",").length >= 11 ){
				String devRead = rsrvStr6.split(",")[0].trim();//是否设备自动读取0:否1:是
				String readRuslt = rsrvStr6.split(",")[1].trim();//读取结果0:失败  1:成功
				String comparisonIs = rsrvStr6.split(",")[2].trim();//人像比对读取结果0:失败  1:成功
				String comparisonRuslt = rsrvStr6.split(",")[3].trim();//读取结果0:失败  1:成功
				String comparisonSeq = rsrvStr6.split(",")[4].trim();//seq
				String authenticityIs = rsrvStr6.split(",")[5].trim();//认证读取结果0:失败  1:成功
				String authenticityRuslt = rsrvStr6.split(",")[6].trim();//读取结果0:失败  1:成功
				String authenticitySeq = rsrvStr6.split(",")[7].trim();//seq
				String provenumIs = rsrvStr6.split(",")[8].trim();//一证五号读取结果0:失败  1:成功
				String provenumRuslt = rsrvStr6.split(",")[9].trim();//读取结果0:失败  1:成功
				String provenumSeq = rsrvStr6.split(",")[10].trim();//seq
				//String devRead = customer.getString("RSRV_TAG1", "").trim();//是否设备自动读取0:否1:是
				//String readRuslt = customer.getString("RSRV_TAG2", "").trim();//读取结果0:失败  1:成功
				this.insertGlobalMorePsptIdLog(mainTrade,provenumIs,provenumRuslt,provenumSeq,psptTypeCode,psptId);
				this.insertDocRealLog(mainTrade,authenticityIs,authenticityRuslt,authenticitySeq,psptTypeCode,psptId);
				this.insertReadCardLog(mainTrade,devRead,readRuslt,psptTypeCode,psptId);
				this.insertPicRetentionLog(mainTrade,comparisonSeq,psptTypeCode,psptId);
				this.insertContrastLog(mainTrade,comparisonIs,comparisonRuslt,comparisonSeq,psptTypeCode,psptId);
			}
			 
		}
		
	}
	
	/**
	 * REQ201911080010 关于实名入网办理日志留存的改造通知 - 一证五号平台查验  - TF_B_INQUIRY_OCFP 
	 *
	 * @author guonj
	 * @date 20200303
	 */
	public void  insertGlobalMorePsptIdLog(IData param,String provenumIs,String provenumRuslt,String provenumSeq,String psptTypeCode,String psptId){
		try {
			IData paramInfo=new DataMap();

			paramInfo.put("BUSPROFLO", param.getString("TRADE_ID",""));//业务办理流水
			   
			paramInfo.put("HOMEPROVIN", "898");//省份

			paramInfo.put("OPRTIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//时间

			paramInfo.put("CHANNELID",  1);//渠道

			paramInfo.put("JOBNUMBER",param.getString("UPDATE_STAFF_ID",""));//工号

			paramInfo.put("IDCARDTYPE", psptTypeCode);//证件类型

			paramInfo.put("IDCARDNUM",MD5Util.getEncryptedPwd(psptId));//证件号码（加密)

			paramInfo.put("PROVENUM", provenumIs);//是否进行一证五号查验

			paramInfo.put("COMPARISONID", provenumSeq);//验证流水ID

			paramInfo.put("RUSLT", provenumRuslt);//是否通过1：是，0：否

			paramInfo.put("REMARK", "insertGlobalMorePsptIdLog-业务编码为"+param.getString("TRADE_TYPE_CODE",""));

			Dao.insert("TF_B_INQUIRY_OCFP", paramInfo, Route.getCrmDefaultDb());
		} catch (Exception e) {
			log.debug("---test_guonj_AddLogFinishAction_insertGlobalMorePsptIdLog----"+e);
		}
	}


	/**
	 * REQ201911080010 关于实名入网办理日志留存的改造通知 - 证件真实性校验  - TF_B_DOC_REAL 
	 *
	 * @author guonj
	 * @date 20200303
	 */
	public void insertDocRealLog(IData param,String authenticityIs,String authenticityRuslt,String authenticitySeq,String psptTypeCode,String psptId){
		try {
			IData paramInfo=new DataMap();

			paramInfo.put("BUSPROFLO", param.getString("TRADE_ID",""));//业务办理流水

			paramInfo.put("HOMEPROVIN", "898");//省份

			paramInfo.put("OPRTIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//时间

			paramInfo.put("CHANNELID",1);//渠道

			paramInfo.put("SOURCE", "01");//能力来源

			paramInfo.put("OPCODE", param.getString("UPDATE_STAFF_ID",""));//操作员ID

			paramInfo.put("IDCARDTYPE",  psptTypeCode);

			paramInfo.put("IDCARDNUM", MD5Util.getEncryptedPwd(psptId));

			paramInfo.put("AUTHENTICITY",authenticityIs);//是否联网验证真实性1：是，0：否

			paramInfo.put("COMPARISONID", "0");//验证流水ID

			paramInfo.put("RUSLT", authenticityRuslt);//是否通过1：是，0：否

			paramInfo.put("REMARK", "insertContrastLog-业务编码为"+param.getString("TRADE_TYPE_CODE",""));


			Dao.insert("TF_B_DOC_REAL", paramInfo);
		} catch (Exception e) {
			log.debug("---test_guonj_AddLogFinishAction_insertDocRealLog----"+e);
		}

	}

	/**
	 * REQ201911080010 关于实名入网办理日志留存的改造通知 - 人证一致性校验   - TF_B_PEODOC_AG 
	 *
	 * @author guonj
	 * @date 20200303
	 */
	public void insertContrastLog(IData param ,String comparisonIs,String comparisonRuslt,String comparisonSeq,String psptTypeCode,String psptId){
		try {
			IData paramInfo=new DataMap();

			paramInfo.put("BUSPROFLO", param.getString("TRADE_ID",""));//业务办理流水

			paramInfo.put("HOMEPROVIN", "898");//省份

			paramInfo.put("OPRTIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//时间

			paramInfo.put("CHANNELID",1);//渠道

			paramInfo.put("SOURCE", "01");//能力来源

			paramInfo.put("OPCODE", param.getString("UPDATE_STAFF_ID",""));//操作员ID

			paramInfo.put("IDCARDTYPE",  psptTypeCode);

			paramInfo.put("IDCARDNUM", MD5Util.getEncryptedPwd(psptId));

			paramInfo.put("PHHOTONUM", "1");//留存照片数量

			paramInfo.put("COMPARISON",comparisonIs);//是否进行人像比对

			paramInfo.put("COMPARISONID", "");//人像比对请求流水ID

			paramInfo.put("SIMILARITY", "95");//比对相似度

			paramInfo.put("RUSLT", comparisonRuslt);//是否通过1：是，0：否

			paramInfo.put("REMARK", "insertContrastLog-业务编码为"+param.getString("TRADE_TYPE_CODE",""));

			Dao.insert("TF_B_PEODOC_AG", paramInfo);
		} catch (Exception e) {
			log.debug("---test_guonj_AddLogFinishAction_insertContrastLog----"+e);
		}

	}
 
	/**
	 * REQ201911080010 关于实名入网办理日志留存的改造通知 - 证件读取  - TF_B_READ_CARD 
	 *
	 * @author guonj
	 * @date 20200303
	 */
	public void insertReadCardLog(IData param ,String devRead,String readRuslt,String psptTypeCode,String psptId){
		try {
			IData paramInfo=new DataMap();

			paramInfo.put("BUSPROFLO", param.getString("TRADE_ID",""));//业务办理流水

			paramInfo.put("HOMEPROVIN", "898");//省份

			paramInfo.put("OPRTIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//时间

			paramInfo.put("CHANNELID",1);//渠道
			 		 
			paramInfo.put("OPCODE", param.getString("UPDATE_STAFF_ID",""));//操作员ID

			paramInfo.put("IDCARDTYPE",  psptTypeCode);//证件类型

			paramInfo.put("IDCARDNUM", MD5Util.getEncryptedPwd(psptId));//证件号码

			paramInfo.put("DEVREAD", devRead);//是否设备自动读取 0:否1:是

			paramInfo.put("RUSLT",readRuslt);//读取结果:0:失败 1:成功

 
			paramInfo.put("REMARK", "insertReadCardLog-业务编码为"+param.getString("TRADE_TYPE_CODE",""));

			Dao.insert("TF_B_READ_CARD", paramInfo);
		} catch (Exception e) {
			log.debug("---test_guonj_AddLogFinishAction_insertReadCardLog----"+e);
		}

	}
	
	/**
	 * REQ201911080010 关于实名入网办理日志留存的改造通知 - 照片留存  - TF_B_PIC_RETENTION 
	 *
	 * @author guonj
	 * @date 20200303
	 */
	public void insertPicRetentionLog(IData param ,String comparisonSeq,String psptTypeCode,String psptId){
		try {
			IData paramInfo=new DataMap();

			paramInfo.put("BUSPROFLO", param.getString("TRADE_ID",""));//业务办理流水

			paramInfo.put("HOMEPROVIN", "898");//省份

			paramInfo.put("OPRTIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//时间

			paramInfo.put("CHANNELID",1);//渠道

			paramInfo.put("OPCODE", param.getString("UPDATE_STAFF_ID",""));//操作员ID

			paramInfo.put("IDCARDTYPE",  psptTypeCode);//证件类型

			paramInfo.put("IDCARDNUM", MD5Util.getEncryptedPwd(psptId));//证件号码

			paramInfo.put("PNNUM", "2");//身份证正反面（数量）
			paramInfo.put("PEOPICNUM", "0");//预约人像照片（数量）--实体渠道填0
			paramInfo.put("BAREPICNUM", "1");//正面免冠照（数量）--实体渠道受理留存1张，
			
			paramInfo.put("SCENEPIC", "1");//是否现场拍照--实体渠道受理1
			paramInfo.put("SOURCE", "01");//能力来源--01在线/02国政通/03省公司/00其它

			paramInfo.put("COMPARISONID",comparisonSeq);//人证一致性查验流水号--人像比对流水号?
			paramInfo.put("INSTIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());//查验时间
 
			paramInfo.put("REMARK", "insertPicRetentionLog-业务编码为"+param.getString("TRADE_TYPE_CODE",""));

			Dao.insert("TF_B_PIC_RETENTION", paramInfo);
		} catch (Exception e) {
			log.debug("---test_guonj_AddLogFinishAction_insertPicRetentionLog----"+e);
		}

	}
	/*
	 * 00	身份证件
	 * 02	外籍护照
	 * 04	军官证
	 * 05	武装警察身份证
	 * 10	临时居民身份证
	 * 11	户口簿
	 * 12	港澳居民往来内地通行证
	 * 13	台湾居民来往大陆通行证	所有字母均大写
	 * 14	外国人永久居留证	所有字母均大
	 * 15	边境通行证
	 * 16	港澳台居民居住证
	 * 99	其他证件
	 */
	private String revertPsptTypeCode(String psptTypeCode ){
		if( "".equals(psptTypeCode) ){
			return "";
		}else if( "0".equals(psptTypeCode) ){
			return "00";
		}else if ( "A".equals(psptTypeCode) ){
			return "02";
		}else if ( "C".equals(psptTypeCode) ){
			return "04";
		}/*else if ( "A".equals(psptTypeCode) ){
			return "05";
		}else if ( "A".equals(psptTypeCode) ){
			return "10";
		}*/else if ( "2".equals(psptTypeCode) ){
			return "11";
		}else if ( "O".equals(psptTypeCode) ){
			return "12";
		}else if ( "N".equals(psptTypeCode) ){
			return "13";
		}else if ( "P".equals(psptTypeCode) ){
			return "14";
		}/*else if ( "A".equals(psptTypeCode) ){
			return "15";
		}*/else if ( "Q".equals(psptTypeCode) ){
			return "16";
		}else{
			return "99";
		}
		
	}
	
}
