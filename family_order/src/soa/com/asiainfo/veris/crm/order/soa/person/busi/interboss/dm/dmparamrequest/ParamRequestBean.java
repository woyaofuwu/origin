
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmparamrequest;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dminfogather.InfoGatherBean;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel.DMBusiMgr;

public class ParamRequestBean extends CSBizBean
{

    static transient final Logger logger = Logger.getLogger(ParamRequestBean.class);

    /**
     * 功能: 可采集或可配置业务列表查询
     * 
     * @data 2013-8-14
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getBusiList(IData inparam) throws Exception
    {

        // 可采集或可配置业务列表查询 在 【信息采集已用到】 直接调用
        return new InfoGatherBean().getBusiList(inparam);
    }

    /**
     * @data 2013-8-14
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getFactoryList(IData inparam) throws Exception
    {

        String provCode = getProvCode();
        IData data = IBossCall.queryParamRequestFactoryListIBOSS(provCode);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP1A110_T1000110_0_0)---返回数据-------" + data);
        // IData data0 = (IData)HttpHelper.callHttpSvc(pd,"ITF_IBOQ_DMDEAL",param,true);

        if (IDataUtil.isNull(data) || !data.getString("RESULTSTATUS", "").equalsIgnoreCase("0"))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_146);
        }
        else if (data.getString("X_RSPTYPE", "").equalsIgnoreCase("0") && data.getString("RESULTSTATUS", "").equalsIgnoreCase("1"))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_1, data.getString("FAILTYPE", "") + ":" + data.getString("FAILREASON", ""));
        }

        /************** IBOSS数据转换 start *******************/
        IDataset manufacturerNames = null;
        IDataset manufacturerIds = null;
        // String 类型 例如：{MANUFACTURERID =["001"]}
        // IData data = new DataMap();
        // IDataset dataset1 = new DatasetList(); IDataset dataset2 = new DatasetList();
        // dataset1.add(0, "0"); dataset1.add(1, "1"); dataset2.add(0, "fuctionname0"); dataset2.add(1, "fuctionname1");
        // data.put("MANUFACTURERID", dataset1); data.put("MANUFACTURERNAME", dataset2);
        //        

        if (data.get("MANUFACTURERID") instanceof String)
        {

            manufacturerNames = new DatasetList("[" + data.get("MANUFACTURERNAME") + "]");
            manufacturerIds = new DatasetList("[" + data.get("MANUFACTURERID") + "]");
        }
        // JSONArray类型 例如：{MANUFACTURERNAME=["001","002"]}
        else
        {

            manufacturerNames = data.getDataset("MANUFACTURERNAME");
            manufacturerIds = data.getDataset("MANUFACTURERID");
        }
        /************** IBOSS数据转换 end *******************/

        IDataset dataset = new DatasetList();
        if (IDataUtil.isNotEmpty(manufacturerNames))
        {
            if (manufacturerNames.size() != manufacturerIds.size())
            {
                CSAppException.apperr(DMBusiException.CRM_DM_103);
            }
            for (int i = 0; i < manufacturerNames.size(); i++)
            {
                IData temp = new DataMap();
                temp.put("MANUFACTURERID", manufacturerIds.get(i));
                temp.put("MANUFACTURERNAME", manufacturerNames.get(i));
                dataset.add(temp);
            }
        }

        return dataset;
    }

    /**
     * 生成流水号
     * 
     * @param provCode
     * @return
     * @throws Exception
     */
    public String getOperateId(String provCode) throws Exception
    {
        String corpBiz = SeqMgr.getCorpBizCode();

        if (StringUtils.isBlank(corpBiz))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_144);
        }

        String seq = "";

        corpBiz = "000" + corpBiz;

        seq = corpBiz.substring(corpBiz.length() - 3, corpBiz.length());

        return SysDateMgr.getSysDate("yyyyMMddHHmmss") + "CSVD" + provCode + seq;
    }

    /**
     * 获取省代码
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public String getProvCode() throws Exception
    {
        String provInfo = StaticInfoQry.qryProvCode(getVisit().getProvinceCode());

        if (StringUtils.isBlank(provInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_310);
        }

        return provInfo;
    }

    public IDataset getTermByHttp(IData inparam) throws Exception
    {

        String provCode = getProvCode();
        String manufactureId = inparam.getString("MANUFACTUREID", "");
        IData data = IBossCall.queryParamRequestTermByHttpIBOSS(provCode, manufactureId);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP1A111_T1000111_0_0)---返回数据-------" + data);
        // IData data0 = (IData)HttpHelper.callHttpSvc(pd,"ITF_IBOQ_DMDEAL",param,true);

        if (IDataUtil.isNull(data) || !data.getString("RESULTSTATUS", "").equalsIgnoreCase("0"))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_146);
        }
        else if (data.getString("X_RSPTYPE", "").equalsIgnoreCase("0") && data.getString("RESULTSTATUS", "").equalsIgnoreCase("1"))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_1, data.getString("FAILTYPE", "") + ":" + data.getString("FAILREASON", ""));
        }

        /************** IBOSS数据转换 start *******************/
        IDataset terminalIds = new DatasetList();
        IDataset terminalNames = new DatasetList();
        IDataset terminalusableFuncs = new DatasetList();
        // String 类型 例如：{TERMINALID =["001"]}
        // IData data = new DataMap();
        // IDataset dataset1 = new DatasetList(); IDataset dataset2 = new DatasetList(); IDataset dataset3 = new
        // DatasetList();
        // dataset1.add(0, "0"); dataset1.add(1, "1"); dataset2.add(0, "edddd"); dataset2.add(1, "wwww");
        // dataset3.add(0, "ee\\ee\\www\\wwe"); dataset3.add(1, "ee\\ee");
        // data.put("TERMINALID", dataset1); data.put("TERMINALNAME", dataset2); data.put("TERMINALUSABLEFUNC",
        // dataset3);

        if (data.get("TERMINALID") instanceof String)
        {

            terminalIds.add(data.get("TERMINALID"));
            terminalNames.add(data.get("TERMINALNAME"));
            terminalusableFuncs.add(data.get("TERMINALUSABLEFUNC"));
        }
        // JSONArray类型 例如：{TERMINALID=["001","002"]}
        else
        {

            terminalIds = data.getDataset("TERMINALID");
            terminalNames = data.getDataset("TERMINALNAME");
            terminalusableFuncs = data.getDataset("TERMINALUSABLEFUNC");
        }
        /************** IBOSS数据转换 end *******************/

        IDataset dataset = new DatasetList();

        if (IDataUtil.isNotEmpty(terminalIds))
        {
            if (terminalIds.size() != terminalNames.size())
            {
                CSAppException.apperr(DMBusiException.CRM_DM_103);
            }
            for (int i = 0; i < terminalIds.size(); i++)
            {
                IData temp = new DataMap();
                temp.put("TERMINALID", terminalIds.get(i));
                temp.put("TERMINALNAME", terminalNames.get(i));
                String termFunc = terminalusableFuncs.get(i).toString();
                termFunc = termFunc.replaceAll("\"", "");
                temp.put("TERMINALUSABLEFUNC", termFunc);
                dataset.add(temp);
            }
        }
        return dataset;

    }

    /**
     * 功能: 发送参数配置请求
     * 
     * @data 2013-8-14
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData sendHttpGather(IData inparam) throws Exception
    {

        String funcids = inparam.getString("FUNCIDS");
        String phone = inparam.getString("PHONE");
        String termStyle = inparam.getString("TERM_STYLE");
        String provCode = getProvCode();
        String operId = getOperateId(provCode);

        IData data = IBossCall.sendParamRequestIBOSS(phone, funcids, provCode, operId, termStyle);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP2C024_T2002024_0_0)---返回数据-------" + data);
        // Object data = HttpHelper.callHttpSvc(pd,"ITF_IBOQ_DMDEAL",param,true);
        if (IDataUtil.isNull(data))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_146);
            return null;
        }
        else
        {
            if (data.getString("X_RSPTYPE", "2").equalsIgnoreCase("0"))
            {
                IData retrundata = new DataMap();
                retrundata.put("OPERATEID", operId);
                retrundata.put("FUNCTIONID", funcids.replaceAll("\"", "").replaceAll(" ", "").replaceAll(",", "-"));
                retrundata.put("TERMINALID", inparam.getString("TERM_STYLE", ""));
                return retrundata;
            }
            else
            {
                CSAppException.apperr(DMBusiException.CRM_DM_146);
                return null;
            }
        }

    }

    /**
     * 登记日志
     * 
     * @data 2013-8-14
     * @param condition
     * @return
     * @throws Exception
     */
    public IDataset sendTuxGather(IData condition) throws Exception
    {

        DMBusiMgr dBusiMgr = new DMBusiMgr();
        String functionId = condition.getString("comminfo_FUNCTIONID", "");
        String[] functions = functionId.split("-");
        IDataset dataset = new DatasetList();
        IDataset functionIds = new DatasetList();
        // 组装数据结构 将FUNCTIONID封装到IDataset
        for (int i = 0; i < functions.length; i++)
        {
            IData functionIdData = new DataMap();
            functionIdData.put("FUNCTIONID", functions[i]);
            functionIds.add(functionIdData);
        }
        IData param = new DataMap();
        param.put("PHONENUM", condition.getString("comminfo_SERIAL_NUMBER", ""));
        param.put("OPERATEID", condition.getString("comminfo_OPERATEID", ""));
        param.put("TERMINALID", condition.getString("comminfo_TERMINALID", ""));
        param.put("FUNCTIONID", functionIds);
        param.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());
        param.put("APPLY_TYPE", "13");
        param.put("PROV_CODE", getProvCode());
        IData tempData = dBusiMgr.DMInsertData(param);
        dataset.add(tempData);
        return dataset;
    }

}
