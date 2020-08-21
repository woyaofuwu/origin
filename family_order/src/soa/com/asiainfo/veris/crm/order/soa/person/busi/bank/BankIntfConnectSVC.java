
package com.asiainfo.veris.crm.order.soa.person.busi.bank;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class BankIntfConnectSVC extends CSBizService
{

	private static Logger logger = Logger.getLogger(BankIntfConnectSVC.class);

    private static final long serialVersionUID = 1L;

    /**
	 * 手机号码和银行卡绑定服务
	 * @param pd
	 * @param param
	 * @throws Exception
	 * @author wukw3
	 */ 
    public IData bankBind(IData input) throws Exception {
    	
    	BankIntfConnectBean bean = (BankIntfConnectBean) BeanManager.createBean(BankIntfConnectBean.class);
    	
    	if(logger.isDebugEnabled()){
    		logger.debug(input.toString());
    	}
    	
        IData ret = bean.bankBindRegister(input);
        
        return ret;
    	
    }
    
    /**
	 * 手机号码和银行卡解绑服务
	 * @param pd
	 * @param param
	 * @throws Exception
	 * @author wukw3
	 */ 
    public IData bankCancelBind(IData input) throws Exception {
    	
    	BankIntfConnectBean bean = (BankIntfConnectBean) BeanManager.createBean(BankIntfConnectBean.class);
    	
    	if(logger.isDebugEnabled()){
    		logger.debug(input.toString());
    	}
    	
        IData ret = bean.UnbankBindRegister(input);
        
        return ret;
    	
    }
    
    /**
	 * 网厅手机号码和银行卡绑定接口
	 * @author wukw3
	 * @param pd
	 * @param inparam
	 * @return resultInfo
	 * @throws Exception
	 */
	public IData bankNetBind(IData inparam) throws Exception {
		IData resultInfo = new DataMap();
		IData tempData = new DataMap();
		
		if(logger.isDebugEnabled()){
    		logger.debug(inparam.toString());
    	}
		
/*		if("".equals(inparam.getString("PASS_WORD",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "206:登录密码为必传");
		}
		
		String password = inparam.getString("PASS_WORD","");
		if(password.length() < 6 || password.length() > 20){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "请输入最小6位，最大20位的密码！");
		}
		
		String reg = "^[0-9]*$";
		if(password.matches(reg)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "密码不能全是数字！");
		}
		reg = "^[A-Za-z]+$";
		if(password.matches(reg)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "密码不能全是字母！");
		}
		reg = "^[A-Za-z0-9]+$";
		if(!password.matches(reg)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "密码由字母和数字组成，不能全为字母和数字！");
		}*/
		
		if("".equals(inparam.getString("SERIAL_NUMBER",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "206:手机号码为必传");
		}
		
		if("".equals(inparam.getString("BANK_NAME",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "206:银行开户行为必传");
		}
		
		if("".equals(inparam.getString("BANK_CARD_NO",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "206:银行卡号为必传");
		}
		tempData.put("BANK_CARD_NO", inparam.getString("BANK_CARD_NO",""));
		tempData.put("PASS_WORD", inparam.getString("PASS_WORD",""));
		tempData.put("BANK_NAME", inparam.getString("BANK_NAME",""));
		
		tempData.putAll(inparam);
		
		IDataset dataset = CSAppCall.call("SS.BankBindDealRegSVC.tradeReg", tempData);
		
		return dataset.getData(0);
	}
	
	/**
	 * 网厅手机号码和银行卡解绑接口
	 * @author wukw3
	 * @param pd
	 * @param inparam
	 * @return resultInfo
	 * @throws Exception
	 */
	public IData bankNetCancelBind(IData inparam) throws Exception {
		IData resultInfo = new DataMap();
		IData tempData = new DataMap();
		
		if(logger.isDebugEnabled()){
    		logger.debug(inparam.toString());
    	}
		
		if("".equals(inparam.getString("SERIAL_NUMBER",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "206:手机号码为必传");
		}
		
		if("".equals(inparam.getString("BANK_NAME",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "206:银行开户行为必传");
		}
		
		if("".equals(inparam.getString("BANK_CARD_NO",""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "206:银行卡号为必传");
		}
		tempData.put("BANK_CARD_NO", inparam.getString("BANK_CARD_NO",""));
		tempData.put("PASS_WORD", inparam.getString("LOGIN_PASSWD",""));
		tempData.put("BANK_NAME", inparam.getString("BANK_NAME",""));
		
		tempData.putAll(inparam);
		
		IDataset dataset = CSAppCall.call("SS.BankCancelBindDealRegSVC.tradeReg", tempData);
		
		return dataset.getData(0);

	}
	
    /**
	 * 手机号码和银行卡解绑服务
	 * @param pd
	 * @param param
	 * @throws Exception
	 * @author wukw3
	 */ 
    public IData QueryUserScore(IData input) throws Exception {
    	
    	BankIntfConnectBean bean = (BankIntfConnectBean) BeanManager.createBean(BankIntfConnectBean.class);
    	
    	if(logger.isDebugEnabled()){
    		logger.debug(input.toString());
    	}
    	
        IData ret = null;//bean.QueryUserScore(input);
        
        return ret;
    	
    }

}
