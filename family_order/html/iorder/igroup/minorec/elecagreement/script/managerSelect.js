$(function(){
	
	changeDepartCounty_manager();
	
})

function queryInfo(){
	
	$.beginPageLoading('加载中...');
		
	ajaxSubmit("searchPart",'queryManagerInfoByParam',null,'QryInfoResultPart',function(data){
		$.endPageLoading();
		hidePopup('popup');
	},function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误信息!", error_info, function(btn){});
	});
}



function backInfo(obj){
	var params = $("#cond_PARAMS").val();
	var strs = params.split(',');
	var returnObj = {};
	
	for(var i=0;i<strs.length;i++)
	{
		var temp = strs[i];
		
		if(temp.indexOf("STAFF_ID")>-1||temp.indexOf("MANAGER_ID")>-1){
			var value = obj.attr('STAFF_ID');
			returnObj[temp] = value;
		}else if(temp.indexOf("STAFF_NAME")>-1||temp.indexOf("MANAGER_NAME")>-1){
			var value = obj.attr('STAFF_NAME');
			returnObj[temp] = value;
		}
	}
	
	setPopupReturnValue(this, returnObj ,true);
	
}

function clearValue(){
	var params = $("#cond_PARAMS").val();
	var strs = params.split(',');
	var returnObj = {};
	for(var i=0;i<strs.length;i++)
	{
		var temp = strs[i];
		
		if(temp.indexOf("STAFF_ID")>-1||temp.indexOf("MANAGER_ID")>-1){
			returnObj[temp] = '';
		}else if(temp.indexOf("STAFF_NAME")>-1||temp.indexOf("MANAGER_NAME")>-1){
			returnObj[temp] = '';
		}
	}
	setPopupReturnValue(this, returnObj ,true);
}

function backPop(){
	var obj = {};
	setPopupReturnValue(this, obj ,true);
}



function changeDepartCounty_manager(){
	var departId = 'cond_DEPART_ID';
	var county = $("#cond_AREA_CODE").val();
	var ontap = $("#POP_"+departId).attr('ontap');
	
	$("#POP_"+departId).attr('ontap',changeDepartParams(ontap,county));
	
	
	ontap = $("#POP_BTN_"+departId).attr('ontap');
	
	$("#POP_BTN_"+departId).attr('ontap',changeDepartParams(ontap,county));
}

function changeDepartParams(ontap,county){
	var ontapSp = ontap.split(',');
	
	var newOntap = ontapSp[0]+','+ontapSp[1]+','+ontapSp[2]+','+ontapSp[3]+',';
	
	var params = ontapSp[4].substring(1,ontapSp[4].length-2);
	
	if(params.indexOf('MGMT_COUNTY')>-1){
		var newParams = '';
		var para = params.split('&');
		for(var i=0;i<para.length;i++){
			if(para[i].indexOf('MGMT_COUNTY')>-1){
				para[i]='MGMT_COUNTY='+county;
			}
			newParams += '&'+para[i];
		}
		params = newParams;
	}else{
		params+= "&MGMT_COUNTY="+county;
	}
	
	newOntap += "'"+params+"')";
	
	return newOntap;
}