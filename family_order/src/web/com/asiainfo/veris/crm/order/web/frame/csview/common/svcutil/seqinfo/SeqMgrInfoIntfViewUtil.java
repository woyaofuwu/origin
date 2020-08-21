
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.seqinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.seqinfo.SeqMgrInfoIntf;

public class SeqMgrInfoIntfViewUtil
{

    /**
     * 查询Molist序列值
     * 
     * @param bc
     * @return
     * @throws Exception
     */
    public static String qryGrpMoListSeqId(IBizCommon bc) throws Exception
    {
        IDataset infosDataset = SeqMgrInfoIntf.qryGrpMoListSeqInfos(bc);
        if (IDataUtil.isEmpty(infosDataset))
        {
            return "";
        }
        return infosDataset.getData(0).getString("SEQ_GRP_MOLIST", "");
    }

    /**
     * 查询Molist序列
     * 
     * @param bc
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMoListSeqInfos(IBizCommon bc) throws Exception
    {
        IDataset infosDataset = SeqMgrInfoIntf.qryGrpMoListSeqInfos(bc);
        return infosDataset;
    }
    
    /**
     * @description 根据用户地州编号获取批次号
     * @author xunyl
     * @date 2016-01-17
     */
    public static String qryBatchId(IBizCommon bc,String userEparchCode)throws Exception{
        IDataset batchInfoList = SeqMgrInfoIntf.qryBatchInfos(bc,userEparchCode);
        IData batchInfo = batchInfoList.getData(0);
        String batTaskId = batchInfo.getString("seq_id");
        return batTaskId;
    }
}
