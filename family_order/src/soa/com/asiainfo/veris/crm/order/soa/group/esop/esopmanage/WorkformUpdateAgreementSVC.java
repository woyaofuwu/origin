package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;

/**
 * 复杂变更，合同协议修改
 *
 * */
public class WorkformUpdateAgreementSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    private static final String state = "2";

    public void execute(IData data) throws Exception {

        String ibsysid = data.getString("BI_SN", "");
        if(StringUtils.isBlank(ibsysid))
        {
        	return;
        }
        
        IDataset otherList = WorkformOtherBean.qryLastInfoByIbsysidAndAttrCode(ibsysid,"ADULT_RESULT");
        String adultResult = "";
        if(DataUtils.isNotEmpty(otherList))
        {
            adultResult = otherList.first().getString("ATTR_VALUE","");
        }

        if(StringUtils.isNotEmpty(adultResult))
        {
            IDataset productList = WorkformProductBean.qryProductByIbsysid(ibsysid);

            if(DataUtils.isNotEmpty(productList))
            {
                for(int i = 0; i < productList.size(); i++)
                {
                    IData product = productList.getData(i);

                    IData param = new DataMap();
                    String productId = product.getString("RSRV_STR1","");
                    if(StringUtils.isNotEmpty(productId)) {
                        param.put("AGREEMENT_ID", productId);
                        if("2".equals(adultResult))
                        {
                        	param.put("ARCHIVES_STATE", "3");//审核通过，合同归档
                        }else {
                        	param.put("ARCHIVES_STATE", "2");//审核不通过，合同不通过
                        }
                        CSAppCall.call("SS.AgreementInfoSVC.updateAgreementFinish", param);
                    }
                    String productIdVw = product.getString("RSRV_STR2","");
                    if(StringUtils.isNotEmpty(productIdVw)) {
                    	 IData paramVw = new DataMap();
                    	 paramVw.put("AGREEMENT_ID", productIdVw);
                         if("2".equals(adultResult))
                         {
                        	paramVw.put("ARCHIVES_STATE", "3");//审核通过，合同归档
                         }else {
                        	paramVw.put("ARCHIVES_STATE", "2");//审核不通过，合同不通过
                         }
                         CSAppCall.call("SS.AgreementInfoSVC.updateAgreementFinish", paramVw);
                    }
                }
            }
        }

    }
}
