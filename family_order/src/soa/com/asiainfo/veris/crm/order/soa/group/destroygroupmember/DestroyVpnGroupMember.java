
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;
import com.asiainfo.veris.crm.order.soa.script.rule.checkafter.is4GVipSimBak;

/*
 * 处理普通vpn集团成员注销,相对于基类 此处要增加处理tf_b_trade_vpn_meb表
 */
public class DestroyVpnGroupMember extends DestroyGroupMember
{
    protected String currentMonthLastTime; // 账期最后时间

    protected DestroyVpnGroupMemberReqData reqData = null;

    public DestroyVpnGroupMember()
    {

    }

    /**
     * 预处理数据
     * 
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        String memUserId = reqData.getUca().getUserId();
        currentMonthLastTime = DiversifyAcctUtil.getLastTimeThisAcctday(memUserId, null); // 获取用户账期最后一天

        // 产品元素注销VPN代理商套餐特殊处理：退655代理商套餐(VPMN JPA)时，需对'1401','1402','1403','4807'套餐进行退订
        infoDiscntDatadeal();

        // add by lixiuyu@20100505 对集团关键人的处理(1-联系人,2-集团领导人)
        if ("1".equals(reqData.getJION_IN()))
        {
            validchkCustManagerId();
        }

        // add by lixiuyu@20101102 验证用户的主套餐是否修改
        validchkProductChange();

        // add by lixiuyu@20101221 特殊处理首月免收集团3元套餐功能费：退Ｖ网成员时，需对3318集团V网首月免收功能费套餐进行退订
        dealVpnMonthDiscnt();
        // add by yuzs: 特殊处理军网695,696
        deal695And696Discnt();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVpn();
        // j2ee 发短信转为配置,其他照旧
        infoRegDataOther();

    }

    /**
     * VPMN一些个性化参数存放到主台帐表的预留字段里
     */
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        // 短信需要的参数 start
        String smsTime = this.getAcceptTime();
        if (diversifyBooking)
        {
            smsTime = currentMonthLastTime;
        }
        data.put("RSRV_STR7", smsTime);
        // 短信需要的参数 end
    }

    //是全网彩印新增
    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
        super.setTradeSvc(map);
        
        if (IDataUtil.isNotEmpty(map)){//全网彩印要使用
            if("860".equals(map.getString("ELEMENT_ID","")) 
                    && "S".equals(map.getString("ELEMENT_TYPE_CODE",""))){
                //校验集团是否是全国V网
                String vwFlagStr = "0";
                IDataset vpnInfo = UserVpnInfoQry.getVpnInfoByUser(reqData.getGrpUca().getUserId());
                if (null != vpnInfo && vpnInfo.size() > 0){
                    int index = vpnInfo.size();
                    for(int i = 0 ; i < index ; i++){
                        IData vpn = vpnInfo.getData(i);
                        if ("4".equals(vpn.getString("VPN_SCARE_CODE")) 
                                || "2".equals(vpn.getString("VPN_SCARE_CODE"))){
                            vwFlagStr = "1";
                            break;
                        }
                    }
                }

                IData inparams = new DataMap();
                if ("0".equals(vwFlagStr)){
                    inparams.put("USER_ID", reqData.getGrpUca().getUserId());
                    inparams.put("SERVICE_ID", "801");
                    IDataset userPlatSvc = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_SVC_ID", inparams);
                    if (null != userPlatSvc && !userPlatSvc.isEmpty()){
                        vwFlagStr = "1";
                    }
                }
                
                map.put("RSRV_STR6", "0"); //0非VoLTE用户 1：VoLTE用户
                map.put("RSRV_STR7", vwFlagStr);//标识用户加入的V网是否是全国V网,1:全国V网;0:本地V网
                
            }
        }
    }
    
    /**
     * 特殊处理首月免收集团3元套餐功能费
     * 
     * @author lixiuyu@20101221
     * @throws Exception
     */
    public void dealVpnMonthDiscnt() throws Exception
    {
        IDataset discnts = reqData.cd.getDiscnt();
        if(IDataUtil.isNotEmpty(discnts)){
        	for(int i = 0; i<discnts.size(); i++){
        		IData discnt = discnts.getData(i);
        		 String discnt_code = discnt.getString("ELEMENT_ID");
        		 if ("3318".equals(discnt_code)){  
	                    discnt.put("END_DATE", currentMonthLastTime);// 分散账期修改 截止本月底
	
	                    // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
	                    discnt.put("DIVERSIFY_ACCT_TAG", "1");
	                    break;
                 }
        	}
        }else{ 
	        String memUserId = reqData.getUca().getUserId();
	        IDataset discntset = UserDiscntInfoQry.getAllValidDiscntByUserId(memUserId);// 查询用户下所有的资费
	        // 退Ｖ网成员时，需对3318集团V网首月免收功能费套餐进行退订
	        if (IDataUtil.isNotEmpty(discntset))
	        {
	            for (int j = 0; j < discntset.size(); j++)
	            {
	                IData discnt = (IData) discntset.get(j);
	                String discnt_code = discnt.getString("DISCNT_CODE");
	                if ("3318".equals(discnt_code))
	                {
	                    discnt.put("ELEMENT_ID", discnt_code);
	                    discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
	
	                    discnt.put("END_DATE", currentMonthLastTime);// 分散账期修改 截止本月底
	
	                    // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
	                    discnt.put("DIVERSIFY_ACCT_TAG", "1");
	                    discnts.add(discnt);
	                    break;
	                }
	            }
	        }
        }
    }
    /*
     * 特殊处理军网695,696注销后的失效时间
     */
    public void deal695And696Discnt() throws Exception
    {
        IDataset discnts = reqData.cd.getDiscnt();

        String memUserId = reqData.getUca().getUserId();
        String lastTimeThisAcctday = DiversifyAcctUtil.getLastTimeThisAcctday(memUserId, SysDateMgr.getLastDateThisMonth());
        
        if(IDataUtil.isNotEmpty(discnts)){
        	for(int i = 0; i<discnts.size(); i++){
        		IData discnt = discnts.getData(i);
        		 String discnt_code = discnt.getString("ELEMENT_ID");
                 if ("695".equals(discnt_code)||"696".equals(discnt_code))
                 {  
                     discnt.put("END_DATE", lastTimeThisAcctday);

                     // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
                     discnt.put("DIVERSIFY_ACCT_TAG", "1");  
                 }
        	}
        }else{ 
	        IDataset discntset = UserDiscntInfoQry.getAllValidDiscntByUserId(memUserId);// 查询用户下所有的资费
	        if (IDataUtil.isNotEmpty(discntset))
	        {
	            for (int j = 0; j < discntset.size(); j++)
	            {
	                IData discnt = (IData) discntset.get(j);
	                String discnt_code = discnt.getString("DISCNT_CODE");
	                if ("695".equals(discnt_code)||"696".equals(discnt_code))
	                {
	                    discnt.put("ELEMENT_ID", discnt_code);
	                    discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
	                    discnt.put("END_DATE", lastTimeThisAcctday);
	
	                    // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
	                    discnt.put("DIVERSIFY_ACCT_TAG", "1");
	                    discnts.add(discnt);
	                    break;
	                }
	            }
	        }
        }
    }
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyVpnGroupMemberReqData();
    }

    /**
     * 655代理商套餐(VPMN JPA)，特殊处理
     * 
     * @throws Exception
     */

    public void infoDiscntDatadeal() throws Exception
    {
    	String serialNumber = reqData.getGrpUca().getSerialNumber();// 获取集团产品编码
        IDataset dataset = reqData.cd.getDiscnt();
        String memUserId = reqData.getUca().getUserId();
        boolean isVpmnJpaDel = false;
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = (IData) dataset.get(i);
                String element_id = data.getString("ELEMENT_ID", "");
                String state = data.getString("MODIFY_TAG", "");
                // 退655代理商套餐(VPMN JPA)时，需对'1401','1402','1403','4807'套餐进行退订
                // modify by chenzg@20170814 渠道代理商或店员在删除移动公司集团网（V0SJ004535）后，次月自动取消渠道代理商或店员通信补贴（包括话音补贴及新业务补贴）套餐自动截止到当月底
                if ( TRADE_MODIFY_TAG.DEL.getValue().equals(state) && "655".equals(element_id) )
                {
                    IDataset discnts = UserDiscntInfoQry.getAllValidDiscntByUserId(memUserId); // 查询用户下所有的资费
                    if (IDataUtil.isNotEmpty(discnts))
                    {
                        for (int j = 0; j < discnts.size(); j++)
                        {
                            IData discnt = (IData) discnts.get(j);
                            String discnt_code = discnt.getString("DISCNT_CODE");
                            if ("1401".equals(discnt_code) || "1402".equals(discnt_code) || "1403".equals(discnt_code) || "4807".equals(discnt_code))
                            {
                            	isVpmnJpaDel = true;
                                discnt.put("ELEMENT_ID", discnt_code);
                                discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                discnt.put("END_DATE", currentMonthLastTime);// 分散账期修改
                                // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
                                discnt.put("DIVERSIFY_ACCT_TAG", "1");
                                dataset.add(discnt);
                            }
                        }
                    }
                }
            }
        }
        /*如果不是：退655代理商套餐(VPMN JPA)时，需对'1401','1402','1403','4807'套餐进行退订，
         * 则判断是否退出V0SJ004535集团，也需要对'1401','1402','1403','4807'套餐进行退订
         * */
        if(!isVpmnJpaDel){
        	if("V0SJ004535".equals(serialNumber) || "V0SJ004536".equals(serialNumber)){
        		IDataset discnts = UserDiscntInfoQry.getAllValidDiscntByUserId(memUserId); // 查询用户下所有的资费
                if (IDataUtil.isNotEmpty(discnts))
                {
                    for (int j = 0; j < discnts.size(); j++)
                    {
                        IData discnt = (IData) discnts.get(j);
                        String discnt_code = discnt.getString("DISCNT_CODE");
                        if ("1401".equals(discnt_code) || "1402".equals(discnt_code) || "1403".equals(discnt_code) || "4807".equals(discnt_code))
                        {
                        	isVpmnJpaDel = true;
                            discnt.put("ELEMENT_ID", discnt_code);
                            discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            discnt.put("END_DATE", currentMonthLastTime);// 分散账期修改
                            // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
                            discnt.put("DIVERSIFY_ACCT_TAG", "1");
                            dataset.add(discnt);
                        }
                    }
                }
        	}
        }
    }

    /**
     * 生成Other表数据
     * 
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        String memUserId = reqData.getUca().getUserId();
        String serialNumber = reqData.getGrpUca().getSerialNumber();// 获取集团产品编码
        boolean isVpmnJpaDel = false;

        IDataset discnts = reqData.cd.getDiscnt();
        if (IDataUtil.isNotEmpty(discnts))
        {
            for (int i = 0; i < discnts.size(); i++)
            {
                IData discnt = (IData) discnts.get(i);
                String element_id = discnt.getString("ELEMENT_ID", "");
                String state = discnt.getString("MODIFY_TAG", "");
                if (TRADE_MODIFY_TAG.DEL.getValue().equals(state) && "655".equals(element_id))
                {
                    IData info = new DataMap();
                    info.put("USER_ID", memUserId);
                    info.put("RSRV_VALUE_CODE", "CHNL");
                    IDataset userOther = UserOtherInfoQry.getUserOther(memUserId, "CHNL");
                    if (IDataUtil.isNotEmpty(userOther))
                    {
                        for (int j = 0; j < userOther.size(); j++)
                        {
                        	isVpmnJpaDel = true;
                            IData data = (IData) userOther.get(j);
                            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            data.put("END_DATE", currentMonthLastTime);// 分散账期修改
                            dataset.add(data);
                        }
                    }
                }
            }
        }

        if (serialNumber.equals("V0HN001010") || serialNumber.equals("V0SJ004001"))
        {
            IData param = new DataMap();
            param.put("USER_ID", memUserId);
            param.put("RSRV_VALUE_CODE", "30");
            IDataset userOther = UserOtherInfoQry.getUserOther(memUserId, "30");

            if (IDataUtil.isNotEmpty(userOther))
            {
                for (int j = 0; j < userOther.size(); j++)
                {
                    IData data = (IData) userOther.get(j);
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    data.put("END_DATE", this.getAcceptTime());
                    dataset.add(data);
                }
            }
        }
        
        /*如果不是：退655代理商套餐(VPMN JPA)时，需对'1401','1402','1403','4807'套餐进行退订，
         * 则判断是否退出V0SJ004535集团，也需要对'1401','1402','1403','4807'套餐进行退订
         * */
        if(!isVpmnJpaDel){
        	if("V0SJ004535".equals(serialNumber) || "V0SJ004536".equals(serialNumber)){
        		IData info = new DataMap();
                info.put("USER_ID", memUserId);
                info.put("RSRV_VALUE_CODE", "CHNL");
                IDataset userOther = UserOtherInfoQry.getUserOther(memUserId, "CHNL");
                if (IDataUtil.isNotEmpty(userOther))
                {
                    for (int j = 0; j < userOther.size(); j++)
                    {
                        IData data = (IData) userOther.get(j);
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        data.put("END_DATE", currentMonthLastTime);// 分散账期修改
                        dataset.add(data);
                    }
                }
        	}
        }
        
        super.addTradeOther(dataset);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (DestroyVpnGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setJION_IN(map.getString("JOIN_IN"));
        reqData.setNeedSms(map.getBoolean("IF_SMS", true));
    }

    /**
     * 判断集团关键人的操作是否属于分管客户经理
     * 
     * @throws Exception
     * @author lixiuyu@20100505
     */
    public void validchkCustManagerId() throws Exception
    {
        String staffId = CSBizBean.getVisit().getStaffId();
        String memUserId = reqData.getUca().getUserId();
        String memEparchCode = reqData.getUca().getUserEparchyCode();
        IDataset mebInfos = GrpMebInfoQry.getGroupInfoByMember(memUserId, memEparchCode, null); // TF_F_CUST_GROUPMEMBER查出集团成员的客户经理
        if (IDataUtil.isNotEmpty(mebInfos))
        {
            IData mebInfo = (IData) mebInfos.get(0);
            String memberKind = mebInfo.getString("MEMBER_KIND", ""); // 成员类型：见参数表TD_S_STATIC/GMB_MEMBERKIND
            String custManagerId = mebInfo.getString("CUST_MANAGER_ID", "");
            if ("1".equals(memberKind) || "2".equals(memberKind)) // 1-联系人,2-集团领导人,0-一般成员
            {
                if (!custManagerId.equals(staffId))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_673);
                }
            }
        }
    }

    /**
     * 验证用户的主套餐是否修改
     * 
     * @throws Exception
     * @author lixiuyu@20101103
     */
    public void validchkProductChange() throws Exception
    {
        String productId = reqData.getUca().getProductId(); // j2ee 这里要取成员的主产品

        IDataset userInfos = UserProductInfoQry.queryUserMainProduct(reqData.getUca().getUserId());

        if (IDataUtil.isNotEmpty(userInfos))
        {
            String thisProductId = "";
            String sysDate = SysDateMgr.getSysTime();
            int size = userInfos.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userInfos.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                    thisProductId = userProduct.getString("PRODUCT_ID");
                }
            }
            if (!thisProductId.equals(productId))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_217);
            }
        }
    }
    
    
}
