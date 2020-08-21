function refreshPartAtferAuth(data)
{
	
	var userInfos = data.get("USER_INFO");
	var serialNumber = userInfos.get("SERIAL_NUMBER");
	var userId = userInfos.get("USER_ID");
	var param = "&USER_ID="+userId+"&SERIAL_NUMBER="+serialNumber;
	$.ajax.submit('AuthPart', 'loadChildInfo',  param, 'ChooseNetworkHiddenPart,ChooseNetworkPart2', function(){
		$("#OPER_TYPE").attr("disabled", false);
		$("#COOPER_AREA").attr("disabled", false);
		$("#COOPER_NET").attr("disabled", false);
		$("#DEPUTY_LIST").attr("disabled", false);
	},    
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
		
    });
}

function changeOper(obj)
{
	if(obj.value=="01" ||  obj.value=="02")
	    $("#COOPER_NET").attr("COOPER_NET",false);
}

function changeArea(obj)
{
	var infoList = new $.DatasetList($("#DEPUTY_INFOS").val());
	var opertype = $("#OPER_TYPE").val();
	var i;
	for(i=0;i<infoList.length;i++){ 
		if(opertype =="02" && obj.value==infoList.get([i],"RSRV_STR2"))
		{   
			$("#COOPER_NET").val(obj.value);
			$("#MODIFY_TAG").val("1");
			return;
		}
	}
	if(opertype =="02"){
		alert("该用户没有开通网络！");
	}
}


function checkOpen()
{
	debugger;
	var deputy_list = new $.DatasetList($("#DEPUTY_INFOS").val());
	var net_infos  =  new $.DatasetList($("#NET_INFOS").val());
	var area = $("#COOPER_AREA");
	var net = $("#COOPER_NET");
	var opertype = $("#OPER_TYPE");
	if(opertype.val()=="02" && net.val()=="")
	{
		alert("合作网络不能为空！");
		return false;
	}
	if(opertype.val()=="02" && deputy_list.length==0)
	{
	
		alert("用户没有开通任何网络，不能注销！");
		return true;
	}
	for(var i=0;i<deputy_list.length;i++)
	{
		if(opertype.val()=="01" && deputy_list.get([i],"RSRV_STR2")==area.val() && deputy_list.get([i],"RSRV_STR3")!=net.val() && net.val()!="")
		{
					if(window.confirm("该地区已经开通了网络"+deputy_list.get([i],"RSRV_STR3")+"，是否更改为网络"+ net_infos.get([j],"PARA_CODE1")+"?")) {
						$("#MODIFY_TAG").val("2");
						return true;
					}
					else{
					   return false;
					}	
				}
		if(opertype.val()=="01" && deputy_list.get([i],"RSRV_STR2")==area.val() && deputy_list.get([i],"RSRV_STR3")==net.val())
		{
			alert("该地区已经开通了该网络，不能重复开通！");
			return false;	
		}
	}
	if(!checkAreaAndNet())
	{
		alert("该网络不属于所选择的地区，请重新选择网络！");
		return false;
	}
	return true;
}


/*校验地区和网络的匹配*/
function checkAreaAndNet()
{
	var area = $("#COOPER_AREA").val();
	var net_infos  =  new $.DatasetList($("#NET_INFOS").val());
	var cooper_net =$("#COOPER_NET").val();
	
	for(var i =0; i<net_infos.length; i++){
		
		if(net_infos.get([i],"PARA_CODE1") == cooper_net){
			
			if(area ==  net_infos.get([i],"PARAM_CODE")){			
				return true;
			}
		}
	}
	return false;
}
