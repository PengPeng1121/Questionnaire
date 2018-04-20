/**
 * Created by asus on 2017/9/11.
 */

function  login() {
    $.ajax({
        type: "POST",
        dataType: 'json',
        url: springUrl +"/login/login",
        data: {
            userCode:$("#userCode").val(),
            password:$("#password").val()
        },
        success: function (data) {
            alert(1);
         console.log(data)
        },
    });
}
