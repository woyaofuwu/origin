
package com.asiainfo.veris.crm.order.web.group.bat.batxhkregcheck;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BatXhkRegCheck extends GroupBasePage
{   
    public void initial(IRequestCycle cycle) throws Throwable
    {
        initialCondition(cycle);
    }

    /**
     * 初始化服务资费区域Condition参数
     */
    public void initialCondition(IRequestCycle cycle) throws Throwable
    {
        getData().put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        getData().put("EPARCHY_CODE", getTradeEparchyCode());
        String esopTag = getData().getString("ESOP_TAG");
        if ("ESOP".equals(esopTag))
        {
            getData().put("ESOP_TAG", esopTag);
        }

        IDataset opertypes = StaticUtil.getStaticList("TRADEBATTASK_OPERTYPE");

        getData().put("OPERTYPES", opertypes);

        setCondition(getData());
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
        String cust_id = getParameter("CUST_ID");
        IDataset userProductList = UCAInfoIntfViewUtil.qryGrpUserInfoByCustId(this, cust_id, false);
        if (IDataUtil.isEmpty(userProductList))
        {
            userProductList = new DatasetList();
        }

        IDataset amList = new DatasetList();

        IDataset adcList = DataHelper.filter(userProductList, "BRAND_CODE=ADCG");
        IDataset masList = DataHelper.filter(userProductList, "BRAND_CODE=MASG");
        amList.addAll(adcList);
        amList.addAll(masList);

        if (IDataUtil.isEmpty(amList))
        {
            CSViewException.apperr(BatException.CRM_BAT_62, cust_id);
        }

        for (int i = 0, size = amList.size(); i < size; i++)
        {
            IData user = amList.getData(i);
            String productid = amList.getData(i).getString("PRODUCT_ID");
            String productNameString = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productid, false);
            if (StringUtils.isBlank(productNameString)||!productid.equals("10005744"))//非学护卡产品删除
            {
                amList.remove(i); // 产品下线了 应该删掉 不然报空指针异常
                i--;
                size--;
                continue;
            }

            String productName = productid + "|" + productNameString + "|" + user.getString("SERIAL_NUMBER") + "|" + user.getString("USER_ID");
            amList.getData(i).put("PRODUCT_NAME", productName);
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
        String eparchyCode = groupUserData.getString("EPARCHY_CODE");
        if (StringUtils.isNotBlank(eparchyCode) || null == eparchyCode)
        {
            eparchyCode = getTradeEparchyCode();
        }
        
        //ADC, MAS需要校验集团是否暂停，暂停不让做导入导出操作
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset userGrpPlatSvcList = CSViewCall.call(this, "CS.UserGrpPlatSvcInfoQrySVC.getLxtGrpPlatSvcByUserId", param);
        if (IDataUtil.isEmpty(userGrpPlatSvcList))
        {
            CSViewException.apperr(GrpException.CRM_GRP_25);//没有找到集团平台服务信息[GRP_PLATSVC],请联系管理员
        }
        //不为A状态 不能进行成员的新增和退订操作
        if (!StringUtils.equals("A", userGrpPlatSvcList.getData(0).getString("BIZ_STATUS")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_1011);//该业务已暂停
        }
        
        IData condition = getData();
        condition.put("GRP_USER_ID", userId);
        condition.put("EPARCHY_CODE", eparchyCode);
        condition.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        condition.put(Route.USER_EPARCHY_CODE, eparchyCode);
        condition.put("GROUP_ID", getData().getString("GROUP_ID", ""));

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

        this.setCondition(condition);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setOrderProductList(IDataset orderProductList);

    public abstract void setUserInfo(IData userInfo);
}
