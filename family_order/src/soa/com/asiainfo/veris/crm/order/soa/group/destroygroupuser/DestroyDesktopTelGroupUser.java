
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctDiscntQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

/**
 * @description 多媒体桌面电话集团产品注销Bean类
 * @author yish
 */
public class DestroyDesktopTelGroupUser extends DestroyGroupUser
{
    private static transient Logger logger = Logger.getLogger(DestroyDesktopTelGroupUser.class);

    public DestroyDesktopTelGroupUser()
    {

    }

    /**
     * @description 子类执行的动作
     * @author yish
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataVpn();
        infoRegOtherData();
        
        decodeAcctDiscnt();
    }

    /**
     * @description 处理台账Other子表的数据
     * @author yish
     */
    public void infoRegOtherData() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入DestroyDesktopTelGroupUser类 infoRegOtherData()>>>>>>>>>>>>>>>>>>");

        String userId = reqData.getUca().getUserId();
        String custId = reqData.getUca().getCustId();
        String inst_id = SeqMgr.getInstId();
        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();

        IDataset uservpninfos = UserVpnInfoQry.getUserVPNInfoByCstId(custId, null);
        IDataset uservpninfo = DataHelper.filter(uservpninfos, "USER_ID=" + userId);
        // 如果只有一条记录并且为此集团USER_ID则删除集团指令,其它则发退订哪个业务
        if (uservpninfos.size() == 1 && uservpninfo.size() == 1)
        {
            centreData.put("USER_ID", userId);
            centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData.put("RSRV_VALUE", "删除集团");
            centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

            centreData.put("RSRV_STR9", "817"); // 服务id
            centreData.put("OPER_CODE", "02"); // 操作类型 02 注销
            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            centreData.put("START_DATE", getAcceptTime());
            centreData.put("END_DATE", getAcceptTime()); // 立即截止
            centreData.put("INST_ID", inst_id);
            dataset.add(centreData);

            if (logger.isDebugEnabled())
                logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 注销操作  数据插入other表用于向centrex平台发删除集团报文>>>>>>>>>>>>>>>>>>");
        }
        /*
         * else 注：集团退订多媒体桌面电话业务规范上有，但无意义且东信北邮未实现，所以不发 { centreData.put("USER_ID", userId);
         * centreData.put("RSRV_VALUE_CODE", "CNTRX"); // domain域 centreData.put("RSRV_VALUE", "Centrex集团多媒体桌面电话业务退订");
         * centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID centreData.put("RSRV_STR9", "6300"); //
         * 服务id centreData.put("OPER_CODE", "07"); // 操作类型 07 退订 centreData.put("RSRV_STR21", "MMDesktopBG");
         * //标识退订的集团业务 MMDesktopBG 多媒体桌面电话 centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
         * centreData.put("START_DATE", getAcceptTime()); centreData.put("END_DATE", getAcceptTime()); // 立即截止
         * centreData.put("INST_ID", inst_id); dataset.add(centreData); if (logger.isDebugEnabled())
         * logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 退订操作  数据插入other表用于向centrex平台发退订集团业务报文>>>>>>>>>>>>>>>>>>"); }
         */
        addTradeOther(dataset);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出DestroyDesktopTelGroupUser类 infoRegOtherData()<<<<<<<<<<<<<<<<<<<");
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
    			
    			if("1".equals(modifyTag))
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
}
