
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.wlan;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DESUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatAfterPfReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class WlanPwdAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        String bizTypeCode=PlatConstants.PLAT_WLAN;
        if ("98009201".equals(pstd.getElementId()))
        {
        	bizTypeCode=PlatConstants.PLAT_WLAN_EDU;
        }
        if(PlatConstants.OPER_MODIFY_PASSWORD.equals(pstd.getOperCode())){
        	PlatReqData prd = (PlatReqData) btd.getRD();
            List<PlatSvcData> psds = prd.getPlatSvcDatas();
            if(psds.size()==0){
            	return;
            }
            PlatSvcData psd = psds.get(0);
			String pwd = "";
			String oldPassword = "";
			List<AttrData> attrList = psd.getAttrs();
	        for (int i = 0; i < attrList.size(); i++)
	        {
	        	AttrData attrData = attrList.get(i);
	        	String attrValue = attrData.getAttrValue();
	        	if ("AIOBS_PASSWORD".equals(attrData.getAttrCode()))
	        	{
	        		pwd=attrValue;
	        	}else if ("OLD_PASSWORD".equals(attrData.getAttrCode())){
	        		oldPassword=attrValue;
	        	}
	        }
			IData inData= new DataMap();
			inData.put("KIND_ID","BIP2B262_T2100261_0_0");//交易唯一标识
			inData.put("X_TRANS_CODE","");//交易编码-IBOSS
			inData.put("MSISDN",uca.getSerialNumber());
			inData.put("OLDPASSWD",oldPassword);
			inData.put("NEWPASSWD", pwd);
			inData.put("BIZ_TYPE_CODE", bizTypeCode);
			IDataset set = IBossCall.dealIboss(CSBizBean.getVisit(), inData, "BIP2B262_T2100261_0_0");
			btd.getRD().setPreType(BofConst.PRE_TYPE_CHECK);
			return;
		}
        //xiekl bug修复  写在这里不清晰， 但是能解决问题；校园wlan退订套餐，注销业务
        if(PlatConstants.OPER_CANCEL_TC.equals(pstd.getOperCode())
        		&& "98009201".equals(pstd.getElementId()))
        {
        	pstd.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
        }

        if (PlatConstants.OPER_RESET.equals(pstd.getOperCode()) || PlatConstants.OPER_MODIFY_PASSWORD.equals(pstd.getOperCode()) || PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
        {

            // 将前台传入的WLAN密码加密
            if (PlatConstants.OPER_RESET.equals(pstd.getOperCode()) && ("0".equals(CSBizBean.getVisit().getInModeCode()) || "1".equals(CSBizBean.getVisit().getInModeCode())))
            {
                AttrTradeData passwordAttrTrade = uca.getUserAttrsByRelaInstIdAttrCode(pstd.getInstId(), "AIOBS_PASSWORD");
                if (passwordAttrTrade != null)
                {
                    AttrTradeData clonePasswordTrade = passwordAttrTrade.clone();
                    clonePasswordTrade.setEndDate(SysDateMgr.getSysTime());
                    clonePasswordTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(uca.getSerialNumber(), clonePasswordTrade);
                }

//                AttrTradeData attr = new AttrTradeData();
//                attr.setAttrCode("AIOBS_PASSWORD");
//                attr.setAttrValue(PlatUtils.geneComplexRandomPassword());
//                attr.setElementId(pstd.getElementId());
//                attr.setEndDate(SysDateMgr.END_DATE_FOREVER);
//                attr.setInstId(SeqMgr.getInstId());
//                attr.setInstType("Z");
//                attr.setModifyTag(BofConst.MODIFY_TAG_ADD);
//                attr.setRelaInstId(pstd.getInstId());
//                attr.setStartDate(SysDateMgr.getSysTime());
//                attr.setUserId(uca.getUserId());
//                btd.add(uca.getSerialNumber(), attr);
//                pstd.getAttrTradeDatas().add(attr);
            }

            List<AttrTradeData> attrList = pstd.getAttrTradeDatas();
            for (int i = 0; i < attrList.size(); i++)
            {
                AttrTradeData attrData = attrList.get(i);
                String attrValue = attrData.getAttrValue();

                if ("AIOBS_PASSWORD".equals(attrData.getAttrCode()) && BofConst.MODIFY_TAG_ADD.equals(attrData.getModifyTag()))
                {

                    if (PlatConstants.IS_ENCRYPT_PASSWORD)
                    {
                        attrValue = DESUtil.encrypt(attrValue); // 对密码进行加密保存
                    }

                    attrData.setAttrValue(attrValue);
                }
            }
        }

        if (PlatConstants.OPER_RESET.equals(pstd.getOperCode()))
        {
            pstd.setOperCode(PlatConstants.OPER_MODIFY_PASSWORD);
        }

    }

}
