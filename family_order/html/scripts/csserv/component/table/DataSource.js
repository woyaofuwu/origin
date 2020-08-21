(function($){
	$.ListDataSourceFactory=function(tableGrid,listDataSourceStr){
		this.List = new Array();
		var dataSourceList = new $.DatasetList(listDataSourceStr);
		this.List = new Array(dataSourceList.length);
		for(var i = 0; i < dataSourceList.length; i++){
			this.List[i] = new $.ListDataSource(this,dataSourceList.get(i));
		}
		this.RowSet = tableGrid;
	};
	$.extend($.ListDataSourceFactory.prototype,{
		getRowSet:function(){
		    return this.RowSet;
	    },
	    setRowSet:function(rowSet){
	    	this.RowSet = rowSet;
	    },
	    active:function(){
	    	for(var i =0;i< this.List.length;i++){
	    		this.List[i].active();
	    	}
	    },
	    count:function(){
	    	if(this.List){
	    		return this.List.length;
	    	}else{
	    		return 0;
	    	}
	    },
	    get:function(i){
	    	if((this.List)&&(i>=0)&&(i <this.List.length)){
	    	    return this.List[i];
	    	}else{
	    		return null;
	    	}
	    },
	    find:function(name){
	    	for(var i =0;i< this.List.length;i++){ 
	    		if (this.List[i].getName() == name){
	    			return this.List[i];
	    		}
	        }
	        return null;
	    },
	    replace:function(aListDataSource){
	    	if (aListDataSource == null) return;
	        var name = aListDataSource.getName();
	        for(var i =0;i< this.List.length;i++){ 
	        	if (this.List[i].getName() == name){
	        		this.List[i] = aListDataSource;	
	        	}
	        }
	    }
	});
	$.ListDataSource=function(factory,dataSource){
		this.Factory = factory;
		this.ListBoxData =  new $.DatasetList();
		this.Name = dataSource.get("Name");
		this.Title = dataSource.get("Title");
		this.Listener = dataSource.get("Listener");
		this.ValueAttr = dataSource.get("ValueAttr");
		this.TextAttr = dataSource.get("TextAttr");
		this.ParameterList = dataSource.get("ListParameter");
	};
	$.extend($.ListDataSource.prototype,{
		getName:function(){
		   return this.Name;
	    },
	    getTitle:function(){
	    	return this.Title;
	    },
	    getValueAttr:function(){
	    	return this.ValueAttr;
	    },
	    getTextAttr:function(){
	    	return this.TextAttr;
	    },
	    getListBoxData:function(){
	    	if(!this.ListBoxData || this.ListBoxData.length <= 0){
	    		var aListDataSource = this;
		    	var params = this.buildPostData();
		    	$.beginPageLoading('Loading.....');
		    	$.ajax.post(null,this.Listener,params,null,function(data){
		    		aListDataSource.setListBoxData(data);
		    		$.endPageLoading();
		    	},function(e,i){
					$.endPageLoading();
				},{async:false});
	    	}	
	    	return this.ListBoxData;
	    },
	    setListBoxData:function(listBoxData){
	    	this.ListBoxData = listBoxData;
	    },
	    findTextById:function(id){
	    	var aText = "";
	    	var listBoxData = this.ListBoxData;
	    	for(var i = 0; i < listBoxData.length; i++){
	    		var aId = listBoxData.get(i,this.ValueAttr);
	    		if(aId == id){
	    			aText = listBoxData.get(i,this.TextAttr);
	    			break;
	    		}
	    	}
	    	return aText;
	    },
	    buildPostData:function(){
	    	var params = "";
	    	for(var i=0;i<this.ParameterList.length;i++){
	    		var name = this.ParameterList.get(i,"Name");
	    		var value = this.ParameterList.get(i,"Value");
	    		params += "&"+name+"="+value;
	    	}
	    	return params;
	    }
	});
	$.Option=function(){
		this.Id=null;
		this.Text=null;
	}
})(Wade);