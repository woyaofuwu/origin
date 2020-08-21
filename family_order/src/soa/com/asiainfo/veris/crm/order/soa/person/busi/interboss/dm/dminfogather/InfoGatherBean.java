
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dminfogather;

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
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel.DMBusiMgr;

public class InfoGatherBean extends CSBizBean
{

    static final Logger logger = Logger.getLogger(InfoGatherBean.class);

    /**
     * 功能: 可采集或可配置业务列表查询
     * 
     * @data 2013-8-10
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getBusiList(IData inparam) throws Exception
    {

        String userEparchyCode = inparam.getString("EPARCHY_CODE");

        // 异地用户不能办理
        if (!CSBizBean.getTradeEparchyCode().equals(userEparchyCode))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_120);
        }

        String provCode = getProvCode();

        IData data = IBossCall.queryInfoGatherIBOSS(provCode);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP1A112_T1000112_0_0)---返回数据-------" + data);
        // IData data0 = (IData)HttpHelper.callHttpSvc(pd,"ITF_IBOQ_DMDEAL",param,true);

        // IData data = new DataMap(); IDataset dataset1 = new DatasetList(); IDataset dataset2 = new DatasetList();
        // dataset1.add(0, "0"); dataset1.add(1, "1"); dataset2.add(0, "fuctionname0"); dataset2.add(1, "fuctionname1");
        // data.put("FUNCTIONID", dataset1); data.put("FUNCTIONNAME", dataset2); data.put("X_RSPTYPE", "0");

        if (IDataUtil.isEmpty(data) || !data.getString("X_RSPTYPE", "").equalsIgnoreCase("0"))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_146);
        }
        else if (data.getString("X_RSPTYPE", "").equalsIgnoreCase("0") && data.getString("RESULTSTATUS", "").equalsIgnoreCase("1"))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_1, data.getString("FAILTYPE", "") + ":" + data.getString("FAILREASON", ""));
        }

        /************** IBOSS数据转换 start *******************/
        IDataset functionIds = new DatasetList();
        IDataset functionNames = new DatasetList();
        // String 类型 例如：{FUNCTIONID =["001"]}
        if (data.get("FUNCTIONID") instanceof String)
        {

            functionIds.add(data.get("FUNCTIONID"));
            functionNames.add(data.get("FUNCTIONNAME"));
        }
        // JSONArray类型 例如：{FUNCTIONNAME=["001","002"]}
        else
        {

            functionIds = data.getDataset("FUNCTIONID");
            functionNames = data.getDataset("FUNCTIONNAME");
        }
        /************** IBOSS数据转换 end *******************/

        IDataset dataset = new DatasetList();
        if (IDataUtil.isNotEmpty(functionNames))
        {
            if (functionNames.size() != functionIds.size())
            {
                CSAppException.apperr(DMBusiException.CRM_DM_103);
            }

            for (int i = 0; i < functionNames.size(); i++)
            {
                IData temp = new DataMap();
                temp.put("FUNCTIONID", functionIds.get(i));
                temp.put("FUNCTIONNAME", functionNames.get(i));
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

    /**
     * 发送信息收集请求
     * 
     * @data 2013-8-10
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData sendHttpGather(IData inparam) throws Exception
    {

        String funcids = inparam.getString("FUNCIDS");
        String phone = inparam.getString("PHONE");
        String provCode = getProvCode();
        String operId = getOperateId(provCode);

        IData data = IBossCall.sendInfoGatherIBOSS(phone, funcids, provCode, operId);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP2C022_T2002022_0_0)---返回数据-------" + data);
        // Object data = HttpHelper.callHttpSvc(pd,"ITF_IBOQ_DMDEAL",param,true);
        if (IDataUtil.isEmpty(data))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_1, "发送平台指令失败，请稍候再试！");
            return null;
        }
        else
        {
            if (data.getString("X_RSPTYPE", "2").equalsIgnoreCase("0"))
            {
                IData retrundata = new DataMap();
                retrundata.put("OPERATEID", operId);
                retrundata.put("FUNCTIONID", funcids.replaceAll("\"", "").replaceAll(" ", "").replaceAll(",", "-"));
                return retrundata;
            }
            else
            {
                CSAppException.apperr(DMBusiException.CRM_DM_1, "发送平台指令失败，请稍候再试！");
                return null;
            }
        }

    }

    /**
     * @data 2013-8-10
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
        param.put("PHONENUM", condition.getString("AUTH_SERIAL_NUMBER", ""));
        param.put("OPERATEID", condition.getString("comminfo_OPERATEID", ""));
        param.put("FUNCTIONID", functionIds);
        param.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());
        param.put("APPLY_TYPE", "12");//
        param.put("PROV_CODE", getProvCode());
        IData tempData = dBusiMgr.DMInsertData(param);
        dataset.add(tempData);
        return dataset;
    }

}
