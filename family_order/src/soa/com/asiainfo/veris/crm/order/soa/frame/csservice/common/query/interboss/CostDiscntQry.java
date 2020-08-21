
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CostDiscntQry
{

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTO(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOChange(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_CH", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOChangeDis(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_CH_DIS", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOChangeRej(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_CH_REJ", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOCommonParam(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCodeParser("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_COMMON", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOCommonParamDis(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCodeParser("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_COMMON_DIS", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOCommonParamRej(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCodeParser("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_COMMON_REJ", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTODesFail(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_DES_FAIL", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数---查询注销时客户经理界面使用的参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTODestroy(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_CUST_DES", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTONew(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_NEW", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOOld(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_OLD1", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOOldForCrtUs(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_OLDCRT", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOOpDis(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_DIS", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数---查询注销时产品经理界面使用的参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDToProDestroy(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_DES", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOReject(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_OBJ_REJECT", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTOTrade(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_OLD_BY_TRADE", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author zouli
     */
    public static IDataset getelementItemaByPk(IData param) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品ID 元素类型 参数显示类型 查询itema表元素配置情况 入参 ELEMENT_TYPE_CODE 元素类型 ATTR_TYPE_CODE 元素显示类型 PRODUCT_ID 产品ID
     * ProductDom::ATTR_ITEMA::TD_B_ATTR_ITEMA::SEL_SEVITEM_BY_PID_IDTYEP
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author zouli
     */
    public static IDataset getelementItemaByProductId(IData param) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_SEVITEM_BY_PID_IDTYEP", param, Route.CONN_CRM_CEN);
    }

    /*
     * 根据serv_code 查询服务参数
     */
    public static IDataset getElementParamAndOptions(IData data) throws Exception
    {

        if (ProvinceUtil.isProvince(ProvinceUtil.SHXI) || ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            data.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        }
        else if (ProvinceUtil.isProvince(ProvinceUtil.XINJ))
        {
            data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        }
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_ELEID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID查询控制参数类
     * 
     * @param param
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getFieldCodeById(IData param) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMB", "SEL_FIELD_CODE_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品ID查询产品名称
     */
    public static IDataset getGrpCtrlDataFromItemb(IData param) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMB", "SEL_GRP_CTRLDATA_BY_ID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据RATEPLANID，查ICB参数
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getIcbsByRatePlan(IData param) throws Exception
    {

        return Dao.qryByCode("TD_F_PORATEPLANICB", "SEL_BY_RATEPLANID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据BIZ_CODE查询业务代码是否存在
     * 
     * @param bizCode
     * @return
     * @throws Exception
     * @author chenl
     * @date 2009-10-8
     */
    public static IData getItemaByBizCode(String bizCode) throws Exception
    {

        IData param = new DataMap();
        param.put("BIZ_CODE", bizCode);
        IDataset result = Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_BIZ_CODE", param, Route.CONN_CRM_CEN);
        return (result != null && result.size() > 0) ? result.getData(0) : new DataMap();
    }

    /**
     * 查询itema表信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getItemAByPk(IData param) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_PK", param, Route.CONN_CRM_CEN);

        return dataset;
    }

    /**
     * 根据产品ID 元素类型 参数显示类型 查询itemb表元素配置情况
     * 
     * @author luojh 2009-08-08 11:28
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     */
    public static IDataset getItembByIdAndType(IData param) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMB", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryElementAttributes(String type, String id) throws Exception
    {

        IData param = new DataMap();
        param.put("ELEMENT_TYPE_CODE", type);
        param.put("ELEMENT_ID", id);
        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        return Dao.qryByCode("TD_B_ATTR_ITEMA", "ELEMENT_ATTR_SEL", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据服务ID查服务参数A,这个方法的作用是为了解决SI_BASE_IN_CODE 是原始的接入码 而不是在地市基础上扩展两位后的编码。原先的处理办法是截掉后两位，考虑到以后业务发展，扩展规则可能
     * 会变，就没有办法支持了。采用新增配置的方法，就没有问题。
     */
    public static String queryServiceItemA(String serviceId) throws Exception
    {

        IData param = new DataMap();
        param.put("ID_TYPE", "S");
        param.put("ID", serviceId);
        param.put("ATTR_CODE", "SI_BASE_IN_CODE");
        param.put("EPARCHY_CODE", "ZZZZ");
        IDataset idset = Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_ITEMAINFO_BY_ID", param, Route.CONN_CRM_CEN);
        return (idset != null && idset.size() > 0) ? idset.getData(0).getString("ATTR_INIT_VALUE", "") : "000000";
    }

    /**
     * 海南取权限控制优惠
     * 
     * @author liuzhengzheng
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getAccessCntrlDiscnt(IData data) throws Exception
    {

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_DISTN_BY_TYPE_CODE", data, Route.CONN_CRM_CEN);
    }

    public IDataset qryActParam(IData indata) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR", indata);
    }

    public IDataset qryArea(String area) throws Exception
    {
        IData comData = new DataMap();
        comData.put("PARA_CODE1", area);
        // CSAppEntity dao = new CSAppEntity(pd, BaseFactory.CENTER_CONNECTION_NAME);
        // return dao.queryListByCodeCodeParser("TD_S_COMMPARA", "SEL_CITY", comData);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CITY", comData);
    }

    // F:\crm\j2ee_hnan\saleserv\src\com\linkage\saleserv\bean\HNAN\person\sundryquery\other\QueryCostDiscountBean.java
    // queryIntegrateCustContact
    public IDataset qryContDiscntInfo(IData inparams, Pagination pagination) throws Exception
    {

        IData indata = new DataMap();
        String endDate = inparams.getString("END_DATE", "");
        if (endDate == null || endDate.equals(""))
            ;
        else
        {
            endDate = endDate + SysDateMgr.END_DATE;
        }
        indata.put("TRADE_TYPE_CODE", "418");
        indata.put("START_DATE", inparams.getString("START_DATE", ""));
        indata.put("END_DATE", endDate);
        indata.put("PARA_CODE1", inparams.getString("QUERY_TYPE", "")); // 可选的活动
        indata.put("PARAM_ATTR", "1268");
        indata.put("TRADE_EPARCHY_CODE", inparams.getString("cond_EPARCHY_CODE"));// inparams.getString("PROSECUTION_WAY1")
        // );
        indata.put("TRADE_CITY_CODE", inparams.getString("CITY_CODE"));

        IDataset datas = new DatasetList();
        datas = Dao.qryByCode("TF_BH_TRADE", "SEL_BY_GIVEACT", indata, pagination);
        // for(int i=1;i<50;i++) //today test
        // {
        // IData data = new DataMap();
        // data.put("TRADE_ID", "TRADE_ID");
        // data.put("CUST_NAME", "CUST_NAME");
        // data.put("SERIAL_NUMBER", "SERIAL_NUMBER");
        // data.put("RSRV_STR1", "RSRV_STR1");
        // data.put("RSRV_STR4", "RSRV_STR4");
        // data.put("RSRV_STR5", "RSRV_STR5");
        // data.put("RSRV_STR2", "RSRV_STR2");
        // data.put("RSRV_STR3", "RSRV_STR3");
        // data.put("ACCEPT_DATE", "ACCEPT_DATE");
        // data.put("FINISH_DATE", "FINISH_DATE");
        // data.put("EPARCHY_CODE", "EPARCHY_CODE");
        // data.put("TRADE_CITY_CODE", "TRADE_CITY_CODE");
        // data.put("TRADE_CITY_CODE", "TRADE_CITY_CODE");
        // data.put("TRADE_DEPART_ID", "TRADE_DEPART_ID");
        // datas.add(data);
        /*
         * SEL_BY_GIVEACT ::::====== SELECT to_char(a.trade_id) trade_id,to_char(a.bpm_id)
         * bpm_id,a.trade_type_code,a.in_mode_code
         * ,a.priority,a.subscribe_state,a.next_deal_tag,a.product_id,a.brand_code,to_char(a.user_id)
         * user_id,to_char(a.cust_id) cust_id,to_char(a.acct_id)
         * acct_id,a.serial_number,a.cust_name,to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss')
         * accept_date,a.accept_month,
         * a.trade_staff_id,a.trade_depart_id,a.trade_city_code,a.trade_eparchy_code,a.term_ip
         * ,a.eparchy_code,a.city_code,a.olcom_tag,to_char(a.exec_time,'yyyy-mm-dd hh24:mi:ss')
         * exec_time,to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,to_char(a.oper_fee)
         * oper_fee,to_char(a.foregift) foregift,to_char(a.advance_pay)
         * advance_pay,a.invoice_no,a.fee_state,to_char(a.fee_time,'yyyy-mm-dd hh24:mi:ss')
         * fee_time,a.fee_staff_id,a.cancel_tag,to_char(a.cancel_date,'yyyy-mm-dd hh24:mi:ss')
         * cancel_date,a.cancel_staff_id,a.cancel_depart_id,a.cancel_city_code,a.cancel_eparchy_code,a.process_tag_set,
         * c.para_code2 rsrv_str1,b.device_no_s rsrv_str2, b.device_no_e rsrv_str3, to_char(b.device_price/100)
         * rsrv_str4,to_char(b.device_num) rsrv_str5, a.remark FROM tf_bh_trade a ,tf_b_tradefee_device b ,td_s_commpara
         * c WHERE trade_type_code=:VTRADE_TYPE_CODE AND a.accept_date>=TO_DATE(:VSTART_DATE, 'YYYY-MM-DD HH24:MI:SS')
         * AND a.accept_date<=TO_DATE(:VEND_DATE, 'YYYY-MM-DD HH24:MI:SS') AND a.trade_id=b.trade_id AND
         * a.acct_id=TO_NUMBER(:VPARA_CODE1) AND c.para_code1=:VPARA_CODE1 AND c.param_attr=:VPARAM_ATTR AND (
         * c.eparchy_code=:VTRADE_EPARCHY_CODE OR c.eparchy_code='ZZZZ') AND ( a.trade_eparchy_code=:VTRADE_EPARCHY_CODE
         * OR :VTRADE_EPARCHY_CODE IS NULL ) AND ( a.trade_city_code=:VTRADE_CITY_CODE OR :VTRADE_CITY_CODE IS NULL )
         * order by accept_date asc
         */
        /*
         * to SELECT to_char(a.trade_id) trade_id, a.serial_number,a.cust_name,to_char(a.accept_date,'yyyy-mm-dd
         * hh24:mi:ss') accept_date,
         * a.trade_depart_id,a.trade_city_code,a.trade_eparchy_code,to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss')
         * finish_date, a.remark ,'RSRV_STR1' RSRV_STR1,'RSRV_STR2' RSRV_STR2,'RSRV_STR3' RSRV_STR3,'RSRV_STR4'
         * RSRV_STR4,'RSRV_STR5' RSRV_STR5 FROM tf_bh_trade a
         */
        // }
        return datas;
    }
}
