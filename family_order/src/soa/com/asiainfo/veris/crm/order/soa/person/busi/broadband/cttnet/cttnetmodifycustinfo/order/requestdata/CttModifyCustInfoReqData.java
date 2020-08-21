
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifycustinfo.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * 客户资料请求数据
 */
public class CttModifyCustInfoReqData extends BaseReqData
{

    private String isRealName;// 实名制

    private String custName;// 客户名称

    private String psptId;// 证件号码

    private String psptAddr;// 证件地址

    private String postAddress;// 通信地址

    private String homeAddress;// 家庭地址

    private String cityCodeA;// 客户地域

    private String psptTypeCode;// 证件类型

    private String sex;// 客户性别

    private String psptEndDate;// 证件有效期

    private String phone;// 联系电话

    private String contact;// 联系人姓名

    private String contactPhone;// 联系人电话

    private String postCode;// 邮政编码

    private String contactTypeCode;// 优先联系方式

    private String workName;// 工作单位

    private String workDepart;// 工作部门

    private String birthday;// 生日

    private String jobTypeCode;// 职业类型

    private String job;// 职位

    private String educateDegreeCode;// 教育程度

    private String email;// 电子邮件

    private String faxNbr;// 传真号码

    private String marriage;// 婚姻状况

    private String nationalityCode;// 国籍

    private String characterTypeCode;// 性格类型

    private String webuserId;// 网站注册名

    private String languageCode;// 语言

    private String localNativeCode;// 籍贯

    private String communityId;// 社会保障号

    private String religionCode;// 信仰

    private String folkCode;// 民族

    private String revenueLevelCode;// 收入等级
    
    private String agentCustName;// 经办人名称

    private String agentPsptTypeCode;// 经办人证件类型

    private String agentPsptId;// 经办人证件号码

    private String agentPsptAddr;// 经办人证件地址
    
    /*******add by liangdg3 for REQ201908310001关于优化铁通模块客户资料变更界面的需求 at 20190912  start *******/
    private String respCustName;// 责任人姓名

    private String respPsptTypeCode;// 责任人证件类型

    private String respPsptId;//责任人证件号码

    private String respPsptAddr;// 责任人证件地址
    /*******add by liangdg3 for REQ201908310001关于优化铁通模块客户资料变更界面的需求 at 20190912  end *******/
    

    public String getBirthday()
    {
        return birthday;
    }

    public String getCharacterTypeCode()
    {
        return characterTypeCode;
    }

    public String getCityCodeA()
    {
        return cityCodeA;
    }

    public String getCommunityId()
    {
        return communityId;
    }

    public String getContact()
    {
        return contact;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public String getContactTypeCode()
    {
        return contactTypeCode;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getEducateDegreeCode()
    {
        return educateDegreeCode;
    }

    public String getEmail()
    {
        return email;
    }

    public String getFaxNbr()
    {
        return faxNbr;
    }

    public String getFolkCode()
    {
        return folkCode;
    }

    public String getHomeAddress()
    {
        return homeAddress;
    }

    public String getIsRealName()
    {
        return isRealName;
    }

    public String getJob()
    {
        return job;
    }

    public String getJobTypeCode()
    {
        return jobTypeCode;
    }

    public String getLanguageCode()
    {
        return languageCode;
    }

    public String getLocalNativeCode()
    {
        return localNativeCode;
    }

    public String getMarriage()
    {
        return marriage;
    }

    public String getNationalityCode()
    {
        return nationalityCode;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getPostAddress()
    {
        return postAddress;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getPsptAddr()
    {
        return psptAddr;
    }

    public String getPsptEndDate()
    {
        return psptEndDate;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public String getReligionCode()
    {
        return religionCode;
    }

    public String getRevenueLevelCode()
    {
        return revenueLevelCode;
    }

    public String getSex()
    {
        return sex;
    }

    public String getWebuserId()
    {
        return webuserId;
    }

    public String getWorkDepart()
    {
        return workDepart;
    }

    public String getWorkName()
    {
        return workName;
    }

    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }

    public void setCharacterTypeCode(String characterTypeCode)
    {
        this.characterTypeCode = characterTypeCode;
    }

    public void setCityCodeA(String cityCodeA)
    {
        this.cityCodeA = cityCodeA;
    }

    public void setCommunityId(String communityId)
    {
        this.communityId = communityId;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public void setContactTypeCode(String contactTypeCode)
    {
        this.contactTypeCode = contactTypeCode;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setEducateDegreeCode(String educateDegreeCode)
    {
        this.educateDegreeCode = educateDegreeCode;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setFaxNbr(String faxNbr)
    {
        this.faxNbr = faxNbr;
    }

    public void setFolkCode(String folkCode)
    {
        this.folkCode = folkCode;
    }

    public void setHomeAddress(String homeAddress)
    {
        this.homeAddress = homeAddress;
    }

    public void setIsRealName(String isRealName)
    {
        this.isRealName = isRealName;
    }

    public void setJob(String job)
    {
        this.job = job;
    }

    public void setJobTypeCode(String jobTypeCode)
    {
        this.jobTypeCode = jobTypeCode;
    }

    public void setLanguageCode(String languageCode)
    {
        this.languageCode = languageCode;
    }

    public void setLocalNativeCode(String localNativeCode)
    {
        this.localNativeCode = localNativeCode;
    }

    public void setMarriage(String marriage)
    {
        this.marriage = marriage;
    }

    public void setNationalityCode(String nationalityCode)
    {
        this.nationalityCode = nationalityCode;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setPostAddress(String postAddress)
    {
        this.postAddress = postAddress;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setPsptAddr(String psptAddr)
    {
        this.psptAddr = psptAddr;
    }

    public void setPsptEndDate(String psptEndDate)
    {
        this.psptEndDate = psptEndDate;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

    public void setReligionCode(String religionCode)
    {
        this.religionCode = religionCode;
    }

    public void setRevenueLevelCode(String revenueLevelCode)
    {
        this.revenueLevelCode = revenueLevelCode;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public void setWebuserId(String webuserId)
    {
        this.webuserId = webuserId;
    }

    public void setWorkDepart(String workDepart)
    {
        this.workDepart = workDepart;
    }

    public void setWorkName(String workName)
    {
        this.workName = workName;
    }
    
    public String getAgentCustName()
    {
        return agentCustName;
    }

    public String getAgentPsptAddr()
    {
        return agentPsptAddr;
    }

    public String getAgentPsptId()
    {
        return agentPsptId;
    }

    public String getAgentPsptTypeCode()
    {
        return agentPsptTypeCode;
    }
    
    public void setAgentCustName(String agentCustName)
    {
        this.agentCustName = agentCustName;
    }

    public void setAgentPsptAddr(String agentPsptAddr)
    {
        this.agentPsptAddr = agentPsptAddr;
    }

    public void setAgentPsptId(String agentPsptId)
    {
        this.agentPsptId = agentPsptId;
    }

    public void setAgentPsptTypeCode(String agentPsptTypeCode)
    {
        this.agentPsptTypeCode = agentPsptTypeCode;
    }
    //REQ201908310001关于优化铁通模块客户资料变更界面的需求by liangdg3 start
    //新增责任人信息getset方法
	public String getRespCustName() {
		return respCustName;
	}

	public void setRespCustName(String respCustName) {
		this.respCustName = respCustName;
	}

	public String getRespPsptTypeCode() {
		return respPsptTypeCode;
	}

	public void setRespPsptTypeCode(String respPsptTypeCode) {
		this.respPsptTypeCode = respPsptTypeCode;
	}
	
	public String getRespPsptId() {
		return respPsptId;
	}

	public void setRespPsptId(String respPsptId) {
		this.respPsptId = respPsptId;
	}

	public String getRespPsptAddr() {
		return respPsptAddr;
	}

	public void setRespPsptAddr(String respPsptAddr) {
		this.respPsptAddr = respPsptAddr;
	}
	//REQ201908310001关于优化铁通模块客户资料变更界面的需求by liangdg3 end
}
