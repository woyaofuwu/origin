
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.elem;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemRelaDealElemInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.RelaElemDealUtil;
import com.asiainfo.veris.crm.order.soa.script.rule.common.SplCheckByRegular;

/**
 * 优惠连带优惠处理
 * 
 * @author Administrator
 */
public class DealElemByElem implements ITradeAction
{

    private void addElemByElem(BusiTradeData btd, ProductModuleTradeData dealElem, List<ProductModuleTradeData> relaElemTDList) throws Exception
    {
        IDataset relaElemInfo = ElemRelaDealElemInfoQry.qryRelaDealInfoByElem(dealElem.getElementType(), dealElem.getElementId(), CSBizBean.getUserEparchyCode());
        for (int i = 0, size = relaElemInfo.size(); i < size; i++)
        {
            dealAdd(btd, dealElem, relaElemInfo.getData(i), relaElemTDList);
        }
    }

    private void dealAdd(BusiTradeData btd, ProductModuleTradeData dealElem, IData relaInfo, List<ProductModuleTradeData> relaElemTDList) throws Exception
    {
        BaseReqData reqData = btd.getRD();
        UcaData uca = reqData.getUca();

        String regExpSn = relaInfo.getString("REGEXP_SN");// 指定号码
        String appointTradeTypeCode = relaInfo.getString("TRADE_TYPE_CODE");// 指定业务类型
        String appointInModeCode = relaInfo.getString("IN_MODE_CODE");// 指定渠道
        String relaElementId = relaInfo.getString("RELA_ELEMENT_ID");
        String relaElementStart = relaInfo.getString("RELA_ELEMENT_START");
        String startOffSet = relaInfo.getString("START_OFFSET");
        String startUnit = relaInfo.getString("START_UNIT");
        String relaElementEnd = relaInfo.getString("RELA_ELEMENT_END");
        String endOffSet = relaInfo.getString("END_OFFSET");
        String endUnit = relaInfo.getString("END_UNIT");
        String relaProductId = relaInfo.getString("RELA_PRODUCT_ID");
        String relaElementTypeCode = relaInfo.getString("RELA_ELEMENT_TYPE_CODE");
        String startDate = dealElem.getStartDate();
        String endDate = dealElem.getEndDate();
        String elementId = dealElem.getElementId();
        String relaPackageId = null;

        // 根据参数配置判断是否需要处理
        if (!RelaElemDealUtil.addCheckByParam(btd, relaInfo))
        {
            return;
        }

        // 计算开始时间
        String tempStartDate = RelaElemDealUtil.getStartDate(btd, relaInfo);
        if (StringUtils.isNotBlank(tempStartDate))
        {
            startDate = tempStartDate;
        }

        // 计算结束时间
        String tempEndDate = RelaElemDealUtil.getEndDate(startDate, btd, relaInfo);
        if (StringUtils.isNotBlank(tempEndDate))
        {
            endDate = tempEndDate;
        }

        // 计算包编码
        // 跟蔡世泳确认了，连带的服务或优惠，要找该元素是否在用户主产品下，如果不在则填-1。营销活动受理时连带出来的元素也一样，填用户主产品的
        // 要让用户可以取消。
        relaProductId = uca.getUserNewMainProductId();
//        IDataset result = PkgElemInfoQry.getElementByPIdElemId(relaProductId, relaElementTypeCode, relaElementId, CSBizBean.getUserEparchyCode());
        IDataset result = UpcCall.getPackageElementInfoByPorductIdElementId(relaProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, relaElementId, relaElementTypeCode);
        if (IDataUtil.isEmpty(result))
        {
            relaPackageId = "-1";
            relaProductId = "-1";
        }
        else
        {
            relaPackageId = result.getData(0).getString("GROUP_ID");
        }

        // 根据用户的资料和台账判断是否需要连带新增
        boolean isFound = false;
        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(relaElementTypeCode))
        {
            List<DiscntTradeData> userRelaElemList = uca.getUserDiscntByDiscntId(relaElementId);
            for (int i = 0, size = userRelaElemList.size(); i < size; i++)
            {
                DiscntTradeData userRelaElem = userRelaElemList.get(i);
                if (!userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
                {
                	String relaElemStartDate = userRelaElem.getStartDate();
                	String relaElemEndDate = userRelaElem.getEndDate();
                	if(endDate.compareTo(relaElemEndDate) > 0)
                	{
                		//如果本次要绑定的元素结束时间大于用户当前该元素的结束时间，则还是需要绑定
                		//如果用户已存在的元素结束时间大于要绑定的元素的开始时间，则元素开始时间改为已存在的元素的结束时间加1秒
                		if(relaElemEndDate.compareTo(startDate) > 0)
                		{
                			startDate = SysDateMgr.addSecond(relaElemEndDate, 1);
                		}
                	}
                	else
                	{
                		isFound = true;
                        break;
                	}
                }
            }
        }
        else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(relaElementTypeCode))
        {
            List<SvcTradeData> userRelaElemList = uca.getUserSvcBySvcId(relaElementId);
            for (int i = 0, size = userRelaElemList.size(); i < size; i++)
            {
                SvcTradeData userRelaElem = userRelaElemList.get(i);
                if (!userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
                {
                	String relaElemStartDate = userRelaElem.getStartDate();
                	String relaElemEndDate = userRelaElem.getEndDate();
                	if(endDate.compareTo(relaElemEndDate) > 0)
                	{
                		//如果本次要绑定的元素结束时间大于用户当前该元素的结束时间，则还是需要绑定
                		//如果用户已存在的元素结束时间大于要绑定的元素的开始时间，则元素开始时间改为已存在的元素的结束时间加1秒
                		if(relaElemEndDate.compareTo(startDate) > 0)
                		{
                			startDate = SysDateMgr.addSecond(relaElemEndDate, 1);
                		}
                	}
                	else
                	{
                		isFound = true;
                        break;
                	}
                }
            }
        }

        // 新增台账
        if (!isFound)
        {
            if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(relaElementTypeCode))
            {
                DiscntTradeData addDiscntTD = new DiscntTradeData();
                addDiscntTD.setUserId(uca.getUserId());
                addDiscntTD.setUserIdA("-1");
                addDiscntTD.setProductId(relaProductId);
                addDiscntTD.setPackageId(relaPackageId);
                addDiscntTD.setElementId(relaElementId);
                addDiscntTD.setStartDate(startDate);
                addDiscntTD.setEndDate(endDate);
                addDiscntTD.setSpecTag("0");
                addDiscntTD.setInstId(SeqMgr.getInstId());
                addDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addDiscntTD.setRemark("连带优惠");
                btd.add(uca.getSerialNumber(), addDiscntTD);
                RelaElemDealUtil.addElemRelaTradeData(btd, dealElem, addDiscntTD);
                relaElemTDList.add(addDiscntTD);
            }
            else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(relaElementTypeCode))
            {
                // 服务处理
                SvcTradeData addSvcTD = new SvcTradeData();
                addSvcTD.setUserId(uca.getUserId());
                addSvcTD.setUserIdA("-1");
                addSvcTD.setProductId(relaProductId);
                addSvcTD.setPackageId(relaPackageId);
                addSvcTD.setElementId(relaElementId);
                addSvcTD.setMainTag("0");
                addSvcTD.setInstId(SeqMgr.getInstId());
                addSvcTD.setStartDate(startDate);
                addSvcTD.setEndDate(endDate);
                addSvcTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addSvcTD.setRemark("连带服务");
                btd.add(uca.getSerialNumber(), addSvcTD);
                RelaElemDealUtil.addElemRelaTradeData(btd, dealElem, addSvcTD);
                relaElemTDList.add(addSvcTD);
            }
        }

        DataBusUtils.addPassiveAddElement(elementId, dealElem.getElementType(), relaElementId, relaElementTypeCode);
    }

    private void dealDel(BusiTradeData btd, ProductModuleTradeData dealElem, IData relaInfo, List<ProductModuleTradeData> relaElemTDList) throws Exception
    {
        BaseReqData reqData = btd.getRD();
        UcaData uca = reqData.getUca();

        String regExpSn = relaInfo.getString("REGEXP_SN");// 指定号码
        String appointTradeTypeCode = relaInfo.getString("TRADE_TYPE_CODE");// 指定业务类型
        String appointInModeCode = relaInfo.getString("IN_MODE_CODE");// 指定渠道
        String relaElementId = relaInfo.getString("RELA_ELEMENT_ID");
        String isRelaDel = relaInfo.getString("IS_RELA_DEL", "0");
        String startDate = dealElem.getStartDate();
        String endDate = dealElem.getEndDate();
        String elementId = dealElem.getElementId();
        String relaElementTypeCode = relaInfo.getString("RELA_ELEMENT_TYPE_CODE");

        if (StringUtils.isNotBlank(regExpSn) && !SplCheckByRegular.matcherText(regExpSn, uca.getSerialNumber()))
        {
            return;
        }

        if ("1".equals(isRelaDel))
        {
            return;
        }

        // 如果被其他元素连带新增了，则不做删除处理
        if (DataBusUtils.isPassiveAddElement(relaElementId, relaElementTypeCode))
        {
            return;
        }

        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(relaElementTypeCode))
        {
            List<DiscntTradeData> userRelaElemList = uca.getUserDiscntByDiscntId(relaElementId);
            for (int i = 0, size = userRelaElemList.size(); i < size; i++)
            {
                DiscntTradeData userRelaElem = userRelaElemList.get(i);
                if (!userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_ADD) && !userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
                {
                    DiscntTradeData delDiscntTD = null;
                    if (userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
                    {
                        delDiscntTD = userRelaElem.clone();
                    }
                    else
                    {
                        delDiscntTD = userRelaElem;
                    }
                    delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    delDiscntTD.setElementId(relaElementId);
                    delDiscntTD.setRemark("连带优惠取消");
                    delDiscntTD.setEndDate(endDate);
                    RelaElemDealUtil.addElemRelaTradeData(btd, dealElem, delDiscntTD);
                    relaElemTDList.add(delDiscntTD);

                    if (userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
                    {
                        btd.add(uca.getSerialNumber(), delDiscntTD);
                    }
                }
            }
        }
        else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(relaElementTypeCode))
        {
            List<SvcTradeData> userRelaElemList = uca.getUserSvcBySvcId(relaElementId);
            for (int i = 0, size = userRelaElemList.size(); i < size; i++)
            {
                SvcTradeData userRelaElem = userRelaElemList.get(i);
                if (!userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_ADD) && !userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
                {
                    SvcTradeData delSvcTD = null;
                    if (userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
                    {
                        // 新增台账
                        delSvcTD = userRelaElem.clone();
                    }
                    else
                    {
                        // 修改原台账
                        delSvcTD = userRelaElem;
                    }

                    delSvcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    delSvcTD.setElementId(relaElementId);
                    delSvcTD.setRemark("连带服务取消");
                    delSvcTD.setEndDate(endDate);
                    RelaElemDealUtil.addElemRelaTradeData(btd, dealElem, delSvcTD);
                    relaElemTDList.add(delSvcTD);

                    if (userRelaElem.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
                    {
                        btd.add(uca.getSerialNumber(), delSvcTD);
                    }
                }
            }
        }
    }

    private void delElemByElem(BusiTradeData btd, ProductModuleTradeData dealElem, List<ProductModuleTradeData> relaElemTDList) throws Exception
    {
        IDataset relaElemInfo = ElemRelaDealElemInfoQry.qryRelaDealInfoByElem(dealElem.getElementType(), dealElem.getElementId(), CSBizBean.getUserEparchyCode());
        for (int i = 0, size = relaElemInfo.size(); i < size; i++)
        {
            dealDel(btd, dealElem, relaElemInfo.getData(i), relaElemTDList);
        }
    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<ProductModuleTradeData> relaElemTDList = new ArrayList<ProductModuleTradeData>();

        
        boolean isCheckRule=true;
        /*
         * 对某些业务进行特殊判断
         */
        List<ProductTradeData> productDatas=btd.get("TF_B_TRADE_PRODUCT");
        if(productDatas!=null&&productDatas.size()>0){
        	
        	String newProductId=null;
        	String oldProductId=null;
        	
        	//如果是产品变呢
        	for(int i=0,size=productDatas.size();i<size;i++){
        		ProductTradeData data=productDatas.get(i);
        		
        		if(data.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
        			newProductId=data.getProductId();
        			
        			if(data.getOldProductId()!=null&&!data.getOldProductId().equals("")){
        				oldProductId=data.getOldProductId();
        			}
        			
        			break;
        		}
        		
        	}
        	
        	//核对是否满足特殊判断
        	if(newProductId!=null&&!newProductId.equals("")&&
        			oldProductId!=null&&!oldProductId.equals("")){
        		IDataset checkDatas=CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3736", "CHAOXIANG_PRODUCT_NO_RELAY",
        				newProductId, oldProductId, "ZZZZ");
        		
        		if(IDataUtil.isNotEmpty(checkDatas)){
        			isCheckRule=false;
        		}
        	}
        	
        }
        
        
        List<DiscntTradeData> discntTDList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        for (int i = 0, size = discntTDList.size(); i < size; i++)
        {
            DiscntTradeData discntTD = discntTDList.get(i);
            if (discntTD.getModifyTag().equals(BofConst.MODIFY_TAG_ADD))
            {
                addElemByElem(btd, discntTD, relaElemTDList);
            }
            else if (discntTD.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
            {
            	
            	if(isCheckRule){
            		delElemByElem(btd, discntTD, relaElemTDList);
            	}  
            }
        }

        // 服务连带处理元素的
        List<SvcTradeData> svcTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        for (int i = 0, size = svcTDList.size(); i < size; i++)
        {
            SvcTradeData svcTD = svcTDList.get(i);
            if (svcTD.getModifyTag().equals(BofConst.MODIFY_TAG_ADD))
            {
                addElemByElem(btd, svcTD, relaElemTDList);
            }
            else if (svcTD.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
            {
                delElemByElem(btd, svcTD, relaElemTDList);
            }
        }

        // 循环relaElemTDList,对之前连带出来的元素再做一次连带处理
        for (int i = 0, size = relaElemTDList.size(); i < size; i++)
        {
            ProductModuleTradeData pmtd = relaElemTDList.get(i);
            if (pmtd.getModifyTag().equals(BofConst.MODIFY_TAG_ADD))
            {
                addElemByElem(btd, pmtd, new ArrayList<ProductModuleTradeData>());
            }
            else if (pmtd.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
            {
                addElemByElem(btd, pmtd, new ArrayList<ProductModuleTradeData>());
            }
        }
    }
}
