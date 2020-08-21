
package com.asiainfo.veris.crm.iorder.web.person.score.scoreexchange;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ScoreExchangeNew extends PersonBasePage
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
        comminfo.put("EPARCHY_NAME", userInfo.getString("EPARCHY_NAME"));
        comminfo.put("CUST_NAME", indata.getString("CUST_NAME"));

        returnMaxScore(temp.getDataset("EXCHANGE_LIST"));	//设置最大积分数
        
        setGoodstype(temp.getDataset("OBJECT_TYPE"));
        setCenttype(temp.getDataset("CENT_TYPE"));
        setExchangtype(temp.getDataset("EXCHANGE_TYPE_CODE"));
        setCommInfo(comminfo);
        setInfos(temp.getDataset("EXCHANGE_LIST"));
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        String strtemps = data.getString("EXCHANGE_DATA");
        if( StringUtils.isNotBlank(strtemps) ) {
        	IDataset temps = new DatasetList(strtemps);
        	IData eData = new DataMap();
            eData.put("EXCHANGE_DATA", temps);
            eData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        	IDataset putData = CSViewCall.call(this, "SS.ScoreExchangeSVC.getEmzData", eData);
        	
        	IDataset params = new DatasetList();
        	IDataset params97 = new DatasetList();
        	if( IDataUtil.isNotEmpty(putData) ) {
        		IData A = putData.first();
        		params = new DatasetList(A.getString("EXCHANGE_DATA"));
        		params97 = new DatasetList(A.getString("EXCHANGE_EMZ"));
        	}
        	
        	IData param = new DataMap();
            param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
            param.put("CARD_NOS", data.getString("CARD_NOS"));
            //param.put("EXCHANGE_EMZ", params97);
            //param.put("EXCHANGE_DATA", params);
            param.put("REMARK", data.getString("REMARK"));
            param.put("OBJECT_SERIAL_NUMBER", data.getString("OBJECT_SERIAL_NUMBER"));
            param.put("HH_CARD_ID", data.getString("HH_CARD_ID"));
            param.put("HH_CARD_NAME", data.getString("HH_CARD_NAME"));
            //2015-05-20  QR-20150519-05积分兑换全部自动显示为“未知认证”bug
            param.put("CHECK_MODE", data.getString("CHECK_MODE"));
            
            if( IDataUtil.isNotEmpty(params) ){
            	param.put("EXCHANGE_DATA", params);
            	
            	if( IDataUtil.isNotEmpty(params97) ){
                	IData param97 = new DataMap();
                    param97 = new DataMap(param);
                    param97.put("EXCHANGE_DATA", params97);
                    
                    param.put("EXCHANGE_EMZ", param97);
                }
            }else{
            	param.put("EXCHANGE_DATA", params97);
            }
            param.put("ISPHONE_K", data.getString("ISPHONE_K"));//用于判断是否是手机端过来的
            IDataset dataset = CSViewCall.call(this, "SS.ScoreExchangeRegSVC.tradeReg", param);
            setAjax(dataset);
        }else{
        	IData param = new DataMap();
            param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
            param.put("CARD_NOS", data.getString("CARD_NOS"));
            param.put("EXCHANGE_DATA", data.getString("EXCHANGE_DATA"));
            param.put("REMARK", data.getString("REMARK"));
            param.put("OBJECT_SERIAL_NUMBER", data.getString("OBJECT_SERIAL_NUMBER"));
            param.put("HH_CARD_ID", data.getString("HH_CARD_ID"));
            param.put("HH_CARD_NAME", data.getString("HH_CARD_NAME"));
            //2015-05-20  QR-20150519-05积分兑换全部自动显示为“未知认证”bug
            param.put("CHECK_MODE", data.getString("CHECK_MODE"));
            param.put("ISPHONE_K", data.getString("ISPHONE_K"));//用于判断是否是手机端过来的
            IDataset dataset = CSViewCall.call(this, "SS.ScoreExchangeRegSVC.tradeReg", param);
            setAjax(dataset);
        }
    }

    /**
     * 查询有价卡资料
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCardRes(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData param = new DataMap();

        IDataset dataset = new DatasetList(data.getString("DATA"));
        param.put("CARD_ID", data.getString("CARD_ID"));// 开始卡号
        param.put("CARD_END", data.getString("CARD_END"));// 结束卡号
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));// 号码
        IDataset cards = CSViewCall.call(this, "SS.ScoreExchangeSVC.queryCardRes", param);

        if (!"0".equals(cards.get(0, "X_RESULTCODE")) && !"1".equals(cards.get(0, "X_RESULTCODE")))
        {
            returnQueryObjectPhone(data, "false", (String) cards.get(0, "X_RESULTINFO"));
            return;
        }
        if ("1".equals(cards.get(0, "X_RESULTCODE")))
        {
            returnQueryObjectPhone(data, "false", (String) cards.get(0, "X_RESULTINFO"));
        }
        cards.remove(0);
        if (IDataUtil.isNotEmpty(dataset) && IDataUtil.isNotEmpty(cards))
        {
            // 处理重复卡号
            for (int i = 0; i < cards.size(); i++)
            {
                for (int j = 0; j < dataset.size(); j++)
                {
                    if (dataset.getData(j).getString("CARD_ID").equals(cards.getData(i).getString("CARD_ID")))
                    {
                        dataset.remove(j);
                    }
                }

            }
        }
        if (IDataUtil.isNotEmpty(dataset))// 处理页面上卡类型不能翻译问题
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                dataset.getData(i).put("CARD_TYPE", dataset.getData(i).get("CARD_TYPE_CODE"));
            }
        }
        cards.addAll(dataset);// 将页面上的值再传回去
        setCards(cards);
    }

    /**
     * 查询目标号码资料
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryObjectPhone(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.get("OBJECT_SERIAL_NUMBER"));
        IData objectData = CSViewCall.callone(this, "SS.ScoreExchangeSVC.queryObjectBySN", param);// bean.queryObjectBySN(data);

        if ("0".equals(objectData.getString("X_RESULTCODE")))
        {
            IData objectInfo = (IData) objectData.get("CUSTINFO");
            returnData.put("OBJECT_NAME", objectInfo.get("CUST_NAME"));
            returnData.put("USER_ID", objectData.getData("USERINFO").getString("USER_ID"));
            returnData.put("ACCT_ID", objectData.getData("ACCTINFO").getString("ACCT_ID"));
            setChangecust(returnData);
        }
        else
        {
            returnQueryObjectPhone(data, "false", objectData.getString("X_RESULTINFO"));
            return;
        }

        returnQueryObjectPhone(data, "true", "ok");
    }

    private void returnQueryObjectPhone(IData data, String flag, String message) throws Exception
    {

        data.put("FLAG", flag);
        data.put("MESSAGE", message);

        this.setAjax(data);
    }
    
    private void returnMaxScore(IDataset scoreList){
    	
    	IData maxAjaxData = new DataMap();
    	Integer maxScore = 0;
    	if(DataSetUtils.isBlank(scoreList)){
    		maxAjaxData.put("MAX_SCORE", maxScore);
        	setAjax(maxAjaxData);
        	return;
    	}
        for (int i = 0; i < scoreList.size(); i++) {
        	String score = scoreList.getData(i).getString("SCORE","0");
			if(Integer.parseInt(score) >= maxScore){
				maxScore = Integer.parseInt(score);
			}
		}
        if(maxScore == 0){
        	maxAjaxData.put("MAX_SCORE", maxScore);
        	setAjax(maxAjaxData);
        	return;
        }
        String numStr = String.valueOf(maxScore);
		int numFirst = Integer.valueOf(numStr.substring(0, 1)) + 1;
		String numNew = String.valueOf(numFirst);
		for (int i = 1; i < numStr.length(); i++) {
			numNew += "0";
		}
		maxAjaxData.put("MAX_SCORE", numNew);
    	setAjax(maxAjaxData);
        
    }

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setCard(IData cardInfo);

    public abstract void setCards(IDataset cardInfos);

    public abstract void setCenttype(IDataset centtype);

    public abstract void setChangecust(IData changecust);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setExchangtype(IDataset exchangtype);

    public abstract void setGoodstype(IDataset goodstype);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
