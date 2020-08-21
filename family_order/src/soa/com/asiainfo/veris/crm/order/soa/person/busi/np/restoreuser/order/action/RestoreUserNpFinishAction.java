
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;

public class RestoreUserNpFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {

        String userId = mainTrade.getString("USER_ID");
        restoreUserNp(userId);
    }

    /**
     * @Function: restoreUserNp
     * @Description: 对应RestoreUserNp
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-19 下午7:32:58 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-19 lijm3 v1.0.0 修改原因
     */
    public void restoreUserNp(String userId) throws Exception
    {
        // 1.查询用户原有携转资料信息
        IDataset nps = UserNpInfoQry.qryUserNpInfosByUserId(userId);
        if (IDataUtil.isEmpty(nps))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_511);
        }
        else if (nps.size() != 1)
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_511);
        }
        // 2.将该条记录搬入历史表
        IData param = new DataMap();
        param.put("USER_ID", userId);
        Dao.executeUpdateByCodeCode("TF_F_USER_NP", "INS_FH_BY_USER_NP", param);

        // 3.获取该用户的USER_TAG_SET字段
        IData user = UcaInfoQry.qryUserInfoByUserId(userId);
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_513);
        }

        // 4.建立一条新携转资料纪录，就是修改一下

        param.put("USER_ID", userId);
        param.put("PORT_OUT_NETID", nps.getData(0).getString("PORT_IN_NETID"));
        param.put("PORT_IN_NETID", nps.getData(0).getString("PORT_OUT_NETID"));
        param.put("NP_TAG", user.getString("USER_TAG_SET").substring(0, 1));
        param.put("REMARK", "携入复机新建记录");
        Dao.executeUpdateByCodeCode("TF_F_USER_NP", "UPD_BY_RESTORE", param);

    }

}
