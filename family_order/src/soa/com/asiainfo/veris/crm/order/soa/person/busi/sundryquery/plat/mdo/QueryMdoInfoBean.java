
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.mdo;

import java.util.Arrays;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MdoInfoQry;

public class QueryMdoInfoBean extends CSBizBean
{

    // YYYYMMDD＋CSVC＋3位省代码＋7位流水号;
    private String getOperNumb(String tradeId) throws Exception
    {
        String provCode = getProvCode();
        StringBuilder sb = new StringBuilder();
        sb.append(SysDateMgr.getSysDate("yyyyMMdd")).append("CSVC");
        sb.append(provCode.substring(provCode.length() - 3));
        sb.append(tradeId.substring(tradeId.length() - 7));
        return sb.toString();
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

        String provCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
        { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
        { "PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode() });
        if (provCode == null || provCode.length() == 0)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "查询省代码无资料！");
        }
        return provCode;
    }

    private void parseRslList(String content, IDataset list)
    {
        if (StringUtils.isNotBlank(content))
        {
            String[] resultListArray = content.split("\",");
            for (int j = 0; j < resultListArray.length; j++)
            {
                if (StringUtils.isNotBlank(resultListArray[j]))
                {
                    String[] resultArray = resultListArray[j].split("\\|");
                    IData result = new DataMap();
                    result.put("SERVICENAME", resultArray[1]);
                    result.put("SERVICEFLAT", resultArray[2]);
                    result.put("SERVICEID", resultArray[3]);
                    result.put("CONTENTNAME", resultArray[4]);
                    result.put("SPNAME", resultArray[5]);
                    result.put("SPCSTEL", resultArray[6]);
                    result.put("ORDERTIME", resultArray[7]);
                    result.put("ORDERSTATUS", resultArray[8]);
                    result.put("STARTTIME", resultArray[9]);
                    result.put("ENDTIME", resultArray[10]);
                    result.put("FEETYPE", resultArray[11]);
                    result.put("SERFEE", resultArray[12]);
                    result.put("INFOFEE", resultArray[13]);
                    result.put("FEESTATUS", resultArray[14]);
                    String tmp=resultArray[15];
                    if(tmp!=null && tmp.contains("]")&& j==resultListArray.length-1)
                    {
                    	resultArray[15]=tmp.substring(0,tmp.length()-2);                       
                    }
                    result.put("CHANNELNAME", resultArray[15]);
                    list.add(result);
                }

            }
        }
    }

    private IData queryFromIBOSS(IData param) throws Exception
    {
        IData data = new DataMap();
        String trade_id = SeqMgr.getTradeId();
        data.put("KIND_ID", "BIP2B311_T2001311_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");

        data.put("INDICTSEQ", getOperNumb(trade_id));
        data.put("MSISDN", param.getString("SERIAL_NUMBER", ""));
        data.put("QUERYSTARTTIME", param.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER);
        data.put("QUERYENDTIME", param.getString("END_DATE") + SysDateMgr.END_DATE);
        data.put("QUERYTYPE", param.getString("MDO_QUERY_MODE"));
        // 受理渠道
        // 01 10086人工;02 10086自动;03 网站;04 短信营业厅;05 掌上营业厅（WAP）;06 USSD;07 自助终端;08 营业厅;09 外呼;10 其他渠道;11 信产部转办;
        // 12 信产部立案;13 客户来电;14 客户来函;15 客户来访;16 集团热线;17 中消协转;18 工信部自查;19 信产部清算司;20 信产部服务处;21 质量万里行;
        // 22 Email;23 国漫热线;24 终端服务厂商;25 总部转办;26 工信部化解;27 总部督办;28 10088人工;29 10088自动;30 315平台;32 热线自查;33 互联网监控;
        // 34 10086官方微博;35 综合部敏感信息;36 CEO信箱;37 10086999短信平台;
        data.put("CONTACTCHANNEL", param.getString("OPR_SOURCE", "08"));
        data.put("CALLERNO", param.getString("SERIAL_NUMBER", ""));
        data.put("CALLEDNO", "");// 被叫号码
        data.put("SUBSNAME", param.getString("CUST_NAME", ""));
        data.put("SUBSLEVEL", "");// 客户级别 01 钻石卡客户;02 金卡客户;03 银卡客户;04 普通客户;
        data.put("SUBSBRAND", "");// 客户品牌编码 01 全球通;02 动感地带;03 神州行（所有地方性品牌均纳入神州行品牌）;04 外省移动客户;05 他网;
        data.put("SVCTYPEID", "10010399");// 此处统一填写：10010399-业务查询-其他
        data.put("HOMEPROV", getProvCode());
        data.put("SVCCITY", CSBizBean.getVisit().getCityCode() + "-" + CSBizBean.getVisit().getCityName());
        data.put("ORIGINTIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));// yyyy-MM-dd HH:mm:ss，省客服收到用户查询的时间
        data.put("ACCEPTTIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));// 本查询工单提交时间yyyy-MM-dd HH:mm:ss
        data.put("ACCEPTSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());// “编码-描述”形式

        IData ret = IBossCall.callHttpIBOSS7("IBOSS", data).getData(0);
        param.put("RSLTPAGENUM", ret.getString("RSLTPAGENUM"));

        if ("0".equals(ret.getString("X_RSPTYPE")) && "0000".equals(ret.getString("X_RSPCODE")))
        {
            String resultPage = ret.getString("RSLTTOTALPAGES");
            String resultContent = ret.getString("QRYRSLTLIST");
            IDataset dataset = new DatasetList();
            int resultSum = 0;
            try
            {
                resultSum = Integer.parseInt(resultPage);
            }
            catch (NumberFormatException e)
            {
            }
            if (resultSum > 1)
            {
                this.parseRslList(resultContent, dataset);
                // dataset = IDataUtil.iData2iDataset(data, "KEY", "VALUE");
                if (dataset.size() == 0)
                    dataset.add(ret);

                // 分页查询剩余记录
                for (int i = 2; i <= resultSum; i++)
                {
                    data.put("KIND_ID", "BIP2B321_T2001321_0_0");
                    data.put("QUERYPAGENUM", i);
                    param.put("QUERYPAGENUM", i);
                    IData resultData = IBossCall.callHttpIBOSS("IBOSS", data).getData(0);

                    IDataset sets = new DatasetList();
                    if ("0".equals(resultData.getString("X_RSPTYPE")) && "0000".equals(resultData.getString("X_RSPCODE")))
                    {
                        resultContent = ret.getString("QRYRSLTLIST");
                        this.parseRslList(resultContent, sets);
                        if (sets.size() == 0)
                            sets.add(resultData);
                    }
                    else
                    {
                        CSAppException.apperr(TradeException.CRM_TRADE_95, "分页查询时操作失败！<br>" + resultData.getString("X_RSPDESC"));
                    }
                    dataset.addAll(sets);
                    this.saveResult(dataset, param);
                }
                // 查询服务归档
                data.put("KIND_ID", "BIP2B322_T2001322_0_0");
                data.put("HANDLINGDEPT", CSBizBean.getVisit().getDepartId() + "-" + CSBizBean.getVisit().getDepartName());
                data.put("HANDLINGSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
                data.put("HANDLINGCOMMENT", "OK");// 归档意见
                data.put("TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                IBossCall.callHttpIBOSS7("IBOSS", data).getData(0);

            }
            else if (resultSum == 1)
            {
                this.parseRslList(resultContent, dataset);
                // dataset = ret.toDataset();
                if (dataset.size() == 0)
                    dataset.add(ret);
                saveResult(dataset, param);
            }
            else
            {
                this.saveResult(null, param);
            }
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "MDO订购关系查询操作失败！<br>" + ret.getString("X_RSPDESC"));
        }
        return ret;
    }

    /**
     * 查询用户MDO订购关系信息
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @param queryType
     * @param pagination
     * @return
     * @throws Exception
     * @author liuke
     */
    public IDataset queryUserMdoSvcInfo(IData input, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        String userId = input.getString("USER_ID");
        String queryType = "2";
        String qryId = SysDateMgr.getSysDate("yyyyMMddHH");
        String startDate = input.getString("START_DATE", "");
        String endDate = input.getString("END_DATE", "");
        String mdoQueryType = input.getString("MDO_QUERY_MODE", "");
        IDataset dataSet = MdoInfoQry.queryUserMdoSvcInfos(userId, queryType, qryId, startDate, endDate, mdoQueryType, pagination);
        if (IDataUtil.isEmpty(dataSet))
        {
            queryFromIBOSS(input);
        }
        // 再查询一次
        return MdoInfoQry.queryUserMdoSvcOtherInfos(userId, queryType, SysDateMgr.getSysDate("yyyyMMddHH"), startDate, endDate, mdoQueryType, pagination);
    }

    private void saveResult(IDataset sets, IData param) throws Exception
    {
        String qryId = SysDateMgr.getSysDate("yyyyMMddHH");// '本次查询ID,yyyymmddhh,同一小时的查询QRY_ID相同';
        String userId = param.getString("USER_ID");
        String serialNumber = param.getString("SERIAL_NUMBER");
        // 昨天以前的记录
        IData delParam = new DataMap();
        delParam.put("QRY_ID_OLD", SysDateMgr.getSysDate("yyyyMMdd"));
        delParam.put("QRY_ID", qryId);
        delParam.put("QUERY_TYPE", "2");
        delParam.put("USER_ID", userId);

        StringBuilder sb = new StringBuilder(200);
        sb.append(" DELETE FROM TF_F_MDO_QUERY_RESULT ");
        sb.append(" WHERE QRY_ID <:QRY_ID_OLD");
        sb.append(" OR (QRY_ID =:QRY_ID");
        sb.append(" AND QUERY_TYPE=:QUERY_TYPE");
        sb.append(" AND USER_ID=:USER_ID)");
        Dao.executeUpdate(sb, delParam);

        IData data = new DataMap();
        if (null == sets || sets.size() == 0)
        {
            data.put("USER_ID", userId);
            data.put("QUERY_TYPE", "2");// 查询类型 1：IVR拨打记录查询；2：订购关系查询
            data.put("QRY_ID", qryId);// '本次查询ID,yyyymmddhh,同一小时的查询QRY_ID相同';
            data.put("ID", "0");
            data.put("RSRV_STR1", param.getString("START_DATE"));
            data.put("RSRV_STR2", param.getString("END_DATE"));
            data.put("RSRV_STR3", param.getString("MDO_QUERY_MODE"));
            Dao.insert("TF_F_MDO_QUERY_RESULT", data);
        }
        else
        {
            for (int i = 0; i < sets.size(); i++)
            {
                data = sets.getData(i);
                data.put("USER_ID", userId);
                data.put("QUERY_TYPE", "2");// 查询类型 1：IVR拨打记录查询；2：订购关系查询
                data.put("QRY_ID", qryId);// '本次查询ID,yyyymmddhh,同一小时的查询QRY_ID相同';
                data.put("ID", param.getString("RSLTPAGENUM", param.getString("QUERYPAGENUM")) + String.valueOf(i));
                data.put("SERIAL_NUMBER", serialNumber);
                data.put("STARTTIME", data.getString("STARTTIME"));
                data.put("ENDTIME", data.getString("ENDTIME"));
                data.put("RSRV_STR1", param.getString("START_DATE"));
                data.put("RSRV_STR2", param.getString("END_DATE"));
                data.put("RSRV_STR3", param.getString("MDO_QUERY_MODE"));
                Dao.insert("TF_F_MDO_QUERY_RESULT", data);
            }
            
        }
    }
}
