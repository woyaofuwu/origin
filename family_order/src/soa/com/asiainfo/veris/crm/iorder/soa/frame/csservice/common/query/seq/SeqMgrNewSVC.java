package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.seq;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class SeqMgrNewSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    public IDataset getInstId(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getInstId());
        idataset.add(result);
        return idataset;
    }
}
