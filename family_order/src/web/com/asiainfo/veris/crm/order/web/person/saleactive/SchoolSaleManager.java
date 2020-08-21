
package com.asiainfo.veris.crm.order.web.person.saleactive;

import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.FtpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SchoolSaleManager extends PersonBasePage
{
	public void querySchoolSaleDetail(IRequestCycle cycle)throws Exception{
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataOutput saleActives = CSViewCall.callPage(this, "SS.SchoolSaleManagerSVC.querySchoolSaleDetail", data,getPagination("pageinfo"));	
		 if (IDataUtil.isEmpty(saleActives.getData()))
        {
            this.setAjax("ALERT_INFO",  "未获取到相关资料！");
        }
		setInfos(saleActives.getData());
		setCount(saleActives.getDataCount());
	}
	public void deleteSchoolSale(IRequestCycle cycle)throws Exception{
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataset delSaleActives = CSViewCall.call(this, "SS.SchoolSaleManagerSVC.delSaleDetail", data);	
		if (IDataUtil.isNotEmpty(delSaleActives))
	    {
			if(delSaleActives.getData(0).getBoolean("FLAG")){
				this.setAjax("ALERT_INFO", "删除成功！");
			}else{
				this.setAjax("ALERT_INFO", "删除失败！");
			}
	    }
	}
	public void addSchoolSale(IRequestCycle cycle)throws Exception{
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataset addSaleActives = CSViewCall.call(this, "SS.SchoolSaleManagerSVC.AddSaleDetail", data);	
		if (IDataUtil.isNotEmpty(addSaleActives))
	    {
			if(addSaleActives.getData(0).getBoolean("FLAG")){
				this.setAjax("ALERT_INFO", "添加成功！");
			}else{
				this.setAjax("ALERT_INFO", "添加失败！");
			}
	    }
	}
	public void editSchoolSale(IRequestCycle cycle)throws Exception{
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataset addSaleActives = CSViewCall.call(this, "SS.SchoolSaleManagerSVC.editSchoolSale", data);	
		if (IDataUtil.isNotEmpty(addSaleActives))
	    {
			if(addSaleActives.getData(0).getBoolean("FLAG")){
				this.setAjax("ALERT_INFO", "保存成功！");
			}else{
				this.setAjax("ALERT_INFO", "保存失败！");
			}
	    }
	}
	public void checkFileExsist(IRequestCycle cycle) throws Exception {
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataset results = CSViewCall.call(this, "SS.SchoolSaleManagerSVC.querySchoolSaleDetail", data);	
		
		IData result = new DataMap();
		IDataset infos  = new DatasetList();
		String orderId="", face_photo="",  back_photo="", group_photo="",  order_status="", accept_date="", order_type="",dir=""; 
		if(IDataUtil.isNotEmpty(results)){
			IData temp = results.getData(0);
			orderId = temp.getString("ORDER_ID","").trim();
			group_photo = temp.getString("GROUP_PHOTO","").trim();
			face_photo = temp.getString("FACE_PHOTO","").trim();
			back_photo = temp.getString("BACK_PHOTO","").trim();
			order_status = temp.getString("ORDER_STATUS","").trim();
			accept_date = temp.getString("ACCEPT_DATE","").trim();
			order_type= temp.getString("ORDER_TYPE","").trim();
			if(!"".equals(order_status)&&!"9".equals(order_status)){
				result.put("RESULT_MESSAGE", "3");//附件已审核
				result.put("ORDER_ID", orderId);
				result.put("ORDER_STATUS", order_status);
			}else{
				if(!"2".equals(order_type)){
					IData config = FtpFileAction.getFtpConfig("personserv", this.getVisit());
					
					///Order/YYYYMMDD/photo/110223-1.jpg
					String tempDir = "/upload/2014SchoolSale/"+SysDateMgr.getDateForYYYYMMDD(accept_date)+"/photo/";
//					String tempDir = "/upload";
					dir=config.getString("ROOT_PATH")+tempDir;
					FtpUtil ftp = new FtpUtil(config.getString("FTP_URL"),config.getString("ACCT_USR"),config.getString("ACCT_PWD"),dir);
					boolean isExist = false;
					int i=0;
					List fileList = ftp.getFileList(dir);
					for(int j=0; j<fileList.size(); j++){
						IData fileData = new DataMap();
						fileData.put("FTP_SITE", "personserv");
						fileData.put("FILE_PATH", "upload");
						fileData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
						if(fileList.get(j).toString().contains(group_photo)){
							isExist = true;
							group_photo=(String)fileList.get(j);
							fileData.put("FILE_NAME", group_photo);
							IDataset fileDs =  CSViewCall.call(this, "SS.SchoolSaleManagerSVC.insertFile", fileData);
							if (IDataUtil.isNotEmpty(fileDs))
						    {
								if(fileDs.getData(0).getBoolean("FLAG")){
									IData temp1 = new DataMap();
									String fileId = fileDs.getData(0).get("FILE_ID").toString();
									ftp.rename(group_photo,fileId );
									temp1.put("URL", "attach?fileId="+fileId+"&action=show&realName="+group_photo);
									temp1.put("BLOCK_NAME", group_photo);
									temp1.put("FILE_ID", fileId);
									infos.add(temp1);
								}
						    }
							i++;
						}else if(fileList.get(j).toString().contains(face_photo)){
							face_photo=(String)fileList.get(j);
							fileData.put("FILE_NAME", face_photo);
							IDataset fileDs =  CSViewCall.call(this, "SS.SchoolSaleManagerSVC.insertFile", fileData);
							if (IDataUtil.isNotEmpty(fileDs))
						    {
								if(fileDs.getData(0).getBoolean("FLAG")){
									IData temp2 = new DataMap();
									String fileId = fileDs.getData(0).get("FILE_ID").toString();
									ftp.rename(face_photo, fileId);
									temp2.put("URL", "attach?fileId="+fileId+"&action=show&realName="+face_photo);
									temp2.put("BLOCK_NAME", face_photo);
									temp2.put("FILE_ID", fileId);
									infos.add(temp2);
								}
						    }
							i++;
						}else if(fileList.get(j).toString().contains(back_photo)){
							back_photo=(String)fileList.get(j);
							fileData.put("FILE_NAME", back_photo);
							IDataset fileDs =  CSViewCall.call(this, "SS.SchoolSaleManagerSVC.insertFile", fileData);
							if (IDataUtil.isNotEmpty(fileDs))
						    {
								if(fileDs.getData(0).getBoolean("FLAG")){
									IData temp3 = new DataMap();
									String fileId = fileDs.getData(0).get("FILE_ID").toString();
									ftp.rename(back_photo, fileId);
									temp3.put("URL", "attach?fileId="+fileId+"&action=show&realName="+back_photo);
									temp3.put("BLOCK_NAME", back_photo);
									temp3.put("FILE_ID", fileId);
									infos.add(temp3);
								}
						    }
							i++;
						}
						if(i==3){
							break;
						}
					}
					if(!isExist){
						result.put("RESULT_MESSAGE", "1");//附件未下载
					}else{
						result.put("RESULT_MESSAGE", "2");////附件已下载
						result.put("ORDER_ID", orderId);
						result.put("ORDER_TYPE", order_type);
						result.put("ORDER_STATUS", order_status);
						result.put("ACCEPT_DATE", accept_date);
					}
					ftp.closeServer();
				}else{
					result.put("RESULT_MESSAGE", "2");////附件已下载
					result.put("ORDER_ID", orderId);
					result.put("ORDER_TYPE", order_type);
					result.put("ORDER_STATUS", order_status);
					result.put("ACCEPT_DATE", accept_date);
				}
			}
		}else{
			result.put("RESULT_MESSAGE", "0");//未找到工单
		}
		this.setAjax(result);
		setInfos(infos);
		setInfo(result);
	}
	
	public void modifyFileName(IRequestCycle cycle)throws Exception{
		IData data = getData();
		IData config = FtpFileAction.getFtpConfig("personserv", this.getVisit());
		String tempDir = "/upload/2014SchoolSale/"+SysDateMgr.getDateForYYYYMMDD(data.getString("ACCEPT_DATE"))+"/photo/";
		String dir=config.getString("ROOT_PATH")+tempDir;
		FtpUtil ftp = new FtpUtil(config.getString("FTP_URL"),config.getString("ACCT_USR"),config.getString("ACCT_PWD"),dir);
		List fileList = ftp.getFileList(dir);
		int listSize = fileList.size();
		String fileStr = data.getString("FILE_DS");
		IDataset fileDs = new DatasetList(fileStr);
		if(IDataUtil.isNotEmpty(fileDs)){
			int size = fileDs.size();
			for(int j=0; j<listSize; j++){
				for(int i=0;i<size;i++){
					if(fileList.get(j).toString().contains(fileDs.getData(i).getString("FILE_ID"))){
						ftp.rename(fileDs.getData(i).getString("FILE_ID"), fileDs.getData(i).getString("REAL_NAME"));
					}
				}
			}
		}
	}
	
    public abstract void setInfos(IDataset infos);
    
    public abstract void setInfo(IData info);
    
    public abstract void setCond(IData cond);
    
    public abstract void setCount(long count);

}
