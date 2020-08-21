package com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.requsetdata.OceanFrontReqData;

/**
 * REQ201805160025_2018年海洋通业务办理开发需求
 * <br/>
 * 4、校验用户是否开通了海洋通业务，没有开通则不能办理本业务.
 * @author zhuoyingzhi
 * @date 20180601
 */
public class OceanFrontAction implements ITradeAction {
    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
        String tradeTypeCode = btd.getTradeTypeCode();
        OceanFrontReqData rd = (OceanFrontReqData)btd.getRD();
        UcaData uca = rd.getUca();
        if ("9830".equals(tradeTypeCode)||"9831".equals(tradeTypeCode)) {
        		//海洋通报停：9830    海洋通报开：9831
        		String userId=uca.getUserId();
        	  //判断用户是否办理了  海洋通  业务
        	  IDataset userOther=UserOtherInfoQry.getUserOther(userId, "HYT");
        	   if(IDataUtil.isEmpty(userOther)){
                   CSAppException.apperr(CrmCommException.CRM_COMM_888,"客户没有开通海洋通业务,不能办理本业务.");
        	   }
        	   IData otherInfo=userOther.getData(0);
        	    //报开：1     报停：0
        	   	String openStopType=otherInfo.getString("RSRV_STR5", "");
        	   
 		       //是否船东   1：船主     0：船员
 		       String isShipOwner=otherInfo.getString("RSRV_STR2", "");
 		       if(!"1".equals(isShipOwner)){
            	   CSAppException.apperr(CrmCommException.CRM_COMM_888,
    			   "海洋通报停/报开,业务受理前条件判断-非海洋通船东,不能办理海洋通报停/报开业务!");
 		       }
 		       
               if("0".equals(openStopType)&&"9830".equals(tradeTypeCode)){
            	   	//海洋通报停：9830
            	   CSAppException.apperr(CrmCommException.CRM_COMM_888,
            			   "海洋通报停,业务受理前条件判断-非海洋通报开状态,不能办理海洋通报停业务!");
               }
               
               if("1".equals(openStopType)&&"9831".equals(tradeTypeCode)){
           	   	//海洋通报开：9831
            	   CSAppException.apperr(CrmCommException.CRM_COMM_888,
            			   "海洋通报开,业务受理前条件判断-非海洋通报停状态,不能办理海洋通报开业务!");
              }
        } 
    }
}
