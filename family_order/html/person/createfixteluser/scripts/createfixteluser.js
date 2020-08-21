/*$Id:$*/
var PAGE_FEE_LIST = $.DataMap();
var PAGE_SCREET_FEE_LIST = $.DataMap();
var PAGE_DEVICEJFQ_FEE_LIST = $.DataMap();
var PAGE_DEVICEZJ_FEE_LIST = $.DataMap();

/*************************************校验开户号码及SIM资源 开始************************************/
/*新开户号码校验*/
function checkSerialNumber() {	

	//初始化部分界面数据
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-error");	    
    $("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
	$("#PSPT_INPUT").removeClass("e_elements-success");
	$("#PSPT_INPUT").removeClass("e_elements-error");
	intiAllElements();
	//清空费用
	$.feeMgr.clearFeeList("9701");
	PAGE_FEE_LIST.clear();
	//重置按钮
	$.cssubmit.disabledSubmitBtn(true);
	//清空产品资料
	if($("#EPARCHY_CODE").val() !=''){
	    initProduct();
	}
	//先将SIM卡号清空
	$("#SIM_CARD_NO").val("898600");
	$("#SIM_CARD_NO").attr("disabled", false);
	
	if($("#SERIAL_NUMBER").val()==''||$("#SERIAL_NUMBER").val().length != 8) {
		alert("号码长度必须为8位！");
	     $("#SERIAL_NUMBER").val('');
	     return false;
	}
	//如果开户号码为空，或格式不正确，则返回
	if(!$.validate.verifyField($("#SERIAL_NUMBER")[0])) {
		return false;
	}

	var serialNumber = "898" + $("#SERIAL_NUMBER").val();
	var oldSimCardNo = $("#OLD_SIM_CARD_NO").val();	//考虑预配情况
	$("#CHECK_RESULT_CODE").val("-1");		//设置号码未校验
	
	var oldSerialNumber = "";
	if(!isNull($("#OLD_SERIAL_NUMBER").val())) {
		oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
	}

	var agentId = $("#AGENT_DEPART_ID");		//开户代理商编码
	var agentDepartId="";
	//不为undefined，表示代理商开户
	if(agentId.length) {
		if(agentId.val()=="") {
			alert("请选择开户代理商！");
			$("#AGENT_DEPART_ID").focus();
			return false;
		}
		agentDepartId = agentId.val().split("#")[0];	//截取代理商部门id
		$("#AGENT_DEPART_ID").val(agentDepartId);
	}
	var param = "&SERIAL_NUMBER=" + serialNumber + "&OLD_SERIAL_NUMBER=" + oldSerialNumber + "&OLD_SIM_CARD_NO=" + oldSimCardNo + "&AGENT_DEPART_ID=" + agentDepartId 
	         + "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()+ "&CHR_CHECKSELENUM=" + $("#CHR_CHECKSELENUM").val() + "&OPEN_TYPE=" + $("#OPEN_TYPE").val();
	$.beginPageLoading("新开户号码校验中......");         
	$.ajax.submit(null, 'checkSerialNumber', param, 'AcctDayPart,NoteTypePart,CityCodePart,CheckSimCardNoHidePart,CheckSerialNumberHidePart,OtherInfoPart,PostInfoPart,ProductTypePart', function(data) {
		//TODO 提示信息待处理
		//增加开户可提示相关信息(规则已终止，省略)
		
		$("#OLD_SERIAL_NUMBER").val(serialNumber);
		
		if($("#B_REOPEN_TAG").val() != "1") {
			$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
			//校验完后界面部分数据处理
			setAjaxAtferCheckMphone(data);
			$.endPageLoading();
		}
		else {
			$("#SIM_CARD_NO").attr("disabled", true);
			//二次开户基本区域数据展现
			var data0 = data.get(0);
			var simCardNo =data0.get("SIM_CARD_NO");
			var psptTypeCode =data0.get("PSPT_TYPE_CODE");
			var psptId =data0.get("PSPT_ID");
			var psptEndDate= data0.get("PSPT_END_DATE");
	        var psptAddr= data0.get("PSPT_ADDR");
	        var userTypeCode= data0.get("USER_TYPE_CODE");
	        var custName= data0.get("CUST_NAME");
	        var birthday= data0.get("BIRTHDAY");
	        var phone= data0.get("PHONE");
	        var postCode= data0.get("POST_CODE");
	        var noteType= data0.get("NOTE_TYPE");
	        var postAddress= data0.get("POST_ADDRESS");
	        var payName= data0.get("PAY_NAME");
	        var payModeCode= data0.get("PAY_MODE_CODE");
	        var superBankCode= data0.get("SUPER_BANK_CODE");
	        var bankCode= data0.get("BANK_CODE");
	        var bankAcctNo= data0.get("BANK_ACCT_NO");
	        var remark= data0.get("REMARK");
			$("#SIM_CARD_NO").val(simCardNo);
			$("#PSPT_TYPE_CODE").val(psptTypeCode);
			$("#PSPT_ID").val(psptId);
			$("#OLD_PSPT_TYPE_CODE").val(psptTypeCode);
			$("#OLD_PSPT_ID").val(psptId);
			$("#PSPT_END_DATE").val(psptEndDate);			
			$("#PSPT_ADDR").val(psptAddr);
			$("#USER_TYPE_CODE").val(userTypeCode);
			$("#CUST_NAME").val(custName);
			$("#BIRTHDAY").val(birthday);
			$("#PHONE").val(phone);
			$("#POST_CODE").val(postCode);
			$("#NOTE_TYPE").val(noteType);
			$("#PAY_NAME").val(payName);
			$("#PAY_MODE_CODE").val(payModeCode);
			$("#SUPER_BANK_CODE").val(superBankCode);
			$("#BANK_CODE").val(bankCode);
			$("#BANK_ACCT_NO").val(bankAcctNo);
			$("#REMARK").val(remark);
			
			var param = "&USER_ID="+data0.get("USER_ID")+"&USER_PRODUCT_ID="+data0.get("PRODUCT_ID")+"&B_REOPEN_TAG="+$("#B_REOPEN_TAG").val();
			selectedElements.renderComponent(param,data0.get("EPARCHY_CODE"));
			$("#productSelectBtn").attr("disabled", true);
			$("#PRODUCT_NAME").attr("disabled", true);
			$("#PRODUCT_TYPE_CODE").attr("disabled", true);
			$("#preChoosePart").css("display","none");
			$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
			
			$("#USER_ID").val(data0.get("USER_ID"));
			$("#CUST_ID").val(data0.get("CUST_ID"));
			$("#PRODUCT_ID").val(data0.get("PRODUCT_ID"));
			$("#BRAND_CODE").val(data0.get("BRAND_CODE"));
			
			var feeData = $.DataMap();	
			feeData.put("MODE", "2");
			feeData.put("CODE", "0");
			feeData.put("FEE",  "0");
			feeData.put("PAY",  "0");		
			feeData.put("TRADE_TYPE_CODE","9701");			
			$.feeMgr.insertFee(feeData);	
			
			$("#ACCT_DAY").val('1');
			$("#ACCT_DAY").attr("disabled", true);
			$("#HINT").css("display", "");
		    $("#HINT").text("该号码已经开户，下面进行二次开户！");
		   
		    $("#productSearchType").attr("disabled", true);
		    $("#productSearch").attr("disabled", true);
		    $.cssubmit.disabledSubmitBtn(false);
		
		}
		
		if(	$("#PAY_MODE_CODE").val()=="0"){
		   $("#SUPER_BANK_CODE").attr("disabled", true);
		   $("#BANK_CODE").attr("disabled", true);
		   $("#BANK_ACCT_NO").attr("disabled", true);
		}
		   $.endPageLoading();
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
	showDetailErrorInfo(error_code,error_info,derror);
    });
	
	$("#SERIAL_NUMBER").val("898" + $("#SERIAL_NUMBER").val());
}
/*新开户号码校验完后返回值的处理*/
function setAjaxAtferCheckMphone(data) {
	var data0 = data.get(0);
	var simCardNo =data0.get("SIM_CARD_NO");
    var checkResultCode = data0.get("CHECK_RESULT_CODE");
	//预配预开时，sim卡自动带出来，且为不可修改
	if(!isNull(simCardNo)) {
		$("#SIM_CARD_NO").val(simCardNo);
		$("#OLD_SIM_CARD_NO").val(simCardNo);
		$("#SIM_CARD_NO").attr("disabled", true);
		$("#CHECK_RESULT_CODE").val(checkResultCode);//设置sim卡校验通过
		$("#HINT").css("display", "");
		$("#HINT").text("号码和sim卡校验成功！");
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
		$("#SIM_CARD_INPUT").addClass("e_elements-success");
		$("#SIM_CARD_INPUT").attr("disabled", true);
	}
	else {
		$("#SIM_CARD_NO").val("898600");
		$("#SIM_CARD_NO").attr("disabled", false);
		$("#CHECK_RESULT_CODE").val(checkResultCode);		//设置号码校验通过
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
	}
	
	if(data0.get("FEE")){
		var feeData = $.DataMap();
		feeData.put("MODE", data0.get("FEE_MODE"));
		feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
		feeData.put("FEE",  data0.get("FEE"));
		feeData.put("PAY",  data0.get("FEE"));		
		feeData.put("TRADE_TYPE_CODE","9701");		
		PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.cloneData(feeData));
		$.feeMgr.insertFee(feeData);	
		
	}
	//物联网开户
	if($("#OPEN_TYPE").val()=='IOT_OPEN'){
		$("#productSearchType").attr("disabled", true);
		$("#productSearch").attr("disabled", true);
	}
}

/*新开户号码校验，初始化界面部分数据*/
function intiAllElements() {
	$("#PSPT_END_DATE").val("");
	$("#PSPT_ID").val("");
	$("#CUST_NAME").val("");
	$("#POST_ADDRESS").val("");
	$("#POST_CODE").val("");
	$("#PHONE").val("");
	$("#PSPT_ADDR").val("");
	$("#CONTACT").val("");
	$("#CONTACT_PHONE").val("");
	$("#WORK_NAME").val("");
	$("#WORK_DEPART").val("");
	$("#EMAIL").val("");
	$("#FAX_NBR").val("");
	$("#HOME_ADDRESS").val("");
	
	if($("#USER_PASSWD")) {
		$("#USER_PASSWD").val("");
	}
	$("#REMARK").val("");
	$("#BIRTHDAY").val("");
	if($("#DEVELOP_NO")) {
		$("#DEVELOP_NO").val("");	//刮刮卡清空
		$("#DEVELOP_NO").attr("disabled", false);	//刮刮卡disabled置为false
	}
	
	$("#PAY_NAME").val("");
	$("#PAY_MODE_CODE").val("");
	$("#BANK_CODE").val("");
	$("#BANK_ACCT_NO").val("");
}

/*SIM卡号校验*/
function checkSimCardNo() {
    
    $("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
	$("#PSPT_INPUT").removeClass("e_elements-success");
	$("#PSPT_INPUT").removeClass("e_elements-error");
	var checkResultCode = $("#CHECK_RESULT_CODE").val();	//校验通过标识
	
	$.feeMgr.clearFeeList("9701");
	$.cssubmit.disabledSubmitBtn(true);
	initProduct();
	var simCardNo = $("#SIM_CARD_NO").val();
	if(simCardNo=="" || simCardNo.length<20) {
		alert("SIM卡号输入不正确");
		$("#SIM_CARD_NO").focus();
		return false;
	}
	
	var oldSimCardNo = "";
	if(!isNull($("#OLD_SIM_CARD_NO").val())) {
		oldSimCardNo=$("#OLD_SIM_CARD_NO").val();
	}

	//先检查服务号码是否校验通过	
	if($("#SERIAL_NUMBER").val()=="") {
		alert("请先校验新开户号码！");
		return false;
	}

	if(checkResultCode=="-1") {
		alert("新开户号码未校验通过！");
		return false;
	}
		
	var serial_number = $("#SERIAL_NUMBER").val();
	var param = "&CHECK_RESULT_CODE=" + checkResultCode + "&OLD_SIM_CARD_NO=" + oldSimCardNo + "&SIM_CARD_NO=" + simCardNo + "&SERIAL_NUMBER=" + serial_number
	+ "&OPEN_TYPE=" + $("#OPEN_TYPE").val()+ "&PROV_OPEN_ADVANCE_PAY_FLAG=" + $("#PROV_OPEN_ADVANCE_PAY_FLAG").val()+ "&PROV_OPEN_OPERFEE_FLAG=" + $("#PROV_OPEN_OPERFEE_FLAG").val()+ "&CHECK_DEPART_ID=" + $("#CHECK_DEPART_ID").val();
	$.beginPageLoading("SIM卡号校验中......");
	$.ajax.submit(null, 'checkSimCardNo', param, 'CheckSimCardNoHidePart', function(data){
		var data0 = data.get(0);
		$("#OLD_SIM_CARD_NO").val(simCardNo);
		$("#CHECK_RESULT_CODE").val(data0.get("CHECK_RESULT_CODE"));
		$.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
		if(data0.get("FEE")){
				var feeData = $.DataMap();
				feeData.put("MODE", data0.get("FEE_MODE"));
				feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
				feeData.put("FEE",  data0.get("FEE"));
				feeData.put("PAY",  data0.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","9701");				
				$.feeMgr.insertFee(feeData);
				PAGE_FEE_LIST.put("SIMCARD_FEE", $.feeMgr.cloneData(feeData));
				

	    }
	    //省内异地开户费用处理
	    if($("#OPEN_TYPE").val()=="PROV_REMOTE_OPEN"){
		    var advancePayFlag=$("#PROV_OPEN_ADVANCE_PAY_FLAG").val();
		    var advancePay=$("#PROV_OPEN_ADVANCE_PAY").val();
		    var operFeeFlag=$("#PROV_OPEN_OPERFEE_FLAG").val();
		    var operFee=$("#PROV_OPEN_OPERFEE").val();
		    if(advancePayFlag=="true"){
		        $.feeMgr.clearFeeList("9701","2");
		      	var feeData = $.DataMap();
				feeData.put("MODE", "2");
				feeData.put("CODE", "0");
				feeData.put("FEE",  data0.get("FEE"));
				feeData.put("PAY",  data0.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","9701");				
				$.feeMgr.insertFee(feeData);
		    }
		     if(operFeeFlag=="true"){
		        $.feeMgr.clearFeeList("9701","0");
		      	var feeData = $.DataMap();
				feeData.put("MODE", "0");
				feeData.put("CODE", "9701");
				feeData.put("FEE",  operFee);
				feeData.put("PAY",  operFee);		
				feeData.put("TRADE_TYPE_CODE","9701");				
				$.feeMgr.insertFee(feeData);
		    }
		    
		 }
		$("#SIM_CARD_INPUT").addClass("e_elements-success");
		//产品搜索引擎参数设置
		$.Search.get("productSearch").setParams("&EPARCHY_CODE="+$("#EPARCHY_CODE").val()+"&SEARCH_TYPE=1");
		//产品结账日设置
		var acct_day = $("#ACCT_DAY");
		selectedElements.setAcctDayInfo(acct_day.val(),"","","");
		//pos机刷卡参数加载
		$.feeMgr.setPosParam("9701", serial_number, $("#EPARCHY_CODE").val());
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#SIM_CARD_INPUT").addClass("e_elements-error");
		showDetailErrorInfo(error_code,error_info,derror);
    });
    

}

/*************************************校验开户号码及SIM资源 结束************************************/

//默认客户姓名和付费账户名字相同
function setPayName() {
	var custName = $("#CUST_NAME").val();	
	$("#PAY_NAME").val(custName);
}
/*根据证件类型设置datatype*/
function checkPsptTypeCode() {

	//如果是身份证类型，证件号码不为空，则进行格式校验
	//湖南 0-本地身份证 1-外地身份证 2-身份证
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	
	if(psptTypeCode==0 || psptTypeCode==1)
	{
		var departKindCode = $("#DEPART_KIND_CODE").val(); 
		var staffv = $("#STAFF_ID").val().substring(0,4); 
		if(departKindCode != "" && departKindCode != "100" && departKindCode != "500" && staffv != "HNSJ" && staffv != "HNHN" && staffv != "SUPERUSR")
		{ 
			$("#PSPT_ID").val("");
			$("#PSPT_ID").attr("disabled",true);
			$("#CUST_NAME").attr("disabled",true);
			$("#PSPT_ADDR").attr("disabled",true);
			$("#PSPT_END_DATE").attr("disabled",true);
		}
	}
	else
	{
		$("#PSPT_ID").attr("disabled",false);
		$("#CUST_NAME").attr("disabled",false);
		$("#PSPT_ADDR").attr("disabled",false);
		$("#PSPT_END_DATE").attr("disabled",false);
	}
	
	if(psptTypeCode==0 || psptTypeCode==1 || psptTypeCode==2) {
		$("#PSPT_ID").attr("datatype", "pspt");
		if($("#PSPT_ID").val()!="") {
			if(!$.validate.verifyField($("#PSPT_ID")[0])) {
				return false;
			}
		}
	}
	else {
		$("#PSPT_ID").attr("datatype", "");
	}
	
	showCon(psptTypeCode);
	if(psptTypeCode=='E' || psptTypeCode=='K' || psptTypeCode=='R' || psptTypeCode=='S' || psptTypeCode=='T')
	{
	    $("#CHECK_ASSURE_CODE").val("1");
	    $("#li_ASSURE_TYPE_CODE").css("display","none"); 
	    $("#li_ASSURE_DATE").css("display","none"); 
	    $("#span_ASSURE_PSPT_TYPE_CODE").addClass("e_required");
	    $("#span_ASSURE_PSPT_ID").addClass("e_required");
	    $("#span_ASSURE_NAME").addClass("e_required");
	    $("#span_ASSURE_PHONE").addClass("e_required");
	    $("#span_ASSURE_PSPT_TYPE_CODE").html('经办人证件类型');
	    $("#span_ASSURE_PSPT_ID").html('经办人证件号码');
	    $("#span_ASSURE_PHONE").html('经办人联系方式');
	    $("#span_ASSURE_NAME").html('经办人名称');
	    $("#ASSURE_PSPT_TYPE_CODE").attr("desc", "经办人证件类型");
	    $("#ASSURE_PSPT_ID").attr("desc", "经办人证件号码"); 
	    $("#ASSURE_PHONE").attr("desc", "经办人联系方式");
	    $("#ASSURE_NAME").attr("desc", "经办人名称");
	    $("#ASSURE_PSPT_TYPE_CODE").attr("nullable", "no");
	    $("#ASSURE_PSPT_ID").attr("nullable", "no"); 
	    $("#ASSURE_NAME").attr("nullable", "no");
	    $("#ASSURE_PHONE").attr("nullable", "no");   
	}
	else
	{
	    $("#CHECK_ASSURE_CODE").val("");
	    $("#li_ASSURE_TYPE_CODE").css("display",""); 
	    $("#li_ASSURE_DATE").css("display",""); 
	    $("#span_ASSURE_PSPT_TYPE_CODE").removeClass("e_required");
	    $("#span_ASSURE_PSPT_ID").removeClass("e_required");
	    $("#span_ASSURE_NAME").removeClass("e_required");
	    $("#span_ASSURE_PHONE").removeClass("e_required");
	    $("#span_ASSURE_PSPT_TYPE_CODE").html('担保人证件类型');
	    $("#span_ASSURE_PSPT_ID").html('担保人证件号码');
	    $("#span_ASSURE_PHONE").html('担保人联系方式');
	    $("#span_ASSURE_NAME").html('担保人名称');
	    $("#ASSURE_PSPT_TYPE_CODE").attr("desc", "担保人证件类型");
	    $("#ASSURE_PSPT_ID").attr("desc", "担保人证件号码"); 
	    $("#ASSURE_PHONE").attr("desc", "担保人联系方式");
	    $("#ASSURE_NAME").attr("desc", "担保人名称");
	    $("#ASSURE_PSPT_TYPE_CODE").attr("nullable", "yes");
	    $("#ASSURE_PSPT_ID").attr("nullable", "yes"); 
	    $("#ASSURE_NAME").attr("nullable", "yes");
	    $("#ASSURE_PHONE").attr("nullable", "yes"); 
	}
    $("#ASSURE_PSPT_TYPE_CODE").val("");
    $("#ASSURE_PSPT_ID").val(""); 
    $("#ASSURE_PHONE").val("");
    $("#ASSURE_NAME").val("");

}


function showCon(psptTypeCode)
{
    if($("#OtherInfoPart").css("display")=="none")
    {
		if(psptTypeCode=='E' || psptTypeCode=='K' || psptTypeCode=='R' || psptTypeCode=='S' || psptTypeCode=='T')
		{
		 $("#span_con").html('填写经办人信息');
		}
		else
		{
		 $("#span_con").html('填写担保信息');
		}
    }
    else
    {
		if(psptTypeCode=='E' || psptTypeCode=='K' || psptTypeCode=='R' || psptTypeCode=='S' || psptTypeCode=='T')
		{
		 $("#span_con").html('隐藏经办人信息');
		}
		else
		{
		  $("#span_con").html('隐藏担保信息');
		}
    }
}




/*校验身份证
 *并设置出生日期
 *检查证件黑名单
 *检查证件下欠费用户
 *证件最大开户用户数校验
 */
function checkPsptId() {

   	$("#PSPT_INPUT").removeClass("e_elements-success");
	$("#PSPT_INPUT").removeClass("e_elements-error");
   
	var psptId = $("#PSPT_ID").val();

	
	// 网上选号检验 
	if( $("#CHOICE_GetCodeMode").val() == "2" && $("#CHOICE_NetUsrpid").val() != $("#PSPT_ID").val() )
	{
		alert( "该号码已做了网上选号,界面录入的证件号与网上选号录入的证件号不符!" );
		return false;
	}
	
	//$("#CHECK_PSPT_CODE").val("");		//设置证件号码校验不通过
	

	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	
	//REQ201602160017 关于更改铁通业务开户界面和变更界面营业执照位数的需求 营业执照只能15位或18位
	if(psptTypeCode=="E"){
		if(psptId.length != 15 && psptId.length != 18){
			alert('营业执照类型校验：证件号码长度需满足15位或者18位！当前：'+psptId.length+'位。');
			$('#PSPT_ID').val('');
			return false;
		}
	}
	
	//只针对身份证才做相关检查
	if(psptTypeCode==0 || psptTypeCode==1 || psptTypeCode==2) {
		$("#PSPT_ID").attr("datatype", "pspt");
		if(!$.validate.verifyField($("#PSPT_ID")[0])) {
				return false;
		}

		//做业务校验 结束
		setBirthday(psptId);
	}

	var param = "&ROUTE_EPARCHY_CODE=0898&PSPT_TYPE_CODE=" + psptTypeCode + "&PSPT_ID=" + psptId + "&CHR_BLACKCHECKMODE=" + $("#CHR_BLACKCHECKMODE").val()
	 + "&CHR_CHECKOWEFEEBYPSPT=" + $("#CHR_CHECKOWEFEEBYPSPT").val()+ "&CHR_CHECKOWEFEEBYPSPT_ALLUSER=" + $("#CHR_CHECKOWEFEEBYPSPT_ALLUSER").val();
	$.beginPageLoading("证件号码校验中......");
	$.ajax.submit(null, 'checkPsptId', param, null, function(data) {
		afterCheckPsptId(data);
		$("#PSPT_INPUT").addClass("e_elements-success");
	    $.endPageLoading();
	},
	
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#PSPT_INPUT").addClass("e_elements-error");
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}

/*根据身份证号设置出生日期*/
function setBirthday(psptValue){
	$("#BIRTHDAY").val(getBirthdatByIdNo(psptValue));
}

/*校验证件后续处理：如：哪些元素不能修改，提示业务是否继续等*/
function afterCheckPsptId(data) {
	var data0 = data.get(0);
	
	var isBlackUser = data0.get("IS_BLACK_USER");
	var blackConfirmMsg= data0.get("BLACK_USER_MSG");
	var isExistsOweFeeFlag = data0.get("IS_EXISTS_OWE_FEE_FLAG");
	var oweConfirmMsg = data0.get("OWE_CONFIRM_HINT_MSG");
	//黑名单提示
	if((""+isBlackUser)=="true") {
		if(!confirm(blackConfirmMsg + "是否继续？\n点击【取消】重新填写证件号码，点击【确定】继续办理！")) {
			$("#PSPT_ID").val("");
			$("#PSPT_ID").focus();
			
			$.endPageLoading();
			return false;
			}
	}
	
	//欠费提示
	if((""+isExistsOweFeeFlag)=="true"){
		if(!confirm(oweConfirmMsg + "是否继续？\n点击【取消】重新填写证件号码，点击【确定】继续办理！")) {
			$("#PSPT_ID").val("");
			$("#PSPT_ID").focus();
			
			$.endPageLoading();
			return false;
		}
	}
	
	var serial_number = $("#SERIAL_NUMBER").val();
	//证件最大用户开户数限制
	var param = "&PSPT_TYPE_CODE=" + $("#PSPT_TYPE_CODE").val() + "&PSPT_ID=" + $("#PSPT_ID").val() + "&SERIAL_NUMBER=" + serial_number
		+ "&OPEN_LIMIT_COUNT=" + $("#OPEN_LIMIT_COUNT").val();
	$.beginPageLoading("证件最大用户开户数限制校验中......");
	$.ajax.submit(null, 'judgeOpenLimit', param, '', function(data2) {
		afterJudgeOpenLimit(data2);
		$.endPageLoading();
	},
    function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

/*校验证件最大用户开户数限制后，合客户*/
function afterJudgeOpenLimit(data) {
	var data0 = data.get(0);
	var openNum = data0.get("OPEN_NUM");
	var alertNum = data0.get("ALERT_NUM");
	$("#HINT").css("display", "");
    $("#HINT").text(data0.get("OPEN_LIMIT_MESSAGE"));
	if(parseInt(alertNum)>0) {
		if((parseInt(openNum))>=parseInt(alertNum)) {
			var serial_number = $("#SERIAL_NUMBER").val();
			var param ="&SERIAL_NUMBER=" + serial_number + "&PSPT_TYPE_CODE=" + $("#PSPT_TYPE_CODE").val() + "&PSPT_ID=" + $("#PSPT_ID").val() + "&OPEN_NUM=" + openNum;
			popupPage('createusertrade.BaseInfoPopup', 'getUserInfoByPspt', param, '入网记录', '500', '400');
		}
	}	
	$("#CHECK_PSPT_CODE").val("1");		//证件号码校验通过标志
}


/*帐户类型变化时，样式处理*/
function checkPayModeCode() {
   var checkResultCode = $("#CHECK_RESULT_CODE").val();	//校验通过标识
   if(checkResultCode=="" || checkResultCode=="-1") {
		alert("新开户号码未校验通过！");
		$("#PAY_MODE_CODE").val("0");
		return false;
   }else if(checkResultCode=="0"){
        alert("SIM卡未校验通过！");
        $("#PAY_MODE_CODE").val("0");
		return false;
   }
	//如果帐户类型为现金
	if($("#PAY_MODE_CODE").val()=="0") {
		$("#li_SUPER_BANK_CODE").attr("class","li e_hideX")
		$("#BankCodePart").attr("class","li e_hideX")
		$("#li_BANK_ACCT_NO").attr("class","li e_hideX")
		
		$("#SUPER_BANK_CODE").val("");
		$("#BANK_CODE").val("");
		$("#BANK_ACCT_NO").val("");
		
		$("#SUPER_BANK_CODE").attr("disabled", true);
		$("#BANK_CODE").attr("disabled", true);
		$("#BANK_ACCT_NO").attr("disabled", true);

		$("#span_SUPER_BANK_CODE").removeClass("e_required");
		$("#span_BANK_CODE").removeClass("e_required");
		$("#span_BANK_ACCT_NO").removeClass("e_required");

		$("#SUPER_BANK_CODE").attr("nullable", "yes");
		$("#BANK_CODE").attr("nullable", "yes");
		$("#BANK_ACCT_NO").attr("nullable", "yes");
		$("#ACCT_DAY").attr("disabled", false);
	}
	else {
		
		$("#li_SUPER_BANK_CODE").attr("class","li")
		$("#BankCodePart").attr("class","li")
		$("#li_BANK_ACCT_NO").attr("class","li")
		
		$("#li_SUPER_BANK_CODE").css("display","")
		$("#BankCodePart").css("display","")
		$("#li_BANK_ACCT_NO").css("display","")
		
		$("#SUPER_BANK_CODE").attr("disabled", false);
		$("#BANK_CODE").attr("disabled", false);
		$("#BANK_ACCT_NO").attr("disabled", false);

		$("#span_SUPER_BANK_CODE").addClass("e_required");
		$("#span_BANK_CODE").addClass("e_required");
		$("#span_BANK_ACCT_NO").addClass("e_required");

		$("#SUPER_BANK_CODE").attr("nullable", "no");
		$("#BANK_CODE").attr("nullable", "no");
		$("#BANK_ACCT_NO").attr("nullable", "no");
		$("#PAPER_TYPE").attr("disabled", true);
		$("#ACCT_DAY").val("1");
		$("#ACCT_DAY").attr("disabled", true);
	}
}

/*上级银行变化时，刷新下级银行*/
function checkSuperBankCode() {
	var serial_number = $("#SERIAL_NUMBER").val();
	var param = "&SUPER_BANK_CODE=" + $("#SUPER_BANK_CODE").val() + "&SERIAL_NUMBER=" + serial_number;
	if($("#OPEN_TYPE").val()=="PROV_REMOTE_OPEN" ||$("#OPEN_TYPE").val()=="NETSEL_OPEN"){
	param +="&CITY_CODE="+ $("#CITY_CODE").val();
	}
	$.beginPageLoading("银行列表获取中......");
	$.ajax.submit(null, 'getBankBySuperBank', param, 'BankCodePart', function() {
		//刷新下级银行区域时，会将样式刷没了，这里认为，可以选择上级银行，则表示下级银行是必填的
		$("#span_BANK_CODE").addClass("e_required");
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function changeAcctDay(){   	
	  var acct_day = $("#ACCT_DAY");
	  selectedElements.setAcctDayInfo(acct_day.val(),"","","");
	  $.feeMgr.clearFeeList("9701");
	  $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
	  $.feeMgr.insertFee(PAGE_FEE_LIST.get("SIMCARD_FEE"));
	  initProduct();
	  $.cssubmit.disabledSubmitBtn(true);
}
/*输入新密码*/
function checkAddNewPasswd() {
	//显示新密码输入弹出框
	showLayer("pwdModify");
	
	//设置新密码输入焦点，可以直接输入，从而减少操作
	$("#NEW_PASSWD").focus();
}

/*输入新密码时，确定*/
function confirmNewPasswd() {
    if($("#PSPT_TYPE_CODE").val()==""){
      alert("证件类型不能为空！");
      return false;
    }
     if($("#PSPT_ID").val()==""){
      alert("证件号码不能为空！");
      return false;
    }
    
     if($("#BIRTHDAY").val()==""){
      alert("出生日期不能为空！");
      return false;
    }
	if(!$.validate.verifyField($("#NEW_PASSWD")[0])) {
		return false;
	}
	
	if($("#NEW_PASSWD").val()=="") {
		alert("新密码不能为空！");
		$("#NEW_PASSWD").focus();
		
		return false;
	}
	
	if($("#NEW_PASSWD_AGAIN").val()=="") {
		alert("重复密码不能为空！");
		$("#NEW_PASSWD_AGAIN").focus();
		
		return false;
	}	
	
	//简单密码校验，密码不能为123456或654321
	if($("#NEW_PASSWD").val()=="123456" || $("#NEW_PASSWD").val()=="654321") {
		alert("密码过于简单，请重新输入！");
		$("#NEW_PASSWD").val("");
		$("#NEW_PASSWD_AGAIN").val("");
		$("#NEW_PASSWD").focus();
		
		return false;
	}
	
	//简单密码校验，密码不能都为同一数字，比如不能为222222
	var flag = true;
	var newPasswd = $("#NEW_PASSWD").val();
	var passwd=newPasswd.charAt(0);
	for(var i=0; i<newPasswd.length; i++) {
		if(passwd!=newPasswd.charAt(i)) {
				flag=false;
			}
	}
	if (flag) {
		alert('密码过于简单，请重新输入！');
		$("#NEW_PASSWD").val("");
		$("#NEW_PASSWD_AGAIN").val("");
		$("#NEW_PASSWD").focus();
		
		return false;
	}	
	
	//简单密码校验，密码不能为手机号码的后六位
	var serialNumber = $("#SERIAL_NUMBER").val();
	if(serialNumber=="") {
		alert("请先输入新开户号码，再输入服务密码！");
		$("#NEW_PASSWD").val("");
		$("#NEW_PASSWD_AGAIN").val("");
		hideLayer("pwdModify");
		
		$("#SERIAL_NUMBER").focus();
		
		return false;
	}
	if($("#NEW_PASSWD").val() == serialNumber.substring(5,11)) {
		alert("密码不能为手机号码的后六位，请重新输入！");
		$("#NEW_PASSWD").val("");
		$("#NEW_PASSWD_AGAIN").val("");
		$("#NEW_PASSWD").focus();
		
		return false;
	}
	
	//比较两次输入的密码
	if($("#NEW_PASSWD").val() != $("#NEW_PASSWD_AGAIN").val()) {
		alert("两次输入的密码不一致，请重新输入！");
		$("#NEW_PASSWD").val("");
		$("#NEW_PASSWD_AGAIN").val("");
		$("#NEW_PASSWD").focus();
		
		return false;
	}
	 
	 $.ajax.setup({async:false});
	 var checkSimplePasswordFlag = "true";
	 var param = "&SERIAL_NUMBER="+serialNumber +"&USER_PASSWD="+$("#NEW_PASSWD").val() +"&PSPT_TYPE_CODE="+$("#PSPT_TYPE_CODE").val()
	  + "&PSPT_ID="+$("#PSPT_ID").val()+ "&BIRTHDAY="+$("#BIRTHDAY").val();
	 $.beginPageLoading("简单密码校验中.......");
	 $.ajax.submit(null, 'checkSimplePassword', param, '', function(data) {
	 	var data0 = data.get(0);
	 	if(data0.get("CHECK_PASS")=="FALSE") {
	 		alert("您的密码过于简单，请重新输入！");
	 		$("#NEW_PASSWD").val("");
			$("#NEW_PASSWD_AGAIN").val("");
			$("#NEW_PASSWD").focus();	 		
	 		checkSimplePasswordFlag = "false";
	 	}
	 		$.endPageLoading();
	 },
	 function(error_code,error_info,derror){
	    checkSimplePasswordFlag = "false";
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
     });
	$.ajax.setup({async:true});

	 $.endPageLoading();
	 
	 if(checkSimplePasswordFlag != "true") {
	 	return false;
	 } 
	 //设置密码
	 $("#USER_PASSWD").val($("#NEW_PASSWD").val());
	 
	//清空新密码输入
	$("#NEW_PASSWD").val("");
	$("#NEW_PASSWD_AGAIN").val("");
	
	//隐藏新密码输入弹出框
	hideLayer("pwdModify");
}

/*输入新密码时，取消*/
function cancelNewPasswd() {
	$("#NEW_PASSWD").val("");
	$("#NEW_PASSWD_AGAIN").val("");
	
	hideLayer("pwdModify");
}

/*输入新密码时，清空*/
function resetNewPasswd() {
	$("#NEW_PASSWD").val("");
	$("#NEW_PASSWD_AGAIN").val("");
	$("#NEW_PASSWD").focus();
}

/*备注信息特殊字符校验*/
function checkRemark() {
	var remark = $("#REMARK").val();
	if(remark=="") {
		return true;
	}
	
	var textvalue = remark;
	var a = new Array('\\','\"','\/','{','}' );
	for(var i = 0; i < a.length; i++) {
		if(textvalue.indexOf(a[i]) >= 0) {	
			alert('请不要输入特殊字符！[' + a[i] + ']');
			$("#REMARK").focus();
			return false;
		}
	}
	
	return true;	
}

/*隐藏或显示基本信息区*/
function checkBaseInfo(btn,o) {
	var button = btn;
	var div = document.getElementById(o);
	if (div.className != "")
	{
		div.className = ""
		button.children[0].className = "e_ico-hide"
		button.children[1].innerHTML = "隐藏非必填项"
	}
	else {
		div.className = "e_hide-x"
		button.children[0].className = "e_ico-show"
		button.children[1].innerHTML = "显示非必填项"
	}
	
}

/*隐藏或显示其它信息区*/
function checkOtherInfo(btn,o) {
	var button = btn;
	var div = document.getElementById(o);
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	if (div.style.display != "none")
	{
		div.style.display = "none"
		button.children[0].className = "e_ico-unfold"
		if(psptTypeCode=='E' || psptTypeCode=='K' || psptTypeCode=='R' || psptTypeCode=='S' || psptTypeCode=='T')
		button.children[1].innerHTML = "填写经办人信息"
		else
		button.children[1].innerHTML = "填写担保信息"
	}
	else {
		div.style.display = ""
		button.children[0].className = "e_ico-fold"
		if(psptTypeCode=='E' || psptTypeCode=='K' || psptTypeCode=='R' || psptTypeCode=='S' || psptTypeCode=='T')
		button.children[1].innerHTML = "隐藏经办人信息"
		else
		button.children[1].innerHTML = "隐藏担保信息"
	}
}

/*************************************公用方法 开始************************************/

/*根据身份证号得到出生日期的函数*/
function getBirthdatByIdNo(iIdNo) {
  var tmpStr="";
  var idDate="";
  var tmpInt=0;
  var strReturn = "";

  if((iIdNo.length!=15) &&(iIdNo.length!=18)) {
    strReturn = "输入的身份证号位数错误！";
    alert(strReturn);
	return "";
  }

  if(iIdNo.length==15) {
    tmpStr=iIdNo.substring(6,12);
    tmpStr= "19" + tmpStr;
    tmpStr= tmpStr.substring(0,4) + "-" + tmpStr.substring(4,6) + "-" + tmpStr.substring(6);

    return tmpStr;
  }
  else {
      tmpStr=iIdNo.substring(6,14);
      tmpStr= tmpStr.substring(0,4) + "-" + tmpStr.substring(4,6) + "-" + tmpStr.substring(6);
      
	  return tmpStr;
	}
}
  
/*去掉空格*/
function trim(str) {
	return str.replace(/^\s+|\s+$/,'');
}

/*检查非空*/
function isNull(str) {
	if(str==undefined || str==null || str=="") {
		return true;
	}
	return false;
}
/*显示一块区域*/
function showLayer(optionID) {
	$("#" + optionID).css("display","");
}

/*隐藏一块区域*/
function hideLayer(optionID) {
	$("#" + optionID).css("display","none");
}

/*************************************公用方法 结束************************************/

function onSubmit(){
   if(!verifyAll('BaseInfoPart'))
   {
	   return false;
   }
   if(!verifyAll('InstallInfoPart'))
   {
	   return false;
   } 

   var data = selectedElements.getSubmitData();
   var param = "&SELECTED_ELEMENTS="+data.toString()+"&PRODUCT_ID="+$("#PRODUCT_ID").val()+"&B_REOPEN_TAG="+$("#B_REOPEN_TAG").val()+
   "&TIETBUSY_TAG="+$("#TIETBUSY_TAG").val()+"&RETURNTAG="+$("#RETURNTAG").val();
   $.cssubmit.addParam(param);
    
   if(!$.validate.verifyField($("#SERIAL_NUMBER")[0]))return false;

	//CHECK_RESULT_CODE:服务号码与SIM校验结果:0:服务校验通过，1:SIM卡校验通过，初始值为-1
	/*var checkPsptCode  =    $("#CHECK_PSPT_CODE").val();
	if(checkPsptCode != "1"){
	   alert("证件号码校验未通过！");
	   $("#PSPT_ID").focus();
	   return false;
   }*/
	
    genDeviceTradeStr();   //生成设备信息串
    return true;
}

/*初始化产品*/
function initProduct(){
	offerList.renderComponent("",$("#EPARCHY_CODE").val());
//	pkgElementList.initElementList(null);
	selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
	$("#PRODUCT_NAME").val('');
}

/*设置用户产品品牌*/
function setBrandCode(){
	if($("#PRODUCT_TYPE_CODE").val()!=""){
	    $("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		initProduct();
	}else{
		$("#BRAND").val('');
	}
	
}
function checkBeforeProduct(){
	//CHECK_RESULT_CODE:服务号码与SIM校验结果:0:服务校验通过，1:SIM卡校验通过，初始值为-1
	var checkResultCode = $("#CHECK_RESULT_CODE").val();
	var checkPsptCode  =    $("#CHECK_PSPT_CODE").val();
	
	//固话业务去掉号码和SIM校验
	/*if(checkResultCode=="-1"){
		alert("新开户号码校验未通过！");
		$("#SERIAL_NUMBER").focus();
		return false;
	}
	if(checkResultCode=="0"){
		alert("SIM卡号校验未通过！");
		$("#SIM_CARD_NO").focus();
		return false;
	}*/
	/*if(checkPsptCode != "1"){
	   alert("证件号码校验未通过！");
	   	$("#PSPT_ID").focus();
	   return false;
	}*/
	
	if(!verifyAll('BaseInfoPart') || !verifyAll("InstallInfoPart"))
	   return false;
   
	ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
}
/*设置产品目录是否可用与产品类型编码*/
function setProductType(obj){
	if(obj.value!=''){
		if(getElement("B_PRODUCT_MODE").value=="true"){
			disabled(getElement('productselectbutton'),true);
		}else{
			disabled(getElement('productselectbutton'),false);
		}
	}else{
		disabled(getElement('productselectbutton'),true);
	}
	getElement('productselectbutton').productTypeCode=obj.value;
}

function afterChangeProduct(productId,productName,brandCode,brandName){
          $.feeMgr.clearFeeList("9701");
		  $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
          $.feeMgr.insertFee(PAGE_FEE_LIST.get("SIMCARD_FEE"));
          $.feeMgr.insertFee(PAGE_SCREET_FEE_LIST.get("SECRET_FEE"));
          $.feeMgr.insertFee(PAGE_DEVICEZJ_FEE_LIST.get("DEVICE_FEE"));
          $.feeMgr.insertFee(PAGE_DEVICEJFQ_FEE_LIST.get("DEVICE_FEE"));
          var feeData = $.DataMap();
          	feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "0");
			feeData.put("FEE",  "0");
			feeData.put("PAY",  "0");		
			feeData.put("TRADE_TYPE_CODE","9701");
			
			$.feeMgr.insertFee(feeData);	
			
			feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "2");
			feeData.put("FEE",  "0");
			feeData.put("PAY",  "0");		
			feeData.put("TRADE_TYPE_CODE","9701");
			$.feeMgr.insertFee(feeData);	

           $("#PRODUCT_ID").val(productId);
           $("#PRODUCT_NAME").val(productName);
            var param = "&NEW_PRODUCT_ID="+productId;
            offerList.renderComponent($("#PRODUCT_ID").val(),$("#EPARCHY_CODE").val());
//            pkgElementList.initElementList(null);
			selectedElements.renderComponent(param,$("#EPARCHY_CODE").val());
			
			var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode + "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
			$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
			    $.cssubmit.disabledSubmitBtn(false);
			  	for(var i = 0; i < data.getCount(); i++) {
			  	     var data0 = data.get(i);
				     if(data0){
								feeData.clear();
								feeData.put("MODE", data0.get("FEE_MODE"));
								feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
								feeData.put("FEE",  data0.get("FEE"));
								feeData.put("PAY",  data0.get("FEE"));		
								feeData.put("TRADE_TYPE_CODE","9701");							
								$.feeMgr.insertFee(feeData);			
						}
			  	}
		
	           },
	           function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
				});
}


function disableElements(data){
 if($("#B_REOPEN_TAG").val()=='1'){
         selectedElements.disableAll();
  }else{
   if(data){
     var temp = data.get(0);
     if(data.get(0).get("NEW_PRODUCT_START_DATE")){
			$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
	}
   }
  }
}
/*************************************远程写卡相关函数 开始************************************/
/*读写卡开关*/
function readOrWriteWithOpen() {
   var checkResultCode = $("#CHECK_RESULT_CODE").val();	//校验通过标识
   if(checkResultCode=="-1") {
		alert("新开户号码未校验通过！");
		return false;
	}
	
	var is3GUser = '0';	//是否3G用户：0-否 1-是
	var chkRead = document.getElementById('chkRead').checked;

	if(chkRead) {
		invokeCard(1, 'afterinvokeCard', is3GUser, 10, $("#SERIAL_NUMBER").val());
	} else {
		invokeCard(0, 'afterinvokeCard', is3GUser, 10, $("#SERIAL_NUMBER").val());
	}
}
/*回调函数：SIM再利用、写卡公用回调函数*/
function afterinvokeCard(obj){
	$("#SIM_CARD_NO").val(obj.get('SIM_CARD_NO'));
	$("#CHECK_RESULT_CODE").val('1');
	checkSimCardNo();
}
/*************************************远程写卡相关函数 结束************************************/
/*************************************产品搜索引擎函数 开始************************************/

function changeSearchType(eventObj){
			var searchType = eventObj.value;
			var param = "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
			param += "&SEARCH_TYPE="+searchType;
			if(searchType == "2"){
				param += "&PRODUCT_ID="+$("#PRODUCT_ID").val();
			}
			$.Search.get("productSearch").setParams(param);
}
function searchOptionEnter(){
			var searchType = $("#productSearchType").val();
			var searchLi = $("#Ul_Search_productSearch li[class=focus]");
			if(searchType == "1"){
				//产品搜索
				var productId = searchLi.attr("PRODUCT_ID");
				var productName = searchLi.attr("PRODUCT_NAME");
				var brandCode = searchLi.attr("BRAND_CODE");
				var brandName = searchLi.attr("BRAND");
				afterChangeProduct(productId,productName,brandCode,brandName);
			}
			else if(searchType == "2"){
				//元素搜索
				var reOrder = searchLi.attr("REORDER");
				var elementId = searchLi.attr("ELEMENT_ID");
				var elementName = searchLi.attr("ELEMENT_NAME");
				var productId = searchLi.attr("PRODUCT_ID");
				var packageId = searchLi.attr("PACKAGE_ID");
				var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
				var forceTag = searchLi.attr("FORCE_TAG");
				
				if(reOrder!="R"&&selectedElements.checkIsExist(elementId,elementTypeCode)){
					alert("您所选择的元素"+elementName+"已经存在于已选区，不能重复添加");
					return false;
				}
				var elementIds = $.DatasetList();
				var selected = $.DataMap();
				selected.put("PRODUCT_ID",productId);
				selected.put("PACKAGE_ID",packageId);
				selected.put("ELEMENT_ID",elementId);
				selected.put("ELEMENT_TYPE_CODE",elementTypeCode);
				selected.put("MODIFY_TAG","0");
				selected.put("ELEMENT_NAME",elementName);
				selected.put("FORCE_TAG",forceTag);
				selected.put("REORDER",reOrder);
				elementIds.add(selected);
				if(selectedElements.addElements){
					selectedElements.addElements(elementIds);
				}
			}
			$("#Div_Search_productSearch").css("visibility","hidden");
}

/** 
 * 选择标准地址后的动作
 */
function afterSelectStandAddress(){
	//自己造资源测试数据
	$("#STAND_ADDRESS").val($("#REGION_NAME").val());
	$("#STAND_ADDRESS_CODE").val($("#REGION_ID").val());
	$("#DETAIL_ADDRESS").val($("#REGION_NAME").val());
	$("#SIGN_PATH").val($("#FOFFICE_ID").val());
}

/** 
 * 地域改变响应
 */
function onAreaTypeChange(){
	var areaType = $("#AREA_TYPE").val();
	if(areaType=="1" || areaType=="3"){
		$("#CLEAR_ACCOUNT").attr("disabled",false);
	}else{
		$("#CLEAR_ACCOUNT").val("0");
		$("#CLEAR_ACCOUNT").attr("disabled",true);
	}
}

/** 
 * 固话号码选择响应
 */
function selSerialNumberAction(){
	var stdAddrCode = $("#STAND_ADDRESS_CODE").val();
	if(!stdAddrCode || stdAddrCode==null || stdAddrCode==""){
		alert("请先选择地址！");
		return;
	}
	
	var signPath = $("#SIGN_PATH").val();
	if(!signPath || signPath==null || signPath==""){
		alert("局向地址为空，请确认地址！");
		return;
	}

	var staffId = top.$("#staffId").val();
	var deptId = top.$("#deptId").val();
	var loginEpachyId = top.$("#loginEpachyId").val();
	
	//TODO:先注释掉资源的号码选择界面,自己造测试数据
	$.popupPage('brm.officeNumberSelect', 'init', '&refresh=true&cond_FOFFICE_ID='+$("#SIGN_PATH").val()+'&cond_STAFF_IF_IN='+staffId+'&cond_OPER_DEPART_ID='+deptId+'&cond_EPARCHY_CODE='+loginEpachyId, '选择固话号码', '620', '400','SERIAL_NUMBER','',subsys_cfg.pbossintf);
	//afterSelectFixedPhoneNumber();
}

/** 
 * 保密方式变更响应
 */
function onSecretChange(){
	var secretValue = $("#SECRET").val();
	//TODO:此处源码应该有问题,不管选择保密或者公开,都需要交纳5元手续费.此处根据个人理解做出修改
	if(secretValue=="1"){
		$.feeMgr.clearFeeList("9701","SECRET");
		if(confirm("保密需要交纳5元的手续费，是否确定选择保密？")){
			var feeData = $.DataMap();	
			feeData.put("MODE","0");
			feeData.put("CODE","9003");
			feeData.put("FEE","500");
			feeData.put("TRADE_TYPE_CODE","9701");
			$.feeMgr.insertFee(feeData);
			PAGE_SCREET_FEE_LIST.put("SECRET_FEE", $.feeMgr.cloneData(feeData));	
			}else{
			$("#SECRET").val("0");
			return;
		}
	}else{
		$.feeMgr.removeFee("9701","0","9003");//删除已经添加的SECRET费用
		PAGE_SCREET_FEE_LIST.clear();
	}
}

/** 
 * 设备类型变更联动响应
 */
function onSelDeviceKindChange(){
    var kind = $("#DEVICE_KIND").val();
    if(kind == null || kind == "")
        return;
    if(kind!="02"){
    	$.ajax.submit(null, "onSelDeviceKindChange", "&DEVICE_KIND="+kind, "deviceUseTypePart", null, null);
    	$("#ISNEWDEVICEKIND").val("");
    }else{
    	$("#USE_TYPE_CODE").attr("disabled", true).attr("nullable", "yes");
    	$("#ISNEWDEVICEKIND").val("1");
    }
}

/** 
 * 选择设备响应动作
 */
function selDeviceAction(){
	var deviceKind = $("#DEVICE_KIND").val();
	//资源选择相关操作暂时先屏蔽,手工造测试数据
	$.popupPage('brm.materialChoose', 'queryMaterialList', '&RES_TYPE_CODE=W&refresh=true&RES_KIND_CODE='+deviceKind, '选择设备', '620', '400','CHOOSEDEVICE','',subsys_cfg.pbossintf);
/*	$("#POP_cond_SEL_DEVICE").val("测试设备");
	$("#cond_SEL_DEVICE").val("测试设备编码");
	$("#SALEPRICE").val("1000");
	$("#MATERIAL_CODE").val("1001");*/
	
	//afterSelectDevice();
}

/** 
 * 前台客户端增加设备信息,对参数进行检查
 */
function addDevice(){
	var deviceKind = $("#DEVICE_KIND").val();
	var isNewDeviceKind = $("#ISNEWDEVICEKIND").val();
	if(isNewDeviceKind=="1"){
		alert("自备设备，不需要新增，请继续后续操作！");
		return;
	}
	if((!deviceKind || deviceKind == "")&&isNewDeviceKind!="1"){
		alert("请先选择设备类型！");
		return;
	}
	
	if(deviceKind=="01"){
	    var num = $.format.number($("#SELECTED_PHONEDEVICENUMBER").val(),0);
	    if(num>=1){
	        alert("已经选择过了该类型设备！");
	        return;
	    }
	}else if (deviceKind=="04"){
	    var num = $.format.number($("#SELECTED_METERDEVICENUMBER").val(),0);
	    if(num>=1){
	        alert("已经选择过了该类型设备！");
	        return;
	    }
	}
	
	var useType = $("#USE_TYPE_CODE").val();
	if((!useType || useType=="")&&isNewDeviceKind!="1"){
		alert("请先选择使用方式！");
		return;
	}
	
	selDeviceAction();
}

/** 
 * 前台客户端增加设备信息，对表格进行动态修改
 */
function afterSelectDevice(){
	var deviceKind = $("#DEVICE_KIND").val();
	var useType = $("#USE_TYPE_CODE").val();
	var saleTypeDesc = $("#SALETYPE_DESC").val();
	var saleTypeCode = $("#SALETYPE_CODE").val();
	var salePrice    = $("#SALEPRICE").val();
	var resCode = $("#MATERIAL_CODE").val();
	var feeMode = "0";
	var feeTypeCode = "0";
	//alert("DEVICE_KIND="+deviceKind+" USE_TYPE_CODE="+useType+" SALETYPE_DESC"+saleTypeDesc+" SALETYPE_CODE"+saleTypeCode+" SALEPRICE="+salePrice+" MATERIAL_CODE"+resCode);
	if(deviceKind=="01"){  //固话座机
	    if(useType=="0"){  //购买
	        var feeData = $.DataMap();	
			feeData.put("MODE","0");
			feeData.put("CODE","9002");//此处处理依赖于固话设备只有两种,且选择设备时每种设备只能加一个
			feeData.put("FEE",  salePrice);
			feeData.put("TRADE_TYPE_CODE","9701");
			
			$.feeMgr.insertFee(feeData);
			PAGE_DEVICEZJ_FEE_LIST.put("DEVICE_FEE", $.feeMgr.cloneData(feeData));
	    } else if(useType=="1"){  //赠送
	        salePrice=0;
	    }else{
	        alert("使用方式不对，请重新选择设备!");
	        return ;
	    }
	    
	    var num = $.format.number($("#SELECTED_PHONEDEVICENUMBER").val(),0);
	    num = num + 1;
	    $("#SELECTED_PHONEDEVICENUMBER").val(num);
	}else if(deviceKind=="04"){  //固话计费器
	    if(useType=="0"){  //押金
	        var feeData = $.DataMap();	
	        feeData.put("MODE","1");
			feeData.put("CODE","9002");
			feeData.put("FEE",  salePrice);
			feeData.put("TRADE_TYPE_CODE","9701");
			$.feeMgr.insertFee(feeData);
			PAGE_DEVICEJFQ_FEE_LIST.put("DEVICE_FEE", $.feeMgr.cloneData(feeData));
	    }else{
	        alert("使用方式不对，请重新选择设备!");
	        return ;
	    }
	    
	    var num = $.format.number($("#SELECTED_METERDEVICENUMBER").val(),0);
	    num = num + 1;
	    $("#SELECTED_METERDEVICENUMBER").val(num);
	}
	
	
	var rowstr=document.createElement("tr");
	var colstr1=document.createElement("td");  //checkbox
	var colstr2=document.createElement("td");  //设备类型CODE
	var colstr3=document.createElement("td");  //设备类型NAME
	var colstr4=document.createElement("td");  //销售CODE
	var colstr5=document.createElement("td");  //销售NAME
	var colstr6=document.createElement("td");  //销售价格
	var colstr7=document.createElement("td");  //使用方式CODE
	var colstr8=document.createElement("td");  //使用方式NAME
	var colstr9=document.createElement("td");  //设备费用大类
	var colstr10=document.createElement("td"); //设备费用小类
	var colstr11=document.createElement("td"); //设备串号
	
	colstr1.innerHTML="<input type='checkbox' name='chkbox' value='"+saleTypeCode+"'/>";
	
	colstr2.style.display = 'none';
	colstr2.innerText=deviceKind;
	//colstr3.innerText=($("#DEVICE_KIND").options[$("#DEVICE_KIND").selectedIndex]).text;
	
	colstr3.innerText = $("#DEVICE_KIND option:selected").text();
	
	colstr4.style.display = 'none';
	colstr4.innerText=saleTypeCode;
	colstr5.innerText=saleTypeDesc;
	
	colstr6.innerText=salePrice/100;
	
	colstr7.style.display = 'none';
	colstr7.innerText=useType;
	//colstr8.innerText=($("#USE_TYPE_CODE").options[$("USE_TYPE_CODE").selectedIndex]).text;
	colstr8.innerText = $("#USE_TYPE_CODE option:selected").text();
		
	colstr9.style.display = 'none';
	colstr10.style.display = 'none';
	colstr9.innerText=feeMode;
	colstr10.innerText=feeTypeCode;
	
	colstr11.innerText=resCode;

	rowstr.appendChild(colstr1);
	rowstr.appendChild(colstr2);
	rowstr.appendChild(colstr3);
	rowstr.appendChild(colstr4);
	rowstr.appendChild(colstr5);
	rowstr.appendChild(colstr6);
	rowstr.appendChild(colstr7);
	rowstr.appendChild(colstr8);
	rowstr.appendChild(colstr9);
	rowstr.appendChild(colstr10);
	rowstr.appendChild(colstr11);

	var deviceList = $("#deviceListBody");
	if(null == deviceList)
		alert("无法获取deviceListBody元素");
	
	deviceList.append(rowstr);
}

/*
为设备列表拼串，已备登记台帐使用
*/
function genDeviceTradeStr(){
	var boxObj = $("*[name=chkbox]");
	var length = boxObj.length;
	var i;
	var addExtMember = $.DatasetList();
	var j=-1;
	for(i=0;i<length;i++){
	    var trObj          = boxObj[i].parentNode.parentNode;//单元格，行
		var deviceKind     = trObj.childNodes[1].innerText;
		var saleTypeCode = trObj.childNodes[3].innerText;
		var saleTypeDesc = trObj.childNodes[4].innerText;
		var salePrice    = trObj.childNodes[5].innerText;
		var useType    = trObj.childNodes[6].innerText;
		var resCode    = trObj.childNodes[10].innerText;
	    
		var idata = $.DataMap();
		if(resCode==""){
			idata.put("DEVICE_CODE", j);//因为主键原因,从-1往下排吧-_-!
			j--;
		}else{
			idata.put("DEVICE_CODE", resCode);
		}
	    idata.put("DEVICE_TYPE_CODE", "W");
	    idata.put("DEVICE_KIND_CODE", deviceKind);
	    idata.put("SALE_TYPE_CODE", saleTypeCode);
	    idata.put("SALE_TYPE_DESC", saleTypeDesc);
	    idata.put("DEVICE_PRICE", salePrice*100);
	    idata.put("USE_TYPE_CODE", useType);
		
		addExtMember.add(idata);
		
		idata = null;
	
	}
	$("#DEVICE_STRING").val(addExtMember.toString());
	addExtMember = null;
	
	return true;
}

/*
选择固话号码后的处理
*/
function afterSelectFixedPhoneNumber(){
	//$("#SERIAL_NUMBER").val("89800000000"); 
	if($("#USERINFO_SERIAL_NUMBER").val() != null && $("#USERINFO_SERIAL_NUMBER").val() != ""){
		$.ajax.submit(null, "releaseSelectedSNRes", "&RES_NO="+$("#SERIAL_NUMBER").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val(), null, null,null);
	}
	$("#USERINFO_SERIAL_NUMBER").val($("#SERIAL_NUMBER").val());
	$("#SERIAL_NUMBER").val($("#SERIAL_NUMBER").val());
	$("#SN_RES_TYPE_CODE").val($("#RES_TYPE_CODE").val());
	$("#SN_RES_KIND_CODE").val($("#RES_KIND_CODE").val());
	$("#SWITCH_ID").val($("#SWITCH_ID").val());
	$("#SWITCH_TYPE").val($("#SWITCH_TYPE").val());
	
/*	$("#SERIAL_NUMBER").val("89820512345");
	$("#RES_TYPE_CODE").val("1");
	$("#RES_KIND_CODE").val("1");
	$("#SWITCH_ID").val("1");
	$("#SWITCH_TYPE").val("1");*/
}

function psptIdClick(){
	if($.custValidate.validatePsptId("PSPT_TYPE_CODE","PSPT_ID"))
	{
		checkPsptId();
	}
	checkRealNameLimitByPspt();
}

function standAddressClick(){
	//afterSelectStandAddress();
}
function validatePsptAddress()
{
	var strPsptAddr = $("#PSPT_ADDR").val();
	$('#POST_ADDRESS').val(strPsptAddr);
}
/*
前台客户端删除设备信息，对表格进行动态修改（可能还需要释放设备）
*/
function delDevice(){
	var boxObj = document.getElementsByName("chkbox");
	var length = boxObj.length;
	var i = 0;
	var index = 0;
	for(i=0;i<length;i++){
		if(boxObj[index].checked){			
			var trObj = boxObj[index].parentNode.parentNode;//单元格，行
			var deviceKind = trObj.childNodes[1].innerText;
			if(deviceKind == "01"){
			    var num = $.format.number($("#SELECTED_PHONEDEVICENUMBER").val(),0);
	            num=num-1;
	            $("#SELECTED_PHONEDEVICENUMBER").val(num);
			}else if (deviceKind == "04"){
			    var num = $.format.number($("#SELECTED_METERDEVICENUMBER").val(),0);
	            num=num-1;
	            $("#SELECTED_METERDEVICENUMBER").val(num);
			}
			
			var fee = trObj.childNodes[5].innerText;
			var feeMod = trObj.childNodes[8].innerText;
			var feeTypeCode = trObj.childNodes[9].innerText;
			var kindTypeCode = trObj.childNodes[2].innerText;
			if(fee!=0){//删除存储的费用信息
			    if("01" == deviceKind){
			    	$.feeMgr.removeFee("9701","0","9002");
			    }else{
			    	$.feeMgr.removeFee("9701","1","9002");
			    }
	        }
			
			trObj.parentNode.removeChild(trObj);//删除整行
		}else{
			index++;
		}
	}
	
	return true;
}

//扫描读取身份证信息
function clickScanPspt() 
{
	getMsgByEForm("PSPT_ID","CUST_NAME,PAY_NAME","","","","PSPT_ADDR,POST_ADDRESS",null,"PSPT_END_DATE");
    $("#PSPT_ID").focus();
    this.checkRealNameLimitByPspt();
}

//检查同一证件号已开实名制用户的数量是否已超出预定值
function checkRealNameLimitByPspt()
{
    var custName = $("#CUST_NAME").val();
    var psptId = $("#PSPT_ID").val();
    if(custName == "" || psptId == "")
    {
        return false;
    }
    var param = "&CUST_NAME="+encodeURIComponent(custName)+"&PSPT_ID="+psptId+"&EPARCHY_CODE=0898";
    param += "&NET_TYPE_CODE=12";
    param += "&SVC_NAME="+"SS.CreatePersonUserSVC.checkRealNameLimitByPsptCtt";
    
    var clazz = "com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.BaseInfoFieldHandler";
    $.beginPageLoading("实名制用户开户数量校验。。。");
	$.httphandler.get(clazz, "checkRealNameLimitByPspt", param, 
		function(data)
		{
			$.endPageLoading();
			if(data && data.get("CODE")!= "0")
			{
				$("#PSPT_INPUT").removeClass("e_elements-success");
				$("#PSPT_INPUT").removeClass("e_elements-error");
				$("#PSPT_INPUT").addClass("e_elements-error");
				$("#PSPT_ID").val("");
				//$("#REALNAME_LIMIT_CHECK_RESULT").val("false");
				alert(data.get("MSG"));
				return;
			}
			else
			{
				$("#PSPT_INPUT").removeClass("e_elements-success");
				$("#PSPT_INPUT").removeClass("e_elements-error");
				$("#PSPT_INPUT").addClass("e_elements-success");
				//$("#REALNAME_LIMIT_CHECK_RESULT").val("true");
			}
		},function(code, info, detail)
		{
			$.endPageLoading();
			$("#PSPT_INPUT").removeClass("e_elements-success");
			$("#PSPT_INPUT").removeClass("e_elements-error");
			$("#PSPT_INPUT").addClass("e_elements-error");
			$("#PSPT_ID").val("");
			MessageBox.error("错误提示","实名制用户开户数量校验获取后台数据错误！",null, null, info, detail);
		},function()
		{
			$.endPageLoading();
			MessageBox.alert("告警提示", "实名制用户开户数量校验超时");
		});	
}

function chkCustName(fieldName)
{
	var obj=$("#"+fieldName);
	var psptTypeCode,psptTypeDesc;
	//判断到底是客户姓名还是经办人姓名
	if(fieldName=="CUST_NAME"){
		psptTypeCode = $("#PSPT_TYPE_CODE").val();
	    psptTypeDesc = $("#PSPT_TYPE_CODE").attr("desc");
	}else if(fieldName=="AGENT_CUST_NAME"){
		psptTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
	    psptTypeDesc = $("#AGENT_PSPT_TYPE_CODE").attr("desc");
	}else if(fieldName=="USE"){
		psptTypeCode = $("#USE_PSPT_TYPE_CODE").val();
	    psptTypeDesc = $("#USE_PSPT_TYPE_CODE").attr("desc");
	}else{
		psptTypeCode = $("#RSRV_STR3").val();
	    psptTypeDesc = $("#RSRV_STR3").attr("desc");
	}
	var custName = $.trim(obj.val());
	var desc = obj.attr("desc");
	
	    
    if(!custName) return;
    
    if(psptTypeCode != "A" && psptTypeCode != "D")
    {
    	var pattern =/[a-zA-Z0-9]/;
    	if(pattern.test(custName)){
    		alert(psptTypeDesc+"不是护照, "+desc+"不能包含数字和字母！");
    		obj.val("");
    		obj.focus();
			return;
    	}
		if(custName.length<2)
		{
			alert(psptTypeDesc+"不是护照,"+desc+"不能少于2个中文字符！");
			obj.val("");
			obj.focus();
			return;
		}
		if(!this.includeChinese(custName))
		{
			alert(psptTypeDesc+"不是护照,"+desc+"不能少于2个中文字符！");
			obj.val("");
			obj.focus();
			return;
		}
	}
    else if(psptTypeCode == "A")
    {
		/*护照：客户名称须大于三个字符，不能全为阿拉伯数字*/
		if(custName.length<3 || $.toollib.isNumber(custName))
		{
			alert(psptTypeDesc+"是护照,"+desc+"须大于三个字符，且不能全为阿拉伯数字！");
			obj.val("");
			obj.focus();
			return;
		}
	}
    
    /*if(custName.indexOf("校园")>-1 || custName.indexOf("海南通")>-1 || custName.indexOf("神州行")>-1 || custName.indexOf("动感地带")>-1 || custName.indexOf("套餐")>-1)
    {
        alert(desc+"不能包含【校园、海南通、神州行、动感地带、套餐】，请重新输入！");
        obj.val("");
        obj.focus();
        return false;
    }*/
    
    this.checkRealNameLimitByPspt();
    
    //如果不需要进行实名制，或者是非客户姓名，则不需要进行实名制校验限制
    /*if(!this.isRealName || fieldName!="CUST_NAME") 
    {
    	if(fieldName=="USE")
    	{
		
			if(this.checkUsePsptIdForReal()) 
			{
				this.checkRealNameLimitByUsePspt();
			}
			
		}
    	
		return;
	}
	
	if(this.checkPsptIdForReal()) 
	{
		this.checkRealNameLimitByPspt();
	}*/
}

function includeChinese(custName)
{
	// 是否包含中文字符
	for(var i=0; i<custName.length; i++)
	{
		if($.toollib.isChinese(custName.charAt(i)))
		{
			return true;
		}
	}
	return false;
}