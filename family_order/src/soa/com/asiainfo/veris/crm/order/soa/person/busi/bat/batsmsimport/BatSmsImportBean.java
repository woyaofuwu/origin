package com.asiainfo.veris.crm.order.soa.person.busi.bat.batsmsimport;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BatSmsImportBean extends CSBizBean{
	
	protected static Logger log = Logger.getLogger(BatSmsImportBean.class);
	
	public IDataset getWellNumList() throws Exception{
		
		StringBuilder strSql = new StringBuilder(); 
		strSql.append(" SELECT DISTINCT(WELL_NUM) AS WELL_NUM FROM ucr_uec.td_s_well_config a ORDER BY a.well_num DESC ");

		IDataset infos = Dao.qryBySql(strSql, null, Route.CONN_CRM_CEN);
		
		return infos;
		
	}
	
	public boolean checkIsRejectUser(IData param) throws Exception{
		
		
		SQLParser parser = new SQLParser(param);
		
		parser.addSQL(" SELECT count(1) AS CNT FROM  ucr_crm1.tf_f_user_svc a WHERE a.service_id=642 AND SYSDATE BETWEEN a.start_date AND a.end_date AND a.user_id IN  ");
		parser.addSQL(" (SELECT b.user_id FROM ucr_crm1.tf_f_user b WHERE 1=1 ");
		parser.addSQL(" AND b.remove_tag=0 ");
		parser.addSQL(" AND b.serial_number=:SERIAL_NUMBER ");
		parser.addSQL(" ) ");
		
		IDataset infos = Dao.qryByParse(parser,Route.CONN_CRM_CG);

		if( null != infos && infos.size()>0 ){
			IData data = infos.getData(0);
			if( null != data ){
				int cnt = data.getInt("CNT",0);
				if( cnt > 0 ){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int[] delTargetList(IDataset param) throws Exception{
		int ret[] = Dao.delete("TF_SM_TARGET_CUSTOMERS", param, 
				new String[]{"GROUP_NUM","SERIAL_NUMBER"}, "uec");
		
		return ret;
	}
	
	public int[] addTargetList(IDataset param) throws Exception{
		int ret[] = Dao.insert("TF_SM_TARGET_CUSTOMERS", param, "uec");
		return ret;
	}
	
	public int[] insertSmsManual(IDataset param) throws Exception{
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO UCR_UEC.TI_O_SMS_MANUAL(SERIAL_NUMBER,SEND_OBJECT,NOTICE_CONTENT,REMARK,DEAL_STATE,STAFF_ID,STAFF_NAME,SERVICE_AREA,PROJECT_NAME) ");
		sql.append(" VALUES(:SERIAL_NUMBER, :SEND_OBJECT, :NOTICE_CONTENT, :REMARK, :DEAL_STATE, :STAFF_ID, :STAFF_NAME, :SERVICE_AREA, :PROJECT_NAME ) ");
		
		int ret[] = Dao.executeBatch(sql, param, "uec");
		return ret;
	}

}
