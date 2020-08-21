$(function(){

	$("#tabs li").bind("tap",function(e){
		if($(e.target).hasClass("on")){
			return ;
		}
		$("#tabs li[class=on]").removeClass("on");
		$(e.target).addClass("on");
		var id = $(e.target).attr("id");
		var keyWords = $("#keyWords").val();
		var url ;
		if(id=="all"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchAll", "searchMenuAndProduct", "&keyWords="+keyWords);
		}else if(id=="phone"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchPhone", "", "&keyWords="+keyWords);
		}else if(id=="net"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchProduct", "", "&keyWords="+keyWords);
		}else if(id=="pre"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchProduct", "", "&keyWords="+keyWords);
		}else if(id=="dataflow"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchProduct", "", "&keyWords="+keyWords);
		}else if(id=="msg"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchProduct", "", "&keyWords="+keyWords);
		}else if(id=="pack"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchProduct", "", "&keyWords="+keyWords);
		}else if(id=="func"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchProduct", "", "&keyWords="+keyWords);
		}else if(id=="action"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchProduct", "", "&keyWords="+keyWords);
		}else if(id=="menu"){
			url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchMenu", "searchMenu", "&keyWords="+keyWords);
		}
		if(url) $("#main").attr("src",url);
		
	});
	
	var keyWords = $("#keyWords").val();
	var url = $.redirect.buildUrl("ngboss.frame.pc.common.SearchAll", "searchMenuAndProduct", "&keyWords="+keyWords);
	$("#main").attr("src",url);
});
