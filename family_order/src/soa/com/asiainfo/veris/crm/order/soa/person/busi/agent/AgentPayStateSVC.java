/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.agent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.agent.AgentBankAcctInfoQry;

public class AgentPayStateSVC extends CSBizService {

	private static final long serialVersionUID = 1L;

	public IDataset queryAgentPayInfos(IData input) throws Exception {

		//System.out.println("AgentPayStateSVCxxxxxxxxxxxxxxxx94 " + input);
		return AgentBankAcctInfoQry.queryAgentPayInfos_1(input, getPagination());
	}

	public IDataset modAgentPayInfoState(IData input) throws Exception {
		IDataset resultSet = new DatasetList();
		AgentPayStateBean bean = BeanManager.createBean(AgentPayStateBean.class);
		resultSet.add(bean.modAgentPayInfoState(input));
		return resultSet;
	}

}
