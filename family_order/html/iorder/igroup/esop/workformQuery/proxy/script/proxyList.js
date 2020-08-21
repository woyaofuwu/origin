function changeRole(obj){

	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('','changeRole',"&ROLE_ID="+obj,'departForm',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
function queryProxy(){
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('','queryProxy','','QryResultPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function queryProxyhistory(){
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('','queryProxyhistory','','QryResultPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function reloadStaff(obj){
	/*var staffList =  $(obj).attr("staffList");
	var departId = $(obj).val();
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('','reloadStaff',"&staffList="+staffList+"&DEPART_ID="+departId,'departForm',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});*/
	
}
function deleteProxy(){
	var chk = document.getElementsByName("TRADES");
	var PROXY_IDS = "";
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
            	PROXY_IDS += chk[i].value;
                if(i == chk.length - 1){
                    
                }else{
                	PROXY_IDS = PROXY_IDS + "|";
                }
            }
       	}
		
	}
	if(PROXY_IDS == ""){
		MessageBox.alert("请选择需要删除的代理！");
		return false;
	}
	$.beginPageLoading('正在提交...');
	$.ajax.submit('','deleteProxy','&PROXY_IDS='+PROXY_IDS,'QryResultPart',function(data){
		MessageBox.alert("删除成功！");
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
function changeProxyType(obj) {
	   if(obj=='0') {
		   $("#bpmletId").css("display","none");
	       //document.getElementById('bpmlist').style.display='none';
	   }else if(obj=='1') {
		   $("#bpmletId").css("display","");
		   showPopup('qrytablePopup','qrytablePopupItem',true)
	   }
};

function changeProxyType() {
		var obj =  $("#PROXY_TYPE").val();
	   if(obj=='0') {
	       //document.getElementById('bpmlist').style.display='none';
	   }else if(obj=='1') {
		   showPopup('qrytablePopup','qrytablePopupItem',true)
	   }
};
	
	
function LayoutWorkForm(){
	debugger;
	if(!$.validate.verifyAll("queryForm")){
		return false;
	}
	var type = $("#PROXY_TYPE").val();
	//var tableList = templteTable.getCheckedRowsData("TRADES");
	var chk = document.getElementsByName("MYTRADES");
	var infoList = "|";
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
            	infoList += chk[i].value;
                if(i == chk.length - 1){
                    
                }else{
                	infoList = infoList + "|";
                }
            }
       	}
		
	}
	if(type == '1' && infoList == "|"){
		MessageBox.alert("选择的流程列表不能为空！");
		return false;
	}
	$.beginPageLoading('正在提交...');
	$.ajax.submit('queryForm,departForm','submitProxy','&INFO_LIST='+infoList,'QryResultPart,queryForm',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function queryProxyList(el){
	debugger;
	var infoList = $(el).attr("PROXY_ID");
	$.ajax.submit('','queryProxyList','&PROXY_ID='+infoList,'oldTablePart',function(data){
		$.endPageLoading();
		showPopup('qryOldtablePopup','qryOldtablePopupItem',true);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
