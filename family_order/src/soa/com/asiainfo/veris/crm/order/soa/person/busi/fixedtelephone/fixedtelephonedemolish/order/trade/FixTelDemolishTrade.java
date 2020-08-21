
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.fixedtelephonedemolish.order.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.TelephoneTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTelephoeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.userconn.UserConnQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;

public class FixTelDemolishTrade extends BaseTrade implements ITrade
{

    private String oldstate;

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        geneTradeMain(btd); // 设置主台账
        geneTradeUser(btd);// 设置用户台账
        // geneTradeAcct(btd);//设置账户台账
        geneTradeTelephone(btd); // 设置用户台账固话资料
        geneTradePayRelation(btd); // 设置用户付费关系业务台账
        geneTradeProduct(btd); // 设置用户产品业务台账
        geneTradeDiscnt(btd); // 设置用户优惠业务台账
        geneTradeSvc(btd); // 设置服务业务台账
        geneTradeAttr(btd); // 设置用户属性业务台账
        geneTradeSvcState(btd); // 设置服务状态业务台账
        geneTradeResInfo(btd); // 设置资源台账

        if (StringUtils.equals(btd.getTradeTypeCode(), "7804"))
        {
            btd.getMainTradeData().setSubscribeType("200");
        }
    }

    private void geneTradeAcct(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        // 更新用户账户信息
        AccountTradeData acctTradeData = btd.getRD().getUca().getAccount().clone();
        String finishDate = btd.getRD().getUca().getAcctDay();
        acctTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        btd.add(btd.getRD().getUca().getSerialNumber(), acctTradeData);
    }

    /**
     * @Function: geneTradeAttr
     * @Description: 用户属性子台帐拼串
     * @date Jun 6, 2014 10:34:42 AM
     * @param btd
     * @throws Exception
     * @author longtian3
     */
    private void geneTradeAttr(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        List<AttrTradeData> arrtList = ucaData.getUserAttrs();
        if (arrtList.size() > 0)
        {
            for (int i = 0, size = arrtList.size(); i < size; i++)
            {
                AttrTradeData attrTradeData = arrtList.get(i).clone();
                attrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                attrTradeData.setEndDate(btd.getRD().getAcceptTime());
                attrTradeData.setRemark("用户固话拆机操作");

                btd.add(ucaData.getSerialNumber(), attrTradeData);
            }
        }
    }

    /**
     * @Function: geneTradeDiscnt
     * @Description: 生成台帐优惠子表
     * @date Jun 6, 2014 10:30:00 AM
     * @param btd
     * @throws Exception
     * @author longtian3
     */
    private void geneTradeDiscnt(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        List<DiscntTradeData> discntList = ucaData.getUserDiscnts();
        if (discntList.size() > 0)
        {
            for (int i = 0, size = discntList.size(); i < size; i++)
            {
                DiscntTradeData discntTradeData = discntList.get(i).clone();
                discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                discntTradeData.setEndDate(btd.getRD().getAcceptTime());
                discntTradeData.setRemark("用户固话拆机操作");

                btd.add(ucaData.getSerialNumber(), discntTradeData);
            }
        }
    }

    /**
     * @Function: geneTradeMain
     * @Description: 拼接主台帐
     * @date Jun 6, 2014 9:27:34 AM
     * @param btd
     * @throws Exception
     * @author longtian3
     */
    private void geneTradeMain(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTrade = btd.getMainTradeData();
        mainTrade.setRsrvStr5(getStrToChar(btd.getRD().getUca().getSerialNumber()));
        mainTrade.setRemark(btd.getRD().getRemark());
        mainTrade.setNetTypeCode(PersonConst.FIX_TEL_NET_TYPE_CODE);
    }
    
	private String getStrToChar(String strTel) {
		
		String tmp = "";
		if(strTel.startsWith("0"))
		{
			tmp = strTel.substring(1).toString();
		}
		else{ 
			tmp = strTel.toString();
		}
		
		//tmp = tmp.replaceAll("\\+", "");
		char[] c = tmp.toCharArray();
		String str2 = "";
		for(int i=c.length-1; i>=0; i--){
			
			str2 += String.valueOf(c[i]);
			str2 += ".";
		}
		str2 += "6.8.e164.arpa";
		return str2;
	}

    /**
     * @Function: geneTradePayRelation
     * @Description: 付费关系子台帐拼串
     * @date Jun 6, 2014 10:04:22 AM
     * @param btd
     * @throws Exception
     * @author longtian3
     */
    private void geneTradePayRelation(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String userId = ucaData.getUserId();
        String endCycleId = SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD);// DiversifyAcctUtil.getLastDayThisAcctday(nowdate,
        // acctnum);

        IDataset payRela = PayRelaInfoQry.queryDefaultPayRelaByUserId(userId);
        if (IDataUtil.isNotEmpty(payRela))
        {
            PayRelationTradeData payRelaTrade = new PayRelationTradeData(payRela.getData(0));
            payRelaTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
            payRelaTrade.setEndCycleId(endCycleId);
            payRelaTrade.setRemark("用户固话拆机操作");

            btd.add(ucaData.getSerialNumber(), payRelaTrade);
        }
    }

    /**
     * @Function: geneTradeProduct
     * @Description: 生成产品子台帐
     * @date Jun 6, 2014 10:24:04 AM
     * @param btd
     * @throws Exception
     * @author longtian3
     */
    private void geneTradeProduct(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        List<ProductTradeData> productList = ucaData.getUserProducts();
        if (productList.size() > 0)
        {
            for (int i = 0, size = productList.size(); i < size; i++)
            {
                ProductTradeData productTradeData = productList.get(i).clone();
                productTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                productTradeData.setEndDate(btd.getRD().getAcceptTime());
                productTradeData.setRemark("用户固话拆机操作");

                btd.add(ucaData.getSerialNumber(), productTradeData);
            }
        }
    }

    /**
     * @Function: geneTradeResInfo
     * @Description: 登记资源台账
     * @date Jun 6, 2014 10:44:08 AM
     * @param btd
     * @throws Exception
     * @author longtian3
     */
    private void geneTradeResInfo(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String userId = ucaData.getUserId();
        IDataset resInfo = UserResInfoQry.getUserResInfoByUserId(userId);
        if (IDataUtil.isNotEmpty(resInfo))
        {
            for (int i = 0, size = resInfo.size(); i < size; i++)
            {
                ResTradeData resTradeData = new ResTradeData(resInfo.getData(i));
                resTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                resTradeData.setEndDate(btd.getRD().getAcceptTime());
                resTradeData.setRemark("用户固话拆机操作");

                btd.add(ucaData.getSerialNumber(), resTradeData);
            }
        }
    }

    /**
     * @Function: geneTradeSvc
     * @Description: 终止用户服务到当前时间
     * @date Jun 6, 2014 10:29:55 AM
     * @param btd
     * @throws Exception
     * @author longtian3
     */
    private void geneTradeSvc(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        List<SvcTradeData> svcList = ucaData.getUserSvcs();
        if (svcList.size() > 0)
        {
            for (int i = 0, size = svcList.size(); i < size; i++)
            {
                SvcTradeData svcTradeData = svcList.get(i).clone();
                svcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                svcTradeData.setEndDate(btd.getRD().getAcceptTime());
                svcTradeData.setRemark("用户固话拆机操作");

                btd.add(ucaData.getSerialNumber(), svcTradeData);
            }
        }
    }

    /**
     * @Function: geneTradeSvcState
     * @Description: 根据用户服务状态看是否为老状态，根据参数表中的配置，终止老状态的结束时间，新插入新状态
     * @date Jun 6, 2014 10:37:11 AM
     * @param btd
     * @throws Exception
     * @author longtian3
     */
    private void geneTradeSvcState(BusiTradeData btd) throws Exception
    {
        ChangeSvcStateComm comm = new ChangeSvcStateComm();
        comm.getSvcStateChangeTrade(btd);
    }

    /**
     * @Function: geneTradeTelephone
     * @Description: 用户固话资料台帐表
     * @date Jun 6, 2014 9:43:31 AM
     * @param btd
     * @throws Exception
     * @author yuezy
     */
    private void geneTradeTelephone(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String userId = ucaData.getUserId();
        IDataset fixTel = UserTelephoeInfoQry.getUserTelephoneByUserId(userId);
        if (IDataUtil.isNotEmpty(fixTel))
        {
            TelephoneTradeData telTrade = new TelephoneTradeData(fixTel.getData(0));
            telTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
            telTrade.setEndDate(btd.getRD().getAcceptTime());
            telTrade.setRemark("用户固话拆机操作");

            IDataset userConn = UserConnQry.getConnByUserIdAndType(userId, "BK");// 查询用户宽带固话共线信息
            if (IDataUtil.isNotEmpty(userConn))
            {
                telTrade.setRsrvStr5(userConn.getData(0).getString("USER_ID_A", ""));
            }

            btd.add(ucaData.getSerialNumber(), telTrade);
        }
    }

    private void geneTradeUser(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        String removeTag = "2";
        String strFinishDate = SysDateMgr.getSysDate();
        UserTradeData userData = btd.getRD().getUca().getUser().clone();
        // 更新用户资料信息
        userData.setRemoveTag(removeTag);
        userData.setDestroyTime(strFinishDate);
        userData.setRemoveCityCode(getVisit().getCityCode());
        userData.setRemoveDepartId(getVisit().getDepartId());
        userData.setRemark(btd.getRD().getRemark());
        userData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        userData.setUserStateCodeset("6");
        btd.add(btd.getRD().getUca().getSerialNumber(), userData);
    }

    public String getOldstate()
    {
        return oldstate;
    }

    public void setOldstate(String oldstate)
    {
        this.oldstate = oldstate;
    }
}
