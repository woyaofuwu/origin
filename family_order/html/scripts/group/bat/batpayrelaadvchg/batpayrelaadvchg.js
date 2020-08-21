    
/*综合帐目列表过滤*/
function filterNoteItems()
{
	var noteItem = $('#newSnInfo_NOTE_ITEM').val();
	
	$.beginPageLoading('正在帐目列表过滤...');
	$.ajax.submit(this,'filterNoteItems','&NOTE_ITEM='+noteItem,'NoteItemArea',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

 
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    $('#acctInfos').html('');
    $('#createArea').css("display","none");
    $("#bcreateTop").css("display","none");  //新增按钮

}

/**
用途：检查输入字符串是否符合金额格式 
格式定义为整数
*/
function checkMoney(obj){
	//var regu = "^[0-9]*(\.[0-9]{1,2})?$"; 
	var regu = "^[0-9]*$"; 
	var reg = new RegExp(regu); 
	if (reg.test(obj)) { 
		return true; 
	} else { 
		return false; 
	} 
}

function changelimit(){

	var limittype = $('#LIMIT_TYPE').val();

	if(limittype =='0'){
		$('#LIMIT').val('');
		$('#LIMIT').attr("disabled",true);
	}
	else{
		$('#LIMIT').attr("disabled",false);
	}
}

function dealAcctInfo(){
	//ajax
	var acctId = $('#acct_ACCT_ID').val();
	var custId = $('#CUST_ID').val();
	var display =  $('#acct_ACCT_ID').find("option:selected").text();
	
	$.beginPageLoading();
	$.ajax.submit('acctArea','queryAcctInfo','&ACCT_ID='+acctId,'createArea2',
			function(data)
		    {
		        $.endPageLoading(); 
		        var x_resultcode = data.get("x_resultcode","0");
		        if(x_resultcode == '-1'){
		      	  ifcheck= false;
		      	  alert(data.get("x_resultinfo"));
		      		return;
		         } 
		        
		        //塞值userid,productid等
		        var splitResult= display.split("|");
		        
		        $('#acctInfo_USER_ID').val(splitResult[4]);
		        $('#acctInfo_PRODUCT_ID').val(splitResult[2]);
		        $('#acctInfo_EPARCHY_CODE').val(splitResult[5]);
		        
			},
			function(error_code,error_info,derror)
			{
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    }
		 );
}

/*高级付费关系变更，提交时的前台校验*/
function checkAdvChgSubmit(){
	if(!$.validate.verifyAll("tabPart")) 
		return false;
	
	var acct_id =  $('#acct_ACCT_ID').val();
	
	if(null == acct_id || acct_id =='') {
		alert("请选择集团帐户后，再进行此操作！");
		return false;
	}
	
	var noteitem = getCheckedBoxNum('itemcodes');
	if ((noteitem==0)) {
		alert("请输入集团编码后选择综合账目！");
		return false;
	}
	var limit = $('#LIMIT').val();
	var limittype = $('#LIMIT_TYPE').val();
	var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/;

	if (!reg.test(limit) && limit !=''){
		alert('限定值必须为数字,请重新输入！');
		return false;
	}
	if (parseInt(limit) < 0 && limit !=''){
		alert("限定值不能小于0，请重新输入！");
		return false;
	}

	if(limittype=='2' && parseInt(limit) > 100 && limit !=''){
		alert("充值比例为："+limit.value);
		alert("限定方式为比例时，限定值不能大于100，请重新输入！");
		return false;
	}
	if(limittype=='1' && parseInt(limit) > 500000 && limit !=''){
		alert("限定方式为金额时，限定值不能大于500000，请重新输入！");
		return false;
	}
	if(limit && limit=='')
	{
		MessageBox.confirm('确认信息','您没有输入限定值，如果确认提交，系统将保存默认值为0',dealMsgBox3);
	}
	
	if($('#MEB_VOUCHER_FILE_LIST')&&$('#MEB_VOUCHER_FILE_LIST').val() == ""){
		alert("请上传凭证信息！");
		mytab.switchTo("凭证信息");
		return false;
	}
	if($('#AUDIT_STAFF_ID')&&$('#AUDIT_STAFF_ID').val() == ""){
		alert("请选择稽核员！");
		mytab.switchTo("凭证信息");
		return false;
	}
	
	var tt= $('#newSnInfo_CheckAll').attr('checked');
	
	if(!$('#newSnInfo_CheckAll').attr('checked'))
		$('#newSnInfo_CheckAll').val('')
		
	var selected = getCheckedValues('itemcodes');
	
	$('#selectItemcodes').val(selected);
	
	return true;
}


function concatString(){
    
    var newSnInfo = "";
    
    if($('#newSnInfo_CheckAll').attr('checked'))
	{
		newSnInfo = "0";
	}
	else
	{
		newSnInfo = "";
	}
	
	var itemcodes = getCheckedValues('itemcodes');
		
    var limit= "0";//限定值
		
	var limitObj = $('#LIMIT_TYPE');
	if(limitObj.disabled == false)
	{
		limit = limitObj.val();
	}else
	{
		limit = "0";
	}
	
	var flag = true;
	$.beginPageLoading();
	$.ajax.submit(this, 'GetPayItemCode','&CheckAll='+newSnInfo+'&itemcodes='+itemcodes, null,function(data){
			$.endPageLoading();
			$('#PAYITEM_CODE').val(data.get('PAYITEM_CODE'));
			commSubmit();
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
			flag = false;
		},{async:false});
	
	return flag;	
	
}

function commSubmit() {
	
	var groupId = $('#GROUP_ID').val();//集团客户编码
	var userIdA = $('#acctInfo_USER_ID').val();//用户标识
	var acctId = $('#acct_ACCT_ID').val();//帐户ID
	var payItemCode = $('#selectItemcodes').val();//付费账目编码selectItemcodes
	var start_cycle_id = $('#START_CYCLE_ID').val();//生效账期
	var end_cycle_id = $('#END_CYCLE_ID').val();//结束账期
	var noteItem=$('#newSnInfo_NOTE_ITEM').val();//综合帐目过滤
	var feeType=$('#FEE_TYPE').val();//费用类别
	var limittype = $('#LIMIT_TYPE').val();//限定方式		
	var limit=$('#LIMIT').val();//限定值
	//var complementTag="0"; //$('#COMPLEMENT_TAG').val();//是否补足
	var complementTag=$('#COMPLEMENT_TAG').val();//是否补足
	var crmSmsOrder=$('#crmSmsOrder').val();//下发订购短信提醒
	var acctSmsOrder=$('#acctSmsOrder').val();//下发月初话费提醒短信
	
	var tt= $('#newSnInfo_CheckAll').attr('checked');
	if(!$('#newSnInfo_CheckAll').attr('checked')){
		$('#newSnInfo_CheckAll').val('');
	}		
	var newSnInfo_CheckAll = $('#newSnInfo_CheckAll').val();//是否全选
	
	var newd = $.DataMap();
	newd.put('GROUP_ID',groupId);
	newd.put('USER_ID',userIdA);
	newd.put('ACCT_ID',acctId);
	newd.put('PAYITEM_CODE',payItemCode);
	newd.put('START_CYCLE_ID',start_cycle_id);
	newd.put('END_CYCLE_ID',end_cycle_id);
	newd.put('NOTE_ITEM',noteItem);
	newd.put('FEE_TYPE',feeType);
	newd.put('LIMIT_TYPE',limittype);
	newd.put('LIMIT',limit);
	newd.put('COMPLEMENT_TAG',complementTag);
	newd.put('newSnInfo_CheckAll',newSnInfo_CheckAll);
	newd.put('crmSmsOrder',crmSmsOrder);
	newd.put('acctSmsOrder',acctSmsOrder);
	if($('#MEB_VOUCHER_FILE_LIST')&&$('#MEB_VOUCHER_FILE_LIST').val() != ""){
		newd.put('MEB_VOUCHER_FILE_LIST',$('#MEB_VOUCHER_FILE_LIST').val());
	}
	if($('#AUDIT_STAFF_ID')&&$('#AUDIT_STAFF_ID').val() != ""){
		newd.put('AUDIT_STAFF_ID',$('#AUDIT_STAFF_ID').val());
	}
	//$.setReturnValue({'POP_CODING_STR':"账户ID::"+acctId+" 付费帐目ID:"+payItemCode+" 是否全选:"+newSnInfo_CheckAll+" 生效账期:"+start_cycle_id+" 结束账期:"+end_cycle_id},false);
 	//$.setReturnValue({'CODING_STR':newd},true);
 	
 	parent.$('#POP_CODING_STR').val("账户ID::"+acctId+" 付费帐目ID:"+payItemCode+" 是否全选:"+newSnInfo_CheckAll+" 生效账期:"+start_cycle_id+" 结束账期:"+end_cycle_id);
	parent.$('#CODING_STR').val(newd);
 	
	parent.hiddenPopupPageGrp();
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
