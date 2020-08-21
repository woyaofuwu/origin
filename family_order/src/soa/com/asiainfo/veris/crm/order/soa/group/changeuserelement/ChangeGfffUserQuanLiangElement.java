
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

/**
 * 集团流量自由充产品、指定用户，全量统付（流量池）
 * 
 * @author 
 */
public class ChangeGfffUserQuanLiangElement extends ChangeUserElement
{
    
    public ChangeGfffUserQuanLiangElement()
    {
    }


    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        
        infoRegDataAttr();
        checkDiscount();
    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoUserOther();
    }
    
    /**
     * 
     * @throws Exception
     */

    private void infoRegDataAttr() throws Exception
    {
        boolean firstElement = false;
        boolean needModify = false;
        String firstDiscntCode = "";
        
        //对只变更优惠折扣的，原来的折扣截止到上月底、新的从现在时间开始算起到2050
        IDataset paramDatas = reqData.cd.getElementParam();
        if(IDataUtil.isNotEmpty(paramDatas)){
            for(int i=0; i < paramDatas.size(); i++){
                IData paramData = paramDatas.getData(i);
                if(IDataUtil.isNotEmpty(paramData)){
                   String discntCode = paramData.getString("ELEMENT_ID","");
                   String attrCode =  paramData.getString("ATTR_CODE","");
                   //String attrValue =  paramData.getString("ATTR_VALUE","");
                   //String attrModifyTag = paramData.getString("MODIFY_TAG","");
                   
                   if(StringUtils.isNotEmpty(attrCode) && "7342".equals(attrCode) && !firstElement){
                       firstElement = true;
                       firstDiscntCode = discntCode;
                       continue;
                   }
                   
                   //需要做修改一下时间
                   if(StringUtils.isNotEmpty(attrCode) && "7342".equals(attrCode) && firstElement){
                       if(firstDiscntCode.equals(discntCode)){
                           needModify = true;
                       }
                   }
                }
            }
            for(int i=0; i < paramDatas.size(); i++){
                IData paramData = paramDatas.getData(i);
                if(IDataUtil.isNotEmpty(paramData)){
                   //String discntCode = paramData.getString("ELEMENT_ID","");
                   String attrCode =  paramData.getString("ATTR_CODE","");
                   //String attrValue =  paramData.getString("ATTR_VALUE","");
                   String attrModifyTag = paramData.getString("MODIFY_TAG","");
                   
                   if(needModify){
                       if(StringUtils.isNotEmpty(attrCode) && "7342".equals(attrCode) &&
                               attrModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue())){
                           
                           //paramData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(-1));
                           paramData.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                           
                       } else if(StringUtils.isNotEmpty(attrCode) && "7342".equals(attrCode) &&
                               attrModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                           
                           paramData.put("START_DATE", getAcceptTime());
                       }
                   }       
                      //公交WIFI套餐的流量总额要以byte保存到td_f_trade_attr，以同步给账务
                   if(StringUtils.isNotEmpty(attrCode) && "7362".equals(attrCode)){
                       String attrInitValueStr = paramData.getString("ATTR_VALUE","");
                       String attrInitValue ="";
                       if( attrModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){ 
                           
                           attrInitValue = String.valueOf(Long.parseLong(attrInitValueStr)*1024*1024);  
                           
                       }else if ( attrModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue())){
                           
                           attrInitValue = attrInitValueStr;  
                           
                       }
                       paramData.put("ATTR_VALUE", attrInitValue);
                   }
                   
                }
            }
        }
    }
    
    private void checkDiscount() throws Exception{
        String staffId = CSBizBean.getVisit().getStaffId(); ;
        IDataset paramDatas = reqData.cd.getElementParam();
        if(IDataUtil.isNotEmpty(paramDatas)){
            for(int i=0; i < paramDatas.size(); i++){
                IData paramData = paramDatas.getData(i);
                if(IDataUtil.isNotEmpty(paramData)){
                   String attrCode =  paramData.getString("ATTR_CODE","");
                   String attrValue =  paramData.getString("ATTR_VALUE","");
                   String attrModifyTag = paramData.getString("MODIFY_TAG","");
                   if(StringUtils.isNotEmpty(attrCode) && ("7360".equals(attrCode))&&attrModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                       //优惠折扣率的全部权限,不校验折扣率
                       if(StaffPrivUtil.isFuncDataPriv(staffId, "GROUPUSER_ALL_GFSP_PRV")){
                           
                       } else if(StaffPrivUtil.isFuncDataPriv(staffId, "GROUPUSER_HALF_GFSP_PRV")){
                           IDataset valueInfos = CommparaInfoQry.getCommparaInfoBy5("CGM","7340","GRP_HALF_GFSP",attrValue,"0898",null);//此处复用了7340的配置
                           if(IDataUtil.isEmpty(valueInfos)){
                               //CSAppException.appError("", "您无权限办理该折扣,您选择的折扣率是" + attrName + ",您只能办理7折及以上折扣!");
                               double attrInt =  Double.parseDouble(attrValue)/10;
                               CSAppException.apperr(GrpException.CRM_GRP_848,attrInt);
                           }
                       } else {
                           if(!"100".equals(attrValue)){
                               //CSAppException.appError("", "您无权限办理该折扣,您选择的折扣率是" + attrName + ",您只能办理10折折扣!");
                               double attrInt =  Double.parseDouble(attrValue)/10;
                               CSAppException.apperr(GrpException.CRM_GRP_849,attrInt);
                           }
                       }
                   }
                }
            }
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    private void infoUserOther() throws Exception{
        
        IDataset svcDatas = reqData.cd.getSvc();
        
        IDataset dataset = new DatasetList();
        
        if(IDataUtil.isNotEmpty(svcDatas)){
            
            for(int i=0; i < svcDatas.size(); i++){
                
                IData svcData = svcDatas.getData(i);
                
                if(IDataUtil.isNotEmpty(svcData)){
                    //String packageId = svcData.getString("PACKAGE_ID","");
                    String eleTypeCode =  svcData.getString("ELEMENT_TYPE_CODE","");
                    String modifyTag =  svcData.getString("MODIFY_TAG","");
                    String elementId =  svcData.getString("ELEMENT_ID","");
                    String startDate =  svcData.getString("START_DATE","");
                    String endDate =  svcData.getString("END_DATE","");
                    
                    if("10008".equals(elementId) && "S".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                        
                        //String groupId = reqData.getUca().getCustGroup().getGroupId();
                        String mgrSn = reqData.getUca().getCustGroup().getGroupMgrSn();//集团联系人号码
                        if(StringUtils.isBlank(mgrSn)){
                            CSAppException.apperr(GrpException.CRM_GRP_854);
                        }
                        
                        IData data = new DataMap();
                        data.put("USER_ID", reqData.getUca().getUserId());
                        data.put("RSRV_VALUE_CODE", "FGPRELAPHONE");
                        data.put("RSRV_VALUE", mgrSn);
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        data.put("START_DATE", startDate);
                        data.put("END_DATE", endDate);
                        data.put("INST_ID", SeqMgr.getInstId());
                        dataset.add(data);
                        
                    } else if("10008".equals(elementId) && "S".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue())){
                        
                        IData inparam = new DataMap();
                        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
                        inparam.put("RSRV_VALUE_CODE", "FGPRELAPHONE");
                        IDataset userOthers = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);
                        
                        if(IDataUtil.isNotEmpty(userOthers)){
                            
                            IData userOther = userOthers.getData(0);
                            userOther.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            userOther.put("END_DATE", endDate);
                            
                            dataset.add(userOther);
                            
                        }
                    } 
                }
            }
            
            super.addTradeOther(dataset);
        }
        
    }
    
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        
        IData data = bizData.getTrade();
        data.put("EXEC_TIME", SysDateMgr.getTheLastTime());
    }
    
    @Override
    protected void chkTradeAfter() throws Exception
    {
        super.chkTradeAfter();
        
        IData orderData = bizData.getOrder();
        orderData.put("EXEC_TIME", SysDateMgr.getTheLastTime());
    }
}
