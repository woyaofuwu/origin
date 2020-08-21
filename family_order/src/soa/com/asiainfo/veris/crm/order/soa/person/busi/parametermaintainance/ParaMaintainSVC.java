
package com.asiainfo.veris.crm.order.soa.person.busi.parametermaintainance;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ParaMaintainSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;

	public IDataset queryParaMaintain(IData params) throws Exception
    {
    	ParaMaintainBean bean = new ParaMaintainBean();
        IDataset res = bean.queryParaMaintain(params, getPagination());
//        //chenxy3 增加权限，对于没权限的数据去掉
         //取消改动
//        if(res!=null && res.size()>0){
//        	for(int i=0;i<res.size();i++){
//        		IData resInfo=res.getData(i);
//        		String right=resInfo.getString("RIGHT_CODE","");
//        		boolean ifRight = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), right);
//        		if(!ifRight){
//        			res.remove(i);
//        		}
//        	}
//        	
//        }
        
        return res;
    }
	
	public IDataset queryCol(IData param) throws Exception
    {
    	ParaMaintainBean bean = new ParaMaintainBean();
        IDataset res = bean.queryCol(param);
        return res;
    }
	
	public IDataset queryPara(IData params) throws Exception
    {
    	ParaMaintainBean bean = new ParaMaintainBean();
        IDataset res = bean.queryPara(params,getPagination());
        return res;
    }
	
	public boolean insertParaMaintain(IData params) throws Exception
    {
    	ParaMaintainBean bean = new ParaMaintainBean();
    	return  bean.insertParaMaintain(params);
    }

	public boolean updateParaMaintain(IData params) throws Exception
    {
    	ParaMaintainBean bean = new ParaMaintainBean();
    	return bean.updateParaMaintain(params);
    }
	
	public boolean deleteParaMaintain(IData params) throws Exception
    {
    	ParaMaintainBean bean = new ParaMaintainBean();
    	return bean.deleteParaMaintain(params);
    }
	
	@SuppressWarnings("rawtypes")
	public IData exportExcel(IData params) throws Exception
	{
		IData initXmlData = this.initXml(params, false);
		String fileName = initXmlData.getString("FILE_NAME");
		IDataset initValueSet = initXmlData.getDataset("OUTPUT_DATESET");
        
		//模板配置
		ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());        
        IData params1 = new DataMap();
        params1.put("posX", "0");
        params1.put("posY", "0");
        params1.put("ftpSite", "personserv");  
        
        // 将数据写入文件并返回文件ID
        String fileId = ImpExpUtil.beginExport(null, params1, fileName, new IDataset[]{initValueSet}, (List) initXmlData.get("SHEETS"));
        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(fileId, fileName);
        IData result = new DataMap();
        result.put("URL", url);
        return result;
	}
	
	@SuppressWarnings("rawtypes")
	public IDataset uploadParaMaintain(IData params) throws Exception
	{
		//生成模板文件
		IData initXmlData = this.initXml(params,true);
		//String fileName = initXmlData.getString("FILE_NAME");
		//IDataset initValueSet = initXmlData.getDataset("OUTPUT_DATESET");
		List sheets = (List) initXmlData.get("SHEETS");
		
		//上传文件
		ParaMaintainBean bean = new ParaMaintainBean();
		IDataset resultSet = new DatasetList();
		resultSet.add(bean.uploadParaMaintain(params, sheets));
        return resultSet;
	}
	
	/** 依据RULE_ID和TABLE_NAME初始化模板文件 */
	private IData initXml(IData data, boolean isImport) throws Exception {
		String ruleId = data.getString("RULE_ID");
		//String ruleName = data.getString("RULE_NAME");
		String tableName = data.getString("TABLE_NAME");
		
		//依据规则ID得到模板文件名(如果没有模板文件，则初始化一个模板文件。   针对规则的经常变更，需要实时初始化模板文件？)
		//依据规则NAME得到下载文件名
		String fileName = ruleId + ".xls";	
		
		//查询该参数规则的所有列信息
		IData qryParams = new DataMap();
		qryParams.put("RULE_ID", ruleId);
		qryParams.put("TABLE_NAME", tableName);        
		//IDataset colRS = CSAppCall.call("SS.ParaMaintainSVC.queryCol", qryParams);
		IDataset colRS = this.queryCol(qryParams);   //为什么不能直接调？？？
        
        //模板文件描述字段：COL_NAME、COL_DESC、COL_VALUE、IS_FIXED、COL_TYPE、COL_NULLABLE、ORDERS
        //以字段默认值作为下载文件第一行数据
        IDataset firstRecordSet = new DatasetList();
        IData firstRecordData = new DataMap();
        firstRecordSet.add(firstRecordData);
        for (int i = 0; i < colRS.size(); i++) {
        	IData columnData = colRS.getData(i);
        	firstRecordData.put(columnData.getString("COL_NAME"), columnData.getString("COL_VALUE"));
        	/*if ("0".equals(columnData.getString("IS_FIXED")) && "3".equals(columnData.getString("COL_TYPE"))) {
        		//给时间一个参考格式
        		firstRecordData.put(columnData.getString("COL_NAME"), "20101010");
        	}*/
        }        
        
        Document document = DocumentHelper.createDocument();
		Element workbook = document.addElement("workbook");
		Element sheet = workbook.addElement("sheet"); 
		sheet.addAttribute("name", "ParamMaintainceImport");
		sheet.addAttribute("desc", "ParamMaintainceImport");
		Element header = sheet.addElement("header");
		header.addAttribute("isshow", "true");
		header.addAttribute("height", "300");
		
		for (int i = 0; i < colRS.size(); i++) {
			IData cellProperty = colRS.getData(i);
			Element cell = header.addElement("cell");
			cell.addAttribute("name", cellProperty.getString("COL_NAME"));	
			cell.addAttribute("desc", cellProperty.getString("COL_DESC"));
			cell.addAttribute("defaultValue", cellProperty.getString("COL_VALUE"));
			cell.addAttribute("isFixed", cellProperty.getString("IS_FIXED"));
			cell.addAttribute("type", cellProperty.getString("COL_TYPE"));
			cell.addAttribute("orders", cellProperty.getString("ORDERS"));
			cell.addAttribute("nullable", "0".equals(cellProperty.getString("COL_NULLABLE")) ? "no" : "yes");
			cell.addAttribute("align", "1");
			cell.addAttribute("width", "5000");
			cell.addAttribute("maxsize", "200");
		}
		
		if (isImport) {
			Element cell = header.addElement("cell");
			cell.addAttribute("name", "IMPORT_ERROR");	
			cell.addAttribute("desc", "导入失败原因");
			cell.addAttribute("defaultValue", "");
			cell.addAttribute("isFixed", "");
			cell.addAttribute("type", "1");
			cell.addAttribute("orders", "");
			cell.addAttribute("nullable", "yes");
			cell.addAttribute("align", "1");
			cell.addAttribute("width", "5000");
			cell.addAttribute("maxsize", "200");
		}		
		
		IData rtnData = new DataMap();
		rtnData.put("FILE_NAME", fileName); //以ruleId命名的excel模板文件
		rtnData.put("SHEETS", document.getRootElement().elements()); //模板缓存
        rtnData.put("OUTPUT_DATESET", firstRecordSet); //rule规则下的固定列记录
        return rtnData;
    }
	
}
