
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class NoteItemInfoQry
{
    /**
     * 高级付费关系变更，综合帐目列表过滤
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj-j2ee
     */
    public static IDataset filterNoteItems(String noteItem) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", noteItem);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.NOTE_ITEM like '%' || :NOTE_ITEM || '%'");
        parser.addSQL(" and note.PRINT_LEVEL='0'"); // 一级综合帐目
        if (ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000000'");
        }
        else
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000001'");
        }
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    //
    // /**排重
    // * @description
    // * @author xiaozp
    // * @date Sep 30, 2009
    // * @version 1.0.0
    // * @param str
    // * @return
    // */
    // public static String checkDuplicate(String str){
    // HashSet hs=new HashSet();
    // String outStr="";
    // if(str!=null&&!"".equals(str)){
    // String[] strArr1= str.split("\\|");
    // for(int i=0;i<strArr1.length;i++)
    // hs.add(strArr1[i]);
    // }
    //
    // for (Iterator iterator = hs.iterator(); iterator.hasNext();) {
    // if("".equals(outStr))
    // outStr = iterator.next().toString();//遍历set,放入新数组
    // else
    // outStr=outStr+"|"+iterator.next().toString();
    //
    // }
    // return outStr;
    // }
    //
    /**
     * 高级付费关系变更，综合帐目列表过滤,用于集团账目高级付费
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset filterNoteItemsByGrp(String noteItem) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", noteItem);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.NOTE_ITEM like '%' || :NOTE_ITEM || '%'");
        parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级综合帐目
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000000'");
        }
        else
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000023'");
        }
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，集团高级付费时用来过滤的
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset filterNoteItemsByGrp(String noteItemCodes, Pagination pagination) throws Exception
    {
        IData data = new DataMap();

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级综合帐目
        parser.addSQL(" and note.NOTE_ITEM_CODE in ( " + noteItemCodes + " )");
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000000'");
        }
        else
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000023'");
        }
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，综合帐目列表过滤,用于集团账目高级付费
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset filterNoteItemsByGrpForHNAN(String noteItem) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", noteItem);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.NOTE_ITEM like '%' || :NOTE_ITEM || '%'");
        parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级综合帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000017'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，集团高级付费时用来过滤的
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset filterNoteItemsByGrpForHNAN(String noteItemCodes, Pagination pagination) throws Exception
    {
        IData data = new DataMap();

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级综合帐目
        parser.addSQL(" and note.NOTE_ITEM_CODE in ( " + noteItemCodes + " )");
        parser.addSQL(" and note.TEMPLET_CODE='50000017'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，综合帐目列表过滤，用于集团账目高级付费
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset filterNoteItemsByGrpForHNAN_1(String noteItem) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", noteItem);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.NOTE_ITEM like '%' || :NOTE_ITEM || '%'");
        parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级综合帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000017'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，综合帐目列表过滤，用于集团账目高级付费
     * 
     * @return
     * @throws Exception
     * @author liaoyi
     */
    public static IDataset filterNoteItemsByGrpForHNANNew(String noteItem) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", noteItem);
        // data.put("EXCLUDE_COMP_ITEMS", excludeCompItems);
        // data.put("EPARCHY_CODE", AppUtil.getRouteEparchy());
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT a.item_id NOTE_ITEM_CODE, a.item_name NOTE_ITEM");
        parser.addSQL(" FROM td_b_item a "); // 付费账目查询
        parser.addSQL(" WHERE a.item_type='0' AND a.item_use_type='1'");
        parser.addSQL(" AND a.item_name like '%' || :NOTE_ITEM || '%'");
        parser.addSQL(" ORDER BY a.item_id");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，综合帐目列表过滤 支持分页
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset filterNoteItemsByItemcodes(String noteItemCodes, Pagination pagination) throws Exception
    {
        IData data = new DataMap();

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.PRINT_LEVEL='0'"); // 一级综合帐目
        parser.addSQL(" and note.NOTE_ITEM_CODE in ( " + noteItemCodes + " )");
        if (ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000000'");
        }
        else
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000001'");
        }
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，综合帐目列表过滤 支持分页
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset filterNoteItemsByPagination(String noteItem, String noteItemCode, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", noteItem);
        data.put("NOTE_ITEM_CODE", noteItemCode);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.NOTE_ITEM like '%' || :NOTE_ITEM || '%'");
        parser.addSQL(" and note.PRINT_LEVEL='0'"); // 一级综合帐目
        parser.addSQL(" and note.NOTE_ITEM_CODE like '%' || :NOTE_ITEM_CODE || '%'"); // 一级综合帐目
        if (ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000000'");
        }
        else
        {
            parser.addSQL(" and note.TEMPLET_CODE='50000001'");
        }
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，综合帐目列表过滤
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset filterNoteItemsForHNan(String noteItem) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", noteItem);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.* from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.NOTE_ITEM like '%' || :NOTE_ITEM || '%'");
        parser.addSQL(" and note.PRINT_LEVEL='2'"); // 一级综合帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000017'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser);
    }

    /**
     * 查找账目项获得账目名称
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xj
     */
    public static IDataset findItems(String itemsstr) throws Exception
    {
        IData data = new DataMap();

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.NOTE_ITEM_CODE IN(" + itemsstr + ")");
        parser.addSQL(" and note.PRINT_LEVEL='1'"); // 一级综合帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000000'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 通过综合账目编码获取其下的所有明细编码
     * 
     * @param item
     * @return
     * @throws Exception
     */
    public static IDataset getDetailItemForGrp(String item, Pagination pagination) throws Exception
    {

        IData map = new DataMap();
        map.put("PARENT_ITEM_CODE", item);
        return Dao.qryByCode("TD_B_NOTEITEM", "SEL_GRP_DETAIL_ITEM", map, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，综合帐目列表过滤，用于集团账目高级付费
     * 
     * @return
     * @throws Exception
     * @author liaoyi
     */
    public static IDataset getNoteItemsByGrpForHNAN(String itemID) throws Exception
    {
        IData data = new DataMap();
        data.put("ITEM_ID", itemID);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT a.item_id NOTE_ITEM_CODE, a.item_name NOTE_ITEM");
        parser.addSQL(" FROM td_b_item a "); // 付费账目查询
        parser.addSQL(" WHERE a.item_type='0' AND a.item_use_type='1'");
        parser.addSQL(" AND a.item_id = :ITEM_ID ");
        parser.addSQL(" ORDER BY a.item_id");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 查询集团高级付费关系账目信息
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPayRelByProdID(String productId) throws Exception
    {

        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_PAY_REL_BY_PRODID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据USER_ID、PLAN_ID查询用户付费计划帐目信息
     * 
     * @param inparams
     *            查询参数(USER_ID,PLAN_ID,ACT_TAG)
     * @param page
     *            分页参数
     * @return 用户付费计划帐目信息
     * @throws Exception
     * @author xiajj
     */
    public static IDataset getUserDetailPayItemByPlayId(String USER_ID, String PLAN_ID, String ACT_TAG, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", USER_ID);
        param.put("PLAN_ID", PLAN_ID);
        param.put("RSRV_STR1", ACT_TAG);
        return Dao.qryByCode("TD_B_NOTEITEM", "SEL_BY_GRP_USER_PLANITEM", param, page, Route.CONN_CRM_CEN);
    }

    /**
     * 获取PAY_ITEMCODE（新）
     * 
     * @param productId
     */
    public static String payItemCode(String productId) throws Exception
    {

        IData data = new DataMap();
        String payitem = "";
        SQLParser parser = new SQLParser(data);
        parser.addSQL("select t.para_code1 from td_s_commpara t where 1 = 1");
        parser.addSQL(" and t.param_code='" + productId + "'");
        parser.addSQL(" and t.param_attr='2'"); //
        IDataset dataset = new DatasetList();
        dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        if (dataset != null && dataset.size() > 0)
        {
            payitem = dataset.getData(0).getString("PARA_CODE1");
        }
        else
        {
            payitem = "";
        }
        return payitem;
    }

    /**
     * 查询付费帐目明细
     * 
     * @param inParams
     * @return 付费帐目明细列表
     * @throws Exception
     * @author deagle-j2ee
     */
    public static IDataset queryDetItemByPageForGrp(IData inParams, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(inParams);
        parser.addSQL("SELECT a.item_id , a.item_name, b.sub_item_id NOTE_ITEM_CODE,c.item_name NOTE_ITEM FROM td_b_item a , td_b_compitem b ,td_b_item c");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.item_id=:PARENT_NOTE_ITEM_CODE ");
        parser.addSQL(" and a.item_type='0' AND a.item_use_type='1' and b.sub_item_id=c.item_id AND a.item_id=b.item_id(+)"); // 二级明细帐目
        parser.addSQL(" ORDER BY a.item_id , b.sub_item_id");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，获取一级综合帐目列表
     * 
     * @return
     * @throws Exception
     * @author xj-j2ee
     */
    public static IDataset queryNoteItems() throws Exception
    {

        IData data = new DataMap();
        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.NOTE_ITEM_CODE, note.NOTE_ITEM from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.PRINT_LEVEL='0'"); // 一级综合帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000001'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 高级付费关系变更，获取二级综合帐目列表
     * 
     * @return
     * @throws Exception
     * @author xj-j2ee
     */
    public static IDataset queryNoteItems2(String temp) throws Exception
    {

        IData data = new DataMap();
        SQLParser parser = new SQLParser(data);
        if (ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            parser.addSQL("select n.* from td_b_noteitem note,td_b_noteitem n where 1 = 1");
            parser.addSQL(" and note.PARENT_ITEM_CODE in (" + temp + ")");
            parser.addSQL(" and note.templet_code=n.TEMPLET_CODE ");
            parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级明细帐目
            parser.addSQL(" and note.note_item_code = n.parent_item_code "); // 二级明细帐目
            parser.addSQL(" and note.TEMPLET_CODE='50000000'");
            parser.addSQL(" order by n.NOTE_ITEM_CODE");
        }
        else
        {
            parser.addSQL("select note.* from td_b_noteitem note where 1 = 1");
            parser.addSQL(" and note.PARENT_ITEM_CODE in (" + temp + ")");
            parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级明细帐目
            parser.addSQL(" and note.TEMPLET_CODE='50000001'");
            parser.addSQL(" order by note.NOTE_ITEM_CODE");
        }
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * ng3添加 高级付费关系变更，获取综合帐目列表 j2ee
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryNoteItemsall() throws Exception
    {
        IData data = new DataMap();

        SQLParser parser = new SQLParser(data);

        parser.addSQL("select note.* from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.PRINT_LEVEL='2'"); // 一级综合帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000017'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryNoteItemsBy60000017(String secondItem) throws Exception
    {

        IData data = new DataMap();
        data.put("NOTE_ITEM_CODE", secondItem);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select distinct(parent_item_code) from td_b_noteitem a where 1 = 1");
        parser.addSQL(" and a.note_item_code in (:NOTE_ITEM_CODE)");
        parser.addSQL(" and a.PRINT_LEVEL='2'"); // 二级明细帐目
        parser.addSQL(" and a.TEMPLET_CODE='60000017'");
        parser.addSQL(" order by a.parent_item_code ASC");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

}
