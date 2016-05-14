Toast.defaults.width='600px';
Toast.defaults.displayDuration=7000;

window.onload = function(){
  var queryStr = window.location.search;
  if(queryStr=='?editSuccess=True'){
    Toast.success('Edit Successful!', 'Edit Confirmation');
  } else if(queryStr=='?editSuccess=False'){
    Toast.error('There was an error in one or more of your inputs!', 'Edit Status');
  } else if(queryStr=='?addSuccess=True'){
    Toast.success('Add Successful!', 'Add Confirmation');
  }else if(queryStr=='?addSuccess=False'){
    Toast.error('There was an error in one or more of your inputs!', 'Add Status');
  }else if(queryStr=='?err=NoBreweryName'){
    Toast.error('You did not specify a brewery name.', 'Error');     
  }
}

function manageBrewery(action,breweryId){
  var breweryName, breweryDescription, breweryLogoURL;
  if(action=='edit'){
    breweryName = document.getElementById("brewery-name"+breweryId).value;
    breweryDescription = document.getElementById("brewery-description"+breweryId).value || "";
    breweryLogoURL = document.getElementById("brewery-logoURL"+breweryId).value;    
  } else {
    breweryName = document.getElementById("brewery-name").value;
    breweryDescription = document.getElementById("brewery-description").value || "";
    breweryLogoURL = document.getElementById("brewery-logoURL").value;    
  }
  
  console.log(breweryName);
  console.log(breweryDescription);
  console.log(breweryLogoURL);
  
  if(!breweryName.match(/\S/)){ //if name empty, short circuit
    window.location = "breweries.php?err=NoBreweryName";
    return;
  }
  
  Toast.defaults.displayDuration=2000; 
  Toast.warning('Processing your request...','Please wait');
  
  var params = null;
  if(action=='edit'){
    params = [breweryId, breweryName, breweryDescription, breweryLogoURL];
  } else if(action=='add'){
    params = [breweryName, breweryDescription, breweryLogoURL];
  }    
  ajaxCall(action, params);
}

function ajaxCall(action, params){
  var formData;
  if(action=='edit'){
    formData = "user-action=edit-brewery&brewery-id="+params[0]+"&brewery-name="+params[1]+"&brewery-description="+params[2]+"&brewery-logoURL="+params[3];
  } else{
    formData = "user-action=add-brewery&brewery-name="+params[0]+"&brewery-description="+params[1]+"&brewery-logoURL="+params[2];
  }
  $.ajax({
    url: "/hoppyadmin/php/manageBrewery.php",
    type: "POST",
    data: formData,
    dataType: 'json',
    success: function(data){
      //data - response from server
      if(data.httpResponseCode==200){
        if(data.response=='addSuccess'){
          window.location = 'breweries.php?addSuccess=True';
        } else if (data.response=='addFailure'){
          window.location = 'breweries.php?addSuccess=False';
        } else if (data.response=='editSuccess'){
          window.location = 'breweries.php?editSuccess=True';  
        } else if (data.response=='editFailure'){
          window.location = 'breweries.php?editSuccess=False';
        }
      } else{ //httpResponseCode is 400
        if(data.response=='addFailure'){
          window.location = 'breweries.php?addSuccess=False';
        } else if (data.response=='editFailure'){
          window.location = 'breweries.php?editSuccess=False';
        }
      }
    },
    error: function (jqXHR, textStatus, errorThrown){
    }
  });
}

