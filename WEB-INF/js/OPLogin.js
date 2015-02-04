$(document).ready(function(){

	$('#answer-login').hide();

	$('#form-login').submit(function(e){return false}); // don't know why but doesn't prevent the reload if i put it with the rest...


	$('#form-login').submit(function(event){
		var usernameValue = $('#username-login').val();

		if(usernameValue.trim()){
			$('#username-login').hide();
			$('#answer-login').show();
			$('<img src="http://localhost:8054/OpenIdProvider/challenge/"' + usernameValue + ' >').insertBefore($('#answer-login'));
		}
	});

	console.log($('#form-login'));

});
