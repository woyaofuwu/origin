
package com.asiainfo.veris.crm.order.soa.person.common.query.tdcustpsptinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TDCustPsptLimitInfoQry
{

    /**
     * @param data
     *            如果包含QryLimit：false值，则查询不加入部门权限。 否则默认加入 :DEPART_ID 部门参数
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryLimitInfo(IData data, Pagination pagination) throws Exception
    {
    	String pstTag= data.getString("PSPT_TAG");
    	if("1".equals(pstTag)){
    		//单位证件类型
    		return queryLimitInfoNoPersonPSPTType(data,pagination);
    	}else if("0".equals(pstTag)){
    		//个人证件
    		return queryLimitInfoPersonPSPTType(data,pagination);
    	}else {// 查询全部证件类型
    		IDataset result = new DatasetList();
    		String qryPsptCode="('D','E','G','L','M')";
    		data.put("QRY_PSPT_CODE", qryPsptCode);
    		IDataset result1 =queryLimitInfoNoPersonPSPTType(data,pagination);
    		qryPsptCode="('0','1','2','3','A','B','C','F','H','I','J','K','N','O','Z')";
    		data.put("QRY_PSPT_CODE", qryPsptCode);
    		IDataset result2 =queryLimitInfoPersonPSPTType(data,pagination);
    		if(result1!=null){
        		result.addAll(result1);
        	}
        	if(result2!=null){
        		result.addAll(result2);
        	}
        	return result;
    	}
    	
    }
    
    /**
    * method_name: 个人证件
    * param: 
    * return:
    * describe: TODO
    * creat_user: chenchunni
    * creat_date: 2020/3/16
    * creat_time: 19:58
    **/
    public static IDataset queryLimitInfoPersonPSPTType(IData data, Pagination pagination) throws Exception
    {
    	IDataset result = null;
		StringBuilder sql = new StringBuilder(" SELECT PSPT_TYPE_CODE, ");
		sql.append("      PSPT_ID, ");
		sql.append("      CUST_NAME,");
		sql.append("      (select count(distinct u.user_id) ");
		sql.append("         from tf_f_user u, tf_f_customer c ");
		sql.append("        Where u.cust_id = c.cust_id ");
		sql.append("          and u.remove_tag = '0' ");
		sql.append("          and  exists (select 1 from tf_F_user_product p     ");
		sql.append("          where p.user_id=u.user_id 						   ");
		sql.append("            and p.partition_id=u.partition_id  			   ");
		sql.append("            and p.main_tag=1               				   ");
		sql.append("            and p.brand_code in ('TDYD')     ");
		sql.append("            and sysdate between p.start_date and p.end_date) ");
		sql.append("          and u.city_code not in ('HNSJ', 'HNHN') ");
		sql.append("          and c.cust_name = L.CUST_NAME ");
		sql.append("          and c.pspt_id = L.PSPT_ID ");
		sql.append("          and c.remove_tag = '0' ");
		sql.append("          and c.is_real_name = '1' ");
		sql.append("          ) ALREADY_COUNT, ");
		sql.append("      LIMIT_COUNT ");
		sql.append(" FROM TF_F_FIXPHONE_PSPT_LIMT L ");
		sql.append(" WHERE 1 = 1 ");
		sql.append("  AND L.EPARCHY_CODE = :EPARCHY_CODE ");
		
		// 默认查询需要加入权限，除CustPsptLimitInfo以外的页面(如CustPsptLimitQuery)可以查出所有记录
		if (data.getBoolean("QryLimit"))
		{
			// 有特殊权限的工号可以查出所有记录, 没有特殊权限的只能查出本部门的记录
			sql.append("  AND L.UPDATE_DEPART_ID = :DEPART_ID ");
		}
		sql.append(" AND  (L.PSPT_ID = :PSPT_ID or :PSPT_ID is null) ");
		sql.append(" AND  (L.CUST_NAME = :CUST_NAME or :CUST_NAME is null) ");

		if(!"".equals(data.getString("QRY_PSPT_CODE"))&&data.getString("QRY_PSPT_CODE")!=null){
			sql.append(" AND L.PSPT_TYPE_CODE IN "+data.getString("QRY_PSPT_CODE"));
		}
		
		result = Dao.qryBySql(sql, data, pagination);
		
		return result;
    }

	/**
	 * method_name: 单位证件
	 * param:
	 * return:
	 * describe: TODO
	 * creat_user: chenchunni
	 * creat_date: 2020/3/16
	 * creat_time: 19:58
	 **/
    public static IDataset queryLimitInfoNoPersonPSPTType(IData data, Pagination pagination) throws Exception
    {
    	IDataset result = new DatasetList();
    	IDataset result0 = null;

    	StringBuilder sql1 = new StringBuilder(" SELECT PSPT_TYPE_CODE, ");
		sql1.append("      PSPT_ID, ");
		sql1.append("      CUST_NAME,");
		sql1.append("      (  ");
		sql1.append("		select count(distinct u.user_id)");
		sql1.append("   			from tf_f_user u, tf_f_customer c");
		sql1.append("  		Where u.cust_id = c.cust_id");
		sql1.append("  			and c.partition_id = mod(u.cust_id, 10000)");
		sql1.append("    		and u.remove_tag = '0'");
		sql1.append("    		and u.city_code not in ('HNSJ', 'HNHN')");
		sql1.append("    		and c.cust_name = L.CUST_NAME ");
		sql1.append("    		and c.pspt_id = L.PSPT_ID ");
		sql1.append("    		and c.remove_tag = '0'");
		sql1.append("    		and c.is_real_name = '1'");
		sql1.append("    		and exists");
		sql1.append("  				(SELECT 1");
		sql1.append("           			FROM tf_F_user_product p");
		sql1.append("          		where p.user_id = u.user_id");
		sql1.append("            		and p.partition_id = mod(u.user_id, 10000)");
		sql1.append("            		and p.main_tag = '1'");
		sql1.append("            		and p.brand_code in ('TDYD')");
		sql1.append("            		and sysdate between p.start_date and p.end_date)");
		sql1.append("   					and not exists");
		sql1.append("    			(SELECT 1");
		sql1.append("           			FROM tf_f_user_other a");
		sql1.append("          		where a.user_id = u.user_id");
		sql1.append("            		and a.partition_id = mod(u.user_id, 10000)");
		sql1.append("            		and a.rsrv_value_code = 'HYYYKBATCHOPEN'");
		sql1.append("            		and sysdate between a.start_date and a.end_date)   ");
		sql1.append("          			) ALREADY_COUNT, ");
		sql1.append("      LIMIT_COUNT ");
		sql1.append(" FROM TF_F_FIXPHONE_PSPT_LIMT L ");
		sql1.append(" WHERE 1 = 1 ");
		sql1.append("  AND L.EPARCHY_CODE = :EPARCHY_CODE ");

		// 默认查询需要加入权限，除CustPsptLimitInfo以外的页面(如CustPsptLimitQuery)可以查出所有记录
		if (data.getBoolean("QryLimit"))
		{
			// 有特殊权限的工号可以查出所有记录, 没有特殊权限的只能查出本部门的记录
			sql1.append("  AND L.UPDATE_DEPART_ID = :DEPART_ID ");
		}
		sql1.append(" AND  (L.PSPT_ID = :PSPT_ID or :PSPT_ID is null) ");
		sql1.append(" AND  (L.CUST_NAME = :CUST_NAME or :CUST_NAME is null) ");
		// 如果查询条件为空，则需要显示个人证件和单位的内容
		if(!"".equals(data.getString("QRY_PSPT_CODE"))&&data.getString("QRY_PSPT_CODE")!=null){
			sql1.append(" AND L.PSPT_TYPE_CODE IN "+data.getString("QRY_PSPT_CODE"));
		}
		
		result0 = Dao.qryBySql(sql1, data, pagination);

    	if(result0!=null){
    		result.addAll(result0);
    	}
    	return result;
    }
}
