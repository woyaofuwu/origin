/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.interfaces;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.ivalues.IPageData;

public interface IPageDataBuilder
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-9 上午10:22:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
     */
    public IPageData buildPageOperData(IData param) throws Exception;
}
