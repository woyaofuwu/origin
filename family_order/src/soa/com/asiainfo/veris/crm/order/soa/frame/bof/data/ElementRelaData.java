/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.frame.bof.data;

/***
 * 用于记录元素连带新增了哪些元素
 */
public class ElementRelaData
{

    private String elementId;// 被连带的元素

    private String elementTypeCode;

    private String relaElementId;// 连带的元素

    private String relaElementTypeCode;

    /***
     * * @return Returns the elementId.
     */
    public String getElementId()
    {
        return elementId;
    }

    /***
     * * @return Returns the elementTypeCode.
     */
    public String getElementTypeCode()
    {
        return elementTypeCode;
    }

    /***
     * * @return Returns the relaElementId.
     */
    public String getRelaElementId()
    {
        return relaElementId;
    }

    /***
     * * @return Returns the relaElementTypeCode.
     */
    public String getRelaElementTypeCode()
    {
        return relaElementTypeCode;
    }

    /***
     * @param elementId
     *            The elementId to set.
     */
    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    /***
     * @param elementTypeCode
     *            The elementTypeCode to set.
     */
    public void setElementTypeCode(String elementTypeCode)
    {
        this.elementTypeCode = elementTypeCode;
    }

    /***
     * @param relaElementId
     *            The relaElementId to set.
     */
    public void setRelaElementId(String relaElementId)
    {
        this.relaElementId = relaElementId;
    }

    /***
     * @param relaElementTypeCode
     *            The relaElementTypeCode to set.
     */
    public void setRelaElementTypeCode(String relaElementTypeCode)
    {
        this.relaElementTypeCode = relaElementTypeCode;
    }
}
