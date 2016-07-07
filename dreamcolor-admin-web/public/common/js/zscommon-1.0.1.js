var zscommon = {};

zscommon.strTrim = function(str){
	str = str.replace(/^\s+|\s+$/g,"");
	return str;
}

zscommon.renderPaging = function(divId , titlePre, titleNext, titleFirst, titleLast, keyword) {
	var divTag = document.getElementById(divId);
	if(divTag == null){
		alert(divId + " is " + divTag);
		return;
	}
	
	divTag.innerHTML = zscommon.strTrim(divTag.innerHTML);
	if(divTag.innerHTML==""){
		return;
	}
	
	var data = eval("("+divTag.innerHTML+")");
	divTag.innerHTML = "";
	if (data.totalRecord == '0'){
		return;
	}
	if (data.pageSize == '0'){
		return;
	}
	data.currentPage = parseInt(data.currentPage);
	data.pageSize = parseInt(data.pageSize);
	data.numDisplay = parseInt(data.numDisplay);

	var totalpage = parseInt(data.totalRecord / data.pageSize);
	if (data.totalRecord % data.pageSize > 0)
		totalpage += 1;
	if(data.currentPage < 0 )
		data.currentPage = 1;
	if (data.currentPage > totalpage)
		data.currentPage = totalpage;    

	var paging = "", next = "", pre = "";
	var pos_start, pos_end;

	var url = data.action + "?";
	if(keyword != ''){
		url += keyword;
	}
	url += "&page=";
	url = url.replace('?&', '?');

	var first = '<li class="first"><a href="'+ url + 1 +'">&lt;&lt; '+titleFirst+'</a></li>';
	var last = '<li class="last"><a href="'+ url + totalpage +'">'+titleLast+' &gt;&gt;</a></li>';
	
	if (data.numDisplay >= totalpage) {
		pos_start = 1;
		pos_end = totalpage;
	}else{
		var half = parseInt(data.numDisplay / 2);	
		if(data.numDisplay % 2 == 1){
			++half;
		}
		if (data.currentPage <= half) {
			pos_start = 1;
		}else {
			if (data.currentPage + half > totalpage) {
				pos_start = totalpage - data.numDisplay + 1;
			} else {
				pos_start = data.currentPage - half + 1;
			}
		}
		pos_end = data.numDisplay;
	}
	if ((data.currentPage - 1) > 0) {
		pre = '<li class="prev"><a href="'+ url + (data.currentPage - 1) +'">&lt;'+titlePre+'</a></li>';
	} else {		
		first = "<li class='first disabled'><a href='#'>&lt;&lt; "+titleFirst+"</a></li>";
		pre = '<li class="prev disabled"><a href="#">&lt;'+titlePre+'</a></li>';
	}    
	if ((data.currentPage + 1) <= totalpage) {		
		next =	'<li class="next"><a href="'+ url + (data.currentPage + 1) +'">'+titleNext+'&gt;</a></li>';
	} else {
		last = "<li class='last disabled'><a href='#'>"+titleLast+" &gt;&gt;</a></li>";		
		next =	'<li class="next disabled"><a href="#">'+titleNext+'&gt;</a></li>';
	}
	for (var i = 0; i < pos_end; i++) {
		//		if (i != 0) {
		//			paging += "&nbsp;-&nbsp;";
		//		}
		if (pos_start == data.currentPage) {
			//			paging += "<strong class='current'>" + pos_start + "</strong>";
			paging += '<li class="active"><a href="#">'+ pos_start + '</a></li>';
		}else {
			//			paging += "<a href='" + url + pos_start + "' class='page' style='cursor: pointer; color: rgb(0, 102, 204);' >" + pos_start + "</a>";
			paging += '<li><a href="'+ url + pos_start +'">'+ pos_start + '</a></li>';
		}
		pos_start++;
	}
	//	var displayTotal = "&nbsp;<strong class='current'>("+ data.currentPage + "/" + totalpage + ")</strong>";
	var displayTotal = "<li class='disabled'><a>("+ data.currentPage + "/" + totalpage + ")</a></li>";

	paging = '<ul class="pagination">'+first +pre + paging + next + last+ displayTotal+'</ul>';
	divTag.innerHTML = paging;
	$("#"+divId).css("display","block");
}
