
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class DiscntInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = -4485409242967869565L;

    public static IDataset queryDiscntsByPkgIdEparchy(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset discntList = DiscntInfoQry.queryDiscntsByPkgIdEparchy(packageId, eparchyCode);

        return discntList;
    }

    public IDataset getDiscntByProduct(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        IDataset discntList = DiscntInfoQry.getDiscntByProduct(productId);

        return discntList;
    }

    public IDataset getDiscntInfoByDisCode(IData input) throws Exception
    {
        String discntCode = input.getString("DISCNT_CODE");

        return DiscntInfoQry.getDiscntInfoByDisCode(discntCode);
    }

    public IDataset getDiscntlist(IData idata) throws Exception
    {

        String discntCode = idata.getString("DISCNT_CODE");

        // return DiscntInfoQry.getDiscntlist(discntCode, null);
        return null;
    }

    public IDataset getDiscntsByUserIdDiscntCode(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        String discntCode = inparams.getString("DISCNT_CODE");
        String eparchy = inparams.getString(Route.ROUTE_EPARCHY_CODE);
        // IDataset discntInfos = DiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, discntCode, eparchy);

        // return discntInfos;
        return null;
    }

    public IDataset getDiscntsByUserIdForGrp(IData inparams) throws Exception
    {
        // IDataset discntInfos = DiscntInfoQry.getDiscntsByUserIdForGrp(inparams.getString("USER_ID"));
        // return discntInfos;

        return null;
    }

    /**
     * 查询优惠名称
     */
    public IDataset getDscNameByPackageId(IData iData) throws Exception
    {
        String discntCode = iData.getString("DISCNT_CODE");
        String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);

        IData retData = new DataMap();
        retData.put("DISCNT_NAME", discntName);

        IDataset retDataSet = new DatasetList();
        retDataSet.add(retData);

        return retDataSet;
    }

    /**
     * @description 根据用户标识、产品模式、品牌获取用户资费信息
     * @author yish
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getMainFamUserId(IData inparam) throws Exception
    {
        String userId = inparam.getString("USER_ID");
        String productMode = inparam.getString("PRODUCT_MODE");
        String brandCode = inparam.getString("BRAND_CODE");

        return UserDiscntInfoQry.getMainFamUserId(userId, productMode, brandCode);
    }

    public IDataset getMembDiscntByGrpProductId(IData iData) throws Exception
    {
        String productId = iData.getString("PRODUCT_ID");
        String eparchyCode = iData.getString("EPARCHY_CODE");
        String tradeStaffId = iData.getString("TRADE_STAFF_ID");
        IDataset MembDiscnt = UserDiscntInfoQry.getMembDiscntByGrpProductId(productId, eparchyCode, tradeStaffId);
        return MembDiscnt;
    }

    public IDataset getMembSaleDiscntByGrpProductId(IData iData) throws Exception
    {
        String productId = iData.getString("PRODUCT_ID");
        String eparchyCode = iData.getString("EPARCHY_CODE");
        // IDataset MembDiscnt = DiscntInfoQry.getMembSaleDiscntByGrpProductId(productId, eparchyCode);
        // return MembDiscnt;

        return null;
    }

    public IDataset getMembVPMNDiscntByGrpProductId(IData inparams) throws Exception
    {
        // IDataset MembDiscnt = DiscntInfoQry.getMembVPMNDiscntByGrpProductId(inparams);
        // return MembDiscnt;

        return null;
    }

    /**
     * 根据user_id 查询集团产品订购优惠信息 并过滤权限
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset getUserDiscntInfo(IData iData) throws Exception
    {
        iData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        // return DiscntInfoQry.getUserDiscntInfo(iData);

        return null;
    }

    public IDataset getUserProductDis(IData iData) throws Exception
    {
        String userId = iData.getString("USER_ID");
        String userIdA = iData.getString("USER_ID_A");
        return UserDiscntInfoQry.getUserProductDis(userId, userIdA);
    }

    /**
     * 通过USER_ID、USER_ID_A、PRODUCT_ID、PACKAGE_ID、SERVICE_ID、INST_ID查询用户某条服务
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset getUserSingleProductDis(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        String userIdA = inparams.getString("USER_ID_A");
        String productId = inparams.getString("PRODUCT_ID");
        String packgeId = inparams.getString("PACKAGE_ID");
        String discntCode = inparams.getString("DISCNT_CODE");
        String instId = inparams.getString("INST_ID");

        IDataset discntInfos = UserDiscntInfoQry.getUserSingleProductDis(userId, userIdA, productId, packgeId, discntCode, instId, null);
        return discntInfos;
    }

    public IDataset getUserSingleProductDisForGrp(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        String userIdA = inparams.getString("USER_ID_A");
        String productId = inparams.getString("PRODUCT_ID");
        String packgeId = inparams.getString("PACKAGE_ID");
        String discntCode = inparams.getString("DISCNT_CODE");
        String instId = inparams.getString("INST_ID");
        IDataset discntInfos = DiscntInfoQry.getUserSingleProductDisForGrp(userId, userIdA, productId, packgeId, discntCode, instId, null);
        return discntInfos;
    }

    public IDataset qryByPidPkgTypeCode(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String pkgTypeCode = input.getString("PACKAGE_TYPE_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        return DiscntInfoQry.qryByPidPkgTypeCode(productId, pkgTypeCode, eparchyCode);
    }

    /**
     * 查询折扣信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset queryDiscntCodeByTradeStaffID(IData idata) throws Exception
    {
        String productId = idata.getString("PRODUCT_ID");

        return DiscntInfoQry.queryDiscntCodeByTradeStaffID(productId, null);
    }

    public IDataset queryDiscntsByProductId(IData inparam) throws Exception
    {
        String productId = inparam.getString("PRODUCT_ID", "");
        IDataset dataset = UserDiscntInfoQry.queryDiscntsByProductId(productId, null);
        for (int i = 0; i < dataset.size(); i++)
        {
            IData map = dataset.getData(i);
            String elementName = map.getString("ELEMENT_ID") + " | " + map.getString("ELEMENT_NAME");
            map.put("ELEMENT_NAME", elementName);
        }
        return dataset;
    }

    /**
     * 查询家庭主卡可选优惠
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryFamilyAppendDiscnt(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset discntList = DiscntInfoQry.getDiscntByProduct(productId);
        IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", eparchyCode);

        return IDataUtil.filterByEqualsCol(discntList, "DISCNT_CODE", commList, "PARAM_CODE");
    }

    public IDataset queryMembVPMNDiscntByGrpProductId(IData inparam) throws Exception
    {
        String userId = inparam.getString("USER_ID", "");
        IDataset dataset = UserDiscntInfoQry.queryMembVPMNDiscntByGrpProductId(userId, null);
        for (int i = 0; i < dataset.size(); i++)
        {
            IData map = dataset.getData(i);
            String elementName = map.getString("ELEMENT_ID") + " | " + map.getString("ELEMENT_NAME");
            map.put("ELEMENT_NAME", elementName);
        }
        return dataset;
    }

    /**
     * 查询集团客户已经订购了年包赠送流量优惠((目前版本写死))
     */
    public IDataset querySaleActiveDiscnt(IData idata) throws Exception
    {
        String userId = idata.getString("USER_ID");

        return UserDiscntInfoQry.querySaleActiveDiscnt(userId, null);
    }
}
