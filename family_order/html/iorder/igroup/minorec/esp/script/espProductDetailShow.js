$(function(){
	var espSny = $("#ESP_SNY_STATE").val();
	var groupId = $("#GROUP_ID").val();
	if (espSny != "1"){
		MessageBox.alert("提示信息", "该集团客户【"+groupId+"】未同步ESP平台，订购前请先进行同步ESP平台操作！前往菜单路径（ CRM > 集团业务 > 资料管理 > 集团客户资料管理 ）集团资料同步！否则无法在ESP平台找到该客户信息，无法进行订购！");
	}
})

function espHtml(){
	var espSny = $("#ESP_SNY_STATE").val();
	var groupId = $("#GROUP_ID").val();
	if (espSny != "1"){
		MessageBox.alert("提示信息", "该集团客户【"+groupId+"】未同步ESP平台，订购前请先进行同步ESP平台操作！前往菜单路径（ CRM > 集团业务 > 资料管理 > 集团客户资料管理 ）集团资料同步！否则无法在ESP平台找到该客户信息，无法进行订购！", function(btn){
			refreshEspSnyState();
		});
		return false;
	}
	var staffId = $("#COND_STAFF_ID").val();
	var eparchyCode = $("#COND_EPARCHY_CODE").val();
	var param = "&staffId="+staffId+"&eparchyCode="+eparchyCode;
    openNav('政企ESP订购', 'csserv.group.querygroupinfo.LoginGRPESP','initial', param, '/order/order');
}

//提交
function submitApply()
{
	var groupId = $("#GROUP_ID").val();
	var espSny = $("#ESP_SNY_STATE").val();
	if (espSny != "1"){
		MessageBox.alert("提示信息", "该集团客户【"+groupId+"】未同步ESP平台，提交前请先进行同步ESP平台！前往菜单路径（ CRM > 集团业务 > 资料管理 > 集团客户资料管理 ）集团资料同步！", function(btn){
			refreshEspSnyState();
		});
		return false;
	}
	var submitData = buildSubmitData(); // 拼凑 提交参数；
	
	var message = "操作成功!";
	
	MessageBox.confirm("提示信息", "请确认是否已经在ESP平台完成订购操作？ 一旦确定之后，无法返回查看该信息。", function(btn){
		if("ok" == btn){
			$.beginPageLoading("数据提交中，请稍后...");
			$.ajax.submit("", "submit", "&SUBMIT_PARAM="+encodeURIComponent(submitData.toString()), "", function(data){
				$.endPageLoading();
				 
				MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
					if("ok" == btn){
						closeNav();
					}
				});
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	});
	 
		
	 
}

//拼凑 提交参数；
function buildSubmitData() {

    var submitData = new Wade.DataMap();
	submitData.put("COMMON_DATA", saveEosCommonData());
    return submitData;
}

function saveEosCommonData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	return eosCommonData;
}

function popupDownload(el) {
	var electronicArchives = $.DatasetList($("#ELECTRONIC_ARCHIVES").text());
	var str="";
	$.each(electronicArchives,function(index,value){
//			console.log(value);
			if (value.get("ARCHIVES_ATTACH")){
				str = str+$.DatasetList(value.get("ARCHIVES_ATTACH")).get(0).get("FILE_ID")+",";
			}
	});
	if (str.endsWith(",")){
		str = str.substring(0, str.length-1);
	}
	 
	contractUpload.loadFile(str)
	
	$("#attachPopupItem .fn:first").hide();
	$("#attachPopupItem [tag=ok]").hide();
	$("#attachPopupItem [tag=clear]").hide();
	//$("#attachPopupItem [tag=delete]").hide();
    showPopup("popup01", "attachPopupItem", true);
    
}

//刷新集团客户esp同步状态
function refreshEspSnyState(){
	var custId = $("#cond_CUST_ID").val();
 	$.ajax.submit("", "refreshESPStatus", "&CUST_ID="+custId,"", function(data){
		$.endPageLoading();
		$("#ESP_SNY_STATE").val(data.get("SYN_ESP"));
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}