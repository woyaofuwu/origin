
package com.asiainfo.veris.crm.order.web.group.bat.batmdmcreatmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class Batmdmcreatmember extends GroupBasePage
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
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId);
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
      
        getData().put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        getData().put("EPARCHY_CODE", getTradeEparchyCode());
        String esopTag = getData().getString("ESOP_TAG");
        if ("ESOP".equals(esopTag))
        {
            getData().put("ESOP_TAG", esopTag);
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
        
        String product_id = condition.getString("PRODUCT_ID");
        
        String mebProductId = UpcViewCall.queryMemProductIdByProductId(this, product_id);
        
        this.setMebProductId(mebProductId);

        this.setCondition(condition);
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
    public void queryTwoCheckbatByWP(IRequestCycle cycle) throws Throwable
    {
        IData twoCheck = new DataMap();
        twoCheck.put("TWOCHECK", "0");
        boolean twoCHECK = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "TWOCHECK");
        if (twoCHECK) {
        	twoCheck.put("TWOCHECK", "1");
		}
    	setAjax(twoCheck);
    }
    
    /*
     * REQ201908130005  MDM手机管控资费套餐、账目及报表需求开发说明
     * guonj@
     * 2019-10-08
     */
    public void isPriv(IRequestCycle cycle) throws Exception
    {
    	String modifyTag = getData().getString("MODIFY_TAG", "");
    	String elementType = getData().getString("ELEMENT_TYPE", "");
    	String attrCode = getData().getString("ATTR_CODE", "");
    	String attrVal = getData().getString("ATTR_VALUE", "");
		if("D".equals(elementType)&&"214485".equals(attrCode) && ("0".equals(modifyTag)||"2".equals(modifyTag))){
			/*判断工号是否有工作手机套餐折扣权限*/
			String tradeStaffId = getVisit().getStaffId();
			int iAttrVal = Integer.parseInt(attrVal);
			if(iAttrVal>100 || iAttrVal<0){
				CSViewException.apperr(ElementException.CRM_ELEMENT_310, "您填写折扣不正确，请确认!");
			}
			if(!StaffPrivUtil.isPriv(tradeStaffId, "PRIV_MDM_DISCOUNT", "1")){
				if(iAttrVal != 100){
					CSViewException.apperr(ElementException.CRM_ELEMENT_310, "您没有MDM手机管控套餐折扣权限，不能办理打折，请确认!");
				}
			}
		}
    }
    
}
