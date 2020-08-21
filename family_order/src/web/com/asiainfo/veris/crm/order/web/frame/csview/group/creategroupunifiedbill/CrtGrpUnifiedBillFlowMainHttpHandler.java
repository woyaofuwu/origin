
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupunifiedbill;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CrtGrpUnifiedBillFlowMainHttpHandler extends CSBizHttpHandler
{
    /**
     * 获取产品ID
     * 
     * @return
     * @throws Exception
     */
    public String getGrpProductId() throws Exception
    {
        return this.getData().getString("PRODUCT_ID", "");
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
        inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));// 集团用户编码
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));// 成员服务号码
        inparam.put("MEM_ROLE_B", getData().getString("ROLE_CODE_B", ""));// 成员角色
        inparam.put("PRODUCT_ID", getGrpProductId());
        inparam.put("REMARK", getData().getString("parm_REMARK"));
        inparam.put("IF_CENTRETYPE", getData().getString("IF_CENTRETYPE", "")); // 融合标识
        inparam.put("CUST_ID", getData().getString("CUST_ID", ""));
        inparam.put("USER_ID_B", getData().getString("MEB_USER_ID", ""));
        inparam.put("BRAND_CODE_B", getData().getString("MEB_BRAND_CODE", ""));
        inparam.put("PRODUCT_ID_B", getData().getString("MEB_PRODUCT_ID", ""));
        inparam.put("EPARCHY_CODE_B", getData().getString("MEB_EPARCHY_CODE", ""));
        IDataset productElements = saveProductElemensFrontData();
        IDataset productParam = saveProductParamInfoFrontData();
        IDataset resinfos = saveResInfoFrontData();
        inparam.put("ELEMENT_INFO", productElements);
        inparam.put("RES_INFO", resinfos);

        // 费用信息
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        String bookingDate = getData().getString("bookingDate");
        if (StringUtils.isNotEmpty(bookingDate))
        {
            inparam.put("PRODUCT_PRE_DATE", bookingDate);
        }

        if (IDataUtil.isNotEmpty(productParam))
            inparam.put("PRODUCT_PARAM_INFO", productParam);

        // IData payPlan = savePayPlanFrontData();
        IData payPlan = new DataMap();
        if (IDataUtil.isNotEmpty(payPlan))
            inparam.put("PLAN_INFO", payPlan);
        // 查询td_b_attr_biz表 品牌：MASG/ADCG 才能受理融合计费
        IDataset result = CSViewCall.call(this, "CS.CreateGroupUnifiedBillSvc.createGroupMember", inparam);
        setAjax(result);
    }

}
