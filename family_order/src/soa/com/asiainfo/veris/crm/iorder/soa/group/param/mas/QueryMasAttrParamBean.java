package com.asiainfo.veris.crm.iorder.soa.group.param.mas;

import java.util.Set;

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
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class QueryMasAttrParamBean extends QueryAttrParamBean{

	public static IData queryMasParamAttrForCrtUsInit(IData param) throws Exception{

        IData result = new DataMap();
        String offerCode= param.getString("PRODUCT_ID"); 
        
        IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "S", "0", null);
        //IData attrItemA = new DataMap();

        if (IDataUtil.isNotEmpty(dataset))
        {
        	result = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "ATTR_INIT_VALUE", "DATA_VAL");
            
        }  

//        if(IDataUtil.isNotEmpty(attrItemA)){
//            Set<String> propNames = attrItemA.keySet();
//            for(String key : propNames)
//            {
//                IData attrCodeInfo = attrItemA.getData(key);
//                IData attrItem = new DataMap();
//                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
//                if(IDataUtil.isNotEmpty(workTypeCodeInfo)){
//                    attrItem.put("DATA_VAL", workTypeCodeInfo);
//                    result.put(key, attrItem);
//                }else{
//                    String  attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
//                    attrItem.put("DATA_VAL", attrItemValue);
//                    result.put(key, attrItem); 
//                }
//            }
//        }
        
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
        
        String custId = param.getString("CUST_ID", "");
        if (StringUtils.isNotEmpty(custId))
        {
            IData data = UcaInfoQry.qryGrpInfoByCustId(custId);
            
            //
            IData exchangeData1 = new DataMap();
            exchangeData1.put("DATA_VAL", data.getString("GROUP_MGR_SN", "")); // 管理员号码 
            result.put("ADMIN_NUM", exchangeData1);
        }
        
        //湖南mas逻辑和adc业务以及其他地区不一样,在配置中 服务代码长度是空的,所以要做处理,防止报空指针,暂时就直接不处理,就放个空
        // 生成服务代码尾
//        String svrlength = result.getData("C_LENGTH").getString("DATA_VAL",""); // 服务代码尾号长度
//        String svrCodeTail = "";// 生成服务代码尾
//        if (StringUtils.isNotBlank(svrlength))
//        {
//            svrCodeTail = getBizCodeTail(svrlength);
//        }
//        IData svrCodeEnd = new DataMap();
//        svrCodeEnd.put("DATA_VAL", svrCodeTail);
//        result.put("SVR_CODE_END", svrCodeEnd);
        
        //还不确定是否需要platsvc.put("INNER_SVC_TAG", "NEW");// 将服务是否是新增的标志，带出去

        IData isDisplay = new DataMap();
        isDisplay.put("DATA_VAL", StaticUtil.getStaticValue("ADC_SPEC_AREA_DISPLAY", offerCode));  
        result.put("IS_SPEC_AREA_DISPLAY",isDisplay);
        
        //遍历列表，找到业务编码字段进行重新编码
        String bizCode = result.getData("BIZ_CODE").getString("DATA_VAL", "");
      	if (StringUtils.isNotBlank(bizCode)
      		    && StringUtils.isNotBlank(param.getString("CUST_ID")))
              {
      		    String groupId = UcaInfoQry.qryGrpInfoByCustId(param.getString("CUST_ID")).getString("GROUP_ID", "");
      		    IData bizParam = new DataMap();
      		    bizParam.put("DATA_VAL", getNewBusiCode(bizCode, groupId));
      		    result.put("BIZ_CODE",bizParam);
              }

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
	
	public static IData queryMasParamAttrForChgUsInit(IData param) throws Exception{

        IData results = new DataMap();
		
		//先尝试取已有服务
		String user_id = param.getString("USER_ID");
		String service_id = param.getString("PRODUCT_ID");
		boolean createsvc = true;// 该服务是新增 还是修改标识 默认为新增
        IDataset usersvc = UserSvcInfoQry.getUserSvcBycon(user_id, "-1", "0", "0", service_id);//package_id在svc表里面已经删掉了,所以随便写个值不影响
        if(IDataUtil.isNotEmpty(usersvc))
        		createsvc = false;
        
        if(createsvc){//选择的服务为新增服务
        	queryMasParamAttrForCrtUsInit(param);
        }else{//选择的服务为已订购服务

        	IData platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(user_id, service_id);// 取平台服务表已经存在的参数

            // 查询在TF_F_USER_GRP_PLATSVC表里面存不下 然后存在attr纵表内的实例属性
            IDataset userattrdataset = UserAttrInfoQry.getuserAttrBySvcId(user_id, service_id);
            IData userattrdata = IDataUtil.hTable2StdSTable(userattrdataset, "ATTR_CODE", "ATTR_VALUE");
            platsvcparam.putAll(userattrdata);
            IData platsvc = platsvcparam;
            platsvc.put("MODIFY_TAG", "2"); // 标识修改 即已经存在的服务信息
            platsvc.put("SERVICE_ID", service_id); // 集团服务ID
            platsvc.put("INNER_SVC_TAG", "OLD");// 将服务是修改标志，带出去

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
            
            platsvc.put("IS_MAS_SERV", platsvc.getString("RSRV_STR5", ""));//是否是mas服务器新增

            platsvc.put("IS_SPEC_AREA_DISPLAY", StaticUtil.getStaticValue("MAS_SPEC_AREA_DISPLAY", service_id));
            
            String staffId = getVisit().getStaffId();
            boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, "GROUPMENBER_MODIFY_PRV");
            platsvc.put("HASMODIFYPRV", bool);
            
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
        
        return results;	
	}
	
	public static IData queryMasParamAttrForCrtMbInit(IData input) throws Exception{
    	IData results = new DataMap();
    	
    	String userIdA = input.getString("EC_USER_ID");
    	String serviceId = input.getString("OFFER_CODE");
    	IData platsvcparam = getUserAPlatSvcParam(userIdA, serviceId);
    	IData memplatsvc = new DataMap();
    	memplatsvc.put("MODIFY_TAG", "0");// 标识新增
        memplatsvc.put("BIZ_CODE", platsvcparam.getString("BIZ_CODE"));// 业务代码
        memplatsvc.put("BIZ_NAME", platsvcparam.getString("BIZ_NAME"));// 业务名称
        memplatsvc.put("BIZ_IN_CODE", platsvcparam.getString("BIZ_IN_CODE"));// 业务接入号
        memplatsvc.put("BIZ_IN_CODE_A", platsvcparam.getString("EC_BASE_IN_CODE_A"));// 业务接入号属性
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
    
    public static IData queryMasParamAttrForChgMbInit(IData input) throws Exception{
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
        memplatsvc.put("BIZ_IN_CODE_A", platsvcparam.getString("EC_BASE_IN_CODE_A"));// 业务接入号属性
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
        	results = queryMasParamAttrForCrtMbInit(input);
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
}
