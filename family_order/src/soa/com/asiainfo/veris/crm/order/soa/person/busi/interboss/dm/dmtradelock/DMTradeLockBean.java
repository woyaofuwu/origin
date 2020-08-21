
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradelock;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel.DMBusiMgr;

public class DMTradeLockBean extends CSBizBean
{
    static transient final Logger logger = Logger.getLogger(DMTradeLockBean.class);

    /**
     * DM业务办理调用流程：ITF_IBOT_INSDMDEAL
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset callInsDMDeal(IData data) throws Exception
    {
        IData param = getParam(data, 2);
        // return TuxedoHelper.callTuxSvc(pd, "ITF_IBOT_INSDMDEAL", param);
        DMBusiMgr dBusiMgr = new DMBusiMgr();

        IData tempData = dBusiMgr.DMInsertData(param);

        if (logger.isDebugEnabled())
            logger.debug("---------------callInsDMDeal---------------" + tempData.toString());

        IDataset dataset = new DatasetList();

        dataset.add(tempData);

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

        if (StringUtils.isBlank(corpBiz) || StringUtils.isEmpty(corpBiz))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_144);
        }

        String seq = "";

        corpBiz = "000" + corpBiz;

        seq = corpBiz.substring(corpBiz.length() - 3, corpBiz.length());

        return SysDateMgr.getSysDate("yyyyMMddHHmmss") + "CSVD" + provCode + seq;
    }

    /**
     * 获取入参
     * 
     * @param param
     * @param tag
     * @return
     * @throws Exception
     */
    public IData getParam(IData param, int tag) throws Exception
    {
        IData data = new DataMap();

        data.put("IN_MODE_CODE", param.getString("IN_MODE_CODE", "0")); // 0——营业厅
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", getVisit().getDepartId());
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        data.put("TRADE_DEPART_PASSWD", "");
        data.put("KIND_ID", param.getString("KIND_ID"));
        data.put("IMEINUM", "IMEI:" + param.getString("IMEINUM"));
        data.put("ACCOUNTNUM", "刘勇");
        data.put("PROV_CODE", param.getString("PROVINCE_CODE"));
        data.put("OPERATEID", param.getString("OPERATEID"));
        // 发报文
        if (tag == 1)
        {
            data.put("PROVINCE_CODE", getVisit().getProvinceCode());
            data.put("ROUTETYPE", "00");
            data.put("ROUTEVALUE", "000");
            data.put("X_RSPTYPE", "");
            data.put("X_RSPCODE", "");
            data.put("X_RSPDESC", "");
            data.put("X_TRANS_CODE", "ITF_IBOT_INSDMDEAL");
        }
        // 调用LCU
        else if (tag == 2)
        {
            data.put("APPLY_TYPE", param.getString("APPLY_TYPE"));
        }
        return data;
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

    public IDataset loadChildTradeInfo(IData input) throws Exception
    {
        String eparchyCode = input.getString("EPARCHY_CODE");
        String userId = input.getString("USER_ID");
        String serialNum = input.getString("SERIAL_NUMBER");

        // 异地用户不能办理
        if (!"1".equals(getVisit().getInModeCode()))
        {
            if (!CSBizBean.getTradeEparchyCode().equals(eparchyCode))
            {
                CSAppException.apperr(DMBusiException.CRM_DM_136);
            }
        }
        // 验证是否已开通DM业务
        IDataset dmInfos = UserResInfoQry.getUserResByRsrvStr(userId, serialNum);

        if (IDataUtil.isEmpty(dmInfos))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_137);
        }

        return dmInfos;
    }

    /**
     * 提交时触发
     * 
     * @param pageData
     * @return
     * @throws Exception
     */
    public IDataset submitTrade(IData pageData) throws Exception
    {

        IData data = new DataMap();

        String serialNum = pageData.getString("SERIAL_NUMBER");
        String operType = pageData.getString("OPER_TYPE");
        String imeiStr = pageData.getString("IMEI");
        String provCode = this.getProvCode();
        String operateId = this.getOperateId(provCode); // 生成流水号

        String kindId = "";
        switch (Integer.parseInt(operType))
        {
            case 17:
                kindId = "BIP2C032_T2002032_0_0"; // 锁定
                break;
            case 18:
                kindId = "BIP2C034_T2002034_0_0"; // 解锁
                break;
            case 19:
                kindId = "BIP2C036_T2002036_0_0"; // 数据清除
                break;
            default:
                break;
        }
        data.put("IMEINUM", imeiStr);
        data.put("KIND_ID", kindId);
        data.put("OPERATEID", operateId);
        data.put("APPLY_TYPE", operType);

        IData rtnInfo = IBossCall.queryDMTradeLockIBOSS(operType, imeiStr, operateId, provCode, kindId);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(" + kindId + ")---返回数据-------" + rtnInfo);

        // getMofficeBySN(pd, serial_number);

        if (!"0".equals(rtnInfo.getString("X_RSPTYPE")))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_1, rtnInfo.getString("X_RSPDESC"));
        }

        IDataset results = this.callInsDMDeal(data);

        return results;
    }
}
