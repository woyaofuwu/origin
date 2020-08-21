package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * 1）	当变更为非云融合套餐时，要截止用户原来绑定的减免优惠（个人宽带减免优惠或商务宽带减免优惠）；
 * 2）	当变更为其他云融合套餐时，要截止用户原来绑定的减免优惠（个人宽带减免优惠或商务宽带减免优惠），绑定新的减免优惠（个人宽带减免优惠或商务宽带减免优惠）；
 * @author Administrator
 *
 */
public class DealKDFeeDiscntAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		ChangeProductReqData changeProductRD=(ChangeProductReqData)btd.getRD();
		
		UcaData uca=changeProductRD.getUca();
		String userId = uca.getUserId();
		String serialNumber = uca.getSerialNumber();
		
		//获取主产品台账数据
		List<ProductTradeData> proTrade=btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
		List<DiscntTradeData> disTrade=btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		String addproYunRongHe = "0";
		String delproYunRongHe = "0";
		String addstartDate = "";
		String addendDate = "";
		String delstartDate = "";
		String delendDate = "";
		if(ArrayUtil.isNotEmpty(proTrade)){
			for(int i =0; i < proTrade.size(); i++){
		        String modifyTag = proTrade.get(i).getModifyTag();
				//新增
				if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
					String productId = proTrade.get(i).getProductId();
					addstartDate = proTrade.get(i).getStartDate();
			        addendDate = proTrade.get(i).getEndDate();
					if("84018913".equals(productId) || "84018914".equals(productId)){
						addproYunRongHe = "500";
					}else if("84018915".equals(productId)){
						addproYunRongHe = "1000";
					}else{
						addproYunRongHe = "ADDQT";
					}
				}
				//删除
				if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
					String productId = proTrade.get(i).getProductId();
					delstartDate = proTrade.get(i).getStartDate();
			        delendDate = proTrade.get(i).getEndDate();
					if("84018913".equals(productId) || "84018914".equals(productId)){
						delproYunRongHe = "500";
					}else if("84018915".equals(productId)){
						delproYunRongHe = "1000";
					}else{
						delproYunRongHe = "DELQT";
					}
				}
			}
		}
		IDataset ids = getRelationByUIDB(userId,serialNumber);////查询该用户是否是办理了商务宽带代付关系
		
		//1）当变更为云融合套餐时，要绑定用户的减免优惠（个人宽带减免优惠或商务宽带减免优惠）；
		if("DELQT".equals(delproYunRongHe) && "500".equals(addproYunRongHe)){//500M
			if(IDataUtil.isNotEmpty(ids)){
				IDataset userDiscs = UserDiscntInfoQry.getAllDiscntByUD(userId, "84018966");
	            if (IDataUtil.isEmpty(userDiscs)) {
	            	addDiscnt(btd, uca, "84018966", addstartDate, addendDate);
	            }
	            if(ArrayUtil.isNotEmpty(disTrade)){
	            	Iterator<DiscntTradeData>  iterator = disTrade.iterator();
	            	while (iterator.hasNext()) {
						String deldiscntCode = iterator.next().getDiscntCode();
						if(deldiscntCode.equals("84018965")){
							iterator.remove();
						}
					}
	            }
			}
		}
		if("DELQT".equals(delproYunRongHe) && "1000".equals(addproYunRongHe)){//1000M
			if(IDataUtil.isNotEmpty(ids)){
				IDataset userDiscs = UserDiscntInfoQry.getAllDiscntByUD(userId, "84018968");
	            if (IDataUtil.isEmpty(userDiscs)) {
	            	addDiscnt(btd, uca, "84018968", addstartDate, addendDate);
	            }
	            if(ArrayUtil.isNotEmpty(disTrade)){
	            	Iterator<DiscntTradeData>  iterator = disTrade.iterator();
	            	while (iterator.hasNext()) {
						String deldiscntCode = iterator.next().getDiscntCode();
						if(deldiscntCode.equals("84018967")){
							iterator.remove();
						}
					}
	            }
			}
		}
		
		//2）当变更为非云融合套餐时，要截止用户原来绑定的减免优惠（个人宽带减免优惠或商务宽带减免优惠）；
		if("ADDQT".equals(addproYunRongHe) && "500".equals(delproYunRongHe)){
			endDiscnt(btd, uca, "84018965,84018966", delendDate);
		}
		if("ADDQT".equals(addproYunRongHe) && "1000".equals(delproYunRongHe)){
			endDiscnt(btd, uca, "84018967,84018968", delendDate);
		}
		//3）当变更为其他云融合套餐时，要截止用户原来绑定的减免优惠（个人宽带减免优惠或商务宽带减免优惠），绑定新的减免优惠（个人宽带减免优惠或商务宽带减免优惠）；
		if("500".equals(addproYunRongHe) && "1000".equals(delproYunRongHe)){//1000M>>500M
			endDiscnt(btd, uca, "84018967,84018968", delendDate);
			if(IDataUtil.isNotEmpty(ids)){
				IDataset userDiscs = UserDiscntInfoQry.getAllDiscntByUD(userId, "84018966");
	            if (IDataUtil.isEmpty(userDiscs)) {
	            	addDiscnt(btd, uca, "84018966", addstartDate, addendDate);
	            }
	            if(ArrayUtil.isNotEmpty(disTrade)){
	            	Iterator<DiscntTradeData>  iterator = disTrade.iterator();
	            	while (iterator.hasNext()) {
						String deldiscntCode = iterator.next().getDiscntCode();
						if(deldiscntCode.equals("84018965")){
							iterator.remove();
						}
					}
	            }
			}
		}
		if("1000".equals(addproYunRongHe) && "500".equals(delproYunRongHe)){//500M>>1000M
			endDiscnt(btd, uca, "84018965,84018966", delendDate);
			if(IDataUtil.isNotEmpty(ids)){
				IDataset userDiscs = UserDiscntInfoQry.getAllDiscntByUD(userId, "84018968");
	            if (IDataUtil.isEmpty(userDiscs)) {
	            	addDiscnt(btd, uca, "84018968", addstartDate, addendDate);
	            }
	            if(ArrayUtil.isNotEmpty(disTrade)){
	            	Iterator<DiscntTradeData>  iterator = disTrade.iterator();
	            	while (iterator.hasNext()) {
						String deldiscntCode = iterator.next().getDiscntCode();
						if(deldiscntCode.equals("84018967")){
							iterator.remove();
						}
					}
	            }
			}
		}
		
	}
	
	/**
	 * 添加优惠
	 * @param btd
	 * @param uca
	 * @throws Exception
	 */
    private void addDiscnt(BusiTradeData btd, UcaData uca,String discntCode, String startDate, String endDate)throws Exception{
    	  DiscntTradeData newDiscnt = new DiscntTradeData();
		  newDiscnt.setUserId(uca.getUserId());
	      newDiscnt.setProductId("-1");
	      newDiscnt.setPackageId("-1");
	      newDiscnt.setElementId(discntCode);
	      newDiscnt.setInstId(SeqMgr.getInstId());
	      newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
	      newDiscnt.setSpecTag("0");
	      newDiscnt.setStartDate(startDate);
	      newDiscnt.setEndDate(endDate);
	      newDiscnt.setRemark("办理云网融合套餐时，后台同时绑定的减免优惠（个人宽带减免优惠或商务宽带减免优惠）！");
	      btd.add(uca.getSerialNumber(), newDiscnt);
    }
    /**
	 * 结束优惠
	 * @param btd
	 * @param uca
	 * @throws Exception
	 */
    private void endDiscnt(BusiTradeData btd, UcaData uca, String discntCodeList, String endDate)throws Exception{
        List<DiscntTradeData> discntData = uca.getUserDiscntsByDiscntCodeArray(discntCodeList);
        if (ArrayUtil.isNotEmpty(discntData)){
        	for (int j =0; j < discntData.size(); j++){
	        	DiscntTradeData delDiscntTD = discntData.get(j).clone();
	        	delDiscntTD.setEndDate(endDate);
	        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
	        	delDiscntTD.setRemark("当变更为非云融合套餐时，要截止用户原来绑定的减免优惠（个人宽带减免优惠或商务宽带减免优惠）！");
	        	btd.add(uca.getSerialNumber(), delDiscntTD);
        	}
            
        }
    }
    
    public IDataset getRelationByUIDB(String userIdB, String serialNumberB) throws Exception
    {
    	StringBuilder sql = new StringBuilder(1000);
		sql.append(" select t.* from tf_f_relation_uu t  ");
		sql.append(" where t.user_id_b=:USER_ID_B");
		sql.append(" and t.relation_type_code='57'");
		sql.append(" and t.SERIAL_NUMBER_B=:SERIAL_NUMBER_B");
		sql.append(" and sysdate between t.start_date and t.end_date ");
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("SERIAL_NUMBER_B", serialNumberB);
		return Dao.qryBySql(sql, param);

    }
}
