
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.mdo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustServException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MdoInfoQry;

public class QueryMdoIvrInfoBean extends CSBizBean
{
    // YYYYMMDD＋CSVC＋3位省代码＋7位流水号;
    private String getOperNumb(String trade_id) throws Exception
    {
        String opernumb;
        String prov_code = getProvCode();
        opernumb = SysDateMgr.getSysDate("yyyymmdd") + "CSVC" + prov_code.substring(prov_code.length() - 3) + trade_id.substring(trade_id.length() - 7);
        return opernumb;
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
        String provCode = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
        { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
        { "PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode() });
        if (provCode == null || provCode.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_310);
        }
        return provCode;
    }

    private void parseRslList(String content, IDataset list)
    {
        if (StringUtils.isNotBlank(content))
        {
            String[] resultListArray = content.split("€€");
            for (int j = 0; j < resultListArray.length; j++)
            {
                if (StringUtils.isNotBlank(resultListArray[j]))
                {
                    String[] resultArray = resultListArray[j].split("\\|");

                    IData result = new DataMap();
                    result.put("SERVICENAME", resultArray[1]);
                    result.put("NUM", resultArray[2]);
                    result.put("PROVINCENAME", resultArray[3]);
                    result.put("RECEIVENUM", resultArray[4]);
                    result.put("SPNAME", resultArray[5]);
                    result.put("SPCSTEL", resultArray[6]);
                    result.put("CALSTARTTIME", resultArray[7]);
                    result.put("CALENDTIME", resultArray[8]);
                    result.put("DURTIME", resultArray[9]);
                    result.put("FEETYPE", resultArray[10]);
                    result.put("SERFEE", resultArray[11]);
                    result.put("INFOFEE", resultArray[12]);

                    list.add(result);
                }

            }
        }
    }

    private IData queryFromIBOSS(IData param) throws Exception
    {
        String trade_id = SeqMgr.getTradeId();
        String kindId = "BIP2B310_T2001310_0_0";
        String routeType = "00";
        String routeValue = "000";
        String serialNumber = param.getString("SERIAL_NUMBER");
        String operNunb = getOperNumb(trade_id);
        String exBeginTime = param.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER;
        String exEndTime = param.getString("END_DATE") + SysDateMgr.END_DATE;
        String oprSource = param.getString("OPR_SOURCE", "08");
        String custName = param.getString("CUST_NAME");
        String provCode = getProvCode();
        IData ret = IBossCall.queryMdoIvrOneIBOSS(kindId, serialNumber, routeType, routeValue, operNunb, exBeginTime, exEndTime, oprSource, custName, provCode);
        if ("0".equals(ret.getString("X_RSPTYPE")) && "0000".equals(ret.getString("X_RSPCODE")))
        {
            String resultPage = ret.getString("RSLTTOTALPAGES");
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
                // dataset = DataHelper.toDataset(ret);
                String content = ret.getString("QRYRSLTLIST");
                this.parseRslList(content, dataset);
                // if (dataset.size() == 0)
                // {
                // dataset.add(ret);
                // }
                // 分页查询剩余记录
                for (int i = 2; i <= resultSum; i++)
                {
                    kindId = "BIP2B320_T2001320_0_0";
                    int queryPageNum = i;
                    IData resultData = IBossCall.queryMdoIvrTwoIBOSS(kindId, serialNumber, routeType, routeValue, operNunb, exBeginTime, exEndTime, oprSource, custName, provCode, queryPageNum);
                    // 经IBOSS人员确认，目前因为平台原因，无法调试IBOSS接口,故此自己这边默认调用成功，自己造报文处理。
                    IDataset sets = new DatasetList();
                    if ("0".equals(resultData.getString("X_RSPTYPE")) && "0000".equals(resultData.getString("X_RSPCODE")))
                    {
                        // sets = DataHelper.toDataset(resultData);
                        content = resultData.getString("QRYRSLTLIST");
                        this.parseRslList(content, sets);

                        if (sets.size() == 0)
                        {
                            sets.add(resultData);
                        }
                    }
                    else
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_380, resultData.getString("X_RSPDESC"));
                    }
                    dataset.addAll(sets);
                }
                // 查询服务归档
                kindId = "BIP2B322_T2001322_0_0";
                IData retGD = IBossCall.queryMdoIvrThreeIBOSS(kindId, serialNumber, routeType, routeValue, operNunb, exBeginTime, exEndTime, oprSource, custName, provCode);
                saveResult(dataset, param);
            }
            else if (resultSum == 1)
            {
                dataset = DataHelper.toDataset(ret);
                if (dataset.size() == 0)
                    dataset.add(ret);
                saveResult(dataset, param);
            }
            else
            {
                saveResult(null, param);
            }
        }
        else
        {
            CSAppException.apperr(CustServException.CRM_CUSTSERV_1, ret.getString("X_RSPDESC"));
        }
        return ret;
    }

    /**
     * IVR拨打记录查询
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @param pagination
     * @return
     * @throws Exception
     * @author xiekl
     */
    public IDataset queryUserMdoIvrInfo(IData input, Pagination pagination) throws Exception
    {
        String userId = input.getString("USER_ID");
        String qryId = SysDateMgr.getSysDate("yyyyMMddHH");
        String startDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");
        IDataset dataSet = MdoInfoQry.getMdoIvrOneInfo(userId, qryId, startDate, endDate, pagination);
        if (dataSet == null || dataSet.size() == 0)
        {
            queryFromIBOSS(input);
        }
        else
        {
            return dataSet;
        }
        // 再查询一次
        qryId = SysDateMgr.getSysDate("yyyyMMddHH");
        return MdoInfoQry.getMdoIvrTwoInfo(userId, qryId, startDate, endDate, pagination);
    }

    private void saveResult(IDataset sets, IData param) throws Exception
    {
        IData data = new DataMap();
        String qryId = SysDateMgr.getSysDate("yyyyMMddHH");// '本次查询ID,yyyymmddhh,同一小时的查询QRY_ID相同';
        // 昨天以前的记录
        IData params = new DataMap();
        params.put("QRY_ID_ONE", SysDateMgr.getSysDate("yyyyMM"));
        params.put("QRY_ID_TWO", qryId);
        params.put("USER_ID", param.getString("USER_ID"));
        Dao.executeUpdateByCodeCode("TF_F_MDO_QUERY_RESULT", "DEL_MDO_QRY", params);

        if (sets == null || sets.size() == 0)
        {
            data.put("USER_ID", param.getString("USER_ID"));
            data.put("QUERY_TYPE", "1");// 查询类型 1：IVR拨打记录查询；2：订购关系查询
            data.put("QRY_ID", qryId);// '本次查询ID,yyyymmddhh,同一小时的查询QRY_ID相同';
            data.put("ID", "0");
            data.put("RSRV_STR1", param.getString("START_DATE"));
            data.put("RSRV_STR2", param.getString("END_DATE"));
            boolean dealFlag = Dao.save("TF_F_MDO_QUERY_RESULT", data);
            if (!dealFlag)
            {
                Dao.insert("TF_F_MDO_QUERY_RESULT", data);
            }
        }
        else
        {
            for (int i = 0; i < sets.size(); i++)
            {
                data = sets.getData(i);
                data.put("USER_ID", param.getString("USER_ID"));
                data.put("QUERY_TYPE", "1");// 查询类型 1：IVR拨打记录查询；2：订购关系查询
                data.put("QRY_ID", qryId);// '本次查询ID,yyyymmddhh,同一小时的查询QRY_ID相同';
                data.put("ID", data.getString("RSLTPAGENUM", data.getString("QUERYPAGENUM", "")) + data.getString("ID", String.valueOf(i)));
                data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
                data.put("STARTTIME", data.getString("CALSTARTTIME"));
                data.put("ENDTIME", data.getString("CALENDTIME"));
                data.put("RSRV_STR1", param.getString("START_DATE"));
                data.put("RSRV_STR2", param.getString("END_DATE"));
            }
            Dao.insert("TF_F_MDO_QUERY_RESULT", sets);
        }
    }
}
