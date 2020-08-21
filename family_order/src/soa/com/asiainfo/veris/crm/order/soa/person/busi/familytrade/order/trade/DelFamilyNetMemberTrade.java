
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyMebTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustFamilyMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.DelFamilyNetMemberReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;

public class DelFamilyNetMemberTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        DelFamilyNetMemberReqData reqData = (DelFamilyNetMemberReqData) bd.getRD();
        List<FamilyMemberData> mebUcaList = reqData.getMebUcaList();
        
        String userId = reqData.getUca().getUserId();
        IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, "45", null);
        if (IDataUtil.isEmpty(result))
        {
            // 查询不到成员的UU关系
            CSAppException.apperr(FamilyException.CRM_FAMILY_85);
        }
        IData rela = result.getData(0);
        String userIdA = rela.getString("USER_ID_A");
        String roleCodeB = rela.getString("ROLE_CODE_B");
        reqData.getPageRequestData().put("KEY_ROLE_CODE_B", roleCodeB);
        //IDataset userDiscntList = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
        
        IDataset familyOffers = UpcCall.queryMembOffersByProdMode("05", "D");
		String discntArrays = this.getDiscntArray(familyOffers);
		
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		List<DiscntTradeData> userDiscntList = uca.getUserDiscntsByDiscntCodeArray(discntArrays);  
        
        if (ArrayUtil.isNotEmpty(userDiscntList) )
        {
        	DiscntTradeData userDiscnt = userDiscntList.get(0);
            String discntCode = userDiscnt.getDiscntCode();
        	if( !"3403".equals(discntCode) && !"3404".equals(discntCode) ){
        		IDataset uuRelas = RelaUUInfoQry.qryRelaByUserIdAThisMonth(userIdA, "45");
                Integer uuSizeDel = uuRelas.size() + mebUcaList.size();	//本次删除和本月删除之和不超过9个成员
        		if( uuSizeDel > 9 ){
        			//亲亲网当月累计可删除9个成员，当前已删除*个成员
            		CSAppException.apperr(FamilyException.CRM_FAMILY_112, uuRelas.size());
            	}
        	}
        }
		//List<FamilyMemberData> mebUcaList = reqData.getMebUcaList();
        for (int i = 0, size = mebUcaList.size(); i < size; i++)
        {
            delMeb(bd, mebUcaList.get(i));
        }
    }

    private void delMeb(BusiTradeData bd, FamilyMemberData familyMeb) throws Exception
    {
        DelFamilyNetMemberReqData reqData = (DelFamilyNetMemberReqData) bd.getRD();
        //IData rdData = reqData.getPageRequestData();	//
        
        String sysdate = reqData.getAcceptTime();
        //String effectNow = familyMeb.getEffectNow();
        String strRemark = "删除亲亲网成员";

        UcaData mebUca = familyMeb.getUca();
        mebUca.setAcctTimeEnv();// 分散账期

        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();
        // 校验成员未完工工单限制 ----start----
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("USER_ID", mebUca.getUserId());
        data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
        data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
        data.put("BRAND_CODE", "");
        FamilyTradeHelper.checkMemberUnfinishTrade(data);
        // 校验成员未完工工单限制 ----end----

        IDataset result = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("45", mebUca.getUserId(), "2");
        if (IDataUtil.isEmpty(result))
        {
            // 查询不到成员的UU关系
            CSAppException.apperr(FamilyException.CRM_FAMILY_85);
        }
        IData rela = result.getData(0);
        String userIdA = rela.getString("USER_ID_A");
        IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(virtualUser))
        {
            // 没有找到虚拟用户
            CSAppException.apperr(FamilyException.CRM_FAMILY_53);
        }
        String roleCodeB = bd.getRD().getPageRequestData().getString("KEY_ROLE_CODE_B");
        if( "1".equals(roleCodeB) ){
        	strRemark = "主号码批量删除成员";
        }else if( "2".equals(roleCodeB) ){
        	strRemark = "成员号码退出亲亲网";
        }
        
        RelationTradeData delMebRelTradeData = new RelationTradeData(rela);
        delMebRelTradeData.setRsrvStr2(delMebRelTradeData.getShortCode());
        delMebRelTradeData.setShortCode("");
        delMebRelTradeData.setEndDate(sysdate);
        delMebRelTradeData.setRsrvTag1("1");
        /*if ("YES".equals(effectNow))
        {
            delMebRelTradeData.setEndDate(sysdate);
            delMebRelTradeData.setRsrvTag1("1");
        }
        else
        {
        	// 已经删除的成员不能再删除
            String mebEndDate = rela.getString("END_DATE");
            String lastTime = SysDateMgr.getTheLastTime();// 最大失效时间
            if (!StringUtils.equals(lastTime, mebEndDate))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_102, mebUca.getSerialNumber());
            }
            delMebRelTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
            delMebRelTradeData.setRsrvTag1("0");
        }*/
        delMebRelTradeData.setRsrvStr1(mebUca.getCustomer().getCustName());
        delMebRelTradeData.setRemark(strRemark);
        delMebRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        bd.add(mebUca.getSerialNumber(), delMebRelTradeData);

        // 处理优惠和服务
        List<SvcTradeData> userSvcList = mebUca.getUserSvcs();
        for (int j = 0, listSize = userSvcList.size(); j < listSize; j++)
        {
            SvcTradeData userSvc = userSvcList.get(j);
            if (userIdA.equals(userSvc.getUserIdA()))
            {
                SvcTradeData delSvcTD = userSvc.clone();
                delSvcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                delSvcTD.setRemark(strRemark);
                delSvcTD.setEndDate(sysdate);
                /*if ("YES".equals(effectNow))
                {// add by zhouwu 2014-06-13 20:26:56
                    delSvcTD.setEndDate(sysdate);
                }
                else
                {
                    delSvcTD.setEndDate(SysDateMgr.getLastDateThisMonth());
                }*/
                bd.add(mebUca.getSerialNumber(), delSvcTD);
            }
        }

        List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
        for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
        {
            DiscntTradeData userDiscnt = userDiscntList.get(j);
            if (userIdA.equals(userDiscnt.getUserIdA()))
            {
                DiscntTradeData delDiscntTD = userDiscnt.clone();
                delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                delDiscntTD.setEndDate(sysdate);
                delDiscntTD.setRemark(strRemark);
                /*if ("YES".equals(effectNow))
                {
                    delDiscntTD.setEndDate(sysdate);
                }
                else
                {
                    delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());
                }*/
                bd.add(mebUca.getSerialNumber(), delDiscntTD);
            }
        }

        // 处理可选优惠
        IDataset commparaList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", mebUca.getUserEparchyCode());
        for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
        {
            DiscntTradeData userDiscnt = userDiscntList.get(j);
            for (int k = 0, commparaSize = commparaList.size(); k < commparaSize; k++)
            {
                IData commpara = commparaList.getData(k);
                if (commpara.getString("PARAM_CODE", "").equals(userDiscnt.getElementId()))
                {
                    DiscntTradeData delDiscntTD = userDiscnt.clone();
                    delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    delDiscntTD.setEndDate(sysdate);
                    delDiscntTD.setRemark(strRemark);
                    /*if ("YES".equals(effectNow))
                    {
                        delDiscntTD.setEndDate(sysdate);
                    }
                    else
                    {
                        delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());
                    }*/
                    bd.add(mebUca.getSerialNumber(), delDiscntTD);
                }

                break;
            }
        }

        result = CustFamilyMebInfoQry.getFamilyMem(mebUca.getCustId(), "0");
        if (IDataUtil.isNotEmpty(result))
        {
            CustFamilyMebTradeData custFamilyMebTD = new CustFamilyMebTradeData(result.getData(0));
            custFamilyMebTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            custFamilyMebTD.setRemoveTag("1");
            custFamilyMebTD.setRemoveDate(sysdate);
            /*if ("YES".equals(effectNow))
            {
                custFamilyMebTD.setRemoveDate(sysdate);
            }
            else
            {
                custFamilyMebTD.setRemoveDate(SysDateMgr.getLastDateThisMonth());
            }*/
            custFamilyMebTD.setRemoveStaffId(CSBizBean.getVisit().getStaffId());
            custFamilyMebTD.setRemoveDepartId(CSBizBean.getVisit().getDepartId());

            bd.add(mebUca.getSerialNumber(), custFamilyMebTD);
        }
    }
    
    public String getDiscntArray(IDataset datas) throws Exception
    {
    	String discnts ="";
    	if(IDataUtil.isNotEmpty(datas))
    	{
    		for(int i=0;i<datas.size();i++)
    		{
    			IData data = datas.getData(i);
    			discnts += data.getString("OFFER_CODE") +",";
    		}
    		if(StringUtils.isNotBlank(discnts))
    		{
    			discnts = discnts.substring(0, discnts.length()-1);
    		}
    	}
    	return discnts;
    }
}
