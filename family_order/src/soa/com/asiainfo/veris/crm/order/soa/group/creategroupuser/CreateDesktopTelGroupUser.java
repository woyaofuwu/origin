
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateDesktopTelGroupUserReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

/**
 * @description 多媒体桌面电话集团产品受理Bean类
 * @author yish
 */
public class CreateDesktopTelGroupUser extends CreateGroupUser
{
    private static transient Logger logger = Logger.getLogger(CreateDesktopTelGroupUser.class);

    boolean flattag = true;

    protected CreateDesktopTelGroupUserReqData reqData = null;

    /**
     * @description 业务执行前处理
     * @author yish
     */
    public void actTradeBefore() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入CreateDesktopTelGroupUser类 actTradeBefore()>>>>>>>>>>>>>>>>>>");

        super.actTradeBefore();

        IData paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            String vpn_no = paramData.getString("VPN_NO", "");
            String serial_number = reqData.getUca().getSerialNumber();
            if (StringUtils.isNotBlank(vpn_no))
            {
                reqData.setVpnNo(vpn_no);
                flattag = false;
            }
            else
            {
                reqData.setVpnNo(serial_number);
                flattag = true;
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CreateDesktopTelGroupUser类 actTradeBefore() 设置的VPN_NO=" + reqData.getVpnNo() + " 标识flattag=" + flattag + "<<<<<<<<<<<<<<<<<<<");
    }

    /**
     * @description 子类执行的动作
     * @author yish
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVPMNVpn();
        infoRegOtherData();
        
        decodeAcctDiscnt();
        this.specDealDiscntStartDate();
    }

    /**
     * @description 得到多媒体桌面电话产品参数
     * @author yish
     * @return
     * @throws Exception
     */
    private IData getParamData() throws Exception
    {
        String curProductId = reqData.getUca().getProductId();
        // 多媒体桌面电话产品参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  执行CreateDesktopTelGroupUser类 getParamData() 得到产品页面传过来的参数<<<<<<<<<<<<<<<<<<<");
        return paramData;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateDesktopTelGroupUserReqData();
    }

    /**
     * @description 处理台帐VPN子表的数据-用户
     */
    public void infoRegDataVPMNVpn() throws Exception
    {
        IData vpnData = super.infoRegDataVpn();

        // VPN数据
        vpnData.put("SCP_CODE", "10");// 湖南固定为00 海南固定为10  否则无法被叫
        vpnData.put("MAX_USERS", "50000"); // 集团最大用户数-初始值50000
        vpnData.put("VPMN_TYPE", "0");// VPN集团类型

        vpnData.put("VPN_USER_CODE", "2");
        vpnData.put("SHORT_CODE_LEN", "6");
        vpnData.put("VPN_NO", reqData.getVpnNo()); // 集团VPN编号
        vpnData.put("RSRV_STR1", reqData.getUca().getSerialNumber());

        this.addTradeVpn(vpnData);
    }

    /**
     * @description 处理台账Other子表的数据
     */
    public void infoRegOtherData() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入CreateDesktopTelGroupUser类 infoRegOtherData()>>>>>>>>>>>>>>>>>>");

        IDataset managerDataset = new DatasetList();

        // 发送创建集团指令
        IData centreData = new DataMap();
        centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", "817"); // 服务id

        if (flattag)
        {
            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            centreData.put("OPER_CODE", "01"); // 操作类型 01 注册
            centreData.put("RSRV_VALUE", "多媒体桌面电话创建集团");
            if (logger.isDebugEnabled())
                logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 新增操作   数据插入other表用于向centrex平台发创建集团报文>>>>>>>>>>>>>>>>>>");
        }
        else
        {
            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            centreData.put("OPER_CODE", "08"); // 操作类型 08 修改
            centreData.put("RSRV_VALUE", "多媒体桌面电话修改集团");
            if (logger.isDebugEnabled())
                logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 修改操作   数据插入other表用于向centrex平台发修改集团信息报文>>>>>>>>>>>>>>>>>>");
        }
        centreData.put("START_DATE", getAcceptTime());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        managerDataset.add(centreData);

        // 发送集团配置业务指令
        IData data = new DataMap();
        data.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        data.put("RSRV_VALUE", "多媒体桌面电话集团配置业务");
        data.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

        data.put("RSRV_STR9", "817"); // 服务id
        data.put("OPER_CODE", "03"); // 操作类型 03 配置集团
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        data.put("START_DATE", getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        data.put("INST_ID", SeqMgr.getInstId());
        managerDataset.add(data);

        addTradeOther(managerDataset);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CreateDesktopTelGroupUser类 infoRegOtherData()<<<<<<<<<<<<<<<<<<<");
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateDesktopTelGroupUserReqData) getBaseReqData();
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR10", reqData.getVpnNo());
    }

    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR10", reqData.getVpnNo());
    }
    
    /**
     * 
     * @throws Exception
     */
    private void  decodeAcctDiscnt() throws Exception
    {
    	IDataset tradeDiscnts = bizData.getTradeDiscnt();
    	if(IDataUtil.isNotEmpty(tradeDiscnts))
    	{
    		String defaultAcctId = "";
    		//获取集团用户的默认账户
    		IDataset tradePayRelation = bizData.getTradePayrelation();
    		
    		if(IDataUtil.isNotEmpty(tradePayRelation))
    		{
    			for (int i = 0; i < tradePayRelation.size(); i++)
    	        {
    				IData payRelation = tradePayRelation.getData(i);
    				if(IDataUtil.isNotEmpty(payRelation)){
    					String actTag = payRelation.getString("ACT_TAG","");
    					String defaultTag = payRelation.getString("DEFAULT_TAG","");
    					String acctId = payRelation.getString("ACCT_ID","");
    					if("1".equals(actTag) 
    							&& "1".equals(defaultTag) 
    							&& StringUtils.isNotBlank(acctId))
    					{
    						defaultAcctId = acctId;
    						break;
    					}
    				}
    	        }
    		}
    		
    		if(StringUtils.isBlank(defaultAcctId))
    		{
    			String errMessage = "获取该集团的默认账户出错！";
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
    		}
    		
    		IDataset paramList = new DatasetList();
    		
    		for (int i = 0; i < tradeDiscnts.size(); i++)
	        {
    			IData tradeDiscnt = tradeDiscnts.getData(i);
    			String discntCode = tradeDiscnt.getString("DISCNT_CODE","");
    			String startDate = tradeDiscnt.getString("START_DATE","");
    			String endDate = tradeDiscnt.getString("END_DATE","");
    			String instId = tradeDiscnt.getString("INST_ID","");
    			if(StringUtils.isNotBlank(discntCode))
    			{
    				
    				IDataset paramDs = CommparaInfoQry.getCommPkInfo("CGM", "7357",discntCode, "0898");
    				if(IDataUtil.isNotEmpty(paramDs))
    				{
    					String paraCode1 = paramDs.getData(0).getString("PARA_CODE1","");
    					if(StringUtils.isNotBlank(paraCode1))
    					{
    						discntCode = paraCode1;
    					}
    				}
    				
    				IData param = new DataMap();
    		        param.put("ACCT_ID", defaultAcctId);
    		        param.put("DISCNT_CODE", discntCode);
    		        param.put("START_DATE", startDate);
    		        param.put("END_DATE", endDate);
    		        param.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 新增
    		        param.put("INST_ID", instId);
    		        param.put("UPDATE_TIME", reqData.getAcceptTime()); //受理时间
    		        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    		        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    		        paramList.add(param);
    			}
	        }
    		
    		super.addTradeAcctDiscnt(paramList);
    	}
    }
    
    /**
     * REQ201807090024关于将IMS套餐立即生效的时间进行修改的需求
     * 特殊处理自定义套餐及海岛畅聊包优惠的开始时间
     * 25号前办理则立即生效
     * 25号及之后办理则下月1号生效
     * @throws Exception
     * @author chenzg
     * @date 2018-7-19
     */
    private void specDealDiscntStartDate() throws Exception{
    	IDataset tradeDiscnts = this.bizData.getTradeDiscnt();
    	IDataset tradeAttrs = this.bizData.getTradeAttr();
    	int cDay = BizEnv.getEnvInt("grp.discnt.cday", 25);		//集团业务IMS套餐生效时间分界值
    	if(IDataUtil.isNotEmpty(tradeDiscnts)){
    		for(int i=0;i<tradeDiscnts.size();i++){
    			IData each = tradeDiscnts.getData(i);
    			String packageId = each.getString("PACKAGE_ID", "");
    			String modifyTag = each.getString("MODIFY_TAG", "");
    			String discntCode = each.getString("DISCNT_CODE", "");
    			String startDate = each.getString("START_DATE", "");
    			if("0".equals(modifyTag) && "800109".equals(discntCode)){
    				String curDay = SysDateMgr.getCurDay();
    				if(Integer.valueOf(curDay) >= cDay){
    					startDate = SysDateMgr.getFirstDayOfNextMonth();
        				each.put("START_DATE", startDate);
        				//同时修改优惠属性的开始时间
        				if(IDataUtil.isNotEmpty(tradeAttrs)){
    						IDataset attrs = DataHelper.filter(tradeAttrs, "ELEMENT_ID="+discntCode);
    						if(IDataUtil.isNotEmpty(attrs)){
    							for(int j=0;j<attrs.size();j++){
    								attrs.getData(j).put("START_DATE", startDate);
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    }
}
