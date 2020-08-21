
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.requestdata.RailWayVipServerReqData;

public class BuildRailWayVipServerReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        RailWayVipServerReqData railWayRqData = (RailWayVipServerReqData) brd;
        IDataUtil.chkParam(param, "RSRV_STR2");
        IDataUtil.chkParam(param, "SVC_LEVEL");

        int consume_score = 0;
        int paraCode1 = param.getInt("RSRV_STR2"); // 需要扣减的积分
        String serviceType = param.getString("SVC_LEVEL", "");

        IData paraminfo = getVipParamMsg(serviceType);
        consume_score = paraminfo.getInt("REDUCE_SCORE"); // 级别所对应的积分
        int thisSvcCount = paraCode1 / consume_score; // 本次服务的总人数

        String userId = brd.getUca().getUserId();

        // 获取服务积分
        IDataset templist = UserOtherInfoQry.getUserOther(userId, "FWJF");
        int svcScore = 0;
        if (IDataUtil.isNotEmpty(templist))
        {
            svcScore = templist.getData(0).getInt("RSRV_STR1", 0);
        }

        // 组装入参
        railWayRqData.setScore(queryUserScore(userId)); // 积分信息
        railWayRqData.setAcceptNum(getAcceptNum()); // 受理单编号
        railWayRqData.setConsumeScore(paraCode1 + ""); // 需要扣减的积分
        railWayRqData.setThisSvccount(thisSvcCount + ""); // 本次服务总人数
        railWayRqData.setSvcScore(svcScore + ""); // 服务积分
        railWayRqData.setAttendants((thisSvcCount - 1) + ""); // 随行人员总数
        railWayRqData.setSvcLevel(serviceType); // 服务类别值

        String userPass = param.getString("USER_PASSWD", ""); // 客服密码
        String inpsptId = param.getString("IDCARDNUM", ""); // 证件号码

        if (StringUtils.isNotBlank(inpsptId))
        {
            railWayRqData.setVerifyMode("0");
            railWayRqData.setInfoContent("证件号码+证件类型");
        }
        else if (StringUtils.isNotBlank(userPass))
        {
            railWayRqData.setVerifyMode("1");
            railWayRqData.setInfoContent("证件号码+用户密码");
        }
    }

    /**
     * 获取受理单编号
     * 
     * @author zhuyu
     * @return bookId
     * @throws Exception
     */
    public String getAcceptNum() throws Exception
    {

        String strDate = SysDateMgr.getSysDateYYYYMMDD();
        String bookId = CSBizBean.getVisit().getStaffEparchyCode() + strDate + SeqMgr.getBookingId();
        return bookId;
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new RailWayVipServerReqData();
    }

    /**
     * 根据客户级别和服务类型 查询允许随从数,应扣积分
     * 
     * @param serviceType
     * @return
     * @throws Exception
     */
    public IData getVipParamMsg(String serviceType) throws Exception
    {
        IData result = new DataMap();
        IData data = new DataMap();
        IDataset paramset = CommparaInfoQry.getCommpara("CSM", "1989", serviceType, "0898");
        if (IDataUtil.isNotEmpty(paramset))
        {
            data.putAll(paramset.getData(0));
            result.put("REDUCE_SCORE", data.getInt("PARA_CODE1", 0));
        }
        else
        {
            result.put("REDUCE_SCORE", "0");
        }

        // 由客户级别计算最多随从人数，默认为2
        result.put("MAX_FOLLOW_NUM", 2);
        return result;
    }

    // 调用积分接口获取用户可用积分
    public String queryUserScore(String userId) throws Exception
    {
        String userScore = "0";

        IDataset userScoreData = AcctCall.queryUserScore(userId);

        if (IDataUtil.isNotEmpty(userScoreData))
        {
            userScore = userScoreData.getData(0).getString("SUM_SCORE", "0");
        }

        return userScore;
    }

}
