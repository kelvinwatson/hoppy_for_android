<?php 
session_start();
ini_set('session.save_path', '/hoppyadmin/saved_sessions');
error_reporting(E_ALL);
ini_set('display_errors',1);
header('Content-Type: application/json');
include 'db.php';
//require 'password_compat/lib/password.php'; //for PHP<5.7

/* CONNECT TO DATABASE */
$mySQLi = connectToDatabase();
setUTF8($mySQLi);

$obj = new stdClass(); //for JSON output

/* RETRIEVE POST PARAMS */
$action = $_POST['user-action'];
$eventId = htmlspecialchars($_POST['event-id']);
$checkedBeers = json_decode($_POST['checked-boxes'],TRUE);
$notCheckedBeers = json_decode($_POST['not-checked-boxes'],TRUE);

/*Debug only
$obj->httpResponseCode = 200;
$obj->response = "FAKE_associate_successs";
$obj->errorMessage = "recvd:$action, $eventId,".json_encode($checkedBeers);
echo json_encode($obj);
return;*/

if ($action == 'associate'){
  if (!($stmt = $mySQLi->prepare("INSERT INTO features(beer_id, event_id) VALUES (?,?)"))){
    $obj->httpResponseCode = 400;
    $obj->response = "associateFailure";
    $obj->errorMessage = 'Prepare failed.';
    echo json_encode($obj);
    return;
  }
  for($i=0, $len=count($checkedBeers); $i<$len; ++$i){
    $beerId = (int)$checkedBeers[$i];
    try{
      $stmt->bind_param("ii", $beerId, $eventId); 
      $stmt->execute();
    } catch(Exception $e){
      $obj->httpResponseCode = 400;
      $obj->response = "associateFailure";
      $obj->errorMessage = 'Bind failed.';
      echo json_encode($obj);
      return;
    }
  }
  $stmt->close();
  
  
  if (!($stmt2 = $mySQLi->prepare("DELETE FROM features WHERE beer_id=? AND event_id=?"))){
    $obj->httpResponseCode = 400;
    $obj->response = "associateFailure";
    $obj->errorMessage = 'Prepare failed.';
    echo json_encode($obj);
    return;
  }
  for($j=0, $len2=count($notCheckedBeers); $j<$len2; ++$j){
    $beerId2 = (int)$notCheckedBeers[$j];
    try{
      $stmt2->bind_param("ii", $beerId2, $eventId); 
      $stmt2->execute();
    } catch(Exception $e){
      $obj->httpResponseCode = 400;
      $obj->response = "associateFailure";
      $obj->errorMessage = 'Bind failed.';
      echo json_encode($obj);
      return;
    }
  }
  $stmt2->close();
  
  $obj->httpResponseCode = 200;
  $obj->response = "associateSuccessful";
  $obj->errorMessage = 'Associate/disassociate successful.';
  echo json_encode($obj);
} 
return;
?>