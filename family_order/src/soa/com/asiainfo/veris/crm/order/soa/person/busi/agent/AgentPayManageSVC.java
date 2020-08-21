/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.agent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.agent.AgentBankAcctInfoQry;

public class AgentPayManageSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 删除银行账户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset deleteAgentInfo(IData input) throws Exception
    {
        IDataset resultSet = new DatasetList();
        AgentPayManageBean bean = BeanManager.createBean(AgentPayManageBean.class);
        resultSet.add(bean.deleteAgentInfo(input));
        return resultSet;
    }

    /**
     * 获取某个代理商银行账户信息
     * 
     * @param input
     *            [AGENT_ID]
     * @return
     * @throws Exception
     */
    public IDataset getAgentPayInfo(IData input) throws Exception
    {
        AgentPayManageBean bean = BeanManager.createBean(AgentPayManageBean.class);
        return bean.getAgentPayInfo(input);
    }

    /**
     * 导入代理商银行账户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset importAgentPayInfos(IData input) throws Exception
    {
        IDataset resultSet = new DatasetList();
        setUserEparchyCode(CSBizBean.getTradeEparchyCode());
        AgentPayManageBean bean = BeanManager.createBean(AgentPayManageBean.class);
        resultSet.add(bean.importAgentPayInfos(input));
        return resultSet;
    }

    /**
     * 查询供应商业务区
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAgentCityCode(IData input) throws Exception
    {
        AgentPayManageBean bean = BeanManager.createBean(AgentPayManageBean.class);
        return bean.qryAgentCityCode(input);
    }

    /**
     * 查询代理商银行账户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryAgentPayInfos(IData input) throws Exception
    {
        String cityCode = input.getString("CITY_CODE");
        String startCode = input.getString("START_AGENT_CODE");
        String endCode = input.getString("END_AGENT_CODE");
        String acctCode = input.getString("ACCOUNT_CODE");
        String removeTag = input.getString("REMOVE_TAG");
        String payChannel = input.getString("PAY_CHANNEL");
        String acctype = input.getString("RSRV_STR1");
        return AgentBankAcctInfoQry.queryAgentPayInfos(acctype,cityCode, startCode, endCode, acctCode, removeTag, payChannel, getPagination(), null);
    }

    /**
     * 保存新增或修改的代理商银行账户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset saveAgentPayInfo(IData input) throws Exception
    {
        IDataset resultSet = new DatasetList();
        AgentPayManageBean bean = BeanManager.createBean(AgentPayManageBean.class);
        resultSet.add(bean.saveAgentPayInfo(input));
        return resultSet;
    }

}
