
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;

public class NPScoreCleanBean extends CSBizBean
{

    /**
     * 获取积分信息
     */
    public IData getCommInfo(IData inData) throws Exception
    {
        IData data = new DataMap();
        IData commInfo = new DataMap();

        String userId = inData.getString("USER_ID");

        // 查用户积分
        IDataset scoreInfo = AcctCall.queryUserScore(userId);
        if (IDataUtil.isEmpty(scoreInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        System.out.println("NPScoreCleanBean----scoreInfo:"+scoreInfo);

        String score = scoreInfo.getData(0).getString("SUM_SCORE");
 
        commInfo.putAll(scoreInfo.getData(0));
        commInfo.put("SCORE", score);
        commInfo.put("RSRV_NUM2", scoreInfo.getData(0).getString("SUM_TOTAL_SCORE"));// 总消费积分

        data.put("COMMINFO", commInfo);

        return data;
    }
}
