
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompInfoQry;

public class ProductCompInfoQry extends CSBizBean
{
    /**
     * @Description:判断集团产品关系关系类型是否符合
     * @author wusf
     * @date 2009-8-10
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset checkTradeTypeCodeInfo(String productId, String relation_type_code, Pagination pagination) throws Exception
    {
        //该方法改造的可能会有漏洞，原有的sql中支持PRODUCT_ID，RELATION_TYPE_CODE为空，如果PRODUCT_ID为空的话，新逻辑就有问题了
        //整个工程搜索后，并未发现有调用这个方法的地方，所以暂时先认为两个参数中的PRODUCT_ID一定是不为空的
        
        String relationTypeCode = UProductCompInfoQry.getRelationTypeCodeByProductId(productId);
        IDataset result = new DatasetList();
        if(StringUtils.isEmpty(relation_type_code) || relationTypeCode.equals(relation_type_code))
        {
            IData comp = new DataMap();
            comp.put("PRODUCT_ID", productId);
            comp.put("RELATION_TYPE_CODE", relationTypeCode);
            result.add(comp);
        }
        
        return result;
        
//        IData param = new DataMap();
//        param.put("PRODUCT_ID", productId);
//        param.put("RELATION_TYPE_CODE", relation_type_code);
//
//        SQLParser parser = new SQLParser(param);
//
//        parser.addSQL("select t.product_id,t.relation_type_code  ");
//        parser.addSQL("  from td_b_product_comp t ");
//        parser.addSQL(" where 1 = 1 ");
//        parser.addSQL("   and t.product_id = :PRODUCT_ID ");
//        parser.addSQL("   and t.relation_type_code = :RELATION_TYPE_CODE ");
//
//        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据产品ID查询组合产品信息
     * 
     * @param param查询参数
     * @return 组合产品信息
     * @throws Exception
     * @author xiajj
     */
    public static IDataset getCompProductInfoByID(String productId) throws Exception
    {
        IData compData = UProductCompInfoQry.getCompInfoByProductId(productId);
        IDataset result = new DatasetList();
        if(IDataUtil.isNotEmpty(compData))
        {
            result.add(compData);
        }
        
        return result;
    }

    /**
     * 根据product_id查询TD_B_PRODUCT_COMP中的产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getProductFromComp(String productId) throws Exception
    {
        IData compData = UProductCompInfoQry.getCompInfoByProductId(productId);
        if(IDataUtil.isNotEmpty(compData))
            return compData;
        else
            return null;
    }

    /**
     * 查询关系类型编码
     */
    public static String getRelaTypeCodeByProductId(String productId) throws Exception
    {
        return UProductCompInfoQry.getRelationTypeCodeByProductId(productId);
    }

    /**
     * 查询产品是否为定制产品
     */
    public static String getUseTagByProductId(String productId) throws Exception
    {
        return UProductCompInfoQry.getUseTagByProductId(productId);
    }

    /**
     * 判断产品是否集团可定制 查询TD_B_PRODUCT_COMP USE_TAG 是否集团定制：0：集团不可定制，1：集团可定制
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean ifGroupCustomize(String productId) throws Exception
    {
        String compTag = getUseTagByProductId(productId);
        if (StringUtils.isEmpty(compTag))
        {
            CSAppException.apperr(GrpException.CRM_GRP_176, productId);
            return false;
        }

        return "1".equals(compTag);
    }

    public static IData queryRelationTypeByProductId(String productId) throws Exception
    {
        IData compData = UProductCompInfoQry.getCompInfoByProductId(productId);
        if(IDataUtil.isNotEmpty(compData))
            return compData;
        else
            return null;
    }
}
