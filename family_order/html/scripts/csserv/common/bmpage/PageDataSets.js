//PageDataSet
function PageDataSet()
{
	this.dataSetId = "";
    this.dataSetType = "";
    this.dataSetKey = "";
    this.dataSetMethod = "";
}

//PageDataSets
function PageDataSets()
{
    this.sets = new Array();
    PageDataSets.prototype.addPageDataSet = PageDataSets_addPageDataSet;
}

function PageDataSets_addPageDataSet(set)
{
    this.sets[this.sets.length] = set;
}