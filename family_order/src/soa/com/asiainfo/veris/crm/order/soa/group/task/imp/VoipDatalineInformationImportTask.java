package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class VoipDatalineInformationImportTask extends ImportTaskExecutor{

	@Override
	public IDataset executeImport(IData data, IDataset dataset) throws Exception {
		IDataset infos = new DatasetList();
		if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_138);
        }
		String ibsysId =  dataset.getData(0).getString("IBSYSID");
		IData inputData =  new DataMap();
		inputData.put("IBSYSID", ibsysId);
		inputData.put("CONFIGNAME", "EOMS_BUSI_STATE");

	    IDataset result = CSAppCall.call("SS.WorkFormSVC.getRenewWorkSheet", inputData);

        if (IDataUtil.isNotEmpty(result)) {
            for (int i = 0; i < result.size(); i++) {
                IData infoss = new DataMap();
                IData resultInfo = result.getData(i);
                String recordNum = resultInfo.getString("RECORD_NUM");
                String nodeId = resultInfo.getString("NODE_ID");
                String tradeId = resultInfo.getString("TRADE_ID");
                String productNo = resultInfo.getString("PRODUCT_NO");
                String productId = resultInfo.getString("PRODUCT_ID");
                String insertTime = resultInfo.getString("CREATE_DATE");
                String valueDesc = resultInfo.getString("VALUEDESC");
                String paramValue = resultInfo.getString("PARAMVALUE");
                String dealType = resultInfo.getString("SHEETTYPE");
                if ("7010".equals(productId)) {
                    infoss.put("BUSI_SIGN", "VOIP专线");
                } else if ("7011".equals(productId)) {
                    infoss.put("BUSI_SIGN", "互联网专线");
                } else if ("7012".equals(productId)) {
                    infoss.put("BUSI_SIGN", "数据专线");
                }
                infoss.put("PARAMVALUE", paramValue);
                infoss.put("VALUEDESC", valueDesc);
                infoss.put("TRADE_ID", tradeId);
                infoss.put("IBSYSID", ibsysId);
                infoss.put("PRODUCT_NO", productNo);
                infoss.put("PRODUCT_ID", productId);
                infoss.put("SERIALNO", resultInfo.getString("SERIALNO"));
                infoss.put("EOMS_ACCEPTTIME", insertTime);
                infoss.put("NODE_ID", nodeId);
                infoss.put("DEAL_TYPE", dealType);
                infoss.put("RECORD_NUM", recordNum);
                IData datas = new DataMap();
                datas.put("RECORD_NUM", recordNum);
                datas.put("NODE_ID", nodeId);
                datas.put("IBSYSID", ibsysId);
                IDataset productAttrInfos = CSAppCall.call("SS.WorkFormSVC.getWorkfromProductAttr", inputData);
                if (IDataUtil.isNotEmpty(productAttrInfos)) {
                    for (int j = 0; j < productAttrInfos.size(); j++) {
                        IData productAttrInfo = productAttrInfos.getData(j);
                        if ("TITLE".equals(productAttrInfo.getString("ATTR_CODE"))) {
                            infoss.put("EOMS_TITLE", productAttrInfo.getString("ATTR_VALUE"));
                        }
                        if ("GROUP_ID".equals(productAttrInfo.getString("ATTR_CODE"))) {
                            infoss.put("EOMS_CUSTOMNO", productAttrInfo.getString("ATTR_VALUE"));
                        }

                    }
                }
                infos.add(infoss);

            }
        }
        for(int i = 0 ;i<infos.size();i++){
        	IData info = infos.getData(i);
        	for(int j = 0;j < dataset.size();j++){
        		IData datas = dataset.getData(j);
        		if(info.getString("IBSYSID").equals(datas.getString("IBSYSID")) && info.getString("PRODUCT_NO").equals(datas.getString("PRODUCT_NO"))){
        			info.put("ZJ", datas.getString("ZJ"));
        		}
        	}
        }
		return infos;
	}

}
