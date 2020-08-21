

function queryNetBookInfos(){
	
	if(!$.validate.verifyAll("QueryCondPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart,recordNav','qryNetBookInfos','','QueryListPart',
			function(data){ 
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

function initNetBookDetail(id){
	
	$('#popup').css('display','')
	var params = "&TRADE_ID=" + id;
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','initNetBookDetail',params,'bookDealPart',function(data){
/*		if($("#cond_PARA_CODE1").val()!="0"){
			$("#submitButton").attr("disabled", true);
			$("#submitButton").attr("className", "e_button-form e_dis");
		}else{
			$("#submitButton").attr("disabled", false);
			$("#submitButton").attr("className", "e_button-form");
		}*/
		
		/**
		 *  REQ201911280007关于优化“NGBOSS-网厅预约业务处理界面”的需求
		 *  @author liwei29
		 *  @date 2019-12-12
		 *  订单状态为“第1次联系失败、第2次联系失败、第3次联系失败户”时，订单可以修改状态
		 */
		if(data.get("cond_BOOK_STATUS") =="1" ||data.get("cond_BOOK_STATUS") =="2"){
			
			$("#cond_PARA_CODE1").attr("disabled",true);
		}else{
			$("#cond_PARA_CODE1").attr("disabled",false);
		}
		
		/**
		 *工单状态为 未处理,处理意见可以输入
		 */
		//需要开放全状态都可以输入处理意见
		/**if(data.get("cond_BOOK_STATUS") == "0"){
			//未处理
			$("#cond_RSRV_STR5").attr("disabled",false);
		}else{
			$("#cond_RSRV_STR5").attr("disabled",true);
		}**/
		
		
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function dealNetBook(){
	var book_id = $("#cond_TRADE_ID").val();
	var BOOK_STATUS = $("#cond_PARA_CODE1").val();
	var cur_BOOK_STATUS =$("#cur_BOOK_STATUS").val();
	//处理意见
	var  RSRV_STR5=$("#cond_RSRV_STR5").val();
/*	if("1" == cur_BOOK_STATUS){
		if(!confirm("该工单状态为正常处理，确认要改变工单状态吗？"))
			return;
		
	}*/
	if(cur_BOOK_STATUS == cond_BOOK_STATUS){
		if(!confirm("该工单状态没有改变，确认要处理吗？"))
			return;
	}
	if(RSRV_STR5 != ''){
		if(RSRV_STR5.length >50){
			alert("处理意见:长度不能大于50");
			return;
		}
	}
	
	$.ajax.submit('','dealNetBook', '&trade_id='+book_id+'&book_status='+BOOK_STATUS+'&RSRV_STR5='+RSRV_STR5,'',function(data){
		alert("处理成功！");
		$('#popup').css('display','none');
		queryNetBookInfos();
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function openPush(){
	var book_id = $("#cond_TRADE_ID").val();
	
	$.ajax.submit('', '',book_id, null, function(data1){
		
			var titleName = '宽带开户(新)';
			var titleUrl  = '/order/iorder?service=page/broadband.widenet.createwideuser.MergeWideUserCreateNew&listener=onInitTrade&WIDE_TYPE=MERGE';
			titleUrl = titleUrl + '&book_id=' + $("#cond_TRADE_ID").val();
			$.nav.openByUrl(titleName,titleUrl);

		 
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}