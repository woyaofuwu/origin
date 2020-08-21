if(typeof(TerminalBookQry)=="undefined"){
	window["TerminalBookQry"]=(function(){
		return{
			queryTerminalBookInfo: function(){
				if($.validate.verifyAll("cond")&&TerminalBookQry.checkDateRange(31)){
					$.beginPageLoading("查询中...");
					$.ajax.submit("cond","qryTerminalBookInfo",null,'terminalList', TerminalBookQry.afterQuery);
				}
			},
			
			afterQuery:function(data){
				$.endPageLoading();
				if(data&&data.length>0){
					$("#TipInfoPart").css("display","none");
				}
				else{
					$("#TipInfoPart").css("display","");
				}
			},
			
			checkDateRange:function(range){
				var startDate = $("#cond_START_DATE").val();
				var endDate = $("#cond_END_DATE").val();
				var startArray = startDate.split("-");
				var endArray = endDate.split("-");
				var dateStart = new Date(startArray[0],startArray[1]-1,startArray[2]);
				var endStart = new Date(endArray[0],endArray[1]-1,endArray[2]);
				var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) ;
				if(day > range){
					alert( "【起始、终止】日期时间段不能超过"+range+"天~");
					return false;
				}
				return true;
			}
		}
	})();
}
