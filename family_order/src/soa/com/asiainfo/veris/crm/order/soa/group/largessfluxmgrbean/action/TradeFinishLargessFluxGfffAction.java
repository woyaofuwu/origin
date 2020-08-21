
package com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean.action;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean.LargessFluxQry;

public class TradeFinishLargessFluxGfffAction implements ITradeFinishAction
{
    private final static Logger logger = Logger.getLogger(TradeFinishLargessFluxGfffAction.class);

    
    public void executeAction(IData mainTrade) throws Exception
    {

        String userId = "";
        String userIdB = "";
        String serialNumber = "";
        String serialNumberB = "";
        userId = mainTrade.getString("USER_ID");
        userIdB = mainTrade.getString("USER_ID_B");
        serialNumber = mainTrade.getString("SERIAL_NUMBER");//个人号码
        serialNumberB = mainTrade.getString("SERIAL_NUMBER_B");//集团编码
        String rsrvStr1 = mainTrade.getString("RSRV_STR1");
        String rsrvStr2 = mainTrade.getString("RSRV_STR2");//本次需要分配的总流量数
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        
        if ("4001".equals(tradeTypeCode))
        {
           
            if(StringUtils.isNotBlank(rsrvStr1) && "0".equals(rsrvStr1)){//个人用户
                IData inParam = new DataMap();
                inParam.put("USER_ID", userIdB);
                
                int resultLock = LargessFluxQry.updateForLock(inParam);
                
                IDataset infoSets = LargessFluxQry.queryUserGrpGfffInfo(inParam);//查询需要分配的流量
                //IDataset infoSets = LargessFluxQry.queryUserGrpGfffInfoAndRowid(inParam);//查询需要分配的流量
                
                if(IDataUtil.isEmpty(infoSets)){
                    CSAppException.apperr(GrpException.CRM_GRP_857,serialNumberB);
                }
                
                IDataset feeSets = LargessFluxQry.qryUserGrpGfffAllFeeBySn(inParam);//查询需要分配的流量
                if(IDataUtil.isEmpty(feeSets)){
                    CSAppException.apperr(GrpException.CRM_GRP_863,serialNumberB);
                }
                String allFeeStr = feeSets.getData(0).getString("ALLFEE");
                
                //int intRsrvStr2 = Integer.parseInt(rsrvStr2) / (1024 * 1024);//本次需要分配的总流量数
                BigDecimal bd = new BigDecimal(rsrvStr2);
                double dbTempRsrvStr2 = bd.doubleValue() / (1024 * 1024);
                double intRsrvStr2 = Double.parseDouble(new BigDecimal(dbTempRsrvStr2).toString());//本次需要分配的总流量数
                double intAllFee = Double.parseDouble(allFeeStr);//集团产品分配到总流量
                //集团产品的总流量不足以分配时，拦截提示
                if(intRsrvStr2 > intAllFee){
                    CSAppException.apperr(GrpException.CRM_GRP_859,serialNumberB);
                }
                
                boolean leaveFeeFlag = false;//每次分配后，是否有不足分配的剩余流量,true:有,false:没有

                for (int i = 0; i < infoSets.size(); i++){
                    IData infoData = infoSets.getData(i);
                    
                    if(IDataUtil.isNotEmpty(infoData)){
                        
                        if(intRsrvStr2 <= 0){//如果值是0或者已经分配完了则直接跳出循环
                            break;
                        }
                        
                        String limitFeeStr = infoData.getString("LIMIT_FEE","0");//总分配的流量数
                        String consumeFeeStr = infoData.getString("CONSUME_FEE","0");//已经分配的流量总数
                        
                        double intRsrvStr2Main = -1;//记录每次分配的流量数,以便新增TF_F_USER_GRP_GFFF_SUB表记录
                        double limitFee = Double.parseDouble(limitFeeStr);
                        double consumeFee = Double.parseDouble(consumeFeeStr);
                        double consumeFeeAll = consumeFee;
                        
                        if(limitFee <= 0){//流量总数是
                            CSAppException.apperr(GrpException.CRM_GRP_858,serialNumberB);
                        }
                        
                        if((limitFee - consumeFee) <= 0){//这个流量已经分配完了，则取下一条分配流量数
                            continue;
                        }
                        
                        if((limitFee - consumeFee) < intRsrvStr2){//这个流量不够分配时,取这个流量的分配后剩余的流量，做下一轮分配
                            //CSAppException.apperr(GrpException.CRM_GRP_859,serialNumberB);
                            intRsrvStr2 = intRsrvStr2 - (limitFee - consumeFee);//不够分配，计算本次分配后剩余多少流量
                            consumeFeeAll = consumeFeeAll + (limitFee - consumeFee);
                            intRsrvStr2Main = limitFee - consumeFee;
                            leaveFeeFlag = true;
                            
                        } else if((limitFee - consumeFee) >= intRsrvStr2){//这个流量分配刚好够分配的次数,则下一条不取分配流量数
                        //if(((limitFee - consumeFee) >= intRsrvStr2) && leaveFeeFlag){//这个流量分配刚好够分配的次数,则下一条不取分配流量数
                            //不做处理
                            
                            consumeFeeAll = consumeFeeAll + intRsrvStr2;
                            intRsrvStr2Main = intRsrvStr2;
                            intRsrvStr2 = intRsrvStr2 - intRsrvStr2;
                            leaveFeeFlag = true;
                        }
                        
                        
                        if(leaveFeeFlag && intRsrvStr2Main >= 0){
                            IData data = new DataMap();
                            data.put("USER_ID", userIdB);
                            data.put("INST_ID", infoData.getString("INST_ID"));
                            data.put("CONSUME_FEE", consumeFeeAll);
                            data.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            //data.put("NUM_FEE", intRsrvStr2Main);
                            
                            boolean resultFlag = Dao.save("TF_F_USER_GRP_GFFF", data,new String[]{"USER_ID","INST_ID"});
                            if(!resultFlag){
                              CSAppException.apperr(GrpException.CRM_GRP_860,serialNumberB);
                            }
                            
                            //int result = LargessFluxQry.updateUserGrpGfffConsume(data);
                            //System.out.println("result=" + result);
                            //if(result<0){
                            //  CSAppException.apperr(GrpException.CRM_GRP_860,serialNumberB);
                            //}
                                

                            
                            IData param = new DataMap();
                            param.put("PARTITION_ID", userIdB.substring(userIdB.length() - 4));
                            param.put("USER_ID_A", userIdB);//分配的集团产品编码的user_id
                            param.put("SERIAL_NUMBER", serialNumberB);//分配的集团产品编码
                            param.put("USER_ID_B", userId);//被分配的个人手机号码的user_id
                            param.put("SERIAL_NUMBER_B", serialNumber);//被分配的个人手机号码
                            param.put("ROLE_CODE", "0");
                            param.put("CONSUME_FEE", intRsrvStr2Main);
                            param.put("INSERT_TIME", SysDateMgr.getSysTime());
                            param.put("START_DATE", SysDateMgr.getSysTime());
                            param.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                            
                            boolean resultInsert = Dao.insert("TF_F_USER_GRP_GFFF_SUB", param);
                            
                            if(!resultInsert){
                                CSAppException.apperr(GrpException.CRM_GRP_861);
                            }
                            
                            leaveFeeFlag = false;
                        }
                    }
                    
                }
                                
            } else if(StringUtils.isNotBlank(rsrvStr1) && "1".equals(rsrvStr1)){//集团用户
                
                String grpSn = mainTrade.getString("RSRV_STR3");//分配的集团产品编码
                String grpUserId = mainTrade.getString("RSRV_STR4");//分配的集团产品编码的user_id
                
                IData inParam = new DataMap();
                inParam.put("USER_ID", grpUserId);
                
                int resultLock = LargessFluxQry.updateForLock(inParam);
                
                IDataset infoSets = LargessFluxQry.queryUserGrpGfffInfo(inParam);//查询需要分配的流量
                
                if(IDataUtil.isEmpty(infoSets)){
                    CSAppException.apperr(GrpException.CRM_GRP_857,grpSn);
                }
                
                IDataset feeSets = LargessFluxQry.qryUserGrpGfffAllFeeBySn(inParam);//查询需要分配的流量
                if(IDataUtil.isEmpty(feeSets)){
                    CSAppException.apperr(GrpException.CRM_GRP_863,grpSn);
                }
                String allFeeStr = feeSets.getData(0).getString("ALLFEE");
                
                //int intRsrvStr2 = Integer.parseInt(rsrvStr2) / (1024 * 1024);//本次需要分配的总流量数
                BigDecimal bd = new BigDecimal(rsrvStr2);
                double dbTempRsrvStr2 = bd.doubleValue() / (1024 * 1024);
                double intRsrvStr2 = Double.parseDouble(new BigDecimal(dbTempRsrvStr2).toString());//本次需要分配的总流量数
                double intAllFee = Double.parseDouble(allFeeStr);//集团产品分配到总流量
                //集团产品的总流量不足以分配时，拦截提示
                if(intRsrvStr2 > intAllFee){
                    CSAppException.apperr(GrpException.CRM_GRP_859,grpSn);
                }
                
                boolean leaveFeeFlag = false;//每次分配后，是否有不足分配的剩余流量,true:有,false:没有
                
                for (int i = 0; i < infoSets.size(); i++){
                    
                    if(intRsrvStr2 <= 0){//如果值是0或者已经分配完了则直接跳出循环
                        break;
                    }
                    
                    IData infoData = infoSets.getData(i);
                    
                    if(IDataUtil.isNotEmpty(infoData)){
                        
                        String limitFeeStr = infoData.getString("LIMIT_FEE","0");//总分配的流量数
                        String consumeFeeStr = infoData.getString("CONSUME_FEE","0");//已经分配的流量总数
                        
                        double intRsrvStr2Main = -1;//记录每次分配的流量数,以便新增TF_F_USER_GRP_GFFF_SUB表记录
                        double limitFee = Double.parseDouble(limitFeeStr);
                        double consumeFee = Double.parseDouble(consumeFeeStr);
                        double consumeFeeAll = consumeFee;
                        
                        if(limitFee <= 0){//流量总数是
                            CSAppException.apperr(GrpException.CRM_GRP_858,grpSn);
                        }
                        
                        if((limitFee - consumeFee) <= 0){//这个流量已经分配完了，则取下一条分配流量数
                            continue;
                        }
                        
                        if((limitFee - consumeFee) < intRsrvStr2){//这个流量不够分配时,取这个流量的分配后剩余的流量，做下一轮分配
                            //CSAppException.apperr(GrpException.CRM_GRP_859,grpSn);
                            intRsrvStr2 = intRsrvStr2 - (limitFee - consumeFee);//不够分配，计算本次分配后剩余多少流量
                            consumeFeeAll = consumeFeeAll + (limitFee - consumeFee);
                            intRsrvStr2Main = limitFee - consumeFee;
                            leaveFeeFlag = true;
                            
                        }
                        else if((limitFee - consumeFee) >= intRsrvStr2){//这个流量分配刚好够分配的次数,则下一条不取分配流量数
                        //if(((limitFee - consumeFee) >= intRsrvStr2) && leaveFeeFlag){//这个流量分配刚好够分配的次数,则下一条不取分配流量数
                            //不做处理
                            
                            consumeFeeAll = consumeFeeAll + intRsrvStr2;
                            intRsrvStr2Main = intRsrvStr2;
                            intRsrvStr2 = intRsrvStr2 - intRsrvStr2;
                            leaveFeeFlag = true;
                        }
                        
                        
                        if(leaveFeeFlag && intRsrvStr2Main >= 0){
                            IData data = new DataMap();
                            data.put("USER_ID", grpUserId);
                            data.put("INST_ID", infoData.getString("INST_ID"));
                            data.put("CONSUME_FEE", consumeFeeAll);
                            data.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            
                            boolean resultFlag = Dao.save("TF_F_USER_GRP_GFFF", data,new String[]{"USER_ID","INST_ID"});
                            
                            if(!resultFlag){
                                CSAppException.apperr(GrpException.CRM_GRP_860,serialNumberB);
                            }
                            
                            
                            IData param = new DataMap();
                            param.put("PARTITION_ID",  grpUserId.substring(grpUserId.length() - 4));
                            param.put("USER_ID_A", grpUserId);//分配的集团产品编码的user_id
                            param.put("SERIAL_NUMBER", grpSn);//分配集团产品编码
                            param.put("USER_ID_B", userId);//被分配的集团产品编码的user_id
                            param.put("SERIAL_NUMBER_B", serialNumber);//被分配的集团产品编码
                            param.put("ROLE_CODE", "0");
                            param.put("CONSUME_FEE", intRsrvStr2Main);
                            param.put("START_DATE", SysDateMgr.getSysTime());
                            param.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                            
                            boolean resultInsert = Dao.insert("TF_F_USER_GRP_GFFF_SUB", param);
                            
                            
                            if(!resultInsert){
                                CSAppException.apperr(GrpException.CRM_GRP_861);
                            }
                        }
                    }
                    
                }
                
            }
           
        }
    }
}
