$(document).ready(function(){

	$('#answer-login').hide();

	$('#form-login').submit(function(e){return false}); // don't know why but doesn't prevent the reload if i put it with the rest...

	$('#form-login').submit(function(event){
		var usernameValue = $('#username-login').val();
		if (usernameValue.trim()){
			$('#form-login').hide();
            $.ajax({
                url: this.action,
                type: this.method,
                data: $(this).serialize(),
            });
			showNewForm();
		}
	});

	var showNewForm = function(){
		var usernameValue = $('#username-login').val();

		$('<form id="form-login2" action="/OpenIdProvider/OpLogin/"><legend>Login</legend></form>').insertAfter($('#form-login'));

		$('#form-login2').append($('<img src="http://localhost:8054/OpenIdProvider/OPChallenge/"' + usernameValue + ' >'));

        $('#form-login2').append($('<input id="username-login2" type="hidden" name="user-login2">'));
        $('#username-login2').val(usernameValue);
		$('#form-login2').append($('<input id="answer" type="password" name="answer" placeholder="Answer">'));
		$('#form-login2').append($('<input id="submit-form-login2" type="submit" value="Submit">'));

		$('#form-login2').submit(function(event){ 
			$.ajax({
				url: this.action, //'http://localhost:8054/OpenIdProvider/OpLogin/',
				//type: this.type,
                //contentType: 'application/json',
				data: $(this).serialize(),/*{
					username: usernameValue,
					answer: $('#answer').val(),
				},*/
				success: function(res){
					window.location.href = res.redirect;
				}
			  });
			return false;
		});
	};
});
