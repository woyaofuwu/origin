
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.PasswordUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.uopinterface.util.RSAUtil;


/**
 * @author Jesus wuhao5 20190611
 *
 */
public class PersonPWChangeBean extends CSBizBean
{
    /**
     * 用户密码重置或变更接口
     * 
     * @param param
     * @return
     * @throws Exception
     */
    /**
     * 服务密码修改(来自外围接口) X_MANAGEMODE 字段说明： 0=申请密码；1=取消密码 ；2=修改密码；3=随机密码（密码为空的情况下）；4=重置密码（用户使用证件号码进行密码重置,同样需要用户输入新的密码）；
     * 5=随机密码（需证件号码）；7=随机密码（需证件号码）；（5、7没有发现有区别，可以删掉7） 8=密保服务重置方式，选择这种方式的话，接口可以直接传入新密码来进行密码重置; 9=重置密码（不要任何校验）
     */
    public IData userPwdModOrRst(IData param) throws Exception
    {
        IData result = new DataMap();
        CSBizBean.getVisit().setLoginEparchyCode("0898");
        CSBizBean.getVisit().setStaffEparchyCode("0898");
        
        System.out.print("wuhao5>person" + param.toString());
        //手机号码
        String serialNumber = param.getData("params").getString("userMobile");
        //操作类型 1:密码修改 2:密码重置
        String opType = param.getData("params").getString("opType");   
        //用户输入的旧密码(加密传输),opType = 1时必填 获取解密后的数据
        String psw = "";
        if("1".equals(opType))
        {        	
            psw = RSAUtil.decrypt(param.getData("params").getString("oldPsw"));
        }
        //用户输入的新密码(加密传输) 获取解密后的数据
        String newPsw = RSAUtil.decrypt(param.getData("params").getString("newPsw"));
        //用户输入的证件类型
        String idType = param.getData("params").getString("idType","");
        //用户输入的证件号码(加密传输) 获取解密后的数据
        String newPsptId = RSAUtil.decrypt(param.getData("params").getString("idValue"));
        //是否短信通知 为0才发短信,其他值不发(营业侧无短信开关,默认下发)
        //String isSmsNotify = param.getData("params").getString("isSmsNotify");
        //接口里定义的操作类型
        String managerMode = "";
        //业务类型编码
        String tradeTypeCode = "";
        //用户编码
        String userId = "";
        //数据库保存的用户证件类型
        String oldPsptType = "";
        //数据库保存的用户证件号码
        String oldPsptId = "";
        //数据库保存的用户密码
        String oldPsw = "";
        //转换为接口里定义的操作类型      
        
        //密码修改
    	if("1".equals(opType))
    	{
    		managerMode = "2";
    	}else if("2".equals(opType))
    	{	
    		//密码重置
    		managerMode = "9";
    	}else
    	{
    		IData object = new DataMap();
            object.put("respCode", "-9999");
            object.put("respDesc", "无此操作类型!");
            
            result.put("object", object);
            return result;
    	}
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        if (uca == null)
        {
            //CSAppException.apperr(CrmUserException.CRM_USER_87);// 未找到用户资料
            IData object = new DataMap();
            object.put("respCode", "-9999");
            object.put("respDesc", "未找到用户资料!");
            
            result.put("object", object);
            return result;            
        }
        //获取用户编码
        userId = uca.getUserId();        
        //获取数据库保存的用户密码
        oldPsw = uca.getUser().getUserPasswd();
        if (uca.getCustPerson() == null)
        {
            //CSAppException.apperr(CrmUserException.CRM_USER_397);// 获取客户资料无数据!
            IData object = new DataMap();
            object.put("respCode", "-9999");
            object.put("respDesc", "获取客户资料无数据!");
            
            result.put("object", object);
            return result;        
        }
        //获取数据库保存的证件号码
        oldPsptId = uca.getCustPerson().getPsptId();
        //获取数据库保存的证件类型
        oldPsptType = uca.getCustPerson().getPsptTypeCode();
        //证件类型比对
        if(idType.equals(oldPsptType))
        {
        	IData object = new DataMap();
            object.put("respCode", "-9999");
            object.put("respDesc", "证件类型与开户证件类型不符!");
            
            result.put("object", object);
            return result;
        }
        //证件号码比对
        if(newPsptId.equals(oldPsptId))
        {
        	if("2".equals(managerMode))
        	{
        		tradeTypeCode = "71";        
    			try {
    				// 针对不同的变更类型检验服务密码
    				PasswordUtil.judgeManagerMode(managerMode, psw, newPsw, oldPsw, newPsptId, oldPsptId, "1", userId);
    			} catch (Exception e) {
    	        	IData object = new DataMap();
    	            object.put("respCode", "-9999");
    	            object.put("respDesc", "服务密码不正确!");
    	            
    	            result.put("object", object);
    	            return result;
    			}                
        	}else
        	{
        		tradeTypeCode = "73";
        	}
			try {
	    		//新密码进行复杂度校验
	    		PasswordUtil.checkPwdComplx(managerMode, serialNumber, oldPsptId, newPsw);
			} catch (Exception e) {
	        	IData object = new DataMap();
	            object.put("respCode", "-9999");
	            object.put("respDesc", e.getMessage());
	            
	            result.put("object", object);
	            return result;
			}      

    		String passwdType = PasswordUtil.getPasswdTypeByManagemode(managerMode);
            IData input = new DataMap();
            input.put("NEW_PASSWD", newPsw);
            input.put("SERIAL_NUMBER", serialNumber);
            input.put("X_MANAGEMODE", managerMode);
            input.put("PASSWD_TYPE", passwdType);
            input.put("TRADE_TYPE_CODE", tradeTypeCode);
            input.put("REMARK", "在线公司调用变更密码接口");
            
			try {
	    		//调用接口变更密码
				CSAppCall.call("SS.ModifyUserPwdInfoRegSVC.tradeReg", input); 
			} catch (Exception e) {
	        	IData object = new DataMap();
	            object.put("respCode", "-9999");
	            object.put("respDesc", "调用密码变更接口错误!");
	            
	            result.put("object", object);
	            return result;
			}                   		
        }else
        {
        	IData object = new DataMap();
            object.put("respCode", "-9999");
            object.put("respDesc", "证件号码不正确!");
            
            result.put("object", object);
            return result;
        }

        IData object = new DataMap();
        object.put("respCode", "0");
        object.put("respDesc", "success");
        
        result.put("object", object);
        return result;
    }
}