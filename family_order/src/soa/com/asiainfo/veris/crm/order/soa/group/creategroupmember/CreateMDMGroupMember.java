
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import java.text.DecimalFormat;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateMDMGroupMember extends CreateGroupMember
{
    private IData paramData = new DataMap();

    /**
     * 构造函数
     * 
     * @author 孙翰韬
     * @param pd
     */
    public CreateMDMGroupMember()
    {
    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        paramData = getParamData();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    public IData getParamData() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paramData))
        {
            return null;
        }
        return paramData;
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR10", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER

    }
    
    /**
     * REQ201803080006关于新增工作手机折扣的需求（NBZQ需求）
     */
    @Override
	protected void setTradeAttr(IData map) throws Exception {
		if(IDataUtil.isNotEmpty(map)){
			String modifyTag = map.getString("MODIFY_TAG", "");
			String instType = map.getString("INST_TYPE", "");
			String attrCode = map.getString("ATTR_CODE", "");
			String attrVal = map.getString("ATTR_VALUE", "0");
			if("D".equals(instType)&&"214485".equals(attrCode) && ("0".equals(modifyTag)||"2".equals(modifyTag))){
				/*判断工号是否有工作手机套餐折扣权限*/
				String tradeStaffId = CSBizBean.getVisit().getStaffId();
				int iAttrVal = Integer.parseInt(attrVal);
				if(iAttrVal>100 || iAttrVal<0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "您填写折扣不正确，请确认!");
				}
				if(!StaffPrivUtil.isPriv(tradeStaffId, "PRIV_MDM_DISCOUNT", "1")){
					if(iAttrVal != 100){
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "您没有MDM手机管控套餐折扣权限，不能办理打折，请确认!");
					}
				}
			}
		}
		super.setTradeAttr(map);
	}


    @Override
    protected void makInit(IData map) throws Exception
    {
        makUcaForMebNormal(map); // 提前查三户
        super.makInit(map);

    }
    /**
     * add by chenzg@20180315 REQ201803020016关于优化集团V网、集团彩铃的集团成员级业务二次确认短信的需求
     */
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        this.paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            if ("1".equals(paramData.getString("TWOCHECK_SMS_FLAG", "")))
            {
                map.put("PAGE_SELECTED_TC", "true");	//页面上选择了下发二次确认短信选项
            }
        }
    }
    @Override
    protected void setTradeDiscnt(IData map) throws Exception
    {
    	super.setTradeDiscnt(map);
    	System.out.println("20180720ff setTradeDiscnt map:"+map);
    	tradeDiscntSeting(map);
    }
    
    private void tradeDiscntSeting(IData map) throws Exception{
    	String discntCode=map.getString("DISCNT_CODE", "");
    	IDataset dataset = reqData.cd.getElementParam();
    	System.out.println("20180717ff1 dataset:"+dataset);
    	
    	String money="";    	
    	IDataset offer_type_code_list = new DatasetList();

        IData iData = new DataMap();
        iData.put("OFFER_CODE", discntCode);
        iData.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
        offer_type_code_list.add(iData);    	
        IData input = new DataMap();
		input.put("OFFER_TYPE_CODE_LIST", offer_type_code_list);
		ServiceResponse response =BizServiceFactory.call("UPC.Out.OfferQueryFSV.qryOfferNamesByOfferTypesOfferCodes", input, null);
        IData out = response.getBody();
        System.out.println("20180717ff1 out:"+out);
        IDataset outList=out.getDataset("OUTDATA");
        String discntCodeName="相关优惠";
        if(!IDataUtil.isEmpty(outList)){
        	discntCodeName=outList.getData(0).getString("OFFER_NAME","");
        }
        
        
    	for (int row = 0, size = dataset.size(); row < size; row++)
        {
    		IData idata = dataset.getData(row);
    		if(idata.getString("RELA_INST_ID", "").equals(map.getString("INST_ID", ""))){
    			String attrVlaue=idata.getString("ATTR_VALUE", "");
        		if(idata.getString("ATTR_CODE", "").equals("254485")){//金额
        			DecimalFormat df=new DecimalFormat("#.00");
        			double moneyDouble=Double.parseDouble(attrVlaue);
        			money="月功能费"+df.format(moneyDouble/100)+"元。";
        		}
    		}
    		
        }
    	if(discntCode.equals("84071842")){
    		String remark=discntCodeName+"，";
    		if(!money.equals("")){
    			remark+=money;
    		}
    		map.put("REMARK",remark);
    	} 
    	System.out.println("20180720ff setTradeDiscnt map-REMARK:"+map.getString("REMARK", ""));

    	
    }

}
