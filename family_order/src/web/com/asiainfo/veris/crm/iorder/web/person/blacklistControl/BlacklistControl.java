package com.asiainfo.veris.crm.iorder.web.person.blacklistControl;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BlacklistControl extends PersonBasePage {
	
	public abstract void setInfos(IDataset infos);
	public abstract void setCode(IDataset code);
    public abstract void setInfo(IData info);
	public abstract void setCount(long count);
	
	public void qryUserData(IRequestCycle cycle)throws Exception {
		String alertInfo = "";
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
		inParam.put("START_DATE", param.getString("START_DATE"));
		inParam.put("END_DATE", param.getString("END_DATE"));
		
		IDataOutput output = CSViewCall.callPage(this, "SS.BlacklistControlQrySVC.getUserInfo", param,getPagination("recordNav"));
		IDataset info = output.getData();
		if (IDataUtil.isEmpty(info))
        {
            alertInfo = "没有符合查询条件的数据!";
        }
		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		this.setInfos(output.getData());
		setCount(output.getDataCount());
	}
	//删除
	public void delBlackUser(IRequestCycle cycle)throws Exception {
		IData param = getData();
		CSViewCall.call(this, "SS.BlacklistControlQrySVC.delBlackUser", param);
	}
	//导入
	public void importUserDataa(IRequestCycle cycle) throws Exception{
		IData param = getData();
		String fileId = param.getString("params");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        // 通过指定的配置文件，以及页面指定的excel导入数据文件取出数据集
        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/BlacklistControl.xml"));
        
        int rightCount = fileData.getInt("rightCount", 0);
        int errorCount = fileData.getInt("errorCount", 0);

        // 文件解析是否成功
        if (IDataUtil.isEmpty(fileData)) {
            CSViewException.apperr(BatException.CRM_BAT_89);
            return;
        }
        // 判断是否有数据
        if (rightCount + errorCount == 0) {
            CSViewException.apperr(BatException.CRM_BAT_86);
            return;
        }
        
        IDataset succSet = new DatasetList();
		IDataset faildSet = new DatasetList();
		
        if (rightCount > 0) {
        	param.put("FILEDATA", fileData);
			IData result = CSViewCall.callone(this,"SS.BlacklistControlQrySVC.batInsertUserData", param);
			IDataset succResult = result.getDataset("SUCCESS");
			IDataset faildResult = result.getDataset("FAILED");
			succSet.addAll(succResult);
			faildSet.addAll(faildResult);
        			
        }else{
			CSViewException.apperr(BatException.CRM_BAT_6);
		}
        		
        if (succSet.size() == 0) {
			CSViewException.apperr(BatException.CRM_BAT_6);
		}

		if (faildSet.size() > 0) {
			System.out.println("fr"+faildSet.size());
			String fileName = "VoLTE黑名单管控导入失败列表.xls";// 导出后生成的文件
			String xmlPath = "export/bat/BLACKLISTFAILED.xml";// 导出时的模版
			IData data = new DataMap();
			data.put("posX", "0");
			data.put("posY", "0");
			data.put("ftpSite", "personserv");
			ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
			String errorFileId = ImpExpUtil
					.beginExport(null, data, fileName,
							new IDataset[] { faildSet },
							ExcelConfig.getSheets(xmlPath));
			
			String url = ImpExpUtil.getDownloadPath(errorFileId, fileName);
			String downLoadUrl = "<a href='" + url
					+ "'>VoLTE黑名单管控导入失败列表.xls</a>";
			String hintInfo = "VoLTE黑名单管控导入情况：共导入"
					+ (rightCount + errorCount) + "条，可以进行导入的记录"
					+ succSet.size() + "条，失败" + faildSet.size() + "条，请击["
					+ downLoadUrl + "]下载导入失败文件!!!";
			IData iData = new DataMap();
			iData.put("RESULT_MSG", hintInfo);
			setAjax(iData);
		} else {
			IData iData = new DataMap();
			iData.put("RESULT_MSG", "VoLTE黑名单管控导入成功！");
			setAjax(iData);
		}
        
	}
	//新增
	public void insertUserData(IRequestCycle cycle)throws Exception {
		
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
		inParam.put("UPDATE_TIME", SysDateMgr.getSysDate());
		inParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		IData result = CSViewCall.callone(this,"SS.BlacklistControlQrySVC.insertUserData", inParam);
		setAjax(result);
	}
	public void onInitTrade(IRequestCycle cycle) throws Exception{
		
		IData paramsData=new DataMap();
        paramsData.put("SUBSYS_CODE", "CSM");
        paramsData.put("PARAM_ATTR", "3451");
        IDataset resultData=CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByAttr", paramsData);
        setCode(resultData);
	}
	
	
	
}
