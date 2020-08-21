function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO")+"&CUST_NAME="+data.get("CUST_INFO").get("CUST_NAME"), 'refreshParts,refreshParts2,phone,otherinfo,VALUE_CARD,hide', function(data){
		$("#CHNAGE_PHONE").attr('disabled',false);
		$("#VALUE_CARD").css('display','none');
//		$("#OBJECT_PHONE").css('display','none');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").val('');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);
		
		//显示用户积分值
		$("#AUTH_USER_SCORE").val($("#SCORE").val());
//		$("#USER_SCORE").html($("#SCORE").val());
		$("#userScorePart").css('display','');
		
		$("#showScoreArea").css('display','');
//		$("#refreshParts2").css('display','');
		//初始化积分区间
		var maxScore = data.get("MAX_SCORE");
		initScoreArea(maxScore);
		
		//若存在从前台热点点击过来，则初始化已选值
		var selectRuleId = $("#SELECTED_RULE_ID").val();
		if(selectRuleId != "" && selectRuleId != null && selectRuleId != undefined){
			initSelected(selectRuleId);
		}
		
		//如果是手机端，则需要样式适配
		if($("html").hasClass("s_phone")) {
			$("#TABLE_CHECK").html('');
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info, function(){$.auth.reflushPage();});
    });
}

function getScoreExCardInfo()   
{      
	$.ajax.submit('AuthPart', 'loadChildInfo', '', 'TradeCardPart', function(data){
		if(data.get('ALERT_INFO') != '') 
		{   
			MessageBox.alert("提示", data.get('ALERT_INFO'));
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
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
	var boxList = $("#" + table + " input[name=custs]");
	boxList.each(function(){
		$(this).parents("tr").css("display","");
	});
	DeptTable.adjust();
}

/**显示选中的*/
function showChoose(table) {

	var boxList = $("#" + table + " input[name=custs]");
	boxList.each(function(){
		if(!this.checked){
			$(this).parents("tr").css("display","none");
		}
	});
	DeptTable.adjust();
}

/** 校验兑换类型限制次数  */
function checkExchangeTypeLimit(obj,boxName) {
	var cardCount = $("#CARD_COUNT").val(); 
	var feeCount = $("#FEE_COUNT").val(); 
	var goodsCount = $("#GOODS_COUNT").val(); 
	var discntCount = $("#DISCNT_COUNT").val();
	
	var boxList = $("*[name=" + boxName +"]");
	var index = $(obj).attr("index");
    var exchange = obj.getAttribute ("exchangetype");
	var alertinfo = "选中的兑奖类型超过此类型的限制!";
	var count = document.getElementById("COUNT_" + index);
	
	if (exchange == "0") {//有价卡
		if(obj.checked==true) {
			if(cardCount==0) {
				MessageBox.alert("提示", alertinfo);	
				obj.checked=false;
				count.value="";
				return;	
			} else {
				cardCount--;			
			}
		}
		if(obj.checked==false) {
			cardCount++;
		}
		$("#CARD_COUNT").val(feeCount) ;	
	}
	if (exchange == "1") {//话费
		if(obj.checked==true) {
			if(feeCount==0) {
				MessageBox.alert("提示",alertinfo);	
				obj.checked=false;
				count.value="";
				return;	
			} else {
				feeCount--;			
			}
		}
		if(obj.checked==false) {
			feeCount++;
		}
		$("#FEE_COUNT").val(feeCount);	
	}		
	if (exchange == "2") {//实物
		if(obj.checked==true) {
			if(goodsCount==0) {
				MessageBox.alert("提示",alertinfo);		
				obj.checked=false;
				count.value="";
				return;	
			} else {
				goodsCount--;			
			}
		}
		if(obj.checked==false) {
			goodsCount++;
		}
		$("#GOODS_COUNT").val(goodsCount) ;	
	}
	if (exchange == "3") {//优惠
		if(obj.checked) {
			if(discntCount==0) {
				MessageBox.alert("提示",alertinfo);	
				obj.checked=false;
				count.value="";
				return;	
			} else {
				discntCount--;			
			}
		}
		if(!obj.checked) {
			discntCount++;
		}
		$("#DISCNT_COUNT").val(discntCount);	
	}
}

/**对应输入数字框*/
function chkInput(obj, boxName) { 
	var index = $(obj).attr("index");
//	var count = document.getElementById("COUNT_" + index);
	var count = $("#COUNT_" + index);
//	var reduces = $("#REDUCE_" + index);
//	var adds = $("#ADD_" + index);
	var exchange = $(obj).attr("exchangetype");
	var boxList = $("*[name=" + boxName +"]");
	if (obj.checked) {
	    if (exchange == "1"){
	    	var reward = parseInt(obj.getAttribute ("reward"));
	    	var money = reward / 100;
	    	count.val("1");
	    	obj.setAttribute("count","1");
	    }else{
	    	count.val("1");
			obj.setAttribute("count","1");
			count.attr('disabled',false);
//			reduces.attr('disabled',false);
//			adds.attr('disabled',false);
		}
		
	} else {
		count.val("");
		count.attr('disabled',true);
//		reduces.attr('disabled',true);
//		adds.attr('disabled',true);
	}
	chkTatle(index,boxName);
}

/**验证表格中选中的值*/
function chkTatle(index,boxName) {
	var tatle = 0;
	var scoreneed = 0;
	var tempneed = 0;
	var scoretotle = parseInt($("#SCORE_TOTLE").val());
	var scoresur = parseInt($("#SCORE_SUR").val());
	var boxList = $("*[name=" + boxName +"]");

	var feeflag = false;
	var cardflag = false;
	var hhflag = false;
	var money = 0;
	var clearOtherCust = true;
	var clearValueCard = true;
	var popupCount = $("#POPUP_COUNT1");
	var popupCount2 = $("#POPUP_COUNT2");
	for (var i = 0; i < boxList.length; i++) {
		if (boxList[i].checked) {
			var c = parseInt(boxList[i].getAttribute("count"));
			var limit = parseInt(boxList[i].getAttribute("limit"));
			if ( limit != -1 && c > limit){
			   MessageBox.alert("提示", "本奖项最多兑'+limit+'次,谢谢!");
			   c = limit;
			   boxList[i].count = limit;
//			   var count = document.getElementById("COUNT_" + index);
			   var count = $("#COUNT_" + index);
			   count.val(limit);
			   boxList[i].setAttribute("count","1");
			}
			
			if (!(c >= 0)) {
				c = 0;
			}
			tatle += c;
	        tempneed = c * parseInt(boxList[i].getAttribute("score"));
			scoreneed += tempneed;
			scoresur = scoretotle - scoreneed;
			if (scoresur < 0){
			    MessageBox.alert("提示", "积分不足本次兑换，请重新选择！");
			    boxList[index].checked = false;
//			    var count = document.getElementById("COUNT_" + index);
			    var count = $("#COUNT_" + index);
//			    var reduces = $("#REDUCE_" + index);
//				var adds = $("#ADD_" + index);
			    var tempscore = parseInt(boxList[i].getAttribute("score"));
			    var tempc = count.val();
			    var tempTatle = parseInt(tempscore) * parseInt(tempc);
			    scoreneed  -= tempTatle;
			    
			    count.val("");
			    tatle -= parseInt(tempc);
			    count.attr('disabled',true);
//			    reduces.attr('disabled',true);
//				adds.attr('disabled',true);
			}else{
				var type = boxList[i].getAttribute("exchangetype");
				var rewardlimit = boxList[i].getAttribute("reward");
				var gifttypecode = boxList[i].getAttribute("gifttypecode");
				if (boxList[i].checked && type == "1") {  //1为话费类型
					$("#buttonSet_" + i).css('display','');
					feeflag = true;
					clearOtherCust = false;
				}
				if (boxList[i].checked && type == "0" && rewardlimit=="0" && (gifttypecode == null || gifttypecode=="")) {  //0为有价卡类型
					$("#buttonSet_" + i).css('display','');
					cardflag = true;
					clearValueCard = false;
				}
				if (boxList[i].checked && type == "A") {  //A为海航里程兑换
					hhflag = true;
				}
			}
		}else{
			$("#buttonSet_" + i).css('display','none');
		}
		
	}
	if(hhflag){
		$("#buttonSet_" + index).css('display','none');
		$("#HH_CARD").css('display','');
	}else{
		$("#HH_CARD").css('display','none');
	}
	
	if (feeflag && ($("#otherinfo_OBJECT_NAME").val() =="" || $("#otherinfo_OBJECT_NAME").val() == null)) {
		if(popupCount.val() == "1"){
			$("#DONATE_OBJECT").css('display','');
			showPopup("myPopup","myPopup_item",true);	//话费转赠弹窗查询	只弹窗首次勾选，再次编辑需点击编辑按钮
			popupCount.val("2");
			DeptTable.adjust();		//弹窗后table格式可能会乱，需自适应格式
		}
	}else
	{
		$("#DONATE_OBJECT").css('display','none');
	} 
	if(clearOtherCust){	//取消勾选最后一个话费赠送时，清空话费转赠区域信息
		
		popupCount.val("1");
		$("#otherinfo_OBJECT_NAME").val("");
		$("#otherinfo_OBJECT_USER_ID").val("");
		$("#otherinfo_OBJECT_ACCT_ID").val("");
		$("#otherinfo_OBJECT_SERIAL_NUMBER").val("");
		DeptTable.adjust();
	}
	if(cardflag){
		if(popupCount2.val() == "1"){
//		$("#buttonSet_" + i).css('display','none');
			$("#CARD_ID").attr('nullable','no');
			$("#VALUE_CARD").css('display','');
//			CardPart.adjust();	//table自适应宽高
			showPopup('myPopupValueCard','myPopup_item_valueCard', true);	//有价卡信息查询弹窗
			popupCount2.val("2");
			DeptTable.adjust();
		}
	}else{
		$("#CARD_ID").attr('nullable','yes');
	    $("#VALUE_CARD").css('display','none');
	}
	if(clearValueCard){	//取消勾选最后一个有价卡的时候 清空有价卡区域
//		$("#CARD_ID").val("");
//		$("#CARD_END").val("");
		popupCount2.val("1");
//		CardPart.deleteRow(CardPart.selected);
	}
	$("#TATLE").val(tatle);
	$("#SCORE_NEED").val(scoreneed);
	$("#SHOW_SCORE_NEED").html(scoreneed);
	if ( tatle == 0) {
	   scoresur = scoretotle;
	}else{
		scoresur = scoretotle - scoreneed;
	}
	$("#SCORE_SUR").val(scoresur);
	$("#SHOW_SCORE_SUR").html(scoresur);
	DeptTable.adjust();
}

function verifyScore(obj, boxName) {
	var num = obj.value;
	var textName = 'COUNT_'+$(obj).attr("index");
	if(!$.validate.verifyField($("#"+textName)[0]))
    {
//       var count = document.getElementById("COUNT_" + $(obj).attr("index"));
       var count = $("#COUNT_" + $(obj).attr("index"));
	   count.val("1");
	   return false;
    }
      
	var money = 0;
	var boxList = $("*[name=" + boxName +"]");
	for (var i = 0; i < boxList.length; i++) {
		if (boxList[i].checked) {
			if (boxList[i].getAttribute("index") == $(obj).attr("index")) {
				boxList[i].setAttribute("count",num);
			}
			chkTatle($(obj).attr("index"),boxName);
		}
		
		if (boxList[i].checked) {
		  var type = boxList[i].getAttribute ("exchangetype");
			if (boxList[i].checked && type == "1") {
				var cc = parseInt(boxList[i].getAttribute ("count"));
				var reward = parseInt(boxList[i].getAttribute ("reward"));
				money += cc*reward;
			}
		}
	}

	if ( money != 0 ){
		var mon = money / 100;
	    MessageBox.info("提示", "兑换话费["+mon+"]元!");	
	}
}

/**选择话费转赠的CHECKBOX*/
function donatePenson(obj, table) {
    var object = $("#otherinfo_OBJECT_SERIAL_NUMBER");
    var objectname = $("#otherinfo_OBJECT_NAME");
	if (obj.checked) {
		$("#otherinfo_OBJECT_SERIAL_NUMBER").attr('nullable','no');
		$("#otherinfo_OBJECT_NAME").attr('nullable','no');
//		$("#OBJECT_PHONE").css('display','');
		showPopup("myPopup","myPopup_item",true);
	} else {
		object.attr('nullable','yes');
		objectname.attr('nullable','yes');
//		$("#OBJECT_PHONE").css('display','none');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").val('') ;
		$("#otherinfo_OBJECT_NAME").val('') ;
  	 	$("#otherinfo_OBJECT_USER_ID").val('') ;
  	 	$("#otherinfo_OBJECT_ACCT_ID").val('') ;
	}
	DeptTable.adjust();
}

/**点击选择兑奖类型*/
function chooseExType(e,obj,table,boxName){
	var index = $(e).attr("LinkIndex");
	var code = $("#" + obj + index).val();
    var scorevalue = $("#CENT_TYPES").val();
    var scorevalueBefore = $("#CENT_TYPESbefore").val();
    $("#EXCHANGE_TYPE_CODES").val(code);
    showChooseType(table,boxName,'exchangetype',code,'score',scorevalue,scorevalueBefore);
    DeptTable.adjust();
}

/**兑换类型选择时*/
function showChooseType(table, boxName, type, typevalue, contype, convalue, convalueBefore) {
	var boxList = $("#" + table + " input[name=custs]");
	if(!boxList) return;
	boxList.each(function(){
		if(!this.checked){	//已选择分值区间
			if(convalueBefore == "00" && convalue == "0"){	//如果分值区间选择为不限制 则根据类型过滤
				if(typevalue == ""){
					$(this).parents("tr").css("display","");
				}else if($(this).attr(type) == typevalue){
					$(this).parents("tr").css("display","");
				}else{
					$(this).parents("tr").css("display","none");
				}
			}else{
				if(typevalue == ""){	//如果兑换类型选择不限制	则根据分值区间过滤
					if(parseInt($(this).attr(contype)) <= parseInt(convalue) && parseInt($(this).attr(contype)) >= parseInt(convalueBefore) 
							&& convalue != "" && convalueBefore != undefined){
						$(this).parents("tr").css("display","");
					}else{
						$(this).parents("tr").css("display","none");
					}
				}else{	//如果兑换类型选择有值 先过滤兑换类型
					if($(this).attr(type) == typevalue && typevalue != ""){
						if(parseInt($(this).attr(contype)) <= parseInt(convalue) && parseInt($(this).attr(contype)) >= parseInt(convalueBefore) 
								&& convalue != "" && convalueBefore != undefined){	//再过滤分值
							$(this).parents("tr").css("display","");
						}else{
							$(this).parents("tr").css("display","none");
						}
					}else{
						$(this).parents("tr").css("display","none");
					}
				}
				
			}
		}
	});
	
}

/**点击选择分值区间*/
function chooseCentType(e,obj,table,boxName){
	var index = $(e).attr("liIndex");
    var cent = $("#" + obj + index).val();
    var centBefore = $("#" + obj + (index-1)).val();
    $("#CENT_TYPES").val(cent);
    if(centBefore == undefined){
    	centBefore = "00";
    }
    $("#CENT_TYPESbefore").val(centBefore);
    var typecode = $("#EXCHANGE_TYPE_CODES").val();		//已选择兑换类型
    var boxList = $("#" + table + " input[name=custs]");
	if(!boxList) return;
	boxList.each(function(){
		if(!this.checked){
			if(typecode == ""){	//如果兑换类型选择为不限制 则根据分值区间过滤
				if(centBefore == "00"){	//如果分值区间选择不限制
					$(this).parents("tr").css("display","");
				}else if(parseInt($(this).attr('score')) <= parseInt(cent) && cent != "" && centBefore != undefined
						&& parseInt($(this).attr('score')) >= parseInt(centBefore)){
					
					$(this).parents("tr").css("display","");
				}else{
					$(this).parents("tr").css("display","none");
				}
			}else{
				if(centBefore == "0"){	//如果分值区间选中的是不限制 直接只过滤兑换类型
					if($(this).attr('exchangetype') == typecode && typecode != ""){
						$(this).parents("tr").css("display","");
						return;
					}
				}else {	//选中的是分值区间的值
					if($(this).attr('exchangetype') == typecode && typecode != ""){	//先过滤兑换类型
						if(parseInt($(this).attr('score')) <= parseInt(cent) && cent != "" && centBefore != undefined	//再过滤分值
								&& parseInt($(this).attr('score')) >= parseInt(centBefore)){
							
							$(this).parents("tr").css("display","");
						}else{
							$(this).parents("tr").css("display","none");
						}
					}else{
						$(this).parents("tr").css("display","none");	
					}
				}
				
			}
		}
	});
	DeptTable.adjust();	//自适应table宽度
}


/**提交时的批串和验证*/
function submitSpellBunch(form,obj,boxName){
	if(!$.validate.verifyBox(form,boxName,'请选中要操作的数据!'))
	{
		return false;
	} 
	
	var cont53 = $("#COUNT_53").val();
	if(cont53>1){
		MessageBox.alert("提示", "办理积分兑换流量解速包，且一次仅能办理一个！");
		return false;
	}
	
	var boxList = $("*[name=" + boxName +"]");
	var spell = "";
	var temp = ""; 
	var money = 0;
	var flag = false;
	var tempMoney = 0;//有价卡兑换面值
	var idata=new Wade.DatasetList();
	for (var i = 0; i < boxList.length; i++){
	  if (boxList[i].checked) {
	    temp = "";
	    var ruleid = boxList[i].value;
	    var score = boxList[i].getAttribute ('score');
	    var extype = boxList[i].getAttribute ('exchangetype');
	    var gift = boxList[i].getAttribute ('gifttype');
	    var gifttypecode = boxList[i].getAttribute ('gifttypecode');
	    var count = boxList[i].getAttribute ('count');
	    var reward = boxList[i].getAttribute ('reward');
	    var rsrv_str3 = boxList[i].getAttribute ('rsrv_str3');
	   var data=new Wade.DataMap();
	   data.put("RULE_ID",ruleid);
	   data.put("COUNT",count);
	   idata.add(data);
	      
	    if( extype == "0" && reward=="0" &&(gifttypecode == null || gifttypecode=="")){
	    	tempMoney = tempMoney + parseInt(count) * parseInt(rsrv_str3);
	    	flag = true;
	    }
	    if ( spell == "" )
	    {
	         spell += temp;
	    }
	    else
	    {
	    	 spell = spell+", "+temp;
	    } 
	    //REQ202005260002_关于开展5G招募活动的开发需求
	    if (ruleid == "8005" || ruleid == "8006") {
	    	MessageBox.alert("提示", "该兑换规则仅供营销活动受理使用，禁止前台办理！");
    		return false;
	    }
	    //REQ202005260002_关于开展5G招募活动的开发需求
	    if (extype == "A") {
	    	var card_id = $("#HH_CARD_ID").val();
	        var card_name = $("#HH_CARD_NAME").val();
	    	var re;
	    	re = /^([0-9]{10})$/;
	    	if (!re.test(card_id)) {
	    		MessageBox.alert("提示", "金鹏会员卡卡号为10位数字，请核实是否正确！");
	    		return false;
	    	}
	    	if (card_name == ""){
	    		MessageBox.alert("提示", "请输入金鹏会员卡姓名！");
	    		return false;
	    	}
	    }
	  }
	}
	
	//有价卡提交参数处理
	if(flag){
//		var str = $.table.get("CardPart").getTableData(null,true);
		var str = CardPart.getData(true);
		if(str == 0 || str == "" || str == undefined)
		{
			MessageBox.alert("提示", "请选择要兑换的充值卡！");
			return false;
		}		
		var total = 0;
		var cardNos = new Wade.DatasetList();
		for(var i=0;i<str.length;i++){
			var obj = $("#cardDel_"+i);
			if(obj.attr("checked")){
				var data = new Wade.DataMap();
				data.put('CARD_ID', str[i].get("CARD_ID"));//有价卡卡号
				cardNos.add(data);
				total = total + parseInt(str[i].get("CARD_VALUE"));//有价卡面值 单位分
			}
		}
		if( total != tempMoney){
			MessageBox.alert("提示", "兑换有价卡价值与实际兑换的不符，业务无法继续!");
			return false;
		}
		var tempCardNos = cardNos.toString();
		$("#otherinfo_X_CARD_CODE").val(tempCardNos);
	}
	
	//兑换列表参数设置
	var tempS = idata.toString();
	$("#otherinfo_CHECK_BOXVALUE").val(tempS);
	return true;
}

/**验证两手机号是否相同*/
function isSamePhone(){
	var phonecode =$("#AUTH_SERIAL_NUMBER");
	var objectcode =$("#otherinfo_OBJECT_SERIAL_NUMBER");
	//var desc = phonecode.desc;
	
	if(!$.validate.verifyField($("#AUTH_SERIAL_NUMBER")[0]) || !$.validate.verifyField($("#otherinfo_OBJECT_SERIAL_NUMBER")[0]))
	{
		return false;
	} 

	if( phonecode.val() == objectcode.val()){
		MessageBox.alert("提示", "转赠号码不能与用户号码相同!");
		  return false;
		}

//参数1 传值域 参数2 调用java的方法 参数3 参数 参数4 返回结果刷新区域 
	$.beginPageLoading();
	$.ajax.submit(this, 'queryObjectPhone', '&OBJECT_SERIAL_NUMBER='+objectcode.val(),'otherinfo', function(data){
		afterFunc(data);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示",error_info);
    });
}

/** 转目标号码之后的处理*/
function afterFunc(data){
  var flag = data.get("FLAG");
  if( flag == "false"){
  	 var mess = data.get("MESSAGE");
  	MessageBox.alert("提示", mess);
  }
}

function showResult(btn){
};    


/**输入有价卡后ajax的刷新*/ 
function cardAjaxRefesh(){
	var inputID = $("#CARD_ID");
	var cardEnd   = $("#CARD_END");
	var sn =$("#AUTH_SERIAL_NUMBER").val();
	if(inputID.val().length < 7 || cardEnd.val().length < 7){
	  var mess = "最低卡号为7位，请输入正确的卡号！";
	  MessageBox.alert("提示", mess);
	  return false;
	}
//	var data = $.table.get("CardPart").getTableData(null,true);
	var data = CardPart.getData(true);

	//参数1 传值域 参数2 调用java的方法 参数3 参数 参数4 返回结果刷新区域 
	$.beginPageLoading();
	$.ajax.submit('card', 'queryCardRes', '&DATA='+data+'&SERIAL_NUMBER='+sn, 'cardArea', function(data){
		initScoreTable(data);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
    });
}




/**有价卡表格的初始化*/
function initScoreTable(data){
	 
   var flag = data.get("FLAG");
  if( flag == "false"){
  	 var mess = data.get("MESSAGE");
  	MessageBox.alert("提示", mess);
  } 
}

/**起止卡号的同步*/
function synCardID(sourc,object){
	$("#"+object+"").val($("#"+sourc+"").val());
}

//业务提交
function onTradeSubmit(form)
{
	if(!submitSpellBunch(form,'otherinfo_CHECK_BOXVALUE','custs'))
	{
		return false;
	}
	var cardNos = $("#otherinfo_X_CARD_CODE").val();//有价卡卡号集合
	var checkBoxValue = $("#otherinfo_CHECK_BOXVALUE").val();//兑换列表参数
	var objectSerialNumber = $("#otherinfo_OBJECT_SERIAL_NUMBER").val();
	var hhCardId = $("#HH_CARD_ID").val();
	var hhCardName = $("#HH_CARD_NAME").val();
	var param = '&CARD_NOS='+cardNos+'&EXCHANGE_DATA='+checkBoxValue+'&OBJECT_SERIAL_NUMBER='+objectSerialNumber
	+'&HH_CARD_ID='+hhCardId+'&HH_CARD_NAME='+hhCardName;
	$.cssubmit.addParam(param);
	$.cssubmit.submitTrade(param);
}

/**
 * 兑换类型样式点击变化
 * @param obj
 */
function changeTypes(obj)
{ 
	var o = $(obj);
	o.siblings().attr("class","link e_center");
	o.attr("class","link on e_center");
}

/**
 * 初始化分值区间；按用户积分 平均4等分
 */
function initScoreArea(e){
	
	var userMaxscore = e;
	
	var scoreLi = "";
	var CENT_TYPE = "CENT_TYPE";
	var DeptTable = "DeptTable";
	var custs = "custs";
	
	if(userMaxscore == "0" || userMaxscore == undefined){
		scoreLi += '<li class="link">';
//		scoreLi += '	<div class="label">分值区间：</div>';
//		scoreLi += '	<div class="value">';
		scoreLi += '	<div class="option">';
		scoreLi += '		<ul>';
		scoreLi += '			<li class="link on e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="0"><div class="main">不限<input type="hidden" name="CENT_TYPE0" id="CENT_TYPE0" value="0" /></div></li>';
		scoreLi += '		</ul>';
//		scoreLi += '	</div>';
//		scoreLi += '	</div>';
		scoreLi += '</li>';
		
		$("#scoreTypes > ul").prepend(scoreLi);
		return;
	}
	
	var averageScore = Math.ceil(userMaxscore/4); 	//向上取整
	var area1 = userMaxscore - averageScore*3;
	var area2 = userMaxscore - averageScore*2;
	var area3 = userMaxscore - averageScore;
	
	scoreLi += '<li class="link">';
//	scoreLi += '	<div class="label">分值区间：</div>';
//	scoreLi += '	<div class="value">';
	scoreLi += '	<div class="option">';
	scoreLi += '		<ul>';
	scoreLi += '			<li class="link on e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="0"><div class="main">不限<input type="hidden" name="CENT_TYPE0" id="CENT_TYPE0" value="0" /></div></li>';
	scoreLi += '			<li class="link e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="1"><div class="main">1-99<input type="hidden" name="CENT_TYPE1" id="CENT_TYPE1" value="99" /></div></li>';
	scoreLi += '			<li class="link e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="2"><div class="main">100-499<input type="hidden" name="CENT_TYPE2" id="CENT_TYPE2" value="499" /></div></li>';
	scoreLi += '			<li class="link e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="3"><div class="main">500-999<input type="hidden" name="CENT_TYPE3" id="CENT_TYPE3" value="999" /></div></li>';
	scoreLi += '			<li class="link e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="4"><div class="main">1000-1999<input type="hidden" name="CENT_TYPE4" id="CENT_TYPE4" value="1999" /></div></li>';
	scoreLi += '			<li class="link e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="5"><div class="main">2000-9999<input type="hidden" name="CENT_TYPE5" id="CENT_TYPE5" value="9999" /></div></li>';
	scoreLi += '			<li class="link e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="6"><div class="main">1万 以上<input type="hidden" name="CENT_TYPE6" id="CENT_TYPE6" value="'+userMaxscore+'" /></div></li>';
//	scoreLi += '			<li class="link e_center" ontap="chooseCentType(this,\''+CENT_TYPE+'\',\''+DeptTable+'\',\''+custs+'\'); changeScoreTypes(this);" liIndex="4"><div class="main">'+area3+' 以上'+'<input type="hidden" name="CENT_TYPE4" id="CENT_TYPE4" value="'+userMaxscore+'" /></div></li>';
//	scoreLi += '			<li ontap="chooseCentType(this,"CENT_TYPE","DeptTable","custs"); changeScoreTypes(this);" ><div class="main">'+area3+'-'+userMaxscore+'<input type="hidden" name="CENT_TYPE4" id="CENT_TYPE4" index="4" value="'+userMaxscore+'" /></div></li>';
	scoreLi += '		</ul>';
//	scoreLi += '	</div>';
//	scoreLi += '	</div>';
	scoreLi += '</li>';
	
	$("#scoreTypes > ul").prepend(scoreLi);
}

/**
 * 分值区间样式点击变化
 */
function changeScoreTypes(obj){
	var o = $(obj);
	o.siblings().attr("class","link e_center");
	o.attr("class","link on e_center");
}

/**
 * 减号按钮逻辑
 * @param obj
 * @param boxName
 * @returns {Boolean}
 */
function reduceCount(obj,boxName){
	var index = $(obj).attr("index");
	var count = $("#COUNT_" + index);
	var reduces = $("#REDUCE_" + index);
	var adds = $("#ADD_" + index);
	if(count.attr("disabled") == true){
		return false;
	}else if(count.val() == 1){	//当count=1时提示
		$.TipBox.show(document.getElementById("COUNT_" + index), "兑换数量必须为大于零的整数！", "red");
		return false;
	}else{
		count.val(parseInt(count.val()) - parseInt(1));
		reduces.val(count.val());
		adds.val(count.val());
		verifyScore(obj, boxName);
	}
}

/**
 * 加号按钮逻辑
 * @param obj
 * @param boxName
 * @returns {Boolean}
 */
function addCount(obj,boxName){
	var index = $(obj).attr("index");
	var count = $("#COUNT_" + index);
	var reduces = $("#REDUCE_" + index);
	var adds = $("#ADD_" + index);
	if(count.attr("disabled") == true){
		return false;
	}else{
		count.val(parseInt(count.val()) + parseInt(1));
		reduces.val(count.val());
		adds.val(count.val());
		verifyScore(obj, boxName);
	}
}

/**
 * 话费转赠弹窗确定校验
 * @returns {Boolean}
 */
function validateCustName(){
	var custName = $("#otherinfo_OBJECT_NAME").val();
	if(custName == null || custName == ""){
		$.TipBox.show(document.getElementById('otherinfo_OBJECT_SERIAL_NUMBER'), "请输入目标号码！", "red");
		return false;
	}
}

/**
 * 有价卡弹窗确定校验
 * @returns {Boolean}
 */
function validateValueCard(){
	var cardID = $("#CARD_ID").val();
	var str = CardPart.getCheckedRowsData("cardDel");
	if(cardID == null || cardID == "" || cardID == undefined){
		$.TipBox.show(document.getElementById('CARD_ID'), "请输入卡号！", "red");
		return false;
	}
	if(str == null || str == undefined)
	{
		MessageBox.alert("提示", "请选择要兑换的充值卡！");
		return false;
	}		
}

/**
 * 编辑按钮
 * @param obj
 */
function showMyPopu(obj){
	var thisCode = $(obj).attr("thisCode");
	var thisRewardlimit = $(obj).attr("thisLimit");
	var thisGifttypecode = $(obj).attr("thisGifttypecode");
	if(thisCode == "1"){
		$("#DONATE_OBJECT").css('display','');
		showPopup('myPopup','myPopup_item', true);
	}
	if(thisCode== "0" && thisRewardlimit=="0" && (thisGifttypecode == null || thisGifttypecode=="")){
		 
		$("#VALUE_CARD").css('display','');
		showPopup('myPopupValueCard','myPopup_item_valueCard', true);
	}
}

/**
 * 查询后初始化已选积分兑换值
 * @param e
 */
function initSelected(e){
	var scoreList = DeptTable.getData(true);	//所有积分数据
	if(scoreList.length > 0){
		for(var i=0; i < scoreList.length; i++){
			var tableRuleID = scoreList[i].get("RULE_ID");
			if(tableRuleID == e){
				$("#DeptTable input[name=custs]:checkbox").eq(i)[0].click();//触发勾选方法
				
				showChoose('DeptTable');//默认只显示勾选项
				showScoreLayer('fullChoose');//默认只显示勾选项
				hideScoreLayer('alreadyChoose');//默认只显示勾选项
			}
		}
	}
}

function showMoreType(){
	$("#foldButton").css("display","");
	$("#unfoldButton").css("display","none");
	$("#moreTypes").removeClass("option-fold");
}

function hideMoreType(){
	$("#foldButton").css("display","none");
	$("#unfoldButton").css("display","");
	$("#moreTypes").addClass("option-fold");
}

/**
 * 点击list的选择框，同时选中隐藏table的选择框
 */
function checkTab(e){
	var thisIndex = $(e).attr("thisIndex");
	$("#CardPart input[name=cardDel]:checkbox").eq(thisIndex).click();//触发勾选方法
}


