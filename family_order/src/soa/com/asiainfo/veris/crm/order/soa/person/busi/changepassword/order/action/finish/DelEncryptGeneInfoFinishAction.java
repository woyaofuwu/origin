
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 修改密码时，删除用户密码因子（tf_f_user_encrypt_gene表）信息
 * 
 * @author liutt
 */
public class DelEncryptGeneInfoFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        if ("100".equals(mainTrade.getString("TRADE_TYPE_CODE")))
        {
            if (!"1".equals(mainTrade.getString("RSRV_STR9")))
            {// 业务类型为100（过户），当RSRV_STR9为1时（过户时有修改密码且有密码因子），才删除密码因子
                return;
            }
        }
        String userId = mainTrade.getString("USER_ID");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        Dao.executeUpdateByCodeCode("TF_F_USER_ENCRYPT_GENE", "DEL_BY_USERID", param);
    }

}
