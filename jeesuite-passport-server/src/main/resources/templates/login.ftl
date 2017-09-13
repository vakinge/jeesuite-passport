<!DOCTYPE html>
<html lang="zh-cn">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
		<meta name="renderer" content="webkit">
		<title>登录</title>
	</head>
	<body>
		<div class="container">
			<div class="line">
				<div class="xs6 xm4 xs3-move xm4-move">
					<br />
					<br />
					<br />
					<br />
					<br />
					<br />
					<form action="" method="post">
					    <input type="hidden" name="state" value="${state!}"/>
                        <input type="hidden" name="client_id" value="${client_id!}"/>
                        <input type="hidden" name="redirect_uri" value="${redirect_uri!}"/>
                        <input type="hidden" name="response_type" value="${response_type!}"/>
						<div class="panel padding">
							<div class="text-center">
								<br>
								<h2><strong>登录</strong></h2></div>
							<div class="" style="padding:30px;">
								<div class="form-group">
									<div class="field field-icon-right">
										<input type="text" class="input" id="username" name="username" placeholder="登录账号" data-validate="required:请填写账号,length#>=5:账号长度不符合要求" />
										<span class="icon icon-user"></span>
									</div>
								</div>
								<div class="form-group">
									<div class="field field-icon-right">
										<input type="password" class="input" id="password" name="password" placeholder="登录密码" data-validate="required:请填写密码,length#>=8:密码长度不符合要求" />
										<span class="icon icon-key"></span>
									</div>
								</div>
								<div class="form-group">
									<div class="field">
										<input type="submit" class="button button-block bg-main text-big" value="登陆" />
									</div>
								</div>
								<div class="form-group">
									<div class="field text-center">
										<p class="text-muted text-center"> <a class="" href="login.html#"><small>忘记密码了？</small></a> | <a class="" href="register.html">注册新账号</a>
										</p>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>
    <script>
if (window != top) top.location.href = location.href;
</script>
</html>