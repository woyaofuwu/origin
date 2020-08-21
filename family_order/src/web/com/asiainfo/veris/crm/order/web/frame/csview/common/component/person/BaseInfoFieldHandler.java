
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class BaseInfoFieldHandler extends BizHttpHandler
{
    public void checkRealNameLimitByPspt() throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("CODE", "1");
        String svcName = data.getString("SVC_NAME", "SS.CreatePersonUserSVC.checkRealNameLimitByPspt");
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset results = CSViewCall.call(this, svcName, data);
        if (IDataUtil.isNotEmpty(results))
        {
            param = results.getData(0);
        }
        setAjax(param);
    }

    public void qryBankBySuperBank() throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("CITY_CODE", "")))
        {
            data.put("CITY_CODE", getVisit().getCityCode());
        }
        IDataset banks = CSViewCall.call(this, "CS.BankInfoQrySVC.getBankBySuperBank3", data);
        setAjax(banks);
    }
    
    /**
     * 开户-身份证判定是否黑名单用户 
     * REQ201510090022 关于新建黑名单库的需求
     * chenxy3 20151022
     */
    public void checkPsptidBlackListInfo() throws Exception
    {
        IData data = getData();
        String psptid=data.getString("PSPT_ID","");
        String tradeTypeCode=data.getString("BLACK_TRADE_TYPE","");
        IData param = new DataMap();
        param.putAll(data);
        String svcName = data.getString("SVC_NAME", "SS.CreatePersonUserSVC.checkPsptidBlackListInfo"); 
        String code="0";//编码，0=非黑名单； 9=黑名单用户
    	String msg="本次办理不成功。您的证件名下的手机号码";//黑名单用户拼串：本次办理不成功。您的证件名下的手机号码*******已欠费*元，请及时缴费。以便正常使用该证件办理业务
    	
        IDataset results = CSViewCall.call(this, svcName, data);
        String fee="0";
        String feeOther="";
        if (IDataUtil.isNotEmpty(results) )
        {
        	for(int i=0;i<results.size();i++){
        		IData blackInfo=results.getData(i);
        		String owe_sn=blackInfo.getString("SERIAL_NUMBER","");
        		String owe_fee=blackInfo.getString("OWE_FEE","0");
        		if(owe_fee!=null && !"0".equals(owe_fee)){
        			code="9";
        			msg+=""+owe_sn+"已欠费"+owe_fee+"元。"; 
        			feeOther=feeOther+"号码"+owe_sn+"欠费"+owe_fee+"元;";
        		}
        	} 
        	if("9".equals(code)){
        		msg+="请及时缴费，以便正常使用该证件办理业务。";
        		if(!"".equals(tradeTypeCode)&&!"undefined".equals(tradeTypeCode)){
	        		//插黑名单日志表
	        		IData insData=new DataMap();
	        		insData.put("USER_ID",""); 
	            	insData.put("SERIAL_NUMBER","");   
	            	insData.put("PSPT_ID",psptid);         
	            	insData.put("IN_MODE_CODE",getVisit().getInModeCode());     
	            	insData.put("UPDATE_STAFF_ID",getVisit().getStaffId());  
	            	insData.put("UPDATE_DEPART_ID",getVisit().getDepartId());
	            	insData.put("TRADE_TYPE_CODE",tradeTypeCode);  
	            	insData.put("FEE", fee);              
	            	insData.put("OTHER_FEE", feeOther);  
	            	CSViewCall.call(this,"SS.BlackUserManageSVC.insertBlackUserCheckLogInfo", insData);
        		}
        	}
        }
        param.put("BLACKCODE", code);
        param.put("BLACKMSG", msg);
        setAjax(param);
    }
    
    /**
     * 无手机宽带开户-判断是否有线上预约工单
	 * REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
	 * zhangxing3
     */
    public void checkTradeBookInfo() throws Exception
    {
        IData data = getData();
        IData ajaxData = new DataMap();
        ajaxData.put("BOOKTAG", "0");
        IDataset results = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkTradeBookInfo", data);
        boolean prvTag = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "NOTCMP_KFSTFPRV");
        if (IDataUtil.isNotEmpty(results) && prvTag)
        {
            ajaxData.put("BOOKTAG", "1");
        }
        if (IDataUtil.isNotEmpty(results) && !prvTag)
        {
        	ajaxData.put("BOOKTAG", "2");
        }
        setAjax(ajaxData);
    }
    
    /**
     * 身份证件实名验证
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifyIdCard() throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        param.put("BUSI_TYPE", "2");//2：存量用户补登记     
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyIdCard", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 人像信息比对
     * 
     * @author dengyi
     * @param 
     * @throws Exception
     */
    public void cmpPicInfo() throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     *    营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifyIdCardName() throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyIdCardName", data);
        setAjax(dataset.getData(0));
    }
    
    /**
     *    营业执照验证
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifyEnterpriseCard() throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyEnterpriseCard", data);
        setAjax(dataset.getData(0));
    }
    
    /**
     *    营业执照验证
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifyOrgCard() throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyOrgCard", data);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 获取军人身份证类型
     * 
     * @param cycle
     * @throws Exception
     */
    public void psptTypeCodePriv() throws Exception
    {   
        IData data = getData();           
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.psptTypeCodePriv", data);
        setAjax(dataset.getData(0));
    }
    
    /**
     *    全网一证5号
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkGlobalMorePsptId() throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkGlobalMorePsptId", data);
        setAjax(dataset.getData(0));
    }

    /**
    * method_name: 省内一证5号校验
    * param: 用户姓名、证件号、个人证件类型
    * return: 登记在网的该用户是否超过5个号
    * describe: 校验证件号在省内开户数量
    * creat_user: chenchunni
    * creat_date: 2020/2/20
    * creat_time: 17:18
    **/
    public void checkProvinceMorePsptId() throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateTDPersonUserSVC.checkProvinceMorePsptId", data);
        setAjax(dataset.getData(0));
    }
    
    
    /** 
     * 
     *获取地址文字关键字。
     * 户主、经办人、使用人、责任人登记证件地址以“国、省、市、县、区、镇、乡、对面的”地址结尾时，不允许进行登记，并提示要求客户登记精确的地址信息。 
     * 
     */
    public void getCommpara3450() throws Exception
    {   
        IData data = getData();    
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.getCommpara3450", data);
        setAjax(dataset.getData(0));
    }

    /**
     * BUS201807260017	关于调整实名制相关规则的需求 by mqx 20180823
     * 代办入网业务权限判断
     * 
     */
    public void verifyAgentPriv() throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
         
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyAgentPriv", param);
        setAjax(dataset.getData(0));
    }  
    
    /**
     * REQ201810190032 	和家固话开户界面增加实名制校验—BOSS侧  by mqx 20190108
     * 和家固话单位开户权限判断
     * 
     */
    public void verifyOrganizationPriv() throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
         
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyOrganizationPriv", param);
        setAjax(dataset.getData(0));
    }

    /**
     * REQ201810190032 	和家固话开户界面增加实名制校验—BOSS侧  by mqx 20190108
     * 和家固话代办权限判断
     *
     */
    public void verifyIMSOpAgentPriv() throws Exception
    {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyIMSOpAgentPriv", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
     * 家庭IMS固话开户(新),在界面进行人像对比后，所获取的证件号码和姓名，要与界面输入的手机号码对应的证件号码和姓名进行对比，信息一致才能提交办理
     * mengqx 20190912
     */
    public void checkIMSPhoneCustInfo() throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
         
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkIMSPhoneCustInfo", param);
        setAjax(dataset.getData(0));
    }

    /**
     * REQ201911290007_【携号转网】关于发布CSMS和SOA间接口协议上身份证件传递要求的通知
     * 修改携转界面使用的客户组件，过滤掉不符合规范证件
     * @author mengqx
     * @date 20200323
     */
    public void queryNpPsptTypeList() throws Exception
    {
        IData param = new DataMap();

        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.queryNpPsptTypeList", param);
        setAjax(dataset.getData(0));
    }
}
