package com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

/**
 * @desc 融合撤单业务受理请求对象
 * @author danglt
 * @date 2018年12月17日
 * @version v1.0
 */
public class TradeCancelReqData
{

    // 撤单原因（必传）
    private String cancelReason;

    // 撤单新生成的ORDER_ID
    private String cancelOrderId;
    
    // 返销新生成的ORDER_ID
    private String undoOrderId;
    
    // 临时ORDER_ID，承载返销和撤单的ORDER_ID
    private String newOrderId;

    // 原受理的ORDER_ID(必传)
    private String oldOrderId;

    // 撤销的业务类型(必传)
    private String cancelOrderType;

    // 撤单受理时间
    private String acceptTime;

    // 是否撤销全部订单标记
    private boolean cancelAll;
    
    // 是否多工单
    private boolean multiOrder;

    private String remarks;

    // 撤销的工单集合
    private List<CancelTradeData> cancelTradeList;

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public String getOldOrderId()
    {
        return oldOrderId;
    }

    public void setOldOrderId(String oldOrderId)
    {
        this.oldOrderId = oldOrderId;
    }

    public String getCancelOrderType()
    {
        return cancelOrderType;
    }

    public void setCancelOrderType(String cancelOrderType)
    {
        this.cancelOrderType = cancelOrderType;
    }

    public String getAcceptTime()
    {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime)
    {
        this.acceptTime = acceptTime;
    }

    public boolean isCancelAll()
    {
        return cancelAll;
    }

    public void setCancelAll(boolean cancelAll)
    {
        this.cancelAll = cancelAll;
    }

    public List<CancelTradeData> getCancelTradeList()
    {
        return cancelTradeList;
    }

    public void setCancelTradeList(IDataset cancelTradeList)
    {
        this.cancelTradeList = new ArrayList<CancelTradeData>();

        for (int i = 0, s = cancelTradeList.size(); i < s; i++)
        {
        	IData cancelTrade = cancelTradeList.getData(i);
            CancelTradeData cancelTradeData = new CancelTradeData();
            cancelTradeData.setTradeId(cancelTrade.getString("TRADE_ID"));
            cancelTradeData.setTradeTypeCode(cancelTrade.getString("TRADE_TYPE_CODE"));
            cancelTradeData.setFinishFlag(cancelTrade.getInt("FINISH_FLAG", 0));
            cancelTradeData.setSerialNumber(cancelTrade.getString("SERIAL_NUMBER"));
            this.cancelTradeList.add(cancelTradeData);
        }
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public String getCancelOrderId()
    {
        return cancelOrderId;
    }

    public void setCancelOrderId(String cancelOrderId)
    {
        this.cancelOrderId = cancelOrderId;
    }

    public String getUndoOrderId()
    {
        return undoOrderId;
    }

    public void setUndoOrderId(String undoOrderId)
    {
        this.undoOrderId = undoOrderId;
    }

    public String getNewOrderId()
    {
        return newOrderId;
    }

    public void setNewOrderId(String newOrderId)
    {
        this.newOrderId = newOrderId;
    }

    public boolean isMultiOrder() {
		return multiOrder;
	}

	public void setMultiOrder(boolean multiOrder) {
		this.multiOrder = multiOrder;
	}

	public class CancelTradeData
    {
    	
        private String tradeId;

        private String tradeTypeCode;

        private int finishFlag;// 完工标记
        
        private String serialNumber;
        
        public String getTradeId()
        {
            return tradeId;
        }

        public void setTradeId(String tradeId)
        {
            this.tradeId = tradeId;
        }

        public String getTradeTypeCode()
        {
            return tradeTypeCode;
        }

        public void setTradeTypeCode(String tradeTypeCode)
        {
            this.tradeTypeCode = tradeTypeCode;
        }

        public int getFinishFlag()
        {
            return finishFlag;
        }

        public void setFinishFlag(int finishFlag)
        {
            this.finishFlag = finishFlag;
        }
        
        
        public String getSerialNumber() {
			return serialNumber;
		}

		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		public IData toData() {
        	IData data = new DataMap();
        	data.put("TRADE_ID", tradeId);
        	data.put("TRADE_TYPE_CODE", tradeTypeCode);
        	data.put("FINISH_FLAG", finishFlag);
        	data.put("SERIAL_NUMBER", serialNumber);
        	return data;
        }
    }
}
