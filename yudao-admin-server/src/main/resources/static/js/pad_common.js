function GetQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}

$(function ()
{
    $("#timeNow").html(gettime(new Date()));

   setInterval(function ()
   {
       $("#timeNow").html(gettime(new Date()));
   },1000)

    //
    // window.AOV.getDorsInfo();

   nav_item_change()

})
//方法二(推荐):
function gettime(t){
    var _time=new Date(t);
    var   year=_time.getFullYear();//2017
    var   month=_time.getMonth()+1;//7
    var   date=_time.getDate();//10
    var   hour=_time.getHours();//10
    var   minute=_time.getMinutes();//56
    var   second=_time.getSeconds();//15
    var   week="";
    switch (_time.getDay()) {           // <!--time.getDay()获取后显示1~7阿拉伯数字-->
        case 1:
            week = "星期一";
            break;
        case 2:
            week = "星期二";
            break;
        case 3:
            week = "星期三";
            break;
        case 4:
            week = "星期四";
            break;
        case 5:
            week = "星期五";
            break;
        case 6:
            week = "星期六";
            break;
        default:
            week = "星期天";
    }

    return   year+"年"+month+"月"+date+"日"+hour+"时"+minute+"分"+second+"秒"+"&nbsp;&nbsp;"+week//这里自己按自己需要的格式拼接
}

function setDorsInfo(dors_title,dors_doctor,dors_time)
{
    $("#dors_title").html(dors_title);
    $("#dors_doctor").html(dors_doctor);
    $("#dors_time").html(dors_time);
}

function nav_item_change() {
    var cureurl = getRealPath();
    console.log("当前url：",cureurl);
    if (cureurl.indexOf("pad") != -1)
    {
        $("#index").attr('class','menue_item_select');
    }

    $(".pad_new_foot div").each(function(i,n){
        var classinfo = $(n).attr('id');
        if (cureurl.indexOf(classinfo) != -1 )
        {
            $(n).attr('class','menue_item_select');
        }
    });

    $(".pad_meet_foot div").each(function(i,n){
        var classinfo = $(n).attr('id');
        if (cureurl.indexOf(classinfo) != -1 )
        {
            $(n).attr('class','meet_menue_item_select');
        }
    });

}

function getRealPath(){
    //var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    // var localhostPath=curWwwPath.substring(0, curWwwPath.indexOf(pathName));
    // var projectName = pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    var list = pathName.split("/");

    pathName = list[list.length -1]

    return pathName;
}

let DECODER_SHXIT = "SHXIT";
let DECODER_LINKPI = "LINKPI";
