<#include "/layout/res.ftl"/>

<div class="layui-fluid">
    <div class="layui-card">
        <div class="layui-form" lay-filter="layuiadmin-app-form-list" id="layuiadmin-app-form-list"
             style="padding: 20px 30px 0 0;">
            <input type="hidden" name="id" value="${(sysUser.id)!}">
            <div class="layui-form-item">
                <div class="layui-form-item">
                    <label class="layui-form-label">用户名</label>
                    <div class="layui-input-block">
                        <input class="layui-input" lay-verify="required" name="username" value="${(sysUser.username)!}" autocomplete="off" placeholder="请输入">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">姓名</label>
                    <div class="layui-input-block">
                        <input class="layui-input" lay-verify="required" name="realname" value="${(sysUser.realname)!}" autocomplete="off" placeholder="请输入">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">手机号码</label>
                    <div class="layui-input-block">
                        <input type="number" maxlength="11" class="layui-input" lay-verify="required" name="mobile" value="${(sysUser.mobile)!}" autocomplete="off" placeholder="请输入">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">微信号码</label>
                    <div class="layui-input-block">
                        <input type="text" maxlength="11" class="layui-input" name="wxNumber" value="${(sysUser.wxNumber)!}" autocomplete="off" placeholder="请输入">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">分配角色</label>
                    <div class="layui-input-block">
                        <div id="laySelect" class="xm-select-demo"></div>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">状态</label>
                    <div class="layui-input-block">
                        <#list dictService.getType('sys_user_status') as status>
                            <#if (status.dictValue?number == sysUser.status)!false>
                                <input type="radio" name="status" value="${status.dictValue}" title="${status.dictLabel}" checked>
                            <#elseif status.isDefault>
                                <input type="radio" name="status" value="${status.dictValue}" title="${status.dictLabel}" checked>
                            <#else>
                                <input type="radio" name="status" value="${status.dictValue}" title="${status.dictLabel}">
                            </#if>
                        </#list>
                    </div>
                </div>
            </div>
            <div class="layui-form-item layui-hide">
                <input type="button" lay-submit lay-filter="layuiadmin-app-form-submit" id="layuiadmin-app-form-submit" value="确认添加">
                <input type="button" lay-submit lay-filter="layuiadmin-app-form-edit" id="layuiadmin-app-form-edit" value="确认编辑">
            </div>
        </div>
    </div>
</div>

<script>
    layui.use(['form'], function(){
        let form = layui.form;

        xmSelect.render({
            el: '#laySelect',
            filterable: true,
            searchTips:"请输入关键词",
            name:"roleIds",
            data:${sysRoles!}
        });

        //监听提交
        form.on('submit(layuiadmin-app-form-submit)', function(data){
            let field = data.field; //获取提交的字段
            let index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引

            //提交 Ajax 成功后，关闭当前弹层并重载表格
            layer.load(1);
            $.ajax({
                url:'/admin/sysuser/addOrModify',
                type:'POST',
                data:field,
                success:function (data) {
                    layer.closeAll();
                    if(data.code !== 200){
                        layer.msg(data.msg,{icon:2});
                        return;
                    }
                    layer.msg(data.msg,{time:1000},function () {
                        parent.layui.table.reload('layTable'); //重载表格
                        parent.layer.close(index); //再执行关闭
                    });
                },
                error:function () {
                    layer.closeAll();
                    layer.alert('系统繁忙，请稍后重试');
                }
            });
        });
    })
</script>