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
  }else if(queryStr=='?err=NoEventName'){
    Toast.error('You did not specify a event name.', 'Error');     
  }
}

function manageEvent(action,eventId){
  var eventName, eventDate, eventLogoURL, eventEndDate;
  if(action=='edit'){
    eventName = $("#event-name"+eventId).val();
    eventDate = $("#event-date"+eventId).val();
    eventLogoURL = $("#event-logoURL"+eventId).val();    
    eventEndDate = $("#event-end-date"+eventId).val();    
  } else {
    eventName = $("#event-name").val();
    eventDate = $("#event-date").val();
    eventLogoURL = $("#event-logoURL").val();    
    eventEndDate = $("#event-end-date").val();    
  }
  
  if(!eventName.match(/\S/)){ //if name empty, short circuit
    window.location = "beers.php?err=NoEventName";
    return;
  }
  
  Toast.defaults.displayDuration=2000; 
  Toast.warning('Processing your request...','Please wait');
  
  var params;
  if(action=='edit'){
    params = [eventId, eventName, eventDate, eventLogoURL, eventEndDate];
  } else if(action=='add'){
    params = [eventName, eventDate, eventLogoURL, eventEndDate];
  }    
  ajaxCall(action, params);
}

function ajaxCall(action, params){
  var formData;
  if(action=='edit'){
    formData = "user-action=edit-event&event-id="+params[0]+"&event-name="+params[1]+"&event-date="+params[2]+"&event-logoURL="+params[3]+"&event-end-date="+params[4];
  } else{
    formData = "user-action=add-event&event-name="+params[0]+"&event-date="+params[1]+"&event-logoURL="+params[2]+"&event-end-date="+params[3];
  }
  $.ajax({
    url: "/hoppyadmin/php/manageEvent.php",
    type: "POST",
    data: formData,
    dataType: 'json',
    success: function(data){
      //data - response from server
      if(data.httpResponseCode==200){
        if(data.response=='addSuccess'){
          window.location = 'events.php?addSuccess=True';
        } else if (data.response=='addFailure'){
          window.location = 'events.php?addSuccess=False';
        } else if (data.response=='editSuccess'){
          window.location = 'events.php?editSuccess=True';  
        } else if (data.response=='editFailure'){
          window.location = 'events.php?editSuccess=False';
        }
      } else{ //httpResponseCode is 400
        if(data.response=='addFailure'){
          window.location = 'events.php?addSuccess=False';
        } else if (data.response=='editFailure'){
          window.location = 'events.php?editSuccess=False';
        }
      }
    },
    error: function (jqXHR, textStatus, errorThrown){
    }
  });
}
