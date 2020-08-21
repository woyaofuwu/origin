package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;
 

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 2015年新春入网预存有礼活动开发需求
 * 规则：判断预存款余额是否大于50元，如果小于50元则返回TRUE
 * 
 * @author Mr.Z
 */
public class CheckUserFeeByAcct extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -6297900872898350305L;

    private static Logger logger = Logger.getLogger(CheckUserFeeByAcct.class);

    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckUserOpenDate() >>>>>>>>>>>>>>>>>>");
        }
        
        //String product_id = ruleparam.getString(databus, "PRODUCT_ID");
        //String package_id = ruleparam.getString(databus, "PACKAGE_ID"); 
        String product_id = databus.getString("PRODUCT_ID");
        String package_id = databus.getString("PACKAGE_ID"); 
        String rtnFlag="";
        String rtnFee="";
        String serial_number=databus.getString("SERIAL_NUMBER");
        IDataset ids=CommparaInfoQry.getCommparaByCode1("CSM", "638", product_id, package_id, null);
        if (ids != null && ids.size() > 0)
        {
        	String para_code2= ids.getData(0).getString("PARA_CODE2");
        	String para_code3= ids.getData(0).getString("PARA_CODE3");
        	String para_code4= ids.getData(0).getString("PARA_CODE4");
        	String param_name= ids.getData(0).getString("PARAM_NAME");
        	
        	IData param = new DataMap();
        	param.put("SERIAL_NUMBER", serial_number);
            param.put("DEPOSIT_CODES", para_code3);
            param.put("NEED_FEE",para_code2);
            
            //账务提供接口名为:AM_CRM_CheckFeeEnough,传进来的存折必须以"|"隔开
            //入参{SERIAL_NUMBER="13687598417",DEPOSIT_CODES="0|266|1",NEED_FEE="5000"}
            IDataset feeList=CSAppCall.callAcct("AM_CRM_CheckFeeEnough", param, false).getData();
            IData feeData=null;
            if(feeList!=null&&feeList.size()>0){
            	feeData=feeList.getData(0);
            	//如果用户缴费累计大于等于50元,返回0和累计缴费的钱
	            rtnFlag=feeData.getString("IS_ENOUGH");
	            rtnFee=feeData.getString("DEPOSITSUMFEE");
	            
	            if(!rtnFlag.equals("0")){//Integer.parseInt(para_code2)<Integer.parseInt(rtnFee)){
	            	
	            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 12062739, param_name);
	                return true;
	            }else{
	            }
            }
        }
 
        
        return false;
    }

}
