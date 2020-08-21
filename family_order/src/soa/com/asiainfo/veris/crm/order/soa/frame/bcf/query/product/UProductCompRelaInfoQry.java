
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UProductCompRelaInfoQry
{
    /**
     * 根据动力100主产品id查询必选子产品信息
     * 
     * @param product_id_a
     * @return
     * @throws Exception
     */
    public static IDataset getCompRelaInfoByPIDA(String product_id_a) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID_A", product_id_a);
        return Dao.qryByCode("TD_B_PRODUCT_COMP_RELA", "SEL_FORCE_BY_PIDA", param, Route.CONN_CRM_CEN);
    }

    /**
     * @Description:根据PRODUCT_ID_B、FORCE_TAG、RELATION_TYPE_CODE获取组合产品关系相关信息
     * @author wusf
     * @date 2010-1-19
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getCompRelaInfoByPIDB(String product_id_b, String force_tag, String relation_type_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID_B", product_id_b);
        data.put("FORCE_TAG", force_tag);
        data.put("RELATION_TYPE_CODE", relation_type_code);
        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_COMP_RELA", "SEL_FORCE_BYPIDB", data);
        return dataset;
    }

    /**
     * 查询组合产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getCompReleInfo(String product_id_a) throws Exception
    {
        IDataset result = UpcCall.queryGrpOffersByGrpOfferId(product_id_a); 
        
        IDataset mebSet = new DatasetList();
        if(IDataUtil.isEmpty(result))
            return mebSet;
        for(int i = 0, isize = result.size(); i < isize; i++)
        {
            IData relOffer = result.getData(i);
            IData product = new DataMap();
            product.put("PRODUCT_ID_A", product_id_a);
            product.put("PRODUCT_ID_B", relOffer.getString("OFFER_CODE"));
            product.put("PRODUCT_EXPLAIN", relOffer.getString("DESCRIPTION"));
            product.put("START_DATE", relOffer.getString("VALID_DATE"));
            product.put("END_DATE", relOffer.getString("EXPIRE_DATE"));
            product.put("UPDATE_TIME", relOffer.getString("DONE_DATE"));
            product.put("UPDATE_STAFF_ID", relOffer.getString("OP_ID"));
            product.put("UPDATE_DEPART_ID", relOffer.getString("ORG_ID"));
            product.put("REMARK", relOffer.getString("REMARK"));
            product.put("RELATION_TYPE_CODE", "11");
            product.put("PRODUCT_NAME", relOffer.getString("OFFER_NAME"));
            String selectTag = relOffer.getString("SELECT_FLAG","");
            if(selectTag.equals(UpcConst.SELECT_FLAG_MUST_CHOOSE))//必选
            {
            	product.put("FORCE_TAG", "1");
            	product.put("DEFAULT_TAG", "1");
            }
            if(selectTag.equals(UpcConst.SELECT_FLAG_CAN_CHOOSE_YES))//默认
            {
            	product.put("FORCE_TAG", "0");
            	product.put("DEFAULT_TAG", "1");
            }
            if(selectTag.equals(UpcConst.SELECT_FLAG_CAN_CHOOSE_NO))//可选
            {
            	product.put("FORCE_TAG", "0");
            	product.put("DEFAULT_TAG", "0");
            }
            
            mebSet.add(product);
        }
        return mebSet;
    }

    /**
     * 查询某BBOSS商品的子产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qrySubProductInfos(String product_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT t.* FROM td_b_product_comp_rela t ");
        parser.addSQL(" WHERE  t.product_id_a = :PRODUCT_ID AND t.relation_type_code = '1' "); // relation_type_code =
        // '1'包产品关系
        return Dao.qryByParse(parser);

    }

    /**
     * 查资费包信息
     * 
     * @author shixb
     * @version 创建时间：2009-5-12 下午10:10:25
     */
    public static IDataset queryProductComp(String product_id, String relation_type_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("RELATION_TYPE_CODE", relation_type_code);

        return Dao.qryByCode("TD_B_PRODUCT_COMP_RELA", "SEL_PRODUCT_COMP", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据product_id_b获得组合产品关系
     * 
     * @author shixb
     * @version 创建时间：2009-11-10 上午10:07:33
     */
    public static IDataset queryProductRealByProductB(String product_id_b) throws Exception
    {
        IDataset result = UpcCall.queryBBossGrpOfferByMebOfferId(product_id_b); 
        
        IDataset mebSet = new DatasetList();
        if(IDataUtil.isEmpty(result))
            return mebSet;
        for(int i = 0, isize = result.size(); i < isize; i++)
        {
            IData relOffer = result.getData(i);
            IData product = new DataMap();
            product.put("PRODUCT_ID_A", relOffer.getString("OFFER_CODE"));
            product.put("PRODUCT_ID_B", product_id_b);
            product.put("PRODUCT_EXPLAIN", relOffer.getString("DESCRIPTION"));
            product.put("START_DATE", relOffer.getString("VALID_DATE"));
            product.put("END_DATE", relOffer.getString("EXPIRE_DATE"));
            product.put("UPDATE_TIME", relOffer.getString("DONE_DATE"));
            product.put("UPDATE_STAFF_ID", relOffer.getString("OP_ID"));
            product.put("UPDATE_DEPART_ID", relOffer.getString("ORG_ID"));
            product.put("REMARK", relOffer.getString("REMARK"));
            product.put("RELATION_TYPE_CODE", "11");
            product.put("PRODUCT_NAME", relOffer.getString("OFFER_NAME"));
            String selectTag = relOffer.getString("SELECT_FLAG","");
            if(selectTag.equals(UpcConst.SELECT_FLAG_MUST_CHOOSE))//必选
            {
            	product.put("FORCE_TAG", "1");
            	product.put("DEFAULT_TAG", "1");
            }
            if(selectTag.equals(UpcConst.SELECT_FLAG_CAN_CHOOSE_YES))//默认
            {
            	product.put("FORCE_TAG", "0");
            	product.put("DEFAULT_TAG", "1");
            }
            if(selectTag.equals(UpcConst.SELECT_FLAG_CAN_CHOOSE_NO))//可选
            {
            	product.put("FORCE_TAG", "0");
            	product.put("DEFAULT_TAG", "0");
            }
            
            mebSet.add(product);
        }
        return mebSet;
    }
    
    
    /**
     * 根据product_id_b获得组合产品关系
     * 
     * @author shixb
     * @version 创建时间：2009-11-10 上午10:07:33
     */
    public static IDataset queryProductRealByProductBAndForceTag(String product_id_b,String forceTag) throws Exception
    {
        IDataset result = UpcCall.queryBBossGrpOfferByMebOfferId(product_id_b); 
        String select_Tag = UpcConst.getSelectFlagForDefaultTagAndForceTag("", forceTag);
        
        IDataset mebSet = new DatasetList();
        if(IDataUtil.isEmpty(result))
            return mebSet;
        for(int i = 0, isize = result.size(); i < isize; i++)
        {
            IData relOffer = result.getData(i);
            IData product = new DataMap();
            
            String selectTag = relOffer.getString("SELECT_FLAG","");
            if (!selectTag.equals(select_Tag)) 
            {
				continue;
			}

            product.put("PRODUCT_ID_A", relOffer.getString("OFFER_CODE"));
            product.put("PRODUCT_ID_B", product_id_b);
            product.put("PRODUCT_EXPLAIN", relOffer.getString("DESCRIPTION"));
            product.put("START_DATE", relOffer.getString("VALID_DATE"));
            product.put("END_DATE", relOffer.getString("EXPIRE_DATE"));
            product.put("UPDATE_TIME", relOffer.getString("DONE_DATE"));
            product.put("UPDATE_STAFF_ID", relOffer.getString("OP_ID"));
            product.put("UPDATE_DEPART_ID", relOffer.getString("ORG_ID"));
            product.put("REMARK", relOffer.getString("REMARK"));
            product.put("RELATION_TYPE_CODE", "11");
            product.put("PRODUCT_NAME", relOffer.getString("OFFER_NAME"));
            
            if(selectTag.equals(UpcConst.SELECT_FLAG_MUST_CHOOSE))//必选
            {
            	product.put("FORCE_TAG", "1");
            	product.put("DEFAULT_TAG", "1");
            }
            if(selectTag.equals(UpcConst.SELECT_FLAG_CAN_CHOOSE_YES))//默认
            {
            	product.put("FORCE_TAG", "0");
            	product.put("DEFAULT_TAG", "1");
            }
            if(selectTag.equals(UpcConst.SELECT_FLAG_CAN_CHOOSE_NO))//可选
            {
            	product.put("FORCE_TAG", "0");
            	product.put("DEFAULT_TAG", "0");
            }
            mebSet.add(product);
        }
        return mebSet;
    }
}
