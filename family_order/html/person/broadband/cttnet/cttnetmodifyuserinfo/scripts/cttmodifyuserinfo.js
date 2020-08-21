function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'ModifyUserInfoPart,ModifyUserInfoPart2', function(data){
		$("#USER_TYPE_CODE").attr("disabled", false);
		$("#AREA_TYPE").attr("disabled", false);
		$("#CLEAR_ACCOUNT").attr("disabled", false);
		$("#ADD_BTN").attr("disabled", false);
		$("#DEL_BTN").attr("disabled", false);
		$("#REMARK").attr("disabled", false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//新增担保人信息
function addAssure()
{
	 $("#ASSURE_PSPT_TYPE_CODE").attr("disabled", false);
	 $("#ASSURE_PSPT_ID").attr("disabled", false);
	 $("#ASSURE_NAME").attr("disabled", false);
	 $("#ASSURE_TYPE_CODE").attr("disabled", false);
	 $("#ASSURE_DATE").attr("disabled", false);
	 
	 $("#ASSURE_PSPT_TYPE_CODE").val("");
	 $("#ASSURE_PSPT_ID").val("");
	 $("#ASSURE_NAME").val("");
	 $("#ASSURE_TYPE_CODE").val("");
	 $("#ASSURE_DATE").val("");
	 
	 $("#ASSURE_PSPT_ID").attr("nullable", "no");
	 $("#ASSURE_PSPT_TYPE_CODE").attr("nullable", "no");
	 $("#ASSURE_NAME").attr("nullable", "no");
}

//删除担保人信息 
function deleteAssure()
{
	 $("#ASSURE_PSPT_TYPE_CODE").attr("disabled", true);
	 $("#ASSURE_PSPT_ID").attr("disabled", true);
	 $("#ASSURE_NAME").attr("disabled", true);
	 $("#ASSURE_TYPE_CODE").attr("disabled", true);
	 $("#ASSURE_DATE").attr("disabled", true);
	 
	 $("#ASSURE_PSPT_TYPE_CODE").val("");
	 $("#ASSURE_PSPT_ID").val("");
	 $("#ASSURE_NAME").val("");
	 $("#ASSURE_TYPE_CODE").val("");
	 $("#ASSURE_DATE").val("");
	 
	 $("#ASSURE_PSPT_ID").attr("nullable", "yes");
	 $("#ASSURE_PSPT_TYPE_CODE").attr("nullable", "yes");
	 $("#ASSURE_NAME").attr("nullable", "yes");
}

//担保证件类型 
function changeTypeCode()
{
     $("#ASSURE_PSPT_ID").val("");
	 $("#ASSURE_NAME").val("");
	 $("#ASSURE_TYPE_CODE").val("");
	 $("#ASSURE_DATE").val("");
	 
	var assurePsptTypeCode = $("#ASSURE_PSPT_TYPE_CODE").val();
	//如果是身份证，则做身份证格式校验
	if(assurePsptTypeCode=="0" || assurePsptTypeCode=="1") {
		$("#ASSURE_PSPT_ID").attr("datatype", "pspt");
	}
	else {
		$("#ASSURE_PSPT_ID").attr("datatype", "text");
	}
}

//证件号码校验
function ckeckPsptNum()
{
   if(!$.validate.verifyField($("#ASSURE_PSPT_ID")[0])) {
   		$("#ASSURE_PSPT_ID").val("");
		return false;
	}
	else {
		//黑名单校验
		$.ajax.submit('AssurePart,AuthPart', 'isBlackUser', null, null, function(data) {
			var data0 = data.get(0);
			var blackType = data0.get("X_IS_BLACK_USER");
		    if(blackType=="1")
		    {
		      $("#ASSURE_PSPT_ID").val("");
		      alert("该担保人用户为黑名单客户!");     
		    }else
		    {  
		      var assureName=data0.get("ASSURE_NAME");
		      var psptUseCount= data0.get("PSPT_COUNT");
		      
		      if (assureName!="")
		      {
		    	  $("#ASSURE_NAME").val(assureName);
				  $("#ASSURE_NAME").attr("disabled", true);

		      }else
		      {
		         if (psptUseCount=="0")
		         {
		        	 $("#ASSURE_NAME").val('');
		   		   	 $("#ASSURE_NAME").attr("disabled", false);
		         }else
		         {            
		            if(confirm('是否选择同客户？'))
		            {
		                var param = '&multi=false&PSPT_TYPE_CODE='+ $("#ASSURE_PSPT_TYPE_CODE").val()+'&PSPT_ID='+$("#ASSURE_PSPT_ID").val();
		    	        popupPage('custlist.CustListPage', 'queryCustList', param, '客户信息列表', '640', '280','ASSURE_NAME');//TODO
		    	        
		            }else
		            {
		            	$("#ASSURE_PSPT_TYPE_CODE").val('');
		            	$("#ASSURE_PSPT_ID").val('');
		            }
		            
		         }
		         $("#ASSURE_NAME").attr("nullable", "no"); 
		      }
		    }
		},
		function(error_code,error_info){
			$.endPageLoading();
			$("#ASSURE_PSPT_ID").val("");
			alert(error_info);
			
	    });
	}
}

/* 判断证件类型和证件号码是否已经被用过了 */
function isPsptUsed(objB) {
	debugger;
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var psptTypeObj = $("#custInfo_PSPT_TYPE_CODE");
	if(objB.id=='custInfo_AGENT_PSPT_ID'){//经办人证件号码校验
		psptTypeObj = $("#custInfo_AGENT_PSPT_TYPE_CODE");
	}
	var desc = objB.getAttribute('desc', '证件号码')+'校验:';
	var psptTypeCode = psptTypeObj.val();
	var psptId = $(objB).val();
	// 1.重复号码：如"666666"或"000000"等
	if(psptId!=""){
		alert(psptId);
		return false;
	}
	if( psptId =="" ){
		if(checkRepeatNumber(psptId)){
			alert(desc+'证件号码，不能全为同一个数字，请重新输入!');
			$(objB).val('');
			return false;
		}
		// 2.证件号码不能为手机号码的前N位或后N位
		if(checkContainInSnQh4Bit(psptId, serialNumber)){
			alert(desc+'电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为证件号码!');
			$(objB).val('');
	 		return false;
		}
		//3.连号：如"123456"或"345678"等
		if(checkSerieisNumbermc(psptId)){
			alert(desc+'连续数字不能作为证件号码，请重新输入!');
			$(objB).val('');
			return false;
		}
		
		/*港澳居民回乡证：证件号码为9位或11位。首位为英文字母“H”或“M”；其余位均为阿拉伯数字*/
		if(psptTypeCode=="H"){
			if(psptId.length!=9&&psptId.length!=11){
				alert('港澳居民回乡证校验：'+objB.desc+'必须为9位或11位。');
				$(objB).val('');
				return false;
			}
			if(!( (psptId.charAt(0)=="H"||psptId.charAt(0)=="M")&&(!isNaN(psptId.substr(1))) )){
				alert('港澳居民回乡证校验：'+objB.desc+'首位必须为英文字母“H”或“M”,其余位均为阿拉伯数字。');
				$(objB).val('');
				return false;
			}
		}
		/*台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。*/
		if(psptTypeCode=="I"){
			if(psptId.length!=8&&psptId.length!=11){
				alert('台湾居民回乡校验：'+objB.desc+'必须为8位或11位。');
				$(objB).val('');
				return false;
			}
			if(psptId.length==8){
				if(isNaN(psptId)){
					alert('台湾居民回乡校验：'+objB.desc+'为8位时，必须均为阿拉伯数字。');
					$(objB).val('');
					return false;
				}
			}
			if(psptId.length==11){
				alert(psptId.substring(0,10));
				if(isNaN(psptId.substring(0,10))){
					alert('台湾居民回乡校验：'+objB.desc+'为11位时，前10位必须均为阿拉伯数字。');
					$(objB).val('');
					return false;
				}
			}
		}
		/*军官证、警官证、护照：证件号码须大于等于6位字符。*/
		if(psptTypeCode=="C" || psptTypeCode=="A"){
			if(psptId.length < 6){
				alert(psptTypeCode=="A" ? "护照校验："+objB.desc+"须大于等于6位字符。" : "军官证类型校验："+objB.desc+"须大于等于6位字符。");
				$(objB).val('');
				return false;
			}
		}
		/*营业执照：证件号码长度需满足15位*/
		if(psptTypeCode=="E"){
			if(psptId.length != 15){
				alert('营业执照类型校验：'+objB.desc+'长度需满足15位。');
				$(objB).val('');
				return false;
			}
		}
		/*组织机构代码证：证件号码长度需满足10位，其规则为“XXXXXXXX-X”，倒数第2位是“-”*/
		if(psptTypeCode=="M"){
			if(psptId.length != 10){
				alert('组织机构代码证类型校验：'+objB.desc+'长度需满足10位。');
				$(objB).val('');
				return false;
			}
			if(psptId.charAt(8) != '-'){
				alert('组织机构代码证类型校验：'+objB.desc+'规则为“XXXXXXXX-X”，倒数第2位是“-”。');
				$(objB).val('');
				return false;
			}
		}
		/*事业单位法人登记证书：证件号码长度需满足12位*/
		if(psptTypeCode=="G"){
			if(psptId.length != 12){
				alert('事业单位法人登记证书类型校验：'+objB.desc+'长度需满足12位。');
				$(objB).val('');
				return false;
			}
		}
		
		/*--身份证件类型新增户口本*/
		// 户口本证件类型，证件号码填的也是身份证，只是不限制必须大于15岁
		if (psptTypeCode != '2' && !verifyField(objB))
			return false;
		if (psptTypeCode == '2' && !checkPsptHain(objB))
			return false;
	}

	/*身份证件类型新增户口本*/
	if(objB.id=='custInfo_PSPT_ID'){
		// 海南允许一个证件号码 多个用户使用， 不需要办理客户合并业务
		if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2")// 当前证件类型为身份证或户口本的自动设置生日
		{
			setBirthdayByPspt();// 设置生日
		}
		// 根据身份证号，自动设置性别
		if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2") {
			if ($("#custInfo_PSPT_ID").val().length == 18
					&& ($("#custInfo_PSPT_ID").val().charAt(16) % 2 == 0)) {
				$("#custInfo_SEX").val('F');
			}
			if ($("#custInfo_PSPT_ID").val().length == 18
					&& ($("#custInfo_PSPT_ID").val().charAt(16) % 2 == 1)) {
				$("#custInfo_SEX").val('M');
			}
			if ($("#custInfo_PSPT_ID").val().length == 15
					&& ($("#custInfo_PSPT_ID").val().charAt(14) % 2 == 0)) {
				$("#custInfo_SEX").val('F');
			}
			if ($("#custInfo_PSPT_ID").val().length == 15
					&& ($("#custInfo_PSPT_ID").val().charAt(14) % 2 == 1)) {
				$("#custInfo_SEX").val('M');
			}
		}
	}
}

//根据身份证信息 来填写客户生日信息
function setBirthdayByPspt()
{
  var pspt_ID=$("#custInfo_PSPT_ID").val();
  var strBirthday;
  
  if (pspt_ID.length==15){ 
     strBirthday="19"+pspt_ID.substr(6,2)+"-"+pspt_ID.substr(8,2)+"-"+pspt_ID.substr(10,2);
  }else
  {
    strBirthday=pspt_ID.substr(6,4)+"-"+pspt_ID.substr(10,2)+"-"+pspt_ID.substr(12,2);
  }
   $("#custInfo_BIRTHDAY").val(strBirthday);  
   

}

//提交校验
function checkBeforeSubmit()
{  
   if(!$.validate.verifyField($("#USER_TYPE_CODE")[0]) || !$.validate.verifyField($("#ASSURE_PSPT_TYPE_CODE")[0]) 
        || !$.validate.verifyField($("#ASSURE_PSPT_ID")[0])  || !$.validate.verifyField($("#ASSURE_NAME")[0])) {
        return false;
      }
	return true;	  
}

function onAreaTypeChange()
{
	var areaType = $("#AREA_TYPE").val();
	if(areaType=="1" || areaType=="3")
	{
		disabled("CLEAR_ACCOUNT",false);
	}
	else
	{
		$("#CLEAR_ACCOUNT").val('0');
		disabled("CLEAR_ACCOUNT",true);
	}
}

//2.重复号码：如"666666"或"000000"等
function checkRepeatNumber(passwd){
	/**
	 * 算法简单描述： 相邻位相减为0
	 */
	for(var i=0; i<(passwd.length-1); i++){
		if( ( parseInt(passwd.charAt(i+1))-parseInt(passwd.charAt(i)) ) != 0 ){
			return false;
		}
	}
	return true;
}

//5.客户手机号码中前4位+后4位或后4位+前4位的组合
function checkContainInSnQh4Bit(pstid, sn){
	if(pstid==null||pstid==""||pstid.length<4||pstid.length>sn.length){
		return false;
	}
	var q4b = sn.substr(0, pstid.length);				// 手机号码前4位
	var h4b = sn.substr(sn.length-pstid.length);		// 手机号码后4位
	if((q4b) == pstid){
		return true;
	}else if((h4b) == pstid){
		return true;
	}
	return false;
}

//1.连号：如"123456"或"345678"等
function checkSerieisNumbermc(passwd){
	/**
	 * 算法简单描述： 相邻位相减等于正负一
	 */
	// 递增
	for(var i=0; i<(passwd.length-1); i++){
		var n1 = parseInt(passwd.charAt(i+1))-parseInt(passwd.charAt(i));
		if( (n1>=0 && n1!=1)||isNaN(n1) ){
			return false;
		}
	}
	// 递减
	for(var i=0; i<(passwd.length-1); i++){
		var n1 = parseInt(passwd.charAt(i+1))-parseInt(passwd.charAt(i));
		if( (n1<=0 && n1!=-1)||isNaN(n1) ){
			return false;
		}
	}
	return true;
}

