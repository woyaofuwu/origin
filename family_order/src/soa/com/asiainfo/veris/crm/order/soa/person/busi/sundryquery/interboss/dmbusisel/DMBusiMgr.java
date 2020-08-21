
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss.DMBusiInfoQry;

public class DMBusiMgr extends CSBizBean
{

    private static final transient Logger logger = Logger.getLogger(DMBusiMgr.class);

    public IData DMInsertData(IData data) throws Exception
    {
        // 生成一条流水号
        String eparchyCode = data.getString("TRADE_EPARCHY_CODE");
        String citycode = data.getString("TRADE_CITY_CODE");
        String departId = data.getString("TRADE_DEPART_ID");
        String staffId = data.getString("TRADE_STAFF_ID");

        String strSequenceId = SeqMgr.getTradeId();

        IData DMBusi = new DataMap();
        IData DMBusiSub = new DataMap();

        String applyType = data.getString("APPLY_TYPE", "20");
        String addTag = "0"; // 标示是否有子表信息

        // 对于业务受理请求，直接插入表
        if (applyType.charAt(0) == '1')
        {
            // 转换APPLY_TYPE，方便查询(如四元素采集请求(11)--四元素采集请求反馈查询(31))
            applyType = applyType.replaceFirst(String.valueOf(applyType.charAt(0)), "3");

            // 插入子表(信息收集或参数配置请求中填了多个业务ID)
            if ((applyType.equals("32") || applyType.equals("33")) && data.getDataset("FUNCTIONID") != null && data.getDataset("FUNCTIONID").size() > 0)
            {
                for (int i = 0; i < data.getDataset("FUNCTIONID").size(); i++)
                {

                    if ("".equals(data.getDataset("FUNCTIONID").get(i, "FUNCTIONID")))
                    {
                        continue;
                    }
                    DMBusiSub.put("OPERATE_ID1", data.getString("OPERATEID", ""));
                    DMBusiSub.put("PARAMCODE", applyType);
                    DMBusiSub.put("FUNCTIONID", data.getDataset("FUNCTIONID").get(i, "FUNCTIONID"));
                    DMBusiSub.put("PARAMNAME", "00");
                    DMBusiSub.put("PARAMVALUE", data.getDataset("PARAMVALUE") == null ? "" : data.getDataset("PARAMVALUE").get(i, "PARAMVALUE"));
                    DMBusiSub.put("PARA1", data.getDataset("PARA1") == null ? "" : data.getDataset("PARA1").get(i, "PARA1"));
                    DMBusiSub.put("PARA2", data.getDataset("PARA2") == null ? "" : data.getDataset("PARA2").get(i, "PARA2"));
                    Dao.executeUpdateByCodeCode("TI_B_DM_BUSISUB", "INS_DM_SUB_DATA", DMBusiSub, Route.CONN_CRM_CEN);
                    addTag = "1";
                }
            }
            // 插入主表
            DMBusi.put("IBSYSID", strSequenceId);
            DMBusi.put("APPLY_TYPE", applyType);
            DMBusi.put("OPERATEID", data.getString("OPERATEID", ""));
            DMBusi.put("PHONENUM", data.getString("PHONENUM", ""));
            DMBusi.put("IMEINUM", data.getString("IMEINUM", ""));
            // 固件升级请求
            if (applyType.equals("34"))
            {
                DMBusi.put("BAGID", data.getString("UPGRADEBAGID", ""));
            }
            // 固件回退请求
            else if (applyType.equals("35"))
            {
                DMBusi.put("BAGID", data.getString("ROLLBACKBAGID", ""));
            }
            // 应用软件下载安装请求
            else if (applyType.equals("36"))
            {
                DMBusi.put("BAGID", data.getString("SOFTWAREBAGID", ""));
            }
            else
            {
                DMBusi.put("BAGID", "");
            }

            DMBusi.put("TERMINALID", data.getString("TERMINALID", ""));
            DMBusi.put("SOFTWAREEDITIONNUM", data.getString("SOFTWAREEDITIONNUM", ""));
            DMBusi.put("ADD_TAG", addTag);
            DMBusi.put("TRADE_EPARCHY_CODE", eparchyCode);
            DMBusi.put("TRADE_CITY_CODE", citycode);
            DMBusi.put("TRADE_DEPART_ID", departId);
            DMBusi.put("TRADE_STAFF_ID", staffId);
            DMBusi.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
            DMBusi.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
            DMBusi.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
            DMBusi.put("ACCOUNTNUM", data.getString("ACCOUNTNUM", ""));

            Dao.executeUpdateByCodeCode("TI_B_DM_BUSI", "INS_DM_DATA", DMBusi, Route.CONN_CRM_CEN);
        }
        // 对于操作取消请求成功的数据，需要更新主表的返销标记CANCEL_TAG
        else if (applyType.equals("20"))
        {
            DMBusi.put("OPERATEID", data.getString("OPERATEID", ""));
            DMBusi.put("CANCEL_TAG", "1");
            DMBusi.put("CANCEL_EPARCHY_CODE", eparchyCode);
            DMBusi.put("CANCEL_CITY_CODE", citycode);
            DMBusi.put("CANCEL_DEPART_ID", departId);
            DMBusi.put("CANCEL_STAFF_ID", staffId);
            DMBusi.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
            DMBusi.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
            DMBusi.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
            DMBusi.put("CANCEL_ACCOUNTNUM", data.getString("ACCOUNTNUM", ""));

            Dao.executeUpdateByCodeCode("TI_B_DM_BUSI", "UPD_DM_CANCELDATA", DMBusi, Route.CONN_CRM_CEN);
        }
        data.put("X_RESULTCODE", "0");
        data.put("X_RESULTINFO", "业务办理成功！");
        data.put("ORDER_ID", data.getString("OPERATEID", ""));
        return data;
    }

    public IData DMSelectData(IData data) throws Exception
    {
        IData dmData = new DataMap();

        IData dataInfo = new DataMap();

        String applyType = data.getString("APPLY_TYPE", "");

        if ("30".equals(applyType))
        {
            applyType = "";
        }

        dmData.put("APPLY_TYPE", applyType);
        dmData.put("RESULTSTATUS", data.getString("RESULTSTATUS", ""));
        dmData.put("OPERATEID", data.getString("OPERATEID", ""));
        dmData.put("PHONENUM", data.getString("PHONENUM", ""));
        dmData.put("IMEINUM", data.getString("IMEINUM", ""));
        dmData.put("BAGID", data.getString("BAGID", ""));
        dmData.put("TERMINALID", data.getString("TERMINALID", ""));
        dmData.put("SOFTWAREEDITIONNUM", data.getString("SOFTWAREEDITIONNUM", ""));
        dmData.put("STARTDATE", data.getString("STARTDATE", ""));
        dmData.put("ENDDATE", data.getString("ENDDATE", ""));
        dmData.put("CONFIGUREAGREEMENT", data.getString("CONFIGUREAGREEMENT", ""));
        dmData.put("FAILTYPE", data.getString("FAILTYPE", ""));
        dmData.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", ""));
        dmData.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE", ""));
        dmData.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", ""));
        dmData.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", ""));
        dmData.put("ACCOUNTNUM", data.getString("ACCOUNTNUM", ""));
        dmData.put("CANCEL_TAG", data.getString("CANCEL_TAG", ""));
        dmData.put("CANCEL_EPARCHY_CODE", data.getString("CANCEL_EPARCHY_CODE", ""));
        dmData.put("CANCEL_CITY_CODE", data.getString("CANCEL_CITY_CODE", ""));
        dmData.put("CANCEL_DEPART_ID", data.getString("CANCEL_DEPART_ID", ""));
        dmData.put("CANCEL_STAFF_ID", data.getString("CANCEL_STAFF_ID", ""));
        dmData.put("CANCEL_ACCOUNTNUM", data.getString("CANCEL_ACCOUNTNUM", ""));

        IDataset busiDataset = DMBusiInfoQry.getDMBusiInfo(dmData);

        if (IDataUtil.isEmpty(busiDataset))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_150);
        }

        IDataset busiSubDataset = new DatasetList();

        for (int i = 0; i < busiDataset.size(); i++)
        {
            if (!busiDataset.getData(i).getString("ADD_TAG", "").equals("1"))
            {
                continue;
            }
            else
            {
                String operID = busiDataset.getData(i).getString("OPERATEID", "");

                busiSubDataset = DMBusiInfoQry.getDMBusiSubInfo(operID);

                break;
            }

        }
        dataInfo.put("BUSI", busiDataset);
        dataInfo.put("DM_BUSI", busiSubDataset);

        return dataInfo;
    }

    /**
     * 根据DM平台发起的业务受理反馈更新主表(插入子表)
     * 
     * @data 2013-8-6
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset DMUpdateData(IData data) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug("===============>>>DM反馈接口调用开始>>1>>>>======DMUpdateData======" + data);
        IDataset dataset = new DatasetList();
        IData tempData = new DataMap();
        IData DMBusiSub = new DataMap();
        IData DMBusi = new DataMap();
        String applyType = data.getString("APPLY_TYPE", "20");
        // 转换APPLY_TYPE，方便查询(如四元素采集请求(11)--四元素采集请求反馈查询(31))
        applyType = applyType.replaceFirst(String.valueOf(applyType.charAt(0)), "3");
        String addTag = "0";
        String operateid = data.getString("OPERATEID", "");
        String paramcode = "";

        IDataset functionids = new DatasetList();
        IDataset paramnames = new DatasetList();
        IDataset paramvalues = new DatasetList();

        IDataset afterfunctionids = new DatasetList();
        IDataset afterparamnames = new DatasetList();
        IDataset afterparamvalues = new DatasetList();

        /************** IBOSS数据转换 start *******************/

        // String 类型 例如：{FUNCTIONID =["001"]}
        if (data.get("FUNCTIONID") instanceof String)
        {

            functionids.add(data.get("FUNCTIONID"));
            paramnames.add(data.get("PARAMNAME"));
            paramvalues.add(data.get("PARAMVALUE"));
        }
        // JSONArray类型 例如：{FUNCTIONID=["001","002"]}
        else
        {
            functionids = data.getDataset("FUNCTIONID");//
            paramnames = data.getDataset("PARAMNAME");//
            paramvalues = data.getDataset("PARAMVALUE");//
        }

        // String 类型 例如：{AFTERFUNCTIONID =["001"]}
        if (data.get("AFTERFUNCTIONID") instanceof String)
        {

            afterfunctionids.add(data.get("AFTERFUNCTIONID"));
            afterparamnames.add(data.get("AFTERPARAMNAME"));
            afterparamvalues.add(data.get("AFTERPARAMVALUE"));
        }
        // JSONArray类型 例如：{AFTERFUNCTIONID=["001","002"]}
        else
        {
            afterfunctionids = data.getDataset("AFTERFUNCTIONID");//
            afterparamnames = data.getDataset("AFTERPARAMNAME");//
            afterparamvalues = data.getDataset("AFTERPARAMVALUE");//
        }

        /************** IBOSS数据转换 end *******************/
        if (logger.isDebugEnabled())
        {
            logger.debug("===============>>>DM反馈接口调用>>>2>>>======FUNCTIONID======" + functionids);
            logger.debug("===============>>>DM反馈接口调用>>>3>>>======AFTERFUNCTIONID======" + afterfunctionids);
        }
        // 业务参数信息插入子表
        if (("32".equals(applyType) || "33".equals(applyType)) && ((functionids != null && functionids.size() > 0) || (afterfunctionids != null && afterfunctionids.size() > 0)))
        {

            // 如果有重复数据，先删除
            DMBusiSub.put("OPERATE_ID1", operateid);
            Dao.executeUpdateByCodeCode("TI_B_DM_BUSISUB", "DEL_DM_SUB_DATA", DMBusiSub, Route.CONN_CRM_CEN);// 删除子表

            for (int i = 0; i < functionids.size(); i++)
            {
                if (logger.isDebugEnabled())
                    logger.debug("===============>>>DM反馈接口调用>>>4>>>======进入FUNCTIONID======");

                paramcode = applyType + "0";
                if ("".equals(functionids.get(i)))
                {
                    continue;
                }
                DMBusiSub.clear();
                DMBusiSub.put("OPERATE_ID1", operateid);
                DMBusiSub.put("PARAMCODE", paramcode);
                DMBusiSub.put("FUNCTIONID", functionids.get(i));
                DMBusiSub.put("PARAMNAME", paramnames.get(i));
                DMBusiSub.put("PARAMVALUE", paramvalues.get(i));
                DMBusiSub.put("PARA1", "");// 文档中没有次节点
                DMBusiSub.put("PARA2", "");// 文档中没有次节点

                Dao.executeUpdateByCodeCode("TI_B_DM_BUSISUB", "INS_DM_SUB_DATA", DMBusiSub, Route.CONN_CRM_CEN); // 插入子表
                addTag = "1";
            }
            for (int i = 0; i < afterfunctionids.size(); i++)
            {
                if (logger.isDebugEnabled())
                    logger.debug("===============>>>DM反馈接口调用>>>5>>>======进入AFTERFUNCTIONID======");

                paramcode = applyType + "1";
                if ("".equals(afterfunctionids.get(i)))
                {
                    continue;
                }
                DMBusiSub.clear();
                DMBusiSub.put("OPERATE_ID1", operateid);
                DMBusiSub.put("PARAMCODE", paramcode);
                DMBusiSub.put("FUNCTIONID", afterfunctionids.get(i));
                DMBusiSub.put("PARAMNAME", afterparamnames.get(i));
                DMBusiSub.put("PARAMVALUE", afterparamvalues.get(i));
                DMBusiSub.put("PARA1", "");// 文档中没有次节点
                DMBusiSub.put("PARA2", "");// 文档中没有次节点

                Dao.executeUpdateByCodeCode("TI_B_DM_BUSISUB", "INS_DM_SUB_DATA", DMBusiSub, Route.CONN_CRM_CEN); // 插入子表
                addTag = "1";
            }
        }
        // 先查询出表中原操作记录
        DMBusi.put("OPERATEID", operateid);
        DMBusi.put("APPLY_TYPE", applyType);
        IDataset dataforreq = DMBusiInfoQry.getDMdataforreq(DMBusi);
        if (logger.isDebugEnabled())
            logger.debug("===============>>>DM反馈接口调用>>>6>>>======dataforreq======" + dataforreq);
        String strSequenceId = "";

        // 没查询到原始记录，插入一条错误记录入表，APPLY_TYPE：4+applyType[1]
        if (IDataUtil.isEmpty(dataforreq))
        {

            if (logger.isDebugEnabled())
                logger.debug("===============>>>DM反馈接口调用>>>7>>>======dataforreq==0======");
            applyType = applyType.replaceFirst(String.valueOf(applyType.charAt(0)), "4");

            strSequenceId = SeqMgr.getTradeId();
            DMBusi.clear();
            DMBusi.put("IBSYSID", strSequenceId);
            DMBusi.put("APPLY_TYPE", applyType);
            DMBusi.put("OPERATEID", operateid);
            DMBusi.put("PHONENUM", data.getString("PHONENUM", ""));
            DMBusi.put("IMEINUM", data.getString("IMEINUM", ""));
            DMBusi.put("RESULTSTATUS", data.getString("RESULTSTATUS", ""));
            DMBusi.put("TERMINALID", data.getString("TERMINALID", ""));
            DMBusi.put("SOFTWAREEDITIONNUM", data.getString("SOFTWAREEDITIONNUM", ""));
            DMBusi.put("BEGINTIME", data.getString("BEGINTIME", ""));
            DMBusi.put("ENDTIME", data.getString("ENDTIME", ""));
            DMBusi.put("FAILTYPE", data.getString("FAILTYPE", ""));
            DMBusi.put("FAILREASON", data.getString("FAILREASON", ""));
            DMBusi.put("ADD_TAG", addTag);
            DMBusi.put("CONFIGUREAGREEMENT", data.getString("CONFIGUREAGREEMENT", ""));
            DMBusi.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", ""));
            DMBusi.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE", ""));
            DMBusi.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", ""));
            DMBusi.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", ""));
            Dao.executeUpdateByCodeCode("TI_B_DM_BUSI", "INS_DM_ERRORDATA", DMBusi, Route.CONN_CRM_CEN);

        }
        // 查询到原始记录，更新主表记录
        else if (dataforreq.size() == 1)
        {
            if (logger.isDebugEnabled())
                logger.debug("===============>>>DM反馈接口调用>>>8>>>======dataforreq.size()==1======");
            DMBusi.clear();
            DMBusi.put("IBSYSID", dataforreq.getData(0).getString("IBSYSID", ""));
            DMBusi.put("PHONENUM", data.getString("PHONENUM", dataforreq.getData(0).getString("PHONENUM", "")));
            DMBusi.put("IMEINUM", data.getString("IMEINUM", dataforreq.getData(0).getString("IMEINUM", "")));
            DMBusi.put("RESULTSTATUS", data.getString("RESULTSTATUS", "2"));
            DMBusi.put("TERMINALID", data.getString("TERMINALID", dataforreq.getData(0).getString("TERMINALID", "")));
            DMBusi.put("SOFTWAREEDITIONNUM", data.getString("SOFTWAREEDITIONNUM", dataforreq.getData(0).getString("SOFTWAREEDITIONNUM", "")));
            DMBusi.put("BEGINTIME", data.getString("BEGINTIME", ""));
            DMBusi.put("ENDTIME", data.getString("ENDTIME", ""));
            DMBusi.put("FAILTYPE", data.getString("FAILTYPE", ""));
            DMBusi.put("FAILREASON", data.getString("FAILREASON", ""));
            DMBusi.put("ADD_TAG", (addTag == "1") ? addTag : dataforreq.getData(0).getString("ADD_TAG", "0"));
            DMBusi.put("CONFIGUREAGREEMENT", data.getString("CONFIGUREAGREEMENT", ""));

            Dao.executeUpdateByCodeCode("TI_B_DM_BUSI", "UDP_DM_REQDATA", DMBusi, Route.CONN_CRM_CEN);
        }

        tempData.put("X_RESULTCODE", "0");
        tempData.put("X_RESULTINFO", "业务办理成功！");
        tempData.put("ORDER_ID", data.getString("OPERATEID", ""));
        dataset.add(tempData);
        if (logger.isDebugEnabled())
            logger.debug("===============>>>DM反馈接口调用结束>>>>9>>======dataset======" + dataset);
        return dataset;

    }

}
