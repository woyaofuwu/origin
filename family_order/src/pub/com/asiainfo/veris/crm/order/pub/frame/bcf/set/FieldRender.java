/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: FieldRender.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:13:38 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class FieldRender
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 下午03:14:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public static String getDBLinkHTMLString(String gridId, String colName, String id, String likeOp)
    {
        return "<a href=\"javascript:void(0);\" onclick=\"TableRowSet_OnDBLink('" + gridId + "','" + colName + "','" + id + "');\">" + id + "</a>";
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 下午03:13:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public static String getDBLinkHTMLString(String gridId, String colName, String id, String text, String likeOp)
    {
        return "<a href=\"javascript:void(0);\" onclick=\"TableRowSet_OnDBLink('" + gridId + "','" + colName + "','" + id + "');\">" + text + "</a>";
    }
}
