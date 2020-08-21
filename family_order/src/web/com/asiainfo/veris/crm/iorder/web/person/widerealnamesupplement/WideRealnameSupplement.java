package com.asiainfo.veris.crm.iorder.web.person.widerealnamesupplement;

import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WideRealnameSupplement extends PersonBasePage {
	 
	
	/*public void onInitTrade(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		String authType = data.getString("authType", "00");
		IData info = new DataMap();
		info.put("authType", authType);
		
		IData paramsData=new DataMap();
        paramsData.put("SUBSYS_CODE", "CSM");
        paramsData.put("PARAM_ATTR", "3451");
        
        IDataset resultData=CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByAttr", paramsData);
        
		setPsptTypeSource(resultData);
		setInfo(info);
	}*/
	
	public void loadChildInfo(IRequestCycle cycle) throws Exception{
		IData pgData = getData();
		String sn = pgData.getString("SERIAL_NUMBER");
		
		IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", sn);
		IDataset userlist = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", inparam);
		
		boolean isExists=false;
		//判断客户是否有7341产品
		if (IDataUtil.isNotEmpty(userlist)) {
			for (int i = 0; i < userlist.size(); i++) {
				String productId =userlist.getData(i).getString("PRODUCT_ID");
				if (("7341".equals(productId))) {
					isExists=true;
					break;
				}
			}
		}
		
		IData result = new DataMap();
		if(!isExists){
			result.put("MSG", "集团商务宽带产品(7341)不存在，不能继续办理业务！");
			result.put("CODE", "-1");
		}else{
			result.put("CODE", "0");
		}
		
		
		IData data = getData();
        // 记录页面初始化时间点，存入台帐表EXEC_ACTION字段
        String execAction = SysDateMgr.getSysTime();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.onInitTrade", data);
        dataset.first().put("EXEC_ACTION", execAction);
        
        //result.put("INPUT_PERMISSION",dataset.first().getString("INPUT_PERMISSION"));
        result.put("INPUT_PERMISSION",dataset.getData(0).getString("INPUT_PERMISSION"));
        
        //IDataset custInfoList = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.queryCustInfoBySN", inparam);
        IDataset custInfoList = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.queryGroupCustInfoBySN", inparam);
        result.put("CUSTINFO",custInfoList.getData(0));
        
        setAjax(result);
        
        setInitInfo(dataset.first());
        
        setCustInfo(custInfoList.getData(0));
		
	}
	
	/**
     * 人像信息比对
     * 
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void cmpPicInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 人像信息比对员工信息
     * 
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
    	setAjax(dataset.getData(0));
    }
	
	/**
     * 身份证在线校验
     * 
     * @author yanwu
     * @param clcle
     * @throws Exception
     */
    public void verifyIdCard(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyIdCard", param);
        setAjax(dataset.getData(0));
    }
	
	/**
     *    全网一证5号
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkGlobalMorePsptId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.checkGlobalMorePsptId", data);
        setAjax(dataset.getData(0));
    }
	
    /**
     *    营业执照验证
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifyEnterpriseCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyEnterpriseCard", data);
        setAjax(dataset.getData(0));
    }
    
    /**
     *   获取军人身份证类型
     * 
     * @param cycle
     * @throws Exception
     */
    public void psptTypeCodePriv(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.psptTypeCodePriv", data);
        setAjax(dataset.getData(0));
    }
    
    public void verifyIdCardName(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyIdCardName", data);
        setAjax(dataset.getData(0));
    }
    
    
    public void verifyOrgCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyOrgCard", data);
        setAjax(dataset.getData(0));
    }
    
    
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData params = new DataMap();
        params.putAll(data);
        params.put("IS_REAL_NAME", "1");
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.wideRealnameSupplementRegSVC.tradeReg", params);
        setAjax(dataset);
    }
    
	public abstract void setInfo(IData info);
	public abstract void setInfos(IDataset infos);
	public abstract void setInitInfo(IData initInfo);
	public abstract void setPsptTypeSource(IDataset psptTypeSource);
	public abstract void setCustInfo(IData custInfo);
	
}