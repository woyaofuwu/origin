/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.person.busi.exchangegoods.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/***
 * 
 */
public class ExchangeGoodsReqData extends BaseReqData
{

    private String newImei;// 新串号

    private String oldImei;// 老串号

    /***
     * * @return Returns the newImei.
     */
    public String getNewImei()
    {
        return newImei;
    }

    /***
     * * @return Returns the oldImei.
     */
    public String getOldImei()
    {
        return oldImei;
    }

    /***
     * @param newImei
     *            The newImei to set.
     */
    public void setNewImei(String newImei)
    {
        this.newImei = newImei;
    }

    /***
     * @param oldImei
     *            The oldImei to set.
     */
    public void setOldImei(String oldImei)
    {
        this.oldImei = oldImei;
    }
}
