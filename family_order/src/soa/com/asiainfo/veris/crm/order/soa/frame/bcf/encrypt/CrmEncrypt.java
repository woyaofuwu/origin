
package com.asiainfo.veris.crm.order.soa.frame.bcf.encrypt;

public final class CrmEncrypt
{
    public static String EncryptPasswd(String oldPwd)
    {
        char cTmp;
        char[] password;
        char[] pwdEncry = new char[oldPwd.length()];

        password = oldPwd.toCharArray();
        for (int i = 0; i < password.length; i++)
        {
            pwdEncry[i] = password[i];
        }

        for (int i = 0; i < password.length; i++)
        {
            cTmp = (char) (password[i] + i + 1 + 4);
            pwdEncry[pwdEncry.length - i - 1] = cTmp;
        }

        return new String(pwdEncry);
    }
}
