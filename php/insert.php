<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
{
    $taskId=$_POST['taskId'];
    $content=$_POST['content'];
    $isDone=$_POST['isDone'];
    $dueDateYear=$_POST['dueDateYear'];
    $dueDateMonth=$_POST['dueDateMonth'];
    $dueDateDayOfMonth=$_POST['dueDateDayOfMonth'];

          try{
              $stmt = $con->prepare('INSERT INTO task(taskId, content, isDone, dueDateYear, dueDateMonth, dueDateDayOfMonth)
              VALUES(:taskId, :content, :isDone, :dueDateYear, :dueDateMonth, :dueDateDayOfMonth)');
              $stmt->bindParam(':taskId', $taskId);
              $stmt->bindParam(':content', $content);
              $stmt->bindParam(':isDone', $isDone);
              $stmt->bindParam(':dueDateYear', $dueDateYear);
              $stmt->bindParam(':dueDateMonth', $dueDateMonth);
              $stmt->bindParam(':dueDateDayOfMonth', $dueDateDayOfMonth);
              if($stmt->execute())
              {
                  $successMSG = "새로운 사용자를 추가했습니다.";
              }
              else
              {
                  $errMSG = "사용자 추가 에러";
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

    if( !$android )
    {
?>
<html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                Name: <input type = "text" name = "taskId" />
                Content: <input type = "text" name = "content" />
                <input type="hidden" name="isDone" value="1"/>
                <input type="hidden" name="dueDateYear" value="2020">
                <input type="hidden" name="dueDateMonth" value="2">
                <input type="hidden" name="dueDateDayOfMonth" value="11">
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
