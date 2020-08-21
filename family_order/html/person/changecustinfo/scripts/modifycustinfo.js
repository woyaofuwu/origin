
function refreshPartAtferAuth(data)
{ 	 
	
	var acctTag=data.get("USER_INFO").get("ACCT_TAG"); //REQ201602290007 关于入网业务人证一致性核验提醒的需求
	var custId=data.get("CUST_INFO").get("CUST_ID");
	var userId=data.get("USER_INFO").get("USER_ID"); 
	var sn=data.get("USER_INFO").get("SERIAL_NUMBER");
//	var AUTH_SPEC=custId+userId+sn; 
//	var firstKey="c";
//	var secondKey="x";
//	var thirdKey="y";
//	AUTH_SPEC =strEnc( AUTH_SPEC ,firstKey,secondKey,thirdKey)+"xxyy";
	var param = "&CUST_ID="+custId
				+"&CUST_EPARCHY_CODE="+data.get("CUST_INFO").get("EPARCHY_CODE")
				+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
				+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER")
				+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()
				+"&CHECK_MODE="+data.get("CHECK_MODE") 
				;  //认证方式 
	$.ajax.submit('', 'loadChildInfo',param , 'CustInfoPart', function(data){ 
		disabledArea("CustInfoPart",false);
		var departKindCode=data.get("DEPART_KIND_CODE"); 
		var inputPermision=data.get("INPUT_PERMISSION"); 
		var staffv=data.get("LOGIN_STAFF_ID").substring(0,4); 
		if(inputPermision==0){
			$("#custInfo_PSPT_TYPE_CODE").empty();
			$("#custInfo_PSPT_TYPE_CODE").append("<option value='0'>本地身份证</option>");
			$("#custInfo_PSPT_TYPE_CODE").append("<option value='1'>外地身份证</option>");
			$("#custInfo_PSPT_TYPE_CODE").append("<option value='3'>军人身份证</option>");
			$("#custInfo_PSPT_ID").attr("disabled",true);
			$("#custInfo_PSPT_ADDR").attr("disabled",true);
			$("#custInfo_PSPT_END_DATE").attr("disabled",true);
			$("#custInfo_CUST_NAME").attr("disabled",true);
			$("#custInfo_SEX").attr("disabled",true); 
		}
		
		var psptTypeCode=$("#custInfo_PSPT_TYPE_CODE").val(); 
		/**
		 * 以上界面户主使用身份证、户口本等证件办理入网业务时，营业员可通过手工调整生日信息，导致一经上传客户资料被拦截被判非实名，
		 * 请进行屏蔽，生日信息全部通过身份证号码进行截取，且不可更改，涉及证件类型为本地身份证、外地身份证、军人身份证、户口本，
		 * 其余个人证件不限制，需人工选择。 
		 * */
		if(psptTypeCode==0 || psptTypeCode==1||psptTypeCode==2||psptTypeCode==3)
		{
			$("#custInfo_BIRTHDAY").attr("disabled",true);
		}
		
		/**
		 * BUG20180316152525_实名制相关业务控制优化bug
		 * <br/>
		 * 如果原来客户为单位证件,经办人和使用人的证件类型不是本地身份证,
		 * 加载出来强制修改为本地身份证是不对的,所以注释掉
		 * @author zhuoyingzhi
		 * @date 20180418
		 */
//		var agentObj=$("#custInfo_AGENT_PSPT_TYPE_CODE");
//		agentObj.find("option[value=0]").attr("selected", true);
//		
//		var useObj=$("#custInfo_USE_PSPT_TYPE_CODE");
//		useObj.find("option[value=0]").attr("selected", true);
		
		
		/**
		 * REQ201602290007 关于入网业务人证一致性核验提醒的需求
		 * chenxy3 2016-03-08
		 * */
		 if(acctTag=="2"){
				$.beginPageLoading("正在查询数据...");
				$.ajax.submit(null,'checkNeedBeforeCheck','','',
						function(data){ 
							var flag=data.get("PARA_CODE1");
							if(flag=="1"){ 
								var param ="&TRADE_ID=10"+"&EPARCHY_CODE=0898"+"&TRADE_TYPE_CODE=60";
								popupPage('beforecheck.BeforeCheck','init',param,'业务检查','600','300',null,null,null,null,false);
							}
						  	$.endPageLoading();
						},function(error_code,error_info){
							$.MessageBox.error(error_code,error_info);
							$.endPageLoading();
				});
		}
		/**
		 * --end--
		 * */
		 
		 
			//军人身份证
			$.ajax.submit(null, 'psptTypeCodePriv',param , null, function(data){ 
				if(data && data.get("X_RESULTCODE")== "0"){ 		   
				    $("#custInfo_USE_PSPT_TYPE_CODE").append("<option value='"+data.get("PSPT_TYPE_CODE")+"'>"+data.get("PSPT_TYPE_NAME")+"</option>");
				    $("#custInfo_AGENT_PSPT_TYPE_CODE").append("<option value='"+data.get("PSPT_TYPE_CODE")+"'>"+data.get("PSPT_TYPE_NAME")+"</option>");		 
				}
			},
			function(error_code,error_info){
		    });
		 
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
	var tradetypecode = $("#TRADE_TYPE_CODE").val();
	if(tradetypecode == 60 || tradetypecode == 3811){
		$("#SHOT_IMG").css("display", "");
		$("#AGENT_SHOT_IMG").css("display", "");
	}
    var realName  = $("#custInfo_REAL_NAME").val();//是否已办理实名制
    var specialRigth = $("#STAFF_SPECIAL_RIGTH").val();//特殊修改权限
    var realReg = $("#REAL_REG").val();//是否实名制预登记
    if(specialRigth=='true')//有特殊修改权限
    {     
		if (realName == 'true')//已经办了实名制 则只需要屏蔽实名制 勾选框
		{
		  //$("#custInfo_REAL_NAME").attr('disabled',true);
		  $("#custInfo_IS_REAL_NAME").val('1');	//设置隐藏域的值
		}
		
		$("#noteSpan").addClass("e_required");//备注必填
		$("#custInfo_REMARK").attr("nullable","no");
		
	}else//没有特殊权限
	{   
	    if (realName == 'true')//已经办了实名制 则需要控制 实名制勾选框，客户名称，证件类型，证件号码，证件地址不能修改，屏蔽证件号码和证件地址文本框
	    {    	        
	        //$("#custInfo_REAL_NAME").attr('disabled','disabled');
			$("#custInfo_CUST_NAME").attr('disabled','disabled');
			$("#custInfo_PSPT_TYPE_CODE").attr('disabled','disabled');
			$("#custInfo_PSPT_ID").attr('disabled','disabled');
			$("#custInfo_PSPT_ADDR").attr('disabled','disabled');			
			$("#psptDIV").css('display','none');
			$("#psptAddrDIV").css('display','none');
			$("#custInfo_IS_REAL_NAME").val('1');   
	    }
	}	
	 
    var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val() ;
	if(psptTypeCode=="D" || psptTypeCode=="E" || psptTypeCode=="G" || psptTypeCode=="L" || psptTypeCode=="M"){	
		$("#span_BIRTHDAY").removeClass("e_required");
		$("#custInfo_BIRTHDAY").attr("nullable","yes");
	}else{
		$("#span_BIRTHDAY").addClass("e_required");		 
		$("#custInfo_BIRTHDAY").attr("nullable","no");
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
	if($("#custInfo_PSPT_TYPE_CODE").val() == "0" || $("#custInfo_PSPT_TYPE_CODE").val() == "1" || $("#custInfo_PSPT_TYPE_CODE").val() == "2"|| $("#custInfo_PSPT_TYPE_CODE").val() == "3"){
		if ($("#custInfo_PSPT_TYPE_CODE").val() == "0" || $("#custInfo_PSPT_TYPE_CODE").val() == "1"|| $("#custInfo_PSPT_TYPE_CODE").val() == "2"|| $("#custInfo_PSPT_TYPE_CODE").val() == "3" ) {
			$("#custInfo_PSPT_ID").attr("datatype","pspt");
		} else {
			$("#custInfo_PSPT_ID").attr("datatype","text");
		}
	    //如果客户性别为空,则根据身份证号自动设置性别
	    if($("#custInfo_SEX").val() == ""){
	    	setSexByPspt($("#custInfo_PSPT_ID").val(),$("#custInfo_SEX"));
   		}
	}else{
		$("#custInfo_PSPT_ID").attr("datatype","text");   
	}	
	//设置经办人证件号码的数据类型
	var agentPsptType = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
	if (agentPsptType == "0" || agentPsptType == "1" || agentPsptType == "2"|| agentPsptType == "3") {
		$("#custInfo_AGENT_PSPT_ID").attr("datatype","pspt");
	}else {
		$("#custInfo_AGENT_PSPT_ID").attr("datatype","text");
	}
	checkAgentInfo();
	
	$("#custInfo_IS_REAL_NAME").val('1');
	/*if(!$("#custInfo_REAL_NAME").attr("checked")){
		$("#custInfo_REAL_NAME").attr("checked",true);
		$("#custInfo_REAL_NAME").attr('disabled','disabled');
		$("#custInfo_IS_REAL_NAME").val('1');
	}*/


	
	
	if (realName == 'true')//已经办了实名制 则只需要屏蔽实名制 勾选框
	{
		if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2" || psptTypeCode=="3") {
			this.renderSpecialFieldByPsptId($("#custInfo_PSPT_ID").val());
		}
	}else{			
		//清空客户证件号、使用人证件号、经办人证件号
		$("#custInfo_PSPT_ID").val("");
		$("#custInfo_USE_PSPT_ID").val("");
		$("#custInfo_AGENT_PSPT_ID").val("");
	}
			
	setEnterpriseInfo(psptTypeCode);
	setOrgInfo(psptTypeCode);
}
function setEnterpriseInfo(psptTypeCode){	
	if(psptTypeCode=="E"){//法人、成立时间、营业开始时间、营业结束时间都要填		 
		$("#custInfo_legalperson").attr("nullable","no");
		$("#custInfo_startdate").attr("nullable","no");
		$("#custInfo_termstartdate").attr("nullable","no");
		$("#custInfo_termenddate").attr("nullable","no");
		$("#span_legalperson,#span_startdate,#span_termstartdate,#span_termenddate").addClass("e_required");		
		$("#enterprisePart").css("display","");		
	}else{
		$("#custInfo_legalperson").attr("nullable","yes");
		$("#custInfo_startdate").attr("nullable","yes");
		$("#custInfo_termstartdate").attr("nullable","yes");
		$("#custInfo_termenddate").attr("nullable","yes");
		$("#span_legalperson,#span_startdate,#span_termstartdate,#span_termenddate").removeClass("e_required");			
		$("#enterprisePart").css("display","none");	
		$("#custInfo_legalperson").val('');
		$("#custInfo_termstartdate").val('');
		$("#custInfo_termenddate").val('');
		$("#custInfo_startdate").val('');
	}
}

function setOrgInfo(psptTypeCode){
	if(psptTypeCode=="M"){//机构类型、有效日期、失效日期
		$("#custInfo_orgtype").attr("nullable","no");
		$("#custInfo_effectiveDate").attr("nullable","no");
		$("#custInfo_expirationDate").attr("nullable","no");
		$("#span_orgtype,#span_effectiveDate,#span_expirationDate").addClass("e_required");		
		$("#orgPart").css("display","");
	}else{ 
		$("#custInfo_orgtype").attr("nullable","yes");
		$("#custInfo_effectiveDate").attr("nullable","yes");
		$("#custInfo_expirationDate").attr("nullable","yes");
		$("#span_orgtype,#span_effectiveDate,#span_expirationDate").removeClass("e_required");				
		$("#orgPart").css("display","none");	
		$("#custInfo_orgtype").val('');
		$("#custInfo_effectiveDate").val('');
		$("#custInfo_expirationDate").val('');
	} 
}
//根据身份证号，自动设置性别
function setSexByPspt(psptId,sexField){
		if(psptId.length == 18 && (psptId.charAt(16)%2 == 0)){
			sexField.val('F');
		}
		if(psptId.length == 18 && (psptId.charAt(16)%2 == 1)){
			sexField.val('M');
		}
		if(psptId.length == 15 && (psptId.charAt(14)%2 == 0)){
			sexField.val('F');
		}
		if(psptId.length == 15 && (psptId.charAt(14)%2 == 1)){
			sexField.val('M');
		}
}

/*实名制验证*/
function chgRealname()
{   
	//var realNameField = $("#custInfo_REAL_NAME");realNameField.attr("checked") && 
    if ($("#custInfo_PSPT_TYPE_CODE").val()=="Z")
    {     
       alert("办理实名制用户，证件类型不能为其它！");
       //realNameField.attr("checked",false);
    }
    $("#custInfo_IS_REAL_NAME").val('1');
    /*if (realNameField.attr("checked")){
    	$("#custInfo_IS_REAL_NAME").val('1');
    }else{
    	$("#custInfo_IS_REAL_NAME").val('0');
    }*/
   
}

function checkPsptTypeCode(objId){
	checkAgentInfo();
	//实名制开户限制
	obj =  $("#"+objId);
	var psptTypeCode = obj.val();
	var custName = $.trim($("#custInfo_CUST_NAME").val());
	var custNameObj = $("#custInfo_CUST_NAME");
	var inputPermission = $("#INPUT_PERMISSION").val();
	if(inputPermission==0&&(psptTypeCode==0||psptTypeCode==1||psptTypeCode==3)){
		$("#custInfo_PSPT_ID").attr("disabled",true);
		$("#custInfo_PSPT_END_DATE").attr("disabled",true);
		$("#custInfo_PSPT_ADDR").attr("disabled",true);
		$("#custInfo_BIRTHDAY").attr("disabled",true);
		$("#custInfo_CUST_NAME").attr("disabled",true);
	}else{
		$("#custInfo_PSPT_ID").attr("disabled",false);
		$("#custInfo_PSPT_END_DATE").attr("disabled",false);
		$("#custInfo_PSPT_ADDR").attr("disabled",false);
		$("#custInfo_BIRTHDAY").attr("disabled",false);
		$("#custInfo_CUST_NAME").attr("disabled",false);
	}
	
	/**
	 * 以上界面户主使用身份证、户口本等证件办理入网业务时，营业员可通过手工调整生日信息，导致一经上传客户资料被拦截被判非实名，
	 * 请进行屏蔽，生日信息全部通过身份证号码进行截取，且不可更改，涉及证件类型为本地身份证、外地身份证、军人身份证、户口本，
	 * 其余个人证件不限制，需人工选择。 
	 * */
	if(psptTypeCode==0 || psptTypeCode==1||psptTypeCode==2||psptTypeCode==3)
	{
		$("#custInfo_BIRTHDAY").attr("disabled",true);
	}
	
	if("custInfo_AGENT_PSPT_TYPE_CODE"==objId){
		custName = $.trim($("#custInfo_AGENT_CUST_NAME").val());
		custNameObj = $("#custInfo_AGENT_CUST_NAME");
		this.checkCustName("custInfo_AGENT_CUST_NAME");
	}
	if("custInfo_USE_PSPT_TYPE_CODE"==objId){
		custName = $.trim($("#custInfo_USE").val());
		custNameObj = $("#custInfo_USE");
		this.checkCustName("custInfo_USE");
	}else{
		this.checkCustName("custInfo_CUST_NAME");
	}
	
    var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val() ;
	if(psptTypeCode=="D" || psptTypeCode=="E" || psptTypeCode=="G" || psptTypeCode=="L" || psptTypeCode=="M"){	
		$("#span_BIRTHDAY").removeClass("e_required");
		$("#custInfo_BIRTHDAY").attr("nullable","yes");
	}else{
		$("#span_BIRTHDAY").addClass("e_required");		 
		$("#custInfo_BIRTHDAY").attr("nullable","no");
	}

		
	
	if(custName!="" && psptTypeCode != "A"){
    	if(this.includeChineseCount(custName)<2){
    		alert(obj.attr("desc")+"不是护照，"+custNameObj.attr("desc")+"不能少于2个中文字符！");
    		custNameObj.val("");
			return ;
    	}
	}

	if('custInfo_AGENT_PSPT_TYPE_CODE'==objId){
	
		if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2"|| psptTypeCode == "3") {
			$("#custInfo_AGENT_PSPT_ID").attr("datatype","pspt");
		}else {
			$("#custInfo_AGENT_PSPT_ID").attr("datatype","text");
		}
		if (psptTypeCode != "0" && psptTypeCode != "1" && psptTypeCode != "2"&& psptTypeCode != "3") {
			$("#custInfo_AGENT_PSPT_ID").val("");
		}else{
			if($("#custInfo_AGENT_PSPT_ID").val()!="") {
				if(!$.validate.verifyField($("#custInfo_AGENT_PSPT_ID"))) {
					$("#custInfo_AGENT_PSPT_ID").val("");
					return;
				}
			}			
		}
				 
		var objValue = $("#custInfo_PSPT_TYPE_CODE").val();		
		 
			var agentObj=$("#custInfo_AGENT_PSPT_TYPE_CODE");
			var agentValue = agentObj.val();
			
			if(agentValue=="D" || agentValue=="E" || agentValue=="G" || agentValue=="L" || agentValue=="M")
			{
				
				alert("经办人只能选择个人证件，请重新选择！");
				agentObj.find("option[index=0]").attr("selected", true);
				$("#custInfo_AGENT_PSPT_ID").attr("datatype", "");
				return;				
			}			
		 
		
	}else if('custInfo_USE_PSPT_TYPE_CODE'==objId){
	
		if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3") {
			$("#custInfo_USE_PSPT_ID").attr("datatype","pspt");
		}else {
			$("#custInfo_USE_PSPT_ID").attr("datatype","text");
		}
		if (psptTypeCode != "0" && psptTypeCode != "1" && psptTypeCode != "2"&& psptTypeCode != "3") {
			$("#custInfo_USE_PSPT_ID").val("");
		}else{
			if($("#custInfo_USE_PSPT_ID").val()!="") {
				if(!$.validate.verifyField($("#custInfo_USE_PSPT_ID"))) {
					$("#custInfo_USE_PSPT_ID").val("");
					return;
				}
			}			
		}	
		
		//证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，
		//必须录入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址，这些信息为必填项目，
		//并且使用人的证件类型只能选择个人证件（身份证、户口本、护照、军官证、港澳台回乡证）证件校验规则同个人大众客户的规则一致。
		var objValue = $("#custInfo_PSPT_TYPE_CODE").val();
		if(objValue=="D" || objValue=="E" || objValue=="G" || objValue=="L" || objValue=="M")
		{
			var useObj=$("#custInfo_USE_PSPT_TYPE_CODE");
			var useObjValue = useObj.val();
			
			if(useObjValue=="D" || useObjValue=="E" || useObjValue=="G" || useObjValue=="L" || useObjValue=="M")
			{
				
				alert("证件类型选择集团证件，使用人的证件类型只能选择个人证件，请重新选择！");
				useObj.find("option[index=0]").attr("selected", true);
				$("#custInfo_USE_PSPT_ID").attr("datatype", "");
				return;				
			}			
		}		
	}
	else{
		if(psptTypeCode=="3"){
			alert("请提醒客户同时出示军人身份证明！并进行留存");
		}
		
		//0-本地身份证 1-外地身份证 2-户口本
		if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode=="2"|| psptTypeCode=="3") {
			$("#custInfo_PSPT_ID").attr("datatype","pspt");
			if($("#custInfo_PSPT_ID").val()!="") { 
				if(!$.validate.verifyField($("#custInfo_PSPT_ID"))) {
					$("#custInfo_PSPT_ID").val("");
					return;
				}
			}
		} else if (psptTypeCode == "Z") {//$("#custInfo_REAL_NAME").attr("checked") && 
			alert("办理实名制用户，证件类型不能为其它！");
			$("#custInfo_PSPT_TYPE_CODE").val(''); 
	    	$("#custInfo_PSPT_TYPE_CODE").focus()
		} else {
			$("#custInfo_PSPT_ID").attr("datatype","text");
			$("#custInfo_PSPT_ID").val("");
		}
		
		//证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，
		//必须录入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址，这些信息为必填项目，
		//并且使用人的证件类型只能选择个人证件（身份证、户口本、护照、军官证、港澳台回乡证）证件校验规则同个人大众客户的规则一致。
		var objValue = $("#custInfo_PSPT_TYPE_CODE").val();
		if(objValue=="D" || objValue=="E" || objValue=="G" || objValue=="L" || objValue=="M")
		{
			var useObj=$("#custInfo_USE_PSPT_TYPE_CODE");
			var useObjValue = useObj.val();
			
			if(useObjValue=="D" || useObjValue=="E" || useObjValue=="G" || useObjValue=="L" || useObjValue=="M")
			{				
				alert("证件类型选择集团证件，使用人的证件类型只能选择个人证件，请重新选择！");
				useObj.find("option[index=0]").attr("selected", true);
				$("#custInfo_USE_PSPT_ID").attr("datatype", "");
				return;				
			}
		}		
	}
	
	if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2"|| psptTypeCode=="3") {
		this.renderSpecialFieldByPsptId($("#custInfo_PSPT_ID").val());
	}
	
	setEnterpriseInfo(psptTypeCode);
	setOrgInfo(psptTypeCode);
	
	this.verifyIdCardName(objId);
	this.checkGlobalMorePsptId(objId);
}

//使用人基本数据校验
function checkUsePsptIdForReal(){
    if($("#custInfo_USE_PSPT_TYPE_CODE").val()=="Z") {
        alert("使用人证件类型不能为其他，请重新选择！");
        return false;
    }
    var psptId = $("#custInfo_USE_PSPT_ID").val();
    if($.toollib.isRepeatCode(psptId))
    {
        alert("使用人证件号码过于简单，请重新输入！");
        return false;
    }
    /*if($("#custInfo_USE").val().indexOf("海南通")>-1)
    {
        alert("使用人姓名不能为【海南通】，请重新输入！");
        $("#custInfo_USE").val("");
        return false;
    }*/
    if(!$.validate.verifyField("custInfo_USE_PSPT_ID")){
        return false;
    }
    return true;
}

//检查同一证件号已开实名制用户的数量是否已超出预定值
function checkRealNameLimitByUsePspt(){
    var custName = $("#custInfo_USE").val();
    var psptId = $("#custInfo_USE_PSPT_ID").val();
    if(custName == "" || psptId == ""){
        return false;
    }
    var serialNunber = $("#AUTH_SERIAL_NUMBER").val();
    var param = "&CUST_NAME="+custName+"&PSPT_ID="+psptId+"&SERIAL_NUMBER="+serialNunber;;
    //如果没有设置则取默认服务名
    /*if(this.realNameSVCName){
    	param += "&SVC_NAME="+this.realNameSVCName;
    }*/ 
    //param += "&SVC_NAME=SS.CreatePersonUserSVC.checkRealNameLimitByUsePspt";
    $.beginPageLoading("使用人证件信息数量校验。。。");
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
	$.ajax.submit(null, 'checkRealNameLimitByUsePspt', param, '', function(data){
		$.endPageLoading();
			if(data && data.get("CODE")!= "0"){
				$("#custInfo_USE_PSPT_ID").attr("value", "");
				alert(data.get("MSG"));
				return false;
			}
			//$("#span_USE_PSPT_ID").addClass("e_elements-success");
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function checkPsptId(objId){	
	
	obj =  $("#"+objId);
	var psptId = $.trim(obj.val());
	var desc = $.trim(obj.attr("desc"));
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	if("custInfo_AGENT_PSPT_ID"==objId){
		psptTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
		if($("#custInfo_AGENT_SCAN_TAG").val() == "0"){
			$("#custInfo_AGENT_SCAN_TAG").val("1");
		}else{
			$("#custInfo_AGENT_BACKBASE64").val("");
			$("#custInfo_AGENT_FRONTBASE64").val("");
		}
		
		if(psptTypeCode==0 || psptTypeCode==1||psptTypeCode==2||psptTypeCode==3){
			psptId = $("#custInfo_AGENT_PSPT_ID").val();
			var cust_age = this.jsGetAgeNew($.trim(psptId));
			/**
			 * BUG20180316152525_实名制相关业务控制优化bug
			 * <br/>
			 * 经办人年龄范围必须在11-120岁之间
			 * @author zhuoyingzhi
			 * @date 20180524
			 */
			/**
			 * REQ201808100006	关于调整实名制相关规则的需求 by mqx 20180823
			 * 办理人16岁以下，经办人必须满16岁
			 * @param idCard
			 * @return
			 */
			if(!this.checkAge($("#custInfo_PSPT_ID").val())){
				if(cust_age < 16){
					MessageBox.error("告警提示","经办人年龄必须满16岁",null, null, null, null);	
					$("#custInfo_AGENT_PSPT_ID").val("");
					return false;
				}
			}
			else if(cust_age < 11 || 120 <= cust_age){
				MessageBox.error("告警提示","经办人年龄范围必须在11-120岁之间",null, null, null, null);	
				$("#custInfo_AGENT_PSPT_ID").val("");
				return false;
			}
		}
		
		
		
	}else if("custInfo_USE_PSPT_ID"==objId){
		psptTypeCode = $("#custInfo_USE_PSPT_TYPE_CODE").val();

	}else{
		if(psptId.length == 0){ 
			alert("证件号码不能为空！");
			
			return false;
		}
		if($("#custInfo_SCAN_TAG").val() == "0"){
			$("#custInfo_SCAN_TAG").val("1");
		}else{
			$("#custInfo_BACKBASE64").val("");
			$("#custInfo_FRONTBASE64").val("");
		}
	}
	var serialNumber =  $("#AUTH_SERIAL_NUMBER").val();
	
	if($.toollib.isRepeatCode(psptId)){
		alert(desc+"不能全为同一个数字，请重新输入！");
		obj.val("");
		obj.focus();
		
		return false;
	}
	if($.toollib.isSerialCode(psptId)){
		alert("连续数字不能作为"+desc+"，请重新输入！");
		obj.val("");
		obj.focus();
		
		return false;
	}
	if(psptId.length>=4 && serialNumber.length>=psptId.length && ( serialNumber.indexOf(psptId) ==0 
		|| serialNumber.lastIndexOf(psptId) == (serialNumber.length-psptId.length) )){
		alert("电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为"+desc+"！");
		obj.val("");
		obj.focus();
		
		return false;
	}
	
	//港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
	if(psptTypeCode=="H"){
		if(psptId.length!=9 && psptId.length!=11){
			alert("港澳居民回乡证校验："+desc+"必须为9位或11位！");
			obj.val("");
			obj.focus();
			
			return false;
		}
		if( !(psptId.charAt(0)=="H" || psptId.charAt(0)=="M") || !$.toollib.isNumber(psptId.substr(1)) ){
			alert("港澳居民回乡证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字！");
			obj.val("");
			obj.focus();
			
			return false;
		}
	}else if(psptTypeCode=="O"){
		var psptIdtemp = psptId.replace(/\s/g,''); 				 				
		if(psptIdtemp!=psptId){
				alert("港澳居民来往内地通行证校验：证件号码中间不能有空格。");
				obj.val("");
				obj.focus();
				
				return false;
		}		
	    //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
		if(psptId.length!=9 && psptId.length!=11){
			alert("港澳居民来往内地通行证校验："+desc+"必须为9位或11位。");
			obj.val("");
			obj.focus();
			
			return false;
		}
		if( !(psptId.charAt(0)=="H" || psptId.charAt(0)=="M") || !$.toollib.isNumber(psptId.substr(1)) ){
			alert("港澳居民来往内地通行证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
			obj.val("");
			obj.focus();
			
			return false;
		}
   }else if(psptTypeCode=="I"){		
	   //台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
		if(psptId.length!=8 && psptId.length!=11){
			alert("台湾居民回乡校验："+desc+"必须为8位或11位！");
			obj.val("");
			obj.focus();
			
			return false;
		}
		if(psptId.length==8){
			if(!$.toollib.isNumber(psptId)){
				alert("台湾居民回乡校验："+desc+"为8位时，必须均为阿拉伯数字！");
				obj.val("");
				obj.focus();
				
				return false;
			}
		}
		if(psptId.length==11){
			if(!$.toollib.isNumber(psptId.substring(0,10))){
				alert("台湾居民回乡校验：："+desc+"为11位时，前10位必须均为阿拉伯数字。");
				obj.val("");
				obj.focus();
				
				return false;
			}
		}
}else if(psptTypeCode=="N"){
		//台湾居民来往大陆通行证:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
		
		var psptIdtemp = psptId.replace(/\s/g,''); 		
		if(psptIdtemp!=psptId){
			alert("台湾居民来往大陆通行证校验：证件号码中间不能有空格。");
			obj.val("");
			obj.focus();
			
			return false;
	    }
		if(psptId.substr(0, 2)!="TW"&&psptId.substr(0, 4)!="LXZH"){	 				

			if(psptId.length==11 ||psptId.length==12){
				if(!$.toollib.isNumber(psptId.substring(0,10))){
					alert("台湾居民来往大陆通行证校验："+desc+"为11或12位时，前10位必须均为阿拉伯数字。");
					obj.val("");
					obj.focus();
					
					return false;
				}
			}else if(psptId.length==8){
				if(!$.toollib.isNumber(psptId)){
					alert("台湾居民来往大陆通行证校验："+desc+"为8位时，必须均为阿拉伯数字。");
					obj.val("");
					obj.focus();
					
					return false;
				}
			}else if(psptId.length==7){
				if(!$.toollib.isNumber(psptId)){
					alert("台湾居民来往大陆通行证校验："+desc+"为7位时，必须均为阿拉伯数字。");
					obj.val("");
					obj.focus();
					
					return false;
				}
			}else{
				alert("台湾居民来往大陆通行证校验："+desc+"格式错误");
				obj.val("");
				obj.focus();
				
				return false;
			}
		}else{
			var psptIdsub ;
			if(psptId.substr(0, 2)=="TW"){
				psptIdsub = psptId.substr(2);
			}else if(psptId.substr(0, 4)=="LXZH"){
				psptIdsub = psptId.substr(4);
			}
			
			var re=/^[()A-Z0-9]+$/;
			var re1=/^[•··.．·\d\u4e00-\u9fa5]+$/;
			var pattern1 =/[A-Z]/;
			var pattern2 =/[0-9]/;
			var pattern3 =/[(]/;
			var pattern4 =/[)]/;
			
			if(re1.test(psptIdsub)||!re.test(psptIdsub)||!pattern1.test(psptIdsub)||!pattern2.test(psptIdsub)||!pattern3.test(psptIdsub)||!pattern4.test(psptIdsub)){
				alert("台湾居民来往大陆通行证校验："+desc+"前2位“TW”或 “LXZH”字符时，后面是阿拉伯数字、英文大写字母与半角“()”的组合");
				obj.val("");
				obj.focus();
				
				return false;
			}				
		}
	}else if(psptTypeCode=="C" || psptTypeCode=="A"){
		var psptIdtemp = psptId.replace(/\s/g,''); 				 				
		if(psptIdtemp!=psptId){
				alert("证件号码中间不能有空格。");
				obj.val("");
				obj.focus();
				
				return false;
		}			
		//军官证、警官证、护照：证件号码须大于等于6位字符
		if(psptId.length < 6){
			var tmpName= psptTypeCode=="A" ? "护照校验：" : "军官证类型校验：";
			alert(tmpName+desc+"须大于等于6位字符！");
			obj.val("");
			obj.focus();
			
			return false;
		}
	}else if(psptTypeCode=="E"){
		//营业执照：证件号码长度需满足15位 20151022 REQ201510140003 营业执照证件规则调整 CHENXY3
		if(psptId.length != 13 && psptId.length != 15 && psptId.length != 18 && psptId.length != 20 && psptId.length != 22 && psptId.length != 24){
			alert("营业执照类型校验："+desc+"长度需满足13位、15位、18位、20位、22位或24位！当前："+psptId.length+"位。");
			obj.val("");
			obj.focus();
			
			return false;
		}
	}else if(psptTypeCode=="M"){
		//组织机构代码校验
		if(psptId.length != 10 && psptId.length != 18){
			alert("组织机构代码证类型校验："+desc+"长度需满足10位或18位。");
			obj.val("");
			obj.focus();
			
			return false;
		}
		if(psptId.length == 10 &&psptId.charAt(8) != "-"){
			alert("组织机构代码证类型校验："+desc+"规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
			obj.val("");
			obj.focus();
			
			return false;
		}
	}else if(psptTypeCode=="G"){
		//事业单位法人登记证书：证件号码长度需满足12位
		if(psptId.length != 12 && psptId.length != 18){
			alert("事业单位法人登记证书类型校验："+desc+"长度需满足12位或者18位！");
			obj.val("");
			obj.focus();
			
			return false;
		}
	}else if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2"|| psptTypeCode == "3") {
		if(psptId.substr(psptId.length-1,psptId.length)=="x"){
			obj.val(psptId.substr(0,psptId.length-1)+"X");
		}
		if("custInfo_AGENT_PSPT_ID"==objId||"custInfo_USE_PSPT_ID"==objId){
		   if(trim(psptId)!=""&&!this.checkCustAge(psptId)){
		     	alert('年龄范围必须在11-120岁之间'); 
				obj.val("");
				obj.focus();
				
				return false;
		   }
		} 
		
		//身份证相关检查 
		obj.attr("datatype", "pspt");
		if(!$.validate.verifyField(obj)) {  
			obj.val("");
			obj.focus();
			
			return false;
		}
		
		var area = {11:"\u5317\u4eac", 12:"\u5929\u6d25", 13:"\u6cb3\u5317", 14:"\u5c71\u897f", 15:"\u5185\u8499\u53e4", 21:"\u8fbd\u5b81", 22:"\u5409\u6797", 23:"\u9ed1\u9f99\u6c5f", 31:"\u4e0a\u6d77", 32:"\u6c5f\u82cf", 33:"\u6d59\u6c5f", 34:"\u5b89\u5fbd", 35:"\u798f\u5efa", 36:"\u6c5f\u897f", 37:"\u5c71\u4e1c", 41:"\u6cb3\u5357", 42:"\u6e56\u5317", 43:"\u6e56\u5357", 44:"\u5e7f\u4e1c", 45:"\u5e7f\u897f", 46:"\u6d77\u5357", 50:"\u91cd\u5e86", 51:"\u56db\u5ddd", 52:"\u8d35\u5dde", 53:"\u4e91\u5357", 54:"\u897f\u85cf", 61:"\u9655\u897f", 62:"\u7518\u8083", 63:"\u9752\u6d77", 64:"\u5b81\u590f", 65:"\u65b0\u7586", 71:"\u53f0\u6e7e", 81:"\u9999\u6e2f", 82:"\u6fb3\u95e8", 91:"\u56fd\u5916"};
		psptId = psptId.toUpperCase();
		if (area[parseInt(psptId.substr(0, 2))] == null) {
			alert("\u8eab\u4efd\u8bc1\u53f7\u7801\u4e0d\u6b63\u786e\u0028\u5730\u533a\u975e\u6cd5\u0029\uff01");
			obj.val("");
			obj.focus();
			
			return false;
		}
				
		if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(psptId))) {
			alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u957f\u5ea6\u4e0d\u5bf9\uff0c\u6216\u8005\u53f7\u7801\u4e0d\u7b26\u5408\u89c4\u5b9a\uff01\n15\u4f4d\u53f7\u7801\u5e94\u5168\u4e3a\u6570\u5b57\uff0c18\u4f4d\u53f7\u7801\u672b\u4f4d\u53ef\u4ee5\u4e3a\u6570\u5b57\u6216X\u3002 ");
			obj.val("");
			obj.focus();
			
			return false;
		}
				
		// 下面分别分析出生日期和校验位
		var len, re;
		len = psptId.length;
		var arrSplit = "";
		var dtmBirth = "";
				
		if (len == 15) {
			re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
			arrSplit = psptId.match(re);  // 检查生日日期是否正确
			dtmBirth = new Date("19" + arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
		}
				
		if (len == 18) {
			re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
			arrSplit = psptId.match(re);  // 检查生日日期是否正确
			dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
		}
				
		var bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
		if (!bGoodDay) {
			alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u91cc\u51fa\u751f\u65e5\u671f\u4e0d\u5bf9\uff01");
			obj.val("");
			obj.focus();
			
			return false;
		}
				
		/*每位加权因子*/
		var powers= new Array("7","9","10","5","8","4","2","1","6","3","7","9","10","5","8","4","2");
		/*第18位校检码*/
		var parityBit=new Array("1","0","X","9","8","7","6","5","4","3","2");
		var psptBit = psptId.charAt(17).toUpperCase();
		var id17 = psptId.substring(0,17);    
		/*加权 */
		var power = 0;
		for(var i=0;i<17;i++){
			power += parseInt(id17.charAt(i),10) * parseInt(powers[i]);
		}              
		/*取模*/ 
		var mod = power % 11;
		var checkBit = parityBit[mod];
		if(psptBit!=checkBit){
			alert('身份证号码不合法'); 
			obj.val("");
			obj.focus();
			
			return false;
		}
			   
		var bit11 = psptId.substring(10,11);
    	var bit13 = psptId.substring(12,13);
		if(bit11!="0"&&bit11!="1")
		{
			alert('18位身份证号码11位必须为0或者1');
			obj.val("");
			obj.focus();
			
			return false;
		}
		if(parseInt(bit13)>3)
		{
			alert('18位身份证号码13位必须小于等于3'); 
			obj.val("");
			obj.focus();
			
			return false;
		}

		this.verifyIdCard(objId);
		if("custInfo_PSPT_ID"==objId){
			this.renderSpecialFieldByPsptId(psptId);
			//根据身份证号码设置性别
			setSexByPspt(psptId,$("#custInfo_SEX"));	
			$("#custInfo_BIRTHDAY").val(this.getBirthdayByPsptId(psptId));
			
		}
		
		
	}
	if( objId=="custInfo_USE_PSPT_ID" ){
		 		 
		if(!this.checkUsePsptIdForReal()) { 
			obj.val("");
			obj.focus();
			return;
		}
		this.checkRealNameLimitByUsePspt();
		
	}

    if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2"|| psptTypeCode == "3"
        || psptTypeCode == "O" || psptTypeCode == "N" || psptTypeCode == "P"
        || psptTypeCode == "Q" || psptTypeCode == "W"	) {
		this.verifyIdCard(objId);
	}
	
	this.verifyIdCardName(objId);	
	this.checkGlobalMorePsptId(objId);
	
	return true;

}

//根据身份证号码获取生日日期
function getBirthdayByPsptId(psptId) {
	var tmpStr="";
	if((psptId.length!=15) &&(psptId.length!=18)) {
	    alert("告警提示", "输入的证件号码位数错误！");
		return tmpStr;
	}
	if(psptId.length==15) {
		tmpStr= "19" + psptId.substring(6,12);
	}else {
    	tmpStr = psptId.substring(6,14);
	}
	return tmpStr.substring(0,4) + "-" + tmpStr.substring(4,6) + "-" + tmpStr.substring(6);
}
//客户名称验证
function checkCustName(objId){
	obj =  $("#"+objId);
	var custNameTrim=obj.val().replace(/\s+/g,"");
	obj.val(custNameTrim);
	var custName = $.trim(obj.val());
	var desc = obj.attr("desc");
	 
    var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
    var psptTypeDesc = $("#custInfo_PSPT_TYPE_CODE").attr("desc");
    if("custInfo_AGENT_CUST_NAME" == objId){
    	 psptTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
         psptTypeDesc = $("#custInfo_AGENT_PSPT_TYPE_CODE").attr("desc");
    }
    
    if("custInfo_USE" == objId){
    	
    	 psptTypeCode = $("#custInfo_USE_PSPT_TYPE_CODE").val();
         psptTypeDesc = $("#custInfo_USE_PSPT_TYPE_CODE").attr("desc");
         		
    }
    
    var re2 = new RegExp("^(全球通|动感地带|套餐|大灵通|乡镇通|无权户|无档户|代办|代理)*$"); 
	var re4 = new RegExp("^([0-9])*$"); 
	var specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
    
    if(!custName) return false;   
	if(custName == ""){
		return false;
    }
	
    var mainPsptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
    if(mainPsptTypeCode == "E"||mainPsptTypeCode == "G"||mainPsptTypeCode == "D" ||mainPsptTypeCode == "M" ||mainPsptTypeCode == "L"){
    	//	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
    	var custnametemp = trim($("#custInfo_CUST_NAME").val());
    	var agentnametemp = trim($("#custInfo_AGENT_CUST_NAME").val());
    	var usenametemp = trim($("#custInfo_USE").val());
    	if(custnametemp!=""&&agentnametemp!=""&&custnametemp==agentnametemp){
			alert("单位名称和经办人名称不能相同！");
			obj.val("");
			obj.focus();
			return;
    	}
    	if(custnametemp!=""&&usenametemp!=""&&custnametemp==usenametemp){
			alert("单位名称和使用人名称不能相同！");
			obj.val("");
			obj.focus();
			return;
    	}
    }else{
	   for(i=0;i<specialStr.length;i++){
		   if (custName.indexOf(specialStr.charAt(i)) > -1){
			   alert(obj.attr("desc")+"包含特殊字符，请检查！");
			   obj.val("");
			   obj.focus();
			   return false;
		   }
	   }
    }
	if(re2.test(custName)){
		alert(desc+"包含非法关键字！");
		obj.val("");
		obj.focus();
		return false;
	}
	
	if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2" || psptTypeCode=="3" || psptTypeCode=="C"|| psptTypeCode=="H"|| psptTypeCode=="I"|| psptTypeCode=="N"|| psptTypeCode=="O"){
		var re=/^[•··.．·\d\u4e00-\u9fa5]+$/;
		if(!re.test(custName)){
			alert(desc+"包含特殊字符，请检查！");
			obj.val("");
			obj.focus();
			return;
		}
	}
	
    if(psptTypeCode != "A" && psptTypeCode != "D"){
    	if(psptTypeCode != "E" && psptTypeCode != "G" && psptTypeCode != "D"  && psptTypeCode != "M"  && psptTypeCode != "L")
	     {//	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书	
    		var pattern =/[a-zA-Z0-9]/;
	         if(pattern.test(custName)){
	        	alert(psptTypeDesc+"不是护照, "+desc+"不能包含数字和字母！");
	        	obj.val("");
	        	obj.focus();
	        	return false;
	         }		
	     }

		if(this.includeChineseCount(custName)<2){
			alert(psptTypeDesc+"不是护照,"+desc+"不能少于2个中文字符！");
			obj.val("");
			obj.focus();
			return;
		}
		
	}else if(psptTypeCode == "A"){
		/*护照：客户名称须大于三个字符，不能全为阿拉伯数字*/
		if(custName.length<3 || $.toollib.isNumber(custName)){
			alert(psptTypeDesc+"是护照,"+desc+"须大于三个字符，且不能全为阿拉伯数字！");
			obj.val("");
			obj.focus();
			return false;
		}
		
		var specialStr ="“”‘’，《》~！@#￥%……&*（）【】｛｝；：‘’“”，。、《》？+——-=";
		for(i=0;i<specialStr.length;i++){
			if (custName.indexOf(specialStr.charAt(i)) > -1){
				alert(obj.attr("desc")+"包含特殊字符，请检查！");
				obj.val("");
				obj.focus();
				return;
			}
		}
	}
    
    if(psptTypeCode == "E"||psptTypeCode == "G"||psptTypeCode == "M"){//	营业执照、组织机构代码证、事业单位法人登记证书
    	if(this.includeChineseCount(custName)<4){
			alert(psptTypeDesc+"不是护照,"+desc+"不能少于4个中文字符！");
			obj.val("");
			obj.focus();
			return;
		}
    }
    
	if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2" || psptTypeCode=="3" || psptTypeCode=="C"|| psptTypeCode=="H"|| psptTypeCode=="I"|| psptTypeCode=="N"|| psptTypeCode=="O"){
		var re=/^[•··.．·\d\u4e00-\u9fa5]+$/;
		if(!re.test(custName)){
			alert(obj.attr("desc")+"包含特殊字符，请检查！");
			obj.val("");
			obj.focus();
			return;
		}
	}else if(psptTypeCode=="H"|| psptTypeCode=="I"|| psptTypeCode=="J"){
		//港澳证、台胞证
		if(custName.length<2){
			alert(psptTypeDesc+"是护照,"+desc+"须两个字符及以上");
			obj.val("");
			obj.focus();
			return;
		}
		var re=/^[•··.．·\d\u4e00-\u9fa5]+$/;
		if(!re.test(custName)){
			alert(obj.attr("desc")+"包含特殊字符，请检查！");
			obj.val("");
			obj.focus();
			return;
		}
	}

    
    /*if(custName.indexOf("校园")>-1 || custName.indexOf("海南通")>-1 || custName.indexOf("神州行")>-1
    		|| custName.indexOf("动感地带")>-1 || custName.indexOf("套餐")>-1) {
        alert(desc+"不能包含【校园、海南通、神州行、动感地带、套餐】，请重新输入！");
        obj.val("");
        obj.focus();
        return false;
    } */
    if("custInfo_CUST_NAME" == objId){
    	 var oldName =  $("#OLD_CUST_NAME").val();
    	 var newName =  $("#custInfo_CUST_NAME").val(); 
    	 var isRealName=$.auth.getAuthData().get('CUST_INFO').get("IS_REAL_NAME");
		if ($("#STAFF_SPECIAL_RIGTH").val() != "true"
				&& $("#IS_IN_PURCHASE").val() == "1" && oldName != newName &&　isRealName=="1") {
			alert("该用户还处在营销活动期限内，不能够修改"+desc+"！");
			$("#custInfo_CUST_NAME").val(oldName);
			return false;
		}
    }
 
    this.verifyIdCardName(objId);
    this.checkGlobalMorePsptId(objId);
    if("custInfo_USE" == objId){
    	if(this.checkUsePsptIdForReal()) {
			this.checkRealNameLimitByUsePspt();
		}
    }
    if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2"|| psptTypeCode == "3"
        || psptTypeCode == "O" || psptTypeCode == "N" || psptTypeCode == "P"
        || psptTypeCode == "Q" || psptTypeCode == "W"	) {
    	this.verifyIdCard(objId);
    }
   
	return true;

}
//客户名称验证
function checkContactName(objId){
	var obj =  $("#"+objId);
	var contactName = $.trim(obj.val());
	var desc = obj.attr("desc");
 
    
    var re2 = new RegExp("^(全球通|动感地带|套餐|大灵通|乡镇通|无权户|无档户|代办|代理)*$"); 
	var re4 = new RegExp("^([0-9])*$"); 
	var specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
    
    if(!contactName) return false;   
	if(contactName == ""){
		return false;
    }

	if(re4.test(contactName)){
		alert(desc+"不能为纯数字！");
		obj.val("");
		obj.focus();
		return false;
	}

	for(i=0;i<specialStr.length;i++){
		if (contactName.indexOf(specialStr.charAt(i)) > -1){
			alert(obj.attr("desc")+"包含特殊字符，请检查！");
			obj.val("");
			obj.focus();
			return false;
		}
	}
	if(re2.test(contactName)){
		alert(desc+"包含非法关键字！");
		obj.val("");
		obj.focus();
		return false;
	}
    /*if(contactName.indexOf("校园")>-1 || contactName.indexOf("海南通")>-1 || contactName.indexOf("神州行")>-1
    		|| contactName.indexOf("动感地带")>-1 || contactName.indexOf("套餐")>-1) {
        alert(desc+"不能包含【校园、海南通、神州行、动感地带、套餐】，请重新输入！");
        obj.val("");
        obj.focus();
        return false;
    }*/
    
	return true;
}

//生日检查 
function checkBirthday()
{
    if($("#custInfo_PSPT_TYPE_CODE").val()=="0" || $("#custInfo_PSPT_TYPE_CODE").val()=="1" || $("#custInfo_PSPT_TYPE_CODE").val()=="2"|| $("#custInfo_PSPT_TYPE_CODE").val()=="3")//如果当前证件类型为0和1(身份证)，则判断 身份证上的生日是否与所填的生日日期相同
    {    
          var birthday=$("#custInfo_BIRTHDAY").val();
      	  var psptId = $("#custInfo_PSPT_ID").val();
          var strBirthday =  getBirthdayByPsptId(psptId);;
               
          if(birthday!=strBirthday)//如果2者日期不同
          {
             if(confirm('身份证号码与生日不吻合，是否将生日改为与身份证号码一致？'))
    		{
    			$("#custInfo_BIRTHDAY").val(strBirthday);
    		}
          }  
    }
  return true;
}
function checkPhone(objId){
	var phoneObj = $("#"+objId);
	var desc = phoneObj.attr("desc");
	var phone = phoneObj.val();
	
	if(phone==""){
		return false;
	} 
	if(!$.toollib.isNumber(phone) || phone.length<6){
		alert(desc+"必须大于等于6阿拉伯数字！");
		phoneObj.val("");
		phoneObj.focus();
		return false;
	}
	
	if($.toollib.isRepeatCode(phone)){
		alert(desc+"不能全为同一个数字，请重新输入！");
		phoneObj.val("");
		phoneObj.focus();
		return false;
	}
	if($.toollib.isSerialCode(phone)){
		alert("连续数字不能作为"+desc+"，请重新输入！");
		phoneObj.val("");
		phoneObj.focus();
		return false;
	}
	
}

function checkAddr(objId)
{
	var custAddrObj =  $("#"+objId);
	var zjNum = this.trimAll(custAddrObj.val()).replace(/[^\x00-\xff]/g, "**").length ; //字节数
	if(custAddrObj.val()=="" || zjNum*1<12){
		 alert(custAddrObj.attr("desc")+'栏录入文字需不少于6个汉字或12个字节！');
		 custAddrObj.val('');
		 custAddrObj.focus();
		 return false;
	}
	 
	 if(!isNaN(custAddrObj.val())&& custAddrObj.val() != ''){
		 alert(custAddrObj.attr("desc")+'栏不能全部为数字');
		 custAddrObj.val('');
		 custAddrObj.focus();
		 return false;
	 }
	 return true;
}

function trimAll(str)
{     
  return str.replace(/(^\s+)|(\s+$)/g,"").replace(/\s/g,""); 
}

function checkAgentInfo(){
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();//这里确实是用客户证件类型来做判断
	      
	/*选择“集团客户”证件类型（营业执照、事业单位法人证书、单位证明、组织机构代码证、社会团体法人登记证书）开户时，
	 * 需支持录入经办人名称、经办人证件类型、经办人证件号码、经办人证件地址，这些信息为必填项目。经办人的证件校验规则同个人大众客户的规则一致。
	 * E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书*/
	if(psptTypeCode=="E" || psptTypeCode=="G" || psptTypeCode=="D" || psptTypeCode=="M" || psptTypeCode=="L"){
		$.beginPageLoading("正在判断数据...");
		var param = "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val(); 
		$.ajax.submit(null,'checkGroupPsptInfo',param,'',
				function(data){ 
					var flag=data.get("EXISE");
					if(flag=="FALSE"){ 
						$("#custInfo_USE").attr("nullable","no");
						$("#custInfo_USE_PSPT_TYPE_CODE").attr("nullable","no");
						$("#custInfo_USE_PSPT_ID").attr("nullable","no");
						$("#custInfo_USE_PSPT_ADDR").attr("nullable","no");
						$("#span_USE,#span_USE_PSPT_TYPE_CODE,#span_USE_PSPT_ID,#span_USE_PSPT_ADDR").addClass("e_required");
					}
				  	$.endPageLoading();
				},function(error_code,error_info){
					$.MessageBox.error(error_code,error_info);
					$.endPageLoading();
		});
		$("#custInfo_AGENT_CUST_NAME").attr("nullable","no");
		$("#custInfo_AGENT_PSPT_TYPE_CODE").attr("nullable","no");
		$("#custInfo_AGENT_PSPT_ID").attr("nullable","no");
		$("#custInfo_AGENT_PSPT_ADDR").attr("nullable","no");
		$("#span_AGENT_CUST_NAME,#span_AGENT_PSPT_TYPE_CODE,#span_AGENT_PSPT_ID,#span_AGENT_PSPT_ADDR").addClass("e_required");
	}else{  
		$("#custInfo_AGENT_CUST_NAME").attr("nullable","yes");
		$("#custInfo_AGENT_PSPT_TYPE_CODE").attr("nullable","yes");
		$("#custInfo_AGENT_PSPT_ID").attr("nullable","yes");
		$("#custInfo_AGENT_PSPT_ADDR").attr("nullable","yes");
		$("#span_AGENT_CUST_NAME,#span_AGENT_PSPT_TYPE_CODE,#span_AGENT_PSPT_ID,#span_AGENT_PSPT_ADDR").removeClass("e_required");
		
		$("#custInfo_USE").attr("nullable","yes");
		$("#custInfo_USE_PSPT_TYPE_CODE").attr("nullable","yes");
		$("#custInfo_USE_PSPT_ID").attr("nullable","yes");
		$("#custInfo_USE_PSPT_ADDR").attr("nullable","yes");
		$("#span_USE,#span_USE_PSPT_TYPE_CODE,#span_USE_PSPT_ID,#span_USE_PSPT_ADDR").removeClass("e_required");

	}
		
}
//扫描读取身份证信息
function clickScanPspt(){
	
	getMsgByEForm("custInfo_PSPT_ID","custInfo_CUST_NAME","custInfo_SEX","custInfo_FOLK_CODE","custInfo_BIRTHDAY","custInfo_PSPT_ADDR,custInfo_POST_ADDRESS",null,"custInfo_PSPT_END_DATE");
	
	this.renderSpecialFieldByPsptId($("#custInfo_PSPT_ID").val());
	
	this.verifyIdCard("custInfo_PSPT_ID");	
	this.verifyIdCardName(fieldName);
}

//扫描读取身份证信息
function clickScanPspt2(){
	var psptTypeCode=$("#custInfo_PSPT_TYPE_CODE").val();
	var needpicinfo = null;
	var tag = (psptTypeCode=="E" || psptTypeCode=="G" 
		|| psptTypeCode=="D" || psptTypeCode=="M" || psptTypeCode=="L")? true : false;
	if(tag){
		//客户证件类型为单位证件
		needpicinfo = "PIC_INFO";
	}
	
	getMsgByEForm("custInfo_AGENT_PSPT_ID","custInfo_AGENT_CUST_NAME",needpicinfo,null,null,"custInfo_AGENT_PSPT_ADDR",null,null);
	
	this.checkPsptId('custInfo_AGENT_PSPT_ID');
	
	this.verifyIdCard("custInfo_AGENT_PSPT_ID");
	this.verifyIdCardName(fieldName);
}

//扫描读取身份证信息（使用人）
function clickScanPspt3(){
	
	getMsgByEForm("custInfo_USE_PSPT_ID","custInfo_USE",null,null,null,"custInfo_USE_PSPT_ADDR",null,null);
	//this.verifyIdCard("custInfo_USE_PSPT_ID");
	//this.verifyIdCardName(fieldName);
}

/* 提交检查  */
function submitCheck(obj) {     
    
	/**************模糊化特殊处理**************************/
	var psptId = $("#custInfo_PSPT_ID").val();	
	if($.trim($("#custInfo_PSPT_TYPE_CODE").val())==""){
		alert("证件类型不能为空！");
		return false;
	}
	
	if(psptId.indexOf('*')>=0)//被模糊化了 则设置证件号码为text类型
	{
		$("#custInfo_PSPT_ID").attr("datatype","text");
	}else
	{
		var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
		
		if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode=="2"|| psptTypeCode=="3") {
			$("#custInfo_PSPT_ID").attr("datatype","pspt");
		}else
		{
			$("#custInfo_PSPT_ID").attr("datatype","text");
		}
	}

	var tradetypecode = $("#TRADE_TYPE_CODE").val();
	if(tradetypecode == 60){		
		var cmpTag = "1";
		$.ajax.submit(null,'isCmpPic','','',
				function(data){ 
			var flag=data.get("CMPTAG");
			if(flag=="0"){ 
				cmpTag = "0";
			}
			$.endPageLoading();
		},function(error_code,error_info){
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		},{
			"async" : false
		});
		if(cmpTag == "0"){		
			var picid = $("#custInfo_PIC_ID").val();
			
			if(null != picid && picid == "ERROR"){
				MessageBox.error("告警提示","客户"+$("#custInfo_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			var psptTypeCode=$("#custInfo_PSPT_TYPE_CODE").val();
			
			//经办人信息
			var agentpicid = $("#custInfo_AGENT_PIC_ID").val();
			var agentTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
			
			if((psptTypeCode == "0" || psptTypeCode == "1" ) && picid == ""){
				/**
				 * REQ201705270006_关于人像比对业务优化需求
				 * <br/>
				 * 客户资料变更：用户个人身份证证件，判断户主或者经办人人像比对通过即可
				 * @author zhuoyingzhi
				 * @date 20170620
				 */
				//经办人名称
			    var  custName = $("#custInfo_AGENT_CUST_NAME").val();
			    //经办人证件号码
				var  psptId = $("#custInfo_AGENT_PSPT_ID").val();
				//经办人证件地址
				var  agentPsptAddr= $("#custInfo_AGENT_PSPT_ADDR").val();
				
				if(agentTypeCode == ''&& custName == '' && psptId == '' && agentPsptAddr== ''){
					MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
					return false;
				}
				
				if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
					MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
					return false;
				}
			}
			
			if(null != agentpicid && agentpicid == "ERROR"){
				MessageBox.error("告警提示","经办人"+$("#custInfo_AGENT_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			
			if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
				MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
				return false;
			}
			/**
			 * REQ201707060020_关于年龄外经办人限制变更的优化
			 * <br/>
			 * 如果客户的证件类型为  户口本且客户未进行摄像  则提示客户摄像
			 * <br/>
			 * 不使用if((psptTypeCode == "0" || psptTypeCode == "1") && picid == "")这个
			 * 业务逻辑说明：证件类型为户口本 不对 经办人进行处理,为了不影响原来的判断逻辑，则单独写。
			 * @author zhuoyingzhi
			 * @date 20170803
			 */
			if(psptTypeCode == "2" && picid == ""){
				//未进行客户摄像
				//客户证件号码
				var custPsptId = $("#custInfo_PSPT_ID").val();	
				
				if(custPsptId != "" && checkCustAge(custPsptId,psptTypeCode) ){
					//11岁（含）至120岁（不含）之间的用户必须通过验证才可以办理（同身份证一致）； 
					MessageBox.error("告警提示","请进行客户摄像!",null, null, null, null);
					return false;
				}
			}
		}
	}
	var email = $("#custInfo_EMAIL").val();
	if(email.indexOf('*')>=0)//被模糊化了 则设置email为text类型
	{
		$("#custInfo_EMAIL").attr("datatype","text");
	}else
	{
		$("#custInfo_EMAIL").attr("datatype","email");
	}
	
	var phone = $("#custInfo_PHONE").val();
	if(phone.indexOf('*')>=0)//被模糊化了 则设置phone为text类型
	{
		$("#custInfo_PHONE").attr("datatype","text");
	}else
	{
		$("#custInfo_PHONE").attr("datatype","mbphone");
	}
	
	var agentPsptId = $("#custInfo_AGENT_PSPT_ID").val();
	if(agentPsptId.indexOf('*')>=0)//被模糊化了 则设置经办人证件号码为text类型
	{
		$("#custInfo_AGENT_PSPT_ID").attr("datatype","text");
	}else
	{
		var agentPsptTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
		if (agentPsptTypeCode == "0" || agentPsptTypeCode == "1" || agentPsptTypeCode=="2"|| agentPsptTypeCode=="3") {
			$("#custInfo_AGENT_PSPT_ID").attr("datatype","pspt");
		}else
		{
			$("#custInfo_AGENT_PSPT_ID").attr("datatype","text");
		}
	}		

	if (!$.validate.verifyAll()){
		return false;
	}
	


	 var psptAddrDIS = $("#custInfo_PSPT_ADDR").attr('disabled');//证件地址不可以操作
	 if(!psptAddrDIS){//add 因已经实名制的地址可能不大于8位，但没有修改权限的又不会显示出来，导致不能提交，客户提出当没显示出来就不需要验证长度
		 if( !checkAddr('custInfo_PSPT_ADDR')){
			 return false;
		 }
	 }

	$.cssubmit.addParam("&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"));
	//var realNameObj =  $("#custInfo_REAL_NAME"); && realNameObj.attr("checked")
	var oldIsRealName = $.auth.getAuthData().get('CUST_INFO').get("IS_REAL_NAME");
	if (oldIsRealName!=1) { //修改之前的客户不是实名制，代表本次是第一次做实名制办理
		if (confirm('您正在办理实名制，一旦提交资料将不能修改。请确认输入的资料无误！')) {
			return checkCustName("custInfo_CUST_NAME") && checkPsptId("custInfo_PSPT_ID")&& checkBirthday();
		} else {
			return false;
		}
	}
	
	return true;
}
function identification(picid,picstream){
	var custName,psptId,psptType,fornt;
	if(picid == "custInfo_PIC_ID"){
		custName = $("#custInfo_CUST_NAME").val();
		psptId = $("#custInfo_PSPT_ID").val();
		psptType = $("#custInfo_PSPT_TYPE_CODE").val();
		fornt = $("#custInfo_FRONTBASE64").val();
	}else{
		custName = $("#custInfo_AGENT_CUST_NAME").val();
		psptId = $("#custInfo_AGENT_PSPT_ID").val();
		psptType = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
		fornt = $("#custInfo_AGENT_FRONTBASE64").val();
	}
	if( psptId == ""){
		alert("请输入证件号码!");
		return false;
	}
	if(custName == ""){
		alert("请输入客户姓名!");
		return false;
	}
	/**
	 * REQ201707060020_关于年龄外经办人限制变更的优化
	 * @author zhuoyingzhi
	 * @date 20170803
	 */
	if(picid == "custInfo_PIC_ID" && psptType == "2"){
		//客户证件类型为  户口本
		if(!checkCustAge(psptId,psptType)){
			//年龄小于11或大于等于120岁的  不需要进行客户摄像
			alert("年龄小于11或大于等于120岁的 ,不需要进行客户摄像");
			return false;
		}
	}
	
	var bossOriginalXml = [];
	bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
	bossOriginalXml.push('<req>');
	bossOriginalXml.push('	<billid>'+'</billid>');
	bossOriginalXml.push('	<brand_name>'+'</brand_name>');
	bossOriginalXml.push('	<brand_code>'+'</brand_code>');
	bossOriginalXml.push('	<work_name>'+'</work_name>');
	bossOriginalXml.push('	<work_no>'+'</work_no>');
	bossOriginalXml.push('	<org_info>'+'</org_info>');
	bossOriginalXml.push('	<org_name>'+'</org_name>');
	bossOriginalXml.push('	<phone>'+$("#AUTH_SERIAL_NUMBER").val()+'</phone>');
	bossOriginalXml.push('	<serv_id>'+'</serv_id>');
	bossOriginalXml.push('	<op_time>'+'</op_time>');
	
	bossOriginalXml.push('	<busi_list>');
	bossOriginalXml.push('		<busi_info>');
	bossOriginalXml.push('			<op_code>'+'</op_code>');
	bossOriginalXml.push('			<sys_accept>'+'</sys_accept>');
	bossOriginalXml.push('			<busi_detail>'+'</busi_detail>');
	bossOriginalXml.push('		</busi_info>');
	bossOriginalXml.push('	</busi_list>');

	bossOriginalXml.push('	<verify_mode>'+'</verify_mode>');
	bossOriginalXml.push('	<id_card>'+'</id_card>');
	bossOriginalXml.push('	<cust_name>'+'</cust_name>');
	bossOriginalXml.push('	<copy_flag></copy_flag>');
	bossOriginalXml.push('	<agm_flag></agm_flag>');
	bossOriginalXml.push('</req>');
	var bossOriginaStr = bossOriginalXml.join("");
	//调用拍照方法
	var resultInfo = makeActiveX.Identification(bossOriginaStr);
	//获取保存结果
	var result = makeActiveX.IdentificationInfo.result;			
	//获取保存照片ID
	var picID = makeActiveX.IdentificationInfo.pic_id;
	
	if(picID != ''){
		alert("人像摄像成功");
	}else{
		alert("人像摄像失败");
		return false;
	}	
	//获取照片流
	var picStream = makeActiveX.IdentificationInfo.pic_stream;
	picStream = escape (encodeURIComponent(picStream));
	if("0" == result){
		$("#"+picid).val(picID);
		$("#"+picstream).val(picStream);
		var param = "&BLACK_TRADE_TYPE=60";			
		param += "&CERT_ID="+psptId;
		param += "&CERT_NAME="+custName;
		param += "&CERT_TYPE="+psptType;
		param += "&PIC_STREAM="+picStream+"&FRONTBASE64="+escape (encodeURIComponent(fornt));			
		param += "&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
		$.beginPageLoading("正在进行人像比对。。。");
		$.ajax.submit(null, 'cmpPicInfo', param, '', function(data){
			$.endPageLoading();
			if(data && data.get("X_RESULTCODE")== "0"){			
				MessageBox.success("成功提示","人像比对成功", null, null, null);
				return true;
			}else if(data && data.get("X_RESULTCODE")== "1"){
				$("#"+picid).val("ERROR");
				$("#"+picstream).val(data.get("X_RESULTINFO"));
				MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
				return false;
			}
		},
		function(error_code,error_info,derror){
			$("#"+picid).val("ERROR");
			$("#"+picstream).val("人像比对失败,请重新拍摄");
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}else{
		MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
	}
}
//
function verifyIdCard(fieldName){	
	var custNameObj,psptObj,psptTypeObj;
	if(fieldName == "custInfo_PSPT_ID"||fieldName=="custInfo_CUST_NAME"){
		custNameObj = $("#custInfo_CUST_NAME");
		psptObj = $("#custInfo_PSPT_ID");
		psptTypeObj = $("#custInfo_PSPT_TYPE_CODE"); 
	}else if(fieldName == "custInfo_USE_PSPT_ID"||fieldName=="custInfo_USE"){
		custNameObj = $("#custInfo_USE");
		psptObj = $("#custInfo_USE_PSPT_ID");
		psptTypeObj = $("#custInfo_USE_PSPT_TYPE_CODE"); 
	}else if(fieldName=="custInfo_AGENT_PSPT_ID"||fieldName=="custInfo_AGENT_CUST_NAME"){
		custNameObj = $("#custInfo_AGENT_CUST_NAME");
		psptObj = $("#custInfo_AGENT_PSPT_ID");
		psptTypeObj = $("#custInfo_AGENT_PSPT_TYPE_CODE"); 		 
	}
	var psptId = $.trim(psptObj.val());
	var custName = $.trim(custNameObj.val());
    if(custName == "" || psptId == ""){
        return false;
    }
	var param =  "&CERT_ID="+psptId+"&CERT_TYPE="+$.trim(psptTypeObj.val())+"&CERT_NAME="+encodeURIComponent(custName)+"&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER")
				+"&TRADE_TYPE_CODE=60";
	$.ajax.submit(null, 'verifyIdCard', param, '', function(data){
		$.endPageLoading();
		if(data && data.get("X_RESULTCODE")== "1"){			
			//MessageBox.error("告警提示","该证件在公安部系统校验不通过，建议用户到当地派出所核对自己的证件信息。若是军人请用军官证或军人身份证开户。",null, null, null, null);
			MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);	
			psptObj.val("");
			return false;
		}else if(data && data.get("X_RESULTCODE")== "2"){ 
			MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);						
			return true;
		}else{
			return true;
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}

function verifyIdCardName(fieldName){
	var custNameObj,psptObj,psptcodeObj;
	if(fieldName == "custInfo_PSPT_ID"||fieldName=="custInfo_CUST_NAME"){
		custNameObj = $("#custInfo_CUST_NAME");
		psptObj = $("#custInfo_PSPT_ID");
		psptcodeObj = $("#custInfo_PSPT_TYPE_CODE");
	}else if(fieldName == "custInfo_USE_PSPT_ID"||fieldName=="custInfo_USE"){		 
		custNameObj = $("#custInfo_USE");
		psptObj = $("#custInfo_USE_PSPT_ID");
		psptcodeObj = $("#custInfo_USE_PSPT_TYPE_CODE");
	}else if(fieldName=="custInfo_AGENT_PSPT_ID"||fieldName=="custInfo_AGENT_CUST_NAME"){		 
		custNameObj = $("#custInfo_AGENT_CUST_NAME");
		psptObj = $("#custInfo_AGENT_PSPT_ID");
		psptcodeObj = $("#custInfo_AGENT_PSPT_TYPE_CODE");
	}
	var psptId = $.trim(psptObj.val());
	var custName = $.trim(custNameObj.val());
	var psptcode = $.trim(psptcodeObj.val());
    if(custName == "" || psptId == ""||psptcode==""){
        return false;
    }
    //营业执照、组织机构代码证、事业单位法人登记证书
    //if(psptcode!="E"&&psptcode!="G"&&psptcode!="M"){
    if(psptcode=="0"||psptcode=="1"||psptcode=="2"||psptcode=="3"){//《一证多名需求》，除了本地外地身份证和户口，其他证件都不允许一证多名
    	return false;
    }
    var param =  "&CERT_TYPE="+psptcode+"&CERT_ID="+psptId +"&CERT_NAME="+encodeURIComponent(custName)+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
    $.beginPageLoading("单位名称校验。。。");
	$.ajax.submit(null, 'verifyIdCardName', param, '', function(data){
		$.endPageLoading();						
		if(data && data.get("X_RESULTCODE")!= "0"){			
			MessageBox.error("告警提示","同一个证件号码不能对应不同的名称",null, null, null, null);
			psptObj.val("");
			return false;
		}else{
			verifyEnterpriseCard();
			verifyOrgCard();
			//return true;
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		});
}

function includeChinese(custName){
	// 是否包含中文字符
	for(var i=0; i<custName.length; i++){
		if($.toollib.isChinese(custName.charAt(i))){
			return true;
		}
	}
	return false;
}

function includeChineseCount(custName){
	// 是否包含中文字符个数
	var count = 0;
	for(var i=0; i<custName.length; i++){
		if($.toollib.isChinese(custName.charAt(i))){
			count++;
		}
	}
	return count;
}

function renderSpecialFieldByPsptId(psptId){//检查年龄是否在16-120之间，显示隐藏经办人栏	
	if(trim(psptId)!=""&&!this.checkAge(psptId)){	
		var specialRigth = $("#STAFF_SPECIAL_RIGTH").val();//特殊修改权限
		var realName  = $("#custInfo_REAL_NAME").val();//是否已办理实名制
		if (specialRigth!='true' && realName == 'true')//没有特殊权限且证件号码，证件地址不能修改，屏蔽证件号码和证件地址文本框
	    {
			return;
	    }
		//REQ201808100006	关于调整实名制相关规则的需求 by mqx 20180823
		//加权限判断
			$.ajax.submit(null, 'verifyAgentPriv', null, '', function(data){
				$.endPageLoading();			
				var permission = data.get("hasAgentPriv");
				if(data && permission!="true"){			
					MessageBox.error("告警提示","该工号没有代办入网的权限！（仅限自办营业厅工号能申请该权限）",null, null, null, null);
					psptObj = $("#custInfo_PSPT_ID");
					psptObj.val("");
					
					$("#custInfo_AGENT_CUST_NAME").attr("nullable","yes");
					$("#custInfo_AGENT_PSPT_TYPE_CODE").attr("nullable","yes");
					$("#custInfo_AGENT_PSPT_ID").attr("nullable","yes");
					$("#custInfo_AGENT_PSPT_ADDR").attr("nullable","yes");
					$("#span_AGENT_CUST_NAME,#span_AGENT_PSPT_TYPE_CODE,#span_AGENT_PSPT_ID,#span_AGENT_PSPT_ADDR").removeClass("e_required");
				}
				else{
					$("#custInfo_AGENT_CUST_NAME").attr("nullable","no");
					$("#custInfo_AGENT_PSPT_TYPE_CODE").attr("nullable","no");
					$("#custInfo_AGENT_PSPT_ID").attr("nullable","no");
					$("#custInfo_AGENT_PSPT_ADDR").attr("nullable","no");
					$("#span_AGENT_CUST_NAME,#span_AGENT_PSPT_TYPE_CODE,#span_AGENT_PSPT_ID,#span_AGENT_PSPT_ADDR").addClass("e_required");		
				}
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				alert(error_info);
				}
			);
	}else{
		$("#custInfo_AGENT_CUST_NAME").attr("nullable","yes");
		$("#custInfo_AGENT_PSPT_TYPE_CODE").attr("nullable","yes");
		$("#custInfo_AGENT_PSPT_ID").attr("nullable","yes");
		$("#custInfo_AGENT_PSPT_ADDR").attr("nullable","yes");
		$("#span_AGENT_CUST_NAME,#span_AGENT_PSPT_TYPE_CODE,#span_AGENT_PSPT_ID,#span_AGENT_PSPT_ADDR").removeClass("e_required");
	}
}
//	营业执照在线校验
function verifyEnterpriseCard(){	
    var psptcode = $.trim($("#custInfo_PSPT_TYPE_CODE").val());
    var psptId = $.trim($("#custInfo_PSPT_ID").val());
    var psptName = $.trim($("#custInfo_CUST_NAME").val());
    var legalperson = $.trim($("#custInfo_legalperson").val());
    var termstartdate = $.trim($("#custInfo_termstartdate").val());
    var termenddate = $.trim($("#custInfo_termenddate").val());
    var startdate = $.trim($("#custInfo_startdate").val());
    
   
    //营业执照 
    if(psptcode!="E" ){
    	return false;
    }
    if(psptId == ""||psptName==""||psptcode==""||legalperson==""||termstartdate==""||termenddate==""||startdate==""){
       return false;
    }
	 /**
	  * REQ201706130001_关于录入联网核验情况的需求
	  * @author zhuoyingzhi
	  * @date 20170921
	  */
    var serialNunber = $("#AUTH_SERIAL_NUMBER").val();
    
    var param =  "&regitNo="+psptId+"&enterpriseName="+encodeURIComponent(psptName)+"&legalperson="+encodeURIComponent(legalperson)
                 +"&termstartdate="+encodeURIComponent(termstartdate)+"&termenddate="+encodeURIComponent(termenddate)+"&startdate="+encodeURIComponent(startdate)
                 +"&SERIAL_NUMBER="+serialNunber+"&TRADE_TYPE_CODE=60"; 
    $.beginPageLoading("营业执照校验。。。");	
	$.ajax.submit(null, 'verifyEnterpriseCard', param, '', function(data){
		$.endPageLoading();
		if(data && data.get("X_RESULTCODE")!= "0"){
			$("#custInfo_PSPT_ID").val('');
			MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
			return false;
		}else{
			return true;
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#custInfo_PSPT_ID").val('');
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}

//	组织机构代码在线校验
function verifyOrgCard(){
    var psptcode = $.trim($("#custInfo_PSPT_TYPE_CODE").val());
    var psptId = $.trim($("#custInfo_PSPT_ID").val());
    var psptName = $.trim($("#custInfo_CUST_NAME").val());
    var orgtype = $.trim($("#custInfo_orgtype").val());
    var effectiveDate = $.trim($("#custInfo_effectiveDate").val());
    var expirationDate = $.trim($("#custInfo_expirationDate").val());		    

    // 组织机构代码证
    if(psptcode!="M" ){
    	return false;
    }
    
    if(psptId == ""||psptName==""||psptcode==""||orgtype==""||effectiveDate==""||expirationDate==""){
       return false;
    }
    
    var param =  "&orgCode="+psptId+"&orgName="+encodeURIComponent(psptName)+"&orgtype="+encodeURIComponent(orgtype)
                 +"&effectiveDate="+encodeURIComponent(effectiveDate)+"&expirationDate="+encodeURIComponent(expirationDate); 
    $.beginPageLoading("组织机构代码证校验。。。");
	$.ajax.submit(null, 'verifyOrgCard', param, '', function(data){
		$.endPageLoading();
		if(data && data.get("X_RESULTCODE")!= "0"){ 
			$("#custInfo_PSPT_ID").val('');
			MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
			return false;
		}else{
			return true;
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#custInfo_PSPT_ID").val('');
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
 }    
	

function checkAge(idCard){
	if(!idCard){return false;}
	var _age = this.jsGetAgeNew(idCard);			
	/*
	var bstr = idCard.substring(6,14)
	var _now = new Date();
	var _bir = new Date(bstr.substring(0,4),bstr.substring(4,6),bstr.substring(6,8));
	var _agen = _now-_bir;
	var _age = Math.round(_agen/(365*24*60*60*1000));*/
	
	//REQ201808100006	关于调整实名制相关规则的需求 by mqx 20180823
	return _age>=16 && _age<=120;
}

/**
 * REQ201808100006	关于调整实名制相关规则的需求 by mqx 20180823
 * 要求精确到天
 * @param idCard
 * @return
 */
function jsGetAgeNew(idCard){				 
    var returnAge;
	var bstr = idCard.substring(6,14)			 
    var birthYear = bstr.substring(0,4);
    var birthMonth = bstr.substring(4,6);
    var birthDay = bstr.substring(6,8);
    
    var d = new Date();
    var nowYear = d.getFullYear();
    var nowMonth = d.getMonth() + 1;
    var nowDay = d.getDate();
    
    if(nowYear == birthYear)
    {
        returnAge = 0;//同年 则为0岁
    }
    else
    {
        var ageDiff = nowYear - birthYear ; //年之差
        if(ageDiff > 0)
        {
            if(nowMonth == birthMonth)
            {
                var dayDiff = nowDay - birthDay;//日之差
                if(dayDiff < 0)
                {
                    returnAge = ageDiff - 1;
                }
                else
                {
                    returnAge = ageDiff ;
                }
            }
            else
            { 
                var monthDiff = nowMonth - birthMonth;//月之差
                if(monthDiff <= 0)
                {
                    returnAge = ageDiff - 1;
                }
                else
                {
                    returnAge = ageDiff ;
                }
            }
        }
        else
        {
            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
        }
    }
    return returnAge;//返回周岁年龄		    
}

function jsGetAge(idCard){				 
    var returnAge;
	var bstr = idCard.substring(6,14)			 
    var birthYear = bstr.substring(0,4);
    var birthMonth = bstr.substring(4,6);
    var birthDay = bstr.substring(6,8);
    
    var d = new Date();
    var nowYear = d.getFullYear();
    var nowMonth = d.getMonth() + 1;
    var nowDay = d.getDate();
    
    if(nowYear == birthYear)
    {
        returnAge = 0;//同年 则为0岁
    }
    else
    {
        var ageDiff = nowYear - birthYear ; //年之差
        if(ageDiff > 0)
        {
		/*
            if(nowMonth == birthMonth)
            {
                var dayDiff = nowDay - birthDay;//日之差
                if(dayDiff < 0)
                {
                    returnAge = ageDiff - 1;
                }
                else
                {
                    returnAge = ageDiff ;
                }
            }
            else
            { */
                var monthDiff = nowMonth - birthMonth;//月之差
                if(monthDiff <= 0)
                {
                    returnAge = ageDiff - 1;
                }
                else
                {
                    returnAge = ageDiff ;
                }
           // }
        }
        else
        {
            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
        }
    }
    return returnAge;//返回周岁年龄		    
}


//全网一证五号检查
function checkGlobalMorePsptId(objId){//全网1证5号需求， 开户证件、使用人证件校验
	
  var custName = "";
  var psptId = "";
  var psptTypeCode = "";
  var clearPsptId = "";
	if(objId=="custInfo_USE_PSPT_ID"||objId=="custInfo_USE"||objId=="custInfo_USE_PSPT_TYPE_CODE"){
	     custName = $("#custInfo_USE").val();
	     psptId = $("#custInfo_USE_PSPT_ID").val();
	     psptTypeCode = $("#custInfo_USE_PSPT_TYPE_CODE").val();
	     clearPsptId = "#custInfo_USE_PSPT_ID";
	}else if(objId=="custInfo_PSPT_ID"||objId=="custInfo_CUST_NAME"||objId=="custInfo_PSPT_TYPE_CODE"){
	     custName = $("#custInfo_CUST_NAME").val();
	     psptId = $("#custInfo_PSPT_ID").val();
	     psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	     clearPsptId = "#custInfo_PSPT_ID";
	}
  
  if(custName == "" || psptId == "" || psptTypeCode == ""){
      return false;
  }

  var param = "&CUST_NAME="+encodeURIComponent(custName)+"&PSPT_ID="+psptId+"&PSPT_TYPE_CODE="+psptTypeCode
               +"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();      
  $.beginPageLoading("全网证件信息数量校验。。。");
	$.ajax.submit(null, 'checkGlobalMorePsptId', param, '', function(data){
		$.endPageLoading();
			if(data && data.get("CODE")!= "0"){
				if(data.get("CODE")== "1"){
					$(clearPsptId).val("");
				}
				alert(data.get("MSG"));
				return false;
			}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$(clearPsptId).val("");
		showDetailErrorInfo(error_code,error_info,derror);
  });
}

/**
 * REQ201707060020_关于年龄外经办人限制变更的优化
 * <br/>
 * 判断是否在
 * 11岁（含）至120岁（不含）之间的
 * @param idCard
 * @param psptTypeCode
 */
function checkCustAge(idCard,psptTypeCode){

	//根据身份证  获取周岁(已有的方法)
	var cust_age = this.jsGetAge(idCard);
	
	if(11 <=cust_age && cust_age < 120 ){
		//11岁（含）至120岁（不含）
		return true;
	}else{
		return false;
	}
}
