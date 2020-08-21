
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

/**
 * //有吉祥号码开户默认绑定优惠,"优惠编码|月份"
 * 
 * @author sunxin
 */
public class DealForBindDiscnt implements ITradeAction
{

    // 准备优惠台帐数据 sunxin
    private void dealForDiscnt(BusiTradeData btd, UcaData uca, IData param) throws Exception
    {

        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(param.getString("USER_ID"));
        newDiscnt.setProductId(param.getString("PRODUCT_ID"));
        newDiscnt.setPackageId(param.getString("PACKAGE_ID"));
        newDiscnt.setElementId(param.getString("DISCNT_CODE"));
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("0");
        newDiscnt.setStartDate(param.getString("START_DATE"));
        newDiscnt.setEndDate(param.getString("END_DATE"));
        newDiscnt.setRemark("吉祥号码开户默认绑定");
        btd.add(uca.getSerialNumber(), newDiscnt);

        List<ProductTradeData> productTrade = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        
        if(productTrade != null && productTrade.size() > 0)
        {
            for(ProductTradeData product : productTrade)
            {
                if(BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()) && "1".equals(product.getMainTag()))
                {
                	//此处prodId与packId为-1，需要手动新增OfferRel
                    OfferRelTradeData offerRel = new OfferRelTradeData();
            		offerRel.setInstId(SeqMgr.getInstId());
            		offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
            		offerRel.setOfferInsId(product.getInstId());
            		offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            		offerRel.setOfferCode(product.getProductId());
            		offerRel.setUserId(product.getUserId());
            		offerRel.setRelOfferInsId(newDiscnt.getInstId());
            		offerRel.setRelOfferCode(newDiscnt.getElementId());
            		offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
            		offerRel.setRelType(BofConst.OFFER_REL_TYPE_LINK);//连带关系
            		offerRel.setStartDate(newDiscnt.getStartDate());
            		offerRel.setEndDate(newDiscnt.getEndDate());
            		offerRel.setRelUserId(newDiscnt.getUserId());
            		offerRel.setGroupId("-1");
            		btd.add(uca.getSerialNumber(), offerRel);
                }
            }
        }
    }

    public void executeAction(BusiTradeData btd) throws Exception
    {

        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String UserId = createPersonUserRD.getUca().getUser().getUserId();
        //吉祥号码必须配置绑定优惠
        IDataset dataSet = ResCall.getMphonecodeInfo(createPersonUserRD.getUca().getSerialNumber());
		if (IDataUtil.isNotEmpty(dataSet))
		{
			String beautifulTag = dataSet.first().getString("BEAUTIFUAL_TAG");
			if (StringUtils.equals("1", beautifulTag) && StringUtils.isBlank(createPersonUserRD.getBindDefaultDiscnt()))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"吉祥号码必须配置绑定优惠");
			}
		}
        if (StringUtils.isNotBlank(createPersonUserRD.getBindDefaultDiscnt())&&StringUtils.equals("0", createPersonUserRD.getBindSaleTag()))
        {
            String bindDftDiscnt = createPersonUserRD.getBindDefaultDiscnt().trim();
            String bindDiscntCode = bindDftDiscnt.split("\\|")[0];
            String bindMonth = bindDftDiscnt.split("\\|")[1];
            IData discntConfig = UDiscntInfoQry.getDiscntInfoByPk(bindDiscntCode);
            if (IDataUtil.isEmpty(discntConfig))
			{
				CSAppException.apperr(ElementException.CRM_ELEMENT_140,bindDiscntCode);
			}
            int month = Integer.parseInt(bindMonth) + 1;// 套餐立即生效，所以办理当月不算，算偏移月份时多加一个月
            IData tempPage0 = new DataMap();
            IData tempPage1 = new DataMap();

            tempPage0.put("USER_ID", UserId);
            tempPage0.put("PRODUCT_ID", "-1");
            tempPage0.put("PACKAGE_ID", "-1");
            tempPage0.put("DISCNT_CODE", "6067");// 吉祥号码承诺在网套餐
            tempPage0.put("START_DATE", SysDateMgr.getSysTime());
            tempPage0.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            dealForDiscnt(btd, createPersonUserRD.getUca(), tempPage0);

            tempPage1.put("USER_ID", UserId);
            tempPage1.put("PRODUCT_ID", "-1");
            tempPage1.put("PACKAGE_ID", "-1");
            tempPage1.put("DISCNT_CODE", bindDiscntCode);// 吉祥号码承诺不销户，不过户套餐
            tempPage1.put("START_DATE", SysDateMgr.getSysTime());
            tempPage1.put("END_DATE", SysDateMgr.endDate(SysDateMgr.getSysTime(), "1", "", month + "", "3"));
            dealForDiscnt(btd, createPersonUserRD.getUca(), tempPage1);

        }

    }

}
