
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.ftthmodemmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class HEMUTerminalManage extends PersonBasePage
{ 
	 public void onInitTrade(IRequestCycle cycle) throws Exception
	    {
	        IDataset applyTypeList = pageutil.getStaticList("HEMU_TERMINAL_TRADE_TYPE");
	        this.setApplyTypeList(applyTypeList);
	    }
    /**
     * 
     * @Description：TODO 和目终端管理
     * @param:@param cycle
     * @param:@throws Exception
     * @return void
     * @throws
     * @Author :tanzheng
     * @date :2017-12-1上午11:46:30
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String routeId = data.getString("EPARCHY_CODE");
        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            data.put("EPARCHY_CODE", "0898");
        }
      
        IDataset dataset = null;
 
        dataset = CSViewCall.call(this, "SS.HEMUTerminalManageSVC.submit", data);
        
        setAjax(dataset.first());
    } 
    /**
     *TODO
     * @Description：根据用户手机号码获取用户信息
     * @param:@param cycle
     * @return void
     * @throws Exception 
     * @throws
     * @Author :tanzheng
     * @date :2017-11-27上午09:56:50
     */
    public void getTerminalBySN(IRequestCycle cycle) throws Exception {
    	IData data = getData();
    	IDataset dataset = null; 
    	IData result = new DataMap();
    	try{
    		
    		 IDataset checkResult = CSViewCall.call(this, "SS.HEMUTerminalManageSVC.checkSerialNumber", data);
    		 if(checkResult!=null && checkResult.size()>0){
     			if(!"0000".equals(((IData)checkResult.get(0)).get("RESULT_CODE"))){
     				result.put("RESULT_CODE", "9998");
        			result.put("RESULT_INFO", ((IData)checkResult.get(0)).get("RESULT_INFO"));
        			setAjax(result);
        			return;
     			}
     		}else{
     			result.put("RESULT_CODE", "9997");
    			result.put("RESULT_INFO", "查询用户异常");
    			setAjax(result);
    			return;
     		}
    		
    		dataset = CSViewCall.call(this, "SS.HEMUTerminalManageSVC.getTerminalBySN", data);
    		if(dataset!=null && dataset.size()>0){
    			result = dataset.first();
    			result.put("RESULT_CODE", "0000");
    		}else{
    			dataset = CSViewCall.call(this, "SS.HEMUTerminalManageSVC.checkHSWUserBySN", data);
    			IData data2 = dataset.getData(0);
    			if(dataset!=null && ("0000".equals(data2.getString("CODE")) || "0001".equals(data2.getString("CODE")))){
    				result = dataset.first();
    				result.put("RESULT_CODE", "0001");
    			}else{
        			result = new DataMap();
        			result.put("RESULT_CODE", "9999");
        			result.put("RESULT_INFO", "用户不能办理该业务");
    			}
    		}
    		
    	}catch (Exception e) {
    		result = new DataMap();
			result.put("RESULT_CODE", "9998");
			result.put("RESULT_INFO", e.getMessage());
		}
    	setAjax(result);
	}
    /**
     * 
     * @Description：校验终端号并预占
     * @param:
     * @return void
     * @throws Exception 
     * @throws
     * @Author :tanzheng
     * @date :2017-12-1上午09:44:16
     */
    public void checkTerminalId(IRequestCycle cycle) throws Exception {
    	IData pagedata = getData(); 
    	String serial_number = pagedata.getString("SERIAL_NUMBER");
    	if(serial_number.length() > 11){//商务宽带去掉宽带号码KD_
    		serial_number = serial_number.substring(3, serial_number.length());
    	}
    	pagedata.put("SERIAL_NUMBER", serial_number);
    	IDataset results = CSViewCall.call(this, "SS.HEMUTerminalManageSVC.checkHEMUTerminal", pagedata);
    	IData retData = results.first();
    	setInfo(retData);
    	setAjax(retData);
	}
    

    public abstract void setApplyTypeList(IDataset applyTypeList);

    public abstract void setInfo(IData info);
    
}
