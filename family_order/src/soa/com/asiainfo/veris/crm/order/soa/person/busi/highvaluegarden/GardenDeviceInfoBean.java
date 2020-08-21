package com.asiainfo.veris.crm.order.soa.person.busi.highvaluegarden;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.database.statement.Parameter;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class GardenDeviceInfoBean extends CSBizBean {

	/**
     * 分页查询
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGardenDeviceInfo(IData param, Pagination pagination) 
    	throws Exception
    {
    	SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT REL_ID,GARDEN_CODE,GARDEN_NAME,DEVICE_CODE,ACTIVITY_CODE, ");
        parser.addSQL("  TO_CHAR(CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss') CREATE_DATE,");
        parser.addSQL("  REMARK,REMOVE_TAG,CREATE_STAFF_ID,RSRV_STR1,RSRV_STR2");
        parser.addSQL(" FROM TF_F_GARDEN_DEVICE_INFO G");
        parser.addSQL("  	WHERE 1 = 1");
        parser.addSQL("  AND G.DEVICE_CODE = :DEVICE_CODE");
        parser.addSQL("  AND G.ACTIVITY_CODE = :ACTIVITY_CODE");
        parser.addSQL("  AND G.GARDEN_NAME like '%' || :GARDEN_NAME || '%'" );
        parser.addSQL("  AND G.REMOVE_TAG = :REMOVE_TAG");
        return Dao.qryByParse(parser, pagination);  
    }
    
	public IData gardenInfoInsert(IData input) throws Exception {
		IData reData = new DataMap();
		try {
			
			String filePath = input.getString("FILE_PATH");
			ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
			IData fileData = ImpExpUtil.beginImport(null, filePath, ExcelConfig.getSheets("import/bat/GARDENINFO.xml"));
			
			if (fileData.get("error") != null && ((IDataset[]) fileData.get("error")).length > 0
					&& ((IDataset[]) fileData.get("error"))[0] != null && ((IDataset[]) fileData.get("error"))[0].size() > 0) 
			{
				//setAjax("result", (((IDataset[]) fileData.get("error"))[0]).getData(0).getString("IMPORT_ERROR"));
				reData.put("result", (((IDataset[]) fileData.get("error"))[0]).getData(0).getString("IMPORT_ERROR"));
				return reData;
			}
			
			IDataset fileDataset = ((IDataset[]) fileData.get("right"))[0];
			if (fileDataset.size() > 10001) {
				reData.put("result","一次性导入不能大于10000条记录");
				return reData;
			}
			
			StringBuilder sql = new StringBuilder(
	                "insert into TF_F_GARDEN_DEVICE_INFO (REL_ID,GARDEN_CODE,GARDEN_NAME,DEVICE_CODE,ACTIVITY_CODE,ACTIVITY_NAME,CREATE_DATE,REMARK,REMOVE_TAG,CREATE_STAFF_ID,RSRV_STR1,RSRV_STR2,UPDATE_DEPART_ID)"
	                        + "select ?,?,?,?,?,?,SYSDATE,?,?,?,?,?,? from dual");
			
			Parameter[] param = new Parameter[fileDataset.size()];
	        for (int i = 0, size = fileDataset.size(); i < size; i++)
	        {
	            /** 构造绑定对象，按顺序绑定参数值 */
	            IData bat = fileDataset.getData(i);
	            param[i] = new Parameter();
	            String id = SeqMgr.getGardenActivityInfoSeq();
	            param[i].add(id);
	            param[i].add(bat.getString("GARDEN_CODE"));
	            param[i].add(bat.getString("GARDEN_NAME"));
	            param[i].add(bat.getString("DEVICE_CODE"));
	            param[i].add(bat.getString("ACTIVITY_CODE"));
	            param[i].add(bat.getString("ACTIVITY_NAME"));
	            param[i].add(bat.getString("REMARK"));
	            param[i].add("0");
	            param[i].add(input.getString("CREATE_STAFF_ID"));
	            param[i].add(bat.getString("RSRV_STR1"));
	            param[i].add(bat.getString("RSRV_STR2"));
	            param[i].add(input.getString("UPDATE_DEPART_ID"));
	        }
	        
			int[] result = Dao.executeBatch(sql, param, Route.CONN_CRM_CG);
			reData.put("result", "添加数据成功!");
		} catch (Exception e) {
			reData.put("result", "添加数据的时候出现问题:" + e.getMessage());
		}
		return reData;
	}
 
	public IData gardenInfoDelete(IData param, Pagination pagination) throws Exception
	{
		IData reData = new DataMap();
    	SQLParser parser = new SQLParser(param);
        parser.addSQL("update TF_F_GARDEN_DEVICE_INFO set REMOVE_TAG=:REMOVE_TAG,UPDATE_TIME=SYSDATE,UPDATE_STAFF_ID=:UPDATE_STAFF_ID,UPDATE_DEPART_ID=:UPDATE_DEPART_ID" +
        		"  where ACTIVITY_CODE = :ACTIVITY_CODE ");
        Dao.executeUpdate(parser);
        reData.put("result", "刪除数据成功!");
        return reData;  
    }    
	
	public IData deleteGardenInfos(IData input) throws Exception {
		IData reData = new DataMap();
		try {
			
			String filePath = input.getString("FILE_PATH");
			ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
			IData fileData = ImpExpUtil.beginImport(null, filePath, ExcelConfig.getSheets("import/bat/GARDENINFODELETE.xml"));
			
			if (fileData.get("error") != null && ((IDataset[]) fileData.get("error")).length > 0
					&& ((IDataset[]) fileData.get("error"))[0] != null && ((IDataset[]) fileData.get("error"))[0].size() > 0) 
			{
				//setAjax("result", (((IDataset[]) fileData.get("error"))[0]).getData(0).getString("IMPORT_ERROR"));
				reData.put("result", (((IDataset[]) fileData.get("error"))[0]).getData(0).getString("IMPORT_ERROR"));
				return reData;
			}
			
			IDataset fileDataset = ((IDataset[]) fileData.get("right"))[0];
			if (fileDataset.size() > 10001) {
				reData.put("result","一次性导入不能大于10000条记录");
				return reData;
			}
			
			//IDataset gardenDatas = input.getDataset("GARDEN_DATA");
			StringBuilder sql = new StringBuilder(
	                "update TF_F_GARDEN_DEVICE_INFO set REMOVE_TAG=(select ? from dual),UPDATE_TIME=SYSDATE,UPDATE_STAFF_ID=(select ? from dual),UPDATE_DEPART_ID=(select ? from dual)" +
	                "  where ACTIVITY_CODE = (select ? from dual) and DEVICE_CODE = (select ? from dual)");
			
			Parameter[] param = new Parameter[fileDataset.size()];
	        for (int i = 0, size = fileDataset.size(); i < size; i++)
	        {
	            /** 构造绑定对象，按顺序绑定参数值 */
	            IData bat = fileDataset.getData(i);
	            param[i] = new Parameter();
	            param[i].add("1");
	            param[i].add(input.getString("UPDATE_STAFF_ID"));
	            param[i].add(input.getString("UPDATE_DEPART_ID"));
	            param[i].add(bat.getString("ACTIVITY_CODE"));
	            //param[i].add(bat.getString("GARDEN_CODE"));
	            param[i].add(bat.getString("DEVICE_CODE"));
	        }
	        
			int[] result = Dao.executeBatch(sql, param, Route.CONN_CRM_CG);
			reData.put("result", "刪除数据成功!");
		} catch (Exception e) {
			reData.put("result", "刪除数据的时候出现问题:" + e.getMessage());
		}
		return reData;
	}
}