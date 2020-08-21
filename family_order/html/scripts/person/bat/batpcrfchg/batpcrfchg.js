if(typeof(BATPCRFCHG)=="undefined"){  
	window["BATPCRFCHG"]=function(){
		
	};
	var batpcrfchg = new BATPCRFCHG();
}

var i=0;

(function(){
	

	$.extend(BATPCRFCHG.prototype,{
		
		addline : function(obj){
			var parent = $("#pctrValue").parent('tbody');
			var num = parent.children('tr').length;
			var num_check = $("input[type='checkbox']:checked").length;
			if(num == num_check){
				if(i==1){i++;}
				
				var line = $("#pctrValue").clone(true);
				line.find(".checkbox").attr('checked',false);
				line.find("#START_DATE").attr('maxName','END_DATE'+i);
				line.find("#START_DATE").attr('name','START_DATE'+i);
				line.find("#START_DATE").attr('id','START_DATE'+i);
				line.find("#END_DATE").attr('minName','START_DATE'+i);
				line.find("#END_DATE").attr('name','END_DATE'+i);
				line.find("#END_DATE").attr('id','END_DATE'+i);
				line.attr('id','pctrValue'+i);
				
				i++;
				parent.append(line);
//				parent.addrow();
			}
//			console.log(('123'));
			if(num>1){
				var box = $(obj);
				if(box.attr('checked')=='checked'||box.attr('checked')== true){
					
				}else{
					var lines = box.parent().parent();
					lines.attr("id","delete");
					$("#delete").remove();
				}
				
			}
			
		}
		
	});
		
	
})();

