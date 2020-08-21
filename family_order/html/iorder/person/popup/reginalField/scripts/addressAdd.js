if(typeof(AddressAdd)=="undefined"){window["AddressAdd"]=function(){};var addressAdd = new AddressAdd();}
(function(){
	
	$.extend(AddressAdd.prototype,{
		clearCache: function(data){
			addressAdd.isShowAllCancel = false;
			addressAdd.isShowSwitch = false;
		},
		optionEnterAction:function(data){
//			var serviceName = $("#Ul_Search_addressSearch li[class=focus]").attr("REGION_NAME");
//			$("#addressSearch").val(serviceName);
//			$("#Div_Search_addressSearch").css("visibility","hidden");
			var serviceName = $("#addressSearch").val();
			ajaxSubmit(this,'queryReginalByRegional','&REGION_NAME='+serviceName,'DataTablePart',function(data){
//				$("tr[REGION_SEL]").addClass("on");
			},null,null);
			
			ajaxSubmit(this,'queryDeviceByReginal','&REGION_NAME='+serviceName,'editPart',null,null,null);
		}
	});
})();

  