function changeQueryType() {
	var choose=getElement("QueryType").value;
	if (choose=="0") { //按集团编码
	   getElement("selectGroupPart").style.display = "block";
	}
	else if (choose=="1") { //按成员手机号码
	   getElement("selectGroupPart").style.display = "none";
	}
}

function searchMemberBySerialNumber() {
   var serialValue = getElement("cond_SERIAL_NUMBER").value;
   if(isOutPhoneCode(serialValue)) {
      var choose=getElement("QueryType").value;
      Wade.page.beginPageLoading();
      if (choose=="0") { //按集团编码
         ajaxDirect4CS(this, 'queryMemberInfo','&cond_SERIAL_NUMBER='+serialValue,null,true,afterCheckGroupInfo);
      } else { //按成员手机号码
         ajaxDirect4CS(this, 'queryMemberInfo2', '&cond_SERIAL_NUMBER='+serialValue, null,true,afterCheckGroupInfo2);
      }
   }
}

/**
 *判断是否是网外号码
 */
function isOutPhoneCode(obj){
	if(obj.length <= 0){
	 alert('请输入服务号码！');
	 return false;
	}
	return true;
} 

function afterCheckGroupInfo() {
    var x_tag = this.ajaxDataset.get(0, "X_TAG");
	var serial_number = getElement("cond_SERIAL_NUMBER").value;
	getElement("SERVICE_TYPE").disabled = false;
}

function afterCheckGroupInfo2() {
   var shortNum = this.ajaxDataset.get(0, "GROUPINFO_NUM");
   if(shortNum >0) {
      popupDialog('group.destroygroupmember.QueryDestroyGroup', 'queryGroupInfoByBW', '&PARENT_BUTTON=groupChooseButton&cond_SERIAL_NUMBER='+getElement("cond_SERIAL_NUMBER").value+'&refresh='+true, '集团信息', '600', '240');
   }
}

function getGroupInfo(){
	var group = getElement('POP_cond_GROUP_ID').value;
	if (group == ""){
		alert ('请输入正确的集团编码！');
		return false;
	}else{
		Wade.page.beginPageLoading();
		ajaxDirect(this,'getGroupBaseInfo','&cond_GROUP_ID='+group,'CustInfoPart',null,getServiceInfo);
	}
}
/**
 *获取服务列表
 *add by wusf
 */
function getServiceInfo(){
	var group = getElement("POP_cond_GROUP_ID").value;
	if (group == ""){
		alert ("请输入正确的集团编码！");
		return false;
	}else{
		Wade.page.beginPageLoading();
		ajaxDirect(this,'queryGrpUserSvc','&cond_GROUP_ID='+group,'ServicePart',null,initOperType);
	}
}

/**
 *对服务选择框进行控制
 *add by wusf
 */
function initOperType(){
	var serialNumber = getElement("cond_SERIAL_NUMBER").value;
	if(serialNumber != ''){
		getElement("SERVICE_TYPE").disabled = false;
	}
}

/**
 *选择服务类型
 *add by wusf
 */
function changeOperType(){
	var serviceId = getElementValue("SERVICE_TYPE");
	if(serviceId != null && serviceId != ''){
		var serviceInfos = getElementValue("serviceInfos");
		var serialNumber = getElementValue("cond_SERIAL_NUMBER");
		var groupId = getElementValue("POP_cond_GROUP_ID");
		Wade.page.beginPageLoading();
		ajaxDirect(this,'getCurrentServiceInfo','&SERVICE_ID='+serviceId+'&SERIAL_NUMBER='+serialNumber+'&GROUP_ID='+groupId+'&serviceInfos='+serviceInfos,'ServiceParamPart');
	}
}

function chooseGroupButton() {
	var group = getElement("cond_GROUP_ID").value;
	if (group == ""){
		return false;
	}else{
		getElement('POP_cond_GROUP_ID').value=group;
		Wade.page.beginPageLoading();
		ajaxDirect(this,'getGroupBaseInfo','&cond_GROUP_ID='+group,'CustInfoPart',null,getServiceInfo);
	}
}

/**
 *判断是否选择了服务
 *add by wusf
 */
function checkInput(){
	var chooseService = getElementValue("OPER_TYPE");
	if(chooseService == ''){
		alert("请选择服务类型!");
		return false;
	}
	return true;
}

/**
 *判断是否符合手机号码格式
 *add by wusf
 */
function checkMbphone(obj){
	if (!/^[1][13458][0-9](\d{8})$/.test(obj.value) && obj.value != "") {
		alert('手机号码格式不正确！');
		return false;
	}
	return true;
}