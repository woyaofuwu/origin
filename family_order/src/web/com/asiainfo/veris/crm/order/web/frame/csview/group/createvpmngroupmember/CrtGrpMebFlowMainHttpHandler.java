
package com.asiainfo.veris.crm.order.web.frame.csview.group.createvpmngroupmember;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public class CrtGrpMebFlowMainHttpHandler extends CSBizHttpHandler
{

    private String shortCode;

    /**
     * 获取产品ID
     * 
     * @return
     * @throws Exception
     */
    public String getGrpProductId() throws Exception
    {

        return this.getData().getString("GRP_PRODUCT_ID", "");

    }

    public String savePayPlanFrontData() throws Exception
    {
        return this.getData().getString("PAY_PLAN_SEL_PLAN_TYPE", "P");
    }

    /**
     * 获取集团产品元素信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset saveProductElemensFrontData() throws Exception
    {

        String selectElementStr = getData().getString("SELECTED_ELEMENTS", "[]");

        IDataset selectElements = new DatasetList(selectElementStr);
        return selectElements;
    }

    public IDataset saveProductParamInfoFrontData() throws Exception
    {

        IDataset resultset = new DatasetList();
        IData result = new DataMap();
        IDataset productParamAttrset = new DatasetList();
        IData productParam = getData("pam", true);
        if (IDataUtil.isEmpty(productParam))
            return null;
        Iterator<String> iterator = productParam.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = productParam.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            if ("SHORT_CODE".equals(key))
            {
                shortCode = (String) value;
            }
            productParamAttrset.add(productParamAttr);

        }

        result.put("PRODUCT_ID", getGrpProductId());
        result.put("PRODUCT_PARAM", productParamAttrset);
        resultset.add(result);
        return resultset;
    }

    /**
     * 获取资源信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset saveResInfoFrontData() throws Exception
    {

        String resInfoStr = getData().getString("DYNATABLE_RES_RECORD", "[]");

        IDataset resinfos = new DatasetList(resInfoStr);
        return resinfos;
    }

    /**
     * submit
     * 
     * @param cycle
     * @throws Exception
     */
    public void submit() throws Exception
    {

        IData inparam = new DataMap();

        inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        inparam.put("MEM_ROLE_B", getData().getString("ROLE_CODE_B", ""));
        inparam.put("REMARK", getData().getString("parm_REMARK"));

        String bookingDate = getData().getString("bookingDate"); // 业务预约时间
        if (StringUtils.isNotEmpty(bookingDate))
        {
            inparam.put("PRODUCT_PRE_DATE", bookingDate);
        }

        String effectNow = getData().getString("EFFECT_NOW");// 产品资费立即生效标志 true立即生效
        if (StringUtils.isNotEmpty(effectNow))
        {
            inparam.put("EFFECT_NOW", "true".equals(effectNow) ? "true" : "false");
        }

        IDataset productElements = saveProductElemensFrontData();
        shortCode = null;
        IDataset productParam = saveProductParamInfoFrontData();
        IDataset resinfos = saveResInfoFrontData();
        inparam.put("ELEMENT_INFO", productElements);
        if (IDataUtil.isNotEmpty(resinfos))
            inparam.put("RES_INFO", resinfos);

        if (IDataUtil.isNotEmpty(productParam))
        {
            inparam.put("PRODUCT_PARAM_INFO", productParam);

        }
        if (!StringUtils.isEmpty(shortCode))
        {
            inparam.put("SHORT_CODE", shortCode);
        }
        inparam.put("PLAN_TYPE", savePayPlanFrontData());

        // 费用信息
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        // 发展人信息
        inparam.put("DEVELOP_STAFF_ID", getData().getString("DEVELOP_STAFF_ID"));

        // 根据产品编号获取产品的品牌信息
        String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, getGrpProductId());
        if ("BOSG".equals(productBrandCode))
        {// BBOSS业务
            IData bbossData = new DataMap(getData().getString("productGoodInfos"));// BBOSS商产品信息
            inparam.put("BBOSS_INFO", bbossData);
            IDataset result = CSViewCall.call(this, "CS.CreateBBossMemSVC.crtOrder", inparam);
            setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.CreateGroupMemberSvc.createGroupMember", inparam);
        setAjax(result);
    }

}
