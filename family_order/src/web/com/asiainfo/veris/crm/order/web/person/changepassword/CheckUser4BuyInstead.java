
package com.asiainfo.veris.crm.order.web.person.changepassword;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckUser4BuyInstead.java
 * @Description: 代客下单密码验证
 *
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-10-24 下午5:13:44
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------*
 * 2014-10-24      yxd         v1.0.0               修改原因
 */
public abstract class CheckUser4BuyInstead extends PersonBasePage
{
	/**
	 * 
	* @Function: init()
	* @Description: 
	*
	* @param:
	* @return：
	* @throws：异常描述
	*
	* @version: v1.0.0
	* @author: yxd
	* @date: 2014-10-24 下午5:14:12
	*
	* Modification History:
	* Date         Author          Version            Description
	*---------------------------------------------------------*
	* 2014-10-24      yxd         v1.0.0               修改原因
	 */
    public void init(IRequestCycle cycle) throws Exception
    {
        this.setInModeCode(super.getVisit().getInModeCode());
    }
    /**
     * 
    * @Function: insertAuthTrade()
    * @Description: 记录认证信息
    *
    * @param:
    * @return：
    * @throws：异常描述
    *
    * @version: v1.0.0
    * @author: yxd
    * @date: 2014-10-27 上午11:04:36
    *
    * Modification History:
    * Date         Author          Version            Description
    *---------------------------------------------------------*
    * 2014-10-27      yxd         v1.0.0               修改原因
     */
    public void insertAuthTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CheckUser4BuyInsteadSVC.insertAuthTrade", data);
        setAjax(dataset);
    }
    
    public abstract void setInModeCode(String inModeCode);

}

