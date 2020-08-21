
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifypwd;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;

public class CttBroadBandModifyPWDSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

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

    public IDataset qryBroadBandUser(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        IData data = new DataMap();
        // 查询宽带信息
        String serialNumber = input.getString("SERIAL_NUMBER");
        String userId = input.getString("USER_ID");
        IDataset accessInfos = BroadBandInfoQry.getBroadBandWidenetActByUserId(userId);
        if (IDataUtil.isEmpty(accessInfos))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_11, serialNumber);
        }
        data.putAll(accessInfos.getData(0));
        IDataset rateInfos = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isNotEmpty(rateInfos))
        {
            data.putAll(rateInfos.getData(0));
        }
        else
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_11, serialNumber);
        }
        // 查询宽带用户速率信息
        // IDataset rateInfos = BroadBandInfoQry.queryUserRateByUserId(userId);
        // if (IDataUtil.isNotEmpty(rateInfos))
        // {
        // data.putAll(rateInfos.getData(0));
        // }
        // else
        // {
        // CSAppException.apperr(BroadBandException.CRM_BROADBAND_12, serialNumber);
        // }
        //
        // // 查询宽带用户地址信息
        // IData param = new DataMap();
        // param.put("USER_ID", userId);
        // IDataset addrInfos = BroadBandInfoQry.queryBroadBandAddressInfo(param);
        // if (IDataUtil.isNotEmpty(addrInfos))
        // {
        // data.putAll(addrInfos.getData(0));
        // }
        // else
        // {
        // CSAppException.apperr(BroadBandException.CRM_BROADBAND_13, serialNumber);
        // }

        result.add(data);
        return result;

    }

}
