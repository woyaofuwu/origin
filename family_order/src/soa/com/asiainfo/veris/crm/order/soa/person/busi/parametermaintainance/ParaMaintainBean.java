
package com.asiainfo.veris.crm.order.soa.person.busi.parametermaintainance;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import com.ailk.biz.BizConstants;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.util.DaoHelper;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ParaMaintainBean extends CSBizBean
{
	
	public IDataset queryParaMaintain(IData params, Pagination page) throws Exception
    {
		params.put("RULE_NAME", params.getString("RULE_NAME"));
		params.put("TABLE_NAME", params.getString("TABLE_NAME"));
		return Dao.qryByCode("TD_B_PARAM_RULE", "SEL_TD_B_PARAM_RULE", params, page, Route.CONN_CRM_CEN);
    }
	
	public IDataset queryCol(IData params) throws Exception
    {
		params.put("RULE_ID", params.getString("RULE_ID"));
		params.put("TABLE_NAME", params.getString("TABLE_NAME"));
		return Dao.qryByCode("TD_B_PARAM_DEF", "SEL_COL", params, Route.CONN_CRM_CEN);
    }
	
	public IDataset queryPara(IData params, Pagination page) throws Exception
    {
		String sql = " select " + params.getString("TABLE_COL", "1") + " from " + params.getString("TABLE_NAME", "dual") + " where 1=1 " + params.getString("TABLE_COND", "");
		return Dao.qryBySql(new StringBuilder(sql), null,page,Route.CONN_CRM_CEN);
    }
	
	public boolean insertParaMaintain(IData params) throws Exception
    {
		IDataset dataset = params.getDataset("DATA_SET_LIST");
		if (dataset != null && dataset.size() > 0) {
			for (int i = 0; i < dataset.size(); i++) {
				IData item = dataset.getData(i);
				initStaff(item);
			}
		}
		
		Dao.insert(params.getString("TABLE_NAME"), dataset, Route.CONN_CRM_CEN);		
		return insertParaMaintainLog(params, 0);	
		
		
		/*initStaff(params);
		
		if( Dao.insert(params.getString("TABLE_NAME"), params, Route.CONN_CRM_CEN)){
			return insertParaMaintainLog(params, 0);			
		}else {
			return false;
		}*/
    }
	
	
	public boolean updateParaMaintain(IData params) throws Exception
    {
		String[] keys = {"ROWID"};
		String[] values = {params.getString("RECORDROWID")};
		initStaff(params);
		
		if( Dao.update(params.getString("TABLE_NAME"), params, keys, values, Route.CONN_CRM_CEN)) {
			return insertParaMaintainLog(params, 1);
		}else{
			return false;
		}
    }

	public boolean deleteParaMaintain(IData params) throws Exception
    {
		String[] keys = {"ROWID"};
		IDataset rowids = params.getDataset("ROWIDS");
		
		String rowidsStr = "";
		for (int i = 0; i < rowids.size(); i++) {
			rowidsStr += ",'" + rowids.getData(i).getString("ROWID") + "'";
		}
		if (rowidsStr.length() > 1) rowidsStr = rowidsStr.substring(1);
		
		IDataset rs = Dao.qryBySql(new StringBuilder("select t.rowid, t.* from " + params.getString("TABLE_NAME") + " t where 1=1 and ROWID in ( " + rowidsStr + " )"), null, Route.CONN_CRM_CEN);
		params.put("DELETE_DATAS", rs.toString());
		initStaff(params);		
		Dao.delete(params.getString("TABLE_NAME"), rowids, keys, Route.CONN_CRM_CEN );
		insertParaMaintainLog(params, 2);
		return true;
    }
	
	/**
	 * 
	 * @param params
	 * @param operType 0:插入; 1:更新; 2:删除
	 * @return
	 * @throws Exception
	 */
	public boolean insertParaMaintainLog(IData params, int operType) throws Exception {
		IData paramsLog = new DataMap();
	
		paramsLog.putAll(params);
		synchronized(SysDateMgr.class) {
			paramsLog.put("LOG_ID", SysDateMgr.currentTimeMillis());
		}
		
		paramsLog.put("OPER_TIME", SysDateMgr.getSysDate());
		paramsLog.put("TAB_CODE", params.getString("TABLE_NAME"));
		paramsLog.put("OPER_TYPE", operType);
		paramsLog.put("OPER_DATA", params.toString());
		initStaff(paramsLog);
		return Dao.insert("TL_B_CONFIG_LOG", paramsLog, Route.CONN_CRM_CEN);
	}
	
	@SuppressWarnings("static-access")
	private void initStaff(IData params) throws Exception {
		params.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
		params.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
		params.put("UPDATE_TIME", SysDateMgr.getSysDate());
	}
	
	public Map<String, IColumnObject> getTableColumns(IData params) throws Exception {
		return DaoHelper.getColumnsByData(new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false), params.getString("TABLE_NAME"));
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public IData uploadParaMaintain(IData input, List sheets) throws Exception 
	{
		IData resultData = new DataMap();
        resultData.put("RESULT_CODE", 0);
        IDataset sucSet = new DatasetList();
        IDataset errSet = new DatasetList();
        
        String filePath = input.getString("FILE_PATH");
        String ruleId = input.getString("RULE_ID");
        //String ruleName = input.getString("RULE_NAME");
        String tableName = input.getString("TABLE_NAME");        
        
        int sucSize = 0;
        if (StringUtils.isBlank(filePath)) {
        	throw new Exception("请上传导入文件!"); 
        }
        
        if (StringUtils.isNotBlank(filePath))
        {
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

            //List s = ExcelConfig.getSheets(xmlPath);
            
            //解析导入数据
            IData array = ImpExpUtil.beginImport(null, filePath, sheets);
            IDataset[] suc = (IDataset[]) array.get("right"); // 解析成功的数据
            IDataset[] err = (IDataset[]) array.get("error"); // 解析失败的数据
            sucSet.addAll(suc[0]);
            if (IDataUtil.isNotEmpty(err[0]))
            {
                for (Object data : err[0])
                {
                    ((IData) data).put("IMPORT_ERROR", "解析错误数据");
                }
                errSet.addAll(err[0]);
            }
        }
        
        //校验导入数据固定列的值
        IData checkedData = checkImportInfos(sucSet, ruleId, tableName);
        IDataset checkedSucessDataset = checkedData.getDataset("success");
        IDataset checkedFailDataset = checkedData.getDataset("fail");
        if (IDataUtil.isNotEmpty(checkedFailDataset))
        {
        	errSet.addAll(checkedFailDataset);
        }
        
        if (checkedSucessDataset.size() > 1000)
        {
        	throw new Exception("最大只能导入1000条 记录！"); 
        }
        
        sucSize = checkedSucessDataset.size();
        // 保存导入数据
        IDataset saveErrs = saveImprotInfos(checkedSucessDataset, tableName); // 数据校验错误数据
        insertParaMaintainLog(input, 0);
        
        if (IDataUtil.isNotEmpty(saveErrs))
        {
            errSet.addAll(saveErrs);
            sucSize -= saveErrs.size();
        }
        
        if (IDataUtil.isNotEmpty(errSet))
        {
            String url = createImportErrDataFile(errSet, ruleId, null, sheets);

            resultData.put("RESULT_CODE", 1);
            resultData.put("DOWNLOAD_URL", url);

            resultData.put("SUC_SIZE", sucSize);
            resultData.put("ERR_SIZE", errSet.size());
        }
        return resultData;
	}
	
	private IData checkImportInfos(IDataset recordDatas, String ruleId, String tableName) throws Exception
    {
		IDataset faildDatas = new DatasetList();
		IDataset succDatas = new DatasetList();
		IData rsData = new DataMap();
		rsData.put("success", succDatas);
		rsData.put("fail", faildDatas);		
		
		IData params = new DataMap();
		params.put("RULE_ID", ruleId);
		params.put("TABLE_NAME", tableName);
		IDataset ruleCols = this.queryCol(params);		
		if (ruleCols == null || ruleCols.size() < 1) {
			throw new Exception("未找到规则配置定义！"); 
		}
		
		for (int i = 0; i < recordDatas.size(); i++) {
			IData recordData = recordDatas.getData(i);
			
			boolean checkSuccessedFlag = true;
			for (int j = 0; j < ruleCols.size(); j++) {
	        	IData ruleCol = ruleCols.getData(j);
	        	if (ruleCol.getString("IS_FIXED", "").equals("1")) { //固定列作为查询条件
	        		String colName = ruleCol.getString("COL_NAME", "");
	        		String colValue = ruleCol.getString("COL_VALUE", "");
	        		
	        		if (!colValue.equals(recordData.getString(colName)) ) {
	    				recordData.put("IMPORT_ERROR", "固定列" + colName + "值不对！");
	    				checkSuccessedFlag = false;
	                    break;
	    			}
	        	}       	
	        }
			
			if (!checkSuccessedFlag) {
				faildDatas.add(recordData);
			} else {
				succDatas.add(recordData);
			}
		}
		return rsData;
    }
	
	private IDataset saveImprotInfos(IDataset dataInfos, String tableName) throws Exception
    {
		IDataset faildDatas = new DatasetList();
        
        for (int i = 0; i < dataInfos.size(); i++)
        {
        	IData recordData = dataInfos.getData(i);
            IData flagData = new DataMap();
            
            // 效验
            checkImportDataInfo(recordData, flagData);
            
            if (!flagData.getBoolean("FLAG"))
            {
                recordData.put("IMPORT_ERROR", flagData.getString("IMPORT_ERROR"));
                faildDatas.add(recordData);
                continue;
            }
            
            initStaff(recordData);
            
            try {
            	if (!Dao.insert(tableName, recordData, Route.CONN_CRM_CEN))
                {
                    recordData.put("IMPORT_ERROR", "插入保存错误");
                    faildDatas.add(recordData);
                }
            } catch (Exception e) {
            	recordData.put("IMPORT_ERROR", "插入保存错误");
                faildDatas.add(recordData);
            }
        }
        return faildDatas;
    }
	
	/**
     * 校验导入数据格式
     * 
     * @param data
     * @param checkData
     * @throws Exception
     */
    private void checkImportDataInfo(IData data, IData checkData) throws Exception
    {
        checkData.put("FLAG", false);
        if (IDataUtil.isEmpty(data))
        {
            checkData.put("IMPORT_ERROR", "记录行数据为空!");
            return;
        }

        /*String cityCode = data.getString("CITY_CODE", "").trim();
        if (StringUtils.isBlank(cityCode))
        {
            checkData.put("IMPORT_ERROR", "分公司不能为空!");
            return;
        }*/
        
        checkData.put("FLAG", true);
    }
    
    /**
     * 生成导入错误数据文件
     * 
     * @param data
     * @param checkData
     * @throws Exception
     */
    private String createImportErrDataFile(IDataset errDataset, String ruleName, String xmlPath, List sheets) throws Exception
    {
        String fileSerializeId = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
        String fileName = ruleName + "导入错误数据(" + SysDateMgr.getSysDate() + ").xls";

        String cfgFile = xmlPath;
        // 生成失败的文件
        //File File = ImpExpUtil.writeDataToFile("xls", new IDataset[] { errDataset }, 
        //		"personserv", fileSerializeId, null, cfgFile);
        File File = ImpExpUtil.writeDataToFile("xls", new IDataset[] { errDataset }, "personserv", fileSerializeId, null, sheets, 0, 0, null);
        // 将导入失败的文件上传
        String errFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(File), "personserv", "upload/import", fileName);

        // ImpExpUtil.beginExport(fileSerializeId, null, fileName, new IDataset[]{errDataset}, cfgData);
        // 生成文件下载的URL
        return ImpExpUtil.getDownloadPath(errFileId, fileName);
    }
    
}
