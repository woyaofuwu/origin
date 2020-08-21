
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;
 
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * REQ201703220007_关于携转回来的吉祥号码老客户复机优化需求
 * <br/>
 * 用户输入号码为吉祥号码，选择用户若用户为携出销户的号码。 
 *  提示：吉祥号码携出用户不允许办理该业务，若想继续使用该号码请尝试重新开户。
 * @author zhuoyingzhi
 * @date 20170713
 */
public class CheckJXNumRestoreUser extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(CheckJXNumRestoreUser.class);


    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
    	
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// 查询时校验，提交的时候不再执行
        {
            String serialNumber = databus.getString("SERIAL_NUMBER"); 
            String userId=databus.getString("USER_ID"); 
            //ResCall.getMphonecodeInfo 生产上已经存在
            IDataset numberInfo = ResCall.getMphonecodeInfo(serialNumber);// 查询号码信息
           //logger.debug("============numberInfo:"+numberInfo+"=====serialNumber========"+serialNumber+",databus:"+databus);
            if (IDataUtil.isNotEmpty(numberInfo))
            {
                String jxNumber = numberInfo.getData(0).getString("BEAUTIFUAL_TAG","");// BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
                //logger.debug("============jxNumber:"+jxNumber);
                if ("1".equals(jxNumber))
                {// 是吉祥号码 
                	
                	IData userParam=new DataMap();
                		  userParam.put("USER_ID", userId);
                     //code_code 已经存在    SEL_ALL_BY_USERID    TF_F_USER  
                    IDataset  userInfo=Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_USERID", userParam);
                     
    	   			//logger.debug("============userInfo:"+userInfo);
    	   			 if(IDataUtil.isNotEmpty(userInfo)){
    	   				  String  userTagSet=userInfo.getData(0).getString("USER_TAG_SET", "");
    	   				  //logger.debug("============userTagSet:"+userTagSet);
    	   				  if("4".equals(userTagSet)){
    	   					  //已携出号码
    	   					  return true;
    	   				  }
    	   			 }
                }
            }      	
        	
        	
        }
        return false;
    } 
}
