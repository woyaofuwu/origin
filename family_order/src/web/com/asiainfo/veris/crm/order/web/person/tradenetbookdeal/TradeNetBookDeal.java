/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.tradenetbookdeal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-4-17 修改历史 Revision 2014-4-17 上午11:08:52
 */
public abstract class TradeNetBookDeal extends PersonBasePage
{

    public void dealNetBook(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData();

        String tradeId = inputData.getString("trade_id");
        String bookStatus = inputData.getString("book_status");
        IData bookInfo = new DataMap();
        bookInfo.put("BOOK_STATUS", bookStatus);
        bookInfo.put("TRADE_ID", tradeId);
        bookInfo.put("TRADE_STAFF_ID", this.getVisit().getStaffId());
        //处理意见
        bookInfo.put("RSRV_STR5", inputData.getString("RSRV_STR5"));
        //判断处理订单是否处理过入过已经处理则处理时间不再更新
        IDataset result = CSViewCall.call(this, "SS.TradeNetBookDealSVC.qryBookInfo", bookInfo);
        if (IDataUtil.isNotEmpty(result))
        {
           String dealTime = result.getData(0).getString("DEAL_DATE","");
           if(StringUtils.isNotEmpty(dealTime)){
        	   
        	   bookInfo.put("RSRV_DATE1", dealTime);
           }
           else{
        	   
        	   dealTime=SysDateMgr.getSysTime();
        	   bookInfo.put("RSRV_DATE1", dealTime);
           }
        
        }
        
        IDataset dataset = CSViewCall.call(this, "SS.TradeNetBookDealSVC.updateNetBook", bookInfo);

        this.setAjax(dataset.getData(0));

    }

    public void initNetBookDetail(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData();

        IDataOutput result = CSViewCall.callPage(this, "SS.TradeNetBookDealSVC.qryBookInfo", inputData, null);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        IData data = dataset.getData(0);
        data.put("CUR_BOOK_STATUS", data.get("BOOK_STATUS"));
        data.put("cond_BOOK_STATUS", data.get("BOOK_STATUS"));
        //备注模糊处理
        data.put("REMARK", likeRemark(data.getString("REMARK")));
        setInfo(data);
        this.setAjax(data);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

        IData cond = new DataMap();

        cond.put("cond_BOOK_STATUS", "0");
        cond.put("cond_IN_MOD_CODE", "2");
        cond.put("cond_BOOK_DEPT_NAME", this.getVisit().getDepartName());
        cond.put("cond_BOOK_DEPT", this.getVisit().getDepartId());
        cond.put("cond_START_DATE", SysDateMgr.getSysTime());
        cond.put("cond_END_DATE", SysDateMgr.getSysTime());
        cond.put("cond_CITY_ID", this.getVisit().getCityCode());
        cond.put("cond_CITY_CODE", this.getVisit().getCityCode());
        if(!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "NETBOOK_QRYALL")){
        	cond.put("cond_NOTQRYALL", true);
        }else{
        	cond.put("cond_NOTQRYALL", false);
        }

        this.setCondition(cond);

        IData data = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        
        IData param = new DataMap();
        if(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "NETBOOK_QRYALL")){
        	param.put("AREA_CODE", "");
        }else{
        	param.put("AREA_CODE", this.getVisit().getCityCode());
        }
        param.put("THIS_TAG","1");
        param.put("USE_TAG", "1");

        this.setBookDepts(CSViewCall.call(this, "SS.TradeNetBookDealSVC.getBookDepts", data));
        this.setBookAreas(CSViewCall.call(this, "CS.AreaInfoQrySVC.getAreaByPk", param));
    }

    /**
     * REQ201911280007关于优化“NGBOSS-网厅预约业务处理界面”的需求 
     * @author liwei29
     * @data 2019-12-12
     * @param update 
     * @throws Exception
     */
    public void qryNetBookInfos(IRequestCycle cycle) throws Exception
    {

        Pagination page = getPagination("recordNav");
        IData inputData = this.getData("cond", true);

        inputData.put("BOOK_DATE", inputData.getString("START_DATE"));
        inputData.put("BOOK_END_DATE", inputData.getString("END_DATE"));
        inputData.put("TRADE_ID", inputData.getString("BOOK_ID"));
        
        IDataOutput result = CSViewCall.callPage(this, "SS.TradeNetBookDealSVC.qryBookInfo", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }else{
    		for(int i=0;i<dataset.size();i++){
	   			 String  remark=dataset.getData(i).getString("REMARK");
	   			 dataset.getData(i).put("REMARK", likeRemark(remark));
	   			 //获取宽带竣工状态若值为空，设置为0
	   			String  bookEndStatus=dataset.getData(i).getString("RSRV_TAG2");
	   			if(bookEndStatus!=null){
	   				dataset.getData(i).put("RSRV_TAG2", bookEndStatus);
	   			}
				 else{
	   				dataset.getData(i).put("RSRV_TAG2", "0");
	   			}
	   		}
        }
       
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(inputData);
        setPageCount(result.getDataCount());
    }

    /**
     * 备注模糊处理
     * @param remark
     * @return
     * @throws Exception
     */
    public static String  likeRemark(String remark)throws Exception{
    	String str="";
    	if(!"".equals(remark)&&remark!=null){
		   		 if(remark.indexOf("联系人：") > 0){
					 //备注模糊处理
					 String contacts=remark.substring(remark.indexOf("联系人：")+4, remark.length());
						String replaceStr="";
						if(contacts.length() > 1){
							if(contacts.startsWith("0")||contacts.startsWith("1")||contacts.startsWith("2")||
									contacts.startsWith("3")||contacts.startsWith("4")||contacts.startsWith("5")||
									contacts.startsWith("6")||contacts.startsWith("7")||contacts.startsWith("8")||
									contacts.startsWith("9")){
								//数字
							    if(contacts.length() >= 3){
							    	replaceStr=contacts.substring(0, 3)+"********";
							    }else{
							    	replaceStr="********";
							    }
							}else{
								//中文
								replaceStr=contacts.substring(0, 1)+"**";
							}
						}else{
							replaceStr="***";
						}
				 str=remark.substring(0, remark.indexOf("联系人："))+"联系人："+replaceStr+"】";
				 }else{
					 str=remark;
				 }
    	 }
		return str;
    }

    public abstract void setBookDepts(IDataset bookDepts);
    
    public abstract void setBookAreas(IDataset bookAreas);

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long count);

}
