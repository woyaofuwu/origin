//360资料综合查询 用户共享信息查询
function onQueryUserShareInfo() {
	if(!infoIsNull()){
		return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart,QueryListPart1,TipInfoPart', function(data){
			if(data.get('SHOWTIP') == 'YES'){
				$("#TipInfo").css("display", "");
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }


function onQueryAccountInfo() {
	if(!infoIsNull()){
		return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	   });
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
   });
}

function onQueryProductInfo() {
	if(!infoIsNull()){
		return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	
	//分页参数
	var ProductNav_current = 1;//当前显示第1页
	var ProductNav_pagesize = 10;//每页显示条数
	var pagin = 
	 '&pagin=ProductNav&ProductNav_count=0&ProductNav_current='+ProductNav_current+'&ProductNav_pagesize='+ProductNav_pagesize+'&ProductNav_needcount=false';
	 
	$.ajax.submit(null, 'setCommInfo', param+pagin, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	  });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
  });
}
function onQueryUserServiceInfo() {
	if(!infoIsNull()){
		return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	  });
	  
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
  });
}
function onQueryUserResourceInfo() {
	if(!infoIsNull()){
		return false;
	}
	 	
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	  });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
  });
}
function onQueryDiscountInfo() {
	if(!infoIsNull()){
		return false;
	}
	 	
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }
//360营销活动信息查询 
function onQuerySaleActiveInfo() {
	if(!infoIsNull()){
		return false;
	}
	 	
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }


 
 function onQueryCustContactInfo() {
 	if(!infoIsNull()){
		return false;
	}
 	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart,QueryCondPart2', function(data){
		 
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }
 
  function onQueryCustComplaintInfo() {
  if(!infoIsNull()){
	return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'init', param, 'QueryListPart,QueryCondPart,QueryCondPart1', function(data){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }
 
 
function onQueryRelationInfo() {
	if(!infoIsNull()){
		return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	   });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
   });
}
function onQueryPostInfo() {
	if(!infoIsNull()){
	return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'queryInfo', param, 'QueryListPart,QueryCondPart', function(data){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
  });
}
function onQueryScoreInfo() {
	if(!infoIsNull()){
	return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'queryInfo', param, 'QueryListPart,QueryCondPart', function(data){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
 });
}
function onQueryCustContactMgr() {
	if(!infoIsNull()){
	return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'queryInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
});
}

function onForegiftInfo() {
	if(!infoIsNull()){
	return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'queryInfo', param, 'QueryListPart,QueryCondPart', function(data){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
});
}

function onQueryCustInfo() {
	if (!infoIsNull()) {
		alert('返回为空');
		return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
			+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&SERIAL_NUMBER='
			+ parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'queryInfo', param, 'QueryListPart', function() {

	}, function(error_code, error_info) {
		$.endPageLoading();
		alert(error_info);
	});
}

//huanghui
function onCustHisInfo()
{
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
				+ '&CUST_ID=' + parent.$("#CUST_ID").val() 
				+ '&SERIAL_NUMBER='+ parent.$("#SERIAL_NUMBER").val();
	alert('用于调试信息，请勿在意！add hui' + param);
	$.ajax.submit(null, 'init', param, 'QueryListPart', function() {

	}, function(error_code, error_info) {
		$.endPageLoading();
	});
}

function onTradeHistoryInfo()
{
	if(!infoIsNull()){
	return false;
	}
	var param = '&USER_ID=' + parent.$("#USER_ID").val()+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'init', param, 'QueryListPart,QueryCondPart,QueryCondPart1,mytab', function(data){
		thQuery();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

//360营销活动信息查询 
function onQueryNpUserInfo() {
	if(!infoIsNull()){
		return false;
	}
	 	
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }

//360宽带信息查询 
function onQueryUserWideNetInfo() {
	if(!infoIsNull()){
		return false;
	}
	 	
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart,QueryListPart1,TipInfoPart', function(data){
			$("#TipInfo").css("display", "none");

		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }

//360客户历史日志查询
function onCustomerHistoryInfo() {
	if(!infoIsNull()){
		return false;
	}
	 	
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryThServerInfos', '', 'refreshArea', function(data){
			
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }

//360用户信用等级信息查询 
function onQueryCreditClassInfo() {
	if(!infoIsNull()){
		return false;
	}
	 	
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }

//同步导出
function exportData() {
	$.beginPageLoading("开始导出...");
	ajaxSubmit('QueryCondPart','exportDataToFile',null,null,function(d){
		if (d && d.get("url")) {
			window.open(d.get("url"),"_self");
		}
		$.endPageLoading();
	},function(e,i){
		$.showErrorMessage("导出失败");
		$.endPageLoading();
	});
}
 

//品牌资费推送器，优惠页签里面调用。
function send10086Mesage(){
    //alert("in_mode_code:"+$("#IN_MODE_CODE").val());
	if ($("#IN_MODE_CODE1").val() == "1") {
		popupPage('userview.SendMessage', 'pageInit', "&refresh=true&IN_MODE_CODE=1&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()
		+"&EPARCHY_CODE="+$("#condition_EPARCHY_CODE").value+"&MESSAGE_CONTENT=品牌资费传送器",'\u54c1\u724c\u8d44\u8d39\u63a8\u9001\u5668', '500', '350' );
	} else {
	    alert("请先查询用户的基本资料再推送品牌资费短信！");
	    return;
	}
}


function infoIsNull(){
	if(parent.$("#SERIAL_NUMBER").val()==""||parent.$("#SERIAL_NUMBER").val()==null){
		parent.mytab.switchTo("用户基本信息");
		return false;
	}
	return true;
}

function onTradeHistoryInfoHis()
{
	if(!infoIsNull()){
	return false;
	}
	var param = '&USER_ID=' + parent.$("#USER_ID").val()+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	$.ajax.submit(null, 'init', param, 'QueryListPart,QueryCondPart,QueryCondPart1,mytab', function(data){

	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function onQueryPlatServInfo() {
	if(!infoIsNull()){
		return false;
	}
	 	
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'QueryCondPart', function(data){
		$.ajax.submit('QueryCondPart', 'queryPlatServInfo', '', 'QueryListPart', function(data){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }

