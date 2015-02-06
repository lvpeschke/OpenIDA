$(document).ready(function(){

	$('#answer-login').hide();

	$('#form-login').submit(function(e){return false}); // don't know why but doesn't prevent the reload if i put it with the rest...


	$('#form-login').submit(function(event){
		var usernameValue = $('#username-login').val();
		if(usernameValue.trim()){
			$('#form-login').hide();
			showNewForm();
		}
	});

	var showNewForm = function(){
		var usernameValue = $('#username-login').val();

		$('<form id="form-login2"><legend>Login</legend></form>').insertAfter($('#form-login'));

		$('#form-login2').append($('<img src="http://localhost:8054/OpenIdProvider/challenge/"' + usernameValue + ' >'));
		$('#form-login2').append($('<input type=password id=answer placeholder="Answer">'));
		$('#form-login2').append($('<input id="submit-form-login2" type="submit" value="Submit">'));

		$('#form-login2').submit(function(event){ 
			$.ajax({
				url: 'http://localhost:8054/OpenIdProvider/OpLogin/',
				contentType: 'application/json',
				data: {
					username: usernameValue,
					answer: $('#answer').val(),
				},
				success: function(res){
					window.location.href = res.redirect;
				}
			  });

			return false;
		});
	};


});
