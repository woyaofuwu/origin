
package com.asiainfo.veris.crm.iorder.web.igroup.bboss.common;  

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.veris.crm.util.MapUtil;


/**
 * @Descrption 前台Ajax调用submit方法调用后台服务查询数据
 * @author chenkh
 * @Date 2015年12月10日
 */
public class AjaxDataVerify extends BizHttpHandler
{
    /**
     * 查询产品状态
     * @throws Exception
     */
    public void qryOfferSta() throws Exception
    {
        IDataset subOffers = new DatasetList(getData().getString("SUBSCRIBER_LIST"));
        IDataset offerStaInfos = new DatasetList();
        for (int i =0;i<subOffers.size();i++)
        {
            IData subOffer =subOffers.getData(i);
            //IDataset prodSubOfferList = subOffer.getDataset("SUBOFFERS");
            if (subOffer==null || subOffer.isEmpty())
                continue;
            IData input = new DataMap();
            input.put("USER_ID", subOffer.getString("USER_ID"));
            /*String offerCode = subOffer.getString("OFFER_CODE");
            IData inparams = new DataMap();//全网编码
            inparams.put("ID", "1");
            inparams.put("ID_TYPE", "B");
            inparams.put("ATTR_OBJ", "PRO"); 
            inparams.put("ATTR_CODE", offerCode);
            IDataset result = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.getBizAttr", inparams);
            String poNumber = "";
            if (result != null && result.size() > 0)
            {
                poNumber = result.getData(0).getString("ATTR_VALUE", "");
            }
            
            input.put("PRODUCT_SPEC_CODE", poNumber);*/
            IDataset ret = CSViewCall.call(this, "CS.UserGrpMerchInfoNewQrySVC.qryMerchpInfoByUserId", input);//ServiceCaller.call("OC.enterprise.IUmOfferStaInfoQuerySV.queryUmOfferSta01BySubscrInsIdOfferId",input);
            IData svcStaInfos = ret.first();
            if (svcStaInfos ==null || svcStaInfos.isEmpty())
                continue;
            IData offerSta = new DataMap();
            offerSta.put("OFFER_ID",subOffer.getString("OFFER_ID"));
            offerSta.put("OFFER_STA",svcStaInfos.getString("STATUS"));
            offerSta.put("OFFER_INDEX",subOffer.getString("OFFER_INDEX"));

            offerStaInfos.add(offerSta);
        }

        // 返回结果
        IData iData = new DataMap();
        iData.put("result",offerStaInfos);
        setAjax(iData);
    }
    /**
     * @Title: getBoundData
     * @Description: 取得bboss下拉框数据联动
     * @param @throws Exception
     * @return void
     * @throws
     */
    public void getBoundData() throws Exception
    {
        // 1- 获取父属性编号、父编码、子属性编号

        String topAttrCode = getData().getString("FATHER_ATTR_CODE");
        String topAttrValue = getData().getString("FATHER_ATTR_VALUE");
        String attrCode = getData().getString("SON_ATTR_CODE");

        // 2-根据上面的参数从BS_PARA_DETAIL表获取对应下拉框值

        IData inparam = new DataMap();
        inparam.put("PARA_TYPE", "BBSS_BOUND_DATA");
        inparam.put("PARA2", topAttrCode);
        inparam.put("PARA3", topAttrValue);
        inparam.put("PARA_CODE", attrCode);
        IData retData = null;//ServiceCaller.call("OC.enterprise.IParaSelectEleItemQuerySV.getSeleEleRelaItem", inparam);//queryOfferChaBySIDAndOfferInsId
        IDataset boundDatas = retData.getDataset("DATAS");

        // 3- 返回结果
        IData idata = new DataMap();
        idata.put("result", boundDatas);
        setAjax(idata);
    }

    public void getInsDataBySubInstIdAndChaSpecId() throws Exception
    {
        String subscriberInstId = getData().getString("SUBSCRIBER_ID");
        String chaSpecId = getData().getString("CHA_SPEC_ID");

        // 1、先查询subscriber信息得到prodInstId
        IData input = new DataMap();
        
        input.put("USER_ID", subscriberInstId);
        input.put("ATTR_CODE", chaSpecId);
        IDataset chaSpecInfos = CSViewCall.call(this,"CS.UserAttrInfoQrySVC.getUserAttrByUserIdc", input);  //CS.UserAttrInfoQrySVC.getUserAttrByUserIdc
        		
        if (IDataUtil.isEmpty(chaSpecInfos))
        {
            IData data = new DataMap();
            data.put("result", new DatasetList());
            setAjax(data);
            return;
        }

        IData retChaInfo = new DataMap();
        retChaInfo.put("result", chaSpecInfos);
        setAjax(retChaInfo);
    }

    public void getPbssBizProdInstId() throws Exception
    {
        // TODO 物联网3006 GPRS
    }
    /**
     * 农政通取序列
     * @throws Exception
     */
    public void getEcNum() throws Exception
    {
    	 IData input = new DataMap();
    	 input.put("SEQ_NAME", "ECNUM");
    	 IData result = null;//ServiceCaller.call("OC.enterprise.IBbossTaskOperateSV.getSeq", input);
    	 String ecNum="";
/*    	 if(IDataUtil.isNotEmpty(result.getDataset("DATAS"))){
	        	IData map=result.getDataset("DATAS").getData(0);
	        	ecNum=map.getString("ECNUM_KID");
	        }*/


         IData retChaInfo = new DataMap();
         retChaInfo.put("result", ecNum);
         setAjax(retChaInfo);
    }
    /**
     * 获取bbos属性序列
     * @throws Exception
     */
    public void getBbossSeqId() throws Exception
    {

         String servCode = getData().getString("SERV_CODE");

             // 调用服务获取云MAS短流程服务代码信息
         IData param = new DataMap();
         param.put("SERV_CODE", servCode);
         IDataset servCodeList = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getServCodeByajax", param);
         IData idata = new DataMap();
         if (IDataUtil.isNotEmpty(servCodeList)){
             	 idata.put("result", "1");
         }else{
             	idata.put("result", "0");
         }

         setAjax(idata);
         
    }

    /**
     * 400号码是否重复
     * @throws Exception
     */
    public void check400Num() throws Exception
    {
        String accessnum = getData().getString("ACCESSNUM");
        boolean isExist = true;
        //查资料
        IData input = new DataMap();
        input.put("SERIAL_NUMBER", accessnum);
        IDataset result = new DatasetList();
        try
        {
            result =  CSViewCall.call(this,"CS.UserInfoQrySVC.getUserInfoBySN", input);  
        }
        catch (Exception e)
        {
            result = new DatasetList();
        }

        IData retChaInfo = new DataMap();
        if (IDataUtil.isEmpty(result))
        {
            isExist = false;
            //查tf_b_trade表看是否存在serial_num
            input.put("TRADE_TYPE_CODE", "4690");
            input.put("ROUTE_EPARCHY_CODE", "0898");
            try
            {
                result =  CSViewCall.call(this,"CS.TradeInfoQrySVC.queryTradeBySnGrp", input);  
            }
            catch (Exception e)
            {
                result = new DatasetList();
            }
    		if(IDataUtil.isNotEmpty(result))
    		{
    			isExist = true;
    		}
            else
            {
            	isExist = false;
            }
        }
        
        retChaInfo.put("result", isExist);
        setAjax(retChaInfo);
    }
    
    /**
     * 查询一点支付上传文件名
     * @throws Exception
     */
    public void getYDZHUploadFileName() throws Exception
    {
        String productOrderId = getData().getString("PRODUCT_ORDER_ID");
        String fileType = getData().getString("FILE_TYPE");
        IData input = new DataMap();
        input.put("PRODUCT_ORDER_ID",productOrderId); 
        input.put("FILE_TYPE",fileType); 
//        IData result = ServiceCaller.call("OrderCentre.enterprise.ITiBBbossQuerySV.queryTiBbossFileName", input);
        String fileName= "";
        IData retChaInfo = new DataMap();
        /*if(IDataUtil.isNotEmpty(result.getDataset("DATAS"))){
            
            fileName= result.getDataset("DATAS").getData(0).getString("FILE_NAME");
        }*/
        retChaInfo.put("result", fileName);
        setAjax(retChaInfo);

    }
    /**
     * @Title: getProdPreInfo
     * @Description: 预受理判断
     * @throws Exception
     * @return void
     * @author chenkh
     * @throws
     */
    public void getProdPreInfo() throws Exception
    {
        String offerId = getData().getString("OFFER_ID");
        boolean isPre = EcBbossCommonViewUtil.isPreMerchpOffer(this,offerId);
        if (isPre)
        {
            IData iData = new DataMap();
            iData.put("result", "10");
            setAjax(iData);
        }
        else
        {
            IData iData = new DataMap();
            iData.put("result", "1");
            setAjax(iData);
        }
    }
	
    /**
     * 多媒体彩铃 判断是固话还是手机号
     * @throws Exception
     */
    public void getCustomRingingIMS() throws Exception
    {
    	 String accessnum = getData().getString("ACCESSNUM");
    	 String mebSubscriberInsId = "";
    	 String imsNum="1234";//批量值默认
    	 if(StringUtils.isNotEmpty(accessnum)&&(!("undefined").equals(accessnum))){
    		 IData input = new DataMap();
        	 input.put("ACCESS_NUM",accessnum);
        	 IData result = null;//ServiceCaller.call("OrderCentre.enterprise.IUmSubScriberSV.querySubscriberByAccessNum", input);
        	 if(IDataUtil.isNotEmpty(result.getDataset("DATAS")))
        	 {
    	        	IData map = result.getDataset("DATAS").getData(0);
    	        	mebSubscriberInsId = map.getString("SUBSCRIBER_INS_ID");
    	        	
    	        	input.put("SUBSCRIBER_INS_ID", mebSubscriberInsId);
    	            IData umResult = null;//ServiceCaller.call("OrderCentre.enterprise.IUmSubScriberQuerySV.queryUmImpuBySubscriberInsId", input);
    	            if(IDataUtil.isNotEmpty(umResult.getDataset("DATAS")))
    	            {
    	                imsNum = umResult.getDataset("DATAS").getData(0).getString("IMPI");
    	            }
    	        }
    	 }
    	
    	IData retChaInfo = new DataMap();
        retChaInfo.put("result", imsNum);
        setAjax(retChaInfo);
    }
    
    /**
     * 和对讲 终端串校验
     */
    public void getCustomRingingIMSh() throws Exception{
    	String resNo = getData().getString("RES_NO");
    	//String resType = getData().getString("RES_TYPE");
    	String modelCode = "-1";
    	String resTradeCode = "ICheckEmodelInfo";
    	String resTypeCode = "D";
    	String resKindCode = "D8";
    	String loginEparchyCode = this.getVisit().getLoginEparchyCode();
    	String cityCode = this.getVisit().getCityCode();
    	String departId = this.getVisit().getDepartId();
    	String staffId = this.getVisit().getStaffId();
    	
    	IData resultData = new DataMap();
    	IData row = null;//EcSfViewUtil.callCheckAndPreOccupyEmodel(resTradeCode,resTypeCode,resNo,resKindCode,modelCode,loginEparchyCode,cityCode,departId,staffId);
    	if (!MapUtil.isEmpty(row)){
			IDataset datas = row.getDataset("DATAS");
			if(IDataUtil.isNotEmpty(datas)){
    			resultData.putAll(datas.getData(0));
			}
	    }
    	
    	setAjax(resultData);
    	    
    }
    //成员操作类型
    /**
     *@description 设置成员的操作类型
     *@author weixb3
     *@version 创建时间：2013-6-28
     */
    protected String getMebOperCode(String meb_status,String mebUserId ,String grpUserId,String eparchyCode) throws Exception
    {
    	 IData inparams = new DataMap();
         inparams.put("USER_ID", mebUserId);
         inparams.put("GRP_USER_ID", grpUserId);
         inparams.put("ROUTE_EPARCHY_CODE", eparchyCode);
         IDataset merchMebs = CSViewCall.call(this, "CS.UserGrpMerchInfoQrySVC.getSEL_BY_USERID_USERIDA", inparams);
        String status = merchMebs.first().getString("STATUS","A");
        
        return status;
        
    }
    
    /*
     * @descripiton 根据产品编号获取并设置新增成员操作中的成员类型
     * @authro xunyl
     * @date 2013-08-09
     */
    protected IDataset setCrtMebTypes(String grpUserId,String productId) throws Exception
    {
    	//productId 产品编码
        IDataset mebTypeSet = StaticUtil.getList(getVisit(), "TD_B_ATTR_BIZ", "ATTR_VALUE", "ATTR_NAME", new java.lang.String[]
        { "ID", "ID_TYPE", "ATTR_OBJ","ATTR_CODE" }, new java.lang.String[]
         { "1", "B", "MTYPE",productId });
        String mebType = "";
        if (mebTypeSet != null && mebTypeSet.size() == 1)
        {
            mebType = mebTypeSet.getData(0).getString("ATTR_VALUE");

        }
        else
        {
            mebTypeSet = new DatasetList();
            IData temp = new DataMap();
            temp.put("ATTR_VALUE", "1");
            temp.put("ATTR_NAME", "签约关系");
            mebTypeSet.add(temp);
            mebType = "1";

        }
        //如果是云MAS业务，添加成员时，只能从集团受理时的名单类型属性中获取
        String attrCode = "";
    	String productSpecCode =StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
    	{ "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
    	{ "1", "B", productId, "PRO" });
    	if(StringUtils.equals("110154", productSpecCode) || StringUtils.equals("110155", productSpecCode) ||
				StringUtils.equals("110156", productSpecCode)){
    		attrCode = productSpecCode+"4008";
    	}else if(StringUtils.equals("110157", productSpecCode) || StringUtils.equals("110158", productSpecCode) || 
    			StringUtils.equals("110159", productSpecCode) || StringUtils.equals("110160", productSpecCode) || 
    			StringUtils.equals("110161", productSpecCode) || StringUtils.equals("110162", productSpecCode)){
    		attrCode = productSpecCode+"4011";
    	}
    	if(StringUtils.isNotBlank(attrCode)){
	        IData inparam = new DataMap();
	        inparam.put("USER_ID", grpUserId);//grpUserId 集团产品UserID
	        inparam.put("ATTR_CODE", attrCode);
	        IDataset userAttrInfoList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserAttrByUserId",inparam);
	        if(IDataUtil.isEmpty(userAttrInfoList)){
	        	CSViewException.apperr(CrmUserException.CRM_USER_1);
	        }else{
	        	IData userAttrInfo = userAttrInfoList.getData(0);
	        	String attrValue = userAttrInfo.getString("ATTR_VALUE");
	        	if(StringUtils.equals("0", attrValue)){
	        		 mebTypeSet = new DatasetList();
	                 IData temp = new DataMap();
	                 temp.put("ATTR_VALUE", attrValue);
	                 temp.put("ATTR_NAME", "黑名单");
	                 mebTypeSet.add(temp);
	                 mebType = attrValue;
	        	}else if(StringUtils.equals("2", attrValue)){
	        		mebTypeSet = new DatasetList();
	                 IData temp = new DataMap();
	                 temp.put("ATTR_VALUE", attrValue);
	                 temp.put("ATTR_NAME", "白名单");
	                 mebTypeSet.add(temp);
	                 mebType = attrValue;
	        	}
	        }
    	}
    	return mebTypeSet;
        //setMebType(mebType);
        //setMebTypeSet(mebTypeSet);
    }
    
    public String getMerchpMebState(IData d) throws Exception
    {
        IData param = new DataMap();
        if (!"".equals(d.getString("MEB_USER_ID", "")) && !"".equals(d.getString("USER_ID", "")))
        {
            param.put("USER_ID", d.getString("MEB_USER_ID"));
            param.put("EC_USER_ID", d.getString("USER_ID"));
            param.put("SERIAL_NUMBER", d.getString("MEB_SERIAL_NUMBER"));
            param.put(Route.ROUTE_EPARCHY_CODE, d.getString("EPARCHY_CODE"));
            IDataset temp = CSViewCall.call(this, "CS.TradeGrpMerchMebInfoQrySVC.qryBBossMerchpMeb", param);
            if (temp != null && temp.size() != 0)
            {
                return temp.getData(0).getString("STATUS", "A");
            }
        }
        return "N";
    }
    
    private final static IDataset call(String svcName, IData input) throws Exception
    {
        IDataOutput output = svcFatCall(svcName, input);
        return output.getData();
    }
    
    private final static IDataOutput svcFatCall(String svcName, IData data) throws Exception
    {
        IDataInput input = new DataInput();
        input.getData().putAll(data);

        IDataOutput output = ServiceFactory.call(svcName, input);

        return output;
    }
    
    /*
     * @descripiton 根据产品编号获取并设置新增成员变更中的成员类型
     * @author xunyl
     * @date 2013-08-09
     */
    protected IDataset setChgMebTypes(String mebUserId,String grpUserId,String productId,String eparchyCode) throws Exception
    {
        String mebType = this.getMebType(mebUserId, grpUserId,eparchyCode);

        // 设置成员类型IDATESET
        IDataset mebTypeSet = StaticUtil.getList(getVisit(), "TD_B_ATTR_BIZ", "ATTR_VALUE", "ATTR_NAME", new java.lang.String[]
        { "ID", "ID_TYPE", "ATTR_OBJ","ATTR_CODE" }, new java.lang.String[]
          { "1", "B", "MTYPE",productId });
        if (mebTypeSet.size() == 0)
        {
            mebTypeSet = new DatasetList();
            IData temp = new DataMap();
            temp.put("ATTR_VALUE", "1");
            temp.put("ATTR_NAME", "签约关系");
            mebTypeSet.add(temp);
            mebType = "1";
        }
        //setMebType(mebType);
        //setMebTypeSet(mebTypeSet);
        return mebTypeSet;
    }
    
    /**
     * 获取成员类型
     * 
     * @param memUserId
     * @param grpUserId
     * @author xunyl
     * @date 2013-04-28
     * @return
     * @throws Exception
     */
    public String getMebType(String memUserId, String grpUserId,String eparchyCode) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", memUserId);
        inparams.put("GRP_USER_ID", grpUserId);
        inparams.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset merchMebs = CSViewCall.call(this, "CS.UserGrpMerchInfoQrySVC.getSEL_BY_USERID_USERIDA", inparams);
        if (merchMebs != null && merchMebs.size() > 0)
        {
            return merchMebs.getData(0).getString("RSRV_TAG1");// 成员类型
        }
        return "";
    }
    
    //处理成员属性初始化  成员类型  成员操作类型  成员状态
    public void dealMbInitInfo() throws Exception{
    	String operType = getData().getString("OPER_TYPE");
    	String grpUserId = getData().getString("GRP_USER_ID");
    	String offerId = getData().getString("MEB_OFFER_ID");
    	String meb_status = getData().getString("MEB_STATUS");
    	String memUserId = getData().getString("MEB_USER_ID");
    	String mebSerialNumber  = getData().getString("MEB_SERIAL_NUMBER");
    	String eparchyCode = getData().getString("EPARCHY_CODE");
    	
    	String productId = IUpcViewCall.getOfferCodeByOfferId(offerId);
    	IData mbDealMbInitInfoData = new DataMap();
    	//成员操作类型  
    	if("CrtMb".equals(operType)){
    		 mbDealMbInitInfoData = crtMbDealMbInitInfo(grpUserId,productId);
    	}else if("ChgMb".equals(operType)){
    		 mbDealMbInitInfoData = chgMbDealMbInitInfo( productId, meb_status, memUserId, grpUserId, mebSerialNumber,eparchyCode);
    	}
    	IData retInfo = new DataMap();
    	retInfo.put("result", mbDealMbInitInfoData);
        setAjax(retInfo);
    }
    
    //成员新增 处理成员属性初始化  成员类型  成员操作类型  成员状态
    private IData crtMbDealMbInitInfo(String grpUserId, String productId) throws Exception{
    	 IData resultData = new DataMap();
    	 //mebOpers  成员操作类型
    	 IData oper = new DataMap();
         oper.put("OPER_CODE", "1");
         oper.put("OPER_NAME", "新增");
         IDataset mebOpers = new DatasetList();
         mebOpers.add(oper);
         
         //mebTypeSet 成员类型
         IDataset mebTypeSets = setCrtMebTypes( grpUserId, productId);
         
         resultData.put("MEB_OPERS", mebOpers);
         resultData.put("MEB_TYPE_SET", mebTypeSets.first());
         
    	 return resultData;
    }
    
    //成员变更 处理成员属性初始化  成员类型  成员操作类型  成员状态
    private IData chgMbDealMbInitInfo(String productId,String meb_status,String memUserId,String grpUserId,String mebSerialNumber,String eparchyCode) throws Exception{
    	IData resultData = new DataMap();
    	//mebOpers  成员操作类型 
    	String mebOpers = getMebOperCode( meb_status,memUserId,grpUserId,eparchyCode);
    	//mebTypeSet 成员类型
    	IDataset mebTypeSets = setChgMebTypes(  memUserId, grpUserId, productId, eparchyCode);
    	//productStatus 成员状态
        IData paramGetMerchpMebState  = new DataMap();
        paramGetMerchpMebState.put("MEB_USER_ID", memUserId);
        paramGetMerchpMebState.put("USER_ID", grpUserId);
        paramGetMerchpMebState.put("MEB_SERIAL_NUMBER", mebSerialNumber);
        paramGetMerchpMebState.put("EPARCHY_CODE", eparchyCode);
    	String mebTypeString = getMerchpMebState( paramGetMerchpMebState);
    	String productStatus = mebTypeString.equals("N") ? "暂停" : "正常";
    	
    	resultData.put("MEB_OPERS", mebOpers);
        resultData.put("MEB_TYPE_SET", mebTypeSets.first());
        resultData.put("PRODUCT_STATUS", productStatus);
    	return resultData;
    }
    
    /**
     * 查询联系人电话及其总部用户名
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryStaffNumber() throws Exception
    {
        String staffId = getData().getString("STAFF_ID");
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        IDataset result = CSViewCall.call(this, "CS.StaffInfoQrySVC.queryStaffInfoForBBoss", param);
        param.put("STAFF_NUMBER", result.getData(0).getString("STAFF_NUMBER", ""));
        param.put("STAFF_PHONE", result.getData(0).getString("SERIAL_NUMBER", ""));
        setAjax(param);
    }
    
    /**
     * 查询合同信息
     * @throws Throwable
     */
    public void attFileCheck() throws Throwable{    
    	IData inParam = new DataMap();
    	inParam.put("FILE_ID", getData().getString("ATT_CODE"));
        IDataset reList = CSViewCall.call(this, "CS.MFileInfoQrySVC.qryFileInfoByFileId", inParam);
        IData redata = new DataMap();
        String reslut = "true";
        if (IDataUtil.isEmpty(reList))
        {
        	reslut = "false";
            redata.put("ERROR_MESSAGE", GrpException.CRM_GRP_642);
        }
        String fileName = reList.getData(0).getString("FILE_NAME");
        String[] fileNameList = fileName.split("\\.");
        if (fileNameList.length <= 1)
        {
        	reslut = "false";
            redata.put("ERROR_MESSAGE", fileName+"合同附件无后缀名,格式错误,请重新上传!");
        }

        redata.put("RESULT", reslut);
        setAjax(redata);
    }
    
    /**
     * 根据BBOSS全网编码获取省内编码
     * @param cycle
     * @throws Throwable
     */
    public void getProProductId() throws Throwable
    {
        // 1- 获取全网产品编号
        String allnetProductId = getData().getString("ALLNET_PRODUCT_ID");

        // 2- 根据全网产品编号获取省内产品编号
        String proProductId = StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_VALUE", "ATTR_OBJ" }, "ATTR_CODE", new String[]
        { "1", "B", allnetProductId, "PRO" });

        // 3- 返回省内产品编号
        IData idata = new DataMap();
        idata.put("result", proProductId);
        setAjax(idata);
    }
    
    /**
     * 根据bboss省内编码获取全网编码
     * @param cycle
     * @throws Throwable
     */
    public void getMerchPId() throws Throwable
    {
        // 1- 获取全网产品编号
        String allnetProductId = getData().getString("ALLNET_PRODUCT_ID");

        // 2- 根据全网产品编号获取省内产品编号
        String proProductId = StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
        { "1", "B", allnetProductId, "PRO" });

        // 3- 返回省内产品编号
        IData idata = new DataMap();
        idata.put("result", proProductId);
        setAjax(idata);
    }

    
    /*
     * 根据MAS 基本接入号属性，查询基本接入号列表
     */
    public void getMasEcCodeListByA() throws Throwable
    {
        String strCustId = "";
        String grpCustInfoValue = "";
        String ecInCodeValue = "";
        IData data = getData();
        String strCodeA = data.getString("EC_BASE_IN_CODE_A");
        if (!strCodeA.equals("02"))
            strCodeA = "01";
        String strGroupId = data.getString("GROUP_ID");

        String bizTypeCode = data.getString("BIZ_TYPE_CODE");
        IData param = new DataMap();
        // 根据group_id获取cust_id
        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, strGroupId);
        if (IDataUtil.isNotEmpty(result))
        {
            strCustId = result.getString("CUST_ID");
            if ("002".equals(bizTypeCode))
            {
                grpCustInfoValue = result.getString("RSRV_STR8");
            }
            else
            {
                grpCustInfoValue = result.getString("RSRV_STR3");

            }
        }

        param.put("CUST_ID", strCustId);
        param.put("BNUM_KIND_NEW", strCodeA);
        if ("002".equals(bizTypeCode))
        {
            param.put("EXTEND_TAG", "BNUMBMMS");
        }
        else
        {
            param.put("EXTEND_TAG", "BNUMB");
        }

        IDataset EcInCode = CSViewCall.call(this, "CS.GrpExtInfoQrySVC.getEcInCodeListByECA", param);

        if (IDataUtil.isNotEmpty(EcInCode))
        {
            ecInCodeValue = EcInCode.getData(0).getString("RSRV_STR1");

        }
        IData idata = new DataMap();
        idata.put("EC_BASE_IN_CODE", ecInCodeValue);
        idata.put("SP_CODE", grpCustInfoValue);
        idata.put("EC_BASE_IN_CODE_A", strCodeA);
        setAjax(idata);
    }
    
    
    /**
     * @description 根据省行业网关云MAS短流程产品的服务代码查询TF_F_USER_GRP_PLATSVC、TF_B_TRADE_GRP_PLATSVC
     * 表中是否已经存在(initParam.js中用到)
     * @author songxw
     * @date 2017-11-13
     */
    public void getMasServCodeByParam() throws Throwable
    {
        String servCode = getData().getString("SERV_CODE");

        // 调用服务获取云MAS短流程服务代码信息
        IData param = new DataMap();
        param.put("SERV_CODE", servCode);
        IDataset servCodeList = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getServCodeByajax", param);
        IData idata = new DataMap();
        if (IDataUtil.isNotEmpty(servCodeList)){
        	 idata.put("result", "1");
        }else{
        	idata.put("result", "0");
        }

        setAjax(idata);
    }
    
    
    /**
     * @description 根据400语音产品的用户编号和400号码的属性编号获取400号码值(initParam.js中用到)
     * @author xunyl
     * @date 2014-06-27
     */
    public void get400NumByUserIdAttrCode() throws Throwable
    {
        // 1- 获取400语音产品的用户编号
        String userId = getData().getString("USER_ID");

        // 2- 获取400号码的属性编号
        String attrCode = getData().getString("ATTR_CODE");

        // 3- 调用服务获取400号码参数信息
        IDataset paramInfoList = UserAttrInfoIntfViewUtil.qryGrpProductAttrInfosByUserIdAndInstTypeAndAttrCode(this, userId, "P", attrCode);

        // 4- 获取400号码
        String attrValue = "";
        if (IDataUtil.isNotEmpty(paramInfoList))
        {
            attrValue = paramInfoList.getData(0).getString("ATTR_VALUE");
        }

        // 5-返回400号码
        IData idata = new DataMap();
        idata.put("result", attrValue);
        setAjax(idata);
    }

}
