
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

public class ScoreDonateBean extends CSBizBean
{

    /**
     * 积分转赠查询
     * 
     * @param pd
     * @param inparam
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IData queryDonateScore(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        if (StringUtils.isBlank(serialNumber))
        {
            // 服务号码不能为空
            CSAppException.apperr(ScoreException.CRM_SCORE_1);
        }

        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            // 该服务号码用户信息不存在
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }

        // 查询用户积分
        String userId = user.getString("USER_ID");
        IDataset scoreInfo = AcctCall.queryUserScore(userId); // 查询积分接口，暂时没通
        if (IDataUtil.isEmpty(scoreInfo))
        {
            // 获取用户积分无数据
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String score = scoreInfo.getData(0).getString("SUM_SCORE");

        // 查询赠送积分
        IDataset zsScores = ScoreAcctInfoQry.queryZsScoreByUserId(userId);
        if (IDataUtil.isEmpty(zsScores))
        {
            // 获取用户赠送积分总值无数据
            CSAppException.apperr(ScoreException.CRM_SCORE_2);
        }
        String zsScore = zsScores.getData(0).getString("ZSSCORE", "0");

        // 查询被赠送积分
        IDataset bzsScores = ScoreAcctInfoQry.queryBzsScoreByUserId(userId);
        if (IDataUtil.isEmpty(bzsScores))
        {
            // 获取用户被赠送积分总值无数据
            CSAppException.apperr(ScoreException.CRM_SCORE_3);
        }
        String bzsScore = bzsScores.getData(0).getString("BZSSCORE", "0");

        IData backInfo = new DataMap();
        backInfo.put("SCORE", score);
        backInfo.put("ZSSCORE", zsScore);
        backInfo.put("BZSSCORE", bzsScore);

        return backInfo;
    }

    /**
     * 获取子业务信息
     * @param inData
     * @return
     * @throws Exception
     *
     * @author zhaohj3
     * @date 2017-12-20 17:19:45
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
        String score = scoreInfo.getData(0).getString("SUM_SCORE");

        commInfo.putAll(scoreInfo.getData(0));
        commInfo.put("SCORE", score);
        commInfo.put("RSRV_NUM2", scoreInfo.getData(0).getString("SUM_TOTAL_SCORE"));// 总消费积分

        data.put("COMMINFO", commInfo);

        return data;
    }
}
