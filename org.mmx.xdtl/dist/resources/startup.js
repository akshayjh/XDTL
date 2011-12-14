/*
 * Executed at engine startup. Any variables defined here will be globals. 
 */
var xdtlDateCode = java.lang.String.format("%1$tY%1$tm%1$td", new Array(new java.util.Date()));

function xdtlDirectoryExists(dir) {
	var f = new java.io.File(dir);
	return f.exists() && f.isDirectory();
}

function xdtlFileExists(fname) {
	var f = new java.io.File(fname);
	return f.exists() && f.isFile();
}

function xdtlArrayToArray(arr) {
	var a = new java.util.ArrayList(arr.length);
	for (var i = 0; i < arr.length; i++) {
		a.add(arr[i]);
	}
	return a;
}

function xdtlFileList(fname, filterstring) {
	var dir = new java.io.File(fname);
	if (filterstring) {
		var generic_obj = { filter: filterstring, accept: function (dir, name) { return name.indexOf(filterstring) > -1; } }
		var filter = new java.io.FilenameFilter(generic_obj);
	}
	else
		filter = null;
		
	return xdtlArrayToArray(dir.list(filter));
}

function xdtlArray() {
	var a = new java.util.ArrayList(arguments.length);

	for (var i = 0; i < arguments.length; i++) {
		a.add(arguments[i]);
	}
	return a;
}

function xdtlArray2(a) {
	for (var i = 0; i < arguments.length; i++) {
		a.add(arguments[i]);
	}
	return a;
}

function xdtlArrayToString (a) {
	var s='';
	s = a.toString();
	return s;
}

function xdtlRowsetToString(rowset) {
	if (rowset.size() == 0) return '';
	var result = '';
	
	for (i = 0; i < rowset.size(); i++) {
		result += rowset.get(i).join(',') + ','
	}
	return result;
}
