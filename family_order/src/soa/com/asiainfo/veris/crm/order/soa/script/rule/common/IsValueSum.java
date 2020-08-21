/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.script.rule.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/***
 * 
 */
public class IsValueSum extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsValueSum.class);

    /**
     * (non-Javadoc)
     * 
     * @see com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript#run(com.ailk.common.data.IData, com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam)
     */
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        /*
         * FEE1 FEE2 FEE3
         */
        int fee1 = Integer.parseInt(ruleParam.getString(databus, "FEE1"));
        int fee2 = Integer.parseInt(ruleParam.getString(databus, "FEE2"));
        int fee3 = Integer.parseInt(ruleParam.getString(databus, "FEE3"));

        if (fee1 + fee2 > fee3)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
