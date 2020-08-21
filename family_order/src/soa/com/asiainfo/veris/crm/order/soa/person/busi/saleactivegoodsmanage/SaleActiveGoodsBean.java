
package com.asiainfo.veris.crm.order.soa.person.busi.saleactivegoodsmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao; 

public class SaleActiveGoodsBean extends CSBizService
{

	/**
     * 查询营销活动
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150512  
     */
    public static IDataset getProductsLists(IData inparams) throws Exception
    {
//
//        SQLParser parser = new SQLParser(inparams);  
//        parser.addSQL(" select D.PRODUCT_ID, D.PRODUCT_NAME, A.LABEL_ID ,D.PRODUCT_ID||'|'||D.PRODUCT_NAME PRODUCT_ID_NAME");
//        parser.addSQL(" from TD_B_PRODUCT D, TD_B_ELEMENT_LABEL A ");
//        parser.addSQL(" where A.LABEL_ID in ('YX02','YX04','YX07') ");
//        parser.addSQL(" and A.ELEMENT_TYPE_CODE = 'P' ");
//        parser.addSQL(" and A.STATE = '1' ");
//        parser.addSQL(" and (A.EPARCHY_CODE = 'ZZZZ' or A.EPARCHY_CODE = '0898') ");
//        parser.addSQL(" and D.PRODUCT_ID = A.ELEMENT_ID ");
//        parser.addSQL(" and D.PRODUCT_MODE = '02' ");
//        parser.addSQL(" and D.RELEASE_TAG = '1' ");
//        parser.addSQL(" and (D.RSRV_STR5 <> 'BOOK2VALID' or D.RSRV_STR5 is null) ");
//        parser.addSQL(" and sysdate between D.START_DATE and D.END_DATE ");
//        parser.addSQL(" and not exists (select 1 ");
//        parser.addSQL(" from td_s_commpara b ");
//        parser.addSQL(" where b.param_attr = '522' ");
//        parser.addSQL(" and a.element_id = b.param_code) ");
//        parser.addSQL(" order by D.PRODUCT_ID "); 
//        
        IData param = new DataMap();
        if("".equals(inparams.getString("EPARCHY_CODE",""))){
        	param.put("EPARCHY_CODE", "0898");
        }

        return Dao.qryByCode("TD_B_SALE_GOODS_EXT", "SEL_PRODUCT_FOR_GOODS", param, Route.CONN_CRM_CEN);
    }
    
    /**
     * 查询营销包
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150512  
     */
    public static IDataset getPackagesLists(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);  
        parser.addSQL(" select T1.PACKAGE_ID||'|'||T1.PACKAGE_NAME PACKAGE_ID_NAME,t1.* from td_b_product_package t,td_b_package t1 "); 
        parser.addSQL(" where 1=1  ");
        parser.addSQL(" and t.package_id = t1.package_id  ");
        parser.addSQL(" and t.product_id=:PRODUCT_ID ");
        
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 查询礼品
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150512  
     */
    public static IDataset getGoodsLists(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);
        String VILID_TAG=inparams.getString("VILID_TAG","");
        String vilid="";
        if(!"".equals(VILID_TAG)&&"0".equals(VILID_TAG)){
        	vilid=" not between ";
        }else{
        	vilid=" between ";
        }
        
        parser.addSQL(" select T.GOODS_ID||'|'||T.GOODS_NAME GOODS_ID_NAME,T.* ");
        parser.addSQL(" from TD_B_SALE_GOODS_EXT T ");
        parser.addSQL(" where 1=1 ");  
        parser.addSQL(" and sysdate "+vilid+" t.start_date and t.end_date ");
        parser.addSQL(" and T.product_id = :PRODUCT_ID "); 
        parser.addSQL(" and T.package_id = :PACKAGE_ID ");
        parser.addSQL(" and T.goods_id = :GOODS_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    
    public static IDataset getPackageGoods(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);  
        parser.addSQL(" select t.*  ");
        parser.addSQL(" from TD_B_SALE_GOODS t ");
        parser.addSQL(" where T.RES_TYPE_CODE = 'D' ");
        parser.addSQL("  AND SYSDATE <T.END_DATE ");
        parser.addSQL(" AND SYSDATE >=T.START_DATE ");
        parser.addSQL("  AND T.GOODS_ID IN (SELECT A.element_id ");
        parser.addSQL(" FROM TD_B_PACKAGE_ELEMENT A ");
        parser.addSQL(" WHERE A.PACKAGE_ID = :PACKAGE_ID) ");
        
        
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    public static IDataset checkIfExistTab(IData inparams) throws Exception
    {
    	String editData=inparams.getString("EDITDATA","");
        SQLParser parser = new SQLParser(inparams);  
        parser.addSQL(" select case when count(1)=0 then '0' else '1' end IF_EXIST ");
        parser.addSQL(" from TD_B_SALE_GOODS_EXT T ");
        parser.addSQL(" where SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
        parser.addSQL(" and t.PRODUCT_ID = :SALE_PRODUCT ");
        parser.addSQL(" and T.PACKAGE_ID = :SALE_PACKAGE ");
        parser.addSQL(" and T.GOODS_ID =:GOODS_ID ");
        if("1".equals(editData)){
        	parser.addSQL(" and T.RES_ID =:RES_ID ");
        } 
        parser.addSQL(" and T.CITY_CODE =:CITY_CODE ");
        
        
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 删除项目
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATED by chenxy3@2015-2-11
     */
    public int deleteSaleGoodsExt(IData inparams) throws Exception
    {

        return Dao.executeUpdateByCodeCode("TD_B_SALE_GOODS_EXT", "DEL_SALE_GOODS_EXT", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 增加项目
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATED by chenxy3@2015-2-11
     */
    public int insertSaleGoodsExt(IData inparams) throws Exception
    {

        return Dao.executeUpdateByCodeCode("TD_B_SALE_GOODS_EXT", "INS_SALE_GOODS_EXT", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 修改项目
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATED by chenxy3@2015-2-11
     */
    public int updateSaleGoodsExt(IData inparams) throws Exception
    {

        return Dao.executeUpdateByCodeCode("TD_B_SALE_GOODS_EXT", "UPD_SALE_GOODS_EXT", inparams, Route.CONN_CRM_CEN);
    }
}
