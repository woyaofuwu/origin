
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * 取消的优惠param_attr=4959的限制条件
 *
 */
public class CannotCancel5GDiscntAction implements ITradeAction
{
	private static Logger logger = Logger.getLogger(CannotCancel5GDiscntAction.class);
    @SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca =btd.getRD().getUca();
    	String serialNumber = uca.getSerialNumber();
		List<String> delDiscntListDiscntList = new ArrayList<String>();
		logger.debug("CannotCancelDiscntAction>>>>>" + serialNumber +"---");
		//如果有优惠变更，将新增的优惠id放到addDiscntList list中
		List<DiscntTradeData> changeDiscnts=btd.get("TF_B_TRADE_DISCNT"); 
		if(changeDiscnts!=null&&changeDiscnts.size()>0){
			for(DiscntTradeData discntTradeData : changeDiscnts){
				if(BofConst.MODIFY_TAG_DEL.equals(discntTradeData.getModifyTag())){
					delDiscntListDiscntList.add(discntTradeData.getDiscntCode());
				}
			}
		}
		
		IData param = new DataMap();
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "4959");
		param.put("PARAM_CODE", "INTERESTS");
		for(String discntCode : delDiscntListDiscntList){
			param.put("PARA_CODE1", discntCode);
			IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
			if(IDataUtil.isNotEmpty(dataset)){
				String userId = uca.getUserId();
				String paraCode2 = dataset.getData(0).getString("PARA_CODE2"); //营销活动productId
				String paraCode3 = dataset.getData(0).getString("PARA_CODE3"); //营销活动packageId
				if(StringUtils.equals(paraCode2, "-1")) {
					CSAppException.apperr(CrmCommException.CRM_COMM_888, "该套餐优惠【"+discntCode+"】不可取消");
				}else {
					IData param1=new DataMap();
					param1.put("USER_ID", userId);
					param1.put("PRODUCT_ID", paraCode2);
					param1.put("PROCESS_TAG", "0");
					IDataset dataset1= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", param1);
					if(IDataUtil.isNotEmpty(dataset1)){
                        if(StringUtils.equals(paraCode3, "-1")) {
                            CSAppException.apperr(CrmCommException.CRM_COMM_888, "该号码办理了【" + paraCode2 + "】营销活动，" + "优惠【" + discntCode + "】不可取消");
                        }else {
                            IData param2=new DataMap();
                            param2.put("USER_ID", userId);
                            param2.put("PRODUCT_ID", paraCode2);
                            param2.put("PACKAGE_ID", paraCode3);
                            param2.put("PROCESS_TAG", "0");
                            IDataset dataset2= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", param2);
                            if(IDataUtil.isNotEmpty(dataset2)){
                                CSAppException.apperr(CrmCommException.CRM_COMM_888, "该号码办理了【" + paraCode2 + "】营销活动下的【" + paraCode3 + "】营销包，" + "优惠【" + discntCode + "】不可取消");
                            }
                        }
					}
				}

			}
		}
		
		
    }
}
