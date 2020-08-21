
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqBookingId;

public class BadWebInfoQry extends CSBizBean
{
	
   static Logger logger=Logger.getLogger(BadWebInfoQry.class);

    /**
     * @param data
     *           
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryBadWebInfo(IData data, Pagination pagination) throws Exception
    {
        IDataset result = null;
        StringBuilder sql = new StringBuilder(" SELECT id, ");
        sql.append("      staff_id, ");
        sql.append("      staff_name, ");
        sql.append("      update_time, ");
        sql.append("      modify_type, ");
        sql.append("      modify_tag, ");
        sql.append("      web_addr, ");
        sql.append("      web_name, ");
        sql.append("      web_source, ");
        sql.append("      remarks   ");
        sql.append("      from UCR_UEC.TD_S_BAD_WEB_INFO t  ");
        
        
        /**
         * 修改理由：
         *   简单问题复杂化了
         */
/*        if(!"".equals(data.getString("STAFF_ID", "")))
        {
        	sql.append("where  staff_id = :STAFF_ID ");
        	if(!"".equals(data.getString("STAFF_NAME", "")))
        	{
        		sql.append("      and staff_name = :STAFF_NAME ");
        	}
        	
        	if(!"".equals(data.getString("WEB_ADDR", "")))
        	{
        		sql.append("      and web_addr like '%'||:WEB_ADDR||'%' ");
        	}
        	if(!"".equals(data.getString("MODIFY_TAG", "")))
        	{
        		sql.append("    and modify_tag = : MODIFY_TAG ");
        	}
        	
        }else
        {
        	if(!"".equals(data.getString("STAFF_NAME", "")))
        	{
        		 sql.append("where  staff_name = :STAFF_NAME ");
        		 if(!"".equals(data.getString("WEB_ADDR", "")))
             	{
             		sql.append("      and web_addr like '%'||:WEB_ADDR||'%' ");
             	}
        	}else
        	{
        		if(!"".equals(data.getString("WEB_ADDR", "")))
        		{
        			sql.append("where  web_addr like '%'||:WEB_ADDR||'%' ");
        		}
        	}
        }*/
       
            sql.append(" where 1=1 ");
        
	        if(!"".equals(data.getString("STAFF_ID", "")))
	        {
	        	sql.append(" and  staff_id = :STAFF_ID ");
	        }
        	if(!"".equals(data.getString("STAFF_NAME", "")))
        	{
        		sql.append(" and staff_name = :STAFF_NAME ");
        	}
        	
        	if(!"".equals(data.getString("WEB_ADDR", "")))
        	{
        		sql.append(" and web_addr like '%'||:WEB_ADDR||'%' ");
        	}
        	/**
        	 * 20160608 
        	 * 添加网站来源查询
        	 */
        	if(!"".equals(data.getString("WEB_SOURCE", "")))
        	{
        		sql.append(" and WEB_SOURCE = :WEB_SOURCE ");
        	}
        	/**
			 * 20160608
        	 * 添加操作类型
        	 */
        	if(!"".equals(data.getString("MODIFY_TAG", "")))
        	{
        		sql.append(" and modify_tag = :MODIFY_TAG ");
        	}
        	
          sql.append(" order by  t.update_time  desc");

        result = Dao.qryBySql(sql, data, pagination, Route.CONN_UOP_UEC);

        return result;
    }
    /**
     * 通过id查询badwebinfo信息
     * 主键：STAFF_ID(操作人), WEB_ADDR(网站地址), WEB_NAME(网站名称)
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset  queryBadWebInfoById(IData data)throws Exception{
    	try {
        	StringBuilder sql=new StringBuilder();
        	
        	sql.append(" select * from  UCR_UEC.TD_S_BAD_WEB_INFO t");
        	sql.append(" where 1=1  ");
        	sql.append("  and t.staff_id = '"+data.getString("STAFF_ID").trim()+"'"); 
        	sql.append(" and t.web_addr = '"+data.getString("WEB_ADDR_old").trim()+"'");
        	sql.append(" and t.web_name = '"+data.getString("WEB_NAME_old").trim()+"'");
        	return Dao.qryBySql(sql, data, Route.CONN_UOP_UEC);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}

    }
    
    /**
     * 批量导入10086端口识别诈骗维护信息
     * @param input
     * @throws Exception
     */
    public static  void importBadwebinfo(IData input) throws Exception{
    	
    	IDataset set = new DatasetList(); // 上传excel文件内容明细
        IDataset results = new DatasetList();
        String fileId = input.getString("cond_STICK_LIST"); // 上传10086端口excelL文件的编号
        String[] fileIds = fileId.split(",");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        for (String strfileId : fileIds)
        {
            IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/badwebinfo/BadWebInfoDealImport.xml"));
            IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
            set.addAll(suc[0]);
        }
        
        if(IDataUtil.isNotEmpty(set)){
            for (int i = 0; i < set.size(); i++)
            {
                IData result = new DataMap();
                result.clear();
                IData  b=new DataMap();
                	   b.clear();
                	   b=set.getData(i);
                
                if("".equals(b.getString("WEB_ADDR"))||b.getString("WEB_ADDR")==null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                }
                if("".equals(b.getString("WEB_NAME"))||b.getString("WEB_NAME")==null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                }
                if("".equals(b.getString("WEB_SOURCE"))||b.getString("WEB_SOURCE")==null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                }
                if("".equals(b.getString("REMARKS"))||b.getString("REMARKS")==null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                }
                	   
                result.put("WEB_ADDR", set.getData(i).getString("WEB_ADDR"));//网站地址
                result.put("WEB_NAME", set.getData(i).getString("WEB_NAME"));//网站中文名称
                result.put("WEB_SOURCE", set.getData(i).getString("WEB_SOURCE"));//网站来源
                result.put("REMARKS", set.getData(i).getString("REMARKS"));//备注信息
                

                result.put("MODIFY_TAG", "0");
                result.put("MODIFY_TYPE", "新增");
                result.put("UPDATE_TIME", SysDateMgr.getSysTime());
                
                result.put("STAFF_ID", getVisit().getStaffId());
                result.put("STAFF_NAME", getVisit().getStaffName());
                
            	String seqId=Dao.getSequence(SeqBookingId.class);
            	result.put("ID", seqId);
                
                results.add(result);
            }
          
            try {
             	Dao.insert("TD_S_BAD_WEB_INFO", results,Route.CONN_UOP_UEC);
			} catch (Exception e) {
				// TODO: handle exception
				CSAppException.apperr(CrmCommException.CRM_COMM_1167);
				throw e;
			}
            
        }else{
        	//模版为空提示错误
        	CSAppException.apperr(CrmCommException.CRM_COMM_1166);
        }
    }

    /**
     * 
     * 根据查询条件，批量修改TD_S_BAD_WEB_INFO
     * @param data
     * @throws Exception
     */
    public static void updateBadWebInfoByCond(IData data) throws Exception{
    	
    	try {
    		
            StringBuilder sql = new StringBuilder();
            sql.append("update  TD_S_BAD_WEB_INFO t  ");
            
            sql.append(" set t.modify_tag=1,t.modify_type='删除' ");
           
            sql.append(" where 1=1 ");
            
    	        if(!"".equals(data.getString("cond_STAFF_ID", "")))
    	        {
    	        	sql.append(" and  staff_id = '" + data.getString("cond_STAFF_ID", "") + "'");
    	        }
            	if(!"".equals(data.getString("cond_STAFF_NAME", "")))
            	{
            		sql.append(" and staff_name = '"+ data.getString("cond_STAFF_NAME", "") +"'");
            	}
            	if(!"".equals(data.getString("cond_WEB_ADDR", "")))
            	{
            		sql.append(" and web_addr like '%"+ data.getString("cond_WEB_ADDR", "") +"%'");
            	}
            	/**
            	 * 20160608 
            	 * 添加网站来源查询
            	 */
            	if(!"".equals(data.getString("cond_WEB_SOURCE", "")))
            	{
            		sql.append(" and WEB_SOURCE = '" + data.getString("cond_WEB_SOURCE", "")+"'");
            	}
            	/**
    			 * 20160608
            	 * 添加操作类型
            	 */
            	if(!"".equals(data.getString("cond_MODIFY_TAG", "")))
            	{
            		sql.append(" and modify_tag = " +data.getString("cond_MODIFY_TAG", ""));
            	}
              
             Dao.executeUpdate(sql, data,Route.CONN_UOP_UEC);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
    }
    
}
