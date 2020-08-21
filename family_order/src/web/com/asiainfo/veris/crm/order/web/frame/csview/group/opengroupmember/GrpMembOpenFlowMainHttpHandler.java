
package com.asiainfo.veris.crm.order.web.frame.csview.group.opengroupmember;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class GrpMembOpenFlowMainHttpHandler extends CSBizHttpHandler
{
    public IData crtParam() throws Exception
    {

        IData inparam = new DataMap();

        String productId = getData().getString("PRODUCT_ID");
        String custId = getData().getString("CUST_ID", "");
        String serialNumber = getData().getString("SERIAL_NUMBER", "");
        String custInfoTeltype = getData().getString("CUST_INFO_TELTYPE", ""); // IMS客户端类型

        // 集团成员产品信息
        IDataset productElements = saveProductElemensFrontData();
        // 产品参数信息
        IDataset productParam = saveProductParamInfoFrontData();
        // 资源信息
        IDataset resInfos = saveResInfoFrontData();

        IData memCustInfo = getMemCustData();
        IData memUserInfo = getMemUserData();
        IData memAcctInfo = getMemAcctData();

        inparam.put("PRODUCT_ID", productId);
        inparam.put("CUST_ID", custId);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("CUST_INFO_TELTYPE", custInfoTeltype);

        // esop参数
        String eos = getData().getString("EOS");
        if (!StringUtils.isEmpty(eos) && !"{}".equals(eos))
        {
            inparam.put("EOS", new DatasetList(eos));
        }

        inparam.put("ELEMENT_INFO", productElements);

        if (IDataUtil.isNotEmpty(productParam))
        {
            inparam.put("PRODUCT_PARAM_INFO", productParam);
        }

        inparam.put("RES_INFO", resInfos);

        // 成员三户信息
        inparam.put("MEM_CUST_INFO", memCustInfo);
        inparam.put("MEM_USER_INFO", memUserInfo);
        inparam.put("MEM_ACCT_INFO", memAcctInfo);

        inparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        return inparam;
    }

    /**
     * 获取集团成员账户信息
     * 
     * @return
     * @throws Exception
     */
    public IData getMemAcctData() throws Exception
    {
        // IData acctInfo = new DataMap(getData().getString("MEM_ACCT_INFO", ""));
        IData acctInfo = getData("MEM_ACCT_INFO");
        return acctInfo;
    }

    /**
     * 获取成员客户信息
     * 
     * @return
     * @throws Exception
     */
    public IData getMemCustData() throws Exception
    {
        // IData custInfo = new DataMap(getData().getString("MEM_CUST_INFO", ""));
        IData memCustInfo = getData("MEM_CUST_INFO");
        return memCustInfo;
    }

    /**
     * 获取成员用户信息
     * 
     * @return
     * @throws Exception
     */
    public IData getMemUserData() throws Exception
    {
        // IData userInfo = new DataMap(getData().getString("MEM_USER_INFO", ""));
        IData memUserInfo = getData("MEM_USER_INFO");
        return memUserInfo;
    }

    /**
     * 获取集团产品元素信息
     * 
     * @author fengsl
     * @date 2013-04-15 *
     * @return
     * @throws Exception
     */
    public IDataset saveProductElemensFrontData() throws Exception
    {
        String selectElementStr = getData().getString("SELECTED_ELEMENTS", "[]");

        IDataset selectElements = new DatasetList(selectElementStr);
        return selectElements;
    }

    /**
     * 获取产品参数信息
     * 
     * @return
     * @throws Exception
     */

    public IDataset saveProductParamInfoFrontData() throws Exception
    {
        IDataset resultset = new DatasetList();
        IData result = new DataMap();
        IDataset productParamAttrset = new DatasetList();
        IData productParam = getData("pam", true);
        if (IDataUtil.isEmpty(productParam))
        {
            return null;
        }

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
        result.put("PRODUCT_ID", getData().getString("PRODUCT_ID"));
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
        IData data = crtParam();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        data.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        IDataset result = CSViewCall.call(this, "CS.OpenGroupMemberSVC.crtTrade", data);
        setAjax(result);
    }

}
