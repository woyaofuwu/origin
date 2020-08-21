
package com.asiainfo.veris.crm.order.soa.group.changevpmnscpcode;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeVPMNScpCode extends ChangeMemElement
{
    private ChangeVPMNScpCodeReqData reqData = null;

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeVPMNScpCodeReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeVPMNScpCodeReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setOLD_SCP_CODE(map.getString("OLD_SCP_CODE", ""));
        reqData.setNEW_SCP_CODE(map.getString("NEW_SCP_CODE", ""));
    }

    /**
     * VPMN一些个性化参数存放到主台帐表的预留字段里
     */
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();
        data.put("NEXT_DEAL_TAG", "1"); // 后续状态可处理标志：0-可继续执行，没有异步节点，或异步节点已完成，1-需等待联机指令，其它-待扩展
        data.put("OLCOM_TAG", "1"); // 指令标记：0-不发指令，1-发指令
        data.put("RSRV_STR1", reqData.getUca().getUserId());// 集团雍天虎USER_ID
        data.put("RSRV_STR2", reqData.getOLD_SCP_CODE());// 旧的SCP_CODE
        data.put("RSRV_STR3", reqData.getNEW_SCP_CODE());// 新的SCP_CODE

    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {

        // 得到业务类型
        String tradeTypeCode = "3153";

        // 设置业务类型
        return tradeTypeCode;
    }

}
