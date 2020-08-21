function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'getCommInfo', "&USER_ID="+data.get("USER_INFO").get("USER_ID"), 'refreshParts2,hide',function(){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function serialNumberKeydown(event)
{
	event = event || window.event; 
	e = event.keyCode;
	
	if (e == 13 || e == 108)
	{
		isSamePhone();
	}
}

/**校验领取数量*/
function verifyAcount(obj, boxName) { 
	var index = $(obj).attr("index");
	var countObj = document.getElementById("COUNT_" + index);
	var checkObj;
	var ruleId = $(obj).attr("ruleId");
	var giftsInfo =  $("*[name=" + boxName +"]");
	var count1 = 0;
	for(var i=0; i<giftsInfo.length; i++){
		var gift = giftsInfo[i];
		if(gift.checked && $(gift).attr("ruleId") == ruleId){
			//计算同一个兑换编码已领取的数量
			var thisCountVal = document.getElementById("COUNT_" + $(gift).attr("index")).value;
			count1 += parseInt(thisCountVal);
			checkObj = gift;
		}
	}
	
	if(count1 > parseInt($(checkObj).attr("limitCount"))){
		alert("["+$(checkObj).attr("goodsName")+"]类型礼品领取数量总和["+count1+"]不能超过兑换数量[" + $(checkObj).attr("limitCount")+ "]个！");
		countObj.value = "";
		checkObj.checked = false;
		countObj.disabled = true;
		return false;
	}
	
}

/**显示一个ID的内容*/
function showScoreLayer(optionID) {  
	$("#"+optionID+"").css("display","");

}
/**隐藏一个ID的内容*/
function hideScoreLayer(optionID) {
	$("#"+optionID+"").css("display","none");
}

/**显示全部*/
function showFull(table) { 
	var boxList = $("#" + table + " input[name=giftId]");
	boxList.each(function(){
		$(this).parents("tr").css("display","");
	});
}

/**显示选中的*/
function showChoose(table) {

	var boxList = $("#" + table + " input[name=giftId]");
	boxList.each(function(){
		if(!this.checked){
			$(this).parents("tr").css("display","none");
		}
	});
}


/**对应输入数字框*/
function chkInput(obj, boxName) {

	var index = $(obj).attr("index");
	var limitCount =$(obj).attr("limitCount");
	var ruleId = $(obj).attr("ruleId");
	var paraCode1 = $(obj).attr("paraCode1");
	var count = document.getElementById("COUNT_" + index);
	var giftsInfo = $("*[name=" + boxName +"]");
	var count1 = 0;
	for(var i=0; i<giftsInfo.length; i++){
		var gift = giftsInfo[i];
		if(obj.checked && gift.checked && gift.ruleId==ruleId && gift.paraCode1!=paraCode1){
			var thisCountVal = document.getElementById("COUNT_" + gift.index).value;
			count1 += parseInt(thisCountVal);
		}		
	}
	if (obj.checked) {
		if(count1+1 <= parseInt(limitCount)){
			count.value = "1";
			//$(obj).attr("actionCount") = 1;
			count.disabled = false;
		}else{
			alert("["+$(obj).attr("goodsName")+"]类型礼品最多只能领取[" + limitCount+ "]个！");
			obj.checked = false;
			count.disabled = true;
			return false;
		}
	} else {
		count.value = "";
		count.disabled = true;
	}
}




//业务提交
function onTradeSubmit(form)
{
	var selectedNum = getCheckedBoxNum('giftId');
	if(!(selectedNum > 0)){
		alert("请选择礼品！");
		return false;
	}
	
	var giftsInfo = $("*[name=giftId]");
	var idata=new Wade.DatasetList();
	for(var i=0; i<giftsInfo.length; i++){
		var gift = giftsInfo[i];
		if(!gift.checked){
			continue;
		}else{
			var data=new Wade.DataMap();
			data.put('TRADE_ID', $(gift).attr('tradeId'));
			data.put('RULE_ID', $(gift).attr('ruleId'));
			data.put('PARA_CODE1', $(gift).attr('paraCode1'));
			data.put('PARA_CODE3', $(gift).attr('paraCode3'));
			data.put('GOODS_NAME', $(gift).attr('goodsName'));
			data.put('ACTION_COUNT', $(gift).attr('actionCount'));
			data.put('LIMIT_COUNT', $(gift).attr('limitCount'));
			data.put('GOODS_STATE', $(gift).attr('goodsState'));
			idata.add(data);
		}		
	}
	var param = '&IDATA='+idata;
	$.cssubmit.addParam(param);
	$.cssubmit.submitTrade(param);
}

