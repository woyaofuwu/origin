
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CreditGrpTradeReg
{
    /**
     * 处理集团信控
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset grpCreditTradeReg(IData input) throws Exception
    {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "");
        String userId = input.getString("USER_ID", "");
        IData userinfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        // 取下BRAND_CODE，每次都按照ZZZZ查不合理，有些操作类型没有配置ZZZZ
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_011, userId);
        }
        String brandCode = userinfo.getString("BRAND_CODE", "");
        String productId = userinfo.getString("PRODUCT_ID", "");
        
        
        
        input.put("BRAND_CODE", brandCode);
        input.put("PRODUCT_ID", productId);
        input.put(Route.USER_EPARCHY_CODE, userinfo.getString("EPARCHY_CODE", "0898"));
        input.put("SUBSCRIBE_TYPE", "200");
        IDataset result = new DatasetList();

        // 停机、开机
        if ("7110".equals(tradeTypeCode) || "7220".equals(tradeTypeCode) || "7305".equals(tradeTypeCode) || "7303".equals(tradeTypeCode) || "7304".equals(tradeTypeCode) || "7301".equals(tradeTypeCode)|| "7317".equals(tradeTypeCode)|| "7400".equals(tradeTypeCode) || "7401".equals(tradeTypeCode)
                || "7631".equals(tradeTypeCode) || "7630".equals(tradeTypeCode))
        {
            if ("ADCG".equals(brandCode) || "MASG".equals(brandCode) || "VPMR".equals(brandCode) || "WLWG".equals(brandCode))
            {
                result = CSAppCall.call("SS.CreditRegSvc.creditReg", input);// 集团暂停恢复
                if ("VPMR".equals(brandCode))
                {
                    result = CSAppCall.call("SS.GrpCreditSVC.creatCreditStopTask", input);// 批量暂停恢复成员
                }
            }else if ("7010".equals(productId) || "7011".equals(productId) || "7012".equals(productId))//专线产品
            {
            	input. put("OLD_TRADE_TYPE_CODE",tradeTypeCode);
            	result = CSAppCall.call("SS.CreditLineRegSvc.creditReg", input);// 集团暂停恢复
            }
            else if ("BOSG".equals(brandCode)||"JKDT".equals(brandCode))
            {
                String stateCode = "";
                if ("7301".equals(tradeTypeCode) || "7303".equals(tradeTypeCode))
                { // 开机
                    stateCode = "0";
                }
                else if ("7110".equals(tradeTypeCode) || "7305".equals(tradeTypeCode))
                { // 高额停机
                    stateCode = "7";
                }
                else if ("7220".equals(tradeTypeCode))
                { // 欠费停机
                    stateCode = "5";
                }
                else if ("7304".equals(tradeTypeCode))
                { // 人工开机
                    stateCode = "N";
                }
                else if ("7631".equals(tradeTypeCode))
                {
                    // 叠加包恢复
                    stateCode = "G";
                }
                else if ("7630".equals(tradeTypeCode))
                {
                    // 叠加包暂停
                    stateCode = "F";
                }
                input.put("STATE_CODE", stateCode);
                result = CSAppCall.call("SS.GrpCreditSVC.bbossCreditReg", input);// BBOSS暂停恢复
                
            } else if("BNBD".equals(brandCode)){//集团商务宽带停复机
                
                if("7301".equals(tradeTypeCode) || "7317".equals(tradeTypeCode) || "7303".equals(tradeTypeCode))
                {
                    input.put("USER_EPARCHY_CODE", "0898");
                    result  = CSAppCall.call("SS.BroadbandMemStateChgBatSVC.crtOpenBat", input);
                } 
                else if("7220".equals(tradeTypeCode))
                {
                    input.put("USER_EPARCHY_CODE", "0898");
                    result  = CSAppCall.call("SS.BroadbandMemStateChgBatSVC.crtStopBat", input);
                }
            } else if("CTRX".equals(brandCode) && "2222".equals(productId)){//IMS集团业务停复机
            	
            	if("7301".equals(tradeTypeCode) || "7317".equals(tradeTypeCode))
                {
                    input.put("USER_EPARCHY_CODE", "0898");
                    result  = CSAppCall.call("SS.ImsDesktopTelMebBatSVC.crtOpenBat", input);
                } 
                else if("7220".equals(tradeTypeCode))
                {
                    input.put("USER_EPARCHY_CODE", "0898");
                    result  = CSAppCall.call("SS.ImsDesktopTelMebBatSVC.crtStopBat", input);
                }
            	
            }else if("ESPG".equals(brandCode)){
            	if("7110".equals(tradeTypeCode) || "7220".equals(tradeTypeCode) || "7305".equals(tradeTypeCode)){
            		input.put("OPER_CODE", "stop");
            	}else if("7301".equals(tradeTypeCode) || "7303".equals(tradeTypeCode) || "7304".equals(tradeTypeCode)){
            		input.put("OPER_CODE", "back");
            	}
            	result = CSAppCall.call("SS.CreditRegSvc.creditReg", input);// 集团暂停恢复
            	if(IDataUtil.isNotEmpty(result)){
        			input.put("TRADE_ID", result.getData(0).getString("TRADE_ID"));
        			CSAppCall.call("SS.EspProductOrderStateSynSVC.crtTrade", input);
        		}
            }

            // 欠费销号
        }
        else if ("7240".equals(tradeTypeCode))
        {
            if ("BOSG".equals(brandCode))
            {
                String stateCode = "9";
                input.put("STATE_CODE", stateCode);
                result = CSAppCall.call("SS.GrpCreditSVC.bbossCreditReg", input);// BBOSS注销
            }
            else
            {
                result = CSAppCall.call("SS.GrpCreditSVC.creatDestoryTask", input);// 一键注销集团
            }

            // 终止VPMN集团统付
        }
        else if ("7800".equals(tradeTypeCode))
        {
            result = CSAppCall.call("SS.AdvPayChgSVC.crtTrade", input);// 自动终止vpmn

            // 成员特殊停集团彩铃
        }
        else if ("7130".equals(tradeTypeCode))
        {
            input.put("DEAL_FLAG", "stop");
            result = CSAppCall.call("SS.MemberStateChgSVC.crtBat", input);
            // 集团代付关系暂停、恢复
        }
        else if ("7811".equals(tradeTypeCode) || "7812".equals(tradeTypeCode))
        {
            result = CSAppCall.call("SS.GrpCreditSVC.payRelationChgTask", input);// 批量暂停恢复代付关系

            // 注销集团彩铃成员信控
        }
        else if ("7820".equals(tradeTypeCode))
        {
            result = CreateEndGrpClrMemberBat(input);
        }

        return result;
    }

    /**
     * 注销集团彩铃成员信控
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset CreateEndGrpClrMemberBat(IData inParam) throws Exception
    {

        String userId = inParam.getString("USER_ID");
        String custId = inParam.getString("CUST_ID");
        IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(custId);
        if (IDataUtil.isEmpty(grpCustInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_716);
        }

        String systime = SysDateMgr.getSysTime();
        // 创建注销集团彩铃成员信控批量任务
        IData BatData = new DataMap();

        IData condData = new DataMap();
        condData.put("GROUP_ID", grpCustInfos.getString("GROUP_ID"));
        condData.put("USER_ID", userId);
        condData.put("PRODUCT_ID", "6200");
        BatData.put("BATCH_OPER_TYPE", "GROUPMEMCANCEL");
        BatData.put("BATCH_TASK_NAME", "信控7820工单");
        BatData.put("SMS_FLAG", "0");
        BatData.put("CODING_STR", condData.toString());
        BatData.put("CREATE_TIME", systime);
        BatData.put("ACTIVE_FLAG", "1");
        BatData.put("ACTIVE_TIME", systime);
        BatData.put("DEAL_TIME", systime);
        BatData.put("DEAL_STATE", "1");
        BatData.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId("6200");// 关系类型
        IDataset relaList = RelaUUInfoQry.qryRelationUU(userId, relationTypeCode);
        StringBuilder builder = new StringBuilder(50);
        if (IDataUtil.isNotEmpty(relaList))
        {
            for (int i = 0, row = relaList.size(); i < row; i++)
            {
                IData relaData = relaList.getData(i);

                relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
            }

            String BatchId = BatDealBean.createBat(BatData, relaList);

            builder.append("批次号[" + BatchId + "]");
        }
        IData retData = new DataMap();

        retData.put("ORDER_ID", builder.toString());

        return IDataUtil.idToIds(retData);
    }
}
