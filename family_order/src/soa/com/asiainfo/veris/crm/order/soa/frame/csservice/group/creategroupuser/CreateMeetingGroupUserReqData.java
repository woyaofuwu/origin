
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

public class CreateMeetingGroupUserReqData extends CreateGroupUserReqData
{
    private String MEETING_TYPECODE;

    private String MEETING_PASSWORD;

    private String MEETING_DI_NUMBER;

    private String MEETING_DIN1;

    private String MEETING_DIN2;

    private String MEETING_DIN3;

    private String MEETING_DIN4;

    private String MEETING_CONTRART;

    public String getMEETING_CONTRART()
    {
        return MEETING_CONTRART;
    }

    public String getMEETING_DI_NUMBER()
    {
        return MEETING_DI_NUMBER;
    }

    public String getMEETING_DIN1()
    {
        return MEETING_DIN1;
    }

    public String getMEETING_DIN2()
    {
        return MEETING_DIN2;
    }

    public String getMEETING_DIN3()
    {
        return MEETING_DIN3;
    }

    public String getMEETING_DIN4()
    {
        return MEETING_DIN4;
    }

    public String getMEETING_PASSWORD()
    {
        return MEETING_PASSWORD;
    }

    public String getMEETING_TYPECODE()
    {
        return MEETING_TYPECODE;
    }

    public void setMEETING_CONTRART(String meeting_contrart)
    {
        MEETING_CONTRART = meeting_contrart;
    }

    public void setMEETING_DI_NUMBER(String meeting_di_number)
    {
        MEETING_DI_NUMBER = meeting_di_number;
    }

    public void setMEETING_DIN1(String meeting_din1)
    {
        MEETING_DIN1 = meeting_din1;
    }

    public void setMEETING_DIN2(String meeting_din2)
    {
        MEETING_DIN2 = meeting_din2;
    }

    public void setMEETING_DIN3(String meeting_din3)
    {
        MEETING_DIN3 = meeting_din3;
    }

    public void setMEETING_DIN4(String meeting_din4)
    {
        MEETING_DIN4 = meeting_din4;
    }

    public void setMEETING_PASSWORD(String meeting_password)
    {
        MEETING_PASSWORD = meeting_password;
    }

    public void setMEETING_TYPECODE(String meeting_typecode)
    {
        MEETING_TYPECODE = meeting_typecode;
    }

}
