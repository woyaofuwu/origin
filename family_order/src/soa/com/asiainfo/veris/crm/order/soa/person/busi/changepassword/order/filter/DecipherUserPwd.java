
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.filter;
 
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn; 

/**
 * 用户密码提交后需要解密
 * 
 * @author liutt
 */
public class DecipherUserPwd implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    { 
        if (input.containsKey("NEW_PASSWD")||input.containsKey("USER_PASSWD"))
        { 
        	String encryPwd=input.getString("NEW_PASSWD","");//密码修改框
        	if(!"".equals(encryPwd)&&encryPwd.indexOf("xxyy")>-1){
        		Des desObj = new Des();
                String pwd=desObj.getDesPwd(encryPwd);
                input.put("NEW_PASSWD", pwd);
        	}
        	String encryUserPwd=input.getString("USER_PASSWD","");//开户的密码录入框
        	if(!"".equals(encryUserPwd)&&encryUserPwd.indexOf("xxyy")>-1){
        		Des desObj = new Des();
                String pwd=desObj.getDesPwd(encryUserPwd);
                input.put("USER_PASSWD", pwd);
        	}
        } 
    }
}
