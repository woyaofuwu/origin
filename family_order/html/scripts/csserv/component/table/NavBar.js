(function(){
	var navbar={
		trunNavPage:function(pagin){
			var id=pagin.attr("paginId");
			var tableId = pagin.attr("tableId");
			var navbar =$("#"+id);
			if (pagin.attr("class").indexOf("e_dis") != -1){
				return;
			}
			var condparams = navbar.attr("condparams");
			var params= $.getNavParams(pagin);
			params += "&"+id+"_needcount=false";
			params += "&"+condparams;	
			$.beginPageLoading(navbar.attr("loadingtxt"));
			$.ajax.submit(null,null,params,navbar.attr("part"),function(d){
				$.endPageLoading();
				$.tableManager.get(tableId).initial(); //重新初始化表格实例
				$("#"+id).attr("condparams",condparams);
			},function(code,info){
				$.endPageLoading();
				$("#"+id).attr("condparams",condparams);
				alert("Erorr Code:" + code + "\r\n" + info);
			});
		},
		initNavPage:function(name, id){
			debugger;
		},
		getNavParams:function(pagin, cur) {
			var count,current,pagesize,needcount,pagecount,name=pagin.attr("paginId");
			var id=pagin.attr("paginId");
			var op=pagin.attr("op");
			var navbar =$("#"+id);
			count=navbar.attr("count");
			current=(cur == null ? navbar.attr("current") : cur);
			pagesize=navbar.attr("pagesize");
			pagecount=navbar.attr("pagecount");
			if (cur == null) {
				if(op == "first") {
					current=1;
				} else if (op == "pre") {
					current=parseInt(current,10)-1;
				} else if (op == "next") {
					current=parseInt(current,10)+1;
				} else if (op == "last"){
					current=parseInt(pagecount,10);
				}
			}
			return params="&pagin="+name+"&"+name+"_count="+count+"&"+name+"_current="+current+"&"+name+"_pagesize="+pagesize;
		}
	};
	
	Wade.extend(navbar);
	Wade.extend(window,navbar);
	
})();
