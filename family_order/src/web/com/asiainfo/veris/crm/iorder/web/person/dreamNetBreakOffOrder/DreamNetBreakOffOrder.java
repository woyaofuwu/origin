package com.asiainfo.veris.crm.iorder.web.person.dreamNetBreakOffOrder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DreamNetBreakOffOrder extends PersonBasePage{ 

	public abstract void setCondition(IData condition);
	public abstract void setInfos(IDataset infos);
	public abstract void setCount(long count);

    
    public void queryDreamNet(IRequestCycle cycle) throws Exception
	{
    	String alertInfo = "";
    
    	IData params = new DataMap();
        IDataOutput infos = CSViewCall.callPage(this, "SS.DreamNetBreakOffOrderSVC.dreamNetBreakOffOrder",params, getPagination("recordNav"));
        
        IDataset info = infos.getData();              
        if (IDataUtil.isEmpty(info))
        {
            alertInfo = "没有符合查询条件的数据!";
        }
		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		setCount(infos.getDataCount());
        setInfos(info);
        
	}
    
    public void batImportDreamNetList(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String params = data.getString("params");
        String[] strs = params.split(",");
        String fileId = strs[0];
        String status = strs[1];
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/DreamNetBreakOffOrder.xml"));
        int rightCount = fileData.getInt("rightCount", 0);
        int errorCount = fileData.getInt("errorCount", 0);
        fileData.put("status",status);

        if (IDataUtil.isEmpty(fileData)) {
            CSViewException.apperr(BatException.CRM_BAT_89);
        }
        if (rightCount + errorCount == 0) {
            CSViewException.apperr(BatException.CRM_BAT_86);
        }
        IDataset succSet = new DatasetList();
        IDataset faildSet = new DatasetList();

        if (rightCount > 0) {
            data.put("FILEDATA", fileData);
           
            IData result = CSViewCall.callone(this, "SS.DreamNetBreakOffOrderSVC.dreamNetImportOrder", data);
            IDataset succResult = result.getDataset("SUCCESS");
            IDataset faildResult = result.getDataset("FAILED");
            succSet.addAll(succResult);
            faildSet.addAll(faildResult);
        } else {
            CSViewException.apperr(BatException.CRM_BAT_6);
        }

        if (succSet.size() == 0) {
            CSViewException.apperr(BatException.CRM_BAT_6);
        }

        if (faildSet.size() > 0) {
            String fileName = "梦网包月类业务暂停新增订购导入失败列表.xls";// 导出后生成的文件
            String xmlPath = "export/bat/BATDREAMNETLISTFAILED.xml";// 导出时的模版
            IData param = new DataMap();
            param.put("posX", "0");
            param.put("posY", "0");
            param.put("ftpSite", "personserv");
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
            String errorFileId = ImpExpUtil.beginExport(null, param, fileName, new IDataset[]
                    {faildSet}, ExcelConfig.getSheets(xmlPath));
            String url = ImpExpUtil.getDownloadPath(errorFileId, fileName);
            String downLoadUrl = "<a href='" + url + "'>梦网包月类业务暂停新增订购批量导入失败列表.xls</a>";
            String hintInfo = "梦网包月类业务暂停新增订购批量导入情况：共导入" + (rightCount + errorCount) + "条，可以进行导入的记录" + succSet.size() + "条，失败" + faildSet.size() + "条，请击[" + downLoadUrl + "]下载导入失败文件!!!";
            IData iData = new DataMap();
            iData.put("RESULT_MSG", hintInfo);
            setAjax(iData);
        } else {
            IData iData = new DataMap();
            iData.put("RESULT_MSG", "梦网包月类业务暂停新增订购批量导入成功！");
            setAjax(iData);
        }
    }
    
    
}