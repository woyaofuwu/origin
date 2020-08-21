
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.ScoreFactory;

public class CheckScoreDonateIntfBean
{

    public void checkScoreDonate(IData inparam) throws Exception
    {
        String serialNumber = inparam.getString("SERIAL_NUMBER", "");
        String objectSerialNumber = inparam.getString("OBJECT_SERIAL_NUMBER", "");
        String donatedScore = inparam.getString("DONATE_SCORE", "");
        if (StringUtils.isBlank(serialNumber))
        {
            // 用户号码不能为空!
            CSAppException.apperr(CrmUserException.CRM_USER_706);
        }
        if (StringUtils.isBlank(objectSerialNumber))
        {
            // 被转赠号码不能为空!
            CSAppException.apperr(CrmUserException.CRM_USER_1123);
        }
        if (StringUtils.isBlank(donatedScore))
        {
            // 转赠积分不能为空
            CSAppException.apperr(CrmUserException.CRM_USER_1124);
        }
        if (serialNumber.equals(objectSerialNumber))
        {
            // 不能自己转赠给自己!
            CSAppException.apperr(CrmUserException.CRM_USER_1125);
        }
        IDataset dataset = UserInfoQry.qryUserScoreLimit(serialNumber);
        if (IDataUtil.isNotEmpty(dataset))
        {
            // 您的积分已作限制，不能办理积分转赠业务。本次办理不成功!
            CSAppException.apperr(CrmUserException.CRM_USER_1126);
        }
        IDataset objectDataset = UserInfoQry.qryUserScoreLimit(objectSerialNumber);
        if (IDataUtil.isNotEmpty(objectDataset))
        {
            // 对方的积分已作限制，不能办理积分转赠业务。本次办理不成功!
            CSAppException.apperr(CrmUserException.CRM_USER_1127);
        }
        int scoreDonated = 0;
        try
        {
            scoreDonated = Integer.parseInt(donatedScore);
        }
        catch (Exception e)
        {
            // 转赠积分必须为整数!
            CSAppException.apperr(CrmUserException.CRM_USER_1128);
        }
        if (scoreDonated <= 0)
        {
            // 转赠积分必须大于0!
            CSAppException.apperr(CrmUserException.CRM_USER_1129);
        }
        if (scoreDonated < 200)
        {
            // 单笔转赠积分数量须不小200分，本次办理不成功!
            CSAppException.apperr(CrmUserException.CRM_USER_1130);
        }
        if (scoreDonated > 10000)
        {
            // 单笔转赠积分数量不得超过10000分，本次办理不成功!
            CSAppException.apperr(CrmUserException.CRM_USER_1131);
        }

        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        UserTradeData userInfo = uca.getUser();

        if (ScoreFactory.OPEN_MODE.equals(userInfo.getOpenMode()))
        {
            // 无权户不能进行积分转赠操作!
            CSAppException.apperr(CrmUserException.CRM_USER_1132);
        }
        if (!ScoreFactory.USER_STATE_CODESET.equals(userInfo.getUserStateCodeset()))
        {
            // 用户号码处于非开通状态,不能转赠!
            CSAppException.apperr(CrmUserException.CRM_USER_1133);
        }

        String score = "0";
        IDataset scoreInfo = AcctCall.queryUserScore(userInfo.getUserId());
        if (IDataUtil.isEmpty(scoreInfo))
        {
            // 获取用户积分无数据!
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        score = scoreInfo.getData(0).getString("SCORE");

        if ("G002".equals(uca.getBrandCode()))
        {
            if (!"1".equals(uca.getCustomer().getIsRealName()))
            {
                // 用户号码不能为神州行非实名制!
                CSAppException.apperr(CrmUserException.CRM_USER_1134);
            }
        }

        UcaData objectUca = UcaDataFactory.getNormalUca(objectSerialNumber);
        UserTradeData objectUserInfo = objectUca.getUser();
        if ("G002".equals(objectUca.getBrandCode()))
        {
            if (!"1".equals(objectUca.getCustomer().getIsRealName()))
            {
                // 被转赠号码不能为神州行非实名制!
                CSAppException.apperr(CrmUserException.CRM_USER_1135);
            }
        }

        IDataset dataset4 = RelaUUInfoQry.check_byuserida_idbzm(objectUca.getUserId(), "45", null, null);// 判断目标号码是不是亲亲网
        IDataset dataset3 = RelaUUInfoQry.check_byuserida_idbzm(userInfo.getUserId(), "45", null, null);// 判断原号码是不是亲亲网
        IDataset dataset2 = RelaUUInfoQry.judgeQinQinWang(userInfo.getUserId(), objectUca.getUserId());
        if (IDataUtil.isEmpty(dataset4))
        {
            // 您不是亲亲网套餐客户，办理不成功。询10086!
            CSAppException.apperr(CrmUserException.CRM_USER_1136);
        }
        if (IDataUtil.isEmpty(dataset3))
        {
            // 您不是亲亲网套餐客户，办理不成功。询10086!
            CSAppException.apperr(CrmUserException.CRM_USER_1136);
        }
        if (dataset2.size() == 0)
        {
            // 您所邀请转赠积分的客户不是您所在亲亲网的成员，办理不成功，如需加入亲亲网请询10086!
            CSAppException.apperr(CrmUserException.CRM_USER_1137);
        }

        String userStateCodeset2 = objectUserInfo.getUserStateCodeset();

        if (!ScoreFactory.USER_STATE_CODESET.equals(userStateCodeset2))
        {
            // 被赠号码处于非开通状态,不能被转赠!
            CSAppException.apperr(CrmUserException.CRM_USER_1138);
        }
        if (Integer.parseInt(score) - scoreDonated < 0)
        {
            // 您所邀请转赠积分的客户积分余额不足，本次办理不成功!
            CSAppException.apperr(CrmUserException.CRM_USER_1139);
        }
    }
}
