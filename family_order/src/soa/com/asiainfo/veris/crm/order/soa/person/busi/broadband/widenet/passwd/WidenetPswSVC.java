package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.passwd;
import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
public class WidenetPswSVC extends CSBizService
{

	private static final long serialVersionUID = 1L;
	/**
	 * 提供给网厅调用修改宽带密码
	 * @return
	 * @throws Exception 
	 */
	public IData PswChg(IData input) throws Exception {
		IDataset dataset=CSAppCall.call("SS.WidenetPswChgRegSVC.tradeReg", input);//this.queryUserSaleActiveInfo(saleData); 
        IData result =  new DataMap();
        if(dataset!=null &&dataset.size()>0){
        	result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "修改成功！");
    	}else{
    		result.put("X_RESULTCODE", "2998");
        	result.put("X_RESULTINFO", "修改失败！");
    	}
		return result;
	}
	/**
	 * 提供给网厅校验宽带用户密码
	 * @return
	 * @throws Exception 
	 */
	public IData PswCheck(IData input) throws Exception {
		IData result =  new DataMap();
		String phoneNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String password = input.getString("PASSWORD");
		phoneNumber = "KD_" + phoneNumber;
        IData userInfo = UcaInfoQry.qryUserInfoBySn(phoneNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
        	result.put("X_RESULTCODE", "2998");
        	result.put("X_RESULTINFO", "用户不存在！");
        	return result;
        }
        String userId = userInfo.getString("USER_ID");
        
        IDataset wideNetInfo = WidenetInfoQry.getUserWidenetInfo(userId);//通过userid获取widenet表信息
        if (IDataUtil.isEmpty(wideNetInfo))
        {
        	result.put("X_RESULTCODE", "2998");
        	result.put("X_RESULTINFO", "宽带用户不存在！");
        	return result;
        }
        String dbPwd = wideNetInfo.first().getString("ACCT_PASSWD");
        if(StringUtils.isBlank(dbPwd)&&StringUtils.isBlank(password)){
        	result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "密码一致，校验通过");
        	return result;
        }
		String key = userId.substring(userId.length() - 9, userId.length());
	    String inputPass = (String) Encryptor.fnEncrypt(password, key);
	    if(StringUtils.isNotBlank(dbPwd) && StringUtils.isNotBlank(inputPass) && inputPass.equals(dbPwd)){
	    	result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "密码一致，校验通过");
        	return result;
	    }else{
	    	result.put("X_RESULTCODE", "2998");
        	result.put("X_RESULTINFO", "密码不一致，校验不通过");
        	return result;
	    }
		
	}

}
