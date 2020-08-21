package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformReleBeanSVC  extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 ** 根据SUB_BUSIFORM_ID 查询父子流程关系信息
	 * @param subBusiformId
	 * @return
	 * @throws Exception
	 * @Date 2019年10月28日
	 * @author xieqj 
	 */
	public IDataset qryBySubBusiformId(IData param) throws Exception {
		String subBusiformId = param.getString("SUB_BUSIFORM_ID");
        return WorkformReleBean.qryBySubBusiformId(subBusiformId);
    }
	
	/**
	 ** 根据 IBSYSID ，RECORD_NUM，STATUS 查询父子流程关系信息
	 * @param param (String Ibsysid, String recodeNum, String status)
	 * @return
	 * @throws Exception
	 * @Date 2019年10月28日
	 * @author xieqj 
	 */
	public IDataset qryReleByIbsysidRecordnum(IData param) throws Exception {
		String Ibsysid = param.getString("IBSYSID");
		String recodeNum = param.getString("RECORD_NUM");
		String status = param.getString("STATUS");
		return WorkformReleBean.qryReleByIbsysidRecordnum(Ibsysid, recodeNum, status);
	}

}
