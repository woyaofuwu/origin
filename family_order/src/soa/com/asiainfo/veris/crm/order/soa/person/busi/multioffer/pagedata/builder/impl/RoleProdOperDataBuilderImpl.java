/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.impl;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.OfferQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.interfaces.IPageDataBuilder;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.PackageOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.ProductOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.RoleOperData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RoleProdOperDataBuilderImpl.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-29 上午10:15:37 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
 */

public class RoleProdOperDataBuilderImpl implements IPageDataBuilder
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-4-24 下午04:33:08 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
     */
    private List<PackageOperData> buildPackOperData(RoleOperData roleOperData, IData param) throws Exception
    {
        List<PackageOperData> packOperList = new ArrayList<PackageOperData>();
        String productId = param.getString("PRODUCT_ID");
        String roleId = param.getString("ROLE_CODE");
        String itemKindId = "ROLE_INCLUDE_PROD";
        IDataset rolePackList = OfferQry.getRolePackageList(productId, roleId, itemKindId);
        for (int i = 0; i < rolePackList.size(); i++)
        {
            PackageOperData packOperData = new PackageOperData();
            IData rolePack = rolePackList.getData(i);
            packOperData.setPackageId(rolePack.getString("PACKAGE_ID"));
            packOperData.setPackageName(rolePack.getString("PACKAGE_NAME"));
            List<ProductOperData> eleOperList = buildProdOperData(packOperData, param);
            packOperData.setEleOperList(eleOperList);
            packOperList.add(packOperData);
        }
        return packOperList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-24 下午04:12:34 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
     */
    public RoleOperData buildPageOperData(IData param) throws Exception
    {
        RoleOperData roleOperData = new RoleOperData();
        String productId = param.getString("PRODUCT_ID");
        String roleId = param.getString("ROLE_CODE");
        IData productRole = OfferQry.getProductRoleByPK(productId, roleId);
        roleOperData.setRoleId(roleId);
        roleOperData.setRoleName(productRole.getString("ROLE_NAME"));
        roleOperData.setNetTypeCode(productRole.getString("NET_TYPE_CODE"));
        List<PackageOperData> packOperList = buildPackOperData(roleOperData, param);
        roleOperData.setPackOperList(packOperList);
        return roleOperData;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-4-24 下午04:41:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
     */
    private List<ProductOperData> buildProdOperData(PackageOperData packOperData, IData param) throws Exception
    {
        List<ProductOperData> prodOperList = new ArrayList<ProductOperData>();
        String packageId = packOperData.getPackageId();
        IDataset pkgEleList = ProductInfoQry.queryByPkgId(packageId);
        for (int i = 0; i < pkgEleList.size(); i++)
        {
            ProductOperData prodOperData = new ProductOperData();
            IData pkgEleInfo = pkgEleList.getData(i);
            prodOperData.setProductId(pkgEleInfo.getString("PRODUCT_ID"));
            prodOperData.setProductName(pkgEleInfo.getString("PRODUCT_NAME"));
            prodOperList.add(prodOperData);
        }
        return prodOperList;
    }

}
