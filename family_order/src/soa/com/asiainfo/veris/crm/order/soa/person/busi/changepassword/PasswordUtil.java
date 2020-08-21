
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEncryptGeneInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;

public class PasswordUtil
{
    private static String KEY = "ailk";

    /**
     * 3.客户登记身份证件的连续6位数字
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkContainInPspt(IData data) throws Exception
    {
        String inpasswd = data.getString("USER_PASSWD", "");
        String psptTypeCode = data.getString("PSPT_TYPE_CODE", "");
        String psptId = data.getString("PSPT_ID", "");
        if (psptId.indexOf(inpasswd) != -1)
        {
            return true;
        }
        return false;
    }

    /**
     * 4.客户手机号码中的连续6位数字。
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkContainInSn(IData data) throws Exception
    {
        String inpasswd = data.getString("USER_PASSWD", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        if (serialNumber.indexOf(inpasswd) != -1)
        {
            return true;
        }
        return false;
    }

    /**
     * 5.客户手机号码中前三位+后三位或后三位+前三位的组合
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkContainInSnQh3Bit(IData data) throws Exception
    {
        String inpasswd = data.getString("USER_PASSWD", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String q3b = serialNumber.substring(0, 3); // 手机号码前三位
        String h3b = serialNumber.substring(serialNumber.length() - 3); // 手机号码后三位

        if ((q3b + h3b).equals(inpasswd))
        {
            return true;
        }
        else if ((h3b + q3b).equals(inpasswd))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 6.密码的不同号码数需大于3，例如：112211，不同号码数为2
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkPasswdRepeatBitNum(IData data) throws Exception
    {
        String inpasswd = data.getString("USER_PASSWD", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        IData bitMap = new DataMap();
        for (int i = 0; i < inpasswd.length(); i++)
        {
            char key = inpasswd.charAt(i);
            bitMap.put("KEY" + key, "VALUE");
        }
        if (bitMap.size() <= 3)
        {
            return true;
        }

        return false;
    }
    /**
     * 前三位后三位等差数列校验，例如：135987
     * 关于完善弱密码集合-REQ201710120004
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkPasswdArithmetic(IData data) throws Exception{
    	String passWordStr = data.getString("USER_PASSWD", "");
    	boolean prefix_flag = true;
		boolean suffix_flag = true;
		int lenArithmetic =(new Double(Math.floor(passWordStr.length()/2))).intValue();//TODO
		int modArithmetic=0;
		if(passWordStr.length()%2!=0){
			modArithmetic=1;
		}
		String prefixlenArithmetic = passWordStr.substring(0, lenArithmetic);
		String suffixlenArithmetic = passWordStr.substring(modArithmetic+lenArithmetic);
		for(int z=0; z<(prefixlenArithmetic.length()-2); z++){
			if((Integer.parseInt(""+prefixlenArithmetic.charAt(z))-Integer.parseInt(""+prefixlenArithmetic.charAt(z+1)))
					!=(Integer.parseInt(""+prefixlenArithmetic.charAt(z+1))-Integer.parseInt(""+prefixlenArithmetic.charAt(z+2))))
			{
				prefix_flag=false;
				break;
			}
		}//前半段校验
		for(int y=0; y<(suffixlenArithmetic.length()-2); y++){
			if((Integer.parseInt(""+suffixlenArithmetic.charAt(y))-Integer.parseInt(""+suffixlenArithmetic.charAt(y+1)))
					!=(Integer.parseInt(""+suffixlenArithmetic.charAt(y+1))-Integer.parseInt(""+suffixlenArithmetic.charAt(y+2))))
			{
				suffix_flag=false;
				break;
			}
		}//后半段校验
		if(prefix_flag&&suffix_flag){
			return true;
		}
		return false;
		
    }
    /**
     * 弱密码库校验
     * 关于完善弱密码集合-REQ201710120004
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkPasswdToConfig(IData data) throws Exception{
    	boolean flag=false;
    	String passWord=data.getString("USER_PASSWD","");
    	// 参数容器
        IData param = new DataMap();
        // SUBSYS_CODE
        param.put("SUBSYS_CODE", "CSM");
        // 配置PARAM_ATTR
        param.put("PARAM_ATTR", "251");
        // 得到业务编码
        param.put("PARAM_CODE", "0");
        param.put("EPARCHY_CODE", "0898");
        // 判断所要配置的业务编码是否已配置过
//        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataset results = CSAppCall.call( "SS.ManageTaskSVC.isTaskConfigured", param);
        if(results.size()>0){
        	for(int i =0;i<results.size();i++){
        		IData result=results.getData(i);
        		if(result.getString("PARA_CODE1", "").equals(passWord)){
        			flag=true;break;
        		}
        	}
        }
    	return flag;
    	
    }
    /**
     * 8.密码不能全为偶数或奇数，如24680
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkPasswsAllJO(IData data) throws Exception
    {
        String inpasswd = data.getString("USER_PASSWD", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        IData numMap = new DataMap();
        for (int i = 0; i < inpasswd.length(); i++)
        {
            char num = inpasswd.charAt(i);
            int mod = num % 2; // 结果是0或1
            numMap.put("KEY" + mod, "VALUE");
        }
        if (numMap.size() == 1)
        {
            return true;
        }
        return false;
    }

    /**
     * 7.前三位的密码不能后三位密码一致，如123123
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkPasswsQh3Bit(IData data) throws Exception
    {
        String inpasswd = data.getString("USER_PASSWD", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String q3b = inpasswd.substring(0, 3); // 密码前三位
        String h3b = inpasswd.substring(inpasswd.length() - 3); // 密码后三位
        if (q3b.equals(h3b))
        {
            return true;
        }
        return false;
    }

    /**
     * 对于修改密码和申请密码中用户设置的新密码进行复杂度校验
     * 
     * @throws Exception
     */
    public static void checkPwdComplx(String managerMode, String serialNumber, String oldPsptId, String newPsw) throws Exception
    {
        if ("0".equals(managerMode) || "2".equals(managerMode) || "4".equals(managerMode) || "9".equals(managerMode) || "8".equals(managerMode))
        {
            boolean flag = false;
            for (int i = 0; i < newPsw.length() - 1; i++)
            {
                if (newPsw.charAt(i) == newPsw.charAt(i + 1))
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
                CSAppException.apperr(CrmUserException.CRM_USER_106);// 450004:新密码数字不能全部相同!
            }

            if (newPsw.equals("123456") || newPsw.equals("654321") || newPsw.equals(serialNumber.substring(5)))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_109);// 450005:新密码不能为简单密码或手机号后6位！;
            }
        }

        if ("0".equals(managerMode) || "2".equals(managerMode) || "4".equals(managerMode) || "8".equals(managerMode) || "9".equals(managerMode))
        {
            if (StringUtils.isBlank(newPsw))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_111);// 450007: 新密码不能为空;
            }
            if (newPsw.trim().length() != 6)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_113);// 新密码长度必须是6位;
            }
            boolean isNum = newPsw.matches("[0-9][0-9][0-9][0-9][0-9][0-9]");
            if (!isNum)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1169);// 新服务密码必须大于或等于零的6位整数！
            }
            if (StringUtils.isNotBlank(oldPsptId) && oldPsptId.length() >= 6)
            {
                if (oldPsptId.indexOf(newPsw) >= 0)
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_1104);// 450009: 新密码不能是证件号码连续6位;
                }
            }
            IData data = new DataMap();
            data.put("USER_PASSWD", newPsw);
            data.put("SERIAL_NUMBER", serialNumber);

            if (checkContainInSn(data))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1196);// 手机号码中的连续6位数字不能作为新密码！
            }
            if (checkContainInSnQh3Bit(data))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1197);// 手机号码中前三位+后三位或后三位+前三位的组合不能作为新密码！

            }
            if (checkPasswdRepeatBitNum(data))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1198);// 密码的不同号码数小于等于3 不能作为新密码！

            }
            if (checkPasswsQh3Bit(data))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1199);// 密码的前后三位一致,不能作为新密码！
            }
            if (checkPasswsAllJO(data))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1200);// 密码全为偶数或奇数,不能作为新密码！
            }
            /* 关于完善弱密码集合-REQ201710120004  begin*/
            if (checkPasswdArithmetic(data))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1209);// 密码为前三位后三位为等差数列！
            }
            if(checkPasswdToConfig(data)){
            	CSAppException.apperr(CrmUserException.CRM_USER_1210);// 弱密码库校验！
            }
            /* 关于完善弱密码集合-REQ201710120004  end*/
            
        }
    }

    /**
     * 2.重复号码：如"666666"或"000000"等
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @date:2010-1-20
     */
    public static boolean checkRepeatNumber(IData data) throws Exception
    {
        String inpasswd = data.getString("USER_PASSWD", "");
        /**
         * 算法简单描述： 相邻位相减为0
         */
        for (int i = 0; i < (inpasswd.length() - 1); i++)
        {
            if ((new Integer(inpasswd.charAt(i + 1)) - new Integer(inpasswd.charAt(i))) != 0)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 1.连号：如"123456"或"345678"等
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkSerieisNumber(IData data) throws Exception
    {
        String inpasswd = data.getString("USER_PASSWD", "");
        /**
         * 算法简单描述： 相邻位相减等于正负一
         */
        // 递增
        for (int i = 0; i < (inpasswd.length() - 1); i++)
        {
            int n1 = new Integer(inpasswd.charAt(i + 1)) - new Integer(inpasswd.charAt(i));
            if (n1 >= 0 && n1 != 1)
            {
                return false;
            }
        }
        // 递减
        for (int i = 0; i < (inpasswd.length() - 1); i++)
        {
            int n1 = new Integer(inpasswd.charAt(i + 1)) - new Integer(inpasswd.charAt(i));
            if (n1 <= 0 && n1 != -1)
            {
                return false;
            }
        }
        return true;
    }

    public static IData checkUserPoorPWD(IData data) throws Exception
    {

        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_CHECK_INFO", "0");
        result.put("X_RESULTINFO", "Trade Ok!");
        if (checkSerieisNumber(data))
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "4");
            result.put("X_RESULTINFO", "6个连续数字不能作为服务密码，请重新输入!");
            return result;
        }
        if (checkRepeatNumber(data))
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "5");
            result.put("X_RESULTINFO", "6个重复数字不能作为服务密码，请重新输入!");
            return result;
        }
        if (checkContainInPspt(data))
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "7");
            result.put("X_RESULTINFO", "证件号码的连续6位数字不能作为服务密码，请重新输入!");
            return result;
        }
        if (checkContainInSn(data))
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "8");
            result.put("X_RESULTINFO", "手机号码中的连续6位数字不能作为服务密码，请重新输入!");
            return result;
        }
        if (checkContainInSnQh3Bit(data))
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "9");
            result.put("X_RESULTINFO", "手机号码中前三位+后三位或后三位+前三位的组合不能作为服务密码!");
            return result;

        }
        if (checkPasswdRepeatBitNum(data))
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "10");
            result.put("X_RESULTINFO", "密码的不同号码数小于等于3 不能作为服务密码，请重新输入!");
            return result;

        }
        if (checkPasswsQh3Bit(data))
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "11");
            result.put("X_RESULTINFO", "密码的前后三位一致,不能作为服务密码，请重新输入!");
            return result;

        }
        if (checkPasswsAllJO(data))
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "12");
            result.put("X_RESULTINFO", "密码全为偶数或奇数,不能作为服务密码，请重新输入!");
            return result;

        }

        return result;
    }

    /**
     * 解密算法
     * 
     * @param password
     * @param key
     * @return
     */
    public static String decrypt(String password)
    {
        String key = KEY;
        String tmp = "";
        for (int i = 0; i < key.length(); i++)
        {
            tmp += (int) key.charAt(i);
        }
        int ifloor = (int) Math.floor(tmp.length() / 5);
        String mult = "" + tmp.charAt(ifloor) + tmp.charAt(ifloor * 2) + tmp.charAt(ifloor * 3) + tmp.charAt(ifloor * 4) + tmp.charAt(ifloor * 5);

        int iceil = (int) Math.ceil(key.length() / 2);

        int ipow = (int) (Math.pow(2, 31) - 1);
        long irandom = Long.parseLong(password.substring(password.length() - 8, password.length()), 16);
        password = password.substring(0, password.length() - 8);
        tmp += irandom;
        while (tmp.length() > 10)
        {
            tmp = (Long.parseLong(tmp.substring(0, 10)) + Long.parseLong(tmp.substring(10, tmp.length()))) + "";
        }
        tmp = (Long.parseLong(tmp) * Long.parseLong(mult) + iceil) % ipow + "";
        int chr = 0;
        String out = "";
        for (int i = 0; i < password.length(); i += 2)
        {
            chr = Integer.parseInt(password.substring(i, i + 2), 16) ^ (int) Math.floor((Double.parseDouble(tmp) / ipow) * 255);
            out += (char) chr;
            tmp = (Long.parseLong(mult) * Long.parseLong(tmp) + iceil) % ipow + "";
        }
        return out;
    }

    /**
     * 加密算法
     * 
     * @param password
     * @param key
     * @return
     */
    public static String encrypt(String password)
    {
        String key = KEY;
        String tmp = "";
        for (int i = 0; i < key.length(); i++)
        {
            tmp += (int) key.charAt(i);
        }
        int ifloor = (int) Math.floor(tmp.length() / 5);
        String mult = "" + tmp.charAt(ifloor) + tmp.charAt(ifloor * 2) + tmp.charAt(ifloor * 3) + tmp.charAt(ifloor * 4) + tmp.charAt(ifloor * 5);
        int iceil = (int) Math.ceil(key.length() / 2);
        int ipow = (int) (Math.pow(2, 31) - 1);
        int irandom = (int) (Math.round(Math.random() * 10000000) % 1000000);
        tmp += irandom;
        while (tmp.length() > 10)
        {
            tmp = (Long.parseLong(tmp.substring(0, 10)) + Long.parseLong(tmp.substring(10, tmp.length()))) + "";
        }
        tmp = (Long.parseLong(tmp) * Long.parseLong(mult) + iceil) % ipow + "";

        int chr = 0;
        String out = "";
        for (int i = 0; i < password.length(); i++)
        {
            chr = password.charAt(i) ^ (int) Math.floor((Double.parseDouble(tmp) / ipow) * 255);
            if (chr < 16)
            {
                out += "0" + Integer.toHexString(chr);
            }
            else
            {
                out += Integer.toHexString(chr);
            }
            tmp = (Long.parseLong(mult) * Long.parseLong(tmp) + iceil) % ipow + "";
        }
        String saltStr = Integer.toHexString(irandom);
        while (saltStr.length() < 8)
        {
            saltStr = "0" + saltStr;
        }
        out += saltStr;
        return out;
    }

    /**
     * 根据外部接口传入的修改密码类型给内部系统密码变更类型赋值
     * 
     * @param managemode
     * @return 1修改密码 2新增密码 3随机密码 4取消密码
     * @throws Exception
     */
    public static String getPasswdTypeByManagemode(String managerMode) throws Exception
    {
        String passwdType = "";
        if (StringUtils.equals("0", managerMode))
        {// 新增密码
            passwdType = "2";
        }
        else if (StringUtils.equals("1", managerMode))
        {// 取消密码
            passwdType = "4";
        }
        else if (StringUtils.equals("2", managerMode) || StringUtils.equals("4", managerMode)// 2修改密码
                // 4重置密码（重置的密码需要用户输入新的密码）
                || StringUtils.equals("8", managerMode) || StringUtils.equals("9", managerMode))
        {// 8和9都是重置密码，但密码需要用户传入，只是不需要校验
            passwdType = "1";
        }
        else if (StringUtils.equals("3", managerMode) || StringUtils.equals("5", managerMode) || StringUtils.equals("7", managerMode))
        {
            passwdType = "3";
        }
        return passwdType;
    }

    /**
     * 针对不同的变更类型检验服务密码
     * 
     * @param managerMode
     *            修改密码类型
     * @param psw
     *            用户输入的旧密码
     * @param nPsw
     *            用户输入的新密码
     * @param oldPwd
     *            数据库中已存的密码
     * @param newPsptId
     *            用户 输入的证件号码
     * @param oldPsptId
     *            用户真实证件号码
     * @param checkTag
     *            是否需要进行校验（海南需要）
     * @param userId
     *            用户ID
     * @throws Exception
     */
    public static void judgeManagerMode(String managerMode, String psw, String newPsw, String oldPwd, String newPsptId, String oldPsptId, String checkTag, String userId) throws Exception
    {

        // 0申请新密码 3申请新随机密码
        if (StringUtils.equals("0", managerMode) || StringUtils.equals("3", managerMode))
        { // 申请新密码，需要验证没有原密码，有则报错
            if (StringUtils.isNotBlank(oldPwd))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_99);// 450002: 用户服务密码已申请！
            }
        }
        // 1取消密码
        else if (StringUtils.equals("1", managerMode))
        {
            if (StringUtils.isBlank(oldPwd))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_81);// 450001: 服务密码不存在！
            }
            if (StringUtils.equals("1", checkTag))
            {
                String encryPsw = PasswdMgr.encryptPassWD(psw, PasswdMgr.genUserId(userId));
                if (!encryPsw.equals(oldPwd))
                {
                    IDataset genSet = UserEncryptGeneInfoQry.getEncryptGeneBySn(userId);
                    if (IDataUtil.isNotEmpty(genSet) && StringUtils.isNotBlank(genSet.getData(0).getString("ENCRYPT_GENE")))
                    {
                        String tempPwd = PasswdMgr.encryptPassWD(psw, genSet.getData(0).getString("ENCRYPT_GENE"));
                        if (StringUtils.equals(tempPwd, oldPwd))
                        {
                            return;
                        }
                    }
                    CSAppException.apperr(CrmUserException.CRM_USER_90);// 450001: 服务密码不正确！
                }
            }
        }
        // 2变更密码
        else if (StringUtils.equals("2", managerMode))
        {
            if (StringUtils.isBlank(oldPwd))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_81);// 450000: 用户服务密码不存在！
            }
            else
            {// 说明过去有密码并且密码验证正确，可以将新的插入了
                if (PasswdMgr.checkUserPassword(psw, userId, oldPwd))
                {
                    return;
                }
                else
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_90);// 450001: 服务密码不正确！
                }
            }
        }
        // 4重置密码（需要验证证件号码,需要用户输入新的密码）
        else if (StringUtils.equals("4", managerMode))
        {
            if (StringUtils.isBlank(newPsptId))
            {
                CSAppException.apperr(CustException.CRM_CUST_112);// 450006:证件号码不能为空！
            }
            if (!StringUtils.equals(oldPsptId, newPsptId))
            {
                CSAppException.apperr(CustException.CRM_CUST_118);// 450013: 证件号码不正确!
            }

        }
        // 5和7都属于随机密码（需要验证证件号码）
        else if (StringUtils.equals("5", managerMode) || StringUtils.equals("7", managerMode))
        {
            if (StringUtils.isBlank(newPsptId))
            {
                CSAppException.apperr(CustException.CRM_CUST_112);// 450006:证件号码不能为空！
            }
            if (StringUtils.equals("1", checkTag))
            {
                if (!StringUtils.equals(oldPsptId, newPsptId))
                {
                    CSAppException.apperr(CustException.CRM_CUST_118);// 450013: 证件号码不正确!
                }
            }
        }

    }

}
