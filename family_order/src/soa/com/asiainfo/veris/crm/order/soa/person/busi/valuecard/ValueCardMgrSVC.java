/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard;

import java.math.BigInteger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ValueCardInfoQry;

/**
 * @CREATED by gongp@2014-5-13 修改历史 Revision 2014-5-13 上午11:26:18
 */
public class ValueCardMgrSVC extends CSBizService
{

    private static final long serialVersionUID = -5864192502093904255L;

    /*
     * 唯一性校验 FOR 有价卡赠送工单配置
     */
    private void checkIsExist(String staffID) throws Exception
    {

        IDataset result = ValueCardInfoQry.queryCanGiveValueCardInfoByStaffID(staffID);
        if (IDataUtil.isNotEmpty(result))
        {
            // common.error("审批工号"+staffID+"对应的记录已经存在不能再增加。");
            CSAppException.apperr(CrmCardException.CRM_CARD_263, staffID);
        }
    }

    /**
     * 根据ROWID查询单条配置 FOR 有价卡赠送工单配置
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-5
     */
    public IDataset getCanGiveValueCardInfoByRowId(IData param) throws Exception
    {

        return ValueCardInfoQry.queryCanGiveValueCardInfo(param.getString("ROW_ID"));
    }

    /**
     * 获取赠送总额审批工单及工单余额
     */
    public IDataset getgetWorkOrders(IData param) throws Exception
    {
        ValueCardMgrBean bean = (ValueCardMgrBean) BeanManager.createBean(ValueCardMgrBean.class);

        return bean.getgetWorkOrders();
    }

    /**
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-7
     */
    public IData getResInfoForImport(IData param) throws Exception
    {
        ValueCardMgrBean bean = (ValueCardMgrBean) BeanManager.createBean(ValueCardMgrBean.class);

        return bean.getResInfoForImport(param);

    }

    /**
     * 获取有价卡信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-13
     */
    public IDataset getValueCardInfo(IData param) throws Exception
    {
        ValueCardMgrBean bean = (ValueCardMgrBean) BeanManager.createBean(ValueCardMgrBean.class);

        IData data = bean.getValueCardInfo(param);

        IDataset resultSet = new DatasetList();
        resultSet.add(data);
        return resultSet;
    }

    /**
     * VPMN赠送
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-8-4
     */
    public IData getVPMNGiveValueCardUserInfo(IData param) throws Exception
    {
        ValueCardMgrBean bean = (ValueCardMgrBean) BeanManager.createBean(ValueCardMgrBean.class);

        return bean.getVPMNGiveValueCardUserInfo(param);
    }

    /**
     * 如果该审批工单号已经有赠送有价卡记录则不能修改 FOR 有价卡赠送工单配置
     * 
     * @param pd
     * @param staffID
     * @throws Exception
     */
    public boolean isGiveCard(String staffID) throws Exception
    {
        IDataset givedataset = ValueCardInfoQry.queryGiveCardLogInfosByStaffID(staffID);

        return IDataUtil.isNotEmpty(givedataset);

    }

    /**
     * 根据ROWID修改或者删除单条配置 FOR 有价卡赠送工单配置
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-5
     */
    public IData operCanGiveValueCardInfo(IData param) throws Exception
    {

        ValueCardMgrBean bean = (ValueCardMgrBean) BeanManager.createBean(ValueCardMgrBean.class);

        IData returnData = new DataMap();

        param.put("OPERA_STAFF_ID", getVisit().getStaffId());

        String staffID = param.getString("STAFF_ID", "");

        if ("DELETE".equals(param.getString("OPER_TYPE")))
        {

            int i = bean.deleteCanGiveValueCardInfo(param);

            returnData.put("SUCC_FLAG", i);

        }
        else if ("UPDATE".equals(param.getString("OPER_TYPE")))
        {
            // 有赠送记录则不能修改审批工单号和分公司
            IDataset oldInfos = ValueCardInfoQry.queryCanGiveValueCardInfo(param.getString("ROW_ID"));

            if (IDataUtil.isNotEmpty(oldInfos))
            {
                
                IData oldInfo = oldInfos.getData(0);
                if (isGiveCard(oldInfo.getString("STAFF_ID")))
                {
                    
                    double newValue = Double.parseDouble(param.getString("TOTAL_AMOUNT", "0"));
                    double oldValue = Double.parseDouble(oldInfo.getString("TOTAL_AMOUNT", "0"));
                    if (newValue < oldValue)
                    {
                        // common.error("修改的审批总金额不能少于原来的审核总金额。");
                        CSAppException.apperr(CrmCardException.CRM_CARD_264);
                    }
                }
                else
                {
                    // 审批工单号修改则校验其唯一性
                    if (!staffID.equals(oldInfo.getString("STAFF_ID", "")))
                    {
                        // 校验工号记录是否存在
                        checkIsExist(staffID);
                    }
                    
                    //有价卡赠送审批工单配置 优化需求，新增 时间可选
        			String endDate=param.getString("END_TIME","");
                    endDate=endDate+" 23:59:59";
                	param.put("END_TIME", endDate);
                	
                    //新增备注说明，将备注信息进行追加        
                	String REMARK="";
                	if(oldInfo.getString("REMARK")!= null && oldInfo.getString("REMARK")!=""){
                		REMARK= oldInfo.getString("REMARK");
                	}
                	REMARK=REMARK+param.getString("REMARKS");
                	//数据库长度限制，则先判断，如果超过则进行截取防止插入失败
                	if(REMARK.length()>100){
                		REMARK=REMARK.substring(0, 100);
                	}
                    param.put("REMARKNEW",REMARK);
                    int i = bean.updateCanGiveValueCardInfo(param);
                    returnData.put("SUCC_FLAG", i);
                }
            }
        }
        else if ("ADD".equals(param.getString("OPER_TYPE")))
        {
            //有价卡赠送审批工单配置 优化需求，新增 时间可选
			String endDate=param.getString("END_TIME","");
            endDate=endDate+" 23:59:59";
        	param.put("END_TIME", endDate);
        	//新增备注信息
        	String remark=param.getString("REMARKS","");
        	param.put("REMARKS", remark);
            checkIsExist(param.getString("STAFF_ID"));
            int i = bean.createCanGiveValueCardInfo(param);

            returnData.put("SUCC_FLAG", i);
        }

        return returnData;
    }

    /**
     * 查询有价卡赠送信息 FOR 有价卡赠送工单配置
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-26
     */
    public IDataset qryGiveCardLogInfos(IData param) throws Exception
    {

        return ValueCardInfoQry.queryGiveCardLogInfos(param, this.getPagination());
    }

    /**
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-5
     */
    public IDataset queryCanGiveValueCardInfos(IData param) throws Exception
    {
        return ValueCardInfoQry.queryCanGiveValueCardInfos(param, this.getPagination());

    }

    /**
     * 手机平台有价卡充值接口
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-30
     */
    public IData valuecardRechargeRegByPhone(IData param) throws Exception
    {

        ValueCardMgrIntfBean bean = (ValueCardMgrIntfBean) BeanManager.createBean(ValueCardMgrIntfBean.class);

        return bean.valuecardRechargeRegByPhone(param);
    }
    
    /**
     * REQ201509240012 有价卡赠送日志查询页面优化需求
     * CHENXY3 2015-11-02
     * 查询限制导出数
     */
    public IData getDownloadNum(IData param) throws Exception
    {
    	IData rtnData=new DataMap();
    	IDataset commparas=CommparaInfoQry.getCommparaAllColByParser("CSM","1120", "-1", "0898");
    	if(commparas!=null && commparas.size()>0){
    		rtnData.put("LIMIT_NUM", commparas.getData(0).getString("PARA_CODE1",""));
    	}
    	
    	return rtnData;
    }
    /**
     * 20160618
     * 有价卡赠送，批量加入，表格信息加载
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getImportGiveValueData(IData param) throws Exception{
    	int totalNumbers=0;//批量导入数据统计，用于拦截文件记录过长
    	int startcardnumber;//导入列表起始卡号
    	int endCardNmber;//导入列表结束卡号
    	int TAG_NUMBER = 10000;//有价卡赠送控制参数 发送 
    	IDataset set = new DatasetList(); // 上传excel文件内容明细
        IDataset results = new DatasetList();
        String fileId = param.getString("cond_give_value"); // 上传有价卡赠送excelL文件的编号
        String[] fileIds = fileId.split(",");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        for (String strfileId : fileIds)
        {
            IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/GiveValueCardImport.xml"));
            IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
            set.addAll(suc[0]);
        }
       
        if(set.size() >= 1){
        	
            if ("418".equals(param.getString("TRADE_TYPE_CODE")))
            {
                IDataset dataset = ParamInfoQry.getTagInfoBySubSys("CS_VIEWCON_VALUECARDGIVE_BAT", "CSM", "0", "0898");
                if (dataset != null && dataset.size() == 1)
                {
                    TAG_NUMBER = dataset.getData(0).getInt("TAG_NUMBER", 5000);
                }
         
            }
        	
            for (int i = 0; i < set.size(); i++)
            {
                IData result = new DataMap();
                result.clear();
                
                if("".equals(set.getData(i).getString("START_CARD_NO"))||set.getData(i).getString("START_CARD_NO")==null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1168);
                }
                if("".equals(set.getData(i).getString("END_CARD_NO"))||set.getData(i).getString("END_CARD_NO")== null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1168);
                }
               //客户号码
                if("".equals(set.getData(i).getString("customerNo"))||set.getData(i).getString("customerNo")== null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1168);
                }
/*              //客户姓名
                if("".equals(set.getData(i).getString("customerName"))||set.getData(i).getString("customerName")== null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1168);
                }
                //对应集团名称
                if("".equals(set.getData(i).getString("groupName"))||set.getData(i).getString("groupName")== null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1168);
                }
                //赠送人姓名
                if("".equals(set.getData(i).getString("giveName"))||set.getData(i).getString("giveName")== null){
                	CSAppException.apperr(CrmCommException.CRM_COMM_1168);
                }*/
                
                result.put("START_CARD_NO", set.getData(i).getString("START_CARD_NO"));//起始卡号
                result.put("END_CARD_NO", set.getData(i).getString("END_CARD_NO"));//截止卡号
                
                result.put("customerNo", set.getData(i).getString("customerNo"));//客户号码
                result.put("customerName", set.getData(i).getString("customerName"));//客户姓名
                result.put("groupName", set.getData(i).getString("groupName"));//对应集团名称
                result.put("giveName", set.getData(i).getString("giveName"));//赠送人姓名
                result.put("importFlag", "true");//导入标志
                
                results.add(result);
                  if ("418".equals(param.getString("TRADE_TYPE_CODE")))
              {
                	  
        		  //startcardnumber=Integer.valueOf(set.getData(i).getString("START_CARD_NO"));
        		  //endCardNmber=Integer.valueOf(set.getData(i).getString("END_CARD_NO"));
        		  
                  BigInteger end = new BigInteger(set.getData(i).getString("END_CARD_NO"));
                  BigInteger start = new BigInteger(set.getData(i).getString("START_CARD_NO"));
        		  
                  endCardNmber = end.intValue();
                  startcardnumber =start.intValue();
                  
				  totalNumbers = totalNumbers + (endCardNmber - startcardnumber + 1);
				// 限制插入数据量
				if (totalNumbers >TAG_NUMBER) {
				     CSAppException.apperr(CrmCardException.CRM_CARD_262, TAG_NUMBER);
					throw new Exception("状态:批量导入量过大将导致超时，请缩小数据！ totalNumbers = " + totalNumbers);
				}
		      }
            }
        }else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_1169);
        }
    	return results;
    }
    /**
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkValeCardInfo(IData param) throws Exception
    {
        ValueCardMgrBean bean = (ValueCardMgrBean) BeanManager.createBean(ValueCardMgrBean.class);
        IData data = bean.checkValeCardInfo(param);

        IDataset resultSet = new DatasetList();
        resultSet.add(data);
        
        return resultSet;
    }
    
    
    /**
     * REQ201704270011_关于购买大额有价卡的业务流程优化
     * <br/>
     * 判断总价格是否超过约定的价格
     * @param clcle
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170717
     */    
    public IDataset getCommparaInfo(IData param) throws Exception{
    	
         String  paramAttr=param.getString("PARAM_ATTR");
         
         String  paraCode1=param.getString("PARA_CODE1");

    	return CommparaInfoQry.getCommparaByCode1("CSM", paramAttr, paraCode1, CSBizBean.getTradeEparchyCode());
    }
}
