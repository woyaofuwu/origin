
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class CustPersonTradeData extends BaseTradeData
{
    private String birthday;

    private String birthdayFlag;

    private String birthdayLunar;

    private String callingTypeCode;

    private String characterTypeCode;

    private String cityCode;

    private String communityId;

    private String contact;

    private String contactFreq;

    private String contactPhone;

    private String contactTypeCode;

    private String custId;

    private String custName;

    private String educateDegreeCode;

    private String educateGradeCode;

    private String email;

    private String eparchyCode;

    private String faxNbr;

    private String folkCode;

    private String graduateSchool;

    private String healthStateCode;

    private String homeAddress;

    private String homePhone;

    private String job;

    private String jobTypeCode;

    private String languageCode;

    private String localNativeCode;

    private String marriage;

    private String modifyTag;

    private String nationalityCode;

    private String phone;

    private String population;

    private String postAddress;

    private String postCode;

    private String postPerson;

    private String psptAddr;

    private String psptEndDate;

    private String psptId;

    private String psptTypeCode;

    private String religionCode;

    private String remark;

    private String removeChange;

    private String removeDate;

    private String removeStaffId;

    private String removeTag;

    private String revenueLevelCode;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String sex;

    private String speciality;

    private String subCallingTypeCode;

    private String webuserId;

    private String webPasswd;

    private String workAddress;

    private String workDepart;

    private String workKind;

    private String workName;

    private String workPhone;

    private String workPostCode;

    public CustPersonTradeData()
    {

    }

    public CustPersonTradeData(IData data)
    {
        this.setBirthday(data.getString("BIRTHDAY"));
        this.setBirthdayFlag(data.getString("BIRTHDAY_FLAG"));
        this.setBirthdayLunar(data.getString("BIRTHDAY_LUNAR"));
        this.setCallingTypeCode(data.getString("CALLING_TYPE_CODE"));
        this.setCharacterTypeCode(data.getString("CHARACTER_TYPE_CODE"));
        this.setCityCode(data.getString("CITY_CODE"));
        this.setCommunityId(data.getString("COMMUNITY_ID"));
        this.setContact(data.getString("CONTACT"));
        this.setContactFreq(data.getString("CONTACT_FREQ"));
        this.setContactPhone(data.getString("CONTACT_PHONE"));
        this.setContactTypeCode(data.getString("CONTACT_TYPE_CODE"));
        this.setCustId(data.getString("CUST_ID"));
        this.setCustName(data.getString("CUST_NAME"));
        this.setEducateDegreeCode(data.getString("EDUCATE_DEGREE_CODE"));
        this.setEducateGradeCode(data.getString("EDUCATE_GRADE_CODE"));
        this.setEmail(data.getString("EMAIL"));
        this.setEparchyCode(data.getString("EPARCHY_CODE"));
        this.setFaxNbr(data.getString("FAX_NBR"));
        this.setFolkCode(data.getString("FOLK_CODE"));
        this.setGraduateSchool(data.getString("GRADUATE_SCHOOL"));
        this.setHealthStateCode(data.getString("HEALTH_STATE_CODE"));
        this.setHomeAddress(data.getString("HOME_ADDRESS"));
        this.setHomePhone(data.getString("HOME_PHONE"));
        this.setJob(data.getString("JOB"));
        this.setJobTypeCode(data.getString("JOB_TYPE_CODE"));
        this.setLanguageCode(data.getString("LANGUAGE_CODE"));
        this.setLocalNativeCode(data.getString("LOCAL_NATIVE_CODE"));
        this.setMarriage(data.getString("MARRIAGE"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setNationalityCode(data.getString("NATIONALITY_CODE"));
        this.setPhone(data.getString("PHONE"));
        this.setPopulation(data.getString("POPULATION"));
        this.setPostAddress(data.getString("POST_ADDRESS"));
        this.setPostCode(data.getString("POST_CODE"));
        this.setPostPerson(data.getString("POST_PERSON"));
        this.setPsptAddr(data.getString("PSPT_ADDR"));
        this.setPsptEndDate(data.getString("PSPT_END_DATE"));
        this.setPsptId(data.getString("PSPT_ID"));
        this.setPsptTypeCode(data.getString("PSPT_TYPE_CODE"));
        this.setReligionCode(data.getString("RELIGION_CODE"));
        this.setRemark(data.getString("REMARK"));
        this.setRemoveChange(data.getString("REMOVE_CHANGE"));
        this.setRemoveDate(data.getString("REMOVE_DATE"));
        this.setRemoveStaffId(data.getString("REMOVE_STAFF_ID"));
        this.setRemoveTag(data.getString("REMOVE_TAG"));
        this.setRevenueLevelCode(data.getString("REVENUE_LEVEL_CODE"));
        this.setRsrvDate1(data.getString("RSRV_DATE1"));
        this.setRsrvDate2(data.getString("RSRV_DATE2"));
        this.setRsrvDate3(data.getString("RSRV_DATE3"));
        this.setRsrvNum1(data.getString("RSRV_NUM1"));
        this.setRsrvNum2(data.getString("RSRV_NUM2"));
        this.setRsrvNum3(data.getString("RSRV_NUM3"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvStr6(data.getString("RSRV_STR6"));
        this.setRsrvStr7(data.getString("RSRV_STR7"));
        this.setRsrvStr8(data.getString("RSRV_STR8"));
        this.setRsrvTag1(data.getString("RSRV_TAG1"));
        this.setRsrvTag2(data.getString("RSRV_TAG2"));
        this.setRsrvTag3(data.getString("RSRV_TAG3"));
        this.setSex(data.getString("SEX"));
        this.setSpeciality(data.getString("SPECIALITY"));
        this.setSubCallingTypeCode(data.getString("SUB_CALLING_TYPE_CODE"));
        this.setWebuserId(data.getString("WEBUSER_ID"));
        this.setWebPasswd(data.getString("WEB_PASSWD"));
        this.setWorkAddress(data.getString("WORK_ADDRESS"));
        this.setWorkDepart(data.getString("WORK_DEPART"));
        this.setWorkKind(data.getString("WORK_KIND"));
        this.setWorkName(data.getString("WORK_NAME"));
        this.setWorkPhone(data.getString("WORK_PHONE"));
        this.setWorkPostCode(data.getString("WORK_POST_CODE"));
    }

    public CustPersonTradeData clone()
    {
        CustPersonTradeData custPersonTradeData = new CustPersonTradeData();
        custPersonTradeData.setBirthday(this.getBirthday());
        custPersonTradeData.setBirthdayFlag(this.getBirthdayFlag());
        custPersonTradeData.setBirthdayLunar(this.getBirthdayLunar());
        custPersonTradeData.setCallingTypeCode(this.getCallingTypeCode());
        custPersonTradeData.setCharacterTypeCode(this.getCharacterTypeCode());
        custPersonTradeData.setCityCode(this.getCityCode());
        custPersonTradeData.setCommunityId(this.getCommunityId());
        custPersonTradeData.setContact(this.getContact());
        custPersonTradeData.setContactFreq(this.getContactFreq());
        custPersonTradeData.setContactPhone(this.getContactPhone());
        custPersonTradeData.setContactTypeCode(this.getContactTypeCode());
        custPersonTradeData.setCustId(this.getCustId());
        custPersonTradeData.setCustName(this.getCustName());
        custPersonTradeData.setEducateDegreeCode(this.getEducateDegreeCode());
        custPersonTradeData.setEducateGradeCode(this.getEducateGradeCode());
        custPersonTradeData.setEmail(this.getEmail());
        custPersonTradeData.setEparchyCode(this.getEparchyCode());
        custPersonTradeData.setFaxNbr(this.getFaxNbr());
        custPersonTradeData.setFolkCode(this.getFolkCode());
        custPersonTradeData.setGraduateSchool(this.getGraduateSchool());
        custPersonTradeData.setHealthStateCode(this.getHealthStateCode());
        custPersonTradeData.setHomeAddress(this.getHomeAddress());
        custPersonTradeData.setHomePhone(this.getHomePhone());
        custPersonTradeData.setJob(this.getJob());
        custPersonTradeData.setJobTypeCode(this.getJobTypeCode());
        custPersonTradeData.setLanguageCode(this.getLanguageCode());
        custPersonTradeData.setLocalNativeCode(this.getLocalNativeCode());
        custPersonTradeData.setMarriage(this.getMarriage());
        custPersonTradeData.setModifyTag(this.getModifyTag());
        custPersonTradeData.setNationalityCode(this.getNationalityCode());
        custPersonTradeData.setPhone(this.getPhone());
        custPersonTradeData.setPopulation(this.getPopulation());
        custPersonTradeData.setPostAddress(this.getPostAddress());
        custPersonTradeData.setPostCode(this.getPostCode());
        custPersonTradeData.setPostPerson(this.getPostPerson());
        custPersonTradeData.setPsptAddr(this.getPsptAddr());
        custPersonTradeData.setPsptEndDate(this.getPsptEndDate());
        custPersonTradeData.setPsptId(this.getPsptId());
        custPersonTradeData.setPsptTypeCode(this.getPsptTypeCode());
        custPersonTradeData.setReligionCode(this.getReligionCode());
        custPersonTradeData.setRemark(this.getRemark());
        custPersonTradeData.setRemoveChange(this.getRemoveChange());
        custPersonTradeData.setRemoveDate(this.getRemoveDate());
        custPersonTradeData.setRemoveStaffId(this.getRemoveStaffId());
        custPersonTradeData.setRemoveTag(this.getRemoveTag());
        custPersonTradeData.setRevenueLevelCode(this.getRevenueLevelCode());
        custPersonTradeData.setRsrvDate1(this.getRsrvDate1());
        custPersonTradeData.setRsrvDate2(this.getRsrvDate2());
        custPersonTradeData.setRsrvDate3(this.getRsrvDate3());
        custPersonTradeData.setRsrvNum1(this.getRsrvNum1());
        custPersonTradeData.setRsrvNum2(this.getRsrvNum2());
        custPersonTradeData.setRsrvNum3(this.getRsrvNum3());
        custPersonTradeData.setRsrvStr1(this.getRsrvStr1());
        custPersonTradeData.setRsrvStr2(this.getRsrvStr2());
        custPersonTradeData.setRsrvStr3(this.getRsrvStr3());
        custPersonTradeData.setRsrvStr4(this.getRsrvStr4());
        custPersonTradeData.setRsrvStr5(this.getRsrvStr5());
        custPersonTradeData.setRsrvStr6(this.getRsrvStr6());
        custPersonTradeData.setRsrvStr7(this.getRsrvStr7());
        custPersonTradeData.setRsrvStr8(this.getRsrvStr8());
        custPersonTradeData.setRsrvTag1(this.getRsrvTag1());
        custPersonTradeData.setRsrvTag2(this.getRsrvTag2());
        custPersonTradeData.setRsrvTag3(this.getRsrvTag3());
        custPersonTradeData.setSex(this.getSex());
        custPersonTradeData.setSpeciality(this.getSpeciality());
        custPersonTradeData.setSubCallingTypeCode(this.getSubCallingTypeCode());
        custPersonTradeData.setWebuserId(this.getWebuserId());
        custPersonTradeData.setWebPasswd(this.getWebPasswd());
        custPersonTradeData.setWorkAddress(this.getWorkAddress());
        custPersonTradeData.setWorkDepart(this.getWorkDepart());
        custPersonTradeData.setWorkKind(this.getWorkKind());
        custPersonTradeData.setWorkName(this.getWorkName());
        custPersonTradeData.setWorkPhone(this.getWorkPhone());
        custPersonTradeData.setWorkPostCode(this.getWorkPostCode());
        return custPersonTradeData;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public String getBirthdayFlag()
    {
        return birthdayFlag;
    }

    public String getBirthdayLunar()
    {
        return birthdayLunar;
    }

    public String getCallingTypeCode()
    {
        return callingTypeCode;
    }

    public String getCharacterTypeCode()
    {
        return characterTypeCode;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getCommunityId()
    {
        return communityId;
    }

    public String getContact()
    {
        return contact;
    }

    public String getContactFreq()
    {
        return contactFreq;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public String getContactTypeCode()
    {
        return contactTypeCode;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getEducateDegreeCode()
    {
        return educateDegreeCode;
    }

    public String getEducateGradeCode()
    {
        return educateGradeCode;
    }

    public String getEmail()
    {
        return email;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getFaxNbr()
    {
        return faxNbr;
    }

    public String getFolkCode()
    {
        return folkCode;
    }

    public String getGraduateSchool()
    {
        return graduateSchool;
    }

    public String getHealthStateCode()
    {
        return healthStateCode;
    }

    public String getHomeAddress()
    {
        return homeAddress;
    }

    public String getHomePhone()
    {
        return homePhone;
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

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getNationalityCode()
    {
        return nationalityCode;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getPopulation()
    {
        return population;
    }

    public String getPostAddress()
    {
        return postAddress;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getPostPerson()
    {
        return postPerson;
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

    public String getRemark()
    {
        return remark;
    }

    public String getRemoveChange()
    {
        return removeChange;
    }

    public String getRemoveDate()
    {
        return removeDate;
    }

    public String getRemoveStaffId()
    {
        return removeStaffId;
    }

    public String getRemoveTag()
    {
        return removeTag;
    }

    public String getRevenueLevelCode()
    {
        return revenueLevelCode;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
    }

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return rsrvStr8;
    }

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public String getSex()
    {
        return sex;
    }

    public String getSpeciality()
    {
        return speciality;
    }

    public String getSubCallingTypeCode()
    {
        return subCallingTypeCode;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_CUST_PERSON";
    }

    public String getWebPasswd()
    {
        return webPasswd;
    }

    public String getWebuserId()
    {
        return webuserId;
    }

    public String getWorkAddress()
    {
        return workAddress;
    }

    public String getWorkDepart()
    {
        return workDepart;
    }

    public String getWorkKind()
    {
        return workKind;
    }

    public String getWorkName()
    {
        return workName;
    }

    public String getWorkPhone()
    {
        return workPhone;
    }

    public String getWorkPostCode()
    {
        return workPostCode;
    }

    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }

    public void setBirthdayFlag(String birthdayFlag)
    {
        this.birthdayFlag = birthdayFlag;
    }

    public void setBirthdayLunar(String birthdayLunar)
    {
        this.birthdayLunar = birthdayLunar;
    }

    public void setCallingTypeCode(String callingTypeCode)
    {
        this.callingTypeCode = callingTypeCode;
    }

    public void setCharacterTypeCode(String characterTypeCode)
    {
        this.characterTypeCode = characterTypeCode;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCommunityId(String communityId)
    {
        this.communityId = communityId;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public void setContactFreq(String contactFreq)
    {
        this.contactFreq = contactFreq;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public void setContactTypeCode(String contactTypeCode)
    {
        this.contactTypeCode = contactTypeCode;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setEducateDegreeCode(String educateDegreeCode)
    {
        this.educateDegreeCode = educateDegreeCode;
    }

    public void setEducateGradeCode(String educateGradeCode)
    {
        this.educateGradeCode = educateGradeCode;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setFaxNbr(String faxNbr)
    {
        this.faxNbr = faxNbr;
    }

    public void setFolkCode(String folkCode)
    {
        this.folkCode = folkCode;
    }

    public void setGraduateSchool(String graduateSchool)
    {
        this.graduateSchool = graduateSchool;
    }

    public void setHealthStateCode(String healthStateCode)
    {
        this.healthStateCode = healthStateCode;
    }

    public void setHomeAddress(String homeAddress)
    {
        this.homeAddress = homeAddress;
    }

    public void setHomePhone(String homePhone)
    {
        this.homePhone = homePhone;
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

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setNationalityCode(String nationalityCode)
    {
        this.nationalityCode = nationalityCode;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setPopulation(String population)
    {
        this.population = population;
    }

    public void setPostAddress(String postAddress)
    {
        this.postAddress = postAddress;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setPostPerson(String postPerson)
    {
        this.postPerson = postPerson;
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

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRemoveChange(String removeChange)
    {
        this.removeChange = removeChange;
    }

    public void setRemoveDate(String removeDate)
    {
        this.removeDate = removeDate;
    }

    public void setRemoveStaffId(String removeStaffId)
    {
        this.removeStaffId = removeStaffId;
    }

    public void setRemoveTag(String removeTag)
    {
        this.removeTag = removeTag;
    }

    public void setRevenueLevelCode(String revenueLevelCode)
    {
        this.revenueLevelCode = revenueLevelCode;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public void setRsrvNum2(String rsrvNum2)
    {
        this.rsrvNum2 = rsrvNum2;
    }

    public void setRsrvNum3(String rsrvNum3)
    {
        this.rsrvNum3 = rsrvNum3;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setRsrvStr8(String rsrvStr8)
    {
        this.rsrvStr8 = rsrvStr8;
    }

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public void setSpeciality(String speciality)
    {
        this.speciality = speciality;
    }

    public void setSubCallingTypeCode(String subCallingTypeCode)
    {
        this.subCallingTypeCode = subCallingTypeCode;
    }

    public void setWebPasswd(String webPasswd)
    {
        this.webPasswd = webPasswd;
    }

    public void setWebuserId(String webuserId)
    {
        this.webuserId = webuserId;
    }

    public void setWorkAddress(String workAddress)
    {
        this.workAddress = workAddress;
    }

    public void setWorkDepart(String workDepart)
    {
        this.workDepart = workDepart;
    }

    public void setWorkKind(String workKind)
    {
        this.workKind = workKind;
    }

    public void setWorkName(String workName)
    {
        this.workName = workName;
    }

    public void setWorkPhone(String workPhone)
    {
        this.workPhone = workPhone;
    }

    public void setWorkPostCode(String workPostCode)
    {
        this.workPostCode = workPostCode;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("BIRTHDAY", this.birthday);
        data.put("BIRTHDAY_FLAG", this.birthdayFlag);
        data.put("BIRTHDAY_LUNAR", this.birthdayLunar);
        data.put("CALLING_TYPE_CODE", this.callingTypeCode);
        data.put("CHARACTER_TYPE_CODE", this.characterTypeCode);
        data.put("CITY_CODE", this.cityCode);
        data.put("COMMUNITY_ID", this.communityId);
        data.put("CONTACT", this.contact);
        data.put("CONTACT_FREQ", this.contactFreq);
        data.put("CONTACT_PHONE", this.contactPhone);
        data.put("CONTACT_TYPE_CODE", this.contactTypeCode);
        data.put("CUST_ID", this.custId);
        data.put("CUST_NAME", this.custName);
        data.put("EDUCATE_DEGREE_CODE", this.educateDegreeCode);
        data.put("EDUCATE_GRADE_CODE", this.educateGradeCode);
        data.put("EMAIL", this.email);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("FAX_NBR", this.faxNbr);
        data.put("FOLK_CODE", this.folkCode);
        data.put("GRADUATE_SCHOOL", this.graduateSchool);
        data.put("HEALTH_STATE_CODE", this.healthStateCode);
        data.put("HOME_ADDRESS", this.homeAddress);
        data.put("HOME_PHONE", this.homePhone);
        data.put("JOB", this.job);
        data.put("JOB_TYPE_CODE", this.jobTypeCode);
        data.put("LANGUAGE_CODE", this.languageCode);
        data.put("LOCAL_NATIVE_CODE", this.localNativeCode);
        data.put("MARRIAGE", this.marriage);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("NATIONALITY_CODE", this.nationalityCode);
        data.put("PHONE", this.phone);
        data.put("POPULATION", this.population);
        data.put("POST_ADDRESS", this.postAddress);
        data.put("POST_CODE", this.postCode);
        data.put("POST_PERSON", this.postPerson);
        data.put("PSPT_ADDR", this.psptAddr);
        data.put("PSPT_END_DATE", this.psptEndDate);
        data.put("PSPT_ID", this.psptId);
        data.put("PSPT_TYPE_CODE", this.psptTypeCode);
        data.put("RELIGION_CODE", this.religionCode);
        data.put("REMARK", this.remark);
        data.put("REMOVE_CHANGE", this.removeChange);
        data.put("REMOVE_DATE", this.removeDate);
        data.put("REMOVE_STAFF_ID", this.removeStaffId);
        data.put("REMOVE_TAG", this.removeTag);
        data.put("REVENUE_LEVEL_CODE", this.revenueLevelCode);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("SEX", this.sex);
        data.put("SPECIALITY", this.speciality);
        data.put("SUB_CALLING_TYPE_CODE", this.subCallingTypeCode);
        data.put("WEBUSER_ID", this.webuserId);
        data.put("WEB_PASSWD", this.webPasswd);
        data.put("WORK_ADDRESS", this.workAddress);
        data.put("WORK_DEPART", this.workDepart);
        data.put("WORK_KIND", this.workKind);
        data.put("WORK_NAME", this.workName);
        data.put("WORK_PHONE", this.workPhone);
        data.put("WORK_POST_CODE", this.workPostCode);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
