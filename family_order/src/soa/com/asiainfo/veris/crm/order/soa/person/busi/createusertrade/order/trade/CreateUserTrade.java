
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PostTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreateUserRequestData;

public class CreateUserTrade extends BaseCreateUserTrade implements ITrade
{

    /**
     * 创建开户具体业务台账
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData CreateUserRD = (CreateUserRequestData) btd.getRD();

        super.createBusiTradeData(btd);

        if (CreateUserRD.getPostInfoPostTag().equals("1"))
        {

            createPostTradeData(btd);// 邮寄资料
        }

        createResTradeData(btd);// 资源台账处理

    }

    /**
     * 生成台帐邮寄资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createPostTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData createUserRD = (CreateUserRequestData) btd.getRD();
        String serialNumber = createUserRD.getUca().getUser().getSerialNumber();
        String newPostContent = createUserRD.getpostContent(); // 邮政投递内容拼串
        String newEmailConent = createUserRD.getEmailContent();// EMAIL投递内容拼串
        String newMmsConent = createUserRD.getMMScontent();// MMS投递内容拼串

        if (StringUtils.isNotBlank(newPostContent))
        {
            PostTradeData postTD = new PostTradeData();
            postTD.setId(createUserRD.getUca().getUserId()); // 标识:客户、用户或帐户标识
            postTD.setIdType("1");// 标识类型：0-客户，1-用户，2-帐户
            postTD.setPostName(createUserRD.getPostInfoPostName());
            postTD.setPostTag(createUserRD.getPostInfoPostTag());// 邮寄标志：0-不邮寄，1-邮寄
            postTD.setPostContent(newPostContent);
            postTD.setPostTypeset("0");// 邮寄方式：0-邮政，2-Email
            postTD.setPostCyc(createUserRD.getPostInfoPostCyc());// 邮寄周期：0-按月，1-按季，2-按年
            postTD.setPostAddress(createUserRD.getPostInfoPostAddress());
            postTD.setPostCode(createUserRD.getPostInfoPostCode());
            postTD.setEmail(createUserRD.getPostInfoEmail());
            postTD.setFaxNbr(createUserRD.getPostInfoFaxNbr());
            postTD.setCustType("0");
            postTD.setStartDate(SysDateMgr.getSysTime());// 预约开户需要处理时间，修改 sunxin
            postTD.setEndDate(SysDateMgr.getTheLastTime());
            postTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            postTD.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, postTD);
        }

        if (StringUtils.isNotBlank(newEmailConent))
        {
            PostTradeData postTD = new PostTradeData();
            postTD.setId(createUserRD.getUca().getUserId()); // 标识:客户、用户或帐户标识
            postTD.setIdType("1");// 标识类型：0-客户，1-用户，2-帐户
            postTD.setPostName(createUserRD.getPostInfoPostName());
            postTD.setPostTag(createUserRD.getPostInfoPostTag());// 邮寄标志：0-不邮寄，1-邮寄
            postTD.setPostContent(newEmailConent);
            postTD.setPostTypeset("2");// 邮寄方式：0-邮政，2-Email
            postTD.setPostCyc(createUserRD.getPostInfoPostCyc());// 邮寄周期：0-按月，1-按季，2-按年
            postTD.setPostAddress(createUserRD.getPostInfoPostAddress());
            postTD.setPostCode(createUserRD.getPostInfoPostCode());
            postTD.setEmail(createUserRD.getPostInfoEmail());
            postTD.setFaxNbr(createUserRD.getPostInfoFaxNbr());
            postTD.setCustType("0");
            postTD.setStartDate(SysDateMgr.getSysTime());// 预约开户需要处理时间，修改 sunxin
            postTD.setEndDate(SysDateMgr.getTheLastTime());
            postTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            postTD.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, postTD);
        }

        if (StringUtils.isNotBlank(newMmsConent))
        {
            PostTradeData postTD = new PostTradeData();
            postTD.setId(createUserRD.getUca().getUserId()); // 标识:客户、用户或帐户标识
            postTD.setIdType("1");// 标识类型：0-客户，1-用户，2-帐户
            postTD.setPostName(createUserRD.getPostInfoPostName());
            postTD.setPostTag(createUserRD.getPostInfoPostTag());// 邮寄标志：0-不邮寄，1-邮寄
            postTD.setPostContent(newMmsConent);
            postTD.setPostTypeset("3");// 邮寄方式：0-邮政，2-Email
            postTD.setPostCyc(createUserRD.getPostInfoPostCyc());// 邮寄周期：0-按月，1-按季，2-按年
            postTD.setPostAddress(createUserRD.getPostInfoPostAddress());
            postTD.setPostCode(createUserRD.getPostInfoPostCode());
            postTD.setEmail(createUserRD.getPostInfoEmail());
            postTD.setFaxNbr(createUserRD.getPostInfoFaxNbr());
            postTD.setCustType("0");
            postTD.setStartDate(SysDateMgr.getSysTime());// 预约开户需要处理时间，修改 sunxin
            postTD.setEndDate(SysDateMgr.getTheLastTime());
            postTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            postTD.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, postTD);
        }

    }

    /**
     * 资源台账处理
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createResTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData createUserRD = (CreateUserRequestData) btd.getRD();
        String serialNumber = createUserRD.getUca().getUser().getSerialNumber();

        // 先拼手机
        ResTradeData resTD = new ResTradeData();
        ResTradeData resTD1 = new ResTradeData();
        String inst_id = SeqMgr.getInstId();
        resTD.setUserId(createUserRD.getUca().getUserId());
        resTD.setUserIdA("-1");
        resTD.setResTypeCode("0");
        resTD.setResCode(createUserRD.getUca().getUser().getSerialNumber());
        resTD.setImsi("0");
        resTD.setKi("");
        resTD.setInstId(inst_id);
        resTD.setStartDate(SysDateMgr.getSysTime());
        resTD.setEndDate(SysDateMgr.getTheLastTime());
        resTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        // --->和多号业务需求的改造:号码资源台账的 RSRVSTR2 设置为 Msisdn_type
        resTD.setRsrvStr2(((CreatePersonUserRequestData)createUserRD).getMsisdn_type() );
        // 如果是网上选号则置tf_f_user_res的号码的RSRV_TAG2为1 , SIM卡的RSRV_TAG2不做操作

        if ("1".equals(createUserRD.getNetChoose()))
        {
            resTD.setRsrvTag2("1");
        }

        if ("1".equals(createUserRD.getOpenType()))
        {
            resTD.setRsrvTag3("1");
        }
        if ("1".equals(createUserRD.getM2mFlag()))
            resTD.setRsrvStr5("01");

        btd.add(serialNumber, resTD);
        // 再拼号卡
        String inst_ida = SeqMgr.getInstId();
        resTD1.setUserId(createUserRD.getUca().getUserId());
        resTD1.setUserIdA("-1");
        resTD1.setInstId(inst_ida);
        resTD1.setStartDate(SysDateMgr.getSysTime());// 预约开户需要处理时间，修改 sunxin
        resTD1.setEndDate(SysDateMgr.getTheLastTime());
        resTD1.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTD1.setResTypeCode("1");
        resTD1.setResCode(createUserRD.getSimCardNo());
        resTD1.setImsi(createUserRD.getImsi());
        resTD1.setKi(createUserRD.getKi());
        if("500".equals(btd.getMainTradeData().getTradeTypeCode())){ //由于很少部分RES_KIND_CODE为空时造成预开没有正常回收问题核查，陈小梅要求。
        	IDataset flags = StaticInfoQry.getStaticValidValueByTypeId("RES_KIND_CODE_FLAG", "1");
        	String resKindCode = createUserRD.getResKindCode();
        	if(IDataUtil.isNotEmpty(flags) && flags.size() > 0 && StringUtils.isBlank(resKindCode)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量预开户时，组装TF_B_TRADE_RES时，RES_KIND_CODE为空了，请核查！");
        	}
        }
        resTD1.setRsrvStr1(createUserRD.getResKindCode());// SIM卡的RESKIND|CAPACITY(资源类型|SIM卡容量)现为一个字段 需要统计侧改造
        // add by zhangxing for HNYD-REQ-20100225-002关于开发代理商白卡空卡买断的报表统计需求
        resTD1.setRsrvStr3(createUserRD.getNewAgentSaleTag());
        resTD1.setRsrvStr4(createUserRD.getResKindCode());// sim的小类编码 可能没有
        // xiekl物联网新增 卡物联网类型放入rsrv_str5

        if ("1".equals(createUserRD.getM2mFlag())){
            resTD1.setRsrvStr5("01"); // 后面action统一处理此类操作 sunxin
        // add byzhangxing for HNYD-REQ-20100225-002关于开发代理商白卡空卡买断的报表统计需求
            resTD1.setRsrvTag1(createUserRD.getResFlag4G()); //保存资料接口过来的标识2、3、4G标识
        }else
            resTD1.setRsrvStr5(createUserRD.getEmptyCardId()); // 白卡卡号

        // add by wenhj HNYD-REQ-20110402-010 start
        String feeTag = "A";
        if ("0".equals(createUserRD.getSimFeeTag()))
            feeTag = "B";
        /*
         * if ("1".equals(createUserRD.getSimFeeTag())) feeTag = "C";
         */
       
        resTD1.setRsrvTag2(feeTag); // A 老买断的卡 , B 未买断的卡 C新买断卡 新增c类型 sunxin
        resTD1.setRsrvTag3("1".equals(createUserRD.getFlag4G()) ? "1" : "0"); // 4G卡标记@add by wukw3 20140401
        resTD1.setRsrvNum5(createUserRD.getSimCardSaleMoney());// 代理商买断开户处理.用于后续返还使用 sunxin
        resTD1.setRsrvStr2(createUserRD.getResTypeCode());
        // add by wenhj HNYD-REQ-20110402-010 end
        btd.add(serialNumber, resTD1);
        String eid = createUserRD.getEid();
    	if(StringUtils.isNotBlank(eid))
        {
    		//一号一终端
    		ResTradeData resTD2 = new ResTradeData();
    		String inst_ida2 = SeqMgr.getInstId();
    	    resTD2.setUserId(createUserRD.getUca().getUserId());
    	    resTD2.setUserIdA("-1");
    	    resTD2.setInstId(inst_ida2);
    	    resTD2.setStartDate(SysDateMgr.getSysTime());// 预约开户需要处理时间，修改 sunxin
    	    resTD2.setEndDate(SysDateMgr.getTheLastTime());
    	    resTD2.setModifyTag(BofConst.MODIFY_TAG_ADD);
    	    resTD2.setResTypeCode("E");
    	    resTD2.setResCode(createUserRD.getSimCardNo());
    	    resTD2.setImsi("");
    	    resTD2.setKi("");
    	    resTD2.setStartDate(SysDateMgr.getSysTime());
            resTD2.setEndDate(SysDateMgr.getTheLastTime());
            resTD2.setRsrvStr1("OneNoOneTerminal");
            resTD2.setRsrvStr3(createUserRD.getPrimarySerialNumber());
            resTD2.setRsrvStr2(createUserRD.getEid());
            resTD2.setRsrvStr4(createUserRD.getNewImei());
            btd.add(serialNumber, resTD2);
        }

    }

}
