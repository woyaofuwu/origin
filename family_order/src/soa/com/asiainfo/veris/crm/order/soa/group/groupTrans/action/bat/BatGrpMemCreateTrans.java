
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;

public class BatGrpMemCreateTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        //VPMN二次确认短信修改，短信回复后，再次调用接口时，不需要再次进行该参数的转换
        String isConfirm = batData.getString("IS_CONFIRM","");
        String batchOperType = batData.getString("BATCH_OPER_TYPE","");
        if("BATADDVPMNMEM".equals(batchOperType) && "true".equals(isConfirm))
            return ;
        if("MUSICRINGMEM".equals(batchOperType) && "true".equals(isConfirm))
            return ;
        if("BATADDWPGRPMEMBER".equals(batchOperType) && "true".equals(isConfirm))
            return ;
        
        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("MEM_ROLE_B", condData.getString("MEM_ROLE_B", "1"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO")));
        svcData.put("RES_INFO", new DatasetList("[]"));
        svcData.put("PRODUCT_PARAM_INFO", new DatasetList("[]"));
        svcData.put("PLAN_TYPE_CODE", condData.getString("PLAN_TYPE"));
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "false"));
        svcData.put("REMARK", batData.getString("REMARK", condData.getString("PRODUCT_ID") + "批量成员新增"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE"));
        svcData.put("PAGE_SELECTED_TC", condData.getString("PAGE_SELECTED_TC", "false"));		//add by chenzg@20180315 界面上选了"下发二次确认短信"选项
        svcData.put("ALL_PARAM", condData);
        
        String sms_flag = batData.getString("SMS_FLAG");
        svcData.put("IF_SMS", (StringUtils.isBlank(sms_flag) || StringUtils.equals("0", sms_flag)) ? false : true);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        IDataUtil.chkParam(condData, "PLAN_TYPE");

        IDataUtil.chkParam(condData, "MEM_ROLE_B");

        IDataUtil.chkParam(condData, "PRODUCT_ID");

        IDataUtil.chkParam(condData, "ELEMENT_INFO");

        IDataUtil.chkParam(condData, "USER_ID");

        IDataUtil.chkParam(batData, "SERIAL_NUMBER");
    }

}
