
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctDiscntQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

/**
 * @description 多媒体桌面电话集团产品变更Bean类
 * @author yish
 */
public class ChangeDesktopTelUserElement extends ChangeUserElement
{
    private static transient Logger logger = Logger.getLogger(ChangeDesktopTelUserElement.class);

    public ChangeDesktopTelUserElement()
    {

    }

    /**
     * @description 业务执行前处理
     * @author yish
     */
    public void actTradeBefore() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入ChangeDesktopTelUserElement类 actTradeBefore()>>>>>>>>>>>>>>>>>>");

        super.actTradeBefore();

        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        if (IDataUtil.isNotEmpty(paramData))
        {
            paramData.remove("VPN_NO");
        }

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChangeDesktopTelUserElement类 actTradeBefore() <<<<<<<<<<<<<<<<<<<");
    }

    /**
     * @description 子类执行的动作
     * @author yish
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegOtherData();
        decodeAcctDiscnt();
        this.specDealDiscntStartDate();
    }

    /**
     * @description 处理台账Other子表的数据
     * @author yish
     */
    public void infoRegOtherData() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入ChangeDesktopTelUserElement类 infoRegOtherData()>>>>>>>>>>>>>>>>>>");

        IDataset managerDataset = new DatasetList();

        // 发送创建集团指令
        IData centreData = new DataMap();
        centreData.put("USER_ID", reqData.getUca().getUserId());
        centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        centreData.put("RSRV_VALUE", "多媒体桌面电话变更集团");
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", "817"); // 服务id
        centreData.put("OPER_CODE", "08"); // 操作类型 08 修改
        centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        centreData.put("START_DATE", getAcceptTime());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        managerDataset.add(centreData);

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 修改操作   数据插入other表用于向centrex平台发修改集团信息报文>>>>>>>>>>>>>>>>>>");
        // 发送集团配置业务指令
        IData data = new DataMap();
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        data.put("RSRV_VALUE", "多媒体桌面电话集团配置业务");
        data.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        data.put("RSRV_STR9", "817"); // 服务id
        data.put("OPER_CODE", "03"); // 操作类型 03 配置集团
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        data.put("START_DATE", getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        data.put("INST_ID", SeqMgr.getInstId());
        managerDataset.add(data);

        addTradeOther(managerDataset);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChangeDesktopTelUserElement类 infoRegOtherData()<<<<<<<<<<<<<<<<<<<");
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
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
    		String userId = reqData.getUca().getUserId();
    		IData payRelas = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
    		
    		if(IDataUtil.isEmpty(payRelas))
    		{
    			String errMessage = "未获取到该集团的默认账户！";
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
    		}
    		
    		defaultAcctId = payRelas.getString("ACCT_ID","");
    		
    		IDataset paramList = new DatasetList();
    		
    		for (int i = 0; i < tradeDiscnts.size(); i++)
	        {
    			IData tradeDiscnt = tradeDiscnts.getData(i);
    			String discntCode = tradeDiscnt.getString("DISCNT_CODE","");
    			String modifyTag = tradeDiscnt.getString("MODIFY_TAG","");
    			String startDate = tradeDiscnt.getString("START_DATE","");
    			String endDate = tradeDiscnt.getString("END_DATE","");
    			String instId = tradeDiscnt.getString("INST_ID","");
    			
    			//新增的优惠
    			if("0".equals(modifyTag))
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
    			else if("2".equals(modifyTag))
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
    				
    				IDataset acctDisInfos = AcctDiscntQry.getAcctDisInfoByAcctIdCode(defaultAcctId, discntCode); 
    				if(IDataUtil.isNotEmpty(acctDisInfos))
    				{
    					IData param = acctDisInfos.getData(0);
        		        param.put("START_DATE", startDate);
        		        param.put("END_DATE", endDate);
        		        param.put("INST_ID", instId);
        		        param.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()); //修改
        		        param.put("UPDATE_TIME", reqData.getAcceptTime()); //修改时间
        		        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        		        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        		        paramList.add(param);
    				}
    			}
    			else if("1".equals(modifyTag))
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
    				
    				IDataset acctDisInfos = AcctDiscntQry.getAcctDisInfoByAcctIdCode(defaultAcctId, discntCode); 
    				if(IDataUtil.isNotEmpty(acctDisInfos))
    				{
    					IData param = acctDisInfos.getData(0);
        		        param.put("START_DATE", startDate);
        		        param.put("END_DATE", endDate);
        		        param.put("INST_ID", instId);
        		        param.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); //删除
        		        param.put("UPDATE_TIME", reqData.getAcceptTime()); //修改时间
        		        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        		        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        		        paramList.add(param);
    				}
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
