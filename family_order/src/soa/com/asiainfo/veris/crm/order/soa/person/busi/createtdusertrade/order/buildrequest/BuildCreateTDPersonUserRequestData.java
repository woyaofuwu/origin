
package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.order.requestdata.CreateTDPersonUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.buildrequest.BuildCreateUserRequestData;

public class BuildCreateTDPersonUserRequestData extends BuildCreateUserRequestData implements IBuilder
{
    /**
     * 构建登记流程 业务数据输入，后续逻辑处理从RequestData中获取数据，即这里的brd
     */

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        super.buildBusiRequestData(param, brd);
        CreateTDPersonUserRequestData createPersonUserRD = (CreateTDPersonUserRequestData) brd;
        createPersonUserRD.getUca().getUser().setNetTypeCode(PersonConst.TD_NET_TYPE_CODE);
        createPersonUserRD.getUca().getUser().setUserTypeCode("0");
        createPersonUserRD.getUca().getCustomer().setIsRealName(param.getString("REAL_NAME", "0"));
        createPersonUserRD.setCardPasswd(param.getString("CARD_PASSWD", "")); // 初始化密码
        createPersonUserRD.setPassCode(param.getString("PASSCODE", "")); // 密码因子
        createPersonUserRD.setDefaultPwdFlag(param.getString("DEFAULT_PWD_FLAG", "0")); // 是否使用初始密码
        // 设置托管信息上级银行：SUPER_BANK_CODE
        createPersonUserRD.setSuperBankCode(param.getString("SUPER_BANK_CODE", ""));
        createPersonUserRD.setOpenType(param.getString("OPEN_TYPE","0"));
        
        createPersonUserRD.setIsTTtransfer(param.getString("TT_TRANSFER",""));

        
        String agentId = param.getString("AGENT_DEPART_ID", "");
        if (agentId.equals(""))
            agentId = param.getString("AGENT_DEPART_ID1", "");
        if (!agentId.equals(""))
        {
            if (agentId.indexOf(" ") != -1)
                agentId = (agentId.split(" "))[0];
        }
        else
            agentId = CSBizBean.getVisit().getDepartId();
        createPersonUserRD.setAgentDepartId(agentId);// 特殊处理 sunxin
        createPersonUserRD.setBindSaleTag(param.getString("BIND_SALE_TAG", "0"));// 绑定营销活动
        createPersonUserRD.setAgentCustName(param.getString("AGENT_CUST_NAME", ""));// 经办人名称
        createPersonUserRD.setAgentPsptTypeCode(param.getString("AGENT_PSPT_TYPE_CODE", ""));// 经办人证件类型
        createPersonUserRD.setAgentPsptId(param.getString("AGENT_PSPT_ID", ""));// 经办人证件号码
        createPersonUserRD.setAgentPsptAddr(param.getString("AGENT_PSPT_ADDR", ""));// 经办人证件地址
    }

    /**
     * 定义requestData对象
     */

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CreateTDPersonUserRequestData();
    }
}
