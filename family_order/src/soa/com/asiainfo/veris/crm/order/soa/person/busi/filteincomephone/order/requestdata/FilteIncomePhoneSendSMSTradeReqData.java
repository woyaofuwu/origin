/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.requestdata;

/**
 * @CREATED by gongp@2014-4-28 修改历史 Revision 2014-4-28 上午11:32:44
 */
public class FilteIncomePhoneSendSMSTradeReqData extends BaseFilteIncomePhoneReqData
{

    private String openfuncRadio;

    private String sendSmsTag;

    public String getOpenfuncRadio()
    {
        return openfuncRadio;
    }

    public String getSendSmsTag()
    {
        return sendSmsTag;
    }

    public void setOpenfuncRadio(String openfuncRadio)
    {
        this.openfuncRadio = openfuncRadio;
    }

    public void setSendSmsTag(String sendSmsTag)
    {
        this.sendSmsTag = sendSmsTag;
    }
}
