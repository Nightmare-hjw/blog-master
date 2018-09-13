<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.jax.blog.model.User" %>
<%@ page import="com.jax.blog.constant.WebConst" %>

<!DOCTYPE HTML>
<html lang="zh-CN">
<head>
    <title>个人博客系统后台管理</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/Logo_40.png" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/plugins/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/plugins/jquery-easyui-1.3.3/themes/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/plugins/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/plugins/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/plugins/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>

    <script type="text/javascript">
        function openTab(text,url,iconCls){
            if($("#tabs").tabs("exists",text)){
                $("#tabs").tabs("select",text);
            }else{
                var content="<iframe frameborder=0 scrolling='auto' style='width:100%; height:100%'  src='${pageContext.request.contextPath}/views/admin/"+url+"'></iframe>";
                $("#tabs").tabs("add",{
                    title:text,
                    iconCls:iconCls,
                    closable:true,
                    content:content
                });
                $('#tabs').tabs('getSelected').css('width', 'auto');
            }
        }

        function openPasswordModifyDialog() {
            $("#dlg").dialog("open").dialog("setTitle", "修改密码");
        }

        function modifyPassword() {
            $("#fm").form("submit",{
                url: "${pageContext.request.contextPath}/admin/password/modify.do",
                onSubmit: function() {
                    var newPassword = $("#password").val();
                    var newPassword2 = $("#password2").val();
                    if(!$(this).form("validate")) {
                        return false; //验证不通过直接false，即没填
                    }
                    if(newPassword != newPassword2) {
                        $.messager.alert("系统提示", "两次输入的密码不一致");
                        return false
                    }
                    return true;
                }, //进行验证，通过才让提交
                success: function(result) {
                    var jsonResult = eval('(' + result + ')');
                    if(jsonResult && jsonResult.code == 'success') {
                        $.messager.alert("系统提示", "密码修改成功，下一次登陆生效");
                        closePasswordModifyDialog();
                    } else {
                        $.messager.alert("系统提示", "密码修改失败");
                    }
                }
            });
        }

        function closePasswordModifyDialog() {
            $("#password").val(""); //保存成功后将内容置空
            $("#password2").val("");
            $("#dlg").dialog("close"); //关闭对话框
        }

        function refreshSystemCache() {
            $.post("${pageContext.request.contextPath}/admin/system/refreshSystemCache.do",{},function(result){
                if(result.success){
                    $.messager.alert("系统提示","已成功刷新系统缓存！");
                }else{
                    $.messager.alert("系统提示","刷新系统缓存失败！");
                }
            },"json");
        }

        function logout() {
            $.messager.confirm("系统提示","您确定要退出系统吗？", function(r){
                if(r) {
                    window.location.href = "${pageContext.request.contextPath}/admin/logout";
                }
            });
        }
    </script>
    <style type="text/css">
        body {
            font-family: microsoft yahei;
        }
    </style>
</head>
<%
    User currentUser = (User) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
%>
<body class="easyui-layout">
<div region="north" style="height: 78px; background-color: #E0ECFF">
    <table style="padding: 5px" width="100%">
        <tr>
            <td width="50%">
                <h2>博客后台管理系统</h2>
            </td>
            <td valign="bottom" align="right" width="50%">
                <font size="3">&nbsp;&nbsp;<strong>欢迎：</strong><%=currentUser.getNickName()%></font>
            </td>
        </tr>
    </table>
</div>
<div region="center">
    <div class="easyui-tabs" fit="true" border="false" id="tabs" style="overflow:hidden;">
        <div title="首页" data-options="iconCls:'icon-home'">
            <div align="center" style="padding-top: 100px"><font color="red" size="10">欢迎使用</font></div>
        </div>
    </div>
</div>
<div region="west" style="width: 220px;" title="导航菜单" split="true">
    <div class="easyui-accordion" data-options="border:false">
        <div title="常用操作" data-options="selected:true,iconCls:'icon-item'" style="padding: 10px">
            <a href="javascript:openTab('发表文章','writeArticle.jsp','icon-writeblog')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-writeblog'" style="width: 150px">写博客</a>
            <a href="javascript:openTab('评论审核','commentReview.jsp','icon-review')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-review'" style="width: 150px">评论审核</a>
        </div>
        <div title="文章管理"  data-options="iconCls:'icon-bkgl'" style="padding:10px;">
            <a href="javascript:openTab('发表文章','writeArticle.jsp','icon-writeblog')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-writeblog'" style="width: 150px;">写博客</a>
            <a href="javascript:openTab('文章信息管理','articleManage.jsp','icon-bkgl')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-bkgl'" style="width: 150px;">文章信息管理</a>
        </div>
        <div title="文章类别管理" data-options="iconCls:'icon-bklb'" style="padding:10px">
            <a href="javascript:openTab('文章类别信息管理','articleTypeManage.jsp','icon-bklb')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-bklb'" style="width: 150px;">文章类别信息管理</a>
        </div>
        <div title="评论管理"  data-options="iconCls:'icon-plgl'" style="padding:10px">
            <a href="javascript:openTab('评论审核','commentReview.jsp','icon-review')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-review'" style="width: 150px">评论审核</a>
            <a href="javascript:openTab('评论信息管理','commentManage.jsp','icon-plgl')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-plgl'" style="width: 150px;">评论信息管理</a>
        </div>
        <div title="个人信息管理"  data-options="iconCls:'icon-grxx'" style="padding:10px">
            <a href="javascript:openTab('修改个人信息','modifyUser.jsp','icon-grxxxg')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-grxxxg'" style="width: 150px;">修改个人信息</a>
            <a href="javascript:openPasswordModifyDialog()" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-modifyPassword'" style="width: 150px;">修改密码</a>
        </div>
        <div title="系统管理"  data-options="iconCls:'icon-system'" style="padding:10px">
            <a href="javascript:openTab('友情链接管理','linkManage.jsp',meta)" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-link'" style="width: 150px">友情链接管理</a>
            <a href="javascript:refreshSystemCache()" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-refresh'" style="width: 150px;">刷新系统缓存</a>
            <a href="${pageContext.request.contextPath }/admin/1" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-link'" style="width: 150px">回到主页</a>
            <a href="javascript:logout()" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-exit'" style="width: 150px;">安全退出</a>
        </div>
    </div>
</div>
<div region="south" style="height: 25px;padding: 5px" align="center">
    <%=currentUser.getNickName()%>的博客系统
</div>
<div id="dlg" class="easyui-dialog" style="width:400px; height:230px;"
     closed="true" buttons="#dlg-buttons">
    <form id="fm" method="post" style="width: 350px; height: 150px;">
        <table cellspacing="8px">
            <tr>
                <td>用户名：</td>
                <td>
                    <input type="text" id="username" name="username" style="border: 0;" value="<%=currentUser.getUsername()%>" readonly="readonly">
                </td>
            </tr>
            <tr>
                <td>新密码：</td>
                <td>
                    <input type="password" id="password" name="password" class="easyui-validatebox"
                           required="true" style="width:200px">
                </td>
            </tr>
            <tr>
                <td>确认新密码：</td>
                <td>
                    <input type="password" id="password2" name="password2" class="easyui-validatebox"
                           required="true" style="width:200px">
                </td>
            </tr>
        </table>
    </form>
</div>

<div id="dlg-buttons">
    <div>
        <a href="javascript:modifyPassword()" class="easyui-linkbutton" iconCls="icon-ok" plain="true">保存</a>
        <a href="javascript:closePasswordModifyDialog()" class="easyui-linkbutton" iconCls="icon-cancel" plain="true">关闭</a>
    </div>
</div>
</body>
</html>
