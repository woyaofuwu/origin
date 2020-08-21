

/**提示信息*/
function showMessageBox(mess, mb){
	MessageBox.show({
	           title:'系统提示：',
	           msg: mess,
	           buttons: MessageBox.OK  
	       });
} 
    
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


function changelimit(){

	var limittype = $('#LIMIT_TYPE').val();
	if(limittype =='0'){
		$('#LIMIT').val('');//不限定时清楚已有值
		$('#LIMIT').attr("disabled",true);
	}
	else{
		$('#LIMIT').attr("disabled",false);
	}
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
		        
		        $('#cond_CHECK_SN_HIDDEN').val('0'); 
		         
			},
			function(error_code,error_info,derror)
			{
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    }
		 );
}



//填充账户详细
function insertAcctDescInfo(data) {
	$('#acct_PAY_NAME').val(data.get('PAY_NAME'));
}

//输入成员服务号码查询成员信息
function checkAdvChgNewSn()
{
	$.beginPageLoading();
	var newsn = $('#cond_SERIAL_NUMBER').val();

	var group_id = $('#GROUP_ID').val();
	if(group_id ==null || trim(group_id) ==''){
		alert("请先输入集团编码查询!");
		$.endPageLoading();
		return;
	}
	
	var acctId = $('#acct_ACCT_ID').val();
	var grpCityCode = $('#acctInfo_CITY_CODE').val();
	if(acctId ==null || trim(acctId) ==''){
		alert("请先选择集团账户!");
		$.endPageLoading();
		return;
	}
	//分散账期修改  清空页面中的值
	//clearValue();
	
	$.ajax.submit('mebArea','getAdvChgNewSnInfo','&MEM_SERIAL_NUMBER='+newsn+'&GROUP_ID='+group_id + '&GROUP_ACCT_CITY_CODE=' + grpCityCode ,'createMebArea',
			function(data)
		    {
		    	//校验规则
				var params = '&GRP_CUST_ID='+$("#CUST_ID").val()
							+'&SERIAL_NUMBER='+newsn
							+'&ACCT_ID='+$("#mem_ACCT_ID").val()
							+'&USER_ID='+$("#USER_ID").val()
							+'&GRP_ACCT_ID='+$('#acct_ACCT_ID').val();
		    	var checkResult = ruleCheck('com.asiainfo.veris.crm.order.web.group.rules.PayRelaAdvChgRule','checkRules', params);
		        $.endPageLoading();
		        
		        if(checkResult == false){
		        	//清空值
		        	$('#cond_SERIAL_NUMBER').val('');
		        	$('#cond_CHECK_SN_HIDDEN').val('');
		        	return;
		        }
		        var x_resultcode = data.get("x_resultcode","0");
		        if(x_resultcode == '-1'){
		      	  ifcheck= false;
		      	  alert(data.get("x_resultinfo"));
		      		return;
		         } 
		         
		        //判断集团与成员用户的业务区
				var tipMsg = data.get("TIP_MSG");
				if(tipMsg != null && tipMsg != '')
				{
					alert(tipMsg);
					return;
				}
				else
				{
					$('#cond_CHECK_SN_HIDDEN').val('1');
				}
				
		        var acctDayDistributton = data.get("USER_ACCTDAY_DISTRIBUTION");
		        
		        //非自然月和当前有账期变更的不允许办集团付费关系变更业务
		        if(acctDayDistributton =='2' || acctDayDistributton =='3'){
		        	alert("用户存在未生效的账期变更业务,当前账期内不允许办理该集团业务,请在[" + data.get("FIRST_DAY_NEXTACCT","") + "]号之后进行账期变更为自然月后再来办理!");
					$('#cond_SERIAL_NUMBER').val('');
					return;
		        }
		        if(acctDayDistributton =='4'){
		       		if(confirm("用户存在非自然月账期,办理该业务必须要求自然月账期,是否确认变更!")){
		       			$('#changeAcctDay').click();
		       			$('#cond_SERIAL_NUMBER').val('');
		       			return;
		       		}
		       	}
		       	
		       	//预约情况,从非自然月变更到自然月的
				if(acctDayDistributton == "1"){
					alert("注意:成员办理此业务为预约业务,预约生效时间[" + data.get("FIRST_DAY_NEXTACCT","") + "]!")
					//userAcctDayMap.put("IF_BOOKING","true");
					//$('#IF_BOOKING').val('true');
				}
				
				$('#cond_SERIAL_NUMBER_HIDDEN').val($('#cond_SERIAL_NUMBER').val());
			},
			function(error_code,error_info,derror)
			{
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    }
		 );
		 
	
}


function addTopTabset()
{  
	/*var tag=$('#tag').val();  
	var payinfodata = $.DataMap(tag);
	var desctag = payinfodata.get('TAG');
	var complementtag = payinfodata.get('COMPLEMENT_TAG');
	var checkalltag = payinfodata.get('CHECK_ALL_TAG');
	 
	if(desctag == 0){
		$('#select_acct_info').css('display','none');
		$('#RefreshTable').css('display','none');
		$('#FEE_TYPE').val(0);
		$('#LIMIT_TYPE')[0].disabled=true;
		$('#LIMIT')[0].disabled=true;
		$('#IS_COMPLEMENT')[0].disabled=true;
		//$("#IS_CHECK_ALL").disabled=true;
		$('#chktag').val('0');	
	}else{
		$('#chktag').val('1');	
		$('#select_acct_info').css('display','');
		$('#RefreshTable').css('display','');
		var isneed = $('#NEED_CHANGE');
		isneed[0].checked=true;
		
		var limittype = $('#LIMIT_TYPE').val(); 
		if(limittype==0){
			$('#FEE_TYPE').val(0);
			$('#LIMIT_TYPE')[0].disabled=true;
			$('#LIMIT')[0].disabled=true;
			$('#IS_COMPLEMENT')[0].disabled=true;
		}else{
			$('#FEE_TYPE').val(1);  
			if($('#IS_COMPLEMENT').val()==1){				
				$('#IS_COMPLEMENT')[0].checked=true;
			}
		}
		if (checkalltag=='-1'){
			$('#IS_CHECK_ALL')[0].checked=true;
		}
	}*/
	
	var chks=$('#RefreshTable input[type=Checkbox]');
	alert(chks); 
	chks.each(function(){  
		if($(this).attr("chk")=="1") 
			$(this).checked=true;
		}) ;
}

function onSubmitBaseTradeCheck(){
	if(!$.validate.verifyAll("tabPart")) 
		return false;
	
	var acct_id =  $('#acct_ACCT_ID').val();
	
	if(null == acct_id || acct_id =='') {
		alert("请选择集团帐户后，再进行此操作！");
		return false;
	}
	
	var user_id = $('#USER_ID').val();
	if(null == user_id || user_id == ''){
	  	alert("请输入服务号码,点击查询！");
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
		alert("充值比例为："+limit.val());
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
	
	if($('#MEB_VOUCHER_FILE_LIST') && $('#MEB_VOUCHER_FILE_LIST').val()==""){
		alert("请上传凭证信息！");
		mytab.switchTo("凭证信息");
		return false;
	}
	if($('#AUDIT_STAFF_ID') && $('#AUDIT_STAFF_ID').val()==""){
		alert("请选择稽核员！");
		mytab.switchTo("凭证信息");
		return false;
	}
	
	var tt= $('#newSnInfo_CheckAll').attr('checked');
	
	if(!$('#newSnInfo_CheckAll').attr('checked'))
		$('#newSnInfo_CheckAll').val('')
		
	var selected = getCheckedValues('itemcodes');
	
	$('#selectItemcodes').val(selected);
	
	var hidden_serial_number = $('#cond_SERIAL_NUMBER_HIDDEN').val();
   	if(hidden_serial_number == ""  || $('#cond_SERIAL_NUMBER').val() =="" || hidden_serial_number != $('#cond_SERIAL_NUMBER').val()){
   		alert("服务号码未检验,请先点击【检验】按钮进行校验!");
   		return false;
   	}
   	
    var checkSn = $('#cond_CHECK_SN_HIDDEN').val();
    if(checkSn == null || checkSn != '1')
    {
   		alert('请重新校验成员号码!');
   		return false;
    }
   	
	return true;
}

/**
 * 分散账期修改
 * 新增方法：分散账期中处理账期的开始账期和结束账期
 * 
 * @param {} obj 账期标志(开始账期或结束账期)
 */
function getCycleId(obj){
	
	if('START_CYCLE_ID' == obj){
		var startCycleIdObj = $("#newSnInfo_START_CYCLE_ID");
		var startCycleId = startCycleIdObj.val();
		var userAcctDay = new Wade.DataMap($("#USER_ACCT_DAY").val());
		var ifBooking = $("#IF_BOOKING").val();
		//预约情况
		if('true' == ifBooking){
			startCycleId = getFirstCycleThisAcct(strConvert(startCycleId),userAcctDay.get("NEXT_ACCT_DAY"),
			                      userAcctDay.get("NEXT_FIRST_DATE"),userAcctDay.get("NEXT_START_DATE"));
			var firstDayNextAcct = userAcctDay.get("FIRST_DAY_NEXTACCT");
			startCycleId = startCycleId > firstDayNextAcct ? startCycleId : firstDayNextAcct ;
			startCycleId =  strConvert(startCycleId); 
		}else{
			
			startCycleId = getFirstCycleThisAcct(strConvert(startCycleId),userAcctDay.get("ACCT_DAY"),
			                      userAcctDay.get("FIRST_DATE"),userAcctDay.get("START_DATE"));
			
			startCycleId =  strConvert(startCycleId); 
			
		}
		
		startCycleIdObj.val(startCycleId);
	}else if('END_CYCLE_ID' == obj){
		var endCycleIdObj = $("#newSnInfo_END_CYCLE_ID");
		var endCycleId = endCycleIdObj.val();
		endCycleId = endCycleId.substring(0,6) + 
			             new Date(endCycleId.substring(0,4),endCycleId.substring(4,6),0).getDate();
		
		endCycleIdObj.val(endCycleId);
	}
}

/*高级付费关系变更，提交时的前台校验*/
function checkAdvChgSubmit()
{
	var day = new Date();
   	var CurrentDate = "";
  	var Year = day.getFullYear();
   	var Month = day.getMonth()+1;
   	CurrentDate += Year;

   	if (Month >= 10 ){
    	CurrentDate += Month;
   	}
   	else{
   		CurrentDate += "0" + Month;
   	}
   	
	var acct_id =  $('#acct_ACCT_ID').val();
	alert(acct_id);
	if(null == acct_id || acct_id =='') {
		alert("请选择集团帐户后，再进行此操作！");
		return false;
	}
	var newsn = $('#cond_SERIAL_NUMBER').val();
	if( null == newsn || newsn == '') {
		alert("请输入付费客户信息！");
		return false;
	}
	
	//分散账期修改 start add
   	var hidden_serial_number = $('#cond_SERIAL_NUMBER').val()
   	if(!hidden_serial_number || hidden_serial_number == "" || hidden_serial_number != $('#newSnInfo_SERIAL_NUMBER').val()){
   		alert("服务号码未检验,请先点击【检验】按钮进行校验!");
   		return false;
   	}
   	//add end
	var start_cycle_id = $('#newSnInfo_START_CYCLE_ID').val();
	var end_cycle_id = $('#newSnInfo_END_CYCLE_ID').val();
	if( null == start_cycle_id || start_cycle_id == '') {
		alert("生效账期不能为空！");
		return false;
	}
	if( null == end_cycle_id || start_cycle_id == '') {
		alert("结束账期不能为空！");
		return false;
	}
	if( CurrentDate > start_cycle_id) {
		alert("生效账期不能小于当前账期！");
		return false;
	}
	if( CurrentDate > end_cycle_id ) {
		alert("结束账期不能小于当前账期！");
		return false;
	}
	if( start_cycle_id >= end_cycle_id) {
		alert("结束账期不能小于或等于生效账期！");
		return false;
	}
	var newSnInfo = getCheckedBoxStr('newSnInfo_CheckAll','');
	var noteitem = getCheckedBoxStr('itemcodes','');
	if ((noteitem=='' && newSnInfo=='')) {
		alert("请选择综合账目！");
		return false;
	}
	var limit = $('#newSnInfo_LIMIT');
	var limittype = $('#newSnInfo_LIMIT_TYPE').val();
	var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/;

	if (!reg.test(limit.val()) && limit.val() !=''){
		alert('限定值必须为数字,请重新输入！');
		return false;
	}
	if (parseInt(limit.val()) < 0 && limit.val() !=''){
		alert("限定值不能小于0，请重新输入！");
		return false;
	}

	if(limittype=='2' && parseInt(limit.val()) > 100 && limit.val() !=''){
		alert("充值比例为："+limit.val());
		alert("限定方式为比例时，限定值不能大于100，请重新输入！");
		return false;
	}
	if(limittype=='1' && parseInt(limit.val()) > 500000 && limit.val() !=''){
		alert("限定方式为金额时，限定值不能大于500000，请重新输入！");
		return false;
	}
	if(limit && limit.val()=='')
	{
		MessageBox.confirm('确认信息','您没有输入限定值，如果确认提交，系统将保存默认值为0',dealMsgBox3);
	}
	else
	{
		submitValid();
	}
	
	return true;
}


/**
 * 分散账期修改
 * 新增方法：格式之间的相互转换YYYY-MM-DD与YYYYMMDD
 * 
 */
function strConvert(str){
	if(str.length == 8){
		return str = str.substring(0,4) + "-" + str.substring(4,6) + "-" + str.substring(6,8);
	}else if(str.length == 10){
		return str.substring(0,4) + str.substring(5,7) +  str.substring(8,10);
	}else{
		return str;
	}
}

/**
 * 分散账期修改
 * 新增方法：获取当前时间nowDate所在账期的开始时间
 * @param {} nowDate 当前时间
 * @param {} acctDay 结账日
 * @param {} firstDate 首次结账日
 * @param {} startDate 开始时间
 * @return {} 当前时间所在账期的开始时间
 */
function getFirstCycleThisAcct(nowDate,acctDay,firstDate,startDate){
	var nowDate = parseDate(nowDate);
	var firstDate = parseDate(firstDate);
	var startDate = parseDate(startDate);
	var tempDate = nowDate;
	tempDate.setDate(acctDay);
	if(tempDate > nowDate){
		tempDate.setMonth(tempDate.getMonth() - 1);
	}
	if(tempDate < firstDate){
		tempDate = startDate;
	}
	return formatDate(tempDate);
}

function parseDate(date){
	var d = date.toDate('yyyy-MM-dd')
	return d;
}

function formatDate(date){
	var dateString = date.format('yyyy-MM-dd');
	return dateString;
}
//下发订购短信提醒 修改
function checkCrmSmsOrder(){
	var crmSmsOrderOjb = $('#crmSmsOrder');
	if(crmSmsOrderOjb.checked==true){
		crmSmsOrderOjb.val("1");
	}else{
		crmSmsOrderOjb.val("0");
	}
	alert(crmSmsOrderOjb.val());
}
//下发月初话费提醒短信 修改
function checkAcctSmsOrder(){
	var acctSmsOrder = $('#acctSmsOrder');
	if(acctSmsOrder.checked==true){
		acctSmsOrder.val("1");
	}else{
		acctSmsOrder.val("0");
	}
	alert(acctSmsOrder.val());
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
