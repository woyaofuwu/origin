
package com.asiainfo.veris.crm.order.web.group.bat.batwpchangemember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public abstract class BatWpChangeMemInfo extends CSBasePage
{
	public abstract void setCondition(IData condition);
	
	/**
	 * 页面初始化
	 * @param cycle
	 * @throws Exception
	 * @author chenhh6
	 * @date 2019-6-21
	 */
    public void init(IRequestCycle cycle) throws Exception
    {
    	String batchOperType = getData().getString("BATCH_OPER_TYPE", "NOXXXX");
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", batchOperType);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	getData().put("MEB_VOUCHER_FILE_SHOW","false");
        }else{
        	getData().put("MEB_VOUCHER_FILE_SHOW","true");
        }
        setCondition(getData());
    }
    
    public abstract void setInfo(IData info);

    public abstract void setOrderProductList(IDataset orderProductList);
    
    
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
        this.init(cycle);

        // pushmail或blackberry集团产品成员新增设置为立即生效
        if ("BATPMBBMEMADD".equals(batchOperType))
        {
            setAjax("EFFECT_NOW", "true");
        }
    }
}
