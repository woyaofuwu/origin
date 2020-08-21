
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class PlatUtils
{
    public static final String PROVINCE_CODE = "0898";

    protected static final Logger log = Logger.getLogger(PlatUtils.class);

    /**
     * 判断密码是否只有数字
     * 
     * @return
     */
    // Wlan密码只能是数字
    public static boolean checkIsNumber(String password)
    {
        for (int i = 0; i < password.length(); i++)
        {
            int charPwd = password.charAt(i);
            if (!((charPwd >= 48 && charPwd <= 57)))
            {
                return false;
            }

        }

        return true;
    }
    
    
    

    /**
     * 判断密码是否只有数字和字符组成
     * 
     * @return
     */
    public static boolean checkIsNumberAndChar(String password)
    {
        for (int i = 0; i < password.length(); i++)
        {
            int charPwd = password.charAt(i);
            if (!(((charPwd >= 48 && charPwd <= 57) || (charPwd >= 97 && charPwd <= 122) || (charPwd >= 65 && charPwd <= 90))))
            {
                return false;
            }

        }
        

        return true;

    }

    /**
     * 生成6位随机密码复杂 加密后的密码也不能包含?号，否则有乱码，这个问题很头疼，先特殊处理
     * 
     * @param n
     * @return
     */
    public static String geneComplexRandomPassword()
    {

        for (int i = 0; i < 50; i++)
        {
            String pwd = geneRandomPassword();
            if (!isSimplePwd(pwd))
            {
                return pwd;
            }
        }

        // 超过50次还是简单密码，人品啊，随便返回个密码吧。
        return geneRandomPassword();
    }

    /**
     * 生成随机密码,可能出现简单密码
     * 
     * @return
     */
    public static String geneRandomPassword()
    {
        String pwd = "";
        for (int i = 0; i < 8; i++)
        {
            char str;
            int iChar = 0;
            int a = (int) (Math.random() * 62);
            if (a >= 0 && a <= 9)
            {
                iChar = a + 48;
            }
            if (a >= 10 && a <= 35)
            {
                iChar = a + 55;
            }
            if (a >= 36 && a <= 61)
            {
                iChar = a + 61;
            }
            if (iChar == 49 || iChar == 108 || iChar == 105 || iChar == 111 || iChar == 48 || iChar == 50 || iChar == 122)
            {// 1、l、i、o、0、2、z
                i--;
                continue;
            }
            str = (char) iChar;
            pwd += str;
        }
        return pwd;
    }

    public static String getAttrValue(String attrCode, List attrs)
    {
        for (int i = 0; i < attrs.size(); i++)
        {
            Object attr = attrs.get(i);
            if (attr instanceof AttrData)
            {
                AttrData attrData = (AttrData) attr;
                String attrValue = attrData.getAttrValue();
                if (attrCode.equals(attrData.getAttrCode()) && (attrValue != null && !"".equals(attrValue)))
                {
                    return attrValue;
                }
            }

            if (attr instanceof AttrTradeData)
            {
                AttrTradeData attrTradeData = (AttrTradeData) attr;
                String attrValue = attrTradeData.getAttrValue();
                if (attrCode.equals(attrTradeData.getAttrCode()) && (attrValue != null && !"".equals(attrValue)) && (attrTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD) || attrTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_USER)))
                {
                    return attrValue;
                }
            }

        }
        return "";
    }
    
    
    
    /**
     * 判断是否是全字母或者全数字
     * @param pwd
     * @return
     */
    public static boolean  checkIsAllNumberOrAllChar(String pwd)
    {
    	int numberCount =0;
    	int charCount =0;
    	  //判断
        for(int i=0;i<pwd.length();i++)
        {
        	
        	
        	 int charPwd = pwd.charAt(i);
             if (!((charPwd >= 48 && charPwd <= 57)))
             {
            	 numberCount ++;
             }
             
             if ((charPwd >= 97 && charPwd <= 122) || (charPwd >= 65 && charPwd <= 90))
             {
            	 charCount ++;
             }
        	
        }
        
        if(numberCount ==0 || charCount ==0)
        {
        	return true;
        }
        
        return false;
    }
    

    /**
     * 省BOSS的编码规则－3位省代码+8位业务编码（BIPCode） +14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长），序号从000001开始，增量步长为1。
     * 
     * @param bipCode
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static String getOperNumb(String bipCode, String trade_id) throws Exception
    {
        String opernumb;
        String provCode = PlatUtils.PROVINCE_CODE;
        opernumb = provCode.substring(provCode.length() - 3) + bipCode + SysDateMgr.getSysDate("yyyyMMddHHmmss") + trade_id.substring(trade_id.length() - 6);
        return opernumb;
    }

    /**
     * 插短信表
     * 
     * @param
     * @param inparam
     * @throws Exception
     */
    public static void insertSms(String serialNumber, String userId, String smsContent, String eparchyCode, String remark) throws Exception
    {
        IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", eparchyCode);
        sendInfo.put("CHAN_ID", "11");
        sendInfo.put("RECV_OBJECT_TYPE", "00");
        sendInfo.put("RECV_OBJECT", serialNumber);
        sendInfo.put("RECV_ID", userId);
        sendInfo.put("SMS_TYPE_CODE", "20");
        sendInfo.put("SMS_KIND_CODE", "02");
        sendInfo.put("NOTICE_CONTENT_TYPE", "0");
        sendInfo.put("NOTICE_CONTENT", smsContent);
        sendInfo.put("FORCE_REFER_COUNT", "1");
        sendInfo.put("SMS_PRIORITY", "5000");
        sendInfo.put("REFER_TIME", SysDateMgr.getSysTime());
        sendInfo.put("DEAL_TIME", SysDateMgr.getSysTime());
        sendInfo.put("DEAL_STATE", "0");
        sendInfo.put("MONTH", SysDateMgr.getCurMonth());
        sendInfo.put("DAY", SysDateMgr.getCurDay());
        sendInfo.put("DEAL_STAFFID", CSBizBean.getVisit().getStaffId());
        sendInfo.put("DEAL_DEPARTID", CSBizBean.getVisit().getDepartId());
        sendInfo.put("REMARK", remark);
        SmsSend.insSms(sendInfo);
    }

    private static boolean isContinuous(String str)
    {
        if (str.length() >= 2)
        {
            char[] pwdChar = str.toCharArray();
            int flag = 1;
            boolean simpleFlag = true;
            for (int i = 0; i < pwdChar.length - 1; i++)
            {
                if ((int) pwdChar[i + 1] != (int) pwdChar[i] + 1)
                {
                    flag = 0;
                    break;
                }
            }
            if (flag == 0)
            {
                for (int i = 0; i < pwdChar.length - 1; i++)
                {
                    if ((int) pwdChar[i + 1] != (int) pwdChar[i] - 1)
                    {
                        simpleFlag = false;
                        break;
                    }
                }
            }
            if (simpleFlag)
                return true;
        }
        return false;
    }

    public static boolean isSimplePwd(String pwd)
    {
        String strMatcher = "";
        Pattern pattern = null;
        Matcher matcher = null;
        
        int length = pwd.length();
        String charBegin = pwd.substring(0,1);
        String charEnd = pwd.substring(length-1, length);

        // n个相同的字符，如：111111；aaaaaa
        strMatcher = "^"+charBegin+"{"+length+"}$";
        pattern = Pattern.compile(strMatcher.toString());
        matcher = pattern.matcher(pwd);
        if (matcher.matches())
        {
            return true;
        }
        
        
        // 多个连续相同的双字符，如：10101010；abababab
        String tempMatcher = "";
        for(int count =1;count<length/2;count++)
        {
        	tempMatcher+="\\1";
        }
        strMatcher = "^(.{2})"+tempMatcher+"$";
        pattern = Pattern.compile(strMatcher.toString());
        matcher = pattern.matcher(pwd);
        if (matcher.matches())
        {
            return true;
        }
        
        
        // 3个连续相同的三字符，如：123123123；abcabcabc
        tempMatcher = "";
        for(int count =1;count<length/3;count++)
        {
        	tempMatcher+="\\1";
        }
        strMatcher = "^(.{3})"+tempMatcher+"$";
        pattern = Pattern.compile(strMatcher.toString());
        matcher = pattern.matcher(pwd);
        if (matcher.matches())
        {
            return true;
        }

        //n个连续相同的四字符，如：12ab12ab；1we41we4
        tempMatcher = "";
        for(int count =1;count<length/4;count++)
        {
        	tempMatcher+="\\1";
        }
        strMatcher = "^(.{4})"+tempMatcher+"$";
        pattern = Pattern.compile(strMatcher.toString());
        matcher = pattern.matcher(pwd);
        if (matcher.matches())
        {
            return true;
        }
        
        // 2个连续的 相同字符，如：111aaaa；
        for(int i=1;i<pwd.length()-1;i++)
        {
        	 strMatcher = "^"+charBegin+"{"+i+"}"+charEnd+"{"+(length-i)+"}"+"$";
             pattern = Pattern.compile(strMatcher.toString());
             matcher = pattern.matcher(pwd);
             if (matcher.matches())
             {
                 return true;
             }
        }
        
        //如果是全字母，或者全字符
        if(checkIsAllNumberOrAllChar(pwd))
        {
        	return true;
        }
        
        // strMatcher = "";

        // 连续的字符，如：123456789；abcefg;987654
        if (isContinuous(pwd))
            return true;

        if (length%2 == 0)
        {
            // 两个连续的 n字符，如：123abc；cba987;
            if (isContinuous(pwd.substring(0, length/2)) && isContinuous(pwd.substring(length/2, length)))
                return true;
        }

        // 固定的密码
        if (pwd != null  && "987654321_123asd456_a1b2c3d4e5_123qwe456_1q2w3e4r_1q2w3e4r5t6y_123qweasd_qwe123asd_qweasd123_qwerty456_123asd456_123456789".indexOf(pwd) > -1)
        {
            return true;
        }

        return false;
    }

    // /**
    // * WLAN密码折腾啊。。 加密后的字符包含?或者？号，会出现乱码
    // *
    // * @param pwd
    // * @return
    // */
    // public static boolean containsSpecialChar(String pwd)
    // {
    // String encodePassword = CrmEncrypt.EncryptPasswd(pwd);
    // char[] charPassword = encodePassword.toCharArray();
    //
    // for (int i = 0; i < charPassword.length; i++)
    // {
    // if ((int) charPassword[i] == 63 || (int) charPassword[i] == 128 || (int) charPassword[i] == 129 || (int)
    // charPassword[i] == 130 || (int) charPassword[i] == 131)
    // {
    // return true;
    // }
    // }
    //
    // return false;
    // }

    /**
     * 移除属性
     * 
     * @param attrCodes
     * @param attrs
     * @return
     */
    public static List<AttrData> removeAttr(String attrCode, List<AttrData> attrs)
    {
        List<AttrData> attrList = new ArrayList<AttrData>();
        for (int i = 0; i < attrs.size(); i++)
        {
            AttrData attr = attrs.get(i);
            if (!attrCode.equals(attr.getAttrCode()))
            {
                attrList.add(attr);
            }
        }
        return attrList;
    }
    
    
    public static void main(String[] args)
    {
    	
    }

}
