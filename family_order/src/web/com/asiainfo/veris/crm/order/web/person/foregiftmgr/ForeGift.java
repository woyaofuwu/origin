/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.foregiftmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-4-8 修改历史 Revision 2014-4-8 下午05:14:37
 */
public abstract class ForeGift extends PersonBasePage
{

    /**
     * 根据发票号码获取发票信息
     * 
     * @param cycle
     * @throws Exception
     * @CREATE BY GONGP@2014-5-17
     */
    public void getInvoiceInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();

        IDataset results = CSViewCall.call(this, "SS.ForeGiftSVC.getInvoiceInfo", pagedata);
        if (results.size() > 0)
        {

            IData invoiceData = results.getData(0);

            if (invoiceData.getString("INVOICE_DATAS") != null)
            {
                this.setForeGifts(new DatasetList(invoiceData.getString("INVOICE_DATAS")));
            }
            this.setInvoice(invoiceData);
			this.setAjax(invoiceData);
        }
    }

    public IDataset getStaffForeGiftOpTypes(IRequestCycle cycle) throws Exception
    {

        boolean foregiftSQTag = StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "FOREGIFTSQ", "1");// 员工数据权限 收取
        boolean foregiftQTTag = StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "FOREGIFTQT", "1");// 员工数据权限 清退
        boolean foregiftWZQTTag = StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "FOREGIFTWZQT", "1");// 员工数据权限 无主清退

        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", "0898");
        inparam.put("PARAM_ATTR", "500");
        inparam.put("SUBSYS_CODE", "CSM");

        IDataset dataset = CSViewCall.call(this, "SS.ForeGiftSVC.getStaffForeGiftOpTypes", inparam);

        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);

            if ("0".equals(data.getString("PARAM_CODE")))
            {// 没有收取权限，删掉
                if (!foregiftSQTag)
                {
                    dataset.remove(i);
                    i--;
                }
            }
            if ("1".equals(data.getString("PARAM_CODE")))
            {// 没有清退权限，删掉
                if (!foregiftQTTag)
                {
                    dataset.remove(i);
                    i--;
                }
            }
            if ("2".equals(data.getString("PARAM_CODE")))
            {// 没有无主清退权限，删掉
                // if (!foregiftWZQTTag)
                // {
                dataset.remove(i);
                i--;
                // }
            }

        }

        return dataset;
    }

    /**
     * 重载父类函数 获取三户以外的参数、业务数据
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();

        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));

        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));
        
        IData acctInfo = new DataMap(pagedata.getString("ACCT_INFO"));

        IDataset results = CSViewCall.call(this, "SS.ForeGiftSVC.getLoadInfo", userInfo);

        IDataset UserForegiftDs = results.getData(0).getDataset("USERFOREGIFT");

        this.setForeGifts(UserForegiftDs);

        this.setOpTypes(getStaffForeGiftOpTypes(cycle));

        this.setCustInfoView(custInfo);

        this.setUserInfoView(userInfo);
        
        this.setAcctInfoView(acctInfo);

        this.setCommInfo(results.getData(0).getData("COMMON_INFO"));

        this.setBalance(results.getData(0).getString("BALANCE_RS"));
		
		this.setAjax(userInfo);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        if (StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "FOREGIFTWZQT", "1"))// // 员工数据权限 无主清退
        {
            IDataset dataset = new DatasetList();
            IData data = new DataMap();
            data.put("PARAM_NAME", "清退无主押金");
            data.put("PARAM_CODE", "2");
            dataset.add(data);

            this.setOpTypes(dataset);

        }
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset dataset = CSViewCall.call(this, "SS.ForeGiftRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    /**
     * REQ201610110009_押金业务界面增加判断拦截
     * @author zhuoyingzhi
     * 20161117
     * 对无主押金效验
     * @param cycle
     * @throws Exception
     */
    public void checkNotForeGift(IRequestCycle cycle)throws Exception{
    	IData data = getData();
    	
		String process_tag=data.getString("FOREGIFT_PROCESS_TAG","");
		String end_date=data.getString("FOREGIFT_END_DATE","");
		String userOtherservUserId=data.getString("FOREGIFT_USER_ID");
		String rsrv_num2=data.getString("FOREGIFT_RSRV_NUM2");//发票记录表  中的押金金额
		
		IData param=new DataMap();
		if ("1".equals(process_tag))
        {
            // common.error("此发票已经清退押金！");
            CSViewException.apperr(FeeException.CRM_FEE_124);
        }
		param.put("endDate", end_date);
		
		IData foreGiftBean=CSViewCall.call(this, "SS.ForeGiftSVC.sysDateCompareToEndDate", param).first();
		if("1".equals(foreGiftBean.getString("flag"))){
			 //结束时间小于等于系统当前时间
			//由于押金发票表没有未清退的记录，请和业务支撑部联系修改数据后再给用户清退
			CSViewException.apperr(FeeException.CRM_FEE_170, "该用户押金数据异常(押金发票表没有未清退的记录),请发OA单到业务支撑部协助核查，谢谢!");
		}
		
		//userid为0  拦截
		if("0".equals(userOtherservUserId)){
			  //出现数据异常
			//user_id=0, 请和业务支撑部联系修改数据后再给用户清退
			CSViewException.apperr(FeeException.CRM_FEE_170, "该用户押金数据异常(user_id=0),请发OA单到业务支撑部协助核查，谢谢!");
		}
		
		//根据tf_f_user_otherserv中id查询    发票资料表
		if(rsrv_num2 !=null&&!"".equals(rsrv_num2)){
			double money=Double.valueOf(rsrv_num2);
			
			param.put("money", money);
			param.put("userId", userOtherservUserId);
			
			String foregiftCode=data.getString("FOREGIFT_CODE");
			param.put("foregiftCode", foregiftCode);
			
		    IData returnData=CSViewCall.call(this, "SS.ForeGiftSVC.checkNotForeGift", param).first();
			if("1".equals(returnData.getString("flag"))){
				  //出现数据异常
				//清退金额大于押金表总金额，请联系业务支撑部修改数据，谢谢！
				CSViewException.apperr(FeeException.CRM_FEE_170, "该用户押金数据异常(清退金额大于押金表金额),请发OA单到业务支撑部协助核查，谢谢!");
			}
		}else{ 
			 //出现数据异常
			 CSViewException.apperr(FeeException.CRM_FEE_170, "数据异常，请发OA单到业务支撑部协助核查，谢谢!");
		}
		IData  iData=new DataMap();
		iData.put("flag", "1");
		setAjax(iData);
    }
    
    

    /**
     * 清退业务校验
     * add by liangdg3 at 20191024 for REQ201910180018押金业务受理及清退电子化存储需求
     * 话费类型，调账务接口判断客户是否欠费
     * @param cycle
     * @throws Exception
     */
    public void checkForeGift(IRequestCycle cycle)throws Exception {
        IData data = getData();
        IData param=new DataMap();
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER",""));
        param.put("USER_ID_FOR_BALANCE", data.getString("USER_ID_FOR_BALANCE"));
        IData results = CSViewCall.call(this, "SS.ForeGiftSVC.getUserBalance", param).first();
        if (results.getInt("USER_BALANCE") < 0){
            CSViewException.apperr(FeeException.CRM_FEE_170, "该用户有欠款没有缴清，请缴清后再办理！");
        }
        IData  iData=new DataMap();
        iData.put("flag", "1");
        setAjax(iData);
    }




    public abstract void setBalance(String rs);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setForeGifts(IDataset foreGifts);// 用户押金dataset

    public abstract void setInvoice(IData invoice);

    public abstract void setOpTypes(IDataset opTypes);

}
