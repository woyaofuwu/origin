
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.BaseDeductReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDeductReqData;

public class BuildDeductIntfReqData extends BaseBuilder implements IBuilder
{
    @Override
    // 积分扣减
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "SCORE_VALUE");

        BaseDeductReqData reqData = (BaseDeductReqData) brd;
        reqData.setRsrvStr1(param.getString("RSRV_STR1"));
        reqData.setScoreValue(param.getString("SCORE_VALUE"));
        reqData.setXOrderId(param.getString("ORDER_ID"));
        reqData.setOprt(param.getString("OPRT"));
        reqData.setUserPasswd(param.getString("USER_PASSWD"));

        checkBefore(brd);// 业务受理时校验
    }

    private void checkBefore(BaseReqData brd) throws Exception
    {
        ScoreDeductReqData reqData = (ScoreDeductReqData) brd;

        // 判断用户状态
        if (!"0".equals(reqData.getUca().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_406);
        }

        // 判断用户积分是否足够兑换
        int scoreValue = Integer.parseInt(reqData.getScoreValue());
        IData data = AcctCall.queryUserScoreone(reqData.getUca().getUserId());
        int score = 0;// 用户积分
        if (IDataUtil.isNotEmpty(data))
        {
            score = Integer.parseInt(data.getString("SCORE"));
        }
        if (scoreValue > score)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_407);
        }

        // 判断用户客服密码是否正确
        String passwd = reqData.getUserPasswd();
        if (StringUtils.isNotEmpty(passwd))
        {
            boolean tag = UserInfoQry.checkUserPassWd(reqData.getUca().getUserId(), passwd);
            if (!tag)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_401);
            }
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ScoreDeductReqData();
    }

}
