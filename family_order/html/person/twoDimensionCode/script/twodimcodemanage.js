/**
 * 管理、查询二维
 * @author dengshu
 * @date   2014年5月21日-下午4:53:25
 */
function reset(){
	$('#submit_part :input').val('');
}
/**
 * 打开二维码生成窗口
 */
function addTwoDimCode(){
	  openNav('新增二维码','twoDimension.TwoDimCodeAdd', 'addInit');
//	 popupPage ('twoDimension.TwoDimCodeAdd','addInit','', '生成二维码', '420', '700');
//	alert(guidValue);
}

/**
 * 查看二维码
 */
function showTwoDimCode(){
	var table = $.table.get("DeptTable");
	if(table.getSelected('codeId')==null){
		MessageBox.alert('请选择数据','请选择数据后再做修改操作');
		return;
	}
	var fileId = table.getSelected('fileId').html();
	 popupPage ('twoDimension.TwoDimCodeImg','imgInit','&FILE_ID='+fileId, '二维码图片', '235', '200');
//	alert(guidValue);
}

/**
 * 修改二维码信息
 */
function  modifyTwoDimCode(){
	var table = $.table.get("DeptTable");
	if(table.getSelected('codeId')==null){
		MessageBox.alert('请选择数据','请选择数据后再做修改操作');
		return;
	}
	var codeState = table.getSelected('codeState').html();
	//待审批和审批不通过状态才允许修改
	if(codeState!=0&&codeState!=2){
		MessageBox.alert('不允许修改二维码','\'审批通过\'或者\'发布\'状态下不允许修改操作');
		return;
	}
	
	var codeId = table.getSelected('codeId').html();
//	debugger;
	openNav('修改二维码','twoDimension.TwoDimCodeAdd','modifyInit','BARCODE_ID='+codeId);
}


/**
 * 审批二维码
 */
function  approvalTwoDimCode(){
	var table = $.table.get("DeptTable");
	if(table.getSelected('codeId')==null){
		MessageBox.alert('请选择数据','请选择数据后再做修改操作');
		return;
	}
	var codeState = table.getSelected('codeState').html();
	//待审批和审批不通过状态才允许修改
	if(codeState!=0&&codeState!=2){
		MessageBox.alert('提示','\'审批通过\'或者\'发布\'状态下不能重复操作！');
		return;
	}
	var codeId = table.getSelected('codeId').html();
//	debugger;
	openNav('审批二维码','twoDimension.TwoDimCodeApproval','modifyInit','BARCODE_ID='+codeId);
}


/**
 * 删除二维码信息
 */
function  deleteTwoDimCode(){
	var table = $.table.get("DeptTable");
	if(table.getSelected('codeId')==null){
		MessageBox.alert('请选择数据','请选择数据后再做删除操作');
		return;
	}
	var codeState = table.getSelected('codeState').html();
	//待审批和审批不通过状态才允许修改
	if(codeState!=0&&codeState!=2){
		MessageBox.alert('不允许修改二维码','\'审批通过\'状态下不允许删除操作');
		return;
	}
	var codeId = table.getSelected('codeId').html();
//	debugger;
	$.beginPageLoading("正在删除二维码数据...");
	
	$.ajax.submit('', "deleteTwoDimCode", 'BARCODE_ID='+codeId, 'result_Table', function(data){
			$.endPageLoading();
			$("#queryBtn").click();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}

/**
 * 查询二维码
 * @param checkFunc
 * @param listenName
 * @returns {Boolean}
 */
function sundryQuery(checkFunc,listenName){
	//查询条件校验
	if(!verifyAll()) {
		return false;
	}
	//校验起始日期范围
	if(checkFunc!=null&&checkFunc!='' ){
		//各业务特殊检查
		if(!eval(checkFunc)()){
			return false;
		};
	}
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', listenName, null, 'result_Table', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}


/**
 * 校验起始日期范围不超过31天
 */
function checkDateRange31(obj){
	//校验起始日期范围
	if(!checkDateRange('cond_START_DATE', 'cond_END_DATE', 31)){
		return false;
	}
	return true
}



/*修改结束日期，开始日期提前一个月*/
function chgEndDateSynStartDate(endObj, startId){
	var endDate = endObj.value;
	if(endObj.value == '' || !verifyField(endObj)){//仅在格式满足的情况下才执行该操作
		endObj.select();
		return;
	}
	if($("#"+startId).val()!=''){
		return;
	}
	var dateArr = endDate.split("-");
	
	var edate = new Date(dateArr[0], parseInt(dateArr[1]-1), dateArr[2]);
	var sdate = new Date(edate.getTime()-(1000*60*60*24*30))
	//月份补0
	var month =(sdate.getMonth()+1);
	if((sdate.getMonth()+1)<10){
		month = '0'+(sdate.getMonth()+1);
	}
	var date = edate.getDate();
	if(edate.getDate()<10){
		date = '0'+edate.getDate();
	}
	sdateStr = sdate.getFullYear()+"-"+month+"-"+date;
	$("#"+startId).val( sdateStr);
}
