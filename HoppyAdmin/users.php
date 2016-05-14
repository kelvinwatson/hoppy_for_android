<?php 
session_start();
error_reporting(E_ALL);
ini_set('display_errors',1);
ini_set('session.save_path', '/hoppyadmin/saved_sessions');
header('Content-Type: text/html; charset=utf-8');
include 'php/db.php';

/* Always redirect user to HTTPS 
if(empty($_SERVER["HTTPS"]) || $_SERVER["HTTPS"] !== "on"){
  header("Location: https://" . $_SERVER["HTTP_HOST"] . $_SERVER["REQUEST_URI"]);
  exit();
}*/

if(!isset($_SESSION['loggedIn']) && $_SESSION['loggedIn']){
  header('Location: index.php');
}

/* CONNECT TO DATABASE */
$mySQLi = connectToDatabase();

?>

<!DOCTYPE html>
<html lang="en">

<head>
  <script src="js/toast.js"></script>
  <script type="text/javascript" src="js/jQuery.js"></script>
  <script src="js/addAdminUser.js"></script>
  
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
      <ul class="nav navbar-nav">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle line-height-custom" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Manage <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="beers.php">Beers</a></li>
            <li><a href="breweries.php">Breweries</a></li>
            <li><a href="events.php">Events</a></li>
            <li><a href="users.php">Users</a></li>
            <li role="separator" class="divider"></li>
            <li class="dropdown-header">Associate</li>
            <li><a href="eventBeers.php">Beers at Events</a></li>
          </ul>
        </li>
      </ul>
      <ul class="nav navbar-form navbar-right">
        <li><span class="glyphicon glyphicon-asterisk" style="color:#7FFF00"></span><span style="color:white"> Logged In (<?php echo $_SESSION['username']; ?>)
        <a type="submit" class="btn btn-info" onclick="logout(); return false;" href="logout.php">Log out</a></li>
      </ul>
    </div> 
  </div>
</nav>



  <div class="container">
    <div class="row">
      <h1>Administrator Portal</h1>
    </div>

    <div class="row"> <!--START VIEW TABLE ROW -->
      <h3>View/Edit Current Users</h3>
      <div class="table-responsive">
        <table class="table">
          <thead>
              <tr>
                <th>Edit</th>
                <th>Username</th>
                <th>Email</th>
              </tr>
            </thead>
            <tbody>
              <?php

              //display table of users
              if (!($stmt = $mySQLi->prepare(
                "SELECT au.id, au.username, au.email, au.password, au.superuser FROM admin_users au ORDER BY au.username ASC"))) {
                echo "Prepare failed: (" . $mySQLi->errno . ") " . $mySQLi->error;
              }
              if (!$stmt->execute()) {
                echo "Execute failed: (" . $stmt->errno . ") " . $stmt->error;
              }
              if(!$stmt->bind_result($uID,$uName,$uEmail,$uPassword,$uSuperuser)){
                echo "Bind failed: "  . $mySQLi->connect_errno . " " . $mySQLii->connect_error;
              }
              while($stmt->fetch()){
                echo 
                "<tr>";
                  if($_SESSION['superUser']){ //only one user is the super user
                    echo "<form action=\"users.php#edit\" method=\"post\">
                      <td style=\"padding-left:0px;\"><input class=\"btn btn-warning\" type=\"submit\" value=\"edit\"><input type=\"hidden\" name=\"user-id\" value=\"$uID\"></td>
                      <td>".$uName."<input type=\"hidden\" name=\"user-name\" value=\"".$uName."\"></td>
                      <td>".$uEmail."<input type=\"hidden\" name=\"user-email\" value=\"".$uEmail."\"></td>
                      <input type=\"hidden\" name=\"user-password\" value=\"$uPassword\">
                      <input type=\"hidden\" name=\"user-superuser\" value=\"$uSuperuser\">
                      <input type=\"hidden\" name=\"user-action\" value=\"edit-user\">
                    </form>";
                  } else{
                    if(!$uSuperuser){
                      echo "<form action=\"users.php#edit\" method=\"post\">
                        <td style=\"padding-left:0px;\"><input class=\"btn btn-warning\" type=\"submit\" value=\"edit\"><input type=\"hidden\" name=\"user-id\" value=\"$uID\"></td>
                        <td>".$uName."<input type=\"hidden\" name=\"user-name\" value=\"".$uName."\"></td>
                        <td>".$uEmail."<input type=\"hidden\" name=\"user-email\" value=\"".$uEmail."\"></td>
                        <input type=\"hidden\" name=\"user-password\" value=\"$uPassword\">
                        <input type=\"hidden\" name=\"user-superuser\" value=\"$uSuperuser\">
                        <input type=\"hidden\" name=\"user-action\" value=\"edit-user\">
                      </form>";
                    }
                  }              
              }
              $stmt->close();
              ?>
            </tbody>
        </table>
      </div>
    </div><!--END VIEW USERS ROW-->
    
    <div id="edit"></div>
    <div class="row"><!--EDIT ROW-->

    <?php if ($_SERVER['REQUEST_METHOD']=='POST' && isset($_POST['user-action'])):?>
      <?php if($_POST['user-action']=='edit-user'):?>
        <h3 style="padding-top: 70px;">Edit Existing User</h3>
        <form class="form-horizontal" action="/">

          <div class="form-group">
          <label for="uName" class="col-sm-2 control-label">Username</label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="uName" value="<?php echo htmlspecialchars($_POST['user-name']); ?>" required>
          </div>
          </div>

          <div class="form-group">
          <label for="uEmail" class="col-sm-2 control-label">Email</label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="uEmail" value="<?php echo htmlspecialchars($_POST['user-email']); ?>">
          </div>
          </div>

          <div class="form-group">
          <label for="uPassword" class="col-sm-2 control-label">Password</label>
          <div class="col-sm-10">
            <!-- Not sure if this should be prepopulated -->
            <!--should be <input type="text" class="form-control" id="uPassword" value="<?php //echo htmlspecialchars($_POST['user-password']);?>">-->
            <input type="password" class="form-control" id="uPassword" placeholder="New Password">
          </div>
          </div>


        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-10">
            <input type="hidden" id="uId" value="<?php echo htmlspecialchars($_POST['user-id']); ?>">
            <button type="button" class="btn btn-primary" onclick="manageUser('edit'); return false;">Confirm Edit</button>
          </div>
        </div>
        </form>
        <hr style="width: 100%; color: black; height: 1px; background-color:#d3d3d3;" />
        <br><br>

      <?php endif; ?>
     <?php endif; ?>

    </div><!--END EDIT ROW-->  
    

  <!--CREATE USER FORM-->
  <a id="add"></a>
  <div class="row">
        <form class="form-horizontal" action="/">
          <fieldset>
            <legend>Create a New User</legend>
            <div class="form-group">
              <label for="inputUsername" class="col-sm-2 control-label">Username</label>
              <div class="col-sm-10">
                <input type="username" class="form-control" id="inputUsername" placeholder="Username" required>
              </div>
            </div>
            <div class="form-group">
              <label for="inputEmail" class="col-sm-2 control-label">Email</label>
              <div class="col-sm-10">
                <input type="email" class="form-control" id="inputEmail" placeholder="Email" required>
              </div>
            </div>
            <div class="form-group">
              <label for="inputPassword" class="col-sm-2 control-label">Password</label>
              <div class="col-sm-10">
                <input type="password" class="form-control" id="inputPassword" placeholder="Password" required>
              </div>
            </div>
            <div class="form-group">
              <div class="col-md-10 col-md-offset-2">
                <button type="reset" class="btn btn-default">Reset</button>
                <button type="submit" class="btn btn-primary" onclick="manageUser('add'); return false;">Submit</button>
              </div>
            </div>
          </fieldset>
        </form>
    </div>
    <div class="col-xs-0 col-md-3"></div>
  </div><!--END ROW-->




  </div> <!-- END CONTAINER -->

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://getbootstrap.com/dist/js/bootstrap.min.js"/>
<script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery.min.js"><\/script>')</script>
<script src="js/toast.js"></script>

</body>
</html>