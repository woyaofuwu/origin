/*$Id:$*/
var PAGE_FEE_LIST = $.DataMap();

/*************************************公用方法 开始************************************/

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

function displaySwitch(btn, o){
	var button = $(btn);
	var div = $('#' + o);

	if (div.css('display') != "none") 
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户基本信息");
	} 
	else 
	{
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户基本信息");
	}
}


/** 用戶认证结束之后执行的js方法 */
function refreshPartAtferAuth(data){
	var acctTag = data.get("USER_INFO").get("ACCT_TAG");
	if(acctTag!=0){
		$.MessageBox.alert("错误提示", "宽带尚未激活，不能办理该业务！");
		return;
	}
	
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'setPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,widenetInfoPart,internetTvInfoPart',
		function(data) {
				var newTag = data.get('NEW_TAG');
				if ('Y' == data.get('NEW_TAG')) {
					$("#NEW_TAG").val("Y");
					$("#NEW_SERIAL_NUMBER_B").val(data.get('NEW_SERIAL_NUMBER_B'));
					$("#NEW_BASE_INFO").css('display', '');
					$("#BASE_INFO").css('display', 'none');
				} else {
					$("#NEW_BASE_INFO").css('display', 'none');
					$("#BASE_INFO").css('display', '');
				}

			var topsetboxtime = $("#TOP_SET_BOX_TIME").val();
			var paraCode2 = data.get('PARA_CODE2');
			
			//BUS201907300031新增度假宽带季度半年套餐开发需求
			var discntCode = data.get('DISCNT_CODE');
			if( "84071448" == discntCode)
			{
				topsetboxtime = 4;
				paraCode2 = 0 ;
                $("#TOP_SET_BOX_TIME").val(topsetboxtime);
                $("#TOP_SET_BOX_TIME").attr("disabled", true);
			}
			if( "84071449" == discntCode)
			{
				topsetboxtime = 6;
				paraCode2 = 0 ;
                $("#TOP_SET_BOX_TIME").val(topsetboxtime);
                $("#TOP_SET_BOX_TIME").attr("disabled", true);
			}
			//BUS201907300031新增度假宽带季度半年套餐开发需求
			
			//魔百和缴费
			$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9082");
			var feeData = $.DataMap();
			feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "9082");
			feeData.put("FEE", topsetboxtime*paraCode2 );
			feeData.put("PAY", topsetboxtime*paraCode2 );
			feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
			$.feeMgr.insertFee(feeData);
			
			//$.nophonetopsetboxcreate.PAGE_FEE_LIST.put("TOP_SET_BOX_FEE", $.feeMgr.cloneData(feeData));
			
			$("#TOP_SET_BOX_FEE").val( topsetboxtime*paraCode2/100 );
			
			$.endPageLoading();
			$.cssubmit.disabledSubmitBtn(false,"submitButton");
		}, function(error_code, error_info, derror) {
			$.endPageLoading();
			$.cssubmit.disabledSubmitBtn(true,"submitButton");
			showDetailErrorInfo(error_code, error_info, derror);
		}
		);
}

/** 魔百和产品选中 */
function queryPackages(){
	var productId = $("#TOP_SET_BOX_PRODUCT_ID").val();
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var param = "&PRODUCT_ID="+productId+"&SERIAL_NUMBER="+serialNumber;
	
	 if ('' === productId) {
		 $("#BOX_MODE_FEE").val('');
		 // 清空魔百和调测费
         $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(), "2", "439");
	 }

	$.beginPageLoading("基础优惠包和可选优惠包查询中......");
	$.ajax.submit(null, 'queryDiscntPackagesByPID', param, 'bPackagePart,oPackagePart',
			function(data) {
				//拦截提示
				if ('-1' == data.get('resultIPTVCode'))
				{
					$.MessageBox.error("拦截提示:", data.get('resultIPTVInfo'));
					$("#TOP_SET_BOX_PRODUCT_ID").val('');
				}
				//传出押金值
				$("#TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT')/100);
				
				//如果临时押金值为空，则将临时押金值置为押金值
				if (null == $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val() || '' == $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val())
				{
					$("#HIDDEN_TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT')/100)
				}
				
				$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9016");
				
				var feeData = $.DataMap();
//				feeData.clear();
//				feeData.put("MODE", "2");
//				feeData.put("CODE", "9016");
//				feeData.put("FEE", data.get('TOP_SET_BOX_DEPOSIT'));
//				feeData.put("PAY", data.get('TOP_SET_BOX_DEPOSIT'));
//				feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
//				$.feeMgr.insertFee(feeData);
				
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
				$("#TOP_SET_BOX_PRODUCT_ID").val('');
			});
}

function queryTopSetBoxFee() {
	if($("#BOX_MODE_FEE").val()==""||$("#BOX_MODE_FEE").val()==null){
		  $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(), "2", "439");
	}else{
		var productId = $("#TOP_SET_BOX_PRODUCT_ID").val();
		var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
		var param = "&PRODUCT_ID="+productId+"&SERIAL_NUMBER="+serialNumber;

		$.ajax.submit(null, 'queryDiscntPackagesByPID', param, 'oPackagePart',
				function(data) {
			
					//传出押金值
					$("#TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT')/100);
					
					//如果临时押金值为空，则将临时押金值置为押金值
					if (null == $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val() || '' == $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val())
					{
						$("#HIDDEN_TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT')/100)
					}
					
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","439");
					
					var feeData = $.DataMap();
					feeData.clear();
					feeData.put("MODE", "2");
					feeData.put("CODE", "439");
					feeData.put("FEE", data.get('TOP_SET_BOX_DEPOSIT'));
					feeData.put("PAY", data.get('TOP_SET_BOX_DEPOSIT'));
					feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
					$.feeMgr.insertFee(feeData);
					
				}, function(error_code, error_info, derror) {
					showDetailErrorInfo(error_code, error_info, derror);
					$("#TOP_SET_BOX_PRODUCT_ID").val('');
				});
	}
}


/** 业务提交校验 */
function submitBeforeCheck(){
	
	var checkResultCode = $("#CHECK_RESULT_CODE").val();	//校验通过标识
	
	var newTg=$("#NEW_TAG").val();
    if(newTg != "Y"){
    	if(checkResultCode=="-1") {
    		alert("新开户号码未校验通过！");
    		$("SERIAL_NUMBER_B").focus();
    		return false;
    	}
    	
    	if(checkResultCode=="0"){
    		alert("SIM卡号校验未通过！");
    		$("#SIM_CARD_NO").focus();
    		return false;
    	}
    	
    	//个人开户信息
    	if(!$.validate.verifyAll("BaseInfo")) {
    		return false;
    	}
    }
	
	var productId=$("#TOP_SET_BOX_PRODUCT_ID").val();
	if(!productId || productId == ""){
		alert("魔百和产品不能为空！");
		return false;
	}
	
	var basePackages=$("#BASE_PACKAGES").val();
	if(!basePackages || basePackages == ""){
		alert("必选基础包不能为空！");
		return false;
	}
	
    var boxModeFee=$("#BOX_MODE_FEE").val();    
    if(!boxModeFee || boxModeFee == ""){
        alert("开通魔百和，魔百和调测费为必选！");
        return false;
    }
    
	if(!$.validate.verifyAll("widenetInfoPart") || !$.validate.verifyAll("internetTvInfoPart")) {
		return false;
	}
	
	if("Y" == newTg){
		var param = "&NEW_SERIAL_NUMBER_B=" + $("#NEW_SERIAL_NUMBER_B").val();
	    $.cssubmit.addParam(param);
	}
	

	return true;
}

/*************************************校验开户号码及SIM资源 开始************************************/
function checkOldSNPwd() {
	var serialNumber = $("#SERIAL_NUMBER_B").val();
	if(serialNumber == null || serialNumber == '' )
	{
		alert("请输入服务号码");
		return;
	}
	var oldSerialNumber = "";
	if($("#OLD_SERIAL_NUMBER").val() != null || $("#OLD_SERIAL_NUMBER").val() != '' ) {
		oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
	}
	//同一个号码时，不在校验 sunxin
	if(oldSerialNumber==serialNumber){
		return ;
	}
	//初始化部分界面数据
	$("#SERIAL_NUMBER_B_INPUT").removeClass("e_elements-success");
	$("#SERIAL_NUMBER_B_INPUT").removeClass("e_elements-error");	    
    $("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
	//intiAllElements();
	//清空费用
	$.feeMgr.clearFeeList("10");
	PAGE_FEE_LIST.clear();
	//重置按钮
	$.cssubmit.disabledSubmitBtn(true);
	//清空产品资料
	if($("#EPARCHY_CODE").val() !=''){
	    //initProduct();
	}
	
	if($("#SERIAL_NUMBER_B").val()==''||$("#SERIAL_NUMBER_B").val().length<11) {
	     $("#SERIAL_NUMBER_B").val('');
	     return false;
	}
	if(!isTel($("#SERIAL_NUMBER_B").val()))
	{ 
		//通过回车
		if(flag==1)
			alert("输入的手机号码不对，请重新输入！");
		return false;
	}
	//如果开户号码为空，或格式不正确，则返回
	if(!$.validate.verifyField($("#SERIAL_NUMBER_B")[0])) {
		return false;
	}
	//测试号码的拦截@tanzheng add by 20190417 start
	var suffixNum = $("#SERIAL_NUMBER_B").val().substring(7);
	if(suffixNum=='1414'||suffixNum=='2424'||suffixNum=='7474'){
		MessageBox.alert("告警提示","测试号码不能用于开户！");
		$("#SERIAL_NUMBER_B_INPUT").addClass("e_elements e_elements-error");
		return false;
	}
	//测试号码的拦截@tanzheng add by 20190417 end
	
	checkMphone(0);
// 测试环境下没有数据 MM表没有记录
};

/**
 * 初始化产品
 * */
function initProduct()
{
	offerList.renderComponent("","0898");
	
	selectedElements.renderComponent("&NEW_PRODUCT_ID=", "0898");
};

/*************************************校验开户号码及SIM资源 开始************************************/
/*校验号码onblur and 回车*/
function checkMphone(flag){
	var serialNumber = $("#SERIAL_NUMBER_B").val();
	//针对实名制，需要传递手机给组件 sunxin
	//$.custInfo.setSerialNumber(serialNumber);
	var oldSimCardNo = $("#OLD_SIM_CARD_NO").val();	//考虑预配情况
	$("#CHECK_RESULT_CODE").val("-1");		//设置号码未校验
	
	var oldSerialNumber = "";
	if(!isNull($("#OLD_SERIAL_NUMBER").val())) {
		oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
	}
	
	if(!isTel(serialNumber))
	{ 
		//通过回车
		if(flag==1)
			alert("输入的手机号码不对，请重新输入！");
		return false;
	}
	
	//先将SIM卡号清空
	$("#SIM_CARD_NO").val("898600");
	$("#SIM_CARD_NO").attr("disabled", false);
	if(serialNumber==''||serialNumber.length<11)return false;
	if(!$.validate.verifyField($("#SERIAL_NUMBER_B")[0])) {
		return false;
	}
	
	var agentId = "";
	var agentCode ="";
	var isAgent = $("#IS_AGENT").val();//开户代理商编码
	if(isAgent=='1')
	agentId= $("#AGENT_DEPART_ID");//开户代理商编码
	else
	agentId=$("#AGENT_DEPART_ID1");//开户代理商编码
	if(agentId.length){//不为undefined，表示代理商开户
		if(agentId.val()==''){
			alert('请选择开户代理商！');
			return false;
		}
		if(agentId.val()!=''){
			agentCode=agentId.val().substring(0,5);
		}
	}
	
	var psptIDselected = "";
    var infoTag = "0";
    if(flag==1){
        psptIDselected = $("#OLD_NET_CHOOSE_ID").val();
        infoTag = $("#INFO_TAG").val();
    }
    else{
    	$("#INFO_TAG").val("0");
    }
	var authFlag = $("#PERSON_AUTH_FLAG").val();
	var authSerialNumber = $("#AUTH_FOR_PERSON_SERIAL_NUMBER").val();
	$.beginPageLoading("新开户号码校验中......");
	$.ajax.submit(null,'checkSerialNumber','&AUTH_FOR_PERSON_SERIAL_NUMBER=' + authSerialNumber+'&PERSON_AUTH_FLAG=' + authFlag+'&PSPTID_SELECTED=' + psptIDselected+'&INFO_TAG='+infoTag+'&SERIAL_NUMBER=' + serialNumber+'&OLD_SERIAL_NUMBER='+oldSerialNumber+'&OLD_SIM_CARD_NO='+oldSimCardNo+'&AGENT_DEPART_ID='+agentCode+ "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()+ "&OPEN_TYPE=" + $("#OPEN_TYPE").val(),'CheckSimCardNoHidePart,CheckSerialNumberHidePart',function(data){
	//TODO 提示信息待处理
		//增加开户可提示相关信息(规则已终止，省略)
		$("#OLD_SERIAL_NUMBER").val(serialNumber);

		$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		//校验完后界面部分数据处理
		setAjaxAtferCheckMphone(data);
		$.endPageLoading();
		$.cssubmit.disabledSubmitBtn(false);
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	$("#SERIAL_NUMBER_B_INPUT").addClass("e_elements-error");
	showDetailErrorInfo(error_code,error_info,derror);
    });
};

/**
 * 新开户号码校验完后返回值的处理
 * */
function setAjaxAtferCheckMphone(data) {
	var data0 = data.get(0);
	var simCardNo =data0.get("SIM_CARD_NO");
    var checkResultCode = data0.get("CHECK_RESULT_CODE");
    var existsPlunder_188= data0.get("EXISTS_188_PLUNDER"); 
   //预配预开时，sim卡自动带出来，且为不可修改
	if(!isNull(simCardNo)) {
		$("#SIM_CARD_NO").val(simCardNo);
		$("#OLD_SIM_CARD_NO").val(simCardNo);
		$("#SIM_CARD_NO").attr("disabled", true);
		$("#CHECK_RESULT_CODE").val(checkResultCode);//设置sim卡校验通过
		//$("#HINT").css("display", "");
		//$("#HINT").text("号码和sim卡校验成功！");
		$("#SERIAL_NUMBER_B_INPUT").addClass("e_elements-success");
		$("#SIM_CARD_INPUT").addClass("e_elements-success");
		
	     //处理密码卡 
		var cardPasswd = $("#CARD_PASSWD").val();	//密码
		var passCode = $("#PASSCODE").val();	//密码因子
		if(cardPasswd!=""&&passCode!=""){
			
			$("#DEFAULT_PWD_FLAG").val("1");//使用初始密码 sunxin
			//hideLayer("PasswdPart");//将密码组件设置不能选择
			
			/*if(confirm('该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？')){
			}
			else{
			   $("#DEFAULT_PWD_FLAG").val("0");//不使用初始密码 sunxin
				showLayer("PasswdPart");//将密码组件设置不能选择
			}*/
		}
		else{
			   $("#DEFAULT_PWD_FLAG").val("0");//不使用初始密码 sunxin
				//showLayer("PasswdPart");//将密码组件设置不能选择
			 }
		
	}
	else {
		$("#SIM_CARD_NO").val("898600");
		$("#SIM_CARD_NO").attr("disabled",false);
		$("#CHECK_RESULT_CODE").val(checkResultCode);		//设置号码校验通过
		$("#SERIAL_NUMBER_B_INPUT").addClass("e_elements-success");
	}
};

function isTel(str){
    var reg=/^([0-9]|[\-])+$/g ;
    if(str.length!=11&&str.length!=13){//增加物联网手机号码长度 13位
     return false;
    }
    else{
      return reg.exec(str);
    }
}

/*新开户号码校验，初始化界面部分数据  后续可能不需要 sunxin*/
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
	
	$("#PAY_NAME").val("");
	$("#PAY_MODE_CODE").val("");
	$("#BANK_CODE").val("");
	$("#BANK_ACCT_NO").val("");
}

/**
 * SIM卡号校验
 * */
function checkSimCardNo(flag) {
   
	var checkResultCode = $("#CHECK_RESULT_CODE").val();	//校验通过标识
	
	//$.cssubmit.disabledSubmitBtn(true);
	var simCardNo = $("#SIM_CARD_NO").val();
	if(simCardNo=="" || simCardNo.length<20) {
	if(flag==1)
		alert("SIM卡号输入不正确");
		//$("#SIM_CARD_NO").focus();
		return false;
	}
	
	var oldSimCardNo = "";
	if(!isNull($("#OLD_SIM_CARD_NO").val())) {
		oldSimCardNo=$("#OLD_SIM_CARD_NO").val();
	}
	if(oldSimCardNo==simCardNo){
	   return;
	}
	$.cssubmit.disabledSubmitBtn(true);
	$.feeMgr.clearFeeList("10");//防止多次点击校验产生多条费用
	$("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
	
	var agentId = "";
	var agentCode ="";
	var isAgent = $("#IS_AGENT").val();//开户代理商编码
	if(isAgent=='1')
	agentId= $("#AGENT_DEPART_ID");//开户代理商编码
	else
	agentId=$("#AGENT_DEPART_ID1");//开户代理商编码
	if(agentId.length){//不为undefined，表示代理商开户
	  if(agentId.val()==''){
	    alert('请选择开户代理商！');
	    return false;
	  }
		if(agentId.val()!=''){
	 	 agentCode=agentId.val().substring(0,5);
		}
	}

	//先检查服务号码是否校验通过	
	if($("#SERIAL_NUMBER_B").val()=="") {
		alert("请先校验新开户号码！");
		return false ;
	}
   
	if(checkResultCode=="-1") {
		alert("新开户号码未校验通过！");
		return false;
	}
	var param = "&CHECK_RESULT_CODE=" + checkResultCode + "&OLD_SIM_CARD_NO=" + oldSimCardNo + "&SIM_CARD_NO=" + simCardNo + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER_B").val()+ "&M2M_FLAG=" + $("#M2M_FLAG").val()+ "&AUTH_FOR_SALE_ACTIVE_TAG=" + $("#AUTH_FOR_SALE_ACTIVE_TAG").val()+ "&SPEC_SN_SECTNO_SIM_FEE=" + $("#SPEC_SN_SECTNO_SIM_FEE").val()+ "&NO_CARD_FEE_BRAND=" + $("#NO_CARD_FEE_BRAND").val()
	+ "&OPEN_TYPE=" + $("#OPEN_TYPE").val()+ "&PROV_OPEN_ADVANCE_PAY_FLAG=" + $("#PROV_OPEN_ADVANCE_PAY_FLAG").val()+ "&PROV_OPEN_OPERFEE_FLAG=" + $("#PROV_OPEN_OPERFEE_FLAG").val()+ "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()+'&AGENT_DEPART_ID='+agentCode;
	$.beginPageLoading("SIM卡号校验中......");
	$.ajax.submit(null, 'checkSimCardNo', param, 'CheckSimCardNoHidePart', function(data){
		var data0 = data.get(0);
		
		$("#OLD_SIM_CARD_NO").val(simCardNo);
		$("#CHECK_RESULT_CODE").val(data0.get("CHECK_RESULT_CODE"));

		/*$.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
		$.feeMgr.insertFee(PAGE_FEE_LIST.get("PRODUCT_FEE"));
		if(data0.get("FEE")){
				var feeData = $.DataMap();
				feeData.put("MODE", data0.get("FEE_MODE"));
				feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
				feeData.put("FEE",  data0.get("FEE"));
				feeData.put("PAY",  data0.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","10");				
				$.feeMgr.insertFee(feeData);
				PAGE_FEE_LIST.put("SIMCARD_FEE", $.feeMgr.cloneData(feeData));
	    }*/
	    
		$("#SIM_CARD_INPUT").addClass("e_elements-success");

		$.cssubmit.disabledSubmitBtn(false);
		//pos机刷卡参数加载
		//$.feeMgr.setPosParam("10", $("#SERIAL_NUMBER").val(), $("#EPARCHY_CODE").val());
		$.endPageLoading();
		//处理密码卡 sunxin
		/*var cardPasswd = $("#CARD_PASSWD").val();	//密码
		var passCode = $("#PASSCODE").val();	//密码因子
		if(cardPasswd!=""&&passCode!=""){
			if(confirm('该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？')){
				$("#DEFAULT_PWD_FLAG").val("1");//使用初始密码 sunxin
				hideLayer("PasswdPart");//将密码组件设置不能选择
			}
			else{
			   $("#DEFAULT_PWD_FLAG").val("0");//不使用初始密码 sunxin
				showLayer("PasswdPart");//将密码组件设置不能选择
			   }
		}
		else{
			   $("#DEFAULT_PWD_FLAG").val("0");//不使用初始密码 sunxin
				showLayer("PasswdPart");//将密码组件设置不能选择
			 }
			 */
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#SIM_CARD_INPUT").addClass("e_elements-error");
		showDetailErrorInfo(error_code,error_info,derror);
    });
};

/**读卡/写卡************* 开始 ************** */
function beforeReadCard(){
	var flag =$("#M2M_FLAG").val();
	if(flag=="1"){
	   $.simcard.setNetTypeCode("07");
	}
		var sn = $("#SERIAL_NUMBER_B").val();
		$.simcard.setSerialNumber(sn);
		return true;
};
function beforeCheckSimCardNo(data) {
	var isWrited = data.get("IS_WRITED");//用来判断卡是否被写过
	if(isWrited == "1"){
		var simno =data.get("SIM_CARD_NO");
		 $("#SIM_CARD_NO").val(simno);
		 checkSimCardNo(1);
		}
};
function afterWriteCard(data){
	if(data.get("RESULT_CODE")=="0"){
		$.simcard.readSimCard();
	}
};
/**读卡/写卡************* 结束*************   */

/*************************************校验开户号码及SIM资源 结束************************************/

//设置魔百和费用
function settopsetboxfee(){
	var topsetboxtime = $("#TOP_SET_BOX_TIME").val();
	$.ajax.submit(this, 'settopsetboxfee',
		'&TOP_SET_BOX_TIME=' + topsetboxtime, '',
		function(data)
		{
			//魔百和缴费
			$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9082");
			var feeData = $.DataMap();
			feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "9082");
			feeData.put("FEE", data.get('TOP_SET_BOX_FEE'));
			feeData.put("PAY", data.get('TOP_SET_BOX_FEE'));
			feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
			$.feeMgr.insertFee(feeData);
			//$.nophonetopsetboxcreate.PAGE_FEE_LIST.put("TOP_SET_BOX_FEE", $.feeMgr.cloneData(feeData));
			
			$("#TOP_SET_BOX_FEE").val(data.get('TOP_SET_BOX_FEE')/100);
			
			$.endPageLoading();
		}, function(error_code, error_info) 
		{
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		});
};
