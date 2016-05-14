<?php 

session_start();
ini_set('session.save_path', '/hoppyadmin/saved_sessions');

error_reporting(E_ALL);
ini_set('display_errors',1);
header('Content-Type: text/html; charset=utf-8');
include 'php/db.php';
include 'php/secure.php';

//TODO: redirectToHTTPS();
//print_r($_SESSION);
?>

<!DOCTYPE html>
<html lang="en">


<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Hoppy Administrator Portal</title>
  <link rel="stylesheet" href="http://getbootstrap.com/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="css/style.css">
  <link rel="stylesheet" href="css/toast.css">
</head>

<body>

<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#"><img class="top-left-logo" src="images/bunny.png"> Hoppy Portal</a>
    </div>
    <div id="navbar" class="navbar-collapse collapse">
      <form class="navbar-form navbar-right">
        <div class="form-group">
          <input id="inputUsername" type="text" placeholder="Username" class="form-control" required>
        </div>
        <div class="form-group">
          <input id="inputPassword" type="password" placeholder="Password" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-success" onclick="authenticate(); return false;">Sign in</button>
      </form>
    </div><!--/.navbar-collapse -->
  </div>
</nav>

</body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://getbootstrap.com/dist/js/bootstrap.min.js"/>
<script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery.min.js"><\/script>')</script>
<script src="js/authenticate.js"></script>
<script src="js/toast.js"></script>
  

</html>

