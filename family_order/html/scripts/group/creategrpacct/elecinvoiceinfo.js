//页面初始化方法
 $(document).ready(function() {
	 //PrintNote();
//	 if($("#POST_TYPE_NOTE").checked){
//		 $("#busi").attr('disabled',true); 
//	 }
//	if($("#postinfo_POST_DATE_MON").val()){//推送日期
//		$("#li_post_date").attr('disabled',false);
//		$("#span_post_date").addClass("e_required");
//		$("#postinfo_POST_DATE_MON").attr("nullable","no");
//	}else{
//		$("#postinfo_POST_DATE_MON").attr("nullable","yes");
//	}
	var epostChannel = $("*[name='POST_CHANNEL']");  //推送区域
	for(var i=0;i<epostChannel.length;i++){
	    if(epostChannel[i].checked){
	    	if(i==0){
	    		$("#span_receive_number").addClass("e_required");
			    $("#postinfo_RECEIVE_NUMBER").attr("nullable","no");
	    	}else{
	    		$("#span_post_adr").addClass("e_required");
			    $("#postinfo_POST_ADR").attr("nullable","no");
	    	}
	    }
    }
 });
 //打印纸质
// function PrintNote(){
//	 var postTypeNote=$("#POST_TYPE_NOTE").val();
//	 if(postTypeNote.checked){
//		 $("#busi").attr('disabled',true); 
//		//去掉发票类型
//		 $("#POST_TYPE_MON").checked = false;
//		 $("#POST_TYPE_CASH").checked = false;
//		 $("#POST_TYPE_BUSI").checked = false;
//		 //手机号码邮箱地址不可编辑
//	     $("#postinfo_RECEIVE_NUMBER").attr('disabled',true); 
//	     $("#postinfo_POST_ADR").attr('disabled',true); 
//		//去掉推送方式
//		 	var epostChannel = $("*[name='POST_CHANNEL']");
//		     for(var i=0;i<epostChannel.length;i++){
//		     	epostChannel[i].checked = false;
//		     }
//		   //清空手机号
//		     $("#span_receive_number").attr("className","");
//				$("#postinfo_RECEIVE_NUMBER").attr("nullable","yes");
//				$("#postinfo_RECEIVE_NUMBER").val("");
//				//清空邮箱号
//				$("#span_post_adr").attr("className","");
//				$("#postinfo_POST_ADR").attr("nullable","yes");
//				$("#postinfo_POST_ADR").val("");
//			 	//清空推送日期
//	 			$("#span_post_date").attr("className","");
//	 			$("#postinfo_POST_DATE_MON").attr("nullable","yes");
//	 			$("#postinfo_POST_DATE_MON").val("");
//			 	
//	 }else{
//		 $("#busi").attr('disabled',false); 
//		 $("#postinfo_RECEIVE_NUMBER").attr('disabled',false); 
//	     $("#postinfo_POST_ADR").attr('disabled',false); 
//	 }
// }

//勾选发票类型
function setPostTypeChange(objCheck)
{
//	if($(objCheck).attr("name")=='POST_TYPE_MON'){ 
//		if(objCheck.checked){
//			$("#postinfo_POST_DATE_MON").attr("nullable","no");
//			$("#li_post_date").attr('disabled',false);
//			$("#span_post_date").addClass("e_required");
//		}else{
//			$("#li_post_date").attr('disabled',true);
// 			$("#span_post_date").attr("className","");
// 			$("#postinfo_POST_DATE_MON").attr("nullable","yes");
// 			$("#postinfo_POST_DATE_MON").val("");
//		
//	}
//	}

		
}


//勾选推送方式
function setPostChannelChange(objCheck)
{
	if($(objCheck).attr("name")=='POST_CHANNEL'){ //月结/现金业务区
		if(objCheck.checked){
			if(objCheck.value==02){
				$("#span_receive_number").addClass("e_required");
				$("#postinfo_RECEIVE_NUMBER").attr("nullable","no");
			}
			if(objCheck.value==12){		
				$("#span_post_adr").addClass("e_required");
				$("#postinfo_POST_ADR").attr("nullable","no");
			}
		}else{
			if(objCheck.value==02){
				$("#span_receive_number").attr("className","");
				$("#postinfo_RECEIVE_NUMBER").attr("nullable","yes");
				$("#postinfo_RECEIVE_NUMBER").val("");
			}
			if(objCheck.value==12){
				$("#span_post_adr").attr("className","");
				$("#postinfo_POST_ADR").attr("nullable","yes");
				$("#postinfo_POST_ADR").val("");
			}	
		}
	}	
}
function onSubmitBaseCheck(){
	if(!checkInfo())
	{
		return;	
	}
	$.beginPageLoading();
	$.ajax.submit('refresh3', "onSubmit", "" , null, function(data){ 
		endPageLoading();
		alert("发票设置成功!"); 
		setPopupReturnValue('','');
	},function(error_code,error_info){
		endPageLoading();
		alert(error_info); 
	});	
}

function checkInfo(){
	if(!isCheckBox("POST_CHANNEL")&&(isCheckBox("POST_TYPE_MON")||isCheckBox("POST_TYPE_CASH")||isCheckBox("POST_TYPE_BUSI"))) // 在有选择发票类型的情况下,邮箱地址和手机号码至少选一个
	{
	   alert("请选择推送方式并添加邮箱地址或手机号码信息!");
	   return false;
	}
	if(isCheckBox("POST_CHANNEL")&&(!isCheckBox("POST_TYPE_MON")&&!isCheckBox("POST_TYPE_CASH")&&!isCheckBox("POST_TYPE_BUSI"))) // 在选择推送方式时，发票类型必须选一个
	{
	   alert("请选择电子发票类型!");
	   return false;
	}
	if($("#postinfo_RECEIVE_NUMBER").val()!=""&&!isTel($("#postinfo_RECEIVE_NUMBER").val())){
		alert("手机号码格式错误");
		return false;
	}
	if($("#postinfo_POST_ADR").val()!=""&&!isEmail($("#postinfo_POST_ADR").val())){
		alert("邮箱格式错误");
		return false;
	}
	if(!verifyAll('refresh3'))
	{
		return false;
	}
	return true;

}


/**
 * 检查是否没有一个被选中
 */
var isCheckBox= function(objId){
	var obj = $("*[name=" + objId +"]");
	if(obj&&obj.length){
		for(var i=0;i<obj.length;i++){
			if(obj[i].checked)
				return true;
		}
	}
	return false;
}
function isTel(str){
var reg=/^([0-9]|[\-])+$/g ;
if(str.length!=11&&str.length!=13){// 增加物联网手机号码长度 13位
return false;
}
else{
 return reg.exec(str);
}
}

function isEmail(str){
reg=/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
return reg.exec(str);
}

