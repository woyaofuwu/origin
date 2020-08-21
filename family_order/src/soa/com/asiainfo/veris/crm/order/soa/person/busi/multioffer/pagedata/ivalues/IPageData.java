/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.ivalues;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: IPageData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-9 上午10:22:11 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
 */
public interface IPageData
{

    public String getItemType();

    public IPageData getParentPageData();

    public void setItemType(String itemType);

    public void setParentPageData(IPageData parent);
}
