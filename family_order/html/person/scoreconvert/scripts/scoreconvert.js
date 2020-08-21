function queryconvert()
{
   if(!verifyAll('QueryPart'))
   {
	   return false;
   }
     $.ajax.submit('QueryPart', 'queryConvertRecord', null, 'RecordPart',function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }

function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'refreshParts,HidePart,ScorePopupPart,ScorePopupPart1', function(data){
		$('#cond_ITEM_POINT1').val('0');
		$('#cond_ITEM_POINT2').val('10000');
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryScoreGifts()
{
   if(!verifyAll('QueryPart'))
   {
	   return false;
   }
    $.ajax.submit('QueryPart,HidePart', 'queryGifts', null, 'ScoreTablePart',function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function cancelConvertGiftOrder(obj)
{
	var orderId = obj.getAttribute ("orderId") == null ? '' : obj.getAttribute ("orderId");
	var procState = obj.getAttribute ("procState") == null ? '' : obj.getAttribute ("procState");
	var orderSubId = obj.getAttribute ("orderSubId")== null ? '' : obj.getAttribute ("orderSubId");
	var orderSeq = obj.getAttribute ("orderSeq")== null ? '' : obj.getAttribute ("orderSeq");

	if ("01" != procState)//crm侧交易状态不为01-完成时，不能撤销订单
	{
		return;
	}
	
	if (confirm("确定撤销该订单?"))
	{	
		var param = '&ORDER_ID='+orderId+'&PROC_STATE='+procState+'&ORDER_SUB_ID='+orderSubId+'&ORDER_SEQ='+orderSeq;
		$.ajax.submit('QueryPart', 'cancelConvertGiftOrder', param, 'RecordPart',function(data){
			if(data.get('RESULT_MESSAGE') != '')
			{
				alert(data.get('RESULT_MESSAGE'));
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
  	  });
  	  }
	obj = null;
}
function queryConvertGiftOrder(obj)
{
	var orderId = obj.getAttribute ("orderId") == null ? '' : obj.getAttribute ("orderId");
	var procState = obj.getAttribute ("procState") == null ? '' : obj.getAttribute ("procState");
	var orderSubId = obj.getAttribute ("orderSubId")== null ? '' : obj.getAttribute ("orderSubId");
	var orderSeq = obj.getAttribute ("orderSeq")== null ? '' : obj.getAttribute ("orderSeq");
	if ("03" == procState)//CRM侧交易状态为03-废止时，不能查询订单
	{
		return;
	}
	if (confirm("查询后将会修改为订单的最新状态，您确定查询该订单？"))
	{
		var param = '&ORDER_ID='+orderId+'&PROC_STATE='+procState+'&ORDER_SUB_ID='+orderSubId+'&ORDER_SEQ='+orderSeq;
		$.ajax.submit('QueryPart', 'queryConvertGiftOrder', param, 'RecordPart',function(data){
			if(data.get('RESULT_MESSAGE') != '')
			{
				alert(data.get('RESULT_MESSAGE'));
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
  	  });
	}
	obj = null;
}

function scoreToConvert(obj)
{
	//清空弹出页面值
	
	$('#POP_ITEM_NAME').val('');
	$('#POP_ITEM_ID').val('');
	$('#POP_ITEM_TYPE').val('');
	$('#POP_ITEM_TYPE_NAME').val('');
	$('#POP_ITEM_NET_TYPE').val('');
	$('#POP_ITEM_NET_TYPE_NAME').val('');
	$('#POP_STORE_TYPE').val('');
	$('#POP_STORE_TYPE_NAME').val('');
	$('#POP_SCORE_VALUE').val('');
	$('#POP_ITEM_NUM').val('');
	$('#POP_SCORE_SUM').val('');
	
	
	
	var itemName = obj.getAttribute("itemName");
	var itemType = obj.getAttribute("itemType");
	var itemTypeName = obj.getAttribute("itemTypeName");
	var storeType = obj.getAttribute("storeType");
	var itemNetType = obj.getAttribute("itemNetType");
	var itemNetTypeName = obj.getAttribute("itemNetTypeName");
	var storeTypeName = obj.getAttribute("storeTypeName");
	var indexNum = obj.getAttribute("idValue");
	var giftId = obj.getAttribute("giftId");
	var scoreValue = obj.getAttribute("scoreValue");
	var userScore = $('#USER_SCORE').val();
	var exp = '^[0-9]*[1-9][0-9]*$';
	var textName = 'ITEM_NUM'+indexNum;
	var itemNum = $('#'+textName).val();
	var exchangeTag = $('#cond_EXCHANGE_TAG').val();
	
	/* 兑换数量格式匹配 */
	if (!itemNum.match(exp))
	{
		alert('兑换数量格式错误，请重新填写！');
		$('#'+textName).focus();
		$('#'+textName).val('1');
		return ;
	}

	/* 凤凰手机报兑换数量不能选择 */
	
	if ((giftId == '358470' || giftId == '358471' || giftId == '358472' || giftId == '358473') && itemNum != 1)
	{
		alert('凤凰手机报兑换数量只能为 1 .');
		$('#'+textName).focus();
		$('#'+textName).val('1');
		return ;
	}
	
	/* 判断用户积分是否够数 */
	var scoreSum = itemNum * scoreValue;

	if(scoreSum > userScore)
	{
		alert('用户现有积分【'+userScore + '】小于兑换礼品所需积分【'+ scoreSum +' 】，请重新选择！');
		$('#'+textName).val('1');
		return ;
	}
	
	$('#popupPart').css('display','');
	
	//设置弹出页面值
	$('#POP_ITEM_NAME').val(itemName);
	$('#POP_ITEM_ID').val(giftId);
	$('#POP_ITEM_TYPE').val(itemType);
	$('#POP_ITEM_TYPE_NAME').val(itemTypeName);
	$('#POP_ITEM_NET_TYPE').val(itemNetType);
	$('#POP_ITEM_NET_TYPE_NAME').val(itemNetTypeName);
	$('#POP_STORE_TYPE').val(storeType);
	$('#POP_STORE_TYPE_NAME').val(storeTypeName);
	$('#POP_SCORE_VALUE').val(scoreValue);
	$('#POP_ITEM_NUM').val(itemNum);
	$('#POP_SCORE_SUM').val(scoreSum);
	

	if(itemType == '01' || storeType == '3')
	{
		$('#deliveryMsgArea').css('display','none');
	}
	else
	{
		$('#deliveryMsgArea').css('display','');//显示实物配送信息区域
	}
	
	obj = null;

}



function queryCity(obj)
{
	var provinceCode = $(obj).val();
     $.ajax.submit('', 'queryCity', '&PROVINCE_CODE='+provinceCode, 'PopCityPart,PoPdistrictArea',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryDistrict(obj)
{
	var cityCode = $(obj).val();
	
     $.ajax.submit('', 'queryDistrict', '&CITY_CODE='+cityCode, 'PoPdistrictArea',function(data){

		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function checkBeforeSubmit()
{
	var itemType = $('#POP_ITEM_TYPE').val();
	var storeType = $('#POP_STORE_TYPE').val();
	
   if(!(itemType == '01' || storeType == '3'))
   {
   	     if(!verifyAll('ScorePopupPart1'))
   		 {
	  		 return false;
  		 }
  		 else
  		 {
  		 	//调查询接口
			var itemName = $('#POP_ITEM_NAME').val();
			var itemId = $('#POP_ITEM_ID').val();
			var delivProvince = $('#POP_DELIV_PROVINCE').val();
			var itemNum = $('#POP_ITEM_NUM').val();
			
			//查询时增加地市、区县条件
			var itemCity = $('#POP_CITY').val();
			var itemDistrict = $('#POP_DISTRICT').val();
   			var param = '&ITEM_ID='+itemId+'&ITEM_NAME='+itemName+'&DELIV_PROVINCE='+delivProvince+'&CITY_ID='+itemCity+'&DISTRICT='+itemDistrict+'&ITEM_NUM='+itemNum;
   			$.ajax.submit('', 'queryGiftCount', param, '',function(data){
	    		var alert_info = data.get('ALERT_INFO');
	    		if(alert_info != '0')
	    		{
	    			alert(alert_info);
	    			return false;
	    		}
	    		else
	    		{
    				$('#popupPart').css('display','none');
					$.cssubmit.submitTrade();
	    		}

			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
    		},{async:false});
  		 }
   }
	else
	{
		return true;
	}
}


