
package com.asiainfo.veris.crm.order.web.group.bat.batcreatmember;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo.UserOtherInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class CreatMemberBatCond extends GroupBasePage
{

    /**
     * 作用：获取集团客户信息 返回界面显示
     * 
     * @author xieyuan 2013-6-15
     * @param cycle
     * @throws Exception
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData groupInfo = queryGroupCustInfo(cycle);
        setCustInfo(groupInfo);
    }

    /**
     * 获取用户用户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getGroupUserInfo(IRequestCycle cycle) throws Exception
    {
        String userId = getData().getString("USER_ID");
        String productId = getData().getString("PRODUCT_ID");
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId);
        if ("8000".equals(productId))
        {
            IData vpnInfo = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userId);
            if (IDataUtil.isNotEmpty(vpnInfo))
            {
                String scareCode = vpnInfo.getString("VPN_SCARE_CODE", "");
                if ("2".equals(scareCode))// 如果是跨省V网则需要跨省资费
                {
                    userInfo.put("isNeedOutProDiscnt", true);
                }
                else
                {
                    userInfo.put("isNeedOutProDiscnt", false);
                }
            }
            
            //start add by wangyf6 at 2014032
            IDataset otherInfoSet = UserOtherInfoIntfViewUtil.qryGrpUserOtherInfosByUserIdAndRsrvValueCode(this, userId, "VPMN_GRPCLIP");
            if(IDataUtil.isNotEmpty(otherInfoSet)){
                IData otherInfos = otherInfoSet.getData(0);
                String rsrvStr1 = otherInfos.getString("RSRV_STR1","");//GRP_CLIP_TYPE 呼叫来显方式
                String rsrvStr2 = otherInfos.getString("RSRV_STR2","");//GRP_USER_CLIP_TYPE 选择号显方式
                String rsrvStr3 = otherInfos.getString("RSRV_STR3","");//GRP_USER_MOD 成员修改号显方式
                userInfo.put("GRP_CLIP_TYPE", rsrvStr1);
                userInfo.put("GRP_USER_CLIP_TYPE", rsrvStr2);          
                userInfo.put("GRP_USER_MOD", rsrvStr3);
            }
            //end add by wangyf6 at 20140320
            
        }
        setUserInfo(userInfo);
    }

    public abstract IData getUserInfo();

    /**
     * 初始化批量弹出窗口页面
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        initialCondition(cycle);
        String esopTag = getData().getString("ESOP_TAG");
        if ("ESOP".equals(esopTag))
        {
            queryEsopInit(cycle);
        }
    }

    /**
     * 初始化服务资费区域Condition参数
     */
    public void initialCondition(IRequestCycle cycle) throws Throwable
    {
        String batchOperType = getData().getString("BATCH_OPER_TYPE");
        // VPMN成员新增
        if ("BATADDVPMNMEM".equals(batchOperType))
        {
            // j2ee VPMN权限 ，判断员工是否有权限选择“加入相应集团”的复选框;
            String staffId = getVisit().getStaffId();
            String dataPriv = "GROUPMENBER_VPMN_PRV";
            boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, dataPriv);
            if (bool)
            {
                getData().put("IS_NEED_SHOW_JRXYJT", "true");
            }
            else
            {
                getData().put("IS_NEED_SHOW_JRXYJT", "false");
            }
        }

        getData().put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        getData().put("EPARCHY_CODE", getTradeEparchyCode());
        String esopTag = getData().getString("ESOP_TAG");
        if ("ESOP".equals(esopTag))
        {
            getData().put("ESOP_TAG", esopTag);
        }
        
        getData().put("IS_SHOW_APPLY_TYPE", "false");
        if ( "BATADDPOCUMEM".equals(batchOperType) || "BATADDWLWMEBJIQIKA".equals(batchOperType) || "BATADDWLWMEBWLT".equals(batchOperType))
        {
            getData().put("IS_SHOW_APPLY_TYPE", "true");
        }
        
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", batchOperType);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
            getData().put("MEB_FILE_SHOW","true");
        }
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        iparam.clear();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", batchOperType);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	getData().put("MEB_VOUCHER_FILE_SHOW","false");
        }else{
        	getData().put("MEB_VOUCHER_FILE_SHOW","true");
        }
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
        
        
        if("BATADDIMSVPMNMEBER".equals(batchOperType))
        {
        	IDataset pzAttrItems = AttrItemInfoIntfViewUtil.qryMebProductItemAInfosByGrpProductIdAndEparchyCode(this, 
        			"8001", getTradeEparchyCode());
        	
        	if(IDataUtil.isNotEmpty(pzAttrItems))
        	{
        		IData attrItem = new DataMap();
        		IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
                // 方便前台取下拉框选项值
                transComboBoxValue(attrItem, pzAttrItem);
                if(IDataUtil.isNotEmpty(attrItem))
                {
                	IData outerCall = attrItem.getData("OuterCall");
                	if(IDataUtil.isNotEmpty(outerCall))
                	{
                		IDataset outerCallList = outerCall.getDataset("DATA_VAL");
                		getData().put("OUTER_CALL_LIST",outerCallList);
                	}
                	IData cencallDispMode = attrItem.getData("CENCALL_DISP_MODE");
                	if(IDataUtil.isNotEmpty(cencallDispMode))
                	{
                		IDataset dispModeList = cencallDispMode.getDataset("DATA_VAL");
                		getData().put("CALL_DISP_MODE_LIST",dispModeList);
                	}
                }
        	}
        }
        
        setCondition(getData());
    }

    /**
     * 为端到端调用页面时初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryEsopInit(IRequestCycle cycle) throws Throwable
    {

        getGroupBaseInfo(cycle);
        queryGroupOrderProduct(cycle);
        refreshProduct(cycle);
        getGroupUserInfo(cycle);
    }

    /**
     * 作用:集团客户已订购产品查询
     * 
     * @author admin 2009-09-04 18:49
     * @param cycle
     * @throws Throwable
     */
    public void queryGroupOrderProduct(IRequestCycle cycle) throws Throwable
    {
        IData paramData = getData();
        String esopTag = paramData.getString("ESOP_TAG");

        String batchOperType = paramData.getString("BATCH_OPER_TYPE", "");

        String matchProductId = StaticUtil.getStaticValue("GROUP_BAT_PRODUCT", batchOperType); // 通过配置取配置的产品

        String custId = getParameter("CUST_ID");
        // 查询该用户已订购产品
        IDataset userProductList = UCAInfoIntfViewUtil.qryGrpUserInfoByCustId(this, custId, false);

        // 可以进行批量处理的产品
        IDataset batProductList = new DatasetList();

        if (StringUtils.isEmpty(matchProductId))
        {
            // 查询可以进行批量处理的产品
            batProductList = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdTypeAttrObjAttrCode(this, "P", "BatFter", "BatFilter");
            if (IDataUtil.isEmpty(batProductList))
                batProductList = new DatasetList();
        }

        IDataset productList = new DatasetList();
        if (IDataUtil.isNotEmpty(userProductList))
        {
            for (int i = 0, size = userProductList.size(); i < size; i++)
            {
                String userProductId = userProductList.getData(i).getString("PRODUCT_ID");

                // 如果配置了只能新增某些配置产品，该功能只处理配置产品
                if (StringUtils.isNotBlank(matchProductId))
                {
                    if (userProductId.matches(matchProductId))
                    {
                        productList.add(userProductList.getData(i));
                    }

                    continue;
                }

                boolean flag = false;

                for (int j = 0, bSize = batProductList.size(); j < bSize; j++)
                {
                    String batProductId = batProductList.getData(j).getString("ID");

                    if (userProductId.equals(batProductId))
                    {
                        flag = true;
                        break;
                    }
                }
                if (flag)
                    productList.add(userProductList.getData(i));
            }
        }
        String WLWG_OPENSYN = StaticUtil.getStaticValue("CREATMEMBERBATCOND", "BATCONDOPEN"); // 通过配置开关控制过滤空集团产品0拦截；1或其他不拦截
        if ( WLWG_OPENSYN != null && "0".equals(WLWG_OPENSYN )) {
        for (int i = 0,aSize = productList.size(); i < aSize; i++) {
        	IData userProduct = productList.getData(i);
        	String productid = userProduct.getString("PRODUCT_ID");
            String relationTypeCode;
            IData data = new DataMap();
            IData inparam = new DataMap();
            data.put("OFFER_TYPE", "P");
            data.put("OFFER_CODE", productid);
            data.put("FIELD_NAME", "RELATION_TYPE_CODE");
            IDataset result = CSViewCall.call(this, "SS.BatDealBeanSvc.queryOfferComChaByCond", data);
            if(IDataUtil.isEmpty(result)){
            	relationTypeCode = "";
            }
            else
            {
            	relationTypeCode = result.getData(0).getString("FIELD_VALUE");
            }
            inparam.put("USER_ID_A", userProduct.getString("USER_ID"));
            inparam.put("RELATION_TYPE_CODE", relationTypeCode);
    		IDataset allMeb = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getAllMebByUSERIDA", inparam);
    		if("WLWG".equals(userProduct.getString("BRAND_CODE"))&&allMeb.isEmpty()){
    			productList.remove(i); //物联网不允许个人用户加入一个没有任何成员的空集团产品
                i--;
                aSize--;
                continue;
    		}
		}
    }
        IDataset amList = new DatasetList();

        amList.addAll(productList);

        for (int i = 0, aSize = amList.size(); i < aSize; i++)
        {
            IData userProduct = amList.getData(i);
            String productid = userProduct.getString("PRODUCT_ID");

            // 查询产品信息
            String productNameString = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productid, false);
            if (StringUtils.isBlank(productNameString))
            {
                amList.remove(i); // 产品下线了 应该删掉 不然报空指针异常
                i--;
                aSize--;
                continue;
            }

            // 获取产品信息
            String productName = productid + "|" + productNameString + "|" + userProduct.getString("SERIAL_NUMBER") + "|" + userProduct.getString("USER_ID") + "|" + userProduct.getString("BRAND_CODE");
            amList.getData(i).put("PRODUCT_NAME", productName);

            if ("ESOP".equals(esopTag))
            {
                String esopUserID = paramData.getString("USER_ID");
                if (userProduct.getString("USER_ID").equals(esopUserID))
                {
                    amList.getData(i).put("disabled", "true");
                    setInfo(amList.getData(i));
                }
            }
        }
        if (IDataUtil.isEmpty(amList))
        {
            CSViewException.apperr(BatException.CRM_BAT_62, custId);
        }

        setOrderProductList(amList);
        this.initialCondition(cycle);

        // pushmail或blackberry集团产品成员新增设置为立即生效
        if ("BATPMBBMEMADD".equals(batchOperType) 
        		|| "BATADDPOCUMEM".equals(batchOperType)
        		|| "BATADDWLWMEBJIQIKA".equals(batchOperType) 
        		|| "BATADDWLWMEBWLT".equals(batchOperType) 
        		|| "BATADDZUNXKHJTPRD".equals(batchOperType)
        		|| "BATADDJTCLBPRD".equals(batchOperType)
        		|| "BATADDIMSVPMNMEBER".equals(batchOperType))

        {
            setAjax("EFFECT_NOW", "true");
        }
    }

    /**
     * 作用：初始化产品
     * 
     * @author luojh 2009-09-04 17:25
     * @param cycle
     * @throws Exception
     */
    public void refreshProduct(IRequestCycle cycle) throws Exception
    {
        String userId = getData().getString("USER_ID", "");

        IData groupUserData = new DataMap();
        if (StringUtils.isNotBlank(userId))
        {
            getGroupUserInfo(cycle);
            groupUserData = getUserInfo();
        }
        String eparchyCode = groupUserData.getString("EPARCHY_CODE");
        if (StringUtils.isNotBlank(eparchyCode) || null == eparchyCode)
        {
            eparchyCode = getTradeEparchyCode();
        }
        IData condition = getData();
        condition.put("GRP_USER_ID", userId);
        condition.put("EPARCHY_CODE", eparchyCode);
        condition.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        condition.put(Route.USER_EPARCHY_CODE, eparchyCode);
        condition.put("GROUP_ID", getData().getString("GROUP_ID", ""));

        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, condition.getString("PRODUCT_ID", ""));
        IData param = new DataMap();
        param.put("PRODUCT_ID", condition.getString("PRODUCT_ID", ""));
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        IDataset roleList = CSViewCall.call(this, "CS.StaticInfoQrySVC.getRoleCodeList", param);
        condition.put("roleList", roleList);

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryCrtMbProductCtrlInfoByProductId(this, condition.getString("PRODUCT_ID", ""));
        // 获取业务类型 元素校验需要
        IData tradeTypeCodeData = productCtrlInfo.getData("TradeTypeCode");

        if (IDataUtil.isEmpty(tradeTypeCodeData))
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_52);
        }
        String tradeTypeCode = tradeTypeCodeData.getString("ATTR_VALUE");

        condition.put("TRADE_TYPE_CODE", tradeTypeCode);
        
        initApplyTypeA(condition);
        
        String batchOperType = condition.getString("BATCH_OPER_TYPE","");
        if (StringUtils.isNotBlank(batchOperType) 
        		&& StringUtils.equals(batchOperType, "BATADDWLWMEBJIQIKA"))
        {
        	String groupType = "0";
        	if(StringUtils.isNotBlank(userId)){
        		IData inParam = new DataMap();
        		inParam.put("USER_ID", userId);
        		inParam.put("SERVICE_ID", "99011015");
        		inParam.put("ATTR_CODE", "GroupType");
                IDataset attrDataSet = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.queryGrpUserAttrGroupTypeByUserId", inParam);
                if (IDataUtil.isNotEmpty(attrDataSet))
                {
                	groupType = attrDataSet.getData(0).getString("ATTR_VALUE","0");
                }
        	}
        	condition.put("GROUP_TYPE", groupType);
        }
        
        String product_id = condition.getString("PRODUCT_ID");
        
        String mebProductId = UpcViewCall.queryMemProductIdByProductId(this, product_id);
        
        this.setMebProductId(mebProductId);
        
        this.setCondition(condition);
    }
    
    /**
     * initApplyTypea初始化函数
     * @author yanwu
     * @date 2015-08-05
     * @param bp
     * @param data
     * @throws Exception 
     * @throws Throwable
     */
    public void initApplyTypeA(IData result) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "9980");
        param.put("PARAM_CODE", "1");
        param.put("EPARCHY_CODE", "0898");
        IDataset applyTypeas = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", param);
        result.put("APPLY_TYPE_A_LIST", applyTypeas);
    } 
    
    /**
     * @author yanwu
     * @date 2015-08-05
     * @param cycle
     * @throws Exception
     */
    public void queryApplyTypebs(IRequestCycle cycle) throws Exception
    {
        IData condition = getData();
        String strTA = condition.getString("APPLY_TYPE_A", "");
       
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "9980");
        param.put("PARAM_CODE", strTA);
        param.put("EPARCHY_CODE", "0898");
        IDataset applyTypebs = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", param);
        condition.put("APPLY_TYPE_B_LIST", applyTypebs);
        this.setCondition(condition);
    }

    /**
     * 
     * @Title: transComboBoxValue  
     * @Description: 方便前台变更页面取下拉框选项值
     * @param @param userAttrItem
     * @param @param pzAttrItem
     * @param @throws Exception    设定文件  
     * @return void    返回类型  
     * @throws
     */
    private void transComboBoxValue(IData userAttrItem, IData pzAttrItem) throws Exception
    {
        if (IDataUtil.isEmpty(pzAttrItem))
        {
            return;
        }
        else if (IDataUtil.isEmpty(userAttrItem)) 
        {
            userAttrItem.putAll(pzAttrItem);
        }
        else 
        {
            for (Iterator iterator = pzAttrItem.keySet().iterator(); iterator.hasNext();)
            {
                String datakey = (String) iterator.next();
                IData tempData = userAttrItem.getData(datakey);
                IData tempData2 = pzAttrItem.getData(datakey);
                if (IDataUtil.isEmpty(tempData))
                {
                    tempData = tempData2;
                    userAttrItem.put(datakey, tempData);
                }
                else if (IDataUtil.isNotEmpty(tempData2))
                {
                    tempData.put("DATA_VAL", tempData2.getDataset("DATA_VAL"));
                }
            }
        }
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setOrderProductList(IDataset orderProductList);

    public abstract void setUserInfo(IData userInfo);
    
    public abstract void setMebProductId(String mebproductid);
    
    /**
     * REQ201812200001关于优化集团产品二次确认功能的需求  查询是否有权限
     * @param cycle
     * @throws Throwable
     */
    public void queryTwoCheckbatCond(IRequestCycle cycle) throws Throwable
    {
        IData twoCheck = new DataMap();
        twoCheck.put("TWOCHECK", "0");
        boolean twoCHECK = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "TWOCHECK");
        if (twoCHECK) {
        	twoCheck.put("TWOCHECK", "1");
		}
    	setAjax(twoCheck);
    }
}
