package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;

public class GrpEinvoiceBean extends CSBizBean
{
    // 根据集团标识查询历史信息
    public static IDataset queryByGroupId(IData data) throws Exception
    {
        // 先根据group_id查询cust_id
        IDataset grpInfos = GrpInfoQry.qryGrpInfoByGroupIdAndRemoveTag(data.getString("GROUP_ID"), "0");
        if (IDataUtil.isEmpty(grpInfos))
        {
            return new DatasetList();
        }
        
        IData params = new DataMap();
        params.put("CUST_ID", grpInfos.getData(0).getString("CUST_ID"));
        params.put("START_DATE", data.getString("START_DATE"));
        params.put("END_DATE", data.getString("END_DATE"));
        params.put("PRINT_FLAG", data.getString("PRINT_FLAG"));
        return Dao.qryByCodeParser("TF_B_PRINTPDF_LOG", "SEL_GRPEINVOICE_BY_CUSTID", params, Route.getJourDb("0898"));
    }

    /**
     * 电子发票冲红
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset modifyEInvoiceTrade(IData data) throws Exception
    {
        IData param = new DataMap();
        String custId = data.getString("CUST_ID");
        String acctId = data.getString("ACCT_ID");
        param.put("REQUEST_ID", data.getString("PRINT_ID"));
        // param.put("ACCT_ID", acctId);
        // param.put("CUST_ID", custId);//按用户设置，custId和acctId不传到账管
        param.put("USER_ID", data.getString("USER_ID"));
        String NewPrintId = SeqMgr.getPrintId();
        param.put("PRINT_ID", NewPrintId);
        IDataset resultset = CSAppCall.call("TAM_ELECNOTE_CANCELNOTE", param);// 发票冲红接口
        if (IDataUtil.isEmpty(resultset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "账务冲红接口返回空！");
        }
        else
        {
            if ("0000".equals(resultset.getData(0).getString("RESULT_CODE")))
            {
                if (StringUtils.isNotBlank(resultset.getData(0).getString("REQUEST_ID")))
                {
                    IData updParam = new DataMap();
                    // 电子发票日志表中没有票据日志表中类似cancel等字段，所以只需修改reprint_flag字段
                    updParam.put("TRADE_ID", data.getString("TRADE_ID"));
                    updParam.put("RSRV_INFO1", NewPrintId);
                    updParam.put("PRINT_ID", resultset.getData(0).getString("REQUEST_ID"));
                    Dao.executeUpdateByCodeCode("TF_B_PRINTPDF_LOG", "UPD_BY_TRADE_ID", updParam,Route.getJourDb(BizRoute.getRouteId()));
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "冲红失败:账务返回冲红发票唯一流水为空");
                }
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "冲红失败:" + resultset.getData(0).getString("RESULT_INFO"));
            }
        }
        return null;
    }

}
