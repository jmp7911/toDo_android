<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
{
    $taskId=$_POST['taskId'];
    $content=$_POST['content'];
    $dueDateYear=$_POST['dueDateYear'];
    $dueDateMonth=$_POST['dueDateMonth'];
    $dueDateDayOfMonth=$_POST['dueDateDayOfMonth'];

          try{
              $stmt = $con->prepare('UPDATE task SET content=:content, dueDateYear=:dueDateYear, dueDateMonth=:dueDateMonth,
              dueDateDayOfMonth=:dueDateDayOfMonth
              WHERE taskId=:taskId');
              $stmt->bindParam(':taskId', $taskId);
              $stmt->bindParam(':content', $content);
              $stmt->bindParam(':dueDateYear', $dueDateYear);
              $stmt->bindParam(':dueDateMonth', $dueDateMonth);
              $stmt->bindParam(':dueDateDayOfMonth', $dueDateDayOfMonth);
              if($stmt->execute())
              {
                  $successMSG = "기존 사용자 변경했습니다.";
              }
              else
              {
                  $errMSG = "사용자 변경 에러";
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
