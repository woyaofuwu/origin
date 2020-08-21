
package com.asiainfo.veris.crm.order.soa.person.busi.terminalAfterSales;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 用户星级查询接口
 * @author zyz
 * @version 1.0
 *
 */
public class QryUserCreditClassSVC extends CSBizService
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1814212176503925400L;
	protected static Logger log = Logger.getLogger(QryUserCreditClassSVC.class);

	/**
	 * 用户星级查询
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData qryUserCreditClass(IData input) throws Exception{
    	
    	IData reslut=new DataMap();
    	try {
    		String serial_number=input.getString("SERIAL_NUMBER");
    		
	        IData parameter = new DataMap();
	        parameter.put("IDTYPE", 0);
	        //区域编码
	        parameter.put("EPARCHY_CODE", "0898");
	        IDataset userList=UserInfoQry.getUserInfoBySerailNumber("0", serial_number);
	        if(IDataUtil.isNotEmpty(userList)){
	        	parameter.put("USER_ID", userList.getData(0).getString("USER_ID"));
	        	parameter.put("ID", userList.getData(0).getString("USER_ID"));
	        }
	        parameter.put("SERIAL_NUMBER", serial_number);
    		IDataset creditInfo = CSAppCall.call("QCC_ITF_GetCreditInfos", parameter);
    		
    		if(IDataUtil.isNotEmpty(creditInfo)){
    		     IData data=creditInfo.getData(0);
    		     //星级编码
    		     String creditClass=data.getString("CREDIT_CLASS");
    		     int key=0;
    		     if(!"".equals(creditClass)&&creditClass != null){
    		    	 key=Integer.valueOf(creditClass);
    		     }
    		     String  creditClassName=""; 
    		     switch (key) {
					   case 1:
						   creditClassName="一星";
						break;
					   case 2:
						   creditClassName="二星";
						break;
					   case 3:
						   creditClassName="三星";
						break;
					   case 4:
						   creditClassName="四星";
						break;
					   case 5:
						   creditClassName="五星";
						break;
					   case 6:
						   creditClassName="五星金";
						break;
					   case 7:
						   creditClassName="五星钻";
						break;
					default:
						creditClassName="未评级";
						break;
				}
    			//星级
    		    reslut.put("CREDIT_CLASS", creditClassName);
    			reslut.put("X_RESULTCODE", "0");
    			reslut.put("X_RESULTINFO", "ok");
    		}else{
    			reslut.put("X_RESULTCODE", "2998");
    			reslut.put("X_RESULTINFO", serial_number+",调用账务无信息返回");
    		}
		} catch (Exception e) {
			// TODO: handle exception
			//log.info("(e);
			reslut.put("X_RESULTCODE", "2998");
			reslut.put("X_RESULTINFO", e.getMessage());
		}
		return reslut;
    }
    
}
