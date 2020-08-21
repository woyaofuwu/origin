
package com.asiainfo.veris.crm.order.web.group.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.fee.UserFeeIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SaleActiveTrade extends GroupBasePage
{
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
//        data.put("TRADE_TYPE_CODE", "3606");
//        data.put("ELEMENT_TYPE_CODE", "A");
//        data.put("EPARCHY_CODE", "0898");
//        setInfo(data);
        
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", "GROUPSALEACTIVE");
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	data.put("AUDIT_INFO_SHOW","false");
        }else{
        	data.put("AUDIT_INFO_SHOW","true");
        }
        setCondition(data);
    }

    /**
     * 业务规则校验
     * 
     * @param data
     * @throws Exception
     */
    public void checkBeforeTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        IDataset infos = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
        setAjax(infos.getData(0));
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.putAll(new DataMap(data.getString("SALEACITVEDATA")));
        data.remove("SALEACITVEDATA");
        data.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        IDataset rtDataset = CSViewCall.call(this, "SS.SaleActiveSVC.crtTrade", data);
        this.setAjax(rtDataset);
    }

    /**
     * 根据活动类型查询活动名称列表[Ajax刷新] QSM_GetCampnName 应返回CAMPN_ID(活动编码)-CAMPN_NAME(活动名称) IDataset
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCampnNames(IRequestCycle cycle) throws Exception
    {
        String campnType = getParameter("CAMPN_TYPE", "");
        IData param = new DataMap();
        param.put("PARENT_PTYPE_CODE", campnType);
        IDataset products = CSViewCall.call(this, "CS.ProductTypeInfoQrySVC.getProductsTypeByParentTypeCode", param);
        if (IDataUtil.isEmpty(products))
        {
            return;
        }
        for (int i = 0; i < products.size(); i++)
        {
            IData each = products.getData(i);
            if ("BOOK2VALID".equals(each.getString("RSRV_STR5", "")))
            {
                products.remove(each);
                i--;
                continue;
            }
            String productId = each.getString("PRODUCT_TYPE_CODE");
            String productName = each.getString("PRODUCT_TYPE_NAME");

            String productInfo = productId + "|" + productName;
            each.put("PRODUCT_INFO", productInfo);
            each.put("PRODUCT_ID", productId);
        }
        setProducts(products);
    }
    /**
     * 根据活动类型查询活动名称列表[Ajax刷新] QSM_GetCampnName 应返回CAMPN_ID(活动编码)-CAMPN_NAME(活动名称) IDataset
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkUserFee(IRequestCycle cycle) throws Exception
    {

        String msg = "";
        boolean flag = true;
        String userId = getParameter("USER_ID", "");
        String saleProductId = getParameter("SALE_PRODUCT_ID", "");
        // 一、校验用户是否欠费，需查询账户余额根据SERIAL_NUMBER
        IData oweFeeData = UserFeeIntfViewUtil.qryGrpUserOweFeeInfo(this, userId);
        if (IDataUtil.isEmpty(oweFeeData))
        {
            flag = false;
            msg = "获取集团用户的账户余额出错！";
        }
        else
        {
        	/**
        	 * REQ202002100007关于优化集团营销活动受理界面欠费办理规则的需求
        	 * 不判断实时欠费
            double acctBalance = Double.valueOf(oweFeeData.getString("ACCT_BALANCE","0"));// 实时欠费
            if (acctBalance < 0)
            {
                flag = false;
                msg = "集团产品（用户）已欠费，不能办理该业务！";
            }
            */
        	
            double last_owe_fee = Double.valueOf(oweFeeData.getString("LAST_OWE_FEE","0"));   //往月欠费
            if (last_owe_fee > 0)
            {
            	IData inParam = new DataMap();
                inParam.put("SUBSYS_CODE", "CSM");
                inParam.put("PARAM_ATTR", "7360");
                inParam.put("PARAM_CODE", "GRP_SALE_OWEFEE");
                inParam.put("PARA_CODE1", saleProductId);
                inParam.put("EPARCHY_CODE", "0898");
                IDataset commInfos = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByParacode1AndAttr", inParam);
                if(IDataUtil.isNotEmpty(commInfos))
                {
                	String staffId = getVisit().getStaffId();
                	boolean activeStop = StaffPrivUtil.isFuncDataPriv(staffId, "GRP_SALE_ACTIVE_OWEFEE");
                	if(!activeStop)
                	{
                		flag = false;
                        msg = "集团产品（用户）存在往月欠费，不能办理该业务1!";
                	}
                }
                else 
                {
                	flag = false;
                    msg = "集团产品（用户）存在往月欠费，不能办理该业务2!";
                }
            }
        }

        IData data = new DataMap();
        data.put("FLAG", flag);
        data.put("ERROR_MESSAGE", msg);
        setAjax(data);
    }
    
    public void queryCampType(IRequestCycle cycle) throws Exception
    {
    	IData param = new DataMap();
        param.put("PARENT_PTYPE_CODE", "K002");
        IDataset campTypes = CSViewCall.call(this, "CS.ProductTypeInfoQrySVC.getProductsTypeByParentTypeCode", param);
        setCampTypes(campTypes);
    }
    /**
     * 根据活动类型查询活动名称列表[Ajax刷新] QSM_GetCampnName 应返回CAMPN_ID(活动编码)-CAMPN_NAME(活动名称) IDataset
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkByProduct(IRequestCycle cycle) throws Exception
    {

        String msg = "";
        boolean flag = true;

        // 二、p_csm_CheckForGrpSaleActive存储过程校验
        IData inData = getData();
        inData.put("EVENT_TYPE", "CHKPROD");
        IDataset rtDataset = CSViewCall.call(this, "SS.SaleActiveSVC.CheckSaleElement", inData);
        if (IDataUtil.isNotEmpty(rtDataset))
        {
            IData result = rtDataset.getData(0);
            if (!"0".equals(result.getString("v_resultcode", "0")))
            {
                flag = false;
                msg = result.getString("v_resultinfo", "");
            }
        }

        String productId = inData.getString("PRODUCT_ID", "");
        String userId = inData.getString("USER_ID", "");
        if(flag){
            IData inparam = new DataMap();
            inparam.put("SUBSYS_CODE", "CSM");
            inparam.put("PARAM_ATTR", "7342");
            inparam.put("PARAM_CODE", "GRP_SALE_SCORE");
            inparam.put("PARA_CODE1", productId);
            inparam.put("EPARCHY_CODE", "0898");
            IDataset commInfos = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByParacode1AndAttr", inparam);
            if (IDataUtil.isNotEmpty(commInfos)){
                
                if(StringUtils.isNotBlank(userId)){
                    inparam.clear();
                    inparam.put("USER_ID", userId);
                    IDataset productInfo = CSViewCall.call(this, "CS.UserProductInfoQrySVC.queryUserMainProductByUserId", inparam);
                    if(IDataUtil.isNotEmpty(productInfo)){
                        inparam.clear();
                        inparam.put("SUBSYS_CODE", "CSM");
                        inparam.put("PARAM_ATTR", "7343");
                        inparam.put("PARAM_CODE", "GRP_SALE_SCORE");
                        inparam.put("PARA_CODE1", productInfo.getData(0).getString("PRODUCT_ID"));
                        inparam.put("PARA_CODE2", productId);
                        inparam.put("EPARCHY_CODE", "0898");
                        IDataset commInfos2 = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getEnableCommparaInfoByCode12", inparam);
                        if(IDataUtil.isEmpty(commInfos2)){
                            flag = false;
                            msg = "该集团产品不可以办理该营销包!";
                        }
                    }
                }
            }
        }
        
        //暂时做互斥判断
        if(flag){
            IData inparam = new DataMap();
            inparam.put("SUBSYS_CODE", "CSM");
            inparam.put("PARAM_ATTR", "7345");
            inparam.put("PARAM_CODE", "GRP_SALE_SCORE");
            inparam.put("PARA_CODE1", productId);
            inparam.put("EPARCHY_CODE", "0898");
            IDataset commInfos = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByParacode1AndAttr", inparam);
            if (IDataUtil.isNotEmpty(commInfos)){
                if(StringUtils.isNotBlank(userId)){
                    inparam.clear();
                    inparam.put("USER_ID", userId);
                    IDataset userSaleInfos = CSViewCall.call(this, "CS.UserSaleActiveInfoQrySVC.queryUserSaleActiveByTag", inparam);
                    if (IDataUtil.isNotEmpty(userSaleInfos)){
                        for (int i = 0, size = commInfos.size(); i < size; i++){
                            String productName = commInfos.getData(i).getString("PARA_CODE2","");//当前订购的产品名称
                            String huchiProductId = commInfos.getData(i).getString("PARA_CODE3","");//配置的互斥的产品ID
                            String huchiProductName = commInfos.getData(i).getString("PARA_CODE4","");//配置的互斥的产品名称
                            if(StringUtils.isNotBlank(huchiProductId)){
                               for(int j=0,counts = userSaleInfos.size(); j < counts; j++){
                                   String saleProductId = userSaleInfos.getData(i).getString("PRODUCT_ID","");
                                   if(saleProductId != null && saleProductId.equals(huchiProductId)){
                                       flag = false;
                                       msg = "您当前办理的营销活动" + productName + "[" + productId + "]与" + huchiProductName +"[" + huchiProductId +"]互斥，不可在办理该营销活动!";
                                       break;
                                   }
                               }
                            }
                        }
                    }
                }
            }
        }
        
        if("69901002".equals(productId) && flag){
            
            IData inparam = new DataMap();
            inparam.put("USER_ID", userId);
            inparam.put("EPARCHY_CODE", "0898");
            IDataset commInfos = CSViewCall.call(this, "CS.UserProductInfoQrySVC.getUserChangeXiangProductByUserId", inparam);
            if (IDataUtil.isEmpty(commInfos)){
                flag = false;
                msg = "您当前办理的营销活动不是规定的集团产品，不可在办理该营销活动!";
            }
        }
        
        /**
         * REQ201809100007 关于集团短信消费送话费营销活动的需求
         * 新增营销活动,判定只有以下8种集团产品可以办理
         */
        if("69901006".equals(productId) && flag ){
        	String grpProductId = inData.getString("GRP_PRODUCT_ID");
        	if(!"9976".equals(grpProductId) && !"9960".equals(grpProductId) && !"996402".equals(grpProductId) && !"996403".equals(grpProductId) && 
        			!"997502".equals(grpProductId) && !"996101".equals(grpProductId) && !"997601".equals(grpProductId) && !"997604".equals(grpProductId)) {
        		 flag = false;
                 msg = "您当前办理的营销活动不是规定的集团产品，不可在办理该营销活动!";
        	}
        }
        
        /**
         * REQ201912270006_新增全省“社戒社康”项目设置约定消费、工作手机流量包流量短信提醒功能
         * 新增营销活动,判定只有集团工作手机产品的集团用户才能办理本活动
         */
        if("69901007".equals(productId) && flag ){
        	String grpProductId = inData.getString("GRP_PRODUCT_ID");
        	if(!"6013".equals(grpProductId)) {
        		 flag = false;
                 msg = "您当前办理的营销活动不是规定的集团产品，不可在办理该营销活动!";
        	}
        }
        
        /*2018年海洋通约定消费送话费营销活动
         * 活动规则：判断集团产品是否代付至少7人，判断集团产品代付成员中，是否有其中一个成员办理了指定的套餐（即1000元海洋通基础套餐）
         * */
        if("69901005".equals(productId) && flag){
            IData inparam = new DataMap();
            inparam.put("USER_ID", userId);
            inparam.put("EPARCHY_CODE", "0898");
            IDataset advMebs = CSViewCall.call(this, "CS.AcctInfoQrySVC.getGrpPrdAdvPayMebByUserId", inparam);
            if (IDataUtil.isEmpty(advMebs) || advMebs.size()<7){
                flag = false;
                msg = "您当前办理的营销活动要求：海洋通集团产品代付至少7个人！";
            }else{
            	//查询代付成员中是否有一个办理了"1000元海洋通基础套餐"
            	//默认没有代付成员办理“1000元海洋通基础套餐”
            	flag = false;
            	msg = "您当前办理的营销活动要求：海洋通集团产品代付至少7个人，且有其中一个成员办理了“1000元海洋通基础套餐”！";
            	for(int i=0;i<advMebs.size();i++){
            		inparam.clear();
                	inparam.put("USER_ID", advMebs.getData(i).getString("USER_ID"));
                    inparam.put("EPARCHY_CODE", "0898");
                    IDataset userDiscnts = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.queryUserHytDisncts", inparam);
                    if(IDataUtil.isNotEmpty(userDiscnts)){
                    	flag = true;
                    	msg = "";
                    	break;	//有一个就可以了
                    }
            	}            	
            }
        }
        
        IData data = new DataMap();
        data.put("FLAG", flag);
        data.put("ERROR_MESSAGE", msg);
        setAjax(data);
    }

    /**
     * 根据活动类型查询活动名称列表[Ajax刷新] QSM_GetCampnName 应返回CAMPN_ID(活动编码)-CAMPN_NAME(活动名称) IDataset
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserAcctInfo(IRequestCycle cycle) throws Exception
    {
        String userId = getParameter("USER_ID", "");
        String eparchyCode = getTradeEparchyCode();
        IData data = new DataMap();

        IData inData = new DataMap();
        inData.put("USER_ID", userId);
        inData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset rtDataset = CSViewCall.call(this, "CS.UserAcctDayInfoQrySVC.getUserAcctDayAndFirstDateInfo", inData);
        if (IDataUtil.isNotEmpty(rtDataset))
        {
            data.put("ACCTDAY", rtDataset.getData(0));
        }
        setAjax(data);
    }

    public abstract void setInfo(IData info);

    public abstract void setProducts(IDataset products);
    
    public abstract void setCampTypes(IDataset campTypes);
    
    public abstract void setCondition(IData condition);
    
}
