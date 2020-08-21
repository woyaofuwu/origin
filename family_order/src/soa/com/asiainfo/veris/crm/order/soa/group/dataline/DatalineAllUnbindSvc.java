
package com.asiainfo.veris.crm.order.soa.group.dataline;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDataLineAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;



public class DatalineAllUnbindSvc extends CSBizService
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * VOIP专线（专网专线）解绑
     * 互联网专线接入（专网专线）解绑
     * 数据专线（专网专线）解绑
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset dataLineAllUnbind(IData data) throws Exception
    {
        IDataset batResultSet = new DatasetList();
        
        //esop订单号
        if(IDataUtil.isEmpty(data)){
            CSAppException.apperr(GrpException.CRM_GRP_887);
        }
        String attrValue = data.getString("ATTR_VALUE","");
        if(StringUtils.isBlank(attrValue)){
            CSAppException.apperr(GrpException.CRM_GRP_888);
        }
        
        IDataset dataLineInfos = TradeDataLineAttrInfoQry.qryTradeDataLineInfoByAttrValue(attrValue);
        if(IDataUtil.isEmpty(dataLineInfos)){
            CSAppException.apperr(GrpException.CRM_GRP_889);
        }
        
        //要解绑的专线信息
        IDataset userDatalines = new DatasetList();
        boolean has7010Data = false;//是否有VOIP专线
        boolean has7011Data = false;//是否有互联网专线
        boolean has7012Data = false;//是否有数据专线
        
        //捞取到数据时，在从主台账历史表获取专线交易类型
        if(IDataUtil.isNotEmpty(dataLineInfos)){
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date nowDate = new Date();
            int nowYear = nowDate.getYear();
            int nowMonth = nowDate.getMonth();
            
            
            //先根据集团用户user_id和product_no(实例号),判断用户是否有有效的数据，只要有一条生效的，即往下走
            for (int i = 0; i < dataLineInfos.size(); i++){
                IData dataLineInfo = dataLineInfos.getData(i);
                String userId = dataLineInfo.getString("USER_ID","");
                String productNo = dataLineInfo.getString("PRODUCT_NO","");
                String productId = dataLineInfo.getString("PRODUCT_ID","");
                
                //集团用户user_id和product_no(实例号)都不为空是，才查询表
                if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(productNo)){
                    
                    // 查询专线信息
                    IData inparam = new DataMap();
                    inparam.put("USER_ID", userId);
                    inparam.put("RSRV_VALUE_CODE", "N001");
                    inparam.put("PRODUCT_NO", productNo);
                    IDataset userOthers = TradeOtherInfoQry.queryUserOtherInfoByUserIdProductNo(inparam);
                    if(IDataUtil.isNotEmpty(userOthers)){
                        
                        IData userOhter = userOthers.getData(0);//结果只能查询出一条专线
                        
                        //判断条件，满足条件的加入到userDatalines
                        if("7010".equals(productId)){//VOIP专线
                            String linePrice = userOhter.getString("RSRV_STR3","0");//专线价格
                            String installationCost = userOhter.getString("RSRV_STR4","0");//安装调试费
                            String oneCost = userOhter.getString("RSRV_STR5","0");//专线一次性通信服务费
                            
                            boolean nowMonthFlag = false;
                            // 安装调试费、一次性费用START_DATE不在当月显示为0
                            String startDate4 = userOhter.getString("START_DATE");
                            int startYear = sdf.parse(startDate4).getYear();
                            int startMonth = sdf.parse(startDate4).getMonth();
                            if (nowMonth == startMonth && nowYear == startYear){
                                nowMonthFlag = true; //当月
                            }
                            
                            if(StringUtils.isNotBlank(linePrice) && !"0".equals(linePrice)){//专线价格不是0,不删除
                                continue;
                                
                            }else if(StringUtils.isNotBlank(linePrice) && "0".equals(linePrice)){
                                
                                //专线价格为0，当安装调试费或者一次性费用不为0,并且是当月录入的，不删除
                                if(nowMonthFlag && 
                                        (!"0".equals(installationCost) || !"0".equals(oneCost))){
                                    continue;
                                    
                                } else {
                                    has7010Data = true;
                                    IData newDataline  = (IData) Clone.deepClone(dataLineInfo);
                                    userDatalines.add(newDataline);
                                }
                            }
                            
                        }else if("7011".equals(productId)){ //互联网专线
                            
                            String linePrice = userOhter.getString("RSRV_STR3","0");//专线价格
                            String installationCost = userOhter.getString("RSRV_STR4","0");//安装调试费
                            String oneCost = userOhter.getString("RSRV_STR5","0");//专线一次性通信服务费
                            String ipCost = userOhter.getString("RSRV_STR6","0");//IP地址使用费
                            
                            boolean nowMonthFlag = false;
                            // 安装调试费、一次性费用START_DATE不在当月显示为0
                            String startDate4 = userOhter.getString("START_DATE");
                            int startYear = sdf.parse(startDate4).getYear();
                            int startMonth = sdf.parse(startDate4).getMonth();
                            if (nowMonth == startMonth && nowYear == startYear){
                                nowMonthFlag = true; //当月
                            }
                            
                            if(StringUtils.isNotBlank(linePrice) && !"0".equals(linePrice)){//专线价格不是0,不删除
                                continue;
                                
                            }else if(StringUtils.isNotBlank(linePrice) && "0".equals(linePrice)){
                                
                                //专线价格为0，当安装调试费或者一次性费用不为0,并且是当月录入的，不删除
                                if(nowMonthFlag && 
                                        (!"0".equals(installationCost) || !"0".equals(oneCost) || !"0".equals(ipCost))){
                                    
                                    continue;
                                } else {
                                    has7011Data = true;
                                    IData newDataline  = (IData) Clone.deepClone(dataLineInfo);
                                    userDatalines.add(newDataline);
                                }
                            }
                                                        
                        }else if("7012".equals(productId)){//数据专线
                            
                            String linePrice = userOhter.getString("RSRV_STR3","0");//专线价格
                            String installationCost = userOhter.getString("RSRV_STR4","0");//安装调试费
                            String oneCost = userOhter.getString("RSRV_STR5","0");//专线一次性通信服务费
                            
                            boolean nowMonthFlag = false;
                            // 安装调试费、一次性费用START_DATE不在当月显示为0
                            String startDate4 = userOhter.getString("START_DATE");
                            int startYear = sdf.parse(startDate4).getYear();
                            int startMonth = sdf.parse(startDate4).getMonth();
                            if (nowMonth == startMonth && nowYear == startYear){
                                nowMonthFlag = true; //当月
                            }
                            
                            if(StringUtils.isNotBlank(linePrice) && !"0".equals(linePrice)){//专线价格不是0,不删除
                                continue;
                            }else if(StringUtils.isNotBlank(linePrice) && "0".equals(linePrice)){
                                
                                //专线价格为0，当安装调试费或者一次性费用不为0,并且是当月录入的，不删除
                                if(nowMonthFlag && 
                                        (!"0".equals(installationCost) || !"0".equals(oneCost))){
                                    continue;
                                    
                                } else {
                                    has7012Data = true;
                                    IData newDataline  = (IData) Clone.deepClone(dataLineInfo);
                                    userDatalines.add(newDataline);
                                }
                            }
                            
                        } else {
                            
                            CSAppException.apperr(GrpException.CRM_GRP_893);
                        }
                        
                    }
                    
                }
               
            }
            
            //用户没有了有效的专线时，则拦截提示
            if(IDataUtil.isEmpty(userDatalines)){
                CSAppException.apperr(GrpException.CRM_GRP_892,attrValue);
            }
            
            
            if(has7010Data){//VOIP专线
                String userId = userDatalines.getData(0).getString("USER_ID");
                IData datas = new DataMap();
                datas.put("DataLine", userDatalines);
                datas.put("GPR_USER_ID", userId);
                datas.put(Route.ROUTE_EPARCHY_CODE, "0898");
                
                batResultSet = CSAppCall.call("SS.GrpVoipDatalineUnbindSvc.voipDatalineCrtTrade", datas);
                
            } else if(has7011Data){//互联网专线
                String userId = userDatalines.getData(0).getString("USER_ID");
                IData datas = new DataMap();
                datas.put("DataLine", userDatalines);
                datas.put("GPR_USER_ID", userId);
                datas.put(Route.ROUTE_EPARCHY_CODE, "0898");
                
                batResultSet = CSAppCall.call("SS.GrpNetinDatalineUnbindSvc.netinDatalineCrtTrade", datas);
                
            } else if(has7012Data){//数据专线
                String userId = userDatalines.getData(0).getString("USER_ID");
                IData datas = new DataMap();
                datas.put("DataLine", userDatalines);
                datas.put("GPR_USER_ID", userId);
                datas.put(Route.ROUTE_EPARCHY_CODE, "0898");
                
                batResultSet = CSAppCall.call("SS.GrpUserDatalineUnbindSvc.grpUserDatalineCrtTrade", datas);
                
            }
            
            
        }
        
        
        return batResultSet;
    }
}                      