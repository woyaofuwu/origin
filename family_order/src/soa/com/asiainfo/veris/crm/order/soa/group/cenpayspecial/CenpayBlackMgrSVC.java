
package com.asiainfo.veris.crm.order.soa.group.cenpayspecial;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


public class CenpayBlackMgrSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 自由充产品黑名单用户管理分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryCenpayBlackList(IData inParam) throws Exception
    {
    	CenpayBlackMgrBean bean = new CenpayBlackMgrBean();
        return bean.queryCenpayBlackList(inParam, getPagination());
    }
    
    /**
     * 根据UserId查询记录
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryCenpayBlackByUserId(IData inParam) throws Exception
    {
    	CenpayBlackMgrBean bean = new CenpayBlackMgrBean();
        return bean.queryCenpayBlackByUserId(inParam);
    }
    
    /**
	 * 根据user_id删除记录
	 * @param input
	 * @throws Exception
	 */
	public void delCenpayBlackByUserId(IData input) throws Exception {
		CenpayBlackMgrBean bean = new CenpayBlackMgrBean();
		bean.delCenpayBlackByUserId(input);
	}
	
	/**
	 * 根据user_id修改记录
	 * @param input
	 * @throws Exception
	 */
	public void updateCenpayBlackByUserId(IData input) throws Exception {
		CenpayBlackMgrBean bean = new CenpayBlackMgrBean();
		bean.updateCenpayBlackByUserId(input);
	}
	
	/**
	 * 新增特权用户
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData addCenpayBlack(IData input) throws Exception {
		CenpayBlackMgrBean bean = new CenpayBlackMgrBean();
		boolean resultFlag = bean.addCenpayBlack(input);
		IData inputData = new DataMap();
		inputData.put("RESULT_FLAG", resultFlag);
		return inputData;
	}
	
}
