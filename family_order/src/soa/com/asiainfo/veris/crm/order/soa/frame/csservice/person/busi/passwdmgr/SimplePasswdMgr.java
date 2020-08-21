
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class SimplePasswdMgr extends CSBizBean
{

    private IData userInfo = new DataMap();

    private IData custInfo = new DataMap();

    private String strPassword = "";

    private String strSerialNumber = "";

    private String strTradeTypeCode = "";

    /**
     * 弱密码校验
     * 
     * @param inData
     * @throws Exception
     */
    public SimplePasswdMgr(IData inData) throws Exception
    {

        super();

        strSerialNumber = inData.getString("USER_SERIAL_NUMBER");

        strPassword = inData.getString("USER_PASSWD");

        strTradeTypeCode = inData.getString("TRADE_TYPE_CODE", "1");

        userInfo.putAll(PasswdAssistant.getUserInfo(inData.getString("USER_SERIAL_NUMBER"), inData.getString("USER_ID", "-1")));

        custInfo.putAll(PasswdAssistant.getCustInfo(userInfo.getString("CUST_ID", "")));
    }

    /**
     * 根据配置的参数配匹
     * 
     * @return
     * @throws Exception
     */
    private boolean checkByCommpara(IData outData) throws Exception
    {

        IDataset listCommpara = PasswdAssistant.getCommpara("4452", strPassword);

        if (listCommpara.size() > 0)
        {
            outData.put("RESULT_CODE", "1");

            if ("71,73".indexOf(strTradeTypeCode) > -1)
            {
                outData.put("RESULT_INFO", "弱密码管理:您的密码过于简单, 请设置一个较复杂的密码保证号码信息安全!");
            }
            else
            {
                outData.put("RESULT_INFO", "弱密码管理:您的密码过于简单, 请到密码变更界面, 设置一个较复杂的密码保证号码信息安全!");
            }

            return true;
        }

        return false;
    }

    /**
     * 根据配置的参数配匹
     * 
     * @return
     * @throws Exception
     */
    private boolean checkByCommparaEx(IData outData) throws Exception
    {

        IDataset listCommpara = PasswdAssistant.getCommpara("4453", strPassword);

        if (listCommpara.size() > 0)
        {
            outData.put("RESULT_CODE", "1");

            if ("71,73".indexOf(strTradeTypeCode) > -1)
            {
                outData.put("RESULT_INFO", "弱密码管理:您的密码过于简单, 请设置一个较复杂的密码保证号码信息安全!");
            }
            else
            {
                outData.put("RESULT_INFO", "弱密码管理:您的密码过于简单, 请到密码变更界面, 设置一个较复杂的密码保证号码信息安全!");
            }

            return true;
        }

        return false;
    }

    /**
     * 匹配证件号码
     * 
     * @return
     * @throws Exception
     */
    private boolean checkByPsptId(IData outData) throws Exception
    {

        // 不是身份证号码不验证
        if (!"0".equals(custInfo.getString("PSPT_TYPE_CODE", "-1")) && !"1".equals(custInfo.getString("PSPT_TYPE_CODE", "-1")))
        {
            return false;
        }

        StringBuilder str = new StringBuilder(custInfo.getString("PSPT_ID"));

        if (str.toString().indexOf(strPassword) > -1 || str.reverse().toString().indexOf(strPassword) > -1)
        {
            outData.put("RESULT_CODE", "1");
            outData.put("RESULT_INFO", "弱密码管理:密码不能是身份证号码连续几位数字!");
            return true;
        }

        return false;
    }

    /**
     * 正则式匹配
     * 
     * @param outData
     * @return
     * @throws Exception
     */
    private boolean checkByRegular(IData outData) throws Exception
    {

        IDataset listCommpara = PasswdAssistant.getCommpara("4453", "0");

        if (listCommpara.size() > 0)
        {
            Pattern pattern = null;
            Matcher matcher = null;
            String strMatcher = "";

            for (int idx = 0; idx < listCommpara.size(); idx++)
            {
                strMatcher = listCommpara.getData(idx).getString("PARA_CODE1", "");

                if (!"".equals(strMatcher))
                {
                    pattern = Pattern.compile(strMatcher.toString());

                    matcher = pattern.matcher(strPassword);

                    if (matcher.matches())
                    {
                        outData.put("RESULT_CODE", "1");

                        if ("71,73".indexOf(strTradeTypeCode) > -1)
                        {
                            outData.put("RESULT_INFO", "弱密码管理:您的密码过于简单, 请设置一个较复杂的密码保证号码信息安全!");
                        }
                        else
                        {
                            outData.put("RESULT_INFO", "弱密码管理:您的密码过于简单, 请到密码变更界面, 设置一个较复杂的密码保证号码信息安全!");
                        }

                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 匹配手机号码
     * 
     * @return
     * @throws Exception
     */
    private boolean checkBySerialNumber(IData outData) throws Exception
    {

        StringBuilder str = new StringBuilder(strSerialNumber);

        if (str.toString().indexOf(strPassword) > -1 || str.reverse().toString().indexOf(strPassword) > -1)
        {
            outData.put("RESULT_CODE", "1");
            outData.put("RESULT_INFO", "弱密码管理:密码不能是手机号码连续几位数字!");
            return true;
        }

        return false;
    }

    /**
     * 密码不能是sim卡后六位
     * 
     * @param outData
     * @return
     * @throws Exception
     */
    private boolean checkBySimCardEnd(IData outData) throws Exception
    {
        boolean flag = false;
        if (!"71".equals(strTradeTypeCode))
        { // modified by luoz_20121224 用户密码变更界面可以不受此限制，因为要允许用户修改密码。HXYD-YZ-REQ-20121024-008关于落实总部新版服务密码及相关业务管理功能点的支撑需求
            IData simCard = PasswdAssistant.getUserSimInfo(outData.getString("USER_ID"));
            if (strPassword != null && !"".equals(strPassword))
            {
                String simCardNum = simCard.getString("RES_CODE", "");
                // 密码不能是simcard的后六位
                if (simCardNum != null && !"".equals(simCardNum) && simCardNum.endsWith(strPassword))
                {
                    flag = true;
                }

            }
        }

        return flag;

    }

    /**
     * 检查用户是否是弱密码,并限制用户办理
     * 
     * @param inData
     * @throws Exception
     */
    public IData isLimitSimplePasswd(IData inData) throws Exception
    {

        IData outData = new DataMap();
        outData.put("RESULT_CODE", "1");

        if (strPassword != null && !"".equals(strPassword))
        {

            // 弱密码配置匹配
            if (this.checkByCommparaEx(outData))
            {
                // PasswdAssistant.insertTiOSms( inData,
                // "您的服务密码过于简单，不能办理该业务，为保护您的信息，请编辑短信501发送至10086、拨打10086、或登录10086.cn修改密码!", userInfo);

                outData.put("RESULT_CODE", "1");
                outData.put("RESULT_INFO", "弱密码管理:您的密码过于简单, 请到密码变更界面, 设置一个较复杂的密码保证号码信息安全!！");
                return outData;
            }

        }

        outData.put("RESULT_CODE", "0");
        outData.put("RESULT_INFO", "");

        return outData;
    }

    /**
     * 检查用户是否是弱密码
     * 
     * @param inData
     * @throws Exception
     */
    public IData isSimplePasswd(IData inData) throws Exception
    {

        IData outData = new DataMap();
        outData.put("RESULT_CODE", "1");

        if (StringUtils.isNotBlank(strPassword))
        {
            // 手机号码匹配
            if (this.checkBySerialNumber(outData))
            {
                PasswdAssistant.insertTiOSms(inData, "尊敬的用户：手机密码不能是手机号码连续几位组成，请稍后到密码变更界面修改！", userInfo);
                outData.put("RESULT_INFO", "尊敬的用户：手机密码不能是手机号码连续几位组成，请重新设置");
                return outData;
            }

            // 证件号码匹配
            if (this.checkByPsptId(outData))
            {
                PasswdAssistant.insertTiOSms(inData, "尊敬的用户：手机密码不能证件号码连续几位组成，请稍后到密码变更界面修改！", userInfo);
                outData.put("RESULT_INFO", "尊敬的用户：新服务密码不能是6位连号或6位重复号码，请重新输入！");
                return outData;
            }

            // 弱密码配置匹配
            if (this.checkByCommpara(outData))
            {
                PasswdAssistant.insertTiOSms(inData, "尊敬的用户：手机密码不能是类似123123连续，112233复数等过于简单密码，请稍后到密码变更界面修改！", userInfo);
                outData.put("RESULT_INFO", "尊敬的用户：新服务密码不能是6位连号或6位重复号码，请重新输入！");
                return outData;
            }

            // 弱密码正则式匹配
            if (this.checkByRegular(outData))
            {
                PasswdAssistant.insertTiOSms(inData, "尊敬的用户：手机密码不能是类似111111连续，112233复数等过于简单密码，请稍后到密码变更界面修改！", userInfo);
                outData.put("RESULT_INFO", "尊敬的用户：手机密码不能是类似111111连续，112233复数等过于简单密码，请重新设置");
                return outData;
            }
            if (this.checkBySimCardEnd(userInfo))
            {
                outData.put("RESULT_INFO", "尊敬的用户：手机密码不能是sim卡的后六位等过于简单密码，请到用户密码变更界面修改！");
                return outData;
            }
        }

        outData.put("RESULT_CODE", "0");
        outData.put("RESULT_INFO", "");

        return outData;
    }
}
