package com.asiainfo.veris.crm.iorder.soa.group.param.adc;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class QueryAdcAttrParamBean extends QueryAttrParamBean{

	private static final Logger logger = LoggerFactory.getLogger(QueryAdcAttrParamBean.class);
	
	public static IData queryAdcParamAttrForCrtUsInit(IData param) throws Exception{

        IData result = new DataMap();
        String offerCode= param.getString("PRODUCT_ID"); 
        
        IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "S", "0", null);
        IData attrItemA = new DataMap();

        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            
        }  

        if(IDataUtil.isNotEmpty(attrItemA)){
            Set<String> propNames = attrItemA.keySet();
            for(String key : propNames)
            {
                IData attrCodeInfo = attrItemA.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                if(IDataUtil.isNotEmpty(workTypeCodeInfo)){
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    result.put(key, attrItem);
                }else{
                    String  attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", attrItemValue);
                    result.put(key, attrItem); 
                }
            }
        }
        
        //塞入一些隐藏参数值
        //操作类型
        IData operState = new DataMap();
        operState.put("DATA_VAL", "01");
        result.put("OPER_STATE", operState);
        //当前服务状态
        IData platSyncState = new DataMap();
        platSyncState.put("DATA_VAL", "1");
        result.put("PLAT_SYNC_STATE",platSyncState);
        //判断增删改操作
        IData modifyTag = new DataMap();
        modifyTag.put("DATA_VAL", "0");
        result.put("MODIFY_TAG", modifyTag);
        //权限判断
        IData hasModifyPrv = new DataMap();	
        String staffId = getVisit().getStaffId();
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, "GROUPMENBER_MODIFY_PRV");
        hasModifyPrv.put("DATA_VAL", bool);
        result.put("HASMODIFYPRV", hasModifyPrv);
        //服务ID
        IData serviceId = new DataMap();
        serviceId.put("DATA_VAL", offerCode);
        result.put("SERVICE_ID", serviceId);
        
        //查询校讯通才需要展示的信息,这一个类都处理比较方便
        String custId = param.getString("CUST_ID", "");
        if (StringUtils.isNotEmpty(custId))
        {
            IData data = UcaInfoQry.qryGrpInfoByCustId(custId);
            
            //
            IData exchangeData1 = new DataMap();
            IData exchangeData2 = new DataMap();
            IData exchangeData3 = new DataMap();
            IData exchangeData4 = new DataMap();
            exchangeData1.put("DATA_VAL", data.getString("GROUP_MGR_SN", "")); // 管理员号码 
            result.put("ADMIN_NUM", exchangeData1);

            
            exchangeData2.put("DATA_VAL", data.getString("CITY_CODE", "")); // 归属市县
            result.put("CITY_CODE", exchangeData2);

            
            exchangeData3.put("DATA_VAL", data.getString("CUST_MANAGER_ID", "")); // 客户经理工号
            result.put("CUST_MANAGER_ID", exchangeData3);

            
            IData staffinfo= UStaffInfoQry.qryStaffInfoByPK(data.getString("CUST_MANAGER_ID", ""));
            if (IDataUtil.isNotEmpty(staffinfo))
            {
            	exchangeData4.put("DATA_VAL", staffinfo.getString("STAFF_NAME", "")); // 客户经理名称
            	result.put("STAFF_NAME", exchangeData4);

                
            }
        }
        
        // 生成服务代码尾
        String svrlength = result.getData("C_LENGTH").getString("DATA_VAL",""); // 服务代码尾号长度
        String svrCodeTail = "";// 生成服务代码尾
        if (StringUtils.isNotBlank(svrlength))
        {
            svrCodeTail = getBizCodeTail(svrlength);
        }
        if(param.getString("OFFER_CODE").equals("9230")){
        	IData custInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
        	if(IDataUtil.isNotEmpty(custInfo)){

                String callingtype = custInfo.getString("CALLING_TYPE_CODE", "");

                IDataset servcodes = StaticUtil.getStaticList("CALLINGTYPE_SERVCODE_" + callingtype);
                if (servcodes.size() > 0) {
                    IData servcode = servcodes.getData(0);
                    String preServcode = servcode.getString("DATA_ID", "");

                    svrCodeTail = svrCodeTail.substring(preServcode.length(), Integer.parseInt(svrlength));
                    svrCodeTail = preServcode + svrCodeTail;

                }           
        	}
        }
        
        IData svrCodeEnd = new DataMap();
        svrCodeEnd.put("DATA_VAL", svrCodeTail);
        result.put("SVR_CODE_END", svrCodeEnd);
        
        //还不确定是否需要platsvc.put("INNER_SVC_TAG", "NEW");// 将服务是否是新增的标志，带出去

        IData isDisplay = new DataMap();
        isDisplay.put("DATA_VAL", StaticUtil.getStaticValue("ADC_SPEC_AREA_DISPLAY", offerCode));  
        result.put("IS_SPEC_AREA_DISPLAY",isDisplay);
        
        //遍历列表，找到业务编码字段进行重新编码
        String bizCode = result.getData("BIZ_CODE").getString("DATA_VAL", "");
      	if (StringUtils.isNotBlank(bizCode)
      		    && StringUtils.isNotBlank(param.getString("CUST_ID")))
              {
      		    if(IDataUtil.isEmpty(UcaInfoQry.qryGrpInfoByCustId(param.getString("CUST_ID")))) return result;
      		    String groupId = UcaInfoQry.qryGrpInfoByCustId(param.getString("CUST_ID")).getString("GROUP_ID", "");
      		    IData bizParam = new DataMap();
      		    bizParam.put("DATA_VAL", getNewBusiCode(bizCode, groupId));
      		    result.put("BIZ_CODE",bizParam);
              }
      	 logger.debug("==============================前台传入数据{}", result);
      	
        return result;
        
    
	}
	
	/**
     * 作用:自动生成服务代码尾
     *
     * @author liaolc 2014-07-24
     */
    public static String getBizCodeTail(String sclen) throws Exception
    {
        int cLen = 0;
        String codeC = "";
        codeC = SeqMgr.getGrpMolist();
        codeC = "00000" + codeC;
        try
        {
            cLen = Integer.parseInt(sclen);
        }
        catch (Exception e)
        {
            cLen = 0;
        }

        codeC = codeC.substring((codeC.length() - cLen), codeC.length());

        return codeC;
    }
    
    /**
     * 组装新的业务编码
     * @param oldBusiCode
     * @param group_id
     * @return
     * @throws Exception
     */
	private static String getNewBusiCode(String oldBusiCode,String group_id) throws Exception
    {
    	String newBusiCode = oldBusiCode;
    	//获取TF_F_CUST_GROUP表记录
    	IDataset groupInfoSet = UserGrpInfoQry.queryCustGrpByGID(group_id);
    	if (IDataUtil.isNotEmpty(groupInfoSet)){
    		IData groupInfo = groupInfoSet.getData(0);
    		//获取集团客户中的子行业编码
    		if (IDataUtil.isNotEmpty(groupInfo) && 
    				StringUtils.isNotBlank(groupInfo.getString("SUB_CALLING_TYPE_CODE",""))){
    			String subCallingTypeCode = groupInfo.getString("SUB_CALLING_TYPE_CODE","");
    			//查看目前的子行业编码是否为最新的编码，如果非最新的编码则取配置的最新编码。
    			IDataset commParam = CommparaInfoQry.getCommparaByCodeCode1("CSM", "2664", "CONVERTCODE", subCallingTypeCode);
    			if(IDataUtil.isNotEmpty(commParam) && IDataUtil.isNotEmpty(commParam.getData(0))){
    				IData paramInfo = commParam.getData(0);
    				String para_code = paramInfo.getString("PARA_CODE2");
    				if (StringUtils.isBlank(para_code)){
                        CSAppException.apperr(CrmUserException.CRM_USER_783,"行业编码转换关系配置数据为空!");
    				}
    				//依据配置的最新编码，将业务编码进行截取转换。
    				newBusiCode = convertBusiCode(oldBusiCode, para_code,3,para_code.trim().length());
    			}else{
    				//如果集团客户表中的子行业编码为最新的编码，则直接转换。
    				newBusiCode = convertBusiCode(oldBusiCode, subCallingTypeCode,3,subCallingTypeCode.trim().length());
    			}
    		}
    	}
    	return newBusiCode;
    }
	
	/***
     * 拼接新的业务编码
     * @param oldBusiCode
     * @param busi_code
     * @param index 截取起始位
     * @param length 截取长度
     * @return
     */
	private static String convertBusiCode(String oldBusiCode,String busi_code,int index,int length){
    	String newBusiCode = oldBusiCode.substring(0,index)+busi_code+oldBusiCode.substring(index+length);
    	return newBusiCode;
    }
	
	public static IData queryAdcParamAttrForChgUsInit(IData param) throws Exception{
		
		IData results = new DataMap();
		
		//先尝试取已有服务
		String user_id = param.getString("USER_ID");
		String service_id = param.getString("PRODUCT_ID");
		boolean createsvc = true;// 该服务是新增 还是修改标识 默认为新增
        IDataset usersvc = UserSvcInfoQry.getUserSvcBycon(user_id, "-1", "0", "0", service_id);//package_id在svc表里面已经删掉了,所以随便写个值不影响
        if(IDataUtil.isNotEmpty(usersvc))
        		createsvc = false;
        
        if(createsvc){//选择的服务为新增服务
        	results = queryAdcParamAttrForCrtUsInit(param);
        }else{//选择的服务为已订购服务
        	IData platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(user_id, service_id);// 取平台服务表已经存在的参数
            String si_base_in_code = platsvcparam.getString("SI_BASE_IN_CODE");// ADC SI 基本接入号 10657020
            String svr_code_head = platsvcparam.getString("RSRV_NUM4");// 服务代码头 106570200
            // 老资料基本接入号和服务代码头有可以为空，为空时查询ATTR表的配置补上
            if (StringUtils.isBlank(si_base_in_code))
            {
                si_base_in_code = AttrItemInfoQry.queryServiceItemA(service_id, "SIBASE_INCODE");
            }
            if (StringUtils.isBlank(svr_code_head))
            {
                svr_code_head = AttrItemInfoQry.queryServiceItemA(service_id, "SVR_CODE_HEAD");
            }
            platsvcparam.put("SIBASE_INCODE", si_base_in_code);
            platsvcparam.put("SIBASE_INCODE_A", platsvcparam.getString("SI_BASE_IN_CODE_A"));
            platsvcparam.put("SVR_CODE_HEAD", svr_code_head);

            // 查询在TF_F_USER_GRP_PLATSVC表里面存不下 然后存在attr纵表内的实例属性
            IDataset userattrdataset = UserAttrInfoQry.getuserAttrBySvcId(user_id, service_id);
            IData userattrdata = IDataUtil.hTable2StdSTable(userattrdataset, "ATTR_CODE", "ATTR_VALUE");
            platsvcparam.putAll(userattrdata);
            IData platsvc = platsvcparam;
            platsvc.put("MODIFY_TAG", "2"); // 标识修改 即已经存在的服务信息
            platsvc.put("SERVICE_ID", service_id); // 集团服务ID
            platsvc.put("INNER_SVC_TAG", "OLD");// 将服务是修改标志，带出去
            if("100022".equals(service_id) || "100024".equals(service_id))
            {
                platsvc.put("SP_CODE", platsvc.getString("RSRV_STR2", ""));;//校讯通填写合作伙伴编码

            }
            else if ("100082".equals(service_id) || "100083".equals(service_id))
            {
                String [] strParamCode  = platsvc.getString("RSRV_STR4", "").split(",");
                String [] strParamValue = platsvc.getString("RSRV_STR2", "").split(",");
                
                for(int i=0;i<strParamCode.length;i++)
                {
                    platsvc.put(strParamCode[i], strParamValue[i]);
                    
                }
            }

            if ("04".equals(platsvc.getString("OPER_STATE")))// "OPER_STATE":"04" 表示暂停
            {
                platsvc.put("PLAT_SYNC_STATE", "P");
            }

            // 业务类型
            platsvc.put("SERVICE_TYPE", platsvc.getString("RSRV_NUM2", ""));
            // 白名单二次确认
            platsvc.put("WHITE_TOWCHECK", platsvc.getString("RSRV_NUM3", ""));
            // 模板短信管理
            platsvc.put("SMS_TEMPALTE", platsvc.getString("RSRV_NUM5", ""));
            // 端口类别
            platsvc.put("PORT_TYPE", platsvc.getString("RSRV_STR1", ""));

            platsvc.put("IS_SPEC_AREA_DISPLAY", StaticUtil.getStaticValue("ADC_SPEC_AREA_DISPLAY", service_id));
            
            if (StringUtils.isNotBlank(platsvcparam.getString("BIZ_CODE"))
    				&& StringUtils.isNotBlank(param.getString("CUST_ID")))
            {
    			String bizCode = platsvcparam.getString("BIZ_CODE");
            	
            	if(IDataUtil.isEmpty(UcaInfoQry.qryGrpInfoByCustId(param.getString("CUST_ID")))) return results;
      		    String groupId = UcaInfoQry.qryGrpInfoByCustId(param.getString("CUST_ID")).getString("GROUP_ID", "");
      		    platsvc.put("BIZ_CODE", getNewBusiCode(bizCode, groupId));
            }
            
            if(IDataUtil.isNotEmpty(platsvc)){
                Set<String> propNames = platsvc.keySet();
                for(String key : propNames)
                {
                    String attrCode = platsvc.getString(key);
                    IData attrItem = new DataMap();
                    attrItem.put("DATA_VAL", attrCode);
                    results.put(key, attrItem);
                }
            }
        }
        
        logger.debug("==============================前台传入数据{}", results);
        
        return results;	
	}
	
	public static IData queryAdcYiKaTongParamForCrtUsInit(IData input) throws Exception{
		IData results = new DataMap();
		IData method  = new DataMap();
		method.put("DATA_VAL", "CrtUs");
		results.put("PRODUCTPARAM_METHOD_NAME", method);
		return results;
	}
	
    public static IData queryAdcYiKaTongParamForChgUsInit(IData input) throws Exception{
    	IData results = new DataMap();
    	IData method  = new DataMap();
		method.put("DATA_VAL", "ChgUs");
		results.put("PRODUCTPARAM_METHOD_NAME", method);

        String grpUserId = input.getString("USER_ID", "");
        String rsrvValueCode = "OFEE";
        IDataset userOther = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(grpUserId, rsrvValueCode);
        if (IDataUtil.isNotEmpty(userOther))
        {
        	IData hireFee = new DataMap();
        	hireFee.put("DATA_VAL", userOther.getData(0).getString("RSRV_VALUE"));
        	results.put("NOTIN_HIRE_FEE", hireFee);
        	IData feeCycle = new DataMap();
        	feeCycle.put("DATA_VAL", userOther.getData(0).getString("RSRV_STR1"));
        	results.put("NOTIN_FEE_CYCLE", feeCycle);
        }

        return results;
	}
    
    public static IData queryAdcParamAttrForCrtMbInit(IData input) throws Exception{
    	IData results = new DataMap();
    	
    	String userIdA = input.getString("EC_USER_ID");
    	String serviceId = input.getString("OFFER_CODE");
    	IData platsvcparam = getUserAPlatSvcParam(userIdA, serviceId);
    	IData memplatsvc = new DataMap();
    	memplatsvc.put("MODIFY_TAG", "0");// 标识新增
        memplatsvc.put("BIZ_CODE", platsvcparam.getString("BIZ_CODE"));// 业务代码
        memplatsvc.put("BIZ_NAME", platsvcparam.getString("BIZ_NAME"));// 业务名称
        memplatsvc.put("BIZ_IN_CODE", platsvcparam.getString("BIZ_IN_CODE"));// 业务接入号
        memplatsvc.put("BIZ_IN_CODE_A", platsvcparam.getString("SI_BASE_IN_CODE_A"));// 业务接入号属性
        memplatsvc.put("BIZ_ATTR", platsvcparam.getString("BIZ_ATTR"));// 业务属性
        memplatsvc.put("EXPECT_TIME", SysDateMgr.getSysTime());// 用户期望生效时间
        memplatsvc.put("PLAT_SYNC_STATE", "1");// 用户服务状态
        memplatsvc.put("SERVICE_ID", serviceId);
        memplatsvc.put("OPER_STATE", "01");
        memplatsvc.put("GRP_PLAT_SYNC_STATE", platsvcparam.getString("PLAT_SYNC_STATE", ""));
        Set<String> param = memplatsvc.keySet();
        for(String key:param){
        	String attrCode = memplatsvc.getString(key);
        	IData attrParam = new DataMap();
        	attrParam.put("DATA_VAL", attrCode);
        	results.put(key, attrParam);
        }
        
    	return results;
    }
    
    public static IData queryAdcParamAttrForChgMbInit(IData input) throws Exception{
    	IData results = new DataMap();
    	
    	String userId = input.getString("USER_ID", "");
        String userIdA = input.getString("EC_USER_ID", "");
        String serviceId = input.getString("OFFER_CODE", "");

        IData memplatsvc = new DataMap();
        // 取GRP_MEB_PLATSVC平台服务表已经存在的参数
        IDataset mebPlatsvcset = UserGrpMebPlatSvcInfoQry.getMemPlatSvc(userId, userIdA, serviceId);

        // 取黑白名单已经存在的参数
        IDataset blackwhiteset = UserBlackWhiteInfoQry.getBlackWhitedatauserIdUserIdaSvcid(userId, userIdA, serviceId);

        // 取集团用户GRP_Platsvc数据
        IData platsvcparam = getUserAPlatSvcParam(userIdA, serviceId);
        
        if (IDataUtil.isNotEmpty(mebPlatsvcset) && IDataUtil.isNotEmpty(blackwhiteset)){
        IData blackwhite = blackwhiteset.getData(0);
        memplatsvc.put("MODIFY_TAG", "2");// 标识修改
        memplatsvc.put("BIZ_CODE", blackwhite.getString("BIZ_CODE", ""));// 业务代码
        memplatsvc.put("BIZ_NAME", blackwhite.getString("BIZ_NAME", ""));// 业务名称
        memplatsvc.put("BIZ_IN_CODE", blackwhite.getString("BIZ_IN_CODE", ""));// 业务接入号
        memplatsvc.put("BIZ_IN_CODE_A", platsvcparam.getString("SI_BASE_IN_CODE_A"));// 业务接入号属性
        memplatsvc.put("BIZ_ATTR", platsvcparam.getString("BIZ_ATTR", ""));// 业务属性
        memplatsvc.put("EXPECT_TIME", blackwhite.getString("EXPECT_TIME", ""));// 用户期望生效时间
        String strSynState = blackwhite.getString("PLAT_SYNC_STATE", "");// 当前服务状态 ;
        String openState = blackwhite.getString("OPER_STATE");

        if (StringUtils.isBlank(strSynState)) // 将来要报异常，表示未得到更新结果
        {
            strSynState = "1";
        }
        memplatsvc.put("PLAT_SYNC_STATE", strSynState);// 用户服务状态

        if ("04".equals(openState))// "OPER_STATE":"04" 表示暂停
        {
            memplatsvc.put("PLAT_SYNC_STATE", "P");
        }

        memplatsvc.put("SERVICE_ID", serviceId);
        memplatsvc.put("OPER_STATE", openState);
        memplatsvc.put("GRP_PLAT_SYNC_STATE", platsvcparam.getString("PLAT_SYNC_STATE", ""));
        
        Set<String> param = memplatsvc.keySet();
        for(String key:param){
        	String attrCode = memplatsvc.getString(key);
        	IData attrItem = new DataMap();
        	attrItem.put("DATA_VAL", attrCode);
        	results.put(key, attrItem);
        }
        }else{
        	results = queryAdcParamAttrForCrtMbInit(input);
        }
    	
    	return results;
    }
    
    public static IData getUserAPlatSvcParam(String userIdA, String serviceId) throws Exception
    {
        IData platsvcparam = new DataMap();

        IDataset tempLists = AttrBizInfoQry.getBizAttrByDynamic(serviceId, "S", "Obver", serviceId, null);

        if (IDataUtil.isEmpty(tempLists))
        {
            platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(userIdA, serviceId);// 取平集团用户参数
        }
        else
        {
            for (int i = 0; i != tempLists.size(); ++i)
            {
                String service_id = tempLists.getData(i).getString("ATTR_VALUE");
                platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(userIdA, service_id);
                // 取平集团用户参数
                if (!platsvcparam.isEmpty())
                {
                    break;
                }
            }
        }
        return platsvcparam;
    }
    
    public static IData queryXxtParamForCrtMbInit(IData param) throws Exception{
    
    	IData results = new DataMap();
    	
    	String mebUserId = param.getString("USER_ID");
    	String mebSn = UcaInfoQry.qryUserInfoByUserId(mebUserId).getString("SERIAL_NUMBER");
    	
    	IData xxtParam = new DataMap();
    	xxtParam.put("NOTIN_SERIAL_NUMBER", mebSn);
    	xxtParam.put("GRP_USER_ID", param.getString("EC_USER_ID"));
    	xxtParam.put("GROUP_ID", param.getString("GROUP_ID"));
    	
    	//配置一个隐藏域,将表格数据存入
    	
    	Set<String> xxt = xxtParam.keySet();
        for(String key:xxt){
        	String attrCode = xxtParam.getString(key);
        	IData attrItem = new DataMap();
        	attrItem.put("DATA_VAL", attrCode);
        	results.put(key, attrItem);
        }
    	
    	return results;
    }
    
    public static IData queryXxtParamForChgMbInit(IData param) throws Exception{
        return null;
    }
    
}
