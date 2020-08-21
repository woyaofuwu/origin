package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateIntfSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.VipSpeOpenIntfBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.ChangeSvcStateRegSVC;

/**
 * @author:YF
 * @Dat:   2013年12月20日   下午8:11:18 
 * @version: v1.0
 * @Description : TODO
 */
public class ChangeUserMobileBean extends CSBizBean {
	private static transient final Logger logger = Logger.getLogger(ChangeUserMobileBean.class);
	
	 /**
     * 停机接口 ：ITF_CRM_ModifyStopReg
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createStopReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>进入停机接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        this.checkState(input);

        input.put("TRADE_TYPE_CODE", "131");
        return this.tradeReg(input);
    }
    
    /**
     * 开机接口：ITF_CRM_ModifyOpenReg
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createOpenReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>进入开机接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        this.checkState(input);

        input.put("TRADE_TYPE_CODE", "133");
        return this.tradeReg(input);
    }
	
	
    /**
     * 最终调到服务状态变更类的业务登记服务上
     * 
     * @param input
     * @return
     * @throws Exception
     */
    private IDataset tradeReg(IData input) throws Exception
    {
        ChangeSvcStateRegSVC regSvc = new ChangeSvcStateRegSVC();
        return regSvc.tradeReg(input);
    }
	
	/**
	 * 客户有效性校验
	 * @Title : checkState
	 * @Description:TODO
	 * @Param : @param input
	 * @return: void
	 * @throws Exception 
	 */
	private void checkState(IData input) throws Exception{
		
		String serial_number = input.getString("SERIAL_NUMBER");
		
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number);
    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        
        String userid = userInfo.getString("USER_ID");
        String identCode = input.getString("IDENT_CODE");
		String contactId = input.getString("CONTACT_ID");
		
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userid, identCode, contactId);
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}
		
	}
}
