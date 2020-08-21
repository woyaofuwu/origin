package com.asiainfo.veris.crm.order.soa.person.busi.BandFeedBack;

import java.util.Arrays;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class BandFeedBackBean extends CSBizBean
{
    public IDataset queryOrderInfos(IData input, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("ORDER_ID", input.getString("ORDER_ID"));
        param.put("SUB_ORDER_ID", input.getString("SUB_ORDER_ID"));
        IDataset dataset = Dao.qryByCodeParser("TF_B_CTRM_GERLSUBORDER", "SEL_ORDER_INFO_BY_ORDER_ID", param, pagination);
        return dataset;       
    }
    
    public IDataset queryReturnInfos(IData input, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("ORDER_ID", input.getString("ORDER_ID"));
        param.put("SUB_ORDER_ID", input.getString("SUB_ORDER_ID"));
        param.put("RETURN_ID", input.getString("RETURN_ID"));
        IDataset dataset = Dao.qryByCodeParser("TF_B_CTRM_RETURN", "SEL_RETURN_INFO_BY_ORDER_ID", param, pagination);
        return dataset; 
    }
    
    public IDataset updateStatus(IData input) throws Exception {
        String orderType = input.getString("ORDER_TYPE");
        IDataset result = new DatasetList();
        IData data = new DataMap();
        if("0".equals(orderType)){
            String orderId = input.getString("ORDER_ID");
            String subOrderId = input.getString("SUBORDER_ID");
            String orderState = input.getString("ORDER_STATE");
            String [] orderIdArray = orderId.split(",");
            String [] subOrderIdArray = subOrderId.split(",");
            String [] orderStateArray = orderState.split(",");
            //备注
            String rsrvStr3 = input.getString("RSRV_STR3");
            String [] rsrvStr3Array = rsrvStr3.split(",");
            
            for (int i = 0; i < orderIdArray.length; i++)
            {
                IData param = new DataMap();
                param.put("ORDER_ID", orderIdArray[i]);
                param.put("SUBORDER_ID", subOrderIdArray[i]);
                param.put("STATE", orderStateArray[i]);
                
                //备注
                param.put("RSRV_STR3", rsrvStr3Array[i]);
                
                int res = updateOrderStatus(param);
                if(res > 0){
                    data.put("RESULT_CODE", "0000");
                    result.add(data);
                } else {
                    data.put("RESULT_CODE", "9999");
                    result.add(data); 
                }
            }
        } else if("1".equals(orderType)){
            String orderId = input.getString("ORDER_ID");
            String subOrderId = input.getString("SUBORDER_ID");
            String returnId = input.getString("RETURN_ID");
            String orderState = input.getString("ORDER_STATE");
            String [] orderIdArray = orderId.split(",");
            String [] subOrderIdArray = subOrderId.split(",");
            String [] returnIdArray = returnId.split(",");
            String [] orderStateArray = orderState.split(",");
            for (int i = 0; i < orderIdArray.length; i++)
            {
                IData param = new DataMap();
                param.put("ORDER_ID", orderIdArray[i]);
                param.put("SUB_ORDER_ID", subOrderIdArray[i]);
                param.put("RETURN_ID", returnIdArray[i]);
                param.put("STATUS", orderStateArray[i]);
                int res = updateReturndStatus(param);
                if(res > 0){
                    data.put("RESULT_CODE", "0000");
                    result.add(data);
                } else {
                    data.put("RESULT_CODE", "9999");
                    result.add(data); 
                }
            }
        }
        
        return result; 
    }

    public static int updateOrderStatus(IData param)throws Exception
    {
        SQLParser sqlParser = new SQLParser(param);
        sqlParser.addSQL(" update TF_B_CTRM_GERLSUBORDER set RSRV_STR1='' , STATE = :STATE,RSRV_STR3=:RSRV_STR3 ");
        sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID AND SUBORDER_ID = :SUBORDER_ID ");
        return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
    }
    
    public static int updateReturndStatus(IData param)throws Exception
    {
        SQLParser sqlParser = new SQLParser(param);
        sqlParser.addSQL(" update TF_B_CTRM_RETURN  set  RSRV_STR1='' , IS_SYNC='0' , STATUS = :STATUS ");
        sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID AND SUB_ORDER_ID = :SUB_ORDER_ID AND RETURN_ID = :RETURN_ID ");
        return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
    }
}
