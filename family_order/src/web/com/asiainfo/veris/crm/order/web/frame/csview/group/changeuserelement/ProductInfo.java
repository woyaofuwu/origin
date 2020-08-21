
package com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class ProductInfo extends GroupBasePage
{

    /**
     * 作用：页面的初始化
     * 
     * @author luoy 2009-07-29
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        productId = getData().getString("GRP_PRODUCT_ID");
        String userId = getData().getString("GRP_USER_ID");
        String busiType = getData().getString("BUSI_TYPE", BizCtrlType.ChangeUserDis);
        String eos = getData().getString("EOS");

        setProductId(productId);
        // 获取产品控制信息
        String productCtrlInfoStr = getData().getString("PRODUCT_CTRL_INFO");
        IData productCtrlInfo = new DataMap();
        if (StringUtils.isBlank(productCtrlInfoStr))
        {
            productCtrlInfo = AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, productId, busiType);
        }
        else
        {
            productCtrlInfo = new DataMap(productCtrlInfoStr);
        }
        setProductCtrlInfo(productCtrlInfo);

        //判断offerList是否需要过滤
        IData filerTag = AttrBizInfoIntfViewUtil.qryAttrBizInfoByIdAndIdTypeAttrObjAttrCode(this, productId, "P", "0", "FilerPgTag");
        if(!filerTag.isEmpty()){
            setServiceName(filerTag.getString("ATTR_VALUE", ""));
        } 
        
        // 产品组件参数
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        data.put("GROUP_ID", getData().getString("GROUP_ID"));
        data.put("EPARCHY_CODE", getData().getString("GRP_USER_EPARCHYCODE", ""));
        data.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE", ""));
        data.put("TRADE_TYPE_CODE", productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE"));
        data.put("GRP_SN", getData().getString("GRP_SN"));
        if (StringUtils.isNotEmpty(eos) && !"{}".equals(eos))
        {
            data.put("EOS", eos);
        }
        String offerLineId  = UpcViewCall.queryOfferLineIdByOfferId(this, "P", productId);
        data.put("OFFER_LINE_ID", offerLineId);
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	data.put("AUDIT_INFO_SHOW","false");
        }else{
        	data.put("AUDIT_INFO_SHOW","true");
        }
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
        
        setCond(data);

        // 初始化已有资源信息
        setResList(GroupProductUtilView.initResList(this, userId, "-1", Route.CONN_CRM_CG));

        // 获取集团产品定制信息
        String useTag = ProductCompInfoIntfViewUtil.qryUseTagStrByProductId(this, productId);
        setUseTag(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag) ? "true" : "false");

        // 保存动力100 用户信息
        if ("DLBG".equals(getData().getString("BRAND_CODE")))
        {
            savePower100FrontData();
        }

        IData info = new DataMap();
        info.put("BUSI_TYPE", busiType);
        setInfo(info);
    }

    // 保存动力100信息
    public void savePower100FrontData() throws Exception
    {

        IDataset power100SetList = null;
        String power100 = getData().getString("selectedCheckBox", "[]");

        if (power100 != null && !power100.equals(""))
        {
            String CompixProductStr = getParameter("grpCompixProduct", "[]");
            IDataset grpCompixProduct = new DatasetList(CompixProductStr);
            boolean ifCanchoice = false;
            if (grpCompixProduct.size() == 0)
                ifCanchoice = true;

            power100SetList = new DatasetList(power100);
            IData productKind = new DataMap();
            for (int i = 0; i < power100SetList.size(); i++)
            {
                IData tmp = power100SetList.getData(i);
                tmp.put("POWER100_PACKAGE_ID", getProductId());
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
            setInfos(infos);
        }

    }

    public abstract void setCond(IData info);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IData power100ProductInfo);

    public abstract void setPlusProducts(IDataset plusProducts);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset reslist);

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
