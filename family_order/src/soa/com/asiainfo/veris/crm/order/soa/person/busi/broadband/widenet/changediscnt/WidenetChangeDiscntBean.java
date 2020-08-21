
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changediscnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class WidenetChangeDiscntBean extends CSBizBean
{
    private static transient final Logger logger = Logger.getLogger(WidenetChangeDiscntBean.class);

    public static String IDataSetToString(IDataset dst, String str) throws Exception
    {
        String s = "";
        for (int i = 0; i < dst.size(); i++)
        {
            s += dst.getData(i).getString(str, "") + ",";
        }
        return s;
    }

    /**
     * 把IDataset中的str1和str2字段 转到 字符串 str1_str2 , 以逗号间隔
     * 
     * @author chenzm
     */
    public static String IDataSetToString(IDataset dst, String str1, String str2) throws Exception
    {
        String s = "";
        for (int i = 0; i < dst.size(); i++)
        {
            s += dst.getData(i).getString(str1, "") + "_" + dst.getData(i).getString(str2, "") + ",";
        }
        return s;
    }

    public IDataset checkDiscnt(IData input) throws Exception
    {
        IData discntParam = new DataMap();
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        boolean mutex = false;
        String discntCode = input.getString("NEW_DISCNT");
        String existsCode = input.getString("EXISTS_DISCNT");
        IData discntInfo = UDiscntInfoQry.getDiscntInfoByPk(discntCode);
        String[] newDiscnt = StringUtils.split(discntCode, ",");
        String[] existsDiscnt = StringUtils.split(existsCode, ",");
        for (int i = 0; i < newDiscnt.length; i++)
        {
            for (int j = 0; j < existsDiscnt.length; j++)
            {
                IDataset elemLimitInfos = ElemLimitInfoQry.checkElementLimitByElementIdAB(newDiscnt[i], existsDiscnt[j], "0");
                if (IDataUtil.isNotEmpty(elemLimitInfos))
                {
                    mutex = true;
                    break;
                }
            }
            if (mutex)
            {
                break;
            }
        }

        if (mutex)
        {
            data.put("CODE", "1");
        }
        else
        {
            data.put("CODE", "0");
        }

        dataset.add(data);
        return dataset;
    }

    public IDataset loadChildInfo(IData input) throws Exception
    {

        IData data = new DataMap();
        IData discntParam = new DataMap();
        IDataset dataset = new DatasetList();
        IDataset paramDataset = new DatasetList();
        // 获取用户已办理的校园宽带优惠 
//        add by hefeng 调产商品改造
//        IDataset userDiscntList = UserDiscntInfoQry.getUserSpecDiscnt(input.getString("USER_ID"), "C"); 
        IDataset userDiscntList = new DatasetList();        
        IDataset userDiscnts= UserDiscntInfoQry.getAllValidDiscntByUserId(input.getString("USER_ID"));//获取用户的优惠
        // 获取能够办理的校园宽带优惠
//        IDataset canDiscntList = UserDiscntInfoQry.getCanSpecDiscnt("C");
           IDataset canDiscntList =  UPackageElementInfoQry.queryPackageElementsByProductIdDisctypeCode("","C");
           
           for(int j=0; j<userDiscnts.size();j++){
        	       IData temp = userDiscnts.getData(j);
        	      for(int k=0; k<canDiscntList.size();k++){
        	    	     String  prarmdiscode=canDiscntList.getData(k).getString("DISCNT_CODE");
        	    	     if(temp.getString("DISCNT_CODE").equals(prarmdiscode)){
        	    	    	 temp.put("DISCNT_NAME", canDiscntList.getData(k).getString("DISCNT_NAME"));
        	    	    	 IData userdistemp =UpcCall.qryOfferExtChaByOfferId(temp.getString("DISCNT_CODE"), "D", "TD_B_DISCNT").getData(0);       	    	    	 
        	    	    	 temp.put("ENABLE_TAG", userdistemp.getString("ENABLE_TAG"));
        	    	    	 temp.put("RSRV_STR3", userdistemp.getString("RSRV_STR3"));
        	    	    	 temp.put("RSRV_STR2", userdistemp.getString("RSRV_STR2"));
        	    	    	 temp.put("RSRV_STR4", userdistemp.getString("RSRV_STR4"));
        	    	    	 
        	    	    	 userDiscntList.add(temp);
        	    	     }
        	      }
        	      
           }

        IDataset canDeleteDiscntList = new DatasetList();

        for (int i = 0, size = canDiscntList.size(); i < size; i++)
        {
            IData canDiscnt = canDiscntList.getData(i);
            String discntCode = canDiscnt.getString("DISCNT_CODE") + "_DEL";
            IData canDeleteDiscnt = new DataMap();
            canDeleteDiscnt.put("DISCNT_CODE", discntCode);
            canDeleteDiscntList.add(canDeleteDiscnt);
        }

        // 办理月份获取
        IDataset deferMonthsInfo = CommparaInfoQry.getCommpara("CSM", "7633", "0", "0898");
        String months = deferMonthsInfo.getData(0).getString("PARA_CODE24", "");
        String[] months2 = StringUtils.split(months, ",");
        IDataset deferMonths = new DatasetList();
        for (int i = 0; i < months2.length; i++)
        {
            IData param = new DataMap();
            param.put("DEFER_MONTHS", months2[i]);
            deferMonths.add(param);
        }

        DiscntPrivUtil.filterDiscntListByPriv(CSBizBean.getVisit().getStaffId(), canDiscntList);
        // 能新增的资费权限
        discntParam.put("SPEC_ADD_LIMIT", IDataSetToString(canDiscntList, "DISCNT_CODE"));

        // 能删除的资费权限
        DiscntPrivUtil.filterDiscntListByPriv(CSBizBean.getVisit().getStaffId(), canDeleteDiscntList);
        for (int i = 0, size = canDeleteDiscntList.size(); i < size; i++)
        {
            IData deleteDiscnt = canDeleteDiscntList.getData(i);
            String deleteDiscntCode = deleteDiscnt.getString("DISCNT_CODE");
            deleteDiscntCode = deleteDiscntCode.substring(0, deleteDiscntCode.length() - 4);
            deleteDiscnt.put("DISCNT_CODE", deleteDiscntCode);

        }

        discntParam.put("SPEC_DEL_LIMIT", IDataSetToString(canDeleteDiscntList, "DISCNT_CODE"));
        // 已办理的资费
        discntParam.put("SPEC_HAVE_DISCNT", IDataSetToString(userDiscntList, "DISCNT_CODE"));

        discntParam.put("CURRENT_TIME", SysDateMgr.getSysTime());
        discntParam.put("FIRST_TIME_NEXT_MONTH", SysDateMgr.getFirstDayOfNextMonth());
        discntParam.put("LAST_TIME_CURRENT_MONTH", SysDateMgr.getLastDateThisMonth());
        discntParam.put("REMAIN_DAYS", SysDateMgr.dayInterval(SysDateMgr.getSysTime(), SysDateMgr.getLastDateThisMonth()) + 1);
        discntParam.put("DEFER_MONTHS", deferMonths);
        discntParam.put("MONTHS", months);

        // 用户余额查询
        // TODO
        // discntParam.put("SPEC_HAVE_DEPOSIT", IDataSetToString(data.getDataset("USER_ACCT_INFO"), "DEPOSIT_CODE",
        // "X_CANUSE_VALUE"));
        IDataset userAcctInfo = AcctCall.getCalcOweFeeByUserAcctId(input.getString("USER_ID"), input.getString("ACCT_ID"), "1");
        

        discntParam.put("SPEC_HAVE_DEPOSIT", IDataSetToString(userAcctInfo.getData(0).getDataset("DEPOSIT_DATA"), "DEPOSIT_CODE", "X_CANUSE_VALUE"));

        paramDataset.add(discntParam);

        data.put("DISCNT_PARAM_LIST", paramDataset);
        data.put("USER_DISCNT_LIST", userDiscntList);
        data.put("CAN_DISCNT_LIST", canDiscntList);
        data.put("DEFER_MONTHS_LIST", deferMonths);

        dataset.add(data);
        return dataset;
    }
}
