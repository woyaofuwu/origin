
package com.asiainfo.veris.crm.order.web.person.restoreuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RestoreUser extends PersonBasePage
{
    /**
     * 功能说明：资源校验 修改时间
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkNewResource(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        String resTypeCode = pageData.getString("RES_TYPE_CODE", "");
        pageData.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("EPARCHY_CODE", ""));
        IData resData = new DataMap();
        resData.put("RESULT_CODE", "N");
        if (StringUtils.equals("1", resTypeCode) || StringUtils.equals("0", resTypeCode))
        {
            IDataset resDataset = CSViewCall.call(this, "SS.RestorePersonUserSVC.checkRestoreNewRes", pageData);
            if (IDataUtil.isNotEmpty(resDataset))
            {
                IData tempData = resDataset.getData(0);
                if (IDataUtil.isNotEmpty(tempData))
                {
                    resData.putAll(tempData);
                    resData.put("RESULT_CODE", "Y");
                    resData.put("START_DATE", SysDateMgr.getSysTime());
                    resData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                }
            }
        }
        setAjax(resData);
    }

    /**
     * 获取产品费用
     * @author
     * @param clcle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        data.put(Route.USER_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    /**
     * 获取147号码可以选择的产品
     * 
     * @author xiaozb
     * @param clcle
     * @throws Exception
     */
    public void getProductForSpc(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.getProductForSpc", data);
        setAjax(dataset);
    }

    /**
     * 认证结束之后回调的方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        pagedata.put(Route.ROUTE_EPARCHY_CODE, pagedata.getString("EPARCHY_CODE", ""));
        IDataset returnDataset = CSViewCall.call(this, "SS.RestorePersonUserSVC.getRestoreUserInfo", pagedata);
        IData returnData = returnDataset.getData(0);
        IData resData = returnData.getData("RES_INFO");
        setResInfos(resData.getDataset("RES_INFOS"));// 原始资源信息
        setDiscntInfos(returnData.getDataset("DISCNT_INFO")); // 用户原优惠信息
        setEditInfo(returnData.getData("EDIT_INFO"));
        setProductTypeList(returnData.getDataset("PRODUCT_TYPE_LIST"));
        
        String familyTag = "0";
        if (StringUtils.isNotBlank(pagedata.getString("USER_ID", ""))) {
        	 IData param = pagedata;
        	 //param.put("ELEMENT_TYPE", "D");
        	 IDataset discntInfo = CSViewCall.call(this, "SS.RestorePersonUserSVC.qryResDiscntInfos", param);
        	 setResDiscntInfos(discntInfo);
        	 IDataset svcInfo = CSViewCall.call(this, "SS.RestorePersonUserSVC.qryResSvcInfos", param);
        	 setResSvcInfos(svcInfo);
        	 IDataset platSvcInfo = CSViewCall.call(this, "SS.RestorePersonUserSVC.qryResplatSvcInfos", param);
        	 setResPlatSvcInfos(platSvcInfo);
        	 IData familyInfo = CSViewCall.callone(this, "SS.RestorePersonUserSVC.qryRestoreFamily", param);
        	 familyTag = familyInfo.getString("FAMILY_TAG");
        }
        
        
        /**
         * REQ201705270006_关于人像比对业务优化需求
         * <br/>
         * 判断是否为携入号码或无效固话号码
         * @author zhuoyingzhi
         * @date 20170630
         */
	        IData param =new DataMap();
			param.put("SERIAL_NUMBER", pagedata.getString("SERIAL_NUMBER", ""));
			IData npTagAndWxTag=new DataMap();
			/**
			 * REQ201805290007_关于人像比对优化需求
			 * <br/>
			 * 由于取消  携入号码或固话号码 人像比对限制,则默认返回非 携入号码或固话号码 方式处理
			 * @author zhuoyingzhi
			 * @date 20180712
			 */
	        //携入标识   1是携入     0不是
			npTagAndWxTag.put("NPTAG", "0");
			//固话标识
			npTagAndWxTag.put("WXTAG", "0");
			
/*			IDataset ids = CSViewCall.call(this, "SS.ModifyUserPwdInfoSVC.queryNpQry", param);
			if (IDataUtil.isNotEmpty(ids)) {
				String asp = ids.getData(0).getString("ASP", "").trim();
				if ("2".equals(asp) || "3".equals(asp)) {
					// 2 是联通号码 3 是电信号码
					npTagAndWxTag.put("NPTAG", "1");
				}
			}			
			IDataset userInfo = CSViewCall.call(this, "SS.ModifyUserPwdInfoSVC.qryUserInfo", param);
		    if(IDataUtil.isNotEmpty(userInfo)){
		    	 String  netTypeCode=userInfo.getData(0).getString("NET_TYPE_CODE", "");
		    	 if("12".equals(netTypeCode)||"11".equals(netTypeCode)||"18".equals(netTypeCode)){
		    		 //商务电话开户+固话装机开户（net_type_code='12')、宽带装机开户（net_type_code='11')、无线固话开户界面（net_type_code='18') 
		    		 npTagAndWxTag.put("WXTAG", "1");
		    	 }
		    }*/ 
		    setNpTagAndWxTag(npTagAndWxTag);
        /*********************************************/
		   IData enitInfoIdate=returnData.getData("EDIT_INFO");
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
	        	enitInfoIdate.put("isAgentRight", "0");
	        }else{
	        	//无经办人摄像权限
	        	enitInfoIdate.put("isAgentRight", "1");
	        }
	     /**********************************************/
	    
	    //IMS固话查询
	    String ImsTag = "0";
	    String widenetTag = "0";  //宽带标识
	    if(StringUtils.isEmpty(pagedata.getString("SERIAL_NUMBER")))
	    {
	    	pagedata.put("SERIAL_NUMBER", pagedata.getString("AUTH_SERIAL_NUMBER"));
	    }
        
		//查询用户固话信息
		IData ImsInfo = CSViewCall.callone(this,"SS.ChangeSvcStateSVC.getDestroyIMSInfoBySn", pagedata);
		if(IDataUtil.isNotEmpty(ImsInfo)) {
			ImsTag = ImsInfo.getString("IMS_TAG");
		}
		enitInfoIdate.put("IMS_TAG", ImsTag);
		
		//查询用户宽带信息
		IData widenetInfo = CSViewCall.callone(this,"SS.ChangeSvcStateSVC.getDestroyWidenetInfoBySn", pagedata);
		if(IDataUtil.isNotEmpty(widenetInfo)) {
			widenetTag = widenetInfo.getString("WIDENET_TAG");
		}
		enitInfoIdate.put("WIDENET_TAG", widenetTag);
		enitInfoIdate.put("FAMILY_TAG", familyTag);
		
        setAjax(enitInfoIdate);
    }

    /**
     * 菜单点击执行的事件
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pgData = this.getData();
        IData editInfo = new DataMap();
        editInfo.put("TRADE_TYPE_CODE", pgData.getString("TRADE_TYPE_CODE", "310"));
        editInfo.put("authType", pgData.getString("authType", "00"));
        setEditInfo(editInfo);
    }

    /**
     *    省内无线固话一证5号
     *
     * @param cycle
     * @throws Exception
     */
    public void checkProvinceMorePsptId(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateTDPersonUserSVC.checkProvinceMorePsptId", data);
        setAjax(dataset.getData(0));
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
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", ""));
        IDataset dataset = CSViewCall.call(this, "SS.RestorePersonUserRegSVC.tradeReg", data);
        
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        IData bizCode = new DataMap();
		bizCode.put("SUBSYS_CODE", "CSM");
		bizCode.put("PARAM_ATTR", "9124");
		bizCode.put("PARAM_CODE", "SAVE_PIC_TAG");
		IDataset commparas = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode", bizCode);
        if (IDataUtil.isNotEmpty(commparas))
        {
        	if("1".equals(commparas.getData(0).getString("PARA_CODE1", "0")))
        	{
		        if(DataSetUtils.isBlank(dataset)) {
					CSViewException.apperr(CrmCommException.CRM_COMM_595);
				}
		        IData params = getData("custInfo_AGENT", true);
		
				String orderId = dataset.first().getString("ORDER_ID","");
		        String tradeId = dataset.first().getString("TRADE_ID","");
		        String sid=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		        orderId=orderId.substring(0, orderId.length()-4);
		        params.put("MOBILENUM", data.getString("SERIAL_NUMBER",""));
		        params.put("IDCARDNUM", data.getString("PSPT_ID",""));  
		        params.put("REMOTE_ORDER_ID", "COP898" + sid + orderId);
		        params.put("TRADE_ID", tradeId);
		        //System.out.println("-----------zhangxing3Fuji------------params:"+params);
		        CSViewCall.call(this, "SS.RemoteDestroyUserSVC.insPsptFrontBack", params);
        	}
        }
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        
        setAjax(dataset);
    }

    public void releaseSingleResOnClose(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.RestorePersonUserSVC.releaseSingleResOnClose", data);
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
    public abstract void setDiscntInfos(IDataset discntList);

    public abstract void setEditInfo(IData commInfo);

    public abstract void setProductTypeList(IDataset productTypeList);

    public abstract void setResInfos(IDataset resList);
    
    public abstract void setNpTagAndWxTag(IData commInfo);
    
    public abstract void setResDiscntInfos(IDataset discntList);
    
    public abstract void setResSvcInfos(IDataset svcList);
    
    public abstract void setResPlatSvcInfos(IDataset platSvcList);


}
