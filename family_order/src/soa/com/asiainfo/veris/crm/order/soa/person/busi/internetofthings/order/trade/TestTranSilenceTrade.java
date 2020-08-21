
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class TestTranSilenceTrade extends BaseTrade implements ITrade
{

    /**
     * 修改普通产品的生效时间。 物联网的普通优惠和服务的生效时间是在测试期产品失效后才能生效
     * 
     * @param btd
     * @throws Exception
     */
    private void changeNormalProductEffectTime(BusiTradeData btd, UcaData uca) throws Exception
    {
        IDataset discntConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9013", "ZZZZ");
        IDataset svcConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9014", "ZZZZ");
        if (IDataUtil.isNotEmpty(discntConfigList))
        {
            for (int i = 0; i < discntConfigList.size(); i++)
            {
                IData discntConfig = discntConfigList.getData(i);
                DiscntTradeData discntData = null;
                // 如果PARA_CODE4配置为1，则该物联网服务和优惠需要在测试期到期后生效
                if ("1".equals(discntConfig.getString("PARA_CODE4")))
                {
                    List<DiscntTradeData> discntDataList = uca.getUserDiscntByDiscntId(discntConfig.getString("PARAM_CODE"));
                    if (CollectionUtils.isNotEmpty(discntDataList))
                    {
                        discntData = discntDataList.get(0);
                    }
                    if (discntData != null)
                    {
                        DiscntTradeData newDiscntData = discntData.clone();
                        newDiscntData.setStartDate(SysDateMgr.getSysTime());
                        newDiscntData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        btd.add(uca.getSerialNumber(), newDiscntData);
                    }
                }
            }
        }

        if (IDataUtil.isNotEmpty(svcConfigList))
        {
            for (int j = 0; j < svcConfigList.size(); j++)
            {
                IData svcConfig = svcConfigList.getData(j);
                SvcTradeData svcData = null;
                // 如果PARA_CODE4配置为1，则该物联网服务和优惠需要在测试期到期后生效
                if ("1".equals(svcConfig.getString("PARA_CODE4")))
                {
                    List<SvcTradeData> svcDataList = uca.getUserSvcBySvcId(svcConfig.getString("PARAM_CODE"));
                    if (CollectionUtils.isNotEmpty(svcDataList))
                    {
                        svcData = svcDataList.get(0);
                    }
                    if (svcData != null)
                    {
                        SvcTradeData newSvcData = svcData.clone();
                        newSvcData.setStartDate(SysDateMgr.getSysTime());
                        newSvcData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        btd.add(uca.getSerialNumber(), newSvcData);
                    }
                }
            }
        }
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();

        UserTradeData userTradeData = uca.getUser();

        if (!"E".equals(userTradeData.getUserTypeCode()))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "非测试期用户不能转为沉默期用户");
        }

        UserTradeData cloneUser = userTradeData.clone();
        cloneUser.setAcctTag("2");// 沉默期用户 出账标识为未出账
        cloneUser.setUserTypeCode("F");// 沉默期用户标识
        cloneUser.setFirstCallTime(null);// 首话时间为空
        cloneUser.setModifyTag(BofConst.MODIFY_TAG_UPD);
        btd.add(uca.getSerialNumber(), cloneUser);

        SvcStateTradeData svcStateTrade = uca.getUserSvcsStateByServiceId("99010000");
        svcStateTrade.setEndDate(SysDateMgr.getSysTime());
        svcStateTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
        btd.add(uca.getSerialNumber(), svcStateTrade);

        SvcStateTradeData newSvcStateTrade = new SvcStateTradeData();
        newSvcStateTrade.setUserId(uca.getUserId());
        newSvcStateTrade.setEndDate(SysDateMgr.getTheLastTime());
        newSvcStateTrade.setInstId(SeqMgr.getInstId());
        newSvcStateTrade.setMainTag("1");
        newSvcStateTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newSvcStateTrade.setRemark("物联网沉默期");
        newSvcStateTrade.setServiceId("99010000");
        newSvcStateTrade.setStartDate(SysDateMgr.getSysTime());
        newSvcStateTrade.setStateCode("S");
        btd.add(uca.getSerialNumber(), newSvcStateTrade);

        // 将测试期优惠立即截止
        String strTestDiscnt = ""; 
        IDataset idsCommparaInfo = CommparaInfoQry.getOnlyByAttr("CSM", "1551", "ZZZZ"); 
        if(IDataUtil.isNotEmpty(idsCommparaInfo))
        { 
	        for (int i = 0; i < idsCommparaInfo.size(); i++) 
	        { 
	        	IData idCommparaInfo = idsCommparaInfo.getData(i);
		        if(StringUtils.isBlank(strTestDiscnt))
		        { 
		        	strTestDiscnt += idCommparaInfo.getString("PARAM_CODE", "").trim(); 
		        }
		        else
		        {
		        	strTestDiscnt += "," + idCommparaInfo.getString("PARAM_CODE", "").trim(); 
		        } 
	        } 
        } 
        // List<DiscntTradeData> discntList = uca.getUserDiscntsByDiscntCodeArray(IotConstants.IOT_TEST_DISCNT); 
        List<DiscntTradeData> lsDiscntList = uca.getUserDiscntsByDiscntCodeArray(strTestDiscnt);
        if (CollectionUtils.isNotEmpty(lsDiscntList))
        {
            DiscntTradeData cloneDiscntTrade = lsDiscntList.get(0).clone();
            cloneDiscntTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
            cloneDiscntTrade.setEndDate(SysDateMgr.getSysTime());
            btd.add(uca.getSerialNumber(), cloneDiscntTrade);
        }
        // 修改普通产品生效时间
        changeNormalProductEffectTime(btd, uca);       
    }
}
