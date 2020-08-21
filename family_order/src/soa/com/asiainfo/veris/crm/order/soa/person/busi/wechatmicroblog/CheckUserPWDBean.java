package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import java.util.Calendar;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.custservice.LockUserPwdNewBean;

public class CheckUserPWDBean extends CSBizBean{
	/**
	 * 客户密码鉴权
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData checkUserPWD4HL(IData data)throws Exception
	{
		IData result = new DataMap();
    	String sn = data.getString("SERIAL_NUMBER");
    	String identCodeType = data.getString("IDENT_CODE_TYPE");//用户身份凭证类型
    	
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String userid = userInfo.getString("USER_ID");
        String userpasswd = userInfo.getString("USER_PASSWD");
        String eparchyCode = userInfo.getString("EPARCHY_CODE","");
		String passWord = data.getString("PASSWORD");//用户密码
		String partition_id = userid.substring(userid.length() - 4,userid.length());
		
		data.put("PARTITION_ID",partition_id);
        data.put("USER_ID", userid);
        if ("03".equals(identCodeType)){
        	LockUserPwdNewBean lockBean = (LockUserPwdNewBean) BeanManager.createBean(LockUserPwdNewBean.class);

            // 判断用户密码是否已经锁定
            int num = lockBean.getOverPlusErrNum(userid, eparchyCode);
            if (num <= 0)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1029);
            }

            if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
            {
                    CSAppException.apperr(CrmUserException.CRM_USER_89);
            }

            if (data.getString("PASSWORD").length() != 6)// 密码长度不正确
            {
            	CSAppException.apperr(CrmUserException.CRM_USER_110);

             }

            boolean res = UserInfoQry.checkUserPassWd(userid, passWord);

            if (res == true)// 密码正确
            {
                boolean flag = PasswdMgr.ifDefaultPassWd(sn, passWord);
                if (flag)
                {
                    result.put("X_RESULTCODE", "0");
                    result.put("X_CHECK_INFO", "6");
                    result.put("RESULTCODE", "0");
                    result.put("CHECK_INFO", "6");
                    result.put("X_RESULTINFO", "密码正确，但为初始化密码");
                }
                else
                {
                    result.put("X_RESULTCODE", "0");
                    result.put("X_CHECK_INFO", "0");
                    result.put("RESULTCODE", "0");
                    result.put("CHECK_INFO", "0");
                    result.put("X_RESULTINFO", "密码正确，且不是初始化密码");//
                }
            }
            else if (res == false)// 密码错误
            {
                CSAppException.apperr(CrmUserException.CRM_USER_90);

            }
        }
        
        String registTime= SysDateMgr.getSysTime();
		String endDate = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime, "yyyy-MM-dd HH:mm:ss"), 300),"yyyy-MM-dd HH:mm:ss");
		data.put("IDENT_START_TIME", registTime);
		data.put("IDENT_START_TIME", endDate);
		
		IData user = new DataMap();
		String customId = data.getString("CUSTOM_ID");
		String strDate = DateFormatUtils.format(Calendar.getInstance().getTime(),"yyyyMMdd");
		IDataset out = UserIdentInfoQry.getseqString();
		String seqId = ((IData) out.get(0)).getString("OUTSTR", "");
		String identCode = "IDENTCODE"+strDate+getVisit().getStaffEparchyCode()+seqId;
		data.put("IDENT_CODE", identCode);//用户身份凭证
		
		//生成凭证
        LoginAuthBean loginBean = new LoginAuthBean();
        loginBean.createIdentInfo(data);
		
		if ("".equals(customId) || customId == null){
			result.put("CUSTOM_ID_STATUS","N");//客户标识是否变更   N 为未变更,Y为已变更
		}else{
			result.put("CUSTOM_ID_STATUS","Y");
		}
		result.put("IDENT_CODE", identCode);//用户身份凭证
		result.put("IDENT_CODE_LEVEL", identCodeType);//用户身份凭证级别 01：一般凭证 03：服务密码凭证
		result.put("IDENT_CODE_TYPE", identCodeType);
		
		user.put("SERIAL_NUMBER", sn);
		user.put("CUSTOM_ID", userInfo.getString("CUST_ID"));//客户标识
		user.put("PROVINCE", userInfo.getString("CITY_CODE"));//客户归属省
		user.put("BRAND", userInfo.getString("BRAND_CODE"));//用户品牌
		user.put("STATUS", userInfo.getString("USER_STATE_CODESET"));//用户状态
		
		result.put("USER_INFO", user);//用户信息
		
        return result;
	}
}
