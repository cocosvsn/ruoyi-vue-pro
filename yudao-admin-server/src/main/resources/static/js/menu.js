let params = "?id=" + GetQueryString('id') + "&ip=" + GetQueryString('ip') + "&port=" + GetQueryString('port') + "&protocol=" + GetQueryString('protocol') + "&category_id=" + GetQueryString('category_id');
    
var beforUrl = ""
let isRecord = false;

function goMain() {
    try {
        if (isRecord) {
        layer.alert("正在录像中, 请先停止录像!");
        return;
        }
    } catch (e) { console.error(e); }
    window.location.href = "index.html" + params;
}

function goScreen() {
    try {
        if (isRecord) {
            layer.alert("正在录像中, 请先停止录像!");
            return;
        }
    } catch (e) { console.error(e); }
    window.location.href = "screen.html" + params;
}

function goMedia() {
    try {
        if (isRecord) {
            layer.alert("正在录像中, 请先停止录像!");
            return;
        }
    } catch (e) { console.error(e); }
    try {
        AOV.hiddenPreview();
    } catch(e) { console.error(e); }
    window.location.href = "media.html" + params;
}

function goTeach() {
    try {
        if (isRecord) {
            layer.alert("正在录像中, 请先停止录像!");
            return;
        }
    } catch (e) { console.error(e); }
    window.location.href = "share.html" + params;
}

function goPatient() {
    try {
        if (isRecord) {
            layer.alert("正在录像中, 请先停止录像!");
            return;
        }
    } catch (e) { console.error(e); }
    try {
        AOV.hiddenPreview();
    } catch (e) { console.error(e); }
    window.location.href = "patient.html" + params;
}

function goBedlight() {
    try {
        if (isRecord) {
            layer.alert("正在录像中, 请先停止录像!");
            return;
        }
    } catch (e) { console.error(e); }
    try {
        AOV.hiddenPreview();
    } catch (e) { console.error(e); }
    window.location.href = "sugrgery.html" + params;
}

function goBed() {
    try {
        if (isRecord) {
            layer.alert("正在录像中, 请先停止录像!");
            return;
        }
    } catch (e) { console.error(e); }
    try {
        AOV.hiddenPreview();
    } catch (e) { console.error(e); }
    window.location.href = "bed.html" + params;
}

function goLight() {
    try {
        if (isRecord) {
            layer.alert("正在录像中, 请先停止录像!");
            return;
        }
    } catch (e) { console.error(e); }
    try {
        AOV.hiddenPreview();
    } catch (e) { console.error(e); }
    window.location.href = "light.html" + params;
}

function goSystem() {
    try {
        if (isRecord) {
            layer.alert("正在录像中, 请先停止录像!");
            return;
        }
    } catch (e) { console.error(e); }
    try {
        AOV.hiddenPreview();
    } catch (e) { console.error(e); }
    window.location.href = "set.html" + params;
}

// 手术室应答示教室连接后，跳转至连线中界面。
function toastScreenView() {
// layer.open({
//   type: 2,
//   title: '远程连线',
//   skin: 'layui-layer-molv', //加上边框
//   area: [window.innerWidth.toString() + "px", window.innerHeight.toString() + "px"], //宽高
//   content: "online.html" + params,
//   closeBtn: 0,
//   success: function (layero, index) {

//   }
// });
    try {
        AOV.hiddenPreview();
    } catch (e) { console.error(e); }
    window.location.href = "online.html" + params;
}

function changeVideoViewpostion() {
    AOV.hiddenPreview()

    //如果页面有videoview
    var origanl_x = window.innerWidth / 2;
    var origanl_y = window.innerHeight / 2;
    var width = $("#videoView").width();
    var height = $("#videoView").height();
    var orignal_x_video = $("#videoView").offset().left + width / 2;
    var orignal_y_video = $("#videoView").offset().top + height / 2;

    var offset_x = orignal_x_video - origanl_x;
    var offset_y = orignal_y_video - origanl_y;

    var bottom = window.innerHeight - $("#videoView").offset().top - height;

    window.AOV.playVideo(beforUrl, $("#videoView").offset().left, $("#videoView").offset().top, bottom - 1, width + 2, height + 1);
}