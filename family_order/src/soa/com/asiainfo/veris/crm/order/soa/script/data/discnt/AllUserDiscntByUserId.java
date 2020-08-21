/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.script.data.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

/***
 * 查询用户所有有效的优惠，包含未生效的
 */
public class AllUserDiscntByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(AllUserDiscntByUserId.class);

    /**
     * (non-Javadoc)
     * 
     * @see com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare#run(com.ailk.common.data.IData)
     */
    @Override
    public void run(IData databus) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserDiscntByUserId() >>>>>>>>>>>>>>>>>>");

        IDataset listUser = databus.getDataset("TF_F_USER");

        IData param = new DataMap();
        param.put("USER_ID", listUser.get(0, "USER_ID"));

        databus.put("TF_F_USER_DISCNT_ALL", Dao.qryByCode("TF_F_USER_DISCNT", "SEL_INS_DESTROY_DISCNT", param));

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserDiscntByUserId() <<<<<<<<<<<<<<<<<<<");
    }

}
