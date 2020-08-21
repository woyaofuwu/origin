package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class GroupMemberData {
	
	private UcaData uca;// 成员三户资料

    private String modifyTag;// 操作标识

    private DiscntData discntData;// 副卡优惠

    private String nickName;// 昵称

    private String mebVerifyMode;// 界面互联网改造，副卡校验方式： 0-密码；1-短信校验；2-免密码
    
    public DiscntData getDiscntData() {
        return discntData;
    }


    public String getModifyTag() {
        return modifyTag;
    }

    public String getNickName() {
        return nickName;
    }

    public UcaData getUca() {
        return uca;
    }

    public String getMebVerifyMode() {
        return mebVerifyMode;
    }

    public void setDiscntData(DiscntData discntData) {
        this.discntData = discntData;
    }

    public void setModifyTag(String modifyTag) {
        this.modifyTag = modifyTag;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUca(UcaData uca) {
        this.uca = uca;
    }

    public void setMebVerifyMode(String mebVerifyMode) {
        this.mebVerifyMode = mebVerifyMode;
    }

}
