
function ctrlInputValue(){
	var feeType = $('#FEE_TYPE').val();//费用类型
	var data6 = $('#DATA6').val();//限定方式
	var data7 = $('#DATA7').val();//限定值
	//feeType 0:全部费用 1:部分费用
	if(feeType=='0'){
		$('#DATA6').val('0');//不限定
		$('#DATA7').val('0');//限定值则为0
		$("#DATA7").attr("disabled",true)
	}
	
	if(feeType=='1'){
		$('#DATA6').val('1');//金额
		$('#DATA7').val('0');//限定值则为0
		$("#DATA7").attr("disabled",false)
	}
}

function checkFormSubmit(){
	var startcycle = $('#DATA3').val();//生效帐期
	var endcycle = $('#DATA4').val();//结束帐期
	var currcycle = $('#CURRENT_CYCLE').val();//当前帐期
	if($('#SERIAL_NUMBER').val()==''){
		alert('请输入一个正确的付费号码!');
		return false;
	}
	if(startcycle == "" )
	{
		alert('生效帐期不能为空!');
		return false;
	}
	if(endcycle == "" )
	{
		alert('结束帐期不能为空!');
		return false;
	}
	if($('#endAcctId').css("display") != "none"&&endcycle == "205012")
	{
		alert('请修改结束帐期！');
		return false;
	}
	if(startcycle > endcycle){
		alert('生效帐期不能大于结束帐期!');
		return false;
	}
	if($('#DATA5').val() == ""){
		alert('付费帐目不能为空!');
		return false;
	}
	if($('#FEE_TYPE').val() == "1" && $('#DATA7').val()==0){
		alert('限定值必须大于0！');
		return false;
	}
	
	if($('#MEM_CUST_NAME').val()=='' || $('#PAY_NAME').val() =='' || $('#PAY_MODE_CODE').val()==''){
		alert('该付费号码的客户名称，账户名称，账户类别资料不全!');
		return false;
	}
	
	return true;
}

function selectJudge(){
	
	var startcycle = $('#DATA3').val();//生效帐期
	var endcycle = $('#DATA4').val();//结束帐期
	var currcycle = $('#CURRENT_CYCLE').val();//当前帐期
	if(currcycle != '' && startcycle == currcycle){
		$('#DATA4').val('205012');
		$("#DATA4").attr("disabled",false)
	}
	if(startcycle != currcycle){
		$("#DATA4").attr("disabled",false)
	}	
}

function selectWlanInfo(){
	var serialNumber = $('#SERIAL_NUMBER').val();
	if(serialNumber == null || serialNumber ==''){
		alert('请输入付费号码!');
		return false;
	}
	
	var allow_brank_code = $('#ALLOW_BRANK_CODE').val();
	var batchOperCode = $('#BATCH_OPER_TYPE').val();
	
	$.beginPageLoading('正在查询付费信息请稍后');
	$.ajax.submit('this','queryInfos','&SERIAL_NUMBER='+serialNumber + '&ALLOW_BRANK_CODE=' +allow_brank_code,'validInputPart,newAcctInfoPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function concatString(){
		
	 var acctId = $('#ACCT_ID').val();//帐户ID
	 var payItem = $('#DATA5').val();//付费科目
	 var custId = $('#CUST_ID').val();
	 var userId = $('#USER_ID').val();
	 var serialNumber = $('#SERIAL_NUMBER').val();
	
	 var custName = $('#MEM_CUST_NAME').val();//客户名称
	 var acctName = $('#PAY_NAME').val();//帐户名称
	 var payModeCode = $('#PAY_MODE_CODE').val();//帐户类别
	 var startCycle = $('#DATA3').val();//生效帐期
	 var endCycle = $('#DATA4').val();//结束帐期
	 var feeType = $('#FEE_TYPE').val();//费用类别
	 var data6 = $('#DATA6').val();//限定方式
	 var data7 = $('#DATA7').val();//限定值
	 var operType = $('#OPER_TYPE');
	 var crmSmsOrder=$('#crmSmsOrder').val();//下发订购短信提醒
	 var acctSmsOrder=$('#acctSmsOrder').val();//下发月初话费提醒短信
	
	 var newd = $.DataMap();
	 
	 newd.put("ACCT_ID",acctId);
	 newd.put("CUST_ID",custId);
	 newd.put("USER_ID",userId);
	 newd.put("SERIAL_NUMBER",serialNumber);
	 newd.put("PAYITEM_CODE",payItem);
	 newd.put("MEM_CUST_NAME",custName);
	 newd.put("ACCT_NAME",acctName);
	 newd.put("PAY_MODE_CODE",payModeCode);
	 newd.put("START_CYCLE",startCycle);
	 
	 newd.put("FEE_TYPE",feeType);
	 newd.put("LIMIT_TYPE",data6);
	 newd.put("LIMIT7",parseInt(data7)*100);
	 newd.put("COMPLEMENT_TAG","0");
	 newd.put('crmSmsOrder',crmSmsOrder);
	 newd.put('acctSmsOrder',acctSmsOrder);
	
 	 newd.put("OPER_TYPE","1");
 	 
 	 if(endCycle != '205012'){
	 	var yearnumber = parseInt(endCycle.substring(0, 4));
	 	var monthnumber = endCycle.substring(4);

	 	monthnumber = monthnumber -1;

	 	if(monthnumber ==0){
	 	   yearnumber = yearnumber -1;
	 	   monthnumber =12;
	 	}
	 	if(monthnumber<10)
	 	   endCycle = ""+yearnumber+"0"+monthnumber;
	 	else
	 	   endCycle = ""+yearnumber+monthnumber; 
 	 } 
	
	 newd.put("END_CYCLE",endCycle);
	 
	 
	 var allowBrankCode = $('#ALLOW_BRANK_CODE').val();
	 if(allowBrankCode != null && allowBrankCode != ""){
	 	newd.put("ALLOW_BRANK_CODE",allowBrankCode);
	 }
	 
	 //add by chenzg@20180706 REQ201804280001集团合同管理界面优化需求
	 if($("#MEB_VOUCHER_FILE_LIST")){
		 var voucherFileList = $("#MEB_VOUCHER_FILE_LIST").val();
		 if(voucherFileList == ""){
			 alert("请上传凭证信息!");
			 return false;
		 }else{
			 newd.put("MEB_VOUCHER_FILE_LIST", voucherFileList);
		 }
	 }
	 if($("#AUDIT_STAFF_ID")){
		 var auditStaffId = $("#AUDIT_STAFF_ID").val();
		 if(auditStaffId == ""){
			 alert("请选择稽核员!");
			 return false;
		 }else{
			 newd.put("AUDIT_STAFF_ID", auditStaffId);
		 }
	 }
	 //alert(newd);
	 //$.setReturnValue({'POP_CODING_STR':"账户ID:"+acctId+" 付费帐目ID:"+payItem},false);
 	 //$.setReturnValue({'CODING_STR':newd},true);
 	 
 	 parent.$('#POP_CODING_STR').val("账户ID:"+acctId+" 付费帐目ID:"+payItem);
	 parent.$('#CODING_STR').val(newd);
 	
	 parent.hiddenPopupPageGrp();
}


function Changeinput(opt) {
   
	if(opt.checked) {
		$('#endAcctId').css("display","");
		$("#DATA4").attr("disabled",false)
	}
	else {
		$('#endAcctId').css("display","none");
   	}
}

function checkSelectMesInfo(obj){
	if(obj){
		var check = obj.checked;
		if(check == true){
			obj.value = "1";//需要发短信提醒
		} else {
			obj.value = "0";
		}
	}
}
