/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.requestdata;

/**
 * @CREATED by gongp@2014-5-4 修改历史 Revision 2014-5-4 上午11:17:11
 */
public class FilteIncomePhoneTradeReqData extends BaseFilteIncomePhoneReqData
{

    private String rejectSn;

    private String operType;

    private String isOpenSms;// 是否要开通短信提醒

    public String getIsOpenSms()
    {
        return isOpenSms;
    }

    public String getOperType()
    {
        return operType;
    }

    public String getRejectSn()
    {
        return rejectSn;
    }

    public void setIsOpenSms(String isOpenSms)
    {
        this.isOpenSms = isOpenSms;
    }

    public void setOperType(String operType)
    {
        this.operType = operType;
    }

    public void setRejectSn(String rejectSn)
    {
        this.rejectSn = rejectSn;
    }

}
