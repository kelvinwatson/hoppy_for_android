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
  }else if(queryStr=='?err=NoBeerName'){
    Toast.error('You did not specify a beer name.', 'Error');     
  }
}

function manageBeer(action,beerId){
  var beerName, beerType, beerABV, beerIBU, beerDescription, breweryId, selectTag;
  if(action=='edit'){
    beerName = $("#beer-name"+beerId).val();
    beerType = $("#beer-type"+beerId).val();
    beerABV = $("#beer-ABV"+beerId).val();    
    beerIBU = $("#beer-IBU"+beerId).val();    
    beerDescription = $("#beer-description"+beerId).val();    
    //selectTag = $("#brewery-name-for"+beerId); //select form element
    //breweryId = selectTag.options[selectTag.selectedIndex].val(); //selected brewery id
    breweryId = $( "#brewery-name-for"+beerId+" option:selected" ).val(); //selected brewery id
  } else {
    beerName = $("#beer-name").val();
    beerType = $("#beer-type").val();
    beerABV = $("#beer-ABV").val();    
    beerIBU = $("#beer-IBU").val();    
    beerDescription = $("#beer-description").val();    
    breweryId = $( "#brewery-name option:selected" ).val(); //selected brewery id
  }
  
  if(!beerName.match(/\S/)){ //if name empty, short circuit
    window.location = "beers.php?err=NoBeerName";
    return;
  }
  
  Toast.defaults.displayDuration=2000; 
  Toast.warning('Processing your request...','Please wait');
  
  var params;
  if(action=='edit'){
    params = [beerId, beerName, beerType, beerABV, beerIBU, beerDescription, breweryId];
  } else if(action=='add'){
    params = [beerName, beerType, beerABV, beerIBU, beerDescription, breweryId];
  }    
  ajaxCall(action, params);
}

function ajaxCall(action, params){
  var formData;
  if(action=='edit'){
    formData = "user-action=edit-beer&beer-id="+params[0]+"&beer-name="+params[1]+"&beer-type="+params[2]+"&beer-ABV="+params[3]+"&beer-IBU="+params[4]+"&beer-description="+params[5]+"&brewery-id="+params[6];
  } else{
    formData = "user-action=add-beer&beer-name="+params[0]+"&beer-type="+params[1]+"&beer-ABV="+params[2]+"&beer-IBU="+params[3]+"&beer-description="+params[4]+"&brewery-id="+params[5];
  }
  $.ajax({
    url: "/hoppyadmin/php/manageBeer.php",
    type: "POST",
    data: formData,
    dataType: 'json',
    success: function(data){
      //data - response from server
      if(data.httpResponseCode==200){
        if(data.response=='addSuccess'){
          window.location = 'beers.php?addSuccess=True';
        } else if (data.response=='addFailure'){
          window.location = 'beers.php?addSuccess=False';
        } else if (data.response=='editSuccess'){
          window.location = 'beers.php?editSuccess=True';  
        } else if (data.response=='editFailure'){
          window.location = 'beers.php?editSuccess=False';
        }
      } else{ //httpResponseCode is 400
        if(data.response=='addFailure'){
          window.location = 'beers.php?addSuccess=False';
        } else if (data.response=='editFailure'){
          window.location = 'beers.php?editSuccess=False';
        }
      }
    },
    error: function (jqXHR, textStatus, errorThrown){
    }
  });
}
