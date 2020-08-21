
package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import org.apache.log4j.Logger;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdAssistant;

public class ModifyUserPswInfoBean extends CSBizBean
{

    private static final Logger logger = Logger.getLogger(ModifyUserPswInfoBean.class);

    public static void main(String[] args)
    {	
    	String userid = "1909100232490987";
        String key = userid.substring(userid.length() - 9, userid.length());
        String oldPass = (String) Encryptor.fnEncrypt("122334", key);
    }
    
    /**
     * 热线密码操作
     * @param input
     * @return
     * @throws Exception
     */
    public IData changePswInfo4HL(IData input) throws Exception{
    	
    	String serial_number = input.getString("SERIAL_NUMBER");
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number);
    	if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        
        String userid = userInfo.getString("USER_ID");
        String identCode = input.getString("IDENT_CODE");
		String contactId = input.getString("CONTACT_ID");
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userid, identCode, contactId);
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}
    	
    	String opr_type = input.getString("OPR_TYPE");   //01为修改密码，02为重置密码
    	
    	String occ_passwd = input.getString("OCC_PASSWD");   //老密码
    	
    	String ncc_passwd = input.getString("NCC_PASSWD");  //新密码
    	
    	if ("02".equals(opr_type)){
    		if ("".equals(identCode)|| identCode == null){
    			 CSAppException.apperr(CustException.CRM_CUST_100);
    		}
    	}
    	
    	//--------修改密码数据
    	IData indata = new DataMap();
    	indata.put("CCPASSWD", occ_passwd); //老密码
    	indata.put("IDVALUE",serial_number);
    	indata.put("IDTYPE", "1");
    	indata.put("NNPASSWD", ncc_passwd);
		indata.put("opr_type", opr_type);
		
    	
		IDataset tempdata = changePswInfoForPhone(indata);
		
		return tempdata.getData(0);
    }
    

    public IDataset changePswInfoForPhone(IData input) throws Exception
    {
        String idtype = input.getString("IDTYPE", "");
        String idValue = input.getString("IDVALUE", "");
        String opr_type = input.getString("opr_type");
        String userpasswd = "";
        IData inparam = new DataMap();
        boolean res = false;
        
        if (idValue == null || "".equals(idValue))
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_31);
        }
        
        
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(idValue);
        if (userInfo == null || userInfo.size() < 1)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_87);
        }
        String userId = userInfo.getString("USER_ID", "");
        userpasswd = userInfo.getString("USER_PASSWD", "");
        String oldpasswd = input.getString("CCPASSWD", "");
        if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
        {
            CSAppException.apperr(CrmUserException.CRM_USER_81);
        }
        
        if(!"02".equals(opr_type)){
	        res = UserInfoQry.checkUserPassWd(userId, oldpasswd);
	        if (res == true)// 密码正确
	        {
	            // ok
	        }
	        else if (res == false)// 密码错误
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_296);
	        }
	        String ccpasswd = input.getString("CCPASSWD");
	        String nnpasswd = input.getString("NNPASSWD");
	        if (ccpasswd != null && !"".equals(ccpasswd) && ccpasswd.equals(nnpasswd))
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_255);
	        }
	
	        checkpwdinfo(input, userId, userpasswd);
        }
        
        
        inparam.put("SERIAL_NUMBER", input.getString("IDVALUE", ""));
        inparam.put("NEW_PASSWD", input.getString("NNPASSWD", ""));
        inparam.put("NEW_PASSWD_AGAIN", input.getString("NNPASSWD", ""));
        inparam.put("OLD_PASSWD", input.getString("CCPASSWD", ""));
        inparam.put("USER_PASSWORD", userpasswd);
        inparam.put("TRADE_TYPE_CODE", "71");// 密码修改服务
        inparam.putAll(input);
        
        
        if("02".equals(opr_type)){
        	inparam.put("PASSWD_TYPE", "5");
        }else{
        	inparam.put("PASSWD_TYPE", "1");
        }
        
        StringBuilder sb = new StringBuilder("SERIAL_NUMBER:").append(inparam.getString("SERIAL_NUMBER"));
        IDataset dataset = CSAppCall.call("SS.ModifyUserPwdInfoRegSVC.tradeReg", inparam);
        IDataset resultSet = new DatasetList();
        IData result = new DataMap();
        result = dataset.getData(0);
        
        if( dataset.size() > 0 ){
        	result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "密码修改成功！");
        }else{
        	result.put("X_RESULTCODE", "-1");
            result.put("X_RESULTINFO", "密码修改失败！");
        }
        
        resultSet.add(result);
        return resultSet;

    }

    public void checkPasswdType(IData param) throws Exception
    {
        String serialNumber = param.getString("IDVALUE", "");
        String passwdType = "2";

        if (passwdType.equals("2"))
        {
            // 变更密码
            // 验证用户是否锁定密码。
            int iErrorLimit = PasswdAssistant.getErrorCountParam("4450", "0");
            int iErrorCount = PasswdAssistant.getErrorCountNow(serialNumber);
            if (iErrorCount > iErrorLimit)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_916);
                // 密码已锁定
            }
        }

    }

    public IDataset checkPspt(IData input) throws Exception
    {
        String userId = input.getString("USER_ID", "");
        String psptId = input.getString("CHECK_PSPT_ID", "");
        IData result = CheckUserInfoMgr.checkUserPsptIdByUserId(psptId, userId);
        IDataset reslutSet = new DatasetList();
        reslutSet.add(result);
        return reslutSet;

    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkPwd(IData input) throws Exception
    {
        String userpassword = input.getString("USER_PASSWORD", "");// 用户密码
        String oldPassword = input.getString("OLD_PASSWD", "");
        String newPassword = input.getString("NEW_PASSWD", "");
        String newPasswordAgain = input.getString("NEW_PASSWD_AGAIN", "");
        String passwdType = input.getString("PASSWD_TYPE", "");// 密码变更类型
        String userId = input.getString("USER_ID", "");

        IDataset resultSet = new DatasetList();
        IData resultData = new DataMap();

        if (userId.length() < 9)
        {
            int len = 9 - userId.length();
            for (int i = 0; i < len; i++)
            {
                userId = "0" + userId;
            }
        }
        if (passwdType.equals("1"))
        {
            if (oldPassword.equals(""))
            {
                resultData.put("X_RESULT_CODE", "700010");
                resultData.put("X_RESULT_INFO", "原密码输入不能为空。");
                resultSet.add(resultData);
                return resultSet;
            }
        }
        String key = userId.substring(userId.length() - 9, userId.length());
        String oldPass = (String) Encryptor.fnEncrypt(oldPassword, key);

        if (!oldPass.equals(userpassword))
        {
            // 现有密码输入不正确
            resultData.put("X_RESULT_CODE", "700011");
            resultData.put("X_RESULT_INFO", "用户现有密码错误。");
            resultSet.add(resultData);
            return resultSet;
        }
        String tmpPass = (String) Encryptor.fnEncrypt(newPassword, key);
        if (tmpPass.equals(userpassword))
        {
            // 修改后密码不能和新密码一致
            resultData.put("X_RESULT_CODE", "700012");
            resultData.put("X_RESULT_INFO", "修改密码和现有密码不能相同。");
            resultSet.add(resultData);
            return resultSet;
        }

        // 密码校验成功返回

        resultData.put("X_RESULT_CODE", "0");
        resultData.put("X_RESULT_INFO", "校验成功。");
        resultSet.add(resultData);

        return resultSet;

    }

    private void checkpwdinfo(IData param, String userId, String userPasswd) throws Exception
    {
        String newPasswd = param.getString("NNPASSWD", "");// 新密码
        String oldPasswd = param.getString("CCPASSWD", "");// 旧密码
        String serialNumber = param.getString("IDVALUE", "");
        String passwdType = param.getString("2", "");

        // 检验原密码是否正确
        if ("1".equals(passwdType) || "2".equals(passwdType))
        {
            String key = userId.substring(userId.length() - 9, userId.length());
            String oldPass = (String) Encryptor.fnEncrypt(oldPasswd, key);
            if (!oldPass.equals(userPasswd))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_296);
            }
        }

        // 对于修改密码和申请密码中用户设置的新密码进行复杂度校验
        if ("0".equals(passwdType) || "2".equals(passwdType) || "4".equals(passwdType) || "9".equals(passwdType))
        {
            boolean flag = false;
            for (int i = 0; i < newPasswd.length() - 1; i++)
            {
                if (newPasswd.charAt(i) == newPasswd.charAt(i + 1))
                {
                    flag = true;
                }
                else
                {
                    flag = false;
                    break;
                }
            }
            if (flag)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_106);
            }

            if (newPasswd.equals("123456") || newPasswd.equals(serialNumber.substring(5)))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_109);
            }
        }

        if ("0".equals(passwdType) || "2".equals(passwdType) || "4".equals(passwdType))
        {
            if ("".equals(newPasswd) || newPasswd == null)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_111);
            }
            if (newPasswd.trim().length() != 6)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_113);
            }
        }

        checkPasswdType(param);

    }

    // 产生用户id后九位的
    public String genUserId(String userId) throws Exception
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
     * 判断密码复杂度
     * 
     * @param password
     * @param serialNumber
     * @return boolean
     */
    private boolean simplePwdValid(String password, String serialNumber)
    {
        boolean valid = false;
        String seqRegex1 = "0123456789";
        String seqRegex2 = "9876543210";
        String seqRegex3 = "0{6}|1{6}|2{6}|3{6}|4{6}|5{6}|6{6}|7{6}|8{6}|9{6}";
        // 包含上述任意值，返回false
        if (seqRegex1.contains(password) || seqRegex2.contains(password) || password.matches(seqRegex3) || serialNumber.contains(password))
        {
            valid = true;
        }
        return valid;
    }

}
