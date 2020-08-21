
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmapplicationsoftware;

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

public class ApplicationSoftwareBean extends CSBizBean
{
    static transient final Logger logger = Logger.getLogger(ApplicationSoftwareBean.class);

    /**
     * 获取可用应用软件包
     * 
     * @data 2013-8-8
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getApplicationSoftwareDs(IData input) throws Exception
    {
        String serialNumber = input.getString("AUTH_SERIAL_NUMBER");

        IData result = IBossCall.queryApplicationSoftwareIBOSS(serialNumber);// 调用IBOSS接口

        // 4测试
        // IData result = new DataMap();result.put("X_RSPTYPE", "0"); IDataset ds1 = new
        // DatasetList();ds1.add("123");ds1.add("456"); IDataset ds2 = new DatasetList();ds2.add("123");ds2.add("456");
        // IDataset ds3 = new DatasetList();ds3.add("123");ds3.add("456"); IDataset ds4 = new
        // DatasetList();ds4.add("123");ds4.add("456"); IDataset ds5 = new
        // DatasetList();ds5.add("360卫士");ds5.add("腾讯管家"); result.put("SOFTWAREBAGID",
        // ds1);result.put("SOFTWAREBAGSIZE", ds2); result.put("SOFTWAREEDITIONNUM",
        // ds3);result.put("SOFTWAREBAGDESC", ds4); result.put("FUNCTIONNAME", ds5);result.put("FUNCTIONNAME",
        // ds5); //测试end

        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_143);
        }

        if (!"0".equals(result.getString("X_RSPTYPE", "")))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_1, result.getString("X_RSPCODE", "") + "查询出错！");
        }

        /************** IBOSS数据转换 start *******************/
        IDataset dataset1 = new DatasetList();
        IDataset dataset2 = new DatasetList();
        IDataset dataset3 = new DatasetList();
        IDataset dataset4 = new DatasetList();
        IDataset dataset5 = new DatasetList();
        // String 类型 例如：{SOFTWAREBAGID =["001"]}
        if (result.get("SOFTWAREBAGID") instanceof String)
        {

            dataset1.add(result.get("SOFTWAREBAGID"));// 应用软件包ID
            dataset2.add(result.get("SOFTWAREBAGSIZE"));// 应用软件包大小
            dataset3.add(result.get("SOFTWAREEDITIONNUM"));// 版本号
            dataset4.add(result.get("SOFTWAREBAGDESC"));// 功能描述
            dataset5.add(result.get("FUNCTIONNAME"));// 业务名称
        }
        // JSONArray类型 例如：{SOFTWAREBAGID=["001","002"]}
        else
        {
            dataset1 = result.getDataset("SOFTWAREBAGID");// 应用软件包ID
            dataset2 = result.getDataset("SOFTWAREBAGSIZE");// 应用软件包大小
            dataset3 = result.getDataset("SOFTWAREEDITIONNUM");// 版本号
            dataset4 = result.getDataset("SOFTWAREBAGDESC");// 功能描述
            dataset5 = result.getDataset("FUNCTIONNAME");// 业务名称
        }
        /************** IBOSS数据转换 end *******************/

        IDataset dataset = new DatasetList();

        if (IDataUtil.isNotEmpty(dataset1))
        {
            for (int i = 0; i < dataset1.size(); i++)
            {
                IData data1 = new DataMap();
                data1.put("SOFTWAREBAGID", dataset1.get(i));
                data1.put("SOFTWAREBAGSIZE", dataset2.get(i));
                data1.put("SOFTWAREEDITIONNUM", dataset3.get(i));
                data1.put("SOFTWAREBAGDESC", dataset4.get(i));
                data1.put("FUNCTIONNAME", dataset5.get(i));

                dataset.add(data1);
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
        IData provInfo = StaticInfoQry.getStaticInfoByTypeIdDataId("PROVINCE_CODE", getVisit().getProvinceCode());

        if (IDataUtil.isEmpty(provInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_310);
        }

        return provInfo.getString("PDATA_ID");
    }

    /**
     * 提交
     * 
     * @data 2013-8-8
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset submitConfirmForm(IData input) throws Exception
    {
        IData data = new DataMap();
        IDataset infos = new DatasetList();

        String serialNumber = input.getString("AUTH_SERIAL_NUMBER");
        String softwareBagid = input.getString("SOFTWARE");
        String provCode = this.getProvCode();
        String operateId = this.getOperateId(provCode);

        IData callData = IBossCall.submitApplicationSoftwareIBOSS(serialNumber, softwareBagid, provCode, operateId);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP2C030_T2002030_0_0)---返回数据-------" + callData);

        if (IDataUtil.isEmpty(callData))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_145);
        }
        if (IDataUtil.isNull(callData) || !"0".equals(callData.getString("X_RSPTYPE", "")))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_1, callData.getString("X_RSPCODE", "") + "发送请求出错！");
        }

        DMBusiMgr dBusiMgr = new DMBusiMgr();
        data.put("PHONENUM", serialNumber);
        data.put("SOFTWAREBAGID", softwareBagid);
        data.put("ACCOUNTNUM", CSBizBean.getVisit().getStaffId());//
        data.put("OPERATEID", operateId);//
        data.put("APPLY_TYPE", "16");
        data.put("TRADE_EPARCHY_CODE", input.getString("TRADE_EPARCHY_CODE", ""));
        data.put("TRADE_CITY_CODE", input.getString("TRADE_CITY_CODE", ""));
        data.put("TRADE_DEPART_ID", input.getString("TRADE_DEPART_ID", ""));
        data.put("TRADE_STAFF_ID", input.getString("TRADE_STAFF_ID", ""));

        IData infoData = dBusiMgr.DMInsertData(data);

        infos.add(infoData);

        return infos;
    }
}
