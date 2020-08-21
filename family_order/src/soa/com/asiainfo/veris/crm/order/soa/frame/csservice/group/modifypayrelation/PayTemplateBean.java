
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.NoteItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class PayTemplateBean extends CSBizBean
{

    /**
     * 查询是否支持高级付费标记
     * 
     * @param tradeData
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserother(IData data) throws Exception
    {
        String userid = data.getString("USER_ID");
        IData params = new DataMap();
        params.put("USER_ID", userid);
        params.put("RSRV_VALUE_CODE", "GRPI");
        params.put("RSRV_VALUE", "集团付费及其群号码");
        params.put("RSRV_STR1", "01");

        return UserOtherInfoQry.getUserOverProvinceInfoForCg(params);
    }

    /**
     * 查询是否支持高级付费标记
     * 
     * @param tradeData
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserothers(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser
                .addSQL("SELECT PARTITION_ID,USER_ID,RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6,RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14,RSRV_NUM15,RSRV_NUM16,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24,RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,RSRV_DATE6,RSRV_DATE7,RSRV_DATE8,RSRV_DATE9,RSRV_DATE10,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5,RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID,DEPART_ID,TRADE_ID,TO_CHAR(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,TO_CHAR(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,TO_CHAR(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,INST_ID FROM tf_f_user_other WHERE 1=1");
        parser.addSQL(" AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)");
        parser.addSQL(" AND user_id=TO_NUMBER(:USER_ID)");
        parser.addSQL(" AND rsrv_value_code=:RSRV_VALUE_CODE");
        parser.addSQL(" AND end_date > sysdate");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 查询费用限制
     */
    public static IDataset showFeeLimit(IData param) throws Exception
    {
        String productid = param.getString("PRODUCT_ID");
        String eparchy_code = param.getString("EPARCHY_CODE");

        return AttrItemInfoQry.qryItems(productid, "P", "GrpFeeLim", null, eparchy_code, null);
    }

    /**
     * 查询模板账目
     */
    public static IDataset showItems(IData data) throws Exception
    {
        String productid = data.getString("PRODUCT_ID");
        IData params = new DataMap();
        params.put("ID", productid);
        params.put("ID_TYPE", "P");
        params.put("ATTR_OBJ", "GrpPayList");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        return AttrItemInfoQry.qryItems(productid, "P", "GrpPayList", null, getVisit().getStaffEparchyCode(), null);

    }

    /**
     * 查询模板，支持区分地州
     */
    public static IDataset showTemplate(IData data) throws Exception
    {
        String productid = data.getString("PRODUCT_ID");
        IData params = new DataMap();
        params.put("ID", productid);
        params.put("ID_TYPE", "P");
        params.put("ATTR_OBJ", "GrpPayCst");
        params.put("ATTR_CODE", "GrpPayCst");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        return AttrItemInfoQry.qryTemplate(productid, "P", "GrpPayCst", "GrpPayCst", getVisit().getStaffEparchyCode(), null);
    }

    /**
     * 查询是否用户群录入
     */
    public IDataset CanInserUserName(String productid) throws Exception
    {

        IData params = new DataMap();
        params.put("ID", productid);
        params.put("ID_TYPE", "P");
        params.put("ATTR_OBJ", "InstUser");
        params.put("ATTR_CODE", "InstUser");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        return AttrItemInfoQry.qryTemplate(productid, "P", "InstUser", "InstUser", getVisit().getStaffEparchyCode(), null);

    }

    /**
     * 查询是否支持合户
     */
    public IDataset CanSameAcct(String productid) throws Exception
    {

        IData params = new DataMap();
        params.put("ID", productid);
        params.put("ID_TYPE", "P");
        params.put("ATTR_OBJ", "CanSameAct");
        params.put("ATTR_CODE", "CanSameAct");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        return AttrItemInfoQry.qryTemplate(productid, "P", "CanSameAct", "CanSameAct", getVisit().getStaffEparchyCode(), null);

    }

    /**
     * 新增模板账目
     */
    public void createItems(IData data, String[] items) throws Exception
    {

        String productid = data.getString("PRODUCT_ID");
        String templatetag = data.getString("TEMPLATE_TAG");
        String itemtag = data.getString("ITEM_TAG");

        if (itemtag.equals("0"))
        {
            return;
        }

        if (items == null || items.length < 1)
        {
            return;
        }

        String itemsstr = "";
        for (int i = 0; i < items.length; i++)
        {
            itemsstr += items[i] + "|";
        }
        if (itemsstr.length() > 0)
        {
            itemsstr = itemsstr.substring(0, itemsstr.length() - 1);
            itemsstr = "'" + itemsstr.replace("|", "','") + "'";
        }

        IDataset itemsfortable = NoteItemInfoQry.findItems(itemsstr);

        if (itemtag.equals("1"))
        {
            IDataset initems = new DatasetList();
            for (int i = 0; i < itemsfortable.size(); i++)
            {
                IData params = new DataMap();
                IData item = (IData) itemsfortable.get(i);
                params.put("ID", productid);
                params.put("ID_TYPE", "P");
                params.put("ATTR_OBJ", "GrpPayList");
                params.put("ATTR_CODE", item.getString("NOTE_ITEM_CODE"));
                params.put("ATTR_NAME", item.getString("NOTE_ITEM"));
                params.put("START_DATE", SysDateMgr.getSysTime());
                params.put("END_DATE", SysDateMgr.getTheLastTime());
                params.put("ATTR_VALUE", "0");
                params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

                initems.add(params);
            }
            Dao.insert("TD_B_ATTR_BIZ", initems);
        }
    }

    /**
     * 新增模板
     */
    public void createTemplates(IData data) throws Exception
    {

        String productid = data.getString("PRODUCT_ID");
        String templatetag = data.getString("TEMPLATE_TAG");
        String itemtag = data.getString("ITEM_TAG");

        if (templatetag.equals("0"))
        {
            return;
        }

        IData params = new DataMap();
        if (itemtag.equals("0") || itemtag.equals(""))
        {
            params.put("ATTR_VALUE", "*");
        }
        else
        {
            params.put("ATTR_VALUE", "GrpPayList");
        }
        params.put("ATTR_NAME", "集团产品付费定制模板");
        params.put("START_DATE", SysDateMgr.getSysTime());
        params.put("END_DATE", SysDateMgr.getTheLastTime());
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        params.put("ID", productid);
        params.put("ID_TYPE", "P");
        params.put("ATTR_OBJ", "GrpPayCst");
        params.put("ATTR_CODE", "GrpPayCst");

        Dao.insert("TD_B_ATTR_BIZ", params, Route.CONN_CRM_CEN);

    }

    /**
     * 删除模板账目
     */
    public void deleteItems(IDataset items) throws Exception
    {

        Dao.delete("TD_B_ATTR_BIZ", items, new String[]
        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_CODE", "ATTR_VALUE", "EPARCHY_CODE" }, Route.CONN_CRM_CEN);

    }

    /**
     * 删除模板
     */
    public void deleteTemplates(IDataset templates) throws Exception
    {

        Dao.delete("TD_B_ATTR_BIZ", templates, new String[]
        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_CODE", "ATTR_VALUE", "EPARCHY_CODE" }, Route.CONN_CRM_CEN);
    }

    /**
     * Adc/Mas业务查询(黑白名单查询) 初始化 产品类型,产品编码
     * 
     * @param tradeData
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset initialProductInfos(Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        return ProductInfoQry.getGrpProductList(null);
    }

    /**
     * 查询是否支持高级付费标记
     * 
     * @param tradeData
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserother(String userid) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userid);
        params.put("RSRV_VALUE_CODE", "GRPI");
        params.put("RSRV_VALUE", "集团付费及其群号码");
        params.put("RSRV_STR1", "01");

        return UserOtherInfoQry.getUserOverProvinceInfoForCg(params);
    }

    /**
     * 查询费用限制
     */
    public IDataset showFeeLimit(String productid, String eparchy_code) throws Exception
    {

        IData params = new DataMap();
        params.put("ID", productid);
        params.put("ID_TYPE", "P");
        params.put("ATTR_OBJ", "GrpFeeLim");
        params.put("EPARCHY_CODE", eparchy_code);

        return AttrItemInfoQry.qryItems(productid, "P", "GrpFeeLim", null, eparchy_code, null);

    }

    /**
     * 查询模板账目
     */
    public IDataset showItems(String productid) throws Exception
    {

        IData params = new DataMap();
        params.put("ID", productid);
        params.put("ID_TYPE", "P");
        params.put("ATTR_OBJ", "GrpPayList");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        return AttrItemInfoQry.qryItems(productid, "P", "GrpPayList", null, getVisit().getStaffEparchyCode(), null);

    }

    /**
     * 查询模板，支持区分地州
     */
    public IDataset showTemplate(String productid) throws Exception
    {

        IData params = new DataMap();
        params.put("ID", productid);
        params.put("ID_TYPE", "P");
        params.put("ATTR_OBJ", "GrpPayCst");
        params.put("ATTR_CODE", "GrpPayCst");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        return AttrItemInfoQry.qryTemplate(productid, "P", "GrpPayCst", "GrpPayCst", getVisit().getStaffEparchyCode(), null);

    }
}
