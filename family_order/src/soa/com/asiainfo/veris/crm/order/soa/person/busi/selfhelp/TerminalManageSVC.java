
package com.asiainfo.veris.crm.order.soa.person.busi.selfhelp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 自助终端服务接口类
 * 
 * @author zhouwu
 * @date 2014-07-29 20:58:17
 */
public class TerminalManageSVC extends CSBizService
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 9040646223307939753L;

	/**
     * 查询归属业务区
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryCityInfos(IData data) throws Exception
    {
        TerminalManageBean bean = (TerminalManageBean) BeanManager.createBean(TerminalManageBean.class);
        return bean.queryUnionCancelInfos(data);
    }

    /**
     * 自助终端资料查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryTerminals(IData data) throws Exception
    {
        TerminalManageBean bean = (TerminalManageBean) BeanManager.createBean(TerminalManageBean.class);
        return bean.queryTerminals(data, getPagination());
    }

    /**
     * 自助终端资料新增
     * 
     * @param data
     * @throws Exception
     */
    public void saveTerminal(IData data) throws Exception
    {
        TerminalManageBean bean = (TerminalManageBean) BeanManager.createBean(TerminalManageBean.class);
        bean.saveTerminal(data);
    }

    /**
     * 自助终端资料删除
     * 
     * @param data
     * @throws Exception
     */
    public void deleteTerminal(IData data) throws Exception
    {
        TerminalManageBean bean = (TerminalManageBean) BeanManager.createBean(TerminalManageBean.class);
        bean.deleteTerminal(data);
    }
    
    /**
     * 自助终端单个资料查询
     * 
     * @param data
     * @throws Exception
     */
    public IDataset queryTerminal(IData data) throws Exception
    {
        TerminalManageBean bean = (TerminalManageBean) BeanManager.createBean(TerminalManageBean.class);
        return bean.queryTerminal(data, getPagination());
    }
    
    /**
     * 自助终端保存费用信息
     * @param data
     * @return
     * @throws Exception
     */
    public IData saveTerminalFee(IData data) throws Exception
    {
    	TerminalManageBean bean = (TerminalManageBean) BeanManager.createBean(TerminalManageBean.class);
        return bean.saveTerminalFee(data);
    }
    
    /**
     * 加载打印信息
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset loadPrintData(IData data) throws Exception
    {
    	TerminalManageBean bean = (TerminalManageBean) BeanManager.createBean(TerminalManageBean.class);
        return bean.loadPrintData(data);
    }
}
