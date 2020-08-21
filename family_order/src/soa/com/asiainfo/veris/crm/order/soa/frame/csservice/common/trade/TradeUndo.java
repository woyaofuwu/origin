
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.cancelchangeproduct.CancelChangeProductUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: UndoTrade.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午07:11:00 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-8-1 liuke v1.0.0 修改原因
 */
public class TradeUndo
{

    private final static Logger logger = Logger.getLogger(TradeUndo.class);

    /**
     * 获取匹配的备份数据（根据key值，比较台账表和备份表的数据，找到当前修改台账对应的原始备份数据）
     * 
     * @param bakInfos
     * @param tradeInfo
     * @param keys
     * @return
     * @throws Exception
     */
    public static IData getBakTradeInfo(IDataset bakTradeInfos, IData tradeInfo, String[] keys) throws Exception
    {
        for (int i = 0; i < bakTradeInfos.size(); i++)
        {
            IData bakTradeinfo = bakTradeInfos.getData(i);
            int length = keys.length;// key的长度
            int cnt = 0;
            for (int k = 0; k < length; k++)
            {
                if (StringUtils.equals("PARTITION_ID", keys[k]))// 如果是分区字段，那就默认算过，trade表中没有该字段，无法比较
                {
                    cnt++;
                }
                else if (StringUtils.equals(tradeInfo.getString(keys[k]), bakTradeinfo.getString(keys[k])))
                {
                    cnt++;
                }
            }
            if (cnt != 0 && cnt == length)// 完全匹配上
            {
                return bakTradeinfo;
            }
        }
        return null;
    }

    private static String getChangeProductStandardUndoTable(String intfId) throws Exception
    {
        String[] tableNames = intfId.split(",");
        String standardTables = "";
        for (int i = 0, size = tableNames.length; i < size; i++)
        {
            String tableName = tableNames[i];
            if (StringUtils.isNotBlank(tableName))
            {
                if ("TF_B_TRADE_SVC".equals(tableName) || "TF_B_TRADE_DISCNT".equals(tableName) || "TF_B_TRADE_ATTR".equals(tableName) || "TF_B_TRADE_OFFER_REL".equals(tableName) || "TF_B_TRADE_PRICE_PLAN".equals(tableName))
                {
                    continue;
                }
                standardTables += tableName + ",";
            }
        }
        return standardTables;
    }

    /**
     * 组装错误信息
     * 
     * @param tradeInfo
     * @param keys
     * @return
     * @throws Exception
     */
    private static String getErrorInfos(IData tradeInfo, String[] keys) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        sb.append("根据[");
        for (int k = 0; k < keys.length; k++)
        {
            sb.append(keys[k]).append("=");
            sb.append(tradeInfo.getString(keys[k]));

            if (k < keys.length - 1)
            {
                sb.append(",");
            }
        }
        sb.append("]").append("无法找到对应的备份数据！");
        return sb.toString();
    }

    /**
     * 获取返销配置数据
     * 
     * @return
     * @throws Exception
     */
    public static IData getUndoParamInfo(String tableName, IDataset commParaInfos) throws Exception
    {
        for (int i = 0; i < commParaInfos.size(); i++)
        {
            IData commparaInfo = commParaInfos.getData(i);
            if (StringUtils.equals(tableName, commparaInfo.getString("PARA_CODE1")))
            {
                IData param = new DataMap();
                String paraCode2 = commparaInfo.getString("PARA_CODE2");// 资料表
                String paraCode3 = commparaInfo.getString("PARA_CODE3");// 资料表所属中心
                String paraCode18 = commparaInfo.getString("PARA_CODE18");// 获取用户分区字段表的字段（USER_ID/CUST_ID/ACCT_ID）
                String paraCode19 = commparaInfo.getString("PARA_CODE19");// 修改用户资料数据相关key
                String paraCode20 = commparaInfo.getString("PARA_CODE20");// 查询台账信息类
                String paraCode21 = commparaInfo.getString("PARA_CODE21");// 查询台账信息方法
                String paraCode22 = commparaInfo.getString("PARA_CODE22", "");// 查询台账备份数据信息类
                String paraCode23 = commparaInfo.getString("PARA_CODE23", "");// 查询台账备份数据信息方法

                if (StringUtils.isBlank(paraCode2))
                {
                    CSAppException.apperr(TradeException.CRM_TRADE_95, tableName + "返销时配置的资料表为空！");
                }

                if (StringUtils.isBlank(paraCode19))
                {
                    CSAppException.apperr(TradeException.CRM_TRADE_95, tableName + "返销时配置的资料修改KEY为空！");
                }

                if (StringUtils.isBlank(paraCode20))
                {
                    CSAppException.apperr(TradeException.CRM_TRADE_95, tableName + "返销时配置的台账信息查询类为空！");
                }

                if (StringUtils.isBlank(paraCode21))
                {
                    CSAppException.apperr(TradeException.CRM_TRADE_95, tableName + "返销时配置的台账信息查询方法为空！");
                }

                // 营销活动的几个表是没有备份表的，且活动只存在新增的情况，不需要查询bak表，此处过滤
                if (StringUtils.isBlank(paraCode22) && (!StringUtils.equals("TF_B_TRADE_SALE_GOODS", tableName) 
                		&& !StringUtils.equals("TF_B_TRADE_SALE_ACTIVE", tableName) && !StringUtils.equals("TF_B_TRADE_SALEACTIVE_BOOK", tableName)
                		&&!StringUtils.equals("TF_B_TRADE_SALE_DEPOSIT", tableName)))
                {
                    CSAppException.apperr(TradeException.CRM_TRADE_95, tableName + "返销时配置的备份数据信息查询类为空！");
                }

                // 营销活动的几个表是没有备份表的，且活动只存在新增的情况，不需要查询bak表，此处过滤
                if (StringUtils.isBlank(paraCode23) && (!StringUtils.equals("TF_B_TRADE_SALE_GOODS", tableName) 
                		&& !StringUtils.equals("TF_B_TRADE_SALE_ACTIVE", tableName) && !StringUtils.equals("TF_B_TRADE_SALEACTIVE_BOOK", tableName)
                		&& !StringUtils.equals("TF_B_TRADE_SALE_DEPOSIT", tableName)))
                {
                    CSAppException.apperr(TradeException.CRM_TRADE_95, tableName + "返销时配置的备份数据信息查询方法为空！");
                }

                param.put("USER_TABLE", StringUtils.upperCase(paraCode2));// 资料表
                param.put("PARTITION_VALUES", StringUtils.upperCase(paraCode18));// 获取用户数据表分区值的字段
                param.put("KEYS", StringUtils.upperCase(paraCode19));// 修改数据相关key
                param.put("QEY_TRADE_CLASS", paraCode20);// 查询台账信息类
                param.put("QEY_TRADE_METHOD", paraCode21);// 查询台账信息方法
                param.put("QEY_BAK_CLASS", paraCode22);// 查询台账备份数据信息类
                param.put("QEY_BAK_METHOD", paraCode23);// 查询台账备份数据信息方法
                param.put("TABLE_ROUTE_ID", paraCode3);// 资料表所属中心
                return param;
            }
        }
        return null;
    }

    /**
     * 插入已完工表
     * 
     * @param mainTrade
     * @throws Exception
     */
    private static void insertTradeFinish(IData mainTrade, long beginTime) throws Exception
    {
        IData tradeFinish = new DataMap();
        tradeFinish.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
        tradeFinish.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE"));
        tradeFinish.put("ACCEPT_MONTH", mainTrade.getString("ACCEPT_MONTH"));
        tradeFinish.put("ACCEPT_DATE", mainTrade.getString("ACCEPT_DATE"));
        tradeFinish.put("FINISH_DATE", SysDateMgr.getSysTime());
        tradeFinish.put("CANCEL_TAG", mainTrade.getString("CANCEL_TAG"));
        tradeFinish.put("CANCEL_DATE", mainTrade.getString("CANCEL_DATE"));
        tradeFinish.put("UPDATE_TIME", SysDateMgr.getSysTime());
        tradeFinish.put("DATA_CLEAR_TAG", "0");
        tradeFinish.put("DATA_CLEAR_USER_ID", mainTrade.getString("USER_ID"));
        tradeFinish.put("RSRV_STR1", "cost time:" + (System.currentTimeMillis() - beginTime));
        Dao.insert("TF_B_TRADE_FINISH", tradeFinish,Route.getJourDb());
    }

    /**
     * 获取备份数据
     * 
     * @param bakInfos
     * @param tradeInfo
     * @param keys
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeBakInfos(IData undoParam, String tradeId) throws Exception
    {
        Class classs = Class.forName(undoParam.getString("QEY_BAK_CLASS"));
        Method queryMethod = classs.getMethod(undoParam.getString("QEY_BAK_METHOD"), String.class);
        return (IDataset) queryMethod.invoke(classs, tradeId);
    }

    /**
     * 获取台账数据
     * 
     * @param bakInfos
     * @param tradeInfo
     * @param keys
     * @return
     * @throws Exception
     */
    private static IDataset queryTradeInfos(IData undoParam, String tradeId) throws Exception
    {
        Class classs = Class.forName(undoParam.getString("QEY_TRADE_CLASS"));
        Method queryMethod = classs.getMethod(undoParam.getString("QEY_TRADE_METHOD"), String.class);
        return (IDataset) queryMethod.invoke(classs, tradeId);
    }

    /**
     * 返销新增的用户订单
     * 
     * @param tradeUser
     * @throws Exception
     */
    private static void UndoAddTrade(String userTabName, IDataset addTradeInfos, String[] keys, IData undoParam, IData mainTrade) throws Exception
    {
        String route = undoParam.getString("TABLE_ROUTE_ID", "");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
        if (StringUtils.isBlank(route))
        {
            route = BizRoute.getRouteId();
        }

        if (StringUtils.equals("TF_F_USER", userTabName))// 用户资料表不删除，否则同步会有问题
        {
            for (int i = 0; i < addTradeInfos.size(); i++)
            {
                IData param = addTradeInfos.getData(i);
                param.put("REMOVE_TAG", "5");
                param.put("REMOVE_EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
                param.put("REMOVE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
                param.put("REMOVE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
                param.put("CANCEL_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
                param.put("CANCEL_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
                param.put("REMARK", "用户档案新增返销！");
            }
            Dao.executeBatchByCodeCode(userTabName, "UPD_INS_UNDO", addTradeInfos, route);
        }
        else if (StringUtils.equals("TF_F_ACCOUNT", userTabName))// 账户资料
        {
            Dao.executeBatchByCodeCode(userTabName, "UPD_DEL_BY_ACCTID", addTradeInfos, route);
        }
        else if (StringUtils.equals("TF_F_CUSTOMER", userTabName)
                || StringUtils.equals("TF_F_CUST_PERSON", userTabName)
                || StringUtils.equals("TF_F_ACCOUNT_ACCTDAY", userTabName)
                || StringUtils.equals("TF_F_USER_ACCTDAY", userTabName))
        {
            // 老系统没有处理
        }
        else if (StringUtils.equals("TF_F_USER_PLATSVC", userTabName))
        {
            Dao.delete(userTabName, addTradeInfos, keys, route);
            for (int i = 0; i < addTradeInfos.size(); i++)
            {
                IData param = addTradeInfos.getData(i);
                param.put("RELA_INST_ID", param.getString("INST_ID"));
            }
            Dao.delete("TF_F_USER_PLATSVC_TRACE", addTradeInfos, new String[]
            { "PARTITION_ID", "USER_ID", "RELA_INST_ID" }, route);
        }
        else if (StringUtils.equals("TF_A_PAYRELATION", userTabName))
        {
            String userId = mainTrade.getString("USER_ID");
            String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);
            firstDayNextAcct = StringUtils.replace(firstDayNextAcct, "-", "");
            //开户返销,用户付费关系表不删除，否则同步给账管的acct_id会是-1，账管会有问题
            for (int i = 0; i < addTradeInfos.size(); i++)
            {
                IData param = addTradeInfos.getData(i);
                param.put("END_CYCLE_ID", firstDayNextAcct);
                param.put("USER_ID", param.getString("USER_ID"));
                param.put("UPDATE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
                param.put("UPDATE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
                param.put("DEFAULT_TAG", param.getString("DEFAULT_TAG"));
                param.put("ACT_TAG", param.getString("ACT_TAG"));
            }
            Dao.executeBatchByCodeCode(userTabName, "UPD_NOR_END", addTradeInfos, route);
        }
        else if (StringUtils.equals("TF_F_USER_RES", userTabName) && "500".equals(tradeTypeCode))
        {
        	 /**
        	  * REQ201611280023  批量预开户返销优化 
        	  * 在做批量预开返销时(trade_type_code=500，系统保留tf_f_user_res表的数据，只将其中的end_date截止到当前时间。
        	  * */
        	 for (int i = 0; i < addTradeInfos.size(); i++)
             {
                 IData param = addTradeInfos.getData(i);
                 param.put("REMARK", "批量预开户返销保留RES记录！");
             }
        	Dao.executeBatchByCodeCode(userTabName, "UPD_RES_END_DATE_UNDO", addTradeInfos, route);
        }
        else
        // 其他用户资料
        {
            Dao.delete(userTabName, addTradeInfos, keys, route);
        }

    }

    /***************************** 工具类 **************************************************/

    // SELECT a.*,a.rowid FROM Td_s_Commpara a where param_attr='6789';
    public static void undoTrade(IData mainTrade) throws Exception
    {
        long beginTime = System.currentTimeMillis();
        String intfId = mainTrade.getString("INTF_ID", "");
        if (logger.isDebugEnabled())
        {
            logger.debug("当前返销台账涉及的台账子表有：" + intfId);
        }
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        if ("110".equals(tradeTypeCode))
        {
            // 产品变更需要进行intf_id的单独配置
            intfId = TradeUndo.getChangeProductStandardUndoTable(intfId);
            CancelChangeProductUtil cancel = new CancelChangeProductUtil(mainTrade, true);
            cancel.doCancelChangeProduct();
        }
        if (StringUtils.isNotBlank(intfId))
        {
            IDataset commParaInfos = CommparaInfoQry.getCommparaAllCol("CSM", "6789", "UNDO", mainTrade.getString("TRADE_EPARCHY_CODE"));
            if (IDataUtil.isEmpty(commParaInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "返销时无法找到配置的参数[td_s_commpara]！");
            }
            String[] tables = StringUtils.split(intfId, ",");
            for (int i = 0; i < tables.length; i++)
            {

                if (StringUtils.isNotBlank(tables[i]))
                {
                    undoTradeTable(StringUtils.upperCase(tables[i]), mainTrade, commParaInfos);
                }
            }
        }
        else
        {
            // 兼容后台直接写台账的情况/或者老系统倒换过来的数据，把所有的台账表都查询一次吧，只能这样了
            IDataset commParaInfos = CommparaInfoQry.getCommparaAllCol("CSM", "6789", "UNDO", mainTrade.getString("TRADE_EPARCHY_CODE"));
            if (IDataUtil.isEmpty(commParaInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "返销时无法找到配置的参数[td_s_commpara]！");
            }

            StringBuilder intfIdSb = new StringBuilder();// 记录台账字表信息，主要是给哪些INTF_ID没值的情况使用
            for (int i = 0; i < commParaInfos.size(); i++)
            {
                String tradeTableName = commParaInfos.getData(i).getString("PARA_CODE1");
                if (StringUtils.isNotBlank(tradeTableName))
                {
                    boolean flag = undoTradeTable(StringUtils.upperCase(tradeTableName), mainTrade, commParaInfos);
                    if (flag)
                    {
                        intfIdSb.append(StringUtils.upperCase(tradeTableName)).append(",");// 记录有数据的台账表
                    }
                }
            }

            if (intfIdSb.length() > 0)// 有问题，先屏蔽
            {
                intfIdSb.append("TF_B_TRADE").append(",");//
                mainTrade.put("INTF_ID", intfIdSb.toString());// 吧整理出来的台账表信息反写进去
            }
        }

        updateMainSubscribeState(mainTrade);// 修改主台账为完工状态
        insertTradeFinish(mainTrade, beginTime);
    }

    /**
     * 处理返销子台账
     */
    private static boolean undoTradeTable(String tableName, IData mainTrade, IDataset commParaInfos) throws Exception
    {
        IData undoParam = getUndoParamInfo(tableName, commParaInfos);
        if (null == undoParam)
        {
            return false;// CSAppException.apperr(TradeException.CRM_TRADE_95,"返销时无法找到"+tableName+"对应的配置参数！");
        }

        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeInfos = queryTradeInfos(undoParam, tradeId);// 找到对应的台账资料
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            String[] keys = StringUtils.split(undoParam.getString("KEYS"), ",");
            String userTabName = undoParam.getString("USER_TABLE");// 资料表
            String partitionValues = undoParam.getString("PARTITION_VALUES");// 获取资料表分区字段值的列

            IDataset addTradeInfos = new DatasetList();// 台账新增的数据
            IDataset updTradeInfos = new DatasetList();// 台账修改或者变更的数据
            for (int i = 0; i < tradeInfos.size(); i++)
            {
                IData tradeInfo = tradeInfos.getData(i);
                String modifyTag = tradeInfo.getString("MODIFY_TAG");

                if (StringUtils.isNotBlank(partitionValues))// 获取分区值的配置存在
                {
                    String temValue = tradeInfo.getString(partitionValues);
                    if (StringUtils.isNotBlank(temValue))
                    {
                        // 暂时默认分区字段都是：PARTITION_ID.以后有特殊情况可以考虑修改配置
                        // 配置了取分区字段值的参数，就可以考虑在修改资料表的key中加入partition_id
                        String partionId = String.valueOf(Long.valueOf(temValue) % 10000);// 根据配置的取分区字段的参数来获取分区值
                        tradeInfo.put("PARTITION_ID", partionId);// 加入分区字段，后面在删除、修改用户资料的时候用上，提交效率
                    }
                }

                if (StringUtils.equals(BofConst.MODIFY_TAG_ADD, modifyTag))
                {
                	if(!isNoCancelDiscnt(tableName,  tradeInfo.getString("DISCNT_CODE"), mainTrade))
                	{
                       addTradeInfos.add(tradeInfo);
                	}
                }
                else if (StringUtils.equals(BofConst.MODIFY_TAG_DEL, modifyTag))
                {
                    // 营销活动的几个表是没有备份表的，且活动只存在新增的情况，不需要处理bak表，此处过滤
                    if (!StringUtils.equals("TF_B_TRADE_SALE_GOODS", tableName)
                            && !StringUtils.equals("TF_B_TRADE_SALE_ACTIVE", tableName) 
                            && !StringUtils.equals("TF_B_TRADE_SALEACTIVE_BOOK", tableName)
                            && !StringUtils.equals("TF_B_TRADE_SALE_DEPOSIT", tableName))
                    {
                        updTradeInfos.add(tradeInfo);
                    }
                }
                else if (StringUtils.equals(BofConst.MODIFY_TAG_UPD, modifyTag))
                {
                    // 营销活动的几个表是没有备份表的，且活动只存在新增的情况，不需要处理bak表，此处过滤
                    if (!StringUtils.equals("TF_B_TRADE_SALE_GOODS", tableName) 
                            && !StringUtils.equals("TF_B_TRADE_SALE_ACTIVE", tableName) 
                            && !StringUtils.equals("TF_B_TRADE_SALEACTIVE_BOOK", tableName)
                            && !StringUtils.equals("TF_B_TRADE_SALE_DEPOSIT", tableName))
                    {
                        updTradeInfos.add(tradeInfo);
                    }
                }
                else if (StringUtils.equals("U", modifyTag))
                {
                    updTradeInfos.add(tradeInfo);
                }
                else if (StringUtils.isBlank(modifyTag))
                {
                    CSAppException.apperr(TradeException.CRM_TRADE_95, "返销时无法找到" + tableName + "对应的modify_tag值");
                }
            }
            UndoAddTrade(userTabName, addTradeInfos, keys, undoParam, mainTrade);// 返销新增数据
            UndoUpdTrade(userTabName, updTradeInfos, keys, undoParam, tradeId);// 返销变更数据
            return true;
        }
        return false;
    }

    /**
     * 返销修改和终止的用户订单 updTradeInfos 当前被修改或者终止的台账数据
     * 
     * @param tradeUser
     * @throws Exception
     */
    private static void UndoUpdTrade(String userTabName, IDataset updTradeInfos, String[] keys, IData undoParam, String tradeId) throws Exception
    {
        if (updTradeInfos.size() > 0)
        {
            String route = undoParam.getString("TABLE_ROUTE_ID", "");
            if (StringUtils.isBlank(route))
            {
                route = BizRoute.getRouteId();
            }

            IDataset bakTradeInfos = queryTradeBakInfos(undoParam, tradeId);// 获取当前table业务备份的数据
            if (IDataUtil.isNotEmpty(bakTradeInfos))
            {
                for (int i = 0; i < updTradeInfos.size(); i++)
                {
                    IData upTradeInfo = updTradeInfos.getData(i);
                    IData bakTradeInfo = getBakTradeInfo(bakTradeInfos, upTradeInfo, keys);
                    if (null != bakTradeInfo)
                    {
                        Dao.update(userTabName, bakTradeInfo, keys, route);

                        // 处理TF_F_USER_PLATSVC_TRACE 只对删除的处理
                        if (StringUtils.equals("TF_F_USER_PLATSVC", userTabName) && BofConst.MODIFY_TAG_DEL.equals(upTradeInfo.getString("MODIFY_TAG")))
                        {
                            bakTradeInfo.put("RELA_INST_ID", bakTradeInfo.getString("INST_ID"));

                            Dao.executeUpdateByCodeCode("TF_F_USER_PLATSVC_TRACE", "DEL_PLATSVC_TRACE", bakTradeInfo, route);
                            Dao.executeUpdateByCodeCode("TF_F_USER_PLATSVC_TRACE", "UPD_PLATSVC_TRACE", bakTradeInfo, route);
                        }
                    }
                    else
                    {
                        CSAppException.apperr(TradeException.CRM_TRADE_95, getErrorInfos(updTradeInfos.getData(i), keys));
                    }
                }
            }
            else
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "返销时无法找到" + userTabName + "对应的备份数据[" + tradeId + "]");
            }
        }
    }

    /**
     * 修改主台账状态
     * 
     * @param mainTrade
     * @throws Exception
     */
    private static void updateMainSubscribeState(IData mainTrade) throws Exception
    {
        mainTrade.put("SUBSCRIBE_STATE", "9");
        //mainTrade.put("FINISH_DATE", SysDateMgr.getSysTime());
        mainTrade.put("EXEC_DESC", "TRADEOK");
        
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE TF_B_TRADE SET SUBSCRIBE_STATE=:SUBSCRIBE_STATE, ");
        sql.append("FINISH_DATE =SYSDATE ,");
        sql.append("EXEC_DESC =:EXEC_DESC ");
        sql.append("WHERE TRADE_ID =:TRADE_ID AND ACCEPT_MONTH =:ACCEPT_MONTH");
        Dao.executeUpdate(sql, mainTrade, Route.getJourDb());
        //Dao.update("TF_B_TRADE", mainTrade,Route.getJourDb());
    }
    
    private static boolean isNoCancelDiscnt(String tableName, String _discntCode, IData mainTrade)throws Exception
    {
    	String _tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
    	
    	if(!StringUtils.equals("TF_B_TRADE_DISCNT", tableName) || !StringUtils.equals("240", _tradeTypeCode))
    	{
    		return false;
    	}
    	
		String _packageId = mainTrade.getString("RSRV_STR2");
		String _eparchyCode = mainTrade.getString("EPARCHY_CODE");
		IDataset commparaDatset = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "520", _packageId, _discntCode, _eparchyCode);
		
		if(IDataUtil.isNotEmpty(commparaDatset))
		{
			return true;
		}
    	
    	return false;
    }

}
