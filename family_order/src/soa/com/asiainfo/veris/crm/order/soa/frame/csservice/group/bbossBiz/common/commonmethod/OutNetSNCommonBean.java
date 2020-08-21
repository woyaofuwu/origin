
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.TimeUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createRecepHallMember.SaveRelBbOrderBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createRecepHallMember.SaveRelBbTradeBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;
/**
 * @description 该类用于放置处理网外号码公用方法
 * @author 
 * @date 
 */
public class OutNetSNCommonBean
{

	  /**
     * @description 根据成员手机号判断该成员是否属于网外号码
     * @author 
     * @date 
     */
	public static boolean isOutNetSn(String memSerialNumber) throws Exception
    {
        // 1- 定义返回结果
        boolean isOutNetSn = false;

        // 2- 根据手机号码查询当前的路由下是否存在有成员用户信息
        IDataset memberUserInfoList = UserGrpInfoQry.getMemberUserInfoBySn(memSerialNumber);
        if (null != memberUserInfoList && memberUserInfoList.size() > 0)
        {
            return isOutNetSn;
        }
        
        // 3- 如果为机顶盒SN，则直接当作网外号码处理(做虚拟用户开户) 
        if(StringUtils.isNotEmpty(memSerialNumber) && memSerialNumber.length()==32){
            return true;
        }

        // 4- 判断是否为本省网内号码，如果为本省网内号码并且没有有效用户信息，直接抛错
        String prov_code = BizEnv.getEnvString("crm.grpcorp.provincecode");
        IData msisdnInfo = MsisdnInfoQry.getMsisonBySerialnumber(memSerialNumber, prov_code, "1", null);
        if (IDataUtil.isNotEmpty(msisdnInfo) && IDataUtil.isEmpty(memberUserInfoList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_842);
        }

        // 5- 循环各个地州库，查询成员用户信息是否存在
        String[] connNames = Route.getAllCrmDb();
        if (connNames == null)
        {
            CSAppException.apperr(DbException.CRM_DB_9);
        }
        memberUserInfoList = searchMemUserInfoFromCrm(memSerialNumber, connNames);
        if (null == memberUserInfoList || memberUserInfoList.size() == 0)
        {
            isOutNetSn = true;
        }

        // 6- 返回结果
        return isOutNetSn;
    }
    
    /**
     * @description 根据网外成员手机号到各CRM库查找三户信息
     * @author
     * @date 
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
    
	public static void dealJKDTOutNetSN(IData mebOpenOrderMap) throws Exception{
                  	
        
		String productSpecCode= mebOpenOrderMap.getString("PRODUCT_SPEC_CODE","");
    			
		//根据BBOSS_MEB_CRT_REL和产品全网编码查static表，是否需要建立rel关系
		IData staticValue = StaticInfoQry.getStaticInfoByTypeIdDataId("BBOSS_MEB_CRT_REL",productSpecCode); 
		
		//建立bb关系
		if(staticValue!= null){
			
			crtRelInfo(mebOpenOrderMap);
		}  
    }
	
	/**
    * 生成工单
    * @param mebRequest
    * @throws Exception
    */
   public static void crtRelInfo(IData  mebOpenOrderMap) throws Exception
   { 
	   String oSubTypeID = mebOpenOrderMap.getString("ACTION");
	   String serialNumber = mebOpenOrderMap.getString("SERIAL_NUMBER");
	   SaveRelBbOrderBean bean = new SaveRelBbOrderBean();
	   
	   if("1".equals(oSubTypeID)){
		   //成员新增
		   IData returnVal = getJKDTMerchUserIdAndProductUserId(mebOpenOrderMap);
		   String merchUserId = returnVal.getString("MERCH_USER_ID","");//商品用户ID
		   String productUserId = returnVal.getString("PRODUCT_USER_ID","");//产品用户ID
		   IData crtOrderInfo = new DataMap();
		   if(StringUtils.isNotEmpty(merchUserId)){
			   IData merchUUInfo = new DataMap();
			   merchUUInfo.put("SERIAL_NUMBER",serialNumber);//成员号码
			   merchUUInfo.put("USER_ID", merchUserId);//集团产商品userid
			   merchUUInfo.put("TRADE_TYPE_CODE", "2352");
			   merchUUInfo.put("MODIFY_TAG", "0");
			   crtOrderInfo.put("MERCH_INFO", merchUUInfo);
			   
		   }
		   
		   IDataset orderUUInfos = new DatasetList();
		   IData orderUUInfo = new DataMap();
		   orderUUInfo.put("SERIAL_NUMBER",serialNumber);//成员号码
		   orderUUInfo.put("USER_ID", productUserId);//集团产商品userid
		   orderUUInfo.put("TRADE_TYPE_CODE", "2352");
		   orderUUInfo.put("MODIFY_TAG", "0");
		   orderUUInfos.add(orderUUInfo);
		   crtOrderInfo.put("ORDER_INFO", orderUUInfos);
		   bean.crtOrder(crtOrderInfo);

	   }else if("0".equals(oSubTypeID))
       {//成员删除
		   IData returnVal = getJKDTMerchUserIdAndProductUserId(mebOpenOrderMap);
		   String merchUserId = returnVal.getString("MERCH_USER_ID","");
		   String productUserId = returnVal.getString("PRODUCT_USER_ID","");
		   IData crtOrderInfo = new DataMap();
		   if(StringUtils.isNotEmpty(merchUserId)){
			   IData merchUUInfo = new DataMap();
			   merchUUInfo.put("SERIAL_NUMBER",serialNumber);//成员号码
			   merchUUInfo.put("USER_ID", merchUserId);//集团产商品userid
			   merchUUInfo.put("TRADE_TYPE_CODE", "2351");
			   merchUUInfo.put("MODIFY_TAG", "1");
			   crtOrderInfo.put("MERCH_INFO", merchUUInfo);
		   }
		   
		   IDataset orderUUInfos = new DatasetList();
		   IData orderUUInfo = new DataMap();
		   orderUUInfo.put("SERIAL_NUMBER",serialNumber);//成员号码
		   orderUUInfo.put("USER_ID", productUserId);//集团产商品userid
		   orderUUInfo.put("TRADE_TYPE_CODE", "2351");
		   orderUUInfo.put("MODIFY_TAG", "1");
		   orderUUInfos.add(orderUUInfo);
		   crtOrderInfo.put("ORDER_INFO", orderUUInfos);
		   bean.crtOrder(crtOrderInfo);
    	   
       }

       
   }
   public static void recordMebLog(IData data,IData result) throws Exception{
	  
	   IData OutNetSNInfo = new DataMap();
	   
	   OutNetSNInfo.put("PKGSEQ", data.getString("pkgSeq"));
	   OutNetSNInfo.put("COMFIRMTYPE", data.getString("ACTION"));
	   OutNetSNInfo.put("HOST_COMPANY", "");
	   OutNetSNInfo.put("PRODUCT_ORDER_ID",data.getString("PRODUCT_ORDER_NUMBER"));
	   OutNetSNInfo.put("PRODUCT_OFFER_ID",data.getString("PRODUCTID"));
	   OutNetSNInfo.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
	   OutNetSNInfo.put("MEB_TYPE",data.getString("USER_TYPE"));
	   OutNetSNInfo.put("RSP_CODE", result.getString("RSPCODE", ""));
	   OutNetSNInfo.put("RSP_DESC", result.getString("RSPDESC", ""));   
	   OutNetSNInfo.put("BIP_CODE", data.getString("BIPCODE", ""));
	  
       IDataset characterIDInfoList = IDataUtil.getDataset("RSRV_STR11", data);
       if(IDataUtil.isNotEmpty(characterIDInfoList)){
		   //成员扩展属性
		   if(IDataUtil.isNotEmpty(characterIDInfoList.getDataset(0))){	
				//添加属性编码
			   OutNetSNInfo.put("CHARACTER_ID",data.getString("RSRV_STR11",""));
						
				//添加属性名称
			   OutNetSNInfo.put("CHARACTER_NAME",data.getString("RSRV_STR12",""));
				
				//添加属性值
			   OutNetSNInfo.put("CHARACTER_VALUE",data.getString("RSRV_STR13",""));
		   }
       }
	   OutNetSNInfo.put("REMARK","集客大厅成员列表／签约关系同步接口网外号码记录");
	   OutNetSNInfo.put("DEAL_DATE",SysDateMgr.getSysTime());
	   OutNetSNInfo.put("UPDATE_TIME",SysDateMgr.getSysTime());  
	   
	   Dao.insert("TF_F_ECRECEP_ORDER_NOTE", OutNetSNInfo,Route.CONN_CRM_CG);
   }
   
   public static void recordOutNetInfo(IData map) throws Exception{

		//定义信息对象
		IData OutNetInfo = new DataMap();
		
		//添加主键编号
		String seqId = TimeUtil.getSysDate("yyyyMMdd", true)+SeqMgr.getXmlInfoId();
		OutNetInfo.put("SEQ_ID", seqId);
		
		//添加BIPCODE
		String bipcode = map.getString("BIPCODE");
		if(StringUtils.isEmpty(bipcode)){
			bipcode=map.getString("KIND_ID");
		}
		OutNetInfo.put("BIPCODE", bipcode);
		
		//获取TRANDS_IDO
		String transIdo = map.getString("TRANSIDO");
		if(StringUtils.isNotEmpty(transIdo)){
			OutNetInfo.put("TRANDS_IDO", transIdo);
		}else{
			OutNetInfo.put("TRANDS_IDO", map.getString("IBSYSID"));//文件接口没有TRANDS_IDO 只有IBSYSID；
		}
		//获取手机号码
		String serialNumber = map.getString("SERIAL_NUMBER");
		OutNetInfo.put("SERIAL_NUMBER", serialNumber);
		
		//获取产品订购关系编码
		String productOfferId = map.getString("PRODUCTID");
		OutNetInfo.put("PRODUCT_OFFER_ID", productOfferId);
		IDataset UserEcrecepProductInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);
		if(IDataUtil.isNotEmpty(UserEcrecepProductInfoList)){
			String ecPoUserID = UserEcrecepProductInfoList.getData(0).getString("USER_ID");
			OutNetInfo.put("PO_USER_ID", ecPoUserID);//集团产品用户编码
			
			IData productUserInfo = UserEcrecepProductInfoList.getData(0);
			// 获取商品关系类型编码
	        String merchSpecCode = productUserInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
	        String merchId = GrpCommonBean.merchJKDTToProduct(merchSpecCode, 0, null);
	        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
	        
	        // 根据产品用户编号和商品关系类型编码查询商产品用户关系
	        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(ecPoUserID, merchRelationTypeCode, Route.CONN_CRM_CG);
	        if (IDataUtil.isNotEmpty(relaBBInfoList))
	        {
	        	String merchUserId = relaBBInfoList.getData(0).getString("USER_ID_A");
	        	OutNetInfo.put("MERCH_USER_ID", merchUserId);//集团商品用户编码	        	
	        }
	        			
		}
		//归档或者文件接口的成员开通，类别为15，如果实时接口成员开通，类别为16
		OutNetInfo.put("XML_ACTION", "15");
		String returnFlag = map.getString("RETURN_FLAG_KT", "");
		String offerId = map.getString("ORDER_NO", "");
		if("".equals(returnFlag) && !"".equals(offerId)){
			OutNetInfo.put("XML_ACTION", "16");
		}
		String productOrderNumber = map.getString("ORDER_NO");
        OutNetInfo.put("PRODUCT_ORDER_NUMBER", productOrderNumber);						
		
		//添加落地时间
		OutNetInfo.put("LOCATE_TIME", SysDateMgr.getSysTime());
		
		//添加报文的处理时间
		OutNetInfo.put("VALID_DATE", SysDateMgr.getSysTime());
		
		OutNetInfo.put("EXPIRE_DATE", SysDateMgr.END_DATE_FOREVER);
  
        // 分割报文串，分别保存到对应的字段中
		String xmlContent = map.toString();
		String[] xmlContentArr = MebCommonBean.splitStringByBytes(xmlContent,4000);
		for(int i=0;i< xmlContentArr.length;i++){
			if(StringUtils.isNotBlank(xmlContentArr[i])){
				OutNetInfo.put("MESSAGE"+(i+1), xmlContentArr[i]);
			}
		}
		if(!OutNetInfo.containsKey("MESSAGE2"))
        {
			OutNetInfo.put("MESSAGE2", "");
        }
        if(!OutNetInfo.containsKey("MESSAGE3"))
        {
        	OutNetInfo.put("MESSAGE3", "");
        }if(!OutNetInfo.containsKey("MESSAGE4"))
        {
        	OutNetInfo.put("MESSAGE4", "");
        }if(!OutNetInfo.containsKey("MESSAGE5"))
        {
        	OutNetInfo.put("MESSAGE5", "");        	
        }
		//调用方法保存
		Dao.delete("TF_F_JKDT_NETMEB", OutNetInfo, Route.CONN_CRM_CEN);
		Dao.insert("TF_F_JKDT_NETMEB", OutNetInfo,Route.CONN_CRM_CEN);
			
   }
   /*
    * @description 集客大厅获取商产品用户编号
    * @date 
    */
   protected static IData getJKDTMerchUserIdAndProductUserId(IData map) throws Exception
   {
       // 1- 定义商品用户编号
       String merchUserId = "";

       // 2- 根据产品订购关系编码查找产品用户信息
       String productOfferId = map.getString("PRODUCTID", "");

       IDataset productUserInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);

       if (productUserInfoList == null || productUserInfoList.size() == 0)
       {
           CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
       }
       IData productUserInfo = productUserInfoList.getData(0);

       // 3- 获取商品关系类型编码
       String merchSpecCode = productUserInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
       String merchId = GrpCommonBean.merchJKDTToProduct(merchSpecCode, 0, null);
       String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);

       // 4- 获取产品用户编号
       String productUserId = productUserInfo.getString("USER_ID");

       // 5- 根据产品用户编号和商品关系类型编码查询商产品用户关系
       IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(productUserId, merchRelationTypeCode, Route.CONN_CRM_CG);
       if (IDataUtil.isEmpty(relaBBInfoList))
       {
           CSAppException.apperr(ProductException.CRM_PRODUCT_215);
       }
       merchUserId = relaBBInfoList.getData(0).getString("USER_ID_A");

       // 6- 返回商产品用户编号
       IData userIdMap = new DataMap();
       userIdMap.put("MERCH_USER_ID", merchUserId);//商品userid
       userIdMap.put("PRODUCT_USER_ID", productUserId);//产品userid
       return userIdMap;
   }
   
}
