function queryUpayReconTotal(){
	//查询条件校验
	if(!$.validate.verifyAll("UpayReconCond")) {
		return false;
	}
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	if(startDate > getCurrDate()){
		MessageBox.alert("提示","开始时间不能大于当前时间！");
		return false;
	}
	if(startDate!=''&&endDate==''){
		MessageBox.alert("提示","请输入结束时间！");
		return false;
	}
	if(startDate==''&&endDate!=''){
		MessageBox.alert("提示","请输入开始时间！");
		return false;
	}
	if((startDate!=''&&endDate=='')||(startDate==''&&endDate!='')){
		MessageBox.alert("提示","查询！");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	 
	$.ajax.submit('UpayReconCond', 'queryUpayReconTotal', null, 'UpayReconList', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
    });
}

function queryUpayRecon(){
	var value=$('#DEAL_TAG').val();
	if('0'==value||'2'==value) {//0:未启动
	  	$('#startButton').css('display','');
	}else {//1:隐藏掉button
	  	$('#startButton').css('display','none');
	}
	//查询条件校验
	if(!$.validate.verifyAll("UpayReconCond")) {
		return false;
	}
	var startDate = $("#cond_START_DATE").val();
	if(startDate > getCurrDate()){
		MessageBox.alert("提示","开始时间不能大于当前时间！");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	 
	$.ajax.submit('UpayReconCond', 'queryUpayRecon', null, 'UpayReconList', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
    });
}
//批量返销
function runCancelTrade(){
	var tradeid = getCheckedValues("trades");
   if(null == tradeid || "" == tradeid) {
   		alert("请选择需要返销的对账工单！");
   }else {
   		$.ajax.submit('UpayReconCond','runCancelTrade','&TRADE_IDS='+tradeid,'UpayReconList',function(data){
   		
		$.endPageLoading();
		$.showSucMessage(data.map.result,"");
	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	});
   }
}

/*当前时间YYYY-MM-DD*/
function getCurrDate() {   
  	var now = new Date();
  	y = now.getFullYear();
  	m = now.getMonth()+1;
  	d = now.getDate();
  	m = m<10?"0"+m:m;
  	d = d<10?"0"+d:d;
  	return y+"-"+m+"-"+d;
}
