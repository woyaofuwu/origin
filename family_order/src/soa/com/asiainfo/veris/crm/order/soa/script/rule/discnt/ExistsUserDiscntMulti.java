/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/***
 * 
 */
public class ExistsUserDiscntMulti extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsUserDiscntMulti.class);

    /**
     * (non-Javadoc)
     * 
     * @see com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript#run(com.ailk.common.data.IData, com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam)
     */
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        /*
         * EPARCHY_CODE PARAM_CODE USER_ID
         */
        boolean resultFlag = false;
        IDataset commparaList = BreQryForCommparaOrTag.getCommparaCode1("CSM", 2001, ruleParam.getString("PARAM_CODE", ""), CSBizBean.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(commparaList))
        {
            IDataset userAllDiscnt = databus.getDataset("TF_F_USER_DISCNT_ALL");// 用户有效的优惠，包含未生效的
            if (IDataUtil.isEmpty(userAllDiscnt))
            {
                resultFlag = false;
            }
            else
            {
                for (int i = userAllDiscnt.size() - 1; i >= 0; i--)
                {
                    IData userDiscnt = userAllDiscnt.getData(i);
                    for (int j = commparaList.size() - 1; j >= 0; j--)
                    {
                        IData commpara = commparaList.getData(j);
                        if (userDiscnt.getString("DISCNT_CODE").equals(commpara.getString("PARA_CODE1")))
                        {
                            // 只要找到一条就够了
                            resultFlag = true;
                            break;
                        }
                    }
                    if (resultFlag)
                    {
                        break;
                    }
                }
            }
        }
        else
        {
            resultFlag = false;
        }

        return resultFlag;
    }

}
