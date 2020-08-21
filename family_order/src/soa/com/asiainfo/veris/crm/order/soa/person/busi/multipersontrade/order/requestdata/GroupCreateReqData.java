package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;

import java.util.ArrayList;
import java.util.List;

public class GroupCreateReqData extends BaseReqData{

    private List<GroupMemberData> memberDataList = new ArrayList<GroupMemberData>();
    
    private DiscntData discntData;// 主卡优惠
    
    private String custName;// 昵称
    
    private String verifyMode;// 副卡校验方式
    
    public void addMemberData(GroupMemberData groupMemberData) {
        this.memberDataList.add(groupMemberData);
    }

	public DiscntData getDiscntData() {
		return discntData;
	}

	public void setDiscntData(DiscntData discntData) {
		this.discntData = discntData;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public List<GroupMemberData> getMemberDataList() {
        return memberDataList;
    }

	public String getVerifyMode() {
		return verifyMode;
	}

	public void setVerifyMode(String verifyMode) {
		this.verifyMode = verifyMode;
	}
    
    
}
