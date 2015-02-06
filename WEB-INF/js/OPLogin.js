$(document).ready(function(){

	$('#answer-login').hide();

	$('#form-login').submit(function(e){return false}); // don't know why but doesn't prevent the reload if i put it with the rest...


	$('#form-login').submit(function(event){
		var usernameValue = $('#username-login').val();
		console.log(usernameValue);

		if(usernameValue.trim()){
			$('#form-login').hide();
			$('<form id=form-login2 action="http://localhost:8054/OpenIdProvider/OpLogin/' + usernameValue + '"><legend>Login</legend></form>').insertAfter($('#form-login'));
			$('#form-login2').append($('<img src="http://localhost:8054/OpenIdProvider/challenge/"' + usernameValue + ' >'));
			$('#form-login2').append($('<input type=password id=answer placeholder="Answer">'));

			$('#form-login2').append($('<input type="submit" value="Submit">'));
		}
	});
});
