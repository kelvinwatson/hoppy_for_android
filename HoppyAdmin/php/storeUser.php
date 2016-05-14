<?php 
error_reporting(E_ALL);
ini_set('display_errors',1);
header('Content-Type: application/json');
include 'db.php';
//require 'password_compat/lib/password.php';

/* CONNECT TO DATABASE */
$mysqli = connectToDatabase();

$action = $_POST['userAction'];
$username = $_POST['userName'];
$email = $_POST['userEmail'];
$plainTextPassword = $_POST['userPassword'];
$hashedPassword = password_hash($plainTextPassword, PASSWORD_DEFAULT);


$obj = new stdClass(); //for JSON output

if($action=='add'){
  //PREPARE FOR DATA INSERTION
  if (!($stmt = $mysqli->prepare("INSERT INTO admin_users(username, email, password) VALUES (?,?,?)"))){
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = "Prepare failed:".$mysqli->error;
    echo json_encode($obj);
    return;
  }
  
  if (!$stmt->bind_param("sss", $username,$email,$hashedPassword)) {
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = "Bind parameters failed:".$mysqli->error;
    echo json_encode($obj);
    return;
  }

  if (!$stmt->execute()) {
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = "Execute insertion failed (1):".$mysqli->error;
    echo json_encode($obj);
    return;
  }  

  $obj->httpResponseCode = 200;
  $obj->response = "addSuccess";
  $obj->errorMessage = "$action, $username, $email, $plainTextPassword, $hashedPassword";
  echo json_encode($obj);
  return;
  
} else if ($action=='edit'){
  $userId = $_POST['userId'];
  
  if (!($stmt = $mysqli->prepare("UPDATE admin_users SET username=?, email=?, password=? WHERE id=?"))){
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = "Prepare failed:".$mysqli->error;
    echo json_encode($obj);
    return;
  }
  
  if (!$stmt->bind_param("sssi", $username,$email,$hashedPassword,$userId)) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = "Bind parameters failed:".$mysqli->error;
    echo json_encode($obj);
    return;
  }

  if (!$stmt->execute()) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = "Execute update failed:".$mysqli->error;
    echo json_encode($obj);
    return;
  }  
  
  $obj->httpResponseCode = 200;
  $obj->response = "editSuccess";
  $obj->errorMessage = "$action, $userId, $username, $email, $plainTextPassword, $hashedPassword";
  echo json_encode($obj);
  return;
}

?>