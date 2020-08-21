package com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformNodeBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductHBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

/**
 * order
 * esop待办生成数据抽取服务实体类
 * @author ckh
 * @date 2018/1/29.
 */
public class EsopWorkTaskDataBean
{
    private static final String[] CHECK_PARAM_NAMES = new String[]{"BI_SN", "BUSIFORM_NODE_ID", "DEAL_STAFF_ID"};

    public IData getWorkTaskDataInfo(IData param) throws Exception
    {
        EsopDeskTopUtils.checkParam(param, CHECK_PARAM_NAMES);
        IData retData = new DataMap();
        String ibSysId = param.getString("BI_SN");
        retData.put("RELA_ID", ibSysId);
        retData.put("TASK_SIGN", param.getString("BUSIFORM_NODE_ID"));
        retData.put("TODO_URL", param.getString("URL"));
        // 1- 电子工单
        retData.put("TASK_TYPE_CODE", "3");
        IDataset reces = new DatasetList();
        IData rece = new DataMap();
        rece.put("RECE_OBJ", param.getString("DEAL_STAFF_ID"));
        reces.add(rece);
        retData.put("RECE_OBJS", reces);

        // 查流程订单表信息
        IDataset eopSubscriberInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibSysId);

        if (DataUtils.isEmpty(eopSubscriberInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibSysId+"找不到TF_B_EOP_SUBSCRIBER表的记录！");
        }

        IDataset eopNodeInfos = WorkformNodeBean.qryWorkformNodeByIbsysidOrderByTime(ibSysId);

        if (DataUtils.isEmpty(eopSubscriberInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibSysId+"找不到TF_B_EOP_NODE表的记录！");
        }
        IData eopSubscriberInfo = eopSubscriberInfos.first();
        retData.put("TASK_LEVEL", eopSubscriberInfo.getString("FLOW_LEVEL"));
        retData.put("TASK_AUTH", eopNodeInfos.first().getString("STAFF_ID"));
        retData.put("PLAN_FINISH_TIME", eopSubscriberInfo.getString("FLOW_EXPECT_TIME"));
        retData.put("GROUP_ID", eopSubscriberInfo.getString("GROUP_ID"));
        retData.put("CUST_NAME", eopSubscriberInfo.getString("CUST_NAME"));
        IData eopProductInfo = WorkformProductBean.qryProductByPk(ibSysId, "0");
        if (DataUtils.isEmpty(eopProductInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibSysId+"找不到TF_B_EOP_PRODUCT表的记录！");
        }
        String productId = eopProductInfo.getString("PRODUCT_ID");
        String productName = eopProductInfo.getString("PRODUCT_NAME");
        retData.put("PRODUCT_ID", productId);
        retData.put("PRODUCT_NAME", productName);
        //调用服务，查询客户服务等级
        IData grpQryData = new DataMap();
        grpQryData.put("GROUP_ID", retData.getString("GROUP_ID"));
        IDataset groupInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", grpQryData);
        if (DataUtils.isEmpty(groupInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过GROUP_ID:"+grpQryData.getString("GROUP_ID")+"找不到集团资料！");
        }
        retData.put("CUST_SERV_LEVEL", groupInfos.first().getString("SERV_LEVEL","4"));
        retData.put("BUSI_TYPE_CODE", "35");

        String nodeDesc = param.getString("NODE_DESC");
        retData.put("TASK_NODE_NAME", nodeDesc);
        String topic = retData.getString("CUST_NAME")+"_"+retData.getString("PRODUCT_NAME")+"_"+retData.getString("TASK_NODE_NAME")+"_"+ibSysId;
        //地址获取
        String address =dealAddress(productId,ibSysId);
        if (StringUtils.isNotEmpty(address))
        {
            topic = topic+"地址："+address;
        }
        retData.put("TASK_TOPIC", topic);
        retData.put("TASK_CONTENT", "您有一笔待办工单：" + topic);

        return retData;
    }

    private String dealAddress(String productId, String ibSysId) throws Exception
    {
        String address = "";
        String provice = "";
        String city = "";
        String area = "";
        String county = "";
        String village = "";
        //判断专线产品取得地址信息
        if ("5080".equals(productId) || "5081".equals(productId) ||
                "5083".equals(productId) || "5093".equals(productId))
        {
            IDataset attrInfos = WorkformAttrBean.qryAttrByIbsysid(ibSysId);
            if (DataUtils.isEmpty(attrInfos))
            {
                return address;
            }
            for (int i = 0; i < attrInfos.size(); i++)
            {

                IData attrInfo = attrInfos.getData(i);
                String recordNum = attrInfo.getString("RECORD_NUM");
                // 一单多线直接跳出循环不计算地址
                if (!StringUtils.equals(recordNum,"1"))
                {
                    city = "";
                    area = "";
                    county = "";
                    village = "";
                    break;
                }
                city = getDataInfo(attrInfo, "pam_cityA");
                if (StringUtils.isEmpty(city))
                {
                    city = getDataInfo(attrInfo, "cityA");
                }
                area = getDataInfo(attrInfo, "pam_areaA");
                if (StringUtils.isEmpty(area))
                {
                    area = getDataInfo(attrInfo, "areaA");
                }
                county = getDataInfo(attrInfo, "pam_countyA");
                if (StringUtils.isEmpty(county))
                {
                    county = getDataInfo(attrInfo, "countyA");
                }
                village = getDataInfo(attrInfo, "pam_villageA");
                if (StringUtils.isEmpty(village))
                {
                    village = getDataInfo(attrInfo, "villageA");
                }
            }
            address = city + area + county + village;
        }
        //判断跨省专线取得地址信息
        else if ("22000510".equals(productId))
        {
            IDataset attrInfos = WorkformAttrBean.qryAttrByIbsysid(ibSysId);
            if (DataUtils.isEmpty(attrInfos))
            {
                return address;
            }
            for (int i = 0; i < attrInfos.size(); i++)
            {
                IData attrInfo = attrInfos.getData(i);
                String recordNum = attrInfo.getString("RECORD_NUM");
                // 一单多线直接跳出循环不计算地址
                if (!StringUtils.equals(recordNum,"1"))
                {
                    provice = "";
                    city = "";
                    area = "";
                    break;
                }
                provice = getDataInfo(attrInfo, "1112053311");
                city = getDataInfo(attrInfo, "1112053305");
                area = getDataInfo(attrInfo, "1112053310");
            }
            if (StringUtils.isEmpty(provice))
            {
                provice = getOptionName(provice,"1112053311");
            }
            if (StringUtils.isEmpty(city))
            {
                city = getOptionName(provice, "1112053305");
            }
            address = provice + city + area;
        }
        return address;
    }

    private String getOptionName(String attrValue,String paramCode) throws Exception
    {
        IDataset datasetInfos = new DatasetList();//MerchInfoQry.getSelectionItem(paramCode);
        for (int i = 0; i < datasetInfos.size(); i++)
        {
            IData dataInfo = datasetInfos.getData(i);
            String optionValue = dataInfo.getString("OPTION_VALUE");
            if (attrValue.equals(optionValue))
            {
                attrValue = dataInfo.getString("OPTION_NAME");
                break;
            }
        }
        return attrValue;
    }

    private String getDataInfo(IData attrInfo, String paramKey)
    {
        String data = "";
        if (StringUtils.equals(paramKey, attrInfo.getString("ATTR_CODE")))
        {
            data = attrInfo.getString("ATTR_VALUE");
        }
        return data;
    }

    public IData getReadTaskDataInfo(IData param) throws Exception
    {
        IData retData = new DataMap();
        String ibSysId = param.getString("BI_SN");
        retData.put("RELA_ID", ibSysId);
        retData.put("INFO_SIGN", param.getString("BUSIFORM_NODE_ID"));
        retData.put("INFO_URL", param.getString("URL"));
        // 1- 电子工单
        retData.put("INFO_TYPE", "4");
        retData.put("INFO_CHILD_TYPE", "41"); //41为待阅
        retData.put("INFO_STATUS", "1"); //处理状态
        retData.put("RECE_OBJS", param.getString("DEAL_STAFF_ID"));
        // 查流程订单表信息
        IDataset eopSubscriberInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibSysId);

        if (DataUtils.isEmpty(eopSubscriberInfos))
        {
        	eopSubscriberInfos  = WorkformSubscribeBean.qryWorkformHSubscribeByIbsysid(ibSysId);
            if(DataUtils.isEmpty(eopSubscriberInfos)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibSysId+"找不到TF_B_EOP_SUBSCRIBER表的记录！");
            }
        }
        //IDataset eweInfo = EweNodeQry.qryEweByBiSn(ibSysId);

      /*  if (DataUtils.isEmpty(eweInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibSysId+"找不到TF_B_EWE表的记录！");
        }*/
        IData eopSubscriberInfo = eopSubscriberInfos.first();
        String name =StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
		    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
		    	{ "URGENCY_LEVEL",eopSubscriberInfo.getString("FLOW_LEVEL")}); //查询对应名字
        retData.put("INFO_LEVEL",name);
        retData.put("INFO_AUTH", param.getString("INFO_AUTH"));
        retData.put("END_TIME", eopSubscriberInfo.getString("FLOW_EXPECT_TIME"));
        retData.put("GROUP_ID", eopSubscriberInfo.getString("GROUP_ID"));
        retData.put("CUST_NAME", eopSubscriberInfo.getString("CUST_NAME"));
        IData eopProductInfo = WorkformProductBean.qryProductByPk(ibSysId, "0");
        if (DataUtils.isEmpty(eopProductInfo))
        {
        	IDataset eopProductInfos = WorkformProductHBean.qryProductByIbsysid(ibSysId);
        	if(DataUtils.isEmpty(eopProductInfos)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibSysId+"找不到TF_B_EOP_PRODUCT表的记录！");
        	}
        	eopProductInfo = eopProductInfos.first();
        }
        String productId = eopProductInfo.getString("PRODUCT_ID");
        String productName = eopProductInfo.getString("PRODUCT_NAME");
        retData.put("PRODUCT_ID", productId);
        retData.put("PRODUCT_NAME", productName);
        //调用服务，查询客户服务等级
        IData grpQryData = new DataMap();
        grpQryData.put("GROUP_ID", retData.getString("GROUP_ID"));
        IDataset groupInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", grpQryData);
        if (DataUtils.isEmpty(groupInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过GROUP_ID:"+grpQryData.getString("GROUP_ID")+"找不到集团资料！");
        }
        retData.put("CUST_SERV_LEVEL", groupInfos.first().getString("SERV_LEVEL","4"));
        String nodeDesc = param.getString("NODE_DESC");
        retData.put("TASK_NODE_NAME", nodeDesc);
        String topic = "您需要处理："+eopSubscriberInfo.getString("RSRV_STR4")+"_"+"专线稽核";
      /*  //地址获取
        String subIbsysid = param.getString("SUB_BI_SN");
        String address =dealAddress(productId,subIbsysid);
        if (StringUtils.isNotEmpty(address))
        {
            topic = topic+"地址："+address;
        }*/
        //根据IbsysId查询产品编码
        IData eweparam = new DataMap();
        eweparam.put("BI_SN", ibSysId);
        IDataset eweInfos = EweNodeQry.qryEweByIbsysid(ibSysId,"0");
        if(IDataUtil.isEmpty(eweInfos)){
        	eweInfos = EweNodeQry.qryEweHByIbsysid(ibSysId,"0");
        	if(DataUtils.isEmpty(eweInfos)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询TF_B_EWE表流程信息为空!");
        	}
        }
        IData eweInfo = eweInfos.first();
        retData.put("INFO_TOPIC", topic);
        retData.put("INFO_CONTENT", "集团编码:"+retData.getString("GROUP_ID")+"</br>集团名称:"+retData.getString("CUST_NAME")+"</br>产品:"+productName+"</br>业务类型:"+param.getString("BUSI_TYPE")+"</br>CRM订单号:"+ibSysId+"</br>当前节点:专线稽核</br>业务创建人:"+eweInfo.getString("ACCEPT_STAFF_ID")+"</br>流程创建时间:"+eweInfo.getString("CREATE_DATE")+"</br>客户经理:"+eweInfo.getString("ACCEPT_STAFF_ID"));
        retData.put("SEND_MONTH", SysDateMgr.getCurMonth()); //月份
        
        return retData;
    }
    
    public IData getReadTaskMinorecDataInfo(IData param) throws Exception
    {
        IData retData = new DataMap();
        String ibSysId = param.getString("BI_SN");
        retData.put("RELA_ID", ibSysId);
        retData.put("INFO_SIGN", param.getString("BUSIFORM_NODE_ID"));
        retData.put("INFO_URL", param.getString("URL"));
        // 1- 电子工单
        retData.put("INFO_TYPE", "4");
        retData.put("INFO_CHILD_TYPE", "41"); //41为待阅
        retData.put("INFO_STATUS", "1"); //处理状态
        retData.put("RECE_OBJS", param.getString("DEAL_STAFF_ID"));
        // 查流程订单表信息
        IDataset eopSubscriberInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibSysId);
        if (DataUtils.isEmpty(eopSubscriberInfos))
        {
            eopSubscriberInfos  = WorkformSubscribeBean.qryWorkformHSubscribeByIbsysid(ibSysId);
            if(DataUtils.isEmpty(eopSubscriberInfos)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibSysId+"找不到TF_B_EOP_SUBSCRIBER表的记录！");
            }
        }
        IData eopSubscriberInfo = eopSubscriberInfos.first();
        String name =StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                { "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
                        { "URGENCY_LEVEL",eopSubscriberInfo.getString("FLOW_LEVEL")}); //查询对应名字
        retData.put("INFO_LEVEL",name);
        retData.put("INFO_AUTH", param.getString("INFO_AUTH"));
        retData.put("END_TIME", eopSubscriberInfo.getString("FLOW_EXPECT_TIME"));
        retData.put("GROUP_ID", eopSubscriberInfo.getString("GROUP_ID"));
        retData.put("CUST_NAME", eopSubscriberInfo.getString("CUST_NAME"));
        String productId = param.getString("PRODUCT_ID");
        String productName = param.getString("PRODUCT_NAME");
        retData.put("PRODUCT_ID", productId);
        retData.put("PRODUCT_NAME", productName);
        //调用服务，查询客户服务等级
        IData grpQryData = new DataMap();
        grpQryData.put("GROUP_ID", retData.getString("GROUP_ID"));
        IDataset groupInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", grpQryData);
        if (DataUtils.isEmpty(groupInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过GROUP_ID:"+grpQryData.getString("GROUP_ID")+"找不到集团资料！");
        }
        retData.put("CUST_SERV_LEVEL", groupInfos.first().getString("SERV_LEVEL","4"));
        String nodeDesc = param.getString("NODE_DESC");
        retData.put("TASK_NODE_NAME", nodeDesc);
        String topic = "您需要处理："+eopSubscriberInfo.getString("RSRV_STR4")+"_"+"中小企业业务稽核";
        //根据IbsysId查询产品编码
        IData eweparam = new DataMap();
        eweparam.put("BI_SN", ibSysId);
        IDataset eweInfos = EweNodeQry.qryEweByIbsysid(ibSysId,"0");
        if(IDataUtil.isEmpty(eweInfos)){
            eweInfos = EweNodeQry.qryEweHByIbsysid(ibSysId,"0");
            if(DataUtils.isEmpty(eweInfos)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询TF_B_EWE表流程信息为空!");
            }
        }
        IData eweInfo = eweInfos.first();
        retData.put("INFO_TOPIC", topic);
        retData.put("INFO_CONTENT", "集团编码:"+retData.getString("GROUP_ID")+"</br>集团名称:"+retData.getString("CUST_NAME")+"</br>产品:"+productName+"</br>业务类型:"+param.getString("BUSI_TYPE")+"</br>CRM订单号:"+ibSysId+"</br>当前节点:中小企业业务稽核</br>业务创建人:"+eweInfo.getString("ACCEPT_STAFF_ID")+"</br>流程创建时间:"+eweInfo.getString("CREATE_DATE")+"</br>客户经理:"+eweInfo.getString("ACCEPT_STAFF_ID"));
        retData.put("SEND_MONTH", SysDateMgr.getCurMonth()); //月份
        return retData;
    }
}
