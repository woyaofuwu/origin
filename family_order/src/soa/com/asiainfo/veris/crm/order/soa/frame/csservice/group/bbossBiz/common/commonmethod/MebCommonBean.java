  
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @descripiton 该类用于放置BBOSS成员公用方法
 * @author xunyl
 * @date 2013-09-10
 */
public class MebCommonBean
{

    /*
     * @description 获取成员用户编号
     * @author xunyl
     * @date 2013-07-11
     */
    public static String getMemberUserId(IData map) throws Exception
    {
        // 1- 定义成员用户编号
        String memUserId = "";

        // 2- 获取成员手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER");

        // 3- 根据成员手机号获取成员用户信息
        IDataset memberUserInfoList = UserGrpInfoQry.getMemberUserInfoBySn(memSerialNumber);

        // 4- 如果成员用户信息不存在(无论是移动号还是铁通号只要是省内号码都因该抛出异常)
        if (memberUserInfoList == null || memberUserInfoList.size() == 0)
        {
            // 4-1 如果为机顶盒SN，则直接当作网外号码处理(做虚拟用户开户)
            if((StringUtils.isNotEmpty(memSerialNumber) && memSerialNumber.length()==32) || "0102001".equals(map.getString("MERCH_SPEC_CODE"))){
                return memUserId;
            }

            // 4-2 判断成员号码是否为本地铁通号码，如果为省内铁通号码则抛出异常
            if ("0".equals(memSerialNumber.substring(0, 1)) && ParamInfoQry.isExistLocalSerialnumber(memSerialNumber))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_211, IntfField.IAGWGrpMemBizRet.ERR39[0], "该成员号码[", memSerialNumber);
            }
           
            //add by xuzh5 REQ201905210032  关于优化省行业网关云MAS业务异网号码成员添加流程 2019-7-5 16:25:47
            String PRODUCT_SPEC_CODE= map.getString("MAS_KEY");
            IData msisdninfo = MsisdnInfoQry.getCrmMsisonBySerialnumber(memSerialNumber);
            if ("898".equals(msisdninfo.getString("PROV_CODE")) && !"1".equals(msisdninfo.getString("ASP")) && "".equals(PRODUCT_SPEC_CODE)) {
            	CSAppException.apperr(GrpException.CRM_GRP_850);
            }

            // 4-3 成员号码如果为省内移动号码则抛出异常
            IData result = new DataMap();
            String curProv = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_SEQUID", ProvinceUtil.getProvinceCodeGrpCorp(), CSBizBean.getTradeEparchyCode());
            if(!"898".equals(curProv)){
                result = RouteInfoQry.getMofficeInfoBySn(memSerialNumber); 
            }else if("898".equals(msisdninfo.getString("PROV_CODE"))){
                result =msisdninfo;
            }         
            if (IDataUtil.isNotEmpty(result) && "".equals(PRODUCT_SPEC_CODE))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_321, "该成员号码[", memSerialNumber);
            }

            // 4-4 外省号码(三户资料不存在则建虚拟三户信息，存在则直接用已有的三户信息)
            String[] connNames = Route.getAllCrmDb();
            if (connNames == null)
            {
                CSAppException.apperr(DbException.CRM_DB_9);
            }
            memberUserInfoList = searchMemUserInfoFromCrm(memSerialNumber, connNames);

        }

        // 5- 获取商品用户编号
        if (IDataUtil.isNotEmpty(memberUserInfoList))
        {
            memUserId = memberUserInfoList.getData(0).getString("USER_ID");
        }

        // 5- 返回结果
        return memUserId;
    }

    /*
     * @description 获取台账编号(trade_id)
     * @author xunyl
     * @date 2013-09-12
     */
    public static String getTradeId(IDataset outDataset) throws Exception
    {
        // 1- 定义返回的台账编号
        String tradeId = "";

        // 2- 获取台账编号
        for (int i = 0; i < outDataset.size(); i++)
        {
            IData outData = outDataset.getData(i);
            if (outData.containsKey("BBOSS_TAG"))
            {
                tradeId = outData.getString("TRADE_ID");
            }
        }

        // 3- 返回台账编号
        return tradeId;
    }

    /*
     * @description 登记完工依赖表数据(商品台账不走服务开通，但必须等产品台账完工后，商品台账才完工)
     * @author xunyl
     * @date 2013-09-12
     */
    public static void regTradeLimitInfo(String merchTradeId, String merchpTradeId) throws Exception
    {
        if ("".equals(merchTradeId) || null == merchTradeId || "".equals(merchpTradeId) || null == merchpTradeId)
        {
            return;
        }
        IData tradeLimitInfo = new DataMap();
        tradeLimitInfo.put("TRADE_ID", merchTradeId);
        tradeLimitInfo.put("LIMIT_TRADE_ID", merchpTradeId);
        tradeLimitInfo.put("LIMIT_TYPE", "0");
        tradeLimitInfo.put("STATE", "0");
        tradeLimitInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchTradeId));
        tradeLimitInfo.put("ROUTE_ID", CSBizBean.getUserEparchyCode());
        Dao.insert("TF_B_TRADE_LIMIT", tradeLimitInfo, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /*
     * @description 根据网外成员手机号到各CRM库查找三户信息
     * @auhtor xunyl
     * @date 2013-07-11
     */
    protected static IDataset searchMemUserInfoFromCrm(String memSerialNumber, String[] connNames) throws Exception
    {
        // 1- 定义成员用户信息
        IDataset memUserInfo = new DatasetList();

        // 2- CRM库查找网外成员的三户信息
        for (int i = 0; i < connNames.length; i++)
        {
            String connName = connNames[i];
            if (connName.indexOf("crm") >= 0)
            {
                memUserInfo = UserInfoQry.getUserInfoBySn(memSerialNumber, "0", "06", connName);
                if (null != memUserInfo && memUserInfo.size() != 0)
                {
                    break;
                }
            }
        }

        // 3- 返回成员用户信息
        return memUserInfo;
    }
    
    /*
     * @description 根据网外成员手机号到各CRM库查找三户信息
     * @auhtor xunyl
     * @date 2013-07-11
     */
    public static IDataset searchOuterMemUserInfo(String memSerialNumber) throws Exception
    {
        // 1- 定义成员用户信息
        IDataset memUserInfo = new DatasetList();
        // 2- CRM库查找网外成员的三户信息
        memUserInfo = UserInfoQry.getUserInfoBySn(memSerialNumber, "0", "06", Route.CONN_CRM_CG);
        // 3- 返回成员用户信息
        return memUserInfo;
    }
    
    /**
     * @description 判断成员产品是否需要开通工单，有开通环节的落地归档直接当成功处理
     * @author xunyl
     * @date 2015-05-12
     */
    public static boolean hasMebOrderOpen(String productId)throws Exception{
        //1- 定义返回值
        boolean hasMebOrderOpen = false;
        
        //2- 查询TD_S_STATIC表，有记录代表该业务有成员开通工单环节
        IDataset staticValue = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID", 
                new String[]{ "TYPE_ID", "DATA_ID" }, 
                new String[]{ "BBOSS_MEB_ORDER_OPEN", productId });
        
        //3- 如果有数据说明该业务有开通工单环节
        if(IDataUtil.isNotEmpty(staticValue)){
            hasMebOrderOpen = true;
        }
        
        //4- 返回结果
        return hasMebOrderOpen;
    }
    
    /**
     * @description 判断成员产品在 暂停恢复操作时 是否存在开通环节
     * @author zhangcheng5
     * @date 2019-08-26
     */
    public static boolean hasActionMebOrderOpen(String productId)throws Exception
    {
        //1- 定义返回值
        boolean hasActionMebOrderOpen = false;
        
        //2- 查询TD_S_STATIC表，有记录代表该业务有成员开通工单环节
        IDataset staticValue = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID", 
                new String[]{ "TYPE_ID", "DATA_ID" }, 
                new String[]{ "BBOSS_MEB_OPEN", productId });
        
        //3- 如果有数据说明该业务有开通工单环节
        if(IDataUtil.isNotEmpty(staticValue)){
        	hasActionMebOrderOpen = true;
        }
        
        //4- 返回结果
        return hasActionMebOrderOpen;
    }

    
    /**
     * @description 字符串分割法
     * @author xunyl
     * @date 2015-02-03
     */
    public static String[] splitStringByBytes(String xmlContent,int ibytes)throws Exception {
        String[] xmlContentArr = new String[10];        
        int index =0;
        int flag = 0;
        xmlContentArr[index] = "";
        for(int j=0;j<xmlContent.length();j++)
        {
            flag=flag+String.valueOf(xmlContent.charAt(j)).getBytes().length;
            if(flag<=ibytes){ 
                xmlContentArr[index]=xmlContentArr[index]+String.valueOf(xmlContent.charAt(j)); 
            }else{
                flag = 0;
                index = index +1;
                xmlContentArr[index] = "";
                j = j-1;
            }
            if(index == 10){
                break;
            }
        }
        return xmlContentArr;
    }

    public static String getJKDTMemberUserId(IData map) throws Exception
    {
        // 1- 定义成员用户编号
        String memUserId = "";

        // 2- 获取成员手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER");

        // 3- 根据成员手机号获取成员用户信息
        IDataset memberUserInfoList = UserGrpInfoQry.getMemberUserInfoBySn(memSerialNumber);

        // 4- 如果成员用户信息不存在(无论是移动号还是铁通号只要是省内号码都因该抛出异常)
        if (memberUserInfoList == null || memberUserInfoList.size() == 0)
        {
            // 4-1 如果为机顶盒SN，则直接当作网外号码处理(做虚拟用户开户)
            if(StringUtils.isNotEmpty(memSerialNumber) && memSerialNumber.length()==32){
                return memUserId;
            }
             
            //4-2 如果为双跨融合产品，则不做以下校验
            String productOfferId=map.getString("PRODUCTID", "");
            IDataset productUserInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);
            if(IDataUtil.isNotEmpty(productUserInfoList)){
            	IData productUserInfo = productUserInfoList.getData(0);
            	String merchSpecCode = productUserInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
            	if(StringUtils.equals("0102001", merchSpecCode)||StringUtils.equals("50016", merchSpecCode)){
            		return memUserId;
            	}
            	//4-2.1  add by mawm 2018-05-18 R00160580-云MAS产品功能优化  白名单剔除不设定成员状态限制,直接退除成员
            	String productId=GrpCommonBean.merchJKDTToProduct(merchSpecCode, 0, null);
            	IDataset isDstRemove=AttrBizInfoQry.getBizAttr(productId, "P", "DstMb", "isDstRemove", null);
            	if(IDataUtil.isNotEmpty(isDstRemove)){
            		String productUserId=productUserInfo.getString("USER_ID","");
            		IDataset userInfo=UserEcrecepMebInfoQry.qryEcrecepMebInfoByEcUserIdSn(memSerialNumber,productUserId, productOfferId);
            		if(IDataUtil.isNotEmpty(userInfo)){
            			memUserId=userInfo.getData(0).getString("USER_ID","");
            			map.put("IS_DESTROY_REMOVE", "Y");
            			map.put("REMOVE_MEB_USER_ID", memUserId);
						map.put("EPARCHY_CODE", userInfo.getData(0).getString("EPARCHY_CODE",""));
            			return memUserId;
            		}
            	}
            }
            
            // 4-3 判断成员号码是否为本地铁通号码，如果为省内铁通号码则抛出异常
            if ("0".equals(memSerialNumber.substring(0, 1)) && ParamInfoQry.isExistLocalSerialnumber(memSerialNumber))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_211, IntfField.IAGWGrpMemBizRet.ERR39[0], "该成员号码[", memSerialNumber);
            }

            IData msisdninfo = MsisdnInfoQry.getCrmMsisonBySerialnumber(memSerialNumber);
            if(IDataUtil.isEmpty(msisdninfo)){
            	CSAppException.apperr(GrpException.CRM_GRP_713, "非移动号码，省BOSS不接收归档!");
            }
            if ("898".equals(msisdninfo.getString("PROV_CODE")) && !"1".equals(msisdninfo.getString("ASP"))) {
                CSAppException.apperr(GrpException.CRM_GRP_850);
            }

            // 4-4 成员号码如果为省内移动号码则抛出异常
            IData result = new DataMap();
            String curProv = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_SEQUID", ProvinceUtil.getProvinceCodeGrpCorp(), CSBizBean.getTradeEparchyCode());
            if(!"898".equals(curProv)){
                result = RouteInfoQry.getMofficeInfoBySn(memSerialNumber);
            }else if("898".equals(msisdninfo.getString("PROV_CODE"))){
                result =msisdninfo;
            }
            if (IDataUtil.isNotEmpty(result))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_321, "该成员号码[", memSerialNumber);
            }

            // 4-5 外省号码(三户资料不存在则建虚拟三户信息，存在则直接用已有的三户信息)
            String[] connNames = Route.getAllCrmDb();
            if (connNames == null)
            {
                CSAppException.apperr(DbException.CRM_DB_9);
            }
            memberUserInfoList = searchMemUserInfoFromCrm(memSerialNumber, connNames);

        }

        // 5- 获取商品用户编号
        if (IDataUtil.isNotEmpty(memberUserInfoList))
        {
            memUserId = memberUserInfoList.getData(0).getString("USER_ID");
        }

        // 5- 返回结果
        return memUserId;
    }
    
    /**
     * @description 根据成员手机号判断该成员是否属于网外号码(此处的网外号码是指没有三户信息的网外号)
     * @author daidl
     * @date 2019-1-23
     */
    public static boolean isOutNetSn(String memSerialNumber) throws Exception
    {
        //1- 定义返回结果
        boolean isOutNetSn = false;        
        
        //2- 判断是否为本省网内号码，如果为本省网内号码并且没有有效用户信息，直接抛错
        String prov_code =BizEnv.getEnvString("crm.grpcorp.provincecode");
        IData msisdnInfo = MsisdnInfoQry.getMsisonBySerialnumber(memSerialNumber, prov_code, "1", null);
 
        if (IDataUtil.isNotEmpty(msisdnInfo) ){
        	return isOutNetSn;
        }
        
        //3- 根据手机号码查询当前的路由下是否存在有成员用户信息(网外号码有可能已经存在虚拟三户信息)
        IDataset memberUserInfoList = UserGrpInfoQry.getMemberUserInfoBySn(memSerialNumber);


        if (null != memberUserInfoList && memberUserInfoList.size() > 0)
        {
            return isOutNetSn;
        }     

        //4- 循环各个地州库，查询成员用户信息是否存在
        String[] connNames = Route.getAllCrmDb();
        if (connNames == null)
        {
            CSAppException.apperr(DbException.CRM_DB_9);
        }
        memberUserInfoList = searchMemUserInfoFromCrm(memSerialNumber, connNames);
    	
        if (IDataUtil.isEmpty(memberUserInfoList))
        {
            isOutNetSn = true;
        }

        //5- 返回结果
        return isOutNetSn;
    }
    
    /**
     * 判断要处理的网外号码，并生成BB关系的台账  daidl
     * @param antiIntfFlag
     * @param map
     * @return
     * @throws Exception
     */
    public static IDataset crtOutNetData(String antiIntfFlag,IData map) throws Exception
    {
    	String productSpecCode="";
    	IDataset ret=new DatasetList();
    	if("1".equals(antiIntfFlag)){//反向
    		String productOfferId = map.getString("PRODUCTID", "");
    		//集客大厅
    		IDataset UserEcrecepProductInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);
    		IDataset  productUserInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
    		if(IDataUtil.isNotEmpty(UserEcrecepProductInfoList)){//集客大厅处理
    			productSpecCode = UserEcrecepProductInfoList.getData(0).getString("PRODUCT_SPEC_CODE","");
    		}else if(IDataUtil.isNotEmpty(productUserInfoList)){//BBOSS处理
    			productSpecCode = productUserInfoList.getData(0).getString("PRODUCT_SPEC_CODE","");
    		}else{
    			 CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);	
    		}
    	}else{
    		String productId="";
    		if("0".equals(map.getString("ACTION", ""))){//删除
    			String userId = map.getString("USER_ID");
    			productId = GrpCommonBean.getProductIdByUserId(userId);
    		}else{
    			IData bbossData = map.getData("BBOSS_INFO");
    	    	// 获取产品信息
    	    	IDataset productInfoList = bbossData.getDataset("PRODUCT_INFO_LIST");
    	    	productId = productInfoList.getData(0).getString("PRODUCT_ID"); 
    		}
    		productSpecCode=GrpCommonBean.productToMerch(productId, 0);	
    	}
    	//需要处理外省号码不生成虚拟三户的产品，配置下面静态参数
    	IData staticValue = StaticInfoQry.getStaticInfoByTypeIdDataId("BBOSS_MEB_CRT_REL",productSpecCode); 
    	

    	if(IDataUtil.isNotEmpty(staticValue)&&MebCommonBean.isOutNetSn(map.getString("SERIAL_NUMBER",""))){
    		IData flagMap = new DataMap();
    		flagMap.put("FLAG", true);
    		ret.add(flagMap);
    		if(StringUtils.isNotBlank(map.getString("RECEPTIONHALLMEM"))){
    			OutNetSNCommonBean.recordOutNetInfo(map);//集客大厅记录日志表
    		}
    		//调创建外网号码台账，新增删除处理，变更不需要处理
    		if("1".equals(map.getString("ACTION", ""))||"0".equals(map.getString("ACTION", ""))){
    			OutNetSNCommonBean.crtRelInfo(map);
    		}
    	}
    	return ret;
    }
}
