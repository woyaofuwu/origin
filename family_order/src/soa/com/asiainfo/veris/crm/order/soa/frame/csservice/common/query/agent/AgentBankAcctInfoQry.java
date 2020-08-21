
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.agent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class AgentBankAcctInfoQry
{

    /**
     * 根据手机号码查询代理商银行账户信息
     * 
     * @param sn
     * @param removeTag
     * @return
     * @throws Exception
     */
    public static IDataset queryAgentPayByAgentCode(String agentCode, String removeTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("AGENT_CODE", agentCode);
        inData.put("REMOVE_TAG", removeTag);
        return Dao.qryByCode("TF_F_AGENTBANK_ACCOUNTINFO", "SEL_AGENTPAY_BY_AGENT_CODE", inData);
    }

    /**
     * 根据AGENT_ID查询代理商银行账户信息
     * 
     * @param agentId
     * @return
     * @throws Exception
     */
    public static IDataset queryAgentPayByPk(String agentId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("AGENT_ID", agentId);
        return Dao.qryByCode("TF_F_AGENTBANK_ACCOUNTINFO", "SEL_AGENTPAY_BY_PK", inData);
    }

    /**
     * 查询代理商银行账户信息
     * 
     * @param cityCode
     * @param startAgentCode
     * @param endAgentCode
     * @param accountCode
     * @param removeTag
     * @param payChannel
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryAgentPayInfos(String acctype,String cityCode, String startAgentCode, String endAgentCode, String accountCode, String removeTag, String payChannel, Pagination pagination, String routeId) throws Exception
    {
        IData inData = new DataMap();
        
        if (StringUtils.isNotBlank(acctype))
        {
        	inData.put("RSRV_STR1", acctype);
        }
        if (StringUtils.isNotBlank(cityCode))
        {
            inData.put("CITY_CODE", cityCode);
        }
        if (StringUtils.isNotBlank(startAgentCode))
        {
            inData.put("START_AGENT_CODE", startAgentCode);
        }
        if (StringUtils.isNotBlank(endAgentCode))
        {
            inData.put("END_AGENT_CODE", endAgentCode);
        }
        if (StringUtils.isNotBlank(accountCode))
        {
            inData.put("ACCOUNT_CODE", accountCode);
        }
        if (StringUtils.isNotBlank(removeTag))
        {
            inData.put("REMOVE_TAG", removeTag);
        }
        if (StringUtils.isNotBlank(payChannel))
        {
            inData.put("PAY_CHANNEL", payChannel);
        }
        if (StringUtils.isBlank(routeId))
        {
            routeId = CSBizBean.getTradeEparchyCode();
        }

        return Dao.qryByCodeParser("TF_F_AGENTBANK_ACCOUNTINFO", "SEL_AGENTPAY_FOR_MANAGE", inData, pagination, routeId);
    }

    public static IDataset queryNotSelfAgentPay(String agentId, String agentCode, String removeTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("AGENT_ID", agentId);
        inData.put("AGENT_CODE", agentCode);
        inData.put("REMOVE_TAG", removeTag);
        return Dao.qryByCode("TF_F_AGENTBANK_ACCOUNTINFO", "SEL_NOTSELF_AGENTPAY", inData);
    }
    
    
    /**
     * 查询代理商银行账户信息
     * @return
     * @throws Exception
     */
    public static IDataset queryAgentPayInfos_1(IData input, Pagination pagination) throws Exception
    {
    	//System.out.println("AgentBankAcctInfoQry-queryAgentPayInfos_1xxxxxxxxxxxxxxxx115 "+input);
        return Dao.qryByCodeParser("TL_O_FEEAGENTLOG", "SEL_AGENTPAY", input, pagination);
    }
    
    /*
     * 更新状态
     */
    public static int modAgentPayInfoState(IData input) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TL_O_FEEAGENTLOG", "UPDATE_AGENTPAY_STATE", input);
    }
}
