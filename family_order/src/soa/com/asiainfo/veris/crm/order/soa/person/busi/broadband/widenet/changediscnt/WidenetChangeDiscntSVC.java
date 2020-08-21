
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changediscnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class WidenetChangeDiscntSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset checkDiscnt(IData input) throws Exception
    {
        WidenetChangeDiscntBean widenetChangeDiscntBean = BeanManager.createBean(WidenetChangeDiscntBean.class);
        return widenetChangeDiscntBean.checkDiscnt(input);
    }

    public IDataset getSpecDiscnt(IData input) throws Exception
    {
//        IData discntInfo = UDiscntInfoQry.getDiscntInfoByPk(input.getString("DISCNT_CODE")); 
    	       
        IData discntInfo=UpcCall.qryOfferExtChaByOfferId(input.getString("DISCNT_CODE"), "D", "TD_B_DISCNT").getData(0);

        return new DatasetList(discntInfo);
    }

    public IDataset loadChildInfo(IData input) throws Exception
    {
        WidenetChangeDiscntBean widenetChangeDiscntBean = BeanManager.createBean(WidenetChangeDiscntBean.class);
        IDataset dataset = widenetChangeDiscntBean.loadChildInfo(input);
        return dataset;
    }
}
