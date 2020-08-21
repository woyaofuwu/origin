
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class DMBusiSelBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(DMBusiSelBean.class);

    /**
     * DM业务办理调用流程：ITF_IBOT_INSDMDEAL
     * 
     * @param pd
     * @param td
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset callInsDMDeal(IData data) throws Exception
    {
        IData param = getParam(data, 2);

        DMBusiMgr dBusiMgr = new DMBusiMgr();

        IData tempData = dBusiMgr.DMInsertData(param);

        // return CSAppCall.call("ITF_IBOT_INSDMDEAL", param);

        IDataset dataset = new DatasetList();

        dataset.add(tempData);

        return dataset;
    }

    /**
     * DM业务数据主表记录拉筛选
     * 
     * @param cycle
     * @throws Exception
     */
    public void fileterDMBusi(IDataset dmBusi, IDataset dmBusi_sub, String bigKey) throws Exception
    {
        IData tempData = null;
        IData data = null;
        String bigValue = null;

        if (IDataUtil.isNotEmpty(dmBusi_sub))
        {
            for (int i = 0; i < dmBusi_sub.size(); i++)
            {
                Object o = dmBusi_sub.get(i);

                if (o != null && o instanceof IData)
                {
                    tempData = (IData) o;

                    bigValue = tempData.getString(bigKey);

                    if (StringUtils.isNotBlank(bigValue))
                    {
                        data = new DataMap();

                        Set<?> tests = tempData.keySet();

                        Iterator<?> testIt = tests.iterator();

                        while (testIt.hasNext())
                        {
                            Object tempKey = testIt.next();

                            if ("RESULTSTATUS".equalsIgnoreCase(tempKey.toString()))
                            {
                                String str = "成功";
                                if ("-1".equalsIgnoreCase(tempData.get(tempKey).toString().trim()))
                                {
                                    str = "未反馈";
                                }
                                else if ("0".equalsIgnoreCase(tempData.get(tempKey).toString().trim()))
                                {
                                    str = "成功";
                                }
                                else if ("1".equalsIgnoreCase(tempData.get(tempKey).toString().trim()))
                                {
                                    str = "失败";
                                }
                                else
                                {
                                    str = tempData.get(tempKey).toString();
                                }
                                data.put(tempKey.toString(), str);
                            }
                            else if ("CANCEL_TAG".equalsIgnoreCase(tempKey.toString()))
                            {
                                String str = "正常";
                                if ("0".equalsIgnoreCase(tempData.get(tempKey).toString()))
                                {
                                    str = "正常";
                                }
                                else if ("1".equalsIgnoreCase(tempData.get(tempKey).toString()))
                                {
                                    str = "取消";
                                }
                                else
                                {
                                    str = tempData.get(tempKey).toString();
                                }
                                data.put(tempKey.toString(), str);
                            }
                            else
                            {
                                data.put(tempKey.toString(), tempData.get(tempKey));
                            }
                        }
                        dmBusi.add(data);
                    }
                }
            }
        }
    }

    /**
     * 获取入参
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IData getParam(IData param, int tag) throws Exception
    {
        IData data = new DataMap();

        data.put("IN_MODE_CODE", param.getString("IN_MODE_CODE", "0")); // 0——营业厅
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// pd.getContext().getEpachyId()
        data.put("TRADE_CITY_CODE", getVisit().getCityCode());// pd.getContext().getCityId()
        data.put("TRADE_DEPART_ID", getVisit().getDepartId());// pd.getContext().getDeptId()
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());// pd.getContext().getStaffId()
        data.put("TRADE_DEPART_PASSWD", "");
        data.put("KIND_ID", param.getString("KIND_ID"));
        data.put("OPERATEID", param.getString("OPERATEID"));

        if (tag == 1)// 发报文
        {
            data.put("PROVINCE_CODE", getVisit().getProvinceCode());// pd.getContext().getProvinceId()
            data.put("ROUTETYPE", "00");
            data.put("ROUTEVALUE", "000");
            data.put("X_RSPTYPE", "");
            data.put("X_RSPCODE", "");
            data.put("X_RSPDESC", "");
            data.put("X_TRANS_CODE", "ITF_IBOT_INSDMDEAL");
        }
        else if (tag == 2)// 调用LCU
        {
            data.put("APPLY_TYPE", param.getString("APPLY_TYPE"));
        }
        return data;
    }

    /**
     * 获取新产品资料列表
     * 
     * @param conParams
     * @return
     * @throws Exception
     */
    public IData queryDMBusi(IData conParams) throws Exception
    {
        IData data = new DataMap();
        IData resultInfo = new DataMap();
        IDataset spinfos = new DatasetList();

        String phonenum = conParams.getString("PHONENUM", "");
        String imeinum = conParams.getString("IMEINUM", "");
        String startdate = conParams.getString("STARTDATE", "");
        String enddate = conParams.getString("ENDDATE", "");
        String apply_type = conParams.getString("APPLY_TYPE", "");

        if (StringUtils.isNotBlank(phonenum) && StringUtils.isNotEmpty(phonenum))
        {
            data.put("PHONENUM", conParams.getString("PHONENUM", ""));
        }
        if (StringUtils.isNotBlank(imeinum) && StringUtils.isNotEmpty(imeinum))
        {
            data.put("IMEINUM", conParams.getString("IMEINUM", ""));
        }
        if (StringUtils.isNotBlank(startdate) && StringUtils.isNotEmpty(startdate))
        {
            data.put("STARTDATE", conParams.getString("STARTDATE", ""));
        }
        if (StringUtils.isNotBlank(enddate) && StringUtils.isNotEmpty(enddate))
        {
            data.put("ENDDATE", conParams.getString("ENDDATE", ""));
        }
        if (StringUtils.isNotBlank(apply_type) && StringUtils.isNotEmpty(apply_type))
        {
            data.put("APPLY_TYPE", conParams.getString("APPLY_TYPE", ""));
        }

        // String LCU_Name="ITF_IBOQ_SELDMDEAL";
        // spinfos = this.getDmBusiInfo(data, LCU_Name);
        DMBusiMgr dMgr = new DMBusiMgr();
        IData tempData = dMgr.DMSelectData(data);

        spinfos = tempData.getDataset("BUSI");

        // 临时end
        if (IDataUtil.isEmpty(spinfos))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_150);
        }

        IDataset dmBusi = new DatasetList();

        this.fileterDMBusi(dmBusi, spinfos, "OPERATEID");

        resultInfo.put("SP_INFO", tempData.getDataset("DM_BUSI"));
        resultInfo.put("DM_BUSI", dmBusi);

        // *****************test callUpdDmDeal DM业务办理反馈 --ITF_IBHT_UPDDMDEAL***start*******//
        /*
         * IData data1 = new DataMap(); //固件升级操作反馈 BIP2C027_T2002027_1_0 24 //BIP2C025_T2002025_1_0 23
         * data1.put("KIND_ID", "BIP2C025_T2002025_1_0"); //20130801162137CSVD731975 存在 20130801162137CSVD731972 不存在
         * data1.put("OPERATEID", "20130801162137CSVD731969"); data1.put("PHONENUM", "13888889999");
         * data1.put("IMEINUM", "124537890000"); data1.put("RESULTSTATUS", "0"); data1.put("BEGINTIME", "20130807");
         * data1.put("ENDTIME", "20500807"); IDataset functionids = new DatasetList(); IDataset paramnames = new
         * DatasetList(); IDataset paramvalues = new DatasetList(); IDataset afterfunctionids = new DatasetList();
         * IDataset afterparamnames = new DatasetList(); IDataset afterparamvalues = new DatasetList(); IData functionid
         * = new DataMap(); IData paramname = new DataMap(); IData paramvalue = new DataMap(); IData afterfunctionid =
         * new DataMap(); IData afterparamname = new DataMap(); IData afterparamvalue = new DataMap();
         * functionid.put("FUNCTIONID", "222"); paramname.put("PARAMNAME", "xx"); paramvalue.put("PARAMVALUE", "333");
         * functionids.add(functionid); paramnames.add(paramname); paramvalues.add(paramvalue); data1.put("FUNCTIONID",
         * functionids); data1.put("PARAMNAME", paramnames); data1.put("PARAMVALUE", paramvalues);
         * afterfunctionid.put("AFTERFUNCTIONID", "001"); afterparamname.put("AFTERPARAMNAME", "yy");
         * afterparamvalue.put("AFTERPARAMVALUE", "111"); afterfunctionids.add(afterfunctionid);
         * afterparamnames.add(afterparamname); afterparamvalues.add(afterparamvalue); data1.put("AFTERFUNCTIONID",
         * afterfunctionids); data1.put("AFTERPARAMNAME", afterparamnames); data1.put("AFTERPARAMVALUE",
         * afterparamvalues); new DMFeedBackInfBean().callUpdDmDeal(data1);
         */
        // *****************test callUpdDmDeal DM业务办理反馈 --ITF_IBHT_UPDDMDEAL***恩对*******//

        return resultInfo;
    }

    /**
     * 获取根据主表记录关联子表 获取DM业务数据列表
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData queryDMBusi_Sub(IData inparam) throws Exception
    {
        IDataset spinfos = null;
        IData resultInfo = new DataMap();
        inparam.put("APPLY_TYPE", inparam.get("PA_APPLY_TYPE"));
        inparam.put("OPERATEID", inparam.get("PA_OPERATEID"));

        if (logger.isDebugEnabled())
            logger.debug("------------PA_APPLY_TYPE-----" + inparam.get("PA_APPLY_TYPE") + "------PA_OPERATEID---------------------" + inparam.get("PA_OPERATEID"));

        // String LCU_Name = "ITF_IBOQ_SELDMDEAL";
        // spinfos = this.getDmBusiInfo(inparam, LCU_Name);
        DMBusiMgr dMgr = new DMBusiMgr();
        IData tempData = dMgr.DMSelectData(inparam);

        spinfos = tempData.getDataset("BUSI");

        if (IDataUtil.isEmpty(spinfos))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_150);
        }

        IDataset dmBusi = new DatasetList();

        this.fileterDMBusi(dmBusi, spinfos, "OPERATEID");

        resultInfo.put("SP_INFO_SUB", tempData.getDataset("DM_BUSI"));
        resultInfo.put("DM_BUSI_SUB", dmBusi);

        return resultInfo;
    }

    /**
     * DM业务操作取消——业务提交
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData submitTrade(IData data) throws Exception
    {
        String operateId = data.getString("OPERATE_ID");

        IData rtnInfo = IBossCall.submitDMBusiSelIBOSS(operateId);

        /*
         * IData rtnInfo = new DataMap(); rtnInfo.put("X_RSPTYPE", 0); rtnInfo.put("X_RSPDESC", "测试！");
         */
        // 临时end
        if (!"0".equals(rtnInfo.getString("X_RSPTYPE")))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_1, rtnInfo.getString("X_RSPDESC"));
        }

        data.put("APPLY_TYPE", "20"); // 20——操作取消
        data.put("OPERATEID", operateId);

        IDataset result = this.callInsDMDeal(data);

        IData ajaxResult = new DataMap();

        if ("0".equals(result.get(0, "X_RESULTCODE")))
        {
            ajaxResult.put("X_RESULTCODE", "0");
            ajaxResult.put("X_RESULTINFO", result.get(0, "X_RESULTINFO"));
            ajaxResult.put("OPERATEID", result.get(0, "OPERATEID"));
            // pd.setAjaxData(ajaxResult);
        }
        else
        {
            ajaxResult.put("X_RESULTCODE", result.get(0, "X_RESULTCODE"));
            ajaxResult.put("X_RESULTINFO", result.get(0, "X_RESULTINFO"));
            ajaxResult.put("OPERATEID", result.get(0, "OPERATEID"));
            // pd.setAjaxData(ajaxResult);
            // common.error(InterBossFactory.DM_UNDO_BUSI_FAIL, result.get(0, "X_RESULTINFO").toString());
            CSAppException.apperr(DMBusiException.CRM_DM_142);
        }

        return ajaxResult;
    }
}
