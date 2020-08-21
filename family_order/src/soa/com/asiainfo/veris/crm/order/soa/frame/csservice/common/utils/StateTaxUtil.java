
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm.SaleDetailQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/*
 * 该类和业务相关,从业务中获取国税相关信息,此Util还依赖query
 */
public class StateTaxUtil extends CSBizBean
{
    /**
     * @Description 根据业务feelist组装split(商品列表)
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset genSpList(IData param) throws Exception
    {
        IDataset feeList = param.getDataset("FEE_LIST");
        IDataset spList = new DatasetList();
        if (TaxUtils.yxTrade(param.getString("TRADE_TYPE_CODE"))){
        	
        	//REQ201505150014 请更改抢4G手机红包营销活动打印发票的货品名称 20150515 by songlm
            //1、先判断是否是营销活动   2、再获取主台帐的包ID，通过包ID查找td_b_package_ext配置数据
            //3、再获取td_b_package_ext表的TAG_SET3，看是否配置为采用特殊打印模版   4、获取主台帐的RSRV_STR7，判断是否是购机活动
            //5、主台帐的REMARK存放了终端串码在华为返回的【品牌型号】，是通过reg的action【ResetMainTradeAction.java】设置的，来源于营销活动台帐的rsrvStr23
            
            String pmgg = "合约套餐费";//默认为原来的  合约套餐费
        	
        	//1、先判断是否是营销活动
            if("240".equals(param.getString("TRADE_TYPE_CODE"))){
            	
            	//根据trade_id获取主台帐信息
            	IDataset mainTradeDataSet = TradeInfoQry.queryUserTradeByBTradeAndBhTrade(param.getString("TRADE_ID"));
            	
            	//主台帐非空的情况下
            	if(IDataUtil.isNotEmpty(mainTradeDataSet)){
            		
            		IData mainTradeData = mainTradeDataSet.getData(0);
            		//2、获取到主台帐中的RSRV_STR2值，即包ID
                	String packegeId = mainTradeData.getString("RSRV_STR2","");
                	
                	if(StringUtils.isNotEmpty(packegeId))
                	{
                		//通过包ID查看td_b_package_ext表数据
                    	IDataset packageExtDataset = PkgExtInfoQry.queryPackageExtInfo(packegeId, "0898");
                    	
                    	//3、获取到td_b_package_ext的TAG_SET3值，并判断第一位是不是1，1代表特殊模版，空或0代表正常
                        String tagSet3 = packageExtDataset.getData(0).getString("TAG_SET3","");
                        
                        //如果TAG_SET3非空，有值，并且第一位是1，即代表采用特殊模版打印
                        if (StringUtils.isNotBlank(tagSet3) && tagSet3.length() > 0 && tagSet3.substring(0, 1).equals("1"))
                        {
                        	//4、获取主台帐表中的RSRV_STR7字段，如果是营销活动的主台帐，该值存的是营销活动类型，即YX03之类的值
                        	String rsrvStr7 = mainTradeData.getString("RSRV_STR7","");
                        	
                        	//判断是否是购机类活动，是则继续
                        	if("YX03".equals(rsrvStr7) || "YX07".equals(rsrvStr7) || "YX08".equals(rsrvStr7) || "YX09".equals(rsrvStr7))
                        	{
                        		//取到主台帐表中的REMARK字段值，如果是购机活动，该值存的是终端串码在华为返回的【品牌型号】
                                String rsrvStr23 = mainTradeData.getString("REMARK","");

                                if(StringUtils.isNotEmpty(rsrvStr23)){
                                	pmgg = rsrvStr23;
                                }
                        	}
                        }
                	}
            	}
            }
            //end
 
        	IData spInfo = new DataMap();
            //spInfo.put("PMGG", "合约套餐费");
        	spInfo.put("PMGG", pmgg);
        	spInfo.put("SPJE", param.getString("TOTAL_FEE", "0"));
        	spList.add(spInfo);
        }else if ("416".equals(param.getString("TRADE_TYPE_CODE"))){//有价卡销售
        	IData spInfo = new DataMap();
            spInfo.put("PMGG", "通信服务费");
        	spInfo.put("SPJE", param.getString("TOTAL_FEE", "0"));
        	spList.add(spInfo);
        }else{
        	if(IDataUtil.isNotEmpty(feeList)){
        		for (int i = 0, size = feeList.size(); i < size; ++i)
                {
                    IData spInfo = new DataMap();
                    IData fee = feeList.getData(i);

                	spInfo.put("PMGG", fee.getString("FEE_TYPE"));
                	spInfo.put("PPXH", fee.getString("BRAND_MODEL"));
                	spInfo.put("JLDW", fee.getString("UNIT"));
                	spInfo.put("SPSL", fee.getString("QUANTITY"));
                	spInfo.put("SPDJ", fee.getString("PRICE"));
                	spInfo.put("SPJE", fee.getString("FEE", "0"));
                    spList.add(spInfo);
                }
        	}
        }
        return spList;
    }

    /**
     * @Description 组装国税发票信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IData genStateTaxPackage(IData param) throws Exception
    {
        /*
         * 此处还要考虑冲红发票时,要传原票据的ticket_id,tax_no,收据补打发票时要传原收据的ticket_id,tax_no. 这些还是需要在打印支持,能明白当前打印的是哪项
         */
    	param.put("KPLX", "1");//正数发票
    	if(StringUtils.equals("1",param.getString("CH_TICKET",""))){//打印冲红发票
    		//判断当前台账有没有打印过冲红发票,根据金额为负数来判断,有的话,表示当前非第一次打印冲红发票,不再传原TAX_NO,TICKET_ID
    		IDataset printedCHReceipt = TicketInfoQry.qryPrintedCHReceipt(param.getString("TRADE_ID",""));
    		if(IDataUtil.isEmpty(printedCHReceipt)){
    			IDataset receipts = TicketInfoQry.qryTradeReceipt(param.getString("TRADE_ID",""));
        		if(IDataUtil.isEmpty(receipts)){
        			CSAppException.apperr(TicketException.CRM_TICKET_11,param.getString("TRADE_ID",""));
        		}
        		
        		for(int i=0,size=receipts.size();i<size;++i){//一个流水中,营业费或者预存款只会有一个当前有效的发票
        			if(StringUtils.equals(param.getString("FEE_MODE",""), receipts.getData(i).getString("FEE_MODE",""))){
        				param.put("O_TAX_NO", receipts.getData(i).getString("TAX_NO",""));
        	    		param.put("O_TICKET_ID", receipts.getData(i).getString("TICKET_ID",""));
        	    		break;
        			}
        		}
    		}
    		
    		param.put("KPLX", "2");//负数发票
    	}
    	
        return TaxUtils.setStateTaxPackageInfo(genSpList(param), param);
    }

    /**
     * @Description 从台账中获取白卡相关信息
     * @param
     * @return String
     * @throws Exception
     */
    public static IData getTradeEmptyCardInfo(String tradeId) throws Exception
    {
        // 从tf_b_trade_res中查找sim卡信息
        IData simInfos = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
        if (null == simInfos || 0 == simInfos.size())
            CSAppException.apperr(TradeException.CRM_TRADE_65, tradeId);

        return simInfos;
    }

    /**
     * @Description 更新账务普通预存款可打金额
     * @param fee	开票时传负值,作废、冲红传正值
     * @throws Exception
     * @date:2010-3-12
     */
    public static void updateAcctPrintFee(String serialNumber, String tradeId, String fee, String queryTag) throws Exception{
        if ("".equals(serialNumber))
            return;

        String acctId = "";
        String acctionCode = "0";

        IDataset dataset = AcctInfoQry.qryAcctInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(dataset)){
            acctId = dataset.getData(0).getString("ACCT_ID", "");
        }else{//如果没有账户信息,则从台账表里捞账户信息
        	dataset = TradeAcctInfoQry.getTradeAccountByTradeId(tradeId);
        	if (IDataUtil.isNotEmpty(dataset))
        		acctId = dataset.getData(0).getString("ACCT_ID", "");
        }

        dataset = SaleDetailQry.getTradeDiscntCode(tradeId, "0");
        if (IDataUtil.isNotEmpty(dataset))
            acctionCode = dataset.getData(0).getString("A_DISCNT_CODE");

        AcctCall.updatePrintFee( acctionCode, acctId, fee, queryTag, tradeId);
    }

    /**
     * @Description 发票作废
     * @param pd
     * @throws Exception
     * @date:2010-3-12
     */
    public static void stateTaxInvoiceCancel(String tradeTypeCode, String taxNo, String ticketId, String fwm,
    		String tradeStaffId,String fee,String ticketTypeCode) throws Exception{
    	String kplx = fee.startsWith("-") ? "5" : "4";//4为正数废票,5为负数废票
    	/**
         * REQ201503040003关于资源侧发票使用界面走权限控制的优化
         * 2015-03-27 CHENXY3
         * 新增调用接口传入 TRADE_TYPE_CODE   TRADE_TYPE_NAME
         * */ 
    	String tradeTypeName ="";
        if(!"".equals(tradeTypeCode)){
        	tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
        }
        tradeStaffId=tradeStaffId+"|"+tradeTypeCode+"-"+tradeTypeName;
        // 已用发票作废
    	IDataset ds = ResCall.stateTaxInvoiceCancel(taxNo, ticketId, fwm, kplx, tradeStaffId, TaxUtils.getNsrlx(tradeTypeCode), 
        		TaxUtils.getFpzldm(), TaxUtils.getPydm(tradeTypeCode),ticketTypeCode);
        
        if (IDataUtil.isEmpty(ds) || !StringUtils.equals("0000", ds.getData(0).getString("RETURNCODE"))){
            CSAppException.apperr(TicketException.CRM_TICKET_13, ds.getData(0).getString("RETURNMSG"));
        }
    }
    
    /**
     * 国税已用发票作废
     * REQ201501270018关于票据作废界面走域权控制的优化
     * @param inparam  
     * @return
     * @throws Exception
     */
    public static void stateTaxInvoiceCancel(IData inparam) throws Exception{
    	String kplx = inparam.getString("FEE").startsWith("-") ? "5" : "4";//4为正数废票,5为负数废票
    	
    	IData inData = new DataMap();
    	inData.put("TAX_NO", inparam.getString("TAX_NO"));
        inData.put("TICKET_ID", inparam.getString("TICKET_ID"));
        inData.put("FWM", inparam.getString("FWM"));
        inData.put("KPLX", kplx);// 开票类型
        inData.put("STAFF_ID", inparam.getString("STAFF_ID"));
        inData.put("NSRLX", TaxUtils.getNsrlx(inparam.getString("TRADE_TYPE_CODE")));
        inData.put("FPZL_DM", TaxUtils.getFpzldm());// 发票种类代码
        inData.put("PYCODE", TaxUtils.getPydm(inparam.getString("PYCODE")));// 票样代码
        inData.put("TICKET_TYPE_CODE", inparam.getString("TICKET_TYPE_CODE"));//票样类型
        //需求新增传值：
        inData.put("TRADE_TYPE_NAME", inparam.getString("TRADE_TYPE_NAME"));// 业务名
        inData.put("TRADE_TYPE_CODE", inparam.getString("TRADE_TYPE_CODE"));// 业务类别
        inData.put("TRADE_ID", inparam.getString("TRADE_ID"));//业务ID 
        // 已用发票作废
    	IDataset ds = ResCall.stateTaxInvoiceCancel(inData); 
        
        if (IDataUtil.isEmpty(ds) || !StringUtils.equals("0000", ds.getData(0).getString("RETURNCODE"))){
            CSAppException.apperr(TicketException.CRM_TICKET_13, ds.getData(0).getString("RETURNMSG"));
        }
    }
    
    /**
     * @Description 获取流水需要冲红的发票号
     * @param pd
     * @throws Exception
     * @date:2010-3-12
     */
    public static String needCHReceipt(String tradeId) throws Exception{
    	if(StringUtils.isEmpty(tradeId))
    		return "";
    	
    	StringBuilder sb = new StringBuilder();
    	IDataset receipts = TicketInfoQry.qryTradeReceipt(tradeId);
		if(IDataUtil.isNotEmpty(receipts)){
			for(int i=0,size=receipts.size();i<size;++i){
				IData receipt = receipts.getData(i);
				if(!StringUtils.equals(receipt.getString("ACCEPT_TIME","").substring(0,7), SysDateMgr.getSysTime().substring(0,7))){
					if(sb.length() > 0)
						sb.append(",");
					sb.append(receipt.getString("TICKET_ID",""));
				}
			}
		}
        
        return sb.toString();
    }
    
    /**
     * @Description 票据使用确认
     * @param pd
     * @throws Exception
     * @date:2010-3-12
     */
    public static void receiptConfirm(String taxNo, String ticketId, String tradeTypeCode, String ticketTypeCode,
    		String tradeId,String serialNumber,String fee,String advanceTicket,String chTicket) throws Exception{
    	
    	String updTicketId = "";
		String updTaxNo = "";
    	if(StringUtils.equals("1", advanceTicket) && !StringUtils.equals("1", chTicket)){//预存款打印发票,且非冲红
    		IDataset tickets = TicketInfoQry.qryTradeTickets(tradeId);
    		if(IDataUtil.isNotEmpty(tickets)){
    			//对于上一次是打印收据,才需要传收据的票据号给资源进行收据状态转换,后续预存款打印发票不需要此操作
        		for(int i=0,size=tickets.size();i<size;++i){
        			IData ticket = tickets.getData(i);
        			//此处应该是增加一个配置表,配置哪些票据是发票,哪些是资源,否则有新增发票或者收据种类的话,此处还要修改代码,目前代码中写死收据为B\F
        			if(StringUtils.equals("2", ticket.getString("FEE_MODE",""))
        					&& TaxUtils.voucherTicket(ticket.getString("TICKET_TYPE_CODE"))){
        				updTaxNo = ticket.getString("TAX_NO","");
        				updTicketId = ticket.getString("TICKET_ID","");
        	    		break;
        			}
        		}
    		}
    	}
		
    	IDataset ticketInfos = ResCall.occupyPrintTicket(taxNo, ticketId, tradeTypeCode, ticketTypeCode, 
    			tradeId, serialNumber, fee, "1",updTaxNo,updTicketId);
    	IData data = null;
    	IDataset invoiceInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(ticketInfos)){
            data = ticketInfos.getData(0);
            invoiceInfos=data.getDataset("OUTDATA");
        }
        if (IDataUtil.isEmpty(invoiceInfos) || !StringUtils.equals("0000", invoiceInfos.getData(0).getString("RETURNCODE"))){
            CSAppException.apperr(TicketException.CRM_TICKET_15, data.getString("RETURNMSG"));
        }
    }
}
