function nav_item_change() {
    var cureurl = getRealPath();
    //console.log("当前url：",cureurl);
   $("#navlist li").each(function(i,n){
       var classinfo = $(n).attr('class');
       if (cureurl.indexOf(classinfo) != -1)
       {
           $(n).attr('class','active');
       }
   });
}


function getRealPath(){
    //var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    // var localhostPath=curWwwPath.substring(0, curWwwPath.indexOf(pathName));
    // var projectName = pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return pathName;
}

