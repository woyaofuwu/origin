
function getAllTableDataStatus(tbName,g){
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					
				    	 var j = getRowData(g, h + e,d,tbName);
							if (j) {
								     var _tag = Wade(i).attr("status");
								     if(_tag){
								    	 j.put("tag",_tag); 
								     }else{
								    	 j.put("tag","O");
								     }
									   b.add(j);
							}
				     
					
						j = null;
					
				});
	}
	c = null;
	d = null;
	return b;
}


function getAllTableData(tbName,g){
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					
				    	 var j = getRowData(g, h + e,d,tbName);
							if (j) {
									   b.add(j);
							}
				     
					
						j = null;
					
				});
	}
	c = null;
	d = null;
	return b;
}


/**
 * 获取table选中行数据
 */
function getCheckedTableData(tbName,g,chekboxName) {
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	//var d = $.table.get(tbName);
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					var isChecked = $('input[name='+checkboxname+']',this).attr("checked");
				     if(isChecked){
				    	 var j = getRowData(g, h + e,d,tbName);
							if (j) {
									   b.add(j);
							}
				     }
					
						j = null;
					
				});
	}
	c = null;
	d = null;
	return b;
}


function getUnCheckedTableData(tbName,g,chekboxName) {
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	//var d = $.table.get(tbName);
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					var isChecked = $('input[name='+checkboxname+']',this).attr("checked");
				     if(!isChecked){
				    	 var j = getRowData(g, h + e,d,tbName);
							if (j) {
									   b.add(j);
							}
				     }
					
						j = null;
					
				});
	}
	c = null;
	d = null;
	return b;
}



/**
 * 为了得到行里下接列表选中数据 写每一行的数据
 */
function getRowData(l,k,d,tabName) {
				var b=d.getTable();
				var g=k==null?parseInt(b.attr("selected"),10):k;
				var e=new Wade.DataMap();
				if(g>=0) {
					var m=b[0].rows[g-d.tabHeadSize+1];					if(m==null||m=="undefined") {
						return null;
					}if(l&&l!="") {
						var d=l.split(",");
						for(var f=0;f<d.length;f++) {
							if(d.tabHead.containsKey(d[f])) {
								var c=a(m.cells[parseInt(d.tabHead.get(d[f]),10)]);
								e.put(d[f],getColumnValue(c[0],tabName));
							}
						}d=null;
					}else {
						var h=d,j=d.tabHead;
						j.eachKey(function (o,i) {
							var n=m.cells[parseInt(j.get(o),10)];							e.put(o,getColumnValue(n,tabName));
						});
						j=null;
					}
				}return e;
			}
			
			
/**
 * 为了得到行里下接列表选中数据 写每一行的数据
 */			
function getColumnValue(d,tabName) {
				if(!d||!d.nodeType) {
					return ;
				}var e=Wade("#"+tabName+" th:nth-child("+(d.cellIndex+1)+")");
				var c=e.attr("type");
				var b=Wade.attr(d,"done");
				var f;
				if(!c||!b) {
					f=Wade("input:first",d).val();
		            //如查有下一个无素，且下一个元素是select  
					if(d.firstElementChild!=null && d.firstElementChild.nodeName=='SELECT'){
					   f=Wade("select:first",d).val();
					}
					if(f==undefined) {
						f=Wade.attr(d,"value");
						if(f==undefined) {
							f=(""+Wade.trim(Wade(d).text())).replace(/\r|\n/ig,"");
						}
					}
				}else {
					switch(c) {
						case "select":f=Wade("select:first",d).val();						break;
						case "datefield":f=Wade("input:first",d).val();
						break;
						case "popup":f=Wade("input[type=hidden]:first",d).val();
						break;
						default:f=Wade("input:first",d).val();
						break;
					}
				}return f;
			}	