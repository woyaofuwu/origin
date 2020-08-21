package com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.ScrData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public class ScrDataTrans
{
    private static IData workformSubscriberData;
    private static IData workformNodeData;
    private static IDataset workformProductList;
    private static IDataset workformAttrList;
    private static IDataset workformSvcList;
    private static IDataset workformDisList;
    private static IDataset workformOtherList;
    private static IDataset workformAttachList;
    private static IDataset workformProductSubList;
    //影响流程驱动的other表信息
    private static IDataset workformSpcOtherList;
    
    private static IDataset workformQuickOrderCondList; //快速办理产品(一单清)受理数据表

    private static IDataset workformQuickOrderMebList; //快速办理产品(一单清)受理数据表

    private static IDataset workformQuickOrderDataList; //快速办理产品(一单清)受理数据表

    private static IDataset workformProductExtList; //快速办理产品(一单清)受理数据表

    private static IData eosCommonData;
    
    public static IData buildWorkformSvcParam(IData submitData) throws Exception
    {
        init();
        
        IData param = new DataMap();
        
        ScrData scrData = new ScrData();

        buildWorkformSubscriberData(submitData.getData("CUST_DATA"), submitData.getData("BUSI_SPEC_RELE"),submitData.getString("SERIAL_NUMBER",""),submitData.getData("ORDER_DATA"),submitData.getData("CONFCRM_DATA"));
        
        IData offerData = submitData.getData("OFFER_DATA");
        String productId = "";
        if(IDataUtil.isNotEmpty(offerData))
        {
            productId = offerData.getString("OFFER_CODE");
            
            buildWorkformProductList(offerData, true);
            
            IDataset subOfferList = offerData.getDataset("SUBOFFERS", new DatasetList());
            for(int i = 0, sizeI = subOfferList.size(); i < sizeI; i++)
            {
                IData subOffer = subOfferList.getData(i);
                String subOfferType = subOffer.getString("OFFER_TYPE");
                if(UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(subOfferType))
                {
                    buildWorkformProductList(subOffer, false);
                }
                else if(UpcConst.ELEMENT_TYPE_CODE_SVC.equals(subOfferType))
                {
                    buildWorkformSvcList(subOffer, i, 0);
                }
                else if(UpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(subOfferType))
                {
                    buildWorkformDisList(subOffer, i, 0);
                }
            }
        }


        IData offerDataList = submitData.getData("OFFER_DATA_LIST");
        if(IDataUtil.isNotEmpty(offerDataList))
        {
            buildWorkformMinoreProductList(offerDataList);  // 流程 的产品 id

            IDataset subOfferList = offerDataList.getDataset("SUBOFFERS");
            if(IDataUtil.isNotEmpty(subOfferList))
            {
                for(int i = 0, sizeI = subOfferList.size(); i < sizeI; i++)
                {
                    IData subOffer = subOfferList.getData(i);
                    String subOfferType = subOffer.getString("OFFER_TYPE");
                    if(UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(subOfferType))
                    {
                        buildWorkformMinoreProductList(subOffer);
                    }
                    else if(UpcConst.ELEMENT_TYPE_CODE_SVC.equals(subOfferType))
                    {
                        buildWorkformSvcList(subOffer, i, 0);
                    }
                    else if(UpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(subOfferType))
                    {
                        buildWorkformDisList(subOffer, i, 0);
                    }
                }
            }
        }

        IData commonData = submitData.getData("COMMON_DATA");
        if(StringUtils.isBlank(productId) && IDataUtil.isNotEmpty(commonData))
        {
            productId = commonData.getString("PRODUCT_ID");
        }
        buildWorkformNodeData(submitData.getData("NODE_TEMPLETE"), productId);
        
        buildWorkformOtherList(submitData.getDataset("OTHER_LIST"));
        
        buildWorkformAttachList(submitData.getDataset("ATTACH_LIST"));
        
        buildWorkformAttachList(submitData.getDataset("DISCNT_ATTACH_LIST"));
        
        buildContractData(submitData.getDataset("CONTRACT_DATA"));
        
        buildIDCData(submitData.getDataset("IDC_DATA"));

        buildTapMarketingData(submitData.getDataset("MARKETING_DATA"));
        
        buildTapMarketingLineData(submitData.getDataset("MARKETING_LINEDATA"));
        
        buildAccountData(submitData.getData("ACCT_DATA"));
        
        buildEomsAttrData(submitData.getDataset("EOMS_ATTR_LIST"));
        
        buildDirectlineAttrData(submitData.getDataset("DIRECTLINE_DATA"));
        
        buildDelParamAttrData(submitData.getDataset("DEL_PARAM_LIST"));
        
        buildAttrData(submitData.getDataset("CUSTOM_ATTR_LIST"));
        
        buildWorkformSpcOtherList(submitData.getDataset("SPC_OTHER_LIST"));
        
        buildQuickOrderOfferList(submitData.getDataset("QUICKORDER_OFFER_COND_LIST"));

        buildQuickOrderOfferMebList(submitData.getDataset("QUICKORDER_OFFER_MEB_LIST"));

        buildQuickOrderOfferDataList(submitData.getDataset("QUICKORDER_OFFER_DATA_LIST"));

        scrData.put(EcEsopConstants.TABLE_EOP_SUBSCRIBE, workformSubscriberData);
        scrData.put(EcEsopConstants.TABLE_EOP_NODE, workformNodeData);
        scrData.put(EcEsopConstants.TABLE_EOP_PRODUCT, workformProductList);
        scrData.put(EcEsopConstants.TABLE_EOP_ATTR, workformAttrList);
        scrData.put(EcEsopConstants.TABLE_EOP_SVC, workformSvcList);
        scrData.put(EcEsopConstants.TABLE_EOP_DIS, workformDisList);
        scrData.put(EcEsopConstants.TABLE_EOP_OTHER, workformOtherList);
        scrData.put(EcEsopConstants.TABLE_EOP_ATTACH, workformAttachList);
        scrData.put(EcEsopConstants.TABLE_EOP_PRODUCT_SUB, workformProductSubList);
        scrData.put(EcEsopConstants.TABLE_EOP_QUICKORDER_COND, workformQuickOrderCondList);
        scrData.put(EcEsopConstants.TABLE_EOP_QUICKORDER_MEB, workformQuickOrderMebList);
        scrData.put(EcEsopConstants.TABLE_EOP_QUICKORDER_DATA, workformQuickOrderDataList);
        scrData.put(EcEsopConstants.TABLE_EOP_PRODUCT_EXT, workformProductExtList);

        buildEosCommonData(submitData.getData("BUSI_SPEC_RELE"), submitData.getData("NODE_TEMPLETE"), commonData);
        
        param.put("EOSAttr", scrData);
        param.put("EOSCom", eosCommonData);
        
        return param;
    }
    
    /**
     * 构造流程订单信息
     * @param
     * @throws Exception
     */
    private static void buildWorkformSubscriberData(IData custData, IData busiSpecRele,String serialNumber, IData orderData, IData confCrmData) throws Exception
    {
    	if (IDataUtil.isNotEmpty(custData))
    	{
            workformSubscriberData.put("GROUP_ID", custData.getString("GROUP_ID"));
            workformSubscriberData.put("CUST_NAME", custData.getString("CUST_NAME"));
		}

    	if (IDataUtil.isNotEmpty(busiSpecRele))
    	{
            workformSubscriberData.put("BPM_TEMPLET_ID", busiSpecRele.getString("BPM_TEMPLET_ID"));
            workformSubscriberData.put("BUSI_TYPE", busiSpecRele.getString("BUSI_TYPE"));
            workformSubscriberData.put("BUSI_CODE", busiSpecRele.getString("BUSI_CODE"));
            workformSubscriberData.put("RSRV_STR2", busiSpecRele.getString("TEMPLET_ID"));
    	}
    	
    	if (IDataUtil.isNotEmpty(orderData))
    	{
            workformSubscriberData.put("RSRV_STR3", StaticUtil.getStaticValue("URGENCY_LEVEL", orderData.getString("URGENCY_LEVEL")));
            workformSubscriberData.put("RSRV_STR4", orderData.getString("TITLE"));
            workformSubscriberData.put("FLOW_LEVEL", StaticUtil.getStaticValue("URGENCY_LEVEL", orderData.getString("URGENCY_LEVEL")));
            workformSubscriberData.put("FLOW_DESC", orderData.getString("TITLE"));

            workformSubscriberData.put("RSRV_STR7", orderData.getString("OPER_TYPE"));
    	}
    	
    	if (IDataUtil.isNotEmpty(confCrmData))
    	{
            workformSubscriberData.put("RSRV_STR5", confCrmData.getString("CONF_IBSYSID"));
    	}
        workformSubscriberData.put("RSRV_STR6", serialNumber);
    }
    
    /**
     * 构造流程产品信息
     * @param offerData
     * @param isTopOffer 是否主商品
     * @throws Exception
     */
    private static void buildWorkformProductList(IData offerData, boolean isTopOffer) throws Exception
    {
        IDataset topOfferChaList = offerData.getDataset("OFFER_CHA", new DatasetList());
        if(isTopOffer && IDataUtil.isNotEmpty(topOfferChaList))
        {
        	for(int i = 0, size = topOfferChaList.size(); i < size; i++)
        	{
        		int workformProductSize = workformProductList.size();
        		IData workformProduct = new DataMap();
        		workformProduct.put("PRODUCT_ID", offerData.getString("OFFER_CODE"));
        		workformProduct.put("PRODUCT_NAME", offerData.getString("OFFER_NAME"));
        		workformProduct.put("RSRV_STR1", offerData.getString("OFFER_ID"));
        		workformProduct.put("RSRV_STR2", offerData.getString("IS_EXPER_DATALINE",""));
        		workformProduct.put("RECORD_NUM", workformProductSize);
        		workformProduct.put("PRODUCT_TYPE_CODE", offerData.getString("CATALOG_ID", ""));
        		workformProduct.put("USER_ID", offerData.getString("USER_ID", ""));
        		workformProduct.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER", ""));
        		workformProductList.add(workformProduct);
        		
        		buildWorkformAttrList(topOfferChaList.getDataset(i), workformProductSize);
        	}
        }
        else if(isTopOffer && IDataUtil.isEmpty(topOfferChaList))
        {
            int workformProductSize = workformProductList.size();
            IData workformProduct = new DataMap();
            workformProduct.put("PRODUCT_ID", offerData.getString("OFFER_CODE"));
            workformProduct.put("PRODUCT_NAME", offerData.getString("OFFER_NAME"));
            workformProduct.put("RSRV_STR1", offerData.getString("OFFER_ID"));
            workformProduct.put("RSRV_STR2", offerData.getString("IS_EXPER_DATALINE",""));
            workformProduct.put("RECORD_NUM", workformProductSize);
            workformProduct.put("PRODUCT_TYPE_CODE", offerData.getString("CATALOG_ID", ""));
            workformProduct.put("USER_ID", offerData.getString("USER_ID", ""));
            workformProduct.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER", ""));
            workformProductList.add(workformProduct);
        }
        else
        {
            int workformProductSubSize = workformProductSubList.size()+1;
            int recordNum = offerData.getInt("RECORD_NUM", workformProductSubSize);
            IData workformProduct = new DataMap();
            workformProduct.put("PRODUCT_ID", offerData.getString("OFFER_CODE"));
            workformProduct.put("PRODUCT_NAME", offerData.getString("OFFER_NAME"));
            workformProduct.put("RSRV_STR1", offerData.getString("OFFER_ID"));
            workformProduct.put("RSRV_STR2", offerData.getString("IS_EXPER_DATALINE",""));
            workformProduct.put("RECORD_NUM", recordNum);
            workformProduct.put("PRODUCT_TYPE_CODE", offerData.getString("CATALOG_ID", ""));
            workformProduct.put("USER_ID", offerData.getString("USER_ID", ""));
            workformProduct.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER", ""));
            workformProductSubList.add(workformProduct);
            
//            buildWorkformAttrList(offerData.getDataset("OFFER_CHA", new DatasetList()), recordNum);
            
            IDataset subOfferList = offerData.getDataset("SUBOFFERS");
            if(IDataUtil.isNotEmpty(subOfferList))
            {
                for(int i = 0, size = subOfferList.size(); i < size; i++)
                {
                    IData subOffer = subOfferList.getData(i);
                    if(UpcConst.ELEMENT_TYPE_CODE_SVC.equals(subOffer.getString("OFFER_TYPE")))
                    {
                        buildWorkformSvcList(subOffer, i, recordNum);
                    }
                    else if(UpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(subOffer.getString("OFFER_TYPE")))
                    {
                        buildWorkformDisList(subOffer, i, recordNum);
                    }
                }
            }
        }
    }


    /**
     * 构造流程产品信息
     * @param offerData
     * @throws Exception
     */
    private static void buildWorkformMinoreProductList(IData offerData) throws Exception
    {
        int workformProductSize = workformProductList.size();
        IData workformProduct = new DataMap();
        workformProduct.put("PRODUCT_ID", offerData.getString("OFFER_CODE"));
        workformProduct.put("PRODUCT_NAME", offerData.getString("OFFER_NAME"));
        workformProduct.put("RSRV_STR1", offerData.getString("OFFER_ID"));
        workformProduct.put("RSRV_STR2", offerData.getString("IS_EXPER_DATALINE",""));
        workformProduct.put("RSRV_STR5", offerData.getString("DEAL_TYPE",""));
        workformProduct.put("RECORD_NUM", workformProductSize);
        workformProduct.put("PRODUCT_TYPE_CODE", offerData.getString("CATALOG_ID", ""));
        workformProduct.put("USER_ID", offerData.getString("USER_ID", ""));
        workformProduct.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER", ""));
        workformProductList.add(workformProduct);

        IDataset productExtList = offerData.getDataset("PRODUCT_EXT");
        if(IDataUtil.isNotEmpty(productExtList))
        {
            for (int j =0;j<productExtList.size();j++)
            {
                int productExtSize = workformProductExtList.size();
                IData productExt = productExtList.getData(j);
                IData workformProductext = new DataMap();
                workformProductext.put("PRODUCT_ID", productExt.getString("OFFER_CODE"));
                workformProductext.put("PRODUCT_NAME", productExt.getString("OFFER_NAME"));
                workformProductext.put("RSRV_STR1", productExt.getString("OFFER_ID",productExt.getString("OFFER_CODE")));
                workformProductext.put("RSRV_STR5", productExt.getString("RSRV_STR5", ""));
                workformProductext.put("PARENT_RECORD_NUM", workformProductSize);
                workformProductext.put("RECORD_NUM", productExtSize);
                workformProductext.put("PRODUCT_TYPE_CODE", productExt.getString("CATALOG_ID", ""));
                workformProductext.put("USER_ID", productExt.getString("USER_ID", ""));
                workformProductext.put("SERIAL_NUMBER", productExt.getString("SERIAL_NUMBER", ""));
                workformProductExtList.add(workformProductext);
            }
        }
    }




    
    /**
     * 构造流程业务数据
     * @param offerChaList
     * @param recordNum
     * @throws Exception
     */
    private static void buildWorkformAttrList(IDataset offerChaList, int recordNum) throws Exception
    {
        if(IDataUtil.isEmpty(offerChaList))
        {
            return ;
        }
        for(int j = 0, sizeJ = offerChaList.size(); j < sizeJ; j++)
        {
            IData subOfferCha = offerChaList.getData(j);
            IData workformAttr = new DataMap();
            workformAttr.put("ATTR_CODE", StringUtils.substringAfter(subOfferCha.getString("ATTR_CODE"), "_"));
            workformAttr.put("ATTR_NAME", subOfferCha.getString("ATTR_NAME"));
            workformAttr.put("ATTR_VALUE", subOfferCha.getString("ATTR_VALUE"));
            workformAttr.put("RECORD_NUM", recordNum);
            workformAttrList.add(workformAttr);
        }
    }
    
    /**
     * 构造流程服务信息
     * @param subSvcOffer
     * @param attrCode
     * @param recordNum
     * @throws Exception
     */
    private static void buildWorkformSvcList(IData subSvcOffer, int attrCode, int recordNum) throws Exception
    {
        Iterator<String> itr = subSvcOffer.keySet().iterator();
        while(itr.hasNext())
        {
            String key = itr.next();
            String value = subSvcOffer.getString(key);
            IData workformSvc = new DataMap();
            workformSvc.put("PARENT_ATTR_CODE", -1);
            workformSvc.put("ATTR_CODE", attrCode);
            workformSvc.put("ATTR_NAME", key);
            if("OFFER_CHA".equals(key))
            {
                workformSvc.put("ATTR_VALUE", "");
            }
            else
            {
                workformSvc.put("ATTR_VALUE", StringUtils.isNotBlank(value) ? value : "");
            }
            workformSvc.put("RECORD_NUM", recordNum);
            workformSvc.put("GROUP_SEQ", attrCode);
            workformSvcList.add(workformSvc);
        }
        
        IDataset subOfferChaList = subSvcOffer.getDataset("OFFER_CHA", new DatasetList());
        for(int j = 0, sizeJ = subOfferChaList.size(); j < sizeJ; j++)
        {
            IData subOfferCha = subOfferChaList.getData(j);
            IData workformSvc = new DataMap();
            workformSvc.put("PARENT_ATTR_CODE", attrCode);
            workformSvc.put("ATTR_CODE", attrCode+100);
            workformSvc.put("ATTR_NAME", subOfferCha.getString("ATTR_CODE"));
            workformSvc.put("ATTR_VALUE", subOfferCha.getString("ATTR_VALUE"));
            workformSvc.put("RSRV_STR1", subOfferCha.getString("ATTR_NAME"));
            workformSvc.put("RECORD_NUM", recordNum);
            workformSvc.put("GROUP_SEQ", attrCode);
            workformSvcList.add(workformSvc);
        }
    }
    
    /**
     * 构造流程资费信息
     * @param subDisOffer
     * @param attrCode
     * @param recordNum
     * @throws Exception
     */
    private static void buildWorkformDisList(IData subDisOffer, int attrCode, int recordNum) throws Exception
    {
        Iterator<String> itr = subDisOffer.keySet().iterator();
        while(itr.hasNext())
        {
            String key = itr.next();
            String value = subDisOffer.getString(key);
            IData workformDis = new DataMap();
            workformDis.put("PARENT_ATTR_CODE", -1);
            workformDis.put("ATTR_CODE", attrCode);
            workformDis.put("ATTR_NAME", key);
            if("OFFER_CHA".equals(key))
            {
                workformDis.put("ATTR_VALUE", "");
            }
            else
            {
                workformDis.put("ATTR_VALUE", StringUtils.isNotBlank(value) ? value : "");
            }
            workformDis.put("RECORD_NUM", recordNum);
            workformDis.put("GROUP_SEQ", attrCode);
            workformDisList.add(workformDis);
        }
        
        IDataset subOfferChaList = subDisOffer.getDataset("OFFER_CHA", new DatasetList());
        for(int j = 0, sizeJ = subOfferChaList.size(); j < sizeJ; j++)
        {
            IData subOfferCha = subOfferChaList.getData(j);
            IData workformDis = new DataMap();
            workformDis.put("PARENT_ATTR_CODE", attrCode);
            workformDis.put("ATTR_CODE", attrCode+100);
            workformDis.put("ATTR_NAME", subOfferCha.getString("ATTR_CODE"));
            workformDis.put("ATTR_VALUE", subOfferCha.getString("ATTR_VALUE"));
            workformDis.put("RSRV_STR1", subOfferCha.getString("ATTR_NAME"));
            workformDis.put("RECORD_NUM", recordNum);
            workformDis.put("GROUP_SEQ", attrCode);
            workformDisList.add(workformDis);
        }
    }
    
    private static void buildWorkformNodeData(IData nodeTempleteData, String productId) throws Exception
    {
    	if (IDataUtil.isNotEmpty(nodeTempleteData)) 
    	{
    	   workformNodeData.put("NODE_ID", nodeTempleteData.getString("NODE_ID"));
		}
        workformNodeData.put("PRODUCT_ID", productId);
    }
    
    private static void buildWorkformOtherList(IDataset otherList) throws Exception
    {
        if(IDataUtil.isEmpty(otherList))
        {
            return ;
        }
        
        for(int i = 0, size = otherList.size(); i < size; i++)
        {
            IData other = otherList.getData(i);
            IData wfOther = new DataMap();
            wfOther.put("ATTR_CODE", other.getString("ATTR_CODE"));
            wfOther.put("ATTR_NAME", other.getString("ATTR_NAME"));
            wfOther.put("ATTR_VALUE", other.getString("ATTR_VALUE"));
            wfOther.put("RECORD_NUM", other.getString("RECORD_NUM","0"));
            workformOtherList.add(wfOther);
            workformSpcOtherList.add(wfOther);
        }
        eosCommonData.put("OTHER_INFO", workformSpcOtherList);
    }
    
    private static void buildWorkformSpcOtherList(IDataset spcOtherList) throws Exception {
        if(IDataUtil.isEmpty(spcOtherList)) {
            return;
        }

        for (int i = 0, size = spcOtherList.size(); i < size; i++) {
            IData other = spcOtherList.getData(i);
            IData wfOther = new DataMap();
            wfOther.put("ATTR_CODE", other.getString("ATTR_CODE"));
            wfOther.put("ATTR_NAME", other.getString("ATTR_NAME"));
            wfOther.put("ATTR_VALUE", other.getString("ATTR_VALUE"));
            wfOther.put("RECORD_NUM", other.getString("RECORD_NUM", "0"));
            workformOtherList.add(wfOther);
        }
    }

    private static void buildWorkformAttachList(IDataset attachList) throws Exception
    {
        if(IDataUtil.isEmpty(attachList))
        {
            return ;
        }
        for(int i = 0, size = attachList.size(); i < size; i++)
        {
            IData attach = attachList.getData(i);
            IData wfAttach = new DataMap();
            wfAttach.put("ATTACH_TYPE", attach.getString("ATTACH_TYPE"));
            wfAttach.put("FILE_ID", attach.getString("FILE_ID"));
            wfAttach.put("DISPLAY_NAME", attach.getString("FILE_NAME"));
            wfAttach.put("ATTACH_NAME", attach.getString("FILE_NAME"));
            wfAttach.put("ATTACH_LENGTH", attach.getString("FILE_SIZE"));
            wfAttach.put("REMARK", attach.getString("REMARK"));
            wfAttach.put("GROUP_SEQ", "0");
            wfAttach.put("RECORD_NUM", "0");
            workformAttachList.add(wfAttach);
        }
    }
    
    private static void buildContractData(IDataset contractData) throws Exception
    {
        if(IDataUtil.isEmpty(contractData))
        {
            return ;
        }
        IData temp = null;
        IData workformAttr = null;
        for(int i=0; i<contractData.size(); i++){
        	temp = contractData.getData(i);
        	workformAttr = new DataMap();
        	workformAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
        	workformAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
        	workformAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));
        	workformAttr.put("ATTR_TYPE", "1");
        	if(temp.getString("RECORD_NUM")!=null&&!temp.getString("RECORD_NUM","").equals("")){
            	workformAttr.put("RECORD_NUM", temp.getString("RECORD_NUM",""));
        	}else{
            	workformAttr.put("RECORD_NUM", "0");
        	}
            workformAttrList.add(workformAttr);
        }
    }
    
    private static void buildTapMarketingLineData(IDataset tapMarketingData) throws Exception
    {
        if(IDataUtil.isEmpty(tapMarketingData))
        {
            return ;
        }
		IDataset linePriceList=StaticUtil.getList(null, "TD_B_EWE_CONFIG", "PARAMNAME", "PARAMVALUE", new String[] {
	            "CONFIGNAME"
	        }, new String[] {
	            "LINEPRICE_EXCITATION"
	        });
		System.out.println("ScrDataTrans buildTapMarketingLineData-linePriceList:"+linePriceList);
		
        for(int i=0; i<tapMarketingData.size(); i++){
        	IData linePriceTap = new DataMap();
    		IData linePriceExc = new DataMap();
    		boolean linePriceTapFlag=false;

        	IData lineData = tapMarketingData.getData(i);
        	IDataset tapMarketingListData=lineData.getDataset("OFFER_CHA");
        	if(IDataUtil.isNotEmpty(tapMarketingListData))
            {
        		for (int j=0; j<tapMarketingListData.size(); j++){
            		boolean linePriceAdd=true;
                	IData temp = tapMarketingListData.getData(j);
                	if(IDataUtil.isNotEmpty(temp))
                    {
                		IData workformAttr = new DataMap();
                    	workformAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
                    	workformAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
                    	workformAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));
                    	workformAttr.put("RECORD_NUM", i+1);
                    	
                        
                        if(workformAttr.getString("ATTR_CODE").equals("NOTIN_RSRV_STR2")&&!workformAttr.getString("ATTR_VALUE","").equals("")){
                        	int val=addLinePrice(workformAttr.getString("ATTR_VALUE"),linePriceList);
                    		System.out.println("ScrDataTrans buildTapMarketingLineData-NOTIN_LINEPRICE_TAP:"+val);
                    		linePriceTap = new DataMap();
                    		linePriceTap.put("ATTR_VALUE", val);
                    		linePriceTap.put("ATTR_CODE", "NOTIN_LINEPRICE_TAP");
                    		linePriceTap.put("ATTR_NAME", "录入资费激励金额");
                    		linePriceTap.put("RECORD_NUM", i+1);
                        }
                        if(workformAttr.getString("ATTR_CODE").equals("NOTIN_MONTHLYFEE_EXCITATION")&&!workformAttr.getString("ATTR_VALUE","").equals("")){
                        	int val=addLinePrice(workformAttr.getString("ATTR_VALUE"),linePriceList);
                    		System.out.println("ScrDataTrans buildTapMarketingLineData-NOTIN_LINEPRICE_EXCITATION:"+val);
                    		linePriceExc = new DataMap();
                    		linePriceExc.put("ATTR_VALUE", val);
                    		linePriceExc.put("ATTR_CODE", "NOTIN_LINEPRICE_EXCITATION");
                    		linePriceExc.put("ATTR_NAME", "实际激励金额（元）");
                    		linePriceExc.put("RECORD_NUM", i+1);
                        }
                        if(workformAttr.getString("ATTR_CODE").equals("NOTIN_LINEPRICE_TAP")&&!workformAttr.getString("ATTR_VALUE","").equals("")){
                        	linePriceTapFlag=true;//不可更新
                            workformAttrList.add(workformAttr);
                        }else if(workformAttr.getString("ATTR_CODE").equals("NOTIN_LINEPRICE_EXCITATION")&&workformAttr.getString("ATTR_VALUE","").equals("")){
                        	
                        }else{
                            workformAttrList.add(workformAttr);
                        }
                    }
                	
                	
            	}
        		if(!linePriceTapFlag&&linePriceTap.getString("ATTR_VALUE")!=null){
                    workformAttrList.add(linePriceTap);

                }
                if(linePriceExc.getString("ATTR_VALUE")!=null){
                    workformAttrList.add(linePriceExc);
                }
        		
            }
        	
        	
        	
        }
        
    }
    private static int addLinePrice(String rsrvStr2,IDataset linePriceList){
		int linePriceValue0=0;
		int linePriceKey=-1;
		int linePriceValue=0;
    	if(rsrvStr2!=null&&IDataUtil.isNotEmpty(linePriceList)){
			double a =Math.ceil(Double.valueOf(rsrvStr2));//向上取整
	    	int  linePrice=Double.valueOf(a).intValue();
			for(int j=0,sizej=linePriceList.size();j<sizej;j++){
             	IData linePriceExcitationData=linePriceList.getData(j);
				int  linePriceKeyi=Integer.parseInt(linePriceExcitationData.getString("PARAMNAME"));
				if(linePriceKeyi==-1){//循环参数价格为-1时为最大价格，当下述循环未能满足，则使用最大价格
					linePriceValue0=Integer.parseInt(linePriceExcitationData.getString("PARAMVALUE"));
				}else if (linePrice<=linePriceKeyi){//制定价格小于等于循环参数价格
					if(linePriceKey>linePriceKeyi||linePriceKey==-1){//当前参数价格大于循环参数价格，则替换，冒泡取出最小值
						linePriceKey=linePriceKeyi;
						linePriceValue=Integer.parseInt(linePriceExcitationData.getString("PARAMVALUE"));
					}
				}
			}
    			
    	}
    	if(linePriceKey==-1){
			return linePriceValue0;
		}else{
			return linePriceValue;
		}
    }
    private static void buildTapMarketingData(IDataset tapMarketingData) throws Exception
    {
        if(IDataUtil.isEmpty(tapMarketingData))
        {
            return ;
        }
        IData temp = null;
        IData workformAttr = null;
        for(int i=0; i<tapMarketingData.size(); i++){
        	temp = tapMarketingData.getData(i);
        	workformAttr = new DataMap();
        	workformAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
        	workformAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
        	workformAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));
        	workformAttr.put("RECORD_NUM", "0");
            workformAttrList.add(workformAttr);
        }
    }
    
    private static void buildIDCData(IDataset idcData) throws Exception
    {
        if(IDataUtil.isEmpty(idcData))
        {
            return ;
        }
        IData temp = null;
        IData workformAttr = null;
        for(int i=0; i<idcData.size(); i++){
        	temp = idcData.getData(i);
        	workformAttr = new DataMap();
        	workformAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
        	workformAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
        	workformAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));
        	if(temp.getString("GROUP_SEQ")!=null){
            	workformAttr.put("GROUP_SEQ", temp.getString("GROUP_SEQ"));
        	}
        	if(temp.getString("RECORD_NUM")!=null){
            	workformAttr.put("RECORD_NUM", temp.getString("RECORD_NUM"));
        	}else{
            	workformAttr.put("RECORD_NUM", "0");
        	}
            workformAttrList.add(workformAttr);
            
            if("ORDER_LastIbsysidFlag".equals(temp.getString("ATTR_CODE"))){//是否最后一笔订单 特殊处理,也入other表
                workformOtherList.add(workformAttr);
            }
        }
    }
    
    private static void buildAccountData(IData acctData) throws Exception
    {
        if(IDataUtil.isEmpty(acctData))
        {
            return ;
        }
        Iterator<String> itr = acctData.keySet().iterator();
        while(itr.hasNext())
        {
            String attrCode = itr.next();
            IData workformAttr = new DataMap();
            workformAttr.put("ATTR_CODE", attrCode);
            workformAttr.put("ATTR_VALUE", acctData.getString(attrCode));
            workformAttr.put("ATTR_TYPE", "2"); //2-账户
            workformAttr.put("RECORD_NUM", "0");
            workformAttrList.add(workformAttr);
        }
    }
    
    private static void buildEosCommonData(IData busiSpecRele, IData nodeTempleteData, IData commonData) throws Exception
    {
    	if(IDataUtil.isNotEmpty(busiSpecRele))
    	{
            eosCommonData.put("BPM_TEMPLET_ID", busiSpecRele.getString("BPM_TEMPLET_ID"));
            eosCommonData.put("BUSI_TYPE", busiSpecRele.getString("BUSI_TYPE"));
            eosCommonData.put("BUSI_CODE", busiSpecRele.getString("BUSI_CODE"));
            eosCommonData.put("IN_MODE_CODE", busiSpecRele.getString("IN_MODE_CODE"));
            eosCommonData.put("BUSIFORM_OPER_TYPE", busiSpecRele.getString("BUSIFORM_OPER_TYPE"));
    	}
    	if(IDataUtil.isNotEmpty(nodeTempleteData))
    	{
    		eosCommonData.put("NODE_ID", nodeTempleteData.getString("NODE_ID"));
    	}
       
        if (IDataUtil.isNotEmpty(commonData))
        {
            eosCommonData.putAll(commonData);
		}
    }
    
    private static void buildQuickOrderOfferList(IDataset quickOrderOfferList) throws Exception
    {
        if(IDataUtil.isEmpty(quickOrderOfferList))
        {
            return ;
        }

        for(int i = 0, size = quickOrderOfferList.size(); i < size; i++)
        {
            IData quickOrderOffer = quickOrderOfferList.getData(i);
            if(StringUtils.isBlank(quickOrderOffer.getString("CUST_ID")))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "快速办理商品数据客户标识CUST_ID不能为空！");
            }
            if(StringUtils.isBlank(quickOrderOffer.getString("PRODUCT_ID")))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "快速办理商品数据产品标识PRODUCT_ID不能为空！");
            }

            IData codingData = quickOrderOffer.getData("CODING_STR");
            String shortCode = quickOrderOffer.getString("SHORT_CODE");
            String codingStr = codingData.toString();
            if(StringUtils.isNotEmpty(shortCode))
            {
                codingStr= codingStr.replace("#SHORT_CODE#",shortCode);
            }

            IData quickOrderCond = new DataMap();
            quickOrderCond.put("CUST_ID", quickOrderOffer.getString("CUST_ID"));
            quickOrderCond.put("PRODUCT_ID", quickOrderOffer.getString("PRODUCT_ID"));
            String dealType = quickOrderOffer.getString("DEAL_TYPE","");
            quickOrderCond.put("RSRV_STR1", quickOrderOffer.getString("RSRV_STR1"));
            quickOrderCond.put("RSRV_STR2", dealType);
            quickOrderCond.put("RSRV_STR5", quickOrderOffer.getString("RSRV_STR5"));
            quickOrderCond.put("SERIAL_NUMBER", "ESP".equals(dealType)?"-1":quickOrderOffer.getString("SERIAL_NUMBER"));
            quickOrderCond.putAll(dealQuickOrderCondition(codingStr));
            
            workformQuickOrderCondList.add(quickOrderCond);

        }
    }

    private static void buildQuickOrderOfferMebList(IDataset quickOrderOfferMebList) throws Exception
    {
        if(IDataUtil.isEmpty(quickOrderOfferMebList))
        {
            return ;
        }

        for(int i = 0, size = quickOrderOfferMebList.size(); i < size; i++)
        {
            IData quickOrderOffer = quickOrderOfferMebList.getData(i);

            IData quickOrderMeb = new DataMap();
            quickOrderMeb.put("PRODUCT_ID",quickOrderOffer.getString("PRODUCT_ID"));
            quickOrderMeb.put("CUST_ID",quickOrderOffer.getString("CUST_ID"));
            quickOrderMeb.put("SERIAL_NUMBER",quickOrderOffer.getString("SERIAL_NUMBER"));
            quickOrderMeb.put("EC_SERIAL_NUMBER",quickOrderOffer.getString("EC_SERIAL_NUMBER"));
            quickOrderMeb.put("RSRV_STR1",quickOrderOffer.getString("RSRV_STR1"));
            quickOrderMeb.putAll(dealQuickOrderCondition(quickOrderOffer.toString()));

            workformQuickOrderMebList.add(quickOrderMeb);
        }
    }

    private static void buildQuickOrderOfferDataList(IDataset quickOrderOfferDataList) throws Exception
    {
        if(IDataUtil.isEmpty(quickOrderOfferDataList))
        {
            return ;
        }

        for(int i = 0, size = quickOrderOfferDataList.size(); i < size; i++)
        {
            IData quickOrderOffer = quickOrderOfferDataList.getData(i);
            IData quickOrderData = new DataMap();
            quickOrderData.put("PRODUCT_ID",quickOrderOffer.getString("PRODUCT_ID"));
            quickOrderData.put("CUST_ID",quickOrderOffer.getString("CUST_ID"));
            quickOrderData.put("SERIAL_NUMBER",quickOrderOffer.getString("SERIAL_NUMBER"));
            quickOrderData.put("EC_SERIAL_NUMBER",quickOrderOffer.getString("EC_SERIAL_NUMBER",""));
            quickOrderData.put("RSRV_STR1",quickOrderOffer.getString("RSRV_STR1"));
            quickOrderData.put("RSRV_STR2",quickOrderOffer.getString("DEAL_TYPE"));

            //ESP产品新增ESP发单人信息
            if ("ESP".equals(quickOrderOffer.getString("DEAL_TYPE"))) {
            	quickOrderData.put("RSRV_STR3",quickOrderOffer.getString("ORDER_STAFF_ID"));
            	quickOrderData.put("RSRV_STR4",quickOrderOffer.getString("ORDER_STAFF_PHONE"));
            }

            quickOrderData.putAll(dealQuickOrderCondition(quickOrderOffer.toString()));

            workformQuickOrderDataList.add(quickOrderData);

        }
    }
    
    private static IData dealQuickOrderCondition(String quickOrderOfferStr) throws Exception
    {
        IDataset codingList = StringSubsection(quickOrderOfferStr, 1800, "GBK");
        
        if (codingList.size() > 10)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "订购条件字符串太长!");
        }

        IData cond = new DataMap();
        for (int i = 0, size = codingList.size(); i < size; i++)
        {
            cond.put("CODING_STR" + (i + 1), codingList.get(i));
        }
        return cond;
    }

    private static IDataset StringSubsection(String source, int byteLength, String charset) throws UnsupportedEncodingException {
    	if (source == null || source.length() == 0)
            return new DatasetList();
        byte[] sByte = source.getBytes(charset);
        char[] sChar = source.toCharArray();
        IDataset dataset = new DatasetList();
        if (sByte.length <= byteLength)
        {
            dataset.add(source);
        }
        else
        {
            int byleCount = 0;
            int first = 0;
            for (int i = 0; i < sChar.length; i++)
            {
                if (sChar[i] > 0x80)
                {
                    byleCount += 2;
                }
                else
                {
                    byleCount += 1;
                }
                if (byleCount == byteLength)
                {
                    if (first == 0)
                        dataset.add(new String(sChar, first, i + 1));
                    else
                        dataset.add(new String(sChar, first + 1, i - first));
                    first = i;
                    byleCount = 0;
                }
                if (byleCount == byteLength + 1)
                {
                    if (first == 0)
                        dataset.add(new String(sChar, first, i));
                    else
                        dataset.add(new String(sChar, first + 1, i - first - 1));
                    first = i - 1;
                    byleCount = 2;
                }
            }
            if (byleCount != 0)
                dataset.add(new String(sChar, first + 1, sChar.length - first - 1));
        }
        return dataset;
	}


    private static void init() throws Exception
    {
        workformSubscriberData = new DataMap();
        workformNodeData = new DataMap();
        workformProductList = new DatasetList();
        workformAttrList = new DatasetList();
        workformSvcList = new DatasetList();
        workformDisList = new DatasetList();
        workformOtherList = new DatasetList();
        workformAttachList = new DatasetList();
        workformProductSubList = new DatasetList();
        workformSpcOtherList = new DatasetList();
        workformQuickOrderCondList = new DatasetList();
        workformQuickOrderMebList = new DatasetList();
        workformQuickOrderDataList = new DatasetList();
        workformProductExtList = new DatasetList();

        eosCommonData = new DataMap();
    }
    
    public static IData buildEosCommonData(IData param) throws Exception
    {
        IData eosCommonData = new DataMap();
        eosCommonData.put("IBSYSID", param.getString("IBSYSID"));
        eosCommonData.put("BUSIFORM_ID", param.getString("BUSIFORM_ID"));
        eosCommonData.put("NODE_ID", param.getString("NODE_ID"));
        eosCommonData.put("BUSIFORM_NODE_ID", param.getString("BUSIFORM_NODE_ID"));
        eosCommonData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        eosCommonData.put("IN_MODE_CODE", param.getString("IN_MODE_CODE"));
        eosCommonData.put("DEAL_STATE", param.getString("DEAL_STATE"));
        eosCommonData.put("BUSI_CODE", param.getString("BUSI_CODE"));
        eosCommonData.put("BUSI_TYPE", param.getString("BUSI_TYPE"));
        eosCommonData.put("GROUP_ID", param.getString("GROUP_ID"));
        return eosCommonData;
    }
    
    private static void buildDirectlineAttrData(IDataset directlineDate) throws Exception
    {
        if(IDataUtil.isEmpty(directlineDate))
        {
            return ;
        }
        IData temp = null;
        IData dAttr = null;
        for(int i=0; i<directlineDate.size(); i++){
        	IDataset temp2 = directlineDate.getDataset(i);
        	for (int j = 0; j < temp2.size(); j++) {
        		temp = temp2.getData(j);
        		dAttr = new DataMap();
        		if(temp.getString("ATTR_CODE").contains("pattr_")) {
        			dAttr.put("ATTR_CODE", StringUtils.substringAfter(temp.getString("ATTR_CODE"), "_"));
        		}else {
        			dAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
        		}
        		//数据专线三个比例数据需要加百分号
        		String attrCode = temp.getString("ATTR_CODE");
        		if(attrCode.equals("NOTIN_RSRV_STR6")||attrCode.equals("NOTIN_RSRV_STR7")||attrCode.equals("NOTIN_RSRV_STR8")||
        		   attrCode.equals("pam_NOTIN_A_PERCENT")||attrCode.equals("pam_NOTIN_GROUP_PERCENT")||attrCode.equals("pam_NOTIN_Z_PERCENT")) {
        			dAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE")+"%");
        		}else {
        			dAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
        		}
        		dAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));
        		dAttr.put("RECORD_NUM", i+1);
        		workformAttrList.add(dAttr);
			}
        }
    }
    
    private static void buildDelParamAttrData(IDataset eomsAttrList) throws Exception
    {
        if(IDataUtil.isEmpty(eomsAttrList))
        {
            return ;
        }
        IData temp = null;
        IData eomsAttr = null;
        for(int i=0; i<eomsAttrList.size(); i++){
        	temp = eomsAttrList.getData(i);
        	eomsAttr = new DataMap();
        	eomsAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
        	eomsAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
        	eomsAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));
        	eomsAttr.put("RECORD_NUM", "0");
            workformAttrList.add(eomsAttr);
        }
    }
    
    private static void buildEomsAttrData(IDataset eomsAttrList) throws Exception
    {
        if(IDataUtil.isEmpty(eomsAttrList))
        {
            return ;
        }
        IData temp = null;
        IData eomsAttr = null;
        for(int i=0; i<eomsAttrList.size(); i++){
        	temp = eomsAttrList.getData(i);
        	eomsAttr = new DataMap();
        	eomsAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
        	eomsAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
        	eomsAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));
        	if(temp.getString("RECORD_NUM")!=null){
            	eomsAttr.put("RECORD_NUM", temp.getString("RECORD_NUM"));
        	}else{
            	eomsAttr.put("RECORD_NUM", "0");
        	}
            workformAttrList.add(eomsAttr);
        }
    }
    
    private static void buildAttrData(IDataset attrDate) throws Exception
    {
    	if(IDataUtil.isEmpty(attrDate))
        {
            return ;
        }
        IData temp = null;
        IData eomsAttr = null;
        for(int i=0; i<attrDate.size(); i++){
        	temp = attrDate.getData(i);
        	eomsAttr = new DataMap();
        	eomsAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
        	eomsAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
        	eomsAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));
        	eomsAttr.put("RECORD_NUM", temp.getString("RECORD_NUM"));
            workformAttrList.add(eomsAttr);
        }
    }
    
}
