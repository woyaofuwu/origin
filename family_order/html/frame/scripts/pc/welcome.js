(function($){

	$.wel = {
		init:function(){
			$("#menu_l1_ul li").bind("click", function(){
				var l2 = $.attr(this, "l2");
				if(l2){
					var el = $("#menu_l2_" + l2);
					if(el && el.length){
						$("#menu_l1_ul li").attr("className", "link");
						$.attr(this, "className", "link on");
						
						$("#menu_ct div[id^=menu_l2]").css("display", "none");
						el.css("display", "");
					}
				}
			});
			
			var def = $("#menu_ct div[id^=menu_l2]:visible");
			if(def && def.length){
				var id = $.attr(def[0], "id");
				if(id && $.isString(id) && id.indexOf("menu_l2_") == 0){
					var l2 = id.substring("menu_l2_".length);
					var li = $("#menu_l1_ul li[l2=" + l2 + "]");
					if(li && li.length){
					 	li.attr("className", "link on");
					}
				}
			}
		}
	};
	
	$(function(){
		$.wel.init();
	});
})(Wade);