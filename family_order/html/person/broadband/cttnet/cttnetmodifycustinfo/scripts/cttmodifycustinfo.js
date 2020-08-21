function refreshPartAtferAuth(data)
{
	ajaxModiCustNameNew();
	
	var param = "&CUST_ID="+data.get("CUST_INFO").get("CUST_ID")
	+"&CUST_EPARCHY_CODE="+data.get("CUST_INFO").get("EPARCHY_CODE")
	+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
	+"&NET_TYPE_CODE="+data.get("USER_INFO").get("NET_TYPE_CODE")
	+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER"); 
	$.ajax.submit('', 'loadChildInfo', param, 'CustInfoPart', function(data)
	{
		disabledArea("CustInfoPart",false);
		var departKindCode = data.get("DEPART_KIND_CODE"); 
		var staffv = data.get("LOGIN_STAFF_ID").substring(0,4); 
		$("#DEPART_KIND_CODE").val(departKindCode);
		$("#STAFF_ID").val(staffv);
		if(departKindCode != null && (departKindCode != "100" && departKindCode!="500") && staffv != "HNSJ" && staffv != "HNHN")
		{
			//$("#custInfo_PSPT_TYPE_CODE").empty();
			//$("#custInfo_PSPT_TYPE_CODE").append("<option value='0'>本地身份证</option>");
			//$("#custInfo_PSPT_TYPE_CODE").append("<option value='1'>外地身份证</option>");
			$("#custInfo_PSPT_ID").attr("disabled",true);
			$("#custInfo_PSPT_ADDR").attr("disabled",true);
			$("#custInfo_PSPT_END_DATE").attr("disabled",true);
			$("#custInfo_CUST_NAME").attr("disabled",true);
			$("#custInfo_SEX").attr("disabled",true); 
		}
		initModifyInfo();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


//客户资料变更初始化方法 
function initModifyInfo()	
{
    var custInfo_REAL_NAME = $("#custInfo_REAL_NAME").val();//是否已办理实名制
    var special_rigth = $("#STAFF_SPECIAL_RIGTH").val();//特殊修改权限
    
    var real_reg = $("#REAL_REG").val();//是否实名制预登记

   if(special_rigth=='true')//有特殊修改权限
   {     
		if (custInfo_REAL_NAME == 'true')//已经办了实名制 则只需要屏蔽实名制 勾选框
		{
		  $("#custInfo_REAL_NAME").attr('disabled',true);
		  $("#custInfo_IS_REAL_NAME").val('1');	//设置隐藏域的值
		}
		
		$("#noteSpan").addClass("e_required");//备注必填
		$("#custInfo_REMARK").attr("nullable","no");
		
	}else//没有特殊权限
	{   
	    if (custInfo_REAL_NAME == 'true')//已经办了实名制 则需要控制 实名制勾选框，客户名称，证件类型，证件号码，证件地址不能修改，屏蔽证件号码文本框
	    {    	        
	        $("#custInfo_REAL_NAME").attr('disabled','disabled');
			$("#custInfo_CUST_NAME").attr('disabled','disabled');
			$("#custInfo_PSPT_TYPE_CODE").attr('disabled','disabled');
			$("#custInfo_PSPT_ID").attr('disabled','disabled');
			$("#custInfo_PSPT_ADDR").attr('disabled','disabled');			
			$("#psptDIV").css('display','none');
			$("#psptAddrDIV").css('display','none');
			$("#custInfo_IS_REAL_NAME").val('1');   
	    }
	}	
	 

  //是否有实名制预受理的信息
  if($("#REAL_REG").val()=="1")
  {	
	alert("用户已经办理了实名制预受理业务，自动默认其资料");
	$("#custInfo_CUST_NAME").val($("#REAL_CUST_NAME").val());
	$("#custInfo_PSPT_TYPE_CODE").val($("#REAL_PSPT_TYPE_CODE").val());
	$("#custInfo_PSPT_ID").val($("#REAL_PSPT_ID").val());
	$("#custInfo_PSPT_ADDR").val($("#REAL_PSPT_ADDR").val());
	$("#custInfo_PHONE").val($("#REAL_PHONE").val());	
  }
  
  //设置证件号码的数据类型
	if($("#custInfo_PSPT_TYPE_CODE").val() == "0" || $("#custInfo_PSPT_TYPE_CODE").val() == "1" || $("#custInfo_PSPT_TYPE_CODE").val() == "2"){
		if ($("#custInfo_PSPT_TYPE_CODE").val() == "0" || $("#custInfo_PSPT_TYPE_CODE").val() == "1" ) {
			$("#custInfo_PSPT_ID").attr("datatype","pspt");
		} else {
			$("#custInfo_PSPT_ID").attr("datatype","text");
		}
	    //如果客户性别为空,则根据身份证号自动设置性别
	    if($("#custInfo_SEX").val() == ""){    
    		if($("#custInfo_PSPT_ID").val().length == 18 && ($("#custInfo_PSPT_ID").val().charAt(16)%2 == 0)){
       			$("#custInfo_SEX").val('F');
       		}
       		if($("#custInfo_PSPT_ID").val().length == 18 && ($("#custInfo_PSPT_ID").val().charAt(16)%2 == 1)){
    				$("#custInfo_SEX").val('M');
       		}
       		if($("#custInfo_PSPT_ID").val().length == 15 && ($("#custInfo_PSPT_ID").val().charAt(14)%2 == 0)){
       			$("#custInfo_SEX").val('F');
       		}
       		if($("#custInfo_PSPT_ID").val().length == 15 && ($("#custInfo_PSPT_ID").val().charAt(14)%2 == 1)){
    				$("#custInfo_SEX").val('M');
       		}		
   		}
	}else{
		$("#custInfo_PSPT_ID").attr("datatype","text");   
	}	
	
	$.CTTCustInfo.init('CustInfoPart');
}

//扫描读取身份证信息
function clickScanPspt()
{
	getMsgByEForm("custInfo_PSPT_ID","custInfo_CUST_NAME","custInfo_SEX","custInfo_FOLK_CODE","custInfo_BIRTHDAY","custInfo_PSPT_ADDR,custInfo_POST_ADDRESS",null,"custInfo_PSPT_END_DATE");
    $("#custInfo_PSPT_ID").focus();
    this.checkRealNameLimitByPspt();
}

/*页面初始化根据证件类型设置证件号码的datatype*/
function chgRealname()
{   

    if ($("#custInfo_REAL_NAME").attr("checked") && $("#custInfo_PSPT_TYPE_CODE").val()=="Z")
    {     
       alert("办理实名制用户，证件类型不能为其它！");
       $("#custInfo_REAL_NAME").attr("checked",false);
    }
    if ($("#custInfo_REAL_NAME").attr("checked")){
        
        $("#custInfo_IS_REAL_NAME").val('1');
    }else
    {
        $("#custInfo_IS_REAL_NAME").val('0');
    }
   
}

/*根据证件类型的变化设置证件号码的datatype */
function judgePspt()
{
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	if(psptTypeCode==0 || psptTypeCode==1)
	{
		var departKindCode = $("#DEPART_KIND_CODE").val(); 
		var staffv = $("#STAFF_ID").val().substring(0,4); 
		if(departKindCode != "" && departKindCode != "100" && departKindCode != "500" && staffv != "HNSJ" && staffv != "HNHN" && staffv != "SUPERUSR")
		{
			$("#custInfo_PSPT_ID").val("");
			$("#custInfo_PSPT_ID").attr("disabled",true);
			$("#custInfo_PSPT_ADDR").attr("disabled",true);
			$("#custInfo_PSPT_END_DATE").attr("disabled",true);
			$("#custInfo_CUST_NAME").attr("disabled",true);
			$("#custInfo_SEX").attr("disabled",true); 
		}
	}
	else
	{
		$("#custInfo_PSPT_ID").attr("disabled",false);
		$("#custInfo_PSPT_ADDR").attr("disabled",false);
		$("#custInfo_PSPT_END_DATE").attr("disabled",false);
		$("#custInfo_CUST_NAME").attr("disabled",false);
		$("#custInfo_SEX").attr("disabled",false); 
	}
	
	if($("#custInfo_PSPT_TYPE_CODE").val() != "A")
	{
    	var str = $("#custInfo_CUST_NAME").val();
    	var pattern =/[a-zA-Z0-9]/;

    	if(pattern.test(str) && str!=""){
    		alert("证件类型不是护照的,客户名称不能包含数字和字母！");
			$("#custInfo_CUST_NAME").val(''); 
			$("#custInfo_CUST_NAME").focus();
			return false;
    	}
		if(str.length<2 && str!="" && !pattern.test(str)){
			alert("证件类型不是护照的,客户名称不能少于2个中文和字符！");
			$("#custInfo_CUST_NAME").val('');
			$("#custInfo_CUST_NAME").focus();
			return false;
		}
	}
	
	var custInfo_REAL_NAME =  $("#custInfo_REAL_NAME").val();
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();	
	if (psptTypeCode == "0" || psptTypeCode == "1") {
		$("#custInfo_PSPT_ID").attr("datatype","pspt");
	} else if ($("#custInfo_IS_REAL_NAME").attr("checked") && psptTypeCode == "Z") {
		alert("办理实名制用户，证件类型不能为其它！");
		$("#custInfo_PSPT_TYPE_CODE").val(''); 
    	$("#custInfo_PSPT_TYPE_CODE").focus()
	} else {
		$("#custInfo_PSPT_ID").attr("datatype","text");
	}
	if (psptTypeCode != "0" && psptTypeCode != "1" && psptTypeCode != "2") {
		$("#custInfo_PSPT_ID").val('');
	}
}
/*客户名称验证 YYY*/
function isInPurchase() {
	var objD = $("#custInfo_PSPT_TYPE_CODE").val();
	var custName = $("#custInfo_CUST_NAME").val();
	if(objD != "A")
	{
    	var pattern =/[a-zA-Z0-9]/;
    	if(pattern.test(custName) && custName!=""){
    		alert("证件类型不是护照的,不能包含数字和字母！");
    		$("#custInfo_CUST_NAME").val('');
    		$("#custInfo_CUST_NAME").focus()
			return false;
    	}
		if(custName.length<2 && custName!="" && !pattern.test(custName)){
			alert("证件类型不是护照的,客户名称不能少于2个中文和字符！");
			$("#custInfo_CUST_NAME").val('');
    		$("#custInfo_CUST_NAME").focus()
			return false;
		}
	}
	
	/*if(custName.indexOf("校园")>-1)
    {
        alert("客户名称不能包含校园，请重新输入！");
        $("#custInfo_CUST_NAME").val('');
		$("#custInfo_CUST_NAME").focus()
        return false;
    }
    
    if(custName.indexOf("海南通")>-1)
    {
        alert("客户名称不能包含海南通，请重新输入！");
        $("#custInfo_CUST_NAME").val('');
		$("#custInfo_CUST_NAME").focus()
        return false;
    }
    
    if(custName.indexOf("神州行")>-1)
    {
        alert("客户名称不能包含神州行，请重新输入！");
        $("#custInfo_CUST_NAME").val('');
		$("#custInfo_CUST_NAME").focus()
        return false;
    }
    
    if(custName.indexOf("动感地带")>-1)
    {
        alert("客户名称不能包含动感地带，请重新输入！");
        $("#custInfo_CUST_NAME").val('');
		$("#custInfo_CUST_NAME").focus()
        return false;
    }
    
    if(custName.indexOf("套餐")>-1)
    {
        alert("客户名称不能包含套餐，请重新输入！");
        $("#custInfo_CUST_NAME").val('');
		$("#custInfo_CUST_NAME").focus()
        return false;
    }*/
	
	var oldName =  $("#OLD_CUST_NAME").val();
	var newName =  $("#custInfo_CUST_NAME").val();
	if ($("#STAFF_SPECIAL_RIGTH").val() != "true"
			&& $("#IS_IN_PURCHASE").val() == "1" && oldName != newName) {
		alert("该用户还处在营销活动期限内，不能够修改客户名称！");
		$("#custInfo_CUST_NAME").val(oldName);
		return false;
	}
	this.checkRealNameLimitByPspt();
	return true;
}


//设置身份证信息到页面
function setIdentityCardInfoToHtml(strCardVersion,strPsptId,strBirthday,strName,strSex,strMz,strAddress)
{
	$("#custInfo_CUST_NAME").val(strName);
	$("#custInfo_PSPT_ADDR").val(strAddress);
	$("#custInfo_HOME_ADDRESS").val(strAddress);
	$("#custInfo_PSPT_ID").val(strPsptId);
	$("#custInfo_SEX").val(strSex);

	var strBirthday;
  
   if (strPsptId.length==18){ 
     strBirthday=strPsptId.substr(6,4)+"-"+strPsptId.substr(10,2)+"-"+strPsptId.substr(12,2);
   }
   
    $("#custInfo_BIRTHDAY").val(strBirthday)
	$("#custInfo_PSPT_TYPE_CODE").val('0');
}

//清除页面上身份证信息
function clearHtmlIdentityCardInfo()
{
    //修改二代证读取信息
	$("#custInfo_CUST_NAME").val('');
	$("#custInfo_PSPT_ADDR").val('');
	$("#custInfo_HOME_ADDRESS").val('');
	$("#custInfo_PSPT_ID").val('');
	$("#custInfo_BIRTHDAY").val('');
	$("#custInfo_SEX").val('');
    $("#custinfo_ReadCardFlag").val(0);
}

//根据身份证信息 来填写客户生日信息 YYY
function setBirthdayByPspt()
{
	
	var pspt_ID = $("#custInfo_PSPT_ID").val();
	var strBirthday;
	if (pspt_ID.length == 15) {
		strBirthday = "19" + pspt_ID.substr(6, 2) + "-" + pspt_ID.substr(8, 2)
				+ "-" + pspt_ID.substr(10, 2);
	} else {
		strBirthday = pspt_ID.substr(6, 4) + "-" + pspt_ID.substr(10, 2) + "-"
				+ pspt_ID.substr(12, 2);
	}
	 $("#custInfo_BIRTHDAY").val(strBirthday);
}

/* 判断证件类型和证件号码是否已经被用过了 YYY*/
function checkPsptId(objId) 
{
	obj =  $("#"+objId);
	var psptId = $.trim(obj.val());
	var desc = $.trim(obj.attr("desc"));
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	if("custInfo_AGENT_PSPT_ID"==objId)
	{
		psptTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
	}
	else
	{
		if(psptId.length == 0)
		{ 
			alert("证件号码不能为空！");
			return false
		}
		
	}
	
	//var psptId = $('#custInfo_PSPT_ID').val();
	//var psptTypeCode = $('#custInfo_PSPT_TYPE_CODE').val();
	var serialNumber =  $('#AUTH_SERIAL_NUMBER').val();
	 //1.重复号码：如"666666"或"000000"等
	/*if(psptId == ""){
		alert("证件号码不能为空！");
		return false;
	}*/
	if(checkRepeatNumber(psptId))
	{
		alert(desc+"不能全为同一个数字，请重新输入！");
		obj.val("");
		obj.focus();
		return false;
	}    
	
	//2.证件号码不能为手机号码的前N位或后N位
	if(checkContainInSnQh4Bit(psptId, serialNumber))
	{
 		alert("电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为"+desc+"！");
		obj.val("");
		obj.focus();
		return false;
	}
    
  //3.连号：如"123456"或"345678"等
	if(checkSerieisNumber(psptId))
	{
		alert("连续数字不能作为"+desc+"，请重新输入！");
		obj.val("");
		obj.focus();
		return false;
	}
	
	/*港澳居民回乡证：证件号码为9位或11位。首位为英文字母“H”或“M”；其余位均为阿拉伯数字*/
	if(psptTypeCode=="H")
	{
		if(psptId.length!=9&&psptId.length!=11)
		{
			alert("港澳居民回乡证校验："+desc+"必须为9位或11位！");
			obj.val("");
			obj.focus();
			return false;
		}
		if(!( (psptId.charAt(0)=="H"||psptId.charAt(0)=="M")&&(!isNaN(psptId.substr(1))) ))
		{
			alert("港澳居民回乡证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字！");
			obj.val("");
			obj.focus();
			return false;
		}
	}
	/*台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。*/
	if(psptTypeCode=="I")
	{
		if(psptId.length!=8&&psptId.length!=11)
		{
			alert("台湾居民回乡校验："+desc+"必须为8位或11位！");
			obj.val("");
			obj.focus();
			return false;
		}
		if(psptId.length==8)
		{
			if(isNaN(psptId))
			{
				alert("台湾居民回乡校验："+desc+"为8位时，必须均为阿拉伯数字！");
				obj.val("");
				obj.focus();
				return false;
			}
		}
		if(psptId.length==11)
		{
			if(isNaN(psptId.substring(0,10)))
			{
				alert("台湾居民回乡校验：："+desc+"为11位时，前10位必须均为阿拉伯数字。");
				obj.val("");
				obj.focus();
				return false;
			}
		}
	}
	/*军官证、警官证、护照：证件号码须大于等于6位字符。*/
	if(psptTypeCode=="C" || psptTypeCode=="A")
	{
		if(psptId.length < 6)
		{
			var tmpName= psptTypeCode=="A" ? "护照校验：" : "军官证类型校验：";
			alert(tmpName+desc+"须大于等于6位字符！");
			obj.val("");
			obj.focus();
			return false;
		}
	}
	/*营业执照：证件号码长度需满足15位*/
	//REQ201602160017 关于更改铁通业务开户界面和变更界面营业执照位数的需求 营业执照只能15位或18位
	if(psptTypeCode=="E")
	{
		if(psptId.length != 15 && psptId.length != 18)
		{
			alert("营业执照类型校验："+desc+"长度需满足15位或者18位！当前："+psptId.length+"位。");
			obj.val("");
			obj.focus();
			return false;
		}
	}
	/*组织机构代码证：证件号码长度需满足10位，其规则为“XXXXXXXX-X”，倒数第2位是“-”*/
	if(psptTypeCode=="M")
	{
		if(psptId.length != 10)
		{
			alert("组织机构代码证类型校验："+desc+"长度需满足10位！");
			obj.val("");
			obj.focus();
			return false;
		}
		if(psptId.charAt(8) != '-')
		{
			alert("组织机构代码证类型校验："+desc+"规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"！");
			obj.val("");
			obj.focus();
			return false;
		}
	}
	/*事业单位法人登记证书：证件号码长度需满足12位*/
	if(psptTypeCode=="G")
	{
		if(psptId.length != 12)
		{
			alert("事业单位法人登记证书类型校验："+desc+"长度需满足12位！");
			obj.val("");
			obj.focus();
			return false;
		}
	}

	if("custInfo_PSPT_ID"==objId)
	{
		// 户口本证件类型，证件号码填的也是身份证，只是不限制必须大于15岁 身份证件类型新增户口本(REQ201311080002)
		if (psptTypeCode != '2' && !verifyField($('#custInfo_PSPT_ID')))
			return false;
		if (psptTypeCode == '2' && !checkPsptHain($('#custInfo_PSPT_ID')))
			return false;
	}
	
	
	// 海南允许一个证件号码 多个用户使用， 不需要办理客户合并业务	
	if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2")// 当前证件类型为身份证或户口本的自动设置生日
	{
		//身份证相关检查 
		obj.attr("datatype", "pspt");
		if(!$.validate.verifyField(obj)) 
		{  
			obj.val("");
			obj.focus();
			return false;
		}
		setBirthdayByPspt();// 设置生日
	}
	// 根据身份证号，自动设置性别
	if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2") {
		var custSex =  $('#custInfo_SEX').val(); 
		if (psptId.length == 18 && (psptId.charAt(16) % 2 == 0)) {
			$('#custInfo_SEX').val('F');
		}
		if (custSex.length == 18 && (custSex.charAt(16) % 2 == 1)) {
			$('#custInfo_SEX').val('M');
		}
		if (custSex.length == 15 && (custSex.charAt(14) % 2 == 0)) {
			$('#custInfo_SEX').val('F');
		}
		if (custSex.length == 15 && (custSex.charAt(14) % 2 == 1)) {
			$('#custInfo_SEX').val('M');
		}
	}
	
	if( objId=="custInfo_PSPT_ID" )
	{
		this.checkRealNameLimitByPspt();
		
	}
}

//检查同一证件号已开实名制用户的数量是否已超出预定值
function checkRealNameLimitByPspt()
{
    var custName = $("#custInfo_CUST_NAME").val();
    var psptId = $("#custInfo_PSPT_ID").val();
    var strNetTypeCode = $("#NET_TYPE_CODE").val();
    if(custName == "" || psptId == "")
    {
        return false;
    }
    var param = "&CUST_NAME=" + custName + "&PSPT_ID="+psptId+"&NET_TYPE_CODE="+strNetTypeCode+"&EPARCHY_CODE=0898";
    //如果没有设置则取默认服务名
    /*if(this.realNameSVCName){
    	param += "&SVC_NAME="+this.realNameSVCName;
    }*/ 
    //param += "&SVC_NAME=SS.CreatePersonUserSVC.checkRealNameLimitByUsePspt";
    $.beginPageLoading("证件信息数量校验。。。");
	/*$.httphandler.get(this.clazz, "checkRealNameLimitByPspt", param, 
		function(data){
			$.endPageLoading();
			if(data && data.get("CODE")!= "0"){
				$("#custInfo_USE_PSPT_ID").attr("datatype", "");
				$("#REALNAME_LIMIT_CHECK_RESULT").val("false");
				alert(data.get("MSG"));
				return;
			}else{
				$("#REALNAME_LIMIT_CHECK_RESULT").val("true");
			}
			$("#span_USE_PSPT_ID").addClass("e_elements-success");
		},function(code, info, detail){
			$.endPageLoading();
			$("#span_USE_PSPT_ID").addClass("e_elements-error");
			MessageBox.error("错误提示","使用人证件信息数量校验获取后台数据错误！",null, null, info, detail);
		},function(){
			$.endPageLoading();
			MessageBox.alert("告警提示", "使用人证件信息数量校验超时");
	});	*/
	$.ajax.submit(null, 'checkRealNameLimitByPspt', param, '', function(data)
	{
		$.endPageLoading();
		if(data && data.get("CODE") != "0")
		{
			$("#custInfo_PSPT_ID").attr("value", "");
			alert(data.get("MSG"));
			return;
		}
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//1.连号：如"123456"或"345678"等
function checkSerieisNumber(passwd){
	/**
	 * 算法简单描述：
	 * 相邻位相减等于正负一	 
	 */
	//递增
	for(var i=0; i<(passwd.length-1); i++){
		var n1 = parseInt(passwd.charAt(i+1))-parseInt(passwd.charAt(i));
		if( (n1>=0 && n1!=1)||isNaN(n1) ){
			return false;
		}
	}
	//递减
	for(var i=0; i<(passwd.length-1); i++){
		var n1 = parseInt(passwd.charAt(i+1))-parseInt(passwd.charAt(i));
		if( (n1<=0 && n1!=-1)||isNaN(n1) ){
			return false;
		}
	}
	return true;
}
//2.重复号码：如"666666"或"000000"等
function checkRepeatNumber(passwd){
	/**
	 * 算法简单描述：
	 * 相邻位相减为0
	 */
	for(var i=0; i<(passwd.length-1); i++){
		if( ( parseInt(passwd.charAt(i+1)) - parseInt(passwd.charAt(i)) ) != 0 ){
			return false;
		}
	}
	return true;
}
//5.客户手机号码中前4位+后4位或后4位+前4位的组合
function checkContainInSnQh4Bit(pstid, sn){
	if(pstid==null || pstid=="" || pstid.length < 4 || pstid.length > sn.length){
		return false;
	}
	var q4b = sn.substr(0, pstid.length);				//手机号码前4位
	var h4b = sn.substr(sn.length-pstid.length);		//手机号码后4位
	if((q4b) == pstid){
		return true;
	}else if((h4b) == pstid){
		return true;
	}
	return false;
}
/**
 * 户口本身份证号码校验，比原来的身份证号码校验少了个限制，就是不限制年龄小于15岁
 */
function checkPsptHain(field) {
	var desc = field.attr("desc");
	if (!field || !field.attr('nodeType') || field.val() == "") {  //(!field || !field.nodeType || field.value == "")
		var msg = desc + "为空或不存在";
		///alertMessage(msg, field);
		return false;
	}
	var Errors = new Array("验证通过!", "身份证号码位数不对!", "身份证号码不合法", "身份证号码校验错误!",
			"身份证地区非法!", "1身份证出生日期不符合要求，是否继续？");
	var area = {
		11 : "北京",
		12 : "天津",
		13 : "河北",
		14 : "山西",
		15 : "内蒙古",
		21 : "辽宁",
		22 : "吉林",
		23 : "黑龙江",
		31 : "上海",
		32 : "江苏",
		33 : "浙江",
		34 : "安徽",
		35 : "福建",
		36 : "江西",
		37 : "山东",
		41 : "河南",
		42 : "湖北",
		43 : "湖南",
		44 : "广东",
		45 : "广西",
		46 : "海南",
		50 : "重庆",
		51 : "四川",
		52 : "贵州",
		53 : "云南",
		54 : "西藏",
		61 : "陕西",
		62 : "甘肃",
		63 : "青海",
		64 : "宁夏",
		65 : "新疆",
		71 : "台湾",
		81 : "香港",
		82 : "澳门",
		91 : "国外"
	};
	var idcard = field.val(), Y, JYM;
	var S, M, ereg;
	var idcard_array = new Array();
	idcard_array = idcard.split("");
	if (!area[parseInt(idcard.substr(0, 2), 10)]) {
		///alertMessage(Errors[4], field);
		return false;
	}
	switch (idcard.length) {
	case 15:
		if ((parseInt(idcard.substr(6, 2)) + 1900) % 4 == 0
				|| ((parseInt(idcard.substr(6, 2)) + 1900) % 100 == 0 && (parseInt(idcard
						.substr(6, 2)) + 1900) % 4 == 0)) {
			ereg = /^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$/;
		} else {
			ereg = /^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}$/;
		}
		if (ereg.test(idcard)) {
			var matches = idcard.match(ereg);
			var nowY = (new Date()).getFullYear();
			if (typeof (matches) == "object" && (matches instanceof Array)
					&& matches.length) {
				if (parseInt(("19" + matches[1].toString()), 10) + 100 < nowY) {
					return confirm(Errors[5]);
				}
			}
			matches = null;
			return true;
		} else {
			///alertMessage(Errors[2], field);
			return false;
		}
		break;
	case 18:
		if (parseInt(idcard.substr(6, 4)) % 4 == 0
				|| (parseInt(idcard.substr(6, 4)) % 100 == 0 && parseInt(idcard
						.substr(6, 4)) % 4 == 0)) {
			ereg = /^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$/;
		} else {
			ereg = /^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}[0-9Xx]$/;
		}
		if (ereg.test(idcard)) {
			var matches = idcard.match(ereg);
			var nowY = (new Date()).getFullYear();
			if (typeof (matches) == "object" && (matches instanceof Array)
					&& matches.length) {
				matches = parseInt(matches[1], 10);
				if ((matches + 100) < nowY) {
					return confirm(Errors[5]);
				}
			}
			matches = null;
			return true;
		} else {
			///alertMessage(Errors[2], field);
			return false;
		}
		break;
	default:
		///alertMessage(Errors[1], field);
		return false;
		break;
	}
	return true;
}


function blackUserDeal(data) 
{
	var blackInfo = data.get(0);
	var blackUser = data.get(0,"BlackUser","0");
	var custList = data.get(0,"CustList","0");
	var userLimit = data.get(0,"UserLimit","0");
	var userCnt = data.get(0,"UserCnt","0");
	var qfxhCnt = data.get(0,"QfxhCnt","0");
	
	if(blackUser > 0)
	{
		var sMobPhonecode= data.get(0,"MOB_PHONECODE");
		var sJoinCause= data.get(0,"JOIN_CAUSE");
		alert("该证件是黑名单资料！黑名单号码：" + sMobPhonecode + "  加入黑名单原因：" + sJoinCause);		
	}
	else if(userLimit >0)
	{
		alert("该证件限制新开户或者该证件已经被使用");	
		$('#custInfo_PSPT_ID').val('');		
	}
	
	if(userCnt > 0)
	{
		alert('该客户证件下已有在用用户：'+userCnt+'个。');
	}
	if(qfxhCnt>0)
	{
		alert('该客户证件下已有欠费销户用户：'+qfxhCnt+'个。');
	}
	var openNum = parseInt(userCnt) + parseInt(qfxhCnt);
	var openLimit = parseInt($.auth.getAuthData().get("CUST_INFO").get("OPEN_LIMIT"));
	

	if(openLimit > 0 && openNum >= openLimit ) 
	{
		alert('该证件已达最大开户限制数'+openLimit+'个，不能再次使用该证件办理该业务！');
		$('#custInfo_PSPT_ID').val('');
	}
	else
	{
		if(openNum > 0) 
		{
		    var strPsptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
		    var strPsptId = $('#custInfo_PSPT_ID').val();
	
			var param ="&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER") + "&PSPT_TYPE_CODE=" + strPsptTypeCode + "&PSPT_ID=" + strPsptId + "&OPEN_NUM=" + openNum;
			popupPage('createusertrade.BaseInfoPopup', 'getUserInfoByPspt', param, '入网记录', '500', '400');
	
		}	
	}
}
//生日检查 YYY
function checkBirthday()
{
	var psptId = $("#custInfo_PSPT_ID");
	if(psptId){
		if(psptId.val().indexOf("*")!=-1){
			return true;
		}
	}
		
 if($("#custInfo_PSPT_TYPE_CODE").val()=="0" || $("#custInfo_PSPT_TYPE_CODE").val()=="1" || $("#custInfo_PSPT_TYPE_CODE").val()=="2")//如果当前证件类型为0和1(身份证)，则判断 身份证上的生日是否与所填的生日日期相同
    {
    
          var birthday=$("#custInfo_BIRTHDAY").val();
       
          var pspt_ID=$("#custInfo_PSPT_ID").val();
          
          var strBirthday;
          
          if (pspt_ID.length==15){ 
             strBirthday="19"+pspt_ID.substr(6,2)+"-"+pspt_ID.substr(8,2)+"-"+pspt_ID.substr(10,2);
          }else
          {
             strBirthday=pspt_ID.substr(6,4)+"-"+pspt_ID.substr(10,2)+"-"+pspt_ID.substr(12,2);
          }    
          if(birthday!=strBirthday)//如果2者日期不同
          {
             if(confirm('身份证号码与生日不吻合，是否将生日改为与身份证号码一致？'))
    		{
    			$("#custInfo_BIRTHDAY").val(strBirthday);
    		}
          }  
    }
  return true;
 ///submitValid();
}

//提交校验
function checkBeforeSubmit()
{  
	var newName = $('#custInfo_CUST_NAME').val();
	var re1 = new RegExp("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-zA-Z])*$"); 
	var re2 = new RegExp("^(全球通|动感地带|套餐|大灵通|乡镇通|无权户|无档户|代办|代理)*$"); 
	var re3 = new RegExp("^([a-zA-Z])*$"); 
	var re4 = new RegExp("^([0-9])*$"); 
	if(re2.test(newName)){
		$('#custInfo_CUST_NAME').val('');
		alert("客户名称包含非法关键字！");
		return false;
	}
	if(re3.test(newName)){
		$('#custInfo_CUST_NAME').val('');
		alert("客户名称不能为纯英文字母！");
		return false;
	}
	if(re4.test(newName)){
		$('#custInfo_CUST_NAME').val('');
		alert("客户名称不能为纯数字！");
		return false;
	}
	if(!$.verifylib.checkEmail($('#custInfo_EMAIL').val())){
		alert("邮件格式不正确！");
		return false;
	}
   if(!$.validate.verifyField($("#custInfo_CUST_NAME")[0]) || !$.validate.verifyField($("#custInfo_PSPT_TYPE_CODE")[0]) //
		 || !$.validate.verifyField($("#custInfo_PSPT_ID")[0]) 
         || !$.validate.verifyField($("#custInfo_PSPT_ADDR")[0])
         || !$.validate.verifyField($("#custInfo_PSPT_END_DATE")[0]) || !$.validate.verifyField($("#custInfo_PHONE")[0])
         || !$.validate.verifyField($("#custInfo_REMARK")[0]))
     {
        return false;
     }
     
	return true;	  
}

/* 提交检查  YYY*/
function submitCheck(obj) {
	if(!checkBeforeSubmit()){
		return false;
	}
	var psptId = $("#custInfo_PSPT_ID");
	if(psptId){
		if(psptId.val().indexOf("*")!=-1){
			psptId.attr('datatype',"text");
			//psptId.setAttribute("datatype","text"); 
		}
	}
	var custInfo_PHONE = $("#custInfo_PHONE");
	if(custInfo_PHONE){
		if(custInfo_PHONE.val().indexOf("*")!=-1){
			custInfo_PHONE.attr("datatype","text"); 
		}
	}
	var custInfo_EMAIL = $("#custInfo_EMAIL");
	if(custInfo_EMAIL){
		if(custInfo_EMAIL.val().indexOf("*")!=-1){
			custInfo_EMAIL.attr("datatype","text"); 
		}
	}
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	
	if (psptTypeCode != '2' && !verifyAll(obj)){
		return false;
	}
	/* 户口本证件类型，证件号码走自己校验规则 */
	if(psptId.val().indexOf("*")==-1){
//		if (psptTypeCode == '2' && !checkPsptHain(psptId)){
//			return false;
//		}
	}
	$.cssubmit.addParam("&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"));
	
	var realNameObj =  $("#custInfo_REAL_NAME");
	var oldIsRealName = $.auth.getAuthData().get('CUST_INFO').get("IS_REAL_NAME");
	if (oldIsRealName!=1 && realNameObj.attr("checked")) { //修改之前的客户不是实名制，代表本次是第一次做实名制办理
		if (confirm('您正在办理实名制，一旦提交资料将不能修改。请确认输入的资料无误！')) {
			if(psptId.val().indexOf("*")==-1){
			   return checkBirthday();
			}
			return true;
		} else {
			return false;
		}
	}
	if(psptId.val().indexOf("*")==-1){
		return checkBirthday();
	}
	return true;
}

function checkAddr()
{
	 var custAddrObj = $('#custInfo_PSPT_ADDR'); 
	 if(custAddrObj==null || custAddrObj.val().length<8 || custAddrObj.val()==''){
		 alert('证件地址栏录入文字需大于8位');
		 custAddrObj.val('');
		 custAddrObj.focus();
	 }
	 
	 if(!isNaN(custAddrObj.val())&& custAddrObj.val() != ''){
		 alert('证件地址栏不能全部为数字');
		 custAddrObj.val('');
		 custAddrObj.focus();
	 }
	 
	 var strPsptAddr = $("#custInfo_PSPT_ADDR").val();
	 $('#custInfo_POST_ADDRESS').val(strPsptAddr);
}

/**
 * @deprecated
 * REQ201710090024-关于铁通迁移固话用户办理界面优化及套餐开发的需求-NGBOSS-铁通模块-个人业务-其他业务-客户资料变更（铁通）
-是否收改号费，请业务支撑部收回是否收改号费选择，下线收费功能  @auth zhaohj3 @date 2017-2-6 8:50:22
 */
function ajaxModiCustName()
{
	var isRecFee = $('#custInfo_IS_RECFEE').val();
	var custName = $('#custInfo_CUST_NAME').val();
	var oldCustName = $('#OLD_CUST_NAME').val();
	var feeMode = $('#FEE_MODE').val();
	var fee = $('#FEE').val()
	var feeTypeCode = $('#FEE_TYPE_CODE').val();
	
	if(custName != oldCustName && feeMode == '')
	{
		if(isRecFee == '1')
		{
			$.ajax.submit('', 'ajaxModiCustName', '', '', function(data){
				if(data.length>0)
				{
					var custName = $('#custInfo_CUST_NAME').val();
					var oldCustName = $('#OLD_CUST_NAME').val();
					
					fee = data.get(0).get("FEE");
					feeMode = data.get(0).get("FEE_MODE");
					feeTypeCode = data.get(0).get("FEE_TYPE_CODE");
					
					var str1 = '';
					str1 = str1 + '您修改了客户名称，并且选择了收取改名费，将收取[' + (parseFloat(fee)/100).toFixed(2) + ']元手续费' + "\n";
					str1 = str1 + '原客户名称：' + oldCustName + "\n";
					str1 = str1 + '新客户名称：' + custName + "\n";
					alert(str1);
					
					var obj =new Wade.DataMap();
					obj.put("TRADE_TYPE_CODE","9726")
					obj.put("MODE",feeMode);
					obj.put("CODE", feeTypeCode);
					obj.put("FEE", parseInt(fee));
					$.feeMgr.removeFee("9726", feeMode, feeTypeCode); 
					$.feeMgr.insertFee(obj);
					$('#FEE_MODE').val(feeMode);
					$('#FEE_TYPE_CODE').val(feeTypeCode);
					$('#FEE').val(fee);
				}
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		    });
		}
	}
	else if(custName != oldCustName && feeMode != '')
	{
		var obj =new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE","9726")
		obj.put("MODE",feeMode);
		obj.put("CODE", feeTypeCode);
		obj.put("FEE", fee);
		if(isRecFee == '1')
		{
			$.feeMgr.removeFee("9726", feeMode, feeTypeCode); 
			$.feeMgr.insertFee(obj);
		}
		else
		{
			$.feeMgr.removeFee("9726", feeMode, feeTypeCode); 
		}
	}
	else if(custName == oldCustName && feeMode != '')
	{
		var obj =new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE","9726")
		obj.put("MODE",feeMode);
		obj.put("CODE", feeTypeCode);
		obj.put("FEE", parseInt(fee));
		$.feeMgr.removeFee("9726", feeMode, feeTypeCode); 
	}
}

/** REQ201710090024-关于铁通迁移固话用户办理界面优化及套餐开发的需求-NGBOSS-铁通模块-个人业务-其他业务-客户资料变更（铁通）
-是否收改号费，请业务支撑部收回是否收改号费选择，下线收费功能  @auth zhaohj3 @date 2017-2-6 8:50:22 */
function ajaxModiCustNameNew()
{
	var custName = $('#custInfo_CUST_NAME').val();
	var oldCustName = $('#OLD_CUST_NAME').val();
	var feeMode = $('#FEE_MODE').val();
	var fee = $('#FEE').val()
	var feeTypeCode = $('#FEE_TYPE_CODE').val();
	
	if(custName != oldCustName && feeMode != '')
	{
		var obj =new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE","9726")
		obj.put("MODE",feeMode);
		obj.put("CODE", feeTypeCode);
		obj.put("FEE", fee);
		$.feeMgr.removeFee("9726", feeMode, feeTypeCode); 
	}
	else if(custName == oldCustName && feeMode != '')
	{
		var obj =new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE","9726")
		obj.put("MODE",feeMode);
		obj.put("CODE", feeTypeCode);
		obj.put("FEE", parseInt(fee));
		$.feeMgr.removeFee("9726", feeMode, feeTypeCode); 
	}
}
