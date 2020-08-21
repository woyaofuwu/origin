if(typeof(UngotGiftQuery)=="undefined"){
	window["UngotGiftQuery"]=(function(){
		return{
			queryUngotGiftList: function(){
				if($.validate.verifyAll("cond")&&UngotGiftQuery.checkDate($('#cond_START_DATE').val(), $('#cond_END_DATE').val())){
					$.ajax.submit("cond","queryUngotGiftList",null,'ungotList', UngotGiftQuery.afterQuery);
				}
			},
			
			afterQuery:function(data){
				if(data&&data.length>0){
					$("#TipInfoPart").css("display","none");
				}
				else{
					$("#TipInfoPart").css("display","");
				}
			},
			
			checkStaffIdLength: function(object){
				var obj = $(object);
				staffId = obj.val().toUpperCase();
				if(staffId.length > 8){
					obj.value = staffId.substring(0,8);
				} else {
					obj.value = staffId;
				}
			},

			putStaffId: function(){
				var tradeStaffIdS = $("#cond_TRADE_STAFF_ID_S");
				var tradeStaffIdE = $("#cond_TRADE_STAFF_ID_E");
				if (tradeStaffIdE.val() == ""&&tradeStaffIdS.val() != "") {
					tradeStaffIdE.val(tradeStaffIdS.val());
				}
			},
			
			checkDate: function(startDate,endDate){
				var endArr = endDate.split("-");
				var startArr = startDate.split("-");
				var edate = new Date(endArr[0], parseInt(endArr[1]-1), endArr[2]);
				var sDate = new Date(startArr[0], parseInt(startArr[1]-1), startArr[2]);
				if( edate.getMonth() == sDate.getMonth() ){		
					return true;
				}
				else
				{
					alert("起始日期和终止日期必须在同一个月!");
					return false;
				}
			
			}
		}
	})();
}
