package com.asiainfo.veris.crm.order.soa.group.dataline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.common.query.BookTradeSVC;

public class CancelOrderUtil extends CSBizService
{
    public static IDataset cancelOrderInfoByOrderId(IData param) throws Exception{
        IDataset result = new DatasetList();
        boolean order = false;
        boolean trade = false ;
        IData data = new DataMap();
        data.put("ORDER_ID", param.getString("ORDER_ID"));
        data.put("CANCEL_STAFF_ID", param.getString("ORDER_ID"));
        data.put("CANCEL_DEPART_ID", param.getString("CANCEL_DEPART_ID"));
        
        IDataset orderInfo  = BookTradeSVC.queryOrderInfoByOrderId(data);
        IDataset orderList = new DatasetList();
        IData order1 = new DataMap();
        IData order3 = new DataMap();

        if(null != orderInfo && orderInfo.size() >0){
            order1 = orderInfo.getData(0);
            
            order1.put("CANCEL_TAG", "1");
            order1.put("CANCEL_STAFF_ID", param.getString("CANCEL_STAFF_ID"));
            order1.put("CANCEL_DEPART_ID", param.getString("CANCEL_DEPART_ID"));
            order1.put("UPDATE_STAFF_ID", param.getString("CANCEL_STAFF_ID"));
            order1.put("UPDATE_DEPART_ID", param.getString("CANCEL_DEPART_ID")); 
            order1.put("CANCEL_DATE", SysDateMgr.getSysDate());
            
            order3 = (IData) Clone.deepClone(order1);
            order3.put("CANCEL_TAG", "3");
            order3.put("CANCEL_STAFF_ID", param.getString("CANCEL_STAFF_ID"));
            order3.put("CANCEL_DEPART_ID", param.getString("CANCEL_DEPART_ID"));
            order3.put("UPDATE_STAFF_ID", param.getString("CANCEL_STAFF_ID"));
            order3.put("UPDATE_DEPART_ID", param.getString("CANCEL_DEPART_ID")); 
            order3.put("CANCEL_DATE", SysDateMgr.getSysDate());
        }
        
        orderList.add(order1);
        orderList.add(order3);
        
        int o[] = BookTradeSVC.insertOrderHInfo(orderList);
        if(o.length > 0){
            order = BookTradeSVC.deleteOrderInfoByOrderId(data);
        }
        
        
        IDataset tradeInfo  = BookTradeSVC.queryTradeInfoByOrderId(data);
        IDataset tradeList = new DatasetList();
        IData trade1 = new DataMap();
        IData trade3 = new DataMap();
        
        if(null != tradeInfo && tradeInfo.size() >0){
            
            for (int i = 0; i < tradeInfo.size(); i++)
            {
                trade1 = tradeInfo.getData(i);
                
                trade1.put("CANCEL_TAG", "1");
                trade1.put("CANCEL_STAFF_ID", param.getString("CANCEL_STAFF_ID"));
                trade1.put("CANCEL_DEPART_ID", param.getString("CANCEL_DEPART_ID"));
                trade1.put("UPDATE_STAFF_ID", param.getString("CANCEL_STAFF_ID"));
                trade1.put("UPDATE_DEPART_ID", param.getString("CANCEL_DEPART_ID")); 
                trade1.put("CANCEL_DATE", SysDateMgr.getSysDate());
                trade1.put("UPDATE_TIME", SysDateMgr.getSysDate());
                
                trade3 = (IData) Clone.deepClone(trade1);
                trade3.put("CANCEL_TAG", "3");
                trade3.put("CANCEL_STAFF_ID", param.getString("CANCEL_STAFF_ID"));
                trade3.put("CANCEL_DEPART_ID", param.getString("CANCEL_DEPART_ID"));
                trade3.put("UPDATE_STAFF_ID", param.getString("CANCEL_STAFF_ID"));
                trade3.put("UPDATE_DEPART_ID", param.getString("CANCEL_DEPART_ID")); 
                trade3.put("CANCEL_DATE", SysDateMgr.getSysDate());
                trade3.put("UPDATE_TIME", SysDateMgr.getSysDate());
                
                tradeList.add(trade1);
                tradeList.add(trade3);
            }
        }
        
        int t[] = BookTradeSVC.insertTradeHInfo(tradeList);
        if(t.length > 0){
            trade = BookTradeSVC.deleteTradeInfoByOrderId(data);
        }
        
        if(order && trade){
            IData res = new DataMap();
            res.put("ORDER_ID", param.getString("ORDER_ID"));
            res.put("RESULT", "0");
            result.add(res);
        }
        
        return result;
    }
}
