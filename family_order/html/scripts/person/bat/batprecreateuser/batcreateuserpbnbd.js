if(typeof(BatCreateUserBNBD)=="undefined"){
	window["BatCreateUserBNBD"]=function(){
		
	};
	var batcreateuserpbnbd = new BatCreateUserBNBD();
}

(function(){
	$.extend(BatCreateUserBNBD.prototype,{
		
		//集团产品编码校验
		checkPhone: function(objId)
		{
			var phoneObj = $("#"+objId);
			var desc = phoneObj.attr("desc");
			var phone = phoneObj.val();
			
			if(phone == "")
			{
				return false;
			}
			else
			{
				$.beginPageLoading("数据校验中...");
				var param = "&SERIAL_NUMBER="+phone;
				$.ajax.submit('','checkPhoneBNBD',param,'',function(data){
					$.endPageLoading();
					if( data.get('X_RESULTCODE') == '-1' )
					{
						var info = data.get("X_RESULTINFO");
						alert(info);
						phoneObj.val("");
						phoneObj.focus();
						return false;
					}
				},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
					phoneObj.val("");
					phoneObj.focus();
					return false;
				});
			}
		}
		
	});
})();
