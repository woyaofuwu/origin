
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupProductPage;

public abstract class ProductInfo extends GroupProductPage
{
    public void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        setDynParam(null);
    }

    public abstract String getAdvValidate();

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setAdvValidate("true");
        IData pData = this.getData();
        
        productId = getData().getString("GRP_PRODUCT_ID", "");
        String grpUserEparchyCode = getData().getString("GRP_USER_EPARCHYCODE");

        // 获取产品控制信息
        String productCtrlInfoStr = getData().getString("PRODUCT_CTRL_INFO");
        IData productCtrlInfo = new DataMap();
        if (StringUtils.isEmpty(productCtrlInfoStr))
        {
            productCtrlInfo = AttrBizInfoIntfViewUtil.qryCrtUsProductCtrlInfoByProductId(this, productId);
        }
        else
        {
            productCtrlInfo = new DataMap(productCtrlInfoStr);

        }
        setProductCtrlInfo(productCtrlInfo);

        String groupId = getData().getString("GROUP_ID", "");
        setGroupId(groupId);
        
        //判断offerList是否需要过滤
        IData filerTag = AttrBizInfoIntfViewUtil.qryAttrBizInfoByIdAndIdTypeAttrObjAttrCode(this, productId, "P", "0", "FilerPgTag");
        if(!filerTag.isEmpty()){
        	setServiceName(filerTag.getString("ATTR_VALUE", ""));
        }  

        // 初始账户信息的账户名称-直接从上个页面传存在乱码
        IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, getData().getString("CUST_ID", ""));
        IData initAcctInfo = new DataMap();
        initAcctInfo.put("CUST_GRP_NAME", groupInfo.getString("CUST_NAME"));
        setAcctInfo(initAcctInfo);

        // 获取定制信息
        String useTag = ProductCompInfoIntfViewUtil.qryUseTagStrByProductId(this, productId);
        setUseTag(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag) ? "true" : "false");

        // 获取产品组件的初始条件信息
        IData cond = new DataMap();
        cond.put("EPARCHY_CODE", grpUserEparchyCode);
        cond.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);
        cond.put("TRADE_TYPE_CODE", productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE"));
        cond.put("EFFECT_NOW", true);
        cond.put("PRODUCT_ID", productId);
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	cond.put("AUDIT_INFO_SHOW","false");
        }else{
        	cond.put("AUDIT_INFO_SHOW","true");
        }
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
        
        setCond(cond);

        // 获取产品参数初始参数
        IData dynparam = new DataMap();
        dynparam.put("PRODUCT_ID", productId);
        dynparam.put("CUST_ID", getData().getString("CUST_ID", ""));
        dynparam.put("GROUP_ID", groupId);
        dynparam.put("USER_EPARCHY_CODE", grpUserEparchyCode);

        // ESOP参数
        String eos = getData().getString("EOS");
        // bboss业务esop数据过大，需通过缓存获取
        if (StringUtils.isEmpty(eos) || "{}".equals(eos))
        {
            String key = CacheKey.getBBossESOPInfoKey(getVisit().getStaffId(), "EOS_" + groupId);

            Object eosObject = SharedCache.get(key);

            if (eosObject != null)
            {
                eos = eosObject.toString();
            }

        }
        if (StringUtils.isNotEmpty(eos) && !"{}".equals(eos))
        {
            dynparam.put("EOS", new DatasetList(eos));
        }
        setDynParam(dynparam);

        // 保存动力100 用户信息
        if ("DLBG".equals(getData().getString("BRAND_CODE")))
        {
            savePower100FrontData();
        }

    }

    /**
     * 保存动力100信息
     * 
     * @throws Exception
     */
    public void savePower100FrontData() throws Exception
    {

        IDataset power100SetList = null;
        String power100 = getData().getString("selectedCheckBox", "");

        if (power100 != null && !power100.equals(""))
        {
            String CompixProductStr = getParameter("grpCompixProduct", "");

            IDataset grpCompixProduct = new DatasetList(CompixProductStr);
            boolean ifCanchoice = false;
            if (grpCompixProduct.size() == 0) // 没有必选产品，true
                ifCanchoice = true;

            power100SetList = new DatasetList(power100);
            IData productKind = new DataMap();
            for (int i = 0; i < power100SetList.size(); i++)
            {

                IData tmp = power100SetList.getData(i);
                tmp.put("POWER100_PACKAGE_ID", getProductId()); // 组合包：“动力100产品” 重新赋值为 ”产品id“
                tmp.put("IS_CANCHOICE", ifCanchoice);

                // 判断选择子用户数

                if (productKind.getString(tmp.getString("PRODUCT_ID_B")) == null)
                    productKind.put(tmp.getString("PRODUCT_ID_B"), "");
            }

            int choice_num = productKind.size();

            // 保存power100信息
            IData infos = new DataMap();
            infos.put("POWER100_PRODUCT_INFO", power100SetList);
            infos.put("POWER100_PRODUCT_SIZE", choice_num);
            infos.put("BRAND_CODE", getData().getString("BRAND_CODE"));
            this.setInfos(infos);

        }
    }

    public abstract void setAcctInfo(IData AcctInfo);

    public abstract void setAcctInfos(IDataset AcctInfo);

    public abstract void setAdvValidate(String advValidate);

    public abstract void setCond(IData param);

    public abstract void setDynParam(IData param);

    public abstract void setGroupId(String groupId);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IData infos);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset resList);

    public abstract void setUseTag(String use_tag);
    
    public abstract void setServiceName(String serviceName);
    /*
     * REQ201909170024  关于企业宽带5折以下价格配置权限的开发需求 
     * guonj@
     * 2019-10-12
     */
    public void isPrivFiveDiscount(IRequestCycle cycle) throws Exception
    {
    	/*判断工号是否有套餐折扣权限*/
		String tradeStaffId = getVisit().getStaffId();
		IData data = getData();
    	data.put("X_RESULTCODE", "0");
    	if (StaffPrivUtil.isPriv(tradeStaffId, "PRIV_BROADBAND_DISCOUNT", "1")) {
    		data.put("X_RESULTCODE", "00");
    	}
        setAjax(data);
    }
}
