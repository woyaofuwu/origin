
package com.asiainfo.veris.crm.order.web.person.changepassword;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户密码变更
 * 
 * @author liutt
 */
public abstract class ModifyUserPwdInfo extends PersonBasePage
{
    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setTradeTypeCode(data.getString("TRADE_TYPE_CODE"));
        setAuthType(data.getString("AUTH_TYPE", "00"));

    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.ModifyUserPwdInfoRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 人像信息比对
     * 
     * @author zhuoyingzhi
     * @param clcle
     * @throws Exception
     * @date 20170626
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
     * REQ201705270006_关于人像比对业务优化需求
     * <br/>
     * 判断是否为携入号码或固话号码
     * @param clcle
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170628
     */
    public  void  checkNpQryOrWX(IRequestCycle clcle)throws Exception{
		IData data = getData();
		String serialNumber = data.getString("SERIAL_NUMBER", "");
		
		IData param =new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		IData reulst=new DataMap();
	    //携入标识   1是携入     0不是
        reulst.put("NPTag", "0");
		//固话标识
		reulst.put("WXTag", "0");
		/**
		 * REQ201805290007_关于人像比对优化需求
		 * <br/>
		 * 由于取消  携入号码或固话号码 人像比对限制,则默认返回非 携入号码或固话号码 方式处理
		 * @author zhuoyingzhi
		 * @date 20180712
		 */
		
/*		IDataset ids = CSViewCall.call(this, "SS.ModifyUserPwdInfoSVC.queryNpQry", param);
         if (IDataUtil.isNotEmpty(ids)) {
			String asp = ids.getData(0).getString("ASP", "").trim();
			if ("2".equals(asp) || "3".equals(asp)) {
				// 2 是联通号码 3 是电信号码
				reulst.put("NPTag", "1");
			}
		}*/

		IDataset userInfo = CSViewCall.call(this, "SS.ModifyUserPwdInfoSVC.qryUserInfo", param);
/*	    if(IDataUtil.isNotEmpty(userInfo)){
	    	 String  netTypeCode=userInfo.getData(0).getString("NET_TYPE_CODE", "");
	    	 if("12".equals(netTypeCode)||"11".equals(netTypeCode)||"18".equals(netTypeCode)){
	    		 //商务电话开户+固话装机开户（net_type_code='12')、宽带装机开户（net_type_code='11')、无线固话开户界面（net_type_code='18') 
	    			reulst.put("WXTag", "1");
	    	 }
	    }*/
        /**
         * REQ201707060009关于补卡、密码重置、复机业务优化的需求
         * <br/>
         * 是否有经办人摄像权限
         * @author zhuoyingzhi
         * @date 20170805
         */
        boolean isAgentPortraitComparisonRight = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "isAgentPortraitComparisonRight");
        if(isAgentPortraitComparisonRight){
       	   //有经办人摄像权限
        	reulst.put("isAgentRight", "0");
        }else{
        	//无经办人摄像权限
        	reulst.put("isAgentRight", "1");
        }
        /**
         * 手动输入身份证号码权限判断
         * <br/>
         * 1 有权限    0无权限 
         * @author 卓英智
         * @date 20171020
         */
        String highpriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "HIGH_PRIV")==true?"1":"0"; 
        reulst.put("highprivRight", highpriv);
        if(IDataUtil.isNotEmpty(userInfo)){
        	param.put("CUST_ID", userInfo.getData(0).getString("CUST_ID",""));
        }
		IData custInfo = CSViewCall.call(this, "SS.ModifyUserPwdInfoSVC.qryCustomerInfo", param).getData(0);
		/**
		 * 由于在uca中有些工号加载出来的信息有些是带"*",所以重新加载一下客户证件信息
		 */
		reulst.put("LOAD_CUST_NAME", custInfo.getString("CUST_NAME", ""));
		reulst.put("LOAD_PSPT_ID", custInfo.getString("PSPT_ID", ""));
		reulst.put("LOAD_PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE", ""));
		/***************************************************************/

		IDataset brandDataset = CSViewCall.call(this, "SS.ModifyUserPwdInfoSVC.qryBrandCode", param);
		String brandcode = "" ;
		if(IDataUtil.isNotEmpty(brandDataset) && brandDataset.size()>0){
			brandcode = ((IData)brandDataset.get(0)).getString("BRAND_CODE");
		}
		reulst.put("BRAND_CODE", brandcode ) ; 
		
		setAjax(reulst);
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
    public abstract void setAuthType(String authType);

    public abstract void setTradeTypeCode(String tradeTypeCode);
}
