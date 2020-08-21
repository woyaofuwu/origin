
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd.order.filter;

import java.util.Random;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

public class WidenetPswChgIntfFilter implements IFilterIn
{

    /**
     * 宽带密码变更入参检查
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        if("0".equals(param.getString("RESET_TAG", "")))  //密码标识 0：宽带密码修改；1是重置密码，不用校验密码
        {
            IDataUtil.chkParam(param, "INSPECTION_TAG");
            if("0".equals(param.getString("INSPECTION_TAG", "")))//修改模式 0：密码修改；1 身份修改，不用校验密码
            {
                IDataUtil.chkParam(param, "OLDPASSWD");
            }else
            {
                IDataUtil.chkParam(param, "PSPTID");
            }
            
            IDataUtil.chkParam(param, "PASSWD");
        }else{
            param.put("PASSWD", generatePasswd(6));
        }
        
        IData userinfo = qryUserinfo(param);
        if(userinfo != null){
            param.put("USER_ID", userinfo.getString("USER_ID"));
            
            if("1".equals(param.getString("INSPECTION_TAG", "")))
            {
                String userId = param.getString("USER_ID");
               
                IDataset infos = CustomerInfoQry.getNormalCustInfoByUserIdPT(userId);
                if (IDataUtil.isNotEmpty(infos))
                {
                    if(!infos.getData(0).getString("PSPT_ID", "").equals(param.getString("PSPTID")))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_783,"输入的身份证与登记身份证不一致！");
                    }
                }
            }
            
        }else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_273);
        }
        
        if("0".equals(param.getString("RESET_TAG", "0")) && "0".equals(param.getString("INSPECTION_TAG", "")))  //密码重置标识  1是重置密码，不用校验密码
        {
            Boolean flag = checkpasswd(param);
            if(!flag)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_296);
            }
        }
        
    }
    
    public IData qryUserinfo(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("REMOVE_TAG", "0");
        IDataset infos = CSAppCall.call("CS.UserInfoQrySVC.getUserInfoBySnNoProduct", param);
        return infos.size()==0 ? null:infos.getData(0);
    }
    
    public boolean checkpasswd(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("PASSWORD", input.getString("OLDPASSWD"));
        param.put("USER_ID", input.getString("USER_ID"));
        param.put("SERIAL_NUMBER",input.getString("SERIAL_NUMBER").indexOf("KD_")!= -1 ? input.getString("SERIAL_NUMBER").substring(3):input.getString("SERIAL_NUMBER"));
        IDataset infos = CSAppCall.call( "CS.AuthCheckSVC.checkPasswd", param);
        
        return infos.getData(0).getString("RESULT_CODE")=="0" ? true:false;
    }

    public void transferDataInput(IData input) throws Exception
    {

        checkInparam(input);
        input.put("QUERY_TYPE", "1");
        input.put("NEW_PASSWORD2", input.getString("PASSWD"));

    }
    
    public String generatePasswd(int length){
        String val = "";     
                 
        Random random = new Random();     
        for(int i = 0; i < length; i++)     
        {     
           val += String.valueOf(random.nextInt(10));     
        } 
        return val;
    }

}
