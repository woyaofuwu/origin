package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import org.apache.commons.lang.RandomStringUtils;
import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class RandomPWDBean extends CSBizBean {
	
	/**
	 * 校验入参
	 * @param data
	 * @throws Exception
	 */
	public void checkParam(IData data) throws Exception{
		if (data.getString("OPR_NUMB") == null
				|| data.getString("OPR_NUMB").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_299);
		}
		if (data.getString("CHANNEL_ID") == null
				|| data.getString("CHANNEL_ID").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1149);
		}
		if (data.getString("OPR_NUMB") == null
				|| data.getString("OPR_NUMB").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_299);
		}
	}
	
	 // 产生用户id后九位的
    public static String genUserId(String userId) throws Exception
    {
        String userIdTemp = "";
        if (userId.length() >= 9) // 加密那里是userid的后九位，不足前面补零
            userIdTemp = userId.substring(userId.length() - 9, userId.length());
        else
        {
            for (int i = 0; i < 9 - userId.length(); i++)
            {
                userIdTemp += "0";
            }
            userIdTemp += userId;
        }
        return userIdTemp;
    }
	
	/**
	 * 生成随机6位密码
	 * @param data
	 * @throws Exception
	 */
	public IData applyPWD(IData data)throws Exception
	{
		checkParam(data);
		IData param = new DataMap();
		String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}
		String userId = userInfo.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		String passwd = RandomStringUtils.randomNumeric(6);
		
		String newpasswd = Encryptor.fnEncrypt(passwd, genUserId(userId));// 密文密码
		String notice = "您本次登录的随机密码为"+passwd+",请保密，并确认是本人操作[中国移动]";
		String registTime= SysDateMgr.getSysTime();
		int effectiveTime = 1800;
		String endDate = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime, "yyyy-MM-dd HH:mm:ss"), effectiveTime),"yyyy-MM-dd HH:mm:ss");
		
		data.put("USER_ID", userId);
		data.put("PARTITION_ID",partition_id);
		data.put("PASSWORD", newpasswd);
		data.put("PWD_FLAG", "0");//2015-07-30 CHENXY3 修改 先默认给0
		data.put("PWD_TYPE", "02");
		data.put("PWD_ACTIVE_TIME", effectiveTime);
		data.put("PWD_START_TIME", registTime);
		data.put("PWD_END_TIME", endDate);
		data.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE",""));
		data.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID",""));
		data.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID",""));
		data.put("UPDATE_TIME", registTime);
		/**先置为无效 chenxy3 2015-09-10 使随机密码只保留最近一条有效数据*/
		this.updPWDEndTime(data);
		
		if(!Dao.insert("TF_B_PWD_LOG", data)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "随机密码日志表生成出错！");
		}
		//发送短信通知
		IData inparam = new DataMap();
        inparam.put("NOTICE_CONTENT", notice);
        inparam.put("RECV_OBJECT", serialNumber);
        inparam.put("RECV_ID", serialNumber);
        inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
        inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
        inparam.put("REMARK", "海南移动业务办理校验随机验证短信通知");
        SmsSend.insSms(inparam);
        
        param.put("PASSWORD_RSLT", "0");//0 成功   1 失败

        return param;
	}
	
	public IData checkPWD(IData data)throws Exception
	{
		String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}
		String userId = userInfo.getString("USER_ID");
		String passwd = data.getString("PASSWORD");
		String newpasswd = Encryptor.fnEncrypt(passwd, genUserId(userId));// 密文密码
		
		IDataset dataset = UserIdentInfoQry.queryPWD(userId, newpasswd, serialNumber);
		if (IDataUtil.isEmpty(dataset)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "随机密码不正确！");
		}
		
		data.put("PASSWORD_RSLT", "0");//0 成功
	    return data;
	}
	
	
	/**
	 * chenxy3 
	 * 2015-09-10
     * 将用户的密码置为无效
     * */
    public static void updPWDEndTime(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER")); 
    	param.put("PWD_TYPE", params.getString("PWD_TYPE"));  
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_B_PWD_LOG t ");
    	sql.append(" set t.pwd_end_time=sysdate,t.pwd_flag='1',t.update_time=sysdate ");
    	sql.append(" where t.SERIAL_NUMBER = :SERIAL_NUMBER and t.pwd_type=:PWD_TYPE  ");
    	sql.append(" and sysdate between t.pwd_start_time and t.pwd_end_time ");
        Dao.executeUpdate(sql, param);
    }
	
}
