
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.auth;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.SmsVerifyCodeBean;

public class AuthCheckSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 认证组件鉴权[宽带用户]
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset authBroadbandCheck(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData result = PasswdMgr.checkUserPasswd(input);
        returnSet.add(result);
        return returnSet;
    }

    /**
     * 认证组件鉴权
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset authCheck(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData result = PasswdMgr.checkUserPasswd(input);
        returnSet.add(result);
        return returnSet;
    }

    /**
     * 校验密码
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkPasswd(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData result = new DataMap();
        boolean resultFlag = PasswdMgr.checkUserPassword(input.getString("PASSWORD"), input.getString("USER_ID"), input.getString("OLD_PASSWORD"));
        result.put("RESULT_CODE", resultFlag ? "0" : "1");
        returnSet.add(result);
        return returnSet;
    }

    /**
     * 用户统一认证--外围接口电子渠道调用
     * 
     * @param input
     *            [CHECK_TAG,SERIAL_NUMBER,USER_PASSWD]
     * @return
     * @throws Exception
     */
    public IDataset checkUserNetPWD(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData result = PasswdMgr.checkUserNetPWD(input);
        returnSet.add(result);
        return returnSet;
    }

    /**
     * 加密明文密码
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getEncryptKey(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData result = new DataMap();
        String encryptKey = PasswdMgr.encryptPassWD(input.getString("PASSWORD"), input.getString("USER_ID"));
        result.put("ENCRYPT_PASSWD", encryptKey);
        returnSet.add(result);
        return returnSet;
    }
    
    /**
     * 发送短信随机验证码
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset sendSmsVerifyCode(IData input) throws Exception
    {
    	IDataset returnSet = new DatasetList();
    	SmsVerifyCodeBean bean = BeanManager.createBean(SmsVerifyCodeBean.class);
    	IData data = bean.sendSmsVerifyCode(input);
    	returnSet.add(data);
    	 return returnSet;
    }
}
