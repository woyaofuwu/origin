package com.asiainfo.veris.crm.order.web.person.bandfeedback;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BandFeedBack extends PersonBasePage
{
    public void queryBandInfo(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);

        IData queryData = new DataMap();
        
        String type = condParams.getString("ORDER_TYPE", "");
        if("0".equals(type)){
            queryData.put("ORDER_ID", condParams.getString("ORDER_ID"));
            queryData.put("SUB_ORDER_ID", condParams.getString("SUB_ORDER_ID"));
            IDataOutput output = CSViewCall.callPage(this, "SS.BandFeedBackSVC.queryOrderInfos", queryData,getPagination("OrderNavBarPart"));
            IDataset OrderInfos = output.getData();
            long count = output.getDataCount();
            setOrderInfos(OrderInfos);
            setOrderType(condParams.getString("ORDER_TYPE"));
            setTotalCount(count);
            setAjax(OrderInfos); 
        }else if("1".equals(type)) {
            queryData.put("ORDER_ID", condParams.getString("ORDER_ID"));
            queryData.put("SUB_ORDER_ID", condParams.getString("SUB_ORDER_ID"));
            queryData.put("RETURN_ID", condParams.getString("RETURN_ID"));
            IDataOutput output = CSViewCall.callPage(this, "SS.BandFeedBackSVC.queryReturnInfos", queryData,getPagination("ReturnNavBarPart"));
            IDataset ReturnInfos = output.getData();
            long count = output.getDataCount();
            setReturnInfos(ReturnInfos);
            setOrderType(condParams.getString("ORDER_TYPE"));
            setTotalCount(count);
            setAjax(ReturnInfos);
        }
    }
    
    public void updateStatus(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IDataset dataset = CSViewCall.call(this, "SS.BandFeedBackSVC.updateStatus", param);
        IData result = dataset.getData(0);
        setAjax(result);
    }
    
    public void feedBackInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String orderType = param.getString("ORDER_TYPE");
        if("0".equals(orderType)){
            String orderId = param.getString("ORDER_ID");
            String subOrderId = param.getString("SUBORDER_ID");
            String orderState = param.getString("ORDER_STATE");
            String createTime = param.getString("CREATE_TIME");
            String updateTime = param.getString("UPDATE_TIME");
            String addition = param.getString("ADDITION");
            String [] orderIdArray = orderId.split(",");
            String [] subOrderIdArray = subOrderId.split(",");
            String [] orderStateArray = orderState.split(",");
            String [] createTimeArray = createTime.split(",");
            String [] updateTimeArray = updateTime.split(",");
            String [] additionArray = addition.split(",");
            for (int i = 0; i < orderIdArray.length; i++)
            {
                IData indata = new DataMap();
                indata.put("ORDER_TYPE",orderType);
                indata.put("ORDER_ID", orderIdArray[i]);
                indata.put("SUBORDER_ID", subOrderIdArray[i]);
                indata.put("STATE", orderStateArray[i]);
                indata.put("CREATE_TIME", createTimeArray[i]);
                indata.put("UPDATE_TIME", updateTimeArray[i]);
                if("IN".equals(orderStateArray[i])){
                    indata.put("ADDITION", additionArray[i]);
                }
                indata.put("LOOP_NUM",i);

                IDataset dataset = CSViewCall.call(this, "SS.BandFeedBackSVC.feedBackOrderStatus", indata);
                IData result = dataset.getData(0);
                setAjax(result);
            }
        } else if("1".equals(orderType)){
            String orderId = param.getString("ORDER_ID");
            String subOrderId = param.getString("SUBORDER_ID");
            String returnId = param.getString("RETURN_ID");
            String orderState = param.getString("ORDER_STATE");
            String oprNum = param.getString("OPR_NUM");
            String acceptDate = param.getString("ACCEPT_DATE");
            String updateTime = param.getString("UPDATE_TIME");
            String addition = param.getString("ADDITION");
            String [] orderIdArray = orderId.split(",");
            String [] subOrderIdArray = subOrderId.split(",");
            String [] returnIdArray = returnId.split(",");
            String [] orderStateArray = orderState.split(",");
            String [] oprNumArray = oprNum.split(",");
            String [] acceptDateArray = acceptDate.split(",");
            String [] updateTimeArray = updateTime.split(",");
            String [] additionArray = addition.split(",");
            for (int i = 0; i < orderIdArray.length; i++)
            {
                IData indata = new DataMap();
                indata.put("ORDER_TYPE",orderType);
                indata.put("ORDER_ID", orderIdArray[i]);
                indata.put("SUB_ORDER_ID", subOrderIdArray[i]);
                indata.put("STATUS", orderStateArray[i]);
                indata.put("RETURN_ID", returnIdArray[i]);
                indata.put("OPR_NUM", oprNumArray[i]);
                indata.put("ACCEPT_DATE", acceptDateArray[i]);
                indata.put("UPDATE_TIME", updateTimeArray[i]);
                if("IN".equals(orderStateArray[i])){
                    indata.put("ADDITION", additionArray[i]);
                }
                indata.put("LOOP_NUM",i);
                
                IDataset dataset = CSViewCall.call(this, "SS.BandFeedBackSVC.feedBackOrderStatus", indata);
                IData result = dataset.getData(0);
                setAjax(result);
            }
        }
    }
    
    public abstract void setOrderInfos(IDataset orderInfos);
    public abstract void setOrderInfo(IData orderInfo);
    public abstract void setReturnInfos(IDataset returnInfos);
    public abstract void setReturnInfo(IData returnInfo);
    public abstract void setOrderType(String orderType);
    public abstract void setRowIndex(int rowIndex);
    public abstract void setTotalCount(long totalCount);
}
