/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.requestdata;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-6-4 修改历史 Revision 2014-6-4 上午09:39:38
 */
public class SaleEntityCardRequestData extends BaseReqData
{
    private String saleTypeRadio;

    private IDataset cardList;

    private String custName;

    public IDataset getCardList()
    {
        return cardList;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getSaleTypeRadio()
    {
        return saleTypeRadio;
    }

    public void setCardList(IDataset cardList)
    {
        this.cardList = cardList;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setSaleTypeRadio(String saleTypeRadio)
    {
        this.saleTypeRadio = saleTypeRadio;
    }

}
