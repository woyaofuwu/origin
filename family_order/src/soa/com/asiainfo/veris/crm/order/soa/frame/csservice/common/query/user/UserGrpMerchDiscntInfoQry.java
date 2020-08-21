
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanQry;

public class UserGrpMerchDiscntInfoQry
{

    /**
     * 从集团库 关联merchpDis和rataplan查询用户订购产品优惠信息
     * 
     * @author ft
     * @param user_id
     * @param merch_spec_code
     * @param product_spec_code
     * @param product_discnt_code
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductDiscnt(String user_id, String merch_spec_code, String product_spec_code, String product_discnt_code, Pagination pagination) throws Exception
    {
        // 获取用户优惠信息
        IDataset merchpDisInfos = UserGrpMerchpDiscntInfoQry.qryMerchpDiscntByUseridMerchScPrdouctScProductDc(user_id, merch_spec_code, product_spec_code, product_discnt_code, pagination);
        // 获取集团rateplan信息
        IDataset rateParams = PoRatePlanQry.getRateplanBySpecDiscntCode(merch_spec_code, product_spec_code, product_discnt_code);
        IDataset merchpDisInfolist = new DatasetList();
        // 返回用户订购优惠信息
        //修改返回得资费列表--add by huangzl3
        if (IDataUtil.isNotEmpty(merchpDisInfos) && IDataUtil.isNotEmpty(rateParams))
        {
        	for(int i=0;i<merchpDisInfos.size();i++) {
        		IData merchpDisInfo = merchpDisInfos.getData(i);
                IData rateParam = rateParams.getData(0);
                merchpDisInfo.putAll(rateParam);
                merchpDisInfolist.add(merchpDisInfo);
        	}          
        }
        return merchpDisInfolist;

    }
    /**
     * 从集团库 集客大厅查询用户订购产品优惠信息 daidl
     * 
     * @author ft
     * @param user_id
     * @param merch_spec_code
     * @param product_spec_code
     * @param product_discnt_code
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getJKDTUserProductDiscnt(String user_id, String merch_spec_code, String product_spec_code, String ratetype,String product_discnt_code, Pagination pagination) throws Exception
    {
        // 获取用户优惠信息
        IDataset merchpDisInfos = UserGrpMerchpDiscntInfoQry.qryMerchpDiscntByUseridMerchScPrdouctScProductDc(user_id, merch_spec_code, product_spec_code, product_discnt_code, pagination);
        // 获取集团rateplan信息
        IDataset rateParams = PoRatePlanQry.getRateplanByNumberAndType(merch_spec_code, product_spec_code, product_discnt_code);

        // 返回用户订购优惠信息
        if (IDataUtil.isNotEmpty(merchpDisInfos) && IDataUtil.isNotEmpty(rateParams))
        {
            IData merchpDisInfo = merchpDisInfos.getData(0);
            IData rateParam = rateParams.getData(0);
            merchpDisInfo.putAll(rateParam);

            return IDataUtil.idToIds(merchpDisInfo);
        }
        else
        {

            return new DatasetList();
        }

    }

    /**
     * 根据userID、merchSpecCode、merchDiscntCode查询TF_F_USER_GRP_MERCH_DISCNT表信息 从集团库 查询用户订购商品优惠信息
     * 
     * @author ft
     * @param user_id
     * @param merch_spec_code
     * @param merch_discnt_code
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchDiscntByUserIdMerchScMerchDc(String user_id, String merch_spec_code, String merch_discnt_code, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);
        param.put("MERCH_DISCNT_CODE", merch_discnt_code);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT a.PARTITION_ID, ");
        sql.append("a.INST_ID, ");
        sql.append("to_char(a.USER_ID) USER_ID, ");
        sql.append("a.MERCH_SPEC_CODE, ");
        sql.append("a.MERCH_DISCNT_CODE, ");
        sql.append("to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("a.UPDATE_STAFF_ID, ");
        sql.append("a.UPDATE_DEPART_ID, ");
        sql.append("a.REMARK, ");
        sql.append("a.RSRV_NUM1, ");
        sql.append("a.RSRV_NUM2, ");
        sql.append("a.RSRV_NUM3, ");
        sql.append("to_char(a.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(a.RSRV_NUM5) RSRV_NUM5, ");
        sql.append("a.RSRV_STR1, ");
        sql.append("a.RSRV_STR2, ");
        sql.append("a.RSRV_STR3, ");
        sql.append("a.RSRV_STR4, ");
        sql.append("a.RSRV_STR5, ");
        sql.append("to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("a.RSRV_TAG1, ");
        sql.append("a.RSRV_TAG2, ");
        sql.append("a.RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_GRP_MERCH_DISCNT a ");
        sql.append("where 1 = 1 ");
        sql.append("and a.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        sql.append("and (a.MERCH_DISCNT_CODE = :MERCH_DISCNT_CODE or ");
        sql.append(":MERCH_DISCNT_CODE IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * todo code_code 表里没有SEL_BY_OFFERID 根据merch_offer_id查询商品订购关系信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchInfoByMerchOfferId(String merchOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT a.PARTITION_ID, ");
        sql.append("to_char(a.USER_ID) USER_ID, ");
        sql.append("a.MERCH_SPEC_CODE, ");
        sql.append("a.MERCH_DISCNT_CODE, ");
        sql.append("to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("a.UPDATE_STAFF_ID, ");
        sql.append("a.UPDATE_DEPART_ID, ");
        sql.append("a.REMARK, ");
        sql.append("a.RSRV_NUM1, ");
        sql.append("a.RSRV_NUM2, ");
        sql.append("a.RSRV_NUM3, ");
        sql.append("to_char(a.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(a.RSRV_NUM5) RSRV_NUM5, ");
        sql.append("a.RSRV_STR1, ");
        sql.append("a.RSRV_STR2, ");
        sql.append("a.RSRV_STR3, ");
        sql.append("a.RSRV_STR4, ");
        sql.append("a.RSRV_STR5, ");
        sql.append("to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("a.RSRV_TAG1, ");
        sql.append("a.RSRV_TAG2, ");
        sql.append("a.RSRV_TAG3, ");
        sql.append("t.ratetype, ");
        sql.append("t.rateplanid, ");
        sql.append("t.rateplandescription, ");
        sql.append("t.icb_no ");
        sql.append("FROM TF_F_USER_GRP_MERCH_DISCNT a, TD_F_PORATEPLAN t ");
        sql.append("where 1 = 1 ");
        sql.append("and a.MERCH_SPEC_CODE = t.pospecnumber ");
        sql.append("and a.merch_discnt_code = t.rateplanid ");
        sql.append("and a.USER_ID = :USER_ID ");
        sql.append("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        sql.append("and (a.MERCH_DISCNT_CODE = :MERCH_DISCNT_CODE or ");
        sql.append(":MERCH_DISCNT_CODE IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

}
