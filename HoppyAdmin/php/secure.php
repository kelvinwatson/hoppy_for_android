<?php

function redirectToHTTPS(){
  if(empty($_SERVER["HTTPS"]) || $_SERVER["HTTPS"] !== "on"){
    header("Location: https://" . $_SERVER["HTTP_HOST"] . $_SERVER["REQUEST_URI"]);
    exit();
  }
}

?>
