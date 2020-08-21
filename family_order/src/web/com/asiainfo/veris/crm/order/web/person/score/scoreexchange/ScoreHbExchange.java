
package com.asiainfo.veris.crm.order.web.person.score.scoreexchange ;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ScoreHbExchange extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData indata = getData();

        // 获取子业务资料
        IData userInfo = new DataMap(indata.getString("USER_INFO", ""));
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        inparam.put("USER_ID", userInfo.getString("USER_ID"));
        inparam.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));

        IDataset data = CSViewCall.call(this, "SS.ScoreExchangeSVC.getCommInfo", inparam);

        IData comminfo = new DataMap();
        IData temp = data.getData(0);
        comminfo.putAll(temp.getData("COMMINFO"));
         
       
        String perscore="";
        
        IData cond = new DataMap();
        cond.put("PARAM_ATTR", "1923");
        cond.put("SUBSYS_CODE", "CSM");
        cond.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        IDataset paramSet = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByAttr", cond);
        
        if(IDataUtil.isNotEmpty(paramSet))
        {
        	perscore = paramSet.getData(0).getString("PARA_CODE1","0");
        }
        
        String score = comminfo.getString("SCORE");
        
        if(Integer.parseInt(score)<Integer.parseInt(perscore) || "0".equals(perscore))
        {
        	CSViewException.apperr(ScoreException.CRM_SCORE_20,score,perscore);       	
        }
        
        int totalfee = Integer.parseInt(score)/Integer.parseInt(perscore);
        
        int needscore = totalfee * Integer.parseInt(perscore) ;
        
        int subscore = Integer.parseInt(score) - needscore ;
        
        comminfo.put("SCORE_TOTLE",score);
        comminfo.put("SCORE_NEED",String.valueOf(needscore));
        comminfo.put("SCORE_SUR",String.valueOf(subscore));
        comminfo.put("EVALUE",String.valueOf(totalfee));
    	comminfo.put("PER_SCORE",perscore);
        
        comminfo.put("EPARCHY_NAME", userInfo.getString("EPARCHY_NAME"));
        comminfo.put("CUST_NAME", indata.getString("CUST_NAME"));

        setCommInfo(comminfo);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData param = new DataMap() ;
        IData param1 = new DataMap();
        DatasetList param2 = new DatasetList ();
        
        IData cond = new DataMap();
        String ruleid = "";
        cond.put("PARAM_ATTR", "1923");
        cond.put("SUBSYS_CODE", "CSM");
        cond.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset paramSet = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByAttr", cond);
        
        if(IDataUtil.isEmpty(paramSet))
        {
        	CSViewException.apperr(ScoreException.CRM_SCORE_21);
        }       
        
        ruleid = paramSet.getData(0).getString("PARA_CODE2","");
        
        if("".equals(data.getString("EVALUE")))
        {
        	CSViewException.apperr(ScoreException.CRM_SCORE_22);
        }
        
        param.put("EVALUE", data.getString("EVALUE"));
        
        param.put("RULE_ID", ruleid);
        param.put("COUNT", "1");
        param2.add(param);      
        param1.put("EXCHANGE_DATA",param2.toString());
        param1.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        
        IDataset dataset = CSViewCall.call(this, "SS.ScoreExchangeRegSVC.tradeReg", param1);
        setAjax(dataset);

    }

    public abstract void setAcctInfo(IData acctInfo);

  //  public abstract void setCard(IData cardInfo);

  //  public abstract void setCards(IDataset cardInfos);

  //  public abstract void setCenttype(IDataset centtype);

  //  public abstract void setChangecust(IData changecust);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setExchangtype(IDataset exchangtype);

    public abstract void setGoodstype(IDataset goodstype);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    

}
