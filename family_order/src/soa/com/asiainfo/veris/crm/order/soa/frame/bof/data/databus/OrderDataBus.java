
package com.asiainfo.veris.crm.order.soa.frame.bof.data.databus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ElementRelaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayMoneyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public final class OrderDataBus
{

    private String acceptTime;

    private List<BusiTradeData> btds = new ArrayList<BusiTradeData>();

    private String batchId;

    private List<FeeTradeData> feeTradeDatas;

    private String orderId;

    private List<String> lockList = new ArrayList<String>();

    private String orderTypeCode;

    private List<PayMoneyTradeData> payMoneyTradeDatas;

    private Map<String, UcaData> ucas = new HashMap<String, UcaData>();

    private List<BizData> grpBizData = new ArrayList<BizData>();

    private List<ElementRelaData> elementRelaDataList = new ArrayList<ElementRelaData>();

    private String submitType;
    
    private String subscribeStateX;// 未支付状态才放值

    private boolean isCreateShoppingDetail = false;

    public String getSubscribeStateX()
    {
        return subscribeStateX;
    }
    
    public void setSubscribeStateX(String subscribeStateX)
    {
        this.subscribeStateX = subscribeStateX;
    }

    public void addBusiTradeData(BusiTradeData btd)
    {
        this.btds.add(btd);
    }

    public void addElementRelaData(ElementRelaData elementRelaData)
    {
        this.elementRelaDataList.add(elementRelaData);
    }

    public void addFeeTradeData(FeeTradeData feeTradeData)
    {
        if (this.feeTradeDatas == null)
        {
            this.feeTradeDatas = new ArrayList<FeeTradeData>();
        }
        this.feeTradeDatas.add(feeTradeData);
    }

    public void addLockList(String lockObj)
    {
        lockList.add(lockObj);
    }

    public void addPayMoneyTradeData(PayMoneyTradeData payMoneyTradeData)
    {
        if (this.payMoneyTradeDatas == null)
        {
            this.payMoneyTradeDatas = new ArrayList<PayMoneyTradeData>();
        }
        this.payMoneyTradeDatas.add(payMoneyTradeData);
    }

    public String getAcceptTime()
    {
        return acceptTime;
    }

    public String getAdvanceFee() throws Exception
    {
        return getFee(BofConst.FEE_MODE_ADVANCEFEE);
    }

    public String getBatchId()
    {
        return batchId;
    }

    public List<BusiTradeData> getBtds()
    {
        return btds;
    }

    /***
     * * @return Returns the elementRelaDataList.
     */
    public List<ElementRelaData> getElementRelaDataList()
    {
        return elementRelaDataList;
    }

    private String getFee(String feeMode) throws Exception
    {
        int fee = 0;
        if (this.feeTradeDatas != null && this.feeTradeDatas.size() > 0)
        {
            for (int i = 0; i < this.feeTradeDatas.size(); i++)
            {
                FeeTradeData feeTradeData = this.feeTradeDatas.get(i);
                if (feeTradeData.getFeeMode().equals(feeMode))
                {
                    fee += Integer.parseInt(feeTradeData.getFee());
                }
            }
        }

        return String.valueOf(fee);
    }

    public String getForeGift() throws Exception
    {
        return getFee(BofConst.FEE_MODE_FOREGIFT);
    }

    public List<BizData> getGrpBizData()
    {
        return grpBizData;
    }

    public List<String> getLockList()
    {
        return lockList;
    }

    public String getOperFee() throws Exception
    {
        return getFee(BofConst.FEE_MODE_OPERFEE);
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getOrderTypeCode()
    {
        return orderTypeCode;
    }

    public String getSubmitType()
    {
        return submitType;
    }

    public UcaData getUca(String serialNumber) throws Exception
    {
        return this.ucas.get(serialNumber);
    }

    public boolean isCreateShoppingDetail()
    {
        return isCreateShoppingDetail;
    }

    public void setAcceptTime(String acceptTime)
    {
        this.acceptTime = acceptTime;
    }

    public void setBatchId(String batchId)
    {
        this.batchId = batchId;
    }

    public void setCreateShoppingDetail(boolean isCreateShoppingDetail)
    {
        this.isCreateShoppingDetail = isCreateShoppingDetail;
    }

    /***
     * @param elementRelaDataList
     *            The elementRelaDataList to set.
     */
    public void setElementRelaDataList(List<ElementRelaData> elementRelaDataList)
    {
        this.elementRelaDataList = elementRelaDataList;
    }

    public void setGrpBizData(List<BizData> grpBizData)
    {
        this.grpBizData = grpBizData;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setOrderTypeCode(String orderTypeCode)
    {
        this.orderTypeCode = orderTypeCode;
    }

    public void setSubmitType(String submitType)
    {
        this.submitType = submitType;
    }

    public void setUca(UcaData uca)
    {
        this.ucas.put(uca.getSerialNumber(), uca);
    }
}
