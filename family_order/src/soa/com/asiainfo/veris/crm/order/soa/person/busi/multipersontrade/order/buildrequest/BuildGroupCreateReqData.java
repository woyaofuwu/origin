package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupMemberData;

public class BuildGroupCreateReqData extends BaseBuilder implements IBuilder{

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
		
		GroupCreateReqData reqData = (GroupCreateReqData) brd;
        UcaData uca = reqData.getUca();
        String mainSn = param.getString("SERIAL_NUMBER");

        // 主卡的
        reqData.setCustName(param.getString("CUST_NAME"));
        reqData.setVerifyMode(param.getString("FMY_VERIFY_MODE"));
        
        //1表示短信认证
        reqData.setVerifyMode(param.getString("CHECK_ADD_MEB"));

        IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));
        if (IDataUtil.isNotEmpty(mebList))
        {
        	for (int i = 0, size = mebList.size(); i < size; i++)
            {
        		IData meb = mebList.getData(i);
                String tag = meb.getString("tag");
                if ("0".equals(tag))
                {
                	// 新增
                    UcaData mebUca = null;
                    String sn = meb.getString("SERIAL_NUMBER_B");
                    
                    // 主号码是否与副号码一致的校验
                    if (StringUtils.equals(mainSn, sn))
                    {
                        CSAppException.apperr(FamilyException.CRM_FAMILY_58);
                    }
                    
                    // 相同成员号码校验
                    for (int j = 0, length = mebList.size(); j < length; j++)
                    {
                        IData tempMeb = mebList.getData(j);
                        String tempSn = tempMeb.getString("SERIAL_NUMBER_B");
                        if (StringUtils.equals(sn, tempSn) && i != j)
                        {
                            CSAppException.apperr(FamilyException.CRM_FAMILY_57);
                        }
                    }
                    mebUca = UcaDataFactory.getNormalUca(sn);

                    GroupMemberData mebData = new GroupMemberData();
                    mebData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    mebData.setUca(mebUca);
                    
                    mebData.setNickName(meb.getString("CUST_NAME_B"));
                    reqData.addMemberData(mebData);
                }
            }
        }
		
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new GroupCreateReqData();
	}

}
