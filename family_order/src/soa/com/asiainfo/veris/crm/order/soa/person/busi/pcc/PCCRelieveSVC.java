
package com.asiainfo.veris.crm.order.soa.person.busi.pcc;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;

/**
 *  
 */
public class PCCRelieveSVC extends CSBizService
{

    private static final long serialVersionUID = 5502421746441250576L;

    public IDataset unlockAreaQryInfo(IData input)throws Exception{
    	IData inParamNew = new DataMap();
        inParamNew.put("USER_ID", input.getString("USER_ID"));
        inParamNew.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        inParamNew.put("IN_DATE", SysDateMgr.getFirstDayOfThisMonth());
        IDataset result = PCCBusinessQry.qryPccOperationTypeForSubscriber(inParamNew);
        if (!result.isEmpty()) {
            String usrStatus = result.getData(0).getString("USR_STATUS", "");
            String execState = result.getData(0).getString("EXEC_STATE", "");
            if (("2".equals(usrStatus) || "3".equals(usrStatus)) && "2".equals(execState)) {//等于2并且执行状态=2，才是限速成功。
            	result.getData(0).put("PCCRELIEVE", "0");
            }else{
            	result.getData(0).put("PCCRELIEVE", "1");
            }
        } else {                           	
        	result = PCCBusinessQry.qryPccHOperationTypeForSubscriber(inParamNew);
        	if (!result.isEmpty()) {
                String usrStatus = result.getData(0).getString("USR_STATUS", "");
                String execState = result.getData(0).getString("EXEC_STATE", "");
                if (("2".equals(usrStatus) || "3".equals(usrStatus)) && "2".equals(execState)) {//等于2并且执行状态=2，才是限速成功。
                	result.getData(0).put("PCCRELIEVE", "0");
                }else{
                	result.getData(0).put("PCCRELIEVE", "1");
                }
            }else
            {
            	IData data = new DataMap();
            	data.put("PCCRELIEVE", "1");
            	result.add(data);
            }
            
        }
    	return result;
    }
}