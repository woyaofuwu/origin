
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GameAccountCheckSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 游戏帐户鉴权接口
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData gameAccountCheck(IData inparam) throws Exception
    {
        GameAccountCheckBean gameBean = BeanManager.createBean(GameAccountCheckBean.class);
        IData result = gameBean.gameAccountCheck(inparam);
        return result;
    }

    @Override
    public final void setTrans(IData input)
    {
        if ("SS.GameAccountCheckSVC.gameAccountCheck".equals(getVisit().getXTransCode()))
        {
            input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
        }
    }
}
