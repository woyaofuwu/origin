
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreDealForBindDiscnt.java
 * @Description: 复机的号码是吉祥号码默认绑定优惠,"优惠编码|月份"
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-8-14 下午8:56:51
 */
public class RestoreDealForBindDiscnt implements ITradeAction
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
    }

    public void executeAction(BusiTradeData btd) throws Exception
    {/*
      * RestoreUserReqData restoreUserReqData = (RestoreUserReqData) btd.getRD(); String UserId =
      * restoreUserReqData.getUca().getUser().getUserId();
      * if(StringUtils.isNotBlank(restoreUserReqData.getBindDefaultDiscnt())) { String bindDftDiscnt =
      * restoreUserReqData.getBindDefaultDiscnt().trim(); String bindDiscntCode = bindDftDiscnt.split("\\|")[0]; String
      * bindMonth = bindDftDiscnt.split("\\|")[1]; int month =
      * Integer.parseInt(bindMonth)+2;//套餐立即生效，所以办理当月不算，算偏移月份时多加一个月 因为后面会减去一个月所以需要加2 IData tempPage0 = new DataMap();
      * IData tempPage1 = new DataMap(); tempPage0.put("USER_ID", UserId); tempPage0.put("PRODUCT_ID", "-1");
      * tempPage0.put("PACKAGE_ID", "-1"); tempPage0.put("DISCNT_CODE", "6067");//吉祥号码承诺在网套餐 tempPage0.put("START_DATE",
      * SysDateMgr.getSysTime()); tempPage0.put("END_DATE", SysDateMgr.END_DATE_FOREVER); dealForDiscnt(btd,
      * restoreUserReqData.getUca(), tempPage0); tempPage1.put("USER_ID", UserId); tempPage1.put("PRODUCT_ID", "-1");
      * tempPage1.put("PACKAGE_ID", "-1"); tempPage1.put("DISCNT_CODE", bindDiscntCode);//吉祥号码承诺不销户，不过户套餐
      * tempPage1.put("START_DATE", SysDateMgr.getSysTime()); tempPage1.put("END_DATE",
      * SysDateMgr.endDate(SysDateMgr.getSysTime(), "1", "", month+"", "3")); dealForDiscnt(btd,
      * restoreUserReqData.getUca(), tempPage1); }
      */
    }
}
