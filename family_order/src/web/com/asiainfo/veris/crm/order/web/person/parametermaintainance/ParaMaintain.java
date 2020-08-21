package com.asiainfo.veris.crm.order.web.person.parametermaintainance;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ParaMaintain extends PersonBasePage {
	
	public abstract void setInfo(IData info);
	
	public abstract void setInfos(IDataset infos);
	
	public abstract void setThinfo(IData thinfo);
	
	public abstract void setThinfos(IDataset thinfos);
	
	public abstract void setTrinfo(IDataset trinfo);
	
	public abstract void setTrinfos(IDataset trinfos);
	
	public abstract void setTdinfo(IData tdinfo);
	
	public abstract void setCondition(IData condition);
	
	public abstract void setCount(long count);
	
	public abstract void setCount2(long count2);
	
	
	
	
	public void qryPrmRuleList(IRequestCycle cycle) throws Exception {
		IData data = getData("cond", true);
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ParaMaintainSVC.queryParaMaintain", data, getPagination("QryPrmRuleNav"));
        setCount(dataCount.getDataCount());
        setInfos(dataCount.getData());
    }
	
	public void qryPrmRecList(IRequestCycle cycle) throws Exception {
		IData data = getData("cond", true);
		data.put("TABLE_NAME", data.getString("TABLE_NAME2"));
		
		String rightCode = data.getString("RIGHT_CODE", "").trim();
		if (!"".equals(rightCode) && !StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), rightCode)) {
			throw new Exception("您无权限操作此规则!");    
		}
		
		//查询参数表所有列信息
        IDataset colRS = CSViewCall.call(this, "SS.ParaMaintainSVC.queryCol", data);
        
        //查询参数表中指定的列的数据集
        String tabName = data.getString("TABLE_NAME");
        String tabCols = "";
        String whereConds = "";
        List<String> columnHeads = new ArrayList<String>();        
        for (int i = 0; i < colRS.size(); i++) {
        	IData columnData = colRS.getData(i);
        	if ("3".equals(columnData.getString("COL_TYPE"))) {
        		tabCols += ",TO_CHAR(" + columnData.getString("COL_NAME") + ", 'YYYY-MM-DD HH24:MI:SS') AS " + columnData.getString("COL_NAME") + " "; 
        	} else {
        		tabCols += "," + columnData.getString("COL_NAME"); 
        	}        	  
        	columnHeads.add(columnData.getString("COL_NAME"));        	
        	if (columnData.getString("IS_FIXED", "").equals("1")) { //固定列作为查询条件
        		whereConds += " and " + columnData.getString("COL_NAME") + "='" + columnData.getString("COL_VALUE", "") + "' ";
        	}       	
        }
        
        if (tabCols.length() > 1) {
        	tabCols = tabCols.substring(1);        	
        	tabCols = " ROWID AS RECORDROWID, " + tabCols;
            
            IData params = new DataMap();
            params.put("TABLE_NAME", tabName);
            params.put("TABLE_COL", tabCols);
            params.put("TABLE_COND", whereConds);
            IDataOutput qryRecordsCount = CSViewCall.callPage(this, "SS.ParaMaintainSVC.queryPara", params, getPagination("QryPrmRecListNav"));
            IDataset qryRecords = qryRecordsCount.getData();
            long count2 = qryRecordsCount.getDataCount();
            
            IDataset rtnRecords = new DatasetList();
            for (int i = 0; i < qryRecords.size(); i++) {
            	IData record = qryRecords.getData(i);
            	//将record转化为dataset  
            	IDataset items = new DatasetList();
            	
            	IData _item = new DataMap();
            	_item.put("COLUMN_NAME", "RECORDROWID");
            	_item.put("COLUMN_VALUE", record.get("RECORDROWID"));
            	items.add(_item);
            	
            	for (int j = 0; j < columnHeads.size(); j++) {
            		String columnName = columnHeads.get(j);
            		IData item = new DataMap();
            		item.put("COLUMN_NAME", columnName);
            		item.put("COLUMN_VALUE", record.get(columnName));
            		item.putAll(colRS.getData(j));        		
            		items.add(item);
            	}
            	rtnRecords.add(items);
            }
            
            setThinfos(colRS);  
            setCount2(count2);
            setTrinfos(rtnRecords);
        }  else {
        	//CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数配置规则没有相关列配置!");
        	//CSAppException.appError("",  "参数配置规则没有相关列配置!");
        	throw new Exception("参数配置规则没有相关列配置!");        	
        }      
    }	
	
	public void deleteParaMaintain(IRequestCycle cycle) throws Exception {
		IData data = getData();
		String tablename = data.getString("TABLE_NAME");
		String delRowIds = data.getString("DEL_ROWIDS");
		
		IDataset rowIdSet = new DatasetList();
		for (String rowid : delRowIds.split(",")) {
			IData item = new DataMap();
			item.put("ROWID", rowid);
			rowIdSet.add(item);
		}
		
		IData params = new DataMap();		
		params.put("TABLE_NAME", tablename);
		params.put("ROWIDS", rowIdSet);
		CSViewCall.call(this, "SS.ParaMaintainSVC.deleteParaMaintain", params);		
	}	
	
	public void saveOrUpdateParaMaintain(IRequestCycle cycle) throws Exception {
		IData data = getData();
		String tablename = data.getString("TABLE_NAME");
		String rcdrowid = data.getString("col_RECORDROWID", "").trim();		
		IData colData = getData("col", true); 
		IData dataOld = getData("old", false); //备份记录
        
		IData params = new DataMap();
        params.put("TABLE_NAME", tablename);  
        IDataset datasetList = new DatasetList();
        datasetList.add(colData);
        params.put("DATA_SET_LIST", datasetList);
        
		if (rcdrowid == null || "".equals(rcdrowid)) {
			CSViewCall.call(this, "SS.ParaMaintainSVC.insertParaMaintain", params);
        } else {
        	params.putAll(dataOld);
        	params.putAll(colData);
			CSViewCall.call(this, "SS.ParaMaintainSVC.updateParaMaintain", params); 
        }
    }	
	
	
	/** 
	 * 动态生成excel模板文件，上传到FTP服务器上，然后下载FTP文件。
	 * (防止重启服务器模板文件丢失)
	 * (如果修改了规则，而模板文件未修改，上传数据可能会因必须字段值不存在报错或者丢失部分非必须字段数据<避免不了>)
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void exportExcel(IRequestCycle cycle) throws Exception {
		IData data = this.getData();		
		IData result = CSViewCall.callone(this, "SS.ParaMaintainSVC.exportExcel", data);
		setAjax("url", result.getString("URL"));
    }	
	
	/** 上传文件 */
	public void uploadData(IRequestCycle cycle) throws Exception {
		IData data = this.getData();		
		IDataset results = CSViewCall.call(this, "SS.ParaMaintainSVC.uploadParaMaintain", data);
        this.setAjax(results.getData(0));
	}

}
