window.onload = function(){
  var queryStr = window.location.search;
  if(queryStr=='?authenticated=True'){
    Toast.success('User authenticated.', 'Authentication Success');
    window.location = "users.php";
  } else if(queryStr=='?authenticated=False'){
    Toast.error('Incorrect username or password', 'Authentication Failed');
    return;
  } 
}

function authenticate(){
  var username = $('#inputUsername').val();
  var password = $('#inputPassword').val();
  if(validateInput(username, password)){ //check for empty inputs
    ajaxCall(username, password);//ajax call,pass params to db
  } 
  return;
}

function validateInput(username, password){
  var errString=null;
  if(!username || !password){ //empty fields
    if(!username){
      errString="Missing user name. ";
    } 
    if(!password){
      errString = errString==null?"Missing password. " : errString+"Missing password. ";
    }
    Toast.error(errString,'Failed to login.');
    return false;
  } else{
    return true;
  }
}

function ajaxCall(username, password){
  var formData = "userName="+username+"&userPassword="+password;
  $.ajax({
    url: "/hoppyadmin/php/authenticateUser.php",
    type: "POST",
    data: formData,
    dataType: 'json',
    success: function(data){
      //data - response from server
      if(data.httpResponseCode==200){
        window.location = 'index.php?authenticated=True';
      } else{ //httpResponseCode is 400
        window.location = 'index.php?authenticated=False'; 
      }  
    },
    error: function (jqXHR, textStatus, errorThrown){
    }
  });
}

