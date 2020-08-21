package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DatalineOrderSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    public IDataset queryCommonDataInfo(IData param)throws Exception{
        DatalineOrderDAO dao = new DatalineOrderDAO();
        return dao.queryCommonDataInfo(param);
    }
    
    public IDataset queryDatalineInfo(IData param)throws Exception{
        DatalineOrderDAO dao = new DatalineOrderDAO();
        return dao.queryDatalineInfo(param);
    }
    
    public IDataset queryDatalineOrder(IData param) throws Exception{
        DatalineOrderDAO dao = new DatalineOrderDAO();
        return dao.queryDatalineOrder(param);
    }
    
    public int updateDalineOrderState(IData param) throws Exception{
        DatalineOrderDAO dao = new DatalineOrderDAO();
        return dao.updateDalineOrderState(param);
    }
    
    public void createTradeExt(IData param) throws Exception
    {
        DatalineOrderDAO dao = new DatalineOrderDAO();
        dao.createTradeExt(param);
    }
    
    public IData queryDataline(IData param) throws Exception
    {
    	String userId = param.getString("USER_ID");
    	return DatalineOrderDAO.queryDataline(userId);
    }
}
