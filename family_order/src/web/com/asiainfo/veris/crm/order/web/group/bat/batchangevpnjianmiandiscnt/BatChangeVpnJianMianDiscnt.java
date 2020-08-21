
package com.asiainfo.veris.crm.order.web.group.bat.batchangevpnjianmiandiscnt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public abstract class BatChangeVpnJianMianDiscnt extends CSBasePage
{

    /**
     * 初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
    	String batchOperType = getData().getString("BATCH_OPER_TYPE", "NOXXXX");
        IDataset discntList = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, "CSM", "238", "0", getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(discntList))
        {
            for (int i = 0, row = discntList.size(); i < row; i++)
            {
                IData discntData = discntList.getData(i);

                discntData.put("DISCNT_DESC", discntData.getString("PARAM_NAME") + "|" + discntData.getString("PARA_CODE1"));
            }
        }

        setDiscntList(discntList);
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", batchOperType);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	getData().put("MEB_VOUCHER_FILE_SHOW","false");
        }else{
        	getData().put("MEB_VOUCHER_FILE_SHOW","true");
        }
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
        
        setCondition(getData());
    }

    public abstract void setDiscntList(IDataset discntList);
    public abstract void setCondition(IData condition);
}
