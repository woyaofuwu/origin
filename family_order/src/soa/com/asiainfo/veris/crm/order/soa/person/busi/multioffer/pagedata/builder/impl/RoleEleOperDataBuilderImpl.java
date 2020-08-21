/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.impl;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.OfferQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.interfaces.IPageDataBuilder;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.AttrOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.DiscntOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.PackageOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.RoleOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.ServiceOperData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PackEleOperDataBuilderImpl.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-24 下午04:12:34 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
 */

public class RoleEleOperDataBuilderImpl implements IPageDataBuilder
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-29 上午10:29:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
     */
    private List<AttrOperData> buildDisAttrOperData(DiscntOperData eleOperData, IData param)
    {
        // TODO Auto-generated method stub
        return new ArrayList<AttrOperData>();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-29 上午10:27:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
     */
    private List<DiscntOperData> buildDiscntOperData(PackageOperData packOperData, IData param) throws Exception
    {
        List<DiscntOperData> discntOperList = new ArrayList<DiscntOperData>();
        String packageId = packOperData.getPackageId();
        IDataset pkgEleList = DiscntInfoQry.queryDiscntsByPkgId(packageId);
        // IDataset pkgEleList = PkgElemInfoQry.getPackageElementByPackageId(packageId);
        for (int i = 0; i < pkgEleList.size(); i++)
        {
            DiscntOperData discntOperData = new DiscntOperData();
            IData pkgEleInfo = pkgEleList.getData(i);
            discntOperData.setElementId(pkgEleInfo.getString("DISCNT_CODE"));
            discntOperData.setElementName(pkgEleInfo.getString("DISCNT_NAME"));
            discntOperData.setElementType("D");
            List<AttrOperData> attrOperList = buildDisAttrOperData(discntOperData, param);
            discntOperData.setAttrOperList(attrOperList);
            discntOperList.add(discntOperData);
        }
        return discntOperList;
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
     * @date: 2014-4-24 下午04:33:08 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
     */
    private List<PackageOperData> buildPackOperData(RoleOperData roleOperData, IData param) throws Exception
    {
        List<PackageOperData> packOperList = new ArrayList<PackageOperData>();
        String productId = param.getString("PRODUCT_ID");
        String roleId = param.getString("ROLE_CODE");
        String itemKindId = "ROLE_INCLUDE_ELEMENT";
        IDataset rolePackList = OfferQry.getRolePackageList(productId, roleId, itemKindId);
        for (int i = 0; i < rolePackList.size(); i++)
        {
            PackageOperData packOperData = new PackageOperData();
            IData rolePack = rolePackList.getData(i);
            packOperData.setPackageId(rolePack.getString("PACKAGE_ID"));
            packOperData.setPackageName(rolePack.getString("PACKAGE_NAME"));
            List<ServiceOperData> svcOperList = buildSvcOperData(packOperData, param);
            packOperData.setSvcOperList(svcOperList);
            List<DiscntOperData> disOperList = buildDiscntOperData(packOperData, param);
            packOperData.setDisOperList(disOperList);
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
     * @date: 2014-4-24 下午04:51:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
     */
    private List<AttrOperData> buildSvcAttrOperData(ServiceOperData eleOperData, IData param)
    {
        // TODO Auto-generated method stub
        return new ArrayList<AttrOperData>();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-29 上午10:27:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
     */
    private List<ServiceOperData> buildSvcOperData(PackageOperData packOperData, IData param) throws Exception
    {
        List<ServiceOperData> svcOperList = new ArrayList<ServiceOperData>();
        String packageId = packOperData.getPackageId();
        IDataset pkgEleList = SvcInfoQry.queryByPkgId(packageId);
        // IDataset pkgEleList = PkgElemInfoQry.getPackageElementByPackageId(packageId);
        for (int i = 0; i < pkgEleList.size(); i++)
        {
            ServiceOperData svcOperData = new ServiceOperData();
            IData pkgEleInfo = pkgEleList.getData(i);
            svcOperData.setElementId(pkgEleInfo.getString("SERVICE_ID"));
            svcOperData.setElementName(pkgEleInfo.getString("SERVICE_NAME"));
            svcOperData.setElementType("S");
            List<AttrOperData> attrOperList = buildSvcAttrOperData(svcOperData, param);
            svcOperData.setAttrOperList(attrOperList);
            svcOperList.add(svcOperData);
        }
        return svcOperList;
    }
}
