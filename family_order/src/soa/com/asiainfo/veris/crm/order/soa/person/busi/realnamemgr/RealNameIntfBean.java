package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.FtpUtil;
import com.ailk.common.util.Utility;
import com.ailk.database.util.SQLParser;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl.ChnlInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.util.RealNameMsDesPlus;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.List;

public class RealNameIntfBean extends CSBizBean {
	
	/**
	 * 作用：下发采集验证工单
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData realNameCreateTrade(IData data) throws Exception {
		if (data.getString("PROV_CODE") == null
				|| data.getString("PROV_CODE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_543);
		}
		if (data.getString("CHANNEL_ID") == null
				|| data.getString("CHANNEL_ID").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1149);
		}
		if (data.getString("MS_OP_CODE") == null
				|| data.getString("MS_OP_CODE","").trim().equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1150);
        } else {
            //REQ201609260003 关于修改BOSS与在线公司实名认证接口传参的需求，将MS_OP_CODE重新赋值为工号对应的手机号码，如找不到手机，则保留赋值工号
            IData query = new DataMap();
            query.put("STAFF_ID", data.getString("MS_OP_CODE").trim());
            IDataset ds = Dao.qryByCode("TD_M_STAFF", "SEL_VALID_BY_PK", query);
            if (ds != null && ds.size() == 1) {
                String serialNumber = ds.getData(0).getString("SERIAL_NUMBER", "").trim();
                if (serialNumber.length() > 0) {
                    data.put("MS_OP_CODE", serialNumber);
                }
            }
        }
		if (data.getString("MS_TEL") == null
				|| data.getString("MS_TEL").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1151);
		}
		if (data.getString("BUSI_TYPE") == null
				|| data.getString("BUSI_TYPE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1152);
		}

		IDataset res=ChnlInfoQry.getGlobalChlId(data.getString("TRADE_DEPART_ID"));
		if(res != null && res.size() > 0)
		{
			data.put("CHANNEL_ID", res.getData(0).getString("GLOBAL_CHNL_ID", ""));
		}
		else
		{
			data.put("CHANNEL_ID", data.getString("TRADE_DEPART_ID", ""));
		}
		//REQ201606200001 关于接入APP活体检测的需求
		data.put("VERIFYLEVEL", data.getString("VERIFYLEVEL",""));
		//活体检测时VERIFYLEVEL传1001000000000000
		
		/**获取开关数据 modify by zouyi 20150525**/
		IData reqType = getRequstType();
		if("1001000000000000".equals(data.getString("VERIFYLEVEL"))||"1100000000000000".equals(data.getString("VERIFYLEVEL"))){
		    reqType.put("PARA_CODE1", "1");
		}
		data.put("para_code1", reqType.getString("PARA_CODE1"));//PARA_CODE1业务开关1打开，其他关闭
		data.put("para_code2", reqType.getString("PARA_CODE2"));//互联网 KIND_ID
		data.put("para_code3", reqType.getString("PARA_CODE3"));//网状网 KIND_ID
		/**end**/

		IData outparams = IBossCall.callIbossRealNameCreateTrade(data).getData(0);
		
		outparams.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
		outparams.put("CHANNEL_ID", data.getString("CHANNEL_ID"));
		outparams.put("PROV_CODE", data.getString("PROV_CODE"));
		outparams.put("BUSI_TYPE", data.getString("BUSI_TYPE"));
		outparams.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		outparams.put("UPDATE_TIME", SysDateMgr.getSysDate());
		outparams.put("STATE", "1");
		outparams.put("SERIAL_NUMBER", data.getString("BILL_ID"));
		//REQ201606200001 关于接入APP活体检测的需求
		String verifyLevel = data.getString("VERIFYLEVEL","");
		String verifyStep = "1"; // 区分是下单的工单还是认证比对的工单
		if(StringUtils.isBlank(verifyLevel) || "1000000000000000".equals(verifyLevel)) {
			verifyLevel = "1000000000000000";
		} else {
            verifyStep = "2";
        }
		outparams.put("RSRV_STR1", verifyLevel);
		outparams.put("RSRV_TAG1", verifyStep);

		if ("".equals(outparams.getString("TRANSACTION_ID", ""))) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1153);
		} else {// 如果正常返回，在boss侧插入记录
			Dao.insert("TF_F_REALNAME_INFO", outparams,Route.CONN_CRM_CEN);// 插入采集工单信息
			//REQ202002270010  [实名制检查整改]关于在线公司人像比对回传流水部分渠道没有记录优化
			IData reqInfo = new DataMap();
			reqInfo.put("SERIAL_NUMBER", data.getString("BILL_ID"));
			reqInfo.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
			reqInfo.put("DEAL_TAG", "0");
			reqInfo.put("CHANNEL_TYPE", "1");
			reqInfo.put("RSRV_TAG1", "1");
			reqInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
			reqInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
			Dao.insert("TD_B_PICTURE_INFO", reqInfo, Route.CONN_CRM_CEN);
		}

		return outparams;
	}
	
	/**
	 * 获取请求方式配置  add by zouyi 20150525
	 * @return
	 * @throws Exception
	 */
	public IData getRequstType() throws Exception{
		IData inparams = new DataMap();
		inparams.put("SUBSYS_CODE", "CSM");
		inparams.put("PARAM_ATTR", "1522");
		inparams.put("PARAM_CODE", "SEND_BILL");
		
		IDataset ds = Dao.qryByCode("TD_S_COMMPARA", "SEL_PARAM_BY_CODE", inparams);
		
		if ((ds != null) && (ds.size() > 0)) {
//			String para_code1 = ds.getData(0).getString("para_code1");
//			String para_code2 = ds.getData(0).getString("para_code2");
//			String para_code3 = ds.getData(0).getString("para_code3");

			return ds.getData(0);
		}
		
		return null;
	}
	
	/**
	 * 作用：采集验证工单处理结果反馈
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData realNameResult (IData data) throws Exception{
		if (data.getString("TRANSACTION_ID") == null
	                || data.getString("TRANSACTION_ID").equals("")) {
			 CSAppException.apperr(CrmCommException.CRM_COMM_1154);			
	    }
		if (data.getString("PROV_CODE") == null
			|| data.getString("PROV_CODE").equals("")) {
			 CSAppException.apperr(CrmCommException.CRM_COMM_543);
	    }
		if (data.getString("CHANNEL_ID") == null
				|| data.getString("CHANNEL_ID").equals("")) {
			 CSAppException.apperr(CrmCommException.CRM_COMM_1149);
	    }
		if (data.getString("CUST_NAME") == null
				|| data.getString("CUST_NAME").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1155);
	    }
		if (data.getString("CUST_CERT_NO") == null
				|| data.getString("CUST_CERT_NO").equals("")) {
			CSAppException.apperr(CustException.CRM_CUST_112);
	    }
		if (data.getString("STAFF_ID") == null
				|| data.getString("STAFF_ID").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1156);
	    }
		if (data.getString("VERIF_RESULT") == null
				|| data.getString("VERIF_RESULT").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1157);
	    }
		
		data.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		if ("1".equals(data.getString("VERIF_RESULT"))){
			data.put("STATE", "0");
		}
		data.put("UPDATE_TIME", SysDateMgr.getSysTime());
		data.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		data.put("UPDATE_DEPART_ID", getVisit().getDepartId());
		data.put("PSPT_ID", data.getString("CUST_CERT_NO"));
		data.put("PSPT_ADDR", data.getString("CUST_CERT_ADDR"));
		data.put("SEX", data.getString("GENDER"));
		/** 新增应答信息:add by zouyi 20150525**/
		data.put("RETURN_CODE", "0000");
		data.put("RETURN_MESSAGE", "SUCCESS");


		// 根据TRANSACTION_ID将TF_F_REALNAME_INFO表中此条记录的所有数据取出来。
		IData realInfoParam = new DataMap();
		realInfoParam.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
		SQLParser parser = new SQLParser(realInfoParam);
		parser.addSQL("SELECT * FROM TF_F_REALNAME_INFO ");
		parser.addSQL(" WHERE TRANSACTION_ID = :TRANSACTION_ID");
		IDataset realInfoSet = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
		if (IDataUtil.isEmpty(realInfoSet)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRANSACTION_ID未找到TF_F_REALNAME_INFO信息");
		}
		IData realInfo = realInfoSet.getData(0);
		realInfo.putAll(data);

		Dao.update("TF_F_REALNAME_INFO", realInfo, new String[]{"TRANSACTION_ID"}, new String[]{data.getString("TRANSACTION_ID")},Route.CONN_CRM_CEN);

		//REQ202002270010  [实名制检查整改]关于在线公司人像比对回传流水部分渠道没有记录优化
		IData upInfo = new DataMap();
		upInfo.put("CARD_ID", data.getString("CUST_CERT_NO"));
		upInfo.put("PIC_NNAME_Z", data.getString("PIC_NAME_Z"));
		upInfo.put("PIC_NNAME_F", data.getString("PIC_NAME_F"));
		upInfo.put("PIC_NNAME_R", data.getString("PIC_NAME_T"));
		upInfo.put("RSRV_TAG1", "1");
		upInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
		upInfo.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
		Dao.executeUpdateByCodeCode("TD_B_PICTURE_INFO","UPD_PICTURE_INFO_BY_TRANSATION", upInfo,Route.CONN_CRM_CEN);

		return data;
	}
	
	/**
	 * 作用：采集验证工单处理结果接口查询
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData resultQuery (IData data) throws Exception{
		IDataset tempIdata = new DatasetList();
		if (data.getString("TRANSACTION_ID") == null
	               || data.getString("TRANSACTION_ID").equals("")) {
			 CSAppException.apperr(CrmCommException.CRM_COMM_1154);
	    }
		SQLParser parser = new SQLParser(data);
		parser.addSQL("  SELECT * FROM TF_F_REALNAME_INFO A " );
		parser.addSQL("  WHERE A.TRANSACTION_ID= :TRANSACTION_ID " );
		//parser.addSQL("  AND A.STATE='0'" );
		
		tempIdata = Dao.qryByParse(parser,Route.CONN_CRM_CEN);

	    IData result=new DataMap();
		if(tempIdata.size() > 0){
			result=tempIdata.getData(0);
		}
		return result;
	}	
	
	/**
	 * 作用：客户端登陆认证
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData doLogin (IData data) throws Exception{
		
		IData rtnData = new DataMap();
		
		/** 入参 **/
		String staffId = data.getString("INPUT_CODE", "");
		String passwd = data.getString("INPUT_PWD", "");
		String loginType = data.getString("LOGIN_TYPE", "");
		String imei = data.getString("IMEI", "");
		String sn = data.getString("TELEPHONE", "");
		String randomCode = null;
		String mphone = "";
		String channel = "";
		String phoneType = "";


		
		/** 入参验证 **/
		if (data.getString("PROV_CODE") == null
				|| data.getString("PROV_CODE").equals("")) {
			rtnData.put("RETURN_CODE", "2999");
			rtnData.put("RETURN_MESSAGE", "省编码不能为空！");
			rtnData.put("X_RESULTCODE", "-1");
			rtnData.put("X_RSPCODE", "2998");
			rtnData.put("X_RESULTINFO", "省编码不能为空！");
			rtnData.put("TELEPHONE", mphone);
			return rtnData;
		}
		if (data.getString("TRANSACTION_ID") == null
				|| data.getString("TRANSACTION_ID").equals("")) {
			rtnData.put("RETURN_CODE", "2999");
			rtnData.put("RETURN_MESSAGE", "全网唯一操作流水号不能为空！");
			rtnData.put("X_RESULTCODE", "-1");
			rtnData.put("X_RSPCODE", "2998");
			rtnData.put("X_RESULTINFO", "全网唯一操作流水号不能为空！");
		 	rtnData.put("TELEPHONE", mphone);
			return rtnData;
		}
		if (loginType == null || loginType.equals("")) {
			rtnData.put("RETURN_CODE", "2999");
			rtnData.put("RETURN_MESSAGE", "登录方式不能为空！");
			rtnData.put("X_RESULTCODE", "-1");
			rtnData.put("X_RSPCODE", "2998");
			rtnData.put("X_RESULTINFO", "登录方式不能为空！");
			rtnData.put("TELEPHONE", mphone);
			return rtnData;
		} else if ("3".equals(loginType)) {//电话号码+短信随机码的登录方式
			if (StringUtils.isEmpty(sn)) {
				rtnData.put("RETURN_CODE", "2999");
				rtnData.put("RETURN_MESSAGE", "手机号码不能为空！");
				rtnData.put("X_RESULTCODE", "CRM_COMM_1033");
				rtnData.put("X_RSPCODE", "2998");
				rtnData.put("X_RESULTINFO", "手机号码不能为空！");
				return rtnData;
			} else {
				IData inparams = new DataMap();
				inparams.put("SERIAL_NUMBER", sn);
			
				IDataset staffInfoset = StaffInfoQry.getStaffInfoByNumber(inparams);
				if (staffInfoset != null && staffInfoset.size() > 0) {
					String managerFlag  = staffInfoset.getData(0).getString("CUST_MANAGER_FLAG");
					phoneType = (StringUtils.isEmpty(managerFlag) || "0".equals(managerFlag))?"1":"2";
					channel = staffInfoset.getData(0).getString("DEPART_ID");
					rtnData.put("CHANNEL_ID", channel);
					rtnData.put("PHONE_TYPE", phoneType);
					/** 新应答信息 add by zouyi **/
					rtnData.put("RETURN_CODE", "0000");
					rtnData.put("RETURN_MESSAGE", "SUCCESS");
				} else {
					rtnData.put("RETURN_CODE", "2001");
					rtnData.put("RETURN_MESSAGE", "找不到员工信息");
					rtnData.put("X_RESULTCODE", "CRM_COMM_1033");
					rtnData.put("X_RSPCODE", "2998");
					rtnData.put("X_RESULTINFO", "员工登录失败，请输入正确员工手机号码!");
					rtnData.put("TELEPHONE", mphone);
				}
			}

		} else if ("2".equals(loginType)) {//2：BOSS工号+密码+短信随机码的登录方式
			IData inparams = new DataMap();
			inparams.put("PASSWORD", passwd);
			inparams.put("STAFF_ID", staffId);// 需要确认账号类型
			inparams.put("X_GET_MODE", "0");
			inparams.put("MTYPE", "WAP");
			
			IDataset staffInfoset = StaffInfoQry.getStaffInfo(inparams);
			if (staffInfoset != null && staffInfoset.size() > 0) {
				mphone = staffInfoset.getData(0).getString("SERIAL_NUMBER");
				channel = staffInfoset.getData(0).getString("DEPART_ID");
			}
		if (staffId == null || staffId.equals("")) {
			rtnData.put("RETURN_CODE", "2999");
			rtnData.put("RETURN_MESSAGE", "帐号不能为空！");
			rtnData.put("X_RESULTCODE", "CRM_COMM_1033");
			rtnData.put("X_RSPCODE", "2998");
			rtnData.put("X_RESULTINFO", "帐号不能为空！");
			rtnData.put("TELEPHONE", mphone);
			return rtnData;
		}
		if (passwd == null || passwd.equals("")) {
			rtnData.put("RETURN_CODE", "2001");
			rtnData.put("RETURN_MESSAGE", "密码不能为空！");
			rtnData.put("X_RESULTCODE", "CRM_COMM_1033");
			rtnData.put("X_RSPCODE", "2998");
			rtnData.put("X_RESULTINFO", "密码不能为空！");
			rtnData.put("TELEPHONE", mphone);
			return rtnData;
		}

		/** 入参验证 **/

		/** 应答信息 **/
		IData tempData = new DataMap();

		IDataOutput output = ServiceFactory.call("SYS_Security_GetStaffInfo",
				createDataInput(inparams));
		IDataset ds = output.getData();
		if ((ds != null) && (ds.size() > 0)) {
			tempData = ds.first();
			String validflag = tempData.getString("VALIDFLAG", "0");

			if ("1".equals(validflag)) {
				rtnData.put("RETURN_CODE", "2999");
				rtnData.put("RETURN_MESSAGE", "账号无权限");
				rtnData.put("X_RESULTCODE", "CRM_COMM_1032");
				rtnData.put("X_RSPCODE", "2998");
				rtnData.put("X_RESULTINFO", "该员工归属的部门已失效!");
				rtnData.put("TELEPHONE", mphone);
				return rtnData;
			}
			String endDate = tempData.getString("END_DATE");
			if ((endDate != null)
					&& (!"".equals(endDate))
					&& (TimeUtil.encodeTimestamp(endDate).getTime() < TimeUtil
							.currentTimeMillis(false))) {
				rtnData.put("RETURN_CODE", "2999");
				rtnData.put("RETURN_MESSAGE", "账号无权限");
				rtnData.put("X_RESULTCODE", "CRM_COMM_1032");
				rtnData.put("X_RSPCODE", "2998");
				rtnData.put("X_RESULTINFO", "该员工工号已经过期!");
				rtnData.put("TELEPHONE", mphone);
				return rtnData;
			}
			
			/** 原应答信息 **/
			rtnData.put("CHANNEL_ID", channel);
			rtnData.put("TELEPHONE", mphone);
			/** 新应答信息 add by zouyi **/
			rtnData.put("RETURN_CODE", "0000");
			rtnData.put("RETURN_MESSAGE", "SUCCESS");

		} else {
			/** 新应答信息 add by zouyi **/
			rtnData.put("RETURN_CODE", "2001");
			rtnData.put("RETURN_MESSAGE", "密码错误");
			rtnData.put("X_RESULTCODE", "CRM_COMM_1033");
			rtnData.put("X_RSPCODE", "2998");
			rtnData.put("X_RESULTINFO", "员工登录失败，请输入正确员工号和密码!");
			rtnData.put("TELEPHONE", mphone);
			return rtnData;
		}

		} else {//目前只支持2：BOSS工号+密码+短信随机码的登录方式  3：电话号码+短信随机码的登录方式
			rtnData.put("RETURN_CODE", "2999");
			rtnData.put("RETURN_MESSAGE", "登录方式不正确！");
			rtnData.put("X_RESULTCODE", "-1");
			rtnData.put("X_RSPCODE", "2998");
			rtnData.put("X_RESULTINFO", "登录方式不正确！");
			rtnData.put("TELEPHONE", mphone);
			return rtnData;
		}
		return rtnData;
	}
	
	public IDataset qryRealNameInfo() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TRANSACTION_ID,ACCEPT_MONTH, PROV_CODE,CHANNEL_ID, SERIAL_NUMBER, CUST_NAME, PSPT_ID, STAFF_ID, VERIF_RESULT,  ");
		sql.append(" NO_PASS_TYPE,RESULT_CAUSE, PSPT_ADDR, SEX, NATION, BIRTHDAY, ISSUING_AUTHORITY, CERT_VALIDDATE,  ");
		sql.append(" CERT_EXPDATE, BUSI_TYPE, PIC_NAME_T, PIC_NAME_Z, PIC_NAME_F, STATE,  ");
		sql.append(" to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_DATE1,RSRV_DATE3, RSRV_DATE2, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, RSRV_NUM1 ");
		sql.append(" FROM TF_F_REALNAME_INFO D ");
		sql.append(" WHERE 1=1 ");
		sql.append(" AND D.RSRV_TAG3 = '0' ");
		sql.append(" AND D.STATE = '0' ");
		sql.append(" AND D.RSRV_TAG1 = '2' ");
		sql.append(" AND UPDATE_TIME > TO_DATE('2018-06-03','YYYY-MM-DD') ");

		return Dao.qryBySql(sql, new DataMap(),Route.CONN_CRM_CEN);
	}
	
	public void updateSyncState(String status,String transActionId,String resultInfo) throws Exception
	{
		IData param = new DataMap();
		param.put("STATUS", status);
		param.put("TRANSACTION_ID", transActionId);
		param.put("RSRV_STR3", resultInfo);
		
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TF_F_REALNAME_INFO SET RSRV_TAG3 =:STATUS,RSRV_STR3 =:RSRV_STR3 WHERE TRANSACTION_ID = :TRANSACTION_ID ");
		Dao.executeUpdate(sql, param,Route.CONN_CRM_CEN);
	}
	
	/**
	 * AEE 调用同步图片信息给东软
	 * @throws Exception
	 */
	public IData synRealNamePicInfo(IData datas) throws Exception
	{
		IDataset realNameInfos = this.qryRealNameInfo();
		if(IDataUtil.isNotEmpty(realNameInfos))
		{
			for(int i=0,size=realNameInfos.size();i<size;i++)
			{
				IData data = realNameInfos.getData(i);
				String tradeId = data.getString("TRANSACTION_ID");
				try
				{
					String rsrvStr2 = data.getString("RSRV_STR2");
					// 调用接口
					IData inParam = new DataMap();
					inParam.put("trade_id", rsrvStr2.split("\\|")[0]);
					inParam.put("business_code", rsrvStr2.split("\\|")[1]);
					inParam.put("business_name", "开户");
					inParam.put("customer_number", data.getString("SERIAL_NUMBER"));
					inParam.put("customer_name", data.getString("CUST_NAME"));
					inParam.put("trade_staff_id", data.getString("UPDATE_STAFF_ID"));
					inParam.put("trade_staff_name", data.getString("UPDATE_STAFF_ID"));
					inParam.put("org_id", data.getString("UPDATE_STAFF_ID"));
					inParam.put("org_name", data.getString("UPDATE_STAFF_ID"));
					inParam.put("bill_type", "1");
					inParam.put("verify_mode", "1");
					IDataset ds = StaffInfoQry.qryStaffInfoByStaffId(data.getString("UPDATE_STAFF_ID"));
					if (ds != null && ds.size() == 1) {
						IData param = new DataMap();
						inParam.put("trade_staff_name", ds.getData(0).getString("STAFF_NAME"));
						inParam.put("org_id", ds.getData(0).getString("DEPART_ID"));
						ds = Dao.qryByCode("TD_M_DEPART", "SEL_ALL_BY_PK", param);
						if (ds != null && ds.size() == 1) {
							inParam.put("org_name", ds.getData(0).getString("DEPART_NAME"));
						}
					}
					inParam.put("op_time", SysDateMgr.getSysTime());
					IDataset pics = new DatasetList();
					if(!StringUtils.isEmpty(data.getString("PIC_NAME_T")))
					{
						IData picT = new DataMap();
						picT.put("image_name", this.splistPicName(data.getString("PIC_NAME_T")));
						picT.put("image_content", this.readFtpImage(data.getString("PIC_NAME_T")));
						pics.add(picT);
					}
					
					if(!StringUtils.isEmpty(data.getString("PIC_NAME_Z")))
					{
						IData picZ = new DataMap();
						picZ.put("image_name", this.splistPicName(data.getString("PIC_NAME_Z")));
						picZ.put("image_content", this.readFtpImage(data.getString("PIC_NAME_Z")));
						pics.add(picZ);
					}
					
					if(!StringUtils.isEmpty(data.getString("PIC_NAME_F")))
					{
						IData picF = new DataMap();
						picF.put("image_name", this.splistPicName(data.getString("PIC_NAME_F")));
						picF.put("image_content", this.readFtpImage(data.getString("PIC_NAME_F")));
						pics.add(picF);
					}
					inParam.put("image", pics);

					JSONObject jSONObject = JSONObject.fromObject(inParam);

					String contentJson = jSONObject.toString();
					IData ibossData = new DataMap();
					IData result = new DataMap();
					ibossData.put("buffer", contentJson);

					String strResult = sendAutoAudit(contentJson);
					JSONObject res = JSONObject.fromObject(strResult);
					String resCode = res.getString("result");
					result.put("RESULT_CODE", resCode);
					if(StringUtils.equals("1", resCode))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "保存失败，图片大于500K!");
					}
					if(StringUtils.equals("2", resCode))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "保存失败，当月APP保存图片数超过100万");
					}
					if(StringUtils.equals("3", resCode))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "保存失败，其他错误!");
					}
					this.updateSyncState("1", tradeId,"OK");
				}
				catch(Exception e)
				{
					String errorMsg =  Utility.getStackTrace(Utility.getBottomException(e)).toString();
	                if(errorMsg != null && !"".equals(errorMsg))
	                	if(errorMsg.length() > 500)
	                		errorMsg = errorMsg.substring(0,300);
					this.updateSyncState("2", tradeId,errorMsg);
				}
			}
		}
		return new DataMap();
	}
	
	private String splistPicName(String picName)throws Exception
	{
		if(!StringUtils.isEmpty(picName) && picName.length()>8)
		{
			return picName.substring(8);
		}
		return picName;
	}
	/**
	 *
	 */
	private String sendAutoAudit(String str) throws Exception{
		
		byte[] requestBytes;  
		requestBytes = str.getBytes("utf-8");  
		
		String url = BizEnv.getEnvString("crm.realinfo.syn.url");
		 
		HttpClient httpClient = new HttpClient();  
		PostMethod postMethod = new PostMethod(url);  
		//postMethod.setRequestHeader("SOAPAction", "http://tempuri.org/GetMiscInfo");//Soap Action Header!  
		  
		InputStream inputStream = new ByteArrayInputStream(requestBytes, 0, requestBytes.length);  
		RequestEntity requestEntity = new InputStreamRequestEntity(inputStream, requestBytes.length, "application/soap+xml; charset=utf-8");  
		postMethod.setRequestEntity(requestEntity);  
		
		HttpClientParams params = new HttpClientParams();
		params.setSoTimeout(10000);
		httpClient.setParams(params);
		int state = httpClient.executeMethod(postMethod);  
		  
		InputStream soapResponseStream = postMethod.getResponseBodyAsStream();  
		InputStreamReader inputStreamReader = new InputStreamReader(soapResponseStream);  
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
		  
		String responseLine = "";  
		String soapResponseInfo = "";  
		while((responseLine = bufferedReader.readLine()) != null) {  
		    soapResponseInfo = soapResponseInfo + responseLine;  
		}  
		return soapResponseInfo;
	}
	
	
	public String readFtpImage(String imgName) throws Exception {
		String imgBase64 = "";
		IDataset commparas = CommparaInfoQry.getCommpara("CSM", "2554", "PZ_PIC_CONFIG", "0898");
		if(IDataUtil.isEmpty(commparas)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取图片服务器存储路径配置失败!");
		}
		//IData config = ftpFileAction.getFtpConfig(ftpSite, getVisit());
		String ibossIP = commparas.first().getString("PARA_CODE1","");//config.getString("FTP_URL");
		String ibossUser = commparas.first().getString("PARA_CODE2","");//config.getString("ACCT_USR");
		String ibossPassword = commparas.first().getString("PARA_CODE3","");//config.getString("ACCT_PWD");
		String rootPath = commparas.first().getString("PARA_CODE4","");//config.getString("ROOT_PATH");
		String ftpSubFolder = commparas.first().getString("PARA_CODE5","");//config.getString("ROOT_PATH");
		boolean isEncry = commparas.first().getBoolean("PARA_CODE6",false);//false 未加密 true 已经加密
		String encryptCipher = commparas.first().getString("PARA_CODE7","PCTCS");
		
		FtpUtil ftpUtil = new FtpUtil(ibossIP, ibossUser, ibossPassword, rootPath);
		try{
			ftpUtil.changeDirectory(rootPath);
			ftpUtil.changeDirectory(ftpSubFolder);
		}catch (Exception e){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取图片服务器存储路径失败！"+e.getMessage());
		}
		
		List<String> listFiles = ftpUtil.getFileList(".");
		if(!listFiles.isEmpty()){
			for(String fileName : listFiles){
				if(fileName.equals(imgName)){
					InputStream file = ftpUtil.getFileStream(fileName);
					if(file != null){
						String imgBase64Src ="";

						imgBase64Src = getfileBase64(file,isEncry,encryptCipher);
						imgBase64 = imgBase64Src;
					}	
					break;
				}
			}
		}
		return imgBase64;
	}
	
	
	public  String getfileBase64(InputStream input, boolean isEncry ,String encryptCipher) throws Exception {
		 byte[] bytes = null;
        ByteArrayOutputStream swapStream = null;
        try {
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];// buff用于存放循环读取的临时数据
            int rc = 0;
            while ((rc = input.read(buff, 0, 1024)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            bytes = swapStream.toByteArray();
            swapStream.close();
            input.close();
            input = null;
            swapStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (swapStream != null) {
                try {
					swapStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }

       String imgStr = new BASE64Encoder().encode(bytes);
       if(isEncry && StringUtils.isNotEmpty(encryptCipher)){
       	 RealNameMsDesPlus desPlus = new RealNameMsDesPlus(encryptCipher);
       	 imgStr = imgStr.replaceAll("\r|\n", "");
       	 imgStr= desPlus.decrypt(imgStr);
       }  
      
		return imgStr;
	}	
	
}
