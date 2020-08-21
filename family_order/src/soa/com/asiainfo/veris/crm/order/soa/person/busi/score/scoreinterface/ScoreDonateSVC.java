
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 积分转赠接口服务
 *
 * @author zhouwu
 * @date 2014-07-02 17:11:52
 */
public class ScoreDonateSVC extends CSBizService
{

    /**
	 *
	 */
	private static final long serialVersionUID = -1416971450480869350L;

	/**
     * 积分转赠查询
     *
     * @param pd
     * @param inparam
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IData queryDonateScore(IData input) throws Exception
    {
        ScoreDonateBean bean = new ScoreDonateBean();
        return bean.queryDonateScore(input);
    }

    public IData getCommInfo(IData input) throws Exception
    {
    	ScoreDonateBean scoreDonateBean = (ScoreDonateBean) BeanManager.createBean(ScoreDonateBean.class);
        return scoreDonateBean.getCommInfo(input);
    }

}
