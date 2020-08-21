
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmelementgather;

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

public class ElementGatherBean extends CSBizBean
{

    static transient final Logger logger = Logger.getLogger(ElementGatherBean.class);

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

        corpBiz = "000" + corpBiz;

        String seq = corpBiz.substring(corpBiz.length() - 3, corpBiz.length());

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
        IData provInfo = StaticInfoQry.getStaticInfoByTypeIdDataId("PROVINCE_CODE", getVisit().getProvinceCode());

        if (IDataUtil.isEmpty(provInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_310);
        }

        return provInfo.getString("PDATA_ID");
    }

    /**
     * 功能: 发指令查询信息
     * 
     * @param condition
     *            operId由YYYYMMDD+HHMMSS+CSVD+省代码+XXX(三位编号)组成
     * @return
     * @throws Exception
     */
    public IData sendHttpGather(IData condition) throws Exception
    {

        String phone = condition.getString("PHONE", "");
        String imei = condition.getString("IMEI", "");
        String provCode = getProvCode();
        String operId = getOperateId(provCode);

        IData data = IBossCall.sendElementGatherIBOSS(imei, phone, provCode, operId);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP2C020_T2002020_0_0)---返回数据-------" + data);
        if (IDataUtil.isEmpty(data))
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
     * 功能: DM日志记录
     * 
     * @param condition
     * @return
     * @throws Exception
     */
    public IDataset sendTuxGather(IData condition) throws Exception
    {

        IData param = new DataMap();
        IDataset dataset = new DatasetList();

        param.put("PHONENUM", condition.getString("comminfo_SERIAL_NUMBER", ""));
        param.put("OPERATEID", condition.getString("comminfo_OPERATEID", ""));
        param.put("IMEINUM", condition.getString("comminfo_IMEI_NUM", ""));
        param.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());
        param.put("APPLY_TYPE", "11");
        param.put("PROV_CODE", getProvCode());

        DMBusiMgr dBusiMgr = new DMBusiMgr();
        IData tempData = dBusiMgr.DMInsertData(param);
        dataset.add(tempData);

        return dataset;
    }

}
