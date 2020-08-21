
package com.asiainfo.veris.crm.order.soa.group.cenpayspecial;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CenpaySpecialMgrSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 自由充产品名单管理分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryCenpaySpecialList(IData inParam) throws Exception
    {
    	CenpaySpecialMgrBean bean = new CenpaySpecialMgrBean();
        return bean.queryCenpaySpecialList(inParam, getPagination());
    }
    
    /**
     * 根据UserId查询记录
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryCenpaySpecialByUserId(IData inParam) throws Exception
    {
    	CenpaySpecialMgrBean bean = new CenpaySpecialMgrBean();
        return bean.queryCenpaySpecialByUserId(inParam);
    }
    
    /**
	 * 根据user_id删除记录
	 * @param input
	 * @throws Exception
	 */
	public void delCenpaySpecialByUserId(IData input) throws Exception {
		CenpaySpecialMgrBean bean = new CenpaySpecialMgrBean();
		bean.delCenpaySpecialByUserId(input);
	}
	
	/**
	 * 根据user_id修改记录
	 * @param input
	 * @throws Exception
	 */
	public void updateCenpaySpecialByUserId(IData input) throws Exception {
		CenpaySpecialMgrBean bean = new CenpaySpecialMgrBean();
		bean.updateCenpaySpecialByUserId(input);
	}
	
	/**
	 * 新增特权用户
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData addCenpaySpecial(IData input) throws Exception {
		CenpaySpecialMgrBean bean = new CenpaySpecialMgrBean();
		boolean resultFlag = bean.addCenpaySpecial(input);
		IData inputData = new DataMap();
		inputData.put("RESULT_FLAG", resultFlag);
		return inputData;
	}
	
}
