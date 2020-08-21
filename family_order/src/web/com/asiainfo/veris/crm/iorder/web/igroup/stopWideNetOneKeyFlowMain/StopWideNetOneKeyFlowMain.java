package com.asiainfo.veris.crm.iorder.web.igroup.stopWideNetOneKeyFlowMain;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcutil.datainfo.uca.IUCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class StopWideNetOneKeyFlowMain extends GroupBasePage
{
    public abstract void setBusi(IData busi);

    public abstract void setOffer(IData offer);

    public abstract void setOffers(IDataset offers);
    
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setInit(IData init);
   
    /**
     * 作用：页面的初始化
     * 
     * @author luojh 2009-07-29
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData pagedata=this.getData();
        
        IData inint = new DataMap();
        String stateFlag = pagedata.getString("STATE_FLAG","");//暂停恢复标记
        inint.put("STATE_FLAG", stateFlag);
        setInit(inint);

    }
    
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData pagedata=this.getData();
        
        String custId = pagedata.getString("CUST_ID");
        
        IDataset offers = new DatasetList();
        
        IDataset  insOffers = IUCAInfoIntfViewUtil.qryUserAndProductByCustIdForGrp(this, custId);
        
        if (DataUtils.isNotEmpty(insOffers))
        {
            String groupId = "";
            String groupName = "";
            IData groupData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
            if (DataUtils.isNotEmpty(groupData))
            {
                groupId = groupData.getString("GROUP_ID");
                groupName = groupData.getString("CUST_NAME");
            }
            for (int j = 0, size = insOffers.size(); j < size; j++)
            {
                IData insoffer = insOffers.getData(j);
                String productId = insoffer.getString("PRODUCT_ID");
                IData PMoffer = UpcViewCall.queryOfferByOfferId(this, "P", productId,"");
                if(DataUtils.isEmpty(PMoffer)||!"7341".equals(productId)){
                    continue;
                }
                
                IData param = new DataMap();
                param.put("PRODUCT_ID",productId);
                //如果树上没有，直接remove
                IDataset productTypeDataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getProductTypesByProductId", param);

                if (DataUtils.isEmpty(productTypeDataset))
                {
                    continue;
                }
                
                String accessNum = insoffer.getString("SERIAL_NUMBER");
                String subscriberInsId = insoffer.getString("USER_ID");
                IData offer = new DataMap();
                offer.put("USER_ID", subscriberInsId);
                offer.put("SERIAL_NUMBER", accessNum);
                offer.put("OFFER_CODE", productId);
                offer.put("OFFER_ID", PMoffer.getString("OFFER_ID", ""));
                offer.put("OFFER_NAME", PMoffer.getString("OFFER_NAME", ""));
                offer.put("BRAND_CODE", insoffer.getString("BRAND_CODE", ""));
                offer.put("GROUP_ID", groupId);
                offer.put("GROUP_NAME", groupName);
                offer.put("CATALOG_DATA", buildCatalogData(offer));
                
                offers.add(offer);
            }
        }
        setOffers(offers);
            
    }
    
    private IData buildCatalogData(IData offerData) throws Exception
    {
        IData cataData = new DataMap();
        cataData.put("USER_ID", offerData.getString("USER_ID", ""));
        cataData.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER", ""));
        cataData.put("OFFER_ID", offerData.getString("OFFER_ID", ""));
        cataData.put("OFFER_CODE", offerData.getString("OFFER_CODE", ""));
        cataData.put("OFFER_NAME", offerData.getString("OFFER_NAME", ""));
        cataData.put("BRAND_CODE", offerData.getString("BRAND_CODE", ""));
        cataData.put("GROUP_ID", offerData.getString("GROUP_ID", ""));
        cataData.put("GROUP_NAME", offerData.getString("GROUP_NAME", ""));
        cataData.put("CUST_ID", offerData.getString("CUST_ID", ""));
        return cataData;
    }
    
    public void queryCustInfos(IRequestCycle cycle) throws Exception
    {
        IData pagedata = this.getData();
        
        String seralNumbr = pagedata.getString("SERIAL_NUMBER");
        
        IData userInfo = UCAInfoIntfViewUtil.qryUserInfoBySn(this, seralNumbr);
        
        if(IDataUtil.isEmpty(userInfo)){
            return;
        }
        
        String operType= getData().getString("OPER_TYPE");// STOP 暂停 ；OPEN 恢复
        if (StringUtils.isBlank(operType))
        {
            CSViewException.apperr(ParamException.CRM_PARAM_517);
        }
        if(!"STOP".equals(operType) && !"OPEN".equals(operType))
        {
            CSViewException.apperr(ParamException.CRM_PARAM_518);
        }
        
        String userStateCode=userInfo.getString("USER_STATE_CODESET");
        
        if("STOP".equals(operType) && "1".equals(userStateCode) )//暂停
        {
            CSViewException.apperr(CrmUserException.CRM_USER_16);
        }
        if("STOP".equals(operType) && "5".equals(userStateCode) )//暂停
        {
            CSViewException.apperr(CrmUserException.CRM_USER_783,"CRM中产品已欠费停机，不能再暂停！");
        }
        else if("OPEN".equals(operType) && "0".equals(userStateCode))//本身正常状态
        {
            CSViewException.apperr(CrmUserException.CRM_USER_15);
        }
        
        //QR-20200501-01 商务宽带产品恢复业务无法自动处理 by guonj@20200509
        IData qryParam = new DataMap();
        String staffId = this.getVisit().getStaffId();
        qryParam.put("TRADE_STAFF_ID", staffId);
		IDataset returnData =CSViewCall.call(this, "SS.StopWideNetOneKeyFlowMainSVC.qryWaittingPayOrderByStaffId", qryParam);
		if(IDataUtil.isNotEmpty(returnData)){
            IData insoffer = returnData.getData(0);
            boolean existStaffPay = insoffer.getBoolean("IS_EXIST_STAFF_PAY");
            if (existStaffPay) {
            	CSViewException.apperr(CrmUserException.CRM_USER_783,"此工号已存在未支付工单，不能再操作！");
			}
		}
		
        
        String userId = userInfo.getString("USER_ID");
        IData param = new DataMap();
        param.put("USER_ID",userId);
        IDataset  userInfos = CSViewCall.call(this, "SS.StopWideNetOneKeyFlowMainSVC.StopWideNetOneKeyFlowMainParam", param);
        IData offer = new DataMap(); 
        
        // 查询成员信息
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, "7341");

        String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, "7341");

        String mebCount = "0";

        if (brandCode.matches(GroupBaseConst.BB_BRAND_CODE))
        {
            mebCount = RelationBBInfoIntfViewUtil.qryCountByUserIdAAndRelationTypeCodeAllCrm(this, userId, relationTypeCode);
        }
        else if("BNBD".equals(brandCode)) //商务宽带
        {
            mebCount= RelationUUInfoIntfViewUtil.qryCountKDUUForAllCrm(this, userId, "47"); 
        }
        else
        {
            mebCount = RelationUUInfoIntfViewUtil.qryCountByUserIdAAndRelationTypeCodeAllCrm(this, userId, relationTypeCode);
        }

        if(IDataUtil.isNotEmpty(userInfos)){
            IData insoffer = userInfos.getData(0);
            IData productInfo = insoffer.getData("PRODUST_INFO");
            IData acctInfo = insoffer.getData("ACCT_INFO");//查询银行信息
            IData groupInfo = insoffer.getData("GROUP_INFO");//查询集团客户信息
            IDataset resInfo = insoffer.getDataset("RES_INFO");//查询res资源信息
            IDataset svcstateInfo = insoffer.getDataset("SVCSTATE_INFO");//查询svcstate表信息
            IData prodInfo = insoffer.getData("PROD_INFO");
            offer.put("GROUP_ID", groupInfo.getString("GROUP_ID"));
            offer.put("CUST_NAME", groupInfo.getString("GROUP_ID"));
            offer.put("USER_EPARCHY_CODE", groupInfo.getString("EPARCHY_CODE"));
            offer.put("GROUP_TYPE_NAME", groupInfo.getString("GROUP_TYPE_NAME"));
            offer.put("CLASS_NAME", groupInfo.getString("CLASS_NAME"));
            offer.put("ENTERPRISE_TYPE_NAME", groupInfo.getString("ENTERPRISE_TYPE_NAME"));
            offer.put("CALLING_TYPE_NAME", groupInfo.getString("CALLING_TYPE_NAME"));
            offer.put("SUB_CALLING_TYPE_NAME", groupInfo.getString("SUB_CALLING_TYPE_NAME"));
            offer.put("SERIAL_NUMBER", resInfo.getData(0).getString("RES_CODE"));
            offer.put("USER_ID", resInfo.getData(0).getString("USER_ID"));
            offer.put("OPEN_DATE", resInfo.getData(0).getString("UPDATE_TIME"));
            offer.put("PRODUCT_ID", prodInfo.getString("PRODUCT_ID"));
            offer.put("PRODUCT_NAME", prodInfo.getString("PRODUCT_NAME"));
            offer.put("BRAND_NAME", prodInfo.getString("BRAND_NAME"));
            offer.put("PAY_NAME", acctInfo.getString("PAY_NAME"));
            offer.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
            offer.put("RSRV_STR3", acctInfo.getString("RSRV_STR3"));
            offer.put("PAY_MODE_NAME", acctInfo.getString("PAY_MODE_NAME"));
            offer.put("BANK", acctInfo.getString("BANK_CODE"));
            offer.put("BANK_ACCT_NO", acctInfo.getString("BANK_ACCT_NO"));
            offer.put("SERVICE_ID", svcstateInfo.getData(0).getString("SERVICE_ID"));
            offer.put("SERVICE_CODE", "服务");
            offer.put("SERVICE_NAME", "集团商务宽带服务");
            offer.put("ELEMENT_ID", productInfo.getString("SERIAL_NUMBER"));
            offer.put("ELEMENT_CODE", "资源");
            offer.put("ELEMENT_NAME","集团产品编码" );
            offer.put("MEB_COUNT",mebCount);
        }
        
        setInfo(offer);
    }
    
    /**
     * 页面提交
     * 
     * @throws Exception
     */
    public void submitChange(IRequestCycle cycle) throws Exception
    {
        IData getData = this.getData();

        // 服务数据
        IData svcData = new DataMap();
        svcData.put("GROUP_ID", getData.getString("GROUP_ID"));
        svcData.put("USER_ID", getData.getString("USER_ID"));
        svcData.put("PRODUCT_ID", getData.getString("PRODUCT_ID"));
        svcData.put("USER_EPARCHY_CODE",getData.getString("USER_EPARCHY_CODE"));
        
        String stateFlag = getData.getString("STATE_FLAG");
        
        IDataset retDataset = new DatasetList();

        // 调用服务
        if("STOP".equals(stateFlag))
        {
             retDataset = CSViewCall.call(this, "SS.ChangeWideNetStateSVC.StopOneKey", svcData);
        }
        else if("OPEN".equals(stateFlag))
        {
             retDataset = CSViewCall.call(this, "SS.ChangeWideNetStateSVC.OpenOneKey", svcData);
        }

        // 设置返回值
        setAjax(retDataset);
    }
    
    
}
