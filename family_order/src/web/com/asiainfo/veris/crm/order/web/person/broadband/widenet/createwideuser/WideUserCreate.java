
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.createwideuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WideUserCreate extends PersonBasePage
{
	
    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideUserCreateSVC.checkSerialNumber", data);
        IDataset acctInfos = dataset.getData(0).getDataset("ALL_ACCT");
        setAllAcct(acctInfos);
        setAjax(dataset);
    }

    /**
     * 获取产品费用
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        data.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        data.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        data.put("PACKAGE_ID", "");
        data.put("ELEMENT_ID", data.getString("ELEMENT_ID"));
        data.put("ELEMENT_TYPE_CODE", "P");
        data.put("TRADE_FEE_TYPE", "3");
        data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    /**
     * @param clcle
     * @throws Exception
     * @author chenzm
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideUserCreateSVC.getWidenetProductInfo", data);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), dataset);
        setProductList(dataset);
    }

    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        if ("ADSL".equals(data.getString("WIDE_TYPE")) || "TTADSL".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "612");
        }
        else if ("XIAN".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "613");
        }
        else if ("GPON".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "600");
        }
        setInfo(param);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        String routeId = data.getString("EPARCHY_CODE");
        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            data.put("EPARCHY_CODE", "0898");
        }
        
        // 商务宽带开户特殊处理，号码需要在原号码后加0000，并递增
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
                
        IDataset wideSNdataset = CSViewCall.call(this, "SS.WideUserCreateSVC.getWideSerialNumber", data);
        String wideSerialNumber = wideSNdataset.getData(0).getString("WIDE_SERIAL_NUMBER");
        data.put("WIDE_SERIAL_NUMBER", wideSerialNumber);
        
        IDataset dataset = CSViewCall.call(this, "SS.WideUserCreateRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setAllAcct(IDataset datas);

    public abstract void setInfo(IData info);

    public abstract void setProductList(IDataset datas);

}
