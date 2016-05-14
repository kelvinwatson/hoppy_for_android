Toast.defaults.width='600px';
Toast.defaults.displayDuration=4000;    

window.onload = function(){
  var queryStr = window.location.search;
  if(queryStr=='?editSuccess=True'){
    Toast.success('User edited successfully.', 'Edit Confirmation');
  } else if(queryStr=='?editSuccess=False'){
    Toast.error('There was an error in one or more of your inputs!', 'Edit Failed');
  } else if(queryStr=='?addSuccess=True'){
    Toast.success('User added successfully.', 'Add Confirmation');
  }else if(queryStr=='?addSuccess=False'){
    Toast.error('There was an error in one or more of your inputs.', 'Add Failed');
  }
}

function manageUser(action) { 
  if(action=='add'){
    var userName = $('#inputUsername').val(); 
    var email = $('#inputEmail').val();
    var password = $('#inputPassword').val();    var params = [userName,email,password];
    if(validateInput(userName, email, password)){ //check for empty inputs
      ajaxCall('add', params);//ajax call,pass params to db
    } else{
      return;
    } 
  } else if(action=='edit'){
    var userId = $('#uId').val();
    var userName = $('#uName').val();
    var email = $('#uEmail').val();
    var password = $('#uPassword').val();
    var params = [userId,userName,email,password];
    if(validateInput(userName, email, password)){ //check for empty inputs
      ajaxCall('edit', params);//ajax call,pass params to db
    } else{
      return;
    }
  }
}

function ajaxCall(action, params){
  if(action=='add'){
    var formData = "userAction=add&userName="+params[0]+"&userEmail="+params[1]+"&userPassword="+params[2];
  } else{
    var formData = "userAction=edit&userId="+params[0]+"&userName="+params[1]+"&userEmail="+params[2]+"&userPassword="+params[3];
  }
  $.ajax({
    url: "/hoppyadmin/php/storeUser.php",
    type: "POST",
    data: formData,
    dataType: 'json',
    success: function(data){
      debugger;
      //data - response from server
      if(data.httpResponseCode==200){
        debugger;
        if(data.response=='addSuccess'){
          window.location = 'users.php?addSuccess=True';
        } else if (data.response=='addFailure'){
          window.location = 'users.php?addSuccess=False';
        } else if (data.response=='editSuccess'){
          window.location = 'users.php?editSuccess=True';  
        } else if (data.response=='editFailure'){
          window.location = 'users.php?editSuccess=False';
        }
      } else{ //httpResponseCode is 400
        debugger;
        if(data.response=='addFailure'){
          debugger;
          window.location = 'users.php?addSuccess=False';
        } else if (data.response=='editFailure'){
          window.location = 'users.php?editSuccess=False';
        }
      }
    },
    error: function (jqXHR, textStatus, errorThrown){
    }
  });
}

function validateInput(userName, email, password){
    var errString=null;
    if(!userName || !email || !password){ //empty fields
      if(!userName){
        errString="You must enter a user name. ";
      } 
      if(!email) {
        errString = errString==null? "You must enter an email address. " : errString+"You must enter an email address. ";
      }
      if(!password){
        errString = errString==null?"You must enter an password. " : errString+"You must enter an password. ";
      }
      Toast.error(errString,'Failed to add user');
      return false;
  } else{
    if(/\s/.test(userName)){ //contains white space
      errString="User name cannot contain spaces, or tabs. "; 
      Toast.error(errString,'Failed to add user');
      return false;
    }
    if(/\s/.test(email)){ //contains white space
      errString= errString==null? "Email address cannot contain spaces, or tabs. ": errString+"Email address cannot contain spaces, or tabs. "; 
      Toast.error(errString,'Failed to add user');
      return false;
    }
    if(/\s/.test(password)){ //contains white space
      errString=errString==null? "Password cannot contain spaces, or tabs. ": errString+"Password cannot contain spaces, or tabs. "; 
      Toast.error(errString,'Failed to add user');
      return false;
    }    
    return true;  
  }
  
}