
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.IotInstanceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PcrfTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot.InstancePfQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot.IotQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.IotConstants;

/**
 * 生成物联网所需要的INST_ID 物联网的INST_ID生成规则 1.操作流水号规则
 * 4位机构编码+8位业务编码（BIPCode）+14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长），序号从000001开始，增量步长为1 2.产品实例规则
 * 产品实例ID编码规则：9+三位省编码+14位流水（定长），序号从00000000000001开始，增量步长为1，由省保证流水号省内唯一
 * 3.用户ID编码规则：9+三位省编码+14位流水（定长），序号从00000000000001开始，增量步长为1，由省保证流水号省内唯一。
 * 
 * @author xiekl
 */
public class GeneIotInstIdAction implements ITradeAction
{
	private static final Logger logger = Logger.getLogger(GeneIotInstIdAction.class);
	protected static Map<String, IData> DISCNT_CONFIG_MAP = new HashMap<String, IData>();

	protected static Map<String, IData> SVC_CONFIG_MAP = new HashMap<String, IData>();

	protected static Map<String, IData> PRODUCT_CONFIG_MAP = new HashMap<String, IData>();

    static
    {
        try
        {
            PRODUCT_CONFIG_MAP = loadConfig("9015");
            SVC_CONFIG_MAP = loadConfig("9014");
            DISCNT_CONFIG_MAP = loadConfig("9013");
        }
        catch (Exception e)
        {
        	logger.error(e);
        }

    }

    public static Map<String, IData> loadConfig(String paramAttr) throws Exception
    {
        Map<String, IData> configMap = new HashMap<String, IData>();
        IDataset configList = CommparaInfoQry.getCommByParaAttr("CSM", paramAttr, "0898");
        for (int i = 0; i < configList.size(); i++)
        {
            IData config = configList.getData(i);
            configMap.put(config.getString("PARAM_CODE"), config);
        }

        return configMap;
    }

    public void executeAction(BusiTradeData btd) throws Exception
    {
        BaseReqData reqData = btd.getRD();
        UcaData uca = reqData.getUca();
        String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
        String serialNumber = uca.getSerialNumber();

        if (!"PWLW".equals(uca.getBrandCode()))
        {
            return;
        }

        if (IotConstants.ALLOW_TRADE_TYPE_CODE.indexOf(tradeTypeCode) > 0 && !"10".equals(tradeTypeCode) && !"500".equals(tradeTypeCode) && !"192".equals(tradeTypeCode))
        {
            stringIotService(serialNumber, btd);
        }

        // 开户,物联网批量开户插入TF_F_INSTANCE_PF表逻辑，已经前移到trade层去处理。
//        if ("10".equals(tradeTypeCode) || "500".equals(tradeTypeCode))
//        {
//            geneUserInstance(serialNumber, btd);
//            geneProductInstance(serialNumber, btd);
//            geneElementInstance(serialNumber, btd);
//            genePkgInstance(serialNumber, btd);
//        }
		if ("500".equals(tradeTypeCode))
		{
			geneUserInstance(serialNumber, btd);
			geneProductInstance(serialNumber, btd);
			geneElementInstance(serialNumber, btd);
			genePkgInstance(serialNumber, btd);
		}
        else if ("110".equals(tradeTypeCode) || "268".equals(tradeTypeCode) || "277".equals(tradeTypeCode))// 产品变更
        {
            geneProductInstance(serialNumber, btd);
            geneElementInstance(serialNumber, btd);
            genePkgInstance(serialNumber, btd);

            dealIotVoiceSvcAttr(serialNumber, btd);//反向退订语音服务，其属性OCSI和TCSI需同时关闭,属性值修改成02
        }
        else if ("272".equals(tradeTypeCode))// 物联网服务恢复
        {
            geneProductInstance(serialNumber, btd);
            geneElementInstance(serialNumber, btd);
        }
        else if (StringUtils.equals("192", tradeTypeCode) || StringUtils.equals("7240", tradeTypeCode))// 销户处理
        {
            //removeUser(serialNumber, btd);
        	geneProductInstance(serialNumber, btd);
            geneElementInstance(serialNumber, btd);
            genePkgInstance(serialNumber, btd);
        }
		else if("279".equals(tradeTypeCode)||"280".equals(tradeTypeCode))// 物联网PCRF策略变更
        {
        	genePcrfInstance(serialNumber, btd);
        }

        //物联网反向接口保存操作流水
        dealIotReverseSeq(btd);
    }

    public void geneElementInstance(String serialNumber, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        List<DiscntTradeData> discntList = uca.getUserDiscnts();
        for (int i = 0; i < discntList.size(); i++)
        {
            DiscntTradeData discntTrade = discntList.get(i);
            if (DISCNT_CONFIG_MAP.get(discntTrade.getElementId()) != null)
            {
                IData discntConfig = (IData) DISCNT_CONFIG_MAP.get(discntTrade.getElementId());
                if (discntConfig.getString("PARAM_CODE").equals(discntTrade.getElementId()) && BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag()))
                {
                	String strStartDate = SysDateMgr.decodeTimestamp(discntTrade.getStartDate(), "yyyyMMddHHmmss");
                	String strEndDate = SysDateMgr.decodeTimestamp(discntTrade.getEndDate(), "yyyyMMddHHmmss");
                	
                    IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
                    tradeData.setCreateTime(SysDateMgr.getSysTime());
                    tradeData.setInstId(discntTrade.getInstId());
                    tradeData.setInstType(IotInstanceTradeData.INST_TYPE_SVC);
                    tradeData.setTradeId(btd.getTradeId());
                    tradeData.setPlatCode("PBSS");
                    tradeData.setProdInstId(SeqMgr.getPbssBizProdInstId());
                    tradeData.setRemark("");
                    tradeData.setRsrvStr1(discntTrade.getElementId());
                    tradeData.setRsrvStr2(discntTrade.getElementType());
                    tradeData.setRsrvStr3(strStartDate);
                    tradeData.setRsrvStr4(strEndDate);
                    tradeData.setRsrvStr5(discntConfig.getString("PARA_CODE1", ""));
                    tradeData.setRsrvStr6(discntConfig.getString("PARA_CODE2", ""));
                    tradeData.setSubsId("");
                    tradeData.setUserId(uca.getUserId());
                    Dao.insert(tradeData.getTableName(), tradeData.toData(), CSBizBean.getTradeEparchyCode());
                }
                else if(discntConfig.getString("PARAM_CODE").equals(discntTrade.getElementId()) && BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag()))
                {
                	String strEndDate = SysDateMgr.decodeTimestamp(discntTrade.getEndDate(), "yyyyMMddHHmmss");
            		
            		IData param = new DataMap();
	                param.put("USER_ID", discntTrade.getUserId());
	                param.put("END_DATE", strEndDate);
	                param.put("INST_ID", discntTrade.getInstId());
	                param.put("INST_TYPE", "S");
	                StringBuilder sql = new StringBuilder();
	                sql.append("UPDATE TF_F_INSTANCE_PF T ");
	                sql.append("   SET T.RSRV_STR4 = :END_DATE ");
	                sql.append(" WHERE T.INST_ID = :INST_ID ");
	                sql.append("   AND T.USER_ID = :USER_ID ");
	                sql.append("   AND T.INST_TYPE = :INST_TYPE ");
	                Dao.executeUpdate(sql, param);
                }
            }
        }

        List<SvcTradeData> svcList = btd.getRD().getUca().getUserSvcs();
        for (int i = 0; i < svcList.size(); i++)
        {
            SvcTradeData svcTrade = svcList.get(i);
            if (SVC_CONFIG_MAP.get(svcTrade.getElementId()) != null)
            {
                IData svcConfig = (IData) SVC_CONFIG_MAP.get(svcTrade.getElementId());
                if (svcConfig.getString("PARAM_CODE").equals(svcTrade.getElementId()) && BofConst.MODIFY_TAG_ADD.equals(svcTrade.getModifyTag()))
                {
                	String strStartDate = SysDateMgr.decodeTimestamp(svcTrade.getStartDate(), "yyyyMMddHHmmss");
                	String strEndDate = SysDateMgr.decodeTimestamp(svcTrade.getEndDate(), "yyyyMMddHHmmss");
                	
                    IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
                    tradeData.setCreateTime(SysDateMgr.getSysTime());
                    tradeData.setInstId(svcTrade.getInstId());
                    tradeData.setInstType(IotInstanceTradeData.INST_TYPE_SVC);
                    tradeData.setTradeId(btd.getTradeId());
                    tradeData.setPlatCode("PBSS");
                    tradeData.setProdInstId(SeqMgr.getPbssBizProdInstId());
                    tradeData.setRemark("");
                    tradeData.setRsrvStr1(svcTrade.getElementId());
                    tradeData.setRsrvStr2(svcTrade.getElementType());
                    tradeData.setRsrvStr3(strStartDate);
                    tradeData.setRsrvStr4(strEndDate);
                    tradeData.setRsrvStr5(svcConfig.getString("PARA_CODE1", ""));
                    tradeData.setRsrvStr6(svcConfig.getString("PARA_CODE2", ""));
                    tradeData.setSubsId("");
                    tradeData.setUserId(uca.getUserId());
                    Dao.insert(tradeData.getTableName(), tradeData.toData(), CSBizBean.getTradeEparchyCode());
                }
                else if(svcConfig.getString("PARAM_CODE").equals(svcTrade.getElementId()) && BofConst.MODIFY_TAG_DEL.equals(svcTrade.getModifyTag()))
                {
                	String strEndDate = SysDateMgr.decodeTimestamp(svcTrade.getEndDate(), "yyyyMMddHHmmss");
            		
            		IData param = new DataMap();
	                param.put("USER_ID", svcTrade.getUserId());
	                param.put("END_DATE", strEndDate);
	                param.put("INST_ID", svcTrade.getInstId());
	                param.put("INST_TYPE", "S");
	                StringBuilder sql = new StringBuilder();
	                sql.append("UPDATE TF_F_INSTANCE_PF T ");
	                sql.append("   SET T.RSRV_STR4 = :END_DATE ");
	                sql.append(" WHERE T.INST_ID = :INST_ID ");
	                sql.append("   AND T.USER_ID = :USER_ID ");
	                sql.append("   AND T.INST_TYPE = :INST_TYPE ");
	                Dao.executeUpdate(sql, param);
                }
            }
        }
    }

    /**
     * 生成包实例 物联网发报文需要发包实例的报文 如果该包订购过，用原包实例ID
     * 
     * @param serialNumber
     * @param btd
     * @throws Exception
     */
    public void genePkgInstance(String serialNumber, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        String strUserID = btd.getMainTradeData().getUserId();
        IData idAllPkgInst = new DataMap();    //已有的包与包实例对应关系
        
        List<SvcTradeData> svcList = uca.getUserSvcs();
        if(CollectionUtils.isNotEmpty(svcList))
		{
        	for (int i = 0; i < svcList.size(); i++)
	        {
        		SvcTradeData svc = svcList.get(i);
                String strUInstId = svc.getInstId();
                String strUElementId = svc.getElementId();
                String strUModifyTag = svc.getModifyTag();
                String strUElementType = svc.getElementType();
                String strUStartDate = svc.getStartDate();
                String strUEndDate = svc.getEndDate();
                
                if (BofConst.MODIFY_TAG_ADD.equals(strUModifyTag))
                {
                    continue;
                }
                
                IData IDUSvcConfig = SVC_CONFIG_MAP.get(strUElementId);
                if (IDataUtil.isEmpty(IDUSvcConfig))
                {
                    continue;
                }
                
                String strUProId = IDUSvcConfig.getString("PARA_CODE1");
                String strUPkgId = IDUSvcConfig.getString("PARA_CODE2");
                
                if(!BofConst.MODIFY_TAG_ADD.equals(strUModifyTag))
                {
                	IData e = idAllPkgInst.getData(strUPkgId);
            		if(IDataUtil.isEmpty(e))
            		{
            			//表中存在的，不用重新生成
            			IData idParentSvcPkg = InstancePfQuery.queryInstanceByInstId(strUserID, strUInstId, "P", CSBizBean.getTradeEparchyCode());
        	            if(IDataUtil.isNotEmpty(idParentSvcPkg))
                    	{
        	            	if(BofConst.MODIFY_TAG_DEL.equals(strUModifyTag))
        	                {
        	            		String strEndDate = SysDateMgr.decodeTimestamp(strUEndDate, "yyyyMMddHHmmss");
        	            		
        	            		IData param = new DataMap();
        		                param.put("USER_ID", strUserID);
        		                param.put("END_DATE", strEndDate);
        		                param.put("INST_ID", strUInstId);
        		                param.put("INST_TYPE", "P");
        		                StringBuilder sql = new StringBuilder();
        		                sql.append("UPDATE TF_F_INSTANCE_PF T ");
        		                sql.append("   SET T.RSRV_STR4 = :END_DATE, ");
        		                sql.append("       T.CREATE_TIME = SYSDATE ");
        		                sql.append(" WHERE T.INST_ID = :INST_ID ");
        		                sql.append("   AND T.USER_ID = :USER_ID ");
        		                sql.append("   AND T.INST_TYPE = :INST_TYPE ");
        		                Dao.executeUpdate(sql, param);
        	                }
        	            	
        	            	String strProdInstID = idParentSvcPkg.getString("PROD_INST_ID");
        	            	
        	            	IData idUPkgInst = new DataMap();
        	            	idUPkgInst.put(strUPkgId, strUInstId);
        	            	idUPkgInst.put("PARA_CODE1", strUProId);
        	            	idUPkgInst.put("PARA_CODE2", strUPkgId);
        	            	idUPkgInst.put("INST_ID", strUInstId);
        	            	idUPkgInst.put("PACKAGE_ID", strProdInstID);
        	            	idUPkgInst.put("MODIFY_TAG", strUModifyTag);
        	            	idUPkgInst.put("ELEMENT_ID", strUElementId);
        	            	idUPkgInst.put("ELEMEMT_TYPE", strUElementType);
        	            	idUPkgInst.put("START_DATE", strUStartDate);
        	            	idUPkgInst.put("END_DATE", strUEndDate);
        	            	idAllPkgInst.put(strUPkgId, idUPkgInst);
                    	}
        	            else
        	            {
        	            	IData idChildrenSvcPkg = InstancePfQuery.queryInstanceRelByInstId(strUserID, strUInstId, "P", CSBizBean.getTradeEparchyCode());
        	            	if(IDataUtil.isNotEmpty(idChildrenSvcPkg))
                        	{
        	            		if(BofConst.MODIFY_TAG_DEL.equals(strUModifyTag))
            	                {
            	            		String strEndDate = SysDateMgr.decodeTimestamp(strUEndDate, "yyyyMMddHHmmss");
            	            		
            	            		IData param = new DataMap();
            		                param.put("USER_ID", strUserID);
            		                param.put("END_DATE", strEndDate);
            		                param.put("INST_ID", strUInstId);
            		                param.put("INST_TYPE", "P");
            		                StringBuilder sql = new StringBuilder();
            		                sql.append("UPDATE TF_F_INSTANCE_PF_REL T ");
            		                sql.append("   SET T.RSRV_STR4 = :END_DATE, ");
            		                sql.append("       T.CREATE_TIME = SYSDATE ");
            		                sql.append(" WHERE T.INST_ID = :INST_ID ");
            		                sql.append("   AND T.USER_ID = :USER_ID ");
            		                sql.append("   AND T.INST_TYPE = :INST_TYPE ");
            		                Dao.executeUpdate(sql, param);
            	                }
                        	}
        				}
            		}
                }
	        }
		}
        
        List<SvcTradeData> lsSvcTrade = btd.get("TF_B_TRADE_SVC");
		if(CollectionUtils.isNotEmpty(lsSvcTrade))
		{
			for (int j = 0; j < lsSvcTrade.size(); j++)
	        {
				SvcTradeData stSvc = lsSvcTrade.get(j);
				String strTInstId = stSvc.getInstId();
	            String strTElementId = stSvc.getElementId();
	            String strTModifyTag = stSvc.getModifyTag();
	            String strTElementType = stSvc.getElementType();
	            String strTStartDate = stSvc.getStartDate();
	            String strTEndDate = stSvc.getEndDate();
	            
	            if (!BofConst.MODIFY_TAG_ADD.equals(strTModifyTag))
	            {
	                continue;
	            }
	            
	            IData idTSvcConfig = SVC_CONFIG_MAP.get(strTElementId);
	            if (IDataUtil.isEmpty(idTSvcConfig))
	            {
	                continue;
	            }
	            
	            String strTProId = idTSvcConfig.getString("PARA_CODE1");
	            String strTPkgId = idTSvcConfig.getString("PARA_CODE2");
	            
	            IData e = idAllPkgInst.getData(strTPkgId);
        		if(IDataUtil.isEmpty(e))
        		{
        			String strStartDate = SysDateMgr.decodeTimestamp(strTStartDate, "yyyyMMddHHmmss");
        			String strEndDate = SysDateMgr.decodeTimestamp(strTEndDate, "yyyyMMddHHmmss");
        			String strProdInstId = SeqMgr.getPbssBizProdInstId();
					
					IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
		            tradeData.setCreateTime(SysDateMgr.getSysTime());
		            tradeData.setInstId(strTInstId);
		            tradeData.setInstType(IotInstanceTradeData.INST_TYPE_PKG);
		            tradeData.setTradeId(btd.getTradeId());
		            tradeData.setPlatCode("PBSS");
		            tradeData.setProdInstId(strProdInstId);
		            tradeData.setRemark("");
		            tradeData.setRsrvStr1(strTElementId);
		            tradeData.setRsrvStr2(strTElementType);
		            tradeData.setRsrvStr3(strStartDate);
		            tradeData.setRsrvStr4(strEndDate);
		            tradeData.setRsrvStr5(strTProId);
		            tradeData.setRsrvStr6(strTPkgId);
		            tradeData.setSubsId("");
		            tradeData.setUserId(strUserID);
		            Dao.insert(tradeData.getTableName(), tradeData.toData(), CSBizBean.getTradeEparchyCode());
		            
		            
	            	IData idUPkgInst = new DataMap();
	            	idUPkgInst.put(strTPkgId, strProdInstId);
	            	idUPkgInst.put("PARA_CODE1", strTProId);
	            	idUPkgInst.put("PARA_CODE2", strTPkgId);
	            	idUPkgInst.put("INST_ID", strTInstId);
	            	idUPkgInst.put("PACKAGE_ID", strProdInstId);
	            	idUPkgInst.put("MODIFY_TAG", strTModifyTag);
	            	idUPkgInst.put("ELEMENT_ID", strTElementId);
	            	idUPkgInst.put("ELEMEMT_TYPE", strTElementType);
	            	idUPkgInst.put("START_DATE", strTStartDate);
	            	idUPkgInst.put("END_DATE", strTEndDate);
	            	idAllPkgInst.put(strTPkgId, idUPkgInst);
        		}
        		else 
        		{
        			String strUProId = e.getString("PARA_CODE1");
        			String strUPkgId = e.getString("PARA_CODE2");
        			String strUElementId = e.getString("ELEMENT_ID");
        			String strUModifyTag = e.getString("MODIFY_TAG");
        			String strUElementType = e.getString("ELEMEMT_TYPE");
        			String strUInstId = e.getString("INST_ID");
        			String strUProdInstID = e.getString("PACKAGE_ID");
        			String strUStartDate = e.getString("START_DATE");
        			String strUEndDate = e.getString("END_DATE");
        			String strStartDate = SysDateMgr.decodeTimestamp(strTStartDate, "yyyyMMddHHmmss");
        			String strEndDate = SysDateMgr.decodeTimestamp(strTEndDate, "yyyyMMddHHmmss");
        			if(strTEndDate.compareTo(strUEndDate) > 0 && BofConst.MODIFY_TAG_DEL.equals(strUModifyTag))
        			{
                		IData param = new DataMap();
                		param.put("PARA_CODE1", strTProId);
    	                param.put("PARA_CODE2", strTPkgId);
                		param.put("T_INST_ID", strTInstId);
    	                param.put("USER_ID", strUserID);
    	                param.put("ELEMENT_ID", strTElementId);
    	                param.put("ELEMEMT_TYPE", strTElementType);
    	                param.put("END_DATE", strEndDate);
    	                param.put("INST_ID", strUInstId);
    	                param.put("INST_TYPE", "P");
    	                StringBuilder sql = new StringBuilder();
    	                sql.append("UPDATE TF_F_INSTANCE_PF T ");
    	                sql.append("   SET T.RSRV_STR1 = :ELEMENT_ID, ");
    	                sql.append("       T.RSRV_STR2 = :ELEMEMT_TYPE, ");
    	                sql.append("       T.RSRV_STR4 = :END_DATE, ");
    	                sql.append("       T.RSRV_STR5 = :PARA_CODE1, ");
    	                sql.append("       T.RSRV_STR6 = :PARA_CODE2, ");
    	                sql.append("       T.INST_ID   = :T_INST_ID, ");
    	                sql.append("       T.CREATE_TIME = SYSDATE ");
    	                sql.append(" WHERE T.INST_ID = :INST_ID ");
    	                sql.append("   AND T.USER_ID = :USER_ID ");
    	                sql.append("   AND T.INST_TYPE = :INST_TYPE ");
    	                Dao.executeUpdate(sql, param);

    	            	e.put("PARA_CODE1", strTProId);
    	            	//e.put("PARA_CODE2", strTPkgId);
    	            	e.put("INST_ID", strTInstId);
    	            	e.put("MODIFY_TAG", strTModifyTag);
    	            	e.put("ELEMENT_ID", strTElementId);
    	            	e.put("ELEMEMT_TYPE", strTElementType);
    	            	e.put("END_DATE", strTEndDate);
    	            	
    	            	strStartDate = SysDateMgr.decodeTimestamp(strUStartDate, "yyyyMMddHHmmss");
            			strEndDate = SysDateMgr.decodeTimestamp(strUEndDate, "yyyyMMddHHmmss");
    	            	IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
    		            tradeData.setCreateTime(SysDateMgr.getSysTime());
    		            tradeData.setInstId(strUInstId);
    		            tradeData.setInstType(IotInstanceTradeData.INST_TYPE_PKG);
    		            tradeData.setTradeId(btd.getTradeId());
    		            tradeData.setPlatCode("PBSS");
    		            tradeData.setProdInstId(strUProdInstID);
    		            tradeData.setRemark("");
    		            tradeData.setRsrvStr1(strUElementId);
    		            tradeData.setRsrvStr2(strUElementType);
    		            tradeData.setRsrvStr3(strStartDate);
    		            tradeData.setRsrvStr4(strEndDate);
    		            tradeData.setRsrvStr5(strUProId);
    		            tradeData.setRsrvStr6(strUPkgId);
    		            tradeData.setSubsId("");
    		            tradeData.setUserId(strUserID);
    		            Dao.insert("TF_F_INSTANCE_PF_REL", tradeData.toData(), CSBizBean.getTradeEparchyCode());
        			}
        			else
        			{
        				IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
    		            tradeData.setCreateTime(SysDateMgr.getSysTime());
    		            tradeData.setInstId(strTInstId);
    		            tradeData.setInstType(IotInstanceTradeData.INST_TYPE_PKG);
    		            tradeData.setTradeId(btd.getTradeId());
    		            tradeData.setPlatCode("PBSS");
    		            tradeData.setProdInstId(strUProdInstID);
    		            tradeData.setRemark("");
    		            tradeData.setRsrvStr1(strTElementId);
    		            tradeData.setRsrvStr2(strTElementType);
    		            tradeData.setRsrvStr3(strStartDate);
    		            tradeData.setRsrvStr4(strEndDate);
    		            tradeData.setRsrvStr5(strTProId);
    		            tradeData.setRsrvStr6(strTPkgId);
    		            tradeData.setSubsId("");
    		            tradeData.setUserId(strUserID);
    		            Dao.insert("TF_F_INSTANCE_PF_REL", tradeData.toData(), CSBizBean.getTradeEparchyCode());
        			}
				}
	        }
		}
        
		List<DiscntTradeData> DiscntList = uca.getUserDiscnts();
        if(CollectionUtils.isNotEmpty(DiscntList))
		{
        	for (int i = 0; i < DiscntList.size(); i++)
	        {
        		DiscntTradeData discnt = DiscntList.get(i);
                String strUInstId = discnt.getInstId();
                String strUElementId = discnt.getElementId();
                String strUModifyTag = discnt.getModifyTag();
                String strUElementType = discnt.getElementType();
                String strUStartDate = discnt.getStartDate();
                String strUEndDate = discnt.getEndDate();
                
                if (BofConst.MODIFY_TAG_ADD.equals(strUModifyTag))
                {
                    continue;
                }
                
                IData IDUDiscntConfig = DISCNT_CONFIG_MAP.get(strUElementId);
                if (IDataUtil.isEmpty(IDUDiscntConfig))
                {
                    continue;
                }
                
                String strUProId = IDUDiscntConfig.getString("PARA_CODE1");
                String strUPkgId = IDUDiscntConfig.getString("PARA_CODE2");
                
                if(!BofConst.MODIFY_TAG_ADD.equals(strUModifyTag))
                {
                	IData e = idAllPkgInst.getData(strUPkgId);
            		if(IDataUtil.isEmpty(e))
            		{
            			//表中存在的，不用重新生成
            			IData idParentDiscntPkg = InstancePfQuery.queryInstanceByInstId(strUserID, strUInstId, "P", CSBizBean.getTradeEparchyCode());
        	            if(IDataUtil.isNotEmpty(idParentDiscntPkg))
                    	{
        	            	if(BofConst.MODIFY_TAG_DEL.equals(strUModifyTag))
        	                {
	    	            		String strEndDate = SysDateMgr.decodeTimestamp(strUEndDate, "yyyyMMddHHmmss");
	    	            		
	    	            		IData param = new DataMap();
	    		                param.put("USER_ID", strUserID);
	    		                param.put("END_DATE", strEndDate);
	    		                param.put("INST_ID", strUInstId);
	    		                param.put("INST_TYPE", "P");
	    		                StringBuilder sql = new StringBuilder();
	    		                sql.append("UPDATE TF_F_INSTANCE_PF T ");
	    		                sql.append("   SET T.RSRV_STR4 = :END_DATE, ");
	    		                sql.append("       T.CREATE_TIME = SYSDATE ");
	    		                sql.append(" WHERE T.INST_ID = :INST_ID ");
	    		                sql.append("   AND T.USER_ID = :USER_ID ");
	    		                sql.append("   AND T.INST_TYPE = :INST_TYPE ");
	    		                Dao.executeUpdate(sql, param);
        	                }
        	            	
        	            	String strProdInstID = idParentDiscntPkg.getString("PROD_INST_ID");
        	            	
        	            	IData idUPkgInst = new DataMap();
        	            	idUPkgInst.put(strUPkgId, strUInstId);
        	            	idUPkgInst.put("PARA_CODE1", strUProId);
        	            	idUPkgInst.put("PARA_CODE2", strUPkgId);
        	            	idUPkgInst.put("INST_ID", strUInstId);
        	            	idUPkgInst.put("PACKAGE_ID", strProdInstID);
        	            	idUPkgInst.put("MODIFY_TAG", strUModifyTag);
        	            	idUPkgInst.put("ELEMENT_ID", strUElementId);
        	            	idUPkgInst.put("ELEMEMT_TYPE", strUElementType);
        	            	idUPkgInst.put("START_DATE", strUStartDate);
        	            	idUPkgInst.put("END_DATE", strUEndDate);
        	            	idAllPkgInst.put(strUPkgId, idUPkgInst);
                    	}
        	            else
        	            {
        	            	IData idChildrenDiscntPkg = InstancePfQuery.queryInstanceRelByInstId(strUserID, strUInstId, "P", CSBizBean.getTradeEparchyCode());
        	            	if(IDataUtil.isNotEmpty(idChildrenDiscntPkg))
                        	{
        	            		if(BofConst.MODIFY_TAG_DEL.equals(strUModifyTag))
            	                {
            	            		String strEndDate = SysDateMgr.decodeTimestamp(strUEndDate, "yyyyMMddHHmmss");
            	            		
            	            		IData param = new DataMap();
            		                param.put("USER_ID", strUserID);
            		                param.put("END_DATE", strEndDate);
            		                param.put("INST_ID", strUInstId);
            		                param.put("INST_TYPE", "P");
            		                StringBuilder sql = new StringBuilder();
            		                sql.append("UPDATE TF_F_INSTANCE_PF_REL T ");
            		                sql.append("   SET T.RSRV_STR4 = :END_DATE, ");
            		                sql.append("       T.CREATE_TIME = SYSDATE ");
            		                sql.append(" WHERE T.INST_ID = :INST_ID ");
            		                sql.append("   AND T.USER_ID = :USER_ID ");
            		                sql.append("   AND T.INST_TYPE = :INST_TYPE ");
            		                Dao.executeUpdate(sql, param);
            	                }
                        	}
        				}
            		}
                }
	        }
		}
        
        List<DiscntTradeData> lsDiscntTrade = btd.get("TF_B_TRADE_DISCNT");
		if(CollectionUtils.isNotEmpty(lsDiscntTrade))
		{
			for (int j = 0; j < lsDiscntTrade.size(); j++)
	        {
				DiscntTradeData stDiscnt = lsDiscntTrade.get(j);
				String strTInstId = stDiscnt.getInstId();
	            String strTElementId = stDiscnt.getElementId();
	            String strTModifyTag = stDiscnt.getModifyTag();
	            String strTElementType = stDiscnt.getElementType();
	            String strTStartDate = stDiscnt.getStartDate();
	            String strTEndDate = stDiscnt.getEndDate();
	            
	            if (!BofConst.MODIFY_TAG_ADD.equals(strTModifyTag))
	            {
	                continue;
	            }
	            
	            IData idTDiscntConfig = DISCNT_CONFIG_MAP.get(strTElementId);
	            if (IDataUtil.isEmpty(idTDiscntConfig))
	            {
	                continue;
	            }
	            
	            String strTProId = idTDiscntConfig.getString("PARA_CODE1");
	            String strTPkgId = idTDiscntConfig.getString("PARA_CODE2");
	            
	            IData e = idAllPkgInst.getData(strTPkgId);
        		if(IDataUtil.isEmpty(e))
        		{
        			String strStartDate = SysDateMgr.decodeTimestamp(strTStartDate, "yyyyMMddHHmmss");
        			String strEndDate = SysDateMgr.decodeTimestamp(strTEndDate, "yyyyMMddHHmmss");
        			String strProdInstId = SeqMgr.getPbssBizProdInstId();
					
					IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
		            tradeData.setCreateTime(SysDateMgr.getSysTime());
		            tradeData.setInstId(strTInstId);
		            tradeData.setInstType(IotInstanceTradeData.INST_TYPE_PKG);
		            tradeData.setTradeId(btd.getTradeId());
		            tradeData.setPlatCode("PBSS");
		            tradeData.setProdInstId(strProdInstId);
		            tradeData.setRemark("");
		            tradeData.setRsrvStr1(strTElementId);
		            tradeData.setRsrvStr2(strTElementType);
		            tradeData.setRsrvStr3(strStartDate);
		            tradeData.setRsrvStr4(strEndDate);
		            tradeData.setRsrvStr5(strTProId);
		            tradeData.setRsrvStr6(strTPkgId);
		            tradeData.setSubsId("");
		            tradeData.setUserId(strUserID);
		            Dao.insert(tradeData.getTableName(), tradeData.toData(), CSBizBean.getTradeEparchyCode());
		            
		            
	            	IData idUPkgInst = new DataMap();
	            	idUPkgInst.put(strTPkgId, strProdInstId);
	            	idUPkgInst.put("PARA_CODE1", strTProId);
	            	idUPkgInst.put("PARA_CODE2", strTPkgId);
	            	idUPkgInst.put("INST_ID", strTInstId);
	            	idUPkgInst.put("PACKAGE_ID", strProdInstId);
	            	idUPkgInst.put("MODIFY_TAG", strTModifyTag);
	            	idUPkgInst.put("ELEMENT_ID", strTElementId);
	            	idUPkgInst.put("ELEMEMT_TYPE", strTElementType);
	            	idUPkgInst.put("START_DATE", strTStartDate);
	            	idUPkgInst.put("END_DATE", strTEndDate);
	            	idAllPkgInst.put(strTPkgId, idUPkgInst);
        		}
        		else 
        		{
        			String strUProId = e.getString("PARA_CODE1");
        			String strUPkgId = e.getString("PARA_CODE2");
        			String strUElementId = e.getString("ELEMENT_ID");
        			String strUModifyTag = e.getString("MODIFY_TAG");
        			String strUElementType = e.getString("ELEMEMT_TYPE");
        			String strUInstId = e.getString("INST_ID");
        			String strUProdInstID = e.getString("PACKAGE_ID");
        			String strUStartDate = e.getString("START_DATE");
        			String strUEndDate = e.getString("END_DATE");
        			
        			String strStartDate = SysDateMgr.decodeTimestamp(strTStartDate, "yyyyMMddHHmmss");
        			String strEndDate = SysDateMgr.decodeTimestamp(strTEndDate, "yyyyMMddHHmmss");
        			if(strTEndDate.compareTo(strUEndDate) > 0 && BofConst.MODIFY_TAG_DEL.equals(strUModifyTag))
        			{
    	                IData param = new DataMap();
    	                param.put("PARA_CODE1", strTProId);
    	                param.put("PARA_CODE2", strTPkgId);
                		param.put("T_INST_ID", strTInstId);
    	                param.put("USER_ID", strUserID);
    	                param.put("ELEMENT_ID", strTElementId);
    	                param.put("ELEMEMT_TYPE", strTElementType);
    	                param.put("END_DATE", strEndDate);
    	                param.put("INST_ID", strUInstId);
    	                param.put("INST_TYPE", "P");
    	                StringBuilder sql = new StringBuilder();
    	                sql.append("UPDATE TF_F_INSTANCE_PF T ");
    	                sql.append("   SET T.RSRV_STR1 = :ELEMENT_ID, ");
    	                sql.append("       T.RSRV_STR2 = :ELEMEMT_TYPE, ");
    	                sql.append("       T.RSRV_STR4 = :END_DATE, ");
    	                sql.append("       T.RSRV_STR5 = :PARA_CODE1, ");
    	                sql.append("       T.RSRV_STR6 = :PARA_CODE2, ");
    	                sql.append("       T.INST_ID   = :T_INST_ID, ");
    	                sql.append("       T.CREATE_TIME = SYSDATE ");
    	                sql.append(" WHERE T.INST_ID = :INST_ID ");
    	                sql.append("   AND T.USER_ID = :USER_ID ");
    	                sql.append("   AND T.INST_TYPE = :INST_TYPE ");
    	                Dao.executeUpdate(sql, param);

    	            	e.put("PARA_CODE1", strTProId);
    	            	//e.put("PARA_CODE2", strTPkgId);
    	            	e.put("INST_ID", strTInstId);
    	            	e.put("MODIFY_TAG", strTModifyTag);
    	            	e.put("ELEMENT_ID", strTElementId);
    	            	e.put("ELEMEMT_TYPE", strTElementType);
    	            	e.put("END_DATE", strTEndDate);
    	            	
    	            	strStartDate = SysDateMgr.decodeTimestamp(strUStartDate, "yyyyMMddHHmmss");
            			strEndDate = SysDateMgr.decodeTimestamp(strUEndDate, "yyyyMMddHHmmss");
    	            	IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
    		            tradeData.setCreateTime(SysDateMgr.getSysTime());
    		            tradeData.setInstId(strUInstId);
    		            tradeData.setInstType(IotInstanceTradeData.INST_TYPE_PKG);
    		            tradeData.setTradeId(btd.getTradeId());
    		            tradeData.setPlatCode("PBSS");
    		            tradeData.setProdInstId(strUProdInstID);
    		            tradeData.setRemark("");
    		            tradeData.setRsrvStr1(strUElementId);
    		            tradeData.setRsrvStr2(strUElementType);
    		            tradeData.setRsrvStr3(strStartDate);
    		            tradeData.setRsrvStr4(strEndDate);
    		            tradeData.setRsrvStr5(strUProId);
    		            tradeData.setRsrvStr6(strUPkgId);
    		            tradeData.setSubsId("");
    		            tradeData.setUserId(strUserID);
    		            Dao.insert("TF_F_INSTANCE_PF_REL", tradeData.toData(), CSBizBean.getTradeEparchyCode());
        			}
        			else
        			{
        				IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
			            tradeData.setCreateTime(SysDateMgr.getSysTime());
			            tradeData.setInstId(strTInstId);
			            tradeData.setInstType(IotInstanceTradeData.INST_TYPE_PKG);
			            tradeData.setTradeId(btd.getTradeId());
			            tradeData.setPlatCode("PBSS");
			            tradeData.setProdInstId(strUProdInstID);
			            tradeData.setRemark("");
			            tradeData.setRsrvStr1(strTElementId);
			            tradeData.setRsrvStr2(strTElementType);
			            tradeData.setRsrvStr3(strStartDate);
			            tradeData.setRsrvStr4(strEndDate);
			            tradeData.setRsrvStr5(strTProId);
			            tradeData.setRsrvStr6(strTPkgId);
			            tradeData.setSubsId("");
			            tradeData.setUserId(strUserID);
			            Dao.insert("TF_F_INSTANCE_PF_REL", tradeData.toData(), CSBizBean.getTradeEparchyCode());
        			}
				}
	        }
		}
    }

    public void geneProductInstance(String serialNumber, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        List<ProductTradeData> productList = uca.getUserProducts();

        for (int j = 0; j < productList.size(); j++)
        {
            ProductTradeData product = productList.get(j);
            if (PRODUCT_CONFIG_MAP.get(product.getProductId()) != null)
            {
                IData productConfig = (IData) PRODUCT_CONFIG_MAP.get(product.getProductId());
                // 如果是新增的产品，且物联网中有此产品
                if (productConfig.getString("PARAM_CODE").equals(product.getProductId()) && BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()))
                {
                	String strStartDate = SysDateMgr.decodeTimestamp(product.getStartDate(), "yyyyMMddHHmmss");
                	String strEndDate = SysDateMgr.decodeTimestamp(product.getEndDate(), "yyyyMMddHHmmss");
                	
                    IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
                    tradeData.setCreateTime(SysDateMgr.getSysTime());
                    tradeData.setInstId(product.getInstId());
                    tradeData.setInstType(IotInstanceTradeData.INST_TYPE_SVC);
                    tradeData.setTradeId(btd.getTradeId());
                    tradeData.setPlatCode("PBSS");
                    tradeData.setProdInstId(SeqMgr.getPbssBizProdInstId());
                    tradeData.setRemark("");
                    tradeData.setRsrvStr1(product.getProductId());
                    tradeData.setRsrvStr2("P");
                    tradeData.setRsrvStr3(strStartDate);
                    tradeData.setRsrvStr4(strEndDate);
                    tradeData.setRsrvStr5(productConfig.getString("PARA_CODE1", ""));
                    tradeData.setRsrvStr6("");
                    tradeData.setSubsId("");
                    tradeData.setUserId(uca.getUserId());
                    Dao.insert(tradeData.getTableName(), tradeData.toData(), CSBizBean.getTradeEparchyCode());
                }
                else if(productConfig.getString("PARAM_CODE").equals(product.getProductId()) && BofConst.MODIFY_TAG_DEL.equals(product.getModifyTag()))
                {
                	String strEndDate = SysDateMgr.decodeTimestamp(product.getEndDate(), "yyyyMMddHHmmss");
            		
            		IData param = new DataMap();
	                param.put("USER_ID", product.getUserId());
	                param.put("END_DATE", strEndDate);
	                param.put("INST_ID", product.getInstId());
	                StringBuilder sql = new StringBuilder();
	                sql.append("UPDATE TF_F_INSTANCE_PF T ");
	                sql.append("   SET T.RSRV_STR4 = :END_DATE ");
	                sql.append(" WHERE T.INST_ID = :INST_ID ");
	                sql.append("   AND T.USER_ID = :USER_ID ");
	                Dao.executeUpdate(sql, param);
                }
            }
        }
    }

    /**
     * 对开户的 TF_INSTANCE_PF的处理
     * 
     * @param serialNumber
     * @param btd
     * @throws Exception
     */
    public void geneUserInstance(String serialNumber, BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
        tradeData.setCreateTime(SysDateMgr.getSysTime());
        tradeData.setInstId("-1");
        tradeData.setInstType(IotInstanceTradeData.INST_TYPE_USER);
        tradeData.setTradeId(btd.getTradeId());
        tradeData.setPlatCode("PBSS");
        tradeData.setProdInstId("0");
        tradeData.setRemark("物联网开户的实例");
        tradeData.setRsrvStr1("");
        tradeData.setRsrvStr2("");
        tradeData.setRsrvStr3("");
        tradeData.setRsrvStr4("");
        tradeData.setRsrvStr5("");
        tradeData.setRsrvStr6("");
        tradeData.setSubsId(SeqMgr.getPbssBizSubsId());
        tradeData.setUserId(userId);
        Dao.insert(tradeData.getTableName(), tradeData.toData(), CSBizBean.getTradeEparchyCode());

    }

    private void removeUser(String serialNumber, BusiTradeData btd)
    {
        // String userId = btd.getRD().getUca().getUserId();

    }

    private void stringIotService(String serialNumber, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        List<SvcTradeData> iotSvcList = uca.getUserSvcsBySvcIdArray("99010000");
        SvcTradeData iotSvc = null;
        if (iotSvcList != null && !iotSvcList.isEmpty())
        {
            iotSvc = iotSvcList.get(0);
        }

        if (iotSvc != null)
        {
            SvcTradeData cloneIotSvc = iotSvc.clone();
            cloneIotSvc.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(serialNumber, cloneIotSvc);
        }
    }
    
    private void dealIotReverseSeq(BusiTradeData btd) throws Exception
    {
    	//反向接口的平台操作流水号保存至预留字段7
    	IDataset dataset = CommparaInfoQry.getDefaultOpenSvcId("CSM","1026","IoTReverseTradeType");
    	for(int i = 0; i < dataset.size(); i++){
    		IData tradetype = dataset.getData(i);
    		if(tradetype.getString("PARAM_CODE").equals(btd.getTradeTypeCode())){
    			String operseq = btd.getRD().getPageRequestData().getString("OPR_SEQ");
    			if(StringUtils.isNotEmpty(operseq)){
    	        	btd.getMainTradeData().setRsrvStr7(operseq);
    	        	
    	        	IotQuery.recordReverseOprSeq(btd.getTradeId(), operseq);//记录到反向操作流水表
    	        }
    		}
    	}
    }
    
    
    
     private void genePcrfInstance(String serialNumber, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        List<PcrfTradeData> pcrftradeList = btd.get("TF_B_TRADE_PCRF");
        for (int i = 0; i < pcrftradeList.size(); i++)
        {
        	PcrfTradeData pcrfTrade = pcrftradeList.get(i);
        	if(BofConst.MODIFY_TAG_ADD.equals(pcrfTrade.getModifyTag())){
        	       IotInstanceTradeData tradeData = new IotInstanceTradeData();// 物联网实例数据
                   tradeData.setCreateTime(SysDateMgr.getSysTime());
                   tradeData.setInstId(pcrfTrade.getInstId());
                   tradeData.setInstType(IotInstanceTradeData.INST_TYPE_SVC);
                   tradeData.setTradeId(btd.getTradeId());
                   tradeData.setPlatCode("PBSS");
                   tradeData.setProdInstId(SeqMgr.getPbssBizProdInstId());
                   tradeData.setRemark("物联网PCRF策略实例");
                   tradeData.setRsrvStr1(pcrfTrade.getRelaInstId());
                   tradeData.setRsrvStr2(pcrfTrade.getStartDate());
                   tradeData.setRsrvStr3(pcrfTrade.getEndDate());
                   tradeData.setRsrvStr4("");
                   tradeData.setRsrvStr5("");
                   tradeData.setRsrvStr6("");
                   tradeData.setSubsId("");
                   tradeData.setUserId(uca.getUserId());
                   Dao.insert(tradeData.getTableName(), tradeData.toData(), CSBizBean.getTradeEparchyCode());
        	}else if(BofConst.MODIFY_TAG_DEL.equals(pcrfTrade.getModifyTag())){
        		String strEndDate = SysDateMgr.decodeTimestamp(pcrfTrade.getEndDate(), "yyyyMMddHHmmss");
        		
        		IData param = new DataMap();
                param.put("USER_ID", pcrfTrade.getUserId());
                param.put("END_DATE", strEndDate);
                param.put("INST_ID", pcrfTrade.getInstId());
                StringBuilder sql = new StringBuilder();
                sql.append("UPDATE TF_F_INSTANCE_PF T ");
                sql.append("   SET T.RSRV_STR3 = :END_DATE ");
                sql.append(" WHERE T.INST_ID = :INST_ID ");
                sql.append("   AND T.USER_ID = :USER_ID ");
                Dao.executeUpdate(sql, param);
        	}
        }
    }


    private void dealIotVoiceSvcAttr(String serialNumber,BusiTradeData btd) throws Exception
    {
     	//判断是否是反向接口退订
    	String operseq = btd.getRD().getPageRequestData().getString("OPR_SEQ");
    	if(StringUtils.isEmpty(operseq)){
    		return ;
    	}
        List<SvcTradeData> svcTrades = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        List<AttrTradeData> svcAttrTrades = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
        if(svcTrades != null && svcTrades.size() > 0){
        	for(SvcTradeData svcTrade: svcTrades){
        		//语音服务退订,需处理属性OCSIPROV、 TCSIPROV、CLIPStatus、IntRoamStatus修改为02
        		if(BofConst.MODIFY_TAG_DEL.equals(svcTrade.getModifyTag()) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTrade.getElementType())){
            		IData svcConfig = SVC_CONFIG_MAP.get(svcTrade.getElementId());
            		if (IDataUtil.isNotEmpty(svcConfig) && "I00010100095".equals(svcConfig.getString("PARA_CODE1")) && "I00010100036".equals(svcConfig.getString("PARA_CODE2")))
    				{
            			if(svcAttrTrades != null && svcAttrTrades.size() > 0){
            				for(AttrTradeData attrTrade : svcAttrTrades){
            					if(attrTrade.getRelaInstId().equals(svcTrade.getInstId()) && ("OCSIPROV".equals(attrTrade.getAttrCode()) || "TCSIPROV".equals(attrTrade.getAttrCode()) || "CLIPStatus".equals(attrTrade.getAttrCode()) || "IntRoamStatus".equals(attrTrade.getAttrCode()))){
            						attrTrade.setAttrValue("02");
            					}
            				}
            			}
    				}
            	}
        	}
        }
    }
}
