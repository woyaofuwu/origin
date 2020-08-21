
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreateUserRequestData;

public class BuildCreateUserRequestData extends BaseBuildCreateUserRequestData implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        super.buildBusiRequestData(param, brd);
        CreateUserRequestData CreateUserRD = (CreateUserRequestData) brd;

        CreateUserRD.setSimCardNo(param.getString("SIM_CARD_NO"));
        CreateUserRD.setImsi(param.getString("IMSI"));
        CreateUserRD.setKi(param.getString("KI"));
        CreateUserRD.setResTypeCode(param.getString("RES_TYPE_CODE", ""));
        CreateUserRD.setResKindName(param.getString("RES_KIND_NAME", ""));
        CreateUserRD.setEmptyCardId(param.getString("EMPTY_CARD_ID", ""));
        CreateUserRD.setNewAgentSaleTag(param.getString("NEW_AGENT_SALE_TAG", ""));
        CreateUserRD.setResKindCode(param.getString("RES_KIND_CODE", ""));
        CreateUserRD.setSimFeeTag(param.getString("SIM_FEE_TAG", ""));
        CreateUserRD.setSimCardSaleMoney(param.getString("SIM_CARD_SALE_MONEY", "0"));
        CreateUserRD.setOPC(param.getString("OPC_VALUE", ""));
        CreateUserRD.setFlag4G(param.getString("FLAG_4G", "0"));
        CreateUserRD.setNetChoose(param.getString("INFO_TAG", ""));// 网上选号 页面赋值
        CreateUserRD.setM2mFlag(param.getString("M2M_FLAG", "0"));// 物联网
        CreateUserRD.setOpenType("AGENT_OPEN".equals(param.getString("OPEN_TYPE", "")) ? "1" : "0");// 是否为代理商开户
        CreateUserRD.setSaleMoney(param.getString("SALE_MONEY", ""));
        
        CreateUserRD.setOccupyTypeCode(param.getString("OCCUPY_TYPE_CODE", ""));// 选号类型 1是网上选号，3是自助选号
        
        if ("1".equals(param.getString("M2M_FLAG"))) {  //记录物联网用户sim 制式标识
        	CreateUserRD.setResFlag4G(param.getString("RES_FLAG_4G","")); //  调资源接口返回的标识sim2、3、4G标识
		}
        // 邮寄信息
        CreateUserRD.setChrOpenPostInfo(param.getString("CHR_OPENPOSTINFO"));
        CreateUserRD.setPostInfoPostTag(param.getString("POST_TAG", ""));
        CreateUserRD.setPostInfoPostTypeSet(param.getString("POST_TYPESET", ""));
        CreateUserRD.setpostContent(param.getString("POSTTYPE_CONTENT", ""));
        CreateUserRD.setemailContent(param.getString("EMAILTYPE_CONTENT", ""));
        CreateUserRD.setMMScontent(param.getString("MMSTYPE_CONTENT", ""));
        CreateUserRD.setPostInfoPostCyc(param.getString("POST_CYC", ""));
        CreateUserRD.setPostInfoPostAddress(param.getString("POST_ADDRESS", ""));
        CreateUserRD.setPostInfoPostCode(param.getString("POST_CODE", ""));
        CreateUserRD.setPostInfoPostName(param.getString("POST_NAME", ""));
        CreateUserRD.setPostInfoFaxNbr(param.getString("FAX_NBR", ""));
        CreateUserRD.setPostInfoEmail(param.getString("EMAIL", ""));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CreateUserRequestData();
    }

}
