
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class RailWayVipServerReqData extends BaseReqData
{
    private String score; // 积分信息

    private String acceptNum; // 受理单编号

    private String consumeScore; // 需要扣减的积分

    private String thisSvccount; // 本次服务总人数

    private String svcScore; // 服务积分

    private String attendants; // 随行人员总数

    private String userPasswd; // 客服密码

    private String idcardnum; // 证件号码

    private String verifyMode; // 校验方式

    private String infoContent; // 证件内容

    private String svcLevel; // 服务类别值

    public final String getAcceptNum()
    {
        return acceptNum;
    }

    public final String getAttendants()
    {
        return attendants;
    }

    public final String getConsumeScore()
    {
        return consumeScore;
    }

    public final String getIdcardnum()
    {
        return idcardnum;
    }

    public final String getInfoContent()
    {
        return infoContent;
    }

    public final String getScore()
    {
        return score;
    }

    public final String getSvcLevel()
    {
        return svcLevel;
    }

    public final String getSvcScore()
    {
        return svcScore;
    }

    public final String getThisSvccount()
    {
        return thisSvccount;
    }

    public final String getUserPasswd()
    {
        return userPasswd;
    }

    public final String getVerifyMode()
    {
        return verifyMode;
    }

    public final void setAcceptNum(String acceptNum)
    {
        this.acceptNum = acceptNum;
    }

    public final void setAttendants(String attendants)
    {
        this.attendants = attendants;
    }

    public final void setConsumeScore(String consumeScore)
    {
        this.consumeScore = consumeScore;
    }

    public final void setIdcardnum(String idcardnum)
    {
        this.idcardnum = idcardnum;
    }

    public final void setInfoContent(String infoContent)
    {
        this.infoContent = infoContent;
    }

    public final void setScore(String score)
    {
        this.score = score;
    }

    public final void setSvcLevel(String svcLevel)
    {
        this.svcLevel = svcLevel;
    }

    public final void setSvcScore(String svcScore)
    {
        this.svcScore = svcScore;
    }

    public final void setThisSvccount(String thisSvccount)
    {
        this.thisSvccount = thisSvccount;
    }

    public final void setUserPasswd(String userPasswd)
    {
        this.userPasswd = userPasswd;
    }

    public final void setVerifyMode(String verifyMode)
    {
        this.verifyMode = verifyMode;
    }

}
