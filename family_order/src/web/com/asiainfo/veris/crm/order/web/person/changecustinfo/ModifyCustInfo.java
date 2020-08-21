
package com.asiainfo.veris.crm.order.web.person.changecustinfo;

import org.apache.tapestry.IRequestCycle;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import java.util.ArrayList;
import java.util.List;

public abstract class ModifyCustInfo extends PersonBasePage
{
    /**
     * 获取子业务信息
     * 
     * @return
     * @throws Exception
     */
    public void getCommInfo(IData allInfo) throws Exception
    {
        IData params = new DataMap();
        IData temp = new DataMap();
        params.put("USER_ID", allInfo.getString("USER_ID"));
        params.put("RSRV_VALUE_CODE", "REAL");
        params.put("SERIAL_NUMBER", allInfo.getString("SERIAL_NUMBER"));
        // 查询用户实名制预登记信息
        IDataset dataset = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherUserId", params);// getUserOtherUserId(params);
        IData custInfo = allInfo.getData("CUST_INFO");
        if (IDataUtil.isNotEmpty(dataset))
        {
            // 如果实名制预受理时 没有填写相应的资料 则用 客户原来的资料代替
            IData data = dataset.getData(0);
            if (StringUtils.isNotBlank(data.getString("RSRV_STR2")))
            {
                temp.put("REAL_CUST_NAME", data.getString("RSRV_STR2"));
            }
            else
            {
                temp.put("REAL_CUST_NAME", custInfo.getString("CUST_NAME"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR3")))
            {
                temp.put("REAL_PSPT_TYPE_CODE", data.getString("RSRV_STR3"));
            }
            else
            {
                temp.put("REAL_PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR4")))
            {
                temp.put("REAL_PSPT_ID", data.getString("RSRV_STR4"));
            }
            else
            {
                temp.put("REAL_PSPT_ID", custInfo.getString("PSPT_ID"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR5")))
            {
                temp.put("REAL_PSPT_ADDR", data.getString("RSRV_STR5"));
            }
            else
            {
                temp.put("REAL_PSPT_ADDR", custInfo.getString("PSPT_ADDR"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR6")))
            {
                temp.put("REAL_PHONE", data.getString("RSRV_STR6"));
            }
            else
            {
                temp.put("REAL_PHONE", custInfo.getString("PHONE"));
            }
            temp.put("REAL_REG", "1");// 存在实名制预登记记录
        }
        else
        {
            temp.put("REAL_CUST_NAME", "");
            temp.put("REAL_PSPT_TYPE_CODE", "");
            temp.put("REAL_PSPT_ID", "");
            temp.put("REAL_PSPT_ADDR", "");
            temp.put("REAL_PHONE", "");
            temp.put("REAL_REG", "0");// 不存在实名制预登记记录
        }

        params.put("USER_ID", allInfo.getString("USER_ID"));
        params.put("GOODS_STATE", "0");
        // 根据用户标识USER_ID查询用户购机信息
        IDataset purchaseDataset = CSViewCall.call(this, "CS.UserSaleGoodsQrySVC.getPurchaseInfoByUserId", params);
        if (!purchaseDataset.isEmpty())
        {
            temp.put("IS_IN_PURCHASE", "1");// 用做前台判断，如果用户还处在营销活动期限内，则不能够修改客户名称
        }
        else
        {
            temp.put("IS_IN_PURCHASE", "0");
        }
        // 用户实名制用TF_F_CUSTOMER表中的IS_REAL_NAME字段来进行判断
        if ("1".equals(custInfo.getString("IS_REAL_NAME", "")))
        {
            temp.put("REAL_NAME", "true");
        }
        else
        {
            temp.put("REAL_NAME", "");
        }
        // 是否有特殊资料修改权限
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "MODIFYSPECUSTINFO"))
        {
            temp.put("STAFF_SPECIAL_RIGTH", "true");
        }
        else
        {
            temp.put("STAFF_SPECIAL_RIGTH", "false");
        }

        setCommInfo(temp);

        // 如果员工拥有特殊权限，且用户为实名制，则记录查询日志。
        if ("true".equals(temp.getString("REAL_NAME")) && "true".equals(temp.getString("STAFF_SPECIAL_RIGTH")))
        {
            writeLanuchLog(allInfo);
        }

    }

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
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        pgData.put(Route.ROUTE_EPARCHY_CODE, pgData.getString("CUST_EPARCHY_CODE"));
        String custId=pgData.getString("CUST_ID");
        String userId=pgData.getString("USER_ID");
    	String sn=pgData.getString("SERIAL_NUMBER");
    	 
    	 /**
         * 密码解密
         * */
    	log.error(">>>>>>modifyCustInfo 密码解密<<<<<<");
    	//获取session_id
    	String sessionId =cycle.getRequestContext().getRequest().getSession().getId();
//        HttpSession session = SessionFactory.getInstance().getSession();
//        if (session != null) {
//        	sessionId = session.getId();
//        }
    	String checkTag="0";
    	IData indata = new DataMap();
		indata.put("PARAM_ATTR", "6899"); 
		IDataset idset = CSViewCall.call(this, "CS.GetInfosSVC.getCommCommparaSVC", indata);
		if(idset!=null && idset.size()>0){
			checkTag=idset.getData(0).getString("PARA_CODE1");
		}
		
		String tradeTypeCode="60";//客户资料变更写死
    	String tradeTypeCodeTag = "0"; //默认不拦截
    	//IDataset ids = CommparaInfoQry.getCommNetInfo("CSM","6999",tradeTypeCode);
    	indata.put("TRADE_TYPE_CODE", tradeTypeCode); 
    	IDataset ids = CSViewCall.call(this, "CS.GetInfosSVC.getTradeCommparaSVC", indata);
		if(ids!=null && ids.size()>0){
			//tradeTypeCodeTag=ids.getData(0).getString("PARA_CODE1");
			if("1".equals(ids.getData(0).getString("PARA_CODE1")))	tradeTypeCodeTag="1";
		}   
		
    	String AUTH_SPEC_UID= String.valueOf(SharedCache.get("USERIDCACHE"+sessionId));//如果是在别的页面鉴权过的，这个值可能为空
    	String AUTH_SPEC_CID=String.valueOf(SharedCache.get("CUSTIDCACHE"+sessionId));//如果是在别的页面鉴权过的，这个值可能为空
    	String AUTH_SPEC_SN=String.valueOf(SharedCache.get("SERIALNUMBERCACHE"+sessionId));//这个一定会有。业务上一定将这个值传过来比对
    	String authState=String.valueOf(SharedCache.get("AUTHSTATECACHE"+sessionId));//校验状态标记
    	if("0".equals(authState)){//必须鉴权的才进行校验
    		String checkPassTag=String.valueOf(SharedCache.get("CHECKPASSTAG"+sessionId));//校验通过标记
    		if(checkPassTag==null || "".equals(checkPassTag) || !"0".equals(checkPassTag)){//必须鉴权的情况下，如果不是鉴权通过（=0通过)来到这里，则有问题
    			log.error(">>>>>>modifyCustInfo error<<<<<<<<<checkPassTag="+checkPassTag+"<<<<<<<<checkPassTag is error>>>>>>>>>> tradeTypeCode="+tradeTypeCode);
        		if(!"0".equals(checkTag)&& !"0".equals(tradeTypeCodeTag)){
        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
        		}
    		} 
    	}
	    if(userId!=null && !"".equals(userId)){ 
	    	if(AUTH_SPEC_UID!=null && !"".equals(AUTH_SPEC_UID) && AUTH_SPEC_UID.indexOf("xxyy")>-1){ 
	        	Des desObj = new Des();
	        	AUTH_SPEC_UID=desObj.getDesPwd(AUTH_SPEC_UID);
	        	if(!AUTH_SPEC_UID.equals(userId)){
	        		log.error(">>>>>>modifyCustInfo error<<<<<<<<<AUTH_SPEC_UID="+AUTH_SPEC_UID+"<<<<<<<<AUTH_SPEC_UID is error>>>>>>>>>>tradeTypeCode="+tradeTypeCode);
	        		if(!"0".equals(checkTag)&& !"0".equals(tradeTypeCodeTag)){
	        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
	        		}
	        	}
	    	}else{
	    		log.error(">>>>>>modifyCustInfo error<<<<<<<<<AUTH_SPEC_UID="+AUTH_SPEC_UID+"<<<<<<<<AUTH_SPEC_UID is null>>>>>>>>>>tradeTypeCode="+tradeTypeCode);
        		if(!"0".equals(checkTag)&& !"0".equals(tradeTypeCodeTag)){
        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
        		}
	    	}
	    }
	    if(custId!=null && !"".equals(custId)){ 
	    	if(AUTH_SPEC_CID!=null&&!"".equals(AUTH_SPEC_CID)&&AUTH_SPEC_CID.indexOf("xxyy")>-1){ 
	        	Des desObj = new Des();
	        	AUTH_SPEC_CID=desObj.getDesPwd(AUTH_SPEC_CID);
	        	if(!AUTH_SPEC_CID.equals(custId)){
	        		log.error(">>>>>>modifyCustInfo error<<<<<<<<<AUTH_SPEC_CID="+AUTH_SPEC_CID+"<<<<<<<<AUTH_SPEC_CID is error>>>>>>>>>>tradeTypeCode="+tradeTypeCode);
	        		if(!"0".equals(checkTag) && !"0".equals(tradeTypeCodeTag)){
	        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
	        		}
	        	}
	    	} else{
	    		log.error(">>>>>>modifyCustInfo error<<<<<<<<<AUTH_SPEC_CID="+AUTH_SPEC_CID+"<<<<<<<<AUTH_SPEC_CID is null>>>>>>>>>>tradeTypeCode="+tradeTypeCode);
        		if(!"0".equals(checkTag) && !"0".equals(tradeTypeCodeTag)){
        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
        		}
	    	}
	    }
	    if(sn!=null && !"".equals(sn)){ 
	    	if(AUTH_SPEC_SN!=null && !"".equals(AUTH_SPEC_SN) && AUTH_SPEC_SN.indexOf("xxyy")>-1){ 
	        	Des desObj = new Des();
	        	AUTH_SPEC_SN=desObj.getDesPwd(AUTH_SPEC_SN);
	        	if(!AUTH_SPEC_SN.equals(sn)){
	        		log.error(">>>>>>modifyCustInfo error<<<<<<<<<AUTH_SPEC_SN="+AUTH_SPEC_SN+"<<<<<<<<AUTH_SPEC_SN is error>>>>>>>>>>tradeTypeCode="+tradeTypeCode);
            		if(!"0".equals(checkTag) && !"0".equals(tradeTypeCodeTag)){
            			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
            		}
	        	}
	    	}else{
	    		log.error(">>>>>>modifyCustInfo error<<<<<<<<<AUTH_SPEC_SN="+AUTH_SPEC_SN+"<<<<<<<<AUTH_SPEC_SN is null>>>>>>>>>>tradeTypeCode="+tradeTypeCode);
        		if(!"0".equals(checkTag) && !"0".equals(tradeTypeCodeTag)){
        			CSViewException.apperr(CrmCommException.CRM_COMM_1187); 
        		}
	    	} 
    	} 
	   
        //如果操作员是通过免密码的方式则 模糊化客户资料
        //如果操作员是通过身份校验 进入页面的 则不模糊化
        if(!StringUtils.equals("F", pgData.getString("CHECK_MODE")))//F是认证组件中的免模糊化
        {
        	pgData.put("X_DATA_NOT_FUZZY","true");//强制不模糊化
        }
        IData custInfos = CSViewCall.callone(this, "SS.ModifyCustInfoSVC.getCustInfo", pgData);
        IData params = new DataMap();
        //营业执照
        params.put("USER_ID", pgData.getString("USER_ID"));
        params.put("RSRV_VALUE_CODE", "ENTERPRISE");
        params.put("SERIAL_NUMBER", pgData.getString("SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherUserId", params);// getUserOtherUserId(params);
        if(dataset!=null&&dataset.size()>0){
            IData data = dataset.getData(0);
            custInfos.getData("CUST_INFO").put("legalperson", data.getString("RSRV_STR1"));
            custInfos.getData("CUST_INFO").put("startdate", data.getString("RSRV_STR2"));
            custInfos.getData("CUST_INFO").put("termstartdate", data.getString("RSRV_STR3"));
            custInfos.getData("CUST_INFO").put("termenddate", data.getString("RSRV_STR4"));
        }
        params.clear();
        
        //组织机构代码证
        params.put("USER_ID", pgData.getString("USER_ID"));
        params.put("RSRV_VALUE_CODE", "ORG");
        params.put("SERIAL_NUMBER", pgData.getString("SERIAL_NUMBER"));
        dataset = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherUserId", params);// getUserOtherUserId(params);
        if(dataset!=null&&dataset.size()>0){
            IData data = dataset.getData(0);
            custInfos.getData("CUST_INFO").put("orgtype", data.getString("RSRV_STR1"));
            custInfos.getData("CUST_INFO").put("effectiveDate", data.getString("RSRV_STR2")); 
            custInfos.getData("CUST_INFO").put("expirationDate", data.getString("RSRV_STR3"));
        }        
        params.clear();
        
        //add by zhangxing3 for QR-20190611-03关于经办人人像比对失败也开户成功问题
        IData data1 = new DataMap();
        data1.putAll(pgData);
        data1.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset1 = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", data1);
        if (IDataUtil.isNotEmpty(dataset1))
        {
        	custInfos.getData("COMM_INFO").put("CMP_TAG", dataset1.getData(0).getString("CMPTAG",""));
        }
        //add by zhangxing3 for QR-20190611-03关于经办人人像比对失败也开户成功问题

       
        //无线固话的证件类型枚举值不一致
        if(StringUtils.equals("3811", pgData.getString("TRADE_TYPE_CODE","60")))
        {
            // 查询当前证件类型是否为单位证件类型
            String psptTypeCode =custInfos.getData("CUST_INFO").getString("PSPT_TYPE_CODE");
            boolean isOrgPsptTypeCode = isOrgPsptTypeCode(psptTypeCode);
            if(isOrgPsptTypeCode) { // 如果是单位证件类型，可以变更为任何类型。
                setPsptTypeSource(pageutil.getStaticList("TD_S_PASSPORTTYPE"));

            }else {// 非单位证件类型的可以变更为除单位证件类型以外的证件类型
                setPsptTypeSource(pageutil.getStaticList("TD_S_NOT_ORG_PASSPORTTYPE"));
            }
        }else
        {
            IDataset  ds =  pageutil.getStaticList("TD_S_PASSPORTTYPE2");
            //如果有军人身份证权限
            IData data = new DataMap();
            IDataset datasetpriv = CSViewCall.call(this, "SS.CreatePersonUserSVC.psptTypeCodePriv", data);
            if (datasetpriv != null && datasetpriv.size() > 0) {
                IData result = datasetpriv.getData(0);
                if (result.getString("X_RESULTCODE", "").equals("0")) {                  
                    data.clear();
                    data.put("DATA_ID",  result.getString("PSPT_TYPE_CODE",""));
                    data.put("DATA_NAME",  result.getString("PSPT_TYPE_NAME",""));
                    ds.add(data);
                }
            }
            setPsptTypeSource(ds);
        }
        String departKindCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_KIND_CODE", this.getVisit().getDepartId());
        IData id=new DataMap();
        /*REQ201503170006  关于开户、补卡等业务校验规则优化的需求
         * 2015-03-20 chenxy3
         * */
        String staffid=getVisit().getStaffId();
        // 是否具有手动输入证件号码权限 1标识有权限
        String permision = StaffPrivUtil.isFuncDataPriv(staffid, "HIGH_PRIV")?"1":"0";
        id.put("INPUT_PERMISSION", permision);
        id.put("LOGIN_STAFF_ID", staffid);
        id.put("DEPART_KIND_CODE", departKindCode);

        custInfos.getData("CUST_INFO").put("INPUT_PERMISSION", permision);
        setCustInfo(custInfos.getData("CUST_INFO"));
        setCommInfo(custInfos.getData("COMM_INFO"));

        //查询责任人信息
        IData params2=new DataMap();
        params2.put("CUST_ID", pgData.getString("CUST_ID"));
        params2.put("SERIAL_NUMBER", pgData.getString("SERIAL_NUMBER"));
        IDataset idCust = CSViewCall.call(this, "CS.CustPersonInfoQrySVC.qryCustPersonOtherInfoByCustId", params2);
        if(idCust!=null&&idCust.size()>0){
        	 id.put("RSRV_STR2", idCust.getData(0).getString("RSRV_STR2","")); 
        	 id.put("RSRV_STR3", idCust.getData(0).getString("RSRV_STR3","")); 
        	 id.put("RSRV_STR4", idCust.getData(0).getString("RSRV_STR4",""));
        	 id.put("RSRV_STR5", idCust.getData(0).getString("RSRV_STR5",""));
        	 
        }
        
        // 查询用户信息,取出PRODUCT_ID
        IData params1 = new DataMap();
        params1.put("X_GETMODE", 0);
        params1.put("SERIAL_NUMBER", sn);
        params1.put("TRADE_TYPE_CODE", "60");
        IDataset ucaInfos = CSViewCall.call(this, "CS.GetInfosSVC.getUCAInfos", params1);
        if(ucaInfos !=null&&ucaInfos.size()>0){
        	IData userInfo = ucaInfos.getData(0).getData("USER_INFO");
        	if(IDataUtil.isNotEmpty(userInfo)){
        		id.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID",""));
        	}
        }
        this.setAjax(id);
    }

    private Boolean isOrgPsptTypeCode(String psptTypeCode) {  // 判断当前证件类型是否属于单位证件类型
        List<String> aList = new ArrayList(){
            {
                add("D"); add("M"); add("L"); add("G"); add("E");
            }
        };
        if (aList.contains(psptTypeCode)){
            return true;
        }
        return false;
    }

    /**
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData params = getData("custInfo", true);
        params.put("IS_REAL_NAME", "1");
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        params.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        params.put("CHECK_MODE", data.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
        //String staffId=CSBizBean.getVisit().getStaffId();//测试用
        IDataset dataset = CSViewCall.call(this, "SS.ModifyCustInfoRegSVC.tradeReg", params);
        
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
				String orderId = dataset.first().getString("ORDER_ID","");
		        String tradeId = dataset.first().getString("TRADE_ID","");
		        String sid=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		        orderId=orderId.substring(0, orderId.length()-4);
		        params.put("MOBILENUM", data.getString("SERIAL_NUMBER",""));
		        params.put("IDCARDNUM", data.getString("PSPT_ID",""));  
		        params.put("REMOTE_ORDER_ID", "COP898" + sid + orderId);
		        params.put("TRADE_ID", tradeId);
		        CSViewCall.call(this, "SS.RemoteDestroyUserSVC.insPsptFrontBack", params);
        	}
        }
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        
        setAjax(dataset);
    }
    
    /**
     * 使用人证件号码数限制校验
     *
     * @author yanwu
     * @param clcle
     * @throws Exception
     */
    public void checkRealNameLimitByUsePspt(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	data.put("CODE", "1");
    	data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkRealNameLimitByUsePspt", data);
        setAjax(dataset.first());
    }
    
    /**
     * 开户人证件号码数限制校验
     *
     * @author zhangxing3
     * @param clcle
     * @throws Exception
     */
    public void checkRealNameLimitByPspt(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	data.put("CODE", "1");
    	data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkRealNameLimitByPspt", data);
        setAjax(dataset.first());
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
        param.put("BUSI_TYPE", "2");//2：存量用户补登记     
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyIdCard", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 获取经办人一个自然月和一年内经办人证件号的数量
     * @param input
     * @return
     * @throws Exception
     */
    public void AgentIdCardNums(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.AgentIdCardNums", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     *    营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifyIdCardName(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyIdCardName", data);
        setAjax(dataset.getData(0));
    }
    
    /**
     *    获取军人身份证类型
     * 
     * @param cycle
     * @throws Exception
     */
    public void psptTypeCodePriv(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.psptTypeCodePriv", data);
        setAjax(dataset.getData(0));
    }    
    
    public void verifyEnterpriseCard(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyEnterpriseCard", data);
        setAjax(dataset.getData(0));
    }   
    public void verifyOrgCard(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyOrgCard", data);
        setAjax(dataset.getData(0));
    }      
    public abstract void setAuthType(String authType);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setTradeTypeCode(String tradeTypeCode);
    
    public abstract void setPsptTypeSource(IDataset psptTypeSource);

    public void writeLanuchLog(IData allInfo) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", allInfo.getString("USER_ID"));
        params.put("SERIAL_NUMBER", allInfo.getString("SERIAL_NUMBER"));
        params.put("REMARK", "记录特殊权限工号查询实名制用户资料！");
        CSViewCall.call(this, "SS.ModifyCustInfoSVC.writeLanuchLog", params);
    }
    
    /**
     * REQ201602290007 关于入网业务人证一致性核验提醒的需求
     * 获取是否需要弹出框，chenxy3
     * 2016-03-08 chenxy3
     */
    public void checkNeedBeforeCheck(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.BeforeCheckSVC.checkNeedBeforeCheck", data);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 客户资料变更
     * 选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）用户资料若满足以下条件：
     * tf_f_user_other where user_id=**** and rsrv_value_code='HYYYKBATCHOPEN'
     * 则可以不输入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址。
     * chenxy3 2016-08-18
     * */
    public void checkGroupPsptInfo(IRequestCycle clcle) throws Exception{
    	IData data = getData();
    	IData ajaxData=new DataMap();
    	String serialNum=data.getString("SERIAL_NUMBER","");
    	IData params = new DataMap();
        params.put("X_GETMODE", 0);
        params.put("SERIAL_NUMBER", serialNum);
        params.put("TRADE_TYPE_CODE", "60");
        // 查询用户信息
        IDataset ucaInfos = CSViewCall.call(this, "CS.GetInfosSVC.getUCAInfos", params);
        if(ucaInfos!=null && ucaInfos.size()>0){
        	String userId=ucaInfos.getData(0).getData("USER_INFO").getString("USER_ID");
        	data.put("USER_ID",userId);
        	IDataset dataset = CSViewCall.call(this, "SS.ModifyCustInfoSVC.checkGroupPsptInfo", data);
            if(dataset!=null && dataset.size()>0){
            	ajaxData.put("EXISE", "TRUE");
            }else{
            	ajaxData.put("EXISE", "FALSE");
            }
        }else{
        	ajaxData.put("EXISE", "FALSE");
        }  	 
        setAjax(ajaxData);
    }
    /**
     *    全网一证5号
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkGlobalMorePsptId(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkGlobalMorePsptId", data);
        setAjax(dataset.getData(0));
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
     * BUS201807260017	关于调整实名制相关规则的需求 by mqx 20180823
     * 代办入网业务权限判断
     * 
     */
    public void verifyAgentPriv(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyAgentPriv", param);
        setAjax(dataset.getData(0));
    }    
}
