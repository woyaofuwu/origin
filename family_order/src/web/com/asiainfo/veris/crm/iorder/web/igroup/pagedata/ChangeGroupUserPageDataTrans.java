package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.usersvc.UserSvcInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.usersvc.UserSvcInfoIntfViewUtil;

public class ChangeGroupUserPageDataTrans extends PageDataTrans
{
    @Override
    public IData transformData() throws Exception
    {
        super.transformData();
        
        IData data = new DataMap();
        
        data.put("PRODUCT_ID", getProductId());
        data.put("USER_INFO", getEcSubscriber());

        IData custInfo = getEcCustomer();
        data.put("USER_EPARCHY_CODE", custInfo.getString("EPARCHY_CODE"));
        
        IDataset offerInfos = getOfferList();
        if(DataUtils.isEmpty(offerInfos))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到商品数据结构！");
        }
        
        IDataset offerInfoList = transformOfferList(offerInfos);
        data.put("ELEMENT_INFO", offerInfoList);
        
        IDataset compOfferChaList = getOfferChaList();
        if(DataUtils.isNotEmpty(compOfferChaList))
        {
            data.put("PRODUCT_PARAM_INFO", transformOfferChaList(getProductId(), compOfferChaList));
        }
        
        IDataset grpPackages = offerInfos.first().getDataset("GRP_PACKAGE_INFO");
        if(DataUtils.isNotEmpty(grpPackages))
        {//定制
        	for(int i=0;i<grpPackages.size();i++){
        		if("3".equals(grpPackages.getData(i).getString("MODIFY_TAG"))){
        			grpPackages.remove(i--);
        		}
        	}
        	data.put("GRP_PACKAGE_INFO", grpPackages);
        }
        if(DataUtils.isNotEmpty(offerInfos.first().getDataset("POWER100_PRODUCT_INFO")))
        {//动力100子商品
        	data.put("POWER100_PRODUCT_INFO", offerInfos.first().getDataset("POWER100_PRODUCT_INFO"));
        }
        String subscriberInsId = offerInfos.first().getString("USER_ID");
        if(StringUtils.isBlank(subscriberInsId))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到用户实例标识！");
        }
        data.put("USER_ID", subscriberInsId);
        
        IData commonInfo = getCommonData();
        if(DataUtils.isNotEmpty(commonInfo)){
            IData eosInfo = commonInfo.getData("ESOP_INFO");
            if(DataUtils.isNotEmpty(eosInfo))
            {
            	data.put("EOS_INFO", eosInfo);
            }
            
            data.put("POST_INFO", commonInfo.getDataset("POST_INFO"));
            data.put("ASKPRINT_INFO", transAskPostInfoList(commonInfo));
            data.put("REMARK", commonInfo.getString("REMARK", ""));
        }
        if(getProductId().equals("8000")){
            IData reData = readyToVpmnUp(offerInfos); 
            String oldvpnscare = reData.getString("OLD_VPN_SCARE_CODE", "0");
            String vpnscare = reData.getString("VPN_SCARE_CODE", "");
            boolean hasAdd801 = reData.getBoolean("HAS_ADD_801", false);
            
            data.put("HAS_ADD_801", reData.getBoolean("HAS_ADD_801", false)); 
            data.put("HAS_DEL_801", reData.getBoolean("HAS_DEL_801", false)); 
            data.put("OLD_801_FLAG", reData.getBoolean("OLD_801_FLAG", false)); 
            
            if (!oldvpnscare.equals("2") && (vpnscare.equals("2") || hasAdd801)){
            	
            	IData batparam = new DataMap();
            	for(int j=0;j<compOfferChaList.size();j++){
            		IData comp = compOfferChaList.getData(j);
            		String attrValue = comp.getString("ATTR_VALUE");
            		String attrCode  = comp.getString("ATTR_CODE");
            		batparam.put(attrCode, attrValue);
            	}
            	
            	data.putAll(batparam);
            	
            	if ("2".equals(vpnscare))
                {
                    data.put("HAS_VPN_SCARE", true); // 选择了跨省升级
                }
                else
                {
                    data.put("HAS_VPN_SCARE", false); // 没选择跨省升级
                }
            }
        }
        
        return data;
    }
    
    public void setServiceName() throws Exception
    {
    	String productId = getProductId();
    	String brandCode = getBrandCode();
    	if("BOSG".equals(brandCode)){
    		setSvcName("CS.ChangeBBossUserSVC.crtOrder");
    	}else if ("6130".equals(productId))
        {// 融合总机
            setSvcName("SS.ChangeCentrexSuperTeleUserElementSVC.crtOrder");
        }else if ("8000".equals(productId))
        {//集团VPMN
            IDataset offerInfos = getOfferList();
            IData reData = readyToVpmnUp(offerInfos);
            String oldvpnscare = "0";
            String vpnscare = "";
            boolean hasAdd801 = false;
            if(IDataUtil.isNotEmpty(reData)){
                 oldvpnscare = reData.getString("OLD_VPN_SCARE_CODE", "0");
                 vpnscare = reData.getString("VPN_SCARE_CODE", "");
                 hasAdd801 = reData.getBoolean("HAS_ADD_801", false);
            }
            // modify by lixiuyu@20100512 VPN升级为跨省VPN， hasAdd801区别是否订购“漫游短号服务”
            if (!oldvpnscare.equals("2") && (vpnscare.equals("2") || hasAdd801))
            {
                // 批量升级
                setSvcName("SS.UpgradeVpnBeanSVC.crtBatUptoCountrywide");
             
                // 设置返回值
            }else{
                setSvcName(EcConstants.EC_OPER_SERVICE.CHANGE_ENTERPRISE_SUBSCRIBER.getValue());

            }
        }
    	/** VOIP专线（专网专线） */
    	else if ("7010".equals(productId))
        {
    		setSvcName("SS.ChangeVoipUserElementSVC.crtOrder");

        }
        /** 互联网专线接入（专网专线） */
    	else if ("7011".equals(productId))
        {
    		setSvcName("SS.ChangeNetinUserElementSVC.crtOrder");

        }
        /** 数据专线（专网专线） */
    	else if ("7012".equals(productId))
        {
    		setSvcName("SS.ChangeDatalineUserElementSVC.crtOrder");

        }else{
            setSvcName(EcConstants.EC_OPER_SERVICE.CHANGE_ENTERPRISE_SUBSCRIBER.getValue());
        }

    }

    /**
     * 满足跨省V网条件，则走批量升级服务
     * 
     * @param productElements
     * @return
     * @throws Exception
     */
    public IData readyToVpmnUp(IDataset productElements) throws Exception
    {

        IData result = new DataMap();
        if(DataUtils.isEmpty(productElements))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到商品数据结构！");
        }
  
        IDataset offerInfoList = transformOfferList(productElements);
        IDataset productParam = productElements.getData(0).getDataset("OFFER_CHA_SPECS");
        if (IDataUtil.isEmpty(productParam))
            return null;
        String oldvpnscare = "0";
        String vpnscare = "";
        String vpnno = "";
        
        for(int k=0;k<productParam.size();k++){
        	String attrValue = productParam.getData(k).getString("ATTR_CODE");
        	if("OLD_VPN_SCARE_CODE".equals(attrValue)){
        		oldvpnscare = productParam.getData(k).getString("ATTR_VALUE");
        	}else if("VPN_SCARE_CODE".equals(attrValue)){
        		vpnscare = productParam.getData(k).getString("ATTR_VALUE");
        	}else if("VPN_NO".equals(attrValue)){
        		vpnno = productParam.getData(k).getString("ATTR_VALUE");
        	}
        }
        
        boolean hasAdd801 = false; // 是否有增加801元素
        boolean hasDel801 = false; // 是否有删除801元素
        if (IDataUtil.isNotEmpty(offerInfoList))
        {
            for (int i = 0, size = offerInfoList.size(); i < size; i++)
            {
                IData eleData = offerInfoList.getData(i);
                if ("801".equals(eleData.getString("ELEMENT_ID")) && TRADE_MODIFY_TAG.Add.getValue().equals(eleData.getString("MODIFY_TAG")))
                {
                    hasAdd801 = true;
                }
                if ("801".equals(eleData.getString("ELEMENT_ID")) && TRADE_MODIFY_TAG.DEL.getValue().equals(eleData.getString("MODIFY_TAG")))
                {
                    hasDel801 = true;
                }
            }
        }
        // add by lixiuyu@20100802 判断用户是否已经订购“漫游短号服务”，如果之前已经订购就不走批量
        boolean old801Flag = false;
        IDataset old801svcs = CommonViewCall.qryGrpUserSvcByUserSvcId(productElements.getData(0).getString("USER_ID"), "801");
        if (IDataUtil.isNotEmpty(old801svcs))
        {
            old801Flag = true;
        }

        result.put("HAS_ADD_801", hasAdd801);
        result.put("HAS_DEL_801", hasDel801);
        result.put("OLD_801_FLAG", old801Flag);
        result.put("VPN_SCARE_CODE", vpnscare);
        result.put("OLD_VPN_SCARE_CODE", oldvpnscare);
        return result;
    }

}
