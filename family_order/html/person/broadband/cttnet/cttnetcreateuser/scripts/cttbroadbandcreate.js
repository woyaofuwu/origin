$(document).ready(function(){
	if($("#MODEM_STYLE").val() =="1"){
		$("#MODEM_NUMERIC").attr("disabled","true");
		$("#QUERY_MODEM").css("display", "none");
	}
});


function setAddrInfo()
{
	$("#STAND_ADDRESS").val("测试标准地址");
	$("#STAND_ADDRESS_CODE").val("12306");
	$("#SIGN_PATH").val("123");
}

(function($){
	$.extend({cttbroadbandcreate:{
		writeBankInfo:function(btn){
			if ("none" != $("#BankPart").css("display")) {
				$("#BankPart").css("display", "none");
				btn.children[0].className = "e_ico-unfold";
				btn.children[1].innerHTML = "填写银行信息";
			} else {
				$("#BankPart").css("display", "");
				btn.children[0].className = "e_ico-fold";
				btn.children[1].innerHTML = "隐藏银行信息";
			}
		},
		writeOtherInfo:function(btn){
			if ("none" != $("#OtherInfoPart").css("display")) {
				$("#OtherInfoPart").css("display", "none");
				btn.children[0].className = "e_ico-unfold";
				btn.children[1].innerHTML = "填写其他信息";
			} else {
				$("#OtherInfoPart").css("display", "");
				btn.children[0].className = "e_ico-fold";
				btn.children[1].innerHTML = "隐藏其他信息";
			}
		},
		writePostInfo:function(btn){
			if ("none" != $("#PostInfoPart").css("display")) {
				$("#PostInfoPart").css("display", "none");
				btn.children[0].className = "e_ico-unfold";
				btn.children[1].innerHTML = "填写邮寄信息";
			} else {
				$("#PostInfoPart").css("display", "");
				btn.children[0].className = "e_ico-fold";
				btn.children[1].innerHTML = "隐藏邮寄信息";
			}
		},
	
		getProductBySpec: function(data){
			var prodSpec = $("#PROD_SPEC_TYPE").val();
			$.beginPageLoading("产品加载中.....");
			$.ajax.submit(this, 'getProductBySpec', "&PROD_SPEC_TYPE=" + prodSpec, 'productListPart', function() {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code, error_info);
			});
		},
		getPackages: function(data){
			var prodId = $("#PRODUCT_ID").val();
			var eparchyCode = $("#USER_EPARCHY_CODE").val();
			var param ="&NEW_PRODUCT_ID="+prodId;
			
			offerList.renderComponent(prodId,eparchyCode);
			selectedElements.clearCache();
			selectedElements.renderComponent(param,eparchyCode);
			
			//初始化宽带帐号信息
			var prodSpec = $("#PROD_SPEC_TYPE").val();
			$.beginPageLoading("产品加载中.....");
			$.ajax.submit(this, 'getAcctList', "&PRODUCT_SPEC=" + prodSpec+"&PRODUCT_ID=" + prodId, 'widenetInfosPart', function() {
				if($("#PRODUCT_ID").val() == "50001004"){
					$("#WIDENET_ACCT_ID").attr("disabled", true);
				}
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code, error_info);
			});
			
		},
		afterRenderSelectedElements:function(data){
			
			var check = selectedElements.checkForcePackage();
		
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
		},
		
		searchElementOptionEnter:function(data){
			//元素搜索
			var searchLi = $("#Ul_Search_elementSearch li[class=focus]");
			var reOrder = searchLi.attr("REORDER");
			var elementId = searchLi.attr("ELEMENT_ID");
			var elementName = searchLi.attr("ELEMENT_NAME");
			var productId = searchLi.attr("PRODUCT_ID");
			var packageId = searchLi.attr("PACKAGE_ID");
			var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
			var forceTag = searchLi.attr("FORCE_TAG");
			
			if(reOrder!="R"&&reOrder!="C"&&selectedElements.checkIsExist(elementId,elementTypeCode)){
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
			$("#Div_Search_elementSearch").css("visibility","hidden");
		},
		
		onTradeSubmit:function(){
			if (!$.validate.verifyAll()) {
				return false;
			}
			
			//校验用户选择的服务和优惠信息
			var data = selectedElements.getSubmitData();
			var checkElement = selectedElements.selectedEls;
			var length = checkElement.length;
			var svcName = "";
			var discntName = "";
			var svcCount = 0;
			var discntCount = 0;
			var mustChooseSvc = 0;
			for(var i=0;i<length;i++){
				var element = checkElement.get(i);
				var elementType = element.get("ELEMENT_TYPE_CODE");
				var elementId = element.get("ELEMENT_ID");
				var modifyTag = element.get("MODIFY_TAG");
				if(element.get("MODIFY_TAG")=="0" && "D" == elementType){
					discntCount++;
				}
				if("501" != elementId && "S" == elementType && element.get("MODIFY_TAG")!="1"&& element.get("MODIFY_TAG")!="0_1"){
					svcCount++;
					
				}
				if("501" == elementId){
					mustChooseSvc++;
				}
			}
			if(mustChooseSvc < 1){
				alert("请选择宽带基础服务!");
				return false;		
			}
			if(discntCount < 1){
				alert("请选择一个优惠!");
				return false;		
			}
			if(discntCount > 1){
				alert("只能选择一个优惠!");
				return false;		
			}
			if(svcCount < 1){
				alert("请选择一个服务!");
				return false;		
			}
			
			if(svcCount > 1){
				alert("只能选择一个可选服务!");
				return false;		
			}
			
			
			if ($("#POST_TAG").val() != "0" && $("#POST_TAG").val()!="") {
				if($("#POST_TYPESET").val()== ""){
					alert("请选择邮寄方式！");
					return false;
				}
			}
			
			
			var selElements = selectedElements.getSubmitData();
			var newPasswd = $("#WIDENET_ACCT_PASSWD").val();
			var widenetAcctId = $("#WIDENET_ACCT_ID").val();
			//var prodId = $("#PRODUCT_ID").val();+"&PRODUCT_ID=" + prodId
			
			var widenetAcctId = $("#WIDENET_ACCT_ID").val();
			var oldAccountId = $("#OLD_ACCOUNT_ID").val();
			if(oldAccountId != widenetAcctId){
				alert("请校验宽带帐号！");
				return false;
			}
			var standAddress = $("#STAND_ADDRESS").val();
			if(standAddress == ""){
				alert("请选择标准地址！");
				return false;
			}
			if(selElements&&selElements.length>0){
				if (confirm("确定办理铁通宽带开户业务吗？")) {
					var param = "&SELECTED_ELEMENTS=" + selElements.toString()+"&USER_TYPE_CODE=0"+"&SERIAL_NUMBER="+widenetAcctId+
					"&USER_PASSWD="+newPasswd;
					$.cssubmit.addParam(param);
					
					return true;
				} else {
					return false;
				}
			}else{
				alert("您没有进行任何操作，不能提交");
			}
		},
		checkWidenetAcctId:function(){//校验宽带帐号
			
			var newPasswd = $("#WIDENET_ACCT_PASSWD").val();
			var widenetAcctId = $("#WIDENET_ACCT_ID").val();
			var oldAccountId = $("#OLD_ACCOUNT_ID").val();
			if(widenetAcctId==""){
				alert("账户不能为空！");
				return false;
			}
			if(oldAccountId == widenetAcctId){
				alert("帐号已校验通过，请勿重复校验！");
				return false;
			}
			
			var aremUserTag = $("#ARAM_USER_TAG").val();
			if(aremUserTag != "yes"){//为非军区用户时,需要较验长度
				var prefix = $("#ACCT_PREFIX").val();
				var tail = $("#ACCT_TAIL").val();
				var patrn="";
				if(tail=="7"){
					patrn = new RegExp("^"+prefix+"\\d{7}$");
					if (!patrn.test(widenetAcctId)) 
					{
						alert("账户号码必须以"+prefix+"开头，后面接"+tail+"位数字");
						return false;
					}			
				} 	
		  	}
		  	if(newPasswd=="")
			{
				alert("账户密码不能为空！");
				return false;
			}
			if(newPasswd.length<6)
			{
				alert("账户密码不能小于6位！");
				return false;
			}
			$.beginPageLoading("宽带帐号校验中.....");
			$.ajax.submit(this, 'checkAcctId', "&widenetAcctId=" + widenetAcctId+"&ARAM_USER_TAG=" + aremUserTag, '', function() {
				$("#OLD_ACCOUNT_ID").val(widenetAcctId);
				alert("校验通过!");
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code, error_info);
			});
			
		},
		setReturnFunc:function(){
		$.feeMgr.removeFee("9711","0","9205");//删除已经添加的modem费用
		$.feeMgr.removeFee("9711","2","201");//删除已经添加的modem费用
		
		//添加新的费用
			var modemStyle = $("#MODEM_STYLE").val();
			var mode = "0";
			var code = "9205";
			var fee = $("#SALEPRICE").val();
			
			if(fee == ""){
				fee = "0";
			}
							
			if(modemStyle == "2"){
				mode = "2";
				code = "201";
			}if(modemStyle == "3"){
				mode = "0";
				code = "9205";
			}else{
				mode = "2";
				code = "200";
				fee = "0";
			}
			var feeData = $.DataMap();
			feeData.put("MODE", mode);
			feeData.put("CODE", code);
			feeData.put("FEE",  fee);
			feeData.put("PAY",  fee);		
			feeData.put("TRADE_TYPE_CODE","9711");		
			$.feeMgr.insertFee(feeData);
		},
		afterSelectAction:function(package){
			var serialNumber = $("#SERIAL_NUMBER").val();
			var eparchyCode = $("#USER_EPARCHY_CODE").val();
			var userProductId = $("#USER_PRODUCT_ID").val();
			var userId= $("#USER_ID").val();
			var araeCode = "";
			var tradeTypeCode = "9711";
			var param ="&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&TRADE_TYPE_CODE="+tradeTypeCode+"&ARAE_CODE="+araeCode;
			offerList.renderComponent(package,eparchyCode,param);
		},
		checkPayModeCode:function(){
			// 如果帐户类型为现金
			if ($("#PAY_MODE_CODE").val() == "0") {
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
			} else {
				$("#SUPER_BANK_CODE").attr("disabled", false);
				$("#BANK_CODE").attr("disabled", false);
				$("#BANK_ACCT_NO").attr("disabled", false);
				$("#span_SUPER_BANK_CODE").addClass("e_required");
				$("#span_BANK_CODE").addClass("e_required");
				$("#span_BANK_ACCT_NO").addClass("e_required");
				$("#SUPER_BANK_CODE").attr("nullable", "no");
				$("#BANK_CODE").attr("nullable", "no");
				$("#BANK_ACCT_NO").attr("nullable", "no");
			}
		},
		writeAssureTypeTag:function(){
			// 如果选择了担保类型
			if ($("#ASSURE_TYPE_CODE").val() != "0" && $("#ASSURE_TYPE_CODE").val()!="") {
				$("#ASSURE_PSPT_ID1").addClass("e_required");
				$("#ASSURE_NAME1").addClass("e_required");
				$("#ASSURE_PSPT_TYPE_CODE1").addClass("e_required");
				$("#ASSURE_PHONE1").addClass("e_required");
				$("#ASSURE_DATE1").addClass("e_required");
				
				$("#ASSURE_PSPT_ID").attr("nullable", "no");
				$("#ASSURE_NAME").attr("nullable", "no");
				$("#ASSURE_PSPT_TYPE_CODE").attr("nullable", "no");
				$("#ASSURE_PHONE").attr("nullable", "no");
				$("#ASSURE_DATE").attr("nullable", "no");
				
			}else{
				$("#ASSURE_PSPT_ID1").removeClass("e_required");
				$("#ASSURE_NAME1").removeClass("e_required");
				$("#ASSURE_PSPT_TYPE_CODE1").removeClass("e_required");
				$("#ASSURE_PHONE1").removeClass("e_required");
				$("#ASSURE_DATE1").removeClass("e_required");
				
				$("#ASSURE_PSPT_ID").attr("nullable", "yes");
				$("#ASSURE_NAME").attr("nullable", "yes");
				$("#ASSURE_PSPT_TYPE_CODE").attr("nullable", "yes");
				$("#ASSURE_PHONE").attr("nullable", "yes");
				$("#ASSURE_DATE").attr("nullable", "yes");
			}
		},
		checkSuperBankCode:function(){
				/**
				 *  上级银行变化时，刷新下级银行 
				 */
			var param = "&SUPER_BANK_CODE=" + $("#SUPER_BANK_CODE").val();
			$.beginPageLoading("银行数据查询中。。。");
			$.ajax.submit(this, 'getBankBySuperBank', param, 'BankCodePart',
					function() {
						// 刷新下级银行区域时，会将样式刷没了，这里认为，可以选择上级银行，则表示下级银行是必填的
						$("#span_BANK_CODE").addClass("e_required");
						$.endPageLoading();
					}, function(error_code, error_info) {
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					});	 
		},
		setModemStyleClass:function(){
			/**
			 * 更改Modem方式样式
			 */
			if($("#MODEM_STYLE").val() !="1"){//1:自备
				$("#MODEM_NUMERIC").val("");
				$("#span_MODEM_STYLE").addClass("e_required");
				$("#span_MODEM_NUMERIC").addClass("e_required");
				$("#MODEM_STYLE").attr("nullable","no");
				$("#MODEM_NUMERIC").attr("nullable","no");
				$("#MODEM_NUMERIC").attr("disabled","");
				$("#MODEM_NUMERIC").attr("readonly","true");
				$("#QUERY_MODEM").css("display", "");
				
				$.feeMgr.removeFee("9711","0","9205");//删除已经添加的modem费用
				$.feeMgr.removeFee("9711","2","201");//删除已经添加的modem费用
				$("#SALETYPE_DESC").value("");
				$("#SALEPRICE").value("");
				$("#SALETYPE_CODE").value("");
				$("#MATERIAL_CODE").value("");
			}else{
				$("#MODEM_NUMERIC").val("");
				$("#span_MODEM_STYLE").removeClass("e_required");
				$("#span_MODEM_NUMERIC").removeClass("e_required");
				$("#MODEM_STYLE").attr("nullable","yes");
				$("#MODEM_NUMERIC").attr("nullable","yes");
				$("#MODEM_NUMERIC").attr("disabled","true");
				$("#QUERY_MODEM").css("display", "none");
			}
		},
		writePostTag:function(){
		 //选择不邮寄
			if($("#postInfo_POST_TAG").val()=="0" || $("#postInfo_POST_TAG").val()=="") {
				//隐藏邮寄信息区
				$("#PostTagHiddenPart").css("display", "none");
				
				//去掉必填样式
				$("#span_postInfo_POST_TYPESET").removeClass("e_required");
				$("#span_postInfo_POSTTYPE_CONTENT").removeClass("e_required");
				$("#span_postInfo_POST_ADDRESS").removeClass("e_required");
				$("#span_postInfo_POST_NAME").removeClass("e_required");
				$("#span_postInfo_FAX_NBR").removeClass("e_required");
				$("#span_postInfo_EMAIL").removeClass("e_required");
		
				//去掉必填限制
				$("#postInfo_POST_TYPESET").attr("nullable", "yes");
				$("#postInfo_POSTTYPE_CONTENT").attr("nullable", "yes");
				$("#postInfo_POST_ADDRESS").attr("nullable", "yes");
				$("#postInfo_POST_NAME").attr("nullable", "yes");
				$("#postInfo_FAX_NBR").attr("nullable", "yes");
				$("#postInfo_EMAIL").attr("nullable", "yes");
				
				//清空邮寄信息
				$("#postInfo_POST_TYPESET").val();
				$("#postInfo_POSTTYPE_CONTENT").val();
				$("#postInfo_POST_ADDRESS").val();
				$("#postInfo_POST_CODE").val();
				$("#postInfo_POST_NAME").val();
				$("#postInfo_FAX_NBR").val();
				$("#postInfo_EMAIL").val();
				
			}
			//选择邮寄
			else {
				//显示邮寄信息区
				$("#PostTagHiddenPart").css("display", "");
				
				$("#postInfo_POST_CYC").attr("disabled", false);
				$("#PostTypeContentPart").attr("disabled", true);
				
				//初始化邮寄信息
				$("#postInfo_POST_ADDRESS").val($("#POST_ADDRESS").val());
				$("#postInfo_POST_CODE").val($("#POST_CODE").val());
				$("#postInfo_POST_NAME").val($("#CUST_NAME").val());
				$("#postInfo_FAX_NBR").val($("#FAX_NBR").val());
				$("#postInfo_EMAIL").val($("#EMAIL").val());
				
				//必填样式
				$("#span_postInfo_POST_TYPESET").addClass("e_required");
				//必填限制
				$("#postInfo_POST_TYPESET").attr("nullable", "no");
				$("#POST_TYPESET").attr("nullable", "no");
			}
		},
		checkAddNewPasswd:function(){
			//显示新密码输入弹出框
			$("#pwdModify").css("display","");
			//设置新密码输入焦点，可以直接输入，从而减少操作
			$("#NEW_PASSWD").focus();
		},
		confirmNewPasswd:function(){
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
			//比较两次输入的密码
			if($("#NEW_PASSWD").val() != $("#NEW_PASSWD_AGAIN").val()) {
				alert("两次输入的密码不一致，请重新输入！");
				$("#NEW_PASSWD").val("");
				$("#NEW_PASSWD_AGAIN").val("");
				$("#NEW_PASSWD").focus();
				
				return false;
			}
			
			 //设置密码
			 $("#WIDENET_ACCT_PASSWD").val($("#NEW_PASSWD").val());
			 
			//清空新密码输入
			$("#NEW_PASSWD").val("");
			$("#NEW_PASSWD_AGAIN").val("");
			
			//隐藏新密码输入弹出框
			$("#pwdModify").css("display","none");
		},
		resetNewPasswd:function(){
			$("#NEW_PASSWD").val("");
			$("#NEW_PASSWD_AGAIN").val("");
			$("#NEW_PASSWD").focus();
		},
		cancelNewPasswd:function(){
			$("#NEW_PASSWD").val("");
			$("#NEW_PASSWD_AGAIN").val("");
			$("#pwdModify").css("display","none");
		},
		checkCttPhone:function(){
			var cttPhone = $("#cond_CTT_PHONE").val();
			if(cttPhone==""){
				alert("请输入固网号码！");
				return false;
			}
			
			$.beginPageLoading("正在校验数据...");
		    $.ajax.submit('', 'queryCttPhone', '&CTT_PHONE='+cttPhone, 'showCttPhoneInfos', function(){
		       $.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		    });  
		},
		tableRowDBClick:function(){
			var table = $.table.get("QueryCttPhoneTable");
			var json = table.getRowData();
			var serialeNumber = json.get('SERIAL_NUMBER','');
			var standAddrCode = json.get('STAND_ADDRESS_CODE','');
			var standAddr = json.get('STAND_ADDRESS','');
			var finishState = json.get('FINISH_STATE','');
			//$("#CTT_PHONE").val(serialeNumber);
			//$("#STAND_ADDRESS").val(standAddr);
			//$("#STAND_ADDRESS_CODE").val(standAddrCode);
			setReturnValue({"CTT_PHONE":serialeNumber, 
					"STAND_ADDRESS":standAddr,
					"STAND_ADDRESS_CODE":standAddrCode}, true);
			
		},
		checkPostTypeSet:function(data){
			 return true;
		}
	}});
})(Wade);

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
    param += "&NET_TYPE_CODE=11";
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
}

function checkPsptTypeCode()
{
	var psptId = $("#PSPT_ID").val();
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
	
	if(psptId=="")
	{
		return false;
	}
	if(psptTypeCode=="")
	{
		return false;
	}
	if(!$.custValidate.validatePsptId('PSPT_TYPE_CODE', 'PSPT_ID')) 
	{
		return false;
	}
	
	//REQ201602160017 关于更改铁通业务开户界面和变更界面营业执照位数的需求 营业执照只能15位或18位
	if(psptTypeCode=="E")
	{
		if(psptId.length != 15 && psptId.length != 18)
		{
			alert('营业执照类型校验：证件号码长度需满足15位或者18位！当前：'+psptId.length+'位。');
			$('#PSPT_ID').val('');
			return false;
		}
	}
	
	$.beginPageLoading("正在校验数据...");
    $.ajax.submit('', 'checkPsptLimit', '&PSPT_ID='+psptId+'&PSPT_TYPE_CODE='+psptTypeCode, 'UserInfoArea', function(data){
	    if(data.get("HAS_OWEFEE_USER") == 1){
	    	MessageBox.confirm("确认提示", data.get("MESSAGE"), function(btn){
						if(btn == "ok"){
							
						}else{
							$.redirect.toPage(null,"person.cttbroadband.CttBroadBandCreate","initData",'');
						}
					}, null, null);		
	    }
	    $('#PAY_NAME').val($('#CUST_NAME').val());
       $.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
    
    this.checkRealNameLimitByPspt();
}

function checkID()
{
	var psptId = $("#PSPT_ID").val();
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	if(psptId=="")
	{
		return false;
	}
	if(psptTypeCode=="")
	{
		return false;
	}
	if(!$.custValidate.validatePsptId('PSPT_TYPE_CODE', 'PSPT_ID')) 
	{
		return false;
	}
	
	//REQ201602160017 关于更改铁通业务开户界面和变更界面营业执照位数的需求 营业执照只能15位或18位
	if(psptTypeCode=="E")
	{
		if(psptId.length != 15 && psptId.length != 18)
		{
			alert('营业执照类型校验：证件号码长度需满足15位或者18位！当前：'+psptId.length+'位。');
			$('#PSPT_ID').val('');
			return false;
		}
	}
	
	$.beginPageLoading("正在校验数据...");
    $.ajax.submit('', 'checkPsptLimit', '&PSPT_ID='+psptId+'&PSPT_TYPE_CODE='+psptTypeCode, 'UserInfoArea', function(data){
	    if(data.get("HAS_OWEFEE_USER") == 1){
	    	MessageBox.confirm("确认提示", data.get("MESSAGE"), function(btn){
						if(btn == "ok"){
							
						}else{
							$.redirect.toPage(null,"person.cttbroadband.CttBroadBandCreate","initData",'');
						}
					}, null, null);		
	    }
	    $('#PAY_NAME').val($('#CUST_NAME').val());
       $.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
    
    this.checkRealNameLimitByPspt();
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

function validatePsptAddress(fieldName)
{
	var obj=$("#"+fieldName);
	var psptAddr = $.trim(obj.val());
	var desc = obj.attr("desc");
	
	if(psptAddr="" || psptAddr.length<8)
	{
		alert(desc+"栏录入文字需大于8位！");
		obj.val("");
		obj.focus();
		return;
	}
	
	if($.toollib.isNumber(psptAddr))
	{
		alert(desc+"栏不能全部为数字！");
		obj.val("");
		obj.focus();
		return;
	}
	var strPsptAddr = $("#PSPT_ADDR").val();
	$('#ASSURE_POST_ADDRESS').val(strPsptAddr);
}