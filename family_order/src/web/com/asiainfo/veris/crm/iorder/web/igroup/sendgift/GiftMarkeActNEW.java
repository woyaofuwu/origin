
package com.asiainfo.veris.crm.iorder.web.igroup.sendgift;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * 集团礼物营销活动
 * @author chenhh6
 */
public abstract class GiftMarkeActNEW extends CSBasePage
{
	/**
     * 作用：页面初始化
     * 
     * @author chenhh6
     * @param cycle请求参数
     * @throws Throwable
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();
        param.getString("IBSYSID", "");
        IDataset list = new DatasetList();
        IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", "CSM");
        list = CSViewCall.call(this, "SS.GiftMarkeQrySVC.getGiftInfo", inparam);
        param.put("DATA", list);
        setInfo(param);
    }

    public abstract void setCond(IData cond);
    public abstract void setInfo(IData info);
    
    
    /**
     * 获取客户信息
     * @throws Exception
     * @author chenhh6
     */
	public void getCustInfo(IRequestCycle cycle) throws Exception{
		String success = "false";
		String msg = "";
		IData map = new DataMap();
		String serialNumber = getData().getString("SERIAL_NUMBER");
		//map = UCAInfoIntfViewUtil.qryUserInfoBySnNoProduct(this, serialNumber);
		IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        map = CSViewCall.callone(this, "SS.GiftMarkeQrySVC.getCustAndGroupInfoBySn", inparam);
        if(map != null){
			success = "true";
		}else {
			msg = "没有查询到用户信息";
			map = new DataMap();
		}
		
		map.put("success", success);
    	map.put("msg", msg);
    	setAjax(map);
    }
	
	
	/**
	 * 新增礼品赠送记录
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author chenhh6
	 */
	public void submitProcess(IRequestCycle cycle) throws Exception {
		IData pageData = getData("cond", true);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		IData inputData = new DataMap();
		inputData.put("SERIAL_NUMBER", pageData.getString("PHONE"));
		inputData.put("CUST_NAME", pageData.getString("CUST_NAME"));
		inputData.put("GROUP_NAME", pageData.getString("GROUP_NAME"));
		inputData.put("GROUP_ID", pageData.getString("GROUP_NUM"));
		inputData.put("SEND_GIFT", pageData.getString("GIFT_TYPE"));
		inputData.put("SEND_COUNT", pageData.getString("SEND_COUNT"));
		inputData.put("SEND_MONEY", pageData.getString("SEND_MONEY"));
		inputData.put("SEND_TIME", format.format(new Date()));
		
		inputData.put("EMPLOYEE_NUM", getVisit().getStaffId());
		inputData.put("CUST_MANAGER_ID", pageData.getString("CUST_MANAGER_ID"));
		inputData.put("CITY_CODE", pageData.getString("CITY_CODE"));
		
		IData info = CSViewCall.callone(this,"SS.GiftMarkeQrySVC.saveSendGiftInfo", inputData);

		setCond(pageData);
		setAjax(info);
	}
}
