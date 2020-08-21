/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.tradenetbookdeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.DepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBookInfoQry;

/**
 * @CREATED by gongp@2014-4-17 修改历史 Revision 2014-4-17 上午11:09:55
 */
public class TradeNetBookDealSVC extends CSBizService
{

    private static final long serialVersionUID = -9031746836488368015L;

    public IDataset getBookDepts(IData param) throws Exception
    {
        return DepartInfoQry.getDeptSelectDatas(getVisit().getStaffEparchyCode(), getVisit().getCityCode());
    }

    public IDataset qryBookInfo(IData param) throws Exception
    {

        return TradeBookInfoQry.qryBookInfo(param, this.getPagination());

    }

    public IDataset updateNetBook(IData param) throws Exception
    {

        TradeNetBookDealBean bean = BeanManager.createBean(TradeNetBookDealBean.class);

        boolean success_tag = bean.updateNetBook(param);

        IDataset dataset = new DatasetList();

        IData data = new DataMap();

        data.put("RESULT_TAG", success_tag);

        dataset.add(data);

        return dataset;
    }

}
