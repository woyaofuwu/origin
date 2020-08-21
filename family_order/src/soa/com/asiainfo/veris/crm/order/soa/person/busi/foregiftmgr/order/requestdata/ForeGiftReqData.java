/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr.order.requestdata;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-4-10 修改历史 Revision 2014-4-10 上午10:16:30
 */
public class ForeGiftReqData extends BaseReqData
{

    private IDataset userForeGifts;// 前台传入的押金Dataset

    private String invoiceNo;// 发票号码

    private String nonCustomerUserId;// 无主押金userId

    private String operType;// 押金操作类型

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public String getNonCustomerUserId()
    {
        return nonCustomerUserId;
    }

    public String getOperType()
    {
        return operType;
    }

    public IDataset getUserForeGifts()
    {
        return userForeGifts;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public void setNonCustomerUserId(String nonCustomerUserId)
    {
        this.nonCustomerUserId = nonCustomerUserId;
    }

    public void setOperType(String operType)
    {
        this.operType = operType;
    }

    public void setUserForeGifts(IDataset userForeGifts)
    {
        this.userForeGifts = userForeGifts;
    }
}
