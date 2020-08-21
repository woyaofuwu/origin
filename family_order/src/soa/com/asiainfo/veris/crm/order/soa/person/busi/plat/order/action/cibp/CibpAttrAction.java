
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.cibp;

import java.util.ArrayList;
import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

/**
 * 处理互联网电视的属性 基础服务需要填写属性； 非基础服务的需要同基础服务属性一致
 * 
 * @author bobo
 */
public class CibpAttrAction implements IProductModuleAction
{

    public static String BASIC_BIZ_CODE = "20830000";

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
    	//将不需要调用服务的判断提前@tanzheng@20190611
    	if (btd.getTradeTypeCode().equals("3800")||btd.getTradeTypeCode().equals("3902"))
    	{
    		return;
    	}
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());


        if (!"51".equals(officeData.getBizTypeCode()))
        {
            return;
        }

        // 基础服务订购时，不处理属性 ;非基础服务订购时，取基础服务的机顶盒属性
        if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode()) && !BASIC_BIZ_CODE.equals(officeData.getBizCode()))
        {
            List<AttrTradeData> attrTradeList = pstd.getAttrTradeDatas();
            if (attrTradeList == null || attrTradeList.isEmpty())
            {
                attrTradeList = new ArrayList<AttrTradeData>();
                List<AttrTradeData> userAttrList = this.getBasicServiceAttr(uca, officeData.getSpCode());

                for (int i = 0; i < userAttrList.size(); i++)
                {
                    AttrTradeData attrTradeData = userAttrList.get(i).clone();
                    attrTradeData.setInstId(SeqMgr.getInstId());
                    attrTradeData.setRelaInstId(pstd.getInstId());
                    attrTradeData.setStartDate(SysDateMgr.getSysTime());
                    attrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    attrTradeList.add(attrTradeData);

                    btd.add(uca.getSerialNumber(), attrTradeData);
                }

                pstd.setAttrTradeDatas(attrTradeList);
            }
        }

        // 机顶盒变更； 同一提供商的机顶盒编码都要变
        if (PlatConstants.OPER_USER_DATA_MODIFY.equals(pstd.getOperCode()))
        {
            pstd.setRsrvStr1(officeData.getSpCode());
            pstd.setRsrvStr2(officeData.getBizCode());

            // 修改同一厂商的机顶盒编码
            List<PlatSvcTradeData> userPlatSvcList = this.getUserPlatSvcBySpCode(uca, officeData.getSpCode(), officeData.getBizCode());
            for (int j = 0; j < userPlatSvcList.size(); j++)
            {
                PlatSvcTradeData userPlatSvc = userPlatSvcList.get(j);
                PlatOfficeData newOfficeData = PlatOfficeData.getInstance(userPlatSvc.getElementId());
                userPlatSvc.setOperCode(PlatConstants.OPER_USER_DATA_MODIFY);
                userPlatSvc.setModifyTag(BofConst.MODIFY_TAG_UPD);
                userPlatSvc.setOprSource(pstd.getOprSource());
                userPlatSvc.setIsNeedPf("1");
                userPlatSvc.setRsrvStr1(newOfficeData.getSpCode());
                userPlatSvc.setRsrvStr2(newOfficeData.getBizCode());
                userPlatSvc.setOperTime(SysDateMgr.getSysTime());
                btd.add(uca.getSerialNumber(), userPlatSvc);

                List<AttrTradeData> userAttrList = userPlatSvc.getAttrTradeDatas();
                for (int k = 0; k < userAttrList.size(); k++)
                {
                    AttrTradeData attrTradeData = userAttrList.get(k);
                    if (attrTradeData != null && !StringUtils.isBlank(PlatUtils.getAttrValue(attrTradeData.getAttrCode(), pstd.getAttrTradeDatas())))
                    {
                        AttrTradeData newAttrTradeData = attrTradeData.clone();
                        newAttrTradeData.setInstId(SeqMgr.getInstId());
                        newAttrTradeData.setAttrValue(PlatUtils.getAttrValue(attrTradeData.getAttrCode(), pstd.getAttrTradeDatas()));
                        newAttrTradeData.setStartDate(SysDateMgr.getSysTime());
                        newAttrTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                        newAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        newAttrTradeData.setElementId(userPlatSvc.getElementId());
                        btd.add(uca.getSerialNumber(), newAttrTradeData);

                        attrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        attrTradeData.setEndDate(SysDateMgr.getSysTime());
                        btd.add(uca.getSerialNumber(), attrTradeData);

                    }
                }
            }
        }

    }

    private List<AttrTradeData> getBasicServiceAttr(UcaData uca, String spCode) throws Exception
    {
        List<PlatSvcTradeData> userPlatSvcList = uca.getUserPlatSvcs();
        List<AttrTradeData> userPlatAttrSvcList = new ArrayList<AttrTradeData>();

        for (int i = 0; i < userPlatSvcList.size(); i++)
        {
            PlatSvcTradeData userPlatSvc = userPlatSvcList.get(i);
            PlatOfficeData officeData = null;
            try
            {
            	  officeData = PlatOfficeData.getInstance(userPlatSvc.getElementId());
            }catch(Exception e)
            {
            	
            }
          
            if (officeData!=null && spCode.equals(officeData.getSpCode()) && BASIC_BIZ_CODE.equals(officeData.getBizCode()))
            {
                userPlatAttrSvcList = uca.getUserAttrsByRelaInstId(userPlatSvc.getInstId());
            }
        }

        return userPlatAttrSvcList;

    }

    /**
     * 通过传入SP_CODE和BIZ_CODE 获取同一个SP的互联网电视的服务，不包括当前的
     * 
     * @param uca
     * @param spCode
     * @return
     * @throws Exception
     */
    private List<PlatSvcTradeData> getUserPlatSvcBySpCode(UcaData uca, String spCode, String bizCode) throws Exception
    {
        // 同一SP的互联网电视的平台服务
        List<PlatSvcTradeData> sameSpUserList = new ArrayList<PlatSvcTradeData>();

        List<PlatSvcTradeData> userPlatSvcList = uca.getUserPlatSvcs();
        List<AttrTradeData> userPlatAttrSvcList = new ArrayList<AttrTradeData>();

        for (int i = 0; i < userPlatSvcList.size(); i++)
        {
            PlatSvcTradeData userPlatSvc = userPlatSvcList.get(i);
            
            PlatOfficeData officeData = null;
            try
            {
            	  officeData = PlatOfficeData.getInstance(userPlatSvc.getElementId());
            }catch(Exception e)
            {
            	
            }
            
            if ((officeData!=null) && spCode.equals(officeData.getSpCode()) && !BofConst.MODIFY_TAG_DEL.equals(userPlatSvc.getModifyTag()) && !bizCode.equals(officeData.getBizCode()))
            {
                userPlatAttrSvcList = uca.getUserAttrsByRelaInstId(userPlatSvc.getInstId());
                userPlatSvc.setAttrTradeDatas(userPlatAttrSvcList);

                sameSpUserList.add(userPlatSvc);
            }
        }

        return sameSpUserList;
    }
}
