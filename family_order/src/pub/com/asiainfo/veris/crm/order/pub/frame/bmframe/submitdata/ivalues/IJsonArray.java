/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues;

import java.io.Serializable;

import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: IJsonArray.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-25 上午11:34:36 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
 */

public interface IJsonArray extends Serializable
{

    String getName();

    IDataset getValue();
}
