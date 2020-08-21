
package com.asiainfo.veris.crm.order.soa.person.busi.selfhelp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TerminalManageExportTask extends ExportTaskExecutor
{

    public IDataset executeExport(IData paramIData, Pagination arg1) throws Exception
    {
        IData inparam = paramIData.subData("cond", true);
        IDataset terminals = CSAppCall.call("SS.TerminalManageSVC.queryTerminals", inparam);
        
        for (int i = 0, size = terminals.size(); i < size; i++)
        {
        	IData terminal = terminals.getData(i);
        	String removeTag = terminal.getString("REMOVE_TAG", "").equals("0") ? "正常" : "注销";
        	terminal.put("REMOVE_TAG", removeTag);
        	
        	float fee = Float.parseFloat(terminal.getString("FEE"))/100 ;
        	terminal.put("FEE", fee+"");
        	
        	float balance = Float.parseFloat(terminal.getString("BALANCE"))/100 ;
        	terminal.put("BALANCE", balance+"");
        	
        	float recvFee = Float.parseFloat(terminal.getString("RECV_FEE"))/100 ;
        	terminal.put("RECV_FEE", recvFee+"");
        	
        	String openMode = StaticUtil.getStaticValue("SELF_HELP_OPEN_MODE", terminal.getString("OPEN_MODE"));
        	terminal.put("OPEN_MODE", openMode);
        	
        	String belongTo = "01".equals(terminal.getString("BELONG_TO"))?"移动购置":"02".equals(terminal.getString("BELONG_TO"))?"渠道自购":"" ;
        	terminal.put("BELONG_TO", belongTo);
        }

        return terminals;
    }

}
