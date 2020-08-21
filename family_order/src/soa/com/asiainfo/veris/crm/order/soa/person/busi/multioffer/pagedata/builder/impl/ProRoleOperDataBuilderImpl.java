/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.impl;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.OfferQry;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.interfaces.IPageDataBuilder;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.ProductOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.RoleOperData;

public class ProRoleOperDataBuilderImpl implements IPageDataBuilder
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-9 上午10:33:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
     */
    public ProductOperData buildPageOperData(IData param) throws Exception
    {
        ProductOperData productOperData = new ProductOperData();
        String productId = param.getString("PRODUCT_ID");
        IData product = UProductInfoQry.qryProductByPK(productId); // 查询产品信息
        productOperData.setProductId(product.getString("PRODUCT_ID"));
        productOperData.setProductName(product.getString("PRODUCT_NAME"));
        List<RoleOperData> roleOperList = buildRoleOperData(productOperData, param);
        productOperData.setRoleOperList(roleOperList);
        return productOperData;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-9 上午10:33:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
     */
    public List<RoleOperData> buildRoleOperData(ProductOperData productOperData, IData param) throws Exception
    {
        List<RoleOperData> roleOperList = new ArrayList<RoleOperData>();
        String productId = param.getString("PRODUCT_ID");
        IDataset proRoleList = OfferQry.getProductRoleList(productId);
        for (int i = 0; i < proRoleList.size(); i++)
        {
            IData proRole = proRoleList.getData(i);
            RoleOperData roleOperData = new RoleOperData();
            roleOperData.setRoleId(proRole.getString("ROLE_CODE"));
            roleOperData.setRoleName(proRole.getString("ROLE_NAME"));
            roleOperData.setNetTypeCode(proRole.getString("NET_TYPE_CODE"));
            roleOperData.setIsGroupRole(proRole.getString("IS_GROUP_ROLE"));
            roleOperData.setIsMainRole(proRole.getString("IS_MAIN_ROLE"));
            roleOperData.setMinNumber(proRole.getString("MIN_NUMBER"));
            roleOperData.setMaxNumber(proRole.getString("MAX_NUMBER"));
            roleOperData.setParentPageData(productOperData);
            roleOperList.add(roleOperData);
        }
        return roleOperList;
    }

}
