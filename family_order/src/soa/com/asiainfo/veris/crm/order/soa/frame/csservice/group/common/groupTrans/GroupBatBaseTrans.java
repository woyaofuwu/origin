
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.groupTrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;

public class GroupBatBaseTrans implements ITrans
{

    /**
     * 初始化批量信息
     * 
     * @param batData
     * @throws Exception
     */
    private void batInitial(IData batData) throws Exception
    {
        // 初始化批量条件信息
        batInitialCond(batData);

        // 初始化基本信息
        batInitialBase(batData);

        // 初始化其他信息(子类继承)
        batInitialSub(batData);
    }

    /**
     * 初始化基本信息
     * 
     * @param batData
     * @throws Exception
     */
    private void batInitialBase(IData batData) throws Exception
    {
    	
        IData svcData = batData.getData("svcData");

        if (IDataUtil.isEmpty(svcData))
        {
            svcData = new DataMap();
            
            if("true".equals(batData.getString("IS_CONFIRM")) && 
            		("BATADDVPMNMEM".equals(batData.getString("BATCH_OPER_TYPE")) 
            		|| "MUSICRINGMEM".equals(batData.getString("BATCH_OPER_TYPE"))
            		|| "BATADDWPGRPMEMBER".equals(batData.getString("BATCH_OPER_TYPE"))))
            {
                svcData.putAll(batData);
            }
            batData.put("svcData", svcData);
        }

        /*add by chenzg@20180703 REQ201804280001集团合同管理界面优化需求 批次号，生成集团业务稽核工单需要，要求一个批次只生成一笔稽核工单*/
        svcData.put("ORIG_BATCH_ID", batData.getString("BATCH_ID")); 
        
        svcData.put("BATCH_ID", batData.getString("OPERATE_ID")); // 为了导出批量生成的台账完工报错信息 改存OPERATE_ID
        svcData.put(GroupBaseConst.X_SUBTRANS_CODE, "GrpBat"); // 批量标志

        svcData.put("IF_SMS", "1".equals(batData.getString("SMS_FLAG")) ? true : false); // 批量业务默认不发短信

        if (StringUtils.isEmpty(batData.getString("IN_MODE_CODE")))
        {
            batData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        }

        svcData.put("NEED_RULE", batData.getData("condData", new DataMap()).getBoolean("NEED_RULE", true));// 传false表示不需要规则校验
        
        IData condData = batData.getData("condData", new DataMap());
        if(IDataUtil.isNotEmpty(condData)){
        	String mebFileShow = condData.getString("MEB_FILE_SHOW","");
        	if(StringUtils.isNotBlank(mebFileShow) && StringUtils.equals("true", mebFileShow)){
        		svcData.put("MEB_FILE_SHOW", mebFileShow);
        		svcData.put("MEB_FILE_LIST", condData.getString("MEB_FILE_LIST",""));
        	}
        	svcData.put("MEB_VOUCHER_FILE_LIST", condData.getString("MEB_VOUCHER_FILE_LIST", "")); 	//上传凭证附件
        	svcData.put("AUDIT_STAFF_ID", condData.getString("AUDIT_STAFF_ID", "")); 	//审核工号
        }
        
        //VPMN成员新增二次确认短信修改
        if("".equals(svcData.getString("BATCH_OPER_TYPE","")))
        {
            String batchOperType = batData.getString("BATCH_OPER_TYPE","");
            if(!"".equals(batchOperType))
            {
                svcData.put("BATCH_OPER_TYPE", batchOperType);
            }
        }
    }

    /**
     * 初始化批量条件信息
     * 
     * @param batData
     * @throws Exception
     */
    private void batInitialCond(IData batData) throws Exception
    {
        String condStr = batData.getString("CODING_STR", "");

        if (StringUtils.isBlank(condStr))
        {
            String batchTaskId = IDataUtil.getMandaData(batData, "BATCH_TASK_ID","0000000000000000");
            
            if(!"0000000000000000".equals(batchTaskId))
                condStr = BatTradeInfoQry.getTaskCondString(batchTaskId);
        }

        if (StringUtils.isNotBlank(condStr))
        {
            IData condData = batData.getData("condData");

            if (IDataUtil.isEmpty(condData))
            {
                condData = new DataMap();
                batData.put("condData", condData);
            }

            condData.putAll(new DataMap(condStr));
        }
    }

    /**
     * 初始化其他信息(子类可实现)
     * 
     * @param batData
     * @throws Exception
     */
    protected void batInitialSub(IData batData) throws Exception
    {

    }

    /**
     * 子类构建服务请求数据
     * 
     * @param iData
     * @throws Exception
     */
    protected void builderBaseSvcData(IData batData) throws Exception
    {

    }

    /**
     * 构建服务请求数据
     * 
     * @param iData
     * @throws Exception
     */
    protected void builderSvcData(IData batData) throws Exception
    {
        builderBaseSvcData(batData);
    }

    /**
     * 批量校验
     * 
     * @param iData
     * @throws Exception
     */
    private void checkGroupBatRequestData(IData batData) throws Exception
    {
        // 校验,最好能够抽象成接口方法,暂时先过程调用
        checkRequestData(batData);
    }

    /**
     * 校验请求参数
     * 
     * @param iData
     * @throws Exception
     */
    protected void checkRequestData(IData batData) throws Exception
    {
        checkRequestDataComm(batData);

        checkRequestDataSub(batData);
    }

    /**
     * 公用请求参数校验
     * 
     * @param iData
     * @throws Exception
     */
    protected void checkRequestDataComm(IData batData) throws Exception
    {

    }

    /**
     * 子类校验请求参数
     * 
     * @param iData
     * @throws Exception
     */
    protected void checkRequestDataSub(IData batData) throws Exception
    {

    }

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 批量初始化
        batInitial(batData);

        // 批量校验
        checkGroupBatRequestData(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }
}
