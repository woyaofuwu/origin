package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class AuditContractInfo extends BizTempComponent
{
	public abstract void setInfo(IData info);
	public abstract void setFileLists(IDataset fileLists);
	
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "iorder/igroup/esop/acceptanceperiod/script/acceptanceperiod.js";
        IData param = getPage().getData();
        String nodeId = param.getString("NODE_ID","");//驳回的情况会有值 
        if(!nodeId.equals("applyConfirm")){
        	if (isAjax)
            {
            }
            else
            {
                getPage().addResAfterBodyBegin(jsFile, false, false);
            }	
        }

        IData inparam = new DataMap();
        inparam.put("IBSYSID", param.getString("IBSYSID"));
        inparam.put("NODE_ID", "apply");
        IDataset attrs = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", inparam);
        String ifNew = "";
        String ContractId = "";
        IData contractData = new DataMap();
        if(IDataUtil.isNotEmpty(attrs)) {
            for (int i = 0, size = attrs.size(); i < size; i++) {
                IData attr = attrs.getData(i);
                if("C_IF_NEW_CONTRACT".equals(attr.getString("ATTR_CODE"))) {
                    ifNew = attr.getString("ATTR_VALUE");
                }
                if("CONTRACT_ID".equals(attr.getString("ATTR_CODE"))) {
                    ContractId = attr.getString("ATTR_VALUE");
                }
            }

            //已有合同
            if("1".equals(ifNew)) {
                IData workformData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, param.getString("IBSYSID"));
                if(IDataUtil.isNotEmpty(workformData)) {
                    String groupId = workformData.getString("GROUP_ID");
                    IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
                    String custId = group.getString("CUST_ID");
                    IData cparam = new DataMap();
                    cparam.put("CUST_ID", custId);
                    cparam.put("CONTRACT_ID", ContractId);
                    IData contractInfo = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryContractInfo", cparam);
                    String contractStr = contractInfo.getString("CONTRACT_FILE_ID");
                    if(StringUtils.isNotBlank(contractStr) && contractStr.contains(":")) {
                        String[] contractStrs = contractStr.split(":");
                        contractData.put("FILE_ID", contractStrs[0]);
                        contractData.put("ATTACH_NAME", contractStrs[1]);
                    }
                }
            }
        }

        // 查询附件
        IData input = new DataMap();
        input.put("IBSYSID", param.getString("IBSYSID"));
        IDataset  filesets = CSViewCall.call(this, "SS.WorkformAttachSVC.qryContractAttach", input);

        if(IDataUtil.isEmpty(filesets)) {
            filesets = new DatasetList();
        }
        if(IDataUtil.isNotEmpty(contractData)){
            filesets.add(contractData);
        }

        setFileLists(filesets);
        
    }
}



