
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

/**
 * 在途工单处理
 * 
 * @author liujy
 */
public abstract class GroupDatalineOrder extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfoCount(long infoCount);

    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData inputParam = new DataMap();

        String ibSysId = param.getString("cond_IB_SYS_ID");
        String groupId = param.getString("cond_GROUP_ID");
        String state = param.getString("cond_ENET_INFO_QUERY_STATE");

        IDataOutput dataOutput = null;
        inputParam.put("IBSYSID", ibSysId);
        inputParam.put("GROUP_ID", groupId);
        inputParam.put("STATE", state);
        
        dataOutput = CSViewCall.callPage(this, "SS.DatalineOrderSVC.queryDatalineOrder", inputParam, getPagination("pageNav"));

        if (null != dataOutput && dataOutput.getData().size() > 0){
            setHintInfo("查询成功~~！");
        }else{
            setHintInfo("没有符合条件的查询结果~~！");
        }

        setCondition(param);
        setInfos(dataOutput.getData());
        setInfoCount(dataOutput.getDataCount());
    }
    
    public void dealDatalineOrder(IRequestCycle cycle) throws Exception {
        IData data = new DataMap();
        IData param = getData();
        StringBuilder sb = new StringBuilder();
        IDataset resultData = new DatasetList();
        String items = param.getString("ITEMS");
        
        String item[] = items.split(",");
        for (int i = 0; i < item.length; i++)
        {
            IData inputParam = new DataMap();
            String str =  item[i];
            String dataline[] = str.split(";");
            inputParam.put("IBSYSID", dataline[0]);
            inputParam.put("GROUP_ID", dataline[1]);
            inputParam.put("STATE", dataline[2]);
            inputParam.put("USER_EPARCHY_CODE", "0898");
            
            resultData = CSViewCall.call(this, "SS.CreateInternetGroupUserBatBean.dealDatalineOrder", inputParam);
            
            if(StringUtils.isNotBlank(resultData.getData(0).getString("ORDER_ID"))){
                sb.append(resultData.getData(0).getString("ORDER_ID"));
                if(i != item.length-1){
                    sb.append(",");
                }
            }
            
            IData commonParam = new DataMap();
            commonParam.put("IBSYSID", dataline[0]);
            commonParam.put("GROUP_ID", dataline[1]);
            commonParam.put("STATE", dataline[2]);
            commonParam.put("USER_EPARCHY_CODE", "0898");
            
            crareTradeExt(commonParam,resultData);
        }
        
        data.put("ORDER_ID", sb.toString());
        if(null != resultData && resultData.size() >0){
            setHintInfo("处理成功！");
            setAjax(data);
          
        }else{
            setHintInfo("处理失败！");
        }
        
    }
    
    public void crareTradeExt(IData param,IDataset resultData)throws Exception {
        String type = "";
        String prodcutId = "";
        IData dataParam = (IData) Clone.deepClone(param);
        dataParam.put("STATE", "1");
        IDataset commonData = CSViewCall.call(this, "SS.DatalineOrderSVC.queryCommonDataInfo",dataParam);
        for (int i = 0; i < commonData.size(); i++)
        {
            IData data = commonData.getData(i);
            if("SHEETTYPE".equals(data.getString("FIELDEN_NAME"))){
                if("32".equals( data.getString("FIELD_CONTENT"))){
                    type = "bossOpenBill";
                }else if("33".equals( data.getString("FIELD_CONTENT"))){
                    type = "bossModify";
                }
            }
            if("SERVICETYPE".equals(data.getString("FIELDEN_NAME"))){
                if("4".equals( data.getString("FIELD_CONTENT"))){
                    prodcutId = "7012";
                }else if("6".equals( data.getString("FIELD_CONTENT"))){
                    prodcutId = "7011";
                }else if("7".equals( data.getString("FIELD_CONTENT"))){
                    prodcutId = "7010";
                }
            }
        }
       
        
        IData tradeExt = new DataMap();
        String tradeId = "";
        if(null != resultData && resultData.size() > 0){
            IData data = resultData.getData(0);
            IData orderParam = new DataMap();
            orderParam.put("ORDER_ID", data.getString("ORDER_ID"));
            orderParam.put("PRODUCT_ID",prodcutId);
            orderParam.put("CANCEL_TAG","0");

            IDataset order = CSViewCall.call(this, "CS.TradeInfoQrySVC.queryTradeByOrderProduct",orderParam);
            for (int i = 0; i < order.size(); i++)
            {
                IData trade = order.getData(i);
                if("0".equals(trade.getString("OLCOM_TAG")) && "N001".equals(trade.getString("BRAND_CODE"))){
                    tradeId = trade.getString("TRADE_ID");
                }
                
            }
        }
        
        tradeExt.put("TRADE_ID", tradeId);
        tradeExt.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        
        tradeExt.put("ATTR_CODE", "ESOP");
        tradeExt.put("ATTR_VALUE", param.getString("IBSYSID"));
        tradeExt.put("UPDATE_TIME", SysDateMgr.getSysDate());
        tradeExt.put("UPDATE_STAFF_ID", "TESTHK01");
        tradeExt.put("UPDATE_DEPART_ID", "36601");
        tradeExt.put("RSRV_STR1",type);
        tradeExt.put("RSRV_STR2", "01");
        tradeExt.put("RSRV_STR10", "EOS");
        
        CSViewCall.call(this, "SS.DatalineOrderSVC.createTradeExt",tradeExt);

    }

}
