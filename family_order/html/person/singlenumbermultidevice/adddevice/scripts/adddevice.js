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
/*显示一块区域*/
function showLayer(optionID) {
	$("#" + optionID).css("display","");
}

/*隐藏一块区域*/
function hideLayer(optionID) {
	$("#" + optionID).css("display","none");
}

function refreshPartAtferAuth(data)
{
	
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&USER_ID="+data.get("USER_INFO").get("USER_ID")+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER"), 'userInfoPart,BaseInfoPart,CustInfoFieldPart,TradeInfoHidePart,CheckSerialNumberHidePart,CheckSimCardNoHidePart,OtherInfoHidePart', function(reData){
		
		$("#PRI_PSPT_ID").val(reData.get("PSPT_ID"));
		$("#PRI_PSPT_TYPE_CODE").val(reData.get("PSPT_TYPE_CODE"));
		
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		$.cssubmit.disabledSubmitBtn(true);
		$.MessageBox.error(error_code,error_info);
	});
}


function onSubmit(){
   /**
	 * 人像比对
	 */
   var AGENT_PIC_ID=$("#AGENT_PIC_ID").val();
   if(AGENT_PIC_ID ==''){
	   $("#AGENT_PIC_ID").val("AGENT_PIC_ID_value");
   }
   if(!verifyAll('BaseInfoPart'))
   {
	   return false;
   }
   if(!verifyAll('CustInfoPart'))
   {
	   return false;
   }
   
   //判断主号码副号码开户证件类型证件号码是否一致
   if(!checkCustInfoIsEqualsPriCustInfo())
   {
   	   return false ;
   }
   
   if($("#DEFAULT_PWD_FLAG").val()!="1"){
	    if(!verifyAll('PasswdPart'))
	   {
		   return false;
	   } 
   }
   if($("#BIRTHDAY").val()==""){
	  $("#BIRTHDAY").val("1900-01-01");
   }
   /**
    * 人像比对
    */
	//AGENT_PIC_ID_value这是界面默认的值,
	//当没有对经办人人像摄像是，就把AGENT_PIC_ID指控
	var AGENT_PIC_ID_111=$("#AGENT_PIC_ID").val();
	if(AGENT_PIC_ID_111 =='AGENT_PIC_ID_value'){
		$("#AGENT_PIC_ID").val("");
	}
	
	var opentype = $("#OPEN_TYPE").val();
	
   //非物联网开户
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
		var picid = $("#PIC_ID").val();
		if(null != picid && picid == "ERROR"){
			MessageBox.error("告警提示","客户"+$("#PIC_STREAM").val(),null, null, null, null);
			return false;
		}
		var psptTypeCode=$("#PSPT_TYPE_CODE").val();
		
		//经办人信息
		var agentpicid = $("#AGENT_PIC_ID").val();
		var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
		
		if((psptTypeCode == "0" || psptTypeCode == "1" ) && picid == ""){
			
			/**
			 * REQ201705270006_关于人像比对业务优化需求
			 * <br/>
			 * 个人开户：用户个人身份证证件开户，判断户主或者经办人人像比对通过即可。 
			 * @author zhuoyingzhi
			 * @date 20170620
			 */
			//经办人名称
		    var  custName = $("#AGENT_CUST_NAME").val();
		    //经办人证件号码
			var  psptId = $("#AGENT_PSPT_ID").val();
			//经办人证件地址
			var  agentPsptAddr= $("#AGENT_PSPT_ADDR").val();
			
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
			MessageBox.error("告警提示","经办人"+$("#AGENT_PIC_STREAM").val(),null, null, null, null);
			return false;
		}
		
		if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
			MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
			return false;
		}
		var param = "&PIC_ID="+picid+"&AGENT_PIC_ID="+agentpicid;
		$.cssubmit.addParam(param);
	}
	/****************************************/
     
     return  confirmParamAll();
}

/*封装confirmAll，处理特殊情况
*1、不同证件类型，不同的检查
*2、当需要后台校验的先执行ajax且其值不为空，再做客户端校验	
*/
function confirmParamAll()
{
  
   	if($("#IMSI").val()=='')
   	{
		alert('IMSI为空！');
		return false;
	}
		
	var agentId = "";
	var isAgent = $("#IS_AGENT").val();//开户代理商编码
	
	if(isAgent=='1')
	{
		agentId= $("#AGENT_DEPART_ID");//开户代理商编码
	}
	else
	{
		agentId=$("#AGENT_DEPART_ID1");//开户代理商编码
	}
	
	if(agentId.length)
	{//不为undefined，表示代理商开户
	  if(agentId.val()=='')
	  {
		  alert('请选择开户代理商！');
		  return false;
	  }
	}
	
	if(!$.validate.verifyField($("#SERIAL_NUMBER_B")[0]))return false;
	if(!$.validate.verifyField($("#SIM_CARD_NO_B")[0]))return false;
	

	//CHECK_RESULT_CODE:服务号码与SIM校验结果:0:服务校验通过，1:SIM卡校验通过，初始值为-1
	var checkResultCode = $("#CHECK_RESULT_CODE").val();
	var checkPsptCode  =    $("#CHECK_PSPT_CODE").val();
	
	if(checkResultCode=="-1")
	{
		alert("新开户号码校验未通过！");
		$("#SERIAL_NUMBER_B").focus();
		return false;
	}
	
	if(checkResultCode=="0")
	{
		alert("SIM卡号校验未通过！");
		$("#SIM_CARD_NO_B").focus();
		return false;
	}
	if($("#REAL_NAME").val()=='1')
	{
		if($("#PSPT_TYPE_CODE").val()=="Z")
		{
			alert("实名制开户，证件类型不能为其他，请重新选择！");
			return false;
		}
		var psptId = $("#PSPT_ID").val();
		if(psptId=="0"||psptId=="00"||psptId=="000"||psptId=="0000"||psptId=="00000"||psptId=="1"||psptId=="11"||psptId=="111"||psptId=="1111"||psptId=="11111"||psptId.indexOf("11111111")>-1)
		{
			alert("实名制开户，证件号码过于简单，请重新输入！");
			return false;
		}
		/*if($("#CUST_NAME").val().indexOf("海南通")>-1)
		{
			alert("实名制开户，客户名称不能为海南通，请重新输入！");
			$("#CUST_NAME").val("");
			return false;
		}*/
		if(!confirm('您正在办理实名制，一旦提交资料将不能修改。请确认输入的资料无误！')){
			return  false;
		}
	}
    if($("#REAL_NAME").val()=="1" && $("#REALNAME_LIMIT_CHECK_RESULT").val() != "true"){
        alert("实名制开户数目校验未通过！");
        return false;
    }
	return true;
}


//加载页面js
$(document).ready(function(){

 	$("#REAL_NAME").val("1");

	var tag =  $("#other_ACTIVE_TAG");
	tag.attr("disabled",true);
 	$("#ACTIVE_TAG").val("0");
	$("#TIETONG_NUMBER").bind("keydown",function(e){
	    if (e.keyCode == 13 || e.keyCode == 108) 
	    {
			// 回车事件
	    	getMobilePhoneByTieTongNumber();
			return false;
		}
		
	});
	
	$.developStaff.init("10"); 
	
    $("#AGENT_PSPT_TYPE_CODE").empty();
    $("#AGENT_PSPT_TYPE_CODE").append("<option value=''>请选择</option>");
    $("#AGENT_PSPT_TYPE_CODE").append("<option value='0'>本地身份证</option>");
    $("#AGENT_PSPT_TYPE_CODE").append("<option value='1'>外地身份证</option>");
	$("#AGENT_PSPT_TYPE_CODE").append("<option value='A'>护照</option>");
	
    $("#USE_PSPT_TYPE_CODE").empty();
    $("#USE_PSPT_TYPE_CODE").append("<option value=''>请选择</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='0'>本地身份证</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='1'>外地身份证</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='A'>护照</option>");
	
});

/*************************************校验开户号码及SIM资源 开始************************************/
function checkOldSNPwd()
{

	if ($.auth.getAuthData() == undefined)
	{
		MessageBox.alert("告警提示","请先进行用户鉴权操作！");
		
		$("#SERIAL_NUMBER_B").val('');
		
		return false;
	}
	
	var serialNumberB = $("#SERIAL_NUMBER_B").val();
	var oldSerialNumber = "";
	
	if(!isNull($("#OLD_SERIAL_NUMBER_B").val()))
	{
		oldSerialNumber = $("#OLD_SERIAL_NUMBER_B").val();
	}
	//同一个号码时，不在校验 sunxin
	if(oldSerialNumber==serialNumberB){
	   return;
	}
	//初始化部分界面数据
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-error");	    
    $("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
	intiAllElements();
	
	PAGE_FEE_LIST.clear();
	//重置按钮
	$.cssubmit.disabledSubmitBtn(true);
	
	if($("#SERIAL_NUMBER_B").val()==''||$("#SERIAL_NUMBER_B").val().length<11) 
	{
	     $("#SERIAL_NUMBER_B").val('');
	     alert("输入的手机号码不对，请重新输入！");
	     return false;
	}
	if(!isTel($("#SERIAL_NUMBER_B").val()))
	{ 
		//通过回车
		if(flag==1)
		{
			alert("输入的手机号码不对，请重新输入！");
		}
			
		return false;
	}
	//如果开户号码为空，或格式不正确，则返回
	if(!$.validate.verifyField($("#SERIAL_NUMBER_B")[0])) {
		return false;
	}

	checkMphone(0);
	
	
}
function isTel(str){
       var reg=/^([0-9]|[\-])+$/g ;
       if(str.length!=11&&str.length!=13){//增加物联网手机号码长度 13位
        return false;
       }
       else{
         return reg.exec(str);
       }
}

/*校验号码onblur and 回车*/
function checkMphone(flag){
	var serialNumberB = $("#SERIAL_NUMBER_B").val();
	//针对实名制，需要传递手机给组件 sunxin
	$.custInfo.setSerialNumber(serialNumberB);
		var oldSimCardNoB = $("#OLD_SIM_CARD_NO_B").val();	//考虑预配情况
		$("#CHECK_RESULT_CODE").val("-1");		//设置号码未校验
		
		var oldSerialNumberB = "";
		if(!isNull($("#OLD_SERIAL_NUMBER_B").val())) {
			oldSerialNumberB = $("#OLD_SERIAL_NUMBER_B").val();
		}
	
	if(!isTel(serialNumberB))
	{ 
		//通过回车
		if(flag==1)
			alert("输入的手机号码不对，请重新输入！");
		return false;
	}
	
	//先将SIM卡号清空
	$("#SIM_CARD_NO_B").val("898600");
	$("#SIM_CARD_NO_B").attr("disabled", false);
	if(serialNumberB==''||serialNumberB.length<11)return false;
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
	$.beginPageLoading("副设备户号码校验中......");
	$.ajax.submit(null,'checkSerialNumber','&AUTH_FOR_PERSON_SERIAL_NUMBER=' + authSerialNumber+'&PERSON_AUTH_FLAG=' + authFlag+'&PSPTID_SELECTED=' + psptIDselected+'&INFO_TAG='+infoTag+'&SERIAL_NUMBER=' + serialNumberB +'&OLD_SERIAL_NUMBER='+oldSerialNumberB+'&OLD_SIM_CARD_NO_B='+oldSimCardNoB+'&AGENT_DEPART_ID='+agentCode+ "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()+ "&OPEN_TYPE=" + $("#OPEN_TYPE").val(),'CheckSimCardNoHidePart,CheckSerialNumberHidePart',function(data){
		
			$("#OLD_SERIAL_NUMBER_B").val(serialNumberB);
		
			//校验完后界面部分数据处理
			setAjaxAtferCheckMphone(data);
			$.endPageLoading();
			
			$.cssubmit.disabledSubmitBtn(false);
		   $.endPageLoading();
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
	showDetailErrorInfo(error_code,error_info,derror);
    });
}

/*新开户号码校验完后返回值的处理*/
function setAjaxAtferCheckMphone(data) 
{
	var data0 = data.get(0);
	var simCardNo =data0.get("SIM_CARD_NO");
    var checkResultCode = data0.get("CHECK_RESULT_CODE");
   
    //预配预开时，sim卡自动带出来，且为不可修改
	if(!isNull(simCardNo)) 
	{
		$("#SIM_CARD_NO_B").val(simCardNo);
		$("#OLD_SIM_CARD_NO_B").val(simCardNo);
		$("#SIM_CARD_NO_B").attr("disabled", true);
		$("#CHECK_RESULT_CODE").val(checkResultCode);//设置sim卡校验通过
		//$("#HINT").css("display", "");
		//$("#HINT").text("号码和sim卡校验成功！");
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
		$("#SIM_CARD_INPUT").addClass("e_elements-success");

		//处理密码卡 sunxin
		var cardPasswd = $("#CARD_PASSWD").val();	//密码
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
		
	}
	else {
		$("#SIM_CARD_NO_B").val("898600");
		$("#SIM_CARD_NO_B").attr("disabled",false);
		$("#CHECK_RESULT_CODE").val(checkResultCode);		//设置号码校验通过
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
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

function beforeReadCard(){
var flag =$("#M2M_FLAG").val();
if(flag=="1"){
   $.simcard.setNetTypeCode("07");
}
	var sn = $("#SERIAL_NUMBER_B").val();
	$.simcard.setSerialNumber(sn);
	return true;
}
function beforeCheckSimCardNo(data) {
var isWrited = data.get("IS_WRITED");//用来判断卡是否被写过
if(isWrited == "1"){
	var simno =data.get("SIM_CARD_NO");
	 $("#SIM_CARD_NO_B").val(simno);
	 checkSimCardNo(1);
	}
}
function afterWriteCard(data){
//	alert(data.toString());
	if(data.get("RESULT_CODE")=="0"){
		$.simcard.readSimCard();
	}
}
/*SIM卡号校验*/
function checkSimCardNo(flag) {
   
	var checkResultCode = $("#CHECK_RESULT_CODE").val();	//校验通过标识
	
	//$.cssubmit.disabledSubmitBtn(true);
	var simCardNo = $("#SIM_CARD_NO_B").val();
	if(simCardNo=="" || simCardNo.length<20) {
	if(flag==1)
	{
		alert("SIM卡号输入不正确");
		//$("#SIM_CARD_NO").focus();
	}
		return false;
	}
	
	var oldSimCardNo = "";
	if(!isNull($("#OLD_SIM_CARD_NO_B").val())) {
		oldSimCardNo=$("#OLD_SIM_CARD_NO_B").val();
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
		
		$("#OLD_SIM_CARD_NO_B").val(simCardNo);
		$("#CHECK_RESULT_CODE").val(data0.get("CHECK_RESULT_CODE"));
		$.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
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
				

	    }
		$("#SIM_CARD_INPUT").addClass("e_elements-success");
		
		$.cssubmit.disabledSubmitBtn(false);
		//pos机刷卡参数加载
		$.feeMgr.setPosParam("10", $("#SERIAL_NUMBER_B").val(), $("#EPARCHY_CODE").val());
		$.endPageLoading();
		//处理密码卡 sunxin
		var cardPasswd = $("#CARD_PASSWD").val();	//密码
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
		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#SIM_CARD_INPUT").addClass("e_elements-error");
		showDetailErrorInfo(error_code,error_info,derror);
    });
    

}

/*************************************校验开户号码及SIM资源 结束************************************/


/**
 * 判断主号码副号码开户证件类型证件号码是否一致
 */
function checkCustInfoIsEqualsPriCustInfo() 
{
	var priPsptId = $("#PRI_PSPT_ID").val();  //主号码证件号码
	var priPsptType = $("#PRI_PSPT_TYPE_CODE").val();  //主号码证件类型
	
	var psptId = $("#PSPT_ID").val();     // 证件号码
	var psptType = $("#PSPT_TYPE_CODE").val();  //证件类型
	
	if(priPsptId == psptId && priPsptType == psptType)
	{
		return true;
	}
	else
	{
		alert('副号码证件类型、证件号码必须与主号码一致！');
		return false ;
	}
	
	return true;
}
