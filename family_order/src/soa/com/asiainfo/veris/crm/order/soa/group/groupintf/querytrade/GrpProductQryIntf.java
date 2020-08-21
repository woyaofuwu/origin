
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;

public class GrpProductQryIntf
{

    /**
     * 获取集团产品类型
     * 
     * @author liujy
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductType(IData data, Pagination pg) throws Exception
    {
        String parentTypeCode = IDataUtil.getMandaData(data, "PARENT_PTYPE_CODE", "1000");

        IDataset productTypeList = ProductTypeInfoQry.getProductsType(parentTypeCode, pg);

        return productTypeList;
    }

    /**
     * 根据PRODUCT_TYPE_CODE获取集团产品子类型
     * 
     * @author liujy
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductByType(IData data, Pagination pg) throws Exception
    {
        String productTypeCode = IDataUtil.getMandaData(data, "PRODUCT_TYPE_CODE");

        if ("-1".equals(productTypeCode))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_333);
        }

        IDataset productList = ProductInfoQry.getProductsByTypeForGroup(productTypeCode, pg);

        return productList;
    }

    /**
     * 根据PRODUCT_ID获取产品信息
     * 
     * @author liujy
     * @return
     * @throws Throwable
     */
    public static IDataset qryProductInfoByProductId(IData data, Pagination pg) throws Exception
    {
        String productId = IDataUtil.getMandaData(data, "PRODUCT_ID");

        IData productInfo = UProductInfoQry.qryProductByPK(productId);

        return IDataUtil.idToIds(productInfo);
    }

    /**
     * 根据产品ID获取产品类型
     * 
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductTypeByProductId(IData inParam, Pagination pg) throws Exception
    {
        String productId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");

        return ProductTypeInfoQry.qryProductTypeCodeByProductId(productId, pg);
    }

    /**
     * 集团产品成员优惠查询
     * 
     * @param product_id
     *            产品编码
     * @param TRADE_STAFF_ID
     *            受理员工
     */
    public static IDataset getProductMebDiscntByProductIdAndStaffId(IData data) throws Exception
    {
        String productId = IDataUtil.getMandaData(data, "PRODUCT_ID");
        String staffId = CSBizBean.getVisit().getStaffId();
        IDataset disDataset = DiscntInfoQry.getProductMebDiscntByProductId(productId);
        if (IDataUtil.isNotEmpty(disDataset))
        {
            if (!"SUPERUSR".equals(staffId))
            {
                IDataset resultDs = new DatasetList();

                // 过滤掉没有权限的数据
                for (int i = 0; i < disDataset.size(); i++)
                {
                    IData disData = disDataset.getData(i);
                    String discntCode = disData.getString("DISCNT_CODE");
                    boolean rigthts = StaffPrivUtil.isDistPriv(staffId, discntCode);
                    if (rigthts)// 有权限
                    {
                        resultDs.add(disData);
                    }
                }
                return resultDs;
            }

        }
        return disDataset;

    }

    /*
     * 得到 角色编码
     */
    public static IDataset getRoleCodeInfo(IData data) throws Exception
    {
        // 得到 relation_type_code
        String productId = IDataUtil.getMandaData(data, "PRODUCT_ID");
        String relaTypCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

        // 得到角色信息
        String typeId = "TD_S_RELATION_ROLE_1_" + relaTypCode;
        IDataset resultDs = new DatasetList();
        IDataset ids = StaticUtil.getStaticList(typeId);
        if (IDataUtil.isEmpty(ids))
        {
            // IData dmap = new DataMap();
            // dmap.put("X_RESULTCODE", "-1");
            // dmap.put("X_RESULTINFO", "该产品" + productId + "没有配置角色信息!");
            // resultDs.add(dmap);
            CSAppException.apperr(GrpException.CRM_GRP_713, "-1:该产品" + productId + "没有配置角色信息!");
        }

        for (int i = 0, cout = ids.size(); i < cout; i++)
        {
            IData tmp = ids.getData(i);
            IData dmap = new DataMap();
            dmap.put("DATA_ID", tmp.getString("DATA_ID"));
            dmap.put("DATA_NAME", tmp.getString("DATA_NAME"));
            resultDs.add(dmap);
        }

        return resultDs;
    }
}
