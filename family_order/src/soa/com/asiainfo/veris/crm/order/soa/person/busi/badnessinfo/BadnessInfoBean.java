
package com.asiainfo.veris.crm.order.soa.person.busi.badnessinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BlackUserInfoQry;

public class BadnessInfoBean extends CSBizBean
{

    private IDataset getBlackUser(IDataset dataset) throws Exception
    {
        IDataset result = new DatasetList();
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(0);
                String serialNumber = data.getString("REPORT_SERIAL_NUMBER");
                IDataset blackUser = BlackUserInfoQry.queryBlacjInfoBySn(serialNumber);
                if (IDataUtil.isNotEmpty(blackUser))
                {
                    result.add(blackUser.getData(0));
                }
            }
        }
        return result;
    }

    public IDataset getMonths(IData data) throws Exception
    {
        // IData conParams = transferData(data);
        String startDate = data.getString("REPORT_START_TIME", "");
        String endDate = data.getString("REPORT_END_TIME", "");
        int startMonth = Integer.parseInt(startDate.substring(5, 7));
        int endMonth = Integer.parseInt(endDate.substring(5, 7));

        if (endMonth - startMonth > 2)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "起止日期不能跨年、不能跨三个月份，请检查输入!");
        }
        else if ((endMonth - startMonth) == 2)
        {
            data.put("MONTH1", "" + startMonth);
            data.put("MONTH2", "" + (startMonth + 1));
            data.put("MONTH3", "" + endMonth);
        }
        else if ((endMonth - startMonth) == 1)
        {
            data.put("MONTH1", "" + startMonth);
            data.put("MONTH2", "" + endMonth);
            if (endMonth + 1 > 12)
                data.put("MONTH3", "" + (endMonth + 1) % 12);
            else
                data.put("MONTH3", "" + (endMonth + 1));
        }
        else if ((endMonth - startMonth) == 0)
        {
            data.put("MONTH1", "" + startMonth);
            if (startMonth + 1 > 12)
                data.put("MONTH2", "" + (startMonth + 1) % 12);
            else
                data.put("MONTH2", "" + (startMonth + 1));
            if (startMonth + 2 > 12)
                data.put("MONTH3", "" + (startMonth + 2) % 12);
            else
                data.put("MONTH3", "" + (startMonth + 2));
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "起止日期不能跨年、不能跨三个月份，请检查出入");
        }

        return IDataUtil.idToIds(data);
    }

    public IDataset getReportCode(IData data) throws Exception
    {
        IDataset reports = new DatasetList();
        IData data1 = new DataMap();
        IData data2 = new DataMap();

        if ("Q1".equals(data.getString("TRADE_TYPE_CODE", "")) || "Q2".equals(data.getString("TRADE_TYPE_CODE", "")) || "S1".equals(data.getString("TRADE_TYPE_CODE", "")) || "S6".equals(data.getString("TRADE_TYPE_CODE", ""))
                || "F8".equals(data.getString("TRADE_TYPE_CODE", "")) || "F9".equals(data.getString("TRADE_TYPE_CODE", "")) || "F10".equals(data.getString("TRADE_TYPE_CODE", "")) || "F11".equals(data.getString("TRADE_TYPE_CODE", "")))
        {
            data1.put("DATA_ID", "01");
            data1.put("DATA_NAME", "省内网内点对点");
            reports.add(data1);
            data2.put("DATA_ID", "02");
            data2.put("DATA_NAME", "省际网内点对点");
            reports.add(data2);
        }

        else if ("Q3".equals(data.getString("TRADE_TYPE_CODE", "")) || "Q4".equals(data.getString("TRADE_TYPE_CODE", "")) || "S2".equals(data.getString("TRADE_TYPE_CODE", "")) || "S7".equals(data.getString("TRADE_TYPE_CODE", ""))
                || "F12".equals(data.getString("TRADE_TYPE_CODE", "")))
        {
            data1.put("DATA_ID", "03");
            data1.put("DATA_NAME", "SP");
            reports.add(data1);
        }

        else if ("Q5".equals(data.getString("TRADE_TYPE_CODE", "")) || "Q6".equals(data.getString("TRADE_TYPE_CODE", "")) || "S3".equals(data.getString("TRADE_TYPE_CODE", "")) || "S8".equals(data.getString("TRADE_TYPE_CODE", ""))
                || "F13".equals(data.getString("TRADE_TYPE_CODE", "")))
        {
            data1.put("DATA_ID", "04");
            data1.put("DATA_NAME", "行业应用");
            reports.add(data1);
        }

        else if ("Q7".equals(data.getString("TRADE_TYPE_CODE", "")) || "Q8".equals(data.getString("TRADE_TYPE_CODE", "")) || "S4".equals(data.getString("TRADE_TYPE_CODE", "")) || "S9".equals(data.getString("TRADE_TYPE_CODE", ""))
                || "F14".equals(data.getString("TRADE_TYPE_CODE", "")))
        {
            data1.put("DATA_ID", "05");
            data1.put("DATA_NAME", "自有业务");
            reports.add(data1);

        }

        else if ("Q9".equals(data.getString("TRADE_TYPE_CODE", "")) || "S5".equals(data.getString("TRADE_TYPE_CODE", "")) || "S10".equals(data.getString("TRADE_TYPE_CODE", "")) || "F15".equals(data.getString("TRADE_TYPE_CODE", "")))
        {
            data1.put("DATA_ID", "06");
            data1.put("DATA_NAME", "其他运营商");
            reports.add(data1);

        }
        else if ("Q10".equals(data.getString("TRADE_TYPE_CODE", "")))
        {
            data1.put("DATA_ID", "07");
            data1.put("DATA_NAME", "wap涉黄");
            reports.add(data1);
        }
        else
        {
            IData data3 = new DataMap();
            IData data4 = new DataMap();
            IData data5 = new DataMap();
            IData data6 = new DataMap();
            IData data7 = new DataMap();
            data1.put("DATA_ID", "01");
            data1.put("DATA_NAME", "省内网内点对点");
            reports.add(data1);
            data2.put("DATA_ID", "02");
            data2.put("DATA_NAME", "省际网内点对点");
            reports.add(data2);
            data3.put("DATA_ID", "03");
            data3.put("DATA_NAME", "SP");
            reports.add(data3);
            data4.put("DATA_ID", "04");
            data4.put("DATA_NAME", "行业应用");
            reports.add(data4);
            data5.put("DATA_ID", "05");
            data5.put("DATA_NAME", "自有业务");
            reports.add(data5);
            data6.put("DATA_ID", "06");
            data6.put("DATA_NAME", "其他运营商");
            reports.add(data6);
            data7.put("DATA_ID", "07");
            data7.put("DATA_NAME", "其他");
            reports.add(data7);
        }

        return reports;
    }

    public IDataset queryBadnessInfos(IData data, Pagination page) throws Exception
    {
        IData param = transferData(data);
        String tradeTypeCode = IDataUtil.chkParam(param, "TRADE_TYPE_CODE");
        String reportType = param.getString("REPORT_TYPE_CODE");
        String reportCode = param.getString("REPORT_CODE");
        String state = param.getString("STATE");
        String serialNumber = param.getString("REPORT_SERIAL_NUMBER");
        String inModeCode = param.getString("IN_MODE_CODE");
        String badProv = param.getString("BADNESS_INFO_PROVINCE", "898");
        String recvInType = param.getString("RECV_IN_TYPE");
        String dealRemark = param.getString("DEAL_REMARK");
        String endTime = param.getString("REPORT_END_TIME");
        String startTime = param.getString("REPORT_START_TIME");
        String badnessInfo = param.getString("BADNESS_INFO");
        String contentType = param.getString("CONTENT_TYPE_CODE");
        String brandCode = param.getString("BADNESS_INFO_BRAND_CODE");
        String custProv = param.getString("REPORT_CUST_PROVINCE");

        IDataset result = new DatasetList();
        if ("Q3".equals(tradeTypeCode) || "Q4".equals(tradeTypeCode) || "Q5".equals(tradeTypeCode) || "Q6".equals(tradeTypeCode) || "Q7".equals(tradeTypeCode) || "Q8".equals(tradeTypeCode) || "Q9".equals(tradeTypeCode))
        {
            result = BadnessInfoQry.queryBadnessInfoByDate3(reportType, reportCode, state, serialNumber, inModeCode, badProv, recvInType, dealRemark, endTime, badnessInfo, startTime, contentType, page);
        }
        else if ("Q1".equals(tradeTypeCode) || "Q2".equals(tradeTypeCode))
        {
            result = BadnessInfoQry.queryBadnessInfoByDate(reportType, reportCode, state, brandCode, serialNumber, badProv, recvInType, dealRemark, endTime, startTime, custProv, contentType, page);
        }
        else if ("Q10".equals(tradeTypeCode))
        {
            result = BadnessInfoQry.queryBadnessInfoByDate10(state, serialNumber, inModeCode, badProv, recvInType, dealRemark, endTime, startTime, badnessInfo, page);
        }
        return result;
    }

    /**
     * form报表类 yangsh6 update
     * 
     * @param data
     * @param page
     * @return
     * @throws Exception
     */
    public IDataset queryBadnessInfosForm(IData data, Pagination page) throws Exception
    {
        String tradeTypeCode = IDataUtil.chkParam(data, "TRADE_TYPE_CODE");
        IData conParams = transferData(data);
        String reportType = conParams.getString("REPORT_TYPE_CODE");
        String reportCode = conParams.getString("REPORT_CODE");
        String endTime = conParams.getString("REPORT_END_TIME");
        String startTime = conParams.getString("REPORT_START_TIME");

        IDataset result = new DatasetList();
        if ("F1".equals(tradeTypeCode))// 2.3.1举报信息量统计报表
        {
            result = BadnessInfoQry.getBadnessInfoByForm1(reportType, reportCode, endTime, startTime, page);
        }
        else if ("F2".equals(tradeTypeCode))// 2.3.2被举报信息量统计报表
        {
            result = BadnessInfoQry.getBadnessInfoByForm2(reportType, reportCode, endTime, startTime, page);
        }
        else if ("F3".equals(tradeTypeCode))// 2.3.3被举报处理率统计报表
        {
            result = BadnessInfoQry.getBadnessInfoByForm3(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F4".equals(tradeTypeCode))// 2.3.4被举报处理及时率统计报表
        {
            result = BadnessInfoQry.getBadnessInfoByForm4(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F5".equals(tradeTypeCode))// 2.3.5被举报信息处理情况统计报表
        {
            result = BadnessInfoQry.getBadnessInfoByForm5(reportType, reportCode, endTime, startTime, page);
        }
        else if ("F6".equals(tradeTypeCode))// 2.3.6被举报信息已处理量按内容分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm6(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F7".equals(tradeTypeCode))// 2.3.7被举报信息省外和省内举报量
        {
            result = BadnessInfoQry.getBadnessInfoByForm7(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F8".equals(tradeTypeCode))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm8(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F9".equals(tradeTypeCode))// 2.3.9点对点被举报信息已处理量按被举报信息品牌分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm9(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F10".equals(tradeTypeCode))// 2.3.10 点对点被举报信息已处理量按入网时间分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm10(reportType, reportCode, endTime, startTime, page);
        }
        else if ("F11".equals(tradeTypeCode))// 2.3.11 点对点被举报信息已处理量中按欠费金额分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm11(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F12".equals(tradeTypeCode))// 2.3.12 SP被举报信息量按SP类型分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm14(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F13".equals(tradeTypeCode))// 2.3.13 行业应用被举报信息量按行业应用类型分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm14(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F14".equals(tradeTypeCode))// 2.3.14 自有业务被举报信息量按自有业务类型分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm14(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F15".equals(tradeTypeCode))// 2.3.15 其他运营商被举报号码已处理量按归属运营商分类
        {
            result = BadnessInfoQry.getBadnessInfoByForm15(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F16".equals(tradeTypeCode))// 2.3.16 被举报信息已处理量分省按处理意见统计报表
        {
            result = BadnessInfoQry.getBadnessInfoByForm16(reportType, reportCode, endTime, startTime, page);

        }
        else if ("F17".equals(tradeTypeCode))// 2.3.17 本省举报按被举报归属省统计报表
        {
            result = BadnessInfoQry.getBadnessInfoByForm17(reportType, reportCode, endTime, startTime, page);

        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "无法识别的业务类型编码：" + tradeTypeCode);
        }
        if ("F5".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("STATE_NAME", "").equals("未处理"))
                        {
                            tempData[0].put("STATE_NAME", "未处理");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));
                        }
                        else if (param.getString("STATE_NAME", "").equals("已处理"))
                        {
                            tempData[1].put("STATE_NAME", "已处理");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));
                        }
                        w++;
                    }
                }

                if (w == 2)
                {

                }
                else if (w == 1)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("STATE_NAME", "未处理");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("STATE_NAME", "已处理");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 2 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            // tempList.add(tempData[2]);
            return tempList;
        }

        if ("F6".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {

            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(),
                    new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(),
                    new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("CONTENT_TYPE_CODE", "").equals("0101"))
                        {
                            tempData[0].put("CONTENT_TYPE_CODE", "0101");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0102"))
                        {
                            tempData[1].put("CONTENT_TYPE_CODE", "0102");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0103"))
                        {
                            tempData[2].put("CONTENT_TYPE_CODE", "0103");
                            tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0104"))
                        {
                            tempData[3].put("CONTENT_TYPE_CODE", "0104");
                            tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0105"))
                        {
                            tempData[4].put("CONTENT_TYPE_CODE", "0105");
                            tempData[4].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[4].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0106"))
                        {
                            tempData[5].put("CONTENT_TYPE_CODE", "0106");
                            tempData[5].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[5].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0107"))
                        {
                            tempData[6].put("CONTENT_TYPE_CODE", "0107");
                            tempData[6].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[6].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0108"))
                        {
                            tempData[7].put("CONTENT_TYPE_CODE", "0108");
                            tempData[7].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[7].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0109"))
                        {
                            tempData[8].put("CONTENT_TYPE_CODE", "0109");
                            tempData[8].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[8].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0201"))
                        {
                            tempData[9].put("CONTENT_TYPE_CODE", "0201");
                            tempData[9].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[9].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0202"))
                        {
                            tempData[10].put("CONTENT_TYPE_CODE", "0201");
                            tempData[10].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[10].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0203"))
                        {
                            tempData[11].put("CONTENT_TYPE_CODE", "0203");
                            tempData[11].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[11].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0204"))
                        {
                            tempData[12].put("CONTENT_TYPE_CODE", "0204");
                            tempData[12].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[12].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0205"))
                        {
                            tempData[13].put("CONTENT_TYPE_CODE", "0205");
                            tempData[13].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[13].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0206"))
                        {
                            tempData[14].put("CONTENT_TYPE_CODE", "0206");
                            tempData[14].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[14].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0207"))
                        {
                            tempData[15].put("CONTENT_TYPE_CODE", "0207");
                            tempData[15].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[15].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0208"))
                        {
                            tempData[16].put("CONTENT_TYPE_CODE", "0208");
                            tempData[16].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[16].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0209"))
                        {
                            tempData[17].put("CONTENT_TYPE_CODE", "0209");
                            tempData[17].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[17].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0301"))
                        {
                            tempData[18].put("CONTENT_TYPE_CODE", "0301");
                            tempData[18].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[18].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0302"))
                        {
                            tempData[19].put("CONTENT_TYPE_CODE", "0302");
                            tempData[19].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[19].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0303"))
                        {
                            tempData[20].put("CONTENT_TYPE_CODE", "0303");
                            tempData[20].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[20].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0304"))
                        {
                            tempData[21].put("CONTENT_TYPE_CODE", "0304");
                            tempData[21].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[21].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0305"))
                        {
                            tempData[22].put("CONTENT_TYPE_CODE", "0305");
                            tempData[22].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[22].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0306"))
                        {
                            tempData[23].put("CONTENT_TYPE_CODE", "0306");
                            tempData[23].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[23].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0307"))
                        {
                            tempData[24].put("CONTENT_TYPE_CODE", "0307");
                            tempData[24].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[24].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0308"))
                        {
                            tempData[25].put("CONTENT_TYPE_CODE", "0308");
                            tempData[25].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[25].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("CONTENT_TYPE_CODE", "").equals("0309"))
                        {
                            tempData[26].put("CONTENT_TYPE_CODE", "0309");
                            tempData[26].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[26].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        w++;
                    }
                }

                if (w == 27)
                {

                }
                else if (w >= 1 && w < 27)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("CONTENT_TYPE_CODE", "0101");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("CONTENT_TYPE_CODE", "0102");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[2].isEmpty() || !tempData[2].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[2].isEmpty())
                        {
                            tempData[2].put("CONTENT_TYPE_CODE", "0103");
                        }
                        tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[3].isEmpty() || !tempData[3].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[3].isEmpty())
                        {
                            tempData[3].put("CONTENT_TYPE_CODE", "0104");
                        }
                        tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[4].isEmpty() || !tempData[4].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[4].isEmpty())
                        {
                            tempData[4].put("CONTENT_TYPE_CODE", "0105");
                        }
                        tempData[4].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[4].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[5].isEmpty() || !tempData[5].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[5].isEmpty())
                        {
                            tempData[5].put("CONTENT_TYPE_CODE", "0106");
                        }
                        tempData[5].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[5].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[6].isEmpty() || !tempData[6].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[6].isEmpty())
                        {
                            tempData[6].put("CONTENT_TYPE_CODE", "0107");
                        }
                        tempData[6].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[6].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[7].isEmpty() || !tempData[7].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[7].isEmpty())
                        {
                            tempData[7].put("CONTENT_TYPE_CODE", "0108");
                        }
                        tempData[7].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[7].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[8].isEmpty() || !tempData[8].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[8].isEmpty())
                        {
                            tempData[8].put("CONTENT_TYPE_CODE", "0109");
                        }
                        tempData[8].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[8].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[9].isEmpty() || !tempData[9].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[9].isEmpty())
                        {
                            tempData[9].put("CONTENT_TYPE_CODE", "0201");
                        }
                        tempData[9].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[9].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[10].isEmpty() || !tempData[10].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[10].isEmpty())
                        {
                            tempData[10].put("CONTENT_TYPE_CODE", "0202");
                        }
                        tempData[10].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[10].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[11].isEmpty() || !tempData[11].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[11].isEmpty())
                        {
                            tempData[11].put("CONTENT_TYPE_CODE", "0203");
                        }
                        tempData[11].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[11].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[12].isEmpty() || !tempData[12].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[12].isEmpty())
                        {
                            tempData[12].put("CONTENT_TYPE_CODE", "0204");
                        }
                        tempData[12].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[12].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[13].isEmpty() || !tempData[13].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[13].isEmpty())
                        {
                            tempData[13].put("CONTENT_TYPE_CODE", "0205");
                        }
                        tempData[13].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[13].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[14].isEmpty() || !tempData[14].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[14].isEmpty())
                        {
                            tempData[14].put("CONTENT_TYPE_CODE", "0206");
                        }
                        tempData[14].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[14].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[15].isEmpty() || !tempData[15].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[15].isEmpty())
                        {
                            tempData[15].put("CONTENT_TYPE_CODE", "0207");
                        }
                        tempData[15].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[15].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[16].isEmpty() || !tempData[16].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[16].isEmpty())
                        {
                            tempData[16].put("CONTENT_TYPE_CODE", "0208");
                        }
                        tempData[16].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[16].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[17].isEmpty() || !tempData[17].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[17].isEmpty())
                        {
                            tempData[17].put("CONTENT_TYPE_CODE", "0209");
                        }
                        tempData[17].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[17].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[18].isEmpty() || !tempData[18].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[18].isEmpty())
                        {
                            tempData[18].put("CONTENT_TYPE_CODE", "0301");
                        }
                        tempData[18].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[18].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[19].isEmpty() || !tempData[19].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[19].isEmpty())
                        {
                            tempData[19].put("CONTENT_TYPE_CODE", "0302");
                        }
                        tempData[19].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[19].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[20].isEmpty() || !tempData[20].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[20].isEmpty())
                        {
                            tempData[20].put("CONTENT_TYPE_CODE", "0303");
                        }
                        tempData[20].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[20].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[21].isEmpty() || !tempData[21].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[21].isEmpty())
                        {
                            tempData[21].put("CONTENT_TYPE_CODE", "0304");
                        }
                        tempData[21].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[21].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[22].isEmpty() || !tempData[22].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[22].isEmpty())
                        {
                            tempData[22].put("CONTENT_TYPE_CODE", "0305");
                        }
                        tempData[22].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[22].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[23].isEmpty() || !tempData[23].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[23].isEmpty())
                        {
                            tempData[23].put("CONTENT_TYPE_CODE", "0306");
                        }
                        tempData[23].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[23].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[24].isEmpty() || !tempData[24].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[24].isEmpty())
                        {
                            tempData[24].put("CONTENT_TYPE_CODE", "0307");
                        }
                        tempData[24].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[24].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[25].isEmpty() || !tempData[25].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[25].isEmpty())
                        {
                            tempData[25].put("CONTENT_TYPE_CODE", "0308");
                        }
                        tempData[25].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[25].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[26].isEmpty() || !tempData[26].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[26].isEmpty())
                        {
                            tempData[26].put("CONTENT_TYPE_CODE", "0309");
                        }
                        tempData[26].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[26].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 27 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            tempList.add(tempData[2]);
            tempList.add(tempData[3]);
            tempList.add(tempData[4]);
            tempList.add(tempData[5]);
            tempList.add(tempData[6]);
            tempList.add(tempData[7]);
            tempList.add(tempData[8]);
            tempList.add(tempData[9]);
            tempList.add(tempData[10]);
            tempList.add(tempData[11]);
            tempList.add(tempData[12]);
            tempList.add(tempData[13]);
            tempList.add(tempData[14]);
            tempList.add(tempData[15]);
            tempList.add(tempData[16]);
            tempList.add(tempData[17]);
            tempList.add(tempData[18]);
            tempList.add(tempData[19]);
            tempList.add(tempData[20]);
            tempList.add(tempData[21]);
            tempList.add(tempData[22]);
            tempList.add(tempData[23]);
            tempList.add(tempData[24]);
            tempList.add(tempData[25]);
            tempList.add(tempData[26]);

            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;

        }

        if ("F7".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("PROVINCE_TYPE", "").equals("省内举报数量"))
                        {
                            tempData[0].put("PROVINCE_TYPE", "省内举报数量");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("PROVINCE_TYPE", "").equals("省外举报数量"))
                        {
                            tempData[1].put("PROVINCE_TYPE", "省外举报数量");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }

                        w++;
                    }
                }

                if (w == 2)
                {

                }
                else if (w == 1)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("PROVINCE_TYPE", "省内举报数量");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("PROVINCE_TYPE", "省外举报数量");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 2 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            // tempList.add(tempData[2]);

            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        if ("F8".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("IS_REAL_NAME", "").equals("是"))
                        {
                            tempData[0].put("IS_REAL_NAME", "是");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("IS_REAL_NAME", "").equals("否"))
                        {
                            tempData[1].put("IS_REAL_NAME", "否");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("IS_REAL_NAME", "").equals("不确定"))
                        {
                            tempData[2].put("IS_REAL_NAME", "不确定");
                            tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        w++;
                    }
                }

                if (w == 3)
                {

                }
                else if (w == 1 || w == 2)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("IS_REAL_NAME", "是");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("IS_REAL_NAME", "否");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[2].isEmpty() || !tempData[2].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[2].isEmpty())
                        {
                            tempData[2].put("IS_REAL_NAME", "不确定");
                        }
                        tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 3 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            tempList.add(tempData[2]);

            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        if ("F9".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("BADNESSINFO_BRAND", "").equals("全球通"))
                        {
                            tempData[0].put("BADNESSINFO_BRAND", "全球通");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("BADNESSINFO_BRAND", "").equals("神州行"))
                        {
                            tempData[1].put("BADNESSINFO_BRAND", "神州行");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESSINFO_BRAND", "").equals("动感地带"))
                        {
                            tempData[2].put("BADNESSINFO_BRAND", "动感地带");
                            tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESSINFO_BRAND", "").equals("不正确填写"))
                        {
                            tempData[3].put("BADNESSINFO_BRAND", "不正确填写");
                            tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        w++;
                    }
                }

                if (w == 4)
                {

                }
                else if (w == 1 || w == 2 || w == 3)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("BADNESSINFO_BRAND", "全球通");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("BADNESSINFO_BRAND", "神州行");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[2].isEmpty() || !tempData[2].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[2].isEmpty())
                        {
                            tempData[2].put("BADNESSINFO_BRAND", "动感地带");
                        }
                        tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[3].isEmpty() || !tempData[3].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[3].isEmpty())
                        {
                            tempData[3].put("BADNESSINFO_BRAND", "不正确填写");
                        }
                        tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 4 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            tempList.add(tempData[2]);
            tempList.add(tempData[3]);
            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        if ("F10".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("IN_DATE", "").equals("1个月内"))
                        {
                            tempData[0].put("IN_DATE", "1个月内");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("IN_DATE", "").equals("2-12个月"))
                        {
                            tempData[1].put("IN_DATE", "2-12个月");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("IN_DATE", "").equals("13-24个月"))
                        {
                            tempData[2].put("IN_DATE", "13-24个月");
                            tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("IN_DATE", "").equals("24个月以上"))
                        {
                            tempData[3].put("IN_DATE", "24个月以上");
                            tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), data.getString("BADNESSINFO_SUM", ""));
                            tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), data.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("IN_DATE", "").equals("不正确填写"))
                        {
                            tempData[4].put("IN_DATE", "不正确填写");
                            tempData[4].put("BADNESSINFO_SUM" + (i - startMonth), data.getString("BADNESSINFO_SUM", ""));
                            tempData[4].put("BADNESSINFO_ZB" + (i - startMonth), data.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        w++;
                    }
                }

                if (w == 5)
                {

                }
                else if (w == 1 || w == 2 || w == 3 || w == 4)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("IN_DATE", "1个月内");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("IN_DATE", "2-12个月");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[2].isEmpty() || !tempData[2].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[2].isEmpty())
                        {
                            tempData[2].put("IN_DATE", "13-24个月");
                        }
                        tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[3].isEmpty() || !tempData[3].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[3].isEmpty())
                        {
                            tempData[3].put("IN_DATE", "24个月以上");
                        }
                        tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[4].isEmpty() || !tempData[4].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[4].isEmpty())
                        {
                            tempData[4].put("IN_DATE", "不正确填写");
                        }
                        tempData[4].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[4].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                }
                else if (w > 5 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            tempList.add(tempData[2]);
            tempList.add(tempData[3]);
            tempList.add(tempData[4]);
            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        if ("F11".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("ALLNEWROWE_FEE", "").equals("0-50（含）元"))
                        {
                            tempData[0].put("ALLNEWROWE_FEE", "0-50（含）元");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("ALLNEWROWE_FEE", "").equals("50-100（含）元"))
                        {
                            tempData[1].put("ALLNEWROWE_FEE", "50-100（含）元");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("ALLNEWROWE_FEE", "").equals("100-1000（含）元"))
                        {
                            tempData[2].put("ALLNEWROWE_FEE", "100-1000（含）元");
                            tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("ALLNEWROWE_FEE", "").equals(">1000元"))
                        {
                            tempData[3].put("ALLNEWROWE_FEE", ">1000元");
                            tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        w++;
                    }
                }

                if (w == 4)
                {

                }
                else if (w == 1 || w == 2 || w == 3)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("ALLNEWROWE_FEE", "0-50（含）元");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("ALLNEWROWE_FEE", "50-100（含）元");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[2].isEmpty() || !tempData[2].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[2].isEmpty())
                        {
                            tempData[2].put("ALLNEWROWE_FEE", "100-1000（含）元");
                        }
                        tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[3].isEmpty() || !tempData[3].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[3].isEmpty())
                        {
                            tempData[3].put("ALLNEWROWE_FEE", ">1000元");
                        }
                        tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 4 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            tempList.add(tempData[2]);
            tempList.add(tempData[3]);
            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        if (("F12".equals(tradeTypeCode) || "F13".equals(tradeTypeCode) || "F14".equals(tradeTypeCode)) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("IN_MODE", "").equals("全网"))
                        {
                            tempData[0].put("IN_MODE", "全网");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("IN_MODE", "").equals("本地"))
                        {
                            tempData[1].put("IN_MODE", "本地");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), data.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), data.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }

                        w++;
                    }
                }

                if (w == 2)
                {

                }
                else if (w == 1)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("IN_MODE", "全网");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("IN_MODE", "本地");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 2 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);

            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        if ("F15".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("VEST_OPERATOR", "").equals("不正确填写"))
                        {
                            tempData[0].put("VEST_OPERATOR", "不正确填写");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("VEST_OPERATOR", "").equals("电信"))
                        {
                            tempData[1].put("VEST_OPERATOR", "电信");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("VEST_OPERATOR", "").equals("联通"))
                        {
                            tempData[2].put("VEST_OPERATOR", "联通");
                            tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        w++;
                    }
                }

                if (w == 3)
                {

                }
                else if (w == 1 || w == 2)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("VEST_OPERATOR", "不正确填写");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("VEST_OPERATOR", "电信");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[2].isEmpty() || !tempData[2].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[2].isEmpty())
                        {
                            tempData[2].put("VEST_OPERATOR", "联通");
                        }
                        tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 3 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            tempList.add(tempData[2]);

            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        if ("F16".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(),
                    new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(),
                    new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("DEAL_RAMARK", "").equals("0101"))
                        {
                            tempData[0].put("DEAL_RAMARK", "0101");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0102"))
                        {
                            tempData[1].put("DEAL_RAMARK", "0102");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0103"))
                        {
                            tempData[2].put("DEAL_RAMARK", "0103");
                            tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0104"))
                        {
                            tempData[3].put("DEAL_RAMARK", "0104");
                            tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0105"))
                        {
                            tempData[4].put("DEAL_RAMARK", "0105");
                            tempData[4].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[4].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0106"))
                        {
                            tempData[5].put("DEAL_RAMARK", "0106");
                            tempData[5].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[5].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0107"))
                        {
                            tempData[6].put("DEAL_RAMARK", "0107");
                            tempData[6].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[6].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0108"))
                        {
                            tempData[7].put("DEAL_RAMARK", "0108");
                            tempData[7].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[7].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0109"))
                        {
                            tempData[8].put("DEAL_RAMARK", "0109");
                            tempData[8].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[8].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0110"))
                        {
                            tempData[9].put("DEAL_RAMARK", "0110");
                            tempData[9].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[9].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0111"))
                        {
                            tempData[10].put("DEAL_RAMARK", "0111");
                            tempData[10].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[10].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0112"))
                        {
                            tempData[11].put("DEAL_RAMARK", "0112");
                            tempData[11].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[11].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0113"))
                        {
                            tempData[12].put("DEAL_RAMARK", "0113");
                            tempData[12].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[12].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0114"))
                        {
                            tempData[13].put("DEAL_RAMARK", "0114");
                            tempData[13].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[13].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0115"))
                        {
                            tempData[14].put("DEAL_RAMARK", "0115");
                            tempData[14].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[14].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0116"))
                        {
                            tempData[15].put("DEAL_RAMARK", "0116");
                            tempData[15].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[15].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0117"))
                        {
                            tempData[16].put("DEAL_RAMARK", "0117");
                            tempData[16].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[16].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0118"))
                        {
                            tempData[17].put("DEAL_RAMARK", "0117");
                            tempData[17].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[17].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0119"))
                        {
                            tempData[18].put("DEAL_RAMARK", "0119");
                            tempData[18].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[18].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0201"))
                        {
                            tempData[19].put("DEAL_RAMARK", "0201");
                            tempData[19].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[19].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0202"))
                        {
                            tempData[20].put("DEAL_RAMARK", "0202");
                            tempData[20].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[20].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0203"))
                        {
                            tempData[21].put("DEAL_RAMARK", "0203");
                            tempData[21].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[21].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0204"))
                        {
                            tempData[22].put("DEAL_RAMARK", "0204");
                            tempData[22].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[22].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0205"))
                        {
                            tempData[23].put("DEAL_RAMARK", "0205");
                            tempData[23].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[23].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0206"))
                        {
                            tempData[24].put("DEAL_RAMARK", "0206");
                            tempData[24].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[24].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0207"))
                        {
                            tempData[25].put("DEAL_RAMARK", "0207");
                            tempData[25].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[25].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0401"))
                        {
                            tempData[26].put("DEAL_RAMARK", "0401");
                            tempData[26].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[26].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0402"))
                        {
                            tempData[27].put("DEAL_RAMARK", "0402");
                            tempData[27].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[27].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0403"))
                        {
                            tempData[28].put("DEAL_RAMARK", "0403");
                            tempData[28].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[28].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0404"))
                        {
                            tempData[29].put("DEAL_RAMARK", "0404");
                            tempData[29].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[29].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0405"))
                        {
                            tempData[30].put("DEAL_RAMARK", "0405");
                            tempData[30].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[30].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("DEAL_RAMARK", "").equals("0406"))
                        {
                            tempData[31].put("DEAL_RAMARK", "0406");
                            tempData[31].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[31].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }

                        w++;
                    }
                }

                if (w == 32)
                {

                }
                else if (w >= 1 && w < 32)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("DEAL_RAMARK", "0101");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("DEAL_RAMARK", "0102");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[2].isEmpty() || !tempData[2].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[2].isEmpty())
                        {
                            tempData[2].put("DEAL_RAMARK", "0103");
                        }
                        tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[3].isEmpty() || !tempData[3].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[3].isEmpty())
                        {
                            tempData[3].put("DEAL_RAMARK", "0104");
                        }
                        tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[4].isEmpty() || !tempData[4].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[4].isEmpty())
                        {
                            tempData[4].put("DEAL_RAMARK", "0105");
                        }
                        tempData[4].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[4].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[5].isEmpty() || !tempData[5].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[5].isEmpty())
                        {
                            tempData[5].put("DEAL_RAMARK", "0106");
                        }
                        tempData[5].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[5].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[6].isEmpty() || !tempData[6].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[6].isEmpty())
                        {
                            tempData[6].put("DEAL_RAMARK", "0107");
                        }
                        tempData[6].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[6].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[7].isEmpty() || !tempData[7].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[7].isEmpty())
                        {
                            tempData[7].put("DEAL_RAMARK", "0108");
                        }
                        tempData[7].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[7].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[8].isEmpty() || !tempData[8].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[8].isEmpty())
                        {
                            tempData[8].put("DEAL_RAMARK", "0109");
                        }
                        tempData[8].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[8].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[9].isEmpty() || !tempData[9].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[9].isEmpty())
                        {
                            tempData[9].put("DEAL_RAMARK", "0110");
                        }
                        tempData[9].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[9].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[10].isEmpty() || !tempData[10].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[10].isEmpty())
                        {
                            tempData[10].put("DEAL_RAMARK", "0111");
                        }
                        tempData[10].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[10].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[11].isEmpty() || !tempData[11].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[11].isEmpty())
                        {
                            tempData[11].put("DEAL_RAMARK", "0112");
                        }
                        tempData[11].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[11].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[12].isEmpty() || !tempData[12].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[12].isEmpty())
                        {
                            tempData[12].put("DEAL_RAMARK", "0113");
                        }
                        tempData[12].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[12].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[13].isEmpty() || !tempData[13].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[13].isEmpty())
                        {
                            tempData[13].put("DEAL_RAMARK", "0114");
                        }
                        tempData[13].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[13].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[14].isEmpty() || !tempData[14].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[14].isEmpty())
                        {
                            tempData[14].put("DEAL_RAMARK", "0115");
                        }
                        tempData[14].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[14].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[15].isEmpty() || !tempData[15].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[15].isEmpty())
                        {
                            tempData[15].put("DEAL_RAMARK", "0116");
                        }
                        tempData[15].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[15].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[16].isEmpty() || !tempData[16].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[16].isEmpty())
                        {
                            tempData[16].put("DEAL_RAMARK", "0117");
                        }
                        tempData[16].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[16].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[17].isEmpty() || !tempData[17].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[17].isEmpty())
                        {
                            tempData[17].put("DEAL_RAMARK", "0118");
                        }
                        tempData[17].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[17].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[18].isEmpty() || !tempData[18].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[18].isEmpty())
                        {
                            tempData[18].put("DEAL_RAMARK", "0119");
                        }
                        tempData[18].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[18].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[19].isEmpty() || !tempData[19].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[19].isEmpty())
                        {
                            tempData[19].put("DEAL_RAMARK", "0201");
                        }
                        tempData[19].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[19].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[20].isEmpty() || !tempData[20].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[20].isEmpty())
                        {
                            tempData[20].put("DEAL_RAMARK", "0202");
                        }
                        tempData[20].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[20].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[21].isEmpty() || !tempData[21].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[21].isEmpty())
                        {
                            tempData[21].put("DEAL_RAMARK", "0203");
                        }
                        tempData[21].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[21].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[22].isEmpty() || !tempData[22].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[22].isEmpty())
                        {
                            tempData[22].put("DEAL_RAMARK", "0204");
                        }
                        tempData[22].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[22].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[23].isEmpty() || !tempData[23].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[23].isEmpty())
                        {
                            tempData[23].put("DEAL_RAMARK", "0205");
                        }
                        tempData[23].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[23].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[24].isEmpty() || !tempData[24].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[24].isEmpty())
                        {
                            tempData[24].put("DEAL_RAMARK", "0206");
                        }
                        tempData[24].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[24].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[25].isEmpty() || !tempData[25].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[25].isEmpty())
                        {
                            tempData[25].put("DEAL_RAMARK", "0207");
                        }
                        tempData[25].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[25].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[26].isEmpty() || !tempData[26].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[26].isEmpty())
                        {
                            tempData[26].put("DEAL_RAMARK", "0401");
                        }
                        tempData[26].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[26].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[27].isEmpty() || !tempData[27].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[27].isEmpty())
                        {
                            tempData[27].put("DEAL_RAMARK", "0402");
                        }
                        tempData[27].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[27].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[28].isEmpty() || !tempData[28].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[28].isEmpty())
                        {
                            tempData[28].put("DEAL_RAMARK", "0403");
                        }
                        tempData[28].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[28].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[29].isEmpty() || !tempData[29].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[29].isEmpty())
                        {
                            tempData[29].put("DEAL_RAMARK", "0404");
                        }
                        tempData[29].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[29].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[30].isEmpty() || !tempData[30].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[30].isEmpty())
                        {
                            tempData[30].put("DEAL_RAMARK", "0405");
                        }
                        tempData[30].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[30].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[31].isEmpty() || !tempData[31].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[31].isEmpty())
                        {
                            tempData[31].put("DEAL_RAMARK", "0406");
                        }
                        tempData[31].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[31].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 32 || w < 1)
                {

                }

            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            tempList.add(tempData[2]);
            tempList.add(tempData[3]);
            tempList.add(tempData[4]);
            tempList.add(tempData[5]);
            tempList.add(tempData[6]);
            tempList.add(tempData[7]);
            tempList.add(tempData[8]);
            tempList.add(tempData[9]);
            tempList.add(tempData[10]);
            tempList.add(tempData[11]);
            tempList.add(tempData[12]);
            tempList.add(tempData[13]);
            tempList.add(tempData[14]);
            tempList.add(tempData[15]);
            tempList.add(tempData[16]);
            tempList.add(tempData[17]);
            tempList.add(tempData[18]);
            tempList.add(tempData[19]);
            tempList.add(tempData[20]);
            tempList.add(tempData[21]);
            tempList.add(tempData[22]);
            tempList.add(tempData[23]);
            tempList.add(tempData[24]);
            tempList.add(tempData[25]);
            tempList.add(tempData[26]);
            tempList.add(tempData[27]);
            tempList.add(tempData[28]);
            tempList.add(tempData[29]);
            tempList.add(tempData[30]);
            tempList.add(tempData[31]);

            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        if ("F17".equals(tradeTypeCode) && IDataUtil.isNotEmpty(result))// 2.3.8点对点被举报信息已处理量按是否实名制分类
        {
            String startDate = data.getString("REPORT_START_TIME", "");
            String endDate = data.getString("REPORT_END_TIME", "");
            int startMonth = Integer.parseInt(startDate.substring(5, 7));
            int endMonth = Integer.parseInt(endDate.substring(5, 7));
            IDataset tempList = new DatasetList();
            IData[] tempData =
            { new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(),
                    new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(), new DataMap(),
                    new DataMap(), new DataMap(), new DataMap() };
            IData param = new DataMap();
            String str = "";
            int k = 0, m = 0, n = 0, w = 0;
            for (int i = startMonth; i <= endMonth; i++)
            {
                w = 0;
                for (int j = 0; j < result.size(); j++)
                {
                    if ("".equals(result.getData(j).getString("YEAR_MONTH", "")) || result.getData(j).getString("YEAR_MONTH", "") == null)
                    {
                        continue;
                    }
                    // log.debug("-----------------------------------------------YEAR_MONTH["+j+"]: "+result.getData(j).getString("YEAR_MONTH",
                    // ""));
                    int month = Integer.parseInt(result.getData(j).getString("YEAR_MONTH", ""));
                    if (i == month)
                    {
                        // data.clear();
                        param = result.getData(j);
                        if (param.getString("BADNESS_INFO_PROVINCE", "").equals("000"))
                        {
                            tempData[0].put("BADNESS_INFO_PROVINCE", "000");
                            tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("100"))
                        {
                            tempData[1].put("BADNESS_INFO_PROVINCE", "100");
                            tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("200"))
                        {
                            tempData[2].put("BADNESS_INFO_PROVINCE", "200");
                            tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("210"))
                        {
                            tempData[3].put("BADNESS_INFO_PROVINCE", "210");
                            tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("220"))
                        {
                            tempData[4].put("BADNESS_INFO_PROVINCE", "220");
                            tempData[4].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[4].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("230"))
                        {
                            tempData[5].put("BADNESS_INFO_PROVINCE", "230");
                            tempData[5].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[5].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("240"))
                        {
                            tempData[6].put("BADNESS_INFO_PROVINCE", "240");
                            tempData[6].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[6].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("250"))
                        {
                            tempData[7].put("BADNESS_INFO_PROVINCE", "250");
                            tempData[7].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[7].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("270"))
                        {
                            tempData[8].put("BADNESS_INFO_PROVINCE", "270");
                            tempData[8].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[8].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("280"))
                        {
                            tempData[9].put("BADNESS_INFO_PROVINCE", "280");
                            tempData[9].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[9].put("BADNESSINFO_ZB" + (i - startMonth), data.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("290"))
                        {
                            tempData[10].put("BADNESS_INFO_PROVINCE", "290");
                            tempData[10].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[10].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("311"))
                        {
                            tempData[11].put("BADNESS_INFO_PROVINCE", "311");
                            tempData[11].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[11].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("351"))
                        {
                            tempData[12].put("BADNESS_INFO_PROVINCE", "351");
                            tempData[12].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[12].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("371"))
                        {
                            tempData[13].put("BADNESS_INFO_PROVINCE", "371");
                            tempData[13].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[13].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("431"))
                        {
                            tempData[14].put("BADNESS_INFO_PROVINCE", "431");
                            tempData[14].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[14].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("451"))
                        {
                            tempData[15].put("BADNESS_INFO_PROVINCE", "451");
                            tempData[15].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[15].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("471"))
                        {
                            tempData[16].put("BADNESS_INFO_PROVINCE", "471");
                            tempData[16].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[16].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("531"))
                        {
                            tempData[17].put("BADNESS_INFO_PROVINCE", "531");
                            tempData[17].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[17].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("551"))
                        {
                            tempData[18].put("BADNESS_INFO_PROVINCE", "551");
                            tempData[18].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[18].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("571"))
                        {
                            tempData[19].put("BADNESS_INFO_PROVINCE", "571");
                            tempData[19].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[19].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("591"))
                        {
                            tempData[20].put("BADNESS_INFO_PROVINCE", "591");
                            tempData[20].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[20].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("731"))
                        {
                            tempData[21].put("BADNESS_INFO_PROVINCE", "731");
                            tempData[21].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[21].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("771"))
                        {
                            tempData[22].put("BADNESS_INFO_PROVINCE", "771");
                            tempData[22].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[22].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("791"))
                        {
                            tempData[23].put("BADNESS_INFO_PROVINCE", "791");
                            tempData[23].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[23].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("851"))
                        {
                            tempData[24].put("BADNESS_INFO_PROVINCE", "851");
                            tempData[24].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[24].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("871"))
                        {
                            tempData[25].put("BADNESS_INFO_PROVINCE", "871");
                            tempData[25].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[25].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("891"))
                        {
                            tempData[26].put("BADNESS_INFO_PROVINCE", "891");
                            tempData[26].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[26].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[0]--------------["+i+"]-----------["+j+"]: "+tempData[0]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("898"))
                        {
                            tempData[27].put("BADNESS_INFO_PROVINCE", "898");
                            tempData[27].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[27].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("931"))
                        {
                            tempData[28].put("BADNESS_INFO_PROVINCE", "931");
                            tempData[28].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[28].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("951"))
                        {
                            tempData[29].put("BADNESS_INFO_PROVINCE", "951");
                            tempData[29].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[29].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("971"))
                        {
                            tempData[30].put("BADNESS_INFO_PROVINCE", "971");
                            tempData[30].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[30].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[2]----------------["+i+"]----------["+j+"]: "+tempData[2]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("991"))
                        {
                            tempData[31].put("BADNESS_INFO_PROVINCE", "991");
                            tempData[31].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[31].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }
                        else if (param.getString("BADNESS_INFO_PROVINCE", "").equals("999"))
                        {
                            tempData[32].put("BADNESS_INFO_PROVINCE", "999");
                            tempData[32].put("BADNESSINFO_SUM" + (i - startMonth), param.getString("BADNESSINFO_SUM", ""));
                            tempData[32].put("BADNESSINFO_ZB" + (i - startMonth), param.getString("BADNESSINFO_ZB", ""));

                            // log.debug("----------------tempData[1]---------------["+i+"]-----------["+j+"]: "+tempData[1]);
                        }

                        w++;
                    }
                }

                if (w == 33)
                {

                }
                else if (w >= 1 && w < 33)
                {
                    if (tempData[0].isEmpty() || !tempData[0].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[0].isEmpty())
                        {
                            tempData[0].put("BADNESS_INFO_PROVINCE", "000");
                        }
                        tempData[0].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[0].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[1].isEmpty() || !tempData[1].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[1].isEmpty())
                        {
                            tempData[1].put("BADNESS_INFO_PROVINCE", "100");
                        }
                        tempData[1].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[1].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[2].isEmpty() || !tempData[2].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[2].isEmpty())
                        {
                            tempData[2].put("BADNESS_INFO_PROVINCE", "200");
                        }
                        tempData[2].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[2].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[3].isEmpty() || !tempData[3].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[3].isEmpty())
                        {
                            tempData[3].put("BADNESS_INFO_PROVINCE", "210");
                        }
                        tempData[3].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[3].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[4].isEmpty() || !tempData[4].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[4].isEmpty())
                        {
                            tempData[4].put("BADNESS_INFO_PROVINCE", "220");
                        }
                        tempData[4].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[4].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[5].isEmpty() || !tempData[5].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[5].isEmpty())
                        {
                            tempData[5].put("BADNESS_INFO_PROVINCE", "230");
                        }
                        tempData[5].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[5].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[6].isEmpty() || !tempData[6].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[6].isEmpty())
                        {
                            tempData[6].put("BADNESS_INFO_PROVINCE", "240");
                        }
                        tempData[6].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[6].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[7].isEmpty() || !tempData[7].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[7].isEmpty())
                        {
                            tempData[7].put("BADNESS_INFO_PROVINCE", "250");
                        }
                        tempData[7].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[7].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[8].isEmpty() || !tempData[8].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[8].isEmpty())
                        {
                            tempData[8].put("BADNESS_INFO_PROVINCE", "270");
                        }
                        tempData[8].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[8].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[9].isEmpty() || !tempData[9].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[9].isEmpty())
                        {
                            tempData[9].put("BADNESS_INFO_PROVINCE", "280");
                        }
                        tempData[9].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[9].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[10].isEmpty() || !tempData[10].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[10].isEmpty())
                        {
                            tempData[10].put("BADNESS_INFO_PROVINCE", "290");
                        }
                        tempData[10].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[10].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[11].isEmpty() || !tempData[11].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[11].isEmpty())
                        {
                            tempData[11].put("BADNESS_INFO_PROVINCE", "311");
                        }
                        tempData[11].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[11].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[12].isEmpty() || !tempData[12].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[12].isEmpty())
                        {
                            tempData[12].put("BADNESS_INFO_PROVINCE", "351");
                        }
                        tempData[12].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[12].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[13].isEmpty() || !tempData[13].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[13].isEmpty())
                        {
                            tempData[13].put("BADNESS_INFO_PROVINCE", "371");
                        }
                        tempData[13].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[13].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[14].isEmpty() || !tempData[14].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[14].isEmpty())
                        {
                            tempData[14].put("BADNESS_INFO_PROVINCE", "431");
                        }
                        tempData[14].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[14].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[15].isEmpty() || !tempData[15].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[15].isEmpty())
                        {
                            tempData[15].put("BADNESS_INFO_PROVINCE", "451");
                        }
                        tempData[15].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[15].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[16].isEmpty() || !tempData[16].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[16].isEmpty())
                        {
                            tempData[16].put("BADNESS_INFO_PROVINCE", "471");
                        }
                        tempData[16].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[16].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[17].isEmpty() || !tempData[17].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[17].isEmpty())
                        {
                            tempData[17].put("BADNESS_INFO_PROVINCE", "531");
                        }
                        tempData[17].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[17].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[18].isEmpty() || !tempData[18].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[18].isEmpty())
                        {
                            tempData[18].put("BADNESS_INFO_PROVINCE", "551");
                        }
                        tempData[18].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[18].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[19].isEmpty() || !tempData[19].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[19].isEmpty())
                        {
                            tempData[19].put("BADNESS_INFO_PROVINCE", "571");
                        }
                        tempData[19].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[19].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[20].isEmpty() || !tempData[20].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[20].isEmpty())
                        {
                            tempData[20].put("BADNESS_INFO_PROVINCE", "591");
                        }
                        tempData[20].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[20].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[21].isEmpty() || !tempData[21].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[21].isEmpty())
                        {
                            tempData[21].put("BADNESS_INFO_PROVINCE", "731");
                        }
                        tempData[21].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[21].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[22].isEmpty() || !tempData[22].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[22].isEmpty())
                        {
                            tempData[22].put("BADNESS_INFO_PROVINCE", "771");
                        }
                        tempData[22].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[22].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[23].isEmpty() || !tempData[23].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[23].isEmpty())
                        {
                            tempData[23].put("BADNESS_INFO_PROVINCE", "791");
                        }
                        tempData[23].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[23].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[24].isEmpty() || !tempData[24].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[24].isEmpty())
                        {
                            tempData[24].put("BADNESS_INFO_PROVINCE", "851");
                        }
                        tempData[24].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[24].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[25].isEmpty() || !tempData[25].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[25].isEmpty())
                        {
                            tempData[25].put("BADNESS_INFO_PROVINCE", "871");
                        }
                        tempData[25].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[25].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[26].isEmpty() || !tempData[26].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[26].isEmpty())
                        {
                            tempData[26].put("BADNESS_INFO_PROVINCE", "891");
                        }
                        tempData[26].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[26].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[27].isEmpty() || !tempData[27].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[27].isEmpty())
                        {
                            tempData[27].put("BADNESS_INFO_PROVINCE", "898");
                        }
                        tempData[27].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[27].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[28].isEmpty() || !tempData[28].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[28].isEmpty())
                        {
                            tempData[28].put("BADNESS_INFO_PROVINCE", "931");
                        }
                        tempData[28].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[28].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[29].isEmpty() || !tempData[29].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[29].isEmpty())
                        {
                            tempData[29].put("BADNESS_INFO_PROVINCE", "951");
                        }
                        tempData[29].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[29].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[30].isEmpty() || !tempData[30].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[30].isEmpty())
                        {
                            tempData[30].put("BADNESS_INFO_PROVINCE", "971");
                        }
                        tempData[30].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[30].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[31].isEmpty() || !tempData[31].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[31].isEmpty())
                        {
                            tempData[31].put("BADNESS_INFO_PROVINCE", "991");
                        }
                        tempData[31].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[31].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }
                    if (tempData[32].isEmpty() || !tempData[32].containsKey("BADNESSINFO_SUM" + (i - startMonth)))
                    {
                        if (tempData[32].isEmpty())
                        {
                            tempData[32].put("BADNESS_INFO_PROVINCE", "999");
                        }
                        tempData[32].put("BADNESSINFO_SUM" + (i - startMonth), "0");
                        tempData[32].put("BADNESSINFO_ZB" + (i - startMonth), "0%");
                    }

                }
                else if (w > 33 || w < 1)
                {

                }
            }
            tempList.add(tempData[0]);
            tempList.add(tempData[1]);
            tempList.add(tempData[2]);
            tempList.add(tempData[3]);
            tempList.add(tempData[4]);
            tempList.add(tempData[5]);
            tempList.add(tempData[6]);
            tempList.add(tempData[7]);
            tempList.add(tempData[8]);
            tempList.add(tempData[9]);
            tempList.add(tempData[10]);
            tempList.add(tempData[11]);
            tempList.add(tempData[12]);
            tempList.add(tempData[13]);
            tempList.add(tempData[14]);
            tempList.add(tempData[15]);
            tempList.add(tempData[16]);
            tempList.add(tempData[17]);
            tempList.add(tempData[18]);
            tempList.add(tempData[19]);
            tempList.add(tempData[20]);
            tempList.add(tempData[21]);
            tempList.add(tempData[22]);
            tempList.add(tempData[23]);
            tempList.add(tempData[24]);
            tempList.add(tempData[25]);
            tempList.add(tempData[26]);
            tempList.add(tempData[27]);
            tempList.add(tempData[28]);
            tempList.add(tempData[29]);
            tempList.add(tempData[30]);
            tempList.add(tempData[31]);
            tempList.add(tempData[32]);

            // log.debug("----------------------xander---------------------------tempList:  "+tempList.toString());
            return tempList;
        }

        return result;
    }

    public IDataset staticsBadnessInfos(IData data, Pagination page) throws Exception
    {
        IData param = transferData(data);
        String tradeTypeCode = IDataUtil.chkParam(param, "TRADE_TYPE_CODE");
        String reportType = param.getString("REPORT_TYPE_CODE");
        String reportCode = param.getString("REPORT_CODE");
        String state = param.getString("STATE");
        String badProv = param.getString("BADNESS_INFO_PROVINCE");
        String recvInType = param.getString("RECV_IN_TYPE");
        String endTime = param.getString("REPORT_END_TIME");
        String startTime = param.getString("REPORT_START_TIME");
        String badnessInfo = param.getString("BADNESS_INFO");
        String contentType = param.getString("CONTENT_TYPE_CODE");
        String reportSum = param.getString("REPORT_SUM");
        String brandCode = param.getString("BADNESS_BRAND_CODE");
        String custProv = param.getString("REPORT_CUST_PROVINCE");
        String inDate = param.getString("IN_DATE");
        String debtBalance = param.getString("DEBT_BALANCE");

        IDataset result = new DatasetList();
        if ("S4".equals(tradeTypeCode))
        {
            if (param.containsKey("REPORT_SUM") && StringUtils.isNotBlank(reportSum))
            {
                result = BadnessInfoQry.queryBadnessInfoByStaticSumS3(reportType, reportCode, state, badProv, recvInType, reportSum, endTime, brandCode, custProv, badnessInfo, startTime, contentType, page);
            }
            else
            {
                result = BadnessInfoQry.queryBadnessInfoByStaticS3(reportType, reportCode, state, badProv, recvInType, endTime, brandCode, custProv, badnessInfo, startTime, contentType, page);
            }
        }
        else if ("S2".equals(tradeTypeCode))
        {
            if (param.containsKey("REPORT_SUM") && StringUtils.isNotBlank(reportSum))
            {
                result = BadnessInfoQry.queryBadnessInfoByStaticSumS2(reportType, reportCode, state, badProv, recvInType, reportSum, endTime, custProv, badnessInfo, startTime, contentType, page);
            }
            else
            {
                result = BadnessInfoQry.queryBadnessInfoByStaticS2(reportType, reportCode, state, badProv, recvInType, endTime, custProv, badnessInfo, startTime, contentType, page);
                // IDataset blackUser = getBlackUser(result);

            }
        }
        else if ("S7".equals(tradeTypeCode) || "S9".equals(tradeTypeCode))
        {
            result = BadnessInfoQry.queryBadnessInfoByStaticS7(reportType, reportCode, state, badProv, recvInType, endTime, custProv, badnessInfo, startTime, contentType, page);
        }
        else if ("S1".equals(tradeTypeCode))
        {
            String flag = "0";
            if (param.containsKey("REPORT_SUM") && !"".equals(param.getString("REPORT_SUM")))
            {
                flag = "1";
            }
            // result = BadnessInfoQry.queryBadnessInfoByStatic4S1(debtBalance,inDate,reportSum,flag,reportType,
            // reportCode, state, badProv, recvInType, endTime, brandCode, custProv, badnessInfo, startTime,
            // contentType, page);
        }
        else if ("S3".equals(tradeTypeCode))
        {
            String flag = "0";
            if (param.containsKey("REPORT_SUM") && !"".equals(param.getString("REPORT_SUM")))
            {
                flag = "1";
            }
            result = BadnessInfoQry.queryBadnessInfoByStatic4S3(flag, reportType, reportCode, state, badProv, recvInType, endTime, brandCode, custProv, badnessInfo, startTime, contentType, page);
        }
        else if ("S5".equals(tradeTypeCode) || "S6".equals(tradeTypeCode) || "S8".equals(tradeTypeCode) || "S10".equals(tradeTypeCode))
        {
            boolean tag = false;
            if (param.containsKey("REPORT_SUM") && !"".equals(param.getString("REPORT_SUM")))
            {
                tag = true;
            }
            result = BadnessInfoQry.queryBadnessInfoByStatic4SOthers(tradeTypeCode, tag, reportType, reportCode, state, badProv, recvInType, endTime, custProv, badnessInfo, startTime, contentType, page);
        }
        return result;
    }

    // 为了和数据库的对应 ,所以替换掉这些数据
    public IData transferData(IData conParams) throws Exception
    {
        if (conParams.containsKey("REPORT_TYPE_CODE"))
        {
            String reportTypeCode = conParams.getString("REPORT_TYPE_CODE", "");
            if ("01".equals(reportTypeCode))
            {
                conParams.put("REPORT_TYPE_CODE", "04");
            }
            else if ("02".equals(reportTypeCode))
            {
                conParams.put("REPORT_TYPE_CODE", "06");
            }
            else if ("03".equals(reportTypeCode))
            {
                conParams.put("REPORT_TYPE_CODE", "05");
            }
            else if ("04".equals(reportTypeCode))
            {
                conParams.put("REPORT_TYPE_CODE", "07");
            }
        }
        if (conParams.containsKey("REPORT_TYPE_CODE") && StringUtils.isNotBlank(conParams.getString("REPORT_CODE", "")))
        {
            String reportCode = conParams.getString("REPORT_CODE", "");
            if (reportCode.length() == 4)
                conParams.put("REPORT_CODE", conParams.getString("REPORT_CODE", "").substring(2, 4));
        }
        return conParams;
    }
}
