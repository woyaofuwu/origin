
package com.asiainfo.veris.crm.order.web.person.insertAPNData.insertAPNData;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InsertAPNData extends PersonBasePage
{
    

    /**
     * 功能说明：校验数据 
     */
    public void verifyInfo(IRequestCycle cycle) throws Exception
    {
    	IData userData = getData(); 
        String paraCode1 = userData.getString("PARA_CODE1", ""); 
        String endDate = userData.getString("END_DATE", ""); 
        String remark = userData.getString("REMARK", ""); 
        userData.put("PARA_CODE1", paraCode1 );
        userData.put("END_DATE", endDate );
        userData.put("REMARK", remark );
        IDataset results = CSViewCall.call(this, "SS.InsertAPNDataSVC.verifyInfo", userData);
        log.info("SS.InsertAPNDataSVC.verifyInfo - results:"+results);
        setAjax(results.first());
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
		IData result = CSViewCall.callone(this, "SS.InsertAPNDataSVC.exportExcel", data);
		setAjax("url", result.getString("URL"));
    }	
	
	/** 上传文件 */
	public void uploadData(IRequestCycle cycle) throws Exception {
		IData data = this.getData();		
		IDataset results = CSViewCall.call(this, "SS.InsertAPNDataSVC.uploadParaMaintain", data);
        this.setAjax(results.getData(0));
	}
	
    /**
     * 业务提交,组件默认提交action方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        String fixNum=data.getString("FIX_NUMBER", "");
        if(!fixNum.startsWith("0898")){
        	data.put("FIX_NUMBER", "0898"+fixNum);
        }
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", ""));
        IDataset dataset = CSViewCall.call(this, "SS.FamilyFixPhoneRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    

    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
}
