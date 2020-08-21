package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 集团业务一些规则配置之外的校验
 * @author chenzg
 * @date 2016-11-04
 */
public class GroupBizNoRuleCheckUtil{
    /**
     * M2M-M2M（行业应用卡）-校验所选优惠是否有折扣权限
     * @param elementParams
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-11-4
     */
    public static void checkM2MDiscntAttrPriv(IDataset elementParams) throws Exception{
        if(IDataUtil.isEmpty(elementParams)){
            return;
        }
        String staffId = CSBizBean.getVisit().getStaffId(); 
        for(int i=0,size=elementParams.size();i<size;i++){
            IData each = elementParams.getData(i);
            String elementId = each.getString("ELEMENT_ID", "");
            String modifyTag = each.getString("MODIFY_TAG", "");
            String instType = each.getString("INST_TYPE", "");
            String attrCode =  each.getString("ATTR_CODE","");
            String attrValue =  each.getString("ATTR_VALUE","");
            if(!"D".equals(instType)){
                continue;
            }
            //新增或者修改优惠时
            if("0".equals(modifyTag) || "2".equals(modifyTag)){
            	//折扣不是100，说明有折扣，需要判工号有没有折扣权限[GRP_VPEN_DISCNT_PRV]
                if("7050".equals(attrCode)&&!"100".equals(attrValue)){
                    //捞公共参数判断该优惠是否要判断折扣权限
                    IDataset commParams = CommparaInfoQry.getCommparaInfoBy5("CGM", "8803", "M2M", elementId, "0898", null);
                    if(IDataUtil.isNotEmpty(commParams)){
                        //判断工号是否有折扣权限
                        if(!StaffPrivUtil.isFuncDataPriv(staffId, "GRP_VPEN_DISCNT_PRV")){
                            double attrInt =  Double.parseDouble(attrValue)/10;
                            CSAppException.apperr(GrpException.CRM_GRP_849, attrInt);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 机器卡产品套餐-校验所选优惠是否有折扣权限
     * @param elementParams
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-11-4
     */
    public static void checkWLWGDiscntAttrPriv(IDataset elementParams) throws Exception{
        if(IDataUtil.isEmpty(elementParams)){
            return;
        }
        String staffId = CSBizBean.getVisit().getStaffId(); 
        for(int i=0,size=elementParams.size();i<size;i++){
            IData each = elementParams.getData(i);
            String elementId = each.getString("ELEMENT_ID", "");
            String modifyTag = each.getString("MODIFY_TAG", "");
            String instType = each.getString("INST_TYPE", "");
            String attrCode =  each.getString("ATTR_CODE","");
            String attrValue =  each.getString("ATTR_VALUE","");
            if(!"D".equals(instType)){
                continue;
            }
            if("0".equals(modifyTag) || "2".equals(modifyTag)){
            	//折扣不是100，说明有折扣，需要判工号有没有折扣权限[GRP_VPEN_DISCNT_PRV]
                if("20120706".equals(attrCode)&&!"100".equals(attrValue)){
                    //捞公共参数判断该优惠是否要判断折扣权限
                    IDataset commParams = CommparaInfoQry.getCommparaInfoBy5("CGM", "8803", "WLWG", elementId, "0898", null);
                    if(IDataUtil.isNotEmpty(commParams)){
                        //判断工号是否有折扣权限
                        if(!StaffPrivUtil.isFuncDataPriv(staffId, "GRP_WLWG_DISCNT_PRV")){
                            double attrInt =  Double.parseDouble(attrValue)/10;
                            CSAppException.apperr(GrpException.CRM_GRP_849, attrInt);
                        }
                    }
                }
            }
            
        }
    }
    /**
     * 集团工作套餐-校验所选优惠是否有折扣权限
     * @param elementParams
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-11-11
     */
    public static String checkGPWPDiscntAttrPriv(IData element) throws Exception{
    	String checkInfo = "";
        if(IDataUtil.isEmpty(element)){
            return "";
        }
        String staffId = CSBizBean.getVisit().getStaffId(); 
        String elementId = element.getString("ELEMENT_ID", "");
        String modifyTag = element.getString("MODIFY_TAG", "");
        String eleTyeCode = element.getString("ELEMENT_TYPE_CODE", "");
        if(!"D".equals(eleTyeCode)){
            return "";
        }
        if("0".equals(modifyTag) || "2".equals(modifyTag)){
        	//捞公共参数判断该优惠是否要判断折扣权限
            IDataset commParams = CommparaInfoQry.getCommparaInfoBy5("CGM", "8803", "GPWP", elementId, "0898", null);
            if(IDataUtil.isNotEmpty(commParams)){
            	IDataset attrDs = element.getDataset("ATTR_PARAM");
                if(IDataUtil.isNotEmpty(attrDs)){
                    for(int iattr=0;iattr<attrDs.size();iattr++){
                        IData attrData = attrDs.getData(iattr);
                        String attrCode = attrData.getString("ATTR_CODE", "");
                        //打折率属性编码
                        if("114485".equals(attrCode)){
                            int attrVal = attrData.getInt("ATTR_VALUE");
                            if(attrVal!=100){
                                //数据权限(域权限) GPWP_DISCNT_ZK_PRV 集团工作套餐折扣权限
                                if(!StaffPrivUtil.isPriv(staffId, "GPWP_DISCNT_ZK_PRV", StaffPrivUtil.PRIV_TYPE_FIELD)){
                                	String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
                                	checkInfo = "您没有集团工作套餐["+discntName+"]["+elementId+"]的打折权限，您当前选择的折扣率是["+attrVal+"%]！";
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return checkInfo;
    }
}
