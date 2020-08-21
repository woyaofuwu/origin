$(function(){
	initGroupInfo();
	initAcctInfo();
	// 选择日期后，将 calendar 的返回给 datefield，并关闭 popup
	$("#myCalendar").select(function(e){
		if(activeDateField && activeDateField.nodeType){
			$(activeDateField).val(strToyyyyMMdd(this.val()));
			if($(activeDateField).attr('id').indexOf('START_CYCLE_ID')!=-1){
				getCycleId('START_CYCLE_ID');
			}else{
				getCycleId('END_CYCLE_ID');
			}
			backPopup(e.target);
		}
	});
})
function dataConvert(obj){
	$(obj).val(strToyyyyMMdd($(obj).val()));
}
function strToyyyyMMdd(str){
	if(str.length == 10){
		return str.substring(0,4) + str.substring(5,7) +  str.substring(8,10);
	}else{
		return str;
	}
}
//保存提交集团信息
function initGroupInfo(){
	if (!ifGetEc()) {
		return;
	}
	var custInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	$("#CUST_ID").val(custInfo.get("CUST_ID"));
	$("#CUST_NAME").val(custInfo.get("CUST_NAME"));
	$("#GROUP_ID").val(custInfo.get("GROUP_ID"));
}
function initAcctInfo(){
	if (!ifGetEc()) {
		return;
	}
	var custInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	var loginCustId = custInfo.get("CUST_ID");
	var groupId = custInfo.get("GROUP_ID");
	$.beginPageLoading('加载中...');
	$.ajax.submit(this,'getGroupBaseInfo','&CUST_ID='+loginCustId+'&GROUP_ID='+groupId,'tabPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

//建立一个变量存储当前 datefield
var activeDateField;

// 打开 popup 的同时，将 datefield 的值赋给 calendar
function setCalendarValue(dateField){
	var el = dateField && dateField.nodeType == 1 ? dateField : document.getElementById(dateField);
	if(!el) return;
	myCalendar.val(el.value);
	activeDateField = el;
}

function showMyDialog(el){
	$.beginPageLoading('加载中...');
	var noteItemCode=$(el).attr("note_item_code");
	var noteItem=$(el).attr("note_item");
	$.ajax.submit(this,'queryPayrelaAdvChgDetItem','&PARENT_NOTE_ITEM_CODE='+noteItemCode+'&PARENT_NOTE_ITEM='+noteItemCode,'refreshtable',function(data){
		$.endPageLoading();
		mydialog.show();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}



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
	//var display =  $('#acct_ACCT_ID').find("option:selected").text();
	var display =  $('#acct_ACCT_ID').text();
	
	$.beginPageLoading();
	$.ajax.submit('acctArea','queryAcctInfo','&ACCT_ID='+acctId,'createArea2',
			function(data)
		    {
		        $.endPageLoading(); 
		        var x_resultcode = data.get("x_resultcode","0");
		        if(x_resultcode == '-1'){
		      	  ifcheck= false;
		      	  //alert(data.get("x_resultinfo"));
		      	  MessageBox.error("错误信息", data.get("x_resultinfo"));
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

	//var group_id = $('#GROUP_ID').val();
	var group_id = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("GROUP_ID");
	if(group_id ==null || trim(group_id) ==''){
		//alert("请先输入集团编码查询!");
		MessageBox.error("错误信息", "请先输入集团编码查询!");
		$.endPageLoading();
		return;
	}
	
	var acctId = $('#acct_ACCT_ID').val();
	var grpCityCode = $('#acctInfo_CITY_CODE').val();
	if(acctId ==null || trim(acctId) ==''){
		//alert("请先选择集团账户!");
		MessageBox.error("错误信息", "请先选择集团账户!");
		$.endPageLoading();
		return;
	}
	
	if(newsn ==null || trim(newsn) ==''){
		//alert("请先选择集团账户!");
		MessageBox.error("错误信息", "请填写成员服务号码!");
		$.endPageLoading();
		return;
	}
	//分散账期修改  清空页面中的值
	//clearValue();
	
	$.ajax.submit('mebArea','getAdvChgNewSnInfo','&MEM_SERIAL_NUMBER='+newsn+'&GROUP_ID='+group_id + '&GROUP_ACCT_CITY_CODE=' + grpCityCode ,'createMebArea',
			function(data)
		    {
		    	//校验规则
		    	var checkResult = ruleCheck('com.asiainfo.veris.crm.order.web.group.rules.PayRelaAdvChgRule','checkRules','&GRP_CUST_ID='+$("#CUST_ID").val()+'&SERIAL_NUMBER='+newsn+'&ACCT_ID='+$("#mem_ACCT_ID").val()+'&USER_ID='+$("#USER_ID").val()+'&GRP_ACCT_ID='+$('#acct_ACCT_ID').val());
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
		      	  //alert(data.get("x_resultinfo"));
		      	  MessageBox.error("错误信息", data.get("x_resultinfo"));
		      		return;
		         } 
		         
		        //判断集团与成员用户的业务区
				var tipMsg = data.get("TIP_MSG");
				if(tipMsg != null && tipMsg != '')
				{
					//alert(tipMsg);
					MessageBox.error("错误信息", tipMsg);
					return;
				}
				else
				{
					$('#cond_CHECK_SN_HIDDEN').val('1');
				}
				
		        var acctDayDistributton = data.get("USER_ACCTDAY_DISTRIBUTION");
		        
		        //非自然月和当前有账期变更的不允许办集团付费关系变更业务
		        if(acctDayDistributton =='2' || acctDayDistributton =='3'){
		        	//alert("用户存在未生效的账期变更业务,当前账期内不允许办理该集团业务,请在[" + data.get("FIRST_DAY_NEXTACCT","") + "]号之后进行账期变更为自然月后再来办理!");
		        	MessageBox.error("错误信息", "用户存在未生效的账期变更业务,当前账期内不允许办理该集团业务,请在[" + data.get("FIRST_DAY_NEXTACCT","") + "]号之后进行账期变更为自然月后再来办理!");
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
					//alert("注意:成员办理此业务为预约业务,预约生效时间[" + data.get("FIRST_DAY_NEXTACCT","") + "]!")
					MessageBox.error("错误信息", "注意:成员办理此业务为预约业务,预约生效时间[" + data.get("FIRST_DAY_NEXTACCT","") + "]!");
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
	//alert(chks); 
	chks.each(function(){  
		if($(this).attr("chk")=="1") 
			$(this).checked=true;
		}) ;
}

function onSubmitBaseTradeCheck(){
	if($("#paycustinfotabset").css("display")=='none'){
		if($("#cond_SERIAL_NUMBER").val()==''||$("#cond_SERIAL_NUMBER").val()==null){
			MessageBox.error("错误信息", "成员服务号码不能为空！");
			return;
		}
	}
	
	if(!$.validate.verifyAll("tabPart")) 
		return false;
	
	var acct_id =  $('#acct_ACCT_ID').val();
	
	if(null == acct_id || acct_id =='') {
		//alert("请选择集团帐户后，再进行此操作！");
		MessageBox.error("错误信息", "请选择集团帐户后，再进行此操作！");
		return false;
	}
	
	var user_id = $('#USER_ID').val();
	if(null == user_id || user_id == ''){
	  	//alert("请输入服务号码,点击查询！");
	  	MessageBox.error("错误信息", "请输入服务号码,点击查询！");
		return false;
	}
	
	var noteitem = getCheckedBoxNum('itemcodes');
	if ((noteitem==0)) {
		//alert("请输入集团编码后选择综合账目！");
		MessageBox.error("错误信息", "请输入集团编码后选择综合账目！");
		return false;
	}
	var limit = $('#LIMIT').val();
	var limittype = $('#LIMIT_TYPE').val();
	var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/;

	if (!reg.test(limit) && limit !=''){
		//alert('限定值必须为数字,请重新输入！');
		MessageBox.error("错误信息", "限定值必须为数字,请重新输入！");
		return false;
	}
	if (parseInt(limit) < 0 && limit !=''){
		//alert("限定值不能小于0，请重新输入！");
		MessageBox.error("错误信息", "限定值不能小于0，请重新输入！");
		return false;
	}

	if(limittype=='2' && parseInt(limit) > 100 && limit !=''){
		//alert("充值比例为："+limit.val());
		//alert("限定方式为比例时，限定值不能大于100，请重新输入！");
		MessageBox.error("错误信息", "限定方式为比例时，限定值不能大于100，请重新输入！<br>充值比例为："+limit.val());
		return false;
	}
	if(limittype=='1' && parseInt(limit) > 500000 && limit !=''){
		//alert("限定方式为金额时，限定值不能大于500000，请重新输入！");
		MessageBox.error("错误信息", "限定方式为金额时，限定值不能大于500000，请重新输入！");
		return false;
	}
	if(limit && limit=='')
	{
		MessageBox.confirm('确认信息','您没有输入限定值，如果确认提交，系统将保存默认值为0',dealMsgBox3);
	}
	
	var tt= $('#newSnInfo_CheckAll').attr('checked');
	
	if(!$('#newSnInfo_CheckAll').attr('checked'))
		$('#newSnInfo_CheckAll').val('')
		
	var selected = getCheckedValues('itemcodes');
	
	$('#selectItemcodes').val(selected);
	
	//根据31676一级综合帐目编码进行特殊处理
	var selected = getCheckedValues('itemcodes');
	if(selected!=null&&selected!=""&& !tt){
		var arr_selected = new Array;
		arr_selected = selected.split(",");
		if(arr_selected!=null&&typeof(arr_selected)!="undefined"&&arr_selected.length>1){
			for(var k=0;k<arr_selected.length;k++){
				if("31676" == arr_selected[k]){
					MessageBox.error("JTZ(除去月租、短信、GPRS)账目不能和其他账目一起混选");
					return false;
				}
			}
		}
		
	}
	
	var hidden_serial_number = $('#cond_SERIAL_NUMBER_HIDDEN').val();
   	if(hidden_serial_number == ""  || $('#cond_SERIAL_NUMBER').val() =="" || hidden_serial_number != $('#cond_SERIAL_NUMBER').val()){
   		//alert("服务号码未检验,请先点击【检验】按钮进行校验!");
   		MessageBox.error("错误信息", "服务号码未检验,请先点击【检验】按钮进行校验!");
   		return false;
   	}
   	
    var checkSn = $('#cond_CHECK_SN_HIDDEN').val();
    if(checkSn == null || checkSn != '1')
    {
   		//alert('请重新校验成员号码!');
   		MessageBox.error("错误信息", "请重新校验成员号码!");
   		return false;
    }
    
    var staffId = $('#AUDIT_STAFF_ID').val();
    if(staffId == null || staffId == '')
    {
    	MessageBox.error("错误信息", "请选择稽核人员!");
   		return false;
    }
    var fileList = $('#MEB_VOUCHER_FILE_LIST').val();
    if(fileList == null || fileList == '')
    {
    	MessageBox.error("错误信息", "请上传凭证信息!");
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
	//alert(acct_id);
	if(null == acct_id || acct_id =='') {
		//alert("请选择集团帐户后，再进行此操作！");
		MessageBox.error("错误信息", "请选择集团帐户后，再进行此操作！");
		return false;
	}
	var newsn = $('#cond_SERIAL_NUMBER').val();
	if( null == newsn || newsn == '') {
		//alert("请输入付费客户信息！");
		MessageBox.error("错误信息", "请输入付费客户信息！");
		return false;
	}
	
	//分散账期修改 start add
   	var hidden_serial_number = $('#cond_SERIAL_NUMBER').val()
   	if(!hidden_serial_number || hidden_serial_number == "" || hidden_serial_number != $('#newSnInfo_SERIAL_NUMBER').val()){
   		//alert("服务号码未检验,请先点击【检验】按钮进行校验!");
   		MessageBox.error("错误信息", "服务号码未检验,请先点击【检验】按钮进行校验!");
   		return false;
   	}
   	//add end
	var start_cycle_id = $('#newSnInfo_START_CYCLE_ID').val();
	var end_cycle_id = $('#newSnInfo_END_CYCLE_ID').val();
	if( null == start_cycle_id || start_cycle_id == '') {
		//alert("生效账期不能为空！");
		MessageBox.error("错误信息", "生效账期不能为空！");
		return false;
	}
	if( null == end_cycle_id || start_cycle_id == '') {
		//alert("结束账期不能为空！");
		MessageBox.error("错误信息", "结束账期不能为空！");
		return false;
	}
	if( CurrentDate > start_cycle_id) {
		//alert("生效账期不能小于当前账期！");
		MessageBox.error("错误信息", "生效账期不能小于当前账期！");
		return false;
	}
	if( CurrentDate > end_cycle_id ) {
		//alert("结束账期不能小于当前账期！");
		MessageBox.error("错误信息", "结束账期不能小于当前账期！");
		return false;
	}
	if( start_cycle_id >= end_cycle_id) {
		//alert("结束账期不能小于或等于生效账期！");
		MessageBox.error("错误信息", "结束账期不能小于或等于生效账期！");
		return false;
	}
	var newSnInfo = getCheckedBoxStr('newSnInfo_CheckAll','');
	var noteitem = getCheckedBoxStr('itemcodes','');
	if ((noteitem=='' && newSnInfo=='')) {
		//alert("请选择综合账目！");
		MessageBox.error("错误信息", "请选择综合账目！");
		return false;
	}
	var limit = $('#newSnInfo_LIMIT');
	var limittype = $('#newSnInfo_LIMIT_TYPE').val();
	var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/;

	if (!reg.test(limit.val()) && limit.val() !=''){
		//alert('限定值必须为数字,请重新输入！');
		MessageBox.error("错误信息", "限定值必须为数字,请重新输入！");
		return false;
	}
	if (parseInt(limit.val()) < 0 && limit.val() !=''){
		//alert("限定值不能小于0，请重新输入！");
		MessageBox.error("错误信息", "限定值不能小于0，请重新输入！");
		return false;
	}

	if(limittype=='2' && parseInt(limit.val()) > 100 && limit.val() !=''){
		//alert("充值比例为："+limit.val());
		//alert("限定方式为比例时，限定值不能大于100，请重新输入！");
		MessageBox.error("错误信息", "限定方式为比例时，限定值不能大于100，请重新输入！<br>充值比例为："+limit.val());
		return false;
	}
	if(limittype=='1' && parseInt(limit.val()) > 500000 && limit.val() !=''){
		//alert("限定方式为金额时，限定值不能大于500000，请重新输入！");
		MessageBox.error("错误信息", "限定方式为金额时，限定值不能大于500000，请重新输入！");
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


/*************************REQ201810100001优化政企集中稽核相关功能需求 begin*************************/
function showAuditAddPopup(el) {
	showPopup('myPopup', 'auditPopupItem', true);
}
//稽核操作：选择稽核员
function selectAudit(el)
{
	$("#ecAccountULAudit li").each(function(){
		$(this).removeClass("checked");
	});
	$(el).addClass("checked");
	var auditId = $(el).attr("id");
	
	$("#AUDIT_STAFF_ID_TEXT").val(auditId);
	$("#AUDIT_STAFF_ID").val(auditId);
	/*var html = "<span class='text' >"+auditId+"</span>";
	
	html += "<span id='AUDIT_STAFF_ID' style='display:none'>"+auditId+"</span>";
	$("#i_auditSelPart .value").html(html);*/
	
	hidePopup('myPopup');
}
/**
 * 稽核信息拼装
 */
function saveAuditInfo() {
	var auditId = $("#AUDIT_STAFF_ID").html();
	var auditIdInfo = new Wade.DataMap();
	auditIdInfo.put("AUDIT_STAFF_ID", auditId);
	var fileList = $("#MEB_VOUCHER_FILE_LIST").val();
	if(fileList!=null&&fileList!=''){
		auditIdInfo.put("MEB_VOUCHER_FILE_LIST", fileList);
	}
	return auditIdInfo;
}



function selectProductAuditPopupItem(obj,auditNo,auditName){
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryAuditInfo", "&STAFF_ID="+auditNo+"&STAFF_NAME="+auditName, "productAuditPopupItem", function(data){

		$.endPageLoading();
		//手动刷新scroller组件
		/*editMainScroll.refresh();*/
		forwardPopup(obj, 'productAuditPopupItem');
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		
    });
}


//绑定上传组件清除按钮事件
function clearUpFile(){
	$("#UP_FILE_TEXT").val("");
	$("#MEB_VOUCHER_FILE_LIST").val("");
}
//多文件上传,绑定上传组件确定按钮事件
function okUpFile(){
	var obj = $("#FILE_UPLOAD").val();
	$("#UP_FILE_TEXT").val(obj.NAME);
	$("#UP_FILE_TEXT").attr('tip',obj.NAME);
	$("#MEB_VOUCHER_FILE_LIST").val(obj.ID);
	hidePopup('myPopup','UI-popup-upload');
}

/*************************REQ201810100001优化政企集中稽核相关功能需求  end*************************/
