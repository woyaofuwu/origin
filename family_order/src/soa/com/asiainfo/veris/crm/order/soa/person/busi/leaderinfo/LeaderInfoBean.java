package com.asiainfo.veris.crm.order.soa.person.busi.leaderinfo;



import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class LeaderInfoBean extends CSBizBean
{
	/**
	 * 功能：查询  by mengqx
	 * @param data
	 * @param page
	 * @return
	 * @throws Exception
	 */
	  public IDataset queryLeaderInfo(IData data, Pagination page) throws Exception
	    {

		  	String leaderName = data.getString("LEADER_NAME", "");
			String serialNumber = data.getString("SERIAL_NUMBER", "");
			String position = data.getString("POSITION", "");

			IData params = new DataMap();
			params.put("LEADER_NAME", leaderName);
			params.put("SERIAL_NUMBER", serialNumber);
			params.put("POSITION", position);
			return Dao.qryByCode("TF_B_LEADER_INFO", "SEL_BY_SN", params, page);
	    }
	  
	  /**
	     * 功能：新增   by mengqx
	     */
	    public void insertLeaderInfo(IData inparams) throws Exception
	    {
	    	Dao.executeUpdateByCodeCode("TF_B_LEADER_INFO", "INS_INFO", inparams);
	    }
	    
	    /**
	     * 功能：更新
	     * @param param
	     * @return
	     * @throws Exception
	     */
	    public void updateLeaderInfo(IData inparams) throws Exception
	    {
	    	Dao.executeUpdateByCodeCode("TF_B_LEADER_INFO", "UPD_BY_SN", inparams);
	    }
	    
	    /**
	     * 功能：删除
	     */
	    public void deleteLeaderInfo(IData inparams) throws Exception
	    {
	    	Dao.executeUpdateByCodeCode("TF_B_LEADER_INFO", "DEL_BY_SN", inparams);
	    }
	    
	    /**
	     * 功能：号码校验 & 权限判断
	     */
	    public IDataset checkBySerialNumberAndPermission(IData inparams) throws Exception
	    {
	    	return Dao.qryByCode("TF_B_LEADER_INFO", "SEL_BY_SN", inparams);
	    }
	  
	 
}