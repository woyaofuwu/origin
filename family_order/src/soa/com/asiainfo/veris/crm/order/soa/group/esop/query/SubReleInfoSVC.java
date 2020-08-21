package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SubReleInfoSVC extends CSBizService 
{
	private static final long serialVersionUID = 1L;
	
	 public IDataset qryInfoByTemplet(IData param) throws Exception {
    	String bpmTempletId = param.getString("BPM_TEMPLET_ID");
    	String ibsysId = param.getString("IBSYSID");
        IDataset subTempletInfo = SubReleInfoQry.qryInfoByTemplet(bpmTempletId);
        if(IDataUtil.isNotEmpty(subTempletInfo)) {
            IDataset bpmTempletInfo = SubReleInfoQry.qryParentBpmtempletIdByIbsysId(ibsysId);
            if(IDataUtil.isNotEmpty(bpmTempletInfo)) {
                return bpmTempletInfo;
            }
        }
        return new DatasetList();
	 }
	 
	 /**
	 * @Title: qryEopAllAttachByIbsysid 
	 * @Description: 查询最新合同附件 
	 * @param IBSYSID ATTACH_TYPE
	 * @throws Exception IDataset
	 * @author zhangzg
	 * @date 2019年11月28日上午11:04:36
	  */
	 public IDataset qryEopAllAttachByIbsysid(IData param) throws Exception {
	     IDataset attachInfoList = SubReleInfoQry.qryGroupAttach(param);
	     if(IDataUtil.isEmpty(attachInfoList)) {
	         attachInfoList = SubReleInfoQry.qryFinishGroupAttach(param);
	     }
	     return attachInfoList;
	 }

}
