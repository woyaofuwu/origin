package com.asiainfo.veris.crm.order.web.person.nophonetopsetbox.nophonetopsetboxcreate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 无手机宽带魔百和开户
 * @author zhengkai5
 *
 */
public abstract class NoPhoneTopSetBoxCreate extends PersonBasePage
{

	/**
	 * 输入号码后的查询
	 * */
    public void setPageInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String wideSerialnumber=data.getString("AUTH_SERIAL_NUMBER");//宽带账号
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        this.setCustInfo(custInfo);//客户名称
        
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        
        userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, userInfo.getString("TD_B_PRODUCT")));
        
        this.setUserInfo(userInfo);//产品名称、开户日期
        
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneTopSetBoxCreateSVC.checkUserForOpenInternetTV", data);
        IData retData = dataset.first();
        this.setUserOldInfo(retData);//宽带信息
        
        IDataset products = retData.getDataset("PRODUCT_INFO_SET");
        this.setProducts(products);//魔百和产品列表
        
        //魔百和费用
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.gettopsetboxfee", data);
    	
    	result.put("DISCNT_CODE", retData.getString("DISCNT_CODE",""));
        String wideSn ="";//新模型宽带对应的手机号码
        if(wideSerialnumber.startsWith("KD_"))
        {
            wideSn = wideSerialnumber.substring(3);
        }
        else
        {
            wideSn=wideSerialnumber;
        }
        
//        String beginNumber = "178";
//        IDataset prefixList = CSViewCall.call(this, "SS.ChooseIdleWideAcctIdSVC.getSerialNumberPrefixList", data);
//        if(IDataUtil.isNotEmpty(prefixList)){
//            beginNumber = prefixList.getData(0).getString("PARA_CODE1").substring(0, 3);
//        }
        
        if(wideSn.startsWith("1") && wideSn.length()==11){//新模型过来的开户，已经开户了，没必要再去开户了           
            result.put("NEW_TAG", "Y");
            result.put("NEW_SERIAL_NUMBER_B", wideSn);
        }   
        this.setAjax(result);
        


    }

	
    /**
     * 选中魔百和产品后
     * */
    public void queryDiscntPackagesByPID(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneTopSetBoxCreateSVC.queryDiscntPackagesByPID", data);
        IData retData = dataset.first();
        IDataset basePackages = retData.getDataset("B_P");
        IDataset optionPackages = retData.getDataset("O_P");
        this.setBasePackages(basePackages);
        this.setOptionPackages(optionPackages);
        setAjax(retData);
    }
	
	
    /**
     * 业务提交
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
        	if (data.getString("AUTH_SERIAL_NUMBER").startsWith("KD_"))
        	{
        		data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        	}
        	else
        	{
        		data.put("SERIAL_NUMBER", "KD_"+data.getString("AUTH_SERIAL_NUMBER"));
        	}
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneTopSetBoxCreateRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    /**
     * 输入新开户号码后的校验，获取开户信息
     * @param clcle
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        //号码限制标识，1为号码限制在td_s_commpara param_attr=2828中
        data.put("NUMBER_LIMIT_FLAG","1" );
        //将业务类型传过去
        data.put("TRADE_TYPE_CODE","4900" );
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkSerialNumber", data);
        setEditInfo(dataset.first());
        setAjax(dataset);
    }
    
    /**
     * 输入SIM卡后的校验，获取卡信息
     * @param clcle
     * @throws Exception
     */
    public void checkSimCardNo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkSimCardNo", data);
        setEditInfo(dataset.first());
        setAjax(dataset);
    }
    
    /**
     * 人像信息比对员工信息
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	/*IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
    	setAjax(dataset.getData(0));*/
    	
    	IData result = new DataMap();
    	result.put("CMPTAG", "0");
    	setAjax(result);
    }
    
    //获取魔百和费用
    public void settopsetboxfee(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	data.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.gettopsetboxfee", data);

    	String fee = result.getString("PARA_CODE2");
    	String month = data.getString("TOP_SET_BOX_TIME");
    	String totalFee = String.valueOf(Integer.parseInt(month)*Integer.parseInt(fee));
    	
    	data.put("TOP_SET_BOX_FEE", totalFee);
        this.setAjax(data);
    }

    public abstract void setBasePackages(IDataset basePackages);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setOptionPackages(IDataset optionPackages);

    public abstract void setProducts(IDataset products);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setUserOldInfo(IData userOldInfo);
    
    public abstract void setEditInfo(IData userOldInfo);

}
