
package com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.usersvc.UserSvcInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.packageelement.PackageElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public class GrpUseChgFlowMainHttpHandler extends CSBizHttpHandler
{

    private String productId;

    private String userId;

    public String getGrpProductId() throws Exception
    {
        if (StringUtils.isBlank(productId))
        {
            productId = getData().getString("GRP_PRODUCT_ID", "");
        }
        return productId;
    }

    public String getGrpUserId() throws Exception
    {
        if (StringUtils.isBlank(userId))
        {
            userId = getData().getString("GRP_USER_ID", "");
        }
        return userId;
    }

    /**
     * 满足跨省V网条件，则走批量升级服务
     * 
     * @param productElements
     * @return
     * @throws Exception
     */
    public IData readyToVpmnUp(IDataset productElements) throws Exception
    {

        IData result = new DataMap();
        IDataset productParamAttrset = new DatasetList();
        IData productParam = getData("pam", true);
        if (IDataUtil.isEmpty(productParam))
            return null;
        String oldvpnscare = productParam.getString("OLD_VPN_SCARE_CODE", "0");
        String vpnscare = productParam.getString("VPN_SCARE_CODE", "");
        String vpnno = productParam.getString("VPN_NO", "");
        boolean hasAdd801 = false; // 是否有增加801元素
        boolean hasDel801 = false; // 是否有删除801元素
        if (IDataUtil.isNotEmpty(productElements))
        {
            for (int i = 0, size = productElements.size(); i < size; i++)
            {
                IData eleData = productElements.getData(i);
                if ("801".equals(eleData.getString("ELEMENT_ID")) && TRADE_MODIFY_TAG.Add.getValue().equals(eleData.getString("MODIFY_TAG")))
                {
                    hasAdd801 = true;
                }
                if ("801".equals(eleData.getString("ELEMENT_ID")) && TRADE_MODIFY_TAG.DEL.getValue().equals(eleData.getString("MODIFY_TAG")))
                {
                    hasDel801 = true;
                }
            }
        }
        // add by lixiuyu@20100802 判断用户是否已经订购“漫游短号服务”，如果之前已经订购就不走批量
        boolean old801Flag = false;
        IDataset old801svcs = UserSvcInfoIntfViewUtil.qryGrpUserSvcByUserSvcId(this, getGrpUserId(), "801");
        if (IDataUtil.isNotEmpty(old801svcs))
        {
            old801Flag = true;
        }

        result.put("HAS_ADD_801", hasAdd801);
        result.put("HAS_DEL_801", hasDel801);
        result.put("OLD_801_FLAG", old801Flag);
        result.put("VPN_SCARE_CODE", vpnscare);
        result.put("OLD_VPN_SCARE_CODE", oldvpnscare);
        return result;
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

    public IDataset saveUserGrpPackageInfoFrontData() throws Exception
    {

        String resInfoStr = getData().getString("SELECTED_GRPPACKAGE_LIST", "[]");

        if ("".equals(resInfoStr))
        {
            return new DatasetList();
        }

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
        IDataset resinfos = saveResInfoFrontData();

        IDataset productElements = saveProductElemensFrontData();

        IDataset productParam = saveProductParamInfoFrontData();

        IDataset userGrpPackaeInfos = saveUserGrpPackageInfoFrontData();

        IData svcData = new DataMap();
        svcData.put("USER_ID", getGrpUserId());
        svcData.put("PRODUCT_ID", getGrpProductId());
        svcData.put("REMARK", getData().getString("param_REMARK", ""));
        svcData.put("RES_INFO", resinfos);
        svcData.put("GRP_PACKAGE_INFO", userGrpPackaeInfos);
        svcData.put("ELEMENT_INFO", productElements);
        svcData.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE"));
        svcData.put("AUDIT_STAFF_ID", getData().getString("AUDIT_STAFF_ID", ""));
        if (IDataUtil.isNotEmpty(productParam))
            svcData.put("PRODUCT_PARAM_INFO", productParam);

        // ESOP参数
        String eos = getData().getString("EOS");
        if (StringUtils.isNotEmpty(eos) && !"{}".equals(eos))
        {
            svcData.put("EOS", new DatasetList(eos));
        }
        IData userInfo = new DataMap();
        userInfo.put("CONTRACT_ID", getData().getString("SELE_CONTRACTPRODUCT_CONTRACT_ID"));
        svcData.put("USER_INFO", userInfo);

        String busiType = getData().getString("BUSI_TYPE");
        if (StringUtils.isNotBlank(busiType))
        {
            svcData.put("BUSI_CTRL_TYPE", busiType);
        }
        // 费用信息
        svcData.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        svcData.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        // 保存power100信息
        String power100ProductInfo = getData().getString("POWER100_PRODUCT_INFO");

        if (StringUtils.isNotEmpty(power100ProductInfo))
        {
            IData power100ProductInfoMap = new DataMap(power100ProductInfo);
            svcData.put("POWER100_PRODUCT_INFO", power100ProductInfoMap.getDataset("POWER100_PRODUCT_INFO"));

        }
        // 根据产品编号获取产品的品牌信息
        String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, getGrpProductId());
		if ("BOSG".equals(brandCode))// BBOSS业务
		{
			IData bbossData = new DataMap(getData().getString("productGoodInfos"));// BBOSS商产品信息
			if (IDataUtil.isNotEmpty(bbossData)) 
			{
			IDataset productInfos = bbossData.getDataset("PRODUCT_INFO_LIST");
			for (int i = 0; i < productInfos.size(); i++) 
			{
				IData product = productInfos.getData(i);
				String productId = product.getString("PRODUCT_ID");
				String productIndex = product.getString("PRODUCT_INDEX");

				// 产品对应的元素信息
				IData productElement = bbossData.getData("PRODUCTS_ELEMENT");
				if (IDataUtil.isNotEmpty(productElement)) 
				{
					IDataset merchPElements = productElement.getDataset(productId + "_" + productIndex);
					if (IDataUtil.isNotEmpty(merchPElements)) 
					{
						IDataset dctDatasetDel = DataHelper.filter(	merchPElements,	"ELEMENT_TYPE_CODE=D,MODIFY_TAG=1");
						for (int j = 0, jSize = dctDatasetDel.size(); j < jSize; j++) 
						{
							String discntId = dctDatasetDel.getData(j).getString("ELEMENT_ID");
							String packageId = dctDatasetDel.getData(j).getString("PACKAGE_ID");
							String cancelTag = PackageElementInfoIntfViewUtil.qryCancelTagStrByPackageIdAndElementIdElementTypeCode(this, packageId,discntId, "D");
							if (!"".equals(cancelTag) && "4".equals(cancelTag)) 
							{
								CSViewException.apperr(	GrpException.CRM_GRP_890, discntId);
							}
						}
					  }
					}
				}
			}

            if (StringUtils.isNotEmpty(getData().getString("BBossParamInfo")))
            {
                IData bbossparam = new DataMap(getData().getString("BBossParamInfo"));// BBOSS产品特殊参数信息
                svcData.put("BBossParamInfo", bbossparam);
            }

            svcData.put("BBOSS_INFO", bbossData);
            IDataset result = CSViewCall.call(this, "CS.ChangeBBossUserSVC.crtOrder", svcData);
            setAjax(result);
            return;
        }
        if ("6130".equals(getGrpProductId()))
        {// 融合总机
            IDataset result = CSViewCall.call(this, "SS.ChangeCentrexSuperTeleUserElementSVC.crtOrder", svcData);
            this.setAjax(result);
            return;
        }
        if ("8000".equals(getGrpProductId()))
        {// 普通V网
            IData reData = readyToVpmnUp(productElements);
            String oldvpnscare = reData.getString("OLD_VPN_SCARE_CODE", "0");
            String vpnscare = reData.getString("VPN_SCARE_CODE", "");
            boolean hasAdd801 = reData.getBoolean("HAS_ADD_801", false);
            // modify by lixiuyu@20100512 VPN升级为跨省VPN， hasAdd801区别是否订购“漫游短号服务”
            if (!oldvpnscare.equals("2") && (vpnscare.equals("2") || hasAdd801))
            {
                IData proParam = getData("pam", true);
                svcData.putAll(proParam);
                svcData.putAll(reData);
                if ("2".equals(vpnscare))
                {
                    svcData.put("HAS_VPN_SCARE", true); // 选择了跨省升级
                }
                else
                {
                    svcData.put("HAS_VPN_SCARE", false); // 没选择跨省升级
                }
                // 批量升级
                IDataset retDataset = CSViewCall.call(this, "SS.UpgradeVpnBeanSVC.crtBatUptoCountrywide", svcData);
                // 设置返回值
                setAjax(retDataset);
                return;
            }
        }

        /** VOIP专线（专网专线） */
        if ("7010".equals(getGrpProductId()))
        {
            IDataset result = CSViewCall.call(this, "SS.ChangeVoipUserElementSVC.crtOrder", svcData);
            this.setAjax(result);
            return;
        }
        /** 互联网专线接入（专网专线） */
        if ("7011".equals(getGrpProductId()))
        {
            IDataset result = CSViewCall.call(this, "SS.ChangeNetinUserElementSVC.crtOrder", svcData);
            this.setAjax(result);
            return;
        }
        /** 数据专线（专网专线） */
        if ("7012".equals(getGrpProductId()))
        {
            IDataset result = CSViewCall.call(this, "SS.ChangeDatalineUserElementSVC.crtOrder", svcData);
            this.setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.ChangeUserElementSvc.changeUserElement", svcData);
        setAjax(result);
    }
}
