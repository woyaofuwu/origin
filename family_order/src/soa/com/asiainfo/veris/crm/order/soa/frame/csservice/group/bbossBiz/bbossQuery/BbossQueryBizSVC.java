
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 此SVC放置查询页面的查询接口逻辑
 * 
 * @author liuxx3
 * @date 2014 -08 -11
 */
public class BbossQueryBizSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 一级BBOSS客户订购产品清单查询--BBOSS产品用户状态查询
     * 
     * @author liuxx3
     * @date 2014-7-10
     */
    public IDataset qryBBossBizEc(IData param) throws Exception
    {
        return QryBBossBizEcbean.qryBBossBizEc(param, this.getPagination());
    }

    /**
     * 一级BBOSS业务成员用户清单查询 -- BBOSS成员用户状态查询
     * 
     * @author liuxx3
     * @date 2014-07-14
     */

    public IDataset qryBBossBizMeb(IData param) throws Exception
    {

        String group_id = param.getString("GROUP_ID");

        if (StringUtils.isNotEmpty(group_id))
        {
            return QryBBossBizMebBean.qryBBossBizMebByGrpId(param, this.getPagination());
        }

        return QryBBossBizMebBean.qryBBossBizMebByOtherParam(param, this.getPagination());
    }

    /**
     * 一级BBOSS业务成员签约订购查询---BBOSS成员状态查询
     * 
     * @author liuxx3
     * @date 2014-07-14
     */

    public IDataset qryBBossBizMebQy(IData param) throws Exception
    {
        return QryBBossBizMebQyBean.qryBBossBizMebQy(param, this.getPagination());
    }

    /**
     * 一级BBOSS业务产品订购状态查询--BBOSS产品状态查询
     * 
     * @author liuxx3
     * @date 2014-07-09
     */

    public IDataset qryBBossBizProdDg(IData param) throws Exception
    {
        return QryBBossBizProdDgMebBean.qryBBossBizProdDgMeb(param, this.getPagination());
    }

    /**
     * 一级BBOSS业务成员订购处理查询--BBOSS成员签约关系订购反馈结果查询
     * 
     * @author liuxx3
     * @date 2014-3-7
     */

    public IDataset qryBBossMeb(IData param) throws Exception
    {
        return QryBBossMebBean.qryBBossMebByParam(param, this.getPagination());
    }

    /**
     * 一级BBOSS业务集团客户订购处理查询 --BBOSS产品订购反馈结果查询
     * 
     * @author liuxx3
     * @date 2014-07-10
     */
    public IDataset qryBBossTradeInfo(IData param) throws Exception
    {
        return QryBBossTradeInfoBean.qryBBossTradeInfo(param, this.getPagination());
    }

    /**
     * 查询商品处理失败页面查询
     * 
     * @author liuxx3
     * @date 2014 -08 -11
     */
    public IDataset queryPoError(IData input) throws Exception
    {
        return QryBBossTradeExtInfoBean.queryPoError(input.getString("cond_GROUP_ID", ""), input.getString("cond_POSPECNUMBER", ""), this.getPagination());
    }

}
