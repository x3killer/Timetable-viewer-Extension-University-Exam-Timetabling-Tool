package my.edu.utar.fyp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExamSlipActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView fullName, stuID;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_slip);

        fullName = findViewById(R.id.nameExam);
        stuID = findViewById(R.id.idExam);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        new Thread(this::queryDatabase).start();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    // Handle the error
                    return;
                }

                if (value != null && value.exists()) {
                    fullName.setText("Name: " + value.getString("fName"));
                    stuID.setText("ID: " + value.getString("studentID"));

                } else {
                    // Document does not exist
                    Toast.makeText(ExamSlipActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void queryDatabase() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connection parameters
            String connectionString = "jdbc:mysql://10.0.2.2:3306/exam";
            String username = "root";
            String password = "020528";

            // Establish connection to the database
            connection = DriverManager.getConnection(connectionString, username, password);

            // Create statement
            statement = connection.createStatement();

            String studentID = stuID.getText().toString().substring(4);

            // Execute query
            String query = "SELECT SUBSTRING_INDEX(roomID, ',', 1) AS roomID, exam.courseID, course.courseName, exam.examDate, CONCAT(exam.startTime, ' - ', exam.endTime) AS time_range \n" +
                    "FROM exam \n" +
                    "INNER JOIN course ON exam.courseID = course.courseID\n" +
                    "INNER JOIN stucourse ON exam.courseID = stucourse.courseID\n" +
                    "WHERE stucourse.stuID = '" + studentID + "'\n" +
                    "ORDER BY examDate;";

            resultSet = statement.executeQuery(query);

            LinearLayout cardContainer = findViewById(R.id.cardContainer); // Assuming you have a LinearLayout with id 'cardContainer' in your layout XML

            while (resultSet.next()) {
                // Retrieve exam data
                String date = resultSet.getString("examDate");
                String timeRange = resultSet.getString("time_range");
                String courseID = resultSet.getString("courseID");
                String courseName = resultSet.getString("courseName");
                String venue = resultSet.getString("roomID");

                // Create a new CardView
                CardView cardView = new CardView(ExamSlipActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(16, 16, 16, 16); // Adjust margins as needed
                cardView.setLayoutParams(params);
                cardView.setCardElevation(8);
                cardView.setRadius(16);

                // Populate the CardView with exam data
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                textView.setPadding(16, 16, 16, 16); // Adjust padding as needed
                textView.setText(courseID + " " + courseName + "\n" +
                        "Date: " + date + "\n" +
                        "Time: " + timeRange + "\n" +
                        "Venue: " + venue);
                cardView.addView(textView);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cardContainer.addView(cardView);
                    }
                });
            }

        } catch (SQLException e) {
            Log.e("ExamSlipActivity", "Error querying database", e);
            // Handle error (e.g., display an error message to the user)
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Log.e("ExamSlipActivity", "Error closing resources", e);
            }
        }
    }

}