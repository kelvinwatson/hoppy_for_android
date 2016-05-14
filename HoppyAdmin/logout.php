<?php session_start();
error_reporting(E_ALL);
ini_set('display_errors',1);
ini_set('session.save_path', '/hoppyadmin/saved_sessions');
header('Content-Type: text/html; charset=utf-8');
include 'php/db.php';

if(!isset($_SESSION['loggedIn'])){
  header('Location: index.php');
  return;
} else if (!$_SESSION['loggedIn']){
  header('Location: index.php');  
  return;
} else {
  session_unset();
  session_destroy();
}

?>
<!DOCTYPE html>
<html lang="en">

<head>
  <script type="text/javascript" src="js/jQuery.js"></script>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Hoppy Administrator Portal</title>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
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

<div class="container">
<h1>Administrator Portal</h1>
<!--LOGIN FORM-->
<div class="row">
  <div class="col-xs-0 col-md-3"></div>
  <div class="col-xs-12 col-md-6">
    <h2>You have successfully logged out.</h2>
  </div>
  <div class="col-xs-0 col-md-3"></div>
</div><!--END ROW-->
</div> <!-- END CONTAINER -->

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://getbootstrap.com/dist/js/bootstrap.min.js"/>
<script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery.min.js"><\/script>')</script>
<script src="js/authenticate.js"></script>
<script src="js/toast.js"></script>
</body>
</html>