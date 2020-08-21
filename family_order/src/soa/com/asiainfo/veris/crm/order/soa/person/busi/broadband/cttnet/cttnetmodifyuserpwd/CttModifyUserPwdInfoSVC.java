
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserpwd;

import org.apache.log4j.Logger;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CttModifyUserPwdInfoSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(CttModifyUserPwdInfoSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * 密码验证
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkPwd(IData input) throws Exception
    {
        String userpassword = input.getString("USER_PASSWORD", "");// 用户密码
        String oldPassword = input.getString("OLD_PASSWD", "");
        String newPassword = input.getString("NEW_PASSWD", "");
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

}
