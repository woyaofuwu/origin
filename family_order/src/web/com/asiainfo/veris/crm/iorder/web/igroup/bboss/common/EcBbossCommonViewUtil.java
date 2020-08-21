package com.asiainfo.veris.crm.iorder.web.igroup.bboss.common;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.veris.crm.staticparam.StaticParamUtil;

/**
 * bboss前台处理公用类
 * @author cy
 *
 */
public class EcBbossCommonViewUtil  {
	
	
	 public final static String CREATE = "0";
	 public final static String DELETE = "1";
	 public final static String UPDATE = "2";
	 public final static String EXIST = "3";

	
	/**
	 * chenyi
	 * 2015-12-18
	 * 根据前台商品操作类型获取当前用户选择的商品销售品操作类型
	 */
	public  static String  getMerchOperCode(IDataset params){
		IDataset offers=params.getData(0).getDataset("OFFERS");
		IData merchinfoData=offers.getData(0).getData("MERCHINFO");
		String opercode=merchinfoData.getString("OPERTYPE");
		if(StringUtils.isEmpty(opercode)){
			opercode=merchinfoData.getString("MERCH_OPER_CODE");
		}
		return opercode;
	//	return  "11";
	}

	 
	/**
	 * chenyi
	 * 2015-12-18
	 * 根据前台商品操作类型获取当前用户选择的商品销售品操作类型
	 */
	public  static String  getMainOperCode(IDataset params){
		//获取当前商品操作类型
		String merchOperCode=getMerchOperCode(params);
		//
		if(EcConstants.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperCode)){//取消当前商品
			return DELETE;
		}if(EcConstants.MERCH_STATUS.MERCH_ADD.getValue().equals(merchOperCode)){//当前商品新增
			return  CREATE;
		}else{
			return EXIST;
		}
		
	}
	
	/**
	 * chenyi
	 * 2015-12-18
	 * 根据前台商品操作类型获取当前ROLE销售品（商品）操作类型
	 * 商品变更组成关系 需要前台确认
	 * @throws Exception 
	 */
	public  static String  getRoleOffOperCode(IDataset params) throws Exception{
		String merchOperCode=getMerchOperCode(params);
		if(EcConstants.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperCode)){//取消当前商品
			return DELETE;
		}else if(EcConstants.MERCH_STATUS.MERCH_ADD.getValue().equals(merchOperCode)){//当前商品新增
			return  CREATE;
		}else if(EcConstants.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchOperCode))
		{
			return UPDATE;//变更根据前台操作判断是新增子产品还是删除子产品
		}else{
		    return  EXIST;
		}
	}
	/**
	 * chenyi
	 * 获取当前主销售品的bustyPE 如果是变更组成关系，需要从前台取操作类型
	 * @return
	 * @throws Exception
	 */
	 public  static String getMainBuisType(IDataset params,String offerid) throws Exception {/*
			//获取当前商品操作类型
			String merchOperCode=getMainOperCode(params);
		    String buisType = "";
			String obj="";
			if(DELETE.equals(merchOperCode)){//取消当前商品
				obj=EcConstants.FLOW_ID_EC_DELETE;
			}else if(CREATE.equals(merchOperCode)){//当前商品新增
				obj=EcConstants.FLOW_ID_EC_CREATE;
			}else{
				obj=EcConstants.FLOW_ID_EC_CHANGE;
			}
			IData input=new DataMap();
			input.put("ID", offerid);
		    input.put("ID_TYPE", "P");
		    input.put("OBJ", obj);
		    input.put("CODE", "BUSI_TYPE");
		    IData result = ServiceCaller.call("OC.enterprise.ICfgProdAttrItemQuerySV.queryProdAttrItems", input);
	        
	        if (result != null && DataUtils.isNotEmpty(result.getDataset("DATAS"))) {
	            IData cfg = result.getDataset("DATAS").first();
	            buisType = cfg.getString("VALUE", "");
	        }
	        return buisType;
	 */    return "";
	     }
	
    
     /*
      * chenyi
      * 2015-12-23
      * 根据商品全网操作变么获取产品全网操作编码
      * suboff 子产品操作类型
      * 本地操作类型
      * var ACTION_PASTE = "5"; // 暂停
		var ACTION_CONTINUE = "6"; // 恢复
		var ACTION_PREDESTROY = "7"; // 预取消
		var ACTION_PREDSTBACK = "8"; // 冷冻期恢复

      */
     public  static String getMerchpOperCode(IData merchinfo,String roleOperCode,IData suboff) throws Exception{/*
    	 String merchOperCode=merchinfo.getString("MERCH_OPER_CODE");
    	 String  merchpOperCode="";
    	  if(EcConstants.MERCH_STATUS.MERCH_ADD.getValue().equals(merchOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperCode)&&"6".equals(roleOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_CANCLEPREDESTORY.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue().equals(merchOperCode)||EcConstants.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchOperCode)){
    		  //获取资费节点，由于前台节点没判断是修改本地资费还是全网资费 需要做特殊处理
    		  IDataset subDataset=suboff.getDataset("SUBOFFERS");
    		  IData result=dealPrice(subDataset);
    		  if(IDataUtil.isEmpty(result)){
    	            BizException.bizerr(BizErr.BIZ_ERR_1, "选择资费操作类型,但为变更任何资费,请重新选择操作");
    		  }
    		  boolean isChgPrice=result.getBoolean("ISCHGPRICE");//判断是否变更资费
    	      boolean isMerchpPrice=result.getBoolean("ISMERCHPPRICE");//是否变更的是全网资费
    	      //同时变更商品操作类型
    	      if(isMerchpPrice){
    	    	  
    	    	  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue();
    	    	  merchinfo.put("MERCH_OPER_CODE", EcConstants.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue());

    	      }else{
    	    	  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_LOCALDISCNT.getValue();
    	    	  merchinfo.put("MERCH_OPER_CODE",EcConstants.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue());
    	      }
    		 
    	  }else if(EcConstants.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(merchOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_MODIFY_PROV.getValue().equals(merchOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PROV.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_MODIFY_MEB.getValue().equals(merchOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_MEB.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperCode)&&"5".equals(roleOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_CAMP_ON.getValue().equals(merchOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_CAMP_ON.getValue();
    	  }else if(EcConstants.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperCode)){
    		  merchpOperCode=EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_PREDESTORY.getValue();
    	  }
    	  return merchpOperCode;
     */return null;
         }
     
     
     /**
      * 判断当前产品是否变更资费，且是变更本地资费还是全网资费
      * @param priceDataset
      * @return
     * @throws Exception 
      */
     private static IData  dealPrice(IBizCommon bc,IDataset subDataset) throws Exception{
    	 
    	 boolean isChgPrice=false;//判断是否变更资费
    	 boolean isMerchpPrice=false;//是否变更的是全网资费
    	 IData result =new DataMap();//处理结果
    	 
    	 //1-资费节点为空 没有变更资费
    	 if(IDataUtil.isEmpty(subDataset)){
    		 return result;
    	 }
    	 
    	 //2-判断当前资费节点操作类型
    	 for(int i=0,sizeI=subDataset.size();i<sizeI;i++){
    		 IData subData=subDataset.getData(i);
    		 if(!"D".equals(subData.getString("OFFER_TYPE"))){
    			 
    			 continue;
    		 }
    		 
    		 String  operCode=subData.getString("OPER_CODE");//当前资费操作类型
    		 if("3".equals(operCode)){   //当前节点未变更
    			 isChgPrice=false;
    			 continue;
    		 }
    		 isChgPrice=true;
    		 String pricePlan=subData.getString("OFFER_ID");//当前定价计划id
    		 String merchpPriceId=merchToProduct(bc,pricePlan,2,true);//获取全网资费id
    		 if(StringUtils.isNotEmpty(merchpPriceId)){
    			 isMerchpPrice=true;
    			 break;
    		 }
    	 }
    	
    	 result.put("ISCHGPRICE", isChgPrice);
    	 result.put("ISMERCHPPRICE", isMerchpPrice);
    	 
    	 return result;
     }
     /**
      * 操作类型定义参考opergroupuser.js
      * 判断当前产品是否变更特征规格
      * @param prodDataset
      * @return
      */
     private static boolean  isChangeProdCha(IDataset prodDataset){
    	 boolean  isChangeProdCha=false;
    	 //1-特征规格节点为空 没有特征规格
    	 if(IDataUtil.isEmpty(prodDataset)){
    		 return isChangeProdCha;
    	 }
    	 //2-判断当前特征规格节点操作类型
    	 IData prodchaData=prodDataset.getData(0);
    	 String operCode=prodchaData.getString("OPER_CODE");
    	 if(!"3".equals(operCode)){
    		 isChangeProdCha=true;
    	 }
    	 return isChangeProdCha;
     }
     /**
      * chenyi
      * 判断当前商品是不是预受理
      * @param offerId
      * @return
     * @throws Exception 
      */
     public static  boolean isPreMerchpOffer(IBizCommon bc ,String offerId) throws Exception{
    	 boolean isPre=true;  
 		 IDataset result = CommonViewCall.getAttrsFromAttrBiz(bc,"1","B","AHEAD",offerId);
 		  if (result != null && DataUtils.isNotEmpty(result))
 		  {
 			 isPre=true;
	      }
 		  else
 		  {
	    	 isPre=false;
	      }
 		 return  isPre;
     }
     

    /** liaolc
    * @Title: queryAttrBizInfoByIdTypeObjCode 
    * @Description: 通过ID、ID_TYPE、OBJ、CODE查询CFG_PROD_ATTR_ITEM表
    * @param id
    * @param type
    * @param obj
    * @param code
    * @return
    * @throws Exception  
    * @return IData
    * @throws 
    */
    public static IData queryAttrBizInfoByIdTypeObjCode(IBizCommon bc,String id, String type, String obj, String code) throws Exception
     {
         IData result = new DataMap();
         IData param = new DataMap();
         param.put("ID", id);
         param.put("ID_TYPE", type);
         param.put("ATTR_OBJ", obj);
         param.put("ATTR_CODE", code);
         IDataset configs = CSViewCall.call(bc, "CS.AttrBizInfoQrySVC.getBizAttr", param);
         if (IDataUtil.isNotEmpty(configs)) 
         {
        	 result = configs.getData(0);
		 }
         
         return result;
     }
    
    public static IDataset queryAttrBizInfosByIdTypeObjCode(String id, String idType, String attrObj, String attrCode) throws Exception
    {
        IDataset result = new DatasetList();
        IData input = new DataMap();
        input.put("ID", id);
        input.put("ID_TYPE", idType);
        input.put("ATTR_OBJ", attrObj);
        input.put("ATTR_CODE", attrCode);
        IDataInput inParam = new DataInput();
        inParam.getData().putAll(input);
        IDataOutput output = ServiceFactory.call("CS.AttrBizInfoQrySVC.getBizAttr", inParam);
        IDataset  dataset = output.getData();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result=dataset;
        }
        return result;
    }
    
    public static IDataset queryMerchOperType(IBizCommon bc,String offerCode, String status) throws Exception
    { 
        
        IDataset operTypes = new DatasetList();
        IData opertype = new DataMap();
       //1、如果是预注销状态
       if ("D".equals(status))
        {
            IData temp = new DataMap();
            temp.put("OPER_VALUE", EcConstants.MERCH_STATUS.MERCH_CANCLEPREDESTORY);
            temp.put("OPER_NAME", "冷冻期恢复商品订购");
            operTypes.add(temp);
            if("22000596".equals(offerCode)){
            	IData temp1 = new DataMap();
            	temp1.put("OPER_VALUE", EcConstants.MERCH_STATUS.MERCH_CANCLE.getValue());
                temp1.put("OPER_NAME", "取消商品订购");
                operTypes.add(temp1);
            }
            return operTypes;
        }
        
       // 2、如果是暂停
        if ("N".equals(status)) 
        { 
            opertype = new DataMap();
            opertype.put("OPER_VALUE", EcConstants.MERCH_STATUS.MERCH_CONTINUE);
            opertype.put("OPER_NAME", "恢复");
            operTypes.add(opertype);
            return operTypes;
        }
        
        //3如果是正常状态
        if ("A".equals(status))
        {
            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue());
            opertype.put("OPER_NAME", "修改订购商品组成关系");
            operTypes.add(opertype);

            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue());
            opertype.put("OPER_NAME", "修改商品资费");
            operTypes.add(opertype);

            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue());
            opertype.put("OPER_NAME", "修改商品本地资费");
            operTypes.add(opertype);

            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue());
            opertype.put("OPER_NAME", "修改商品属性");
            operTypes.add(opertype);

            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue());
            opertype.put("OPER_NAME", "商品暂停");
            operTypes.add(opertype);

            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue());
            opertype.put("OPER_NAME", "商品恢复");
            operTypes.add(opertype);
            
            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_MEB.getValue());
            opertype.put("OPER_NAME", "变更成员");
            operTypes.add(opertype);
            
            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PROV.getValue());
            opertype.put("OPER_NAME", "业务开展省新增或删除");
            operTypes.add(opertype);
            
            opertype = new DataMap();
           // opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_ATT_CHANGE.getValue());
            opertype.put("OPER_NAME", "合同变更");
            operTypes.add(opertype);
        }

        if (!"D".equals(status))
        {
            IData inparams = new DataMap();
            inparams.put("ID", "1");
            inparams.put("ID_TYPE", "B");
            inparams.put("ATTR_CODE", offerCode);
            inparams.put("ATTR_OBJ", "PREDESTORY");
            IDataset result = CSViewCall.call(bc, "CS.AttrBizInfoQrySVC.getBizAttr", inparams);
            if (result == null || result.size() == 0)
            {
                opertype = new DataMap();
                opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue());
                opertype.put("OPER_NAME", "取消商品订购");
                operTypes.add(opertype);
            }
            else
            {
                opertype = new DataMap();
                opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue());
                opertype.put("OPER_NAME", "预取消商品订购");
                operTypes.add(opertype);
            }
        }

       /* if ("D".equals(status))
        {
            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue());
            opertype.put("OPER_NAME", "冷冻期恢复商品订购");
            operTypes.add(opertype);

            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue());
            opertype.put("OPER_NAME", "取消商品订购");
            operTypes.add(opertype);
        } */
        
        return operTypes;
    }
    
    public static IDataset queryMebMerchOperType(String offerId, String status) throws Exception
    {
        IDataset operType = new DatasetList();
        // 2、如果是暂停
        if ("N".equals(status)) 
        {
            IData temp = new DataMap();
            temp.put("OPER_VALUE","4");
            temp.put("OPER_NAME", "恢复");
            operType.add(temp);
        }
        
        return operType;
    }
    
    /**
     * @description 省boss商产品规格编码与集团规格编码互换 
     * @author chenyi
     * @date 2015-11-24
     * elementId 需转换元素编码
     * mode  0代表商品转换   1代表产品转换  2代表资费转换
     * isLocalToBboss 需转换元素是否为本地，  true elementID本地转换为全网   false  elementID全网转换本地
     */
    public static String merchToProduct(IBizCommon bc,String elementId, int mode,boolean isLocalToBboss) throws Exception
    {
    	String resultValue="";
        if(StringUtils.isEmpty(elementId)){
        	//抛错  转换元素不存在
        }
        if(isLocalToBboss)
        {
	        //1-本地元素转换全网
	        resultValue=localToBboss(bc,elementId,mode);
        }
        else
        {
            //2-全网元素转换本地	
        	resultValue=bbossToLocal(elementId,mode);
        }
        return resultValue;
    }
    /**
     * chenyi
     * 2015-11-24
     * 本地转全网编码
     * @param elementId
     * @param mode
     * @return
     * @throws Exception 
     */
    private static String  localToBboss(IBizCommon bc,String elementId, int mode) throws Exception
    {
    	String value="";
    	String obj="";
    	String id="1";
    	String idType="B";
    	String code=elementId;
    	if(mode==1||mode==0)
    	{
    		 obj="PRO";
    	}
    	else
    	{
    		 obj="DIS";
    	}
    	IData dcs =queryAttrBizInfoByIdTypeObjCode(bc,id, idType, obj, code);
    	if(IDataUtil.isEmpty(dcs)&&(mode==1||mode==0))
    	{
    		//抛错，没配置
    		CSViewException.apperr(GrpException.CRM_GRP_37,elementId);
    	}
    	if(IDataUtil.isEmpty(dcs)&&(mode==2))
    	{
    		return null;
    	}
    	
    	value = dcs.getString("ATTR_VALUE");

    	return value;
    }
    
    /**
     * chenyi
     * 2015-11-24
     * 全网编码f
     * @param elementId
     * @param mode
     * @return
     * @throws Exception 
     */
    private static String bbossToLocal(String elementId, int mode) throws Exception
    {
    	String code="";
    	String obj="";
    	String id="1";
    	String idType="B";
    	String value=elementId;
    	if(mode==1||mode==0)
    	{
    		 obj="PRO";
    	}
    	else
    	{
    		 obj="DIS";
    	}
/* llc后续修改 后续用到再修改        
    	IDataset dcs =sv.queryProdAttrItemsByVal(id, idType, obj, value,null);
    	if(DataUtils.isEmpty(dcs))
    	{
    		//抛错，没配置
    		BizException.bizerr(CustomerException.CRM_CUSTOMER_40,elementId);
    	}
    	
    	//全网商产品编码一致
    	if(dcs.length>1)
    	{
    		for(int i=0,sizeI=dcs.length;i<sizeI;i++)
    		{
    			String tag=dcs[i].getString(CfgProdAttrItemBO.S_Tag);
    			if(mode==0&&StringUtils.equals("ISmerch",tag ))
    			{
    				code=dcs[i].getString(CfgProdAttrItemBO.S_Code);
				} 
    			if(mode==1)
    			{
    				code=dcs[i].getString(CfgProdAttrItemBO.S_Code);
    			}
    		}
    	}
    	else
    	{
    		code = dcs[0].getString(CfgProdAttrItemBO.S_Code);
    	}
    	*/
    	return code;
    }
    
    /*
     * @description 处理产品计费号
     * @param1 productId指产品编号
     * @param2 serialNumber指初始化生成的虚拟号
     * @param3 paramList该产品对应的参数集
     * @author chenyi
     * @date 2015-12-29
     * @version_1 某些业务对SERIAL_NUMBER需要做特殊处理，例如网信业务和400业务，需要将集团长服务代码和400号码替换掉前台传递的SERIAL_NUMBER
     */
    public static String dealaccess_num(IBizCommon bc,String productId, IDataset paramList) throws Exception
    {
        String access_num="";
        String productSpecCode = merchToProduct(bc,productId,1,true);
        // 根据产品编号从TD_S_STATIC获取相关配置，结果集为空说明无需处理，非空则需要处理
        IData inParam = new DataMap();
        inParam.put("PARA_TYPE","BBOSS_SERIAL_NUMBER");
        inParam.put("ELEMENT_ID", productSpecCode);
        IData  staticValue = null;//ServiceCaller.call("OrderCentre.enterprise.param.IParaSelectEleItemSV.getCompSelectEleItem", inParam);

        if (IDataUtil.isEmpty(staticValue) || IDataUtil.isEmpty(staticValue.getDataset("DATAS")))
        {
            return access_num;
        }
        IDataset staticListVal = staticValue.getDataset("DATAS");
        String codeTypeAlias = staticListVal.getData(0).getString("PARA2");// 参数编号
        for (int i = 0; i < paramList.size(); i++)
        {
            if (codeTypeAlias.equals(paramList.getData(i).getString("CHA_SPEC_ID")))
            {
            	access_num = paramList.getData(i).getString("CHA_VALUE");
                break;
            }
        }
        return access_num;
    }
    
    /** 
    * @Title: getMebOperCode 
    * @Description: 根据成员产品id查询成员变更操作类型
    * @param mebOfferId
    * @return
    * @throws Exception  
    * @return IDataset    
    * @author chenkh
    * @throws 
    */
    public static IDataset getMebOperCode(IBizCommon bc,String mebOfferId,String status) throws Exception
    {
        IDataset operType = new DatasetList();
        // 1、如果是正常状态
        if ("A".equals(status))
        {
            IData mebConfig = queryAttrBizInfoByIdTypeObjCode(bc,mebOfferId, "P", "0", "CHGMEMBEROPTYPE");
            if (IDataUtil.isNotEmpty(mebConfig))
            {
                // 1.2、根据配置放入对应的key：value值
                String[] operTypeStr = mebConfig.getString("VALUE").split(",");
                for (int i = 0; i < operTypeStr.length; i++)
                {
                    IData temp = new DataMap();
                    temp.put("OPER_VALUE", operTypeStr[i]);
                    temp.put("OPER_NAME", StaticParamUtil.getStaticValue("BBOSS_CHGMEB_OPERTYPE", operTypeStr[i]));
                    operType.add(temp);
                }
            }
        }
        // 2、如果是暂停
        else if ("N".equals(status)) 
        {
            IData temp = new DataMap();
            temp.put("OPER_VALUE", EcConstants.BBOSS_MEB_STATUS.MEB_CONTINUE.getValue());
            temp.put("OPER_NAME", "恢复");
            operType.add(temp);
        }
        return operType;
    }

    /**
     * @description 根据商品操作类型编码获取商品操作类型名称
     * @author xunyl
     * @date 2014-07-10
     */
    public static  String getOperNameByOperType(String operType) throws Exception
    {
        String operTypeName = "";
        if (EcConstants.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(operType))
        {
            operTypeName = "修改订购商品组成关系";
        }
        else if (EcConstants.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(operType))
        {
            operTypeName = "修改商品资费";
        }
        else if (EcConstants.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue().equals(operType))
        {
            operTypeName = "修改商品本地资费";
        }
        else if (EcConstants.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(operType))
        {
            operTypeName = "修改商品属性";
        }
        else if (EcConstants.MERCH_STATUS.MERCH_PASTE.getValue().equals(operType))
        {
            operTypeName = "商品暂停";
        }
        else if (EcConstants.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(operType))
        {
            operTypeName = "商品恢复";
        }
        else if (EcConstants.MERCH_STATUS.MERCH_MODIFY_MEB.getValue().equals(operType))
        {
            operTypeName = "变更成员";
        }
        else if (EcConstants.MERCH_STATUS.MERCH_MODIFY_PROV.getValue().equals(operType))
        {
            operTypeName = "业务开展省新增或删除";
        }
		else if (EcConstants.MERCH_STATUS.MERCH_CANCLE.getValue().equals(operType))
		{
			operTypeName = "取消商品订购";
		}
		else if (EcConstants.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(operType))
		{
			operTypeName = "预取消商品订购";
		}
		else if (EcConstants.MERCH_STATUS.MERCH_CONTRACT_CHG.getValue().equals(operType))
		{
			operTypeName = "合同变更";
		}
        return operTypeName;
    }
    
   
    /*
     * @description 管理节点中台帐表的值与资料表的值相互之间进行比比较，获取最新参数值
     * @author xunyl
     * @date 2013-11-14
     */
    protected static IDataset getNewestAttrInfoList(IDataset tradeAttrInfoList, IDataset userAttrInfoList) throws Exception
    {
        IDataset attrInfoList = new DatasetList();

        for (int i = 0; i < tradeAttrInfoList.size(); i++)
        {
            IData tradeAttrInfo = tradeAttrInfoList.getData(i);
            for (int j = 0; j < userAttrInfoList.size(); j++)
            {
                // 用户资料表和台帐表同时出现，表明该参数已经做过修改，显示台帐表数据即可
                IData userAttrDInfo = userAttrInfoList.getData(j);
                if (StringUtils.equals(tradeAttrInfo.getString("CHA_SPEC_ID"), userAttrDInfo.getString("CHA_SPEC_ID")) && StringUtils.equals(tradeAttrInfo.getString("GROUP_ATTR"), userAttrDInfo.getString("GROUP_ATTR")))
                {
                    userAttrInfoList.remove(j);
                    j--;
                }
            }

            // 台帐表状态为DEL，则说明该参数已经被删除，页面无需显示
            String action = tradeAttrInfo.getString("ACTION");
            if (StringUtils.equals("1", action))
            {
                tradeAttrInfoList.remove(i);
                i--;
            }
        }

        attrInfoList.addAll(tradeAttrInfoList);
        attrInfoList.addAll(userAttrInfoList);

        return attrInfoList;
    }

    /**
     * chenyi
     * 处理账户信息
     * @param accountInfo
     * @return
     * @throws Exception
     */
    public static  IData transformAcctInfo(IData accountInfo) throws Exception
    {
        IData acctInfo = new DataMap();
        if(IDataUtil.isEmpty(accountInfo))
        {
            return acctInfo;
        }
        String acctId = accountInfo.getString("ACCT_ID");
        if(StringUtils.isNotBlank(acctId))
        {
            acctInfo.put("ACCT_ID", acctId);
            acctInfo.put("OPER_CODE", PageDataTrans.ACTION_EXITS);
        }
        else
        {
            acctInfo.put("ACCT_NAME", accountInfo.getString("ACCT_NAME"));
            acctInfo.put("ACCT_TYPE", accountInfo.getString("ACCT_TYPE"));
            acctInfo.put("OPER_CODE", PageDataTrans.ACTION_CREATE);
        }
        
        return acctInfo;
    }
    /**
     * 转换MainOffer的信息
     * @param offerInfos
     * @return
     * @throws Exception
     */
    public static  IDataset transformOfferList(IData mainOfferInfo) throws Exception
    {
        IDataset listOfferInfo = new DatasetList();
        String mainOfferId = mainOfferInfo.getString("OFFER_ID");
        String mainOfferInsId = mainOfferInfo.getString("OFFER_INS_ID", "");
        IDataset subOffers = mainOfferInfo.getDataset("SUBOFFERS");
        if(IDataUtil.isNotEmpty(subOffers))
        {
            IDataset subOfferList = buildListOfferInfo(subOffers, mainOfferId, mainOfferInsId);
            listOfferInfo.addAll(subOfferList);
        }
        return listOfferInfo;
    }
	/**
	 * 转换BBOSS，ATTR表数据为 attrCode，attrValue，用于前台显示
	 * @param params
	 * @return
	 */
    public static  IData transAttrInfo(IDataset params)
	{
		IData busi = new DataMap();
		if (IDataUtil.isEmpty(params)) 
		{
			return null;
		}
		for (int i = 0; i < params.size(); i++)
		{
			IData param = params.getData(i);
			String attrCode = param.getString("ATTR_CODE");
			String attrValue = param.getString("ATTR_VALUE");
			String nameAttr = param.getString("RSRV_STR3");
			String groupAttr = param.getString("RSRV_STR4");
			if (StringUtils.isNotEmpty(attrCode))
			{
				IData chaInfo = new DataMap();
				chaInfo.put("ATTR_CODE", attrCode);
				chaInfo.put("ATTR_VALUE", attrValue);
				// 属性组需要特殊处理
				if (StringUtils.isNotEmpty(groupAttr))
				{
					chaInfo.put("GROUP_ATTR", groupAttr);
				}
				// 属性名称需要特殊处理
				if (StringUtils.isNotEmpty(groupAttr))
				{
					chaInfo.put("NAME_ATTR", nameAttr);
				}
				busi.put("B" + attrCode, chaInfo);//由于td_m_esop_page_element表配置显示方式为：ognl:busi.B1112413305.ATTR_VALUE
			}

		}

		return busi;
	}

    private static  IDataset buildListOfferInfo(IDataset subOffers, String relOfferId, String relOfferInsId) throws Exception
    {
        IDataset listOfferInfo = new DatasetList();
        IData offerData;
        for(int j = 0, sizeJ = subOffers.size(); j < sizeJ; j++)
        {
            IData subOffer = subOffers.getData(j);
            String offerType= subOffer.getString("OFFER_TYPE");
            //如果不为服务和资费，跳过
            if(StringUtils.equals("P", offerType)){
            	continue;
            }
            offerData = new DataMap();
            
            offerData.put("OFFER_ID", subOffer.getString("OFFER_ID"));
            offerData.put("OFFER_TYPE",offerType);
            offerData.put("OFFER_INS_ID", subOffer.getString("OFFER_INS_ID",""));
            offerData.put("REL_OFFER_ID", relOfferId);
            offerData.put("REL_OFFER_INS_ID", relOfferInsId);
            offerData.put("ACTION", subOffer.getString("OPER_CODE"));
            
            IDataset subOfferChaList = subOffer.getDataset("OFFER_CHA_SPECS");
            if(IDataUtil.isNotEmpty(subOfferChaList))
            {
                if(EXIST.equals(subOffer.getString("OPER_CODE")))
                {//如果子商品状态是不变且存在商品特征，则将子商品状态改为变更
                    offerData.put("ACTION",UPDATE);
                }
                offerData.put("ATTR_PARAMS", subOfferChaList);
            }
            
            listOfferInfo.add(offerData);
            
            if(IDataUtil.isNotEmpty(subOffer.getDataset("SUBOFFERS")))
            {
                IDataset subOfferList = buildListOfferInfo(subOffer.getDataset("SUBOFFERS"), subOffer.getString("OFFER_ID"), subOffer.getString("OFFER_INS_ID",""));
                listOfferInfo.addAll(subOfferList);
            }
        }
        return listOfferInfo;
    }
    /**
     * chenyi
     * 2016-12-23
     * 获取
     * @param offerid
     * @param epartchy_code
     * @return
     * @throws Exception
     */
    public  static String  getNewAccessNum(String offerid,String epartchy_code) throws Exception{
    	 IData inputData=new DataMap();
    	 inputData.put("EPARTCHY_CODE",epartchy_code);
    	 inputData.put("OFFER_ID", offerid);
    	 inputData.put("ROUTE_CODE", epartchy_code);
    	 IData svData = null;//ServiceCaller.call("OC.enterprise.IECAccessNumOperateSV.getEcAccessNumber",inputData);
         IDataset resultDataset= svData.getDataset("DATAS");
         if(IDataUtil.isNotEmpty(resultDataset)){
        	 String accessNumString=resultDataset.getData(0).getString("ACCESS_NUM");
        	 return accessNumString;
         }else{
        	 return "";
         }
    }

	public static void mixEsopChaWithOnlineOfferCha(IData busi, IDataset esopChaInfos)
	{
		for (int i = 0; i < esopChaInfos.size(); i++)
		{
			IData esopChaInfo = esopChaInfos.getData(i);
			esopChaInfo.put("VALUE",esopChaInfo.getString("CHA_VALUE"));
			busi.put("B"+esopChaInfo.getString("CHA_SPEC_ID"), esopChaInfo);
		}
	}
	
    
    /**
     * 商品名称下拉框显示
     * @param data
     * @throws Exception
     */
    public static void qryMerchInfos(IBizCommon bc ,IData data) throws Exception
    {
        IDataset MerchInfo = UpcViewCall.queryPoByValid(bc);
        data.put("MERCH_INFO", MerchInfo);
    }
    
    /**
     * 子商品下拉框显示
     * @param data
     * @throws Exception
     * @author liaolc 2018-1-13
     */
    public static void qryProductInfos(IBizCommon bc,IData data) throws Exception
    {
        String pospecnumber = data.getString("POSPECNUMBER");
        IDataset result = UpcViewCall.queryPoproductByPospecNumber(bc, pospecnumber);
        data.put("PRODUCT_INFO", result);
        data.put("PRODUCTSPECNUMBER", result.getData(0).getString("PRODUCTSPECNUMBER"));
    }
    /**
     * 查询static表数据
     * @param bc
     * @param data
     * @return
     * @throws Exception
     */
    public static  IDataset qryStaticInfos(IBizCommon bc ,IData data) throws Exception
    {
    	return CSViewCall.call(bc, "CS.StaticInfoQrySVC.getStaticValueByTypeId", data);
    }
    
	/*
	 * @description 预受理转正式受理时，台帐表获取产品参数值信息
	 * @author liaolc
	 * @date 2018-1-17
	 */
    public static  IDataset getAttrInfoListFromTradeTab(IBizCommon bc, String tradeId)throws Exception{
		
		IData inparam = new DataMap();
		inparam.put("TRADE_ID",tradeId);
		inparam.put("MODIFY_TAG",TRADE_MODIFY_TAG.Add.getValue());
		IDataset attrInfoList = CSViewCall.call(bc, "CS.TradeAttrInfoQrySVC.getAttrByTradeID",inparam);
				
		return attrInfoList;
	}
    
	/**
	 * 现在已经存在UU关系的BBOSS子用户
	 * @param bc
	 * @param userIdA
	 * @param productId
	 * @return
	 * @throws Exception
	 */
    public static IDataset queryBbossUserProductInfo(IBizCommon bc ,String userIdA,String productId) throws Exception
    {
        // 现在已经存在UU关系的BBOSS子用户

        IData inparams = new DataMap();
        inparams.put("PRODUCT_ID", productId);
        
        IDataset comp = CSViewCall.call(bc, "CS.ProductCompInfoQrySVC.getCompProductInfoByID", inparams);

        if (IDataUtil.isEmpty(comp))
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_23, inparams.getString("PRODUCT_ID"));
        }
        
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);
        param.put("RELATION_TYPE_CODE", ((IData) comp.get(0)).getString("RELATION_TYPE_CODE"));
        param.put("ROLE_CODE_B", "0");// 子产品跟商品的role_code_b关系
        IDataset relationUUs = CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.getUUInfoForGrp", param);
        if (IDataUtil.isEmpty(relationUUs))
        {
            CSViewException.apperr(GrpException.CRM_GRP_195);
        }

        IDataset bbossUserProductList = new DatasetList();
        for (int i = 0; i < relationUUs.size(); i++)
        {
            IData bbossUserData = new DataMap();
        	IData d = relationUUs.getData(i);
            IData temp = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bc, d.getString("USER_ID_B"), false);
            if (IDataUtil.isEmpty(temp))
            {
                continue;
            }
            if ("BOSG".equals(temp.getString("BRAND_CODE")))
            {
            	String offerCode =  temp.getString("PRODUCT_ID");
            	bbossUserData.put("BRAND_CODE", temp.getString("BRAND_CODE"));
            	bbossUserData.put("OFFER_CODE",offerCode);
            	bbossUserData.put("OFFER_NAME", temp.getString("PRODUCT_NAME"));
            	bbossUserData.put("USER_ID", temp.getString("USER_ID"));
            	bbossUserData.put("OFFER_ID",IUpcViewCall.getOfferIdByOfferCode(offerCode));
            	bbossUserData.put("OFFER_TYPE","P");
            	bbossUserProductList.add(bbossUserData);
            }
            
        }

        return bbossUserProductList;
    }
    
    public static String  bbossTransOperType(IBizCommon bc ,String operType,String offerCode) throws Exception
    {
    	String operCode ="1";
        // 1、集团受理转换
        if (BizCtrlType.CreateUser.equals(operType))
        {
         	
            boolean isPre = EcBbossCommonViewUtil.isPreMerchpOffer(bc,offerCode);
            // 预受理
            if (isPre)
            {
            	operCode= "10";
            }
            // 正式受理
            else 
            {
            	operCode= "1";
            }
        }
        // 2、集团 注销、变更转换
        else 
        {
        	operCode= "1";
		} 
        return operCode;
    }
    
    
    public static String queryAttrBizValueByIdTypeObjCode(IBizCommon bc,String prodId) throws Exception
    {
        String value = "";
        IDataset result = CommonViewCall.getAttrsFromAttrBiz(bc,"1","B","PRO",prodId);
        if (DataUtils.isNotEmpty(result)) 
        {
            value= result.getData(0).getString("ATTR_VALUE");
        }
        return value;
    }
    
    /**
     * @author hudie
     * @作用 初始化操作类型 商品操作类型
     */
    public static IDataset initOperTypesChg(IBizCommon bc, String status, String productId) throws Exception
    {
        IDataset operTypes = new DatasetList();
        IData opertype = new DataMap();
        if ("A".equals(status))
        {	
        	IData inparams = new DataMap();
        	inparams.put("ID", productId);
        	inparams.put("ID_TYPE", "P");
        	inparams.put("ATTR_OBJ", "0");
        	inparams.put("ATTR_CODE", "CHGMERCHOPTYPE");

            IDataset operTypeInfoList = CSViewCall.call(bc, "CS.AttrBizInfoQrySVC.getBizAttr", inparams);//AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, productId, "P", "0", "CHGMERCHOPTYPE");
            if (IDataUtil.isEmpty(operTypeInfoList))
            {
                return operTypes;
            }

            String operType = operTypeInfoList.getData(0).getString("ATTR_VALUE");
            String[] operTypeArray = operType.split(",");
            for (int i = 0; i < operTypeArray.length; i++)
            {
                operType = operTypeArray[i];
                String operTypeName = getOperNameByOperType(operType);
                IData operTypeInfo = new DataMap();
                operTypeInfo.put("OPER_VALUE", operType);
                operTypeInfo.put("OPER_NAME", operTypeName);
                operTypes.add(operTypeInfo);
            }
        }

        if("N".equals(status)){
            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue());
            opertype.put("OPER_NAME", "商品恢复");
            operTypes.add(opertype);
        }else if("D".equals(status)){
            opertype = new DataMap();
            opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue());
            opertype.put("OPER_NAME", "冷冻期恢复商品订购");
            operTypes.add(opertype);
        }else{
        	IData inparams = new DataMap();
        	
        	inparams.put("ID", "1");
        	inparams.put("ID_TYPE", "B");
        	inparams.put("ATTR_OBJ", "PREDESTORY");
        	inparams.put("ATTR_CODE", productId);
        	
            IDataset result = CSViewCall.call(bc, "CS.AttrBizInfoQrySVC.getBizAttr", inparams);//AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "PREDESTORY", productId);
            if (IDataUtil.isEmpty(result))
            {
                opertype = new DataMap();
                opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue());
                opertype.put("OPER_NAME", "取消商品订购");
                operTypes.add(opertype);
            }
            else
            {
                opertype = new DataMap();
                opertype.put("OPER_VALUE", GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue());
                opertype.put("OPER_NAME", "预取消商品订购");
                operTypes.add(opertype);
            }
        }
        
        return operTypes;

    }
}
