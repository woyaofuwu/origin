function refreshPartAtferAuth(data)
{
  disabledArea("changeData",false);
	
}
//设置身份证信息到页面
function setIdentityCardInfoToHtml(strCardVersion,strPsptId,strBirthday,strName,strSex,strMz,strAddress)
{

	$("#custInfo_CUST_NAME").val(strName);
	$("#custInfo_PSPT_ID").val(strPsptId);
	$("#PSPT_ADDR").val(strAddress);
	$("#custInfo_PSPT_TYPE_CODE").val('0');
}

//清除页面上身份证信息
function clearHtmlIdentityCardInfo()
{
    //修改二代证读取信息
    $("#custInfo_CUST_NAME").val('');
    $("#custInfo_PSPT_ID").val('');
    $("#custinfo_ReadCardFlag").val(0);
}

function checkPsptTypeCode(){
	//实名制开户限制
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	if(psptTypeCode == "Z"){
		$.TipBox.show(document.getElementById('custInfo_PSPT_TYPE_CODE'), "实名制预受理，证件类型不能为其他，请重新选择！", "red");
//		alert("实名制预受理，证件类型不能为其他，请重新选择！");
		$(this).find("option[index=0]").attr("selected", true);
		return;
	}
	
	var custName = $.trim($("#custInfo_CUST_NAME").val());
	if(custName!="" && psptTypeCode != "A"){
    	if(custName.length<2 || !$.toollib.isChinese(custName)){
    		$.TipBox.show(document.getElementById('custInfo_CUST_NAME'), $("#custInfo_PSPT_TYPE_CODE").attr("desc")+"不是护照，"+$("#custInfo_CUST_NAME").attr("desc")+"不能少于2个中文字符！", "red");
//    		alert($("#custInfo_PSPT_TYPE_CODE").attr("desc")+"不是护照，"+$("#custInfo_CUST_NAME").attr("desc")+"不能少于2个中文字符！");
    		$("#custInfo_CUST_NAME").val("");
			return ;
    	}
	}
	//0-本地身份证 1-外地身份证
	if(psptTypeCode==0 || psptTypeCode==1 || psptTypeCode==2) {
		$("#custInfo_PSPT_ID").attr("datatype", "pspt");
		if($("#custInfo_PSPT_ID").val()!="") {
			if(!$.validate.verifyField($("#custInfo_PSPT_ID"))) {
				$("#custInfo_PSPT_ID").val("");
				return;
			}
		}
	}else {
		$("#custInfo_PSPT_ID").attr("datatype", "text");
		$("#custInfo_PSPT_ID").val("");
	}
}

function checkPsptId(){
	var obj = $("#custInfo_PSPT_ID");
	var psptId = $.trim(obj.val());
	var desc = $.trim(obj.attr("desc"));
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	var serialNumber =  $("#AUTH_SERIAL_NUMBER").val();
	if(psptId.length == 0) return;
	
	if($.toollib.isRepeatCode(psptId)){
		$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), desc+"不能全为同一个数字，请重新输入!", "red");
//		alert(desc+"不能全为同一个数字，请重新输入!");
		obj.val("");
		obj.focus();
		return;
	}
	if($.toollib.isSerialCode(psptId)){
		$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "连续数字不能作为"+desc+"，请重新输入!", "red");
//		alert("连续数字不能作为"+desc+"，请重新输入!");
		obj.val("");
		obj.focus();
		return;
	}
	if(psptId.length>=4 && serialNumber.length>=psptId.length && ( serialNumber.indexOf(psptId) ==0 
		|| serialNumber.lastIndexOf(psptId) == (serialNumber.length-psptId.length) )){
		$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为"+desc+"!", "red");
//		alert("电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为"+desc+"!");
		obj.val("");
		obj.focus();
		return;
	}
	
	//港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
	if(psptTypeCode=="H"){
		if(psptId.length!=9 && psptId.length!=11){
			$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "港澳居民回乡证校验："+desc+"必须为9位或11位。", "red");
//			alert("港澳居民回乡证校验："+desc+"必须为9位或11位。");
			obj.val("");
			obj.focus();
			return;
		}
		if( !(psptId.charAt(0)=="H" || psptId.charAt(0)=="M") || !isNumber(psptId.substr(1)) ){
			$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "港澳居民回乡证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。", "red");
//			alert("港澳居民回乡证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
			obj.val("");
			obj.focus();
			return;
		}
	}else if(psptTypeCode=="I"){
		//台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
		if(psptId.length!=8 && psptId.length!=11){
			$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "台湾居民回乡校验："+desc+"必须为8位或11位。", "red");
//			alert("台湾居民回乡校验："+desc+"必须为8位或11位。");
			obj.val("");
			obj.focus();
			return;
		}
		if(psptId.length==8){
			if(!isNumber(psptId)){
				$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "台湾居民回乡校验："+desc+"为8位时，必须均为阿拉伯数字。", "red");
//				alert("台湾居民回乡校验："+desc+"为8位时，必须均为阿拉伯数字。");
				obj.val("");
				obj.focus();
				return;
			}
		}
		if(psptId.length==11){
			if(!isNumber(psptId.substring(0,10))){
				$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "台湾居民回乡校验：："+desc+"为11位时，前10位必须均为阿拉伯数字。", "red");
//				alert("台湾居民回乡校验：："+desc+"为11位时，前10位必须均为阿拉伯数字。");
				obj.val("");
				obj.focus();
				return;
			}
		}
	}else if(psptTypeCode=="C" || psptTypeCode=="A"){
		//军官证、警官证、护照：证件号码须大于等于6位字符
		if(psptId.length < 6){
			var tmpName= psptTypeCode=="A" ? "护照校验：" : "军官证类型校验：";
			$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), tmpName+desc+"须大于等于6位字符!", "red");
//			alert(tmpName+desc+"须大于等于6位字符!");
			obj.val("");
			obj.focus();
			return;
		}
	}else if(psptTypeCode=="E"){
		//营业执照：证件号码长度需满足15位
		if(psptId.length != 15){
			$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "营业执照类型校验："+desc+"长度需满足15位！", "red");
//			alert("营业执照类型校验："+desc+"长度需满足15位！");
			obj.val("");
			obj.focus();
			return;
		}
	}else if(psptTypeCode=="M"){
		//组织机构代码校验
		if(psptId.length != 10){
			$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "组织机构代码证类型校验："+desc+"长度需满足10位。", "red");
//			alert("组织机构代码证类型校验："+desc+"长度需满足10位。");
			obj.val("");
			obj.focus();
			return;
		}
		if(psptId.charAt(8) != "-"){
			$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "组织机构代码证类型校验："+desc+"规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。", "red");
//			alert("组织机构代码证类型校验："+desc+"规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
			obj.val("");
			obj.focus();
			return;
		}
	}else if(psptTypeCode=="G"){
		//事业单位法人登记证书：证件号码长度需满足12位
		if(psptId.length != 12){
			$.TipBox.show(document.getElementById('custInfo_PSPT_ID'), "事业单位法人登记证书类型校验："+desc+"长度需满足12位。", "red");
//			alert("事业单位法人登记证书类型校验："+desc+"长度需满足12位。");
			obj.val("");
			obj.focus();
			return;
		}
	}else if(psptTypeCode==0 || psptTypeCode==1 || psptTypeCode==2) {
		//身份证相关检查
		$("#custInfo_PSPT_ID").attr("datatype", "pspt");
		if(!$.validate.verifyField($("#custInfo_PSPT_ID"))) {
			obj.val("");
			obj.focus();
			return;
		}
	}

}

function checkCustName(){
	var obj = $("#custInfo_CUST_NAME");
	var custName = $.trim(obj.val());
	var desc = obj.attr("desc");
    var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
    var psptTypeDesc = $("#custInfo_PSPT_TYPE_CODE").attr("desc");
    if(!custName) return;
    
    if(psptTypeCode != "A"){
    	var pattern =/[a-zA-Z0-9]/;
    	if(pattern.test(custName)){
    		$.TipBox.show(document.getElementById('custInfo_CUST_NAME'), psptTypeDesc+"不是护照, "+desc+"不能包含数字和字母！", "red");
//    		alert(psptTypeDesc+"不是护照, "+desc+"不能包含数字和字母！");
    		obj.val("");
    		obj.focus();
			return;
    	}
		if(custName.length<2 && !$.toollib.isChinese(custName)){
			$.TipBox.show(document.getElementById('custInfo_CUST_NAME'), psptTypeDesc+"不是护照,"+desc+"不能少于2个中文和字符！", "red");
//			alert(psptTypeDesc+"不是护照,"+desc+"不能少于2个中文和字符！");
			obj.val("");
			obj.focus();
			return;
		}
	}else{
		/*护照：客户名称须大于三个字符，不能全为阿拉伯数字*/
		if(custName.length<3 || $.toollib.isNumber(custName)){
			$.TipBox.show(document.getElementById('custInfo_CUST_NAME'), psptTypeDesc+"是护照,"+desc+"须大于三个字符，且不能全为阿拉伯数字！", "red");
//			alert(psptTypeDesc+"是护照,"+desc+"须大于三个字符，且不能全为阿拉伯数字！");
			obj.val("");
			obj.focus();
			return;
		}
	}
    
    /*if(custName.indexOf("校园")>-1 || custName.indexOf("海南通")>-1 || custName.indexOf("神州行")>-1
    		|| custName.indexOf("动感地带")>-1 || custName.indexOf("套餐")>-1) {
    	$.TipBox.show(document.getElementById('custInfo_CUST_NAME'), desc+"不能包含【校园、海南通、神州行、动感地带、套餐】，请重新输入！", "red");
//        alert(desc+"不能包含【校园、海南通、神州行、动感地带、套餐】，请重新输入！");
        obj.val("");
        obj.focus();
        return false;
    }*/
 
}
function afterCheckCustName(){
	var obj = $("#custInfo_CUST_NAME");
	var custName = $.trim(obj.val());
	if(custName == ""){
		return;
    }
	var specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
	for(i=0;i<specialStr.length;i++){
		if (custName.indexOf(specialStr.charAt(i)) > -1){
			$.TipBox.show(document.getElementById('custInfo_CUST_NAME'), obj.attr("desc")+"包含特殊字符，请检查！", "red");
//			alert(obj.attr("desc")+"包含特殊字符，请检查！");
			obj.val("");
			obj.focus();
			return;
		}
	}
}
function checkPhone()
{    
    if($("#PHONE").val()=="" || $("#PHONE").val()==null){
    	return;
    } 
    $.validate.verifyField($("#PHONE"));
}

function checkAddr()
{
	 var custAddrObj = $('#PSPT_ADDR'); 	
	 if(custAddrObj==null || custAddrObj.val().length<8 || custAddrObj.val()==''){
		 $.TipBox.show(document.getElementById('PSPT_ADDR'), "证件地址栏录入文字需大于8位", "red");
//		 alert('证件地址栏录入文字需大于8位');
		 custAddrObj.val('');
		 custAddrObj.focus();
	 }
	 
	 if(!isNaN(custAddrObj.val())&& custAddrObj.val() != ''){
		 $.TipBox.show(document.getElementById('PSPT_ADDR'), "证件地址栏不能全部为数字", "red");
//		 alert('证件地址栏不能全部为数字');
		 custAddrObj.val('');
		 custAddrObj.focus();
	 }
}

//提交校验
function checkBeforeSubmit()
{  
   if(!$.validate.verifyAll()) return false;
   return true;
}
