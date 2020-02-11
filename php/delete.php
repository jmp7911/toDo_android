<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
{
    $taskId=$_POST['taskId'];

          try{
              $stmt = $con->prepare('delete from task where taskId = :taskId');
              $stmt->bindParam(':taskId', $taskId);
              if($stmt->execute())
              {
                  $successMSG = "사용자를 삭제했습니다.";
              }
              else
              {
                  $errMSG = "사용자 삭제 에러";
              }

          } catch(PDOException $e) {
              die("Database error: " . $e->getMessage());
          }

    }
?>
<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

	$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

?>
