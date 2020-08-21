
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemodifyacct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class CttBroadBandModifyAcctSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private void AddWidenetRule(IData addrInfo) throws Exception
    {
        IDataset paramset = ParamInfoQry.getCommparaByCode("CSM", "1311", "", CSBizBean.getTradeEparchyCode());
        int count = 1;
        String acctNo = "";
        if (!paramset.isEmpty())
            count = paramset.getData(0).getInt("PARA_CODE1", 1);
        for (int i = 0; i < count; i++)
        {
            paramset.clear();
            IData temp = new DataMap();
            paramset = ParamInfoQry.getCommparaByCode("CSM", "1310", "-1", CSBizBean.getTradeEparchyCode());
            if (paramset.isEmpty())
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1003, "该地州[" + CSBizBean.getTradeEparchyCode() + "]没有配置账号规则");
            }
            if (!paramset.getData(0).getString("PARA_CODE2").equals(""))
            {
                acctNo = paramset.getData(0).getString("PARA_CODE1") + "";
                addrInfo.put("ACCT_PREFIX", paramset.getData(0).getString("PARA_CODE1"));// 指定以什么数字开头
                addrInfo.put("ACCT_TAIL", paramset.getData(0).getString("PARA_CODE2"));// 指定的长度
            }
            addrInfo.put("NEW_ACCESS_ACCT", acctNo);// 帐号

        }
    }

    public IDataset checkWidenetAcctId(IData param) throws Exception
    {
        String newAcctId = param.getString("NEW_ACCESS_ACCT");
        IDataset wideNetAcctList = WidenetInfoQry.getUserWidenetActInfos(newAcctId);
        if (IDataUtil.isNotEmpty(wideNetAcctList))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_27);
        }
        wideNetAcctList.clear();
        wideNetAcctList = TradeInfoQry.qryWidenetAcctIdTrade(newAcctId);
        if (IDataUtil.isNotEmpty(wideNetAcctList))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_27);
        }
        int num = BroadBandInfoQry.checkAccountIdInAccountIp("0", newAcctId);
        if (num > 0)
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_103);
        }
        IDataset result = new DatasetList();
        return result;
    }

    public IDataset getOldBroadBandInfo(IData param) throws Exception
    {

        IDataset addrInfoDataset = WidenetInfoQry.getUserWidenetInfo(param.getString("USER_ID"));
        if (addrInfoDataset.isEmpty())
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_13, param.getString("SERIAL_NUMBER"));
        }
        IData addrInfo = addrInfoDataset.getData(0);

        IDataset accessAcctInfos = BroadBandInfoQry.getBroadBandWidenetActByUserId(param.getString("USER_ID"));
        if (accessAcctInfos.isEmpty())
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_11, param.getString("SERIAL_NUMBER"));
        }
        IData accessAcctInfo = accessAcctInfos.getData(0);

        addrInfo.put("ACCESS_ACCT", accessAcctInfo.getString("ACCESS_ACCT"));
        AddWidenetRule(addrInfo);
        IDataset resultDataset = new DatasetList();
        resultDataset.add(addrInfo);
        return resultDataset;
    }
}
