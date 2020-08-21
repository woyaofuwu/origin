function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO")+"&CUST_NAME="+data.get("CUST_INFO").get("CUST_NAME"), 'refreshParts,refreshParts2,phone,otherinfo,VALUE_CARD,hide', function(){
		$("#CHNAGE_PHONE").attr('disabled',false);
		$("#VALUE_CARD").css('display','none');
		$("#OBJECT_PHONE").css('display','none');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").val('');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);
	});
}

function getScoreExCardInfo()   
{      
	$.ajax.submit('AuthPart', 'loadChildInfo', '', 'TradeCardPart', function(data){
		if(data.get('ALERT_INFO') != '') 
		{   
			alert(data.get('ALERT_INFO'));
		}
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
}

/**显示选中的*/
function showChoose(table) {

	var boxList = $("#" + table + " input[name=custs]");
	boxList.each(function(){
		if(!this.checked){
			$(this).parents("tr").css("display","none");
		}
	});
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
				alert(alertinfo);	
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
				alert(alertinfo);	
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
				alert(alertinfo);		
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
				alert(alertinfo);	
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
	var count = document.getElementById("COUNT_" + index);
	var exchange = $(obj).attr("exchangetype");
	var boxList = $("*[name=" + boxName +"]");
	if (obj.checked) {
	    if (exchange == "1"){
	    	var reward = parseInt(obj.getAttribute ("reward"));
	    	var money = reward / 100;
	    	count.value = "1";
	    	obj.setAttribute("count","1");
	    }else{
			count.value = "1";
			obj.setAttribute("count","1");
			count.disabled = false;
		}
		
	} else {
		count.value = "";
		count.disabled = true;
	}
	chkTatle(index,boxName);
}

/**验证表格中选中的值*/
function chkTatle(index,boxName) {
	debugger;
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
	for (var i = 0; i < boxList.length; i++) {
		if (boxList[i].checked) {
			var c = parseInt(boxList[i].getAttribute("count"));
			var limit = parseInt(boxList[i].getAttribute("limit"));
			if ( limit != -1 && c > limit){
			   alert('本奖项最多兑'+limit+'次,谢谢!');
			   c = limit;
			   boxList[i].count = limit;
			   var count = document.getElementById("COUNT_" + index);
			   count.value= limit;
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
			    alert("积分不足本次兑换，请重新选择！");
			    boxList[index].checked = false;
			    var count = document.getElementById("COUNT_" + index);
			    var tempscore = parseInt(boxList[i].getAttribute("score"));
			    var tempc = count.value;
			    var tempTatle = parseInt(tempscore) * parseInt(tempc);
			    scoreneed  -= tempTatle;
			    
			    count.value="";
			    tatle -= parseInt(tempc);
			    count.disabled = true;
			}else{
				var type = boxList[i].getAttribute("exchangetype");
				var rewardlimit = boxList[i].getAttribute("reward");
				var gifttypecode = boxList[i].getAttribute("gifttypecode");

				if (boxList[i].checked && type == "1") {  //1为话费类型
					feeflag = true;
				}
				if (boxList[i].checked && type == "0" && rewardlimit=="0" && (gifttypecode == null || gifttypecode=="")) {  //0为有价卡类型
					cardflag = true;
				}
				if (boxList[i].checked && type == "A") {  //A为海航里程兑换
					hhflag = true;
				}
			}
		}
		
	}
	
	if(hhflag){
		$("#HH_CARD").css('display','');
	}else{
		$("#HH_CARD").css('display','none');
	}
	
	if (feeflag) {
		$("#DONATE_OBJECT").css('display','');
	}else
	{
		$("#DONATE_OBJECT").css('display','none');
	} 

	if(cardflag){
		$("#CARD_ID").attr('nullable','no');
	    $("#VALUE_CARD").css('display','');
	}else{
		$("#CARD_ID").attr('nullable','yes');
	    $("#VALUE_CARD").css('display','none');
	}
	
	$("#TATLE").val(tatle);
	$("#SCORE_NEED").val(scoreneed);
	if ( tatle == 0) {
	   scoresur = scoretotle;
	}else{
		scoresur = scoretotle - scoreneed;
	}
	$("#SCORE_SUR").val(scoresur);
}

function verifyScore(obj, boxName) {
	var num = obj.value;
	var textName = 'COUNT_'+$(obj).attr("index");
	if(!$.validate.verifyField($("#"+textName)[0]))
    {
       var count = document.getElementById("COUNT_" + $(obj).attr("index"));
	   count.value= "1";
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
	    alert("兑换话费["+mon+"]元!");	
	}
}

/**选择话费转赠的CHECKBOX*/
function donatePenson(obj, table) {
    var object = $("#otherinfo_OBJECT_SERIAL_NUMBER");
    var objectname = $("#otherinfo_OBJECT_NAME");
	if (obj.checked) {
		$("#otherinfo_OBJECT_SERIAL_NUMBER").attr('nullable','no');
		$("#otherinfo_OBJECT_NAME").attr('nullable','no');
		$("#OBJECT_PHONE").css('display','');
	} else {
		object.attr('nullable','yes');
		objectname.attr('nullable','yes');
		$("#OBJECT_PHONE").css('display','none');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").val('') ;
		$("#otherinfo_OBJECT_NAME").val('') ;
  	 	$("#otherinfo_OBJECT_USER_ID").val('') ;
  	 	$("#otherinfo_OBJECT_ACCT_ID").val('') ;
	}
}

/**选择兑奖类型下拉*/
function chooseExType(obj,table,boxName){
    var code = $("[name=" + obj +"]").val();
    var scorevalue = $("#CENT_TYPE").val();
    showChooseType(table,boxName,'exchangetype',code,'score',scorevalue);
}

/**选择分值下拉*/
function chooseCentType(obj,table,boxName){
    var cent = $("[name=" + obj +"]").val();
    var typecode = $("#EXCHANGE_TYPE_CODE").val();
    var boxList = $("#" + table + " input[name=custs]");
	if(!boxList) return;
	boxList.each(function(){
		if(!this.checked){
			if($(this).attr('score') <= cent && cent != ""){
				$(this).parents("tr").css("display","");
				if($(this).attr('exchangetype')!=typecode && typecode != ""){
					$(this).parents("tr").css("display","none");
				}else{
					$(this).parents("tr").css("display","");
				}
			}else{
				$(this).parents("tr").css("display","none");	
			}
		}
	});
}

/**下拉列表选中时根据条件显示值*/
function showChooseType(table, boxName, type, typevalue, contype, convalue) {
	var boxList = $("#" + table + " input[name=custs]");
	if(!boxList) return;
	boxList.each(function(){
		if(!this.checked){
			if($(this).attr(type)!=typevalue && typevalue != ""){
				$(this).parents("tr").css("display","none");
			}else{
			
				if($(this).attr(contype)!=convalue && convalue != ""){
					$(this).parents("tr").css("display","none");
				}else{
					$(this).parents("tr").css("display","");
				}
			}
		}
	});
	
}


/**提交时的批串和验证*/
function submitSpellBunch(form,obj,boxName){
	if(!$.validate.verifyBox(form,boxName,'请选中要操作的数据!'))
	{
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
	    
	    if (extype == "A") {
	    	var card_id = $("#HH_CARD_ID").val();
	        var card_name = $("#HH_CARD_NAME").val();
	    	var re;
	    	re = /^([0-9]{10})$/;
	    	if (!re.test(card_id)) {
	    		alert("金鹏会员卡卡号为10位数字，请核实是否正确！");
	    		return false;
	    	}
	    	if (card_name == ""){
	    		alert("请输入金鹏会员卡姓名！");
	    		return false;
	    	}
	    }
	  }
	}
	
	//有价卡提交参数处理
	if(flag){
		var str = $.table.get("CardPart").getTableData(null,true);
		if(str.length == 0)
		{
			alert("请选择要兑换的充值卡！");
			return false;
		}		
		var total = 0;
		var cardNos = new Wade.DatasetList();
		for(var i=0;i<str.length;i++){
			var obj = $("#cardDel_"+i);
			if(obj.attr("checked")){
				var data = new Wade.DataMap();
				data.put('CARD_ID', str.get(i).get("CARD_ID"));//有价卡卡号
				cardNos.add(data);
				total = total + parseInt(str.get(i).get("CARD_VALUE"));//有价卡面值 单位分
			}
		}
		if( total != tempMoney){
			alert("兑换有价卡价值与实际兑换的不符，业务无法继续!");
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
		  alert("转赠号码不能与用户号码相同!");
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
		alert(error_info);
    });
}

/** 转目标号码之后的处理*/
function afterFunc(data){
  var flag = data.get("FLAG");
  if( flag == "false"){
  	 var mess = data.get("MESSAGE");
  	 alert(mess);
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
	  alert(mess);
	  return false;
	}
	var data = $.table.get("CardPart").getTableData(null,true);
	
	//参数1 传值域 参数2 调用java的方法 参数3 参数 参数4 返回结果刷新区域 
	$.beginPageLoading();
	$.ajax.submit('card', 'queryCardRes', '&DATA='+data+'&SERIAL_NUMBER='+sn, 'cardArea', function(data){
		initScoreTable(data);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}




/**有价卡表格的初始化*/
function initScoreTable(data){
	 
   var flag = data.get("FLAG");
  if( flag == "false"){
  	 var mess = data.get("MESSAGE");
  	 alert(mess);
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
