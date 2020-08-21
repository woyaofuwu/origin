/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.blackusermanager;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @CREATED by gongp@2014-6-19 修改历史 Revision 2014-6-19 下午07:11:23
 */
public class BlackUserManageSVC extends CSBizService
{
    private static final long serialVersionUID = 6328471594629158805L;

    /**
     * 黑名单管理
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-19
     */
    public IData blackUserManagement(IData input) throws Exception
    {

        BlackUserManagerBean bean = BeanManager.createBean(BlackUserManagerBean.class);

        return bean.blackUserManagement(input);
    }

    /**
     * 黑名单管理接口（接口名: ITF_CRM_BLACK_MANAGER）
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-20
     */
    public IData blackUserManagementIntf(IData input) throws Exception
    {

        BlackUserManagerBean bean = BeanManager.createBean(BlackUserManagerBean.class);

        return bean.blackUserManagementIntf(input);
    }

    /**
     * 黑名单查询
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-19
     */
    public IDataset qryBlackUserList(IData input) throws Exception
    {
        BlackUserManagerBean bean = BeanManager.createBean(BlackUserManagerBean.class);

        return bean.queryBlackUserList(input);
    }
    
    /**
     * REQ201606300007 关于增加系统黑名单后台查询日志的需求
     * chenxy 20160706 
     * 插黑名单日志表
     */
    public IData insertBlackUserCheckLogInfo(IData input) throws Exception
    {

        BlackUserManagerBean bean = BeanManager.createBean(BlackUserManagerBean.class); 
        bean.insertBlackUserCheckLogInfo(input);
        return new DataMap();
    }
    

    /**
     * REQ201606300007 关于增加系统黑名单后台查询日志的需求
     * chenxy 20160706 
     * 黑名单用户日志查询
     * SS.BlackUserManageSVC.qryBlackUserLog
     */
    public IDataset qryBlackUserLog(IData inparams) throws Exception
    { 
    	IDataset qryList= BlackUserManagerBean.qryBlackUserLog(inparams,getPagination());
    	//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ITEM", "ITEM_ID", "ITEM_NAME", map.getString("PARA_CODE5"))
    	for(int k=0;k<qryList.size();k++){
    		IData logData=qryList.getData(k);
    		String inModeCode=logData.getString("IN_MODE_CODE","");
    		String inModeCodeCHN="";
    		if(!"".equals(inModeCode)){
    			inModeCodeCHN=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE", inModeCode);
    			logData.put("IN_MODE_CODE_CHN", inModeCodeCHN);
    		}
    		String staffId=logData.getString("UPDATE_STAFF_ID","");
    		String staffIdCHN="";
    		if(!"".equals(staffId)){
    			staffIdCHN=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
    			logData.put("STAFF_ID_CHN", staffIdCHN);
    		}
    		String departId=logData.getString("UPDATE_DEPART_ID","");
    		String departIdCHN="";
    		if(!"".equals(departId)){
    			departIdCHN=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId);
    			logData.put("DEPART_ID_CHN", departIdCHN);
    		}
    		String tradeTypeCode=logData.getString("TRADE_TYPE_CODE","");
    		String tradeTypeCodeCHN="";
    		if(!"".equals(tradeTypeCode)){
    			tradeTypeCodeCHN=StaticUtil.getStaticValue("BLACK_TRADE_TYPE", tradeTypeCode);
    			logData.put("TRADE_TYPE_CODE_CHN", tradeTypeCodeCHN);
    		}
    	}
    	return qryList;
    }
}
