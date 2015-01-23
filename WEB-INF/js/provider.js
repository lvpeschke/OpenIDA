function fetchUserData (){
  fetchReyingPartyData();
}

function fetchReyingPartyData () {
  $.getJSON( "../OpenIdProvider/getRelyingPartyData", function( data ) {
  	console.log(data);
  var items = [];
  $.each( data, function( key, val ) {
    console.log("key is->" + key + "<->" + val);
   items.push( "<li >'" + key +  "'<input type='" + 'checkbox' + "' checked='" + val + "' name='" + key + "'></li>" );
  });
 
  $( "<ul/>", {
    html: items.join( "" )
  }).appendTo( "#relyingpartyDiv" );
});
}