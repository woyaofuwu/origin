/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.script.rule.other;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/***
 * 判断用户的OTHER表是否有记录 条件有RSRV_VALUE_CODE,RSRV_STR2,RSRV_STR4
 */
public class ExistsUserOther extends BreBase implements IBREScript
{

    /**
     * (non-Javadoc)
     * 
     * @see com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript#run(com.ailk.common.data.IData, com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam)
     */
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        boolean bResult = false;

        IDataset userOtherList = databus.getDataset("TF_F_USER_OTHER");
        String rsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");
        String rsrvStr1 = ruleParam.getString(databus, "RSRV_STR1");
        String rsrvStr2 = ruleParam.getString(databus, "RSRV_STR2");

        for (Iterator iter = userOtherList.iterator(); iter.hasNext();)
        {
            IData userOther = (IData) iter.next();
            if (rsrvValueCode.equals(userOther.getString("RSRV_VALUE_CODE")) && (rsrvStr1.equals("*") || rsrvStr1.equals(userOther.getString("RSRV_STR1"))) && (rsrvStr2.equals("*") || rsrvStr2.equals(userOther.getString("RSRV_STR2"))))
            {
                bResult = true;
                break;
            }
        }

        return bResult;
    }
}
