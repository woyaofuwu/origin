
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;

/**
 * 
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckUser4BuyInsteadSVC.java
 * @Description: 代客下单密码验证服务
 *
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-10-24 下午5:11:49
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------*
 * 2014-10-24      yxd         v1.0.0               修改原因
 */
public class CheckUser4BuyInsteadSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    public IData insertAuthTrade(IData input) throws Exception
	{
		String serialNumber = input.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码【" + serialNumber + "】不存在");
		}
		String inModeCode = input.getString("IN_MODE_CODE");
		String checkState = "0"; // 认证结果
		if (StringUtils.equals("1", inModeCode))
		{
			//客服直接取IVR认证结果
			checkState = input.getString("CHECK_STATE");
		}
		else
		{
			//营业厅比对密码
			String userPasswd = input.getString("USER_PASSWD");
			String encryptPwd = PasswdMgr.encryptPassWD(userPasswd, userInfo.getString("USER_ID"));
			String pwd = userInfo.getString("USER_PASSWD");
			if(StringUtils.equals(encryptPwd, pwd))
			{
				checkState = "1";
			}
		}
		IData param = new DataMap();
		String remark = "1".equals(checkState) ? "密码正确！" : "密码不正确或密码输入次数超过限制！";
		param.put("CHECK_ID", SeqMgr.getCheckBuy());
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userInfo.getString("USER_ID"));
		param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
		param.put("CHECK_STATE", checkState);
		param.put("REMARK", remark);
		param.put("IN_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		Dao.insert("TF_F_USER_CHECK4BUYISTEAD", param);
		return param;
	}
    /**
     * 
    * @Function: queryChekInfo()
    * @Description: 查询认证成功记录
    *
    * @param:
    * @return：
    * @throws：异常描述
    *
    * @version: v1.0.0
    * @author: yxd
    * @date: 2014-10-29 上午10:49:22
    *
    * Modification History:
    * Date         Author          Version            Description
    *---------------------------------------------------------*
    * 2014-10-29      yxd         v1.0.0               修改原因
     */
    public IData queryChekInfo(IData input) throws Exception
	{
    	IData retData = new DataMap();
    	IData param = new DataMap();
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	if(StringUtils.isBlank(serialNumber))
    	{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "入参手机号码【SERIAL_NUMBER】不能为空");
    	}
    	param.put("SERIAL_NUMBER", serialNumber);
    	IDataset retSet = Dao.qryByCode("TF_F_USER_CHECK4BUYISTEAD", "SEL_Info_InHalf", param);
    	if(IDataUtil.isNotEmpty(retSet))
    	{
    		retData = retSet.first();
    		if(StringUtils.equals(retData.getString("CHECK_STATE"), "1"))
    		{
    			retData.put("X_RESULTCODE", "0");
    		}
    		else
    		{
    			retData.put("X_RESULTCODE", "1");
    			retData.put("X_RESULTINFO", "该手机号码号码最近半小时内无密码校验成功的记录");
    		}
    	}
    	else
    	{
    		retData.put("X_RESULTCODE", "1");
    		retData.put("X_RESULTINFO", "该手机号码号码最近半小时内无密码校验成功的记录");
    	}
    	return retData;    	
	}
    
}
