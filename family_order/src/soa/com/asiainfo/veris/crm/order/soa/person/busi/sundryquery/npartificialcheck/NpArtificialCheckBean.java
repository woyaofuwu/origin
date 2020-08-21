
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npartificialcheck;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class NpArtificialCheckBean extends CSBizBean
{

    public IData checkNotPass(IData param) throws Exception
    {
        String str = param.getString("datas");
        String remark = param.getString("remark");
        if (StringUtils.isNotBlank(str))
        {
            IDataset infos = new DatasetList(str);
            for (Object obj : infos)
            {
                IData info = (IData) obj;
                IData sdata = new DataMap();
                sdata.put("TRADE_ID", info.getString("TRADE_ID"));
                sdata.put("ACCEPT_MONTH", info.getString("ACCEPT_MONTH"));
                sdata.put("SERIAL_NUMBER", info.getString("SERIAL_NUMBER"));
                sdata.put("STATE", "230");
                sdata.put("RESULT_CODE", "699");
                sdata.put("RESULT_MESSAGE", remark);
                // data.put("BOOK_SEND_TIME", sysdate);//吴丽甘要求屏蔽2010-11-22
                sdata.put("RSRV_STR3", getVisit().getStaffId());// 审核工号
                sdata.put("RSRV_STR4", getVisit().getDepartId());// 审核部门
                sdata.put("RSRV_STR5", "FAIL|" + remark);// 审核失败原因
                Dao.save("TF_B_TRADE_NP", sdata,Route.getJourDb(Route.CONN_CRM_CG));// 先提交，防止执行到一个不能激活的号码造成之前已经激活的号码没有改标志位。
                
                IData data1 = new DataMap();
                data1.put("TRADE_ID", info.getString("TRADE_ID"));
                data1.put("ACCEPT_MONTH", info.getString("ACCEPT_MONTH"));
                data1.put("CANCEL_TAG", "1");

                Dao.save("TF_B_TRADE", data1, new String[]
                { "TRADE_ID", "ACCEPT_MONTH" },Route.getJourDb(Route.CONN_CRM_CG));
            }
        }
        IData rData = new DataMap();
        rData.put("MSG", "受理成功!");
        return rData;
    }

    public IData checkPass(IData param) throws Exception
    {
        String str = param.getString("datas");
        if (StringUtils.isNotBlank(str))
        {
            IDataset infos = new DatasetList(str);
            for (Object obj : infos)
            {
                IData info = (IData) obj;
                IData sdata = new DataMap();
                sdata.put("TRADE_ID", info.getString("TRADE_ID"));
                sdata.put("ACCEPT_MONTH", info.getString("ACCEPT_MONTH"));
                sdata.put("SERIAL_NUMBER", info.getString("SERIAL_NUMBER"));
                sdata.put("STATE", "220");
                sdata.put("RESULT_CODE", "200");
                sdata.put("RESULT_MESSAGE", "OK");
                // data.put("BOOK_SEND_TIME", sysdate);//吴丽甘要求屏蔽2010-11-22
                sdata.put("RSRV_STR3", getVisit().getStaffId());// 审核工号
                sdata.put("RSRV_STR4", getVisit().getDepartId());// 审核部门
                sdata.put("RSRV_STR5", "SUCCESS|" + "审核通过");// 审核失败原因
                Dao.save("TF_B_TRADE_NP", sdata,Route.getJourDb(Route.CONN_CRM_CG));// 先提交，防止执行到一个不能激活的号码造成之前已经激活的号码没有改标志位。

            }
        }
        IData rData = new DataMap();
        rData.put("MSG", "审核成功!");
        return rData;
    }

    public IDataset queryTradeNpInfos(IData param, Pagination pagination) throws Exception
    {

        String strSubsysCode = "CSM";
        String iParamAttr = "163";
        String strParamCode = "0";
        String strEparchyCode = "0898";

        IDataset dataset = CommparaInfoQry.getCommparaAllCol(strSubsysCode, iParamAttr, strParamCode, strEparchyCode);

        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取人工审核时间配置失败！");
        }

        String areaCode = param.getString("AREA_CODE", "").trim();
        String staffCityCode = getVisit().getCityCode();

        if ("HNSJ".equals(staffCityCode) || "HNYD".equals(staffCityCode) || "HNKF".equals(staffCityCode))
        {
            if ("0898".equals(areaCode) || "HNSJ".equals(areaCode) || "HNYD".equals(areaCode) || "HNKF".equals(areaCode))
            {
                areaCode = "HAIN";
            }

        }
        else
        {
            if (!areaCode.equals(staffCityCode))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "你归属区域[" + staffCityCode + "]，你没有权限查询区域[" + areaCode + "]的数据！");
            }
        }
        String rsrv_num1 = dataset.getData(0).getString("PARA_CODE1");
        String cancel_tag = "0";
        String state = "210";
        String trade_type_code = "41";
        IDataset ids = TradeNpQry.getTradeNpBySelByAcceptDate(rsrv_num1, cancel_tag, state, trade_type_code, areaCode, pagination);

        if (IDataUtil.isNotEmpty(ids))
        {
        	for(int i=0;i<ids.size();i++)
        	{
        		IData id = ids.getData(i);
        		String 	 user_id = id.getString("USER_ID");
        		IDataset crmInfos= TradeNpQry.getTradeNpBySelByAcceptDate2(user_id, areaCode);
        		if(IDataUtil.isNotEmpty(crmInfos))
        		{
        			id.put("VIP_CLASS_ID", crmInfos.getData(0).getString("VIP_CLASS_ID"));
        			id.put("VIP_TYPE_CODE", crmInfos.getData(0).getString("VIP_TYPE_CODE"));
        			id.put("OPEN_DATE", crmInfos.getData(0).getString("OPEN_DATE"));
        			id.put("CITY_CODE", crmInfos.getData(0).getString("CITY_CODE"));
        		}
        		
        	}
        }
        
        if (IDataUtil.isNotEmpty(ids))
        {
            for (int i = 0, len = ids.size(); i < len; i++)
            {
                IData data = ids.getData(i);
                String user_id = data.getString("USER_ID");
                IDataset fees = TradeNpQry.getUserAverageFeeByUserId(user_id, "6");
                if (IDataUtil.isNotEmpty(fees))
                {
                    int feeLen = fees.size();
                    if (feeLen != 1)
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户近半年平均话费失败！");
                    }
                    data.put("AVERAGE_FEE", FeeUtils.formatMoney(fees.getData(0).getString("PARAM_CODE", "0"), "0.00"));

                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户近半年平均话费失败！");

                }

            }
        }
        return ids;
    }
}
