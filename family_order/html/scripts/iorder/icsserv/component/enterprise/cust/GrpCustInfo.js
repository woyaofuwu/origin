var tempGroupId;
$(function(){
	debugger;
	var bpm = $("#BPM_TEMLET_ID").val();
	if("GROUPATTRCHANGE" == bpm){
		$("#cond_CUSTINFO_TAG").html("归属集团信息");
		return;
	}
	if($.enterpriseLogin && $.enterpriseLogin.isLogin())
	{
		var loginGroupId = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("GROUP_ID");
		if($("#cond_GROUP_ID_INPUT").val() == "")
		{//如果外框进行了认证，并且集团组件GROUP_ID为空，则将认证集团带入组件
			queryGroup(loginGroupId, null);
		}
		else
		{
			if(loginGroupId != $("#cond_GROUP_ID_INPUT").val() && $.isFunction(window["refreshEnterpriseLogin"]))
			{
				refreshEnterpriseLogin($("#cond_GROUP_ID_INPUT").val());
			}
		}
	}
});

function queryGroupInfo(el, e)
{
	if (e.keyCode != 13 && e.keyCode != 108) 
	{
		return ;
	}
	
	if(!$.validate.verifyField("cond_GROUP_ID_INPUT"))
	{
		return ;
	}
	debugger;
	var groupId = $.trim($("#cond_GROUP_ID_INPUT").val());
	queryGroup(groupId, el);
	if(!groupId)
	{
		return ;
	}
	var bpm = $("#BPM_TEMLET_ID").val();
	if("GROUPATTRCHANGE" == bpm){
		return;
	}
	if (tempGroupId != groupId && tempGroupId != undefined&&tempGroupId!=null&&tempGroupId!="") {
		
		$("#cond_OFFER_NAME").val("");
		$("#cond_TEMPLET_ID").val("");
		//更换集团时默认新增
		$("#cond_OPER_TYPE").val("20");
		changeOperType();
		
		$.enterpriseLogin.refreshGroupInfo(tempGroupId);
	}
	tempGroupId = groupId;
}

function queryGroup(groupId, el)
{
	var param = "GROUP_ID="+groupId;
	var bpm = $("#BPM_TEMLET_ID").val();
	var checkTag = $("#checkTag").val();
	/*if("GROUPATTRCHANGE" == bpm){
		var loginGroupId = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("GROUP_ID");
		if(groupId == loginGroupId){
			MessageBox.alert("提示信息","归属集团与原集团为同一集团，请重新输入！");
			return;
		}
	}*/
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "", param, "groupBasePart,moreCustPart", function(data){
		if("GROUPATTRCHANGE" != bpm){
			$.enterpriseLogin.refreshGroupInfo(groupId);
		}
//		MessageBox.alert("提示信息", "您正在处理<span class='e_red'>【"+groupId+"】</span>的业务，认证信息会随之变化！", function(btn){
//			$.enterpriseLogin.refreshGroupInfo(groupId);
//		});
		$.endPageLoading();
		if(el)
		{
			el.blur();
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function changeAreaByCity()
{
	debugger;
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("cond_CITYNAME");
	if (cityField.value == '海口')	{
		cond_COUNTYNAME.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			cond_COUNTYNAME.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		cond_COUNTYNAME.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			cond_COUNTYNAME.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		cond_COUNTYNAME.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				cond_COUNTYNAME.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
};