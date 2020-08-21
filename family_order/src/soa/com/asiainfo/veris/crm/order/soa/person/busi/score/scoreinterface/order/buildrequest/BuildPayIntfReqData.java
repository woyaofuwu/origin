
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScorePayReqData;

public class BuildPayIntfReqData extends BaseBuilder implements IBuilder
{
    @Override
    // 积分支付
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        // 入参校验
        IDataUtil.chkParam(param, "TRADE_SEQ");
        IDataUtil.chkParam(param, "MOBILE");
        IDataUtil.chkParam(param, "PAY_POINT");
        
        ScorePayReqData reqData = (ScorePayReqData) brd;
        reqData.setTRADE_SEQ(param.getString("TRADE_SEQ",""));
        reqData.setTRADE_TIME(param.getString("TRADE_TIME",""));
        reqData.setORGID(param.getString("ORGID",""));
        reqData.setTRADE_ID(param.getString("TRADE_ID",""));
        reqData.setF_ORDER_ID(param.getString("F_ORDER_ID",""));
        reqData.setMOBILE(param.getString("MOBILE",""));
        reqData.setCC_PASSWD(param.getString("CC_PASSWD",""));
        reqData.setPAY_POINT(param.getString("PAY_POINT",""));
        reqData.setACTION_TYPE(param.getString("ACTION_TYPE",""));
        checkBefore(brd);// 业务受理时校验
    }

    private void checkBefore(BaseReqData brd) throws Exception
    {
    	ScorePayReqData reqData = (ScorePayReqData) brd;

        // 判断用户状态
        if (!"0".equals(reqData.getUca().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_411);
        }

        // 判断用户积分是否足够兑换
        int scoreValue = Integer.parseInt(reqData.getPAY_POINT());
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
        String passwd = reqData.getCC_PASSWD();
        if (StringUtils.isNotEmpty(passwd))
        {
        	String originUserPassWd = reqData.getUca().getUser().getUserPasswd();
        	boolean tag = PasswdMgr.checkUserPassword(passwd, reqData.getUca().getUserId(), originUserPassWd);
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
        return new ScorePayReqData();
    }

}
