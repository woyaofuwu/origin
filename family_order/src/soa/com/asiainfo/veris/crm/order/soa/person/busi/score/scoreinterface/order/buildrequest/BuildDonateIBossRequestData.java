
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.ScoreFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateIBossRequestData;

public class BuildDonateIBossRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
     // 入参校验
        IDataUtil.chkParam(param, "TRADE_SEQ");
        IDataUtil.chkParam(param, "L_MOBILE");
        IDataUtil.chkParam(param, "B_MOBILE");
        IDataUtil.chkParam(param, "TRANSFER_POINT");

        ScoreDonateIBossRequestData reqData = (ScoreDonateIBossRequestData) brd;
        reqData.setTRADE_SEQ(param.getString("TRADE_SEQ",""));
        reqData.setTRADE_TIME(param.getString("TRADE_TIME",""));
        reqData.setORGID(param.getString("ORGID",""));
        reqData.setL_MOBILE(param.getString("L_MOBILE",""));
        reqData.setB_MOBILE(param.getString("B_MOBILE",""));
        reqData.setTRANSFER_POINT(param.getString("TRANSFER_POINT",""));
        reqData.setCC_PASSWD(param.getString("CC_PASSWD",""));
        reqData.setCOMMENTS(param.getString("COMMENTS",""));
        IData ldata = AcctCall.queryUserScoreone(reqData.getUca().getUserId());
        String lscore = "0";// 转让人用户积分
        String lscore_sum = "0";// 转让人用户总积分
        if (IDataUtil.isNotEmpty(ldata))
        {
            lscore = ldata.getString("SCORE");

            lscore_sum= ldata.getString("SUM_SCORE",lscore);
        }
        reqData.setSCORE(lscore);
        reqData.setSUM_SCORE(lscore_sum);
        String bscore = "0";// 受让人用户积分
        String bscore_sum = "0";// 受让人用户总积分
        String USERID = "";// 受让人用户积分
        IData userinfo=UserInfoQry.getUserInfoBySN(param.getString("B_MOBILE",""));
        if (IDataUtil.isNotEmpty(userinfo))
        {
        	USERID=userinfo.getString("USER_ID", "");
        	IData bdata = AcctCall.queryUserScoreone(USERID);
        	if (IDataUtil.isNotEmpty(bdata))
            {
        		bscore = bdata.getString("SCORE");
        		bscore_sum= bdata.getString("SUM_SCORE",bscore);
            }
        }
        reqData.setOBJ_SCORE(bscore);
        reqData.setOBJ_SUM_SCORE(bscore_sum);
        reqData.setOBJ_USERID(USERID);
        IData user =reqData.getUca().getUser().toData();
        user.put("SCORE", lscore);
        loadCheck(user);
        checkBefore(brd);// 业务受理时校验

    }
    
    private void checkBefore(BaseReqData brd) throws Exception
    {
    	ScoreDonateIBossRequestData reqData = (ScoreDonateIBossRequestData) brd;

        // 判断用户积分是否足够兑换
        int scoreValue = Integer.parseInt(reqData.getTRANSFER_POINT());
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
        // 判断用户状态 是否正常
        String userStateCodeset = reqData.getUca().getUser().getUserStateCodeset();
        // 积分商城办理积分扣减时如果用户是特开状态（N）时系统报用户状态不正常扣减失败，请修改，N也是正常的用户状态，允许办理积分业务
        if (!"0".equals(userStateCodeset) && !"N".equals(userStateCodeset))
        {
            // 用户状态不正常扣减失败!
            CSAppException.apperr(CrmUserException.CRM_USER_406);
        }
        // 判断用户客服密码是否正确
        String passwd = reqData.getCC_PASSWD();
        if (StringUtils.isNotEmpty(passwd))
        {
            boolean tag = UserInfoQry.checkUserPassword(reqData.getUca().getUserId(), passwd);
            if (!tag)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_401);
            }
        }
    }
    private void loadCheck(IData inData) throws Exception
    {
        // TODO Auto-generated method stub/* 获取用户品牌编码,用户开户方式 ,用户状态,用户积分,用户基本品牌 */
        // String brandCode = queryBrand( td);
        String open_mode = inData.getString("OPEN_MODE");
        String user_state_codeset = inData.getString("USER_STATE_CODESET");
        /* 根据user_state_codeset取得state_name */
        IData inparam = new DataMap();
        inparam.put("STATECODESET", user_state_codeset);
        inparam.put("SERVICE_ID", ScoreFactory.SERVICE_ID);
        /* 获取用户积分 */
        String score_value = inData.getString("SCORE", "0");

        if (ScoreFactory.OPEN_MODE.equals(open_mode))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_252);
        }
        if (Integer.parseInt(score_value) <= 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_254);
        }
    }
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ScoreDonateIBossRequestData();
    }

}
