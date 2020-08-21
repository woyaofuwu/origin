package com.asiainfo.veris.crm.order.soa.group.upgprelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class UnifyPayRelationQrySVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUnifyPayInfo(IData inparam) throws Exception
    {
        UnifyPayRelationQryBean bean = new UnifyPayRelationQryBean();
        return bean.qryGrpUnifyPayInfo(inparam, this.getPagination());
    }

    /**
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUnifyPayAcctInfo(IData inparam) throws Exception
    {
        UnifyPayRelationQryBean bean = new UnifyPayRelationQryBean();
        return bean.qryGrpUnifyPayAcctInfo(inparam);
    }
    
    /**
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUnifyPayInfoByAcctId(IData inparam) throws Exception
    {
        UnifyPayRelationQryBean bean = new UnifyPayRelationQryBean();
        return bean.qryGrpUnifyPayInfoByAcctId(inparam, this.getPagination());
    }
    
}