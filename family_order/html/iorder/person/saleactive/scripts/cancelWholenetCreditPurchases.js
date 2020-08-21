function queryCancelData(){
	if(!$.validate.verifyAll("queryCanelCondPart")){
		return false;
	}
	$.beginPageLoading();
	$.ajax.submit('QueryCancelPart', 'queryCancelInfo', null, 'QueryCancelListPart,HiddenPart', function(data){
		$.endPageLoading();
		if(data.get('PROCTYP')=="1"){
			if($('#Cancel2_BTN').css("display")=="block"){
				$('#Cancel2_BTN').css("display","none");
			}
			$('#Cancel1_BTN').css("display","block");
		}else{
			if($('#Cancel1_BTN').css("display")=="block"){
				$('#Cancel1_BTN').css("display","none");
			}
			$('#Cancel2_BTN').css("display","block");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function CancelRequest(){
	var PROCTYP=$('#PROCTYP').val();
	var procTyp=$('#procTyp').val();
	if(PROCTYP!=procTyp){
		alert("申请类型不一致"); 
		return false;
	}
	if(PROCTYP=='2'){
		CancelRequestBefore();
	}
	$.beginPageLoading();
	$.ajax.submit('QueryCancelPart','cancelRequest',null,'QueryCancelListPart',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
	
}

function CancelRequestBefore(){
	var mplOrdDt=$('#MPL_ORD_DT').val();
	var date=mplOrdDt.substring(0,6);
	var date1=Number(date);
	var myDate=new Date();
	var month=myDate.getMonth()>8?(myDate.getMonth()+1).toString():'0' + (myDate.getMonth()+1);
	var date=String(myDate.getFullYear())+month;
	var date2=Number(date);
	if(date1<date2){
		alert("跨月订单不支持退货！");
		return false;
	}else if(date1>date2){
		alert("您输入的日期不对！");
	}
	
	
}