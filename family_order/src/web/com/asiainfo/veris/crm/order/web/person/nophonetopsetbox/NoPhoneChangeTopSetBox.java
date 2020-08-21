package com.asiainfo.veris.crm.order.web.person.nophonetopsetbox;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 无手机魔百合 换机
 * @author zhengkai5 
 */
public abstract class NoPhoneChangeTopSetBox extends PersonBasePage{

	
	public abstract void setUserInfo(IData userInfo);
	
	public abstract void setCustInfo(IData custInfo);
	
	public abstract void setResInfo(IData resInfo);
	
	public abstract void setOldResInfo(IData oldResInfo);
	
	public abstract void setBasePackages(IDataset basePackages);

    public abstract void setOptionPackages(IDataset optionPackages);
    
    public abstract void setProducts(IDataset products);

    public abstract void setUserOldInfo(IData userOldInfo);

	public void loadPageInfo(IRequestCycle cycle) throws Exception
    {
		IData data = getData();
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        
        if (StringUtils.isNotBlank(userInfo.getString("PRODUCT_ID")))
        {
            userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, userInfo.getString("PRODUCT_ID")));
        }
        
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneTopSetBoxSVC.checkUserValidForChangeTopsetbox", data);
        IData retData = dataset.first();
        IData resInfo = retData.getData("OLD_RES_INFO");
        IData internetTvUserInfo = retData.getData("USER_INFO");
        
        this.setOldResInfo(resInfo);
        this.setCustInfo(custInfo);
        this.setUserInfo(userInfo);
        this.setUserOldInfo(internetTvUserInfo);
        
    }
	
	
	/**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        if (!StringUtils.isEmpty(data.getString("AUTH_SERIAL_NUMBER", "")))
        {
        	String serialNumber = data.getString("AUTH_SERIAL_NUMBER", "") ;
        	if(!"KD_".equals(serialNumber.substring(0, 3))){
        		serialNumber = "KD_"+serialNumber ;
        	}
            data.put("SERIAL_NUMBER", serialNumber);
            data.put("SERIAL_NUMBER_B", serialNumber);
        }
        
        //通过后台获取 147号码（魔百和绑定在147手机号码上）
		IData relaUU = CSViewCall.callone(this, "SS.NoPhoneTopSetBoxSVC.getRelaUUInfoBySnB", data);
		data.put("SERIAL_NUMBER", relaUU.getString("SERIAL_NUMBER_A"));
        
        data.put("BASE_PACKAGES", data.getString("OLD_BASE_PACKAGES").split(",")[0]);
        data.put("PRODUCT_ID", data.getString("OLD_BASE_PACKAGES").split(",")[1]);
        
        if (!"-1".equals(data.getString("OLD_OPTION_PACKAGES").split(",")[0]))
        {
            data.put("OPTION_PACKAGES", data.getString("OLD_OPTION_PACKAGES").split(",")[0]);
        }
        
        data.put("INTERNET_TV_SOURCE", "CHANGE_TOPSETBOX");		//用来标记是做换机顶盒
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneTopSetBoxRegSVC.tradeReg", data);
        setAjax(dataset);
    }
	
    /**
     * @Function: checkTerminal()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午4:58:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void checkTerminal(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("INTERNET_TV_SOURCE", "CHANGE_TOPSETBOX");		//用来标记是做换机顶盒
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkTerminal", data);
        IData retData = dataset.first();
        this.setResInfo(retData);
        this.setAjax(retData);
    }
    	
}
