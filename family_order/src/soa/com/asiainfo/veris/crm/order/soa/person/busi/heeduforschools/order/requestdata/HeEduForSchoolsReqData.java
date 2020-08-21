
package com.asiainfo.veris.crm.order.soa.person.busi.heeduforschools.order.requestdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 *REQ201910140021_关于和教育互动业务成员批量变更归属学校的需求 sundz
 */

public class HeEduForSchoolsReqData extends BaseReqData
{


    private String productId;//产品id
    private String serialNumber;//手机号
    private String  activeTime;//受理时间
    private String  userId;
    private String  selectType;//1==修改，2=新增
    private IData condDataA;
    private IData condDataB;

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public IData getCondDataA() {
        return condDataA;
    }

    public void setCondDataA(IData condDataA) {
        this.condDataA = condDataA;
    }

    public IData getCondDataB() {
        return condDataB;
    }

    public void setCondDataB(IData condDataB) {
        this.condDataB = condDataB;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }
}
