
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.productelementview;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.multitableinfo.UserProductElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userplatsvcinfo.UserPlatSvcInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public class ProductElementViewHttpHandler extends CSBizHttpHandler
{

    /*
     * @description BBOSS商品有多个成员产品，同一个成员有可能被添加到多个成员产品下面
     * @author xunyl
     * @date 2013-08-07
     */
    protected IDataset getBBossUsers(String memUserId, String grpUserId, String productId, String eparchCode) throws Exception
    {
        // 1- 定义返回对象
        IDataset bbossUserIds = new DatasetList();

        // 2- 根据商品用户编号查询出商品对应的所有产品用户
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        IDataset relaUUInfoList = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(this, grpUserId, relationTypeCode, "0");// 0表示商品与产品UU关系
        if (IDataUtil.isEmpty(relaUUInfoList))
        {
            return bbossUserIds;
        }

        // 3- 校验产品用户是否被成员订购
        for (int i = 0; i < relaUUInfoList.size(); i++)
        {
            IData relaUUInfo = relaUUInfoList.getData(i);
            String productUserId = relaUUInfo.getString("USER_ID_B");
            boolean isMebUserInfo = isMebProductUserInfo(productUserId, memUserId, relationTypeCode, eparchCode);
            if (isMebUserInfo)
            {
                bbossUserIds.add(productUserId);
                ;
            }
        }

        // 4- 返回集团下的所有用户编号
        return bbossUserIds;
    }

    /**
     * 查询集团用户ID订购的元素信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public IDataset getGrpProductElements(String userId) throws Exception
    {
        IDataset userElementList = UserProductElementInfoIntfViewUtil.qryGrpUserElementInfosByUserId(this, userId);
        return userElementList;
    }

    public IDataset getMebProductElements(String userId, String userIdA, String productId, String eparchyCode) throws Exception
    {

        String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
        IDataset elementList = new DatasetList();
        if ("BOSG".equals(productBrandCode))
        {// BBOSS业务
            IDataset bbossUserIds = getBBossUsers(userId, userIdA, productId, eparchyCode);
            for (int i = 0; i < bbossUserIds.size(); i++)
            {
                IDataset bbossUserElementDataset = getUserElements(userId, bbossUserIds.get(i).toString(), productId, eparchyCode);
                elementList.addAll(bbossUserElementDataset);
            }
        }
        else
        {
            IDataset userElementList = getUserElements(userId, userIdA, productId, eparchyCode);
            IDataset usersSpElementList = getUserSPElements(userId, userIdA, productId, eparchyCode);
            elementList.addAll(userElementList);
            elementList.addAll(usersSpElementList);

        }
        return elementList;
    }

    /**
     * 查询用户订购的元素信息
     * 
     * @param userId
     * @param userIdA
     * @param productId
     * @param eparchycode
     * @return
     * @throws Exception
     */
    protected IDataset getUserElements(String userId, String userIdA, String productId, String eparchycode) throws Exception
    {
        IDataset useproductelmemets = UserProductElementInfoIntfViewUtil.qryUserElementInfosByUserIdAndUserIdA(this, userId, userIdA, eparchycode);

        return useproductelmemets;
    }

    /**
     * 查询用户订购的sp服务信息
     * 
     * @param userId
     * @param userIdA
     * @param productId
     * @param eparchycode
     * @return
     * @throws Exception
     */
    protected IDataset getUserSPElements(String userId, String userIdA, String productId, String eparchycode) throws Exception
    {

        IDataset spElements = UserPlatSvcInfoIntfViewUtil.qryMebPlatSvcInfosByUserIdAndGrpProductId(this, userId, productId, eparchycode);
        if (IDataUtil.isNotEmpty(spElements))
        {
            for (int j = 0; j < spElements.size(); j++)
            {
                IData map = spElements.getData(j);
                map.put("ELEMENT_TYPE_NAME", "SP服务");
            }
        }

        return spElements;
    }

    /*
     * @description 校验产品用户是否被成员订购
     * @author xunyl
     * @date 2014-05-28
     */
    private boolean isMebProductUserInfo(String userId, String mebUserId, String relationTypeCode, String eparchCode) throws Exception
    {
        // 1- 定义返回变量(默认为false)
        boolean result = false;

        // 2- 查询用户是否为当前商品的产品用户
        IDataset productUserInfoList = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, mebUserId, userId, relationTypeCode, eparchCode);
        if (IDataUtil.isNotEmpty(productUserInfoList))
        {
            result = true;
        }

        // 3- 返回查询结果
        return result;
    }

    public void renderProductElementsList() throws Exception
    {
        IData inpara = getData();
        String userId = inpara.getString("USER_ID");
        String userIdA = inpara.getString("USER_ID_A");
        String eparchyCode = inpara.getString("EPARCHY_CODE");
        String productId = inpara.getString("PRODUCT_ID");
        IDataset elementList = new DatasetList();
        if (StringUtils.isNotBlank(userIdA) && !userIdA.equals("-1"))
        { // 成员的
            elementList = getMebProductElements(userId, userIdA, productId, eparchyCode);
        }
        else
        {
            elementList = getGrpProductElements(userId);
        }

        this.setAjax(elementList);

    }

}
