
package com.asiainfo.veris.crm.order.web.frame.csview.group.changevpmnmemelement;

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

public class ChgMemEleFlowMainHttpHandler extends CSBizHttpHandler
{

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

        inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        inparam.put("REMARK", getData().getString("parm_REMARK"));
        inparam.put("PRODUCT_ID", getGrpProductId());
        IDataset productElements = saveProductElemensFrontData();
        IDataset productParam = saveProductParamInfoFrontData();
        IDataset resinfos = saveResInfoFrontData();
        inparam.put("ELEMENT_INFO", productElements);
        inparam.put("RES_INFO", resinfos);

        // 费用信息
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        // esop参数
        String eos = getData().getString("EOS");
        if (!StringUtils.isEmpty(eos) && !"{}".equals(eos))
        {
            inparam.put("EOS", new DatasetList(eos));
        }

        if (IDataUtil.isNotEmpty(productParam))
            inparam.put("PRODUCT_PARAM_INFO", productParam);

        // 根据产品编号获取产品的品牌信息
        String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, getGrpProductId());
        if ("BOSG".equals(productBrandCode))
        {// BBOSS业务
            IData bbossData = new DataMap(getData().getString("productGoodInfos"));// BBOSS商产品信息
            inparam.put("BBOSS_INFO", bbossData);
            IDataset result = CSViewCall.call(this, "CS.ChangeBBossMemSVC.crtOrder", inparam);
            setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.ChangeMemElementSvc.changeMemElement", inparam);
        setAjax(result);
    }

}
