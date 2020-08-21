
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.hiservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryHIServiceQry;

public class QueryHIServiceBean extends CSBizBean
{
    protected static Logger log = Logger.getLogger(QueryHIServiceBean.class);

    /**
     * 获取参数转换的String <BR>
     * 
     * @param infos
     * @return
     * @throws Exception
     */
    public static String changedFromParams(IDataset infos) throws Exception
    {
        IData rtnInfo = null;
        if (IDataUtil.isEmpty(infos))
        {
            return null;
        }
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (int i = 0; i < infos.size(); i++)
        {
            IData info = infos.getData(i);
            String param_code = info.getString("PARAM_CODE");
            String param_value = info.getString("PARAM_VALUE");
            String service_id = info.getString("SERVICE_ID");

            if (StringUtils.isEmpty(param_code) || StringUtils.isEmpty(param_value) || StringUtils.isEmpty(service_id))
            {
                continue;
            }
            List<String> list = map.get(param_code);
            if (null == list)
            {
                list = new ArrayList<String>();
            }

            list.add(service_id + "," + param_value);
            map.put(param_code, list);
        }

        Iterator<Entry<String, List<String>>> it = map.entrySet().iterator();
        while (it.hasNext())
        {
            if (null == rtnInfo)
            {
                rtnInfo = new DataMap();
            }
            Entry<String, List<String>> entry = it.next();

            String param_code = entry.getKey();

            String param_value = null;

            List<String> valueList = entry.getValue();
            // List<String> keyList = Arrays.asList(valueMap.keySet().toArray(new String[valueMap.keySet().size()]));
            // Collections.sort(keyList);
            for (String value : valueList)
            {
                if (null == param_value)
                {
                    param_value = value;
                    continue;
                }
                param_value += (";" + value);
            }
            rtnInfo.put(param_code, param_value);
        }
        if (IDataUtil.isEmpty(rtnInfo))
        {
            return null;
        }
        return rtnInfo.toString();
    }

    /**
     * 向指定的IDATA中加入参数信息 <BR>
     * 
     * @param busiParamInfo
     * @param param_code
     * @param param_value
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void addBusiParamInfo(IDataset targetBusiParamInfos, String param_code, String param_value, String service_id) throws Exception
    {
        IData busiParamInfo = new DataMap();
        busiParamInfo.put("PARAM_CODE", param_code);
        busiParamInfo.put("PARAM_VALUE", param_value);
        busiParamInfo.put("SERVICE_ID", service_id);
        targetBusiParamInfos.add(busiParamInfo);
    }

    /**
     * 拼服务开通订单接口表
     * 
     * @throws Exception
     */
    public void addCrmOrderInfor(Object data, IData datas) throws Exception
    {
        addData("X_TI_C_ORDER", data, datas);
    }

    /**
     * 拼服务开通参数接口表
     * 
     * @throws Exception
     */
    public void addCrmParam(Object data, IData datas) throws Exception
    {
        addData("X_TI_C_PARAM", data, datas);
    }

    /**
     * 拼开通订单接口表
     * 
     * @throws Exception
     */
    public void addCrmSubcriber(Object data, IData datas) throws Exception
    {
        addData("X_TI_C_SUBSCRIBER", data, datas);
    }

    /**
     * 拼开通资源订单接口表
     * 
     * @throws Exception
     */
    public void addCrmTradeRes(Object data, IData datas) throws Exception
    {
        addData("X_TI_C_RESOURCE", data, datas);
    }

    /**
     * 拼开通服务订单接口表
     * 
     * @throws Exception
     */
    public void addCrmTradeSvc(Object data, IData datas) throws Exception
    {
        addData("X_TI_C_SVC", data, datas);
    }

    /**
     * 拼开通服务属性订单接口表
     * 
     * @throws Exception
     */
    public void addCrmTradeSvcAttr(Object data, IData datas) throws Exception
    {
        addData("X_TI_C_SVC_ATTR", data, datas);
    }

    /**
     * 拼开通服务状态订单接口表
     * 
     * @throws Exception
     */
    public void addCrmTradeSvcState(Object data, IData datas) throws Exception
    {
        addData("X_TI_C_SVCSTATE", data, datas);
    }

    /**
     * 拼服务开通用户服务接口表
     * 
     * @throws Exception
     */
    public void addCrmUserSvc(Object data, IData datas) throws Exception
    {
        addData("X_TI_F_USER_SVC", data, datas);
    }

    /**
     * 新增需要登记的数据
     * 
     * @param key
     *            与服务开通平台约定拼串关键字，如：X_TI_C_ORDER是开通订单接口表
     * @param value
     *            IData或IDataset数据
     * @throws Exception
     */
    private void addData(String key, Object value, IData datas) throws Exception
    {
        IDataset valueset = new DatasetList();
        if (value instanceof IData)
        {
            valueset.add((IData) value);
        }
        else if (value instanceof IDataset)
        {
            IDataset dataset = (IDataset) value;
            for (int row = 0; row < dataset.size(); row++)
            {
                valueset.add((IData) dataset.get(row));
            }
        }
        IDataset infoset = (IDataset) datas.get(key);
        if (infoset == null)
        {
            datas.put(key, valueset);
        }
        else
        {
            for (int row = 0; row < valueset.size(); row++)
                infoset.add(valueset.get(row));
        }
    }

    /**
     * 根据传入的DataMap 分解各个服务开通接口表拼串
     * 
     * @param data
     *            各个服务开通接口表拼串
     * @param Key
     *            服务开通接口表名
     * @author lism
     * @throws Exception
     */
    public IData createTrade(IData dataMap) throws Exception
    {
        Iterator it = dataMap.entrySet().iterator();
        IData datas = new DataMap();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            Object oTradeInfos = entry.getValue();
            if ("TI_C_ORDER".equals(key)) // 客户产品订单
                addCrmOrderInfor(oTradeInfos, datas);
            else if ("TI_C_SUBSCRIBER".equals(key)) // 订单
                addCrmSubcriber(oTradeInfos, datas);
            else if ("TI_C_SVC".equals(key)) // 服务订单
                addCrmTradeSvc(oTradeInfos, datas);
            else if ("TI_C_SVC_ATTR".equals(key)) // 服务属性订单
                addCrmTradeSvcAttr(oTradeInfos, datas);
            else if ("TI_C_SVCSTATE".equals(key)) // 服务状态订单
                addCrmTradeSvcState(oTradeInfos, datas);
            else if ("TI_C_RESOURCE".equals(key)) // 资源订单
                addCrmTradeRes(oTradeInfos, datas);
            else if ("TI_C_PARAM".equals(key)) // 参数
                addCrmParam(oTradeInfos, datas);
            else if ("TI_F_USER_SVC".equals(key)) // 用户服务
                addCrmUserSvc(oTradeInfos, datas);

        }
        return datas;
    }

    /**
     * 为业务参数赋值 <BR>
     * 
     * @param subscriberInfos
     * @param sourceBusiParamInfos
     * @return
     * @throws Exception
     */
    public IDataset evaluateparam(IDataset sourceBusiParamInfos, IData data) throws Exception
    {
        IDataset targetBusiParamInfos = new DatasetList();
        Iterator it = sourceBusiParamInfos.iterator();
        while (it.hasNext())
        {
            IData sourceBusiParamInfo = (IData) it.next();

            String paramCode = sourceBusiParamInfo.getString("PARAM_CODE");
            if (StringUtils.isEmpty(paramCode))
            {
                continue;
            }
            // String must_tag = sourceBusiParamInfo.getString(TDOSubscriberSvcVarConstant.MUST_TAG);

            if ("S000G004".equals(paramCode))
            {
                sourceBusiParamInfo.put("PARAM_VALUE", data.getString("SERIAL_NUMBER", ""));
            }
            else if ("S000G002".equals(paramCode))
            {
                IDataset userResInfos = QueryHIServiceQry.getUserResInfos(data.getString("USER_ID", ""));
                // 需要在此处加一个是否为空的判断
                // EmptyCheckUtils.emptyException(userResInfos, MessageFormat.format("user_id({0})，userResInfos",
                // this.user_id), this.getClass(), new Exception(), this.engine, 1);
                IData userResInfo = userResInfos.getData(0);

                sourceBusiParamInfo.put("PARAM_VALUE", userResInfo.get("IMSI"));
            }
            else if ("S000G004".equals(paramCode))
            {// 东信VPMN查询
                sourceBusiParamInfo.put("PARAM_VALUE", data.getString("SERIAL_NUMBER", ""));
            }
            else if ("S000G115".equals(paramCode))
            {// HLR状态查询
                sourceBusiParamInfo.put("PARAM_VALUE", "121");
            }
            else if ("S000G101".equals(paramCode))
            {// 华为VPMN查询
                IDataset userVpmnInfos = QueryHIServiceQry.getUserVPMNInfos(data.getString("USER_ID", ""));
                if (IDataUtil.isEmpty(userVpmnInfos))
                {
                    continue;
                    // EmptyCheckUtils.emptyException(userVpmnInfos, MessageFormat.format("user_id({0})，userResInfos",
                    // this.user_id), this.getClass(), new Exception(), this.engine, 1);
                }

                IData userVpmnInfo = userVpmnInfos.getData(0);
                sourceBusiParamInfo.put("PARAM_VALUE", userVpmnInfo.get("VPMN_ID"));
            }
            else if ("S000G188".equals(paramCode))
            {// IP直通车信息查询
                sourceBusiParamInfo.put("PARAM_VALUE", data.getString("SERIAL_NUMBER", ""));
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_783, "未知的查询参数" + paramCode + "！");
            }

            this.addBusiParamInfo(targetBusiParamInfos, paramCode, sourceBusiParamInfo.getString("PARAM_VALUE"), data.getString("SERVICE_ID", ""));
        }
        // 增加swichid、SwitchTypeCode参数
        this.addBusiParamInfo(targetBusiParamInfos, "SWITCH_ID", data.getString("SWITCH_ID", ""), "-1"); // this.guardAngelParams.switch_id
        this.addBusiParamInfo(targetBusiParamInfos, "SWITCH_TYPE_CODE", data.getString("SWITCH_TYPE_CODE", ""), "-1");
        return targetBusiParamInfos;
    }

    public IData getdata(IData data) throws Exception
    {
        IData subscriberInfo = new DataMap();
        IDataset nodata = new DatasetList();
        Date currTime = new Date();

        IData data1 = new DataMap();
        data1.put("SUBSCRIBE_ID", data.getString("TRADE_ID", ""));// trade_id
        data1.put("CANCEL_SUBSCRIBE_ID", "0");//	
        data1.put("SUBSCRIBE_TYPE", "602");// TRADE_TYPE_CODE
        data1.put("NET_TYPE_CODE", "00");// NET_TYPE_CODE
        data1.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));// PRODUCT_ID
        data1.put("BRAND_CODE", data.getString("BRAND_CODE", ""));// BRAND_CODE
        data1.put("IN_MODE_CODE", "0");// IN_MODE_CODE
        data1.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", ""));// EPARCHY_CODE
        data1.put("CANCEL_TAG", "0");// CANCEL_TAG {
        data1.put("USER_ID", data.getString("USER_ID", ""));// RSRV_STR4
        data1.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));// SERIEL_NUMBER
        data1.put("ORDER_ID", "0");// ORDER_ID
        data1.put("PRIORITY", "290");// PRIORITY

        data1.put("BATCH_ID", "0");// BATCH_ID
        data1.put("BATCH_COUNT", "0");// BATCH_COUNT
        data1.put("REFER_TIME", SysDateMgr.date2String(currTime, "yyyy-MM-dd HH:mm:ss"));// ACCEPT_DATE
        data1.put("REFER_DEPART_ID", "pf");// TRADE_DEPART_ID
        data1.put("REPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", ""));// TRADE_EPARCHY_CODE
        data1.put("REFER_STAFF_ID", "pf");// TRADE_STAFF_ID
        data1.put("EXEC_TIME", SysDateMgr.date2String(currTime, "yyyy-MM-dd HH:mm:ss"));// EXEC_TIME
        data1.put("EXEC_MONTH", SysDateMgr.date2String(currTime, "MM"));// ACCEPT_MONTH
        data1.put("DBSRC", data.getString("DB_SOURCE", "")); // old one is this.inparams.getDb_source()
        data1.put("BIZ_TAG", "00");
        data1.put("OPEN_MODE", "0");
        data1.put("RESP_MODE", "1");
        // 交易方向
        data1.put("BUSI_DIR", "0");
        subscriberInfo.put("TI_C_SUBSCRIBER", data1);// 获取订单

        subscriberInfo.put("TI_C_ORDER", nodata);// 获取客户订单
        subscriberInfo.put("TI_C_SVC_ATTR", nodata);
        subscriberInfo.put("TI_C_SVCSTATE", nodata);
        subscriberInfo.put("TI_C_RESOURCE", nodata);
        subscriberInfo.put("TI_F_USER_SVC", nodata);

        // 获取服务订单
        // 获取服务订单的服务标识 联指查询类业务只需要拼订单、服务、参数即可
        IDataset svcInfos = new DatasetList();
        IDataset busiInfos = new DatasetList();
        IData busiInfo = new DataMap();
        IDataset busiParamInfos = new DatasetList();
        IData svcInfo = new DataMap();
        String service_id = "77700002";// HLR查询指令
        svcInfo.put("SUBSCRIBER_ID", data.getString("TRADE_ID", ""));
        svcInfo.put("EXEC_MONTH", data.getString("TRADE_EPARCHY_CODE", "").substring(4, 6));
        svcInfo.put("SVC_ID", service_id);
        svcInfo.put("MODIFY_TAG", "0");
        svcInfo.put("START_TIME", SysDateMgr.date2String(currTime, "yyyy-MM-dd HH:mm:ss"));
        svcInfo.put("END_TIME", SysDateMgr.END_DATE_FOREVER);
        svcInfos.add(svcInfo);

        busiParamInfos = this.qureypramas(service_id, data);

        subscriberInfo.put("TI_C_SVC", svcInfos);
        String str = changedFromParams(busiParamInfos);
        subscriberInfo.put("TI_C_PARAM", new DataMap(str));

        // 等全部数据准备完成后开始一次性分解拼串处理
        IData rtnSubscriberInfo = createTrade(subscriberInfo);
        return rtnSubscriberInfo;
    }

    /**
     * 功能：用于查询交换机状态 作者：GongGuang
     */
    public IDataset queryHIService(IData data, Pagination page) throws Exception
    {
        String serialNum = data.getString("SERIAL_NUMBER", "");
        String eparchyCode = CSBizBean.getUserEparchyCode();
        String service_id = data.getString("SERVICE_ID", "");
        // 组织crm侧调服务开通的数据
        IData rtnInfo = new DataMap();
        IDataset rtnlistInfo = new DatasetList();
        IData subscriberInfo = getdata(data);

        // 参数数据为空则返回-2，表示该工单不用做服务开通
        if ((subscriberInfo != null) && (subscriberInfo.size() <= 0))
        {
            rtnInfo.put("PARA_CODE1", "");
            rtnInfo.put("PARA_CODE2", "");
            rtnInfo.put("X_RESULTCODE", -2);
            rtnInfo.put("X_RESULTINFO", "参数数据为空则返回-2，表示该工单不用做服务查询。");
            rtnInfo.put("X_RSPTYPE", "2");
            rtnInfo.put("X_RSPCODE", "2998");
            rtnlistInfo.add(rtnInfo);
            return rtnlistInfo;
        }

        IData crmData = new DataMap();
        crmData.put("0", subscriberInfo);
        rtnlistInfo = QueryHIServiceQry.queryHIServiceBySN(serialNum, eparchyCode, page, service_id); // 实际上是调用服务开通的webservice接口
        if ((rtnlistInfo.size()) < 0)
        {
            return new DatasetList();
        }
        IDataset subtemp = (DatasetList) subscriberInfo.get("X_TI_C_SUBSCRIBER");
        String subscriberid = subtemp.getData(0).getString("SUBSCRIBE_ID");
        // 等待20秒
        String rtnStr = null;
        int count = 0;
        while (count < 5)
        {
            Thread.sleep(3000);
            long subscriber_id = Long.parseLong(subscriberid);
            rtnStr = null;// this.fireSubscriberQuery(subscriber_id); //调用HLR指令服务查询WS

            IData resmap = new DataMap(rtnStr);
            if (!resmap.isEmpty())
            {
                break;
            }
            count++;
        }
        IData resmap = new DataMap(rtnStr);

        if (resmap.isEmpty())
        {
            return new DatasetList();
        }
        else
        {

            Iterator it = resmap.entrySet().iterator();

            for (int i = 0; it.hasNext(); i++)
            {
                it.next();
                IData tempmap = resmap.getData(new Integer(i).toString());
                IData tempdata = new DataMap();

                tempdata.put("PARA_CODE1", tempmap.get("PARAM_CODE"));
                tempdata.put("PARA_CODE2", tempmap.get("PARAM_VALUE"));
                rtnlistInfo.add(tempdata);
            }
        }
        // 以下是进行数据转换。
        if (IDataUtil.isNotEmpty(rtnlistInfo))
        {
            IData newData;
            for (int i = 0; i < rtnlistInfo.size(); i++)
            {
                newData = rtnlistInfo.getData(i);
                // 对参数值进行转换
                if ("1".equals(newData.getString("PARA_CODE2")))
                    newData.put("PARA_CODE2_CN", "开通");
                else if ("0".equals(newData.getString("PARA_CODE2")))
                    newData.put("PARA_CODE2_CN", "未开通");
                else
                    newData.put("PARA_CODE2_CN", newData.getString("PARA_CODE2", ""));

                // 对参数名称进行转换
                if ("MPHONECODE".equals(newData.getString("PARA_CODE1")))
                    newData.put("PARA_CODE1_CN", "手机号码");
                else if ("IMSI".equals(newData.getString("PARA_CODE1")))
                    newData.put("PARA_CODE1_CN", "IMSI");
                else if ("SRBT".equals(newData.getString("PARA_CODE1")))
                    newData.put("PARA_CODE1_CN", "彩铃");
                else if ("INT".equals(newData.getString("PARA_CODE1")))
                    newData.put("PARA_CODE1_CN", "国际及港澳台长途");
                else if ("NAT".equals(newData.getString("PARA_CODE1")))
                    newData.put("PARA_CODE1_CN", "国内长途");
                else if ("OCCF".equals(newData.getString("PARA_CODE1")))
                    newData.put("PARA_CODE1_CN", "全时通");
                else if ("SERVICE AREA NAT".equals(newData.getString("PARA_CODE1")))
                    newData.put("PARA_CODE1_CN", "国内漫游");
                else if ("SERVICE AREA INT".equals(newData.getString("PARA_CODE1")))
                    newData.put("PARA_CODE1_CN", "国际及港澳台漫游");
                else
                    newData.put("PARA_CODE1_CN", newData.getString("PARA_CODE1"));
            }

        }
        if (IDataUtil.isEmpty(rtnlistInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_783, "依据号码找不到交换机状态！");
        }
        return rtnlistInfo;
    }

    public IDataset qureypramas(String service_id, IData data) throws Exception
    {
        IDataset svcBusiParamInfos = new DatasetList();
        IDataset infos = QueryHIServiceQry.getSubscriberSvcVarBySvcId(service_id);
        if (infos.size() > 0)
        {
            for (int i = 0; i < infos.size(); i++)
            {
                IData queryInfo = (IData) infos.get(i);
                IData svcBusiParamInfo = new DataMap();
                String param_code = queryInfo.getString("PARAM_CODE");
                if (StringUtils.isEmpty(param_code))
                {
                    continue;
                }
                svcBusiParamInfo.put("PARAM_CODE", param_code);
                svcBusiParamInfo.put("PARAM_VALUE", queryInfo.get("PARAM_VALUE"));
                String must_tag = queryInfo.getString("MUST_TAG");
                if (StringUtils.isEmpty(must_tag))
                {
                    must_tag = "0";
                }
                svcBusiParamInfo.put("MUST_TAG", must_tag);
                svcBusiParamInfos.add(svcBusiParamInfo);
            }
        }
        return evaluateparam(svcBusiParamInfos, data);
    }

}
