
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

public class GameAccountCheckBean extends CSBizBean
{

    /**
     * 游戏帐户鉴权接口
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IData gameAccountCheck(IData param) throws Exception
    {
        String serialNumber = param.getString("IDVALUE", "");
        IData result = new DataMap();
        if ("".equals(serialNumber.trim()))
        {
            result.put("X_RESULTCODE", "0925");// 手机号码为空,转成其它错误
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }

        IData userParam = new DataMap();
        userParam.put("SERIAL_NUMBER", serialNumber);
        IDataset userInfos = PlatInfoQry.getUserInfoBySNOpenDate(userParam);

        if (userInfos == null || userInfos.size() == 0)
        {
            result.put("X_RESULTCODE", "0925");// 用户信息不存在
            result.put("X_RESULTINFO", "用户信息不存在");
            result.put("DEAL_RESULT", "02");
            result.put("BALANCE", "-1");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }

        IData user = userInfos.getData(0);
        String removeTag = user.getString("REMOVE_TAG");
        if ("0".equals(removeTag))
        {
            // 查询用户主体服务状态
            userParam.clear();
            userParam.put("USER_ID", user.getString("USER_ID"));
            IDataset states = PlatInfoQry.getUserMainState(userParam);
            if (states == null || states.size() == 0)
            {
                result.put("X_RESULTCODE", "536627");// 查不到主体服务状态,转成其它错误
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
                return result;
            }
            IData state = states.getData(0);
            if (!"0".equals(state.getString("STATE_CODE")))
            {
                result.put("X_RESULTCODE", "0922");// 用户已停机
                result.put("X_RESULTINFO", "用户已停机");
                result.put("DEAL_RESULT", "04");
                result.put("BALANCE", "-1");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
                return result;
            }
            else
            {
                result.put("DEAL_RESULT", "00");
                result.put("BALANCE", "-1");
                return result;
            }
        }
        else if ("1".equals(removeTag) || "3".equals(removeTag))
        {
            result.put("X_RESULTCODE", "0923");// 预销户
            result.put("X_RESULTINFO", "用户已预销户");
            result.put("DEAL_RESULT", "03");
            result.put("BALANCE", "-1");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        else
        {
            result.put("X_RESULTCODE", "0924");// 用户销户
            result.put("X_RESULTINFO", "用户已销户");
            result.put("DEAL_RESULT", "02");
            result.put("BALANCE", "-1");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
    }
}
