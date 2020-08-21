
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

public class SchoolSaleManagerSVC extends CSBizService
{
    /**
     * 校园营销明细
     * 
     * @param userId
     * @return
     * @throws Exception
     * @author huangsl
     * @date 2014-9-09
     */
    public IDataset querySchoolSaleDetail(IData params) throws Exception
    {
    	return SaleActiveInfoQry.querySchoolSaleDetail(params.getString("SERIAL_NUMBER"), params.getString("PSPT_ID"),
        		params.getString("ORDER_TYPE"),params.getString("ORDER_ID"), params.getString("ORDER_STATUS"),params.getString("START_DATE"),
        		params.getString("END_DATE"),this.getPagination());
    }
    public IDataset delSaleDetail(IData param) throws Exception
    {
    	boolean flag = true;
    	flag = Dao.delete("TI_B_COLLEGES_SALE", param, new String[] {"ORDER_ID"},Route.CONN_CRM_CEN);
    	IDataset result = new DatasetList();
    	IData data = new DataMap();
    	data.put("FLAG", flag);
    	result.add(data);
    	return result;
    }
    public  IDataset AddSaleDetail(IData param) throws Exception
    {
    	boolean flag = true;
    	param.put("ORDER_ID", SeqMgr.getSaleOrderId());
		param.put("ACCEPT_DATE", SysDateMgr.getSysDate());
		param.put("UPDATE_TIME", SysDateMgr.getSysDate());
		param.put("CHECK_STAFF_ID", getVisit().getStaffId());
		param.put("CHECK_DEPART_ID", getVisit().getDepartId());
		param.put("TRADE_STAFF_NAME", getVisit().getStaffName());
		param.put("TRADE_STAFF_ID", getVisit().getStaffId());
		param.put("INPUT_STAFF_ID", getVisit().getStaffId());
		param.put("INPUT_DEPART_ID",  getVisit().getDepartId());
    	flag = Dao.insert("TI_B_COLLEGES_SALE", param, Route.CONN_CRM_CEN);
    	IDataset result = new DatasetList();
    	IData data = new DataMap();
    	data.put("FLAG", flag);
    	result.add(data);
    	return result;
    }
    public IDataset editSchoolSale(IData param) throws Exception
    {
    	boolean flag = true;
    	if("2".equals(param.getString("ORDER_STATUS", "9"))){
    		if(param.containsKey("RSRV_STR2")){
    			param.remove("RSRV_STR2");
    		}
    		if(param.containsKey("RSRV_STR3")){
    			param.remove("RSRV_STR3");
    		}
		}
    	param.put("UPDATE_TIME", SysDateMgr.getSysDate());
    	param.put("RSRV_STR1", SysDateMgr.getSysTime());
		param.put("CHECK_STAFF_ID", getVisit().getStaffId());
		param.put("CHECK_DEPART_ID", getVisit().getDepartId());
    	flag = Dao.save("TI_B_COLLEGES_SALE", param, new String[] {"ORDER_ID"},Route.CONN_CRM_CEN);
    	IDataset result = new DatasetList();
    	IData data = new DataMap();
    	data.put("FLAG", flag);
    	result.add(data);
    	return result;
    }
    
    public IDataset insertFile(IData param) throws Exception
    {
    	String fileId = SeqMgr.getFileId();
    	param.put("CREA_STAFF", getVisit().getStaffName());
		param.put("CREA_TIME", SysDateMgr.getSysTime());
		param.put("FILE_TYPE", "1");
		param.put("FILE_KIND", "1");
    	param.put("FILE_ID", fileId);
    	boolean flag = Dao.insert("WD_F_FTPFILE", param,Route.CONN_CRM_CEN);
    	IDataset result = new DatasetList();
    	IData data = new DataMap();
    	data.put("FLAG", flag);
    	data.put("FILE_ID", fileId);
    	result.add(data);
    	return result;
    }
}
