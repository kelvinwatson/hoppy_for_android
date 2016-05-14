Toast.defaults.width='600px';
Toast.defaults.displayDuration=7000;

window.onload = function(){
  var queryStr = window.location.search;
  if(queryStr=='?associateSuccess=True'){
    Toast.success('Association Successful!', 'Confirmation');
  } else if(queryStr=='?associateSuccess=False'){
    Toast.error('Unable to associate beers!', 'Status');
  }
}

function associateBeers(eventId){
  var checkedBoxes = $('.beer-checkbox-for-event-'+eventId+ ':checked').map(function(){
    return $(this).val();
  }).get();  
  
  var notCheckedBoxes = $('.beer-checkbox-for-event-'+eventId+ ':not(:checked)').map(function(){
    return $(this).val();
  }).get();  
  
  Toast.defaults.displayDuration=2000; 
  Toast.warning('Processing your request...','Please wait');
  
  ajaxCall(eventId, checkedBoxes, notCheckedBoxes);
}

function ajaxCall(eventId, checked, notChecked){
  var sChked = JSON.stringify(checked);
  var sNChked = JSON.stringify(notChecked)
  var formData = "user-action=associate&event-id="+eventId+"&checked-boxes="+sChked+"&not-checked-boxes="+sNChked;
  $.ajax({
    url: "/hoppyadmin/php/associateBeersToEvent.php",
    type: "POST",
    data: formData,
    dataType: 'json',
    success: function(data){
      //data - response from server
      if(data.httpResponseCode==200){
        if(data.response=='associateSuccess'){
          window.location = 'eventBeers.php?associateSuccess=True';
        } else if (data.response=='associateFailure'){
          window.location = 'eventBeers.php?associateSuccess=False';
        }
      } else{ //httpResponseCode is 400
        if(data.response=='associateFailure'){
          window.location = 'eventBeers.php?associateSuccess=False';
        } else if (data.response=='associateFailure'){
          window.location = 'eventBeers.php?associateSuccess=False';
        }
      }
    },
    error: function (jqXHR, textStatus, errorThrown){
    }
  });
}
