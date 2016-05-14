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
$eventId = null;
$eventName = htmlspecialchars($_POST['event-name']);
$eventDate = htmlspecialchars($_POST['event-date']);
$eventLogoURL = htmlspecialchars($_POST['event-logoURL']);
$eventEndDate = htmlspecialchars($_POST['event-end-date']);

/*Debug only
$obj->httpResponseCode = 200;
$obj->response = "FAKEaddEventSuccesss";
$obj->errorMessage = "recvd:$action, $eventId, $eventName, $eventDate, $eventLogoURL, $eventEndDate";
echo json_encode($obj);
return;*/

if ($action == 'edit-event'){
  $eventId = htmlspecialchars($_POST['event-id']);
  if (!($stmt = $mySQLi->prepare("UPDATE events SET name=?, event_date=?, logo_url=?, end_date=? WHERE id=?"))) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = 'Prepare failed.';
    echo json_encode($obj);
    return;
  }
  
  if (!$stmt->bind_param("ssssi", $eventName, $eventDate, $eventLogoURL, $eventEndDate, $eventId)) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = 'Bind failed.';
    echo json_encode($obj);
    return;
  }
  if (!$stmt->execute()) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = 'Execute failed.';
    echo json_encode($obj);
    return;
  }
  $stmt->close();
  $obj->httpResponseCode = 200;
  $obj->response = "editSuccess";
  $obj->errorMessage = 'Edit event successful.';
  echo json_encode($obj);
} else if ($action == "add-event"){
    
  if (!($stmt = $mySQLi->prepare("INSERT INTO events(name, event_date, logo_url, end_date) VALUES (?,?,?,?)"))){
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = 'Prepare failed.';
    echo json_encode($obj);
    return;
  }
  if (!$stmt->bind_param("ssss", $eventName, $eventDate, $eventLogoURL, $eventEndDate)) {
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = 'Bind failed.';
    echo json_encode($obj);
    return;
  }
  if (!$stmt->execute()) {
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = 'Execute failed.';
    echo json_encode($obj);
    return;
  }
  $stmt->close();
  $obj->httpResponseCode = 200;
  $obj->response = "addSuccesss";
  $obj->errorMessage = 'Add event successful.';
  echo json_encode($obj);
}
return;
?>